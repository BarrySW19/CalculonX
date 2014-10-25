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

public class PawnScorerTest extends AbstractAnalyserTest {

    public PawnScorerTest() {
        super(new PawnStructureScorer());
    }

    @Test
	public void testBasicScore() {
        setPosition("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
		assertEquals(0, scorer.scorePosition(board, context));
	}

    @Test
	public void testIslands() {
		setPosition("rnbqkbnr/ppp1pppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
		assertEquals(PawnStructureScorer.S_ISLAND, scorer.scorePosition(board, context));
		
		setPosition("rnbqkbnr/pppppppp/8/8/8/8/PP1PP1PP/RNBQKBNR w KQkq - 0 1"); 
		assertEquals(-2 * PawnStructureScorer.S_ISLAND, scorer.scorePosition(board, context));
	}
	
    @Test
	public void testIsolated() {
		setPosition("rnbqkbnr/pp1pp1pp/8/8/8/8/P1P1PPPP/RNBQKBNR w KQkq - 0 1"); 
		assertEquals(-2 * PawnStructureScorer.S_ISOLATED, scorer.scorePosition(board, context));
	}

    @Test
	public void testDoubled() {
		// Both have islands, only one has doubled
		setPosition("rnbqkbnr/pp1ppppp/8/8/8/3P4/PP1PPPPP/RNBQKBNR w KQkq - 0 1"); 
		assertEquals(-PawnStructureScorer.S_DOUBLED, scorer.scorePosition(board, context));

		// Here - test pawn gets an advance bonus
		setPosition("rnbqkbnr/pp1ppppp/8/8/3P4/3P4/PP1PPPP1/RNBQKBNR w KQkq - 0 1"); 
		assertEquals(-2 * PawnStructureScorer.S_DOUBLED, scorer.scorePosition(board, context));
	}
	
    @Test @Deprecated
	public void testAdvanced() {
		setPosition("rnbqkbnr/pppppppp/3P4/8/8/8/PPP1PPPP/RNBQKBNR w KQkq - 0 1"); 
		assertEquals(0, scorer.scorePosition(board, context));

		setPosition("rnbqkbnr/ppp1pppp/8/8/8/3p4/PPPPPPPP/RNBQKBNR w KQkq - 0 1"); 
		assertEquals(0, scorer.scorePosition(board, context));
	}
}
