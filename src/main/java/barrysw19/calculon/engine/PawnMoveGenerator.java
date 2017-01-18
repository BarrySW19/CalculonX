/*
 * Calculon - A Java chess-engine.
 *
 * Copyright (C) 2008-2017 Barry Smith
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

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class PawnMoveGenerator implements PieceMoveGenerator {

    @Override
    public Iterator<BitBoardMove> iterator(final MoveGeneratorImpl.MoveGeneratorContext context) {
        return iteratorIntern(context, false);
    }

    @Override
    public Iterator<BitBoardMove> generateThreatMoves(final MoveGeneratorImpl.MoveGeneratorContext context) {
        return iteratorIntern(context, true);
    }

    private Iterator<BitBoardMove> iteratorIntern(final MoveGeneratorImpl.MoveGeneratorContext context, final boolean threatsOnly) {
        final byte playerIdx = context.getBitBoard().getPlayer();
        final ShiftStrategy shiftStrategy = BitBoard.getShiftStrategy(playerIdx);
        final long myPawns = context.getBitBoard().getBitmapPawns() & context.getBitBoard().getBitmapColor(playerIdx);

        // Calculate pawns with nothing in front of them
        final long movablePawns = shiftStrategy.shiftBackwardOneRank(
                shiftStrategy.shiftForwardOneRank(myPawns) & ~context.getBitBoard().getAllPieces());

        if(movablePawns == 0) {
            return Collections.emptyIterator();
        }

        return new PawnMoveIterator(context, movablePawns, threatsOnly);
    }

    private static class PawnMoveIterator extends AbstractMoveIterator {
        private final MoveGeneratorImpl.MoveGeneratorContext context;
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
        private final boolean threatsOnly;

        PawnMoveIterator(final MoveGeneratorImpl.MoveGeneratorContext context, final long piecesMap, final boolean threatsOnly) {
            this.context = context;
            this.threatsOnly = threatsOnly;
            this.bitBoard = context.getBitBoard();
            this.alreadyInCheck = context.isAlreadyInCheck();
            this.potentialPins = context.getPotentialPins();
            this.player = bitBoard.getPlayer();
            this.shiftStrategy =  BitBoard.getShiftStrategy(this.player);

            pieces = BitIterable.of(piecesMap).iterator();
            nextPiece();
        }

        private void nextPiece() {
            currentPiece = pieces.next();
            safeFromCheck = ((currentPiece & potentialPins) == 0) & !alreadyInCheck;

            if(threatsOnly && (currentPiece & context.getPotentialDiscoveries()) == 0) {
                moves = BitIterable.empty().iterator();
                return;
            }

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
                if(safeFromCheck || CheckDetector.isMoveLegal(pushTwo, bitBoard, !alreadyInCheck)) {
                    return pushTwo;
                } else {
                    return fetchNextMove();
                }
            }

            BitBoardMove pushPawn = BitBoard.generateMove(currentPiece, nextMove, player, Piece.PAWN);
            if(safeFromCheck || CheckDetector.isMoveLegal(pushPawn, bitBoard, !alreadyInCheck)) {
                if ((nextMove & BitBoard.FINAL_RANKS) != 0) {
                    queuedMoves.addAll(BitBoard.generatePromotions(currentPiece, nextMove, player));
                    return fetchNextMove();
                }
                return pushPawn;
            }
            return fetchNextMove();
        }
    }
}
