package cs555.crawler.node;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;

import cs555.crawler.communications.Link;
import cs555.crawler.crawlControl.CrawlComplete;
import cs555.crawler.crawlControl.CrawlElection;
import cs555.crawler.crawlControl.CrawlRequest;
import cs555.crawler.crawlControl.LocalCrawlComplete;
import cs555.crawler.peer.Peer;
import cs555.crawler.peer.PeerList;
import cs555.crawler.rankControl.BeginRound;
import cs555.crawler.rankControl.DomainInfo;
import cs555.crawler.rankControl.LocalRankingComplete;
import cs555.crawler.rankControl.RankElection;
import cs555.crawler.rankControl.RoundComplete;
import cs555.crawler.url.CrawlerState;
import cs555.crawler.url.Page;
import cs555.crawler.utilities.Constants;
import cs555.crawler.utilities.Tools;
import cs555.search.common.AccessPoint;
import cs555.search.common.AccessPointList;
import cs555.search.common.Word;
import cs555.search.common.WordSet;

public class NodeManager extends Node{

	CrawlerState state;
	PeerList peerList;

	String linkFile;
	String slaveFile;
	int maxDepth;
	
	int RankRound;
	
	int wordCount;

	Vector<String> chard;
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
		wordCount = 0;
		chard = new Vector<String>();
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
	
	public void sendObject(Peer p, Object o) {
		sendData(p, Tools.objectToBytes(o));
	}
	
	public void broadcastObject(Object o) {
		byte[] data = Tools.objectToBytes(o);
		
		for (Peer p : peerList.getAllPeers()) {
			p.ready = false;
			sendData(p, data);
		}
	}
	
	//================================================================================
	// Begin Crawling
	//================================================================================
	public void broadcastElection(){

		ArrayList<Page> allDomains = new ArrayList<Page>(state.getAllPages());

		// Build Peer List
		for (Page page : allDomains) {
			System.out.println("Looking for place for domain : " + page.domain);
			state.makrUrlPending(page);

			Peer peer = peerList.getReadyPeer();
			peer.setDomain(page.domain);
			peer.setSeedUrl(page.urlString);
			peer.setLink(connect(peer));
			peer.initLink();

		}
		
		// Send peer list
		broadcastObject(peerList);
		
		// Send election messages
		for (Peer p : peerList.getAllPeers()) {
			CrawlElection election = new CrawlElection(Tools.getLocalHostname(), serverPort, p.domain, p.seedURL);
			sendObject(p, election);
		}
		
	}
	
	//================================================================================
	// Begin Page Rank
	//================================================================================
	public void beginPageRank() {
		RankElection election = new RankElection(Tools.getLocalHostname(), serverPort);
		int totalCrawled = 0;

		synchronized (peerList) {
			for (Peer p : peerList.getAllPeers()) {
				p.ready = false;
				p.setLink(connect(p));
				sendObject(p, election);

				// Wait for machine's domain
				byte[] bytes = p.waitForData();

				Object obj = Tools.bytesToObject(bytes);
				
				if (obj == null) {
					System.out.println("Null domain info");
					return;
				}
				
				if (obj instanceof DomainInfo) {
					DomainInfo domainInfo = (DomainInfo) obj;
					p.hostname = domainInfo.hostname;
					p.port = domainInfo.port;
					p.domain = domainInfo.domain;
					p.initLink();
					
					totalCrawled += domainInfo.linksCrawled;
					
					System.out.println("got reply : " + p.hostname + " has " + p.domain);
						
				}
			}
		}

		System.out.println("Total Links Crawled : " + totalCrawled);
		System.out.println("Sending peer list"); 
		
		for (Peer p : peerList.getAllPeers()) {
			sendObject(p, peerList);
		}
		
		beginRound();
	}

