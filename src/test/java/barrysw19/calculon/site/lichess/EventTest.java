package barrysw19.calculon.site.lichess;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.google.common.io.Resources;
import com.google.common.reflect.ClassPath;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

class EventTest {

    @Test
    void testLoadEvent() throws IOException {
        final String json =
                Resources.toString(Resources.getResource("lc_challenge.json"), StandardCharsets.UTF_8);
        final ObjectReader reader = new ObjectMapper().readerFor(Event.class);
        final Event event = reader.readValue(json);
        System.out.println(event);
    }
}