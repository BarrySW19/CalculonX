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
package barrysw19.calculon.fics;

import java.util.ArrayList;
import java.util.List;

public class FICSConfig {
	
	private String operatorName;
	private String loginName;
	private int acceptMin, acceptMax;
	private int maxRematches;
	private boolean reseek;
	private List<Seek> seekAds = new ArrayList<Seek>();
	
	public String getOperatorName() {
		return operatorName;
	}
	public void setOperatorName(String operatorName) {
		this.operatorName = operatorName;
	}
	public String getLoginName() {
		return loginName;
	}
	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}
	public int getAcceptMin() {
		return acceptMin;
	}
	public void setAcceptMin(int acceptMin) {
		this.acceptMin = acceptMin;
	}
	public int getAcceptMax() {
		return acceptMax;
	}
	public void setAcceptMax(int acceptMax) {
		this.acceptMax = acceptMax;
	}
	public void addSeekAd(Seek seekAd) {
		seekAds.add(seekAd);
	}
	public List<Seek> getSeekAds() {
		return seekAds;
	}
	public int getMaxRematches() {
		return maxRematches;
	}
	public void setMaxRematches(int maxRematches) {
		this.maxRematches = maxRematches;
	}
	public boolean isReseek() {
		return reseek;
	}
	public void setReseek(boolean reseek) {
		this.reseek = reseek;
	}

	public static class Seek {
		private int initialTime;
		private int increment;
		
		public int getInitialTime() {
			return initialTime;
		}
		public void setInitialTime(int initialTime) {
			this.initialTime = initialTime;
		}
		public int getIncrement() {
			return increment;
		}
		public void setIncrement(int increment) {
			this.increment = increment;
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
		    
		    retValue = "Seek ( "
		        + super.toString() + TAB
		        + "initialTime = " + this.initialTime + TAB
		        + "increment = " + this.increment + TAB
		        + " )";
		
		    return retValue;
		}
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
	    
	    retValue = "FICSConfig ( "
	        + super.toString() + TAB
	        + "operatorName = " + this.operatorName + TAB
	        + "loginName = " + this.loginName + TAB
	        + "acceptMin = " + this.acceptMin + TAB
	        + "acceptMax = " + this.acceptMax + TAB
	        + "maxRematches = " + this.maxRematches + TAB
	        + "reseek = " + this.reseek + TAB
	        + "seekAds = " + this.seekAds + TAB
	        + " )";
	
	    return retValue;
	}
}
