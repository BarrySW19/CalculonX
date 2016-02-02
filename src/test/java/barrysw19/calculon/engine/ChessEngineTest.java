package barrysw19.calculon.engine;

import barrysw19.calculon.notation.FENUtils;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ChessEngineTest {

    @Test @Ignore("need to find out why this evaluation is wrong")
    public void testCorrectCapture() {
        // Calculon 0.5.11 makes the bad move Nxb2 here - best seems to be Nxe3
        BitBoard board = FENUtils.getBoard("r3kb1r/ppp1pppp/2n5/4P3/3P2B1/4BN2/PP3PPP/R2nK2R b KQkq - 0 11");
        ChessEngine chessEngine = new ChessEngine(30);

        SearchContext ctx1 = chessEngine.getScoredMove(board, "D1E3", 5, 50);
        SearchContext ctx2 = chessEngine.getScoredMove(board, "D1B2", 5, 50);
        System.out.println(ctx1);
        System.out.println(ctx2);
        assertTrue(ctx1.getScore() < ctx2.getScore());
    }

    @Test @Ignore("Need to make engine simplify when advantageous")
    public void testSimplify() {
        BitBoard board = FENUtils.getBoard("8/R1R2pk1/5q1p/1p6/8/5PP1/PP3PK1/8 w - - 0 32");
        ChessEngine chessEngine = new ChessEngine(600);

        SearchContext ctx1 = chessEngine.getScoredMove(board, "C7F7", 5, 50); // Rxf7+
        SearchContext ctx2 = chessEngine.getScoredMove(board, "B2B3", 5, 50); // b3
        System.out.println(ctx1);
        System.out.println(ctx2);
        assertTrue(ctx1.getScore() < ctx2.getScore());
    }

    @Test
    public void testTimeout() {
        BitBoard board = FENUtils.getBoard("r3k2r/pppq1ppp/2nbbn2/3pp3/3PP3/2NBBN2/PPPQ1PPP/R3K2R w KQkq - 0 8");
        ChessEngine chessEngine = new ChessEngine();
        chessEngine.setTargetTime(1);
        chessEngine.setQDepth(20);
        int moves = new MoveGeneratorImpl(board).getAllRemainingMoves().size();
        Collection<SearchContext> result = chessEngine.getScoredMoves(board);

        // Engine shouldn't have time to calculate any move, should timeout.
        assertTrue(result.size() < moves);
    }

    @Test
    public void testNoTimeout() {
        BitBoard board = FENUtils.getBoard("r3k2r/pppq1ppp/2nbbn2/3pp3/3PP3/2NBBN2/PPPQ1PPP/R3K2R w KQkq - 0 8");
        ChessEngine chessEngine = new ChessEngine();
        chessEngine.setTargetTime(10);
        chessEngine.setQDepth(1);
        List<SearchContext> result = chessEngine.getScoredMoves(board);
        for(SearchContext scoredMove: result) {
            assertEquals(SearchContext.Status.NORMAL, scoredMove.getStatus());
        }
    }

//    @Test
//    public void testDebug1() {
//        BitBoard board = FENUtils.getBoard("r3k2r/pppq1ppp/2nbbn2/3pp3/3PP3/2NBBN2/PPPQ1PPP/R3K2R w KQkq - 0 8");
//        ChessEngine chessEngine = new ChessEngine();
//        chessEngine.setTargetTime(10);
//        chessEngine.setQDepth(10);
//        long time = System.nanoTime();
//        Collection <SearchContext> result = chessEngine.getScoredMoves(board);
//        System.out.println((System.nanoTime() - time) / 1_000_000);
//    }
//
//    @Test
//    public void testDebug2() {
//        BitBoard board = FENUtils.getBoard("3k4/3p4/8/8/8/8/6P1/6K1 w - - 0 0");
//        ChessEngine chessEngine = new ChessEngine();
//        chessEngine.setTargetTime(10);
//        chessEngine.setQDepth(10);
//        Collection<SearchContext> result = chessEngine.getScoredMoves(board);
//    }
}
