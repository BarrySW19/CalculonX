package nl.zoidberg.calculon.icc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MovesRecorder implements Lv1BlockHandler.Lv1Listener {

    private static final Logger LOG = LoggerFactory.getLogger(Lv1BlockHandler.class);

    public static final Pattern MOVES_PATTERN = Pattern.compile(
            "(\\d+)\\.\\s+(\\S+)\\s+\\(\\S+\\)\\s+(\\S+)\\s+\\(");

    public MovesRecorder() {
    }

    @Override
    public List<String> accept(String s) {
        if( !"150".equals(s.split(" ")[0])) {
             return Collections.emptyList();
        }

        LOG.debug("Processing Moves");
        Matcher matcher = MOVES_PATTERN.matcher(s);
        while(matcher.find() && Integer.parseInt(matcher.group(1)) <= 12) {
            LOG.debug("Move: {} {} {}", matcher.group(1), matcher.group(2), matcher.group(3) );
        }

        return Collections.emptyList();
    }
}
