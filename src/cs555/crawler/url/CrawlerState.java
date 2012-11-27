package cs555.crawler.url;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import cs555.crawler.utilities.*;
import cs555.search.common.WordSet;

public class CrawlerState implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public ArrayList<Page> readyList;
	public ArrayList<Page> pendingList;
	public ArrayList<Page> doneList;
	public ArrayList<Page> errorList;
	
	String linkFile;
	int maxDepth;
	
	
	//================================================================================
	// Constructors
	//================================================================================
	public CrawlerState(String lf){
		linkFile = lf;
		
		readyList = new ArrayList<Page>();
		pendingList = new ArrayList<Page>();
		doneList = new ArrayList<Page>();
		errorList = new ArrayList<Page>();
		maxDepth = Constants.depth;
		
		buildState();
	}
	
	public CrawlerState() {
		readyList = new ArrayList<Page>();
		pendingList = new ArrayList<Page>();
		doneList = new ArrayList<Page>();
		errorList = new ArrayList<Page>();
		maxDepth = Constants.depth;
	}
	
	public void buildState(){
		try{
			// Open the file that is the first 
			FileInputStream fstream = new FileInputStream(linkFile);
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));

			String strLine;

			//Read File Line By Line
			while ((strLine = br.readLine()) != null)   {
				createURLFromLine(strLine);
			}
			
			//Close the input stream
			in.close();
		}catch (Exception e){//Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
	}
	
	// Turn a line of text into a peer
	public void createURLFromLine(String line){
		String[] lineParts = line.split(" , ");

		String url = lineParts[0];
		String domain = lineParts[1];
		
		if (url.endsWith("/")) {
			url = url.substring(0, url.length()-1);
		}
		
		Page p = new Page(url, 0, domain);
		addPage(p);
	}
	
	
	//================================================================================
	// Accessor
	//================================================================================
	// Get next ready URL
	public Page getNextReadyPage(){
		
		if (!readyList.isEmpty()){
			Page url = readyList.get(0);
			url.status = Constants.URL_Pending;
			pendingList.add(url);
			
			return url;
		}
		
		return null;
	}
	
	public int crawledLinks() {
		return doneList.size();
	}
	
	// get all pages
	public ArrayList<Page> getAllPages() {
		return getNextReadySet(readyList.size());
	}
	
	public ArrayList<Page> getCompletedPages() {
		return doneList;
	}
	
 	// Get multiple pages
	public ArrayList<Page> getNextReadySet(int n){
		ArrayList<Page> readySet= new ArrayList<Page>();
		
		for (int i=0; i<n; i++){
			readySet.add(readyList.get(i));
		}
		
		return readySet;
	}
	
	//================================================================================
	// List manipulation 
	//================================================================================
	// Add peer
	public boolean addPage(Page u){
		if (!contains(u)){
			if (u.depth < maxDepth){
				readyList.add(u);
				return true;
			}
			
		}
		
		return false;
	}
	
	public Page findPendingUrl(Page u){
		for (Page url : pendingList){
			if (url.equals(u)){
				return url;
			}
		}
		
		return null;
	}
	
	public Page findReadyUrl(Page u) {
		for (Page url : readyList) {
			if (url.equals(u)) {
				return url;
			}
		}
		
		return null;
	}
	
	public Page findCompleteUrl(Page u) {
		for (Page url : doneList) {
			if (url.equals(u)) {
				return url;
			}
		}
		
		return null;
	}
	
	// Find a page in any state
	public Page findPage(Page p) {
		Page returnPage = findReadyUrl(p);
		
		if (returnPage == null) {
			returnPage = findPendingUrl(p);
		}
		
		if (returnPage == null) {
			returnPage = findCompleteUrl(p);
		}
		
		return returnPage;
	}
	
	public Page findPage(String url) {
		return findPage(new Page(url));
	}
	
	// Mark complete
	public void markUrlComplete(Page u){
		Page url = findPendingUrl(u);
		
		if (url != null){
			url.status = Constants.URL_Complete;
			pendingList.remove(url);
			doneList.add(url);
		}
	}
	
	public void markUrlError(Page u) {
		Page url = findPendingUrl(u);
		
		if (url != null) {
			url.status = Constants.URL_Error;
			pendingList.remove(url);
			errorList.add(url);
		}
	}
	
	public void makrUrlPending(Page u) {
		Page url = findReadyUrl(u);
		
		if (url != null) {
			url.status = Constants.URL_Pending;
			readyList.remove(url);
			pendingList.add(url);
		}
	}
	
	public void sortCompleted() {
		Collections.sort(doneList);
	}
	
	//================================================================================
	// Completion methods 
	//================================================================================
	public boolean shouldContinue(){
		if (readyLinksRemaining()) {
			return true;
		}
		
		if (pendingLinksRemaining()) {
			return true;
		}
		
		return false;
	}
	
	public boolean readyLinksRemaining(){
		return !readyList.isEmpty();
	}
	
	public boolean pendingLinksRemaining(){
		return !pendingList.isEmpty();
	}
	
	public void completeGraph() {
		
		// For every link we crawled, mark it's endpoints with the proper starting point
		for (Page page : doneList) {
			for (String link : page.getOutgoingLinks()) {
				Page dest = findCompleteUrl(new Page(link));
				
				if (dest != null) {
					dest.addIncomingLink(page.urlString);
				}
			}
		}
	}
	
	public WordSet getWordSet() {
		WordSet words = new WordSet();
		
		for (Page p : getCompletedPages()) {
			words.addWordSet(p.getWordSet());
		}
		
		return words;
	}
	//================================================================================
	// House Keeping
	//================================================================================
	// Override .contains method
	public boolean contains(Page url) {

		for (Page u : readyList){
			if (url.equals(u)){
				//System.out.println("In ready");
				return true;
			}
		}
		
		for (Page u : pendingList){
			if (url.equals(u)){
				//System.out.println("in pending");
				return true;
			}
		}

		for (Page u : doneList){
			if (url.equals(u)){
				return true;
			}
		}
		
		for (Page u : errorList) {
			if (url.equals(u)) {
				return true;
			}
		}
		return false;
	}

	// Override .toString method
	public String toString() {
		String s = "";

		s += "\nReady:\n";
		for (Page u : readyList){
			s += u.toString() + "\n";
		}
		
		s += "\nPending:\n";
		
		for (Page u : pendingList){
			s += u.toString() + "\n";
		}
		
		s += "\nDone:\n";
		
		for (Page u : doneList){
			s += u.toString() + "\n";
		}
		
		return s;
	}
	
	public String diagnostics() {
		String s = "";
		HashMap<String, Integer> files = new HashMap<String, Integer>();
		
		s += "Crawled links : " + doneList.size() + "\n";
		
		for (Page p : doneList) {
			
			for (Map.Entry<String,Integer> entry : p.metaData.fileMap.entrySet()) {
				String format = entry.getKey();
				int count = entry.getValue();

				if (files.containsKey(format)) {
					int curr = files.get(format);
					files.put(format, curr + count);
				}
				
				else {
					files.put(format, count);
				}
			}
		}
		
		s += "Files : \n";
		
		for (Map.Entry<String,Integer> entry : files.entrySet()) {
			String format = entry.getKey();
			int count = entry.getValue();

			s += format + " = " + count + "\n";
		}
		
		
		return s;
	}
	
	public String graphDiagnostics() {
		String s = "";
		
		s += "Crawled links: " + doneList.size() + "\n";
		
		int i = 0;
		for (Page p : doneList) {
			if (i == 25) {
				break;
			}
			
			s += p.toString();
			
			i++;
		}
		
		return s;
	}
	
	public String remaining() {
		String s = "Ready Remaining : " + readyList.size() + "\n";
		s += "Pending Remainaing : " + pendingList.size() + "\n";
		
		if (pendingList.size() < 5) {
			for (Page p : pendingList) {
				s += p.urlString + " : " + p.depth + "\n";
			}
		}
		return s;
	}

}
