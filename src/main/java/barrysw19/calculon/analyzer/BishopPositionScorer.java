package barrysw19.calculon.analyzer;

import barrysw19.calculon.engine.BitBoard;
import barrysw19.calculon.model.Piece;
import barrysw19.calculon.util.BitIterable;

/**
 * Piece-square evaluation for bishops.
 * <p>
 * Bishops are generally strongest when they are centralized and have room to
 * operate on long diagonals, so this scorer rewards central development and
 * penalizes edge/corner placement.
 */
public class BishopPositionScorer implements PositionScorer {

    /**
     * White-oriented piece-square table indexed by square 0..63.
     * The values are intentionally modest; bishop pair and material should still
     * dominate the evaluation.
     */
    static final int[] BISHOP_PST = {
            -20, -10, -10, -10, -10, -10, -10, -20,
            -10,   0,   0,   0,   0,   0,   0, -10,
            -10,   0,   5,  10,  10,   5,   0, -10,
            -10,   5,   5,  10,  10,   5,   5, -10,
            -10,   0,  10,  10,  10,  10,   0, -10,
            -10,  10,  10,  10,  10,  10,  10, -10,
            -10,   5,   0,   0,   0,   0,   5, -10,
            -20, -10, -10, -10, -10, -10, -10, -20
    };

    @Override
    public int scorePosition(BitBoard bitBoard, Context context) {
        return score(bitBoard, Piece.WHITE) - score(bitBoard, Piece.BLACK);
    }

    private static int score(BitBoard bitBoard, byte color) {
        int score = 0;
        long bishops = bitBoard.getBitmapColor(color) & bitBoard.getBitmapBishops();
        for (long bishop : BitIterable.of(bishops)) {
            int index = Long.numberOfTrailingZeros(bishop);
            if (color == Piece.BLACK) {
                index ^= 56; // mirror vertically for black
            }
            score += BISHOP_PST[index];
        }
        return score;
    }
}
