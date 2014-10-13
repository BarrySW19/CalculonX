package nl.zoidberg.calculon.analyzer;

import nl.zoidberg.calculon.engine.BitBoard;
import nl.zoidberg.calculon.model.Piece;

/**
 * Penalise minor pieces left on the back rank.
 */
public class BackRankMinorPieceScorer implements PositionScorer {

    @Override
    public int scorePosition(BitBoard bitBoard, Context context) {
        return getScore(bitBoard, Piece.WHITE) - getScore(bitBoard, Piece.BLACK);
   	}

    private int getScore(BitBoard bitBoard, byte color) {
        long rank = BitBoard.getRankMap(color == Piece.WHITE ? 0 : 7);
        long colorMap = bitBoard.getBitmapColor(color);
        long pieces = (colorMap & (bitBoard.getBitmapBishops() | bitBoard.getBitmapKnights()) & rank);
        return -150 * Long.bitCount(pieces);
    }
}
