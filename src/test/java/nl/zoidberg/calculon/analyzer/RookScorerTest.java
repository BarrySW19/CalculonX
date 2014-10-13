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
	
	private RookScorer rs = new RookScorer();

    @Test
	public void testScorer() {
		BitBoard board = new BitBoard();
		String fen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
		FENUtils.loadPosition(fen, board);
		assertEquals(0, rs.scorePosition(board, new PositionScorer.Context()));

		fen = "rnbqkbnr/1ppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
		FENUtils.loadPosition(fen, board);
		assertEquals(-RookScorer.OPEN_FILE_SCORE, rs.scorePosition(board, new PositionScorer.Context()));

		fen = "rnbqkbnr/pppppppp/8/8/8/8/1PPPPPP1/RNBQKBNR w KQkq - 0 1";
		FENUtils.loadPosition(fen, board);
		assertEquals(2*RookScorer.OPEN_FILE_SCORE, rs.scorePosition(board, new PositionScorer.Context()));
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
}
