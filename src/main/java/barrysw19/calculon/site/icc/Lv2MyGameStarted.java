package barrysw19.calculon.site.icc;

import barrysw19.calculon.model.Piece;

public class Lv2MyGameStarted {
    private ResponseBlockLv2 lv2Block;
    private final int whiteInitial;
    private final int blackInitial;
    private final int whiteIncrement;
    private final int blackIncrement;

    public Lv2MyGameStarted(ResponseBlockLv2 lv2Block) {
        this.lv2Block = lv2Block;
        String[] tokens = lv2Block.tokenize();
        whiteInitial = Integer.parseInt(tokens[7]);
        whiteIncrement = Integer.parseInt(tokens[8]);
        blackInitial = Integer.parseInt(tokens[9]);
        blackIncrement = Integer.parseInt(tokens[10]);
    }

    public int getInitialTime(byte color) {
        return color == Piece.WHITE ? whiteInitial : blackInitial;
    }

    public int getIncrement(byte color) {
        return color == Piece.WHITE ? whiteIncrement : blackIncrement;
    }
}
