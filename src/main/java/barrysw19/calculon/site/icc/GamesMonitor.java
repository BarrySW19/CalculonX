package barrysw19.calculon.site.icc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GamesMonitor implements Lv1BlockHandler.Lv1Listener {
    private static final Logger LOG = LoggerFactory.getLogger(Lv1BlockHandler.class);

    public static final Pattern GAME_PATTERN = Pattern.compile(
            "(\\d+)\\s+(\\d+)\\s+(\\S+)\\s+(\\d+)\\s+(\\S+)\\s+[FJbs]r\\s+\\d+\\s+\\d+\\s+[WB]:\\s+(\\d+)");

    private Set<Integer> gamesList = new HashSet<>();

    @Override
    public List<String> accept(String s) {
        if( !"155".equals(s.split(" ")[0])) {
             return Collections.emptyList();
        }

        List<String> commands = new ArrayList<>();
        Matcher matcher = GAME_PATTERN.matcher(s);
        Set<Integer> currentSet = new HashSet<>();
        while(matcher.find()) {
            currentSet.add(Integer.parseInt(matcher.group(1)));
            int wRating = Integer.parseInt(matcher.group(2));
            int bRating = Integer.parseInt(matcher.group(4));
            int moveCount = Integer.parseInt(matcher.group(6));
            int gameId = Integer.parseInt(matcher.group(1));
            if(wRating < 2000 || bRating < 2000 || moveCount < 12 || gamesList.contains(gameId)) {
                continue;
            }
            LOG.debug("grab game {}", matcher.group(1));
            commands.add("moves " + matcher.group(1));
        }
        gamesList = currentSet;
        return commands;
    }
}
