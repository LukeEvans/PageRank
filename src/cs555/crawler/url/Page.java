package cs555.crawler.url;

import java.util.ArrayList;
import java.util.HashMap;

import cs555.crawler.utilities.*;
import cs555.crawler.wireformats.FetchRequest;

public class Page implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public int status;
	public String urlString;
	public int depth;
	public String domain;
	public PageMetadata metaData;
	
	//================================================================================
	// Constructor
	//================================================================================
	public Page(String url){
		urlString = url;
		domain = urlString;
		status = Constants.URL_Ready;
		depth = 0;
		metaData = new PageMetadata();
		
	}
	
	public Page(String url, int dep, String d){
		urlString = url;
		domain = d;
		status = Constants.URL_Ready;
		depth = dep;
		metaData = new PageMetadata();
		
	}
	
	//================================================================================
	// Modifiers
	//================================================================================
	public FetchRequest getFetchRequest(){
		return new FetchRequest(domain, depth, urlString, metaData.links);
	}
	
	public void accumulate(ArrayList<String> links, HashMap<String, Integer> fileMap) {
		metaData.addLinks(links);
		metaData.parseFiles(fileMap);
	}
	
	public void accumulate(ArrayList<String> outgoingLinks, WordList words) {
		
		metaData.addOutgoingLinks(outgoingLinks);
		metaData.addWords(words);
		
		System.out.println("Done accumulating");
	}
	
	public ArrayList<String> getOutgoingLinks() {
		return metaData.edges.outgoing;
	}
	
	public void addIncomingLink(String in) {
		metaData.addIncomingLink(in);
	}
	
	//================================================================================
	// House Keeping
	//================================================================================
	// Override .equals
	public boolean equals(Page other){
		if (this.urlString.equalsIgnoreCase(other.urlString)){
			return true;
		}
		return false;
	}
	
	// Override toString
	public String toString(){
		String s = "";
		
		s += urlString + ": \n";
		s += metaData.toString();
		
		return s;
	}
}
