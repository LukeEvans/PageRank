package cs555.dht.wireformats;

import java.nio.ByteBuffer;

import cs555.dht.utilities.Constants;
import cs555.dht.utilities.Tools;

public class LookupRequest{

	public int size;
	
	public int type; // 4
	
	public int hopCount; // 4
	
	public int hostLength; // 4 
	public String hostName; // hostLength
	
	public int port; // 4
	
	public int id; // 4
	
	public int resolveID; // 4
	
	public int ftEntry; // 4
	
	//================================================================================
	// Constructors
	//================================================================================
	public LookupRequest(String h, int p, int i, int r, int e){
		init(h, p, i, r, e);
	}
	
	public LookupRequest(){
		init("",0,-1,-1, -1);
	}
	
	public void init(String h, int p, int i, int r, int e){
		type = Constants.lookup_request;
		hopCount = 0;
		
		hostLength = h.length();
		hostName = h;
		
		port = p;
		
		id = i;
		
		resolveID = r;
		
		ftEntry = e;
		
		size = 4 + 4 + 4 + hostLength + 4 + 4 + 4 + 4;
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
		
		// Hop count 
		bbuff.putInt(hopCount);
		
		// Host length and hostname
		bbuff.putInt(hostLength);
		bbuff.put(Tools.convertToBytes(hostName));
		
		// Port 
		bbuff.putInt(port);
		
		// ID
		bbuff.putInt(id);
		
		// Resolve ID
		bbuff.putInt(resolveID);
		
		// Entry seeking
		bbuff.putInt(ftEntry);
		
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
		
		// Hopcount 
		hopCount = bbuff.getInt();
		
		// Host length and hostname
		hostLength = bbuff.getInt();
		byte[] hostBytes = new byte[hostLength];
		bbuff.get(hostBytes);
		hostName = new String(hostBytes,0,hostLength);
		
		// Port
		port = bbuff.getInt();
		
		// ID
		id = bbuff.getInt();
		
		// Resolve ID
		resolveID = bbuff.getInt();
		
		// Entry Seeking
		ftEntry = bbuff.getInt();
	}
	
	//================================================================================
	// House Keeping
	//================================================================================
	// Override the toString method
	public String toString(){
		String s = "";
		
		s += "node: " + id + " resolving: " + resolveID + " hopCount: " + hopCount + "\n";
		
		return s;
	}
	
	// Override the equals method
	public boolean equals(LookupRequest other) {
		if (this.hostName.equalsIgnoreCase(other.hostName)){
			if (this.port == other.port) {
				if (this.resolveID == other.resolveID) {
					return true;
				}
			}
			
		}
		
		return false;
	}
	
}
