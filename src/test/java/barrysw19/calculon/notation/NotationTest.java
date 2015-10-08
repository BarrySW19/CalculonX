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

public class NotationTest {

    @Test
	public void testPgnMoves() {
		BitBoard board = new BitBoard().initialise();
		
		PGNUtils.applyMoves(board, "e4", "c5", "Nf3");
		assertEquals("rnbqkbnr/pp1ppppp/8/2p5/4P3/5N2/PPPP1PPP/RNBQKB1R b KQkq - 1 2", FENUtils.generate(board));
	}
	
    @Test
	public void testChecks() {
		BitBoard board = FENUtils.getBoard("7k/R7/1R6/8/8/8/8/7K w - - 0 1");
		assertEquals("Rb8#", PGNUtils.translateMove(board, "B6B8"));

		board = FENUtils.getBoard("7k/R7/1R5n/8/8/8/8/7K w - - 0 1");
		assertEquals("Rb8+", PGNUtils.translateMove(board, "B6B8"));
	}
	
    @Test
	public void testMove() {
		String[] moves = {
				"h4", "d5", "c3", "Nf6", "Qa4+", "Qd7", "Qf4", "g6", "b3", "b6", "Rh3", "Kd8", "d3", "h6", "Rf3", "a5",
				"Qa4", "Qxa4", "bxa4", "h5", "Rf4", "Bd7", "Na3", "Kc8", "Rd4", "e6", "Rf4", "Be7", "Nf3", "Bd8", "Ng1",
				"Bc6", "Rd4", "e5", "Rf4", "exf4", "c4", "dxc4", "dxc4", "Bxa4", "c5", "bxc5", "Bd2", "Bd7", "Bxf4",
				"Re8", "Bd2", "Be6", "Rc1", "a4", "Bf4", "Rh8", "Be3", "Re8", "Rc2", "Bf5", "Rd2", 
		};
		BitBoard bb = new BitBoard().initialise();
		
		PGNUtils.applyMoves(bb, moves);
		assertTrue(PGNUtils.toPgnMoveMap(bb).containsKey("Nfd7"));
		assertTrue(PGNUtils.toPgnMoveMap(bb).containsKey("Nbd7"));
		PGNUtils.applyMove(bb, "Nfd7");
		PGNUtils.applyMove(bb, "Rb2");
		
		FENUtils.loadPosition("r3kbnr/ppp1qppp/n3p3/8/b3P3/2NK1N2/PPPP1PPP/R1BQ1B1R b kq - 0 7", bb);
		assertTrue(PGNUtils.toPgnMoveMap(bb).containsKey("O-O-O+"));
	}
}
