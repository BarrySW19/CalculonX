package barrysw19.calculon.gui;

import barrysw19.calculon.engine.BitBoard;
import barrysw19.calculon.notation.PGNUtils;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class GameWindow extends JPanel {
    private final GuiBoard guiBoard;
    private final JTable moveTable;
    private final JTextArea pgnArea;

    public GameWindow() {
        this.setLayout(new BorderLayout());
        guiBoard = new GuiBoard(15);
        moveTable = new JTable();
        moveTable.setCellSelectionEnabled(true);
        moveTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        moveTable.getColumnModel().addColumnModelListener(new TableColumnModelListener() {
            @Override
            public void columnAdded(TableColumnModelEvent e) { }
            @Override
            public void columnRemoved(TableColumnModelEvent e) { }
            @Override
            public void columnMoved(TableColumnModelEvent e) {}
            @Override
            public void columnMarginChanged(ChangeEvent e) { }
            @Override
            public void columnSelectionChanged(ListSelectionEvent e) {
                handleListSelectionEvent(e);
            }
        });
        moveTable.getSelectionModel().addListSelectionListener(this::handleListSelectionEvent);

        pgnArea = new JTextArea();

        this.add(guiBoard, BorderLayout.CENTER);

        JScrollPane scrollPane = new JScrollPane(moveTable);
        this.add(scrollPane, BorderLayout.EAST);
        this.add(pgnArea, BorderLayout.SOUTH);
    }

    private void handleListSelectionEvent(ListSelectionEvent e) {
        if(e.getValueIsAdjusting()) {
            return;
        }
        BitBoard bitBoard = new BitBoard().initialise();
        int idx = moveTable.getSelectedRow() * 2 + moveTable.getSelectedColumn() + 1;
        java.util.List<String> pgn = ((MovesTableModel) moveTable.getModel()).moves;
        PGNUtils.applyMoves(bitBoard, pgn.subList(0, idx));
        SwingUtilities.invokeLater(() -> guiBoard.setBoard(bitBoard));
    }

    public static void main(String[] args) {
        GameWindow gameWindow;
        JFrame jFrame = new JFrame();
        jFrame.getContentPane().add(gameWindow = new GameWindow());
        jFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        java.util.List<String> moves = PGNUtils.splitNotation("1. e4 c5 2. Nf3 d6 3. d4 cxd4 4. Nxd4 Nf6 5. Nc3 e6 6. Be3 Be7 7. Bb5+ Bd7\n" +
                "8. Be2 Nc6 9. O-O O-O 10. Ndb5 Qb8 11. Bf4 e5 12. Bg5 Be6 13. Qd3 a6 14. Bxf6\n" +
                "gxf6 15. Na3 b5 16. Nd5 Qa7 17. Qf3 Bxd5 18. exd5 Nd4 19. Qg4+ Kh8 20. Bd3\n" +
                "Rg8 21. Qh3 Rg7 22. c3 b4 23. cxb4 Rag8 24. Kh1 Qb8 25. b5 axb5 26. b4 Qa7\n" +
                "27. Nxb5 Nxb5 28. Bxb5 Qd4 29. Rad1 Qxb4 30. Bd3 e4 31. Be2 Rxg2 32. Qxg2\n" +
                "Rxg2 33. Kxg2 f5 34. Rb1 Qd2 35. Bc4 Bf6 36. Rb8+ Kg7 37. Rg1 Be5 38. a4\n" +
                "Qf4 39. a5 Qxh2+ 40. Kf1+ Kf6 41. Rgg8 Qh3+ 42. Ke2 Qc3 43. Ba6 f4 44. Kf1\n" +
                "f3 45. Rb1 Bh2 46. Rc8 Qd2 47. Bc4 e3 48. fxe3 Bg3 49. Be2 Qxe2+ 50. Kg1\n" +
                "Qg2#\n");
        gameWindow.moveTable.setModel(new MovesTableModel(moves));

        SwingUtilities.invokeLater(() -> {
            jFrame.pack();
            jFrame.setVisible(true);
        });
    }

    private static class MovesTableModel extends DefaultTableModel {
        private final java.util.List<String> moves;

        public MovesTableModel(java.util.List<String> moves) {
            this.moves = moves;
        }

        @Override
        public int getRowCount() {
            if(moves == null) {
                return 0;
            }
            return (moves.size() / 2) + (moves.size() % 2);
        }

        @Override
        public int getColumnCount() {
            return 2;
        }

        @Override
        public String getColumnName(int columnIndex) {
            return columnIndex == 0 ? "White" : "Black";
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return String.class;
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return false;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            int idx = rowIndex * 2 + columnIndex;
            return idx < moves.size() ? moves.get(idx) : "";
        }

        @Override
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) { }
    }
}
