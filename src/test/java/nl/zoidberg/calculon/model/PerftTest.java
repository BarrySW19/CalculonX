/**
 * Calculon - A Java chess-engine.
 *
 * Copyright (C) 2008-2009 Barry Smith
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
package nl.zoidberg.calculon.model;

import nl.zoidberg.calculon.engine.BitBoard;
import nl.zoidberg.calculon.engine.BitBoard.BitBoardMove;
import nl.zoidberg.calculon.engine.MoveGeneratorImpl;
import nl.zoidberg.calculon.notation.FENUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * These tests verify the move generation software. The number of possible moves at various depths
 * is tested against the known correct values. If these don't match exactly, something is wrong
 * in the move generation logic. The correct values from any position can be found using crafty
 * with the 'perft' command.
 */
public class PerftTest {
    private static Logger LOG = LoggerFactory.getLogger(PerftTest.class);

    private static final int MAX_COUNT = 10_000_000;

    @Test
    public void testStartPosition() {
        testConfig(new PerftTestConfig("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1",
                new long[]{20, 400, 8902, 197281, 4865609,}));
    }

    @Test
    public void testMidGame1() {
        testConfig(new PerftTestConfig("r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq - 0 1",
                new long[]{48, 2039, 97862, 4085603, 193690690,}));
    }

    @Test
    public void testEndGame1() {
        testConfig(new PerftTestConfig("8/2p5/3p4/KP5r/1R3p1k/8/4P1P1/8 w - - 0 1",
                new long[]{14, 191, 2812, 43238, 674624, 11030083, 178633661,}));
    }

    @Test
    public void testManyCaptures1() {
        testConfig(new PerftTestConfig("r3k2r/pppq1ppp/2nbbn2/3pp3/3PP3/2NBBN2/PPPQ1PPP/R3K2R w KQkq - 0 8",
                new long[]{41, 1680, 69126, 2833127,}));
    }

    @Test
    public void testManyCaptures2() {
        testConfig(new PerftTestConfig("r3k2r/ppp2p1p/3bbnpB/n2Np3/q2PP3/2PB1N2/PP1Q1PPP/R3K2R w KQkq - 0 11",
                new long[]{47, 2055, 93774,}));
    }

    @Test
    public void testEndGame2() {
        testConfig(new PerftTestConfig("8/PPP4k/8/8/8/8/4Kppp/8 w - - 0 1",
                new long[]{18, 290, 5044, 89363, 1745545,}));
    }

    @Test
    public void testEndGame3() {
        testConfig(new PerftTestConfig("8/3K4/2p5/p2b2r1/5k2/8/8/1q6 b - - 0 1",
                new long[]{50, 279, 13310, 54703, 2538084,}));
    }

    @Test
    public void testEndGame4() {
        testConfig(new PerftTestConfig("8/p3kp2/6p1/3r1p1p/7P/8/3p2P1/3R1K2 w - - 0 1",
                new long[]{10, 218, 2886, 63771, 927197,}));
    }

    @Test
    public void testMidGame3() {
        testConfig(new PerftTestConfig("r4rk1/1pp1qppp/p1np1n2/2b1p1B1/2B1P1b1/P1NP1N2/1PP1QPPP/R4RK1 w - - 0 10",
                new long[] {46, 2_079, 89_890, 3_894_594, 164_075_551, 6_923_051_137L, 287_188_994_746L }));
    }

    private void testConfig(PerftTestConfig config) {
        List<Long> useCounts = new ArrayList<>();
        for (int i = 0; i < config.expectedCounts.length && config.expectedCounts[i] < MAX_COUNT; i++) {
            useCounts.add(config.expectedCounts[i]);
        }
        executeBoard(FENUtils.getBoard(config.position), useCounts);
    }

    private void executeBoard(BitBoard board, List<Long> expect) {
        for (int x = 0; x < expect.size(); x++) {
            assertEquals((long) expect.get(x), generateToDepth(x + 1, board));
            LOG.debug("Checked count: {}", expect.get(x));
        }
    }

    private int generateToDepth(int depth, BitBoard bitBoard) {
        if (depth == 1) {
            return new MoveGeneratorImpl(bitBoard).getAllRemainingMoves().size();
        }

        int count = 0;
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

        private PerftTestConfig(String position, long[] expectedCounts) {
            this.position = position;
            this.expectedCounts = expectedCounts;
        }
    }
}
