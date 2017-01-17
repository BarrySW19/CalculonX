/*
 * Calculon - A Java chess-engine.
 *
 * Copyright (C) 2008-2016 Barry Smith
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
package barrysw19.calculon.model;

import barrysw19.calculon.engine.BitBoard;
import barrysw19.calculon.engine.BitBoard.BitBoardMove;
import barrysw19.calculon.engine.MoveGeneratorImpl;
import barrysw19.calculon.notation.FENUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.function.LongConsumer;
import java.util.stream.LongStream;
import java.util.stream.StreamSupport;

import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertEquals;

/**
 * These tests verify the move generation software. The number of possible moves at various depths
 * is tested against the known correct values. If these don't match exactly, something is wrong
 * in the move generation logic. The correct values from any position can be found using crafty
 * with the 'perft' command.
 */
public class PerftTest {
    private static Logger LOG = LoggerFactory.getLogger(PerftTest.class);

    private static final long MAX_COUNT = 5_000_000;

    @Test
    public void testStartPosition() {
        testConfig(new PerftTestConfig("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1",
                20, 400, 8_902, 197_281, 4_865_609, 119_060_324, 3_195_901_860L));
    }

    @Test
    public void testEndGame1() {
        testConfig(new PerftTestConfig("8/2p5/3p4/KP5r/1R3p1k/8/4P1P1/8 w - - 0 1",
                14, 191, 2_812, 43_238, 674_624, 11_030_083, 178_633_661));
    }

    @Test
    public void testManyCaptures1() {
        testConfig(new PerftTestConfig("r3k2r/pppq1ppp/2nbbn2/3pp3/3PP3/2NBBN2/PPPQ1PPP/R3K2R w KQkq - 0 8",
                41, 1_680, 69_126, 2_833_127));
    }

    @Test
    public void testManyCaptures2() {
        testConfig(new PerftTestConfig("r3k2r/ppp2p1p/3bbnpB/n2Np3/q2PP3/2PB1N2/PP1Q1PPP/R3K2R w KQkq - 0 11",
                47, 2_055, 93_774));
    }

    @Test
    public void testEndGame2() {
        testConfig(new PerftTestConfig("8/PPP4k/8/8/8/8/4Kppp/8 w - - 0 1",
                18, 290, 5_044, 89_363, 1_745_545));
    }

    @Test
    public void testEndGame3() {
        testConfig(new PerftTestConfig("8/3K4/2p5/p2b2r1/5k2/8/8/1q6 b - - 0 1",
                50, 279, 13_310, 54_703, 2_538_084));
    }

    @Test
    public void testEndGame4() {
        testConfig(new PerftTestConfig("8/p3kp2/6p1/3r1p1p/7P/8/3p2P1/3R1K2 w - - 0 1",
                10, 218, 2_886, 63_771, 927_197));
    }

    @Test
    public void testMidGame1() {
        testConfig(new PerftTestConfig("r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq - 0 1",
                48, 2_039, 97_862, 4_085_603, 193_690_690));
    }

    @Test
    public void testMidGame2() {
        testConfig(new PerftTestConfig("r3k2r/Pppp1ppp/1b3nbN/nP6/BBP1P3/q4N2/Pp1P2PP/R2Q1RK1 w kq - 0 1",
                6, 264, 9_467, 422_333, 15_833_292, 706_045_033));
    }

    @Test
    public void testMidGame3() {
        testConfig(new PerftTestConfig("r2q1rk1/pP1p2pp/Q4n2/bbp1p3/Np6/1B3NBn/pPPP1PPP/R3K2R b KQ - 0 1",
                6, 264, 9_467, 422_333, 15_833_292, 706_045_033));
    }

    @Test
    public void testMidGame4() {
        testConfig(new PerftTestConfig("r4rk1/1pp1qppp/p1np1n2/2b1p1B1/2B1P1b1/P1NP1N2/1PP1QPPP/R4RK1 w - - 0 10",
                46, 2_079, 89_890, 3_894_594, 164_075_551, 6_923_051_137L, 287_188_994_746L));
    }

