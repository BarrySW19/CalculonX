package barrysw19.calculon.engine;

import barrysw19.calculon.notation.FENUtils;

import javax.swing.*;

public class AnalyseWindow {

    public static void main(String[] args) {
        BitBoard board = FENUtils.getBoard("Q7/8/8/8/3k4/8/8/7K w - - 0 0");
        ChessEngine chessEngine = new ChessEngine(5);

        SearchContext ctx1 = chessEngine.getScoredMove(board, "A8A6", 5, 50);

    }

    public static void openWindow(SearchContext context) {
        JFrame jf = new JFrame("Move Tree");
        jf.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        JScrollPane scrollPane = new JScrollPane(new JTree(context.getTreeModel()));
        jf.getContentPane().add(scrollPane);
        jf.pack();
        jf.setVisible(true);
    }
}
