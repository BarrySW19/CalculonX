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

import java.util.HashMap;
import java.util.Map;

public class DiagonalBitmapGen {

	public static int[][] MOVES = new int[][] {
		{ 1, 1 }, 	{ 1, -1 },
		{ -1, 1 }, 	{ -1, -1 },
	};
	
	private static Map<String, int[]> dirs = new HashMap<String, int[]>();
	static {
		dirs.put("BM_U", new int[] { 0, 1 });
		dirs.put("BM_UR", new int[] { 1, 1 });
		dirs.put("BM_R", new int[] { 1, 0 });
		dirs.put("BM_DR", new int[] { 1, -1 });
		dirs.put("BM_D", new int[] { 0, -1 });
		dirs.put("BM_DL", new int[] { -1, -1 });
		dirs.put("BM_L", new int[] { -1, 0 });
		dirs.put("BM_UL", new int[] { -1, 1 });
	}

	public static void main(String[] args) {
		for(int file = 0; file < 8; file++) {
			for(int rank = 0; rank < 8; rank++) {
				for(String dir: dirs.keySet()) {
					long bitmap = 0;
					int[] move = dirs.get(dir);
					int nFile = file + move[0];
					int nRank = rank + move[1];
					while(((nFile & ~0x07) | (nRank & ~0x07)) == 0) {
						bitmap |= 1L<<(nRank<<3)<<nFile;
						nFile += move[0];
						nRank += move[1];
					}
					System.out.println("\t\tmaps2[" + dir + "][" + (rank<<3|file) + "] = " + bitmap + "L;");
					//System.out.println(BitBoard.toPrettyString(bitmap));
				}
			}
		}
	}
}
