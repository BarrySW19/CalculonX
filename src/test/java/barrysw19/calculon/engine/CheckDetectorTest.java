package barrysw19.calculon.engine;

import barrysw19.calculon.notation.FENUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CheckDetectorTest {

    @Test
    public void testCheckDetector1() {
        BitBoard bitBoard = FENUtils.getBoard("r1bqkbnr/pppppppp/n7/8/8/3P4/PPPKPPPP/RNBQ1BNR b kq - 2 2");
        boolean inCheck = CheckDetector.isPlayerJustMovedInCheck(bitBoard);
        assertFalse(inCheck);
    }

    @Test
    public void testCheckDetector2() {
        // Checks lots of pins
        BitBoard bitBoard = FENUtils.getBoard("rnbq1bnr/pppkpppp/8/3N4/8/8/PPPPPPPP/R1BQKBNR w KQ - 1 3");
        assertFalse(CheckDetector.isPlayerJustMovedInCheck(bitBoard));
    }

    @Test
    public void testSlidingAttackerChecks() {
        BitBoard bitBoard = FENUtils.getBoard("k3r3/6b1/8/4K3/8/8/8/8 b - - 0 1");
        assertTrue(CheckDetector.isPlayerJustMovedInCheck(bitBoard));
    }

    @Test
    public void testSlidingAttackerNoChecks() {
        BitBoard bitBoard = FENUtils.getBoard("k3b3/6r1/8/4K3/8/8/8/8 b - - 0 1");
        assertFalse(CheckDetector.isPlayerJustMovedInCheck(bitBoard));
    }

    @Test
    public void testSlidingAttackerBlockedChecks1() {
        BitBoard bitBoard = FENUtils.getBoard("k1r5/6b1/5N2/2N5/8/2K5/8/8 b - - 0 1");
        assertFalse(CheckDetector.isPlayerJustMovedInCheck(bitBoard));
    }

    @Test
    public void testSlidingAttackerBlockedChecks2() {
        // Bishop blocked by rook
        BitBoard bitBoard = FENUtils.getBoard("k7/6b1/5r2/8/8/2K5/8/8 b - - 0 1");
        assertFalse(CheckDetector.isPlayerJustMovedInCheck(bitBoard));
    }

    @Test
    public void testSlidingAttackerBlockedChecks3() {
        // Rook blocked by bishop
        BitBoard bitBoard = FENUtils.getBoard("k7/2r5/2b5/8/8/2K5/8/8 b - - 0 1");
        assertFalse(CheckDetector.isPlayerJustMovedInCheck(bitBoard));

        bitBoard = FENUtils.getBoard("k7/2b5/2r5/8/8/2K5/8/8 b - - 0 1");
        assertTrue(CheckDetector.isPlayerJustMovedInCheck(bitBoard));
    }
}
