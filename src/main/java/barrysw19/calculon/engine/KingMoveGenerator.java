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

public class KingMoveGenerator implements PieceMoveGenerator {

	private static final long EMPTY_WKS = 3L<<5;
	private static final long EMPTY_WQS = 7L<<1;
	private static final long EMPTY_BKS = 3L<<61;
	private static final long EMPTY_BQS = 7L<<57;

    @Override
    public Iterator<BitBoardMove> iterator(final MoveGeneratorImpl.MoveGeneratorContext context) {
        return iteratorIntern(context, false);
    }

    @Override
    public Iterator<BitBoardMove> generateThreatMoves(final MoveGeneratorImpl.MoveGeneratorContext context) {
        return iteratorIntern(context, true);
    }

    private Iterator<BitBoardMove> iteratorIntern(final MoveGeneratorImpl.MoveGeneratorContext context, boolean threatsOnly) {
        long kingMap = context.getBitBoard().getBitmapColor() & context.getBitBoard().getBitmapKings();
        if(kingMap == 0) {
            return Collections.emptyIterator(); // Shouldn't really happen.
        }

        if(context.isAlreadyInCheck() || threatsOnly) {
            return new KingMoveIterator(context, kingMap, threatsOnly);
        }

        return new CompoundIterator<>(
                new CastlingIterator(context.getBitBoard(), kingMap),
                new KingMoveIterator(context, kingMap, false)
        );
    }

	// Bit twiddling routines...
	private static boolean isCastlingPossible(BitBoard bitBoard, short castleDir) {
		BitBoardMove bbMove = BitBoard.generateCastling(castleDir);
		bitBoard.makeMove(bbMove);
        boolean rv = ! CheckDetector.isPlayerJustMovedInCheck(bitBoard);
		bitBoard.unmakeMove();
		return rv;
	}

	private static boolean isIntermediateCheck(BitBoard bitBoard, long fromSquare, long toSquare, byte player) {
		BitBoardMove bbMove = BitBoard.generateMove(fromSquare, toSquare, player, Piece.KING);
		bitBoard.makeMove(bbMove);
        boolean rv = CheckDetector.isPlayerJustMovedInCheck(bitBoard);
		bitBoard.unmakeMove();
		return rv;
	}

	private static class CastlingIterator extends AbstractMoveIterator {
        private final short[] aDirection;
        private final long[] aEmpty;
        private final long[] aMidSquare;

        private final BitBoard bitBoard;
        private final byte player;
        private final long kingMap;
        private final byte castleFlags;
        private int option = 0;

        CastlingIterator(final BitBoard bitBoard, final long kingMap) {
            this.bitBoard = bitBoard;
            this.kingMap = kingMap;
            this.player = bitBoard.getPlayer();
            this.castleFlags = bitBoard.getCastlingOptions();
            if(this.player == Piece.WHITE) {
                aDirection = new short[] { BitBoard.CASTLE_WKS, BitBoard.CASTLE_WQS };
                aEmpty = new long[] { EMPTY_WKS, EMPTY_WQS };
            } else {
                aDirection = new short[] { BitBoard.CASTLE_BKS, BitBoard.CASTLE_BQS };
                aEmpty = new long[] { EMPTY_BKS, EMPTY_BQS };
            }
            aMidSquare = new long[] { kingMap<<1, kingMap>>>1 };
        }

        @Override
        BitBoardMove fetchNextMove() {
            if(option >= 2) {
                return null;
            }

            int pOption = option;
            option++;

            if((castleFlags & aDirection[pOption]) != 0 && (bitBoard.getAllPieces() & aEmpty[pOption]) == 0) {
                if( ! isIntermediateCheck(bitBoard, kingMap, aMidSquare[pOption], player)) {
                    if(isCastlingPossible(bitBoard, aDirection[pOption])) {
                        return BitBoard.generateCastling(aDirection[pOption]);
                    }
                }
            }

            return fetchNextMove();
        }
    }

    private static class KingMoveIterator extends AbstractMoveIterator {
        private final BitBoard bitBoard;
        private final long enemyPieces;
        private final byte player;
        private final boolean threatsOnly;
        private final long kingPos;
        private final Iterator<Long> moves;

        KingMoveIterator(final MoveGeneratorImpl.MoveGeneratorContext context, final long kingPos, final boolean threatsOnly) {
            this.bitBoard = context.getBitBoard();
            this.player = context.getBitBoard().getPlayer();
            this.enemyPieces = context.getBitBoard().getBitmapOppColor();
            this.kingPos = kingPos;
            this.threatsOnly = threatsOnly;

            long movesMap = Bitmaps.KING_MOVES[Long.numberOfTrailingZeros(kingPos)]&(~bitBoard.getBitmapColor());
            if((context.getPotentialDiscoveries() & kingPos) == 0 && threatsOnly) {
                movesMap &= bitBoard.getBitmapOppColor();
            }
            this.moves = BitIterable.of(movesMap).iterator();
        }

        @Override
        BitBoardMove fetchNextMove() {
            if( ! moves.hasNext()) {
                return null;
            }

            long nextMove = moves.next();
            BitBoardMove move;
            if((nextMove & enemyPieces) == 0) {
                move = BitBoard.generateMove(kingPos, nextMove, player, Piece.KING);
            } else {
                move = BitBoard.generateCapture(kingPos, nextMove, player, Piece.KING, bitBoard.getPiece(nextMove));
            }

            if(threatsOnly && !move.isCapture()) {
                bitBoard.makeMove(move);
                boolean isLegal = !CheckDetector.isPlayerJustMovedInCheck(bitBoard);
                boolean discoveredCheck = CheckDetector.isPlayerToMoveInCheck(bitBoard);
                bitBoard.unmakeMove();
                if(isLegal && discoveredCheck) {
                    return move;
                }
                return fetchNextMove();
            }

            if(CheckDetector.isMoveLegal(move, bitBoard)) {
                return move;
            }

            return this.fetchNextMove();
        }
    }
}
