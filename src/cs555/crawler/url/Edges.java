package cs555.crawler.url;

import java.util.ArrayList;

public class Edges implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public ArrayList<String> incoming;
	public ArrayList<String> outgoing;
	
	//================================================================================
	// Constructors
	//================================================================================
	public Edges() {
		incoming = new ArrayList<String>();
		outgoing = new ArrayList<String>();
	}
	
	//================================================================================
	// Modifiers
	//================================================================================
	public void addIncoming(String in) {
		if (!incoming.contains(in)) {
			incoming.add(in);
		}
	}
	
	public void addOutgoing(String out) {
		if (!outgoing.contains(out)) {
			outgoing.add(out);
		}
	} 

	// Add a list of outgoing edges
	public void addOutgoingSet(ArrayList<String> outs) {
		for (String s : outs) {
			addOutgoing(s);
		}
	}
	
	//================================================================================
	// House Keeping
	//================================================================================
	public String toString() {
		String s = "";
		
		s += "Incoming Links: \n";
		
		for (String in : incoming) {
			s += "incoming: " + in + "\n";
		}
		
		s += "Outgoing Links: \n";
		
		for (String out : outgoing) {
			s += "outgoing: " + out + "\n";
		}
		
		return s;
	}
}
