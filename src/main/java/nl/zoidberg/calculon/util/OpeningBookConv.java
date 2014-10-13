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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.StringTokenizer;

import nl.zoidberg.calculon.engine.BitBoard;
import nl.zoidberg.calculon.notation.FENUtils;
import nl.zoidberg.calculon.notation.PGNUtils;

public class OpeningBookConv {

	public static void main(String[] args) throws Exception {
		BufferedReader br = new BufferedReader(new FileReader("/home/barrys/wstest/calculon/doc/openingbook.txt"));
		PrintWriter pw = new PrintWriter(new FileWriter("/home/barrys/wstest/calculon/doc/openingbook.xml"));
		
		for(;;) {
			String position = br.readLine();
			if("#END#".equals(position)) {
				break;
			}
			
			String moves = br.readLine();
			pw.println("\t\t<moves>");
			pw.println("\t\t\t<position>" + position + "</position>");
			BitBoard bb = FENUtils.getBoard(position + " 0 1");
			for(StringTokenizer st = new StringTokenizer(moves); st.hasMoreTokens(); ) {
				String s = st.nextToken();
				String move = s.substring(0, s.indexOf('{')).toUpperCase();
				String count = s.substring(s.indexOf('{') + 1, s.lastIndexOf('}'));
				pw.println("\t\t\t<move pgn=\"" + PGNUtils.translateMove(bb, move) + "\" count=\"" + count + "\"/>");
			}
			pw.println("\t\t</moves>");
		}
		pw.close();
		br.close();
	}
}
