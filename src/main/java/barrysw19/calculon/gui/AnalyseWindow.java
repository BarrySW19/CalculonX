package barrysw19.calculon.gui;

import barrysw19.calculon.engine.BitBoard;
import barrysw19.calculon.engine.SearchContext;
import barrysw19.calculon.gui.GuiBoard;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;

import static barrysw19.calculon.engine.BitBoard.createCopy;

public class AnalyseWindow {

    public static void openWindow(SearchContext context) {
        JFrame jf = new JFrame("Move Tree");
        jf.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        jf.setLayout(new BorderLayout());

        GuiBoard guiBoard = new GuiBoard(25);
        guiBoard.setBoard(createCopy(context.getInitialBoard()));

        JTree jTree = new JTree(context.getTreeModel());
        jTree.addTreeSelectionListener(e -> {
            BitBoard initial = BitBoard.createCopy(context.getInitialBoard());
            for(Object obj: e.getPath().getPath()) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) obj;
                SearchContext.SearchNode searchNode = (SearchContext.SearchNode) node.getUserObject();
                BitBoard.BitBoardMove move = searchNode.getMove();
                initial.makeMove(move);
            }
            guiBoard.setBoard(initial);
            guiBoard.repaint();
        });

        JScrollPane scrollPane = new JScrollPane(jTree);
        jf.getContentPane().add(scrollPane, BorderLayout.CENTER);
        jf.getContentPane().add(guiBoard, BorderLayout.WEST);

        jf.pack();
        //jf.setLocation(2000, 500);
        jf.setVisible(true);
    }
}
