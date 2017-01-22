package barrysw19.calculon.engine;

import barrysw19.calculon.notation.FENUtils;
import org.junit.Test;

import static org.junit.Assert.*;

public class MoveGeneratorImplTest {

    @Test
    public void testAttackGeneration1() {
        BitBoard bitBoard = FENUtils.getBoard("7k/8/5PPP/8/8/1P6/P7/7K w - - 0 1");
        assertEquals(0b00000000_11110000_00000000_00000000_00000101_00000010_11000000_01000000L,
                MoveGeneratorImpl.calculateAllAttackedSquares(bitBoard, bitBoard.getPlayer()));
    }

    @Test
    public void testAttackGeneration2() {
        BitBoard bitBoard = FENUtils.getBoard("7k/8/6N1/8/8/8/P7/N6K w - - 0 1");
        assertEquals(0b10100000_00010000_00000000_00010000_10100000_00000010_11000100_01000000L,
                MoveGeneratorImpl.calculateAllAttackedSquares(bitBoard, bitBoard.getPlayer()));
    }

    @Test
    public void testAttackGeneration3() {
        BitBoard bitBoard = FENUtils.getBoard("7k/8/5PPP/8/8/1P6/P7/7K w - - 0 1").reverse();
        assertEquals(0b01000000_11000000_00000010_00000101_00000000_00000000_11110000_00000000L,
                MoveGeneratorImpl.calculateAllAttackedSquares(bitBoard, bitBoard.getPlayer()));
    }

    @Test
    public void testAttackGeneration4() {
        BitBoard bitBoard = FENUtils.getBoard("7k/8/8/8/8/2B5/8/n6K w - - 0 1");
//        bitBoard.printBoard();
//        BitBoard.printBits(MoveGeneratorImpl.calculateAllAttackedSquares(bitBoard, bitBoard.getPlayer()));
        assertEquals(0b10000000_01000000_00100000_00010001_00001010_00000000_11001010_01010001L,
                MoveGeneratorImpl.calculateAllAttackedSquares(bitBoard, bitBoard.getPlayer()));
    }
}