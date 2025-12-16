/**
 * Calculon - A Java chess-engine.
 * <p>
 * Copyright (C) 2008-2009 Barry Smith
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 *      http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package barrysw19.calculon.uci;

import barrysw19.calculon.engine.BitBoard;
import barrysw19.calculon.engine.ChessEngine;
import barrysw19.calculon.notation.FENUtils;
import barrysw19.calculon.notation.PGNUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.*;

@Slf4j
public class UCIInterface {
	private final Map<String, Command> commands = new HashMap<>();
	private final PrintStream out;
	private volatile boolean terminate = false;
//	private boolean debug = false;
	
	private final BitBoard board = new BitBoard();
	
	static void main() {
		UCIInterface uciInterface = new UCIInterface();
		
		try {
			uciInterface.startInterface();
		} catch (Exception x) {
			log.error("UCI Error", x);
		}
	}
	
	private UCIInterface() {
		out = System.out;
		commands.put("uci", 		new CommandUCI());
		commands.put("isready", 	new CommandIsReady());
		commands.put("debug", 		new CommandDebug());
		commands.put("setoption", new CommandSetOption());
		commands.put("register", new CommandRegister());
		commands.put("ucinewgame", new CommandUciNewGame());
		commands.put("position", 	new CommandPosition());
		commands.put("go", 			new CommandGo());
		commands.put("stop", new CommandStop());
		commands.put("ponderhit", new CommandPonderhit());
		commands.put("quit", 		new CommandQuit());
	}

	private void startInterface() throws IOException {
		BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
		String command;
		while((command = input.readLine()) != null) {
			log.info("UCI(in): '{}'", command);
			List<String> splitCommand = new ArrayList<>(Arrays.asList(StringUtils.split(command)));
			if(splitCommand.isEmpty()) {
				log.warn("Empty command received from interface");
				continue;
			}
			Command exec = commands.get(splitCommand.getFirst());
			if(exec == null) {
				log.warn("Unknown/unsupported command: {}", command);
				continue;
			}
			splitCommand.removeFirst();
			exec.execute(splitCommand);
			if(terminate) {
				break;
			}
		}
	}
	
	void send(String s) {
		out.println(s);
		log.info("UCI(out): '{}'", s);
	}

	public void terminate() {
		this.terminate = true;
	}

	public void setDebug(boolean debug) {
//		this.debug = debug;
	}

    // ------------------------------------- Commands ---------------------------

	private class CommandGo implements Command {

		public void execute(List<String> args) {
            ChessEngine node = new ChessEngine();
            
            String bestMove = node.getPreferredMove(board);
            String pgnMove = PGNUtils.translateMove(board, bestMove);
            send("info pv " + pgnMove);
            send("bestmove " + pgnMove);
		}
	}

	private class CommandQuit implements Command {
		
		public void execute(List<String> args) {
			UCIInterface.this.terminate();
		}
	}

	private static class CommandUciNewGame implements Command {

		public void execute(List<String> args) {
			// Not implemented
		}
	}

	private static class CommandStop implements Command {

		public void execute(List<String> args) {
			// Not implemented
		}
	}

	private static class CommandRegister implements Command {

		public void execute(List<String> args) {
			// Not required
		}
	}

	private class CommandPosition implements Command {

		public void execute(List<String> args) {
			if(args.isEmpty()) {
				UCIInterface.log.info("Bad 'position' command");
				return;
			}
			
			if("startpos".equals(args.get(0))) {
				UCIInterface.this.board.initialise();
				args.removeFirst();
			} else if("fen".equals(args.get(0))) {
				String fen = args.get(1) + " " +
                        args.get(2) + " " +
                        args.get(3) + " " +
                        args.get(4) + " " +
                        args.get(5) + " " +
                        args.get(6);
				
				FENUtils.loadPosition(fen, UCIInterface.this.board);
				args.removeFirst();
				args.removeFirst();
				args.removeFirst();
				args.removeFirst();
				args.removeFirst();
				args.removeFirst();
				args.removeFirst();
			}
			if(args.isEmpty()) {
				return;
			}
			if("moves".equals(args.get(0))) {
				args.removeFirst();
				while(!args.isEmpty()) {
					if( ! "...".equals(args.getFirst())) {
						board.makeMove(board.getMove(args.getFirst().toUpperCase()));
					}
					args.removeFirst();
				}
			}
			log.info("Position: {}", FENUtils.generate(board));
		}
	}

	private static class CommandPonderhit implements Command {

		public void execute(List<String> args) {
			// TODO Auto-generated method stub
		}
	}

	private class CommandDebug implements Command {
		
		public void execute(List<String> args) {
			if(args.isEmpty()) {
				UCIInterface.log.info("Bad 'debug' command");
				return;
			}
				
			if("on".equals(args.getFirst())) {
				UCIInterface.this.setDebug(true);
			}
			if("off".equals(args.getFirst())) {
				UCIInterface.this.setDebug(false);
			}
		}
	}

	private class CommandIsReady implements Command {
		
		public void execute(List<String> args) {
			UCIInterface.this.send("readyok");
		}
	}
	
	private static class CommandSetOption implements Command {
		
		public void execute(List<String> args) {
			// Not required
		}
	}

	private class CommandUCI implements Command {
		
		public void execute(List<String>  args) {
			UCIInterface.this.send("id name Calculon 0.1");
			UCIInterface.this.send("id author Vox");
			UCIInterface.this.send("uciok");
		}
	}
}
