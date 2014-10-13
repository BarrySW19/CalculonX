/**
 * Calculon - A Java chess-engine.
 *
 * Copyright (C) 2008-2009 Barry Smith
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package nl.zoidberg.calculon.notation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nl.zoidberg.calculon.engine.BitBoard;
import nl.zoidberg.calculon.model.Piece;

import org.apache.commons.lang.StringUtils;

/**
* the string "<12>" to identify this line.
* eight fields representing the board position.  The first one is White's
  8th rank (also Black's 1st rank), then White's 7th rank (also Black's 2nd),
  etc, regardless of who's move it is.
* color whose turn it is to move ("B" or "W")
* -1 if the previous move was NOT a double pawn push, otherwise the chess
  board file  (numbered 0--7 for a--h) in which the double push was made
* can White still castle short? (0=no, 1=yes)
* can White still castle long?
* can Black still castle short?
* can Black still castle long?
* the number of moves made since the last irreversible move.  (0 if last move
  was irreversible.  If the value is >= 100, the game can be declared a draw
  due to the 50 move rule.)
* The game number
* White's name
* Black's name
* my relation to this game:
* initial time (in seconds) of the match
* increment In seconds) of the match
* White material strength
* Black material strength
* White's remaining time
* Black's remaining time
* the number of the move about to be made (standard chess numbering -- White's
  and Black's first moves are both 1, etc.)
* verbose coordinate notation for the previous move ("none" if there were
  none) [note this used to be broken for examined games]
* time taken to make previous move "(min:sec)".
* pretty notation for the previous move ("none" if there is none)
* flip field for board orientation: 1 = Black at bottom, 0 = White at bottom.
 */
public class Style12 {
    private static final String FILES = "abcdefgh";

	public static final int REL_POSITION 		= -3;
	public static final int REL_OBSERVING_EX 	= -2;
	public static final int REL_EXAMINER 		= 2;
	public static final int REL_OPP_TO_MOVE 	= -1;
	public static final int REL_ME_TO_MOVE 		= 1;
	public static final int REL_OBSERVING 		= 0;

	private String whiteName, blackName;
	private int gameNumber;
	private int myRelationToGame;
	private int whiteStrength, blackStrength;
	private int timeInitial, timeIncrement;
	private String gameFEN;
	private int whiteTime, blackTime;
	private String previousMovePGN;
	private int moveNumber;
	private byte sideToPlay;
	private int halfMoveCount;
	
	public Style12(String s) {
		List<String> style12 = new ArrayList<>(Arrays.asList(StringUtils.split(s)));
		gameFEN = convertStyle12(style12);
		
		halfMoveCount = Integer.parseInt(style12.get(15));
		gameNumber = Integer.parseInt(style12.get(16));
		whiteName = style12.get(17);
		blackName = style12.get(18);
		myRelationToGame = Integer.parseInt(style12.get(19));
		timeInitial = Integer.parseInt(style12.get(20));
		timeIncrement = Integer.parseInt(style12.get(21));
		whiteStrength = Integer.parseInt(style12.get(22));
		blackStrength = Integer.parseInt(style12.get(23));
		whiteTime = Integer.parseInt(style12.get(24));
		blackTime = Integer.parseInt(style12.get(25));
		moveNumber = Integer.parseInt(style12.get(26));
		previousMovePGN = style12.get(29);
		sideToPlay = "W".equals(style12.get(9)) ? Piece.WHITE : Piece.BLACK;
	}

	public String getWhiteName() {
		return whiteName;
	}

	public String getBlackName() {
		return blackName;
	}

	public int getGameNumber() {
		return gameNumber;
	}

	public int getMyRelationToGame() {
		return myRelationToGame;
	}

	public int getWhiteStrength() {
		return whiteStrength;
	}

	public int getBlackStrength() {
		return blackStrength;
	}

	public int getTimeInitial() {
		return timeInitial;
	}

	public int getTimeIncrement() {
		return timeIncrement;
	}

	public String getGameFEN() {
		return gameFEN;
	}

	public int getWhiteTime() {
		return whiteTime;
	}

	public int getBlackTime() {
		return blackTime;
	}

	public String getPreviousMovePGN() {
		return previousMovePGN;
	}

	public int getMoveNumber() {
		return moveNumber;
	}

