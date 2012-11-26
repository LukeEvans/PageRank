package cs555.crawler.pool;

import java.util.ArrayList;

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

		//System.out.println("\n\nRANKING STATE : " + state.graphDiagnostics());

		
		for (Page p : state.getCompletedPages()) {
			for (String url : p.getOutgoingLinks()) {

				Page outgoing = state.findPage(url);
				RankData data = new RankData(Constants.pageRank, p.getOutgoingScore(), url); 

				
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
