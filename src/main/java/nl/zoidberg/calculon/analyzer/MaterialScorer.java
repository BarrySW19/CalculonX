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

public class MaterialScorer implements PositionScorer {
    public static final int VALUE_QUEEN     = 9000;
    public static final int VALUE_ROOK      = 5000;
    public static final int VALUE_BISHOP    = 3250;
    public static final int VALUE_KNIGHT    = 3100;
    public static final int VALUE_PAWN      = 1000;

    @Override
    public int scorePosition(BitBoard bitBoard, Context context) {
		int score = 0;

		long bitmapWhite = bitBoard.getBitmapWhite();
        long bitmapBlack = bitBoard.getBitmapBlack();

		score += VALUE_QUEEN * (Long.bitCount(bitmapWhite & bitBoard.getBitmapQueens())
				- Long.bitCount(bitmapBlack & bitBoard.getBitmapQueens()));
		score += VALUE_ROOK * (Long.bitCount(bitmapWhite & bitBoard.getBitmapRooks())
				- Long.bitCount(bitmapBlack & bitBoard.getBitmapRooks()));
		score += VALUE_BISHOP * (Long.bitCount(bitmapWhite & bitBoard.getBitmapBishops())
				- Long.bitCount(bitmapBlack & bitBoard.getBitmapBishops()));
		score += VALUE_KNIGHT * (Long.bitCount(bitmapWhite & bitBoard.getBitmapKnights())
				- Long.bitCount(bitmapBlack & bitBoard.getBitmapKnights()));
		score += VALUE_PAWN * (Long.bitCount(bitmapWhite & bitBoard.getBitmapPawns())
				- Long.bitCount(bitmapBlack & bitBoard.getBitmapPawns()));
		
		return score;
	}
}
