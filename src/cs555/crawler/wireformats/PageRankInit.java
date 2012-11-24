package cs555.crawler.wireformats;

import cs555.crawler.utilities.Constants;

public class PageRankInit extends ElectionMessage{

	//================================================================================
	// Overridden Constructors
	//================================================================================
	public PageRankInit(int p, String h, String d, String u){
		super(p,h,d,u);
		type = Constants.Page_Rank_init;
	}
	
	public PageRankInit(){
		super();
		type = Constants.Page_Rank_init;
	}

}
