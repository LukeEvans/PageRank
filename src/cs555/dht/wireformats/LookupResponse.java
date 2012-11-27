package cs555.dht.wireformats;

import cs555.dht.utilities.Constants;

public class LookupResponse extends LookupRequest{

	//================================================================================
	// Overridden Constructors
	//================================================================================
	public LookupResponse(String h, int p, int i, int r, int e){
		super.init(h, p, i, r, e);
		type = Constants.lookup_reply;
		
	}
	
	
	public LookupResponse(){
		super.init("", 0, -1,-1, -1);
		type = Constants.lookup_reply;
	}
	
	//================================================================================
	// Verification
	//================================================================================
	public boolean responseResolves(int n) {
		return n == resolveID;
	}
}
