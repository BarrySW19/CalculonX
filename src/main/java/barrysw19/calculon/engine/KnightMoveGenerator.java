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

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class KnightMoveGenerator extends PieceMoveGenerator {
	
	// Pre-generated knight moves
    public static final long[] KNIGHT_MOVES = new long[] {
            0b00000000_00000000_00000000_00000000_00000000_00000010_00000100_00000000L,
            0b00000000_00000000_00000000_00000000_00000000_00000101_00001000_00000000L,
            0b00000000_00000000_00000000_00000000_00000000_00001010_00010001_00000000L,
            0b00000000_00000000_00000000_00000000_00000000_00010100_00100010_00000000L,
            0b00000000_00000000_00000000_00000000_00000000_00101000_01000100_00000000L,
            0b00000000_00000000_00000000_00000000_00000000_01010000_10001000_00000000L,
            0b00000000_00000000_00000000_00000000_00000000_10100000_00010000_00000000L,
            0b00000000_00000000_00000000_00000000_00000000_01000000_00100000_00000000L,
            0b00000000_00000000_00000000_00000000_00000010_00000100_00000000_00000100L,
            0b00000000_00000000_00000000_00000000_00000101_00001000_00000000_00001000L,
            0b00000000_00000000_00000000_00000000_00001010_00010001_00000000_00010001L,
            0b00000000_00000000_00000000_00000000_00010100_00100010_00000000_00100010L,
            0b00000000_00000000_00000000_00000000_00101000_01000100_00000000_01000100L,
            0b00000000_00000000_00000000_00000000_01010000_10001000_00000000_10001000L,
            0b00000000_00000000_00000000_00000000_10100000_00010000_00000000_00010000L,
            0b00000000_00000000_00000000_00000000_01000000_00100000_00000000_00100000L,
            0b00000000_00000000_00000000_00000010_00000100_00000000_00000100_00000010L,
            0b00000000_00000000_00000000_00000101_00001000_00000000_00001000_00000101L,
            0b00000000_00000000_00000000_00001010_00010001_00000000_00010001_00001010L,
            0b00000000_00000000_00000000_00010100_00100010_00000000_00100010_00010100L,
            0b00000000_00000000_00000000_00101000_01000100_00000000_01000100_00101000L,
            0b00000000_00000000_00000000_01010000_10001000_00000000_10001000_01010000L,
            0b00000000_00000000_00000000_10100000_00010000_00000000_00010000_10100000L,
            0b00000000_00000000_00000000_01000000_00100000_00000000_00100000_01000000L,
            0b00000000_00000000_00000010_00000100_00000000_00000100_00000010_00000000L,
            0b00000000_00000000_00000101_00001000_00000000_00001000_00000101_00000000L,
            0b00000000_00000000_00001010_00010001_00000000_00010001_00001010_00000000L,
            0b00000000_00000000_00010100_00100010_00000000_00100010_00010100_00000000L,
            0b00000000_00000000_00101000_01000100_00000000_01000100_00101000_00000000L,
            0b00000000_00000000_01010000_10001000_00000000_10001000_01010000_00000000L,
            0b00000000_00000000_10100000_00010000_00000000_00010000_10100000_00000000L,
            0b00000000_00000000_01000000_00100000_00000000_00100000_01000000_00000000L,
            0b00000000_00000010_00000100_00000000_00000100_00000010_00000000_00000000L,
            0b00000000_00000101_00001000_00000000_00001000_00000101_00000000_00000000L,
            0b00000000_00001010_00010001_00000000_00010001_00001010_00000000_00000000L,
            0b00000000_00010100_00100010_00000000_00100010_00010100_00000000_00000000L,
            0b00000000_00101000_01000100_00000000_01000100_00101000_00000000_00000000L,
            0b00000000_01010000_10001000_00000000_10001000_01010000_00000000_00000000L,
            0b00000000_10100000_00010000_00000000_00010000_10100000_00000000_00000000L,
            0b00000000_01000000_00100000_00000000_00100000_01000000_00000000_00000000L,
            0b00000010_00000100_00000000_00000100_00000010_00000000_00000000_00000000L,
            0b00000101_00001000_00000000_00001000_00000101_00000000_00000000_00000000L,
            0b00001010_00010001_00000000_00010001_00001010_00000000_00000000_00000000L,
            0b00010100_00100010_00000000_00100010_00010100_00000000_00000000_00000000L,
            0b00101000_01000100_00000000_01000100_00101000_00000000_00000000_00000000L,
            0b01010000_10001000_00000000_10001000_01010000_00000000_00000000_00000000L,
            0b10100000_00010000_00000000_00010000_10100000_00000000_00000000_00000000L,
            0b01000000_00100000_00000000_00100000_01000000_00000000_00000000_00000000L,
            0b00000100_00000000_00000100_00000010_00000000_00000000_00000000_00000000L,
            0b00001000_00000000_00001000_00000101_00000000_00000000_00000000_00000000L,
            0b00010001_00000000_00010001_00001010_00000000_00000000_00000000_00000000L,
            0b00100010_00000000_00100010_00010100_00000000_00000000_00000000_00000000L,
            0b01000100_00000000_01000100_00101000_00000000_00000000_00000000_00000000L,
            0b10001000_00000000_10001000_01010000_00000000_00000000_00000000_00000000L,
            0b00010000_00000000_00010000_10100000_00000000_00000000_00000000_00000000L,
            0b00100000_00000000_00100000_01000000_00000000_00000000_00000000_00000000L,
            0b00000000_00000100_00000010_00000000_00000000_00000000_00000000_00000000L,
            0b00000000_00001000_00000101_00000000_00000000_00000000_00000000_00000000L,
            0b00000000_00010001_00001010_00000000_00000000_00000000_00000000_00000000L,
            0b00000000_00100010_00010100_00000000_00000000_00000000_00000000_00000000L,
            0b00000000_01000100_00101000_00000000_00000000_00000000_00000000_00000000L,
            0b00000000_10001000_01010000_00000000_00000000_00000000_00000000_00000000L,
            0b00000000_00010000_10100000_00000000_00000000_00000000_00000000_00000000L,
            0b00000000_00100000_01000000_00000000_00000000_00000000_00000000_00000000L,
    };

    @Override
    public Iterator<BitBoardMove> iterator(BitBoard bitBoard, boolean alreadyInCheck, long potentialPins) {
        long piecesMap = bitBoard.getBitmapColor() & bitBoard.getBitmapKnights();
        if(piecesMap == 0) {
            return Collections.emptyIterator();
        }
        return new KnightMoveIterator(bitBoard, piecesMap, alreadyInCheck, potentialPins);
    }

    @Override
    public void generateMoves(BitBoard bitBoard, boolean alreadyInCheck, long potentialPins, List<BitBoardMove> rv) {
        for(Iterator<BitBoardMove> iter = this.iterator(bitBoard, alreadyInCheck, potentialPins); iter.hasNext(); ) {
            rv.add(iter.next());
        }
    }

    private static class KnightMoveIterator extends AbstractMoveIterator {
        private final BitBoard bitBoard;
        private final boolean alreadyInCheck;
        private final long potentialPins;
        private final long enemyPieces;
        private final byte player;

        private Iterator<Long> pieces;
        private long currentPiece;
        private boolean safeFromCheck;
        private Iterator<Long> moves;

        public KnightMoveIterator(final BitBoard bitBoard, final long piecesMap, final boolean alreadyInCheck, final long potentialPins) {
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
            moves = BitIterable.of(KNIGHT_MOVES[Long.numberOfTrailingZeros(currentPiece)] & ~bitBoard.getBitmapColor()).iterator();
        }

        @Override
        BitBoardMove fetchNextMove() {
            if(!moves.hasNext() && !pieces.hasNext()) {
                return null;
            }

            if(!moves.hasNext()) {
                nextPiece();
                return this.fetchNextMove();
            }

            long nextMove = moves.next();
            BitBoardMove move;
            if((nextMove & enemyPieces) == 0) {
                move = BitBoard.generateMove(currentPiece, nextMove, player, Piece.KNIGHT);
            } else {
                move = BitBoard.generateCapture(currentPiece, nextMove, player, Piece.KNIGHT, bitBoard.getPiece(nextMove));
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
