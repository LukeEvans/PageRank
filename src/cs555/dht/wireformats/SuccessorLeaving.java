package cs555.dht.wireformats;

import cs555.dht.utilities.Constants;

public class SuccessorLeaving extends RegisterRequest{

	//================================================================================
	// Overridden Constructors
	//================================================================================
	public SuccessorLeaving(String h, int p, int i){
		super.init(h, p, i);
		type = Constants.Successor_Leaving;
		
	}
	
	
	public SuccessorLeaving(){
		super.init("", 0, -1);
		type = Constants.Successor_Leaving;
	}
}
