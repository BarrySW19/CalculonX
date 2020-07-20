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
package barrysw19.calculon.notation;

import barrysw19.calculon.engine.BitBoard;
import barrysw19.calculon.engine.EngineUtils;
import barrysw19.calculon.model.Piece;
import org.apache.commons.lang3.StringUtils;

public class FENUtils {
	private static final String FILES = "abcdefgh";

	public static String generate(BitBoard bitBoard) {
		return generateWithoutMoveCounts(bitBoard) + " " + bitBoard.getHalfMoveCount() +
				" " + bitBoard.getMoveNumber();
	}
	
	public static String generateWithoutMoveCounts(BitBoard bitBoard) {
		return generatePosition(bitBoard) +
				" " + (bitBoard.getPlayer() == Piece.WHITE ? "w" : "b") +
				" " + generateCastling(bitBoard) +
				" " + generateEnPassant(bitBoard);
	}
	
	public static String generateEnPassant(BitBoard board) {
		if( ! board.isEnPassant()) { 
			return "-";
		}

		return String.valueOf(Character.toLowerCase(EngineUtils.FILES.charAt(board.getEnPassantFile()))) +
				EngineUtils.RANKS.charAt(board.getEnPassantRank());
	}
	
	public static String generateCastling(BitBoard board) {
		StringBuilder fen = new StringBuilder();
		
		if((board.getCastlingOptions() & BitBoard.CASTLE_WKS) != 0) {
			fen.append("K");
		}
		if((board.getCastlingOptions() & BitBoard.CASTLE_WQS) != 0) {
			fen.append("Q");
		}
		if((board.getCastlingOptions() & BitBoard.CASTLE_BKS) != 0) {
			fen.append("k");
		}
		if((board.getCastlingOptions() & BitBoard.CASTLE_BQS) != 0) {
			fen.append("q");
		}
		if(fen.length() == 0) {
			fen.append("-");
		}
		
		return fen.toString();
	}
	
	public static String generatePosition(BitBoard board) {
		StringBuilder fen = new StringBuilder();
		
		for(int rank = 7; rank >= 0; rank--) {
			int emptyCount = 0;
			for(int file = 0; file < 8; file++) {
				long pos = 1L<<(rank<<3)<<file;
				String symbol = getSymbol((byte)
						(((board.getBitmapBlack()&pos) == 0 ? Piece.WHITE : Piece.BLACK) | board.getPiece(pos)));
				if(symbol == null) {
					emptyCount++;
				} else {
					if(emptyCount > 0) {
						fen.append(emptyCount);
						emptyCount = 0;
					}
					fen.append(symbol);
				}
			}
			if(emptyCount > 0) {
				fen.append(emptyCount);
			}
			if(rank > 0) {
				fen.append("/");
			}
		}
		
		return fen.toString();
	}
	
	static String getSymbol(byte piece) {
		
		byte pieceType = EngineUtils.getType(piece);
		
		if(pieceType == Piece.EMPTY) {
			return null;
		}
		
		String symbol = null;
		if(pieceType == Piece.PAWN) {
			symbol = "p";
		} else if(pieceType == Piece.KNIGHT) {
			symbol = "n";
		} else if(pieceType == Piece.BISHOP) {
			symbol = "b";
		} else if(pieceType == Piece.ROOK) {
			symbol = "r";
		} else if(pieceType == Piece.QUEEN) {
			symbol = "q";
		} else if(pieceType == Piece.KING) {
			symbol = "k";
		}
		
		if(EngineUtils.getColor(piece) == Piece.BLACK) {
			return symbol;
		} else {
			return symbol != null ? symbol.toUpperCase() : null;
		}
	}
	
	public static BitBoard loadPosition(String string, BitBoard board) {
		board.clear();
		String[] fields = StringUtils.split(string);
		String[] ranks = StringUtils.split(fields[0], '/');
		
		for(int rank = 7; rank >= 0; rank--) {
			int file = 0;
			for(int j = 0; j < ranks[7-rank].length(); j++) {
				char c = ranks[7-rank].charAt(j);
				switch(c) {
				case 'P':
					board.setPiece(file, rank, (byte) (Piece.PAWN | Piece.WHITE));
					break;
				case 'p':
					board.setPiece(file, rank, (byte) (Piece.PAWN | Piece.BLACK));
					break;
				case 'R':
					board.setPiece(file, rank, (byte) (Piece.ROOK | Piece.WHITE));
					break;
				case 'r':
					board.setPiece(file, rank, (byte) (Piece.ROOK | Piece.BLACK));
					break;
				case 'N':
					board.setPiece(file, rank, (byte) (Piece.KNIGHT | Piece.WHITE));
					break;
				case 'n':
					board.setPiece(file, rank, (byte) (Piece.KNIGHT | Piece.BLACK));
					break;
				case 'B':
					board.setPiece(file, rank, (byte) (Piece.BISHOP | Piece.WHITE));
					break;
				case 'b':
					board.setPiece(file, rank, (byte) (Piece.BISHOP | Piece.BLACK));
					break;
				case 'Q':
					board.setPiece(file, rank, (byte) (Piece.QUEEN | Piece.WHITE));
					break;
				case 'q':
					board.setPiece(file, rank, (byte) (Piece.QUEEN | Piece.BLACK));
					break;
				case 'K':
					board.setPiece(file, rank, (byte) (Piece.KING | Piece.WHITE));
					break;
				case 'k':
					board.setPiece(file, rank, (byte) (Piece.KING | Piece.BLACK));
					break;
				}
				if(c >= '1' && c <= '8') {
					file += c - '1';
				}
				file++;
			}
		}
		
		board.setPlayer("b".equals(fields[1]) ? Piece.BLACK : Piece.WHITE);
		board.setCastlingOptions((byte) 
				((fields[2].contains("K") ? BitBoard.CASTLE_WKS : 0) |
				(fields[2].contains("Q") ? BitBoard.CASTLE_WQS : 0) |
				(fields[2].contains("k") ? BitBoard.CASTLE_BKS : 0) |
				(fields[2].contains("q") ? BitBoard.CASTLE_BQS : 0)));
		
		if(fields[3].length() == 2) {
			board.setEnPassantFile(FILES.indexOf(fields[3].charAt(0)));
		} else {
			board.setEnPassantFile(-1);
		}
		board.setMoveNumber(Short.parseShort(fields[5]));
		
		return board;
	}

	public static BitBoard getBoard(String fen) {
		BitBoard board = new BitBoard();
		loadPosition(fen, board);
		return board;
	}
}
