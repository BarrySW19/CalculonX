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

public class KnightMoveGenerator implements PieceMoveGenerator {
	
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
    public Iterator<BitBoardMove> iterator(final MoveGeneratorImpl.MoveGeneratorContext context) {
        long piecesMap = context.getBitBoard().getBitmapColor() & context.getBitBoard().getBitmapKnights();
        if(piecesMap == 0) {
            return Collections.emptyIterator();
        }
        return new KnightMoveIterator(context, piecesMap, false);
    }

    @Override
    public Iterator<BitBoardMove> generateThreatMoves(final MoveGeneratorImpl.MoveGeneratorContext context) {
        long piecesMap = context.getBitBoard().getBitmapColor() & context.getBitBoard().getBitmapKnights();
        if(piecesMap == 0) {
            return Collections.emptyIterator();
        }

        return new KnightMoveIterator(context, piecesMap, true);
    }

    private static class KnightMoveIterator extends AbstractMoveIterator {
        private final MoveGeneratorImpl.MoveGeneratorContext context;

        private final BitBoard bitBoard;
        private final boolean alreadyInCheck;
        private final long potentialPins;
        private final long enemyPieces;
        private final byte player;

        private Iterator<Long> pieces;
        private long currentPiece;
        private boolean safeFromCheck;
        private Iterator<Long> moves;

        private final boolean threatsOnly;
        private boolean possibleDiscovery;

        KnightMoveIterator(final MoveGeneratorImpl.MoveGeneratorContext context, final long piecesMap, final boolean threatsOnly) {
            this.context = context;

            this.bitBoard = context.getBitBoard();
            this.alreadyInCheck = context.isAlreadyInCheck();
            this.potentialPins = context.getPotentialPins();
            this.player = bitBoard.getPlayer();
            this.enemyPieces = bitBoard.getBitmapOppColor();
            this.threatsOnly = threatsOnly;

            pieces = BitIterable.of(piecesMap).iterator();
            nextPiece();
        }

        private void nextPiece() {
            currentPiece = pieces.next();
            possibleDiscovery = (currentPiece & context.getPotentialDiscoveries()) != 0;
            safeFromCheck = ((currentPiece & potentialPins) == 0) & !alreadyInCheck;

            long allMoves = KNIGHT_MOVES[Long.numberOfTrailingZeros(currentPiece)] & ~bitBoard.getBitmapColor();
            if(!possibleDiscovery && threatsOnly) {
                // Discovered checks are not possible, so just try captures and checks.
                long threatMoves = allMoves & bitBoard.getBitmapOppColor(); // Captures
                for(long otherMove: BitIterable.of(allMoves & ~threatMoves)) { // Checks
                    if((KNIGHT_MOVES[Long.numberOfTrailingZeros(otherMove)] & bitBoard.getBitmapOppColor() & bitBoard.getBitmapKings()) != 0) {
                        threatMoves |= otherMove;
                    }
                }
                allMoves = threatMoves;
            }
            moves = BitIterable.of(allMoves).iterator();
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

            boolean legalityChecked = false;
            // If we are only generating threats, and the move is not a capture or a direct check then see if it's a discovered check
            if(threatsOnly && !move.isCapture()
                && (KNIGHT_MOVES[Long.numberOfTrailingZeros(nextMove)] & enemyPieces & bitBoard.getBitmapKings()) == 0)
            {
                bitBoard.makeMove(move);
                legalityChecked = !CheckDetector.isPlayerJustMovedInCheck(bitBoard);
                boolean discoveredCheck = CheckDetector.isPlayerToMoveInCheck(bitBoard);
                bitBoard.unmakeMove();
                if(!discoveredCheck) {
                    return fetchNextMove();
                }
            }

            if(safeFromCheck) {
                return move;
            }

            if(legalityChecked || CheckDetector.isMoveLegal(move, bitBoard, !alreadyInCheck)) {
                return move;
            }

            return this.fetchNextMove();
        }
    }
}
