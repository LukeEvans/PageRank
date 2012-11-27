package cs555.crawler.node;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;


import cs555.crawler.communications.Link;
import cs555.crawler.crawlControl.CrawlComplete;
import cs555.crawler.crawlControl.CrawlElection;
import cs555.crawler.crawlControl.CrawlRequest;
import cs555.crawler.crawlControl.LocalCrawlComplete;
import cs555.crawler.url.CrawlerState;
import cs555.crawler.url.Page;
import cs555.crawler.url.WordList;
import cs555.crawler.utilities.Constants;
import cs555.crawler.utilities.Tools;
import cs555.crawler.wireformats.ElectionMessage;
import cs555.crawler.wireformats.PageRankInit;
import cs555.crawler.wireformats.Payload;
import cs555.crawler.wireformats.RankData;
import cs555.crawler.peer.Peer;
import cs555.crawler.peer.PeerList;
import cs555.crawler.pool.*;
import cs555.crawler.rankControl.BeginRound;
import cs555.crawler.rankControl.DomainInfo;
import cs555.crawler.rankControl.LocalRankingComplete;
import cs555.crawler.rankControl.RankElection;
import cs555.crawler.rankControl.RankInfo;
import cs555.crawler.rankControl.RoundComplete;

public class Worker extends Node{

	Peer nodeManager;
	ThreadPoolManager poolManager;
	String domain;
	CrawlerState state;
	Vector<RankInfo> incomingRankData;
	Vector<CrawlRequest> incomingCrawlRequests;

	PeerList peerList;

	Object crawlLock;
	boolean localCrawlDone;
	
	//================================================================================
	// Constructor
	//================================================================================
	public Worker(int port,int threads){
		super(port);
		nodeManager = null;
		//managerLink = null;
		poolManager = new ThreadPoolManager(threads);
		domain = new String();
		state = new CrawlerState();
		incomingRankData = new Vector<RankInfo>();
		incomingCrawlRequests = new Vector<CrawlRequest>();
		peerList = null;
		localCrawlDone = false;
	}


	public void initServer(){
		super.initServer();
		poolManager.start();
	}

	//================================================================================
	// Send
	//================================================================================
	public void sendData(Peer p, byte[] bytes) {
		SendTask send = new SendTask(p, bytes);
		poolManager.execute(send);
	}

	public void sendObject(Peer p, Object o) {
		sendData(p, Tools.objectToBytes(o));
	}

	//================================================================================
	// Receive
	//================================================================================
	// Receieve data
	public synchronized void receive(byte[] bytes, Link l){
		int messageType = Tools.getMessageType(bytes);

		Object obj = Tools.bytesToObject(bytes);

		if (obj == null) {
			return;
		}

		if (obj instanceof PeerList) {
			peerList = (PeerList) obj;

			// Init all links
			for (Peer p : peerList.getAllPeers()) {
				p.setLink(connect(p));
				p.initLink();
			}

			return;
		}

		// Crawler messages
		if (obj instanceof CrawlElection) {
			CrawlElection election = (CrawlElection) obj;
			nodeManager = new Peer(election.managerHost, election.managerPort);
			nodeManager.setLink(l);
			domain = election.domain;
			
//			byte[] peerBytes = nodeManager.waitForData();
//			Object peerObj = Tools.bytesToObject(peerBytes);
//			if (peerObj instanceof PeerList) {
//				peerList = (PeerList) peerObj;
//								
//				for (Peer p : peerList.getAllPeers()) {
//					p.setLink(connect(p));
//					p.initLink();
//				}
//			}
			
			// Begin Crawling
			System.out.println("Crawling " + domain + "...\n");
			CrawlRequest request = new CrawlRequest(election.domain, election.url, 0);
			publishLink(request);

			return;
		}

		if (obj instanceof CrawlRequest) {
			CrawlRequest request = (CrawlRequest) obj;
			incomingCrawlRequests.add(request);

			return;
		}
		
		if (obj instanceof LocalCrawlComplete) {
			//synchronized (crawlLock) {
				localCrawlDone = true;
			//}
			
			System.out.println("Crawling remote links : " + incomingCrawlRequests.size());
			crawlRemoteLinks();
			
			return;
		}
		
		if (obj instanceof CrawlComplete) {
			System.out.println("Crqwl Complete");
			//crawlComplete();
		}

		// Page Rank messages
		if (obj instanceof RankElection) {
			RankElection election = (RankElection) obj;

			nodeManager = new Peer(election.managerHost, election.managerPort);
			nodeManager.setLink(l);

			readFromDisk();

			DomainInfo reply = new DomainInfo(Tools.getLocalHostname(), serverPort, domain, state.crawledLinks());
			sendObject(nodeManager, reply);

			return;
		}

		if (obj instanceof BeginRound) {
			System.out.println("Begining round");
			RankTask ranker = new RankTask(state, this);
			poolManager.execute(ranker);

			return;
		}

		if (obj instanceof RankInfo) {
			RankInfo info = (RankInfo) obj;

			incomingRankData.add(info);

			return;
		}

		if (obj instanceof LocalRankingComplete) {
			tallyRemoteRanks();
			return;
		}

		if (obj instanceof RoundComplete) {
			rankComplete();
			return;
		}

		switch (messageType) {
		case Constants.Election_Message:
			ElectionMessage election = new ElectionMessage();
			election.unmarshall(bytes);

			nodeManager = new Peer(election.host, election.port);
			nodeManager.setLink(l);

			domain = election.domain;

			System.out.println(election);

			System.out.println("Crawling...\n");
			//			FetchRequest domainReq = new FetchRequest(election.domain, 0, election.url, new ArrayList<String>());
			//			publishLink(domainReq);

			break;

		case Constants.Fetch_Request:
			//			FetchRequest request = new FetchRequest();
			//			request.unmarshall(bytes);
			//
			//			publishLink(request);

			break;

		case Constants.Node_Complete:
			crawlComplete();
			System.exit(0);

			break;

		case Constants.Page_Rank_init:
			PageRankInit prInit = new PageRankInit();
			prInit.unmarshall(bytes);

			nodeManager = new Peer(prInit.host, prInit.port);
			nodeManager.setLink(l);

			readFromDisk();

			PageRankInit reply = new PageRankInit(serverPort, Tools.getLocalHostname(), domain, String.valueOf(state.crawledLinks()));
			sendData(nodeManager, reply.marshall());

			break;

		case Constants.Payload:
			Payload payload = new Payload();
			payload.unmarshall(bytes);

			if (payload.number == Constants.Page_Rank_Begin) {
				RankTask ranker = new RankTask(state, this);
				poolManager.execute(ranker);
			}

			else if (payload.number == Constants.PRContinue) {
				System.out.println("Tallying remote");
				tallyRemoteRanks();
			}

			else if (payload.number == Constants.PRComplete) {
				// Sort the crawl state
				state.sortCompleted();
				System.out.println(state.graphDiagnostics());
			}

			break;

		case Constants.Page_Rank_Transmit:
			RankData data = new RankData();
			data.unmarshall(bytes);

			//incomingRankData.add(data);

			break;

		default:
			System.out.println("Unrecognized Message");
			break;
		}

	}

