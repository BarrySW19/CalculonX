package barrysw19.calculon.engine;

public class SearchContext implements Comparable<SearchContext> {

    public static enum Status {
        NORMAL, TIMEOUT, ABORT, CHECKMATE
    }

    private int maxDepth;
    private int maxQDepth;
    private int depth;
    private int qDepth;
    private int score;
    private String algebraicMove;
    private Status status = Status.NORMAL;
    // TODO - Erm...
    private BitBoard.BitBoardMove[] moves = new BitBoard.BitBoardMove[20];

    public SearchContext(String algebraicMove) {
        this.algebraicMove = algebraicMove;
    }

    public BitBoard.BitBoardMove[] getMoves() {
        return moves;
    }

    public String getAlgebraicMove() {
        return algebraicMove;
    }

    public void setAlgebraicMove(String algebraicMove) {
        this.algebraicMove = algebraicMove;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getMaxDepth() {
        return maxDepth;
    }

    public int getMaxQDepth() {
        return maxQDepth;
    }

    public SearchContext descend() {
        depth++;
        maxDepth = Math.max(maxDepth, depth);
        return this;
    }

    public SearchContext ascend() {
        depth--;
        return this;
    }

    public SearchContext qDescend() {
        qDepth++;
        maxQDepth = Math.max(maxQDepth, qDepth);
        return this;
    }

    public SearchContext qAscend() {
        qDepth--;
        return this;
    }

    @Override
    public int compareTo(SearchContext o) {
        return new Integer(score).compareTo(o.getScore());
    }

    @Override
    public String toString() {
        return "SearchContext{" +
                "maxDepth=" + maxDepth +
                ", maxQDepth=" + maxQDepth +
                ", depth=" + depth +
                ", qDepth=" + qDepth +
                ", score=" + score +
                ", algebraicMove='" + algebraicMove + '\'' +
                ", status=" + status +
                '}';
    }
}
