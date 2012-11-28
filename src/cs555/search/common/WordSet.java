package cs555.search.common;

import java.io.Serializable;
import java.util.ArrayList;

public class WordSet implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ArrayList<Word> words;
	
	//================================================================================
	// Constructor
	//================================================================================
	public WordSet() {
		words = new ArrayList<Word>();
	}
	
	//================================================================================
	// Modifiers
	//================================================================================
	public void addWord(Word w) {
		int wordIndex = indexOf(w);
		
		if (wordIndex > -1) {
			words.get(wordIndex).addSearchSet(w.searchSet);
			
		}
		
		else {
			words.add(w);
		}
	}
	
	public void addWordSet(WordSet wordSet) {
		for (Word w : wordSet.words) {
			addWord(w);
		}
	}
	
	//================================================================================
	// House Keeping
	//================================================================================
	public boolean contains(Word other) {
		return indexOf(other) > -1;
	}
	
	public int indexOf(Word other) {
		for (int i=0; i<words.size(); i++) {
			if (words.get(i).equals(other)) {
				return i;
			}
		}
		
		return -1;
	}
	
	public String toString() {
		String s = "";
		
		for (int i=0; i<5; i++) {
			s += words.get(i) + "\n";
		}
		
		return s;
	}
}
