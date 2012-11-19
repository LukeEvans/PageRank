package cs555.crawler.node;

import java.util.ArrayList;
import java.util.HashMap;

import cs555.crawler.communications.Link;
import cs555.crawler.url.CrawlerState;
import cs555.crawler.url.Page;
import cs555.crawler.utilities.Constants;
import cs555.crawler.utilities.Tools;
import cs555.crawler.wireformats.ElectionMessage;
import cs555.crawler.wireformats.FetchRequest;
import cs555.crawler.wireformats.HandoffLookup;
import cs555.crawler.wireformats.NodeComplete;
import cs555.crawler.wireformats.Verification;
import cs555.crawler.peer.Peer;
import cs555.crawler.pool.*;

public class Worker extends Node{

	Peer nodeManager;
	ThreadPoolManager poolManager;
	String domain;
	CrawlerState state;

	//================================================================================
	// Constructor
	//================================================================================
	public Worker(int port,int threads){
		super(port);
		nodeManager = null;

		poolManager = new ThreadPoolManager(threads);
		domain = new String();
		state = new CrawlerState();
	}


	public void initServer(){
		super.initServer();
		poolManager.start();
	}

	//================================================================================
	// Receive
	//================================================================================
	// Receieve data
	public synchronized void receive(byte[] bytes, Link l){
		int messageType = Tools.getMessageType(bytes);

		switch (messageType) {
		case Constants.Election_Message:
			ElectionMessage election = new ElectionMessage();
			election.unmarshall(bytes);

			Verification electionReply = new Verification(election.type);
			l.sendData(electionReply.marshall());

			nodeManager = new Peer(election.host, election.port);
			domain = election.domain;

			System.out.println(election);

			System.out.println("Crawling...\n");
			FetchRequest domainReq = new FetchRequest(election.domain, 0, election.url, new ArrayList<String>());
			publishLink(domainReq);

			break;

		case Constants.Fetch_Request:
			FetchRequest request = new FetchRequest();
			request.unmarshall(bytes);

			//System.out.println("Got handoff\n" + request.url + " Depth: " + request.depth);

			publishLink(request);

			break;

		case Constants.Node_Complete:
			printDomainInfo();
			System.exit(0);
			
			break;
			
		default:
			System.out.println("Unrecognized Message");
			break;
		}

		l.close();
	}

	//================================================================================
	// Add links to crawl
	//================================================================================
	public void publishLink(FetchRequest request) {

		// Return if we're already at our max depth
		if (request.depth == Constants.depth) {
			//System.out.println("Depth is at : " + request.depth);
			return;
		}

		synchronized (state) {
			Page page = new Page(request.url, request.depth, request.domain);
			
			if (state.addPage(page)) {
				state.makrUrlPending(page);
				fetchURL(page, request);
			}

		}
	}


	public void fetchURL(Page page, FetchRequest request) {
		
		//System.out.println("Fetching : " + request.url);
		FetchTask fetcher = new FetchTask(page, request, this);
		//FetchParseTask fetcher = new FetchParseTask(page, request, this);
		poolManager.execute(fetcher);
	}


	//================================================================================
	// Fetch Completion
	//================================================================================
	public void linkComplete(Page page, ArrayList<String> links, HashMap<String, Integer> fileMap) {
		//System.out.println("Link complete : " + page.urlString);
		
		synchronized (state) {
			state.findPendingUrl(page).accumulate(links, fileMap);
			state.markUrlComplete(page);
		}

		for (String s : links) {
			// If we're tracking this domain handle it
			if (s.contains("." + domain)) {
				//System.out.println("Mine " + s);
				FetchRequest req = new FetchRequest(page.domain, page.depth + 1, s, new ArrayList<String>());
				publishLink(req);
			}

			// Else, hand it off
			else {
				Link managerLink = connect(nodeManager);

				//System.out.println("Does not contain my domain : " + domain + " =? " + s);
				HandoffLookup handoff = new HandoffLookup(s, page.depth + 1, s, new ArrayList<String>());
				managerLink.sendData(handoff.marshall());

				managerLink.close();
			}
		}

		// If we're done, print
		if (!state.shouldContinue()) {
			NodeComplete complete = new NodeComplete(Constants.Node_Complete);
			sendBytes(nodeManager, complete.marshall());
		}	

	}

	public void linkErrored(Page page) {
		//System.out.println("Error on page : " + page.urlString);

		synchronized (state) {
			state.markUrlError(page);

			// If we're done, print
			if (!state.shouldContinue()) {
				NodeComplete complete = new NodeComplete(Constants.Node_Complete);
				sendBytes(nodeManager, complete.marshall());
			}
		}
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
