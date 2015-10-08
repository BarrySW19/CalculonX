package barrysw19.calculon.timing;

import barrysw19.calculon.analyzer.GameScorer;
import barrysw19.calculon.engine.BitBoard;
import barrysw19.calculon.notation.FENUtils;
import org.junit.Test;

import java.util.BitSet;

import static org.junit.Assert.assertEquals;

public class HashValueTest {

    @Test
    public void testHashGeneration() {
        int[] setBits = { 2, 5, 8, 9, 12, 14, 17, 18, 20, 21, 25, 30, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53,
                54, 55, 56, 57, 58, 59, 60, 61, 62, 71, 75, 79, 83, 87, 91, 95, 99, 102, 103, 105, 107, 108, 109,
                111, 112, 114, 115, 117, 118, 119, 120, 121, 123, 125, 127, 130, 131 };
        BitSet testSet = new BitSet();
        for(int i: setBits) {
            testSet.set(i);
        }
        BitBoard board = new BitBoard().initialise();
        assertEquals(testSet, board.getCacheId());
    }

    @Test
    public void testHashGeneration2() {
        int[] setBits = { 20, 21, 22, 29, 30, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 44, 46, 47, 49, 50, 51, 52, 53, 54, 56, 58 };
        BitSet testSet = new BitSet();
        for(int i: setBits) {
            testSet.set(i);
        }
        BitBoard board = FENUtils.getBoard("k7/8/8/8/8/8/8/7K w - - 0 1");
        assertEquals(testSet, board.getCacheId());
    }

    //@Test
    public void speedTest() {
        BitBoard board = FENUtils.getBoard("r3k3/ppp2p1r/2qp3p/4pb2/2Pn2p1/P2P2B1/1P3PPP/R2QKB1R w - - 0 1");
        for(int i = 0; i < 1000000; i++) {
            board.getCacheId();
        }

        long t = System.nanoTime();
        for(int i = 0; i < 1000000; i++) {
            board.getCacheId();
        }
        System.out.println((System.nanoTime() - t) / 1000000);

        t = System.nanoTime();
        for(int i = 0; i < 1000000; i++) {
            GameScorer.getDefaultScorer().score(board);
        }
        System.out.println((System.nanoTime() - t) / 1000000);
    }
}
