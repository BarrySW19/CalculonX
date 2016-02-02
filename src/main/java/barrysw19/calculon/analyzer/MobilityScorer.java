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
package barrysw19.calculon.analyzer;

import barrysw19.calculon.model.Piece;
import barrysw19.calculon.engine.BitBoard;
import barrysw19.calculon.engine.PreGeneratedMoves;
import barrysw19.calculon.util.BitIterable;

public class MobilityScorer implements PositionScorer {
    public static final int PER_SQUARE = 50;

    @Override
    public int scorePosition(BitBoard bitBoard, Context context) {
        return getScore(bitBoard, Piece.WHITE) - getScore(bitBoard, Piece.BLACK);
    }

    private int getScore(BitBoard bitBoard, byte color) {
        int score = 0;

        long straightPieces = bitBoard.getBitmapColor(color) & (bitBoard.getBitmapQueens() | bitBoard.getBitmapRooks());
        for(long nextPiece: BitIterable.of(straightPieces)) {
            int index = Long.numberOfTrailingZeros(nextPiece);
            for(long[] nextDirection: PreGeneratedMoves.STRAIGHT_MOVES[index]) {
                for(long nextSquare: nextDirection) {
                    if((bitBoard.getBitmapAll() & nextSquare) != 0) {
                        break;
                    }
                    score += PER_SQUARE;
                }
            }
        }

        long diagonalPieces = bitBoard.getBitmapColor(color) & (bitBoard.getBitmapQueens() | bitBoard.getBitmapBishops());
        for(long nextPiece: BitIterable.of(diagonalPieces)) {
            int index = Long.numberOfTrailingZeros(nextPiece);
            for(long[] nextDirection: PreGeneratedMoves.DIAGONAL_MOVES[index]) {
                for(long nextSquare: nextDirection) {
                    if((bitBoard.getBitmapAll() & nextSquare) != 0) {
                        break;
                    }
                    score += PER_SQUARE;
                }
            }
        }

        return score;
    }
}