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

public class BishopPairScorer implements PositionScorer {

    @Override
    public int scorePosition(BitBoard bitBoard, Context context) {
        return getScore(bitBoard, Piece.WHITE) - getScore(bitBoard, Piece.BLACK);
	}

	private int getScore(BitBoard bitBoard, byte color) {
		long colorMap = bitBoard.getBitmapColor(color);
		if(Long.bitCount(colorMap & bitBoard.getBitmapBishops()) >= 2) {
			return (colorMap & bitBoard.getBitmapQueens()) == 0 ? 150 : 300;
		}
		return 0;
	}
}
