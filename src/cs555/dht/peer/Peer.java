package cs555.dht.peer;

// Class to abstract the peer
public class Peer {

	public String hostname;
	public int port;
	public String nickname;
	public int id;

	//================================================================================
	// Constructors
	//================================================================================
	public Peer(String host, int p, int h) {
		hostname = host;
		port = p;
		id = h;
	}
	
	public Peer(String host, int p) {
		hostname = host;
		port = p;
		id = -1;
	}
	
	//================================================================================
	// House Keeping
	//================================================================================

	// Override .equals method
	public boolean equals(Peer other) {
		if (other.hostname.equalsIgnoreCase(this.hostname)) {
			if (other.port == this.port) {
				if (other.id == this.id){
					return true;
				}
			}
		}

		return false;
	}

	// Override .toString method
	public String toString() {
		String s = "";

		s += "[" + hostname + ", " + port + ", " + id + "]";

		return s;
	}

}
