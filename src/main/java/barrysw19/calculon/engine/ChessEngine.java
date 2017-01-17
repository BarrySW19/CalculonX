/*
 * Calculon - A Java chess-engine.
 *
 * Copyright (C) 2008-2017 Barry Smith
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package barrysw19.calculon.engine;

import barrysw19.calculon.analyzer.GameScorer;
import barrysw19.calculon.engine.BitBoard.BitBoardMove;
import barrysw19.calculon.notation.PGNUtils;
import barrysw19.calculon.opening.OpeningBook;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

import static java.util.stream.Collectors.toList;

public class ChessEngine {
    private final static Logger LOG = LoggerFactory.getLogger(ChessEngine.class);
    // As we can't have -MIN_VALUE being used...
    private final static int BIG_VALUE = 9_999_999;
    private final static int PRUNE_MARGIN = 1000; // Drop moves this much worse than best move.

    private static final ExecutorService executorService = Executors.newFixedThreadPool(2);

    private MoveGeneratorFactory moveGeneratorFactory = MoveGeneratorImpl::new;

	private GameScorer gameScorer;
    private int depthForSearch;
    private int qDepth = 5;
    private int targetTime = 30;
    private long terminateTime;
    private Cache<BitSet, Integer> scoreCache
            = CacheBuilder.newBuilder().maximumSize(5*1024*1024).recordStats().build();

    public ChessEngine() {
        this(GameScorer.getDefaultScorer());
	}

    public ChessEngine(int targetTime) {
        this();
        this.targetTime = targetTime;
    }
	
	public ChessEngine(final GameScorer gameScorer) {
		this.gameScorer = gameScorer;
	}

    public final ChessEngine setQDepth(int qDepth) {
        this.qDepth = qDepth;
        return this;
    }

    /**
     * Mostly for testing purposes - allows mocked/test move generation. Could also be used to set
     * different generators for speed or quiescence.
     * @param moveGeneratorFactory Any move generator.
     */
    public void setMoveGeneratorFactory(MoveGeneratorFactory moveGeneratorFactory) {
        this.moveGeneratorFactory = moveGeneratorFactory;
    }

    public SearchContext getPreferredMoveContext(BitBoard bitBoard) {
		String bookMove = OpeningBook.getDefaultBook() == null
                ? null : OpeningBook.getDefaultBook().getBookMove(bitBoard);
		if(bookMove != null) {
			LOG.info("Using book move: {}", bookMove);
            return new SearchContext(PGNUtils.toPgnMoveMap(bitBoard).get(bookMove), bitBoard);
		}

        scoreCache.invalidateAll();
		List<SearchContext> allMoves = new ArrayList<>(getScoredMoves(bitBoard));
        if(LOG.isDebugEnabled()) {
            Collections.sort(allMoves, (o1, o2) -> o1.getAlgebraicMove().compareTo(o2.getAlgebraicMove()));
            for (SearchContext ctx: allMoves) {
                LOG.debug("Final moves: {}", ctx);
            }
        }

        LOG.debug("Cache stats: {}", scoreCache.stats());
        scoreCache.invalidateAll();
        return selectBestMove(allMoves);
	}

	public String getPreferredMove(BitBoard bitBoard) {
        return getPreferredMoveContext(bitBoard).getAlgebraicMove();
	}

    /**
     * Outer loop - responsible for getting the move scores and pruning them down to
     * the set to scan deeper.
     *
     * @param bitBoard The board to analyse.
     * @return The moves and scores.
     */
    public List<SearchContext> getScoredMoves(BitBoard bitBoard) {
        terminateTime = System.nanoTime() + (targetTime * 1_000_000_000L);
        terminateTime -= 250_000_000L;  // Safety margin for bullet games 0.25s
        depthForSearch = 1;
        LOG.debug("terminate at: " + terminateTime + ", current: " + System.nanoTime());
        final Map<String, SearchContext> bestMoves = new HashMap<>();
        final Map<String, SearchContext> allMoves = new HashMap<>();
        while(true) {
            final List<SearchContext> bestMoveList = Lists.newArrayList(bestMoves.values());
            LOG.debug("Search to depth {} with {} moves", depthForSearch, bestMoveList.size());
            final List<SearchContext> scoredMoves = getScoredMoves(bitBoard, bestMoveList);

            boolean terminated = false;
            for(SearchContext result: scoredMoves) {
                LOG.debug("{}", result);
                if(result.getStatus() != SearchContext.Status.NORMAL) {
                    terminated = true;
                    continue;
                }

                // Put missing moves, or fully searched moves into the results map.
                if( !bestMoves.containsKey(result.getAlgebraicMove()) || result.getStatus() == SearchContext.Status.NORMAL) {
                    bestMoves.put(result.getAlgebraicMove(), result);
                }
            }
            allMoves.putAll(bestMoves);

            int bestScore = Integer.MAX_VALUE;
            for(SearchContext currentMove: bestMoves.values()) {
                bestScore = Math.min(bestScore, currentMove.getScore());
            }
            final int pruneLevel = bestScore + PRUNE_MARGIN;
            LOG.debug("Best: {}, prune at: {}", bestScore, pruneLevel);
            // Remove moves which seem much worse than the best move so they don't get searched again.
            for(String move: new HashSet<>(bestMoves.keySet())) {
                if(bestMoves.get(move).getScore() > pruneLevel) {
                    LOG.debug("Dropping move: {}", move);
                    bestMoves.remove(move);
                }
            }

            // Return when time is up, or only one move is left, or a forced checkmate is found.
            if(terminated || bestMoves.size() <= 1 || bestScore <= GameScorer.MATE_SCORE) {
                return new ArrayList<>(allMoves.values());
            }
            depthForSearch++;
            qDepth = Math.max(5, depthForSearch + 3);
        }
    }

    // package private for testing at present - test hook only
    SearchContext getScoredMove(final BitBoard bitBoard, String move, int depthForSearch, int qDepth) {
        this.qDepth = qDepth;
        terminateTime = System.nanoTime() + (targetTime * 1_000_000_000L);
        terminateTime -= 250_000_000L;  // Safety margin for bullet games 0.25s
        this.depthForSearch = depthForSearch;
        this.qDepth = Math.max(5, depthForSearch + 3);
        SearchContext context = new SearchContext(move, bitBoard);
        return getScoredMoves(bitBoard, Collections.singletonList(context)).get(0);
    }

	private List<SearchContext> getScoredMoves(final BitBoard bitBoard, final List<SearchContext> movesFilter) {
        ImmutableMap<String, SearchContext> movesToAnalyse = Maps.uniqueIndex(movesFilter, SearchContext::getAlgebraicMove);
        List<FutureTask<SearchContext>> tasks = new ArrayList<>();

		for(Iterator<BitBoardMove> moveItr = moveGeneratorFactory.createMoveGenerator(bitBoard); moveItr.hasNext(); ) {
			final BitBoardMove move = moveItr.next();
            final BitBoard cloneBitBoard = BitBoard.createCopy(bitBoard);
            final String algebraic = move.getAlgebraic();
            if( !movesToAnalyse.isEmpty() && !movesToAnalyse.containsKey(algebraic)) {
                // LOG.debug("Skipping move {}", algebraic);
                continue;
            }

            FutureTask<SearchContext> scoredMoveFutureTask = new FutureTask<>(() -> {
                final SearchContext searchContext = new SearchContext(algebraic, bitBoard);
                cloneBitBoard.makeMove(move);
                int score = alphaBeta(-BIG_VALUE, BIG_VALUE, depthForSearch, cloneBitBoard, searchContext.descend(move));
                searchContext.setScore(score);
                LOG.debug("Ran: {}", searchContext);
                cloneBitBoard.unmakeMove();
                return searchContext;
            });
            tasks.add(scoredMoveFutureTask);
            executorService.submit(scoredMoveFutureTask);
		}

        final List<SearchContext> rv = new ArrayList<>();
        for(FutureTask<SearchContext> i: tasks) {
            try {
                rv.add(i.get());
            } catch (InterruptedException|ExecutionException e) {
                LOG.error("Unexpected exception", e);
            }
        }

		return rv;
	}

    // Negamax
    private int alphaBeta(int alpha, int beta, int depthLeft, BitBoard bitBoard, SearchContext searchContext) throws ExecutionException {
        if(System.nanoTime() > terminateTime) {
            searchContext.setStatus(SearchContext.Status.TIMEOUT);
            searchContext.ascend();
            return 0;
        }
        if(depthLeft <= 0) {
            searchContext.flip();
            int rv = quiesce(bitBoard, alpha, beta, qDepth, searchContext);
            searchContext.ascend();
            return rv;
        }

        Iterator<BitBoardMove> moveItr = moveGeneratorFactory.createMoveGenerator(bitBoard);
        if( ! moveItr.hasNext()) {
            int rv = gameScorer.score(bitBoard);
            if(rv == GameScorer.MATE_SCORE) {
//                rv *= (depthLeft + 1); // Prefer mate-in-1 to mate-in-2
                rv *= (depthLeft + qDepth + 2); // Prefer mate-in-1 to mate-in-2
            }
            searchContext.ascend();
            return rv;
        }

        while (moveItr.hasNext())  {
            BitBoardMove nextMove = moveItr.next();
            bitBoard.makeMove(nextMove);

            int score = -alphaBeta(-beta, -alpha, depthLeft - 1, bitBoard, searchContext.descend(nextMove));
            bitBoard.unmakeMove();
            if(score >= beta) {
                searchContext.ascend();
                searchContext.getMoves()[depthForSearch - depthLeft] = nextMove;
                return beta;
            }
            if(score > alpha) {
                alpha = score;
            }
        }
        searchContext.ascend();
        return alpha;
    }

    private int quiesce(final BitBoard bitBoard, int alpha, int beta, int depth, SearchContext searchContext) throws ExecutionException {
        if(System.nanoTime() > terminateTime) {
            searchContext.setStatus(SearchContext.Status.TIMEOUT);
            searchContext.qAscend();
            return 0;
        }

        searchContext.addInfo("Q("+alpha+","+beta+")");
        int standPat = gameScorer.score(bitBoard);

        List<BitBoardMove> threatMoves = new MoveGeneratorImpl(bitBoard).getThreateningMoves();
        if(depth < 3 && !CheckDetector.isPlayerToMoveInCheck(bitBoard)) {
            threatMoves = threatMoves.stream().filter(BitBoardMove::isCapture).collect(toList());
        }
        //final boolean moveIsForced = threatMoves.size() == 1 && CheckDetector.isPlayerToMoveInCheck(bitBoard);

        // Apply the stand pat if the player could make a null move, or has no moves available.
        if ( !CheckDetector.isPlayerToMoveInCheck(bitBoard) || standPat == GameScorer.MATE_SCORE || threatMoves.isEmpty()) {
            if(standPat == GameScorer.MATE_SCORE) {
                standPat *= Math.max(1, depth+1);
            }
            if (standPat >= beta) {
                searchContext.addInfo("rB=" + beta);
                searchContext.qAscend();
                return beta;
            }

            if (alpha < standPat) {
                searchContext.addInfo("A=SP=" + standPat);
                alpha = standPat;
            }
        }

        if(depth <= 0 && beta != BIG_VALUE) {
            searchContext.addInfo("^B=" + beta);
            searchContext.qAscend();
            return beta; // Should this be alpha or beta??
        }

        for(BitBoardMove move: threatMoves) {
            bitBoard.makeMove(move);
            int score = -quiesce(bitBoard, -beta, -alpha, depth - 1, searchContext.qDescend(move));
            bitBoard.unmakeMove();

            if( score >= beta) {
                searchContext.addInfo("mB=" + beta);
                searchContext.qAscend();
                return beta;
            }
            if( score > alpha) {
                searchContext.addInfo("mA=SP=" + standPat);
                alpha = score;
            }
        }

        searchContext.addInfo("rA=" + alpha);
        searchContext.qAscend();
        return alpha;
    }

	private static List<SearchContext> selectBestMoves(List<SearchContext> allMoves) {
		return selectBestMoves(allMoves, 0, 5);
	}
	
	/**
	 * Selects all moves sharing the lowest (i.e. best) score.
	 * 
	 * @param allMoves All moves to parse.
	 * @return The moves within margin.
	 */
	private static List<SearchContext> selectBestMoves(List<SearchContext> allMoves, int margin, int maxMoves) {
		if(allMoves.size() == 0) {
			return allMoves;
		}

		allMoves = allMoves.stream().filter(c -> c.getStatus() == SearchContext.Status.NORMAL).collect(toList());
		Collections.sort(allMoves);
		int bestScore = allMoves.get(0).getScore();
		while(allMoves.size() > maxMoves || allMoves.get(allMoves.size()-1).getScore() > bestScore+margin) {
			allMoves.remove(allMoves.size()-1);
		}
		return allMoves;
	}

	private static SearchContext selectBestMove(List<SearchContext> allMoves) {
		List<SearchContext> bestMoves = selectBestMoves(allMoves);
		if(bestMoves.size() == 0) {
			return null;
		}
        SearchContext selectedMove = bestMoves.get((int) (Math.random() * bestMoves.size()));
        LOG.info("Selected move: {}", selectedMove);
		return selectedMove;
	}

    public void setTargetTime(int max) {
        this.targetTime = max;
    }

    public interface MoveGeneratorFactory {
        MoveGenerator createMoveGenerator(BitBoard bitBoard);
    }
}
