package cs555.search.common;

import java.io.Serializable;

public class Search implements Comparable<Search>, Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public String pageUrl;
	public double pageScore;
	
	//================================================================================
	// Constructor
	//================================================================================
	public Search(String u, double s) {
		pageUrl = u;
		pageScore = s;
	}
	
	//================================================================================
	// House Keeping
	//================================================================================
	public boolean isTheSame(Search s) {
		if (this.pageUrl.equalsIgnoreCase(s.pageUrl)) {
			return true;
		}
		
		return false;
	}

	public String toString() {
		String s = "";
		
		s += pageUrl + " : " + pageScore;
		
		return s;
	}
	
	@Override
	public int compareTo(Search o) {
		if (pageScore == o.pageScore) return 0;
		if (pageScore > o.pageScore) return -1;
		else return 1;		
	}
}
