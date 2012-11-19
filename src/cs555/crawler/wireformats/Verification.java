package cs555.crawler.wireformats;

import cs555.crawler.utilities.Constants;

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
