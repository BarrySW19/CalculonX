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
import java.util.List;

public class KingMoveGenerator extends PieceMoveGenerator {

	private static final long EMPTY_WKS = 3L<<5;
	private static final long EMPTY_WQS = 7L<<1;
	private static final long EMPTY_BKS = 3L<<61;
	private static final long EMPTY_BQS = 7L<<57;

	// Pre-generated king moves
	public static final long[] KING_MOVES = new long[] {
            0b00000000_00000000_00000000_00000000_00000000_00000000_00000011_00000010L,
            0b00000000_00000000_00000000_00000000_00000000_00000000_00000111_00000101L,
            0b00000000_00000000_00000000_00000000_00000000_00000000_00001110_00001010L,
            0b00000000_00000000_00000000_00000000_00000000_00000000_00011100_00010100L,
            0b00000000_00000000_00000000_00000000_00000000_00000000_00111000_00101000L,
            0b00000000_00000000_00000000_00000000_00000000_00000000_01110000_01010000L,
            0b00000000_00000000_00000000_00000000_00000000_00000000_11100000_10100000L,
            0b00000000_00000000_00000000_00000000_00000000_00000000_11000000_01000000L,
            0b00000000_00000000_00000000_00000000_00000000_00000011_00000010_00000011L,
            0b00000000_00000000_00000000_00000000_00000000_00000111_00000101_00000111L,
            0b00000000_00000000_00000000_00000000_00000000_00001110_00001010_00001110L,
            0b00000000_00000000_00000000_00000000_00000000_00011100_00010100_00011100L,
            0b00000000_00000000_00000000_00000000_00000000_00111000_00101000_00111000L,
            0b00000000_00000000_00000000_00000000_00000000_01110000_01010000_01110000L,
            0b00000000_00000000_00000000_00000000_00000000_11100000_10100000_11100000L,
            0b00000000_00000000_00000000_00000000_00000000_11000000_01000000_11000000L,
            0b00000000_00000000_00000000_00000000_00000011_00000010_00000011_00000000L,
            0b00000000_00000000_00000000_00000000_00000111_00000101_00000111_00000000L,
            0b00000000_00000000_00000000_00000000_00001110_00001010_00001110_00000000L,
            0b00000000_00000000_00000000_00000000_00011100_00010100_00011100_00000000L,
            0b00000000_00000000_00000000_00000000_00111000_00101000_00111000_00000000L,
            0b00000000_00000000_00000000_00000000_01110000_01010000_01110000_00000000L,
            0b00000000_00000000_00000000_00000000_11100000_10100000_11100000_00000000L,
            0b00000000_00000000_00000000_00000000_11000000_01000000_11000000_00000000L,
            0b00000000_00000000_00000000_00000011_00000010_00000011_00000000_00000000L,
            0b00000000_00000000_00000000_00000111_00000101_00000111_00000000_00000000L,
            0b00000000_00000000_00000000_00001110_00001010_00001110_00000000_00000000L,
            0b00000000_00000000_00000000_00011100_00010100_00011100_00000000_00000000L,
            0b00000000_00000000_00000000_00111000_00101000_00111000_00000000_00000000L,
            0b00000000_00000000_00000000_01110000_01010000_01110000_00000000_00000000L,
            0b00000000_00000000_00000000_11100000_10100000_11100000_00000000_00000000L,
            0b00000000_00000000_00000000_11000000_01000000_11000000_00000000_00000000L,
            0b00000000_00000000_00000011_00000010_00000011_00000000_00000000_00000000L,
            0b00000000_00000000_00000111_00000101_00000111_00000000_00000000_00000000L,
            0b00000000_00000000_00001110_00001010_00001110_00000000_00000000_00000000L,
            0b00000000_00000000_00011100_00010100_00011100_00000000_00000000_00000000L,
            0b00000000_00000000_00111000_00101000_00111000_00000000_00000000_00000000L,
            0b00000000_00000000_01110000_01010000_01110000_00000000_00000000_00000000L,
            0b00000000_00000000_11100000_10100000_11100000_00000000_00000000_00000000L,
            0b00000000_00000000_11000000_01000000_11000000_00000000_00000000_00000000L,
            0b00000000_00000011_00000010_00000011_00000000_00000000_00000000_00000000L,
            0b00000000_00000111_00000101_00000111_00000000_00000000_00000000_00000000L,
            0b00000000_00001110_00001010_00001110_00000000_00000000_00000000_00000000L,
            0b00000000_00011100_00010100_00011100_00000000_00000000_00000000_00000000L,
            0b00000000_00111000_00101000_00111000_00000000_00000000_00000000_00000000L,
            0b00000000_01110000_01010000_01110000_00000000_00000000_00000000_00000000L,
            0b00000000_11100000_10100000_11100000_00000000_00000000_00000000_00000000L,
            0b00000000_11000000_01000000_11000000_00000000_00000000_00000000_00000000L,
            0b00000011_00000010_00000011_00000000_00000000_00000000_00000000_00000000L,
            0b00000111_00000101_00000111_00000000_00000000_00000000_00000000_00000000L,
            0b00001110_00001010_00001110_00000000_00000000_00000000_00000000_00000000L,
            0b00011100_00010100_00011100_00000000_00000000_00000000_00000000_00000000L,
            0b00111000_00101000_00111000_00000000_00000000_00000000_00000000_00000000L,
            0b01110000_01010000_01110000_00000000_00000000_00000000_00000000_00000000L,
            0b11100000_10100000_11100000_00000000_00000000_00000000_00000000_00000000L,
            0b11000000_01000000_11000000_00000000_00000000_00000000_00000000_00000000L,
            0b00000010_00000011_00000000_00000000_00000000_00000000_00000000_00000000L,
            0b00000101_00000111_00000000_00000000_00000000_00000000_00000000_00000000L,
            0b00001010_00001110_00000000_00000000_00000000_00000000_00000000_00000000L,
            0b00010100_00011100_00000000_00000000_00000000_00000000_00000000_00000000L,
            0b00101000_00111000_00000000_00000000_00000000_00000000_00000000_00000000L,
            0b01010000_01110000_00000000_00000000_00000000_00000000_00000000_00000000L,
            0b10100000_11100000_00000000_00000000_00000000_00000000_00000000_00000000L,
            0b01000000_11000000_00000000_00000000_00000000_00000000_00000000_00000000L,
    };

