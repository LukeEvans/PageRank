package cs555.crawler.rankControl;

import java.io.Serializable;

public class RankElection implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public String managerHost;
	public int managerPort;
	
	//================================================================================
	// Constructor
	//================================================================================
	public RankElection(String h, int p) {
		managerHost = h;
		managerPort = p;
	}

}
