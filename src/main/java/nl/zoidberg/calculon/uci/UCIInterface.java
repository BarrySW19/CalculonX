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
package nl.zoidberg.calculon.uci;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import nl.zoidberg.calculon.engine.BitBoard;
import nl.zoidberg.calculon.engine.ChessEngine;
import nl.zoidberg.calculon.notation.FENUtils;
import nl.zoidberg.calculon.notation.PGNUtils;

import org.apache.commons.lang.StringUtils;

public class UCIInterface {
	private static Logger log = Logger.getLogger(UCIInterface.class.getName());
	
	private Map<String, Command> commands = new HashMap<String, Command>();
	private PrintStream out;
	private volatile boolean terminate = false;
//	private boolean debug = false;
	
	private BitBoard board = new BitBoard();
	
	public static void main(String[] args) {
		UCIInterface uciInterface = new UCIInterface();
		
		try {
			uciInterface.startInterface();
		} catch (Exception x) {
			log.log(Level.SEVERE, "UCI Error", x);
		}
	}
	
	private UCIInterface() {
		out = System.out;
		commands.put("uci", 		new CommandUCI());
		commands.put("isready", 	new CommandIsReady());
		commands.put("debug", 		new CommandDebug());
		commands.put("setoption", 	new CommandSetOption());
		commands.put("register", 	new CommandRegister());
		commands.put("ucinewgame", 	new CommandUciNewGame());
		commands.put("position", 	new CommandPosition());
		commands.put("go", 			new CommandGo());
		commands.put("stop", 		new CommandStop());
		commands.put("ponderhit", 	new CommandPonderhit());
		commands.put("quit", 		new CommandQuit());
	}

	private void startInterface() throws IOException {
		BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
		String command;
		while((command = input.readLine()) != null) {
			log.fine("UCI(in): '" + command + "'");
			List<String> splitCommand = new ArrayList<String>(Arrays.asList(StringUtils.split(command)));
			if(splitCommand.size() == 0) {
				log.warning("Empty command received from interface");
				continue;
			}
			Command exec = commands.get(splitCommand.get(0));
			if(exec == null) {
				log.warning("Unknown/unsupported command: " + command);
				continue;
			}
			splitCommand.remove(0);
			exec.execute(splitCommand);
			if(terminate) {
				break;
			}
		}
	}
	
	void send(String s) {
		out.println(s);
		log.fine("UCI(out): '" + s + "'");
	}

	public void terminate() {
		this.terminate = true;
	}

	public void setDebug(boolean debug) {
//		this.debug = debug;
	}
	
	public static Logger getLog() {
		return log;
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

	private class CommandUciNewGame implements Command {

		public void execute(List<String> args) {
			// Not implemented
		}
	}

	private class CommandStop implements Command {

		public void execute(List<String> args) {
			// Not implemented
		}
	}

	private class CommandRegister implements Command {

		public void execute(List<String> args) {
			// Not required
		}
	}

	private class CommandPosition implements Command {

		public void execute(List<String> args) {
			if(args.size() < 1) {
				UCIInterface.log.info("Bad 'position' command");
				return;
			}
			
			if("startpos".equals(args.get(0))) {
				UCIInterface.this.board.initialise();
				args.remove(0);
			} else if("fen".equals(args.get(0))) {
				String fen = new StringBuilder()
					.append(args.get(1)).append(" ")
					.append(args.get(2)).append(" ")
					.append(args.get(3)).append(" ")
					.append(args.get(4)).append(" ")
					.append(args.get(5)).append(" ")
					.append(args.get(6))
					.toString();
				
				FENUtils.loadPosition(fen, UCIInterface.this.board);
				args.remove(0);
				args.remove(0);
				args.remove(0);
				args.remove(0);
				args.remove(0);
				args.remove(0);
				args.remove(0);
			}
			if(args.size() == 0) {
				return;
			}
			if("moves".equals(args.get(0))) {
				args.remove(0);
				while(args.size() > 0) {
					if( ! "...".equals(args.get(0))) {
						board.makeMove(board.getMove(args.get(0).toUpperCase()));
					}
					args.remove(0);
				}
			}
			log.fine("Position: " + FENUtils.generate(board));
		}
	}

	private class CommandPonderhit implements Command {

		public void execute(List<String> args) {
			// TODO Auto-generated method stub
		}
	}

	private class CommandDebug implements Command {
		
		public void execute(List<String> args) {
			if(args.size() < 1) {
				UCIInterface.log.info("Bad 'debug' command");
				return;
			}
				
			if("on".equals(args.get(0))) {
				UCIInterface.this.setDebug(true);
			}
			if("off".equals(args.get(0))) {
				UCIInterface.this.setDebug(false);
			}
		}
	}

	private class CommandIsReady implements Command {
		
		public void execute(List<String> args) {
			UCIInterface.this.send("readyok");
		}
	}
	
	private class CommandSetOption implements Command {
		
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
