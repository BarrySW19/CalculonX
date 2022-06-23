package barrysw19.calculon.notation;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class Style12Test {

    @Test
    public void testStyle12Create() {
        Style12 style12 = new Style12("<12> --r-kb-r -p--pppp pq-pbn-- --n----- ----P--- -NNBBP-- PPP-Q-PP R----RK- W -1 0 0 1 0 0 894 forfan CalculonX -1 15 15 38 38 991 1029 12 R/a8-c8 (0:14) Rc8 1");
        assertEquals(Style12.REL_OPP_TO_MOVE, style12.getMyRelationToGame());
        assertTrue(style12.getBoard().equalPosition(FENUtils.getBoard("2r1kb1r/1p2pppp/pq1pbn2/2n5/4P3/1NNBBP2/PPP1Q1PP/R4RK1 w k - 0 12")));
    }
}