	//================================================================================
	// Add links to crawl
	//================================================================================
	public void crawlRemoteLinks() {
		synchronized (incomingCrawlRequests) {
			for (CrawlRequest req : incomingCrawlRequests) {
				publishLink(req);
			}
		}
	}

	public void publishLink(CrawlRequest request) {

		// If this link doesn't belong to us, return
		if (!request.url.contains(domain) && !request.url.contains("chm.colostate.edu")) {
			System.out.println("Trying to publish url that doesn't belong to me. URL: " + request.url + " Domain: " + domain );
			return;
		}

		// Return if we're already at our max depth
		if (request.depth == Constants.depth) {
			return;
		}

		synchronized (state) {
			Page page = new Page(request.url, request.depth, request.domain);

			if (state.addPage(page)) {
				state.makrUrlPending(page);
				fetchURL(page, request);
			}

			// Add incoming link to this page
			if (request.incoming != null) {
				Page thisPage = state.findPage(page);

				if (thisPage != null) {
					thisPage.addIncomingLink(request.incoming);
				}
			}
		}
	}


	public void fetchURL(Page page, CrawlRequest request) {
		System.out.println("Fetching : " + request.url);
		FetchTask fetcher = new FetchTask(page, request, this);
		poolManager.execute(fetcher);
	}


	//================================================================================
	// Fetch Completion
	//================================================================================
	public void linkComplete(Page page, ArrayList<String> links, HashMap<String, Integer> fileMap, WordList wordList) {
		System.out.println("Link complete : " + page.urlString);

		synchronized (state) {
			Page p = state.findPendingUrl(page);

			if (p != null) {
				p.accumulate(links, wordList);
				state.markUrlComplete(page);
			}

			else {
				System.out.println("link completed that wasn't pending");
				return;
			}
		}

		for (String s : links) {
			// If we're tracking this domain handle it
			if (s.contains("." + domain)) {
				// My link
				CrawlRequest req = new CrawlRequest(page.domain, s, page.depth+1);
				publishLink(req);
			}

			// Else, hand it off
			else {
				Peer leader = peerList.findDomainLeader(s);

				if (leader != null) {
					CrawlRequest req = new CrawlRequest(leader.domain, s, page.depth+1, page.urlString);
					sendObject(leader, req);
				}
			}
		}

		// If we're done, print
		if (!state.shouldContinue()) {
			System.out.println("Sending complete message :)");
			sendCompleteMessage();
		}	

	}

	public void linkErrored(Page page) {
		//System.out.println("Error on page : " + page.urlString);

		synchronized (state) {
			state.markUrlError(page);

			// If we're done, print
			if (!state.shouldContinue()) {
				sendCompleteMessage();
			}
		}
	}

