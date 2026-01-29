package barrysw19.calculon.analyzer;

import barrysw19.calculon.engine.BitBoard;
import barrysw19.calculon.model.Piece;

public class KingCentralisationScorer implements PositionScorer {
    private static final int[] SCORES = { 0, 2, 5, 10, 10, 5, 2, 0 };

    @Override
    public int scorePosition(BitBoard bitBoard, Context context) {
        return getScore(bitBoard, Piece.WHITE, context) - getScore(bitBoard, Piece.BLACK, context);
	}

	private int getScore(BitBoard bitBoard, byte color, Context context) {
        if( ! context.isEndgame()) {
            return 0;
        }

        long king = bitBoard.getBitmapColor(color) & bitBoard.getBitmapKings();
		int[] kingPos = BitBoard.toCoords(king);
		
		return (SCORES[kingPos[0]] + SCORES[kingPos[1]]);
	}
}
