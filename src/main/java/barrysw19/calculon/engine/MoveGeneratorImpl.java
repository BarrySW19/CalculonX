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

public class MoveGeneratorImpl implements MoveGenerator {
    private static final List<PieceMoveGenerator> MASTER;

    private static final long EDGES = BitBoard.getFileMap(0) | BitBoard.getFileMap(7)
            | BitBoard.getRankMap(0) | BitBoard.getRankMap(7);
	
	static {
        final List<PieceMoveGenerator> list = new LinkedList<>();

		list.add(new PawnCaptureGenerator());
        list.add(new KnightMoveGenerator());
        list.add(new StraightMoveGenerator(Piece.BISHOP));
        list.add(new StraightMoveGenerator(Piece.ROOK));
        list.add(new StraightMoveGenerator(Piece.QUEEN));
        list.add(new PawnMoveGenerator());
        list.add(new KingMoveGenerator());

        MASTER = Collections.unmodifiableList(list);
	}
	
    private List<PieceMoveGenerator> generators;
    private final boolean drawnByRule;

    private final MoveGeneratorContext context;

    private Iterator<BitBoardMove> moveIterator;

	public MoveGeneratorImpl(BitBoard bitBoard) {
        this.generators = MASTER;
        context = new MoveGeneratorContext(bitBoard);
        drawnByRule = bitBoard.isDrawnByRule();
	}

    public static SquareCounts calculateSquareCounting(BitBoard bitBoard, byte color) {
        SquareCounts squareCounts = new SquareCounts();
	    calculateSquareCounting(bitBoard, color, squareCounts, 1);
	    calculateSquareCounting(bitBoard, color == Piece.WHITE ? Piece.BLACK: Piece.WHITE, squareCounts, -1);
	    return squareCounts;
    }

    private static void calculateSquareCounting(BitBoard bitBoard, byte color, SquareCounts squareCounts, int sign) {
        long[] pawnAttacks = color == Piece.WHITE ? PawnCaptureGenerator.WHITE_ATTACK : PawnCaptureGenerator.BLACK_ATTACK;
	    for(long p: BitIterable.of(bitBoard.getBitmapPawns(color))) {
	        squareCounts.addCounts(pawnAttacks[Long.numberOfTrailingZeros(p)], sign);
        }

        for(long s: BitIterable.of(bitBoard.getBitmapKnights(color))) {
            squareCounts.addCounts(KnightMoveGenerator.KNIGHT_MOVES[Long.numberOfTrailingZeros(s)], sign);
        }

        squareCounts.addCounts(KingMoveGenerator.KING_MOVES[Long.numberOfTrailingZeros(bitBoard.getBitmapKings(color))], sign);

        for(long s: BitIterable.of(bitBoard.getBitmapBishops(color)|bitBoard.getBitmapQueens(color))) {
            int pos = Long.numberOfTrailingZeros(s);
            for(long enemy: BitIterable.of(Bitmaps.diag2Map[pos] & (bitBoard.getAllPieces() | EDGES))) {
                long between = Bitmaps.SLIDE_MOVES[pos][Long.numberOfTrailingZeros(enemy)];
                if(Long.bitCount(between & bitBoard.getAllPieces()) == 0) {
                    squareCounts.addCounts(between|enemy, sign);
                }
            }
        }

        for(long s: BitIterable.of(bitBoard.getBitmapRooks(color)|bitBoard.getBitmapQueens(color))) {
            int pos = Long.numberOfTrailingZeros(s);
            for(long enemy: BitIterable.of(Bitmaps.cross2Map[pos] & (bitBoard.getAllPieces() | EDGES))) {
                long between = Bitmaps.SLIDE_MOVES[pos][Long.numberOfTrailingZeros(enemy)];
                if(Long.bitCount(between & bitBoard.getAllPieces()) == 0) {
                    squareCounts.addCounts(between|enemy, sign);
                }
            }
        }
    }

    public static class SquareCounts {
	    private int[] counts = new int[64];

	    public void addCounts(long bitmap, int sign) {
	        for(long l: BitIterable.of(bitmap)) {
	            int key = Long.numberOfTrailingZeros(l);
	            counts[key] += sign;
            }
        }

        public long getPositiveSquares() {
	        long bitmap = 0;
	        for(int i = 0; i < 64; i++) {
	            if(counts[i] > 0) {
	                bitmap |= 1L<<i;
                }
            }
            return bitmap;
        }

        public long getNegativeSquares() {
            long bitmap = 0;
            for(int i = 0; i < 64; i++) {
                if(counts[i] < 0) {
                    bitmap |= 1L<<i;
                }
            }
            return bitmap;
        }
    }

	public static long calculateAllAttackedSquares(BitBoard bitBoard, byte color) {
	    long myPawns = bitBoard.getBitmapPawns(color);
        long attacked = color == Piece.WHITE
                ? (myPawns & ~BitBoard.getFileMap(0)) << 7 | (myPawns & ~BitBoard.getFileMap(7)) << 9
                : (myPawns & ~BitBoard.getFileMap(0)) >>> 9 | (myPawns & ~BitBoard.getFileMap(7)) >> 7;
        for(long s: BitIterable.of(bitBoard.getBitmapKnights(color))) {
            attacked |= KnightMoveGenerator.KNIGHT_MOVES[Long.numberOfTrailingZeros(s)];
        }

        attacked |= KingMoveGenerator.KING_MOVES[Long.numberOfTrailingZeros(bitBoard.getBitmapKings(color))];

        for(long s: BitIterable.of(bitBoard.getBitmapBishops(color)|bitBoard.getBitmapQueens(color))) {
            int pos = Long.numberOfTrailingZeros(s);
            for(long enemy: BitIterable.of(Bitmaps.diag2Map[pos] & (bitBoard.getAllPieces() | EDGES))) {
                long between = Bitmaps.SLIDE_MOVES[pos][Long.numberOfTrailingZeros(enemy)];
                if(Long.bitCount(between & bitBoard.getAllPieces()) == 0) {
                    attacked |= between|enemy;
                }
            }
        }

        for(long s: BitIterable.of(bitBoard.getBitmapRooks(color)|bitBoard.getBitmapQueens(color))) {
            int pos = Long.numberOfTrailingZeros(s);
            for(long enemy: BitIterable.of(Bitmaps.cross2Map[pos] & (bitBoard.getAllPieces() | EDGES))) {
                long between = Bitmaps.SLIDE_MOVES[pos][Long.numberOfTrailingZeros(enemy)];
                if(Long.bitCount(between & bitBoard.getAllPieces()) == 0) {
                    attacked |= between|enemy;
                }
            }
        }

        return attacked;
    }

