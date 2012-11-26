package cs555.crawler.rankControl;

import java.io.Serializable;

public class LocalRankingComplete implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public String host;
	public int port;
	
	//================================================================================
	// Constructor
	//================================================================================
	public LocalRankingComplete(String h, int p) {
		host = h;
		port = p;
	}
}
