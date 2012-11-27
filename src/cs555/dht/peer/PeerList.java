package cs555.dht.peer;

import java.util.ArrayList;

import cs555.dht.utilities.*;

// Peer list is a data structure to maintain the list of peers in the system
public class PeerList {

	ArrayList<Peer> listOfPeers;;

	//================================================================================
	// Init
	//================================================================================
	public PeerList() {
		listOfPeers = new ArrayList<Peer>();
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
	public Peer findPeer(String host, int port, int id){
		Peer newPeer = new Peer(host, port, id);

		for (Peer p : listOfPeers){
			if (newPeer.equals(p)){
				return p;
			}
		}

		return null;
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

	
	// Get first peer
	public Peer getFirstPeer(){
		if (listOfPeers.size() == 0){
			return null;
		}
		
		return listOfPeers.get(0);
	}
	
	// Get all peers
	public ArrayList<Peer> getAllPeers(){
		return listOfPeers;
	}
	
	public int size(){
		return listOfPeers.size();
	}
	
	// Determine if hash is acceptable to add
	public boolean hashUnique(int hash) {
		for (Peer p : listOfPeers) {
			if (p.id == hash) {
				return false;
			}
		}
		
		return true;
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
