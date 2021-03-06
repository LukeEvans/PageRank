package cs555.crawler.node;

import java.io.IOException;
import java.util.ArrayList;

import cs555.crawler.communications.Link;
import cs555.crawler.peer.Peer;
import cs555.crawler.peer.PeerList;
import cs555.crawler.url.CrawlerState;
import cs555.crawler.url.Page;
import cs555.crawler.utilities.Constants;
import cs555.crawler.utilities.Tools;
import cs555.crawler.wireformats.ElectionMessage;
import cs555.crawler.wireformats.FetchRequest;
import cs555.crawler.wireformats.FetchResponse;
import cs555.crawler.wireformats.HandoffLookup;
import cs555.crawler.wireformats.LocalRankComplete;
import cs555.crawler.wireformats.NodeComplete;
import cs555.crawler.wireformats.PageRankInit;
import cs555.crawler.wireformats.PageRankRoundComplete;
import cs555.crawler.wireformats.Payload;
import cs555.crawler.wireformats.RankData;

public class NodeManager extends Node{

	CrawlerState state;
	PeerList peerList;

	String linkFile;
	String slaveFile;
	int maxDepth;
	
	int RankRound;

	//================================================================================
	// Constructor
	//================================================================================
	public NodeManager(CrawlerState s, PeerList list, int port,String lf, String sf){
		super(port);

		peerList = list;
		state = s;
		linkFile = lf;
		slaveFile = sf;
		maxDepth = Constants.depth;

		RankRound = 0;
	}
	
