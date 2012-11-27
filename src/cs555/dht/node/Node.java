package cs555.dht.node;

import java.io.IOException;
import java.net.Socket;


import cs555.dht.communications.*;
import cs555.dht.peer.Peer;

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
			return null;
		}

		return link;
	}

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