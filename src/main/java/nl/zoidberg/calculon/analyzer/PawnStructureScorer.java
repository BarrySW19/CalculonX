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

public class PawnStructureScorer implements PositionScorer {
	public final static long CENTRE = 0b00000000_00000000_00000000_00011000_00011000_00000000_00000000_00000000L;
	public final static int S_ISLAND 		= 100;
	public final static int S_ISOLATED 	= 100;
    public final static int S_DOUBLED 	= 100;
    public final static int S_CENTRE 	    = 150;

    @Override
    public int scorePosition(BitBoard bitBoard, Context context) {
		final long whitePawns = bitBoard.getBitmapPawns(Piece.WHITE);
		final long blackPawns = bitBoard.getBitmapPawns(Piece.BLACK);

		int score = 0;
		score += countIslands(blackPawns) * S_ISLAND;
		score -= countIslands(whitePawns) * S_ISLAND;
		
        score += Long.bitCount(context.getIsolatedPawns() & bitBoard.getBitmapBlack()) * S_ISOLATED;
        score -= Long.bitCount(context.getIsolatedPawns() & bitBoard.getBitmapWhite()) * S_ISOLATED;

		score += getDoubledScore(whitePawns, blackPawns);

        // Bonus for pawns in the centre
        score += Long.bitCount(whitePawns & CENTRE) * S_CENTRE;
        score -= Long.bitCount(blackPawns & CENTRE) * S_CENTRE;

		return score;
	}
	
	private int countIslands(long pawns) {
		boolean inSea = true;
		int count = 0;
		for(int file = 0; file < 8; file++) {
			long pawnsOnFile = pawns & BitBoard.getFileMap(file);
			if(pawnsOnFile != 0 && inSea) {
				count++;
				inSea = false;
			} else if(pawnsOnFile == 0 && !inSea) {
				inSea = true;
			}
		}
		return count;
	}
	
	private int getDoubledScore(long whitePawns, long blackPawns) {
		int score = 0;
		
		for(int file = 0; file < 8; file++) {
			int wCount = Long.bitCount(whitePawns & BitBoard.getFileMap(file));
			int bCount = Long.bitCount(blackPawns & BitBoard.getFileMap(file));
			score -= (wCount > 1 ? (wCount-1) * S_DOUBLED : 0);
			score += (bCount > 1 ? (bCount-1) * S_DOUBLED : 0);
		}
		return score;
	}
}
