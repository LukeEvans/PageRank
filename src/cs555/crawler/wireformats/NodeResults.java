package cs555.crawler.wireformats;

import java.nio.ByteBuffer;

import cs555.crawler.utilities.Constants;

// Wireformat for message payload
public class NodeResults {

	public int size;
	public int type;
	public int numberSent;
	public int numberReceived;
	public int sumSent;
	public int sumReceived;

	// Constructor for sending message
	public NodeResults(int ns, int nr, int ss, int sr) {
		size = 4 + 4 + 4 + 4 + 4;
		type = Constants.Node_Results;
		numberSent = ns;
		numberReceived = nr;
		sumSent = ss;
		sumReceived = sr;
	}

	// Constructor for receiving message
	public NodeResults() {
		size = 0;
		type = Constants.Node_Results;
		numberSent = 0;
		numberReceived = 0;
		sumSent = 0;
		sumReceived = 0;
	}

	// Marshall message
	public byte[] marshall() {
		byte[] bytes = new byte[size + 4];
		ByteBuffer bbuff = ByteBuffer.wrap(bytes);

		// Size
		bbuff.putInt(size);

		// type
		bbuff.putInt(type);

		// Number Sent
		bbuff.putInt(numberSent);

		// Number Received
		bbuff.putInt(numberReceived);
		
		// Sum Sent
		bbuff.putInt(sumSent);
		
		// Sum received
		bbuff.putInt(sumReceived);
		
		return bytes;
	}

	// Unmarshall message
	public void unmarshall(byte[] bytes) {
		ByteBuffer bbuff = ByteBuffer.wrap(bytes);

		// Size
		size = bbuff.getInt();

		// type
		type = bbuff.getInt();

		// Number Sent
		numberSent = bbuff.getInt();
		
		// Number Received
		numberReceived = bbuff.getInt();
		
		// Sum Sent
		sumSent = bbuff.getInt();
		
		// Sum Received
		sumReceived = bbuff.getInt();
	}


	
	//================================================================================
	// House Keeping
	//================================================================================
	
	// Override .equals
	public boolean equals(NodeResults other){
		
		if (this.numberSent == other.numberSent){
			if (this.numberReceived == other.numberReceived){
				if (this.sumSent == other.sumSent){
					if (this.sumReceived == other.sumReceived){
						return true;
					}
				}
			}
		}
		
		return false;
	}
}
