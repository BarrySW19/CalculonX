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

public class MaterialScorerTest extends AbstractAnalyserTest {
    private static final int ALL_PIECES =
            MaterialScorer.VALUE_PAWN * 8
            + MaterialScorer.VALUE_ROOK * 2
            + MaterialScorer.VALUE_KNIGHT * 2
            + MaterialScorer.VALUE_BISHOP * 2
            + MaterialScorer.VALUE_QUEEN;

    public MaterialScorerTest() {
        super(new MaterialScorer());
    }

    @Test
	public void testAllPieces() {
        assertScore(0, "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
	}

    @Test
	public void testAllWhite() {
        assertScore(ALL_PIECES, "4k3/8/8/8/8/8/PPPPPPPP/RNBQKBNR w - - 0 1");
	}

    @Test
	public void testAllBlack() {
        assertScore(-ALL_PIECES, "rnbqkbnr/pppppppp/8/8/8/8/8/4K3 w - - 0 1");
	}

    @Test
	public void testRookValue() {
        assertScore(MaterialScorer.VALUE_ROOK, "1nbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w - - 0 1");
	}

    @Test
	public void testKnightValue() {
        assertScore(-MaterialScorer.VALUE_KNIGHT, "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/R1BQKBNR w - - 0 1");
	}

    @Test
	public void testBishopValue() {
        assertScore(2 * MaterialScorer.VALUE_BISHOP, "rn1qk1nr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w - - 0 1");
	}

    @Test
	public void testQueenValue() {
        assertScore(-MaterialScorer.VALUE_QUEEN, "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNB1KBNR w - - 0 1");
	}

    @Test
	public void testPawnValue() {
        assertScore(4 * MaterialScorer.VALUE_PAWN, "rnbqkbnr/p1p2pp1/8/8/8/8/PPPPPPPP/RNBQKBNR w - - 0 1");
	}
}
