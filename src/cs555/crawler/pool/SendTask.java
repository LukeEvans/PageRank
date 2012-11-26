package cs555.crawler.pool;

import java.io.IOException;

import cs555.crawler.peer.Peer;

public class SendTask implements Task {

	Peer peer;
	byte[] dataToSend;

	//================================================================================
	// Constructor
	//================================================================================
	public SendTask(Peer p, byte[] d) {
		peer = p;
		dataToSend = d;
	}

	public void run() {
		System.out.println("Sending"); 
		
		try {
			peer.sendData(dataToSend);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void setRunning(int i) {
		// TODO Auto-generated method stub

	}



}
