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

public class KingSafetyScorerTest extends AbstractAnalyserTest {

    public KingSafetyScorerTest() {
        super(new KingSafetyScorer());
    }

    @Test
	public void testStart() {
		setPosition("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w - - 0 1");
		assertEquals(0, scorer.scorePosition(board, context));
	}

    @Test
	public void testCastled() {
		setPosition("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQ1RK1 w - - 0 1");
		assertEquals(250, scorer.scorePosition(board, context));
	}

    @Test
	public void testPawnGone() {
		setPosition("rnbq1rk1/1ppppppp/8/8/8/8/PPPPPP1P/RNBQ1RK1 w - - 0 1");
		assertEquals(-70, scorer.scorePosition(board, context));
	}

    @Test
	public void testFiancettoed() {
		setPosition("rnbq1rk1/pppppppp/8/8/8/8/PPPPPPBP/RNBQ1RK1 w - - 0 1");
		assertEquals(-30, scorer.scorePosition(board, context));
	}
}
