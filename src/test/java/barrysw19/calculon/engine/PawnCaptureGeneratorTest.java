package barrysw19.calculon.engine;

import barrysw19.calculon.notation.FENUtils;
import barrysw19.calculon.notation.PGNUtils;
import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.assertTrue;

public class PawnCaptureGeneratorTest {

    @Test
    public void testEnPassantCapture() {
        BitBoard bitBoard = FENUtils.getBoard("8/2p5/3p4/kP5R/1r2Pp1K/8/6P1/8 b - e3 0 1");
        Set<String> pgn = PGNUtils.convertMovesToPgn(bitBoard,
                new PawnCaptureGenerator().generateThreatMoves(new MoveGeneratorImpl.MoveGeneratorContext(bitBoard)));
        assertTrue(pgn.contains("fxe3+"));
    }
}