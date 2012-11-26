package cs555.crawler.rankControl;

import java.io.Serializable;

public class BeginRound implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public int roundNumber;
	
	//================================================================================
	// Constructor
	//================================================================================
	public BeginRound(int i) {
		roundNumber = i;
	}
}
