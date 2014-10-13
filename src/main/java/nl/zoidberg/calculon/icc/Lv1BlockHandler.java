package nl.zoidberg.calculon.icc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class Lv1BlockHandler {
    private static final Logger LOG = LoggerFactory.getLogger(Lv1BlockHandler.class);

    private StringBuilder builder = new StringBuilder();
    private boolean inBlock = false;
    private char prevChar = 0;
    private ICCInterface iccInterface;

    private List<Lv1Listener> listeners = new ArrayList<>();

    public Lv1BlockHandler(ICCInterface iccInterface) {
        this.iccInterface = iccInterface;
//        listeners.add(new GamesMonitor());
//        listeners.add(new MovesRecorder());
    }

    public void add(char c) {
        if(c == ('Y'&0x1f)) {
            prevChar = c;
            return;
        }

        if(c == '[' && prevChar == ('Y'&0x1f)) {
            inBlock = true;
            prevChar = c;
            return;
        }

        if(c == ']' && prevChar == ('Y'&0x1f)) {
            inBlock = false;
            fireBlockReceived();
            prevChar = c;
            return;
        }

        if(inBlock) {
            builder.append(c);
        }
        prevChar = c;
    }

    private void fireBlockReceived() {
        LOG.debug("Block1: >" + builder.toString() + "<");
        for(Lv1Listener listener: listeners) {
            for(String command: listener.accept(builder.toString())) {
                iccInterface.send(command);
            }
        }
        builder.setLength(0);
    }

    public static interface Lv1Listener {
        public List<String> accept(String s);
    }
}
