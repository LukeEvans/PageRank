package cs555.crawler.utilities;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Inet4Address;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

// Functions used all over the program. Handy location
public class Tools {

	// ================================================================================
	// Message functions
	// ================================================================================
	public static byte[] objectToBytes(Object o) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutput out = null;
		try {
		  out = new ObjectOutputStream(bos);   
		  out.writeObject(o);
		  byte[] bytes = bos.toByteArray();
		  
		  out.close();
		  bos.close();
		  
		  return bytes;
		  
		} catch(IOException e) {
			System.out.println("Could not create bytes from object");
			return null;
		}
	}
	
	public static Object bytesToObject(byte[] bytes) {
		ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
		ObjectInput in = null;
		try {
		  in = new ObjectInputStream(bis);
		  Object o = in.readObject(); 
		  
		  bis.close();
		  in.close();
		  
		  return o;
		  
		} catch(Exception e) {
			System.out.println("Could not creat object from bytes");
			return null;
		}
	}
	
	// Get Message type
	public static int getMessageType(byte[] bytes) {
		byte[] copy = bytes.clone();
		ByteBuffer bbuff = ByteBuffer.wrap(copy);

		// Size
		bbuff.getInt();

		// Return type
		return bbuff.getInt();
	}

	// Generate random number
	public static int generateRandomNumber() {

		int Min = 0;
		int Max = 65535;

		int random = Min + (int) (Math.random() * ((Max - Min) + 1));

		random -= 32768;

		return random;
	}


	//================================================================================
	// Link Functions 
	//================================================================================
	// Create input stream
	public static InputStream createInput(Socket s){
		InputStream sin;

		try {
			sin = s.getInputStream();
			return sin;
		} catch (IOException e){
			printStackTrace(e);
			return null;
		}
	}

	// Create output stream
	public static OutputStream createOutputStream(Socket s){
		OutputStream sout;

		try {
			if (!s.isClosed()) {
				sout = s.getOutputStream();
				return sout;
			}
			
			return null;
		} catch (IOException e){
			printStackTrace(e);
			return null;
		}
	}


	// ================================================================================
	// Host Functions
	// ================================================================================
	public static String getLocalHostname() {
		return getHostname("localhost");
	}

	public static String getHostname(String hname) {
		if (hname.equalsIgnoreCase("localhost") || hname.equalsIgnoreCase("127.0.0.1")){

			try {
				String host = Inet4Address.getLocalHost().getHostName();

				return host;

			} catch (UnknownHostException e) {
				printStackTrace(e);
			}
		}

		return hname;
	}

	// Get hostname without domain
	public static String getShortHostname(String hname){
		String host = getHostname(hname);
		String[] hostParts = host.split("\\.");

		if (hostParts.length > 1){
			return hostParts[0];
		}

		return null;
	}

	public static void sleep(int time){
		try {
			Thread.sleep(time * 1000);
		} catch (InterruptedException e) {
			printStackTrace(e);
		}
	}
	
	public static void sleep(int time, int fraction) {
		System.out.println("sleeping for : " + (time * (1000/fraction)));
		try {
			Thread.sleep(time * (1000/fraction));
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	//================================================================================
	// Byte Manipulations
	//================================================================================
	// convert string to byte array
	public static byte[] convertToBytes(String s){
		return s.getBytes();
	}

	// Convert Int to byte array
	public static byte[] convertToBytes(int i){
		return convertToBytes(Integer.toString(i));
	}

	//================================================================================
	// Error Handling
	//================================================================================
	public static void printStackTrace(Exception e){
		e.printStackTrace();
		System.exit(1);

	}

	//================================================================================
	// URL Functions
	//================================================================================
	public static String flattenURL(String url) {
		String s = url.replace("/", "<-_->");
		return s;
	}

	public static String inflateURL(String url) {
		String s = url.replace("<-_->", "/");
		return s;
	}
}
