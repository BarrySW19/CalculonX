package barrysw19.calculon.analyzer;

import barrysw19.calculon.model.Piece;
import barrysw19.calculon.engine.BitBoard;
import barrysw19.calculon.engine.PreGeneratedMoves;
import barrysw19.calculon.util.BitIterable;

public class MobilityScorer implements PositionScorer {

    @Override
    public int scorePosition(BitBoard bitBoard, Context context) {
        return getScore(bitBoard, Piece.WHITE) - getScore(bitBoard, Piece.BLACK);
    }

    private int getScore(BitBoard bitBoard, byte color) {
        int score = 0;
        final long allPieces = bitBoard.getBitmapAll();

        long straightPieces = bitBoard.getBitmapColor(color) & (bitBoard.getBitmapQueens() | bitBoard.getBitmapRooks());
        score += calculateMobility(allPieces, straightPieces, PreGeneratedMoves.STRAIGHT_MOVES) * 50;

        long diagonalPieces = bitBoard.getBitmapColor(color) & (bitBoard.getBitmapQueens() | bitBoard.getBitmapBishops());
        score += calculateMobility(allPieces, diagonalPieces, PreGeneratedMoves.DIAGONAL_MOVES) * 50;

        return score;
    }

    private int calculateMobility(long allPieces, long piecesToCheck, long[][][] moves) {
        int count = 0;
        for(long nextPiece: BitIterable.of(piecesToCheck)) {
            int index = Long.numberOfTrailingZeros(nextPiece);
            int pieceCount = 0;
            for(long[] nextDirection: moves[index]) {
                for(long nextSquare: nextDirection) {
                    if((allPieces & nextSquare) != 0) {
                        break;
                    }
                    pieceCount++;
                }
            }
            count += Math.min(pieceCount, 15);
        }
        return count;
    }
}
