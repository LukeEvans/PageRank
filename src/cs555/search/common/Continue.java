package cs555.search.common;

import java.io.Serializable;

public class Continue implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public String contString;
	
	//================================================================================
	// Constructor
	//================================================================================
	public Continue(String w) {
		contString = w;
	}
}
