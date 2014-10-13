package nl.zoidberg.calculon.analyzer;

import nl.zoidberg.calculon.engine.BitBoard;
import nl.zoidberg.calculon.engine.ChessEngine;
import nl.zoidberg.calculon.notation.FENUtils;
import nl.zoidberg.calculon.notation.PGNUtils;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

// Some chess problems which the engine should easily solve.
public class ProblemsTest {

    @Test
    public void testProblem001() {
        runTest("5r2/ppR2Nbk/1n3qp1/1b5p/8/1B6/P2Q1PPP/3R2K1 w - - 0 1", "Qh6+");
    }

    @Test
    public void testProblem002() {
        runTest("2r3k1/1b3ppp/p3p3/Bp1n4/4q3/PQ2P1P1/1P2BP1P/5RK1 b - - 0 1", "Qg2+");
    }

    @Test
    public void testProblem003() {
        runTest("6k1/pb3pp1/8/7p/P1Q5/3r1P1B/4q2P/1R3N1K b - - 0 1", "Rxf3");
    }

    @Test
    public void testProblem004() {
        runTest("r2qr1k1/ppp1bppp/5n2/6B1/2PR4/P3P3/1PQ2PPP/4KB1R b K - 0 1", "Qxd4");
    }

    @Test
    public void testProblem005() {
        runTest("7k/1bpp2p1/3bp3/p4B2/2P5/P3BrPq/1PQ2P1P/4RRK1 b - - 0 1", "Qg2+");
    }

    @Test
    public void testProblem006() {
        runTest("r1b1k2r/ppp1qppp/5B2/3Pn3/8/8/PPP2PPP/RN1QKB1R b KQkq - 0 1", "Nf3#");
    }

    private void runTest(String fen, String pgn) {
        // As all tests should have only one move it should never use 60 secs
        ChessEngine engine = new ChessEngine(60);
        BitBoard board = FENUtils.getBoard(fen);
        String move = engine.getPreferredMove(board);
        assertEquals(pgn, PGNUtils.translateMove(board, move));
    }
}
