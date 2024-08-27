package barrysw19.calculon.site.lichess;

import barrysw19.calculon.engine.BitBoard;
import barrysw19.calculon.engine.ChessEngine;
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
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

public class LichessInterface {
    private static final Logger LOG = LoggerFactory.getLogger(LichessInterface.class);
    private static final String URI_ROOT = "https://lichess.org/api";

    private final ExecutorService executors = Executors.newFixedThreadPool(5);
    private HttpClient httpClient;
    private String oauthToken;

    private final ObjectReader reader = new ObjectMapper().readerFor(Event.class);

    public static void main(String[] args) throws IOException, InterruptedException {
        new LichessInterface().execute();
    }

    private void execute() throws IOException, InterruptedException {
        oauthToken = Resources.toString(Resources.getResource("oauth.txt"), StandardCharsets.UTF_8);
        httpClient = HttpClient.newHttpClient();

        final HttpRequest httpRequest = requestBuilder(URI.create(URI_ROOT + "/stream/event")).build();
        final HttpResponse<Stream<String>> response =
                httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofLines());
        try(final Stream<String> lines = response.body()) {
            lines.forEach(this::handleEvent);
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
            //LOG.info("Game stream response: {}", response.statusCode());
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
            LOG.info("Game stream ended: {}", event.getGame().getGameId());
        } catch (IOException|InterruptedException e) {
            LOG.error("Error on game stream", e);
            throw new RuntimeException(e);
        }
    }

    private void handleGameEvent(String json, ChessEngine engine, String gameId, byte myColor) {
        if(StringUtils.isBlank(json)) {
            LOG.info("Keep-Alive (game)");
            return;
        }
        try {
            final Event event = reader.readValue(json);
            if("gameState".equals(event.getType())) {
                final BitBoard bitBoard = new BitBoard().initialise().makeMoves(event.getMoves());
                if(bitBoard.getPlayer() == myColor) {
                    final String move = engine.getPreferredMove(bitBoard);
                    transmitMove(gameId, convertForLichess(move, bitBoard));
                }
            }
            LOG.info("Game event: {}, myTurn={}", json, event.getGame());
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
        LOG.info("Sending move: {}", myMove);
        final HttpResponse<Void> response;
        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.discarding());
        } catch (IOException|InterruptedException e) {
            throw new RuntimeException(e);
        }
        LOG.info("Move response: {}", response.statusCode());
    }

    private void challengeCanceled(final Event event) {
        LOG.info("Challenge Cancelled: {}", event);
    }

    private void challenge(final Event event) {
        LOG.info("Challenge: {}", event);
        final String responseType;
        if (event.getChallenge().getRated() ||
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