    @Override
    public Iterator<BitBoardMove> iterator(BitBoard bitBoard, boolean alreadyInCheck, long potentialPins) {
        long kingMap = bitBoard.getBitmapColor() & bitBoard.getBitmapKings();
        if(kingMap == 0) {
            return Collections.emptyIterator(); // Shouldn't really happen.
        }

        if(alreadyInCheck) {
            return new KingMoveIterator(bitBoard, kingMap);
        }

        return new CompoundIterator<>(
                new CastlingIterator(bitBoard, kingMap),
                new KingMoveIterator(bitBoard, kingMap)
        );
    }

	@Override
	public void generateMoves(BitBoard bitBoard, boolean alreadyInCheck, long potentialPins, List<BitBoardMove> rv) {
        for (Iterator<BitBoardMove> iter = this.iterator(bitBoard, alreadyInCheck, potentialPins); iter.hasNext(); ) {
            rv.add(iter.next());
        }
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

        private long currentPiece;
        private Iterator<Long> moves;

        KingMoveIterator(final BitBoard bitBoard, final long piecesMap) {
            this.bitBoard = bitBoard;
            this.player = bitBoard.getPlayer();
            this.enemyPieces = bitBoard.getBitmapOppColor();
            this.currentPiece = piecesMap;
            this.moves = BitIterable.of(KING_MOVES[Long.numberOfTrailingZeros(piecesMap)]&(~bitBoard.getBitmapColor())).iterator();
        }

        @Override
        BitBoardMove fetchNextMove() {
            if( ! moves.hasNext()) {
                return null;
            }

            long nextMove = moves.next();
            BitBoardMove move;
            if((nextMove & enemyPieces) == 0) {
                move = BitBoard.generateMove(currentPiece, nextMove, player, Piece.KING);
            } else {
                move = BitBoard.generateCapture(currentPiece, nextMove, player, Piece.KING, bitBoard.getPiece(nextMove));
            }

            bitBoard.makeMove(move);
            if (!CheckDetector.isPlayerJustMovedInCheck(bitBoard)) {
                bitBoard.unmakeMove();
                return move;
            }

            bitBoard.unmakeMove();
            return this.fetchNextMove();
        }
    }
}
