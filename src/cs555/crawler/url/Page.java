package cs555.crawler.url;

import java.util.ArrayList;
import java.util.HashMap;

import cs555.crawler.rankControl.RankInfo;
import cs555.crawler.utilities.*;
import cs555.search.common.Search;
import cs555.search.common.Word;
import cs555.search.common.WordSet;

public class Page implements Comparable<Page>, java.io.Serializable {

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
	public void accumulate(ArrayList<String> links, HashMap<String, Integer> fileMap) {
		metaData.addLinks(links);
		metaData.parseFiles(fileMap);
	}
	
	public void accumulate(ArrayList<String> outgoingLinks, WordList words) {
		
		metaData.addOutgoingLinks(outgoingLinks);
		metaData.addWords(words);		
	}
	
	public ArrayList<String> getOutgoingLinks() {
		return metaData.edges.outgoing;
	}
	
	public void addIncomingLink(String in) {
		metaData.addIncomingLink(in);
	}
	
	public double getOutgoingScore() {
		synchronized (metaData) {
			return metaData.pageScore / metaData.edges.outgoing.size();	
		}
	}
	
	public void tallyRankData(RankInfo data) {
		synchronized (metaData) {
			metaData.tallyScore(data.score);	
		}
	}
	
	public void rankRoundComplete() {
		synchronized (metaData) {
			metaData.finalizeScore();	
		}
	}
	
	public WordSet getWordSet() {
		WordSet words = new WordSet();
		
		int i=0;
		
		for (String w : metaData.words.words) {
			
			if (i == 5) {
				break;
			}
			
			System.out.println("Page adding words");
			Word word = new Word(w);
			Search search = new Search(urlString, metaData.pageScore);
			word.addSearch(search);
			words.addWord(word);
			
			i++;
		}
		
		return words;
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
		
		s += urlString + ": ";
		s += metaData.toString();
		
		return s;
	}

	@Override
	public int compareTo(Page o) {
		if (metaData.pageScore == o.metaData.pageScore) return 0;
		if (metaData.pageScore > o.metaData.pageScore) return -1;
		else return 1;		
	}
}
