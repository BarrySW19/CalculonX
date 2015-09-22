package nl.zoidberg.calculon.util;

import java.util.NoSuchElementException;
import java.util.PrimitiveIterator;
import java.util.stream.LongStream;

/**
 * Treats a 64 bit long as an iterable of the bits set to 1. This is slightly slower than doing the
 * iteration directly, but simplifies the code. The iterable itself is immutable and thus thread safe
 * but iterators created from it are not.
 *
 * The individual bits set in the supplied value can be read out via an iterator of a LongStream.
 */
public class BitIterable implements Iterable<Long> {
    private final long value;
    private final long[] bitValues;

    private BitIterable(long value) {
        this.value = value;
        this.bitValues = new long[Long.bitCount(value)];

        for(int i = 0; i < bitValues.length; i++) {
            bitValues[i] = Long.lowestOneBit(value);
            value ^= bitValues[i];
        }
    }

    public BitIterable(long[] bitValues) {
        this.bitValues = new long[bitValues.length];
        System.arraycopy(bitValues, 0, this.bitValues, 0, bitValues.length);
        long value = 0;
        for(long l: this.bitValues) {
            value |= l;
        }
        this.value = value;
    }

    public static BitIterable of(long val) {
        return new BitIterable(val);
    }

    public static BitIterable of(long[] bitValues) {
        return new BitIterable(bitValues);
    }

    public long getValue() {
        return value;
    }

    public LongStream longStream() {
        return LongStream.of(bitValues);
    }

    @Override
    public PrimitiveIterator.OfLong iterator() {
        return new BitIterator();
    }

    private class BitIterator implements PrimitiveIterator.OfLong {
        private int idx = 0;

        private BitIterator() { }

        @Override
        public long nextLong() {
            if( ! hasNext()) {
                throw new NoSuchElementException();
            }

            return bitValues[idx++];
        }

        @Override
        public boolean hasNext() {
            return idx < bitValues.length;
        }
    }
}
