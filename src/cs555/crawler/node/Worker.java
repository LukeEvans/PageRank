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
import cs555.crawler.peer.Peer;
import cs555.crawler.peer.PeerList;
import cs555.crawler.pool.*;
import cs555.crawler.rankControl.BeginRound;
import cs555.crawler.rankControl.DomainInfo;
import cs555.crawler.rankControl.LocalRankingComplete;
import cs555.crawler.rankControl.RankElection;
import cs555.crawler.rankControl.RankInfo;
import cs555.crawler.rankControl.RoundComplete;
import cs555.search.common.AccessPoint;
import cs555.search.common.Continue;
import cs555.search.common.Word;
import cs555.search.common.WordSet;

public class Worker extends Node{

	Peer nodeManager;
	ThreadPoolManager poolManager;
	String domain;
	CrawlerState state;
	Vector<RankInfo> incomingRankData;
	Vector<CrawlRequest> incomingCrawlRequests;

	PeerList peerList;

	int rankRound;

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
		rankRound = 0;
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

			// Begin Crawling
			System.out.println("Crawling " + domain + "...\n");
			CrawlRequest request = new CrawlRequest(election.domain, election.url, 0);
			publishLink(request);

			return;
		}

		if (obj instanceof CrawlRequest) {
			CrawlRequest request = (CrawlRequest) obj;
			publishLink(request);

			return;
		}

		if (obj instanceof CrawlComplete) {
			crawlComplete();
			return;
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
			rankRound++;
			System.out.println("Begining round: " + rankRound);
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


		// DHT Seeding Messages
		if (obj instanceof AccessPoint) {

			readFromDisk();

			AccessPoint accessPoint = (AccessPoint) obj;
			Peer peer = new Peer(accessPoint.host, accessPoint.port);
			peer.setLink(connect(peer));

			System.out.println("Got access point : " + peer.hostname);

			WordSet words = state.getWordSet();

			Continue cont = new Continue("cont");


			System.out.println("Sending continue");
			Peer man = new Peer("bean", 5678);
			man.setLink(connect(man));
			sendObject(man, cont);
			System.out.println("Sent continue");
			
			Tools.sleep(1);

			System.out.println("Sending words");
			Word w = words.words.get(0);
			Tools.writeObject(man.link, words);
			System.out.println("Sent words");




			//			int i=0;
			//			for (WordSet chunk : words.getChunks()) {
			//				if (i==1) {
			//					//break;
			//				}
			//
			//				sendObject(peer, chunk);
			//				System.out.println("Sent 1 chunk: " + chunk);
			//
			//				Object reply = Tools.bytesToObject(peer.waitForData());
			//				
			//				if (!(reply instanceof Continue)) {
			//					System.out.println("Got a reply that was not a continue");
			//					break;
			//				}
			//				i++;
			//
			//			}

			System.exit(0);

			//saveWords(words);
			//System.out.println("Words saved");

		}
	}

	//================================================================================
	// Add links to crawl
	//================================================================================
	public void crawlRemoteLinks() {
		synchronized (incomingCrawlRequests) {
			Vector<CrawlRequest> temp = new Vector<CrawlRequest>(incomingCrawlRequests);

			for (CrawlRequest req : temp) {
				publishLink(req);

				if (incomingCrawlRequests.size() > 0) {
					incomingCrawlRequests.remove(0);
				}
			}
		}
	}

	public void publishLink(CrawlRequest request) {

		// If this link doesn't belong to us, return
		if (!request.url.contains(domain) && !request.url.contains("chm.colostate.edu")) {
			System.out.println("Trying to publish url that doesn't belong to me. URL: " + request.url + " Domain: " + domain );
			if (!state.shouldContinue()) {
				sendCompleteMessage();
			}
			return;
		}

		// Return if we're already at our max depth
		if (request.depth == Constants.depth) {
			if (!state.shouldContinue()) {
				sendCompleteMessage();
			}
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

		if (!state.shouldContinue()) {
			sendCompleteMessage();
		}
	}


	public void fetchURL(Page page, CrawlRequest request) {
		//System.out.println("Fetching : " + request.url);
		FetchTask fetcher = new FetchTask(page, request, this);
		poolManager.execute(fetcher);
	}


	//================================================================================
	// Fetch Completion
	//================================================================================
	public void linkComplete(Page page, ArrayList<String> links, HashMap<String, Integer> fileMap, WordList wordList) {
		//System.out.println("Link complete : " + page.urlString);

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
					sendObject(nodeManager, req);
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
		LocalCrawlComplete local = new LocalCrawlComplete(Tools.getLocalHostname(), serverPort);
		sendObject(nodeManager, local);
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

	public void saveWords(WordSet set) {
		String flatDomain = Tools.flattenURL(domain);

		// Write to disk with FileOutputStream
		FileOutputStream f_out;
		try {
			f_out = new FileOutputStream(Constants.base_path + flatDomain + ".words");
			// Write object with ObjectOutputStream
			ObjectOutputStream obj_out = new ObjectOutputStream (f_out);

			// Write object out to disk
			obj_out.writeObject (state);

			obj_out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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

		try {
			saveToDisk();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Could not save state to Disk");
			e.printStackTrace();
		}

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
