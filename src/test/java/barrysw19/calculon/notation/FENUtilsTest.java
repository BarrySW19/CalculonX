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
package barrysw19.calculon.notation;

import barrysw19.calculon.engine.BitBoard;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class FENUtilsTest {

    @Test
	public void testFenConversion1() {
		String fen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
		BitBoard board = new BitBoard().initialise();
		assertEquals(fen, FENUtils.generate(board));
		assertEquals(fen, FENUtils.generate(FENUtils.loadPosition(fen, board)));
	}

    @Test
	public void testFenConversion2() {
        // Tests a bug found in setting of en passant file doesn't come back!

		String fen = "rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1";
		BitBoard board = new BitBoard();
		assertEquals(fen, FENUtils.generate(FENUtils.loadPosition(fen, board)));

        BitBoard playedBoard = new BitBoard().initialise();
        PGNUtils.applyMove(playedBoard, "e4");
        assertTrue(playedBoard.equalPosition(board));
	}
		
    @Test
	public void testFenConversion3() {
		String fen = "rnbqkbnr/pp1ppppp/8/2p5/4P3/8/PPPP1PPP/RNBQKBNR w KQkq c6 0 2";
		BitBoard board = new BitBoard();
		assertEquals(fen, FENUtils.generate(FENUtils.loadPosition(fen, board)));
	}
	
    @Test
	public void testFenConversion4() {
		String fen = "rnbqkbnr/pp1ppppp/8/2p5/4P3/5N2/PPPP1PPP/RNBQKB1R b KQkq - 0 2";
		BitBoard board = new BitBoard();
		assertEquals(fen, FENUtils.generate(FENUtils.loadPosition(fen, board)));
	}

    @Test
	public void testFenConversion5() {
		String fen = "4k3/8/8/8/8/8/4P3/4K3 w - - 0 39";
		BitBoard board = new BitBoard();
		assertEquals(fen, FENUtils.generate(FENUtils.loadPosition(fen, board)));
		
		PGNUtils.applyMove(board, "Kd1");
		assertEquals("4k3/8/8/8/8/8/4P3/3K4 b - - 1 39", FENUtils.generate(board));
		
		PGNUtils.applyMove(board, "Kd8");
		assertEquals("3k4/8/8/8/8/8/4P3/3K4 w - - 2 40", FENUtils.generate(board));
		
		FENUtils.loadPosition("4k3/8/8/8/8/8/4P3/3K4 b - - 6 39", board);
		PGNUtils.applyMove(board, "Kd8");
		assertEquals("3k4/8/8/8/8/8/4P3/3K4 w - - 1 40", FENUtils.generate(board));
	}
}
