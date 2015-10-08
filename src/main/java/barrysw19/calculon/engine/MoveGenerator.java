package barrysw19.calculon.engine;

import java.util.Iterator;
import java.util.List;

public interface MoveGenerator extends Iterator<BitBoard.BitBoardMove> {
    public List<BitBoard.BitBoardMove> getThreateningMoves();
}
