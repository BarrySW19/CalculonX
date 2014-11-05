package nl.zoidberg.calculon.engine;

import nl.zoidberg.calculon.notation.FENUtils;
import nl.zoidberg.calculon.notation.PGNUtils;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class BitBoardTest {

    @Test
    public void testCheckRepeatMove() {
        BitBoard board = FENUtils.getBoard("r1b5/ppQpk1br/6pp/5p2/P7/8/1PP2PPP/RN4K1 b - - 0 21");

        PGNUtils.applyMoves(board, "Kf7", "Nc3");
        assertFalse(board.isRepeated());

        PGNUtils.applyMoves(board, "Ke7", "Nb1");
        assertTrue(board.isRepeated());

        PGNUtils.applyMoves(board, "Kf7", "Nc3");
        assertTrue(board.isRepeated());
    }
}
