/**
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
package nl.zoidberg.calculon.engine;

import java.util.List;

import nl.zoidberg.calculon.engine.BitBoard.BitBoardMove;
import nl.zoidberg.calculon.model.Piece;

public class KingMoveGenerator extends PieceMoveGenerator {

	private static final long EMPTY_WKS = 3L<<5;
	private static final long EMPTY_WQS = 7L<<1;
	private static final long EMPTY_BKS = 3L<<61;
	private static final long EMPTY_BQS = 7L<<57;

	// Pre-generated king moves
	public static final long[] KING_MOVES = new long[64];
	static {
        KING_MOVES[0] = 0b00000000_00000000_00000000_00000000_00000000_00000000_00000011_00000010L;
        KING_MOVES[1] = 0b00000000_00000000_00000000_00000000_00000000_00000000_00000111_00000101L;
        KING_MOVES[2] = 0b00000000_00000000_00000000_00000000_00000000_00000000_00001110_00001010L;
        KING_MOVES[3] = 0b00000000_00000000_00000000_00000000_00000000_00000000_00011100_00010100L;
        KING_MOVES[4] = 0b00000000_00000000_00000000_00000000_00000000_00000000_00111000_00101000L;
        KING_MOVES[5] = 0b00000000_00000000_00000000_00000000_00000000_00000000_01110000_01010000L;
        KING_MOVES[6] = 0b00000000_00000000_00000000_00000000_00000000_00000000_11100000_10100000L;
        KING_MOVES[7] = 0b00000000_00000000_00000000_00000000_00000000_00000000_11000000_01000000L;
        KING_MOVES[8] = 0b00000000_00000000_00000000_00000000_00000000_00000011_00000010_00000011L;
        KING_MOVES[9] = 0b00000000_00000000_00000000_00000000_00000000_00000111_00000101_00000111L;
        KING_MOVES[10] = 0b00000000_00000000_00000000_00000000_00000000_00001110_00001010_00001110L;
        KING_MOVES[11] = 0b00000000_00000000_00000000_00000000_00000000_00011100_00010100_00011100L;
        KING_MOVES[12] = 0b00000000_00000000_00000000_00000000_00000000_00111000_00101000_00111000L;
        KING_MOVES[13] = 0b00000000_00000000_00000000_00000000_00000000_01110000_01010000_01110000L;
        KING_MOVES[14] = 0b00000000_00000000_00000000_00000000_00000000_11100000_10100000_11100000L;
        KING_MOVES[15] = 0b00000000_00000000_00000000_00000000_00000000_11000000_01000000_11000000L;
        KING_MOVES[16] = 0b00000000_00000000_00000000_00000000_00000011_00000010_00000011_00000000L;
        KING_MOVES[17] = 0b00000000_00000000_00000000_00000000_00000111_00000101_00000111_00000000L;
        KING_MOVES[18] = 0b00000000_00000000_00000000_00000000_00001110_00001010_00001110_00000000L;
        KING_MOVES[19] = 0b00000000_00000000_00000000_00000000_00011100_00010100_00011100_00000000L;
        KING_MOVES[20] = 0b00000000_00000000_00000000_00000000_00111000_00101000_00111000_00000000L;
        KING_MOVES[21] = 0b00000000_00000000_00000000_00000000_01110000_01010000_01110000_00000000L;
        KING_MOVES[22] = 0b00000000_00000000_00000000_00000000_11100000_10100000_11100000_00000000L;
        KING_MOVES[23] = 0b00000000_00000000_00000000_00000000_11000000_01000000_11000000_00000000L;
        KING_MOVES[24] = 0b00000000_00000000_00000000_00000011_00000010_00000011_00000000_00000000L;
        KING_MOVES[25] = 0b00000000_00000000_00000000_00000111_00000101_00000111_00000000_00000000L;
        KING_MOVES[26] = 0b00000000_00000000_00000000_00001110_00001010_00001110_00000000_00000000L;
        KING_MOVES[27] = 0b00000000_00000000_00000000_00011100_00010100_00011100_00000000_00000000L;
        KING_MOVES[28] = 0b00000000_00000000_00000000_00111000_00101000_00111000_00000000_00000000L;
        KING_MOVES[29] = 0b00000000_00000000_00000000_01110000_01010000_01110000_00000000_00000000L;
        KING_MOVES[30] = 0b00000000_00000000_00000000_11100000_10100000_11100000_00000000_00000000L;
        KING_MOVES[31] = 0b00000000_00000000_00000000_11000000_01000000_11000000_00000000_00000000L;
        KING_MOVES[32] = 0b00000000_00000000_00000011_00000010_00000011_00000000_00000000_00000000L;
        KING_MOVES[33] = 0b00000000_00000000_00000111_00000101_00000111_00000000_00000000_00000000L;
        KING_MOVES[34] = 0b00000000_00000000_00001110_00001010_00001110_00000000_00000000_00000000L;
        KING_MOVES[35] = 0b00000000_00000000_00011100_00010100_00011100_00000000_00000000_00000000L;
        KING_MOVES[36] = 0b00000000_00000000_00111000_00101000_00111000_00000000_00000000_00000000L;
        KING_MOVES[37] = 0b00000000_00000000_01110000_01010000_01110000_00000000_00000000_00000000L;
        KING_MOVES[38] = 0b00000000_00000000_11100000_10100000_11100000_00000000_00000000_00000000L;
        KING_MOVES[39] = 0b00000000_00000000_11000000_01000000_11000000_00000000_00000000_00000000L;
        KING_MOVES[40] = 0b00000000_00000011_00000010_00000011_00000000_00000000_00000000_00000000L;
        KING_MOVES[41] = 0b00000000_00000111_00000101_00000111_00000000_00000000_00000000_00000000L;
        KING_MOVES[42] = 0b00000000_00001110_00001010_00001110_00000000_00000000_00000000_00000000L;
        KING_MOVES[43] = 0b00000000_00011100_00010100_00011100_00000000_00000000_00000000_00000000L;
        KING_MOVES[44] = 0b00000000_00111000_00101000_00111000_00000000_00000000_00000000_00000000L;
        KING_MOVES[45] = 0b00000000_01110000_01010000_01110000_00000000_00000000_00000000_00000000L;
        KING_MOVES[46] = 0b00000000_11100000_10100000_11100000_00000000_00000000_00000000_00000000L;
        KING_MOVES[47] = 0b00000000_11000000_01000000_11000000_00000000_00000000_00000000_00000000L;
        KING_MOVES[48] = 0b00000011_00000010_00000011_00000000_00000000_00000000_00000000_00000000L;
        KING_MOVES[49] = 0b00000111_00000101_00000111_00000000_00000000_00000000_00000000_00000000L;
        KING_MOVES[50] = 0b00001110_00001010_00001110_00000000_00000000_00000000_00000000_00000000L;
        KING_MOVES[51] = 0b00011100_00010100_00011100_00000000_00000000_00000000_00000000_00000000L;
        KING_MOVES[52] = 0b00111000_00101000_00111000_00000000_00000000_00000000_00000000_00000000L;
        KING_MOVES[53] = 0b01110000_01010000_01110000_00000000_00000000_00000000_00000000_00000000L;
        KING_MOVES[54] = 0b11100000_10100000_11100000_00000000_00000000_00000000_00000000_00000000L;
        KING_MOVES[55] = 0b11000000_01000000_11000000_00000000_00000000_00000000_00000000_00000000L;
        KING_MOVES[56] = 0b00000010_00000011_00000000_00000000_00000000_00000000_00000000_00000000L;
        KING_MOVES[57] = 0b00000101_00000111_00000000_00000000_00000000_00000000_00000000_00000000L;
        KING_MOVES[58] = 0b00001010_00001110_00000000_00000000_00000000_00000000_00000000_00000000L;
        KING_MOVES[59] = 0b00010100_00011100_00000000_00000000_00000000_00000000_00000000_00000000L;
        KING_MOVES[60] = 0b00101000_00111000_00000000_00000000_00000000_00000000_00000000_00000000L;
        KING_MOVES[61] = 0b01010000_01110000_00000000_00000000_00000000_00000000_00000000_00000000L;
        KING_MOVES[62] = 0b10100000_11100000_00000000_00000000_00000000_00000000_00000000_00000000L;
        KING_MOVES[63] = 0b01000000_11000000_00000000_00000000_00000000_00000000_00000000_00000000L;
	}

    private void generateCaptureMoves(BitBoard bitBoard, long kingPos, List<BitBoardMove> rv) {
        byte player = bitBoard.getPlayer();
        long kingMoves = KING_MOVES[Long.numberOfTrailingZeros(kingPos)];
        kingMoves &= bitBoard.getBitmapOppColor(player);
        while(kingMoves != 0) {
        	long nextMove = Long.lowestOneBit(kingMoves);
        	kingMoves ^= nextMove;
        	BitBoardMove bbMove = BitBoard.generateCapture(
        			kingPos, nextMove, player, Piece.KING, bitBoard.getPiece(nextMove));
        	bitBoard.makeMove(bbMove);
            if( ! CheckDetector.isPlayerJustMovedInCheck(bitBoard)) {
                rv.add(bbMove);
            }
        	bitBoard.unmakeMove();
        }
    }

	@Override
	public void generateMoves(BitBoard bitBoard, boolean alreadyInCheck, long potentialPins, List<BitBoardMove> rv) {
		byte player = bitBoard.getPlayer();

		// There can be only one...
		long king = bitBoard.getBitmapColor(player) & bitBoard.getBitmapKings();
		long emptyMoves = KING_MOVES[Long.numberOfTrailingZeros(king)]&(~bitBoard.getAllPieces());

		while(emptyMoves != 0) {
			long nextMove = Long.lowestOneBit(emptyMoves);
			emptyMoves ^= nextMove;
			BitBoardMove bbMove = BitBoard.generateMove(king, nextMove, player, Piece.KING);
			bitBoard.makeMove(bbMove);
            if( ! CheckDetector.isPlayerJustMovedInCheck(bitBoard)) {
                rv.add(bbMove);
            }
			bitBoard.unmakeMove();
		}

		byte castleFlags = bitBoard.getCastlingOptions();
		if(player == Piece.WHITE && ! alreadyInCheck) {
			if((castleFlags & BitBoard.CASTLE_WKS) != 0 && (bitBoard.getAllPieces() & EMPTY_WKS) == 0) {
                if( ! isIntermediateCheck(bitBoard, king, king<<1, player)) {
                	if(isCastlingPossible(bitBoard, player, BitBoard.CASTLE_WKS)) {
                        rv.add(BitBoard.generateCastling(BitBoard.CASTLE_WKS));
                	}
                }
			}
			if((castleFlags & BitBoard.CASTLE_WQS) != 0 && (bitBoard.getAllPieces() & EMPTY_WQS) == 0) {
                if( ! isIntermediateCheck(bitBoard, king, king>>>1, player)) {
                	if(isCastlingPossible(bitBoard, player, BitBoard.CASTLE_WQS)) {
                        rv.add(BitBoard.generateCastling(BitBoard.CASTLE_WQS));
                	}
                }
			}
		} else if(player == Piece.BLACK && ! alreadyInCheck) {
			if((castleFlags & BitBoard.CASTLE_BKS) != 0 && (bitBoard.getAllPieces() & EMPTY_BKS) == 0) {
                if( ! isIntermediateCheck(bitBoard, king, king<<1, player)) {
                	if(isCastlingPossible(bitBoard, player, BitBoard.CASTLE_BKS)) {
                        rv.add(BitBoard.generateCastling(BitBoard.CASTLE_BKS));
                	}
                }
			}
			if((castleFlags & BitBoard.CASTLE_BQS) != 0 && (bitBoard.getAllPieces() & EMPTY_BQS) == 0) {
                if( ! isIntermediateCheck(bitBoard, king, king>>>1, player)) {
                	if(isCastlingPossible(bitBoard, player, BitBoard.CASTLE_BQS)) {
                        rv.add(BitBoard.generateCastling(BitBoard.CASTLE_BQS));
                	}
                }
			}
		}

		while(king != 0) {
			long nextPiece = Long.lowestOneBit(king);
			king ^= nextPiece;
			this.generateCaptureMoves(bitBoard, nextPiece, rv);
		}
	}

	// Bit twiddling routines...
	private boolean isCastlingPossible(BitBoard bitBoard, byte player, short castleDir) {
		BitBoardMove bbMove = BitBoard.generateCastling(castleDir);
		bitBoard.makeMove(bbMove);
        boolean rv = ! CheckDetector.isPlayerJustMovedInCheck(bitBoard);
		bitBoard.unmakeMove();
		return rv;
	}

	private boolean isIntermediateCheck(BitBoard bitBoard, long fromSquare, long toSquare, byte player) {
		BitBoardMove bbMove = BitBoard.generateMove(fromSquare, toSquare, player, Piece.KING);
		bitBoard.makeMove(bbMove);
        boolean rv = CheckDetector.isPlayerJustMovedInCheck(bitBoard);
		bitBoard.unmakeMove();
		return rv;
	}
}