    /**
     * <p>Potential pins is a quick way to determine which moves need to be checked for discovered checks.
     * The resulting long is all the directions from the king which contain a sliding attacker (of
     * a type which can attack in that direction). This means that any move from one of these squares could
     * possibly result in a discovered check. For such moves the CheckDetector needs to be called.</p>
     *
     * @param color the side to calculate for, if this is the side to play then calculate pins, otherwise discoveries.
     */
	private static long calculatePotentialPins(BitBoard bitBoard, byte color) {
        final int kingIdx = Long.numberOfTrailingZeros(bitBoard.getBitmapKings(color));
        final long enemyDiagAttackers = Bitmaps.diag2Map[kingIdx] & bitBoard.getBitmapOppColor(color) & (bitBoard.getBitmapBishops() | bitBoard.getBitmapQueens());
        final long enemyLineAttackers = Bitmaps.cross2Map[kingIdx] & bitBoard.getBitmapOppColor(color) & (bitBoard.getBitmapRooks() | bitBoard.getBitmapQueens());

        long potentialPins = 0;
        for(long l: BitIterable.of(enemyLineAttackers|enemyDiagAttackers)) {
            final long pinningSquares = Bitmaps.SLIDE_MOVES[kingIdx][Long.numberOfTrailingZeros(l)];
            final long ownPieces = pinningSquares & bitBoard.getBitmapColor(); // Pieces of the side to move
            final int ownPieceCount = Long.bitCount(ownPieces);
            if (ownPieceCount == 1 && (pinningSquares & bitBoard.getBitmapOppColor()) == 0) {
                potentialPins |= ownPieces;
            }

            // Note: There is a situation where two pieces can be removed from a rank in a single move - en passant.
            // This means a pawn could potentially be pinned on a rank even though another pawn is next to it; it
            // could legally advance or capture normally but not via en passant.
            if (bitBoard.isEnPassant()) {
                int enPassantRank = bitBoard.getPlayer() == Piece.WHITE ? 4 : 3;
                long enPassantPawn = (1L << (enPassantRank << 3)) << bitBoard.getEnPassantFile();
                long neighbours = (enPassantPawn << 1 | enPassantPawn >>> 1) & BitBoard.getRankMap(enPassantRank);
                if (ownPieceCount == 1 && (pinningSquares & (bitBoard.getBitmapOppColor() & ~enPassantPawn)) == 0
                        && (ownPieces & neighbours) != 0) {
                    potentialPins |= ownPieces;
                }
            }
        }

        return potentialPins;
    }

    public void setGenerators(PieceMoveGenerator... g) {
        generators = Arrays.asList(g);
    }

	public boolean hasNext() {
        return !drawnByRule && getMoveIterator().hasNext();
    }

    private Iterator<BitBoardMove> getMoveIterator() {
        if(moveIterator == null) {
            List<Iterator<BitBoardMove>> iterators =
                    MASTER.stream().map(g -> g.iterator(context)).collect(toList());

            //noinspection unchecked
            moveIterator = new CompoundIterator<>(iterators.toArray(new Iterator[iterators.size()]));
        }
        return moveIterator;
    }

	public BitBoardMove next() {
        if(!hasNext()) {
            throw new NoSuchElementException();
        }

        return getMoveIterator().next();
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
	public Iterator<BitBoardMove> getThreatMovesIterator() {
	    if(CheckDetector.isPlayerToMoveInCheck(context.bitBoard)) {
            return new CompoundIterator<>(generators.stream().map(m -> m.iterator(context)).collect(toList()));
        } else {
            return new CompoundIterator<>(generators.stream().map(m -> m.generateThreatMoves(context)).collect(toList()));
        }
    }

    /**
     * Holder for any information which might be useful to multiple generators but can be calculated only once.
     */
	public static class MoveGeneratorContext {
        private final BitBoard bitBoard;
        private final boolean alreadyInCheck;
        private final long potentialPins;
        private final long potentialDiscoveries;

        public MoveGeneratorContext(final BitBoard bitBoard) {
            this.bitBoard = bitBoard;
            this.alreadyInCheck = CheckDetector.isPlayerToMoveInCheck(bitBoard);
            this.potentialPins = calculatePotentialPins(bitBoard, bitBoard.getPlayer());
            this.potentialDiscoveries = calculatePotentialPins(bitBoard, bitBoard.getOppPlayer());
        }

        public BitBoard getBitBoard() {
            return bitBoard;
        }

        boolean isAlreadyInCheck() {
            return alreadyInCheck;
        }

        long getPotentialPins() {
            return potentialPins;
        }

        long getPotentialDiscoveries() {
            return potentialDiscoveries;
        }
    }
}
