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

public class KingCentralisationScorer implements PositionScorer {
    private static final int[] SCORES = { 0, 2, 5, 10, 10, 5, 2, 0 };

    @Override
    public int scorePosition(BitBoard bitBoard, Context context) {
        return getScore(bitBoard, Piece.WHITE, context) - getScore(bitBoard, Piece.BLACK, context);
	}

	private int getScore(BitBoard bitBoard, byte color, Context context) {
        if( ! context.isEndgame()) {
            return 0;
        }

		int score = 0;
		long king = bitBoard.getBitmapColor(color) & bitBoard.getBitmapKings();
		int[] kingPos = BitBoard.toCoords(king);
		
		return score += (SCORES[kingPos[0]] + SCORES[kingPos[1]]);
	}
}
