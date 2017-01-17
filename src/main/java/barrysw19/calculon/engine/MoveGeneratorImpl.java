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

import barrysw19.calculon.model.Piece;
import barrysw19.calculon.engine.BitBoard.BitBoardMove;

import java.util.*;

import static java.util.stream.Collectors.toList;

public class MoveGeneratorImpl implements MoveGenerator {
    private static final int[] DIR_LINE = new int[] { Bitmaps.BM_U, Bitmaps.BM_D, Bitmaps.BM_L, Bitmaps.BM_R, };
    private static final int[] DIR_DIAG = new int[] { Bitmaps.BM_UR, Bitmaps.BM_DR, Bitmaps.BM_UL, Bitmaps.BM_DL, };
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
	
    private List<PieceMoveGenerator> generators;
	private final BitBoard bitBoard;
	private final boolean inCheck;
    private final boolean drawnByRule;
    private long potentialPins = 0;

    private final Iterator<BitBoardMove> moveIterator;

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

        List<Iterator<BitBoardMove>> iterators =
                MASTER.stream().map(g -> g.iterator(this.bitBoard, this.inCheck, this.potentialPins)).collect(toList());
        //noinspection unchecked
        moveIterator = new CompoundIterator<>(iterators.toArray(new Iterator[iterators.size()]));
        drawnByRule = bitBoard.isDrawnByRule();
	}

    public void setGenerators(PieceMoveGenerator... g) {
        generators = Arrays.asList(g);
    }

	public boolean hasNext() {
        return !drawnByRule && moveIterator.hasNext();
    }

	public BitBoardMove next() {
        if(!hasNext()) {
            throw new NoSuchElementException();
        }

        return moveIterator.next();
	}

	public void remove() {
		throw new UnsupportedOperationException();
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
                for(Iterator<BitBoardMove> iter = generator.iterator(bitBoard, inCheck, potentialPins); iter.hasNext(); ) {
                    moves.add(iter.next());
                }
            } else {
			    generator.generateThreatMoves(bitBoard, inCheck, potentialPins, moves);
            }
		}
		
		return moves;
	}
}
