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
package nl.zoidberg.calculon.opening;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import nl.zoidberg.calculon.engine.BitBoard;
import nl.zoidberg.calculon.notation.FENUtils;

import org.apache.commons.digester.Digester;

public class OpeningBook {
	private static Logger log = Logger.getLogger(OpeningBook.class.getName());
	private static OpeningBook defaultBook;

	private Map<String, MoveList> book = new HashMap<String, MoveList>();
	
	public static void setUseOpeningBook(boolean useOpeningBook) {
		if(useOpeningBook) {
			defaultBook = null;
			getDefaultBook();
		} else {
			defaultBook = new OpeningBook(); // Empty book
		}
	}
	
	public static OpeningBook getDefaultBook() {
		if(defaultBook != null) {
			return defaultBook;
		}

		Digester digester = new Digester();
		digester.addObjectCreate("calculon/opening-book", OpeningBook.class);
		digester.addObjectCreate("calculon/opening-book/moves", OpeningBook.MoveList.class);
		
		digester.addCallMethod("calculon/opening-book/moves/move", "addMove", 2, new Class[] { String.class, Integer.class});
		digester.addCallParam("calculon/opening-book/moves/move", 0, "pgn");
		digester.addCallParam("calculon/opening-book/moves/move", 1, "count");
		
		digester.addCallMethod("calculon/opening-book/moves/position", "setPosition", 1);
		digester.addCallParam("calculon/opening-book/moves/position", 0);
		
		digester.addSetNext("calculon/opening-book/moves", "addMoveList");

		try {
			log.fine("Creating opening book");
			defaultBook = (OpeningBook) digester.parse(OpeningBook.class.getResourceAsStream("/calculon.xml"));
			return defaultBook;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public void addMoveList(MoveList moveList) {
		book.put(moveList.getPosition(), moveList);
	}
	
	public String getBookMove(BitBoard board) {
		if(board == null) {
			return null;
		}
		
		MoveList moves = book.get(FENUtils.generateWithoutMoveCounts(board));
		if(moves == null) {
			return null;
		}
		return moves.getMove();
	}
	
	public static class MoveList {
		private String position;
		private Map<String, Integer> moves = new HashMap<String, Integer>();
		
		public void setPosition(String position) {
			this.position = position;
		}
		
		public String getPosition() {
			return position;
		}

		public void addMove(String pgn, Integer count) {
			moves.put(pgn, count);
		}
		
		public String getMove() {
			int total = 0;
			for(int i: moves.values()) {
				total += i;
			}
			int moveNum = (int) (Math.random() * total);
			total = 0;
			for(String move: moves.keySet()) { 
				int count = moves.get(move);
				if(moveNum >= total && moveNum < total+count) {
					return move;
				}
				total += count;
			}
			return null;
		}
	}
}
