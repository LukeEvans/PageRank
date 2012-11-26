package cs555.crawler.peer;

import java.io.IOException;

import cs555.crawler.communications.Link;

// Class to abstract the peer
public class Peer {

	public String hostname;
	public int port;
	public String domain;
	public boolean ready;
	Link link;

	//================================================================================
	// Constructor
	//================================================================================
	public Peer(String host, int p) {
		hostname = host;
		port = p;
		domain = new String();
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
	
	public void sendData(byte[] bytes) {
		if (link != null) { 
			try {
				link.sendData(bytes);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return;
			}
		}
	}
	
	public byte[] waitForData() {
		System.out.println("Waiting for data");
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
