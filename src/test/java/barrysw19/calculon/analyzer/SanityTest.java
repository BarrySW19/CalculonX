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

import barrysw19.calculon.notation.PGNUtils;
import barrysw19.calculon.engine.BitBoard;
import barrysw19.calculon.engine.ChessEngine;
import barrysw19.calculon.engine.MoveGeneratorImpl;
import barrysw19.calculon.model.Piece;
import barrysw19.calculon.model.Result;
import barrysw19.calculon.notation.FENUtils;
import barrysw19.calculon.opening.OpeningBook;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Check some obvious moves get made.
 */
public class SanityTest {

	static {
		OpeningBook.setUseOpeningBook(false);
	}
	
    @Test
	public void testTakeHangingPiece() {
		BitBoard board = new BitBoard();
		String fen = "8/1r3k2/8/8/8/8/6K1/RR6 w - - 0 1";
		FENUtils.loadPosition(fen, board);

		ChessEngine node = new ChessEngine();
		String bestMove = node.getPreferredMove(board);
		assertNotNull(bestMove);
		bestMove = PGNUtils.translateMove(board, bestMove);
		assertEquals("Rxb7+", bestMove);
	}

    @Test
	public void testCheckmate() {
		BitBoard board = new BitBoard();
		String fen = "5k2/R7/8/8/8/8/6K1/1R6 w - - 0 1";
		FENUtils.loadPosition(fen, board);

		ChessEngine node = new ChessEngine();
		String bestMove = node.getPreferredMove(board);
		
		assertNotNull(bestMove);
		bestMove = PGNUtils.translateMove(board, bestMove);
		assertEquals("Rb8#", bestMove);
	}

    @Test
	public void testNoMaterialDraw() {
		assertEquals(FENUtils.getBoard("7k/7p/8/8/8/8/8/1B5K w - - 0 1").getResult(), Result.RES_NO_RESULT);
		assertEquals(FENUtils.getBoard("7k/7b/8/8/8/8/8/1B5K w - - 0 1").getResult(), Result.RES_DRAW);
		assertEquals(FENUtils.getBoard("7k/7b/8/8/8/8/8/1BN4K w - - 0 1").getResult(), Result.RES_NO_RESULT);
		assertEquals(FENUtils.getBoard("7k/7r/8/8/8/8/8/1B5K w - - 0 1").getResult(), Result.RES_NO_RESULT);

		BitBoard board = FENUtils.getBoard("7k/6bp/8/8/8/8/8/1B5K w - - 0 1");
		assertEquals(board.getResult(), Result.RES_NO_RESULT);
		
		// Only sacrificing the bishop for the pawn draws

		assertEquals("Bxh7", PGNUtils.translateMove(board, new ChessEngine(2).getPreferredMove(board)));
		PGNUtils.applyMove(board, "Bxh7");
		assertEquals(0, new MoveGeneratorImpl(board).getAllRemainingMoves().size());
	}

    @Test
	public void test3RepeatDrawDetect() {
		BitBoard bitBoard = FENUtils.getBoard("8/7k/8/8/8/8/PPP4K/5Q2 w - - 0 1");

		bitBoard.makeMove(BitBoard.generateMove(
				BitBoard.coordToPosition("H2"), BitBoard.coordToPosition("H1"), Piece.WHITE, Piece.KING));
		bitBoard.makeMove(BitBoard.generateMove(
				BitBoard.coordToPosition("H7"), BitBoard.coordToPosition("H8"), Piece.BLACK, Piece.KING));
		
		bitBoard.makeMove(BitBoard.generateMove(
				BitBoard.coordToPosition("H1"), BitBoard.coordToPosition("H2"), Piece.WHITE, Piece.KING));
		bitBoard.makeMove(BitBoard.generateMove(
				BitBoard.coordToPosition("H8"), BitBoard.coordToPosition("H7"), Piece.BLACK, Piece.KING));
		bitBoard.makeMove(BitBoard.generateMove(
				BitBoard.coordToPosition("H2"), BitBoard.coordToPosition("H1"), Piece.WHITE, Piece.KING));
		bitBoard.makeMove(BitBoard.generateMove(
				BitBoard.coordToPosition("H7"), BitBoard.coordToPosition("H8"), Piece.BLACK, Piece.KING));
		
		bitBoard.makeMove(BitBoard.generateMove(
				BitBoard.coordToPosition("H2"), BitBoard.coordToPosition("H1"), Piece.WHITE, Piece.KING));

		// Now, Kh8 draws against K+Q...
		assertEquals("H8H7", new ChessEngine().getPreferredMove(bitBoard));
		bitBoard.makeMove(BitBoard.generateMove(
				BitBoard.coordToPosition("H8"), BitBoard.coordToPosition("H7"), Piece.BLACK, Piece.KING));
		assertEquals(3, bitBoard.getRepeatedCount());
	}
	
