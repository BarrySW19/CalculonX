package barrysw19.calculon.analyzer;

import barrysw19.calculon.engine.BitBoard;
import barrysw19.calculon.engine.Bitmaps;
import barrysw19.calculon.engine.KingMoveGenerator;
import barrysw19.calculon.model.Piece;
import barrysw19.calculon.util.BitIterable;

import static barrysw19.calculon.engine.BitBoard.getFileMap;
import static barrysw19.calculon.engine.BitBoard.getRankMap;

public class CheckmateScorer implements PositionScorer {
    private static long[] x = new long[] {
            getRankMap(3) | getRankMap(4),
            getRankMap(2) | getRankMap(5),
            getRankMap(1) | getRankMap(6),
            getRankMap(0) | getRankMap(7),
    };

    private static long[] y = new long[] {
            getFileMap(3) | getFileMap(4),
            getFileMap(2) | getFileMap(5),
            getFileMap(1) | getFileMap(6),
            getFileMap(0) | getFileMap(7),
    };

    @Override
    public int scorePosition(BitBoard bitBoard, Context context) {
        return scorePosition(bitBoard, Piece.WHITE) - scorePosition(bitBoard, Piece.BLACK);
    }

    private int scorePosition(BitBoard bitBoard, final byte color) {
        if(Long.bitCount(bitBoard.getBitmapOppColor(color)) > 1) {
            return 0;
        }

        final long oppColor = bitBoard.getBitmapOppColor(color);
        long oKingPos = bitBoard.getBitmapOppColor(color) & bitBoard.getBitmapKings();
        int score = 0;
        for(int i = 0; i < x.length; i++) {
            if((oKingPos & x[i]) != 0) {
                score += (i * 50);
            }
        }

        for(int i = 0; i < y.length; i++) {
            if((oKingPos & y[i]) != 0) {
                score += (i * 50);
            }
        }

        int dist = calcDist(oppColor & bitBoard.getBitmapKings(), ~oppColor & bitBoard.getBitmapKings());
        score += (14 - dist) * 10;

        long q = (~oppColor & bitBoard.getBitmapQueens());
        if(Long.bitCount(q) == 1) {
            dist = calcDist(oppColor & bitBoard.getBitmapKings(), q);
            score += (14 - dist) * 10;
        }

        long oppMoves = KingMoveGenerator.KING_MOVES[Long.numberOfTrailingZeros(bitBoard.getBitmapOppColor(color) & bitBoard.getBitmapKings())];
        for(long l: BitIterable.of(q)) {
            long atk = Bitmaps.star2Map[Long.numberOfTrailingZeros(l)];
            oppMoves = oppMoves & ~atk;
        }
        score -= 15 * Long.bitCount(oppMoves);

        return score;
    }

    private static int calcDist(long p1, long p2) {
        int[] oppPos = BitBoard.toCoords(p1);
        int[] myPos = BitBoard.toCoords(p2);
        return Math.abs(oppPos[0] - myPos[0]) + Math.abs(oppPos[1] - myPos[1]);
    }
}
