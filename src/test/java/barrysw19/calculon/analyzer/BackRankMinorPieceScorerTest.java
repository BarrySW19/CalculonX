package barrysw19.calculon.analyzer;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class BackRankMinorPieceScorerTest extends AbstractAnalyserTest {

    public BackRankMinorPieceScorerTest() {
        super(new BackRankMinorPieceScorer());
    }

    @Test
    public void testScore1() {
        setPosition("1k6/8/8/7q/7Q/8/8/1B4NK b - - 0 1");
        assertEquals(-300, scorer.scorePosition(board, context));
    }

    @Test
    public void testScore2() {
        setPosition("1kn5/8/8/7q/7Q/8/8/1B4NK b - - 0 1");
        assertEquals(-150, scorer.scorePosition(board, context));
    }

    @Test
    public void testScore3() {
        setPosition("1k6/8/8/7q/7Q/8/B7/6NK b - - 0 1");
        assertEquals(-150, scorer.scorePosition(board, context));
    }
}
