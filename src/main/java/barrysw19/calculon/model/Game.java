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

import barrysw19.calculon.analyzer.*;
import barrysw19.calculon.engine.ChessEngine;
import barrysw19.calculon.engine.BitBoard;

public class Game {
	private BitBoard board;
	private Player whitePlayer, blackPlayer;

	public Game() {
		board = new BitBoard().initialise();
		whitePlayer = new Player();
		blackPlayer = new Player();

//		GameScorer whiteScorer = GameScorer.getDefaultScorer();

		GameScorer scorer1 = GameScorer.getDefaultScorer();
		GameScorer scorer2 = new GameScorer();
		scorer2.addScorer(new MaterialScorer());
		scorer2.addScorer(new BishopPairScorer());
		scorer2.addScorer(new PawnStructureScorer());
		scorer2.addScorer(new KnightScorer());
		scorer2.addScorer(new RookScorer());

		whitePlayer.setGameScorer(scorer1);
		
		// GameScorer blackScorer = new GameScorer();
		// blackScorer.addScorer(new MaterialScorer());
		// blackScorer.addScorer(new BishopPairScorer());
		// blackScorer.addScorer(new PawnStructureScorer());
		// blackScorer.addScorer(new KnightActivityScorer());
		blackPlayer.setGameScorer(scorer2);
	}

	public BitBoard getBoard() {
		return board;
	}

	public Player getWhitePlayer() {
		return whitePlayer;
	}

	public Player getBlackPlayer() {
		return blackPlayer;
	}

	public static void main(String[] args) throws Exception {
		
		try {
			run();
		} catch (Exception x) {
			x.printStackTrace();
		}
	}

	public Player getCurrentPlayer() {
		return board.getPlayer() == Piece.WHITE ? whitePlayer : blackPlayer;
	}

	public static void run() throws Exception {
		Game game = new Game();
//		FENUtils.loadPosition("1rbq2r1/3pkpp1/2n1p2p/1N1n4/1p1P3N/3Q2P1/1PP2PBP/R3R1K1 b - - 1 16", game.getBoard());
//		FENUtils.loadPosition("r1bqk2r/pppp1ppp/2n1p3/b7/2nPQ3/P1N2N2/1PPBPPPP/R3KB1R b KQkq - 4 9", game.getBoard());
//		FENUtils.loadPosition(
//				FENUtils.convertStyle12("<12> -------- -------- -k------ p------- -ppr---- ----p--- ----K--- ------q- B -1 0 0 0 0 1 176 DREZIO CalculonX 2 20 20 0 18 1797 2082 73 K/f3-e2 (0:01) Ke2 0 0 0"),
//				game.getBoard());
		
		for (;;) {
			GameScorer useScorer = game.getBoard().getPlayer() == Piece.WHITE ?
					game.getWhitePlayer().getGameScorer() : game.getBlackPlayer().getGameScorer();
			ChessEngine node = new ChessEngine(useScorer);
			String bestMove = node.getPreferredMove(game.getBoard());
			
			if (bestMove == null) {
				break;
			}
			if (game.getBoard().getHalfMoveCount() >= 100) {
				break;
			}
//			String pgnMove = PGNUtils.translateMove(game.getBoard(), bestMove);
			
			game.getBoard().makeMove(game.getBoard().getMove(bestMove));
//			for(String s: TextUtils.getMiniTextBoard(game.getBoard())) {
//				log.debug(s);
//			}
		}
	}
}
