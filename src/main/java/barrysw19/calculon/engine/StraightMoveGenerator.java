/**
 * Calculon - A Java chess-engine.
 *
 * Copyright (C) 2008-2016 Barry Smith
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

import java.util.List;

public abstract class StraightMoveGenerator extends PieceMoveGenerator {

	protected abstract byte getPieceType();
	
	protected void makeBoardMoves(BitBoard bitBoard, long source, long[] destinations,
								boolean alreadyInCheck, boolean safeFromCheck, List<BitBoardMove> rv)
    {
        final byte player = bitBoard.getPlayer();
        boolean isCapture = false;

        int idx = 0;
        while( !isCapture && idx < destinations.length) {
            long moveTo = destinations[idx];
            if((moveTo & bitBoard.getBitmapColor(player)) != 0) {
                return;
            }

            isCapture = (moveTo & bitBoard.getBitmapOppColor(player)) != 0;
            BitBoardMove bbMove;
            if(isCapture) {
                // This is a capturing move.
                bbMove = BitBoard.generateCapture(
                        source, moveTo, player, getPieceType(), bitBoard.getPiece(moveTo));
            } else {
                bbMove = BitBoard.generateMove(source, moveTo, player, getPieceType());
            }

            if(safeFromCheck) {
                rv.add(bbMove);
            } else {
                bitBoard.makeMove(bbMove);
                if( ! CheckDetector.isPlayerJustMovedInCheck(bitBoard, ! alreadyInCheck)) {
                    rv.add(bbMove);
                }
                bitBoard.unmakeMove();
            }

            idx++;
        }
	}
}
