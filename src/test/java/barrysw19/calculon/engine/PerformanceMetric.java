package barrysw19.calculon.engine;

import barrysw19.calculon.notation.FENUtils;

public class PerformanceMetric {

    public static void main(String[] args) {
        ChessEngine chessEngine = new ChessEngine(60);
        BitBoard bitBoard = FENUtils.getBoard("r3k2r/Pppp1ppp/1b3nbN/nP6/BBP1P3/q4N2/Pp1P2PP/R2Q1RK1 w kq - 0 1");
        chessEngine.getPreferredMoveContext(bitBoard);

        System.out.println("Calls: " + chessEngine.getCallMetric());
    }
}
