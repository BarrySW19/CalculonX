package barrysw19.calculon.engine;

import java.util.Iterator;
import java.util.NoSuchElementException;

public abstract class AbstractMoveIterator implements Iterator<BitBoard.BitBoardMove> {
    protected BitBoard.BitBoardMove nextMove;

    /**
     * This method should either fetch the next available move, or return null if no further moves are available.
     *
     * @return the next move, if one exists - null otherwise.
     */
    protected abstract BitBoard.BitBoardMove fetchNextMove();

    @Override
    public boolean hasNext() {
        if(nextMove != null) {
            return true;
        }
        nextMove = fetchNextMove();
        return nextMove != null;
    }

    @Override
    public BitBoard.BitBoardMove next() {
        if( ! hasNext()) {
            throw new NoSuchElementException();
        }

        BitBoard.BitBoardMove tmpMove = nextMove;
        nextMove = null;
        return tmpMove;
    }
}
