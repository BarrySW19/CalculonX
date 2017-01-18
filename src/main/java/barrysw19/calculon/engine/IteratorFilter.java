package barrysw19.calculon.engine;

import java.util.Iterator;
import java.util.function.Predicate;

public class IteratorFilter extends AbstractMoveIterator {
    private final Iterator<BitBoard.BitBoardMove> iterator;
    private final Predicate<BitBoard.BitBoardMove> filter;

    public IteratorFilter(Iterator<BitBoard.BitBoardMove> iterator, Predicate<BitBoard.BitBoardMove> filter) {
        this.iterator = iterator;
        this.filter = filter;
    }

    @Override
    BitBoard.BitBoardMove fetchNextMove() {
        while(iterator.hasNext()) {
            BitBoard.BitBoardMove move = iterator.next();
            if(filter.test(move)) {
                return move;
            }
        }
        return null;
    }
}