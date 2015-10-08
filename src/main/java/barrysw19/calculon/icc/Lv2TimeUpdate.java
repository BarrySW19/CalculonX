package barrysw19.calculon.icc;

import barrysw19.calculon.model.Piece;

public class Lv2TimeUpdate {
    private ResponseBlockLv2 blockLv2;
    private byte color;
    private long msec;

    public Lv2TimeUpdate(ResponseBlockLv2 blockLv2) {
        this.blockLv2 = blockLv2;
        String[] tokens = blockLv2.tokenize();
        color = "W".equals(tokens[2]) ? Piece.WHITE : Piece.BLACK;
        msec = Long.parseLong(tokens[3]);
    }

    public ResponseBlockLv2 getBlockLv2() {
        return blockLv2;
    }

    public byte getColor() {
        return color;
    }

    public long getMsec() {
        return msec;
    }
}
