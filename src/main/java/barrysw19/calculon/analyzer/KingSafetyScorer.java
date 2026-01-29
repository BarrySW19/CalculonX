package barrysw19.calculon.analyzer;

import barrysw19.calculon.engine.BitBoard;
import barrysw19.calculon.engine.Bitmaps;
import barrysw19.calculon.model.Piece;

public class KingSafetyScorer implements PositionScorer {

    @Override
    public int scorePosition(BitBoard bitBoard, Context context) {
        return getScore(bitBoard, Piece.WHITE, context) - getScore(bitBoard, Piece.BLACK, context);
	}

	private int getScore(BitBoard bitBoard, byte color, Context context) {
        if(context.isEndgame()) {
            return 0;
        }

		int score = 0;
		long king = bitBoard.getBitmapKings(color);
		int mapIdx = Long.numberOfTrailingZeros(king);
        int kingRank = mapIdx>>>3;
        if((color == Piece.WHITE && kingRank !=0) || (color == Piece.BLACK && kingRank != 7)) {
            return 0; // Only score safety if the king is on the back rank.
        }

		long inFront = Bitmaps.KING_MOVES[mapIdx]
		           & BitBoard.getRankMap(kingRank + (color == Piece.WHITE ? 1 : -1)) & bitBoard.getBitmapColor(color);
		score += 70 * (Long.bitCount(inFront & bitBoard.getBitmapPawns()));
		score += 40 * (Long.bitCount(inFront & ~bitBoard.getBitmapPawns()));

		int[] kingPos = BitBoard.toCoords(king);
		
		if(kingPos[0] == 3 || kingPos[0] == 4) {
			score -= 250;
		}
		
		return score;
	}

}
