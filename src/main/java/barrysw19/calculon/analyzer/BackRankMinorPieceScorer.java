package barrysw19.calculon.analyzer;

import barrysw19.calculon.engine.BitBoard;
import barrysw19.calculon.model.Piece;

/**
 * Penalise minor pieces left on the back rank.
 */
public class BackRankMinorPieceScorer implements PositionScorer {

    @Override
    public int scorePosition(BitBoard bitBoard, Context context) {
        if(context.isEndgame()) {
            // Only really relevant for opening/middlegame.
            return 0;
        }

        return getScore(bitBoard, Piece.WHITE) - getScore(bitBoard, Piece.BLACK);
   	}

    private int getScore(BitBoard bitBoard, byte color) {
        long rank = BitBoard.getRankMap(color == Piece.WHITE ? 0 : 7);
        long colorMap = bitBoard.getBitmapColor(color);
        long pieces = (colorMap & (bitBoard.getBitmapBishops() | bitBoard.getBitmapKnights()) & rank);
        return -150 * Long.bitCount(pieces);
    }
}
