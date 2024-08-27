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
package barrysw19.calculon.engine;

import barrysw19.calculon.model.Piece;
import barrysw19.calculon.util.BitIterable;

public class CheckDetector {

	/**
	 * Tests whether the player who just moved has left themselves in check - i.e. it was an illegal move.
	 * 
	 * @param bitBoard The chess board.
	 * @return True if the player moved into check.
	 */
	public static boolean isPlayerJustMovedInCheck(BitBoard bitBoard) {
    	byte color = bitBoard.getPlayer() == Piece.WHITE ? Piece.BLACK : Piece.WHITE;
    	return inCheck(bitBoard, color, false);
	}
	
    public static boolean isPlayerJustMovedInCheck(BitBoard bitBoard, boolean pinCheckOnly) {
    	byte color = bitBoard.getPlayer() == Piece.WHITE ? Piece.BLACK : Piece.WHITE;
    	return inCheck(bitBoard, color, pinCheckOnly);
    }

    public static boolean isMoveLegal(BitBoard.BitBoardMove move, BitBoard bitBoard, boolean pinCheckOnly) {
        bitBoard.makeMove(move);
        boolean inCheck = isPlayerJustMovedInCheck(bitBoard, pinCheckOnly);
        bitBoard.unmakeMove();
        return !inCheck;
    }

    public static boolean isMoveLegal(BitBoard.BitBoardMove move, BitBoard bitBoard) {
        return isMoveLegal(move, bitBoard, false);
    }

	public static boolean isPlayerToMoveInCheck(BitBoard bitBoard) {
    	return inCheck(bitBoard, bitBoard.getPlayer(), false);
	}
	
    /**
     * The pin check flag can be used for tests when a piece other than the king moved,
     * possibly exposing the king to check. As this sort of check could only be by a
     * bishop, rook or queen it is not necessary to check for checks by enemy pawns,
     * knights or king.
     * 
     * @param bitBoard The board reference
     * @param color The color to check
     * @param pinCheckOnly Only check pin type checks
     * @return True if in check
     */
    private static boolean inCheck(BitBoard bitBoard, final byte color, boolean pinCheckOnly) {
    	final long kingMap = bitBoard.getBitmapColor(color) & bitBoard.getBitmapKings();
    	final int kingIdx = Long.numberOfTrailingZeros(kingMap);
        final long allEnemyPieces = bitBoard.getBitmapOppColor(color);

        if( ! pinCheckOnly) {
	        long enemyPawns = allEnemyPieces & bitBoard.getBitmapPawns();
	        
	        // Theoretically, we would incorrectly find pawns on the 1st/8th rank with this, but there shouldn't
	        // be any there :)
	        long checkPawns = color == Piece.WHITE
	        		? ((kingMap & ~BitBoard.getFileMap(7))<<9 | (kingMap & ~BitBoard.getFileMap(0))<<7)
	        		: ((kingMap & ~BitBoard.getFileMap(0))>>>9 | (kingMap & ~BitBoard.getFileMap(7))>>>7);
	        
	        if((enemyPawns & checkPawns) != 0) {
	        	return true;
	        }
	        
	        long kingMoves = Bitmaps.KING_MOVES[kingIdx];
	        if((kingMoves & bitBoard.getBitmapKings() & allEnemyPieces) != 0) {
	        	return true;
	        }
	        
	        long knightMoves = KnightMoveGenerator.KNIGHT_MOVES[kingIdx];
	        if((knightMoves & bitBoard.getBitmapKnights() & allEnemyPieces) != 0) {
	        	return true;
	        }
        }

        final long lineAttackers = (allEnemyPieces & Bitmaps.cross2Map[kingIdx] & (bitBoard.getBitmapRooks()|bitBoard.getBitmapQueens()));
        final long diagAttackers = (allEnemyPieces & Bitmaps.diag2Map[kingIdx] & (bitBoard.getBitmapBishops()|bitBoard.getBitmapQueens()));

        for(long pos: BitIterable.of(lineAttackers|diagAttackers)) {
            if((Bitmaps.SLIDE_MOVES[kingIdx][Long.numberOfTrailingZeros(pos)] & bitBoard.getAllPieces()) == 0) {
                return true;
            }
        }

        return false;
    }
}
