package cs555.crawler.pool;

import cs555.crawler.node.Worker;
import cs555.crawler.url.CrawlerState;

public class RankTask implements Task {

	CrawlerState state;
	Worker node;
	String domain;
	
	//================================================================================
	// Constructor
	//================================================================================
	public RankTask(CrawlerState cs, Worker w, String d) {
		state = cs;
		node = w;
		domain = d;
	}
	
	public void run() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setRunning(int i) {
		// TODO Auto-generated method stub
		
	}

	

}
