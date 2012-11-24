package cs555.crawler.wireformats;

import cs555.crawler.utilities.Constants;

public class PageRankRoundComplete extends Payload{

	//================================================================================
	// Overridden Constructors
	//================================================================================
	public PageRankRoundComplete(int number){
		super(number);
		type = Constants.PRound_Complete;
	}
	
	public PageRankRoundComplete(){
		super();
		type = Constants.PRound_Complete;
	}
}