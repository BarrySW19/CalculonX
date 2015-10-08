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
package barrysw19.calculon.engine;

import barrysw19.calculon.model.Piece;

public class EngineUtils {
//	private static final Logger log = Logger.getLogger(EngineUtils.class.getName());
	
	public static final String FILES = "ABCDEFGH";
	public static final String RANKS = "12345678";
	
	private static String[] coords = new String[64];
	static {
		coords[0] = "A1";
		coords[1] = "A2";
		coords[2] = "A3";
		coords[3] = "A4";
		coords[4] = "A5";
		coords[5] = "A6";
		coords[6] = "A7";
		coords[7] = "A8";
		coords[8] = "B1";
		coords[9] = "B2";
		coords[10] = "B3";
		coords[11] = "B4";
		coords[12] = "B5";
		coords[13] = "B6";
		coords[14] = "B7";
		coords[15] = "B8";
		coords[16] = "C1";
		coords[17] = "C2";
		coords[18] = "C3";
		coords[19] = "C4";
		coords[20] = "C5";
		coords[21] = "C6";
		coords[22] = "C7";
		coords[23] = "C8";
		coords[24] = "D1";
		coords[25] = "D2";
		coords[26] = "D3";
		coords[27] = "D4";
		coords[28] = "D5";
		coords[29] = "D6";
		coords[30] = "D7";
		coords[31] = "D8";
		coords[32] = "E1";
		coords[33] = "E2";
		coords[34] = "E3";
		coords[35] = "E4";
		coords[36] = "E5";
		coords[37] = "E6";
		coords[38] = "E7";
		coords[39] = "E8";
		coords[40] = "F1";
		coords[41] = "F2";
		coords[42] = "F3";
		coords[43] = "F4";
		coords[44] = "F5";
		coords[45] = "F6";
		coords[46] = "F7";
		coords[47] = "F8";
		coords[48] = "G1";
		coords[49] = "G2";
		coords[50] = "G3";
		coords[51] = "G4";
		coords[52] = "G5";
		coords[53] = "G6";
		coords[54] = "G7";
		coords[55] = "G8";
		coords[56] = "H1";
		coords[57] = "H2";
		coords[58] = "H3";
		coords[59] = "H4";
		coords[60] = "H5";
		coords[61] = "H6";
		coords[62] = "H7";
		coords[63] = "H8";		
	}
	
	public static String toCoord(long oneBit) {
		int trailing = Long.numberOfTrailingZeros(oneBit);
		return coords[(trailing&0x38)>>3 | (trailing&0x07)<<3];
	}
	
	public static String toSimpleAlgebraic(int file1, int rank1, int file2, int rank2) {
		return coords[file1<<3|rank1] + coords[file2<<3|rank2];
	}
	
	public static byte getType(byte piece) {
		return (byte) (piece & Piece.MASK_TYPE);
	}
	
	public static byte getColor(byte piece) {
		return (byte) (piece & Piece.MASK_COLOR);
	}
}
