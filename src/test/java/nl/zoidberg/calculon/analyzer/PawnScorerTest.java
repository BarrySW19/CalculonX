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

public class PawnScorerTest {

    @Test
	public void testBasicScore() {
		BitBoard board = new BitBoard().initialise();
		assertEquals(0, new PawnStructureScorer().scorePosition(board, new PositionScorer.Context()));
	}

    @Test
	public void testIslands() {
		BitBoard board = FENUtils.getBoard("rnbqkbnr/ppp1pppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
		assertEquals(PawnStructureScorer.S_ISLAND, new PawnStructureScorer().scorePosition(board, new PositionScorer.Context()));
		
		board = FENUtils.getBoard("rnbqkbnr/pppppppp/8/8/8/8/PP1PP1PP/RNBQKBNR w KQkq - 0 1"); 
		assertEquals(-2 * PawnStructureScorer.S_ISLAND, new PawnStructureScorer().scorePosition(board, new PositionScorer.Context()));
	}
	
    @Test
	public void testIsolated() {
		BitBoard board = FENUtils.getBoard("rnbqkbnr/pp1pp1pp/8/8/8/8/P1P1PPPP/RNBQKBNR w KQkq - 0 1"); 
		assertEquals(-2 * PawnStructureScorer.S_ISOLATED, new PawnStructureScorer().scorePosition(board, new PositionScorer.Context()));
	}

    @Test
	public void testDoubled() {
		// Both have islands, only one has doubled
		BitBoard board = FENUtils.getBoard("rnbqkbnr/pp1ppppp/8/8/8/3P4/PP1PPPPP/RNBQKBNR w KQkq - 0 1"); 
		assertEquals(-PawnStructureScorer.S_DOUBLED, new PawnStructureScorer().scorePosition(board, new PositionScorer.Context()));

		// Here - test pawn gets an advance bonus
		board = FENUtils.getBoard("rnbqkbnr/pp1ppppp/8/8/3P4/3P4/PP1PPPP1/RNBQKBNR w KQkq - 0 1"); 
		assertEquals(-2 * PawnStructureScorer.S_DOUBLED, new PawnStructureScorer().scorePosition(board, new PositionScorer.Context()));
	}
	
    @Test @Deprecated
	public void testAdvanced() {
		BitBoard board = FENUtils.getBoard("rnbqkbnr/pppppppp/3P4/8/8/8/PPP1PPPP/RNBQKBNR w KQkq - 0 1"); 
		assertEquals(0, new PawnStructureScorer().scorePosition(board, new PositionScorer.Context()));

		board = FENUtils.getBoard("rnbqkbnr/ppp1pppp/8/8/8/3p4/PPPPPPPP/RNBQKBNR w KQkq - 0 1"); 
		assertEquals(0, new PawnStructureScorer().scorePosition(board, new PositionScorer.Context()));
	}
}
