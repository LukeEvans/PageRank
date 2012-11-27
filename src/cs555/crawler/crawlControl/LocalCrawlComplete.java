package cs555.crawler.crawlControl;

import java.io.Serializable;

public class LocalCrawlComplete implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public String host;
	public int port;
	
	//================================================================================
	// Constructor
	//================================================================================
	public LocalCrawlComplete(String h, int p) {
		host = h;
		port = p;
	}
}
