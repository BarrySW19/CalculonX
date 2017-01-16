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

import java.util.*;

import static java.util.stream.Collectors.toList;

class PawnCaptureGenerator extends PieceMoveGenerator {
    private static final int[] PROMOTE_PIECES = { Piece.QUEEN, Piece.ROOK, Piece.BISHOP, Piece.KNIGHT };

	@Override
	public Iterator<BitBoardMove> iterator(final BitBoard bitBoard, final boolean alreadyInCheck, final long potentialPins) {
        byte player = bitBoard.getPlayer();

        long myPawns = bitBoard.getBitmapColor(player) & bitBoard.getBitmapPawns();
        long enemyPieces = bitBoard.getBitmapOppColor(player);
        long epLocation = -1;
        if(bitBoard.isEnPassant()) {
            // Just treat the enpassant square as another enemy piece.
            epLocation = 1L<<(bitBoard.getEnPassantRank()<<3)<<bitBoard.getEnPassantFile();
            enemyPieces |= epLocation;
        }

        long captureRight = player == Piece.WHITE
                ? (enemyPieces & ~BitBoard.getFileMap(0))>>>9 : (enemyPieces & ~BitBoard.getFileMap(0))<<7;
        long captureLeft = player == Piece.WHITE
                ? (enemyPieces & ~BitBoard.getFileMap(7))>>>7 : (enemyPieces & ~BitBoard.getFileMap(7))<<9;
        myPawns &= (captureLeft | captureRight);

        if(myPawns == 0) {
            return Collections.emptyIterator();
        }

		return new PawnCaptureIterator(bitBoard, myPawns, alreadyInCheck, potentialPins, enemyPieces, epLocation);
	}

    private static class PawnCaptureIterator extends AbstractMoveIterator {
        private final BitBoard bitBoard;
        private final boolean alreadyInCheck;
        private final long potentialPins;
        private final long enemyPieces;
        private final long epLocation;

        private Iterator<Long> pieces;
        private long currentPiece;
        private boolean safeFromCheck;
        private Iterator<Long> moves;
        private final List<BitBoardMove> queuedMoves = new LinkedList<>();

        PawnCaptureIterator(final BitBoard bitBoard, final long piecesMap,
                            final boolean alreadyInCheck, final long potentialPins, final long enemyPieces, final long epLocation)
        {
            this.bitBoard = bitBoard;
            this.alreadyInCheck = alreadyInCheck;
            this.potentialPins = potentialPins;
            this.enemyPieces = enemyPieces;
            this.epLocation = epLocation;

            pieces = BitIterable.of(piecesMap).iterator();
            nextPiece();
        }

        private void nextPiece() {
            currentPiece = pieces.next();
            safeFromCheck = ((currentPiece & potentialPins) == 0) & !alreadyInCheck;
            if(bitBoard.getPlayer() == Piece.WHITE) {
                moves = BitIterable.of((((currentPiece << 7) & ~BitBoard.getFileMap(7)) | ((currentPiece << 9) & ~BitBoard.getFileMap(0))) & enemyPieces).iterator() ;
            } else {
                moves = BitIterable.of((((currentPiece >>> 7) & ~BitBoard.getFileMap(0)) | ((currentPiece >>> 9) & ~BitBoard.getFileMap(7))) & enemyPieces).iterator() ;
            }
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
            tryCaptures(bitBoard, currentPiece, nextMove, epLocation, alreadyInCheck, safeFromCheck, queuedMoves);
            return fetchNextMove();
        }
    }

	private static void tryCaptures(BitBoard bitBoard, long nextPiece,
			long captured, long epLocation, boolean alreadyInCheck, boolean safeFromCheck, List<BitBoardMove> rv)
    {
		final byte player = bitBoard.getPlayer();
        final byte captureType = bitBoard.getPiece(captured);
        BitBoardMove bbMove;

		if(captured == epLocation) {
			bbMove = BitBoard.generateEnPassantCapture(nextPiece, captured, player);
		} else {
			bbMove = BitBoard.generateCapture(
					nextPiece, captured, player, Piece.PAWN, captureType);
		}

		boolean moveOk = true;
        if( !safeFromCheck) {
            bitBoard.makeMove(bbMove);
            moveOk = !CheckDetector.isPlayerJustMovedInCheck(bitBoard, !alreadyInCheck);
            bitBoard.unmakeMove();
        }

		if(moveOk) {
			if((captured & BitBoard.FINAL_RANKS) == 0) {
				rv.add(bbMove);
			} else {
                rv.addAll(Arrays.stream(PROMOTE_PIECES).mapToObj(
                        (p) -> BitBoard.generateCaptureAndPromote(nextPiece, captured, player, captureType, (byte) p)).collect(toList()));
			}
		}
	}
}
