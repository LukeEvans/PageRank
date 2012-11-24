package cs555.crawler.node;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;


import cs555.crawler.communications.Link;
import cs555.crawler.url.CrawlerState;
import cs555.crawler.url.Page;
import cs555.crawler.url.WordList;
import cs555.crawler.utilities.Constants;
import cs555.crawler.utilities.Tools;
import cs555.crawler.wireformats.ElectionMessage;
import cs555.crawler.wireformats.FetchRequest;
import cs555.crawler.wireformats.HandoffLookup;
import cs555.crawler.wireformats.NodeComplete;
import cs555.crawler.wireformats.PageRankInit;
import cs555.crawler.wireformats.Payload;
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


			publishLink(request);

			break;

		case Constants.Node_Complete:
			System.out.println("Got complete message");
			//state.completeGraph();
			crawlComplete();
			System.exit(0);

			break;

		case Constants.Page_Rank_init:
			PageRankInit prInit = new PageRankInit();
			prInit.unmarshall(bytes);

			nodeManager = new Peer(prInit.host, prInit.port);

			readFromDisk();

			PageRankInit reply = new PageRankInit(serverPort, Tools.getLocalHostname(), domain, String.valueOf(state.crawledLinks()));
			l.sendData(reply.marshall());

			break;

		case Constants.Payload:
			Payload payload = new Payload();
			payload.unmarshall(bytes);

			if (payload.number == Constants.Continue) {
				System.out.println("Starint page rank!");
			}

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

			synchronized (state) {
				if (!state.pendingLinksRemaining()) {
					NodeComplete complete = new NodeComplete(serverPort);
					sendBytes(nodeManager, complete.marshall());
				}
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
			if (request.links != null && request.links.size() > 0) {
				String incoming = request.links.get(0);
				Page thisPage = state.findPage(page);

				if (thisPage != null) {
					thisPage.addIncomingLink(incoming);
				}
			}

		}
	}


	public void fetchURL(Page page, FetchRequest request) {

		System.out.println("Fetching : " + request.url);
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
			}
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

				ArrayList<String> handoffSourceURL = new ArrayList<String>();
				handoffSourceURL.add(page.urlString);
				HandoffLookup handoff = new HandoffLookup(s, page.depth + 1, s,handoffSourceURL);
				managerLink.sendData(handoff.marshall());

				managerLink.close();
			}
		}

		// If we're done, print
		if (!state.shouldContinue()) {
			System.out.println("Sending complete message");
			NodeComplete complete = new NodeComplete(serverPort);
			sendBytes(nodeManager, complete.marshall());
		}	

		else {

			if (state.pendingList.size() <= 10) {
				System.out.println("remaining : " + state.remaining());
			}
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

	public void addIncomingPageForAll(ArrayList<String> links, String from) {

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
						System.out.println("Abs path : " + fileEntry.getAbsolutePath());
						FileInputStream f_in = new FileInputStream(fileEntry.getAbsolutePath());

						// Read object using ObjectInputStream
						ObjectInputStream obj_in = new ObjectInputStream (f_in);

						obj = obj_in.readObject();

						if (obj instanceof CrawlerState) {
							// Cast object to a State
							state = (CrawlerState) obj;

							// Do something with state....
							System.out.println("Read state : " + state.graphDiagnostics());
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
