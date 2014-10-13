package nl.zoidberg.calculon.engine;

import nl.zoidberg.calculon.analyzer.*;
import nl.zoidberg.calculon.model.Piece;
import nl.zoidberg.calculon.model.Result;
import nl.zoidberg.calculon.notation.PGNUtils;

public class Tournament {

    public static void main(String[] args) {
        BitBoard board = new BitBoard().initialise();

        GameScorer gs1 = GameScorer.getDefaultScorer();
        GameScorer gs2 = new GameScorer();
        gs2.addScorer(new MaterialScorer());
        gs2.addScorer(new BishopPairScorer());
        gs2.addScorer(new MobilityScorer());
        gs2.addScorer(new PawnStructureScorer());
        gs2.addScorer(new KnightScorer());
        gs2.addScorer(new RookScorer());
        gs2.addScorer(new KingSafetyScorer());
        gs2.addScorer(new BackRankMinorPieceScorer());
        gs2.addScorer(new KingCentralisationScorer());

        ChessEngine engine1 = new ChessEngine(3);
        ChessEngine engine2 = new ChessEngine(gs2);
        engine2.setTargetTime(3);

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
        System.out.println(sb.toString());
    }
}
