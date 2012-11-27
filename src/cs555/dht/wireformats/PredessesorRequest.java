package cs555.dht.wireformats;

import cs555.dht.utilities.Constants;

public class PredessesorRequest extends RegisterRequest{

	//================================================================================
	// Overridden Constructors
	//================================================================================
	public PredessesorRequest(String h, int p, int i){
		super.init(h, p, i);
		type = Constants.Predesessor_Request;
		
	}
	
	
	public PredessesorRequest(){
		super.init("", 0, -1);
		type = Constants.Predesessor_Request;
	}
}
