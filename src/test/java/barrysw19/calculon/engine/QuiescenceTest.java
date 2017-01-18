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
import barrysw19.calculon.analyzer.MaterialScorer;
import barrysw19.calculon.model.Piece;
import barrysw19.calculon.notation.FENUtils;
import barrysw19.calculon.notation.PGNUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class QuiescenceTest {
    private GameScorer gameScorer;

    @Before
    public void setUp() {
        gameScorer = new GameScorer();
        gameScorer.addScorer(new MaterialScorer());
    }

    @Test
    public void testThreatMoveGeneration() {
        BitBoard board = FENUtils.getBoard("2rr4/5p1k/6pp/8/3QBn2/P4P2/1P3P1q/2RRK3 w - - 3 30");

        PGNUtils.applyMove(board, "Qxd8");
        assertEquals(Sets.newHashSet("Rxc1", "Qxf2+", "Ng2+", "Qg1+", "Qh1+", "Nd3+", "Rxd8"),
                PGNUtils.convertMovesToPgn(board, new MoveGeneratorImpl(board).getThreatMovesIterator()));

        PGNUtils.applyMove(board, "Qg1+");
        assertEquals(Collections.singleton("Kd2"),
                PGNUtils.convertMovesToPgn(board, new MoveGeneratorImpl(board).getThreatMovesIterator()));

        PGNUtils.applyMove(board, "Kd2");
        assertTrue(PGNUtils.convertMovesToPgn(board, new MoveGeneratorImpl(board).getThreatMovesIterator()).contains("Qxf2#"));
    }

    @Test
    public void testEvaluation1() {
        // Extra capture should not be taken...
        BitBoard board = FENUtils.getBoard("1k6/6p1/2pp4/8/8/8/4B1P1/7K w - - 0 1");
        assertEquals(MaterialScorer.VALUE_BISHOP - 2*MaterialScorer.VALUE_PAWN, gameScorer.score(board));

        ChessEngine chessEngine = new ChessEngine(gameScorer);
        chessEngine.setTargetTime(1);
        chessEngine.setMoveGeneratorFactory(new TestGeneratorFactory(
                new String[][] {
                        { "E2F3" },
                        { "D6D5" },
                }
        ));
        List<SearchContext> results = chessEngine.getScoredMoves(board);
        assertEquals("E2F3", results.get(0).getAlgebraicMove());
        assertEquals(2*MaterialScorer.VALUE_PAWN - MaterialScorer.VALUE_BISHOP, results.get(0).getScore());
    }

    @Test
    public void testEvaluation2() {
        // Expect extra capture RxQ, PxR in eval
        BitBoard board = FENUtils.getBoard("1k3q2/4p3/5p2/5R2/8/8/5R2/7K w - - 0 1");
        assertEquals(-1000, gameScorer.score(board));

        ChessEngine chessEngine = new ChessEngine(gameScorer);
        chessEngine.setTargetTime(1);
        chessEngine.setMoveGeneratorFactory(new TestGeneratorFactory(
                new String[][] {
                        { "F5F6" },
                        { "F8F6" },
                }
        ));
        List<SearchContext> results = chessEngine.getScoredMoves(board);
        assertEquals("F5F6", results.get(0).getAlgebraicMove());
        assertEquals(1000, results.get(0).getScore());
    }

    @Test
    public void testEvaluation3() {
        // Expect capture of queen in quiescence
        BitBoard board = FENUtils.getBoard("1k3q2/1p6/5p2/5R2/8/8/5R2/7K w - - 0 1");
        assertEquals(-1000, gameScorer.score(board));

        ChessEngine chessEngine = new ChessEngine(gameScorer);
        chessEngine.setTargetTime(1);
        chessEngine.setMoveGeneratorFactory(new TestGeneratorFactory(
                new String[][] {
                        { "F5F6" },
                        { "F8F6" },
                }
        ));
        List<SearchContext> results = chessEngine.getScoredMoves(board);
        assertEquals("F5F6", results.get(0).getAlgebraicMove());
        assertEquals(-4000, results.get(0).getScore());
    }

    @Test
    public void testEvaluationWithPromotion() {
        BitBoard board = FENUtils.getBoard("2R5/2P5/8/8/8/8/2b5/2k3K1 w - - 0 1");
        ChessEngine chessEngine = new ChessEngine(gameScorer);
        chessEngine.setTargetTime(1);
        chessEngine.setMoveGeneratorFactory(new TestGeneratorFactory(
                new String[][] {
                        { "C8H8", "H1G2" },
                }
        ));
        List<SearchContext> results = chessEngine.getScoredMoves(board);
        assertEquals(2, results.size());
    }

    @Test
    public void testStupidMove1() {
        BitBoard bitBoard = new BitBoard().initialise();
        PGNUtils.applyMoves(bitBoard,
                "e4", "e5", "Nf3", "d5", "exd5", "Qxd5", "a3", "Bf5", "Qe2", "Be7", "Nxe5", "Bxc2"); // g4??
        String preBoard = FENUtils.generate(bitBoard);
        ChessEngine chessEngine = new ChessEngine(gameScorer);
        chessEngine.setTargetTime(1);
        chessEngine.setMoveGeneratorFactory(new TestGeneratorFactory(
                new String[][] {
                        { "G2G4", "B1C3" },
                }
        ));
//        List<ChessEngine.ScoredMove> allMoves = chessEngine.getScoredMoves(bitBoard);
//        assertEquals(2, allMoves.size());
        String results = chessEngine.getPreferredMove(bitBoard);
        assertEquals(preBoard, FENUtils.generate(bitBoard));
        assertEquals("B1C3", results);
    }
    /**
     * Static evaluation is +8, but the next move would allow an extra pawn capture for +9
     */
    @Test
    public void testExpectAdditionalCapture() {
        BitBoard board = FENUtils.getBoard("1k6/3p4/8/8/8/1Q6/8/7K w - - 0 1");
        assertEquals(8000, gameScorer.score(board));

        ChessEngine chessEngine = new ChessEngine(gameScorer);
        chessEngine.setTargetTime(1);
        chessEngine.setMoveGeneratorFactory(new TestGeneratorFactory(
                new String[][] {
                        { "B3D3" },
                        { "D7D5" },
                }
        ));
        List<SearchContext> results = chessEngine.getScoredMoves(board);
        assertEquals("B3D3", results.get(0).getAlgebraicMove());
        // Immediate score - queen - pawn = 8000
        assertEquals(-9000, results.get(0).getScore());

        chessEngine = new ChessEngine(gameScorer);
        chessEngine.setTargetTime(1);
        chessEngine.setMoveGeneratorFactory(new TestGeneratorFactory(
                new String[][] {
                        { "B3D3" },
                        { "D7D5" },
                }
        ));
        results = chessEngine.getScoredMoves(board);
        // With quiescence, expect the pawn got grabbed too
        assertEquals(-9000, results.get(0).getScore());
    }

    /**
     * Another capture is possible, but the analysis should reject it as it leads to greater loss.
     */
    @Test
    public void testExpectRejectedCapture() {
        BitBoard board = FENUtils.getBoard("5k2/3p4/1r6/8/8/1Q6/8/7K w - - 0 1");
        assertEquals(3000, gameScorer.score(board));

        ChessEngine chessEngine = new ChessEngine(gameScorer);
        chessEngine.setTargetTime(1);
        chessEngine.setMoveGeneratorFactory(new TestGeneratorFactory(
                new String[][] {
                        { "B3E3" },
                        { "B6E6" },
                }
        ));
        List<SearchContext> results = chessEngine.getScoredMoves(board);
        assertEquals("B3E3", results.get(0).getAlgebraicMove());
        // Immediate score - queen - pawn = 8000
        assertEquals(-3000, results.get(0).getScore());
    }

    @Test
    public void testBadMove1() {
        BitBoard board = FENUtils.getBoard("r1bq1rk1/pp1n1pp1/2pbpn1p/3p4/2PP4/2N1PN2/PPQ1BPPP/1RB2RK1 b - - 1 9");
        ChessEngine chessEngine = new ChessEngine(gameScorer);
        chessEngine.setTargetTime(2);

        List<SearchContext> results = chessEngine.getScoredMoves(board);
        Map<String, SearchContext> moves = new HashMap<>();
        for(SearchContext m: results) {
            moves.put(m.getAlgebraicMove(), m);
        }

        assertTrue(moves.containsKey("D7B6"));
        assertTrue(moves.get("D7B6").getScore() > 1000);
    }

    @Test
    public void testPawnCaptures1() {
        BitBoard board = FENUtils.getBoard("3k4/8/3p4/5P2/4P3/8/8/3K4 w - - 0 1");
        PieceMoveGenerator generator = new PawnCaptureGenerator();
        List<BitBoard.BitBoardMove> moves = new ArrayList<>();

        BitBoardTest.generateMoves(generator, board, moves);
        assertEquals(0, moves.size());
    }

    @Test
    public void testPawnCaptures2() {
        BitBoard board = FENUtils.getBoard("3k4/8/8/3p1P2/4P3/8/8/3K4 w - - 0 1");
        PieceMoveGenerator generator = new PawnCaptureGenerator();
        List<BitBoard.BitBoardMove> moves = Lists.newArrayList(generator.generateThreatMoves(new MoveGeneratorImpl.MoveGeneratorContext(board)));
        assertEquals(1, moves.size());
        assertEquals("E4D5", moves.get(0).getAlgebraic());
    }

    @Test
    public void testPawnMoves1() {
        BitBoard board = FENUtils.getBoard("3k4/8/8/3p1P2/4P3/8/8/3K4 w - - 0 1");
        MoveGeneratorImpl generator = new MoveGeneratorImpl(board);
        generator.setGenerators(new PawnMoveGenerator());
        Set<String> moves = PGNUtils.convertMovesToPgn(board, generator.getThreatMovesIterator());

        assertEquals(Collections.emptySet(), moves);
    }

    @Test
    public void testPawnMoves2() {
        // Pawn push gives discovered check
        BitBoard board = FENUtils.getBoard("8/8/8/3p1P2/k3P2R/8/8/3K4 w - - 0 1");
        MoveGeneratorImpl generator = new MoveGeneratorImpl(board);
        generator.setGenerators(new PawnMoveGenerator());
        Set<String> moves = PGNUtils.convertMovesToPgn(board, generator.getThreatMovesIterator());

        assertEquals(Sets.newHashSet("e5+"), moves);
    }

    @Test
    public void testBishopMoves1() {
        // Pawn push gives discovered check
        BitBoard board = FENUtils.getBoard("2k5/7p/8/8/4B3/8/8/7K w - - 0 1");
        MoveGeneratorImpl generator = new MoveGeneratorImpl(board);
        generator.setGenerators(new StraightMoveGenerator(Piece.BISHOP));
        Set<String> moves = PGNUtils.convertMovesToPgn(board, generator.getThreatMovesIterator());

        assertEquals(Sets.newHashSet("Bxh7", "Bf5+", "Bb7+"), moves);
    }

    private static class TestGeneratorFactory implements ChessEngine.MoveGeneratorFactory {
        private List<String[]> moveList = new ArrayList<>();

        TestGeneratorFactory(String[][] moves) {
            Collections.addAll(moveList, moves);
        }

        @Override
        public MoveGenerator createMoveGenerator(BitBoard bitBoard) {
            if(moveList.size() == 0) {
                return new MoveGeneratorImpl(bitBoard);
            }
            System.out.println("Creating: " + Arrays.toString(moveList.get(0)));
            return new TestGenerator(moveList.remove(0), bitBoard);
        }
    }

    private static class TestGenerator implements MoveGenerator {
        private BitBoard bitBoard;
        private Iterator<BitBoard.BitBoardMove> iterator;

        TestGenerator(String[] moves, BitBoard bitBoard) {
            this.bitBoard = bitBoard;
            List<BitBoard.BitBoardMove> moveList = new ArrayList<>();
            for(String s: moves) {
                moveList.add(bitBoard.getMove(s));
            }
            iterator = moveList.iterator();
        }

        @Override
        public Iterator<BitBoard.BitBoardMove> getThreatMovesIterator() {
            return new MoveGeneratorImpl(bitBoard).getThreatMovesIterator();
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public BitBoard.BitBoardMove next() {
            BitBoard.BitBoardMove next = iterator.next();
            System.out.println(next);
            return next;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
