package barrysw19.calculon.engine;

import barrysw19.calculon.analyzer.*;
import barrysw19.calculon.model.Piece;
import barrysw19.calculon.model.Result;
import barrysw19.calculon.notation.PGNUtils;

public class Tournament {

    public static void main(String[] args) {
        BitBoard board = new BitBoard().initialise();

        //GameScorer gs1 = GameScorer.getDefaultScorer();
        GameScorer gs2 = new GameScorer()
                .addScorer(new MaterialScorer());

        ChessEngine engine1 = new ChessEngine().setTargetTime(3);
        ChessEngine engine2 = new ChessEngine().setTargetTime(3).setGameScorer(gs2);

        StringBuilder sb = new StringBuilder();
        int move = 1;
        while(board.getResult() == Result.RES_NO_RESULT) {
            String alg;
            if(board.getPlayer() == Piece.WHITE) {
                sb.append(move++).append(". ");
                alg = engine1.getPreferredMove(board);
            } else {
                alg = engine2.getPreferredMove(board);
            }
            String pgn = PGNUtils.translateMove(board, alg);
            System.out.println(pgn);
            sb.append(pgn).append(" ");
            board.makeMove(board.getMove(alg));
        }
        System.out.println("[Event \"ICC\"]");
        System.out.println("[Site \"Internet Chess Club\"]");
        System.out.println("[Date \"2013.11.30\"]");
        System.out.println("[White \"TalEnuf\"]");
        System.out.println("[Black \"CalculonX\"]");
        System.out.println("[Result \"*\"]");
        System.out.println("[TimeControl \"120+1\"]\n");
        System.out.println(board.getResult());
        System.out.println(sb);
    }
}