	//================================================================================
	// Send
	//================================================================================
	public void sendData(Peer p, byte[] bytes) {
		try {
			p.sendData(bytes);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void broadcastElection(){

		ArrayList<Page> allDomains = new ArrayList<Page>(state.getAllPages());

		for (Page page : allDomains) {
			System.out.println("Looking for place for domain : " + page.domain);
			state.makrUrlPending(page);

			Peer peer = peerList.getReadyPeer();
			peer.setDomain(page.domain);
			peer.setLink(connect(peer));
			peer.initLink();

			synchronized (state) {
				ElectionMessage electionMsg = new ElectionMessage(serverPort, Tools.getLocalHostname(), page.domain, page.urlString);
				sendData(peer, electionMsg.marshall());
			}


		}
	}

	public void broadcastCompletion() {
		NodeComplete complete = new NodeComplete(Constants.Node_Complete);

		for (Peer p : peerList.getAllPeers()) {
			sendData(p, complete.marshall());
		}
	}

	public void broadcastContinue(int type) {
		Payload cont = new Payload(type);

		for (Peer p : peerList.getAllPeers()) {
			p.ready = false;
			sendData(p, cont.marshall());
		}
	}

	public void beginPageRank() {
		PageRankInit prInit = new PageRankInit(serverPort, Tools.getLocalHostname(), Constants.pageRank, Constants.pageRank);
		int totalCrawled = 0;

		synchronized (peerList) {
			for (Peer p : peerList.getAllPeers()) {
				p.ready = false;
				p.setLink(connect(p));
				sendData(p, prInit.marshall());

				// Wait for machine's domain
				byte[] bytes = p.waitForData();

				if (Tools.getMessageType(bytes) == Constants.Page_Rank_init) {
					PageRankInit reply = new PageRankInit();
					reply.unmarshall(bytes);

					p.hostname = reply.host;
					p.port = reply.port;
					p.domain = reply.domain;

					System.out.println("got reply : " + p.hostname + " has " + p.domain);

					p.initLink();
					totalCrawled += Integer.parseInt(reply.url);
				}
			}
		}

		System.out.println("Total Links Crawled : " + totalCrawled);
		beginRound();
	}

	public void beginRound() {
		if (RankRound == Constants.Page_Rank_Rounds) {
			System.out.println("Page Rank complete");
			return;
		}
		
		RankRound++;
		broadcastContinue(Constants.Page_Rank_Begin);
		
	}
	
	//================================================================================
	// Receive
	//================================================================================
	// Receieve data
	public synchronized void receive(byte[] bytes, Link l){
		int messageType = Tools.getMessageType(bytes);

		//System.out.println("message : " + messageType);
		
		switch (messageType) {
		case Constants.Fetch_Response:

			FetchResponse response = new FetchResponse();
			response.unmarshall(bytes);

			System.out.println("Got: " + response);

			break;

		case Constants.Handoff_Lookup:

			try {
				HandoffLookup lookup = new HandoffLookup();
				lookup.unmarshall(bytes);
				
				Peer leader = peerList.findDomainLeader(lookup.url);

				if (leader != null) {
					leader.ready = false;	

					FetchRequest handoff = new FetchRequest(leader.domain, lookup.depth, lookup.url, lookup.links);
					sendData(leader, handoff.marshall());
				}
			} catch (Exception e) {
				// TODO: handle exception
				System.out.println("Got a handoff request for some reason");
			}


			break;

		case Constants.Node_Complete:

			NodeComplete complete = new NodeComplete();
			complete.unmarshall(bytes);
			
			Peer p = peerList.findPeer(l.remoteHost, complete.number);

			//System.out.println("complete from : " + l.remoteHost);
			
			if (p != null) {
				p.ready = true;
			}

			if (peerList.allPeersDone()) {
				// Broadcast to everyone to print data
				broadcastCompletion();
			}	
			
			else {
				//System.out.println("remaining : " + peerList.numberRemainingPeers());
			}

			break;

		case Constants.Local_Complete:
			LocalRankComplete localComplete = new LocalRankComplete();
			localComplete.unmarshall(bytes);
			
			Peer peer = peerList.findPeer(l.remoteHost, localComplete.number);

			//System.out.println("\n\nmessage\n\n");
			
			//System.out.println("Complete message from : " + peer.hostname);
			
			if (peer != null) {
				peer.ready = true;
			}

			if (peerList.allPeersDone()) {
				System.out.println("Sending continue");
				broadcastContinue(Constants.PRContinue);
			}	
			
			else {
				//System.out.println("remaining : " + peerList.numberRemainingPeers());
			}
			
			break;
			
		case Constants.PRound_Complete:
			PageRankRoundComplete roundComplete = new PageRankRoundComplete();
			roundComplete.unmarshall(bytes);
			
			//System.out.println("\nyoyoyo got round complete from : " + l.remoteHost);
			
			Peer worker = peerList.findPeer(l.remoteHost, roundComplete.number);

			if (worker != null) {
				worker.ready = true;
			}

			if (peerList.allPeersDone()) {
				
				if (RankRound < Constants.Page_Rank_Rounds) {
					beginRound();
				}
				
				else {
					broadcastContinue(Constants.PRComplete);
				}
			}	
			
			break;
		
		case Constants.Page_Rank_Transmit:
			RankData data = new RankData();
			data.unmarshall(bytes);
			
			//System.out.println("transmit from : " + l.remoteHost);
			
			Peer prLeader = peerList.findDomainLeader(data.url);

			if (prLeader != null) {
				sendData(prLeader, data.marshall());
			}
			
			break;
			
		default:

			System.out.println("Unrecognized Message: " + messageType + " from: " + l.remoteHost);
			break;
		}

	}


	//================================================================================
	//================================================================================
	// Main
	//================================================================================
	//================================================================================
	public static void main(String[] args){

		int port = 0;
		String linkFile = "";
		String slaveFile = "";
		String workType = "";

		if (args.length == 4) {
			port = Integer.parseInt(args[0]);
			linkFile = args[1];
			slaveFile = args[2];
			workType = args[3];
		}


		else {
			System.out.println("Usage: java node.NodeManager PORT LINK-FILE SLAVE-FILE TYPE");
			System.exit(1);
		}

		// Create peer list
		PeerList peerList = new PeerList(slaveFile, port);
		CrawlerState state = new CrawlerState(linkFile);

		// Create node
		NodeManager manager = new  NodeManager(state, peerList, port, linkFile, slaveFile);
		manager.initServer();

		// Crawl Request
		if (workType.equalsIgnoreCase("crawl")) {
			// Broadcast our election message
			manager.broadcastElection();
		}

		// Page Rank Request
		else if (workType.equalsIgnoreCase("rank")) {
			// Begin page rank
			manager.beginPageRank();
		}

		else {
			System.out.println("Unrecognized type of request. 'crawl' or 'rank' expected");
		}

	}
}
