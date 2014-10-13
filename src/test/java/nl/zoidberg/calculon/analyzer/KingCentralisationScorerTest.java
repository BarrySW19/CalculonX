package nl.zoidberg.calculon.analyzer;

import nl.zoidberg.calculon.notation.FENUtils;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class KingCentralisationScorerTest {
    private PositionScorer scorer = new KingCentralisationScorer();

    @Test
    public void testDiffInEndgame() {
        PositionScorer.Context ctx = new PositionScorer.Context();
        ctx.setEndgame(true);
        assertEquals(20, scorer.scorePosition(
                FENUtils.getBoard("k7/8/8/8/3K4/8/8/8 w - - 0 1"), ctx));
    }

    @Test
    public void testDiffNoEndgame() {
        PositionScorer.Context ctx = new PositionScorer.Context();
        ctx.setEndgame(false);
        assertEquals(0, scorer.scorePosition(
                FENUtils.getBoard("k7/8/8/8/3K4/8/8/8 w - - 0 1"), ctx));
    }

    @Test
    public void testDiffNearKings() {
        PositionScorer.Context ctx = new PositionScorer.Context();
        ctx.setEndgame(true);
        assertEquals(10, scorer.scorePosition(
                FENUtils.getBoard("8/8/2k5/8/3K4/8/8/8 w - - 0 1"), ctx));
    }
}
