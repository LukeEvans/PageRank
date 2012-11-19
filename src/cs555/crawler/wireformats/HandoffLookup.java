package cs555.crawler.wireformats;

import java.util.ArrayList;

import cs555.crawler.utilities.Constants;

public class HandoffLookup extends FetchRequest{

	//================================================================================
	// Overridden Constructors
	//================================================================================
	public HandoffLookup(String d, int dep, String u, ArrayList<String> list){
		super.init(d, dep, u, list);
		type = Constants.Handoff_Lookup;
		
	}
	
	public HandoffLookup(){
		super.init("", 0, "", new ArrayList<String>());
		type = Constants.Fetch_Response;
	}
}
