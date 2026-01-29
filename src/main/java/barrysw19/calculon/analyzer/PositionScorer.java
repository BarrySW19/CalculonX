package barrysw19.calculon.analyzer;

import barrysw19.calculon.engine.BitBoard;
import barrysw19.calculon.model.Piece;
import lombok.Getter;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public interface PositionScorer {
    List<Byte> COLORS = Collections.unmodifiableList(Arrays.asList(Piece.WHITE, Piece.BLACK));
	
	int scorePosition(BitBoard bitBoard, Context context);

    /**
     * Context which will be populated by the game scorer and passed to the position
     * scorers for information. Should be used for information which might be useful to
     * multiple scorers so it only gets calculated once.
     */
    class Context {
        private final BitBoard bitBoard;
        @Getter
        private boolean endgame = false;
        @Getter
        private long isolatedPawns;
        private long backwardPawns;

        public Context(BitBoard bitBoard) {
            this.bitBoard = bitBoard;
            populateContext();
        }

        private void populateContext() {
            if(bitBoard.getBitmapQueens() == 0) {
                // Initial endgame test - maybe improve this later?
                if(Long.bitCount(bitBoard.getBitmapBishops() | bitBoard.getBitmapKnights() | bitBoard.getBitmapRooks()) <= 6) {
                    endgame = true;
                }
            }

            long allPawns = bitBoard.getBitmapPawns();
            isolatedPawns = calcIsolatedPawns(bitBoard.getBitmapWhite() & allPawns)
                    | calcIsolatedPawns(bitBoard.getBitmapBlack() & allPawns);
        }

        public long getBackwardPawns() {
            return backwardPawns;
        }

        private static long calcIsolatedPawns(long pawns) {
            long isolatedPawns = 0;
            long prevFile = 0;
            long thisFile = pawns & BitBoard.getFileMap(0);

            for(int file = 0; file < 8; file++) {
                long nextFile = (file == 7 ? 0 : pawns & BitBoard.getFileMap(file+1));

                if(thisFile != 0 && prevFile == 0 && nextFile == 0) {
                    isolatedPawns |= thisFile;
                }
                prevFile = thisFile;
                thisFile = nextFile;
            }
            return isolatedPawns;
        }
    }
}
