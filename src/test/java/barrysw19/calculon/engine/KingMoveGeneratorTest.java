package barrysw19.calculon.engine;

import barrysw19.calculon.notation.FENUtils;
import barrysw19.calculon.notation.PGNUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class KingMoveGeneratorTest {

    @Test
    public void testKingMovesFromCornerSquare() {
        // Created with GitHub CoPilot
        final BitBoard bitBoard = FENUtils.getBoard("k7/8/8/8/8/8/8/K7 w - - 0 1");
        final List<BitBoard.BitBoardMove> getMoves = Lists.newArrayList(
                new KingMoveGenerator().iterator(new MoveGeneratorImpl.MoveGeneratorContext(bitBoard)));

        final Set<String> moves = PGNUtils.convertMovesToPgn(bitBoard, getMoves);
        assertEquals(Sets.newHashSet("Ka2", "Kb2", "Kb1"), moves);
    }

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