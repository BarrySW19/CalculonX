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



public class KnightMoveBitmapGen {

	public static int[][] MOVES = new int[][] {
		{ 1, 2 }, 	{ 1, -2 },
		{ -1, 2 }, 	{ -1, -2 },
		{ 2, 1 },	{ 2, -1 },
		{ -2, 1 },	{ -2, -1 },
	};

	public static void main(String[] args) {
        for(int rank = 0; rank < 8; rank++) {
            for(int file = 0; file < 8; file++) {
				long bitmap = 0;
				for(int[] move: MOVES) {
					int nFile = file + move[0];
					int nRank = rank + move[1];
					if(((nFile & ~0x07) | (nRank & ~0x07)) == 0) {
						bitmap |= 1L<<(nRank<<3)<<nFile;
					}
				}
				System.out.println("\t\tKNIGHT_MOVES[" + (rank<<3|file) + "] = "
                        + BinaryPrint.print(bitmap) + ";");
				// System.out.println(BitBoard.toPrettyString(bitmap));
			}
		}
	}
}
