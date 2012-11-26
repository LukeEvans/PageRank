package cs555.crawler.rankControl;

import java.io.Serializable;

public class Message implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public int number;
	
	//================================================================================
	// Constructor
	//================================================================================
	public Message(int n) {
		number = n;
	}
}
