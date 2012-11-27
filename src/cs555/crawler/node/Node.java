package cs555.crawler.node;

import java.io.IOException;
import java.net.Socket;

import cs555.crawler.communications.*;
import cs555.crawler.peer.Peer;
import cs555.crawler.utilities.*;

// Main process in the system
public class Node {

	ServerSockThread server;

	// Configuration 
	int serverPort;


	//================================================================================
	// General's constructor
	//================================================================================
	public Node(int port){
		serverPort = port;
		server = new ServerSockThread(serverPort, this);
	}


	public void initServer(){
		server.start();
	}


	// Connect to peer
	public Link connect(Peer p){
		Socket sock;
		Link link = null;

		try {
			sock = new Socket(p.hostname,p.port);
			link = new Link(sock, this);
		} catch (IOException e){
			System.out.println("Could not connect to: " + p.hostname + ", " + p.port);
			Tools.printStackTrace(e);
		}

		return link;
	}

	//================================================================================
	// Send
	//================================================================================
	// Send data
//	public void sendPayload(Link link, int number){
//		Payload payload = new Payload(number);
//		link.sendData(payload.marshall());
//	}

//	// Send any bytes
//	public void sendBytes(Peer p, byte[] bytes) {
//		Link link = connect(p);
//		link.sendData(bytes);
//		link.close();
//	}
	
	//================================================================================
	// Broadcast
	//================================================================================
//	public void broadcastMessage(ArrayList<Peer> nodes, byte[] data, int expectedReply){
//		for (Peer peer : nodes){
//			Link link = connect(peer);
//			link.sendData(data);
//
//			int replyInt = link.waitForIntReply();
//			
//			if (expectedReply != replyInt){
//				System.out.println("Reply Doesn't match");
//				System.exit(1);
//			}
//		}
//	}

	//================================================================================
	// Receive
	//================================================================================
	// Receieve data
	public synchronized void receive(byte[] bytes, Link l){
		// Override
	}

	//================================================================================
	// Output
	//================================================================================
	// Print output
	public void printOutput() {
		// Override
	}


	//================================================================================
	// Cleanup
	//================================================================================
	// Close the server
	public void cleanup(){
		server.cont = false;
		System.exit(0);
	}


}