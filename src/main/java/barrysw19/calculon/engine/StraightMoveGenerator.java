/**
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

import java.util.List;
import java.util.function.Consumer;
import java.util.function.ToLongFunction;

public class StraightMoveGenerator extends PieceMoveGenerator {
    private final ToLongFunction<BitBoard> piecesToProcess;
    private final long[][][] slidingMoves;
    private final byte pieceType;

    public byte getPieceType() {
        return pieceType;
    }

    public StraightMoveGenerator(ToLongFunction<BitBoard> piecesToProcess, long[][][] slidingMoves, byte pieceType) {
        this.piecesToProcess = piecesToProcess;
        this.slidingMoves = slidingMoves;
        this.pieceType = pieceType;
    }

    @Override
    public void generateMoves(BitBoard bitBoard, boolean alreadyInCheck, long potentialPins, List<BitBoardMove> rv) {
        final long pieces = piecesToProcess.applyAsLong(bitBoard);
        for(long nextPiece: BitIterable.of(pieces)) {
            boolean safeFromCheck = ((nextPiece & potentialPins) == 0) & !alreadyInCheck;

            long[][] allMoves = slidingMoves[Long.numberOfTrailingZeros(nextPiece)];
            for(long[] dirMoves: allMoves) {
                makeBoardMoves(bitBoard, nextPiece, dirMoves, alreadyInCheck, safeFromCheck, rv);
            }
        }
    }

    private void makeBoardMoves(BitBoard bitBoard, long source, long[] destinations,
								boolean alreadyInCheck, boolean safeFromCheck, List<BitBoardMove> rv)
    {
        final byte player = bitBoard.getPlayer();
        final long myPieces = bitBoard.getBitmapColor(player);
        final long opponentsPieces = bitBoard.getBitmapOppColor(player);

        final Consumer<BitBoardMove> moveConsumer = safeFromCheck
                ? (rv::add)
                : (m -> {
            bitBoard.makeMove(m);
            if (!CheckDetector.isPlayerJustMovedInCheck(bitBoard, !alreadyInCheck)) {
                rv.add(m);
            }
            bitBoard.unmakeMove();
        });

        for(long moveTo: destinations) {
            if((moveTo & myPieces) != 0) {
                return;
            }

            BitBoardMove bbMove;
            if((moveTo & opponentsPieces) != 0) {
                // This is a capturing move.
                bbMove = BitBoard.generateCapture(
                        source, moveTo, player, getPieceType(), bitBoard.getPiece(moveTo));
            } else {
                bbMove = BitBoard.generateMove(source, moveTo, player, getPieceType());
            }

            moveConsumer.accept(bbMove);

            if(bbMove.isCapture()) {
                return;
            }
        }
	}
}
