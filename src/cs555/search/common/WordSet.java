package cs555.search.common;

import java.io.Serializable;
import java.util.ArrayList;

public class WordSet implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public String domain;
	public int domainLinks;
	
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
		int wordIndex = indexOfWord(w);
		
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
	// Accessors
	//================================================================================
	public ArrayList<WordSet> getChunks() {
		ArrayList<WordSet> chunks = new ArrayList<WordSet>();
		
		int chunkSize = 5;
		int wordSize = words.size();
		int numberOfChunks = wordSize / chunkSize;
		
		int wordIndex = 0;
		
		for (int i=0; i<numberOfChunks; i++) {
			WordSet set = new WordSet();
			
			for (int j=0; j<chunkSize; j++) {
				set.addWord(words.get(wordIndex));
				wordIndex++;
			}
			
			chunks.add(set);
		}
		
		int remaining = wordSize - wordIndex;
		WordSet leftover = new WordSet();
		for (int i=wordIndex; i<wordSize; i++) {
			leftover.addWord(words.get(i));
		}
		
		System.out.println("Remaining : " + remaining);
		
		if (remaining > 0) {
			chunks.add(leftover);
		}
		
		return chunks;
	}
	//================================================================================
	// House Keeping
	//================================================================================
//	public boolean contains(Word other) {
//		return indexOf(other) > -1;
//	}
	
	public int indexOfWord(Word other) {
		for (int i=0; i<words.size(); i++) {
			if (words.get(i).isSameAs(other)) {
				return i;
			}
		}
		
		return -1;
	}
	
	public String toString() {
		String s = "";
		
		s += "Word Count: " + words.size();
		
		return s;
	}
}
