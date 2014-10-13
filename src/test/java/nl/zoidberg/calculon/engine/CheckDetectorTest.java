package nl.zoidberg.calculon.engine;

import nl.zoidberg.calculon.notation.FENUtils;
import org.junit.Test;

import static org.junit.Assert.assertFalse;

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
}
