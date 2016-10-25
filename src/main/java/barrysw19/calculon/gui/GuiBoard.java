package barrysw19.calculon.gui;

import barrysw19.calculon.engine.BitBoard;
import barrysw19.calculon.util.BitIterable;

import javax.swing.*;
import java.awt.*;

public class GuiBoard extends JPanel {
    private Color cDark = new Color(255, 206, 158);
    private Color cLight = new Color(209, 139, 71);
    private int dimension;
    private GuiComponents guiComponents;
    private BitBoard board = new BitBoard().initialise();
    private boolean reversed = false;

    public GuiBoard(int dimension) {
        this.dimension = dimension;
        setPreferredSize(new Dimension(dimension * 8, dimension * 8));
        guiComponents = GuiComponents.generateForPixelSize(dimension);
    }

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(cDark);
        g2d.fillRect(0, 0, dimension * 8, dimension * 8);
        g2d.setColor(cLight);
        for(int i = 0; i < 4; i++) {
            for(int j = 0; j < 4; j++) {
                g2d.fillRect(dimension * i * 2, j * dimension * 2, dimension, dimension);
                g2d.fillRect(dimension * i * 2 + dimension, dimension * (j*2 + 1), dimension, dimension);
            }
        }

        for(long pos: BitIterable.of(board.getAllPieces())) {
            int piece = board.getColoredPiece(pos);
            int[] coords = BitBoard.toCoords(pos);
            if(reversed) {
                coords[0] = 7 - coords[0];
                coords[1] = 7 - coords[1];
            }
            g2d.drawImage(guiComponents.getImage(piece), coords[0] * dimension, (7 - coords[1]) * dimension, this);
        }
    }

    public void setBoard(BitBoard board) {
        this.board = board;
    }

    public BitBoard getBoard() {
        return board;
    }

    public boolean isReversed() {
        return reversed;
    }

    public GuiBoard setReversed(boolean reversed) {
        this.reversed = reversed;
        return this;
    }
}
