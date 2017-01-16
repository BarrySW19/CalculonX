/**
 * Calculon - A Java chess-engine.
 *
 * Copyright (C) 2008-2010 Barry Smith
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
package barrysw19.calculon.engine;

import barrysw19.calculon.engine.BitBoard.BitBoardMove;
import barrysw19.calculon.model.Piece;

import java.util.Iterator;
import java.util.List;

public class PawnCaptureGenerator extends PieceMoveGenerator {

	@Override
	public Iterator<BitBoardMove> iterator(final BitBoard bitBoard, final boolean alreadyInCheck, final long potentialPins) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void generateMoves(BitBoard bitBoard, boolean alreadyInCheck, long potentialPins, List<BitBoardMove> rv) {
		byte player = bitBoard.getPlayer();
		
		long myPawns = bitBoard.getBitmapColor(player) & bitBoard.getBitmapPawns();
		long enemyPieces = bitBoard.getBitmapOppColor(player);
		long epLocation = -1;
		if(bitBoard.isEnPassant()) {
			// Just treat the enpassant square as another enemy piece.
			epLocation = 1L<<(bitBoard.getEnPassantRank()<<3)<<bitBoard.getEnPassantFile();
			enemyPieces |= epLocation;
		}
		
		long captureRight = player == Piece.WHITE
				? (enemyPieces & ~BitBoard.getFileMap(0))>>>9 : (enemyPieces & ~BitBoard.getFileMap(0))<<7;
		long captureLeft = player == Piece.WHITE
				? (enemyPieces & ~BitBoard.getFileMap(7))>>>7 : (enemyPieces & ~BitBoard.getFileMap(7))<<9;
		
		myPawns &= (captureLeft | captureRight);
		
		while(myPawns != 0) {
			long nextPiece = Long.lowestOneBit(myPawns);
			myPawns ^= nextPiece;
			boolean safeFromCheck = ((nextPiece & potentialPins) == 0) & !alreadyInCheck;
			
			if((nextPiece & captureLeft) != 0) {
				long captured = (player == Piece.WHITE ? nextPiece<<7 : nextPiece>>>9);
				tryCaptures(bitBoard, player, nextPiece, captured, epLocation, alreadyInCheck, safeFromCheck, rv);
			}
			if((nextPiece & captureRight) != 0) {
				long captured = (player == Piece.WHITE ? nextPiece<<9 : nextPiece>>>7);
				tryCaptures(bitBoard, player, nextPiece, captured, epLocation, alreadyInCheck, safeFromCheck, rv);
			}
		}
	}
	
	private void tryCaptures(BitBoard bitBoard, byte player, long nextPiece,
			long captured, long epLocation, boolean alreadyInCheck, boolean safeFromCheck, List<BitBoardMove> rv) {
		
		BitBoardMove bbMove;
		if(captured == epLocation) {
			bbMove = BitBoard.generateEnPassantCapture(nextPiece, captured, player);
		} else {
			bbMove = BitBoard.generateCapture(
					nextPiece, captured, player, Piece.PAWN, bitBoard.getPiece(captured));
		}
		
		if(safeFromCheck) {
			if((captured & BitBoard.FINAL_RANKS) == 0) {
				rv.add(bbMove);
			} else {
				rv.add(BitBoard.generateCaptureAndPromote(
						nextPiece, captured, player, bitBoard.getPiece(captured), Piece.QUEEN));
				rv.add(BitBoard.generateCaptureAndPromote(
						nextPiece, captured, player, bitBoard.getPiece(captured), Piece.ROOK));
				rv.add(BitBoard.generateCaptureAndPromote(
						nextPiece, captured, player, bitBoard.getPiece(captured), Piece.BISHOP));
				rv.add(BitBoard.generateCaptureAndPromote(
						nextPiece, captured, player, bitBoard.getPiece(captured), Piece.KNIGHT));
			}
		} else {
			bitBoard.makeMove(bbMove);
			if( ! CheckDetector.isPlayerJustMovedInCheck(bitBoard, ! alreadyInCheck)) {
				bitBoard.unmakeMove();
				if((captured & BitBoard.FINAL_RANKS) == 0) {
					rv.add(bbMove);
				} else {
					rv.add(BitBoard.generateCaptureAndPromote(
							nextPiece, captured, player, bitBoard.getPiece(captured), Piece.QUEEN));
					rv.add(BitBoard.generateCaptureAndPromote(
							nextPiece, captured, player, bitBoard.getPiece(captured), Piece.ROOK));
					rv.add(BitBoard.generateCaptureAndPromote(
							nextPiece, captured, player, bitBoard.getPiece(captured), Piece.BISHOP));
					rv.add(BitBoard.generateCaptureAndPromote(
							nextPiece, captured, player, bitBoard.getPiece(captured), Piece.KNIGHT));
				}
			} else {
				bitBoard.unmakeMove();
			}
		}
	}
}
