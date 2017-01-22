package barrysw19.calculon.engine;

import barrysw19.calculon.notation.FENUtils;
import org.junit.Test;

import java.util.List;

public class BadMovesFromGamesTest {

    @Test
    public void testBadMove1() {
        // Not Qxg2
        BitBoard bitBoard = FENUtils.getBoard("6rk/4bp1p/3p1p2/3P4/1q2p3/7Q/P3BPrP/3R1R1K w - - 0 32");
        ChessEngine chessEngine = new ChessEngine(5);
        //List<SearchContext> searchContext = chessEngine.getScoredMoves(bitBoard);
        SearchContext searchContext = chessEngine.getPreferredMoveContext(bitBoard);
        System.out.println(searchContext);
    }

    @Test
    public void testBadMove2() {
        BitBoard bitBoard = FENUtils.getBoard("rnb1kb1r/pp3ppp/1qpp1n2/8/2BNPB2/8/PPP2PPP/RN1QK2R w KQkq - 0 7");
        ChessEngine chessEngine = new ChessEngine(5);
        //List<SearchContext> searchContext = chessEngine.getScoredMoves(bitBoard);
        SearchContext searchContext = chessEngine.getPreferredMoveContext(bitBoard);
        System.out.println(searchContext);
    }
}
