package cs555.crawler.peer;

import java.io.IOException;
import java.io.Serializable;

import cs555.crawler.communications.Link;

// Class to abstract the peer
public class Peer implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String hostname;
	public int port;
	public String domain;
	public String seedURL;
	public boolean ready;
	public transient Link link;

	//================================================================================
	// Constructor
	//================================================================================
	public Peer(String host, int p) {
		hostname = host;
		port = p;
		domain = new String();
		seedURL = new String();
		ready = true;
		link = null;
	}

	public void setLink(Link l) {
		link = l;
	}

	public void initLink() {
		if (link != null) {
			link.initLink();
		}
	}

	public void sendData(byte[] bytes) throws IOException {
		if (link != null) { 
			link.sendData(bytes);
		}
		
		else {
			System.out.println(hostname + " link is null");
		}
	}

	public byte[] waitForData() {
		return link.waitForData();
	}

	public void closeLink() {
		if (link != null) {
			link.close();
			link = null;
		}
	}
	//================================================================================
	// Domain Tracking
	//================================================================================
	public void setDomain(String d) {
		domain = new String(d);
	}

	public void setSeedUrl(String u) {
		seedURL = new String(u);
	}
	//================================================================================
	// House Keeping
	//================================================================================

	// Override .equals method
	public boolean equals(Peer other) {
		if (other.hostname.equalsIgnoreCase(this.hostname)) {
			if (other.port == this.port) {
				return true;
			}
		}

		return false;
	}


	// Override .toString method
	public String toString() {
		String s = "";

		s += "[" + hostname + ", " + port + " , " + domain + "]";

		return s;
	}

}
