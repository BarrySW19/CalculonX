package barrysw19.calculon.gui;

import barrysw19.calculon.engine.BitBoard;
import barrysw19.calculon.engine.MoveGeneratorImpl;
import barrysw19.calculon.model.Piece;
import barrysw19.calculon.util.BitIterable;

import javax.swing.*;
import java.awt.*;

public class GuiBoard extends JPanel {
//    private Color cDark = new Color(255, 206, 158);
//    private Color cLight = new Color(209, 139, 71);
    private Color cDark = new Color(128, 128, 128);
    private Color cLight = new Color(224, 224, 224);
    private int dimension;
    private GuiComponents guiComponents;
    private BitBoard board = new BitBoard().initialise();
    private boolean reversed = false;

    private boolean highlightAttacks = true;

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

        if(highlightAttacks) {
            g2d.setColor(new Color(255, 0, 0, 96));
            MoveGeneratorImpl.SquareCounts squareCounts = MoveGeneratorImpl.calculateSquareCounting(board, Piece.WHITE);
            for(long pos: BitIterable.of(squareCounts.getPositiveSquares())) {
                int square = Long.numberOfTrailingZeros(pos);
                int y = 7 - (square>>>3);
                g2d.fillRect((square&0x07) * dimension, y * dimension, dimension, dimension);
            }

            g2d.setColor(new Color(0, 255, 0, 96));
            for(long pos: BitIterable.of(squareCounts.getNegativeSquares())) {
                int square = Long.numberOfTrailingZeros(pos);
                int y = 7 - (square>>>3);
                g2d.fillRect((square&0x07) * dimension, y * dimension, dimension, dimension);
            }
        }
    }

    public void setBoard(BitBoard board) {
        this.board = board;
        repaint();
    }

    public boolean isReversed() {
        return reversed;
    }

    public GuiBoard setReversed(boolean reversed) {
        this.reversed = reversed;
        return this;
    }
}
