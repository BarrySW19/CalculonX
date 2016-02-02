package barrysw19.calculon.analyzer;

import barrysw19.calculon.engine.BitBoard;
import barrysw19.calculon.model.Piece;

/**
 * Punish the computer for not castling.
 */
public class CastlingScorer implements PositionScorer {
    @Override
    public int scorePosition(BitBoard bitBoard, Context context) {
        return scorePosition(bitBoard, Piece.WHITE) - scorePosition(bitBoard, Piece.BLACK);
    }

    private int scorePosition(BitBoard bitBoard, final byte color) {
        final short options = bitBoard.getCastlingOptions();
        switch(color) {
            case Piece.WHITE:
                return (options & (BitBoard.CASTLE_WKS|BitBoard.CASTLE_WQS)) == 0 ? 0 : -250;
            case Piece.BLACK:
                return (options & (BitBoard.CASTLE_BKS|BitBoard.CASTLE_BQS)) == 0 ? 0 : -250;
        }
        return 0;
    }
}