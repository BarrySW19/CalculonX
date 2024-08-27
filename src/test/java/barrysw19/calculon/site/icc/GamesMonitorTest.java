package barrysw19.calculon.site.icc;

import barrysw19.calculon.engine.BitBoard;
import barrysw19.calculon.notation.FENUtils;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import org.junit.jupiter.api.Test;

import javax.ws.rs.core.MediaType;
import java.net.URI;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GamesMonitorTest {

    private static final String data = """
            155 *
            1263 2876 tuechdisaigon(C)     2903 paj(C)                sr 60  15       B: 16
            561 2916 Data(C)              2813 Anesthesia(C)         sr 15   0       W: 15
            1729 1509 mineral              1275 SlowFlo(C)            sr 30  30       W: 28
            702 1346 abi                  1437 marath                sr 20   0       B:  4
            477 1418 babapooja            1344 clevercat13           sr 35  10       W: 17
            1543 1265 sten                 1419 knightstorm           sr 15   0       B: 25
            493 1238 SeniorCitizen        1341 guyd52                sr 45  45       B: 43
            185 1370 Osmin                1202 jacquim               sr 15  12       B: 15
            780 1257 e4d4                 1295 jm0000006             sr 15  15       W: 44
                      47 games displayed (of 504).
            """;
    public static final Pattern GAME_PATTERN = Pattern.compile(
            "(\\d+)\\s+(\\d+)\\s+(\\S+)\\s+(\\d+)\\s+(\\S+)\\s+[bs]r\\s+\\d+\\s+\\d+\\s+[WB]:\\s+(\\d+)");

    @Test
    public void patternTest() {
        Matcher m = GAME_PATTERN.matcher(data);
        while(m.find()) {
            for(int i = 0; i <= m.groupCount(); i++) {
                System.out.println("-"+m.group(i)+"-");
            }
        }
    }

    private static String data2 = "\n" +
            "Hodaka (2064) vs. RoterBaron (2132) --- 2013.12.04 15:52:57 \n" +
            "Rated Blitz match, initial time: 3 minutes, increment: 0 seconds\n" +
            "\n" +
            "Move  Hodaka             RoterBaron      \n" +
            "----  ----------------   ----------------\n" +
            "  1.  e4       (0:01)    e5       (0:01)  \n" +
            "  2.  Nf3      (0:02)    Nc6      (0:01)  \n" +
            "  3.  Nc3      (0:02)    g6       (0:01)  \n" +
            "  4.  Bc4      (0:01)    Bg7      (0:01)  \n" +
            "  5.  d3       (0:01)    Nge7     (0:01)  \n" +
            "  6.  Be3      (0:01)    d6       (0:01)  \n" +
            "  7.  Qd2      (0:01)    h6       (0:03)  \n" +
            "  8.  O-O      (0:03)    Nd4      (0:01)  \n" +
            "  9.  Bxd4     (0:01)    exd4     (0:02)  \n" +
            " 10.  Ne2      (0:01)    c5       (0:02)  \n" +
            " 11.  c3       (0:02)    dxc3     (0:04)  \n" +
            " 12.  bxc3     (0:00)    O-O      (0:03)  \n" +
            " 13.  d4       (0:01)    cxd4     (0:03)  \n" +
            " 14.  cxd4     (0:02)    Nc6      (0:01)  \n" +
            "       {Game in progress} *\n";

    public static final Pattern MOVES_PATTERN = Pattern.compile(
            "(\\d+)\\.\\s+(\\S+)\\s+\\(\\S+\\)\\s+(\\S+)\\s+\\(");

    public static final String SERVER_ROOT_URI = "http://localhost:7474/db/data/";

    // Live server test
    public void patternTest2() {
        WebResource resource = Client.create().resource(SERVER_ROOT_URI + "node");
        ClientResponse response = resource.accept(MediaType.APPLICATION_JSON)
                .type(MediaType.APPLICATION_JSON).entity("{}").post(ClientResponse.class);
        System.out.println(response.getClientResponseStatus());
        URI nodeUri = response.getLocation();
        System.out.println(nodeUri);
        response.close();

        String fen = FENUtils.generateWithoutMoveCounts(new BitBoard().initialise());
        String propertyUri = nodeUri.toString() + "/properties/position";
        WebResource resource2 = Client.create().resource(propertyUri);
        ClientResponse response2 = resource2.accept(MediaType.APPLICATION_JSON)
                .type(MediaType.APPLICATION_JSON).entity("\"" + fen + "\"").put(ClientResponse.class);
        System.out.println(String.format( "PUT to [%s], status code [%d]",
                propertyUri, response2.getStatus() ) );
        response2.close();
    }
}
