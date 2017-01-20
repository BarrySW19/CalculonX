package barrysw19.calculon.analyzer;

import barrysw19.calculon.engine.BitBoard;
import barrysw19.calculon.model.Piece;
import barrysw19.calculon.util.BitIterable;

public class PassedPawnScorer implements PositionScorer {
    private static final int[] S_ADVANCE = { 0, 20, 25, 250, 500, 1000, 2000, };

    private static final long[] MASK_WHITE = new long[64];
    private static final long[] MASK_BLACK = new long[64];

    static {
        // Pre-calculate file masks
        final long[] fileMasks = new long[8];
        for(int i = 0; i < 8; i++) {
            long pawnsRight = i == 0 ? 0 : BitBoard.getFileMap(i - 1);
            long pawnHere = BitBoard.getFileMap(i);
            long pawnsLeft = i == 7 ? 0 : BitBoard.getFileMap(i + 1);
            fileMasks[i] = (pawnsRight|pawnsLeft|pawnHere) & ~(BitBoard.getRankMap(0)|BitBoard.getRankMap(7));
        }

        for(int w = 8; w < 48; w++) {
            int b = 63 - w;
            MASK_WHITE[w] = fileMasks[w & 0x07];
            MASK_BLACK[b] = fileMasks[b & 0x07];

            for(int i = 0; i <= (w>>>3); i++) {
                MASK_WHITE[w] &= ~BitBoard.getRankMap(i);
            }

            for(int i = 7; i >= (b>>>3); i--) {
                MASK_BLACK[b] &= ~BitBoard.getRankMap(i);
            }
        }
    }

    @Override
    public int scorePosition(BitBoard bitBoard, Context context) {
        return score(bitBoard, Piece.WHITE) - score(bitBoard, Piece.BLACK);
    }

    private static int score(BitBoard bitBoard, byte color) {
        boolean hasQueenAndRook = (bitBoard.getBitmapColor(color) & bitBoard.getBitmapQueens()) != 0
                && (bitBoard.getBitmapColor(color) & bitBoard.getBitmapRooks()) != 0;
        long passedPawns = getPassedPawns(bitBoard, color);
        int score = 0;

        for(long next: BitIterable.of(passedPawns)) {
            int[] pos = BitBoard.toCoords(next);
            pos[1] = color == Piece.WHITE ? pos[1] : 7 - pos[1];
            int pScore = S_ADVANCE[pos[1]];
            if(pos[0] == 0 || pos[0] == 7) {
                pScore = pScore * 3 / 4;
            }
            if( ! hasQueenAndRook) {
                pScore = pScore * 3 / 4;
            }
            score += pScore;
        }
        return score;
    }

    static long getPassedPawns(BitBoard bitBoard, byte color) {
        long passedPawns = 0;

        for(long next: BitIterable.of(bitBoard.getBitmapPawns(color))) {
            final int pawnIndex = Long.numberOfTrailingZeros(next);
            long mask = color == Piece.WHITE ? MASK_WHITE[pawnIndex] : MASK_BLACK[pawnIndex];
            if((mask & bitBoard.getBitmapOppColor(color) & bitBoard.getBitmapPawns()) == 0) {
                passedPawns |= next;
            }
        }

        return passedPawns;
    }
}
