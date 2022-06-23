package barrysw19.calculon.util;

import org.junit.jupiter.api.Test;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.PrimitiveIterator;

import static org.junit.jupiter.api.Assertions.*;

public class BitIteratorTest {

    @Test
    @SuppressWarnings("unused")
    public void testIterator() {
        int count = 0;
        for(long l: BitIterable.of(0xFF)) {
            count++;
        }
        assertEquals(8, count);
    }

    @Test
    public void testValues() {
        BitIterable bitIterable = BitIterable.of(0b10000000_01000000_00100000_00010000_00001000_00000100_00000010_00000001L);
        Iterator<Long> iterator = bitIterable.iterator();
        assertEquals(0x0000000000000001L, iterator.next().longValue());
        assertEquals(0x0000000000000200L, iterator.next().longValue());
        assertEquals(0x0000000000040000L, iterator.next().longValue());
        assertEquals(0x0000000008000000L, iterator.next().longValue());
        assertEquals(0x0000001000000000L, iterator.next().longValue());
        assertEquals(0x0000200000000000L, iterator.next().longValue());
        assertEquals(0x0040000000000000L, iterator.next().longValue());
        assertEquals(0x8000000000000000L, iterator.next().longValue());
    }

    @Test
    public void testZero() {
        assertThrows(NoSuchElementException.class,
                () -> {
                    BitIterable bitIterable = BitIterable.of(0x1);
                    Iterator<Long> iterator = bitIterable.iterator();
                    assertTrue(iterator.hasNext());

                    assertEquals(1L, iterator.next().longValue());
                    assertFalse(iterator.hasNext());
                    iterator.next();
                });
    }

    @Test
    public void testLongStream() {
        BitIterable bitIterable = BitIterable.of(0b00100101);
        assertEquals(3, bitIterable.longStream().count());
        assertEquals(0b00100000, bitIterable.longStream().max().getAsLong());
        assertEquals(0b00000001, bitIterable.longStream().min().getAsLong());
    }

    @Test
    public void testLongStreamLimits() {
        assertEquals(0, BitIterable.of(0).longStream().count());
        assertEquals(1, BitIterable.of(1).longStream().count());
        assertEquals(1, BitIterable.of(0b10000000_00000000_00000000_00000000_00000000_00000000_00000000_00000000L).longStream().count());
        assertEquals(64, BitIterable.of(0b11111111_11111111_11111111_11111111_11111111_11111111_11111111_11111111L).longStream().count());
    }

    @Test
    public void testAltConstructor() {
        BitIterable bitIterable = BitIterable.of(new long[] { 0b001, 0b100});
        assertEquals(2, bitIterable.longStream().count());
        assertEquals(0b101, bitIterable.getValue());
    }

    @SuppressWarnings("unused")
    public void speedTest1() {
        long t = System.nanoTime();
        for(int i = 0; i < 10_000_000; i++) {
            long l = 0b10101010_10101010_10101010_10101010_10101010_10101010_10101010_10101010L;
            while(l != 0) {
                long v = Long.lowestOneBit(l);
                l ^= v;
                t += 0;
            }
        }
        t = System.nanoTime() - t;
        System.out.println(t / 1_000_000);
    }

    @SuppressWarnings("unused")
    public void speedTest2() {
        long t = System.nanoTime();
        for(int i = 0; i < 10_000_000; i++) {
            for(Long x: BitIterable.of(0b10101010_10101010_10101010_10101010_10101010_10101010_10101010_10101010L)) {
                t += 0;
            }
        }
        t = System.nanoTime() - t;
        System.out.println(t / 1_000_000);
    }

    @SuppressWarnings("unused")
    public void speedTest3() {
        long t = System.nanoTime();
        BitIterable bitIterable = BitIterable.of(0b10101010_10101010_10101010_10101010_10101010_10101010_10101010_10101010L);
        for(int i = 0; i < 10_000_000; i++) {
            for(PrimitiveIterator.OfLong iter = bitIterable.iterator(); iter.hasNext(); ) {
                iter.nextLong();
                t += 0;
            }
        }
        t = System.nanoTime() - t;
        System.out.println(t / 1_000_000);
    }

    @SuppressWarnings("unused")
    public void speedTest4() {
        long t = System.nanoTime();
        BitIterable bitIterable = BitIterable.of(0b10101010_10101010_10101010_10101010_10101010_10101010_10101010_10101010L);
        for(int i = 0; i < 10_000_000; i++) {
            bitIterable.longStream().forEach((z) -> { });
        }
        t = System.nanoTime() - t;
        System.out.println(t / 1_000_000);
    }
}
