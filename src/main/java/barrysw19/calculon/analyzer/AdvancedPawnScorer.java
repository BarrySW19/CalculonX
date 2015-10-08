package barrysw19.calculon.analyzer;

import barrysw19.calculon.engine.BitBoard;
import barrysw19.calculon.model.Piece;
import barrysw19.calculon.util.BitIterable;

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
public class AdvancedPawnScorer implements PositionScorer {

    /*
     * Give some sort of bonus - to encourage the engine to advance pawns - there should actually be some more
     * detail worked into this as, for example, connected passed pawns on the 7th are about equal in value to
     * a rook. These values just encourage the engine to advance pawns and work it out from there.
     */
    public static final int[] RANK_SCORE = { 0, 0, 5, 20, 50, 150, 350, 0 };

    @Override
    public int scorePosition(BitBoard bitBoard, Context context) {
        if(!context.isEndgame()) {
            return 0;
        }
        return scorePosition(bitBoard, Piece.WHITE) - scorePosition(bitBoard, Piece.BLACK);
    }

    private int scorePosition(BitBoard bitBoard, final byte color) {
        return BitIterable.of(bitBoard.getBitmapPawns(color)).longStream()
                .mapToInt((pawn) -> scorePawn(pawn, color)).sum();
    }

    private static int scorePawn(long pawn, byte color) {
        int rank = BitBoard.toCoords(pawn)[1];
        rank = (color == Piece.WHITE ? rank : 7 - rank);
        return RANK_SCORE[rank];
    }
}
