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

import barrysw19.calculon.analyzer.GameScorer;
import barrysw19.calculon.analyzer.MaterialScorer;
import barrysw19.calculon.engine.BitBoard;
import barrysw19.calculon.engine.ChessEngine;
import com.google.common.collect.Sets;
import org.junit.Test;

import java.util.Date;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PGNUtilsTest {

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

	@Test
	public void testAllMoveGeneration() {
        Set<String> moves = PGNUtils.getAllMoves(new BitBoard().initialise());
		assertEquals(Sets.newHashSet("Na3", "Nc3", "Nf3", "Nh3", "a3", "a4", "b3", "b4", "c3", "c4", "d3", "d4", "e3", "e4", "f3", "f4", "g3", "g4", "h3", "h4"), moves);
	}

	@Test
    public void testLoadMoves() {
        System.out.println(new Date());
        final String moves = "1. e4 c5 2. Nf3 d6 3. d4 cxd4 4. Nxd4 Nf6 5. Nc3 e6 6. Be3 Be7 7. Bb5+ Bd7\n" +
                "8. Be2 Nc6 9. O-O O-O 10. Ndb5 Qb8 11. Bf4 e5 12. Bg5 Be6 13. Qd3 a6 14. Bxf6\n" +
                "gxf6 15. Na3 b5 16. Nd5 Qa7 17. Qf3 Bxd5 18. exd5 Nd4 19. Qg4+ Kh8 20. Bd3\n" +
                "Rg8 21. Qh3 Rg7 22. c3 b4 23. cxb4 Rag8 24. Kh1 Qb8 25. b5 axb5 26. b4 Qa7\n" +
                "27. Nxb5 Nxb5 28. Bxb5 Qd4 29. Rad1 Qxb4 30. Bd3 e4 31. Be2 Rxg2";
        List<String> results = PGNUtils.splitNotation(moves);
        BitBoard bitBoard = new BitBoard().initialise();
        PGNUtils.applyMoves(bitBoard, results);
        ChessEngine chessEngine = new ChessEngine(30);
        GameScorer gameScorer = new GameScorer().addScorer(new MaterialScorer());
        chessEngine.getScoredMoves(bitBoard).forEach(sc -> System.out.println(sc + " " + PGNUtils.translateMove(bitBoard, sc.getAlgebraicMove())) );
    }
}
