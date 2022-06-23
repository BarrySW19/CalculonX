package barrysw19.calculon.engine;

import barrysw19.calculon.notation.FENUtils;
import barrysw19.calculon.notation.PGNUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toSet;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class KnightMoveGeneratorTest {

    @Test
    public void testNormalMoveGeneration() {
        BitBoard board = FENUtils.getBoard("5k2/8/8/1p3p2/3N4/8/7P/7K w - - 0 1"); // N on d4, p on b5, f5
        List<BitBoard.BitBoardMove> allMoves = Lists.newArrayList(
                new KnightMoveGenerator().iterator(new MoveGeneratorImpl.MoveGeneratorContext(board)));
        Set<String> knightMoves = allMoves.stream().map(m -> PGNUtils.translateMove(board, m.getAlgebraic())).collect(toSet());

        assertEquals(Sets.newHashSet("Nc6", "Nc2", "Nxf5", "Nxb5", "Nf3", "Nb3", "Ne6+", "Ne2"), knightMoves);
    }

    @Test
    public void testThreatMoveGeneration() {
        BitBoard board = FENUtils.getBoard("8/6k1/8/1p6/3N4/5p2/7P/7K w - - 0 1"); // N on d4, p on b5, f5
        List<BitBoard.BitBoardMove> threatMoves = Lists.newArrayList(
                new KnightMoveGenerator().generateThreatMoves(new MoveGeneratorImpl.MoveGeneratorContext(board)));

        Set<String> knightMoves = threatMoves.stream().map(m -> PGNUtils.translateMove(board, m.getAlgebraic())).collect(toSet());
        assertEquals(Sets.newHashSet("Nxf3", "Nxb5", "Ne6+", "Nf5+"), knightMoves);
    }

    @Test
    public void testThreatMoveGenerationWithDiscoveredCheck() {
        BitBoard board = FENUtils.getBoard("k7/8/8/8/8/8/N7/R6K w - - 0 1");
        List<BitBoard.BitBoardMove> threatMoves = Lists.newArrayList(
                new KnightMoveGenerator().generateThreatMoves(new MoveGeneratorImpl.MoveGeneratorContext(board)));

        Set<String> knightMoves = threatMoves.stream().map(m -> PGNUtils.translateMove(board, m.getAlgebraic())).collect(toSet());
        assertEquals(Sets.newHashSet("Nc3+", "Nc1+", "Nb4+"), knightMoves);
    }

    @Test
    public void testThreatMoveGenerationWithoutDiscoveredCheck() {
        BitBoard board = FENUtils.getBoard("k7/p7/8/8/8/8/N7/R6K w - - 0 1");
        List<BitBoard.BitBoardMove> threatMoves = Lists.newArrayList(
                new KnightMoveGenerator().generateThreatMoves(new MoveGeneratorImpl.MoveGeneratorContext(board)));

        Set<String> knightMoves = threatMoves.stream().map(m -> PGNUtils.translateMove(board, m.getAlgebraic())).collect(toSet());
        assertEquals(Collections.emptySet(), knightMoves);
    }
}