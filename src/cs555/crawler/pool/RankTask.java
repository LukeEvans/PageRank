package cs555.crawler.pool;

import cs555.crawler.node.Worker;
import cs555.crawler.url.CrawlerState;
import cs555.crawler.url.Page;
import cs555.crawler.utilities.Constants;
import cs555.crawler.wireformats.RankData;

public class RankTask implements Task {

	CrawlerState state;
	Worker node;
	
	//================================================================================
	// Constructor
	//================================================================================
	public RankTask(CrawlerState cs, Worker w) {
		state = cs;
		node = w;
	}
	
	public void run() {
		System.out.println("Ranking");
		
		for (Page p : state.getAllPages()) {
			System.out.println("new page");
			for (String url : p.getOutgoingLinks()) {
				System.out.println("New outgoing link");
				
				Page outgoing = state.findPage(url);
				RankData data = new RankData(Constants.pageRank, p.getOutgoingScore(), url); 
				
				// If the link belongs to us, handle it
				if (outgoing != null) {
					System.out.println("handling : " + url);
					node.handlRanking(outgoing, data);
				}
				
				// Else, forward it
				else {
					System.out.println("forwarding : " + url);
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