	public void sendCompleteMessage() {
		System.out.println("complete message method");
		//synchronized (crawlLock) {
			if (localCrawlDone) {
				System.out.println("Sending global");
				CrawlComplete global = new CrawlComplete(Tools.getLocalHostname(), serverPort);
				sendObject(nodeManager, global);
			}
			
			else {
				System.out.println("Sending local");
				LocalCrawlComplete local = new LocalCrawlComplete(Tools.getLocalHostname(), serverPort);
				sendObject(nodeManager, local);
			}
		//}
	}
	//================================================================================
	// Page Rank Methods 
	//================================================================================
	public void handlRanking(Page p, RankInfo data) {
		synchronized (state) {
			Page page = state.findPage(p);

			if (page != null) {
				page.tallyRankData(data);
			}
		}
	}

	public void forwardRanking(RankInfo data) {
		Peer leader = peerList.findDomainLeader(data.url);

		if (leader != null) {
			sendObject(leader, data);
		}

		else {
			System.out.println("leader is null");
		}
	}

	public void localRankingComplete() {
		//System.out.println("Sending local complete");
		LocalRankingComplete complete = new LocalRankingComplete(Tools.getLocalHostname(), serverPort);

		//Tools.sleep(1);
		sendObject(nodeManager, complete);
	}

	public void tallyRemoteRanks() {
		synchronized (incomingRankData) {
			// Start processing the ranks that came in
			for (RankInfo data : incomingRankData) {
				Page page = state.findPage(data.url);

				if (page != null) {
					page.tallyRankData(data);
				}

			}

			incomingRankData.clear();
		}

		// Tell the node manager that we're done with this round
		RoundComplete complete = new RoundComplete(Tools.getLocalHostname(), serverPort);
		sendObject(nodeManager, complete);
	}

	//================================================================================
	// Printing
	//================================================================================
	public void printDomainInfo() {
		synchronized (state) {
			System.out.println("\n================================================================================");
			System.out.println("Diagnostics for domain : " + domain);
			System.out.println(state.diagnostics());
			System.out.println("================================================================================\n");	
		}
	}

	public void printNodeInfo() {
		synchronized (state) {
			System.out.println("\n================================================================================");
			System.out.println("Diagnostics for domain : " + domain);
			System.out.println("Crawled Links : " + state.crawledLinks());
			System.out.println("================================================================================\n");	
		}
	}

	public void readFromDisk() {
		File folder = new File(Constants.base_path);

		for (File fileEntry : folder.listFiles()) {
			if (fileEntry.exists() && fileEntry.isFile()) {
				String fileString = fileEntry.getName();

				System.out.println("file String : " + fileString);

				if (fileString.endsWith(".results")) {

					domain = Tools.inflateURL(fileString.replace(".results", ""));

					System.out.println("domain : " + domain);

					// Read an object
					Object obj;
					try {
						// Read from disk using FileInputStream
						FileInputStream f_in = new FileInputStream(fileEntry.getAbsolutePath());

						// Read object using ObjectInputStream
						ObjectInputStream obj_in = new ObjectInputStream (f_in);

						obj = obj_in.readObject();

						if (obj instanceof CrawlerState) {
							// Cast object to a State
							state = (CrawlerState) obj;

							// Do something with state....
							//System.out.println("Read state : " + state.graphDiagnostics());
							break;
						}

						else {
							System.out.println("State could not be read from file");
						}

						obj_in.close();

					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
	}

	public void saveToDisk() throws IOException {

		String flatDomain = Tools.flattenURL(domain);

		synchronized (state) {
			// Write to disk with FileOutputStream
			FileOutputStream f_out = new FileOutputStream(Constants.base_path + flatDomain + ".results");

			// Write object with ObjectOutputStream
			ObjectOutputStream obj_out = new ObjectOutputStream (f_out);

			// Write object out to disk
			obj_out.writeObject (state);

			obj_out.close();
		}
	}

	public void crawlComplete() {

		nodeManager.closeLink();

		synchronized (state) {
			state.completeGraph();
		}

		printNodeInfo();

		try {
			saveToDisk();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Could not save crawl state to disk");
			e.printStackTrace();
		}
	}

	public void rankComplete() {
		// Finalize scores
		for (Page p : state.getCompletedPages()) {
			p.rankRoundComplete();
		}

		// Sort the completed links
		state.sortCompleted();

		System.out.println("Page Rank Complete: \n" + state.graphDiagnostics());
	}
	//================================================================================
	//================================================================================
	// Main
	//================================================================================
	//================================================================================
	public static void main(String[] args){

		int port = 0;
		int threads = 5;

		if (args.length == 1) {
			port = Integer.parseInt(args[0]);
		}

		else if (args.length == 2){
			port = Integer.parseInt(args[0]);
			threads = Integer.parseInt(args[1]);
		}

		else {
			System.out.println("Usage: java node.Worker PORT <THREADS>");
			System.exit(1);
		}


		// Create node
		Worker worker = new Worker(port,threads);
		worker.initServer();

	}
}
