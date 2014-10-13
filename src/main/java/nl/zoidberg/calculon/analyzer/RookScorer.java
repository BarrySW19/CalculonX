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
package nl.zoidberg.calculon.analyzer;

import nl.zoidberg.calculon.engine.BitBoard;
import nl.zoidberg.calculon.model.Piece;

public class RookScorer implements PositionScorer {

	public static final int OPEN_FILE_SCORE = 150;
	public static final int CONNECTED_BONUS = 150;

    @Override
    public int scorePosition(BitBoard bitBoard, Context context) {
        return getScore(bitBoard, Piece.WHITE) - getScore(bitBoard, Piece.BLACK);
	}
	
	private int getScore(BitBoard bitBoard, byte color) {
		int score = 0;
		
		long rookMap = (bitBoard.getBitmapColor(color) & bitBoard.getBitmapRooks());
		while(rookMap != 0) {
			long nextRook = Long.lowestOneBit(rookMap);
			rookMap ^= nextRook;
			int file = (int) (Long.numberOfTrailingZeros(nextRook) % 8);
			if((bitBoard.getBitmapColor(color) & bitBoard.getBitmapPawns() & BitBoard.getFileMap(file)) == 0) {
				score += OPEN_FILE_SCORE;
			}
		}
		
		rookMap = (bitBoard.getBitmapColor(color) & bitBoard.getBitmapRooks());
		if(Long.bitCount(rookMap) == 2) {
			int[] rook1 = BitBoard.toCoords(Long.highestOneBit(rookMap));
			int[] rook2 = BitBoard.toCoords(Long.lowestOneBit(rookMap));
			if(rook1[0] == rook2[0]) {
				long connMask = 0;
				for(int i = Math.min(rook1[1], rook2[1]) + 1; i < Math.max(rook1[1], rook2[1]); i++) {
					connMask |= 1L<<(i<<3)<<rook1[0];
				}
				if((connMask & bitBoard.getAllPieces()) == 0) {
					score += CONNECTED_BONUS;
				}
			} else if(rook1[1] == rook2[1]) {
				long connMask = 0;
				for(int i = Math.min(rook1[0], rook2[0]) + 1; i < Math.max(rook1[0], rook2[0]); i++) {
					connMask |= 1L<<(rook1[1]<<3)<<i;
				}
				if((connMask & bitBoard.getAllPieces()) == 0) {
					score += CONNECTED_BONUS;
				}
			}
		}
				
		return score;
	}
}
