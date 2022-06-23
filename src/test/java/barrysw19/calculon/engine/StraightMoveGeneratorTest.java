package barrysw19.calculon.engine;

import barrysw19.calculon.model.Piece;
import barrysw19.calculon.notation.FENUtils;
import barrysw19.calculon.notation.PGNUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class StraightMoveGeneratorTest {

    @Test
    public void testNormalMoveGenerationBishop() {
        BitBoard bitBoard = FENUtils.getBoard("8/p1k5/8/8/3B4/4P3/1p3r1P/7K w - - 0 1");
        List<BitBoard.BitBoardMove> getMoves = Lists.newArrayList(
                new StraightMoveGenerator(Piece.BISHOP).iterator(
                        new MoveGeneratorImpl.MoveGeneratorContext(bitBoard)));

        Collection<String> moves = PGNUtils.convertMovesToPgn(bitBoard, getMoves);
        assertEquals(Sets.newHashSet("Bf6", "Bg7", "Bh8", "Bc3", "Bxa7", "Bc5", "Be5+", "Bxb2", "Bb6+"), moves);
    }

    @Test
    public void testThreatMoveGenerationBishop() {
        BitBoard bitBoard = FENUtils.getBoard("8/p1k5/8/8/3B4/4P3/1p3r1P/7K w - - 0 1");
        List<BitBoard.BitBoardMove> getMoves = Lists.newArrayList(
                new StraightMoveGenerator(Piece.BISHOP).generateThreatMoves(new MoveGeneratorImpl.MoveGeneratorContext(bitBoard)));

        Collection<String> moves = PGNUtils.convertMovesToPgn(bitBoard, getMoves);
        assertEquals(Sets.newHashSet("Bxa7", "Be5+", "Bxb2", "Bb6+"), moves);
    }

    @Test
    public void testThreatMoveGenerationBishopWithDiscoveredCheck() {
        BitBoard bitBoard = FENUtils.getBoard("k7/8/8/3P4/8/8/B7/R6K w - - 0 1");
        List<BitBoard.BitBoardMove> getMoves = Lists.newArrayList(
                new StraightMoveGenerator(Piece.BISHOP).generateThreatMoves(new MoveGeneratorImpl.MoveGeneratorContext(bitBoard)));

        Collection<String> moves = PGNUtils.convertMovesToPgn(bitBoard, getMoves);
        assertEquals(Sets.newHashSet("Bb1+", "Bb3+", "Bc4+"), moves);
    }

    @Test
    public void testNormalMoveGenerationRook() {
        BitBoard bitBoard = FENUtils.getBoard("8/p1k5/8/8/1p1Rp3/3P4/5r1P/7K w - - 0 1");
        List<BitBoard.BitBoardMove> getMoves = Lists.newArrayList(
                new StraightMoveGenerator(Piece.ROOK).iterator(new MoveGeneratorImpl.MoveGeneratorContext(bitBoard)));

        Collection<String> moves = PGNUtils.convertMovesToPgn(bitBoard, getMoves);
        assertEquals(Sets.newHashSet("Rd7+", "Rd5", "Rxe4", "Rd6", "Rd8", "Rc4+", "Rxb4"), moves);
    }

    @Test
    public void testThreatMoveGenerationRook() {
        BitBoard bitBoard = FENUtils.getBoard("8/p1k5/8/8/1p1Rp3/3P4/5r1P/7K w - - 0 1");
        List<BitBoard.BitBoardMove> getMoves = Lists.newArrayList(
                new StraightMoveGenerator(Piece.ROOK).generateThreatMoves(new MoveGeneratorImpl.MoveGeneratorContext(bitBoard)));

        Collection<String> moves = PGNUtils.convertMovesToPgn(bitBoard, getMoves);
        assertEquals(Sets.newHashSet("Rd7+", "Rxe4", "Rc4+", "Rxb4"), moves);
    }

    @Test
    public void testThreatMoveGenerationRookWithDiscoveredCheck() {
        BitBoard bitBoard = FENUtils.getBoard("k7/8/8/8/8/6P1/4P1R1/K6B w - - 0 1");
        List<BitBoard.BitBoardMove> getMoves = Lists.newArrayList(
                new StraightMoveGenerator(Piece.ROOK).generateThreatMoves(new MoveGeneratorImpl.MoveGeneratorContext(bitBoard)));

        Collection<String> moves = PGNUtils.convertMovesToPgn(bitBoard, getMoves);
        assertEquals(Sets.newHashSet("Rh2+", "Rg1+", "Rf2+"), moves);
    }

    @Test
    public void testThreatMoveGenerationRookWithoutDiscoveredCheck() {
        BitBoard bitBoard = FENUtils.getBoard("k7/1p6/8/8/8/6P1/4P1R1/K6B w - - 0 1");
        List<BitBoard.BitBoardMove> getMoves = Lists.newArrayList(
                new StraightMoveGenerator(Piece.ROOK).generateThreatMoves(new MoveGeneratorImpl.MoveGeneratorContext(bitBoard)));

        Collection<String> moves = PGNUtils.convertMovesToPgn(bitBoard, getMoves);
        assertEquals(Collections.emptySet(), moves);
    }
}
