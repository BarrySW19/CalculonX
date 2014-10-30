package nl.zoidberg.calculon.util;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class BitIterable implements Iterable<Long> {
    private final long val;

    private BitIterable(long val) {
        this.val = val;
    }

    public static BitIterable of(long val) {
        return new BitIterable(val);
    }

    @Override
    public Iterator<Long> iterator() {
        return new BitIterator(val);
    }

    // Not thread safe
    private static class BitIterator implements Iterator<Long> {
        private long iterVal;

        private BitIterator(long iterVal) {
            this.iterVal = iterVal;
        }

        @Override
        public boolean hasNext() {
            return iterVal != 0;
        }

        @Override
        public Long next() {
            if(iterVal == 0) {
                throw new NoSuchElementException();
            }
            long rv = Long.lowestOneBit(iterVal);
            iterVal ^= rv;
            return rv;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
