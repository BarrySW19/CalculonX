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

public class PawnStructureScorerTest extends AbstractAnalyserTest {

    public PawnStructureScorerTest() {
        super(new PawnStructureScorer());
    }

    @Test
	public void testBasicScore() {
		assertScore(0, "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
	}

    @Test
	public void testIslands() {
		assertScore(PawnStructureScorer.S_ISLAND, "rnbqkbnr/ppp1pppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
		assertScore(-2 * PawnStructureScorer.S_ISLAND, "rnbqkbnr/pppppppp/8/8/8/8/PP1PP1PP/RNBQKBNR w KQkq - 0 1");
	}
	
    @Test
	public void testIsolated() {
		assertScore(-2 * PawnStructureScorer.S_ISOLATED, "rnbqkbnr/pp1pp1pp/8/8/8/8/P1P1PPPP/RNBQKBNR w KQkq - 0 1");
	}

    @Test
	public void testDoubled() {
		// Both have islands, only one has doubled
		assertScore(-PawnStructureScorer.S_DOUBLED, "rnbqkbnr/pp1ppppp/8/8/8/3P4/PP1PPPPP/RNBQKBNR w KQkq - 0 1");

		// Here - test pawn gets an advance bonus
		assertScore(-2 * PawnStructureScorer.S_DOUBLED + PawnStructureScorer.S_CENTRE,
                "rnbqkbnr/pp1ppppp/8/8/3P4/3P4/PP1PPPP1/RNBQKBNR w KQkq - 0 1");
	}
	
    @Test @Deprecated
	public void testAdvanced() {
		assertScore(0, "rnbqkbnr/pppppppp/3P4/8/8/8/PPP1PPPP/RNBQKBNR w KQkq - 0 1");
		assertScore(0, "rnbqkbnr/ppp1pppp/8/8/8/3p4/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
	}
}
