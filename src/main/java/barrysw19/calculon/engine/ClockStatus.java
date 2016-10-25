package barrysw19.calculon.engine;

public class ClockStatus {
    private int initialMinutes;
    private int secondIncrement;
    private long msec;

    public ClockStatus() { }

    public ClockStatus(int initialMinutes, int secondIncrement) {
        setInitialMinutes(initialMinutes);
        setSecondIncrement(secondIncrement);
    }

    public int getInitialMinutes() {
        return initialMinutes;
    }

    public ClockStatus setInitialMinutes(int initialMinutes) {
        this.initialMinutes = initialMinutes;
        msec = initialMinutes * 60 * 1000;
        return this;
    }

    public int getSecondIncrement() {
        return secondIncrement;
    }

    public ClockStatus setSecondIncrement(int secondIncrement) {
        this.secondIncrement = secondIncrement;
        return this;
    }

    public long getMsec() {
        return msec;
    }

    public ClockStatus setMsec(long msec) {
        this.msec = msec;
        return this;
    }

    /**
     * Return an estimate of the time available for the next n moves.
     */
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
