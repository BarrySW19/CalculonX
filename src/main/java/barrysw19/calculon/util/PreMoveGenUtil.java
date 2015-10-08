package barrysw19.calculon.util;

public class PreMoveGenUtil {
    public static int[][] MOVES = new int[][] {
            // Up/down, right/left
            { 1, 0 }, { 1, 1 },
            { 0, 1 }, { -1, 1 },
            { -1, 0 }, { -1, -1 },
            { 0, -1 }, 	{ 1, -1 }
    };

    public static void main(String[] args) {
        for(int r = 0; r < 8; r++) {
            for(int f = 0; f < 8; f++) {
                generateFrom(f, r);
            }
        }
    }

    private static void generateFrom(int f, int r) {
        int s = ((r<<3)|f);
        System.out.println(String.format("SLIDE_MOVES[%d] = new long[][] {", s));
        for(int m = 0; m < 8; m++) {
            int[] moves = MOVES[m];
            int f1 = f + moves[1];
            int r1 = r + moves[0];
            System.out.println("{");
            while(f1 >= 0 && f1 < 8 && r1 >= 0 && r1 < 8) {
                long pos = 1L<<((r1<<3)|f1);
                System.out.println(BinaryPrint.print(pos) + ",");
                f1 += moves[1];
                r1 += moves[0];
            }
            System.out.println("},");
        }
        System.out.println("};");
    }
}
