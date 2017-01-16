/*
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

import java.util.*;

public class PawnMoveGenerator extends PieceMoveGenerator {

    @Override
    public Iterator<BitBoardMove> iterator(final BitBoard bitBoard, final boolean alreadyInCheck, final long potentialPins) {
        byte playerIdx = bitBoard.getPlayer();
        ShiftStrategy shiftStrategy = BitBoard.getShiftStrategy(playerIdx);
        long myPawns = bitBoard.getBitmapPawns() & bitBoard.getBitmapColor(playerIdx);

        // Calculate pawns with nothing in front of them
        long movablePawns = shiftStrategy.shiftBackwardOneRank(
                shiftStrategy.shiftForwardOneRank(myPawns) & ~bitBoard.getAllPieces());

        if(movablePawns == 0) {
            return Collections.emptyIterator();
        }

        return new PawnMoveIterator(bitBoard, movablePawns, alreadyInCheck, potentialPins);
    }

    private static class PawnMoveIterator extends AbstractMoveIterator {
        private final BitBoard bitBoard;
        private final boolean alreadyInCheck;
        private final long potentialPins;
        private Iterator<Long> pieces;
        private long currentPiece;
        private boolean safeFromCheck;
        private Iterator<Long> moves;
        private final ShiftStrategy shiftStrategy;
        private final List<BitBoardMove> queuedMoves = new LinkedList<>();
        private final byte player;

        PawnMoveIterator(final BitBoard bitBoard, final long piecesMap, final boolean alreadyInCheck, final long potentialPins) {
            this.bitBoard = bitBoard;
            this.alreadyInCheck = alreadyInCheck;
            this.potentialPins = potentialPins;
            this.player = bitBoard.getPlayer();
            this.shiftStrategy =  BitBoard.getShiftStrategy(this.player);

            pieces = BitIterable.of(piecesMap).iterator();
            nextPiece();
        }

        private void nextPiece() {
            currentPiece = pieces.next();
            safeFromCheck = ((currentPiece & potentialPins) == 0) & !alreadyInCheck;
            long movesMap = shiftStrategy.shiftForward(currentPiece, 1);
            if((currentPiece & BitBoard.getRankMap(shiftStrategy.getPawnStartRank())) != 0) {
                movesMap |= shiftStrategy.shiftForward(currentPiece, 2) & ~bitBoard.getAllPieces();
            }

            moves = BitIterable.of(movesMap).iterator();
        }

        @Override
        BitBoardMove fetchNextMove() {
            if(!queuedMoves.isEmpty()) {
                return queuedMoves.remove(0);
            }

            if(!moves.hasNext() && !pieces.hasNext()) {
                return null;
            }

            if(!moves.hasNext()) {
                nextPiece();
                return this.fetchNextMove();
            }

            long nextMove = moves.next();

            if(nextMove == shiftStrategy.shiftForward(currentPiece, 2)) {
                BitBoardMove pushTwo = BitBoard.generateDoubleAdvanceMove(currentPiece, nextMove, player);
                if(safeFromCheck || isSafeAfterMove(bitBoard, pushTwo, alreadyInCheck)) {
                    return pushTwo;
                } else {
                    return fetchNextMove();
                }
            }

            BitBoardMove pushPawn = BitBoard.generateMove(currentPiece, nextMove, player, Piece.PAWN);
            if(safeFromCheck || isSafeAfterMove(bitBoard, pushPawn, alreadyInCheck)) {
                if ((nextMove & BitBoard.FINAL_RANKS) != 0) {
                    queuedMoves.addAll(BitBoard.generatePromotions(currentPiece, nextMove, player));
                    return fetchNextMove();
                }
                return pushPawn;
            }
            return fetchNextMove();
        }
    }

    private static boolean isSafeAfterMove(final BitBoard bitBoard, final BitBoardMove bitBoardMove, final boolean alreadyInCheck) {
        bitBoard.makeMove(bitBoardMove);
        boolean inCheck = CheckDetector.isPlayerJustMovedInCheck(bitBoard, !alreadyInCheck);
        bitBoard.unmakeMove();
        return !inCheck;
    }
}
