package cs555.dht.wireformats;

import cs555.dht.utilities.Constants;

public class Verification extends Payload{

	//================================================================================
	// Overridden Constructors
	//================================================================================
	public Verification(int number){
		super(number);
		type = Constants.Verification;
	}
	
	public Verification(){
		super();
		type = Constants.Verification;
	}
}
