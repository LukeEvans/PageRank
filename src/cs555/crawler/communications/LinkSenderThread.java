package cs555.crawler.communications;

import java.io.OutputStream;
import java.net.Socket;

import cs555.crawler.utilities.Tools;

public class LinkSenderThread extends Thread {

	OutputStream sout;
	Socket socket;
	public boolean cont;

	//================================================================================
	// Constructor
	//================================================================================
	public LinkSenderThread(Socket s) {
		socket = s;
		sout = Tools.createOutputStream(socket);
		cont = true;
	}
	
	//================================================================================
	// Run
	//================================================================================
	public void run() {
		
		while(cont) {
			
		}
	}
}
