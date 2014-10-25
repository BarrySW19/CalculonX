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
package nl.zoidberg.calculon.analyzer;

import nl.zoidberg.calculon.engine.BitBoard;
import nl.zoidberg.calculon.notation.FENUtils;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class RookScorerTest {
	private static final RookScorer rs = new RookScorer();

    @Test
	public void testEqualScorer() {
        assertEquals(0, scorePosition("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"));
    }

    @Test
    public void testHalfOpenScorer() {
        assertEquals(-RookScorer.HALF_OPEN_FILE_SCORE,
                scorePosition("rnbqkbnr/1ppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"));
        assertEquals(2 * RookScorer.HALF_OPEN_FILE_SCORE,
                scorePosition("rnbqkbnr/pppppppp/8/8/8/8/1PPPPPP1/RNBQKBNR w KQkq - 0 1"));
	}

    @Test
    public void testOpenScorer() {
        assertEquals(-RookScorer.OPEN_FILE_SCORE,
                scorePosition("rnbqkbn1/1ppppppp/8/8/8/8/1PPPPPPP/1NBQKBNR w KQkq - 0 1"));
    }

    @Test
    public void testPigsOnThe7thScore() {
        assertEquals(RookScorer.PIGS_ON_THE_SEVENTH,
                scorePosition("1k2r2r/1pp2RR1/p2p4/8/8/P2P4/1PP5/1K6 w - - 0 1"));
    }

    @Test
	public void testConnectedScore() {
		BitBoard board = new BitBoard();
		FENUtils.loadPosition("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/R4RK1 w KQkq - 0 1", board);
		assertEquals(150, rs.scorePosition(board, new PositionScorer.Context()));

		FENUtils.loadPosition("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/4RRK1 w KQkq - 0 1", board);
		assertEquals(150, rs.scorePosition(board, new PositionScorer.Context()));

		FENUtils.loadPosition("rnbqkbnr/pppppppp/8/8/7P/7R/PPPPPPP1/1NBQKBNR w KQkq - 0 1", board);
		assertEquals(150, rs.scorePosition(board, new PositionScorer.Context()));
	}

    private static int scorePosition(String fen) {
        BitBoard board = new BitBoard();
        FENUtils.loadPosition(fen, board);
        return rs.scorePosition(board, new PositionScorer.Context());
    }
}
