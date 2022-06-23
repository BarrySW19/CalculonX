package barrysw19.calculon.analyzer;

import org.junit.jupiter.api.Test;

public class AdvancedPawnScorerTest extends AbstractAnalyserTest {

    public AdvancedPawnScorerTest() {
        super(new AdvancedPawnScorer());
    }

    @Test
    public void testSimpleAdvancedWhitePawn() {
        assertScore(AdvancedPawnScorer.RANK_SCORE[3], "7k/5ppp/8/8/5P2/8/6PP/7K w - - 0 0");
    }

    @Test
    public void testSimpleAdvancedBlackPawn() {
        assertScore(-AdvancedPawnScorer.RANK_SCORE[4], "7k/6pp/8/8/5p2/8/5PPP/7K w - - 0 1");
    }
}
