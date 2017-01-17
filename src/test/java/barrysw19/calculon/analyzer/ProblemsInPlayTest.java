package barrysw19.calculon.analyzer;

import barrysw19.calculon.engine.BitBoard;
import barrysw19.calculon.engine.ChessEngine;
import barrysw19.calculon.notation.FENUtils;
import barrysw19.calculon.notation.PGNUtils;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

public class ProblemsInPlayTest {

    /**
     * This test shows up a problem where the engine correctly identifies the mate-in-1 but, because
     * a number of moves are never correctly scored they end up with MIN_VALUE which appears to
     * be better.
     *
     * The original problem was due to the engine not calculating the score correctly when the quiescence
     * depth hit bottom on a forced move. The solution was to extend the search depth by one in this
     * situation.
     */
    @Test
    public void testMissedMateInOne() {
        runTest("3k4/R7/p1BB1N2/P7/7P/8/KP6/8 w - - 0 1", "Ra8#");
    }

    @Test @Ignore("fix by improving engine")
    public void testBadMove1() {
        runTest("r2qkb1r/1Q3ppp/p4n2/2p1p3/3nP3/N7/PPP2P1P/R1B1KB1R b kq - 0 15", "Rb8"); // Qa5+ is bad
    }

    @Test
    public void testBadMove2() {
        runTest("3rkb1r/1Q3ppp/p4n2/q1p1p3/4PP2/N1P2n2/PP3K1P/R1B2B1R b k - 0 18", "Nd2"); // Rd7 is bad
    }

    private void runTest(String fen, String pgn) {
        ChessEngine engine = new ChessEngine(2);
        engine.setQDepth(7);
        BitBoard board = FENUtils.getBoard(fen);
        String move = engine.getPreferredMove(board);
        Assert.assertEquals(pgn, PGNUtils.translateMove(board, move));
    }
}
