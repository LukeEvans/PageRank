package cs555.crawler.wireformats;

import cs555.crawler.utilities.Constants;

public class LocalRankComplete extends Payload{

	//================================================================================
	// Overridden Constructors
	//================================================================================
	public LocalRankComplete(int number){
		super(number);
		type = Constants.Local_Complete;
	}
	
	public LocalRankComplete(){
		super();
		type = Constants.Local_Complete;
	}
}