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

public class MaterialScorerTest {
    private static final int ALL_PIECES =
            MaterialScorer.VALUE_PAWN * 8
            + MaterialScorer.VALUE_ROOK * 2
            + MaterialScorer.VALUE_KNIGHT * 2
            + MaterialScorer.VALUE_BISHOP * 2
            + MaterialScorer.VALUE_QUEEN;

	private MaterialScorer scorer = new MaterialScorer();
	private BitBoard board = new BitBoard();

    @Test
	public void testAllPieces() {
		board.initialise();
		assertEquals(0, scorer.scorePosition(board, new PositionScorer.Context()));
	}

    @Test
	public void testAllWhite() {
		FENUtils.loadPosition("4k3/8/8/8/8/8/PPPPPPPP/RNBQKBNR w - - 0 1", board);
		assertEquals(ALL_PIECES, scorer.scorePosition(board, new PositionScorer.Context()));
	}

    @Test
	public void testAllBlack() {
		FENUtils.loadPosition("rnbqkbnr/pppppppp/8/8/8/8/8/4K3 w - - 0 1", board);
		assertEquals(-ALL_PIECES, scorer.scorePosition(board, new PositionScorer.Context()));
	}

    @Test
	public void testRookValue() {
		FENUtils.loadPosition("1nbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w - - 0 1", board);
		assertEquals(5000, scorer.scorePosition(board, new PositionScorer.Context()));
	}

    @Test
	public void testKnightValue() {
		FENUtils.loadPosition("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/R1BQKBNR w - - 0 1", board);
		assertEquals(-MaterialScorer.VALUE_KNIGHT, scorer.scorePosition(board, new PositionScorer.Context()));
	}

    @Test
	public void testBishopValue() {
		FENUtils.loadPosition("rn1qk1nr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w - - 0 1", board);
		assertEquals(2*MaterialScorer.VALUE_BISHOP, scorer.scorePosition(board, new PositionScorer.Context()));
	}

    @Test
	public void testQueenValue() {
		FENUtils.loadPosition("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNB1KBNR w - - 0 1", board);
		assertEquals(-9000, scorer.scorePosition(board, new PositionScorer.Context()));
	}

    @Test
	public void testPawnValue() {
		FENUtils.loadPosition("rnbqkbnr/p1p2pp1/8/8/8/8/PPPPPPPP/RNBQKBNR w - - 0 1", board);
		assertEquals(4000, scorer.scorePosition(board, new PositionScorer.Context()));
	}
}
