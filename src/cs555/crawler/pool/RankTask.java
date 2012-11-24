package cs555.crawler.pool;

import cs555.crawler.node.Worker;
import cs555.crawler.url.CrawlerState;
import cs555.crawler.url.Page;
import cs555.crawler.utilities.Constants;
import cs555.crawler.wireformats.RankData;

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
		
		for (Page p : state.getAllPages()) {
			for (String url : p.getOutgoingLinks()) {
				Page outgoing = state.findPage(url);
				RankData data = new RankData(Constants.pageRank, p.getOutgoingScore(), url); 
				
				// If the link belongs to us, handle it
				if (outgoing != null) {
					outgoing.tallyRankData(data);
				}
				
				// Else, forward it
				else {
					node.forwardRanking(data);
				}
			}
		}
		
		node.localRankingComplete();
	}

	@Override
	public void setRunning(int i) {
		// TODO Auto-generated method stub
		
	}

	

}
