package cs555.crawler.peer;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;

import cs555.crawler.utilities.*;

// Peer list is a data structure to maintain the list of peers in the system
public class PeerList implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	ArrayList<Peer> listOfPeers;;
	String localHost;
	int locaPort;
	String filename;

	//================================================================================
	// Init
	//================================================================================
	public PeerList(String file, int p) {
		listOfPeers = new ArrayList<Peer>();
		localHost = Tools.getLocalHostname();
		locaPort = p;
		filename = file;

		// Build List
		buildList();

		// Remove ourselves
		removeSelf();
	}
	
	public PeerList(int p) {
		listOfPeers = new ArrayList<Peer>();
		localHost = Tools.getLocalHostname();
		locaPort = p;
	}

	// Read hosts from file
	public void buildList(){
		try{
			// Open the file that is the first 
			FileInputStream fstream = new FileInputStream(filename);
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));

			String strLine;

			//Read File Line By Line
			while ((strLine = br.readLine()) != null)   {
				createPeerFromLine(strLine);
			}
			//Close the input stream
			in.close();
		}catch (Exception e){//Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
	}

	// Turn a line of text into a peer
	public void createPeerFromLine(String line){
		String[] stringParts = line.split("\\s+");
		
		if (stringParts.length == 2){
			String host = Tools.getHostname(stringParts[0]);
			int port = Integer.parseInt(stringParts[1]);
			
			// Add Peer
			Peer peer = new Peer(host, port);
			addPeer(peer);
		}
	}


	//================================================================================
	// List manipulation 
	//================================================================================
	// Add peer
	public void addPeer(Peer p){
		if (!contains(p)){
			listOfPeers.add(p);
		}
	}

	// Remove peer
	public void removePeer(Peer p){
		for (int i=0; i<listOfPeers.size(); i++){
			if (p.equals(listOfPeers.get(i))){
				listOfPeers.remove(i);
			}
		}
	}

	// Find Peer matching description
	public Peer findPeer(String host, int port){
		Peer newPeer = new Peer(host, port);

		for (Peer p : listOfPeers){
			if (newPeer.equals(p)){
				return p;
			}
		}

		return null;
	}

	// Remove self from list
	public void removeSelf(){
		Peer self = findPeer(localHost, locaPort);
		
		if (self != null){
			System.out.println("Removing self from peer list: ("+localHost+":"+locaPort+")");
			removePeer(self);
		}
	}


	//================================================================================
	// Accessor methods
	//================================================================================
	// Get next peer
	public Peer getNextPeer(){
		if (listOfPeers.size() == 0){
			return null;
		}
		
		int peerIndex = Math.abs(Tools.generateRandomNumber()) % listOfPeers.size();
		return listOfPeers.get(peerIndex);
	}

	public Peer getReadyPeer() {
		
		Peer p = getNextPeer();
		
		while (!p.ready) {
			p = getNextPeer();
		}
		
		p.ready = false;
		
		return p;
	}
	
	// Get first peer
	public Peer getFirstPeer(){
		if (listOfPeers.size() == 0){
			return null;
		}
		
		return listOfPeers.get(0);
	}
	
	// Get all peers we're waiting on
	public ArrayList<Peer> getAllPendingPeers() {
		ArrayList<Peer> pending = new ArrayList<Peer>();
		
		for (Peer p : listOfPeers) {
			if (!p.ready) {
				pending.add(p);
			}
		}
		
		return pending;
	}
	
	public int numberRemainingPeers() {
		return getAllPendingPeers().size();
	}
	
	public boolean allPeersDone() {
		if (getAllPendingPeers().size() == 0) {
			return true;
		}
		
		return false;
	}
	
	// Get all peers
	public ArrayList<Peer> getAllPeers(){
		return listOfPeers;
	}
	
	public int size(){
		return listOfPeers.size();
	}
	
	// Get peer for a domain
	public Peer findDomainLeader(String d) {
		for (Peer p : listOfPeers) {
			if (!p.domain.equalsIgnoreCase("")) {
				if (d.contains("." + p.domain)) {
					return p;
				}
			}
		}
		
		return null;
	}
	
	//================================================================================
	// House Keeping
	//================================================================================
	// Override .contains method
	public boolean contains(Peer p) {

		for (Peer peer : listOfPeers){
			if (p.equals(peer)){
				return true;
			}
		}

		return false;
	}

	// Override .toString method
	public String toString() {
		String s = "";

		for (Peer peer : listOfPeers){
			s += peer.toString() + "\n";
		}
		return s;
	}


}
