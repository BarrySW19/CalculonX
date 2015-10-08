package barrysw19.calculon.util;

import barrysw19.calculon.engine.BitBoard;
import barrysw19.calculon.engine.MoveGeneratorImpl;
import barrysw19.calculon.engine.PawnMoveGenerator;

public class PerformanceTests {

    public static void main(String[] args) throws Exception {
        BitBoard board = new BitBoard().initialise();

        // Min 1350
        long pre = System.nanoTime();
        for(int x = 0; x < 1000000; x++) {
            MoveGeneratorImpl moveGenerator = new MoveGeneratorImpl(board);
            moveGenerator.setGenerators(new PawnMoveGenerator());
            moveGenerator.getAllRemainingMoves();
        }
        pre = System.nanoTime() - pre;
        System.out.println("time(ms): " + pre/1000000);
    }
}
