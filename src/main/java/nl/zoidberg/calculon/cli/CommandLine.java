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
package nl.zoidberg.calculon.cli;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import nl.zoidberg.calculon.engine.BitBoard;
import nl.zoidberg.calculon.engine.ChessEngine;
import nl.zoidberg.calculon.notation.PGNUtils;

public class CommandLine {
	
	public static void main(String[] args) throws Exception {
		BitBoard board = new BitBoard().initialise();
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String s;
		while((s = br.readLine()) != null) {
			PGNUtils.applyMove(board, s);
			ChessEngine node = new ChessEngine();
			String move = node.getPreferredMove(board);
			System.out.println(PGNUtils.translateMove(board, move));
			board.makeMove(board.getMove(move));
		}
	}
}