	public byte getSideToPlay() {
		return sideToPlay;
	}

	public int getHalfMoveCount() {
		return halfMoveCount;
	}

	public BitBoard getBoard() {
		return FENUtils.getBoard(gameFEN);
	}
	
	public boolean isInitialPosition() {
		return (moveNumber == 1 && sideToPlay == Piece.WHITE);
	}
	
	public int getGameTime() {
		return (timeInitial * 60) + (timeIncrement * 40);
	}
	
	public byte getMyColor() {
		if(myRelationToGame == REL_ME_TO_MOVE) {
			return sideToPlay;
		}
		if(myRelationToGame == REL_OPP_TO_MOVE) {
			return sideToPlay == Piece.WHITE ? Piece.BLACK: Piece.WHITE;
		}
		return Piece.WHITE;
	}
	
	public boolean isFlagged() {
		return ((sideToPlay == Piece.WHITE && whiteTime < 0)
				|| (sideToPlay == Piece.BLACK && blackTime < 0));
	}
	
	public String getOpponentName() {
		if(myRelationToGame == REL_ME_TO_MOVE) {
			return sideToPlay == Piece.WHITE ? blackName : whiteName;
		}
		if(myRelationToGame == REL_OPP_TO_MOVE) {
			return sideToPlay == Piece.WHITE ? whiteName : blackName;
		}
		return null;
	}
	
	public boolean isMyGame() {
		return (myRelationToGame == Style12.REL_ME_TO_MOVE || myRelationToGame == Style12.REL_OPP_TO_MOVE);
	}

	/**
	 * Constructs a <code>String</code> with all attributes
	 * in name = value format.
	 *
	 * @return a <code>String</code> representation 
	 * of this object.
	 */
	public String toString()
	{
	    final String TAB = "    ";
	    
	    String retValue = "";
	    
	    retValue = "Style12 ( "
	        + super.toString() + TAB
	        + "whiteName = " + this.whiteName + TAB
	        + "blackName = " + this.blackName + TAB
	        + "gameNumber = " + this.gameNumber + TAB
	        + "myRelationToGame = " + this.myRelationToGame + TAB
	        + "whiteStrength = " + this.whiteStrength + TAB
	        + "blackStrength = " + this.blackStrength + TAB
	        + "timeInitial = " + this.timeInitial + TAB
	        + "timeIncrement = " + this.timeIncrement + TAB
	        + "gameFEN = " + this.gameFEN + TAB
	        + "whiteTime = " + this.whiteTime + TAB
	        + "blackTime = " + this.blackTime + TAB
	        + "previousMovePGN = " + this.previousMovePGN + TAB
	        + "moveNumber = " + this.moveNumber + TAB
	        + "sideToPlay = " + this.sideToPlay + TAB
	        + " )";
	
	    return retValue;
	}

    public static String convertStyle12(List<String> fields) {
        StringBuilder fen = new StringBuilder();
        for(int i = 0; i < 8; i++) {
            int eCount = 0;
            String line = fields.get(i + 1);
            for(int j = 0; j < line.length(); j++) {
                if(line.charAt(j) == '-') {
                    eCount++;
                } else {
                    if(eCount > 0) {
                        fen.append(eCount);
                        eCount = 0;
                    }
                    fen.append(line.charAt(j));
                }
            }
            if(eCount > 0) {
                fen.append(eCount);
            }
            if(i < 7) {
                fen.append("/");
            }
        }

        fen.append(" ").append(fields.get(9).toLowerCase()).append(" ");
        StringBuilder buf = new StringBuilder();
        if("1".equals(fields.get(11))) {
            buf.append("K");
        }
        if("1".equals(fields.get(12))) {
            buf.append("Q");
        }
        if("1".equals(fields.get(13))) {
            buf.append("k");
        }
        if("1".equals(fields.get(14))) {
            buf.append("q");
        }
        fen.append(buf.length() == 0 ? "-" : buf.toString()).append(" ");

        if("-1".equals(fields.get(10))) {
            fen.append("- ");
        } else {
            fen.append(FILES.charAt(Integer.parseInt(fields.get(10))));
            fen.append("B".equals(fields.get(9)) ? "3" : "6").append(" ");
        }
        fen.append(fields.get(15)).append(" ");
        fen.append(fields.get(26));

        return fen.toString();
    }
}
