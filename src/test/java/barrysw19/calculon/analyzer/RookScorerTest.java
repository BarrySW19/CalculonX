/**
 * Calculon - A Java chess-engine.
 *
 * Copyright (C) 2008-2014 Barry Smith
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

import org.junit.Test;

public class RookScorerTest extends AbstractAnalyserTest {

    public RookScorerTest() {
        super(new RookScorer());
    }

    @Test
	public void testEqualScorer() {
        assertScore(0, "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
    }

    @Test
    public void testHalfOpenScorer() {
        assertScore(-RookScorer.HALF_OPEN_FILE_SCORE, "rnbqkbnr/1ppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
        assertScore(2 * RookScorer.HALF_OPEN_FILE_SCORE, "rnbqkbnr/pppppppp/8/8/8/8/1PPPPPP1/RNBQKBNR w KQkq - 0 1");
	}

    @Test
    public void testOpenScorer() {
        assertScore(-RookScorer.OPEN_FILE_SCORE, "rnbqkbn1/1ppppppp/8/8/8/8/1PPPPPPP/1NBQKBNR w KQkq - 0 1");
    }

    @Test
    public void testIsolatedPawnAttack() {
        assertScore(RookScorer.ISOLATED_ATTACK_SCORE, "6kr/2p3pp/8/8/8/8/5PPP/2R3K1 w - - 0 0");
    }

    @Test
    public void testPigsOnThe7thScore() {
        assertScore(RookScorer.PIGS_ON_THE_SEVENTH, "1k2r2r/1pp2RR1/p2p4/8/8/P2P4/1PP5/1K6 w - - 0 1");
    }

    @Test
	public void testConnectedScore() {
		assertScore(RookScorer.CONNECTED_BONUS, "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/R4RK1 w KQkq - 0 1");
        assertScore(RookScorer.CONNECTED_BONUS, "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/4RRK1 w KQkq - 0 1");
        assertScore(RookScorer.CONNECTED_BONUS, "rnbqkbnr/pppppppp/8/8/7P/7R/PPPPPPP1/1NBQKBNR w KQkq - 0 1");
	}
}
