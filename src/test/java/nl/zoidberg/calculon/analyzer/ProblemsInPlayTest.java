package nl.zoidberg.calculon.analyzer;

import nl.zoidberg.calculon.engine.BitBoard;
import nl.zoidberg.calculon.engine.ChessEngine;
import nl.zoidberg.calculon.notation.FENUtils;
import nl.zoidberg.calculon.notation.PGNUtils;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

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

    private void runTest(String fen, String pgn) {
        // As all tests should have only one move it should never use 60 secs
        ChessEngine engine = new ChessEngine(60);
        BitBoard board = FENUtils.getBoard(fen);
        String move = engine.getPreferredMove(board);
        assertEquals(pgn, PGNUtils.translateMove(board, move));
    }
}
