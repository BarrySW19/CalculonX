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
import barrysw19.calculon.model.Piece;
import barrysw19.calculon.util.BitIterable;

import java.util.Iterator;
import java.util.List;

public class PawnMoveGenerator extends PieceMoveGenerator {

    @Override
    public Iterator<BitBoardMove> iterator(final BitBoard bitBoard, final boolean alreadyInCheck, final long potentialPins) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void generateMoves(BitBoard bitBoard, boolean alreadyInCheck, long potentialPins, List<BitBoardMove> rv) {
        byte playerIdx = bitBoard.getPlayer();
        ShiftStrategy shiftStrategy = BitBoard.getShiftStrategy(playerIdx);
        long myPawns = bitBoard.getBitmapPawns() & bitBoard.getBitmapColor(playerIdx);

        // Calculate pawns with nothing in front of them
        long movablePawns = shiftStrategy.shiftBackwardOneRank(
                shiftStrategy.shiftForwardOneRank(myPawns) & ~bitBoard.getAllPieces());

        // As we know which pawns can move one, see which of them can move two.
        long doubleMovePawns = movablePawns & BitBoard.getRankMap(shiftStrategy.getPawnStartRank());
        doubleMovePawns = shiftStrategy.shiftBackward(
                shiftStrategy.shiftForward(doubleMovePawns, 2) & ~bitBoard.getAllPieces(), 2);

        for(long nextPawn: BitIterable.of(movablePawns)) {
            boolean doubleMove = (doubleMovePawns & nextPawn) != 0;
            boolean safeFromCheck = ((nextPawn & potentialPins) == 0) & !alreadyInCheck;

            long toSquare = shiftStrategy.shiftForwardOneRank(nextPawn);
            if (safeFromCheck) {
                if ((toSquare & BitBoard.FINAL_RANKS) != 0) {
                    rv.addAll(BitBoard.generatePromotions(nextPawn, toSquare, playerIdx));
                } else {
                    rv.add(BitBoard.generateMove(nextPawn, toSquare, playerIdx, Piece.PAWN));
                    if(doubleMove) {
                        rv.add(BitBoard.generateDoubleAdvanceMove(
                                nextPawn, shiftStrategy.shiftForward(nextPawn, 2), playerIdx));
                    }
                }
            } else {
                BitBoardMove bbMove = BitBoard.generateMove(nextPawn, toSquare, playerIdx, Piece.PAWN);
                bitBoard.makeMove(bbMove);
                if (!CheckDetector.isPlayerJustMovedInCheck(bitBoard, !alreadyInCheck)) {
                    if ((toSquare & BitBoard.FINAL_RANKS) != 0) {
                        rv.addAll(BitBoard.generatePromotions(nextPawn, toSquare, playerIdx));
                    } else {
                        rv.add(bbMove);
                    }
                }
                bitBoard.unmakeMove();

                if(doubleMove) {
                    BitBoardMove pushTwo = BitBoard.generateDoubleAdvanceMove(
                            nextPawn, shiftStrategy.shiftForward(nextPawn, 2), playerIdx);
                    bitBoard.makeMove(pushTwo);
                    if(!CheckDetector.isPlayerJustMovedInCheck(bitBoard, !alreadyInCheck)) {
                        rv.add(pushTwo);
                    }
                    bitBoard.unmakeMove();
                }
            }
        }
    }
}
