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
package nl.zoidberg.calculon.model;

import nl.zoidberg.calculon.engine.BitBoard;
import nl.zoidberg.calculon.engine.BitBoard.BitBoardMove;
import nl.zoidberg.calculon.engine.CheckDetector;
import nl.zoidberg.calculon.engine.MoveGeneratorImpl;
import nl.zoidberg.calculon.engine.PawnCaptureGenerator;
import nl.zoidberg.calculon.notation.FENUtils;
import nl.zoidberg.calculon.notation.PGNUtils;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class BoardTest {

    @Test
	public void threeRepeatDrawDetect1() {
		BitBoard bitBoard = new BitBoard().initialise();
		
		PGNUtils.applyMoves(bitBoard, new String[] { "Nf3", "Nf6", "Ng1", "Ng8", "Nf3", "Nf6", "Ng1", });
		assertEquals(22, new MoveGeneratorImpl(bitBoard).getAllRemainingMoves().size());
		
		PGNUtils.applyMove(bitBoard, "Ng8");
		assertEquals(0, new MoveGeneratorImpl(bitBoard).getAllRemainingMoves().size());
	}
	
    @Test
	public void enPassant() {
		BitBoard bitBoard = new BitBoard().initialise();
		PGNUtils.applyMoves(bitBoard, new String[] { "Nh3", "d5", "Ng1", "d4", "e4", });
		
		assertEquals("rnbqkbnr/ppp1pppp/8/8/3pP3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 3", FENUtils.generate(bitBoard));
		
		PGNUtils.applyMove(bitBoard, "dxe3");
		assertEquals("rnbqkbnr/ppp1pppp/8/8/8/4p3/PPPP1PPP/RNBQKBNR w KQkq - 0 4", FENUtils.generate(bitBoard));
	}

    @Test
	public void castling() {
		BitBoard bitBoard = FENUtils.getBoard("4k3/8/8/8/8/8/8/4K2R w KQkq - 0 1");
		PGNUtils.applyMove(bitBoard, "O-O");
		assertEquals("4k3/8/8/8/8/8/8/5RK1 b kq - 1 1", FENUtils.generate(bitBoard));
		
		bitBoard = FENUtils.getBoard("4k3/8/8/8/8/8/8/R3K2R w KQkq - 0 1");
		PGNUtils.applyMove(bitBoard, "O-O-O");
		assertEquals("4k3/8/8/8/8/8/8/2KR3R b kq - 1 1", FENUtils.generate(bitBoard));
		
		bitBoard = FENUtils.getBoard("r3k2r/8/8/8/8/8/8/4K3 b KQkq - 0 1");
		PGNUtils.applyMove(bitBoard, "O-O-O");
		assertEquals("2kr3r/8/8/8/8/8/8/4K3 w KQ - 1 2", FENUtils.generate(bitBoard));
	}
	
    @Test
	public void knightCheck() {
		BitBoard bitBoard = FENUtils.getBoard("7k/8/8/8/8/8/5n2/7K w - - 0 4");
		assertEquals(true, CheckDetector.isPlayerToMoveInCheck(bitBoard));

		bitBoard = FENUtils.getBoard("7k/8/n7/8/8/6n1/8/7K w - - 0 4");
		assertEquals(true, CheckDetector.isPlayerToMoveInCheck(bitBoard));

		bitBoard = FENUtils.getBoard("7k/8/n7/8/8/6n1/8/7K b - - 0 4");
		assertEquals(false, CheckDetector.isPlayerToMoveInCheck(bitBoard));

		bitBoard = FENUtils.getBoard("7k/8/n7/8/8/5n2/8/7K w - - 0 4");
		assertEquals(false, CheckDetector.isPlayerToMoveInCheck(bitBoard));
	}
	
    @Test
	public void rookCheck() {
		BitBoard bitBoard = FENUtils.getBoard("7k/8/8/8/8/8/8/r6K w - - 0 4");
		assertEquals(true, CheckDetector.isPlayerToMoveInCheck(bitBoard));
	}

    @Test
	public void bishopCheck() {
		BitBoard board = FENUtils.getBoard("b6k/8/8/8/8/8/8/7K w - - 0 4");
		assertEquals(true, CheckDetector.isPlayerToMoveInCheck(board));

		board = FENUtils.getBoard("B6k/8/8/8/8/8/8/7K w - - 0 4");
		assertEquals(false, CheckDetector.isPlayerToMoveInCheck(board));
	}
	
    @Test
	public void knightMoves() {
		BitBoard board = FENUtils.getBoard("7k/8/8/8/8/8/7P/7K w - - 0 1");
		int baseMoves = new MoveGeneratorImpl(board).getAllRemainingMoves().size();
		
		board = FENUtils.getBoard("7k/8/8/8/3N4/8/7P/7K w - - 0 1");
		assertEquals(baseMoves + 8, new MoveGeneratorImpl(board).getAllRemainingMoves().size());

		board = FENUtils.getBoard("7k/8/8/8/8/8/7P/N6K w - - 0 1");
		assertEquals(baseMoves + 2, new MoveGeneratorImpl(board).getAllRemainingMoves().size());
	}
	
    @Test
	public void pawnCaptures() {
		BitBoard board = FENUtils.getBoard("7k/7p/8/bp1n2P1/1PP1P3/8/8/7K w - - 0 1");
		List<BitBoardMove> rv = new ArrayList<BitBoardMove>(); 
		new PawnCaptureGenerator().generateMoves(board, false, -1L, rv); 
		assertEquals(4, rv.size());

		String fen = "7k/7p/8/bp1n2P1/1PP1P3/8/8/7K b - - 0 1";
		board = FENUtils.getBoard(fen);
		
		PGNUtils.applyMove(board, "h5");
		assertTrue(board.isEnPassant());
		assertEquals(7, board.getEnPassantFile());
		assertEquals(5, board.getEnPassantRank());
		
		rv.clear();
		new PawnCaptureGenerator().generateMoves(board, false, -1L, rv); 
		assertEquals(5, rv.size());
		
		board = board.reverse();
		rv.clear();
		new PawnCaptureGenerator().generateMoves(board, false, -1L, rv); 
		assertEquals(5, rv.size());
		assertTrue(board.isEnPassant());
		assertEquals(7, board.getEnPassantFile());
		assertEquals(2, board.getEnPassantRank());
	}
}
