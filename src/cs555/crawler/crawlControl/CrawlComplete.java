package cs555.crawler.crawlControl;

import java.io.Serializable;

public class CrawlComplete implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public String host;
	public int port;
	
	//================================================================================
	// Constructor
	//================================================================================
	public CrawlComplete(String h, int p) {
		host = h;
		port = p;
	}
}
