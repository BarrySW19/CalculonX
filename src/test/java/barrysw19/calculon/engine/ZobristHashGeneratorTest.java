package barrysw19.calculon.engine;

import barrysw19.calculon.notation.PGNUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ZobristHashGeneratorTest {

    @Test
    public void checkSameHashByDifferentPath() {
        ZobristHashGenerator hashGenerator = new ZobristHashGenerator();

        BitBoard bitBoard = new BitBoard().initialise();
        long initValue = hashGenerator.generateHash(bitBoard);

        PGNUtils.applyMoves(bitBoard,"Nf3", "Nc6", "Ng1", "Nb8");

        assertEquals(initValue, hashGenerator.generateHash(bitBoard));
    }

    @Test
    public void checkDifferentHashForNewPosition() {
        ZobristHashGenerator hashGenerator = new ZobristHashGenerator();

        BitBoard bitBoard = new BitBoard().initialise();
        long initValue = hashGenerator.generateHash(bitBoard);

        PGNUtils.applyMoves(bitBoard,"Nf3", "Nc6");

        assertNotEquals(initValue, hashGenerator.generateHash(bitBoard));
    }
}