package barrysw19.calculon.engine;

/**
 * Use polymorphism instead of conditionals, e.g. if(player == WHITE) ...
 */
interface ShiftStrategy {
    ShiftStrategy WHITE = new WhiteStrategy();
    ShiftStrategy BLACK = new BlackStrategy();

    long shiftForwardOneRank(long pos);
    long shiftForward(long pos, int distance);
    long shiftBackwardOneRank(long pos);
    long shiftBackward(long pos, int distance);

    int getPawnStartRank();

    abstract class BasicStrategy implements ShiftStrategy {
        @Override
        public long shiftForwardOneRank(long pos) {
            return shiftForward(pos, 1);
        }

        @Override
        public long shiftBackwardOneRank(long pos) {
            return shiftBackward(pos, 1);
        }
    }

    class WhiteStrategy extends BasicStrategy {
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

    class BlackStrategy extends BasicStrategy {
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
