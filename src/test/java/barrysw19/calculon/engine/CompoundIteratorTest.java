package barrysw19.calculon.engine;

import com.google.common.collect.Lists;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SuppressWarnings("unchecked")
public class CompoundIteratorTest {

    @Test
    public void testBothPopulated() {
        CompoundIterator<String> iter = new CompoundIterator<>(
                Lists.newArrayList("A", "B").iterator(),
                Lists.newArrayList("C", "D").iterator()
        );

        assertEquals("A", iter.next());
        assertEquals("B", iter.next());
        assertEquals("C", iter.next());
        assertEquals("D", iter.next());
        assertFalse(iter.hasNext());
    }

    @Test
    public void testFirstEmpty() {
        CompoundIterator<String> iter = new CompoundIterator<>(
                new ArrayList().iterator(),
                Lists.newArrayList("C", "D").iterator()
        );

        assertEquals("C", iter.next());
        assertEquals("D", iter.next());
        assertFalse(iter.hasNext());
    }

    @Test
    public void testSecondEmpty() {
        CompoundIterator<String> iter = new CompoundIterator<>(
                Lists.newArrayList("A", "B").iterator(),
                new ArrayList().iterator()
        );

        assertEquals("A", iter.next());
        assertEquals("B", iter.next());
        assertFalse(iter.hasNext());
    }

    @Test
    public void testBothEmpty() {
        CompoundIterator<String> iter = new CompoundIterator<>(
                new ArrayList().iterator(),
                new ArrayList().iterator()
        );

        assertFalse(iter.hasNext());
    }

    @Test
    public void testThree() {
        CompoundIterator<String> iter = new CompoundIterator<>(
                Lists.newArrayList("A").iterator(),
                Lists.newArrayList("B").iterator(),
                Lists.newArrayList("C").iterator()
        );

        assertEquals("A", iter.next());
        assertEquals("B", iter.next());
        assertEquals("C", iter.next());
        assertFalse(iter.hasNext());
    }

    @Test
    public void testOne() {
        CompoundIterator<String> iter = new CompoundIterator<>(
                Lists.newArrayList("A").iterator()
        );

        assertEquals("A", iter.next());
        assertFalse(iter.hasNext());
    }

    @Test
    public void testNone() {
        CompoundIterator<String> iter = new CompoundIterator<>();

        assertFalse(iter.hasNext());
    }
}