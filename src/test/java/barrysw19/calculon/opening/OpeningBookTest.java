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
package barrysw19.calculon.opening;

import barrysw19.calculon.engine.BitBoard;
import barrysw19.calculon.notation.FENUtils;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class OpeningBookTest {
	
    @Test
	public void testOpeningBook() {
		OpeningBook.setUseOpeningBook(true);
		BitBoard board = FENUtils.getBoard("rnbqkbnr/ppp1pppp/8/3p4/8/1P6/P1PPPPPP/RNBQKBNR w KQkq d6 0 1");
		assertEquals("Bb2", OpeningBook.getDefaultBook().getBookMove(board));
	}
	
    @Test
	public void testDefaultBook() {
		OpeningBook openingBook = OpeningBook.getDefaultBook();
		assertNotNull(openingBook);
	}
}
