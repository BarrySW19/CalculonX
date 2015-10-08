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

import org.junit.Test;

import javax.imageio.ImageIO;
import java.util.Arrays;

public class KingSafetyScorerTest extends AbstractAnalyserTest {

    public KingSafetyScorerTest() {
        super(new KingSafetyScorer());
    }

    @Test
	public void testStart() {
		assertScore(0, "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w - - 0 1");
	}

    @Test
	public void testCastled() {
		assertScore(250, "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQ1RK1 w - - 0 1");
	}

    @Test
	public void testPawnGone() {
        System.out.println(Arrays.toString(ImageIO.getReaderMIMETypes()));
		assertScore(-70, "rnbq1rk1/1ppppppp/8/8/8/8/PPPPPP1P/RNBQ1RK1 w - - 0 1");
	}

    @Test
	public void testFiancettoed() {
		assertScore(-30, "rnbq1rk1/pppppppp/8/8/8/8/PPPPPPBP/RNBQ1RK1 w - - 0 1");
	}

    @Test
    public void testFiancettoedNoBishop() {
        assertScore(210, "6k1/1q6/8/8/8/Q7/5PPP/6K1 w - - 0 1");
        assertScore(180, "6k1/1q6/8/8/8/Q5P1/5PBP/6K1 w - - 0 1");
        assertScore(140, "6k1/1q6/8/8/8/Q5P1/5P1P/6K1 w - - 0 1");

    }
}
