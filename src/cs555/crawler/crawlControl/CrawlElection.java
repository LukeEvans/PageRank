package cs555.crawler.crawlControl;

import java.io.Serializable;

public class CrawlElection implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
 
	public String managerHost;
	public int managerPort;	
	public String domain;
	public String url;
	
	//================================================================================
	// Constructor
	//================================================================================
	public CrawlElection(String h, int p, String d, String u) {
		managerHost = h;
		managerPort = p;
		domain = d;
		url = u;
	}
}
