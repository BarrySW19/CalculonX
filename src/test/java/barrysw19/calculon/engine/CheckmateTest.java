package barrysw19.calculon.engine;

import barrysw19.calculon.analyzer.GameScorer;
import barrysw19.calculon.notation.FENUtils;
import barrysw19.calculon.notation.PGNUtils;
import org.junit.Ignore;
import org.junit.Test;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class CheckmateTest {

    private static volatile int cc = 0, gc = 0;

    @Test @Ignore
    public void test1() throws Exception {
        int threads = 12;
        ExecutorService executorService = Executors.newFixedThreadPool(threads);
        for(int i = 0; i < threads; i++) {
            executorService.submit(this::checkmateTest);
        }
        executorService.awaitTermination(1, TimeUnit.DAYS);
    }

    public void checkmateTest() {
        while(true) {
            BitBoard bitBoard = FENUtils.getBoard("7R/8/8/3k4/8/8/P7/7K b - - 0 1");
            ChessEngine chessEngine = new ChessEngine(1);

            StringBuilder sb = new StringBuilder();
            int i;
            for(i = 0; i < 50; i++) {
                String algebraic = chessEngine.getPreferredMove(bitBoard);
                if(algebraic == null) {
                    System.out.println("ERR: " + bitBoard);
                    break;
                }
                if(i % 2 == 0) {
                    sb.append(i / 2 + 1).append(". ");
                }
                sb.append(PGNUtils.translateMove(bitBoard, algebraic)).append(" ");
                bitBoard.makeMove(bitBoard.getMove(algebraic));
                if(GameScorer.getDefaultScorer().score(bitBoard) == GameScorer.MATE_SCORE) {
                    cc++;
                    break;
                }
            }
            if( ! (GameScorer.getDefaultScorer().score(bitBoard) == GameScorer.MATE_SCORE)) {
                writeFailed(sb.toString());
            }
            gc++;
            System.out.println(sb);
            System.out.println(cc + " / " + gc);
        }
    }

    public static synchronized void writeFailed(String s) {
        try (PrintWriter pw = new PrintWriter(new FileWriter("c:/temp/mate.pgn", true))) {
            pw.println(s);
        } catch (IOException ignore) { }
    }
}
