package nl.zoidberg.calculon.analyzer;

import nl.zoidberg.calculon.engine.BitBoard;
import nl.zoidberg.calculon.model.Piece;
import nl.zoidberg.calculon.util.BitIterable;

public class PassedPawnScorer implements PositionScorer {

    private static long[] FILE_MASKS = new long[8];
    private static int[] S_ADVANCE = { 0, 20, 25, 250, 500, 1000, 2000, };

    static {
        // Pre-calculate file masks
        for(int i = 0; i < 8; i++) {
            long pawnsRight = i == 0 ? 0 : BitBoard.getFileMap(i - 1);
            long pawnHere = BitBoard.getFileMap(i);
            long pawnsLeft = i == 7 ? 0 : BitBoard.getFileMap(i + 1);
            FILE_MASKS[i] = (pawnsRight|pawnsLeft|pawnHere) & ~(BitBoard.getRankMap(0)|BitBoard.getRankMap(7));
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

    public static long getPassedPawns(BitBoard bitBoard, byte color) {
        long passedPawns = 0;
        long pawns = bitBoard.getBitmapPawns(color);

        for(long next: BitIterable.of(pawns)) {
            long oppMask = FILE_MASKS[Long.numberOfTrailingZeros(next) % 8];
            int pawnRank = Long.numberOfTrailingZeros(next) >> 3;
            if(color == Piece.WHITE) {
                for(int i = 1; i <= pawnRank; i++) {
                    oppMask &= ~BitBoard.getRankMap(i);
                }
            } else {
                for(int i = 6; i >= pawnRank; i--) {
                    oppMask &= ~BitBoard.getRankMap(i);
                }
            }
            oppMask &= (bitBoard.getBitmapOppColor(color) & bitBoard.getBitmapPawns());
            if(oppMask == 0) {
                passedPawns |= next;
            }
        }
        return passedPawns;
    }
}
