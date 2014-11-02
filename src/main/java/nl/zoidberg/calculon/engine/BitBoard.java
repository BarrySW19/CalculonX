/**
 * Calculon - A Java chess-engine.
 *
 * Copyright (C) 2008-2010 Barry Smith
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

import nl.zoidberg.calculon.model.Piece;
import nl.zoidberg.calculon.model.Result;

import java.util.*;


public class BitBoard {
	public static final long FINAL_RANKS = 255L<<56 | 255L;
	
	public final static int MAP_WHITE 	= Piece.WHITE;
	public final static int MAP_BLACK 	= Piece.BLACK;
	
	public final static int MAP_PAWNS 	= Piece.PAWN;
	public final static int MAP_KNIGHTS = Piece.KNIGHT;
	public final static int MAP_BISHOPS = Piece.BISHOP;
	public final static int MAP_ROOKS 	= Piece.ROOK;
	public final static int MAP_QUEENS 	= Piece.QUEEN;
	public final static int MAP_KINGS 	= Piece.KING;

    public static final short CASTLE_WQS = 0x01;
    public static final short CASTLE_WKS = 0x02;
    public static final short CASTLE_BQS = 0x04;
    public static final short CASTLE_BKS = 0x08;
	public static final int IS_EN_PASSANT = 0x10;
	public static final int EN_PASSANT_MASK = 0xF0;
	public static final byte CASTLE_MASK = 0x0F;

    private final static int IDX_FLAGS  = 7;
    private static final long PLAYER_MASK = 0x100;
	private long bitmaps[] = new long[9];

	private short moveCount;
    //private short halfMoveCount;
//	private BitBoardMove lastMove;
    private Stack<ReverseMove> reverseMoves = new Stack<>();

    private static class ReverseMove {
        private long[] reverseBitmaps = new long[9];
        private short reverseHalfMove;
        private BitBoardMove reverseMove;

        private ReverseMove(int reverseHalfMove, BitBoardMove reverseMove) {
            this.reverseHalfMove = (short) reverseHalfMove;
            this.reverseMove = reverseMove;
        }
    }

    @Override
    public BitBoard clone() {
        BitBoard bb = new BitBoard();
        System.arraycopy(bitmaps, 0, bb.bitmaps, 0, bitmaps.length);
        bb.moveCount = moveCount;
        bb.reverseMoves = (Stack<ReverseMove>) reverseMoves.clone();
        return bb;
    }

    public byte getCastlingOptions() {
        return (byte) (bitmaps[IDX_FLAGS] & CASTLE_MASK);
    }

    public static ShiftStrategy getShiftStrategy(int colour) {
        return colour == Piece.WHITE ? ShiftStrategy.WHITE : ShiftStrategy.BLACK;
    }
	
	public static long getRankMap(int rank) {
		if(rank >=0 && rank < 8) {
			return 255L<<(rank*8);
		}
		return 0;
	}

	/**
	 * For use by position loading routines - clears the board.
	 * 
	 * @return The BitBoard itself.
	 */
	public BitBoard clear() {
		for(int i = 0; i < bitmaps.length; i++) {
			bitmaps[i] = 0;
		}
		invalidateHistory();
		
		return this;
	}
	
	/**
	 * For use by position loading routines - puts a piece on a square.
	 * 
	 * @param file The file 0 = A, 7 = H
	 * @param rank The rank minus 1
	 * @param piece The piece
	 * @return Reference to this bitboard
	 */
	public BitBoard setPiece(int file, int rank, byte piece) {
		long pos = 1L<<(rank<<3)<<file;
		bitmaps[piece&Piece.MASK_TYPE] |= pos;
		bitmaps[piece&Piece.MASK_COLOR] |= pos;
		invalidateHistory();
		return this;
	}
	
	public BitBoard setPlayer(byte player) {
        if(player == Piece.WHITE) {
            bitmaps[IDX_FLAGS] &= ~PLAYER_MASK;
        } else {
            bitmaps[IDX_FLAGS] |= PLAYER_MASK;
        }
		invalidateHistory();
		return this;
	}
	
	public BitBoard setMoveNumber(short moveNumber) {
		this.moveCount = (short) ((moveNumber - 1) * 2);
		if(getPlayer() == Piece.BLACK) {
			this.moveCount++;
		}
		return this;
	}
	
	public BitBoard setCastlingOptions(byte options) {
        bitmaps[IDX_FLAGS] &= ~CASTLE_MASK;
        bitmaps[IDX_FLAGS] |= (options & CASTLE_MASK);
        invalidateHistory();
        return this;
	}
	
	public BitBoard setEnPassantFile(int file) {
        bitmaps[IDX_FLAGS] &= ~EN_PASSANT_MASK;
		if(file != -1) {
            bitmaps[IDX_FLAGS] |= IS_EN_PASSANT;
            bitmaps[IDX_FLAGS] |= (file<<5);
		}
		return this;
	}
	
	private void invalidateHistory() {
		moveCount = 0;
		reverseMoves.clear();
	}
	
	public BitBoard initialise() {
		bitmaps = new long[9];
		bitmaps[Piece.WHITE] = 0xFFFFL;
		bitmaps[Piece.BLACK] = 0xFFFFL<<48;
		bitmaps[Piece.PAWN] = 0xFFL<<48 | 0xFFL<<8;
		bitmaps[Piece.ROOK] = 0x81L<<56 | 0x81L;
		bitmaps[Piece.KNIGHT] = 0x42L<<56 | 0x42L;
		bitmaps[Piece.BISHOP] = 0x24L<<56 | 0x24L;
		bitmaps[Piece.QUEEN] = 0x08L<<56 | 0x08L;
		bitmaps[Piece.KING] = 0x10L<<56 | 0x10L;
        bitmaps[IDX_FLAGS] = CASTLE_MASK;
		moveCount = 0;
		reverseMoves.clear();
		
//		System.out.println(toPrettyString(bitmaps[Piece.BLACK]));
		return this;
	}
	
	public boolean isEnPassant() {
		return (bitmaps[IDX_FLAGS] & IS_EN_PASSANT) != 0;
	}

    @SuppressWarnings("unused")
    public short getMoveCount() {
		return moveCount;
	}

	public short getMoveNumber() {
    	return (short) (moveCount / 2 + 1);
    }
	
	public short getHalfMoveCount() {
		return reverseMoves.isEmpty() ? 0 : reverseMoves.peek().reverseHalfMove;
	}

	public int getEnPassantFile() {
		return (int) ((bitmaps[IDX_FLAGS] & EN_PASSANT_MASK)>>>5);
	}
	
	public int getEnPassantRank() {
		return getPlayer() == Piece.WHITE ? 5 : 2;
	}
	
	public static long getFileMap(int file) {
		return 0x0101010101010101L << file;
	}
	
	public BitBoard() {
	}
	
	public byte getPlayer() {
        return (bitmaps[IDX_FLAGS] & PLAYER_MASK) == 0 ? Piece.WHITE : Piece.BLACK;
	}

    @SuppressWarnings("unused")
	public static long toMap(int file, int rank) {
		return 1L<<(rank<<3)<<file;
	}

	public String toString() {
		StringBuilder buf = new StringBuilder();

		buf.append("white   ").append(toPrettyString(bitmaps[MAP_WHITE])).append("\n");
		buf.append("black   ").append(toPrettyString(bitmaps[MAP_BLACK])).append("\n");

		buf.append("pawns   ").append(toPrettyString(bitmaps[MAP_PAWNS])).append("\n");
		buf.append("rooks   ").append(toPrettyString(bitmaps[MAP_ROOKS])).append("\n");
		buf.append("knights ").append(toPrettyString(bitmaps[MAP_KNIGHTS])).append("\n");
		buf.append("bishops ").append(toPrettyString(bitmaps[MAP_BISHOPS])).append("\n");
		buf.append("queens  ").append(toPrettyString(bitmaps[MAP_QUEENS])).append("\n");
		buf.append("kings   ").append(toPrettyString(bitmaps[MAP_KINGS])).append("\n");
		buf.append("flags   ").append(Integer.toBinaryString((int) (bitmaps[IDX_FLAGS] & 0xFF)))
                .append(", player = ").append(getPlayer());
		
		return buf.toString();
	}
	
    /**
     * Not a flip, but a complete reversal of colors. Creates essentially the same position, but with colors reversed.
     *
     * @return The reversed board (this).
     */
    public BitBoard reverse() {
    	for(int i = 0; i < bitmaps.length; i++) {
            if(i != IDX_FLAGS) {
    		    bitmaps[i] = reverse(bitmaps[i]);
            }
    	}

        long tempValue = bitmaps[Piece.WHITE];
    	bitmaps[Piece.WHITE] = bitmaps[Piece.BLACK];
    	bitmaps[Piece.BLACK] = tempValue;

    	byte newCastleFlags = (byte) (((bitmaps[IDX_FLAGS] & 0x03) << 2) | ((bitmaps[IDX_FLAGS] & 0x0c)>>>2));
        bitmaps[IDX_FLAGS] &= ~CASTLE_MASK;
        bitmaps[IDX_FLAGS] |= newCastleFlags;
        bitmaps[IDX_FLAGS] ^= PLAYER_MASK;
        
    	invalidateHistory();

        return this;
    }
    
    private static long reverse(long bits) {
    	return (getRankMap(0) & bits) << 56
			| (getRankMap(1) & bits) << 40
			| (getRankMap(2) & bits) << 24
			| (getRankMap(3) & bits) << 8
			| (getRankMap(4) & bits) >>> 8
			| (getRankMap(5) & bits) >>> 24
			| (getRankMap(6) & bits) >>> 40
			| (getRankMap(7) & bits) >>> 56;
    }
	
	public final long getBitmapColor() {
		return bitmaps[getPlayer()];
	}

    public final long getBitmapColor(byte color) {
        return color == Piece.WHITE ? getBitmapWhite() : getBitmapBlack();
    }

    public final long getBitmapAll() {
        return bitmaps[Piece.WHITE] | bitmaps[Piece.BLACK];
    }

    public final long getBitmapOppColor() {
		return bitmaps[getPlayer()^Piece.BLACK];
	}

	public final long getBitmapOppColor(byte color) {
		return color == Piece.WHITE ? getBitmapBlack() : getBitmapWhite();
	}
	
	public final long getBitmapWhite() {
		return bitmaps[MAP_WHITE];
	}

	public final long getBitmapBlack() {
		return bitmaps[MAP_BLACK];
	}

    public final long getBitmapPawns() {
        return bitmaps[MAP_PAWNS];
    }

    public final long getBitmapRooks() {
		return bitmaps[MAP_ROOKS];
	}

	public final long getBitmapKnights() {
		return bitmaps[MAP_KNIGHTS];
	}

	public final long getBitmapBishops() {
		return bitmaps[MAP_BISHOPS];
	}

	public final long getBitmapQueens() {
		return bitmaps[MAP_QUEENS];
	}

    public final long getBitmapKings() {
        return bitmaps[MAP_KINGS];
    }

    public final long getBitmapKings(byte color) {
        return getBitmapColor(color) & bitmaps[MAP_KINGS];
    }

    public final long getBitmapPawns(byte color) {
        return getBitmapColor(color) & bitmaps[MAP_PAWNS];
    }

    public static String toPrettyString(long val) {
		StringBuilder buf = new StringBuilder();
		for(int i = 0; i < 64; i++) {
			buf.insert(0, (val & 1L<<i) == 0 ? "0" : "1");
			if(i%8 == 7) {
				buf.insert(0, " ");
			}
		}
		buf.delete(0, 1);
		return buf.toString();
	}
	
	/**
	 * Returns the file/rank of the lowest 1 in the bitmap; the value is returned as file first, then rank
     * to match the normal practice of quoting a square in this way, e.g. E4.
     *
	 * @param bitmap The bitmap
	 * @return file/rank pair as ints, file first then rank.
	 */
	public static int[] toCoords(long bitmap) {
		int zeros = Long.numberOfTrailingZeros(bitmap);
        return new int[] { zeros&0x07, zeros>>>3};
	}

	public long getAllPieces() {
		return bitmaps[MAP_BLACK] | bitmaps[MAP_WHITE];
	}

	public final boolean hasMatingMaterial() {
		if((bitmaps[MAP_QUEENS] | bitmaps[MAP_ROOKS] | bitmaps[MAP_PAWNS]) != 0) {
			return true;
		}
		long minorMap = bitmaps[MAP_BISHOPS] | bitmaps[MAP_KNIGHTS];
        return Long.bitCount(minorMap & bitmaps[MAP_BLACK]) > 1 || Long.bitCount(minorMap & bitmaps[MAP_WHITE]) > 1;
    }
	
	public byte getPiece(long pos) {
		long val = Long.rotateLeft(bitmaps[MAP_PAWNS] & pos, MAP_PAWNS)
			| Long.rotateLeft(bitmaps[MAP_KNIGHTS] & pos, MAP_KNIGHTS)
			| Long.rotateLeft(bitmaps[MAP_BISHOPS] & pos, MAP_BISHOPS)
			| Long.rotateLeft(bitmaps[MAP_ROOKS] & pos, MAP_ROOKS)
			| Long.rotateLeft(bitmaps[MAP_QUEENS] & pos, MAP_QUEENS)
			| Long.rotateLeft(bitmaps[MAP_KINGS] & pos, MAP_KINGS);
		if(val == 0) {
			return Piece.EMPTY;
		}
		return (byte) ((Long.numberOfTrailingZeros(val) - Long.numberOfTrailingZeros(pos)) & 0x07);
	}
	
	public byte getColoredPiece(long pos) {
		byte piece = getPiece(pos);
		if((bitmaps[MAP_BLACK] & pos) != 0) {
			piece |= Piece.BLACK;
		}
		return piece;
	}
	
	private void castle(BitBoardMove move) {
		switch(move.castleDir) {
		case CASTLE_WKS:
			bitmaps[MAP_WHITE] ^= 0xF0L;
			bitmaps[MAP_KINGS] ^= 0x50L;
			bitmaps[MAP_ROOKS] ^= 0xA0L;
			break;
		case CASTLE_WQS:
			bitmaps[MAP_WHITE] ^= 0x1DL;
			bitmaps[MAP_KINGS] ^= 0x14L;
			bitmaps[MAP_ROOKS] ^= 0x09L;
			break;
		case CASTLE_BKS:
			bitmaps[MAP_BLACK] ^= -1152921504606846976L;
			bitmaps[MAP_KINGS] ^= 5764607523034234880L;
			bitmaps[MAP_ROOKS] ^= -6917529027641081856L;
			break;
		case CASTLE_BQS:
			bitmaps[MAP_BLACK] ^= 2089670227099910144L;
			bitmaps[MAP_KINGS] ^= 1441151880758558720L;
			bitmaps[MAP_ROOKS] ^= 648518346341351424L;
			break;
		}
        bitmaps[IDX_FLAGS] &= ~move.castleOff;
	}
	
	public void makeMove(BitBoardMove move) {
        ReverseMove reverseMove;
        if(reverseMoves.isEmpty()) {
            reverseMove = new ReverseMove(1, null);
        } else {
            ReverseMove prevMove = reverseMoves.peek();
            reverseMove = new ReverseMove(prevMove.reverseHalfMove + 1, prevMove.reverseMove);
        }
        System.arraycopy(bitmaps, 0, reverseMove.reverseBitmaps, 0, bitmaps.length);

        reverseMoves.add(reverseMove);
		moveCount++;
        bitmaps[IDX_FLAGS] ^= PLAYER_MASK;
        bitmaps[IDX_FLAGS] &= ~EN_PASSANT_MASK;

		if(move.castle) {
			castle(move);
			return;
		}

		if(move.capture) {
            reverseMove.reverseHalfMove = 0;
			bitmaps[move.captureType] ^= move.captureSquare;
			bitmaps[move.colorIndex^0x08] ^= move.captureSquare;
		} else if(move.pieceIndex == Piece.PAWN) {
            reverseMove.reverseHalfMove = 0;
		}
		bitmaps[move.pieceIndex] ^= move.xorPattern;
		bitmaps[move.colorIndex] ^= move.xorPattern;
		if(move.promotion) {
			bitmaps[move.pieceIndex] ^= move.toSquare;
			bitmaps[move.promoteTo] ^= move.toSquare;
		}
		if(move.enpassant) {
            bitmaps[IDX_FLAGS] &= ~EN_PASSANT_MASK;
            bitmaps[IDX_FLAGS] |= move.epFile;
		} else {
            bitmaps[IDX_FLAGS] &= ~EN_PASSANT_MASK;
		}
        bitmaps[IDX_FLAGS] &= ~move.castleOff;
	}
	
	public void unmakeMove() {
		moveCount--;
        ReverseMove reverseMove = reverseMoves.pop();
        System.arraycopy(reverseMove.reverseBitmaps, 0, bitmaps, 0, bitmaps.length);
	}

    private static byte[] SPACES = { 0, 7, 8, 15 };

    public BitSet getCacheId() {
        byte[] baseData = new byte[65];
        baseData[64] = (byte) 0b11111111;
        addBitmapToChecksum(bitmaps[MAP_KNIGHTS], Piece.KNIGHT, baseData);
        addBitmapToChecksum(bitmaps[MAP_BISHOPS], Piece.BISHOP, baseData);
        addBitmapToChecksum(bitmaps[MAP_ROOKS], Piece.ROOK, baseData);
        addBitmapToChecksum(bitmaps[MAP_QUEENS], Piece.QUEEN, baseData);
        addBitmapToChecksum(bitmaps[MAP_KINGS], Piece.KING, baseData);
        addBitmapToChecksum(bitmaps[MAP_BLACK], Piece.BLACK, baseData);

        int flagsCount = 5;
        int outPosition = 0;
        byte[] out = new byte[64];
        for(int i = 0; i < 64; i++) {
            if(baseData[i] != 0) {
                out[outPosition++] = baseData[i];
            } else if(baseData[i] == 0 && flagsCount > 0) {
                out[outPosition++] = SPACES[((int) ((bitmaps[IDX_FLAGS] >>> ((flagsCount - 1) * 2)) & 3))];
                flagsCount--;
            } else {
                int zeroCount = 0;
                while(zeroCount+i < baseData.length && baseData[i+zeroCount] == 0) {
                    zeroCount++;
                }
                if(zeroCount == 1) {
                    out[outPosition++] = 0;
                } else {
                    zeroCount = Math.min(33, zeroCount);
                    i += (zeroCount - 1);
                    out[outPosition++] = (byte) (zeroCount >= 18 ? 0x0f : 0x07);
                    out[outPosition++] = (byte) ((zeroCount-2) & 0x0f);
                }
            }
        }

//        for(int i = 0; i < 8; i++) {
//            for(int j = 0; j < 8; j++) {
//                if(i*8+j < out.length) {
//                    System.out.print(String.format("%02x ", out[i*8+j]));
//                }
//            }
//            System.out.println("");
//        }

        long[] bitmapData = new long[(outPosition/16) + 1];
        for(int i = 0; i < outPosition; i++) {
            bitmapData[i/16] |= ((long)out[i]) << ((i%16)*4);
        }

//        for(long lx: l) {
//            System.out.println(String.format("%016x", lx));
//        }

        return BitSet.valueOf(bitmapData);
    }

    private static void addBitmapToChecksum(long bitmap, byte value, byte[] checkSumData) {
        while (bitmap != 0) {
            long nextBit = Long.lowestOneBit(bitmap);
            bitmap ^= nextBit;
            int location = Long.numberOfTrailingZeros(nextBit);
            checkSumData[location] |= value;
        }
    }

    public Result getResult() {
		if(isDrawnByRule()) {
			return Result.RES_DRAW;
		}

		// Do a fast dirty test to rule out stalemates. We look for any pieces which couldn't possibly
        // be pinned and see if they have obvious moves.
		boolean inCheck = CheckDetector.isPlayerToMoveInCheck(this); 
        if( ! inCheck) {
            byte player = getPlayer();
            long myKing = bitmaps[MAP_KINGS] & bitmaps[player];
            int kingIdx = Long.numberOfTrailingZeros(myKing);
            long possiblePins = Bitmaps.star2Map[kingIdx]
                       & ~BitBoard.getFileMap(kingIdx&0x07) & ~Bitmaps.BORDER & bitmaps[player];
            possiblePins ^= bitmaps[getPlayer()];

            long freePawns = possiblePins & bitmaps[MAP_PAWNS];
            freePawns &= ~(getPlayer() == Piece.WHITE ? getAllPieces()>>>8 : getAllPieces()<<8);
            if(freePawns != 0) {
                // Any pawn move means we're not stalemated...
                return Result.RES_NO_RESULT;
            }
        }

        if ( ! new MoveGeneratorImpl(this).hasNext()) {
			if (inCheck) {
		   		return (getPlayer() == Piece.BLACK ? Result.RES_WHITE_WIN : Result.RES_BLACK_WIN);
			} else {
				return Result.RES_DRAW; // Stalemate
			}
		}
        
		return Result.RES_NO_RESULT;
	}

	public int getRepeatedCount() {
        ReverseMove reverseMove = reverseMoves.isEmpty() ? null : reverseMoves.peek();
		if(reverseMove == null || reverseMove.reverseHalfMove < 8) {
			// No point in checking until at least 8 half moves made...
			return 1;
		}

        final BitBoard clone = this.clone();
		int repeatCount = 1;
		while(! clone.reverseMoves.isEmpty() && clone.reverseMoves.peek().reverseHalfMove >= 2) {
			clone.unmakeMove();
			clone.unmakeMove();
			if(this.equalPosition(clone)) {
				repeatCount++;
			}
		}
    	return repeatCount;
    }
	
	public boolean equalPosition(BitBoard bb) {
        return Arrays.equals(bb.bitmaps, bitmaps);
	}

	public boolean isDrawnByRule() {
        int halfCount = reverseMoves.isEmpty() ? 0 : reverseMoves.peek().reverseHalfMove;
        return halfCount >= 100 || this.getRepeatedCount() >= 3 || !hasMatingMaterial();
    }
    
	public static BitBoardMove generateMove(
			long fromSquare, long toSquare, int colorIndex, int pieceIndex) {
		return new BitBoardMove(fromSquare, toSquare, colorIndex, pieceIndex);
	}

	public static BitBoardMove generateDoubleAdvanceMove(
			long fromSquare, long toSquare, int colorIndex) {
		BitBoardMove move = new BitBoardMove(fromSquare, toSquare, colorIndex, Piece.PAWN);
		move.enpassant = true;
		move.epFile = (short) (((Long.numberOfTrailingZeros(fromSquare)&0x07)<<5) | IS_EN_PASSANT);
		return move;
	}
	
	public static BitBoardMove generateCapture(
			long fromSquare, long toSquare, byte colorIndex, byte pieceIndex, byte captureType) {
		return new BitBoardMove(fromSquare, toSquare, colorIndex, pieceIndex, captureType);
	}

    public static BitBoardMove generatePromote(
   			long fromSquare, long toSquare, int colorIndex, byte promoteTo) {
   		BitBoardMove move = new BitBoardMove(fromSquare, toSquare, colorIndex, Piece.PAWN);
   		move.promotion = true;
   		move.promoteTo = promoteTo;
   		return move;
   	}

    public static List<BitBoardMove> generatePromotions(long fromSquare, long toSquare, int colorIndex) {
        BitBoardMove[] moves = new BitBoardMove[4];
        moves[0] = BitBoard.generatePromote(fromSquare, toSquare, colorIndex, Piece.QUEEN);
        moves[1] = BitBoard.generatePromote(fromSquare, toSquare, colorIndex, Piece.KNIGHT);
        moves[2] = BitBoard.generatePromote(fromSquare, toSquare, colorIndex, Piece.ROOK);
        moves[3] = BitBoard.generatePromote(fromSquare, toSquare, colorIndex, Piece.BISHOP);
        return Arrays.asList(moves);
   	}

	public static BitBoardMove generateCaptureAndPromote(
			long fromSquare, long toSquare, int colorIndex, byte captureType, byte promoteTo) {
		BitBoardMove move = new BitBoardMove(fromSquare, toSquare, colorIndex, Piece.PAWN, captureType);
		move.promotion = true;
		move.promoteTo = promoteTo;
		return move;
	}

	public static BitBoardMove generateEnPassantCapture(
			long fromSquare, long toSquare, int colorIndex) {
		BitBoardMove move = new BitBoardMove(fromSquare, toSquare, colorIndex, Piece.PAWN, Piece.PAWN);
		move.captureSquare = (colorIndex == Piece.WHITE ? toSquare>>>8 : toSquare<<8);
		return move;
	}
	
	public static BitBoardMove generateCastling(short castleDir) {
		return new BitBoardMove(castleDir);
	}
	
	public static long coordToPosition(String coord) {
		return 1L<<(EngineUtils.FILES.indexOf(coord.charAt(0)) | EngineUtils.RANKS.indexOf(coord.charAt(1))<<3); 
	}
	
	/**
	 * Translates an algebraic move into a BitBoardMove - slow but effective... definitely not for use in
	 * move generation routines, but fine for use with interfaces, etc.
	 * 
	 * @param move - A simple algebraic, e.g. E2E4, F7F8=Q, O-O-O.
	 * @return The bitboard move.
	 */
	public BitBoardMove getMove(String move) {
		if((move.equals("E1G1") && getPiece(1L<<4) == Piece.KING)
				|| (move.equals("E8G8") && getPiece(1L<<60) == Piece.KING)) {
			move = "O-O";
		}
		if((move.equals("E1C1") && getPiece(1L<<4) == Piece.KING)
				|| (move.equals("E8C8") && getPiece(1L<<60) == Piece.KING)) {
			move = "O-O-O";
		}

        byte player = getPlayer();
		if(move.equals("O-O")) {
			return BitBoard.generateCastling(player == Piece.WHITE ? BitBoard.CASTLE_WKS : BitBoard.CASTLE_BKS);
		}
		if(move.equals("O-O-O")) {
			return BitBoard.generateCastling(player == Piece.WHITE ? BitBoard.CASTLE_WQS : BitBoard.CASTLE_BQS);
		}
		
		long from = coordToPosition(move.substring(0, 2));
		long to = coordToPosition(move.substring(2, 4));
		
		byte piece = getPiece(from);
		byte pieceCap = getPiece(to);
		if(piece == Piece.PAWN && Math.abs(Long.numberOfTrailingZeros(from) - Long.numberOfTrailingZeros(to)) == 16) {
			return BitBoard.generateDoubleAdvanceMove(from, to, player);
		}
		
		if(piece == Piece.PAWN && (to & FINAL_RANKS) != 0) {
			byte promoTo = 0;
			switch(move.charAt(move.length()-1)) {
				case 'Q': promoTo = Piece.QUEEN; break;
				case 'R': promoTo = Piece.ROOK; break;
				case 'N': promoTo = Piece.KNIGHT; break;
				case 'B': promoTo = Piece.BISHOP; break;
			}
			if(pieceCap != 0) {
				return BitBoard.generateCaptureAndPromote(from, to, player, pieceCap, promoTo);
			} else {
				return BitBoard.generatePromote(from, to, player, promoTo);
			}
		}
		
		if(piece == Piece.PAWN && Math.abs(Long.numberOfTrailingZeros(from) - Long.numberOfTrailingZeros(to)) != 8 && pieceCap == 0) {
			return BitBoard.generateEnPassantCapture(from, to, player);
		}
		
		if(pieceCap != 0) {
			return BitBoard.generateCapture(from, to, player, piece, getPiece(to));
		} else {
			return BitBoard.generateMove(from, to, player, piece);
		}
	}
	
	/**
	 * Represents all the information needed to make/unmake a move on a bitboard.
	 */
	public static class BitBoardMove {
		private int colorIndex;
		private int pieceIndex;
		private byte captureType;
		private byte promoteTo;
		private long captureSquare;
		private long toSquare;
		private long fromSquare;
		private short castleDir;
		private short epFile;
		
		private long xorPattern;
		private boolean capture = false;
		private boolean promotion = false;
		private boolean castle = false;
		private boolean enpassant = false;
		private byte castleOff;

		private BitBoardMove(final short castleDir) {
			this.castle = true;
			this.castleDir = castleDir;
			if(castleDir == CASTLE_WKS || castleDir == CASTLE_WQS) {
				castleOff = CASTLE_WKS | CASTLE_WQS;
			} else {
				castleOff = CASTLE_BKS | CASTLE_BQS;
			}
		}
		
		private BitBoardMove(final long fromSquare, final long toSquare, final int colorIndex, final int pieceIndex) {
			this.fromSquare = fromSquare;
			this.toSquare = toSquare;
			this.colorIndex = colorIndex;
			this.pieceIndex = pieceIndex;
			this.xorPattern = (fromSquare|toSquare);
			
			if((xorPattern & 0x90L) != 0) {
				castleOff |= CASTLE_WKS;
			}
			if((xorPattern & 0x11L) != 0) {
				castleOff |= CASTLE_WQS;
			}
			if((xorPattern & 0x90L<<56) != 0) {
				castleOff |= CASTLE_BKS;
			}
			if((xorPattern & 0x11L<<56) != 0) {
				castleOff |= CASTLE_BQS;
			}
		}

		private BitBoardMove(final long fromSquare, final long toSquare, final int colorIndex, final int pieceIndex, final byte captureType) {
			this(fromSquare, toSquare, colorIndex, pieceIndex);
			this.captureType = captureType;
			this.capture = true;
			this.captureSquare = toSquare;
		}
		
		public String getAlgebraic() {
			if(castle) {
				switch(castleDir) {
				case CASTLE_WKS:
				case CASTLE_BKS:
					return "O-O";
				case CASTLE_WQS:
				case CASTLE_BQS:
					return "O-O-O";
				}
			}
			String move = EngineUtils.toCoord(fromSquare) + EngineUtils.toCoord(toSquare);
			if(promotion) {
				switch(promoteTo) {
					case Piece.QUEEN: 	move = move + "=Q"; break;
					case Piece.ROOK: 	move = move + "=R"; break;
					case Piece.BISHOP: 	move = move + "=B"; break;
					case Piece.KNIGHT: 	move = move + "=N"; break;
				}
			}
			return move;
		}

        public boolean isCapture() {
            return capture;
        }

        public boolean isPromotion() {
            return promotion;
        }

		/**
		 * Constructs a <code>String</code> with all attributes
		 * in name = value format.
		 *
		 * @return a <code>String</code> representation 
		 * of this object.
		 */
		public String toString() {
		    final String TAB = ", ";

            return "BitBoardMove ( "
                + "colorIndex = " + Piece.COLORS[this.colorIndex] + TAB
                + "pieceIndex = " + Piece.NAMES[this.pieceIndex] + TAB
                + (capture ? "captureType = " + Piece.NAMES[this.captureType] + TAB : "")
                + (promotion ? "promoteTo = " + Piece.NAMES[this.promoteTo] + TAB : "")
                + (capture ? "captureSquare = " + EngineUtils.toCoord(this.captureSquare) + TAB : "")
                + "from/to = " + EngineUtils.toCoord(this.fromSquare) + EngineUtils.toCoord(this.toSquare) + TAB
                + (castle ? "castleDir = " + this.castleDir + TAB : "")
//		        + "xorPattern = " + this.xorPattern + TAB
                + "castleOff = " + this.castleOff + TAB
                + (enpassant ? "epFile = " + this.epFile + TAB : "")
                + " )";
		}
	}
}
