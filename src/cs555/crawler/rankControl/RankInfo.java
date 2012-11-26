package cs555.crawler.rankControl;

import java.io.Serializable;

public class RankInfo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public String domain;
	public String url;
	public int score;
	
	//================================================================================
	// Constructor
	//================================================================================
	public RankInfo(String d, String u, int s) {
		domain = d;
		url = u;
		score = s;
	}

	public RankInfo(String u, int s) {
		url = u;
		score = s;
	}
}
