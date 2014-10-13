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
package nl.zoidberg.calculon.engine;

import java.util.List;

import nl.zoidberg.calculon.engine.BitBoard.BitBoardMove;
import nl.zoidberg.calculon.model.Piece;


public class RookMoveGenerator extends StraightMoveGenerator {

	@Override
	public void generateMoves(BitBoard bitBoard, boolean alreadyInCheck, long potentialPins, List<BitBoardMove> rv) {
		long pieces = bitBoard.getBitmapColor() & bitBoard.getBitmapRooks();
		while(pieces != 0) {
			long nextPiece = Long.lowestOneBit(pieces);
			pieces ^= nextPiece;
			boolean safeFromCheck = ((nextPiece & potentialPins) == 0) & !alreadyInCheck;
			
			int mapIdx = Long.numberOfTrailingZeros(nextPiece);
			makeUpBoardMoves(bitBoard, nextPiece, Bitmaps.maps2[Bitmaps.BM_U][mapIdx], 8, alreadyInCheck, safeFromCheck, rv);
			makeUpBoardMoves(bitBoard, nextPiece, Bitmaps.maps2[Bitmaps.BM_R][mapIdx], 1, alreadyInCheck, safeFromCheck, rv);
			makeDownBoardMoves(bitBoard, nextPiece, Bitmaps.maps2[Bitmaps.BM_L][mapIdx], 1, alreadyInCheck, safeFromCheck, rv);
			makeDownBoardMoves(bitBoard, nextPiece, Bitmaps.maps2[Bitmaps.BM_D][mapIdx], 8, alreadyInCheck, safeFromCheck, rv);
		}
	}

	@Override
	protected byte getPieceType() {
		return Piece.ROOK;
	}
}