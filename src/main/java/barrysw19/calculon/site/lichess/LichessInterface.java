package barrysw19.calculon.site.lichess;

import barrysw19.calculon.engine.BitBoard;
import barrysw19.calculon.engine.ChessEngine;
import barrysw19.calculon.engine.SearchContext;
import barrysw19.calculon.model.Piece;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.google.common.io.Resources;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LichessInterface {
    private static final Logger LOG = LoggerFactory.getLogger(LichessInterface.class);
    private static final String URI_ROOT = "https://lichess.org/api";
    private static final List<String> BOTS = List.of(
            "maia5", "maia9", "ChessChildren",
            "Demolito_L4", "Demolito_L5", "Demolito_L6"
    );

    private final ExecutorService executors = Executors.newCachedThreadPool();
    private final ScheduledExecutorService cronExecutor = Executors.newScheduledThreadPool(5);

    private final ConcurrentHashMap<String, Event> gamesInProgress = new ConcurrentHashMap<>();
    private HttpClient httpClient;
    private String oauthToken;

    private final ObjectReader reader = new ObjectMapper().readerFor(Event.class);

    public static void main(String[] args) throws IOException, InterruptedException {
        new LichessInterface().execute();
    }

    private void execute() throws IOException, InterruptedException {
        oauthToken = Resources.toString(Resources.getResource("oauth.txt"), StandardCharsets.UTF_8);
        httpClient = HttpClient.newHttpClient();

        cronExecutor.scheduleAtFixedRate(this::issueChallenges, 5, 60, TimeUnit.SECONDS);
        final HttpRequest httpRequest = requestBuilder(URI.create(URI_ROOT + "/stream/event")).build();
        final HttpResponse<Stream<String>> response =
                httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofLines());
        try(final Stream<String> lines = response.body()) {
            lines.forEach(this::handleEvent);
        }
    }

    private void issueChallenges() {
        LOG.info("Ongoing: {}", gamesInProgress.size());
        if(gamesInProgress.size() >= 2) {
            return;
        }

        final String botName = BOTS.get(new Random().nextInt(BOTS.size()));
        final Map<String, String> params = Map.of(
                "rated", "true",
                "clock.limit", "60",
                "clock.increment", "1",
                "color", "random",
                "variant", "standard",
                "keepAliveStream", "false"
        );
        final String formBody = params.entrySet().stream()
                        .map(e -> URLEncoder.encode(e.getKey(), StandardCharsets.UTF_8) + "="
                        + URLEncoder.encode(e.getValue(), StandardCharsets.UTF_8))
                                .collect(Collectors.joining("&"));
        final HttpRequest request = requestBuilder(
                URI.create(URI_ROOT + "/challenge/" + botName))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(formBody))
                .build();
        try {
            final HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            LOG.info("Sent Challenge: {} {} {}", botName, response.statusCode(), response.body());
        } catch (IOException|InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void handleEvent(final String json) {
        try {
            if(StringUtils.isBlank(json)) {
                LOG.info("Keep-Alive");
                return;
            }
            final Event event = reader.readValue(json);
            LOG.info("Event: {}", event);

            switch (event.getType()) {
                case "challenge" -> challenge(event);
                case "challengeCanceled" -> challengeCanceled(event);
                case "gameStart" -> gameStart(event);
            }
        } catch (final JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private void gameStart(final Event event) {
        LOG.info("Game Start: {}", event);
        executors.submit(() -> gameStarted(event));
    }

    private void gameStarted(final Event event) {
        final URI uri = URI.create(URI_ROOT + "/bot/game/stream/" + event.getGame().getGameId());
        final HttpRequest request = requestBuilder(uri).build();
        try {
            LOG.info("Calling game stream: {}", uri);
            final HttpResponse<Stream<String>> response =
                    httpClient.send(request, HttpResponse.BodyHandlers.ofLines());
            gamesInProgress.put(event.getGame().getGameId(), event);
            final ChessEngine chessEngine = new ChessEngine().setTargetTime(3);
            final byte myColor;
            if(Optional.ofNullable(event.getGame()).map(Game::getMyTurn).orElse(false)) {
                myColor = Piece.WHITE;
                transmitMove(event.getGame().getGameId(),
                        chessEngine.getPreferredMove(new BitBoard().initialise()));
            } else {
                myColor = Piece.BLACK;
            }
            try(final Stream<String> lines = response.body()) {
                lines.forEach(l -> handleGameEvent(l, chessEngine, event.getGame().getGameId(), myColor));
            }
            gamesInProgress.remove(event.getGame().getGameId());
            LOG.info("Game stream ended: {}, active remaining: {}",
                    event.getGame().getGameId(), gamesInProgress.size());
        } catch (IOException|InterruptedException e) {
            LOG.error("Error on game stream", e);
            throw new RuntimeException(e);
        } catch (RuntimeException x) {
            LOG.error("Exception on game stream", x);
            gameStarted(event);
            throw x;
        }
    }

    private void handleGameEvent(String json, ChessEngine engine, String gameId, byte myColor) {
        if(StringUtils.isBlank(json)) {
            LOG.info("Keep-Alive (game)");
            return;
        }
        try {
            final Event event = reader.readValue(json);
            LOG.info("Game event: {}", json);
            if("gameState".equals(event.getType()) && "started".equals(event.getStatus())) {
                final int remainingTime;
                if(myColor == Piece.WHITE) {
                    remainingTime = (event.getWtime() + (40 * event.getWinc())) / 1000;
                } else {
                    remainingTime = (event.getBtime() + (40 * event.getBinc())) / 1000;
                }
                final BitBoard bitBoard = new BitBoard().initialise().makeMoves(event.getMoves());
                if(bitBoard.getPlayer() == myColor) {
                    final int targetTime = Math.max(1, remainingTime / 40);
                    LOG.info("Target time: {}", targetTime);
                    engine.setTargetTime(targetTime);
                    final SearchContext move = engine.getPreferredMoveContext(bitBoard);
                    if(move != null) {
                        transmitMove(gameId, convertForLichess(move.getAlgebraicMove(), bitBoard));
                    }
                }
            }
        } catch (JsonProcessingException e) {
            LOG.error("Error handling game event", e);
            throw new RuntimeException(e);
        }
    }

    private String convertForLichess(String move, BitBoard bitBoard) {
        return switch (move) {
            case "O-O" -> bitBoard.getPlayer() == Piece.WHITE ? "e1g1" : "e8g8";
            case "O-O-O" -> bitBoard.getPlayer() == Piece.WHITE ? "e1c1" : "e8c8";
            default -> move.toLowerCase();
        };
    }

    private void transmitMove(final String gameId, final String myMove) {
        final URI uri =
                URI.create(URI_ROOT + "/bot/game/" + gameId + "/move/" + myMove.toLowerCase());
        final HttpRequest request = requestBuilder(uri).POST(HttpRequest.BodyPublishers.noBody()).build();
        final HttpResponse<Void> response;
        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.discarding());
        } catch (IOException|InterruptedException e) {
            throw new RuntimeException(e);
        }
        LOG.info("Sent move {}, response: {}", myMove, response.statusCode());
    }

    private void challengeCanceled(final Event event) {
        LOG.info("Challenge Cancelled: {}", event);
    }

    private void challenge(final Event event) {
        LOG.info("Challenge: {}", event);
        final String responseType;
        if("calculonx".equals(event.getChallenge().getChallenger().getId())) {
            LOG.info("Ignoring own challenge");
            return;
        }

        if ((event.getChallenge().getRated() && !"BOT".equals(event.getChallenge().getChallenger().getTitle())) ||
                !"standard".equals(event.getChallenge().getVariant().getKey())) {
//                || !"rockwomble".equals(event.getChallenge().getChallenger().getId())) {
            responseType = "/decline";
        } else {
            responseType = "/accept";
        }
        LOG.info("Challenge: {}", responseType);
        final URI acceptUri = URI.create(URI_ROOT + "/challenge/" + event.getChallenge().getId() + responseType);
        final HttpRequest request = requestBuilder(acceptUri).POST(HttpRequest.BodyPublishers.noBody()).build();
        try {
            HttpResponse<Void> response = httpClient.send(request, HttpResponse.BodyHandlers.discarding());
            LOG.info("Challenge response: {}", response.statusCode());
        } catch (IOException|InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private HttpRequest.Builder requestBuilder(final URI uri) {
        return HttpRequest.newBuilder()
                .uri(uri)
                .header("Authorization", "Bearer " + oauthToken);
    }
}
