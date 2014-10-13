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
package nl.zoidberg.calculon.util;


public class KingMoveBitmapGen {
    private static final String ZEROS = "0000000000000000000000000000000000000000000000000000000000000000";
    public static void main(String[] args) {

        for (int rank = 0; rank < 8; rank++) {
            for (int file = 0; file < 8; file++) {
                long bitmap = 0;
                if (rank < 7 && file < 7) {
                    bitmap |= 1L << ((rank + 1) << 3) << (file + 1);
                }
                if (rank < 7) {
                    bitmap |= 1L << ((rank + 1) << 3) << (file);
                }
                if (rank < 7 && file > 0) {
                    bitmap |= 1L << ((rank + 1) << 3) << (file - 1);
                }
                if (file < 7) {
                    bitmap |= 1L << (rank << 3) << (file + 1);
                }
                if (file > 0) {
                    bitmap |= 1L << (rank << 3) << (file - 1);
                }
                if (rank > 0 && file < 7) {
                    bitmap |= 1L << ((rank - 1) << 3) << (file + 1);
                }
                if (rank > 0) {
                    bitmap |= 1L << ((rank - 1) << 3) << (file);
                }
                if (rank > 0 && file > 0) {
                    bitmap |= 1L << ((rank - 1) << 3) << (file - 1);
                }
                System.out.println("\t\tKING_MOVES[" + (rank << 3 | file) + "] = "
                        + BinaryPrint.print(bitmap) + ";");
            }
        }
    }}
