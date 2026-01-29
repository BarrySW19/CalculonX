package barrysw19.calculon.analyzer;

import barrysw19.calculon.engine.BitBoard;
import barrysw19.calculon.model.Piece;
import barrysw19.calculon.model.Result;

import java.util.HashSet;
import java.util.Set;

public class GameScorer {
	public static final int MATE_SCORE = -100000;
	
	private static final GameScorer instance = getUnweightedScorer();
	
	private static GameScorer getUnweightedScorer() {
		GameScorer rv = new GameScorer();

        // TODO - something to encourage exchanging when ahead on material
        // TODO - something to encourage rooks opposite queens

		rv.addScorer(new MaterialScorer());
		rv.addScorer(new BishopPairScorer());
		rv.addScorer(new MobilityScorer());
		rv.addScorer(new PawnStructureScorer());
		rv.addScorer(new KnightScorer());
		rv.addScorer(new RookScorer());
		rv.addScorer(new KingSafetyScorer());
        rv.addScorer(new BackRankMinorPieceScorer());
        rv.addScorer(new KingCentralisationScorer());
        rv.addScorer(new PassedPawnScorer());
        rv.addScorer(new AdvancedPawnScorer());
//        rv.addScorer(new CastlingScorer());

		rv.addScorer(new CheckmateScorer());

		return rv;
	}
	
	private final Set<PositionScorer> scorers = new HashSet<>();
	
	public static GameScorer getDefaultScorer() {
		return instance;
	}
	
	public GameScorer addScorer(PositionScorer scorer) {
		scorers.add(scorer);
		return this;
	}

    /**
     * Generate a score - positive is good for the current player. Position scorers however can stick with the
     * convention of having white as positive.
     *
     * @param bitBoard The board to score.
     * @return The score.
     */
    public int score(final BitBoard bitBoard) {
        Result result = bitBoard.getResult();

        if(result == Result.RES_DRAW) {
            return 0;
        }

        if(result == Result.RES_BLACK_WIN || result == Result.RES_WHITE_WIN) {
            return MATE_SCORE;
        }

        final PositionScorer.Context context = new PositionScorer.Context(bitBoard);
        int score = scorers.stream().mapToInt(scorer -> scorer.scorePosition(bitBoard, context)).sum();

        return score * (bitBoard.getPlayer() == Piece.WHITE ? 1 : -1);
    }
}
