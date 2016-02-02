/**
 * Calculon - A Java chess-engine.
 * <p/>
 * Copyright (C) 2008-2013 Barry Smith
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package barrysw19.calculon.icc;

import barrysw19.calculon.notation.PGNUtils;
import barrysw19.calculon.engine.BitBoard;
import barrysw19.calculon.engine.ChessEngine;
import barrysw19.calculon.engine.ClockStatus;
import barrysw19.calculon.engine.MoveGeneratorImpl;
import barrysw19.calculon.model.Piece;
import barrysw19.calculon.notation.FENUtils;
import barrysw19.calculon.notation.Style12;
import barrysw19.calculon.opening.OpeningBook;
import org.apache.commons.digester.Digester;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.util.*;

public class ICCInterface {
    private static final Logger LOG = LoggerFactory.getLogger(ICCInterface.class);

    private static final String TALK_RESPONSE = "I'm sorry Dave, I'm afraid I can't do that.";

    private static boolean shutdown = false;
    private static ICCSConfig iccConfig;

    private Socket connection;
    private Thread moveThread = null;
    private List<ConnectionListener> listeners = new ArrayList<>();
    private List<BlockHandler> blockHandlers = new ArrayList<>();
    private PrintStream out;
    private String opponent = null;
    private boolean rated = true;
    private boolean accept = true;
    private boolean alive = true;
    private BitBoard currentBoard;
    private OpeningBook openingBook;
    private Map<Byte, ClockStatus> clocks = new HashMap<>();
    private Lv1BlockHandler lv1BlockHandler = new Lv1BlockHandler(this);

    private volatile int gameNumber = -1;

    public static void main(String[] args) throws Exception {
        if (System.getProperty("calculon.password") == null) {
            LOG.error("password must be specified.");
            System.exit(-1);
        }

        while (!shutdown) {
            try {
                new ICCInterface().connect();
            } catch (Exception x) {
                LOG.error("Error", x);
                try {
                    Thread.sleep(60000);
                } catch (InterruptedException ix) {
                    LOG.error("This doesn't happen", ix);
                }
            }
        }
    }

    private ICCInterface() {
        Digester digester = new Digester();

        digester.addObjectCreate("calculon/icc", ICCSConfig.class);
        digester.addBeanPropertySetter("calculon/icc/operator-name", "operatorName");
        digester.addBeanPropertySetter("calculon/icc/login-name", "loginName");
        digester.addBeanPropertySetter("calculon/icc/accept-min", "acceptMin");
        digester.addBeanPropertySetter("calculon/icc/accept-max", "acceptMax");
        digester.addBeanPropertySetter("calculon/icc/max-rematches", "maxRematches");
        digester.addBeanPropertySetter("calculon/icc/reseek", "reseek");
        digester.addBeanPropertySetter("calculon/icc/formula", "formula");
        digester.addObjectCreate("calculon/icc/default-seeks/seek", ICCSConfig.Seek.class);
        digester.addSetProperties("calculon/icc/default-seeks/seek", "time", "initialTime");
        digester.addSetProperties("calculon/icc/default-seeks/seek", "inc", "increment");
        digester.addSetNext("calculon/icc/default-seeks/seek", "addSeekAd");

        try {
            iccConfig = (ICCSConfig) digester.parse(ClassLoader.getSystemResourceAsStream("calculon.xml"));
        } catch (Exception e) {
            LOG.warn("Config reading failed", e);
            throw new RuntimeException(e);
        }
        LOG.trace(iccConfig.toString());

        openingBook = OpeningBook.getDefaultBook();

        listeners.add(new DebugListener());
        listeners.add(new ChallengeListener());
        listeners.add(new BoardListener());
        listeners.add(new AbortListener());
        listeners.add(new CommandListener());
        listeners.add(new ReseekListener());
        listeners.add(new ChatListener());

        listeners.add(new BlockLv2Listener());

        blockHandlers.add(new GameStartedHandler());
        blockHandlers.add(new GameEndedHandler());
        blockHandlers.add(new ClockUpdateHandler());
    }

    public void connect() throws IOException {
        connection = new Socket("chessclub.com", 23);
        doLogin();
        BufferedReader reader = new BufferedReader(new InputStreamReader(
                connection.getInputStream()));
        out = new PrintStream(connection.getOutputStream());

        send("set level1 1");
        send("set style 12");
        if (!StringUtils.isBlank(iccConfig.getFormula())) {
            send("set formula " + iccConfig.getFormula());
        }
        receiveLevel2(
                DgCommand.DG_MY_GAME_STARTED,
                DgCommand.DG_MY_GAME_RESULT,
                DgCommand.DG_SEND_MOVES,
                DgCommand.DG_MOVE_ALGEBRAIC,
                DgCommand.DG_MOVE_SMITH,
                DgCommand.DG_MOVE_TIME,
                DgCommand.DG_MOVE_CLOCK,
                DgCommand.DG_MSEC);

        setStatus();
        if (iccConfig.isReseek()) {
            reseek();
        }

        Runnable keepAlive = () -> {
            while (alive) {
                send("games *r-e-B-o-M-f-K-w-L-d-z");
                try {
                    Thread.sleep(60000);
                } catch (InterruptedException x) {
                    // Ignore
                }
            }
        };
        Thread keepAliveThread = new Thread(keepAlive);
        keepAliveThread.setDaemon(true);
        keepAliveThread.start();

        StringBuilder line = new StringBuilder();
        int c;
        try {
            while ((c = reader.read()) != -1) {
                lv1BlockHandler.add((char) c);

                // Ignore CTRL-M, CTRL-G
                if (c == ('M' & 0x1F) || c == ('G' & 0x1F)) {
                    continue;
                }
                line.append((char) c);
                if (c == '\n') {
                    fireDataReceived(line.toString());
                    line.setLength(0);
                    continue;
                }
                if (line.length() >= 2 && line.charAt(line.length() - 2) == ('Y' & 0x1F) && line.charAt(line.length() - 1) == ']') {
                    fireDataReceived(line.toString());
                    line.setLength(0);
                }
            }
        } finally {
            alive = false;
            try {
                reader.close();
                out.close();
            } catch (Exception x) {
                // ignore
            }
        }
    }

    private void receiveLevel2(DgCommand... dgCommands) {
        for (DgCommand dgCommand : dgCommands) {
            send("set-2 " + String.valueOf(dgCommand.getCommandNumber()) + " 1");
        }
    }

    private void fireDataReceived(String s) {
        for (ConnectionListener listener : listeners) {
            try {
                listener.message(s);
            } catch (Exception e) {
                LOG.warn("Handler " + listener + " threw exception", e);
            }
        }
    }

    private void doLogin() throws IOException {
        int c;
        String sLogin = "login: ";
        int sptr = 0;
        while ((c = connection.getInputStream().read()) != -1) {
            if (c == sLogin.charAt(sptr)) {
                sptr++;
                if (sptr == sLogin.length()) {
                    LOG.debug("Sending login name");
                    connection.getOutputStream()
                            .write((iccConfig.getLoginName() + "\n").getBytes());
                    break;
                }
            } else {
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
            for (int i = 0; i < 4; i++) {
                try {
                    Thread.sleep(15000);
                } catch (InterruptedException x) {
                    // ignore
                }
                if (gameNumber != -1) {
                    return;
                }
                send("resume");
            }
            for (ICCSConfig.Seek seek : iccConfig.getSeekAds()) {
                send(seek.getCommand());
            }
        };
        new Thread(seeker).start();
    }

    public synchronized void send(String s) {
        LOG.debug(">>> {}", s);
        out.println(s);
    }

    private void tellOp(String s) {
        send("tell " + iccConfig.getOperatorName() + " " + s);
    }

    private void setStatus() {
        if (shutdown) {
            send("set 9 Current Status: Shutting down.");
        } else if (iccConfig.isReseek()) {
            send("set 9 Current Status: Auto (accept " + (accept ? "on" : "off") + ").");
        } else {
            send("set 9 Current Status: Manual (accept " + (accept ? "on" : "off") + ").");
        }
    }

    private interface ConnectionListener {
        void message(String s);
    }

    private interface BlockHandler {
        void processBlock(ResponseBlockLv2 responseBlock);

        boolean accept(ResponseBlockLv2 responseBlock);
    }

    private class DebugListener implements ConnectionListener {
        public void message(String s) {
            LOG.debug("<<< {}", s);
        }
    }

    private class ReseekListener implements ConnectionListener {
        public void message(String s) {
        }
    }

    private class ChallengeListener implements ConnectionListener {
        // e.g. Challenge: BarryNL (2029) CalculonX (2000) rated Blitz 5 0

        public void message(String s) {
            if (s.startsWith("Challenge: ") && !accept) {
                send("decline");
                return;
            }

            if (s.startsWith("Challenge: ") && s.contains(" (adjourned)")) {
                LOG.debug("Accepting adjourned game.");
                send("accept");
                return;
            }

            if (s.startsWith("Challenge: ") && accept) {
                String[] args = StringUtils.split(s);
                int gameLength = Integer.parseInt(args[args.length - 2]) * 60 + Integer.parseInt(args[args.length - 1]) * 40;

                if ("rated".equals(args[args.length - 4])
                        && gameLength >= iccConfig.getAcceptMin() && gameLength <= iccConfig.getAcceptMax()) {
                    LOG.debug("Accepting: '{}' {}s", s, gameLength);
                    send("accept");
                } else {
                    LOG.debug("Rejecting: '{}' {}s", s, gameLength);
                    send("decline");
                }
                return;
            }

            if (s.startsWith("Creating: ")) {
                LOG.info("Starting game: '" + s + "'");
                List<String> fields = Arrays.asList(StringUtils.split(s));
                boolean playingWhite = iccConfig.getLoginName().equals(fields.get(1));
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

            if (s.startsWith(iccConfig.getOperatorName() + " ")) {
                return;
            }

            String[] fields = StringUtils.split(s);
            if (fields.length >= 3 && "tells".equals(fields[1]) && "you:".equals(fields[2])) {
                send("tell " + fields[0] + " " + TALK_RESPONSE);
                saveChat(s);
            }
            if (fields.length >= 3 && "says:".equals(fields[1])) {
                send("say " + TALK_RESPONSE);
                saveChat(s);
            }
        }
    }

    private static void saveChat(String s) {
        try {
            PrintWriter pw = new PrintWriter(new FileWriter("c:/Development/chatlog.txt", true));
            pw.println(s);
            pw.close();
        } catch (IOException e) {
            LOG.error("Error writing chatlog", e);
        }
    }

    private class CommandListener implements ConnectionListener {
        public void message(String s) {
            if (!s.startsWith(iccConfig.getOperatorName() + " tells you: ")) {
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
                iccConfig.setReseek(false);
                accept = false;
                setStatus();
            }

            if ("accept".equals(words.get(3))) {
                if (words.size() > 4 && "on".equals(words.get(4))) {
                    accept = true;
                    shutdown = false;
                } else {
                    accept = false;
                }
                tellOp("accept " + (accept ? "on" : "off"));
                setStatus();
            }

            if (words.size() > 4 && "reseek".equals(words.get(3))) {
                if ("on".equals(words.get(4))) {
                    iccConfig.setReseek(true);
                    shutdown = false;
                } else {
                    iccConfig.setReseek(false);
                }
                tellOp("reseek " + (iccConfig.isReseek() ? "on" : "off"));
                setStatus();
            }
        }
    }

    /**
     * This method listens for board status messages broadcast when each move is made, or a game starts,
     * and makes a move when it's my turn.
     */
    private class BoardListener implements ConnectionListener {
        public void message(String s) {
            if (!s.startsWith("<12> ")) {
                return;
            }

            final Style12 style12 = new Style12(s);
            if (!style12.isMyGame()) {
                return;
            }

            gameNumber = style12.getGameNumber();
            opponent = style12.getOpponentName();
            if (style12.isInitialPosition()) {
                LOG.debug("Creating new board for new game");
                currentBoard = new BitBoard().initialise();
            }

            if (!(style12.getMyRelationToGame() == Style12.REL_ME_TO_MOVE)) {
                return;
            }

            if (style12.isFlagged()) {
                gameNumber = -1;
                currentBoard = null;
                return;
            }

            if (style12.getHalfMoveCount() >= 100) {
                LOG.info("Claiming draw by 50-move rule");
                send("draw");
                return;
            }

            if (currentBoard != null && !"none".equals(style12.getPreviousMovePGN())) {
                try {
                    String applyMove = PGNUtils.toPgnMoveMap(currentBoard).get(style12.getPreviousMovePGN());
                    if (applyMove != null) {
                        currentBoard.makeMove(currentBoard.getMove(applyMove));
                    } else {
                        LOG.warn(String.format("Out of sync board: move %s not possible: %s",
                                style12.getPreviousMovePGN(), FENUtils.generate(currentBoard)));
                        currentBoard = style12.getBoard();
                    }
                } catch (Exception x) {
                    LOG.error("Apply move failed", x);
                }
            }

            if (currentBoard == null || !currentBoard.equalPosition(style12.getBoard())) {
                LOG.warn("Out of sync board detected - resetting! "
                        + (currentBoard == null ? "" : FENUtils.generate(currentBoard)) + " ||| " + FENUtils.generate(style12.getBoard()));
                currentBoard = style12.getBoard();
            }

            if (currentBoard.getRepeatedCount() >= 3) {
                LOG.info("Claiming draw by 3-fold repitition (opp move)");
                send("draw");
                return;
            }

            String bookMove = openingBook.getBookMove(currentBoard);
            if (bookMove != null) {
                PGNUtils.applyMove(currentBoard, bookMove);
                send(bookMove);
                LOG.debug("Using book move: " + bookMove);
                return;
            }

            if (!new MoveGeneratorImpl(currentBoard).hasNext()) {
                LOG.debug("Looks like that game is over!");
                return;
            }

            Runnable moveMaker = () -> {
                BitBoard myBoard = currentBoard;
                ChessEngine engine = new ChessEngine();
                myBoard.getPlayer();
                ClockStatus clockStatus = clocks.get(myBoard.getPlayer());
                if (clockStatus != null) {
                    int moveTime = clockStatus.getSecondsForMoves(20) / 20;
                    int maxNow = (int) (clockStatus.getMsec() / 1000);
                    moveTime = Math.min(moveTime, maxNow);
                    engine.setTargetTime(Math.max(1, moveTime));
                    LOG.info("Set clock " + moveTime);
                } else {
                    LOG.error("No clock status");
                }
                String bestMove = engine.getPreferredMove(myBoard);
                if (bestMove != null) {
                    if (gameNumber != -1) {
                        LOG.info("Moving: " + PGNUtils.translateMove(myBoard, bestMove));
                        if (currentBoard != null) {
                            currentBoard.makeMove(currentBoard.getMove(bestMove));
                        }
                        send(bestMove.toLowerCase());
                        if (currentBoard.getRepeatedCount() >= 3) {
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

    private class BlockLv2Listener implements ConnectionListener {
        private StringBuffer currentBlock = new StringBuffer();

        public void message(String s) {
            for (int i = 0; i < s.length(); i++) {
                if (s.charAt(i) == ('Y' & 0x1F) && s.charAt(i + 1) == '(') {
                    currentBlock.setLength(0);
                }
                currentBlock.append(s.charAt(i));
                if (s.charAt(i) == ')' && s.charAt(i - 1) == ('Y' & 0x1F)) {
                    for (ResponseBlockLv2 block : parseBlockResponseLv2(currentBlock.toString())) {
                        LOG.debug("LV2: {}", block);
                        blockHandlers.stream().filter(handler -> handler.accept(block)).forEach(
                                handler -> handler.processBlock(block));
                    }
                }
            }
            currentBlock.append("\n");
        }
    }

    private List<ResponseBlockLv2> parseBlockResponseLv2(String lvl2String) {
        List<ResponseBlockLv2> rv = new ArrayList<>();

        Stack<StringBuffer> allBlocks = new Stack<>();
        for (int i = 0; i < lvl2String.length(); i++) {
            if (lvl2String.charAt(i) == ('Y' & 0x1F) && lvl2String.charAt(i + 1) == '(') {
                allBlocks.push(new StringBuffer());
                i++;
                continue;
            }
            if (lvl2String.charAt(i) == ('Y' & 0x1F) && lvl2String.charAt(i + 1) == ')') {
                rv.add(ResponseBlockLv2.createResponseBlock(allBlocks.pop().toString()));
                i++;
                continue;
            }
            allBlocks.peek().append(lvl2String.charAt(i));
        }

        LOG.debug(String.valueOf(rv));
        return rv;
    }

    // Level 2 style block handlers

    private class ClockUpdateHandler implements BlockHandler {

        @Override
        public boolean accept(ResponseBlockLv2 responseBlock) {
            return responseBlock.getCommand() == DgCommand.DG_MSEC
                    && Integer.parseInt(responseBlock.tokenize()[1]) == gameNumber;
        }

        @Override
        public void processBlock(ResponseBlockLv2 responseBlock) {
            Lv2TimeUpdate timeUpdate = new Lv2TimeUpdate(responseBlock);
            ClockStatus clockStatus = clocks.get(timeUpdate.getColor());
            clockStatus.setMsec(timeUpdate.getMsec());
            LOG.debug("Clock Set: " + clockStatus);
        }
    }

    private class GameStartedHandler implements BlockHandler {

        @Override
        public boolean accept(ResponseBlockLv2 responseBlock) {
            return responseBlock.getCommand() == DgCommand.DG_MY_GAME_STARTED;
        }

        @Override
        public void processBlock(ResponseBlockLv2 responseBlock) {
            Lv2MyGameStarted lv2MyGameStarted = new Lv2MyGameStarted(responseBlock);
            clocks.put(Piece.WHITE, new ClockStatus());
            clocks.put(Piece.BLACK, new ClockStatus());

            clocks.get(Piece.WHITE).setInitialMinutes(lv2MyGameStarted.getInitialTime(Piece.WHITE));
            clocks.get(Piece.WHITE).setSecondIncrement(lv2MyGameStarted.getIncrement(Piece.WHITE));
            clocks.get(Piece.BLACK).setInitialMinutes(lv2MyGameStarted.getInitialTime(Piece.BLACK));
            clocks.get(Piece.BLACK).setSecondIncrement(lv2MyGameStarted.getIncrement(Piece.BLACK));
            LOG.info("Game starts:" + clocks);
        }
    }

    private class GameEndedHandler implements BlockHandler {

        @Override
        public boolean accept(ResponseBlockLv2 responseBlock) {
            return responseBlock.getCommand() == DgCommand.DG_MY_GAME_RESULT;
        }

        @Override
        public void processBlock(ResponseBlockLv2 responseBlock) {
            LOG.trace("Game ends: [" + responseBlock.getData() + "]");
            currentBoard = null;
            gameNumber = -1;
            opponent = null;

            while (moveThread != null && moveThread.isAlive()) {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException x) {
                    // Ignore
                }
            }

            if (shutdown) {
                send("quit");
            } else if (iccConfig.isReseek()) {
                reseek();
            }
        }
    }
}
