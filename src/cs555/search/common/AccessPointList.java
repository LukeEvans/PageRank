package cs555.search.common;

import java.io.Serializable;
import java.util.ArrayList;

public class AccessPointList implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public int size;
	public ArrayList<AccessPoint> accessPeers;
	
	//================================================================================
	// Constructor
	//================================================================================
	public AccessPointList(int s, ArrayList<AccessPoint> ap) {
		size = 0;
		accessPeers = ap;
	}
}
