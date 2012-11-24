package cs555.crawler.peer;

// Class to abstract the peer
public class Peer {

	public String hostname;
	public int port;
	public String domain;
	public boolean ready;

	//================================================================================
	// Constructor
	//================================================================================
	public Peer(String host, int p) {
		hostname = host;
		port = p;
		domain = new String();
		ready = true;
		
	}

	
	//================================================================================
	// Domain Tracking
	//================================================================================
	public void setDomain(String d) {
		domain = new String(d);
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

		s += "[" + hostname + ", " + port + "]";

		return s;
	}

}
