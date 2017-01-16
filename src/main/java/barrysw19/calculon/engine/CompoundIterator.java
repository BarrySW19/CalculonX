package barrysw19.calculon.engine;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class CompoundIterator<T> implements Iterator<T> {
    private final Iterator<T>[] iterators;
    private int index = 0;

    public CompoundIterator(Iterator<T>... iterators) {
        this.iterators = iterators;
    }

    @Override
    public boolean hasNext() {
        return iterators.length > 0 && (iterators[index].hasNext() || (++index < iterators.length && hasNext()));
    }

    @Override
    public T next() {
        if( ! hasNext()) {
            throw new NoSuchElementException();
        }

        return iterators[index].next();
    }
}
