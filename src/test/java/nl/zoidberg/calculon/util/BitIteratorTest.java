package nl.zoidberg.calculon.util;

import org.junit.Test;

import java.util.Iterator;
import java.util.NoSuchElementException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class BitIteratorTest {

    @Test
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

    @Test(expected = NoSuchElementException.class)
    public void testZero() {
        BitIterable bitIterable = BitIterable.of(0x1);
        Iterator<Long> iterator = bitIterable.iterator();
        assertTrue(iterator.hasNext());

        assertEquals(1L, iterator.next().longValue());
        assertFalse(iterator.hasNext());
        iterator.next();
    }
}