    @Test
    public void testMidGame5() {
        testConfig(new PerftTestConfig("rnbq1k1r/pp1Pbppp/2p5/8/2B5/8/PPP1NnPP/RNBQK2R w KQ - 1 8",
                44, 1_486, 62_379, 2_103_487, 89_941_194));
    }

    private void testConfig(PerftTestConfig config) {
        List<Long> useCounts = LongStream.of(config.expectedCounts).filter(l -> l <= MAX_COUNT).boxed().collect(toList());
        BigDecimal execTime = new BigDecimal(System.currentTimeMillis());
        executeBoard(FENUtils.getBoard(config.position), useCounts);
        execTime = new BigDecimal(System.currentTimeMillis()).subtract(execTime).scaleByPowerOfTen(-3);
        LOG.info(String.format("Test took %s seconds.", execTime.toString()));
    }

    private void executeBoard(BitBoard board, List<Long> expect) {
        for (int x = 0; x < expect.size(); x++) {
            CalcSpliterator calcSpliterator = new CalcSpliterator(x + 1, BitBoard.createCopy(board));
            long result = StreamSupport.longStream(calcSpliterator, true)
//                  .peek(l -> { LOG.info("Sum {}", l); })
                    .sum();
            LOG.debug("Expect: {}, Result: {}", expect.get(x), result);
            assertEquals(expect.get(x).longValue(), result);
        }
    }

    private static long generateToDepth(int depth, BitBoard bitBoard) {
        if (depth == 1) {
            return new MoveGeneratorImpl(bitBoard).getAllRemainingMoves().size();
        }

        long count = 0;
        for (Iterator<BitBoardMove> moveItr = new MoveGeneratorImpl(bitBoard); moveItr.hasNext(); ) {
            BitBoardMove move = moveItr.next();
            Object cacheId = bitBoard.getCacheId();
            bitBoard.makeMove(move);
            count += generateToDepth(depth - 1, bitBoard);
            bitBoard.unmakeMove();
            if ( ! cacheId.equals(bitBoard.getCacheId())) {
                throw new IllegalStateException("make/unmake caused differences");
            }
        }
        return count;
    }

    private static class PerftTestConfig {
        private String position;
        private long[] expectedCounts;

        private PerftTestConfig(String position, long... expectedCounts) {
            this.position = position;
            this.expectedCounts = expectedCounts;
        }
    }

    private static class CalcSpliterator implements Spliterator.OfLong {
        private int depth;
        private BitBoard bitBoard;
        private List<BitBoardMove> moves;
        private boolean todo = true;

        CalcSpliterator(int depth, BitBoard bitBoard) {
            this(depth, bitBoard, new MoveGeneratorImpl(bitBoard).getAllRemainingMoves());
        }

        CalcSpliterator(int depth, BitBoard bitBoard, List<BitBoardMove> moves) {
//            LOG.info("New spliterator at depth: {}", depth);
            this.depth = depth;
            this.bitBoard = bitBoard;
            this.moves = moves;
        }

        @Override
        public Spliterator.OfLong trySplit() {
            if(depth < 5 || moves.isEmpty()) {
                return null;
            }

            if(moves.size() > 1) {
                List<BitBoardMove> splitMoves = moves.subList(moves.size() / 2, moves.size());
                moves = moves.subList(0, moves.size() / 2);
                return new CalcSpliterator(depth, BitBoard.createCopy(bitBoard), splitMoves);
            }

            bitBoard.makeMove(moves.get(0));
            depth--;
            moves = new MoveGeneratorImpl(bitBoard).getAllRemainingMoves();
            return trySplit();
        }

        @Override
        public boolean tryAdvance(LongConsumer action) {
            if(todo) {
                action.accept(generateToDepth(depth, bitBoard));
                todo = false;
                return true;
            }
            return false;
        }

        @Override
        public long estimateSize() {
            return Long.MAX_VALUE;
        }

        @Override
        public int characteristics() {
            return CONCURRENT | DISTINCT | IMMUTABLE | NONNULL;
        }
    }
}
