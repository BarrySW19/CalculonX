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

import barrysw19.calculon.engine.BitBoard;
import barrysw19.calculon.engine.BitBoard.BitBoardMove;
import barrysw19.calculon.engine.CheckDetector;
import barrysw19.calculon.engine.EngineUtils;
import barrysw19.calculon.engine.MoveGeneratorImpl;
import barrysw19.calculon.model.Piece;
import barrysw19.calculon.model.Result;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static java.lang.String.format;
import static java.util.stream.Collectors.toSet;

public class PGNUtils {
    private static final Logger LOG = LoggerFactory.getLogger(PGNUtils.class);

	private PGNUtils() {
	}

	public static void applyMove(BitBoard bitBoard, String move) {
		Optional<String> algMove = Optional.ofNullable(PGNUtils.toPgnMoveMap(bitBoard).get(move));
        bitBoard.makeMove(bitBoard.getMove(algMove.orElseThrow(() -> {
            final String errMsg = format("No move found: %s %s", FENUtils.generate(bitBoard), move);
            LOG.error(errMsg);
            return new NullPointerException(errMsg);
        })));
	}

    public static Set<String> convertMovesToPgn(final BitBoard bitBoard, final Collection<BitBoardMove> moves) {
        return moves.stream().map(BitBoardMove::getAlgebraic).map(a -> PGNUtils.translateMove(bitBoard, a)).collect(toSet());
    }

    public static Set<String> convertMovesToPgn(final BitBoard bitBoard, final Iterator<BitBoardMove> moves) {
	    List<BitBoardMove> movesList = Lists.newArrayList(moves);
	    return convertMovesToPgn(bitBoard, movesList);
    }

    public static void applyMoves(BitBoard bitBoard, String... moves) {
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
