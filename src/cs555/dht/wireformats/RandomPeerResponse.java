package cs555.dht.wireformats;

import cs555.dht.utilities.Constants;

public class RandomPeerResponse extends RegisterRequest{

	//================================================================================
	// Overridden Constructors
	//================================================================================
	public RandomPeerResponse(String h, int p, int i){
		super.init(h, p, i);
		type = Constants.RandomPeer_Response;
		
	}
	
	
	public RandomPeerResponse(){
		super.init("", 0, -1);
		type = Constants.RandomPeer_Response;
	}
}
