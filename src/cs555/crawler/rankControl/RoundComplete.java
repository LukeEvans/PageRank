package cs555.crawler.rankControl;

import java.io.Serializable;

import cs555.crawler.utilities.Constants;

public class RoundComplete implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public String host;
	public int port;
	public int roundComplete;
	
	//================================================================================
	// Constructor
	//================================================================================
	public RoundComplete(String h, int p) {
		host = h;
		port = p;
		
		roundComplete = Constants.PRound_Complete;
	}

}
