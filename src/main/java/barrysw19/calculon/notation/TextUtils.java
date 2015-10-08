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
package barrysw19.calculon.notation;

import barrysw19.calculon.model.Piece;
import barrysw19.calculon.engine.BitBoard;
import barrysw19.calculon.engine.EngineUtils;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class TextUtils {
	
	public static List<String> getMiniTextBoard(BitBoard board) {
		List<String> rv = new ArrayList<>();
		rv.add("+--------+");
		for(int rank = 7; rank >= 0; rank--) {
			StringBuilder cRank = new StringBuilder("|");
			for(int file = 0; file < 8; file++) {
				byte piece = board.getColoredPiece(1L<<(rank<<3)<<file);
				if(piece == Piece.EMPTY) {
					cRank.append((file+rank)%2 == 0 ? "." : " ");
				} else {
					cRank.append(FENUtils.getSymbol(piece));
				}
			}
			rv.add(cRank.append("|").toString());
		}
		rv.add("+--------+");
		return rv;
	}
	
	public static String textBoardToString(List<String> l) {
		StringBuilder buf = new StringBuilder();
		for(String s: l) {
			buf.append(s).append("\n");
		}
		return buf.toString();
	}
	
	public static String getHtmlBoard(BitBoard board) {
		StringBuilder buf = new StringBuilder();
		buf.append("<html>\n");
		buf.append("<head><link rel='stylesheet' type='text/css' href='/chess.css' /></head>\n");
		buf.append("<body>\n");
		buf.append("<table cellpadding='0' cellspacing='0' border='0'>\n");
		
		for(int rank = 7; rank >= 0; rank--) {
			buf.append("<tr>\n");
			for(int file = 0; file < 8; file++) {
				if((file+rank)%2 == 1) {
					buf.append("<td class='square_l'>");
				} else {
					buf.append("<td class='square_d'>");
				}
				byte piece = board.getColoredPiece(1L<<(rank<<3)<<file);
				if(piece != Piece.EMPTY) {
					buf.append("<img src='img/25px-Chess_tile_").append(getImageName(piece)).append(".png' />");
				}
				buf.append("</td>\n");
			}
			buf.append("</tr>\n");
		}
		
		buf.append("</table>\n");
		buf.append("</body>\n");
		buf.append("</html>\n");

		return buf.toString();
	}

	private static String getImageName(byte piece) {
		byte pieceType = EngineUtils.getType(piece);
		
		if(pieceType == Piece.EMPTY) {
			return "";
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
			return symbol + "d";
		} else {
			return symbol + "l";
		}
	}
}
