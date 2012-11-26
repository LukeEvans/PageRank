package cs555.crawler.communications;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

import cs555.crawler.utilities.*;

// Link Receiver is a thread that listens for data
public class LinkReceiverThread extends Thread{

	InputStream sin;
	Socket socket;
	Link link;
	public boolean cont;
	
	//================================================================================
	//  Constructor
	//================================================================================
	public LinkReceiverThread(Socket s, Link l) {
		socket = s;
		sin = Tools.createInput(socket);
		link = l;
		
		if (sin == null){
			link.close();
		}
		
		cont = true;
	}
	
	
	//================================================================================
	// Run
	//================================================================================
	public void run(){
		byte[] bytesnum = new byte[Constants.LEN_BYTES];
		
		while (cont){
			int numRead;
			
			try {
				numRead = sin.read(bytesnum);
				
				if (numRead < 0){
					break;
				}
				
				// Pass data back to Link
				link.dataReceived(numRead,bytesnum);
				
			} catch (IOException e){
				cont = false;
				//Tools.printStackTrace(e);
			}
		}
						
		// Close link
		link.close();
	}
}
