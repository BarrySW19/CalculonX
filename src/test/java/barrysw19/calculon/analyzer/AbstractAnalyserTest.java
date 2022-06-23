package barrysw19.calculon.analyzer;

import barrysw19.calculon.engine.BitBoard;
import barrysw19.calculon.notation.FENUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
public abstract class AbstractAnalyserTest {
    protected final PositionScorer scorer;
    protected BitBoard board;
    protected PositionScorer.Context context;

    protected AbstractAnalyserTest(PositionScorer scorer) {
        this.scorer = scorer;
    }

    protected void setPosition(String fen) {
        board = FENUtils.getBoard(fen);
        context = new PositionScorer.Context(board);
    }

    protected void assertScore(int score, String fen) {
        setPosition(fen);
        assertEquals(score, scorer.scorePosition(board, context));
    }
}
