package barrysw19.calculon.engine;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PreGeneratedMovesTest {

    @Test
    public void testPreGenMovesIteratorAllMoves() {
        PreGeneratedMoves.PreGeneratedMoveIterator iter = new PreGeneratedMoves.PreGeneratedMoveIterator(PreGeneratedMoves.SLIDE_MOVES[15]);

        int count = 0;
        while(iter.next() != 0) {
            count++;
        }
        assertEquals(21, count);
    }

    @Test
    public void testPreGenMovesIteratorSkipMoves() {
        PreGeneratedMoves.PreGeneratedMoveIterator iter = new PreGeneratedMoves.PreGeneratedMoveIterator(PreGeneratedMoves.SLIDE_MOVES[15]);

        iter.next();
        iter.nextDirection();

        int count = 0;
        while(iter.next() != 0) {
            count++;
        }
        assertEquals(15, count);
    }
}