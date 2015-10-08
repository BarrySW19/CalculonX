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
package barrysw19.calculon.model;

public class Piece {
	public final static byte EMPTY 	= 0x00;
	
	public final static byte PAWN 	= 0x01;
	public final static byte KNIGHT = 0x02;
	public final static byte BISHOP = 0x03;
	public final static byte ROOK 	= 0x04;
	public final static byte QUEEN 	= 0x05;
	public final static byte KING 	= 0x06;
	
	public final static byte WHITE 	= 0x00;
	public final static byte BLACK	= 0x08;
	
	public final static byte MASK_TYPE	= 0x07;
	public final static byte MASK_COLOR	= 0x08;
	
	public final static String[] NAMES = { "-", "Pawn", "Knight", "Bishop", "Rook", "Queen", "King" };
	public final static String[] COLORS = { "White", "", "", "", "", "", "", "", "Black" };
}
