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

public class BishopScorerTest {

	private BishopPairScorer scorer = new BishopPairScorer();
	private BitBoard board = new BitBoard();

    @Test
	public void testBishopScore1() {
		assertEquals(0, scorer.scorePosition(board.initialise(), new PositionScorer.Context()));
	}
	
    @Test
	public void testBishopScore2() {
		FENUtils.loadPosition("2b1kb2/8/8/8/8/8/8/7K w - - 0 1", board);
		assertEquals(-150, scorer.scorePosition(board, new PositionScorer.Context()));
	}
	
    @Test
	public void testBishopScore3() {
		FENUtils.loadPosition("2b1kb2/q7/8/8/8/8/8/7K w - - 0 1", board);
		assertEquals(-300, scorer.scorePosition(board, new PositionScorer.Context()));
	}

    @Test
	public void testBishopScore4() {
		FENUtils.loadPosition("2b1kb2/q7/8/8/8/8/8/2B2B1K w - - 0 1", board);
		assertEquals(-150, scorer.scorePosition(board, new PositionScorer.Context()));
	}
}
