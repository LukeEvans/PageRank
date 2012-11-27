package cs555.crawler.pool;

import cs555.crawler.node.Worker;
import cs555.crawler.rankControl.RankInfo;
import cs555.crawler.url.CrawlerState;
import cs555.crawler.url.Page;

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
		
		for (Page p : state.getCompletedPages()) {
			for (String url : p.getOutgoingLinks()) {

				Page outgoing = state.findPage(url);
				RankInfo data = new RankInfo(url, p.getOutgoingScore());
				
				// If the link belongs to us, handle it
				if (outgoing != null) {
					node.handlRanking(outgoing, data);
				}

				// Else, forward it
				else {
					if (!url.contains(p.domain)) {
						node.forwardRanking(data);
					}
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
