package cs555.search.common;

import java.io.Serializable;

public class AccessPoint implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public String host;
	public int port;
	
	//================================================================================
	// Constructor
	//================================================================================
	public AccessPoint(String h, int p) {
		host = h;
		port = p;
	}
}
