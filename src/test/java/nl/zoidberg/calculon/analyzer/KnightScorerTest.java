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

public class KnightScorerTest {
	
	private KnightScorer scorer = new KnightScorer();
	private BitBoard board = new BitBoard();

    @Test
	public void testKnightInCorner() {
		FENUtils.loadPosition("7k/8/8/8/8/8/8/N6K w - - 0 1", board);
		int expect = KnightScorer.targetScores[2]; 
		assertEquals(expect, scorer.scorePosition(board, new PositionScorer.Context()));
	}

    @Test
	public void testKnightInCornerOwnPieceBlocking() {
		FENUtils.loadPosition("7k/8/8/8/8/8/2R5/N6K w - - 0 1", board);
		int expect = KnightScorer.targetScores[2]; 
		assertEquals(expect, scorer.scorePosition(board, new PositionScorer.Context()));
	}

    @Test
	public void testKnightInCornerPawnBlocking() {
		FENUtils.loadPosition("7k/8/8/8/8/3p4/8/N6K w - - 0 1", board);
		int expect = KnightScorer.targetScores[1]; 
		assertEquals(expect, scorer.scorePosition(board, new PositionScorer.Context()));
	}

    @Test
	public void testKnightInCornerAttacking() {
		FENUtils.loadPosition("7k/8/8/8/8/8/2r5/N6K w - - 0 1", board);
		int expect = KnightScorer.targetScores[2]; 
		assertEquals(expect, scorer.scorePosition(board, new PositionScorer.Context()));
	}

    @Test
	public void testKnightInMiddle() {
		FENUtils.loadPosition("7k/8/8/8/3N4/8/8/7K w - - 0 1", board);
		int expect = (KnightScorer.targetScores[8] + KnightScorer.rankScores[3]); 
		assertEquals(expect, scorer.scorePosition(board, new PositionScorer.Context()));
	}

    @Test
	public void testBlackKnightInMiddle() {
		FENUtils.loadPosition("7k/8/8/8/3n4/8/8/7K w - - 0 1", board);
		int expect = (KnightScorer.targetScores[8] + KnightScorer.rankScores[4]); 
		assertEquals(-expect, scorer.scorePosition(board, new PositionScorer.Context()));
	}

    @Test
	public void testKnightInMiddleNonSecure() {
		int expect = (KnightScorer.targetScores[8] + KnightScorer.rankScores[3]); 

		FENUtils.loadPosition("7k/4p3/8/8/3N4/2P5/8/7K w - - 0 1", board);
		assertEquals(expect, scorer.scorePosition(board, new PositionScorer.Context()));

		FENUtils.loadPosition("7k/2p5/8/8/3N4/2P5/8/7K w - - 0 1", board);
		assertEquals(expect, scorer.scorePosition(board, new PositionScorer.Context()));

		FENUtils.loadPosition("7k/8/8/2p5/3N4/2P5/8/7K w - - 0 1", board);
		assertEquals(expect, scorer.scorePosition(board, new PositionScorer.Context()));
	}

    @Test
	public void testKnightInMiddleSecure() {
		FENUtils.loadPosition("7k/8/8/8/3N4/2P5/8/7K w - - 0 1", board);

		int expect = (KnightScorer.targetScores[8] + KnightScorer.rankScores[3] + KnightScorer.SECURE_BONUS); 
		assertEquals(expect, scorer.scorePosition(board, new PositionScorer.Context()));
		
		FENUtils.loadPosition("7k/8/8/8/3Np3/2P5/8/7K w - - 0 1", board);
		expect = (KnightScorer.targetScores[7] + KnightScorer.rankScores[3] + KnightScorer.SECURE_BONUS); 
		assertEquals(expect, scorer.scorePosition(board, new PositionScorer.Context()));
	}
}
