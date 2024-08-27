package barrysw19.calculon.analyzer;

import barrysw19.calculon.engine.BitBoard;
import barrysw19.calculon.engine.Bitmaps;
import barrysw19.calculon.model.Piece;
import barrysw19.calculon.util.BitIterable;

import static barrysw19.calculon.engine.BitBoard.getFileMap;
import static barrysw19.calculon.engine.BitBoard.getRankMap;

public class CheckmateScorer implements PositionScorer {
    private static final long[] SCORE_RANK = new long[] {
            getRankMap(3) | getRankMap(4),
            getRankMap(2) | getRankMap(5),
            getRankMap(1) | getRankMap(6),
            getRankMap(0) | getRankMap(7),
    };

    private static final long[] SCORE_FILE = new long[] {
            getFileMap(3) | getFileMap(4),
            getFileMap(2) | getFileMap(5),
            getFileMap(1) | getFileMap(6),
            getFileMap(0) | getFileMap(7),
    };

    @Override
    public int scorePosition(BitBoard bitBoard, Context context) {
        return scorePosition(bitBoard, Piece.WHITE) - scorePosition(bitBoard, Piece.BLACK);
    }

    private int scorePosition(BitBoard bitBoard, final byte color) {
        final long enemyPieces = bitBoard.getBitmapOppColor(color);
        if(Long.bitCount(enemyPieces) > 1) {
            // Only run against lone king
            return 0;
        }

        final long myPieces = bitBoard.getBitmapColor(color);
        if(Long.bitCount(myPieces) > 3) {
            return 0;
        }

        final long enemyKingPos = bitBoard.getBitmapOppColor(color) & bitBoard.getBitmapKings();
        int score = 0;

        // The enemy king is better near the side of the board
        for(int i = 0; i < SCORE_RANK.length; i++) {
            if((enemyKingPos & SCORE_RANK[i]) != 0) {
                score += (i * 50);
            }
            if((enemyKingPos & SCORE_FILE[i]) != 0) {
                score += (i * 50);
            }
        }

        // My king is better near the opponent's
        int dist = calcDist(enemyKingPos, myPieces & bitBoard.getBitmapKings());
        score += (28 - dist) * 10;

        long oppMoves = Bitmaps.KING_MOVES[Long.numberOfTrailingZeros(enemyKingPos)];

        final long myQueens = (myPieces & bitBoard.getBitmapQueens());
        for(long queen: BitIterable.of(myQueens)) {
            dist = calcDist(enemyKingPos, queen);
            score += (28 - dist) * 5;
            long attackedSquares = Bitmaps.star2Map[Long.numberOfTrailingZeros(queen)];
            oppMoves = oppMoves & ~attackedSquares;
        }

        final long myRooks = (myPieces & bitBoard.getBitmapRooks());
        for(long rook: BitIterable.of(myRooks)) {
            dist = calcDist(enemyKingPos, rook);
            score += (28 - dist) * 5;
            long attackedSquares = Bitmaps.cross2Map[Long.numberOfTrailingZeros(rook)];
            oppMoves = oppMoves & ~attackedSquares;
        }
        // The fewer moves the opponent's king has, the better.
        score -= 15 * Long.bitCount(oppMoves);

        return score;
    }

    private static int calcDist(long p1, long p2) {
        int[] oppPos = BitBoard.toCoords(p1);
        int[] myPos = BitBoard.toCoords(p2);
//        return Math.abs(oppPos[0] - myPos[0]) + Math.abs(oppPos[1] - myPos[1]);
        int d1 = Math.abs(oppPos[0] - myPos[0]);
        int d2 = Math.abs(oppPos[1] - myPos[1]);
        return Math.max(d1, d2) * 3 + Math.min(d1, d2);
    }
}
