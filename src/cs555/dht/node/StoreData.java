package cs555.dht.node;

import cs555.dht.communications.Link;
import cs555.dht.peer.Peer;
import cs555.dht.utilities.Constants;
import cs555.dht.utilities.Tools;
import cs555.dht.wireformats.*;

public class StoreData extends Node {

	String hostName;
	int port;
	String filename;
	int filehash;
	int idSpace;

	public StoreData(int p, String fname, int fhash, int s) {
		super(p);
		hostName = Tools.getLocalHostname();
		port = p;
		filename = fname;
		filehash = fhash;
		idSpace = s;

		if (filehash == -1) {
			filehash = Tools.generateHash(filename);
		}
	}

	//================================================================================
	// Init
	//================================================================================
	public void initServer(){
		super.initServer();
	}

	public void initLookup(String dHost, int dPort) {
		Link managerLink = connect(new Peer(dHost, dPort));
		
		// Get random peer from discovery
		RandomPeerRequest randomReq = new RandomPeerRequest(hostName, port, filehash);
		managerLink.sendData(randomReq.marshall());
		
		byte[] randomNodeData = managerLink.waitForData();
		int msgType = Tools.getMessageType(randomNodeData);
		
		switch (msgType) {
		case Constants.RandomPeer_Response:
			
			RandomPeerResponse randomRes = new RandomPeerResponse();
			randomRes.unmarshall(randomNodeData);
			
			LookupRequest lookupReq = new LookupRequest(hostName, port, filehash, filehash, Constants.store_request);
			Peer accessPoint = new Peer(randomRes.hostName, randomRes.port, randomRes.id);
			Link accessLink = connect(accessPoint);
			
			System.out.println("access point : " + accessPoint.id);
			
			accessLink.sendData(lookupReq.marshall());
			
			System.out.println("Looking up hash : " + lookupReq.resolveID);
			
			break;

		default:
			break;
		}
	}

	//================================================================================
	// Receive
	//================================================================================
	// Receieve data
	public synchronized void receive(byte[] bytes, Link l){
		int messageType = Tools.getMessageType(bytes);

		switch (messageType) {
		case Constants.lookup_reply:

			LookupResponse response = new LookupResponse();
			response.unmarshall(bytes);
			System.out.println("Data belongs to: " + response.id);
			
			Peer candidate = new Peer(response.hostName, response.port);
			Link candidateLink = connect(candidate);
			
			
			// Send store request
			TransferRequest storeReq = new TransferRequest(filename, filehash);
			candidateLink.sendData(storeReq.marshall());
			System.out.println("Requesting : " + storeReq);
			
			if (candidateLink.waitForIntReply() == Constants.Continue) {
				// Send data item to candidate
				Tools.sendFile(filename, candidateLink.socket);
			}
			
			break;

		default:
			System.out.println("Unrecognized Message");
			break;
		}
	}

	//================================================================================
	//================================================================================
	// Main
	//================================================================================
	//================================================================================
	public static void main(String[] args){

		String discoveryHost = "";
		int discoveryPort = 0;
		int localPort = 0;
		String fileName = "";
		int fileHash = -1;
		int idSpace = 16;

		if (args.length >= 4) {
			discoveryHost = args[0];
			discoveryPort = Integer.parseInt(args[1]);
			localPort = Integer.parseInt(args[2]);
			fileName = args[3];

			if (args.length >= 5) {
				fileHash = Integer.parseInt(args[4]);

				if (args.length >= 6) {
					idSpace = Integer.parseInt(args[5]);
				}
			}

		}

		else {
			System.out.println("Usage: java cs555.dht.node.StoreData DISCOVERY-NODE DISCOVERY-PORT LOCAL-PORT FILE-NAME <CUSTOM-HASH>");
			System.exit(1);
		}


		// Create node
		StoreData storeHandler = new StoreData(localPort, fileName, fileHash, idSpace);

		// Start
		storeHandler.initServer();
		storeHandler.initLookup(discoveryHost, discoveryPort);
	}
}
