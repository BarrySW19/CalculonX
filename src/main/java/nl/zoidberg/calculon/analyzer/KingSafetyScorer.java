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
import nl.zoidberg.calculon.engine.KingMoveGenerator;
import nl.zoidberg.calculon.model.Piece;

public class KingSafetyScorer implements PositionScorer {

    @Override
    public int scorePosition(BitBoard bitBoard, Context context) {
        return getScore(bitBoard, Piece.WHITE, context) - getScore(bitBoard, Piece.BLACK, context);
	}

	private int getScore(BitBoard bitBoard, byte color, Context context) {
        if(context.isEndgame()) {
            return 0;
        }

		int score = 0;
		long king = bitBoard.getBitmapKings(color);
		int mapIdx = Long.numberOfTrailingZeros(king);
        int kingRank = mapIdx>>>3;
        if((color == Piece.WHITE && kingRank !=0) || (color == Piece.BLACK && kingRank != 7)) {
            return 0; // Only score safety if the king is on the back rank.
        }

		long inFront = KingMoveGenerator.KING_MOVES[mapIdx]
		           & BitBoard.getRankMap(kingRank + (color == Piece.WHITE ? 1 : -1)) & bitBoard.getBitmapColor(color);
		score += 70 * (Long.bitCount(inFront & bitBoard.getBitmapPawns()));
		score += 40 * (Long.bitCount(inFront & ~bitBoard.getBitmapPawns()));

		int[] kingPos = BitBoard.toCoords(king);
		
		if(kingPos[0] == 3 || kingPos[0] == 4) {
			score -= 250;
		}
		
		return score;
	}

}
