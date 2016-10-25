package barrysw19.calculon.gui;

import barrysw19.calculon.model.Piece;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscodingHints;
import org.apache.batik.transcoder.image.ImageTranscoder;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;

public class GuiComponents {
    private final static String[] svgFiles = {
            "Chess_plt45.svg", "Chess_pdt45.svg",
            "Chess_nlt45.svg", "Chess_ndt45.svg",
            "Chess_blt45.svg", "Chess_bdt45.svg",
            "Chess_rlt45.svg", "Chess_rdt45.svg",
            "Chess_qlt45.svg", "Chess_qdt45.svg",
            "Chess_klt45.svg", "Chess_kdt45.svg",
    };

    private BufferedImage[] images = new BufferedImage[16];

    public BufferedImage getImage(int colour, int piece) {
        return getImage(colour + piece);
    }

    public BufferedImage getImage(int colouredPiece) {
        return images[colouredPiece];
    }

    public static GuiComponents generateForPixelSize(int size) {
        GuiComponents guiComponents = new GuiComponents();
        TranscodingHints hints = null;
        int colour = Piece.WHITE;
        int piece = Piece.PAWN;
        try {
            for (final String fileName : svgFiles) {
                final InputStream inputStream = GuiComponents.class.getResourceAsStream("/svg/" + fileName);
                TranscoderInput transcoderInput = new TranscoderInput(inputStream);
                BasicImageTranscoder imageTranscoder = new BasicImageTranscoder();
                if (hints == null) {
                    hints = new TranscodingHints(imageTranscoder.getTranscodingHints());
                    hints.put(ImageTranscoder.KEY_USER_STYLESHEET_URI, GuiComponents.class.getResource("/svg/svg_hints.css").toURI().toString());
                    hints.put(ImageTranscoder.KEY_WIDTH, (float) size);
                }
                imageTranscoder.setTranscodingHints(hints);
                imageTranscoder.transcode(transcoderInput, null);
                inputStream.close();
                guiComponents.images[piece + colour] = imageTranscoder.getBufferedImage();
                colour = colour == Piece.WHITE ? Piece.BLACK : Piece.WHITE;
                if (colour == Piece.WHITE) {
                    piece++;
                }
            }
            return guiComponents;
        } catch (URISyntaxException|TranscoderException|IOException e) {
            throw new RuntimeException(e);
        }
    }
}
