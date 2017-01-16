/*
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
import barrysw19.calculon.util.BitIterable;

import java.util.Collections;
import java.util.Iterator;
import java.util.function.ToLongFunction;

class StraightMoveGenerator extends PieceMoveGenerator {
    private final ToLongFunction<BitBoard> fSelectPieces;
    private final long[][][] slidingMoves;
    private final byte pieceType;

    StraightMoveGenerator(ToLongFunction<BitBoard> fSelectPieces, long[][][] slidingMoves, byte pieceType) {
        this.fSelectPieces = fSelectPieces;
        this.slidingMoves = slidingMoves;
        this.pieceType = pieceType;
    }

    @Override
    public Iterator<BitBoardMove> iterator(final BitBoard bitBoard, final boolean alreadyInCheck, final long potentialPins) {
        final long piecesMap = fSelectPieces.applyAsLong(bitBoard);
        if(piecesMap == 0) {
            return Collections.emptyIterator();
        }
        return new StraightMoveIterator(bitBoard, piecesMap, alreadyInCheck, potentialPins);
    }

    private class StraightMoveIterator extends AbstractMoveIterator {
        private final BitBoard bitBoard;
        private final boolean alreadyInCheck;
        private final long potentialPins;
        private final long enemyPieces;
        private final byte player;

        private Iterator<Long> pieces;
        private long currentPiece;
        private boolean safeFromCheck;
        private PreGeneratedMoves.PreGeneratedMoveIterator moves;

        public StraightMoveIterator(final BitBoard bitBoard, final long piecesMap, final boolean alreadyInCheck, final long potentialPins) {
            this.bitBoard = bitBoard;
            this.alreadyInCheck = alreadyInCheck;
            this.potentialPins = potentialPins;
            this.player = bitBoard.getPlayer();
            this.enemyPieces = bitBoard.getBitmapOppColor();

            pieces = BitIterable.of(piecesMap).iterator();
            nextPiece();
        }

        private void nextPiece() {
            currentPiece = pieces.next();
            safeFromCheck = ((currentPiece & potentialPins) == 0) & !alreadyInCheck;
            moves = new PreGeneratedMoves.PreGeneratedMoveIterator(slidingMoves[Long.numberOfTrailingZeros(currentPiece)]);
        }

        @Override
        BitBoardMove fetchNextMove() {
            long nextMove = moves.next();
            if(nextMove == 0 && !pieces.hasNext()) {
                return null;
            }

            if(nextMove == 0) {
                nextPiece();
                return this.fetchNextMove();
            }

            BitBoardMove move;
            if((nextMove & bitBoard.getBitmapColor()) != 0) {
                moves.nextDirection();
                return fetchNextMove();
            }

            if((nextMove & enemyPieces) == 0) {
                move = BitBoard.generateMove(currentPiece, nextMove, player, pieceType);
            } else {
                move = BitBoard.generateCapture(currentPiece, nextMove, player, pieceType, bitBoard.getPiece(nextMove));
                moves.nextDirection();
            }

            if(safeFromCheck) {
                return move;
            }

            bitBoard.makeMove(move);
            if (!CheckDetector.isPlayerJustMovedInCheck(bitBoard, !alreadyInCheck)) {
                bitBoard.unmakeMove();
                return move;
            }

            bitBoard.unmakeMove();
            return this.fetchNextMove();
        }
    }
}
