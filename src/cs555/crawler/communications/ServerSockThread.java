package cs555.crawler.communications;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import cs555.crawler.utilities.*;
import cs555.crawler.node.*;

// Server Sock Thread listens for remote connections
public class ServerSockThread extends Thread{

	ServerSocket server;
	Socket socket;
	int port;
	Node node;
	public boolean cont;
	
	//================================================================================
	// Constructor
	//================================================================================
	public ServerSockThread(int p, Node n){
		port = p;
		node = n;
		cont = true;
	}
	
	//================================================================================
	// Run
	//================================================================================
	public void run(){
		System.out.println("Starting server on: " + Tools.getLocalHostname() + ", " + port);
		
		try {
			server = new ServerSocket(port);
		} catch (IOException e){
			Tools.printStackTrace(e);
		}
		
		while (cont) {
			try {
				socket = server.accept();
				System.out.println("New socket at : " + socket.getLocalPort());
				Link link = new Link(socket, node);
				link.initLink();
								
			} catch (IOException e){
				Tools.printStackTrace(e);
			}
		}
	}
}
