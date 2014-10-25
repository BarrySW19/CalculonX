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

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class BishopScorerTest extends AbstractAnalyserTest {

    public BishopScorerTest() {
        super(new BishopPairScorer());
    }

    @Test
	public void testBishopScore1() {
        setPosition("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
		assertEquals(0, scorer.scorePosition(board, context));
	}
	
    @Test
	public void testBishopScore2() {
		setPosition("2b1kb2/8/8/8/8/8/8/7K w - - 0 1");
		assertEquals(-150, scorer.scorePosition(board, context));
	}
	
    @Test
	public void testBishopScore3() {
		setPosition("2b1kb2/q7/8/8/8/8/8/7K w - - 0 1");
		assertEquals(-300, scorer.scorePosition(board, context));
	}

    @Test
	public void testBishopScore4() {
		setPosition("2b1kb2/q7/8/8/8/8/8/2B2B1K w - - 0 1");
		assertEquals(-150, scorer.scorePosition(board, context));
	}
}
