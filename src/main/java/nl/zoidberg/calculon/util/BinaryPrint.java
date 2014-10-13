package nl.zoidberg.calculon.util;

public class BinaryPrint {
    private static final String ZEROS = "0000000000000000000000000000000000000000000000000000000000000000";

    public static String print(long bitmap) {
        StringBuilder sb = new StringBuilder(ZEROS);
        while(bitmap != 0) {
            long bit = Long.lowestOneBit(bitmap);
            int pos = Long.numberOfTrailingZeros(bit);
            sb.replace(63-pos, 64-pos, "1");
            bitmap &= ~bit;
        }
        for(int x = 0; x < 7; x++) {
            sb.insert(8 + (9*x), "_");
        }
        sb.insert(0, "0b").append("L");
        return sb.toString();
    }
}
