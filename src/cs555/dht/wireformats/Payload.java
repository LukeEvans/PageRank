package cs555.dht.wireformats;

import java.nio.ByteBuffer;

import cs555.dht.utilities.Constants;

// Wireformat for message payload
public class Payload {

	public int size;
	public int type;
	public int number;

	// Constructor for sending message
	public Payload(int num) {
		size = 4 + 4;
		type = Constants.Payload;
		number = num;
	}

	// Constructor for receiving message
	public Payload() {
		size = 0;
		type = Constants.Payload;
		number = 0;
	}

	// Marshall message
	public byte[] marshall() {
		byte[] bytes = new byte[size + 4];
		ByteBuffer bbuff = ByteBuffer.wrap(bytes);

		// Size
		bbuff.putInt(size);

		// type
		bbuff.putInt(type);

		// Number
		bbuff.putInt(number);

		return bytes;
	}

	// Unmarshall message
	public void unmarshall(byte[] bytes) {
		ByteBuffer bbuff = ByteBuffer.wrap(bytes);

		// Size
		size = bbuff.getInt();

		// type
		type = bbuff.getInt();

		// Number
		number = bbuff.getInt();
	}


	
	//================================================================================
	// House Keeping
	//================================================================================
	
	// To String
	public String toString() {
		String s = "";

		s += "Message Type : " + type + "\n";
		s += "Number       : " + number + "\n";

		return s;
	}
	
	// Override .equals
	public boolean equals(Payload other){
		return this.number == other.number;
	}
}
