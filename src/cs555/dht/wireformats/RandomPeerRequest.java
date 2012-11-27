package cs555.dht.wireformats;

import cs555.dht.utilities.Constants;

public class RandomPeerRequest extends RegisterRequest{

	//================================================================================
	// Overridden Constructors
	//================================================================================
	public RandomPeerRequest(String h, int p, int i){
		super.init(h, p, i);
		type = Constants.RandomPeer_Requst;
		
	}
	
	
	public RandomPeerRequest(){
		super.init("", 0, -1);
		type = Constants.RandomPeer_Requst;
	}
}
