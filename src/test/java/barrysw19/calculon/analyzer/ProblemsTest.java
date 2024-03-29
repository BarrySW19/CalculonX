package barrysw19.calculon.analyzer;

import barrysw19.calculon.engine.BitBoard;
import barrysw19.calculon.engine.ChessEngine;
import barrysw19.calculon.notation.FENUtils;
import barrysw19.calculon.notation.PGNUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

// Some chess problems which the engine should easily solve.
//@Ignore // too slow
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
        // Queen on e2, rook on d3 - used to randomly fail - seems ok now
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

    @Test
    public void testChessTempo001() {
        runTest("8/1r2kp2/8/5B2/K1b3P1/5R2/3R4/r7 w - - 4 1", "Ra3", "Bb3+");
    }

    @Test
    public void testChessTempo002() {
        runTest("2r3k1/5pp1/p1n3q1/1pb1rN2/5BP1/2P2Q2/1P1RRK2/8 w - - 3 1", "Kf1", "Rxf5");
    }

    @Test //@Ignore("slow")
    public void testChessTempo003() {
        // The next position after Rxd5 Rxd5
        runTest("2r3k1/p3qppp/2p5/Q2r4/1P2p3/4P3/P3BPPP/2R3K1 w - - 0 3", "Qxd5");

        // Quiescent search should spot 1... Rd6 2. Rxd5 Rxd5 3. Qxd5 cxd5 4. Rxc8+ Qf8 5. Rxf8+ Kxf8 *
        runTest("2rr2k1/p3qppp/2p5/Q2b4/1P2p3/4P3/P3BPPP/2RR2K1 b - b3 0 1", "Rd6", "Rxd5");
    }

    @Test
    public void testChessTempo004() {
        // Works, but should be virtually instant instead of ~5 secs
        runTest("7r/4k1p1/p3npP1/1p2P2P/8/2P1R3/PP4N1/2K5 b - - 0 1", "Rxh5", "exf6+"); // Best by +4.5
    }

    @Test
    public void testChessTempo005() {
        runTest("3q1rk1/3n1pp1/1p1p3p/1B1P2n1/1P6/4BP2/5QPP/2R3K1 w - - 5 1", "Bd4", "Nh3+");
    }

    @Test
    public void testChessTempo006() {
        runTest("rn3rk1/bpp1qppp/3pbn2/1p2p3/P1N1P3/2PP1N2/2B2PPP/R1BQ1RK1 w - - 0 1", "axb5", "Bxf2+");
    }

    @Test
    public void testChessTempo007() {
        runTest("6k1/pR3p2/7p/2PpN1p1/3rnn2/P7/5PPP/5RK1 w - - 0 1", "c6", "Ne2+");
    }

    private void runTest(String fen, String firstMove, String expectedMove) {
        BitBoard board = FENUtils.getBoard(fen);
        PGNUtils.applyMove(board, firstMove);
        runTest(FENUtils.generate(board), expectedMove);
    }

    private void runTest(String fen, String pgn) {
        // As all tests should have only one move it should never use 60 secs
        ChessEngine engine = new ChessEngine(5);
        BitBoard board = FENUtils.getBoard(fen);
        String move = engine.getPreferredMove(board);
        assertEquals(pgn, PGNUtils.translateMove(board, move));
    }
}
