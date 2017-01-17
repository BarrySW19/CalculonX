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
package barrysw19.calculon.engine;

import barrysw19.calculon.engine.BitBoard.BitBoardMove;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class PieceMoveGenerator {

    public abstract Iterator<BitBoardMove> iterator(final BitBoard bitBoard, final boolean alreadyInCheck, final long potentialPins);

    /**
     * Generate threatening moves to use in quiescence searching.
     */
    public void generateThreatMoves(BitBoard bitBoard, boolean alreadyInCheck, long potentialPins, List<BitBoardMove> rv) {
        final List<BitBoardMove> tempMoves = new ArrayList<>();
//        generateMoves(bitBoard, alreadyInCheck, potentialPins, tempMoves);
        for (Iterator<BitBoardMove> iter = iterator(bitBoard, alreadyInCheck, potentialPins); iter.hasNext(); ) {
            BitBoardMove move = iter.next();
            if (move.isCapture() || move.isPromotion()) {
                rv.add(move);
                continue;
            }
            bitBoard.makeMove(move);
            if (CheckDetector.isPlayerToMoveInCheck(bitBoard)) {
                rv.add(move);
            }
            bitBoard.unmakeMove();
        }
    }
}