	public void beginRound() {
		if (RankRound == Constants.Page_Rank_Rounds) {
			System.out.println("Page Rank complete");
			return;
		}
		
		RankRound++;
		BeginRound begin = new BeginRound(RankRound);
		broadcastObject(begin);		
	}
	
	
	//================================================================================
	// Begin DHT Seeding
	//================================================================================
	public void beginSeedingDHT(String host, int port) {
//		Peer dhtManager = new Peer(host, port);
//		dhtManager.setLink(connect(dhtManager));
//		
//		AccessPointList dhtNodes = new AccessPointList(peerList.size());
//		sendObject(dhtManager, dhtNodes);
//		
//		byte[] bytes = dhtManager.waitForData();
//		Object obj = Tools.bytesToObject(bytes);
//		
//		if (obj instanceof AccessPointList) {
//			AccessPointList accesPoints = (AccessPointList) obj;
//			
//			for (AccessPoint point : accesPoints.accessPeers) {
//				Peer worker = peerList.getReadyPeer();
//				worker.setLink(connect(worker));
//				worker.initLink();
//				
//				System.out.println("Sendig access point to peer: " + worker.hostname);
//				sendObject(worker, point);
//			}
//		}
		
		for (Peer p : peerList.getAllPeers()) {
			p.setLink(connect(p));
			p.initLink();
		}
		
		//testing
		AccessPoint ap = new AccessPoint(Tools.getLocalHostname(), serverPort);
		broadcastObject(ap);
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
		
		// Crawler Messages
		if (obj instanceof LocalCrawlComplete) {
			LocalCrawlComplete complete = (LocalCrawlComplete) obj;
			//System.out.println("local complete from : " + complete.host);
			Peer donePeer = peerList.findPeer(complete.host, complete.port);
			
			if (donePeer != null) {
				donePeer.ready = true;
			}
			
			if (peerList.allPeersDone()) {
				CrawlComplete crawlComplete = new CrawlComplete(Tools.getLocalHostname(), serverPort);
				broadcastObject(crawlComplete);
			}
			
			return;
		}
		
		if (obj instanceof CrawlRequest) {
			CrawlRequest handoff = (CrawlRequest) obj;
			Peer peer = peerList.findDomainLeader(handoff.url);
			if (peer != null) {
				peer.ready = false;
			}
			
			return;
		}
		
		// Page Rank Messages
		if (obj instanceof LocalRankingComplete) {
			LocalRankingComplete complete = (LocalRankingComplete) obj;
			Peer donePeer = peerList.findPeer(complete.host, complete.port);
			
			if (donePeer != null) {
				donePeer.ready = true;
			}
			
			if (peerList.allPeersDone()) {
				LocalRankingComplete localComplete = new LocalRankingComplete(Tools.getLocalHostname(), serverPort);
				broadcastObject(localComplete);
			}
			
			return;
		}
		
		if (obj instanceof RoundComplete) {
			RoundComplete complete = (RoundComplete) obj;
			
			Peer donePeer = peerList.findPeer(complete.host, complete.port);
			
			if (donePeer != null) {
				donePeer.ready = true;
			}
			
			if (peerList.allPeersDone()) {
				
				if (RankRound < Constants.Page_Rank_Rounds) {
					beginRound();
				}
				
				else {
					RoundComplete pageRankComplete = new RoundComplete(Tools.getLocalHostname(), serverPort);
					broadcastObject(pageRankComplete);
				}
			}
			
			return;
		}
		
		if (obj instanceof WordSet) {
			WordSet set = (WordSet) obj;
			System.out.println("Got set: " + set + " from " + l.remoteHost);
			
			return;
		}
		
		if (obj instanceof Word) {
			wordCount++;
			System.out.println("count : " + wordCount);
			return;
		}
		
		if (obj instanceof AccessPoint) {
			System.out.println("Holy mother fucking shit");
		}
		
		System.out.println("unrecognized");
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
		String dhtManagerHost = "";
		int dhtManagerPort = 0;
		
		if (args.length == 4) {
			port = Integer.parseInt(args[0]);
			linkFile = args[1];
			slaveFile = args[2];
			workType = args[3];
		}

		else if (args.length == 6) {
			port = Integer.parseInt(args[0]);
			linkFile = args[1];
			slaveFile = args[2];
			workType = args[3];
			dhtManagerHost = args[4];
			dhtManagerPort = Integer.parseInt(args[5]);
		}

		else {
			System.out.println("Usage: java node.NodeManager PORT LINK-FILE SLAVE-FILE TYPE <dht manager host> <dht manager port>");
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
		
		else if (workType.equalsIgnoreCase("seed")) {
			// Begin seeding dht
			manager.beginSeedingDHT(dhtManagerHost, dhtManagerPort);
		}

		else {
			System.out.println("Unrecognized type of request. 'crawl' or 'rank' expected");
		}

	}
}
