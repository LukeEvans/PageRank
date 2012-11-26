package cs555.crawler.rankControl;

import java.io.Serializable;

import cs555.crawler.utilities.Constants;

public class RoundComplete implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public int roundComplete;
	
	//================================================================================
	// Constructor
	//================================================================================
	public RoundComplete() {
		roundComplete = Constants.PRound_Complete;
	}

}
