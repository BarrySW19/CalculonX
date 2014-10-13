package nl.zoidberg.calculon.analyzer;

import nl.zoidberg.calculon.engine.BitBoard;
import nl.zoidberg.calculon.model.Piece;
import nl.zoidberg.calculon.notation.FENUtils;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PassedPawnScorerTest {
    // Spassky-Petrosian - White's passed pawn is superior
    // Fritz rates this position about a pawn ahead for white
    private static final String FEN = "2r2rk1/p4ppp/1p6/n2P4/5Q2/5N2/q4PPP/3RR1K1 w - - 0 0";

    @Test
    public void testPosition() {
        BitBoard board = FENUtils.getBoard(FEN);
        assertEquals(0b00000000_00000000_00000000_00001000_00000000_00000000_00000000_00000000L, PassedPawnScorer.getPassedPawns(board, Piece.WHITE));
        assertEquals(0b00000000_00000001_00000010_00000000_00000000_00000000_00000000_00000000L, PassedPawnScorer.getPassedPawns(board, Piece.BLACK));
        System.out.println(GameScorer.getDefaultScorer().score(board));

        int score = new PassedPawnScorer().scorePosition(board, null);
        assertTrue(score > 0); // White advantage
    }
}
