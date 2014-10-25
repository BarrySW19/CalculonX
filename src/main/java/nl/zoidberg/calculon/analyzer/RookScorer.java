/**
 * Calculon - A Java chess-engine.
 *
 * Copyright (C) 2008-2014 Barry Smith
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
    public static final int OPEN_FILE_SCORE         = 150;
    public static final int HALF_OPEN_FILE_SCORE    = 60;
    public static final int CONNECTED_BONUS         = 100;
    public static final int PIGS_ON_THE_SEVENTH     = 120;
    public static final int ISOLATED_ATTACK_SCORE   = 160;

    @Override
    public int scorePosition(BitBoard bitBoard, Context context) {
        return getScore(bitBoard, Piece.WHITE, context) - getScore(bitBoard, Piece.BLACK, context);
	}
	
	private int getScore(BitBoard bitBoard, byte color, Context context) {
		int score = 0;

        // First, give rooks a good score if they stand on (half) open files.
		long rookMap = (bitBoard.getBitmapColor(color) & bitBoard.getBitmapRooks());
		while(rookMap != 0) {
			long nextRook = Long.lowestOneBit(rookMap);
			rookMap ^= nextRook;
			int file = Long.numberOfTrailingZeros(nextRook) % 8;
            long allPawnsOnFile = bitBoard.getBitmapPawns() & BitBoard.getFileMap(file);
            if(allPawnsOnFile == 0) {
                score += OPEN_FILE_SCORE;
            } else {
                // Not on an open file - check if it's half open.
                if((bitBoard.getBitmapColor(color) & allPawnsOnFile) == 0) {
                    boolean isolated = (allPawnsOnFile & context.getIsolatedPawns()) != 0;
                    // TODO This could be improved by considering what type of pawn is on the half open file.
                    // TODO Is it backward or isolated? If so, score should be higher.
                    score += isolated ? ISOLATED_ATTACK_SCORE : HALF_OPEN_FILE_SCORE;
                }
            }
		}

        // Next, give rooks a bonus if they are connected or if they are "pigs on the 7th".
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

                // Are they pigs on the 7th?
                if(color == Piece.WHITE && rook1[1] == 6 || color == Piece.BLACK && rook1[1] == 1) {
                    score += PIGS_ON_THE_SEVENTH;
                }
			}
		}
				
		return score;
	}
}
