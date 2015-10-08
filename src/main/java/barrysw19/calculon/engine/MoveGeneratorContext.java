package barrysw19.calculon.engine;

public class MoveGeneratorContext {
    protected final BitBoard bitBoard;
    protected final boolean alreadyInCheck;
    protected final long potentialPins;

    protected MoveGeneratorContext(BitBoard bitBoard, boolean alreadyInCheck, long potentialPins) {
        this.bitBoard = bitBoard;
        this.alreadyInCheck = alreadyInCheck;
        this.potentialPins = potentialPins;
    }

    public BitBoard getBitBoard() {
        return bitBoard;
    }

    public boolean isAlreadyInCheck() {
        return alreadyInCheck;
    }

    public long getPotentialPins() {
        return potentialPins;
    }
}
