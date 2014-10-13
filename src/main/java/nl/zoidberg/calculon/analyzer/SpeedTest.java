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
import nl.zoidberg.calculon.engine.BitBoard.BitBoardMove;
import nl.zoidberg.calculon.engine.ChessEngine;
import nl.zoidberg.calculon.engine.MoveGeneratorImpl;
import nl.zoidberg.calculon.notation.FENUtils;
import nl.zoidberg.calculon.opening.OpeningBook;

import java.util.List;

public class SpeedTest {

//	Bullet          1422         22    20     3    45   1536 (11-May-2009)
//	Blitz           1237        448   287    67   802   1387 (10-May-2009)
//	Standard        1270         95    91    13   199   1574 (06-May-2009)
	
	public static void main(String[] args) throws Exception {
		OpeningBook.setUseOpeningBook(false);
		
		BitBoard board = new BitBoard();
		FENUtils.loadPosition("1rbq2r1/3pkpp1/2n1p2p/1N1n4/1p1P3N/3Q2P1/1PP2PBP/R3R1K1 b - - 1 16", board);
		ChessEngine engine = new ChessEngine();

		long pre = System.currentTimeMillis();
		engine.getPreferredMove(board);
        System.out.println("Time (search): " + (System.currentTimeMillis() - pre) + " ms");

        pre = System.currentTimeMillis();
		engine.getPreferredMove(board);
        System.out.println("Time (search2): " + (System.currentTimeMillis() - pre) + " ms");

        pre = System.currentTimeMillis();
		engine = new ChessEngine();
		engine.getPreferredMove(board);
        System.out.println("Time (search3): " + (System.currentTimeMillis() - pre) + " ms");

        pre = System.currentTimeMillis();
		engine = new ChessEngine();
		engine.getPreferredMove(board);
        System.out.println("Time (search4): " + (System.currentTimeMillis() - pre) + " ms");

		pre = System.currentTimeMillis();
        RunningCount rc = new RunningCount();
		generateToDepth(5, board, rc);
        System.out.print("Time (perft): " + (System.currentTimeMillis() - pre) + " ms" 
        		+ "  [" + (115196793000L / (System.currentTimeMillis() - pre)) + " nodes/sec]");
        System.out.println(", status = " + (rc.count == 115196793 ? "OK" : "Error! " + rc.count));
	}

	private static void generateToDepth(int depth, BitBoard board, RunningCount runningCount) {
		if(depth == 1) {
			List<BitBoardMove> allMoves = new MoveGeneratorImpl(board).getAllRemainingMoves();
			runningCount.count += allMoves.size();
			return;
		}
		for(BitBoardMove move: new MoveGeneratorImpl(board).getAllRemainingMoves()) {
			board.makeMove(move);
			generateToDepth(depth-1, board, runningCount);
			board.unmakeMove();
		}
	}

	private static class RunningCount {
		private long count = 0;
		
		public void reset() {
			count = 0;
		}
	}
}
