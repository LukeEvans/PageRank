package cs555.crawler.wireformats;

import java.net.URL;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import cs555.crawler.utilities.Constants;
import cs555.crawler.utilities.Tools;

public class FetchRequest {

	public int size;
	
	public int type; // 4
	public int depth; // 4
	
	public int domainLength; // 4 
	public String domain; // domainLength
	
	public int urlLength; // 4
	public String url; // urllength
	
	public int numberOfLinks; // 4
	public ArrayList<String> links; // for each + 4
	
	
	//================================================================================
	// Constructors
	//================================================================================
	public FetchRequest(String d, int dep, String u, ArrayList<String> list){
		init(d, dep, u, list);
	}
	
	public FetchRequest(String d, int dep, String u, URL[] urls){
		init(d, dep, u, removeUnrelatedLinks(urls,d));
		
	}
	
	public FetchRequest(){
		init("",0,"",new ArrayList<String>());
	}
	
	public void init(String d, int dep, String u, ArrayList<String> list){
		domainLength = d.length();
		domain = d;
		urlLength = u.length();
		url = u;
		links = list;
		type = Constants.Fetch_Request;
		depth = dep;
		numberOfLinks = list.size();
		
		size = 4 + 4 + 4 + domainLength + 4 + urlLength + 4;
		for (String s : links){
			size += 4;
			size += s.length();
		}
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
		
		// Depth 
		bbuff.putInt(depth);
		
		// Domain length and domain
		bbuff.putInt(domainLength);
		bbuff.put(Tools.convertToBytes(domain));
		
		// Url length and url
		bbuff.putInt(urlLength);
		bbuff.put(Tools.convertToBytes(url));
		
		// number of links and links
		bbuff.putInt(numberOfLinks);
		for (String s : links){
			bbuff.putInt(s.length());
			bbuff.put(Tools.convertToBytes(s));
		}
		
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
		
		// Depth
		depth = bbuff.getInt();
		
		// Domain length and domain
		domainLength = bbuff.getInt();
		byte[] domainBytes = new byte[domainLength];
		bbuff.get(domainBytes);
		domain = new String(domainBytes,0,domainLength);
		
		
		// Url length and url
		urlLength = bbuff.getInt();
		byte[] urlBytes = new byte[urlLength];
		bbuff.get(urlBytes);
		url = new String(urlBytes,0,urlLength);
		
		// number of links and links
		numberOfLinks = bbuff.getInt();
		for (int i=0; i<numberOfLinks; i++){			
			int strLen = bbuff.getInt();
			byte[] stringBytes = new byte[strLen];
			bbuff.get(stringBytes);
			links.add(new String(stringBytes,0,strLen));
		}
	}
	
	//================================================================================
	// House Keeping
	//================================================================================
	public String toString(){
		String s = "";
		
		s += "Fetch: " + type + "\n";
		s += "Domain: " + domain + "\n";
		s += "URL:    " + url + "\n";
		s += "Depth: " + depth + "\n";
		
		s += "[ ";
		for (String string : links){
			s += string + ", ";
		}
		s += "]\n";
		
		return s;
	}
	
	
	public ArrayList<String> removeUnrelatedLinks(URL[] urls,String d){
		ArrayList<String> relatedLinks = new ArrayList<String>();
		
		for (URL url : urls){
			String urlString = url.toString();
		
			if (urlString.startsWith(d)){
				relatedLinks.add(urlString);
			}
		}
		
		return relatedLinks;
	}
}
