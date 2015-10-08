package barrysw19.calculon.engine;

import barrysw19.calculon.notation.PGNUtils;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class EngineTest {

    // Test to trap a bug found in testing from occurring again... (on 50 move rule issue)
    @Test
    public void testLoadOver50Moves() {
        String moveList = "1. Nc3 c5 2. e4 e6 3. Bc4 Nf6 4. Qf3 Nc6 5. Qf4 g5 \n" +
                "6. Qg3 d5 7. Bd3 c4 8. exd5 Nb4 9. Nb5 Nxd3+ 10. cxd3 Nh5 \n" +
                "11. Nc7+ Kd7 12. Qf3 Kxc7 13. Qxh5 cxd3 14. Qxf7+ Qe7 15. d6+ Kxd6 \n" +
                "16. Qf3 Kc7 17. Qxd3 Qc5 18. Nf3 Bd7 19. Qe3 h6 20. Qxc5+ Bxc5 \n" +
                "21. d4 Be7 22. Bd2 Bc6 23. Rc1 Rad8 24. Ba5+ b6 25. Ne5 Kb7 \n" +
                "26. Nxc6 Rc8 27. d5 exd5 28. a4 Rxc6 29. Rxc6 Kxc6 30. Bc3 Re8 \n" +
                "31. Kd2 Bd6 32. h4 Bf4+ 33. Kd3 g4 34. Bd4 a5 35. b3 h5 \n" +
                "36. Rg1 b5 37. g3 Bh6 38. Rd1 b4 39. Rb1 Re7 40. Bf6 Rf7 \n" +
                "41. Bd8 Rxf2 42. Rd1 Rf3+ 43. Kc2 Rxg3 44. Bxa5 Rg2+ 45. Kb1 Rg3 \n" +
                "46. Kc2 Rg2+ 47. Kb1 Kc5 48. Bd8 Re2 49. Bf6 Bd2 50. Kc2 Bf4+ \n" +
                "51. Kd3 Re3+ 52. Kc2 Re2+ 53. Kd3 Re3+ 54. Kc2 Re4 55. a5 Re2+ \n" +
                "56. Kd3 Ra2 57. Bd8 Rb2 58. Bb6+ Kc6 59. Kd4 Rxb3 60. Re1 Rf3 \n" +
                "61. Re8 Bd6 62. Rc8+ Kb7 63. Rh8 Rf5 64. Rh6 Kc6 65. a6 Rf8 \n" +
                "66. Rxd6+ Kxd6 67. Bc5+ Kc6 68. Bxf8 b3 69. Kc3 b2 70. Kxb2 Kb6 \n" +
                "71. Bd6 Kxa6 72. Kc3 Kb6 73. Kd4 Ka6 74. Kxd5 Kb7 75. Be5 Ka6 \n" +
                "76. Kc5 Ka5 77. Bd6 Ka6 78. Kc6 Ka5 79. Be5 Kb4 80. Kd5 Ka5 \n" +
                "81. Bc7+ Kb4 82. Bf4 Kb3 83. Be5";
        BitBoard bitBoard = new BitBoard().initialise();

        for(String move: moveList.split(" ")) {
            if( ! move.contains(".")) {
                PGNUtils.applyMove(bitBoard, move);
            }
        }
        Set<String> pgnMoves = PGNUtils.toPgnMoveMap(bitBoard).keySet();
        assertEquals(new HashSet<>(Arrays.asList("Ka4", "Ka2", "Ka3", "Kc2", "Kb4", "g3")), pgnMoves);
    }
}
