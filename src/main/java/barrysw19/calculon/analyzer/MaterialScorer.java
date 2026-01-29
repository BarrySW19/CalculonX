package barrysw19.calculon.analyzer;

import barrysw19.calculon.engine.BitBoard;

public class MaterialScorer implements PositionScorer {
    public static final int VALUE_QUEEN     = 9000;
    public static final int VALUE_ROOK      = 5000;
    public static final int VALUE_BISHOP    = 3250;
    public static final int VALUE_KNIGHT    = 3100;
    public static final int VALUE_PAWN      = 1000;

    @Override
    public int scorePosition(BitBoard bitBoard, Context context) {
		int score = 0;

		long bitmapWhite = bitBoard.getBitmapWhite();
        long bitmapBlack = bitBoard.getBitmapBlack();

		score += VALUE_QUEEN * (Long.bitCount(bitmapWhite & bitBoard.getBitmapQueens())
				- Long.bitCount(bitmapBlack & bitBoard.getBitmapQueens()));
		score += VALUE_ROOK * (Long.bitCount(bitmapWhite & bitBoard.getBitmapRooks())
				- Long.bitCount(bitmapBlack & bitBoard.getBitmapRooks()));
		score += VALUE_BISHOP * (Long.bitCount(bitmapWhite & bitBoard.getBitmapBishops())
				- Long.bitCount(bitmapBlack & bitBoard.getBitmapBishops()));
		score += VALUE_KNIGHT * (Long.bitCount(bitmapWhite & bitBoard.getBitmapKnights())
				- Long.bitCount(bitmapBlack & bitBoard.getBitmapKnights()));
		score += VALUE_PAWN * (Long.bitCount(bitmapWhite & bitBoard.getBitmapPawns())
				- Long.bitCount(bitmapBlack & bitBoard.getBitmapPawns()));
		
		return score;
	}
}
