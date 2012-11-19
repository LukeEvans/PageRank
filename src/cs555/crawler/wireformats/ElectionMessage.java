package cs555.crawler.wireformats;

import java.nio.ByteBuffer;

import cs555.crawler.utilities.Constants;
import cs555.crawler.utilities.Tools;

public class ElectionMessage {

	public int size;
	public int type; // 4
	public int port; // 4
	public int hostLength; // 4
	public String host; // hostLength
	
	public int domainLen; // 4
	public String domain; // domainLen
	
	public int urlLen; // 4
	public String url; // urlLen
	
	//================================================================================
	// Overridden Constructors
	//================================================================================
	public ElectionMessage(int p, String h, String d, String u){
		hostLength = h.length();
		host = h;
		port = p;
		
		domainLen = d.length();
		domain = d;
		
		urlLen = u.length();
		url = u;
			
		type = Constants.Election_Message;
		
		size = 4 + 4 + 4 + hostLength + 4 + domainLen + 4 + urlLen;
	}
	
	public ElectionMessage(){
		hostLength = 0;
		host = "";
		port = 0;
		size = 0;
		type = Constants.Election_Message;
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
		
		// Port 
		bbuff.putInt(port);
		
		// Host length and host
		bbuff.putInt(hostLength);
		bbuff.put(Tools.convertToBytes(host));
		
		// Domain
		bbuff.putInt(domainLen);
		bbuff.put(Tools.convertToBytes(domain));
		
		// Url
		bbuff.putInt(urlLen);
		bbuff.put(Tools.convertToBytes(url));
		
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
		
		// port
		port = bbuff.getInt();
		
		// Domain length and domain
		hostLength = bbuff.getInt();
		byte[] hostBytes = new byte[hostLength];
		bbuff.get(hostBytes);
		host = new String(hostBytes,0,hostLength);
		
		// Domain
		domainLen = bbuff.getInt();
		byte[] dBytes = new byte[domainLen];
		bbuff.get(dBytes);
		domain = new String(dBytes,0,domainLen);
		
		// Url
		urlLen = bbuff.getInt();
		byte[] uBytes = new byte[urlLen];
		bbuff.get(uBytes);
		url = new String(uBytes,0,urlLen);
	}
	
	//================================================================================
	// House Keeping
	//================================================================================
	public String toString(){
		String s = "";
		
		s += "NodeManager: " + host + ":" + port + "\n";
		s += "Domain: " + domain + " Url: " + url + "\n";
		
		return s;
	}
	
}
