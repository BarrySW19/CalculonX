package barrysw19.calculon.analyzer;

import barrysw19.calculon.engine.BitBoard;
import barrysw19.calculon.model.Piece;
import barrysw19.calculon.util.BitIterable;

/**
 * Piece-square evaluation for queens.
 * <p>
 * Queens are most effective when they can influence the centre while staying
 * out of the corners and edges, so this scorer rewards centralisation and
 * penalises passive placement.
 */
public class QueenPositionScorer implements PositionScorer {

    /**
     * White-oriented piece-square table indexed by square 0..63.
     * The values are intentionally modest; material should still dominate.
     */
    static final int[] QUEEN_PST = {
            -20, -10, -10,  -5,  -5, -10, -10, -20,
            -10,   0,   0,   0,   0,   0,   0, -10,
            -10,   0,   5,   5,   5,   5,   0, -10,
             -5,   0,   5,  10,  10,   5,   0,  -5,
             -5,   0,   5,  10,  10,   5,   0,  -5,
            -10,   0,   5,   5,   5,   5,   0, -10,
            -10,   0,   0,   0,   0,   0,   0, -10,
            -20, -10, -10,  -5,  -5, -10, -10, -20
    };

    @Override
    public int scorePosition(BitBoard bitBoard, Context context) {
        return score(bitBoard, Piece.WHITE) - score(bitBoard, Piece.BLACK);
    }

    private static int score(BitBoard bitBoard, byte color) {
        int score = 0;
        long queens = bitBoard.getBitmapColor(color) & bitBoard.getBitmapQueens();
        for (long queen : BitIterable.of(queens)) {
            int index = Long.numberOfTrailingZeros(queen);
            if (color == Piece.BLACK) {
                index ^= 56; // mirror vertically for black
            }
            score += QUEEN_PST[index];
        }
        return score;
    }
}
