package cs555.dht.wireformats;

import java.nio.ByteBuffer;

import cs555.dht.utilities.Constants;
import cs555.dht.utilities.Tools;

public class RegisterRequest{

	public int size;
	
	public int type; // 4
	
	public int hostLength; // 4 
	public String hostName; // hostLength
	
	public int port; // 4
	
	public int id; // 4
	
	//================================================================================
	// Constructors
	//================================================================================
	public RegisterRequest(String h, int p, int i){
		init(h, p, i);
	}
	
	public RegisterRequest(){
		init("",0,-1);
	}
	
	public void init(String h, int p, int i){
		type = Constants.Registration_Request;
		hostLength = h.length();
		hostName = h;
		
		port = p;
		id = i;
		
		size = 4 + 4 + hostLength + 4 + 4;
	}
	
	
	//================================================================================
	// Marshall
	//================================================================================
	public byte[] marshall(){
		byte[] bytes = new byte[size + 4];
		ByteBuffer bbuff = ByteBuffer.wrap(bytes);
		
		// Size
		bbuff.putInt(size);
		
		// type
		bbuff.putInt(type);
		
		// Host length and hostname
		bbuff.putInt(hostLength);
		bbuff.put(Tools.convertToBytes(hostName));
		
		// Port 
		bbuff.putInt(port);
		
		// Id
		bbuff.putInt(id);
		
		return bytes;
	}
	
	
	//================================================================================
	// Unmarshall
	//================================================================================
	public void unmarshall(byte[] bytes){
		ByteBuffer bbuff = ByteBuffer.wrap(bytes);
		
		// Size
		size = bbuff.getInt();
			
		// type
		type = bbuff.getInt();
		
		// Host length and hostname
		hostLength = bbuff.getInt();
		byte[] hostBytes = new byte[hostLength];
		bbuff.get(hostBytes);
		hostName = new String(hostBytes,0,hostLength);
		
		// Port
		port = bbuff.getInt();
		
		// ID
		id = bbuff.getInt();
		
	}
	
	//================================================================================
	// House Keeping
	//================================================================================
	// Override the toString method
	public String toString(){
		String s = "";
		
		s += "Peer: " + hostName + ":" + port + ", " + id + "\n";
		
		return s;
	}
	
	// Override the equals method
	public boolean equals(RegisterRequest other) {
		if (this.hostName.equalsIgnoreCase(other.hostName)) {
			if (this.port == other.port) {
				if (this.id == other.id) {
					return true;
				}
			}
		}
		
		return false;
	}
}
