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
import barrysw19.calculon.model.Piece;
import barrysw19.calculon.util.BitIterable;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.ToLongFunction;

class StraightMoveGenerator extends PieceMoveGenerator {
    private final ToLongFunction<BitBoard> fSelectPieces;
    private final long[][][] slidingMoves;
    private final byte pieceType;
    private final long[] allMoves;

    StraightMoveGenerator(final byte pieceType) {
        this.pieceType = pieceType;
        switch (pieceType) {
            case Piece.BISHOP:
                this.slidingMoves = PreGeneratedMoves.DIAGONAL_MOVES;
                this.allMoves = Bitmaps.diag2Map;
                this.fSelectPieces = (BitBoard bb) -> (bb.getBitmapColor() & bb.getBitmapBishops());
                break;
            case Piece.ROOK:
                this.slidingMoves = PreGeneratedMoves.STRAIGHT_MOVES;
                this.allMoves = Bitmaps.cross2Map;
                this.fSelectPieces = (BitBoard bb) -> (bb.getBitmapColor() & bb.getBitmapRooks());
                break;
            case Piece.QUEEN:
                this.slidingMoves = PreGeneratedMoves.SLIDE_MOVES;
                this.allMoves = Bitmaps.star2Map;
                this.fSelectPieces = (BitBoard bb) -> (bb.getBitmapColor() & bb.getBitmapQueens());
                break;
            default:
                throw new IllegalArgumentException();
        }
    }

    @Override
    public Iterator<BitBoardMove> iterator(final BitBoard bitBoard, final boolean alreadyInCheck, final long potentialPins) {
        final long piecesMap = fSelectPieces.applyAsLong(bitBoard);
        if(piecesMap == 0) {
            return Collections.emptyIterator();
        }
        return new StraightMoveIterator(bitBoard, piecesMap, alreadyInCheck, potentialPins, false);
    }

    @Override
    public void generateThreatMoves(BitBoard bitBoard, boolean alreadyInCheck, long potentialPins, List<BitBoardMove> rv) {
        long piecesMap = fSelectPieces.applyAsLong(bitBoard);
        if(piecesMap == 0) {
            return;
        }

        StraightMoveIterator iterator = new StraightMoveIterator(bitBoard, piecesMap, alreadyInCheck, potentialPins, true);
        while(iterator.hasNext()) {
            rv.add(iterator.next());
        }
    }

    private class StraightMoveIterator extends AbstractMoveIterator {
        private final BitBoard bitBoard;
        private final boolean alreadyInCheck;
        private final long potentialPins;
        private final long enemyPieces;
        private final byte player;
        private final boolean threatsOnly;

        private Iterator<Long> pieces;
        private long currentPiece;
        private boolean safeFromCheck;
        private PreGeneratedMoves.PreGeneratedMoveIterator moves;
        private long threatSquares = ~0L;

        StraightMoveIterator(final BitBoard bitBoard, final long piecesMap,
                             final boolean alreadyInCheck, final long potentialPins, final boolean threatsOnly)
        {
            this.bitBoard = bitBoard;
            this.alreadyInCheck = alreadyInCheck;
            this.potentialPins = potentialPins;
            this.player = bitBoard.getPlayer();
            this.enemyPieces = bitBoard.getBitmapOppColor();
            this.threatsOnly = threatsOnly;

            pieces = BitIterable.of(piecesMap).iterator();
            nextPiece();
        }

        private void nextPiece() {
            currentPiece = pieces.next();
            safeFromCheck = ((currentPiece & potentialPins) == 0) & !alreadyInCheck;
            if(threatsOnly) {
                threatSquares = allMoves[Long.numberOfTrailingZeros(bitBoard.getBitmapOppColor() & bitBoard.getBitmapKings())];
                threatSquares &= allMoves[Long.numberOfTrailingZeros(currentPiece)];
                threatSquares |= bitBoard.getBitmapOppColor();
            }
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

            if((nextMove & threatSquares) == 0) {
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

            if(CheckDetector.isMoveLegal(move, bitBoard, !alreadyInCheck)) {
                return move;
            }

            return this.fetchNextMove();
        }
    }
}
