package barrysw19.calculon.engine;

import barrysw19.calculon.notation.FENUtils;
import barrysw19.calculon.notation.PGNUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.junit.Test;

import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class KingMoveGeneratorTest {

    @Test
    public void testNormalMoveGeneration() {
        BitBoard bitBoard = FENUtils.getBoard("k7/8/8/8/3PK3/8/8/7B w - - 0 1");
        List<BitBoard.BitBoardMove> getMoves = Lists.newArrayList(
                new KingMoveGenerator().iterator(new MoveGeneratorImpl.MoveGeneratorContext(bitBoard)));

        Collection<String> moves = PGNUtils.convertMovesToPgn(bitBoard, getMoves);
        assertEquals(Sets.newHashSet("Ke3+", "Kf4+", "Kd3+", "Kf5+", "Ke5+", "Kf3", "Kd5"), moves);
    }

    @Test
    public void testDiscoveryThreatMoveGeneration() {
        BitBoard bitBoard = FENUtils.getBoard("k7/8/8/8/3PK3/8/8/7B w - - 0 1");
        List<BitBoard.BitBoardMove> getMoves = Lists.newArrayList(
                new KingMoveGenerator().generateThreatMoves(new MoveGeneratorImpl.MoveGeneratorContext(bitBoard)));

        Collection<String> moves = PGNUtils.convertMovesToPgn(bitBoard, getMoves);
        assertEquals(Sets.newHashSet("Ke3+", "Kf4+", "Kd3+", "Kf5+", "Ke5+"), moves);
    }

    @Test
    public void testMoveGenerationCapturesOnly() {
        BitBoard bitBoard = FENUtils.getBoard("7k/8/8/8/3pKp2/8/8/7B w - - 0 1");
        List<BitBoard.BitBoardMove> getMoves = Lists.newArrayList(
                new KingMoveGenerator().generateThreatMoves(new MoveGeneratorImpl.MoveGeneratorContext(bitBoard)));

        Collection<String> moves = PGNUtils.convertMovesToPgn(bitBoard, getMoves);
        assertEquals(Sets.newHashSet("Kxf4", "Kxd4"), moves);
    }
}