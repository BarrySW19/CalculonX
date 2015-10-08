package barrysw19.calculon.util;

import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoggerFactory {
    public static Logger getLogger() {
        Logger log = Logger.getLogger("LOG");
        log.setLevel(Level.FINE);
        Handler handler = new ConsoleHandler();
        handler.setLevel(Level.FINE);
        handler.setFormatter(new LogFormatter());

        for (Handler h : log.getHandlers()) {
            log.removeHandler(h);
        }
        log.addHandler(handler);
        return log;
    }
}
