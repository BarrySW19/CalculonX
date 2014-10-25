/**
 * Calculon - A Java chess-engine.
 *
 * Copyright (C) 2008-2013 Barry Smith
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

public class MobilityScorerTest extends AbstractAnalyserTest {

    public MobilityScorerTest() {
        super(new MobilityScorer());
    }

    @Test
    public void testMobilityScore1() {
        setPosition("4k3/8/8/3p4/8/8/6B1/4K3 w - - 0 1");
        assertEquals(MobilityScorer.PER_SQUARE * 5, scorer.scorePosition(board, context));
    }

    @Test
    public void testMobilityScore2() {
        setPosition("2k5/1q3N2/1p6/3p4/8/8/6B1/4K3 w - - 0 1");
        assertEquals(MobilityScorer.PER_SQUARE * (5 - 8), scorer.scorePosition(board, context));
    }

    @Test
    public void testMobilityScore3() {
        setPosition("2k5/1q3N2/1p6/3p4/8/8/1R4B1/4K3 w - - 0 1");
        assertEquals(MobilityScorer.PER_SQUARE * (5 + 9 - 8), scorer.scorePosition(board, context));
    }

    @Test
    public void testMobilityScore4() {
        setPosition("2r2rk1/1bq2pbp/1np1p1p1/1pN5/pP1PB3/P1P2QP1/1B2RP1P/1R4K1 b - - 0 23");
        assertEquals(MobilityScorer.PER_SQUARE * (24 - 19), scorer.scorePosition(board, context));
    }
}
