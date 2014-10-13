package nl.zoidberg.calculon.analyzer;

import nl.zoidberg.calculon.engine.BitBoard;
import nl.zoidberg.calculon.notation.FENUtils;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class BackRankMinorPieceScorerTest {

    @Test
    public void testScore1() {
        BitBoard board = FENUtils.getBoard("1k6/8/8/8/8/8/8/1B4NK b - - 0 1");
        BackRankMinorPieceScorer scorer = new BackRankMinorPieceScorer();
        assertEquals(-300, scorer.scorePosition(board, new PositionScorer.Context()));
    }

    @Test
    public void testScore2() {
        BitBoard board = FENUtils.getBoard("1kn5/8/8/8/8/8/8/1B4NK b - - 0 1");
        BackRankMinorPieceScorer scorer = new BackRankMinorPieceScorer();
        assertEquals(-150, scorer.scorePosition(board, new PositionScorer.Context()));
    }

    @Test
    public void testScore3() {
        BitBoard board = FENUtils.getBoard("1k6/8/8/8/8/8/B7/6NK b - - 0 1");
        BackRankMinorPieceScorer scorer = new BackRankMinorPieceScorer();
        assertEquals(-150, scorer.scorePosition(board, new PositionScorer.Context()));
    }
}
