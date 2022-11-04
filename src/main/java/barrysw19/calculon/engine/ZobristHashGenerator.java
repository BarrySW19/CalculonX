package barrysw19.calculon.engine;

import barrysw19.calculon.util.BitIterable;

import java.util.Random;

public class ZobristHashGenerator {
    // Actually, only 64x12 of these will be used - idx 0, 7, 8 and 15 have no corresponding piece
    private final long[][] RANDOM_HASHES = new long[64][16];

    public ZobristHashGenerator() {
        populateHashValues();
    }

    public long generateHash(final BitBoard board) {
        long hash = 0;
        for (Long aLong : BitIterable.of(board.getAllPieces())) {
            int pos = Long.numberOfTrailingZeros(aLong);
            byte piece = board.getColoredPiece(pos);
            hash ^= RANDOM_HASHES[pos][piece];
        }
        return hash;
    }

    private void populateHashValues() {
        final Random random = new Random(0);
        for(int i = 0; i < 64; i++) {
            for(int j = 0; j < 16; j++) {
                RANDOM_HASHES[i][j] = random.nextLong();
            }
        }
    }
}