    @Test
	public void testSomeChessProblems() {
		// A fairly simple mate-in-2
		chessTempoDotComTest("2rr4/5p1k/6pp/8/3QBn2/P4P2/1P3P1q/2RRK3 w - - 3 30", "Qxd8", "Qg1+");
	}
	
	private void chessTempoDotComTest(String fen, String firstMove, String reply) {
		BitBoard bitBoard = FENUtils.getBoard(fen);
		PGNUtils.applyMove(bitBoard, firstMove);
		assertEquals(reply, PGNUtils.translateMove(bitBoard, new ChessEngine(2000).getPreferredMove(bitBoard)));
	}
	
    @Test
	public void testQuiesce1() {
		// Taking here is a bad idea... crafty plays Nf3 here...
		BitBoard bitBoard = FENUtils.getBoard("r5nk/r4ppp/r7/b7/8/R7/R5PP/R5NK w - - 0 1");
		ChessEngine engine = new ChessEngine();
//        System.out.println(engine.getPreferredMove(bitBoard));
//		engine.setDepth(3);
//		engine.setQuiesce(true);
//		assertFalse("A4A5".equals(engine.getPreferredMove(bitBoard)));

		// But, now with an extra rook, it's a good idea.. crafty find Rxa5 at depth 11
		bitBoard = FENUtils.getBoard("r5nk/r4ppp/r7/b7/R7/R7/R5PP/R5NK w - - 0 1");
//		assertEquals("A4A5", engine.getPreferredMove(bitBoard));
	}

//	public void testQuiesce2() {
//		// Taking here is a bad idea... crafty plays Nf3 here...
//		BitBoard bitBoard = new Board().initialise().getBitBoard();
//		PGNUtils.applyMoves(bitBoard, new String[] {
//				"e3", "e5", "Nf3", "Qe7", "g3", "Qf6", "Bg2", "e4", "Nd4",});
//		
//		ChessEngine engine = new ChessEngine();
//		engine.setDepth(3);
//		engine.setQuiesce(true);
//		System.out.println(engine.getPreferredMove(bitBoard));
//	}
	
    @Test
	public void testTactics1() {
		BitBoard bitBoard = FENUtils.getBoard("5rk1/p7/1p2p1q1/2b2p1p/5R1P/1B4P1/P4P2/3Q2K1 w - - 1 32");
		ChessEngine engine = new ChessEngine();
		PGNUtils.applyMove(bitBoard, "Qd7");
		assertEquals("G6G3", engine.getPreferredMove(bitBoard));
	}
	
//	public void testQuiesce3() {
//		String s = "2r3k1/Rpr2p1p/3pp1p1/1P1p4/2nP4/2P1P1P1/4NP1P/1R4K1 b - - 0 24";
//		BitBoard bitBoard = FENUtils.getBitBoard(s);
//		walkQuiesceTree(bitBoard, 1);
//	}
//	
//	private void walkQuiesceTree(BitBoard bitBoard, int depth) {
//		for(BitBoardMove move: new MoveGeneratorImpl(bitBoard).getThreateningMoves()) {
//			System.out.println("" + depth + ": " + PGNUtils.translateMove(bitBoard, move.getAlgebraic()));
//			bitBoard.makeMove(move);
//			walkQuiesceTree(bitBoard, depth + 1);
//			bitBoard.unmakeMove();
//		}
//	}
}
