package cs555.crawler.communications;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import cs555.crawler.utilities.*;
import cs555.crawler.wireformats.*;
import cs555.crawler.node.*;

// Link is a class to abstract a connection between nodes
public class Link {

	public String remoteHost;
	int port;
	public Socket socket;
	Node node;
	LinkReceiverThread receiver;

	//================================================================================
	// Constructor
	//================================================================================
	public Link(Socket s, Node n) {
		socket = s;
		port = s.getPort();
		node = n;
		remoteHost = socket.getInetAddress().getHostName();
		receiver = new LinkReceiverThread(socket, this);
	}

	public void initLink(){
		receiver.start();
	}


	//================================================================================
	// Send 
	//================================================================================
	public void sendData(byte[] dataToBeSent) throws IOException{

		OutputStream sout = Tools.createOutputStream(socket);

		if (sout != null) {
			sout.write(dataToBeSent);
			sout.flush();
		}
	}


	//================================================================================
	// Receive
	//================================================================================
	public void dataReceived(int bytes, byte[] dataReceived){
		node.receive(dataReceived,this);
	}

	// Waits for an int to come back
	public int waitForIntReply(){

		byte[] data = waitForData();
		int messageType = Tools.getMessageType(data);

		switch (messageType) {
		case Constants.Verification:

			Verification ack = new Verification();
			ack.unmarshall(data);

			return ack.number;

		default:
			break;
		}

		return -1;
	}


	public byte[] waitForData(){
		InputStream sin = Tools.createInput(socket);
		byte[] bytesnum = new byte[Constants.LEN_BYTES];
		int numRead;

		try {
			numRead = sin.read(bytesnum);

			if (numRead >= 0){
				return bytesnum;
			}

		} catch (IOException e){
			Tools.printStackTrace(e);
		}

		return null;
	}
	//================================================================================
	// House Keeping
	//================================================================================
	public void close() {
		receiver.cont = false;

		try {
			socket.close();
		} catch (IOException e){
			System.out.println("Could not close socket");
			Tools.printStackTrace(e);
		}
	}
}
