package nl.zoidberg.calculon.engine;

/**
 * Use polymorphism instead of conditionals, e.g. if(player == WHITE) ...
 */
public interface ShiftStrategy {
    public static final ShiftStrategy WHITE = new WhiteStrategy();
    public static final ShiftStrategy BLACK = new BlackStrategy();

    public long shiftForwardOneRank(long pos);
    public long shiftForward(long pos, int distance);
    public long shiftBackwardOneRank(long pos);
    public long shiftBackward(long pos, int distance);

    public int getPawnStartRank();

    public static abstract class BasicStrategy implements ShiftStrategy {
        @Override
        public long shiftForwardOneRank(long pos) {
            return shiftForward(pos, 1);
        }

        @Override
        public long shiftBackwardOneRank(long pos) {
            return shiftBackward(pos, 1);
        }
    }

    public static class WhiteStrategy extends BasicStrategy {
        @Override
        public long shiftForward(long pos, int distance) {
            // Move everything 1 -> 8 and clear back rank
            return (pos << (8 * distance));
        }

        @Override
        public long shiftBackward(long pos, int distance) {
            // Move everything in direction 8 -> 1, new row is automatically empty.
            return (pos >>> (8 * distance));
        }

        @Override
        public int getPawnStartRank() {
            return 1;
        }
    }

    public static class BlackStrategy extends BasicStrategy {
        @Override
        public long shiftForward(long pos, int distance) {
            // Move everything in direction 8 -> 1, new row is automatically empty.
            return (pos >>> (8 * distance));
        }

        @Override
        public long shiftBackward(long pos, int distance) {
            // Move everything 1 -> 8 and clear back rank
            return (pos << (8 * distance));
        }

        @Override
        public int getPawnStartRank() {
            return 6;
        }
    }
}
