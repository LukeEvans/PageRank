package cs555.crawler.rankControl;

import java.io.Serializable;

public class DomainInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public String hostname;
	public int port;
	public String domain;
	public int linksCrawled;
	
	//================================================================================
	// Constructor
	//================================================================================
	public DomainInfo(String h, int p, String d, int lc) {
		hostname = h;
		port = p;
		domain = d;
		linksCrawled = lc;
	}
}
