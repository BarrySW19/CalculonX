package barrysw19.calculon.analyzer;

import barrysw19.calculon.engine.BitBoard;
import barrysw19.calculon.engine.ChessEngine;
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
    public void playKingQueenPawnVsKingEndgame() {
        expectForcedMateFromPosition("Q7/8/8/8/3k4/8/P7/7K w - - 0 0", 50);
    }

    @Test
    public void playKingQueenVsKingEndgame() {
        expectForcedMateFromPosition("Q7/8/8/8/3k4/8/8/7K w - - 0 0", 50);
    }

    @Test
    public void playKingRookVsKingEndgame() {
        expectForcedMateFromPosition("R7/8/8/8/3k4/8/8/7K w - - 0 0", 50);
    }

    private void expectForcedMateFromPosition(String position, int maxMoves) {
        BitBoard board = FENUtils.getBoard(position);
        ChessEngine chessEngine = new ChessEngine(5);
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < maxMoves && GameScorer.getDefaultScorer().score(board) != GameScorer.MATE_SCORE; i++) {
            String algebraic = chessEngine.getPreferredMove(board);
            if(i % 2 == 0) {
                sb.append(i / 2 + 1).append(". ");
            }
            sb.append(PGNUtils.translateMove(board, algebraic)).append(" ");
            board.makeMove(board.getMove(algebraic));
        }

        LOG.info("Forced Mate: {}", sb);
        assertTrue(GameScorer.getDefaultScorer().score(board) == GameScorer.MATE_SCORE);
    }
}
