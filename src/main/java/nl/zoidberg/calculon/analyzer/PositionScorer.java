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
package nl.zoidberg.calculon.analyzer;

import nl.zoidberg.calculon.engine.BitBoard;

public interface PositionScorer {
	
	public int scorePosition(BitBoard bitBoard, Context context);

    /**
     * Context which will be populated by the game scorer and passed to the position
     * scorers for information.
     */
    public static class Context {
        private boolean endgame = false;

        public void setEndgame(boolean endgame) {
            this.endgame = endgame;
        }

        public boolean isEndgame() {
            return endgame;
        }
    }
}
