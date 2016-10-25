package barrysw19.calculon.gui;

import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.ImageTranscoder;

import java.awt.*;
import java.awt.image.BufferedImage;

public class BasicImageTranscoder extends ImageTranscoder {
    private BufferedImage bufferedImage;

    public BufferedImage getBufferedImage() {
        return bufferedImage;
    }

    public Dimension getDimension() {
        return new Dimension(bufferedImage.getWidth(), bufferedImage.getHeight());
    }

    @Override
    public BufferedImage createImage(int width, int height) {
        return new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    }

    @Override
    public void writeImage(BufferedImage bufferedImage, TranscoderOutput transcoderOutput) throws TranscoderException {
        this.bufferedImage = bufferedImage;
    }
}
