package barrysw19.calculon.site.icc;

import barrysw19.calculon.site.icc.ResponseBlockLv2;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ResponseBlockLv2Test {

    @Test
    public void testTokenize() {
        ResponseBlockLv2 blockLv2 = ResponseBlockLv2.createResponseBlock(
                "15 1174 BarryNL CalculonX 0 Standard 0 60 60 60 60 1 \u0019{\u0019} 1457 1754 1634939971 {} {C} 0 0 0 {} 0");
        List<String> tokens = Arrays.asList(blockLv2.tokenize());
        assertEquals("15", tokens.get(0));
        assertEquals("1174", tokens.get(1));
        assertEquals("BarryNL", tokens.get(2));
        assertEquals("1", tokens.get(11));
        assertEquals("", tokens.get(12));
        assertEquals("", tokens.get(16));
        assertEquals("C", tokens.get(17));
        assertEquals("0", tokens.get(22));
        assertEquals(23, tokens.size());
    }
}
