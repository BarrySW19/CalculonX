/**
 * Calculon - A Java chess-engine.
 *
 * Copyright (C) 2008-2009 Barry Smith
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package nl.zoidberg.calculon.analyzer;

import nl.zoidberg.calculon.engine.BitBoard;

public interface PositionScorer {
	
	public int scorePosition(BitBoard bitBoard, Context context);

    /**
     * Context which will be populated by the game scorer and passed to the position
     * scorers for information. Should be used for information which might be useful to
     * multiple scorers so it only gets calculated once.
     */
    public static class Context {
        private boolean endgame = false;
        private long isolatedPawns;

        public Context(BitBoard bitBoard) {
            populateContext(bitBoard);
        }

        private void populateContext(BitBoard bitBoard) {
            if(bitBoard.getBitmapQueens() == 0) {
                // Initial endgame test - maybe improve this later?
                if(Long.bitCount(bitBoard.getBitmapBishops() | bitBoard.getBitmapKnights() | bitBoard.getBitmapRooks()) <= 4) {
                    endgame = true;
                }
            }

            long allPawns = bitBoard.getBitmapPawns();
            isolatedPawns = calcIsolatedPawns(bitBoard.getBitmapWhite() & allPawns)
                    | calcIsolatedPawns(bitBoard.getBitmapBlack() & allPawns);
        }

        public boolean isEndgame() {
            return endgame;
        }

        public long getIsolatedPawns() {
            return isolatedPawns;
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
