package cs555.search.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

public class Word implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public String word;
	transient public ArrayList<Search> searchSet;
	
	//================================================================================
	// Constructors
	//================================================================================
	public Word(String w) {
		word = w;
		searchSet = new ArrayList<Search>();
	}
	
	//================================================================================
	// Modifiers
	//================================================================================
	public void addSearch(Search search) {
		int searchIndex = indexOf(search);
		
		// If we already have this url, keep the maximum of the two
		if (searchIndex > -1) {
			if (search.pageScore > searchSet.get(searchIndex).pageScore) {
				searchSet.remove(searchIndex);
				searchSet.add(search);
			}
		}
		
		// Otherwise, just add it
		else {
			searchSet.add(search);
		}
	}
	
	public void addSearchSet(ArrayList<Search> set) {
		for (Search s : set) {
			addSearch(s);
		}
	}
	
	public void sort() {
		Collections.sort(searchSet);
	}
	
	public ArrayList<Search> getIntersection(Word other) {
		ArrayList<Search> intersection = new ArrayList<Search>();
		
		for (Search s : other.searchSet) {
			if (contains(s)) 
				intersection.add(s);
		}
		
		return intersection;
	}
	//================================================================================
	// House Keeping
	//================================================================================
	public int indexOf(Search other) {
		for (int i=0; i<searchSet.size(); i++) {
			if (searchSet.get(i).equals(other)) {
				return i;
			}
		}
		
		return -1;
	}
	
	public boolean contains(Search other) {
		for (Search s : searchSet) {
			if (s.equals(other)) {
				return true;
			}
		}
		
		return false;
	}
	
	public boolean equals(Word other) {
		if (word.equalsIgnoreCase(other.word)) {
			return true;
		}
		
		return false;
	}
	
	public String toString() {
		String s = "";
		
		s += "Word: " + word + " link set size: " + searchSet.size() + "\n";
		
		return s;
	}
}

