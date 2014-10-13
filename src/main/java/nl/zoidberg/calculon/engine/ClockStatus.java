package nl.zoidberg.calculon.engine;

public class ClockStatus {
    private int initialMinutes;
    private int secondIncrement;
    private long msec;

    public int getInitialMinutes() {
        return initialMinutes;
    }

    public void setInitialMinutes(int initialMinutes) {
        this.initialMinutes = initialMinutes;
        msec = initialMinutes * 60 * 1000;
    }

    public int getSecondIncrement() {
        return secondIncrement;
    }

    public void setSecondIncrement(int secondIncrement) {
        this.secondIncrement = secondIncrement;
    }

    public long getMsec() {
        return msec;
    }

    public void setMsec(long msec) {
        this.msec = msec;
    }

    public int getSecondsForMoves(int moves) {
        int sec = (int) (this.msec / 1000);
        sec += (moves * secondIncrement);
        return sec;
    }

    @Override
    public String toString() {
        String clock = String.format("%02d:%02d", (msec/1000) / 60, (msec/1000) % 60);
        return "ClockStatus[" + clock +"] {" +
                "initialMinutes=" + initialMinutes +
                ", secondIncrement=" + secondIncrement +
                ", msec=" + msec +
                '}';
    }
}
