package cs555.crawler.crawlControl;

import java.io.Serializable;

public class CrawlRequest implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public String domain; 
	public String url;
	public int depth;
	public String incoming;
	
	//================================================================================
	// Constructors
	//================================================================================
	public CrawlRequest(String dom, String ur, int dep, String in) {
		init(dom, ur, dep, in);
	}
	
	public CrawlRequest(String dom, String ur, int dep) {
		init(dom, ur, dep, null);
	}
	
	public void init(String dom, String ur, int dep, String in) {
		domain = dom;
		url = ur;
		depth = dep;
		incoming = in;
	}
}
