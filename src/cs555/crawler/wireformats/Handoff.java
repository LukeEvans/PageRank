package cs555.crawler.wireformats;

import cs555.crawler.utilities.Constants;

public class Handoff extends ElectionMessage{

	//================================================================================
	// Overridden Constructors
	//================================================================================
	public Handoff(String h, int p, String d){
		super(p,h,d,d);
		type = Constants.Handoff_Reply;
	}
	
	public Handoff(){
		super();
		type = Constants.Handoff_Reply;
	}
}
