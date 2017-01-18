package barrysw19.calculon.engine;

import barrysw19.calculon.model.Piece;
import barrysw19.calculon.notation.FENUtils;
import barrysw19.calculon.notation.PGNUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toSet;
import static org.junit.Assert.*;

public class StraightMoveIteratorTest {

    @Test
    public void testNormalMoveGenerationBishop() {
        BitBoard bitBoard = FENUtils.getBoard("8/p1k5/8/8/3B4/4P3/1p3r1P/7K w - - 0 1");
        List<BitBoard.BitBoardMove> getMoves = Lists.newArrayList(
                new StraightMoveGenerator(Piece.BISHOP).iterator(bitBoard, CheckDetector.isPlayerJustMovedInCheck(bitBoard), 0));

        Collection<String> moves = PGNUtils.convertMovesToPgn(bitBoard, getMoves);
        assertEquals(Sets.newHashSet("Bf6", "Bg7", "Bh8", "Bc3", "Bxa7", "Bc5", "Be5+", "Bxb2", "Bb6+"), moves);
    }

    @Test
    public void testThreatMoveGenerationBishop() {
        BitBoard bitBoard = FENUtils.getBoard("8/p1k5/8/8/3B4/4P3/1p3r1P/7K w - - 0 1");
        List<BitBoard.BitBoardMove> getMoves = new ArrayList<>();
        new StraightMoveGenerator(Piece.BISHOP).generateThreatMoves(
                bitBoard, CheckDetector.isPlayerJustMovedInCheck(bitBoard), 0, getMoves);

        Collection<String> moves = PGNUtils.convertMovesToPgn(bitBoard, getMoves);
        assertEquals(Sets.newHashSet("Bxa7", "Be5+", "Bxb2", "Bb6+"), moves);
    }

    @Test
    public void testNormalMoveGenerationRook() {
        BitBoard bitBoard = FENUtils.getBoard("8/p1k5/8/8/1p1Rp3/3P4/5r1P/7K w - - 0 1");
        List<BitBoard.BitBoardMove> getMoves = Lists.newArrayList(
                new StraightMoveGenerator(Piece.ROOK).iterator(bitBoard, CheckDetector.isPlayerJustMovedInCheck(bitBoard), 0));

        Collection<String> moves = PGNUtils.convertMovesToPgn(bitBoard, getMoves);
        assertEquals(Sets.newHashSet("Rd7+", "Rd5", "Rxe4", "Rd6", "Rd8", "Rc4+", "Rxb4"), moves);
    }

    @Test
    public void testThreatMoveGenerationRook() {
        BitBoard bitBoard = FENUtils.getBoard("8/p1k5/8/8/1p1Rp3/3P4/5r1P/7K w - - 0 1");
        List<BitBoard.BitBoardMove> getMoves = new ArrayList<>();
        new StraightMoveGenerator(Piece.ROOK).generateThreatMoves(
                bitBoard, CheckDetector.isPlayerJustMovedInCheck(bitBoard), 0, getMoves);

        Collection<String> moves = PGNUtils.convertMovesToPgn(bitBoard, getMoves);
        assertEquals(Sets.newHashSet("Rd7+", "Rxe4", "Rc4+", "Rxb4"), moves);
    }
}
