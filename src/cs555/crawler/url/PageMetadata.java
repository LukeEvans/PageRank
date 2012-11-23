package cs555.crawler.url;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PageMetadata {


	ArrayList<String> links;
	public HashMap<String, Integer> fileMap;
	public Edges edges;
	public WordList words;

	//================================================================================
	// Constructor
	//================================================================================
	public PageMetadata() {
		links = new ArrayList<String>();
		fileMap = new HashMap<String, Integer>();
	}

	//================================================================================
	// Manipulation
	//================================================================================
	public void addFile(String s, int i) {
		int curr = 0;

		if (fileMap.containsKey(s)) {
			curr = fileMap.get(s);
		}

		curr += i;
		fileMap.put(s, curr);
	}

	public void parseFiles(HashMap<String, Integer> files) {
		for (Map.Entry<String,Integer> entry : files.entrySet()) {
			String format = entry.getKey();
			int count = entry.getValue();

			if (fileMap.containsKey(format)) {
				int curr = fileMap.get(format);
				fileMap.put(format, curr + count);
			}
			
			else {
				fileMap.put(format, count);
			}
		}
	}

	public void addLinks(ArrayList<String> l) {

		for (String s : l) {
			if (!links.contains(s)) {
				links.add(s);
			}
		}
	}
	
	public void addIncomingLink(String in) {
		edges.addIncoming(in);
	}
	
	public void addOutgoingLinks(ArrayList<String> outs) {
		System.out.println("Adding links in metadata");
		edges.addOutgoingSet(outs);
	}

	public void addWords(WordList wl) {
		System.out.println("Adding words in metadata");
		words.addSet(wl);
	}
	
	//================================================================================
	// House Keeping
	//================================================================================
	public String toString() {
		String s = "";

		s += edges.toString();
		s += words.toString();

		return s;
	}
}
