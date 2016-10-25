package barrysw19.calculon.analyzer;

import barrysw19.calculon.engine.AnalyseWindow;
import barrysw19.calculon.engine.BitBoard;
import barrysw19.calculon.engine.ChessEngine;
import barrysw19.calculon.engine.SearchContext;
import barrysw19.calculon.notation.FENUtils;
import barrysw19.calculon.notation.PGNUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertTrue;

/**
 * Check the engine can force mate in K+Q v K and K+R v K endgames.
 */
@Ignore // Too slow for normal builds
public class ForcedMateTest {
    private static final Logger LOG = LoggerFactory.getLogger(ForcedMateTest.class);

    @Test @Ignore
    public void playKingQueenPawnVsKingEndgame() throws InterruptedException {
        expectForcedMateFromPosition("Q7/8/8/8/3k4/8/P7/7K w - - 0 0", 50);
    }

    @Test
    public void playKingQueenVsKingEndgame() throws InterruptedException {
        expectForcedMateFromPosition("Q7/8/8/8/3k4/8/8/7K w - - 0 0", 50);
    }

    @Test
    public void playKingRookVsKingEndgame() throws InterruptedException {
        expectForcedMateFromPosition("R7/8/8/8/3k4/8/8/7K w - - 0 0", 50);
    }

    private void expectForcedMateFromPosition(String position, int maxMoves) throws InterruptedException {
        BitBoard board = FENUtils.getBoard(position);
        ChessEngine chessEngine = new ChessEngine(2);
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for(int i = 0; i < maxMoves && GameScorer.getDefaultScorer().score(board) != GameScorer.MATE_SCORE; i++) {
            SearchContext context = chessEngine.getPreferredMoveContext(board);
            if(context.getScore() == -9999999 && first && context.getStatus() == SearchContext.Status.NORMAL) {
                first = false;
                AnalyseWindow.openWindow(context);
                Thread.sleep(100000000L);
            }
            if(i % 2 == 0) {
                sb.append(i / 2 + 1).append(". ");
            }
            sb.append(PGNUtils.translateMove(board, context.getAlgebraicMove())).append(" ");
            board.makeMove(board.getMove(context.getAlgebraicMove()));
        }

        LOG.info("Forced Mate: {}", sb);
        assertTrue(GameScorer.getDefaultScorer().score(board) == GameScorer.MATE_SCORE);
    }
}
