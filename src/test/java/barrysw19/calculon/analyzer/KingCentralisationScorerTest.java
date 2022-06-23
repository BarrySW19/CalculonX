package barrysw19.calculon.analyzer;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class KingCentralisationScorerTest extends AbstractAnalyserTest {

    public KingCentralisationScorerTest() {
        super(new KingCentralisationScorer());
    }

    @Test
    public void testDiffInEndgame() {
        setPosition("k7/8/8/8/3K4/8/8/8 w - - 0 1");
        PositionScorer.Context ctx = mock(PositionScorer.Context.class);
        when(ctx.isEndgame()).thenReturn(true);
        when(ctx.getIsolatedPawns()).thenReturn(context.getIsolatedPawns());
        assertEquals(20, scorer.scorePosition(board, ctx));
    }

    @Test
    public void testDiffNoEndgame() {
        setPosition("k7/8/8/8/3K4/8/8/8 w - - 0 1");
        PositionScorer.Context ctx = mock(PositionScorer.Context.class);
        when(ctx.isEndgame()).thenReturn(false);
        when(ctx.getIsolatedPawns()).thenReturn(context.getIsolatedPawns());
        assertEquals(0, scorer.scorePosition(board, ctx));
    }

    @Test
    public void testDiffNearKings() {
        setPosition("8/8/2k5/8/3K4/8/8/8 w - - 0 1");
        PositionScorer.Context ctx = mock(PositionScorer.Context.class);
        when(ctx.isEndgame()).thenReturn(true);
        when(ctx.getIsolatedPawns()).thenReturn(context.getIsolatedPawns());
        assertEquals(10, scorer.scorePosition(board, ctx));
    }
}
