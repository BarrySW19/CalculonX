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

public class PawnStructureScorer implements PositionScorer {
	
	public static int S_ISLAND 		= 100;
	public static int S_ISOLATED 	= 100;
	public static int S_DOUBLED 	= 100;
	
	private static int[] S_ADVANCE = { 20, 100, 200, 400, };

    @Override
    public int scorePosition(BitBoard bitBoard, Context context) {
		long whitePawns = bitBoard.getBitmapWhite()&bitBoard.getBitmapPawns();
		long blackPawns = bitBoard.getBitmapBlack()&bitBoard.getBitmapPawns();

		int score = 0;
		score += countIslands(blackPawns) * S_ISLAND;
		score -= countIslands(whitePawns) * S_ISLAND;
		
		score += getIsolatedCount(blackPawns) * S_ISOLATED;
		score -= getIsolatedCount(whitePawns) * S_ISOLATED;
		
		score += getDoubledScore(whitePawns, blackPawns);
		score += getAdvanceScore(whitePawns, blackPawns);
		
		return score;
	}
	
	private int getAdvanceScore(long whitePawns, long blackPawns) {
		
		int score = 0;
//		for(int rank = 3; rank < 7; rank++) {
//			score += Long.bitCount(whitePawns&BitBoard.getRankMap(rank)) * S_ADVANCE[rank-3];
//			score -= Long.bitCount(blackPawns&BitBoard.getRankMap(7-rank)) * S_ADVANCE[rank-3];
//		}
		
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
	
	private int getIsolatedCount(long pawns) {
		int count = 0;
		long prevFile = 0;
		long thisFile = 0;
		for(int file = 0; file < 8; file++) {
			if(file == 0) {
				thisFile = pawns & BitBoard.getFileMap(file);
			}
			long nextFile = (file == 7 ? 0 : pawns & BitBoard.getFileMap(file+1));
			
			if(thisFile != 0 && prevFile == 0 && nextFile == 0) {
				count += Long.bitCount(thisFile);
			}
			prevFile = thisFile;
			thisFile = nextFile;
		}
		return count;
	}
}
