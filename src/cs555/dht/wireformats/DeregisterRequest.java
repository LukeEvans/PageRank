package cs555.dht.wireformats;

import cs555.dht.utilities.Constants;

public class DeregisterRequest extends RegisterRequest{

	//================================================================================
	// Overridden Constructors
	//================================================================================
	public DeregisterRequest(String h, int p, int i){
		super.init(h, p, i);
		type = Constants.Deregister_Request;
		
	}
	
	
	public DeregisterRequest(){
		super.init("", 0, -1);
		type = Constants.Deregister_Request;
	}
}
