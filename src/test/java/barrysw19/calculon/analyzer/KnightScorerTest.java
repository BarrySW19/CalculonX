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
package barrysw19.calculon.analyzer;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class KnightScorerTest extends AbstractAnalyserTest {
	
    public KnightScorerTest() {
        super(new KnightScorer());
    }

    @Test
	public void testKnightInCorner() {
		setPosition("7k/8/8/8/8/8/8/N6K w - - 0 1");
		assertEquals(KnightScorer.targetScores[2], scorer.scorePosition(board, context));
	}

    @Test
	public void testKnightInCornerOwnPieceBlocking() {
		setPosition("7k/8/8/8/8/8/2R5/N6K w - - 0 1");
		assertEquals(KnightScorer.targetScores[2], scorer.scorePosition(board, context));
	}

    @Test
	public void testKnightInCornerPawnBlocking() {
		setPosition("7k/8/8/8/8/3p4/8/N6K w - - 0 1");
		assertEquals(KnightScorer.targetScores[1], scorer.scorePosition(board, context));
	}

    @Test
	public void testKnightInCornerAttacking() {
		setPosition("7k/8/8/8/8/8/2r5/N6K w - - 0 1");
		assertEquals(KnightScorer.targetScores[2], scorer.scorePosition(board, context));
	}

    @Test
	public void testKnightInMiddle() {
		setPosition("7k/8/8/8/3N4/8/8/7K w - - 0 1");
		int expect = (KnightScorer.targetScores[8] + KnightScorer.rankScores[3]); 
		assertEquals(expect, scorer.scorePosition(board, context));
	}

    @Test
	public void testBlackKnightInMiddle() {
		setPosition("7k/8/8/8/3n4/8/8/7K w - - 0 1");
		int expect = (KnightScorer.targetScores[8] + KnightScorer.rankScores[4]); 
		assertEquals(-expect, scorer.scorePosition(board, context));
	}

    @Test
	public void testKnightInMiddleNonSecure() {
		int expect = (KnightScorer.targetScores[8] + KnightScorer.rankScores[3]); 

		setPosition("7k/4p3/8/8/3N4/2P5/8/7K w - - 0 1");
		assertEquals(expect, scorer.scorePosition(board, context));

		setPosition("7k/2p5/8/8/3N4/2P5/8/7K w - - 0 1");
		assertEquals(expect, scorer.scorePosition(board, context));

		setPosition("7k/8/8/2p5/3N4/2P5/8/7K w - - 0 1");
		assertEquals(expect, scorer.scorePosition(board, context));
	}

    @Test
	public void testKnightInMiddleSecure() {
		setPosition("7k/8/8/8/3N4/2P5/8/7K w - - 0 1");

		int expect = (KnightScorer.targetScores[8] + KnightScorer.rankScores[3] + KnightScorer.SECURE_BONUS); 
		assertEquals(expect, scorer.scorePosition(board, context));
		
		setPosition("7k/8/8/8/3Np3/2P5/8/7K w - - 0 1");
		expect = (KnightScorer.targetScores[7] + KnightScorer.rankScores[3] + KnightScorer.SECURE_BONUS); 
		assertEquals(expect, scorer.scorePosition(board, context));
	}
}
