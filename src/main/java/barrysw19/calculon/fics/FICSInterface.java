/*
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
package barrysw19.calculon.fics;

import barrysw19.calculon.engine.BitBoard;
import barrysw19.calculon.engine.ChessEngine;
import barrysw19.calculon.engine.ClockStatus;
import barrysw19.calculon.model.Piece;
import barrysw19.calculon.notation.FENUtils;
import barrysw19.calculon.notation.PGNUtils;
import barrysw19.calculon.notation.Style12;
import barrysw19.calculon.opening.OpeningBook;
import org.apache.commons.digester.Digester;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.*;

public class FICSInterface {

	private static final Logger LOG = LoggerFactory.getLogger(FICSInterface.class);

	private static boolean shutdown = false;
	private final static String talkResponse = "I'm sorry Dave, I'm afraid I can't do that.";
	private static FICSConfig ficsConfig;

	private Socket connection;
	private Thread moveThread = null;
	private List<ConnectionListener> listeners = new ArrayList<>();
	private PrintStream out;
	private String opponent = null;
	private boolean rated = false;
	private int gameNumber = -1;
	private boolean playingWhite = true;
	private boolean accept = true;
	private boolean alive = true;
	private OpeningBook openingBook;
//	private GameScorer currentScorer;
	private BitBoard currentBoard;
	private boolean blockOn = false;
	private int blockCount = 1;
	private Map<Byte, ClockStatus> clocks = new HashMap<>();
	
	public static void main(String[] args) throws Exception {
		
		if(System.getProperty("calculon.password") == null)
		{
			LOG.error("password must be specified.");
			System.exit(-1);
		}
		
		while(!shutdown) {
			try {
				new FICSInterface().connect();
			} catch (Exception x) {
				LOG.error("Error", x);
				try { Thread.sleep(60000); } catch (InterruptedException ignored) { }
			}
		}
	}

	private FICSInterface() {
		Digester digester = new Digester();
		
		digester.addObjectCreate("calculon/fics", FICSConfig.class);
		digester.addBeanPropertySetter("calculon/fics/operator-name", "operatorName");
		digester.addBeanPropertySetter("calculon/fics/login-name", "loginName");
		digester.addBeanPropertySetter("calculon/fics/accept-min", "acceptMin");
		digester.addBeanPropertySetter("calculon/fics/accept-max", "acceptMax");
		digester.addBeanPropertySetter("calculon/fics/max-rematches", "maxRematches");
		digester.addBeanPropertySetter("calculon/fics/reseek", "reseek");
		digester.addObjectCreate("calculon/fics/default-seeks/seek", FICSConfig.Seek.class);
		digester.addSetProperties("calculon/fics/default-seeks/seek", "time", "initialTime");
		digester.addSetProperties("calculon/fics/default-seeks/seek", "inc", "increment");
		digester.addSetNext("calculon/fics/default-seeks/seek", "addSeekAd");
		
		try {
			ficsConfig = (FICSConfig) digester.parse(ClassLoader.getSystemResourceAsStream("calculon.xml"));
		} catch (Exception e) {
			LOG.error("Config reading failed", e);
			throw new RuntimeException(e);
		}
		LOG.debug(ficsConfig.toString());
		
		openingBook = OpeningBook.getDefaultBook();

		listeners.add(new DebugListener());
		listeners.add(new ChallengeListener());
		listeners.add(new BoardListener());
		listeners.add(new AbortListener());
		listeners.add(new CommandListener());
		listeners.add(new GameEndedListener());
		listeners.add(new ReseekListener());
		listeners.add(new ChatListener());
		listeners.add(new BlockListener());

        clocks.put(Piece.WHITE, new ClockStatus());
        clocks.put(Piece.BLACK, new ClockStatus());
	}

	private void connect() throws IOException {
		connection = new Socket("freechess.org", 23);
		doLogin();
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				connection.getInputStream()));
		out = new PrintStream(connection.getOutputStream());

		send("set style 12");
		send("iset movecase 1");
		send("iset block 1");
		blockOn = true;
		
		setStatus();
		if (ficsConfig.isReseek()) {
			reseek();
		}

		Runnable keepAlive = () -> {
            while(alive) {
                send("date");
                try { Thread.sleep(60000 * 15); } catch (InterruptedException ignored) { }
            }
        };
		Thread keepAliveThread = new Thread(keepAlive);
		keepAliveThread.start();

		String line;
		try {
			while ((line = reader.readLine()) != null) {
				if (line.trim().length() == 0) {
                    LOG.debug("Recv: '" + line + "'");
					continue;
				}
                LOG.info("Recv: '" + line + "'");
				for (ConnectionListener listener : listeners) {
					listener.message(line);
				}
			}
		} finally {
			alive = false;
			try {
				reader.close();
				out.close();
			} catch (Exception ignored) { }
		}
	}

	private void doLogin() throws IOException {
		int c;
		String sLogin = "login: ";
		int sptr = 0;
        connection.getOutputStream().write("\n".getBytes());
		while ((c = connection.getInputStream().read()) != -1) {
			if (c == sLogin.charAt(sptr)) {
				sptr++;
				if (sptr == sLogin.length()) {
					LOG.debug("Sending login name");
					connection.getOutputStream()
							.write((ficsConfig.getLoginName() + "\n").getBytes());
					break;
				}
			} else {
				System.out.print((char) c);
				sptr = 0;
			}
		}

		sLogin = "password: ";
		sptr = 0;
		while ((c = connection.getInputStream().read()) != -1) {
			if (c == sLogin.charAt(sptr)) {
				sptr++;
				if (sptr == sLogin.length()) {
					LOG.debug("Sending password");
					connection.getOutputStream().write((System.getProperty("calculon.password") + "\n").getBytes());
					break;
				}
			} else {
				sptr = 0;
			}
		}
	}
	
	private void reseek() {
		send("resume");
		Runnable seeker = () -> {
            for(int i = 0; i < 1; i++) {
                try { Thread.sleep(15000); } catch (InterruptedException ignored) { }
                if(gameNumber != -1) {
                        return;
                }
                send("resume");
            }
            for(FICSConfig.Seek seek: ficsConfig.getSeekAds()) {
                send("seek " + seek.getInitialTime() + " " + seek.getIncrement() + " formula");
            }
        };
		new Thread(seeker).start();
	}

	private synchronized void send(String s) {
		if(blockOn) {
			s = (String.valueOf(blockCount++) + " " + s);
			if(blockCount > 9) {
				blockCount = 1;
			}
		}
		LOG.debug(">>> " + s);
		out.println(s);
	}

	private void tellOp(String s) {
		send("tell " + ficsConfig.getOperatorName() + " " + s);
	}
	
	private void setStatus() {
		if(shutdown) {
			send("set 9 Current Status: Shutting down.");
		} else if (ficsConfig.isReseek()) {
			send("set 9 Current Status: Auto (accept " + (accept?"on":"off") + ").");
		} else {
			send("set 9 Current Status: Manual (accept " + (accept?"on":"off") + ").");
		}
	}

	private interface ConnectionListener {
		void message(String s);
	}

	private class DebugListener implements ConnectionListener {
		public void message(String s) {
			LOG.debug("<<< " + s);
		}
	}

	private class ReseekListener implements ConnectionListener {
		public void message(String s) {
		}
	}

	private class ChallengeListener implements ConnectionListener {
		public void message(String s) {
			if (s.startsWith("Challenge: ") && !accept) {
				send("decline");
				return;
			}
			
			if (s.startsWith("Challenge: ") && accept) {
				String[] args = StringUtils.split(s);
				args[args.length-1] = args[args.length-1].substring(0, args[args.length-1].length()-1);
				int gameLength = Integer.parseInt(args[args.length-2])*60 + Integer.parseInt(args[args.length-1])*40;
				
				if("rated".equals(args[args.length-4]) 
						&& gameLength >= ficsConfig.getAcceptMin() && gameLength <= ficsConfig.getAcceptMax()) {
					LOG.debug("Accepting: '" + s + "' " + gameLength + "s");
					send("accept");
				} else {
					LOG.debug("Rejecting: '" + s + "' " + gameLength + "s");
					send("decline");
				}
				return;
			}
			if (s.startsWith("Creating: ")) {
				LOG.info("Starting game: '" + s + "'");
				List<String> fields = Arrays.asList(StringUtils.split(s));
				playingWhite = ficsConfig.getLoginName().equals(fields.get(1));
				opponent = playingWhite ? fields.get(3) : fields.get(1);
				rated = "rated".equals(fields.get(5));
				send("finger " + opponent);
			}
		}
	}

	private class AbortListener implements ConnectionListener {
		public void message(String s) {
			if (opponent != null
					&& s.startsWith(opponent + " would like to abort the game;")
					&& !rated) {
				send("abort");
			}
		}
	}

	private class ChatListener implements ConnectionListener {
		public void message(String s) {
			
			if(s.startsWith(ficsConfig.getOperatorName() + " ")) {
				return;
			}
			
			String[] fields = StringUtils.split(s);
			if(fields.length >= 3 && "tells".equals(fields[1]) && "you:".equals(fields[2])) {
				send("tell " + fields[0] + " " + talkResponse);
			}
			if(fields.length >= 3 && "says:".equals(fields[1])) {
				send("say " + talkResponse);
			}
		}
	}

	private class CommandListener implements ConnectionListener {
		public void message(String s) {
			if (!s.startsWith(ficsConfig.getOperatorName() + " tells you: ")) {
				return;
			}
			
			List<String> words = Arrays.asList(StringUtils.split(s));
			if (words.size() < 4) {
				return;
			}

			if ("do".equals(words.get(3))) {
				StringBuilder buf = new StringBuilder();
				for (int i = 4; i < words.size(); i++) {
					buf.append(words.get(i)).append(" ");
				}
				send(buf.toString().trim());
				tellOp("sent '" + buf.toString().trim() + "'.");
			}

			if ("shutdown".equals(words.get(3))) {
				tellOp("Will shutdown after current game.");
				shutdown = true;
				ficsConfig.setReseek(false);
				accept = false;
				setStatus();
			}
			
			if ("accept".equals(words.get(3))) {
				if(words.size() > 4 && "on".equals(words.get(4))) {
					accept = true;
					shutdown = false;
				} else {
					accept = false;
				}
				tellOp("accept " + (accept ? "on" : "off"));
				setStatus();
			}
			
			if (words.size() > 4 && "reseek".equals(words.get(3))) {
				if("on".equals(words.get(4))) {
					ficsConfig.setReseek(true);
					shutdown = false;
				} else {
					ficsConfig.setReseek(false);
				}
				tellOp("reseek " + (ficsConfig.isReseek() ? "on" : "off"));
				setStatus();
			}
		}
	}
	
	private class GameEndedListener implements ConnectionListener {
		private String[] PATTERNS = new String[] {
				" resigns}",
				" checkmated}",
				" forfeits on time}",
				" Game aborted on move ",
				" Neither player has mating material}",
				" game aborted}",
				" Game drawn by repetition}",
				" Game drawn by stalemate}",
				" Game drawn by the 50 move rule}",
				" lost connection; game adjourned}",
				" Game drawn because both players ran out of time}",
				" forfeits by disconnection}",
				" Game courtesyaborted by ",
				" Game aborted by mutual agreement} ",
				" has no material to mate} ",
				};

		public void message(String s) {
			StringBuilder buf = new StringBuilder().append("{Game ").append(
					gameNumber).append(" (");
			buf.append(playingWhite ? ficsConfig.getLoginName() : opponent);
			buf.append(" vs. ");
			buf.append(!playingWhite ? ficsConfig.getLoginName() : opponent);
			buf.append(") ");

			String prefix = buf.toString();
			if (!s.startsWith(prefix)) {
				return;
			}

			boolean gameEnded = false;
			for (String ending : PATTERNS) {
				gameEnded |= (s.contains(ending));
			}

			if (gameEnded) {
				LOG.info("Game ends: " + s);
				currentBoard = null;
				gameNumber = -1;
				opponent = null;
				
				while(moveThread != null && moveThread.isAlive()) {
					try { Thread.sleep(200); } catch (InterruptedException ignored) { }
				}
				
				if (shutdown) {
					send("quit");
				} else if (ficsConfig.isReseek()) {
					reseek();
				}
			}
		}
	}

	private class BoardListener implements ConnectionListener {
		public void message(String s) {
			if (!s.startsWith("<12> ")) {
				return;
			}
			
			final Style12 style12 = new Style12(s);
			
			if(style12.isMyGame()) {
				gameNumber = style12.getGameNumber();
				opponent = style12.getOpponentName();
				if(style12.isInitialPosition()) {
					currentBoard = new BitBoard().initialise();
                    clocks.put(Piece.WHITE, new ClockStatus(style12.getTimeInitial(), style12.getTimeIncrement()));
                    clocks.put(Piece.BLACK, new ClockStatus(style12.getTimeInitial(), style12.getTimeIncrement()));
                    LOG.info("Game starts:" + clocks);
				}
                clocks.get(Piece.WHITE).setMsec(style12.getWhiteTime() * 1000);
                clocks.get(Piece.BLACK).setMsec(style12.getBlackTime() * 1000);
			}
			
			if ( ! (style12.getMyRelationToGame() == Style12.REL_ME_TO_MOVE)) {
				return;
			}
			
			if(style12.isFlagged()) {
				gameNumber = -1;
				currentBoard = null;
				return;
			}

			if(style12.getHalfMoveCount() >= 100) {
				LOG.info("Claiming draw by 50-move rule");
				send("draw");
				return;
			}
			
			if(currentBoard != null && !"none".equals(style12.getPreviousMovePGN())) {
				try {
					PGNUtils.applyMove(currentBoard, style12.getPreviousMovePGN());
				} catch (Exception x) {
					LOG.error("Apply move failed: " + currentBoard + " " + style12.getPreviousMovePGN(), x);
				}
			}
			
			if(currentBoard == null || ! currentBoard.getCacheId().equals(style12.getBoard().getCacheId())) {
				LOG.warn("Out of sync board detected - resetting!");
				currentBoard = style12.getBoard();
			}
			
			if(currentBoard.getRepeatedCount() >= 3) {
				LOG.info("Claiming draw by 3-fold repitition (opp move)");
				send("draw");
				return;
			}
			
			String bookMove = openingBook.getBookMove(currentBoard);
			if(bookMove != null) {
				PGNUtils.applyMove(currentBoard, bookMove);
				send(bookMove);
				LOG.debug("Using book move: " + bookMove);
				return;
			}
			
			Runnable moveMaker = () -> {
                final BitBoard myBoard = currentBoard;
                ChessEngine engine = new ChessEngine(3);

                if(!clocks.containsKey(myBoard.getPlayer())) {
                    LOG.warn("No clock present");
                }
                Optional.ofNullable(clocks.get(myBoard.getPlayer())).ifPresent((cs) -> {
                    final int targetTime = cs.getTargetMoveTime();
                    LOG.info("Set clock " + targetTime);
                    engine.setTargetTime(targetTime);
                });

                String bestMove = engine.getPreferredMove(myBoard);
                if(bestMove != null) {
                    if(gameNumber != -1) {
                        LOG.info("Board: " + FENUtils.generate(myBoard));
                        LOG.info("Moving: " + PGNUtils.translateMove(myBoard, bestMove));
                        if(currentBoard != null) {
                            PGNUtils.applyMove(currentBoard, PGNUtils.translateMove(myBoard, bestMove));
                        }
                        send(bestMove.toLowerCase());
                        if(currentBoard.getRepeatedCount() >= 3) {
                            LOG.info("Claiming draw by 3-fold repitition (my move)");
                            send("draw");
                        }
                    } else {
                        LOG.info("Game not active - move aborted");
                    }
                }
                moveThread = null;
            };
			
			moveThread = new Thread(moveMaker);
			moveThread.start();
		}
	}
	
	private class BlockListener implements ConnectionListener {
		private StringBuffer currentBlock = new StringBuffer();
		private boolean inBlock = false;
		
		@Override
		public void message(String s) {
			for(int i = 0; i < s.length(); i++) {
				if(s.charAt(i) == 0x15) {
					inBlock = true;
					currentBlock.setLength(0);
				}
				if(inBlock) {
					currentBlock.append(s.charAt(i));
				}
				if(inBlock && s.charAt(i) == 0x17) {
					inBlock = false;
					new ResponseBlock(currentBlock.toString());
				}
			}
			if(inBlock) {
				currentBlock.append("\n");
			}
		}
	}
	
	@SuppressWarnings("unused")
	private class ResponseBlock {
		private int blockId;
		private int responseCode;
		private String data;
		
		private ResponseBlock(final String s) {
			StringBuilder buf = new StringBuilder(s);
			if(buf.charAt(0) != 0x15) {
				throw new IllegalArgumentException("Data not started with 0x15");
			}
			if(buf.charAt(buf.length()-1) != 0x17) {
				throw new IllegalArgumentException("Data not terminated with 0x17");
			}
			buf.delete(0, 1);
			buf.delete(buf.length()-1, buf.length());
			
			blockId = Integer.parseInt(buf.substring(0, buf.indexOf("\u0016")));
			buf.delete(0, buf.indexOf("\u0016") + 1);

			responseCode = Integer.parseInt(buf.substring(0, buf.indexOf("\u0016")));
			buf.delete(0, buf.indexOf("\u0016") + 1);

			data = buf.toString(); 
		}
		
		public int getBlockId() {
			return blockId;
		}
		public void setBlockId(int blockId) {
			this.blockId = blockId;
		}
		public int getResponseCode() {
			return responseCode;
		}
		public void setResponseCode(int responseCode) {
			this.responseCode = responseCode;
		}
		public String getData() {
			return data;
		}
		public void setData(String data) {
			this.data = data;
		}

		/**
		 * Constructs a <code>String</code> with all attributes
		 * in name = value format.
		 *
		 * @return a <code>String</code> representation 
		 * of this object.
		 */
		public String toString()
		{
		    final String TAB = "    ";

			return "ResponseBlock ( "
                + super.toString() + TAB
                + "blockId = " + this.blockId + TAB
                + "responseCode = " + this.responseCode + TAB
                + "data = " + this.data + TAB
                + " )";
		}
	}
}
