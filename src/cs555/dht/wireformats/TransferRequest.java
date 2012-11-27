package cs555.dht.wireformats;

import java.nio.ByteBuffer;

import cs555.dht.utilities.Constants;
import cs555.dht.utilities.Tools;

/**
 * 
 * @author levans
 *This is a wire format class that contains a message that the routers send to the discovery 
 *node to request registration.
 */
public class TransferRequest {
	
	public int size; //4
	public int type; //4
	public int filehash; //4
	public int pathLen; //4
	public String path; //pathLen
	
	//================================================================================
	// Constructors
	//================================================================================
	public TransferRequest(String p, int h){
		init(p, h);
	}
	
	public TransferRequest(){
		init("",-1);
	}
	
	public void init(String p, int h) {
		size = 4 + 4 + 4 + p.length();
		type = Constants.store_request;
		filehash = h;
		pathLen = p.length();
		path = p;
		
	}
	
	
	public byte[] marshall(){
			
		byte[] bytes = new byte[size + 4];
		
		ByteBuffer bbuff = ByteBuffer.wrap(bytes); 
		
		// Size of entire buffer
		bbuff.putInt(size);
		
		// Type of data
		bbuff.putInt(type);
		
		// filehash 
		bbuff.putInt(filehash);
		
		// Path length and path
		bbuff.putInt(pathLen);
		bbuff.put(Tools.convertToBytes(path));
		
		return bytes;
		
	}
	
	public void unmarshall(byte[] bytes){
		ByteBuffer bbuff = ByteBuffer.wrap(bytes);
		
		// Total size
		size = bbuff.getInt();

		// Type of message
		type = bbuff.getInt();

		// File hash
		filehash = bbuff.getInt();
		
		// Path
		pathLen = bbuff.getInt();
		byte[] pathBytes = new byte[pathLen];
		bbuff.get(pathBytes);
		path = new String(pathBytes,0,pathLen);
		
	}
	
	//================================================================================
	// House Keeping
	//================================================================================
	public String toString(){
		String s = "";
		
		s += "Message Type: " + type + "\n";
		s += "Path: " + path + "\n";
		
		return s;
	}
}
