package barrysw19.calculon.engine;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;

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
    private TreeModel treeModel;
    private DefaultMutableTreeNode currentNode;
    private final BitBoard initialBoard;

    private BitBoard.BitBoardMove[] moves = new BitBoard.BitBoardMove[20];

    public SearchContext(String algebraicMove, BitBoard bitBoard) {
        this.algebraicMove = algebraicMove;
        this.initialBoard = bitBoard;
    }

    public TreeModel getTreeModel() {
        return treeModel;
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

    public SearchContext descend(BitBoard.BitBoardMove move) {
        descendNode(move);
        depth++;
        maxDepth = Math.max(maxDepth, depth);
        return this;
    }

    public SearchContext ascend() {
        currentNode = (DefaultMutableTreeNode) currentNode.getParent();
        depth--;
        return this;
    }

    private void descendNode(BitBoard.BitBoardMove move) {
        if(treeModel == null) {
            currentNode = new DefaultMutableTreeNode(new SearchNode(move));
            treeModel = new DefaultTreeModel(currentNode);
        } else {
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(new SearchNode(move));
            currentNode.add(node);
            currentNode = node;
        }
    }

    public void addInfo(String s) {
        SearchNode node = ((SearchNode)currentNode.getUserObject());
        node.setText(node.getText() + " " + s);
    }

    public SearchContext qDescend(BitBoard.BitBoardMove move) {
        descendNode(move);
        qDepth++;
        maxQDepth = Math.max(maxQDepth, qDepth);
        return this;
    }

    public SearchContext flip() {
        //depth--;
        qDepth++;
        maxQDepth = Math.max(maxQDepth, qDepth);
        return this;
    }

    public SearchContext qAscend() {
        qDepth--;
        if(qDepth > 0) {
            currentNode = (DefaultMutableTreeNode) currentNode.getParent();
        }
        return this;
    }

    public int getQDepth() {
        return qDepth;
    }

    public int getDepth() {
        return depth;
    }

    public BitBoard getInitialBoard() {
        return initialBoard;
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

    public static class SearchNode {
        private final BitBoard.BitBoardMove move;
        private String text;

        public SearchNode(BitBoard.BitBoardMove move) {
            this.move = move;
            this.text = move.toString();
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public BitBoard.BitBoardMove getMove() {
            return move;
        }

        @Override
        public String toString() {
            return text;
        }
    }
}
