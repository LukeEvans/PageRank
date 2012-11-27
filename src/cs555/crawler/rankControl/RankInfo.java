package cs555.crawler.rankControl;

import java.io.Serializable;

public class RankInfo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public String domain;
	public String url;
	public double score;
	
	//================================================================================
	// Constructor
	//================================================================================
	public RankInfo(String d, String u, double s) {
		domain = d;
		url = u;
		score = s;
	}

	public RankInfo(String u, double s) {
		url = u;
		score = s;
	}
}
