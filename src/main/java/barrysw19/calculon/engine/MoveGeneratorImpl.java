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
package barrysw19.calculon.engine;

import barrysw19.calculon.model.Piece;
import barrysw19.calculon.engine.BitBoard.BitBoardMove;

import java.util.*;

public class MoveGeneratorImpl implements MoveGenerator {
	
	private static final List<PieceMoveGenerator> MASTER;
	
	static {
        List<PieceMoveGenerator> list = new LinkedList<>();

		list.add(new PawnCaptureGenerator());
        list.add(new KnightMoveGenerator());
        list.add(new StraightMoveGenerator(
				(BitBoard bb) -> (bb.getBitmapColor() & bb.getBitmapBishops()), PreGeneratedMoves.DIAGONAL_MOVES, Piece.BISHOP));
        list.add(new StraightMoveGenerator(
                (BitBoard bb) -> (bb.getBitmapColor() & bb.getBitmapRooks()), PreGeneratedMoves.STRAIGHT_MOVES, Piece.ROOK));
        list.add(new StraightMoveGenerator(
                (BitBoard bb) -> (bb.getBitmapColor() & bb.getBitmapQueens()), PreGeneratedMoves.SLIDE_MOVES, Piece.QUEEN));
        list.add(new PawnMoveGenerator());
        list.add(new KingMoveGenerator());

        MASTER = Collections.unmodifiableList(list);
	}
	
	private static int[] DIR_LINE = new int[] { Bitmaps.BM_U, Bitmaps.BM_D, Bitmaps.BM_L, Bitmaps.BM_R, };
	private static int[] DIR_DIAG = new int[] { Bitmaps.BM_UR, Bitmaps.BM_DR, Bitmaps.BM_UL, Bitmaps.BM_DL, };
	
    private List<PieceMoveGenerator> generators;
	private BitBoard bitBoard;
	private List<BitBoardMove> queuedMoves = new LinkedList<>();
	private int genIndex = 0;
	private boolean inCheck;
	private long potentialPins = 0;

	public MoveGeneratorImpl(BitBoard bitBoard) {
		this.bitBoard = bitBoard;
        this.generators = MASTER;
		this.inCheck = CheckDetector.isPlayerToMoveInCheck(bitBoard);
		
		long enemyDiagAttackers = bitBoard.getBitmapOppColor() & (bitBoard.getBitmapBishops() | bitBoard.getBitmapQueens());
		long enemyLineAttackers = bitBoard.getBitmapOppColor() & (bitBoard.getBitmapRooks() | bitBoard.getBitmapQueens());
		int myKingIdx = Long.numberOfTrailingZeros(bitBoard.getBitmapColor() & bitBoard.getBitmapKings());

        for (int aDIR_LINE : DIR_LINE) {
            if ((Bitmaps.maps2[aDIR_LINE][myKingIdx] & enemyLineAttackers) != 0) {
                potentialPins |= Bitmaps.maps2[aDIR_LINE][myKingIdx];
            }
        }

        for (int aDIR_DIAG : DIR_DIAG) {
            if ((Bitmaps.maps2[aDIR_DIAG][myKingIdx] & enemyDiagAttackers) != 0) {
                potentialPins |= Bitmaps.maps2[aDIR_DIAG][myKingIdx];
            }
        }
	}

    public void setGenerators(PieceMoveGenerator... g) {
        generators = Arrays.asList(g);
    }

	public boolean hasNext() {
		if(queuedMoves.size() == 0) {
			populateMoves();
		}
		return (queuedMoves.size() > 0);
	}

	public BitBoardMove next() {
		if(queuedMoves.size() == 0) {
			populateMoves();
		}
		if(queuedMoves.size() == 0) {
			throw new NoSuchElementException();
		}

		return queuedMoves.remove(0);
	}

	public void remove() {
		throw new UnsupportedOperationException();
	}
	
	private void populateMoves() {
		if(genIndex >= generators.size()) {
			return;
		}

		if(bitBoard.isDrawnByRule()) {
			return;
		}

		while (queuedMoves.isEmpty() && genIndex < generators.size()) {
            PieceMoveGenerator nextGen = generators.get(genIndex++);
            nextGen.generateMoves(bitBoard, inCheck, potentialPins, queuedMoves);
        }
	}
	
	/**
	 * An easy way to generate all moves - this will be useful for testing and legal move generation, but not for
	 * calculation as it's too slow.
	 */
	public List<BitBoardMove> getAllRemainingMoves() {
		List<BitBoardMove> moves = new LinkedList<>();
		while(this.hasNext()) {
			moves.add(this.next());
		}
		return moves;
	}

    @Override
	public List<BitBoardMove> getThreateningMoves() {
		List<BitBoardMove> moves = new LinkedList<>();

		for(PieceMoveGenerator generator: generators) {
            // Rule: If the player is in check then all moves are needed, otherwise just
            // captures, checks and pawn promotions.
            if(CheckDetector.isPlayerToMoveInCheck(bitBoard)) {
                generator.generateMoves(bitBoard, inCheck, potentialPins, moves);
            } else {
			    generator.generateThreatMoves(bitBoard, inCheck, potentialPins, moves);
            }
		}
		
		return moves;
	}
}
