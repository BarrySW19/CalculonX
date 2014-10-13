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
package nl.zoidberg.calculon.notation;

import nl.zoidberg.calculon.engine.BitBoard;
import nl.zoidberg.calculon.engine.BitBoard.BitBoardMove;
import nl.zoidberg.calculon.engine.CheckDetector;
import nl.zoidberg.calculon.engine.EngineUtils;
import nl.zoidberg.calculon.engine.MoveGeneratorImpl;
import nl.zoidberg.calculon.model.Piece;
import nl.zoidberg.calculon.model.Result;

import java.util.*;

public class PGNUtils {

	private PGNUtils() {
	}

	public static void applyMove(BitBoard bitBoard, String move) {
		String algMove = PGNUtils.toPgnMoveMap(bitBoard).get(move);
		bitBoard.makeMove(bitBoard.getMove(algMove));
	}
	
	public static void applyMoves(BitBoard bitBoard, String[] moves) {
		for(String s: moves) {
			applyMove(bitBoard, s);
		}
	}
	
	public static Map<String, String> toPgnMoveMap(BitBoard bitBoard) {
		List<BitBoardMove> allMoves = new MoveGeneratorImpl(bitBoard).getAllRemainingMoves();
		Map<String, String> rv = new HashMap<>();
		
		for(BitBoardMove intMove: allMoves) {
			rv.put(translateMove(bitBoard, intMove.getAlgebraic()), intMove.getAlgebraic());
		}
		for(String pgnMove: new HashSet<>(rv.keySet())) {
			if(pgnMove.endsWith("+") || pgnMove.endsWith("#")) {
				rv.put(pgnMove.substring(0, pgnMove.length()-1), rv.get(pgnMove));
			}
		}
		return rv;
	}

	private static String getCheckNotation(BitBoard b) {
		if( ! CheckDetector.isPlayerToMoveInCheck(b)) {
			return "";
		}
		if(b.getResult() == Result.RES_BLACK_WIN || b.getResult() == Result.RES_WHITE_WIN) {
			return "#";
		}
		return  "+";
	}
	
	/**
	 * Translate a simple algebraic move (e.g. G1F3) into its PGN equivalent (e.g. Nf3).
	 * 
	 * @param bitBoard The board
	 * @param simpleAlgebraic The simple move notation, e.g. E2E4
	 * @return The PGN version of the move
	 */
	public static String translateMove(BitBoard bitBoard, String simpleAlgebraic) {
		if (simpleAlgebraic.startsWith("O-")) {
			BitBoardMove bbMove = bitBoard.getMove(simpleAlgebraic);
			bitBoard.makeMove(bbMove);
			String rv = simpleAlgebraic + getCheckNotation(bitBoard);
			bitBoard.unmakeMove();
			return rv;
		}

		int fromFile = EngineUtils.FILES.indexOf(simpleAlgebraic.charAt(0));
		int fromRank = EngineUtils.RANKS.indexOf(simpleAlgebraic.charAt(1));
		int toFile = EngineUtils.FILES.indexOf(simpleAlgebraic.charAt(2));
		int toRank = EngineUtils.RANKS.indexOf(simpleAlgebraic.charAt(3));

		byte movePiece = bitBoard.getColoredPiece(1L<<(fromRank<<3)<<fromFile);
		
		if (movePiece == Piece.EMPTY) {
			throw new RuntimeException("Move from empty square: " + simpleAlgebraic);
		}

		StringBuilder move = new StringBuilder();
		boolean testClash = false;
		switch (movePiece & Piece.MASK_TYPE) {
		case Piece.KNIGHT:
			move.append("N");
			testClash = true;
			break;
		case Piece.BISHOP:
			move.append("B");
			testClash = true;
			break;
		case Piece.ROOK:
			move.append("R");
			testClash = true;
			break;
		case Piece.QUEEN:
			move.append("Q");
			testClash = true;
			break;
		case Piece.KING:
			if(fromFile == 4 && toFile == 6) {
				bitBoard.makeMove(bitBoard.getMove(simpleAlgebraic));
				String rv = "O-O" + getCheckNotation(bitBoard);
				bitBoard.unmakeMove();
				return rv;
			} else if(fromFile == 4 && toFile == 2) {
				bitBoard.makeMove(bitBoard.getMove(simpleAlgebraic));
				String rv = "O-O-O" + getCheckNotation(bitBoard);
				bitBoard.unmakeMove();
				return rv;
			} else {
				move.append("K");
			}
			break;
		}

		if (testClash) {
			String fromSquare = simpleAlgebraic.substring(0, 2);
			List<BitBoardMove> m = new MoveGeneratorImpl(bitBoard).getAllRemainingMoves();
			List<String> clashingPieces = new ArrayList<>();
			for (BitBoardMove key : m) {
				if (key.getAlgebraic().startsWith("O-")) {
					continue;
				}
				if (!simpleAlgebraic.substring(2, 4).equals(key.getAlgebraic().substring(2, 4))) {
					continue;
				}
				String pieceSquare = key.getAlgebraic().substring(0, 2);
				if (fromSquare.equals(pieceSquare) || bitBoard.getColoredPiece(BitBoard.coordToPosition(pieceSquare)) != movePiece) {
					continue;
				}
				clashingPieces.add(key.getAlgebraic());
			}

			if (clashingPieces.size() != 0) {
				boolean sameFile = false;
				boolean sameRank = false;
				for (String clash : clashingPieces) {
					if (clash.charAt(0) == simpleAlgebraic.charAt(0)) {
						sameFile = true;
					}
					if (clash.charAt(1) == simpleAlgebraic.charAt(1)) {
						sameRank = true;
					}
				}
				if (!sameFile) {
					move.append(Character
							.toLowerCase(simpleAlgebraic.charAt(0)));
				} else if (!sameRank) {
					move.append(simpleAlgebraic.charAt(1));
				} else {
					move.append(simpleAlgebraic.substring(0, 2).toLowerCase());
				}
			}
		}

		byte targetPiece = bitBoard.getPiece(1L<<(toRank<<3)<<toFile);
		if (targetPiece != 0) {
			if ((movePiece & Piece.MASK_TYPE) == Piece.PAWN) {
				move.append(Character.toLowerCase(simpleAlgebraic.charAt(0)));
			}
			move.append("x");
		} else if ((movePiece & Piece.MASK_TYPE) == Piece.PAWN && toFile != fromFile) {
			// En passant
			move.append(Character.toLowerCase(simpleAlgebraic.charAt(0)))
					.append("x");
		}

		move.append(Character.toLowerCase(simpleAlgebraic.charAt(2))).append(simpleAlgebraic.charAt(3));
		bitBoard.makeMove(bitBoard.getMove(simpleAlgebraic));

		if (simpleAlgebraic.indexOf('=') >= 0) {
			move.append(simpleAlgebraic.substring(simpleAlgebraic.indexOf('=')));
		}

		if (CheckDetector.isPlayerToMoveInCheck(bitBoard)) {
			move.append(getCheckNotation(bitBoard));
		}

		bitBoard.unmakeMove();
		return move.toString();
	}
}
