package nl.zoidberg.calculon.util;

import java.util.NoSuchElementException;
import java.util.PrimitiveIterator;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Treats a 64 bit long as an iterable of the bits set to 1. This is slightly slower than doing the
 * iteration directly, but simplifies the code. The iterable itself is immutable and thus thread safe
 * but iterators created from it are not.
 */
public class BitIterable implements Iterable<Long> {
    private final long val;

    private BitIterable(long val) {
        this.val = val;
    }

    public static BitIterable of(long val) {
        return new BitIterable(val);
    }

    @SuppressWarnings("unused")
    public static Stream<Long> stream(long val) {
        return StreamSupport.stream(new BitIterable(val).spliterator(), false);
    }

    @SuppressWarnings("unused")
    public static Stream<Long> parallelStream(long val) {
        return StreamSupport.stream(new BitIterable(val).spliterator(), true);
    }

    @Override
    public PrimitiveIterator.OfLong iterator() {
        return new BitIterator(val);
    }

    private static class BitIterator implements PrimitiveIterator.OfLong {
        private long iterVal;

        private BitIterator(long iterVal) {
            this.iterVal = iterVal;
        }

        @Override
        public long nextLong() {
            if(iterVal == 0) {
                throw new NoSuchElementException();
            }

            long rv = Long.lowestOneBit(iterVal);
            iterVal ^= rv;
            return rv;
        }

        @Override
        public boolean hasNext() {
            return iterVal != 0;
        }
    }
}
