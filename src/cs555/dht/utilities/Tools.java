package cs555.dht.utilities;

import java.io.*;
import java.math.BigInteger;
import java.net.Inet4Address;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

// Functions used all over the program. Handy location
public class Tools {

	// ================================================================================
	// Message functions
	// ================================================================================
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
			sout = s.getOutputStream();
			return sout;
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

	// Accept user input
	public static String readInput(String output) {
		// TODO Auto-generated method stub
		String input = "";
		System.out.println(output);

		BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
		try {
			input = stdin.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return input;
	}

	//================================================================================
	// File sending and receieving
	//================================================================================
	public static boolean sendFile(String sPath,Socket sock) {
		// TODO Auto-generated method stub
		// sendfile

		try {

			File myFile = new File(sPath);
			byte [] mybytearray  = new byte [(int)myFile.length()];
			FileInputStream fis = new FileInputStream(myFile);
			BufferedInputStream bis = new BufferedInputStream(fis);
			bis.read(mybytearray,0,mybytearray.length);
			OutputStream os = sock.getOutputStream();
			System.out.println("Sending...");
			os.write(mybytearray,0,mybytearray.length);
			os.flush();

			System.out.println("Sent file : " + sPath);
			sock.close();
			bis.close();
			return true;

		} catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
		

	}

	public static boolean receiveFile(String path,Socket sock) {
		int bytesRead;
		int current = 0;

		path = Constants.base_path + path;
		
		try {
			System.out.println("Connecting...");
			// receive file
			byte [] mybytearray  = new byte [Constants.LEN_BYTES];
			InputStream is = sock.getInputStream();
			FileOutputStream fos = new FileOutputStream(path);
			BufferedOutputStream bos = new BufferedOutputStream(fos);
			bytesRead = is.read(mybytearray,0,mybytearray.length);
			current = bytesRead;

			do {
				bytesRead = is.read(mybytearray, current, (mybytearray.length-current));
				if(bytesRead >= 0) current += bytesRead;
			} while(bytesRead > -1);

			bos.write(mybytearray, 0 , current);
			bos.flush();

			bos.close();
			sock.close();

			return true;

		} catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}


	//================================================================================
	// Remove file from fs
	//================================================================================
	public static void removeFile(String fileName) {
		File f = new File(fileName);

	    // Make sure the file or directory exists and isn't write protected
	    if (!f.exists())
	      throw new IllegalArgumentException(
	          "Delete: no such file or directory: " + fileName);

	    if (!f.canWrite())
	      throw new IllegalArgumentException("Delete: write protected: "
	          + fileName);

	    // If it is a directory, make sure it is empty
	    if (f.isDirectory()) {
	      String[] files = f.list();
	      if (files.length > 0)
	        throw new IllegalArgumentException(
	            "Delete: directory not empty: " + fileName);
	    }

	    // Attempt to delete it
	    f.delete();
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
	// Hash functions
	//================================================================================

	public static String convertBytesToHex(byte[] buf) {
		StringBuffer strBuf = new StringBuffer();
		for (int i = 0; i < buf.length; i++) {
			int byteValue = (int) buf[i] & 0xff;
			if (byteValue <= 15) {
				strBuf.append("0");
			}
			strBuf.append(Integer.toString(byteValue, 16));
		}
		return strBuf.toString();
	}

	public static byte[] convertHexToBytes(String hexString) {
		int size = hexString.length();
		byte[] buf = new byte[size / 2];
		int j = 0;
		for (int i = 0; i < size; i++) {
			String a = hexString.substring(i, i + 2);
			int valA = Integer.parseInt(a, 16);
			i++;
			buf[j] = (byte) valA;
			j++;
		}
		return buf;
	}

	// Generates hash
	public static int generateHash() {
		// Timestamp
		long epoch = System.currentTimeMillis()/1000;
		generateHash(String.valueOf(epoch));

		return generateHash(String.valueOf(epoch));

	}

	// Generate hash of String
	public static int generateHash(String item) {
		int bitsToTake = Constants.Id_Space / 4;

		String itemMD5 = md5(item);
		String partMD5 = "";

		// Take sampling of characters
		for (int i=itemMD5.length()-1; i>=0; i--) {
			if (partMD5.length() < bitsToTake) {
				if (i % 8 == 0) {
					partMD5 += itemMD5.charAt(i);
				}
			}
		}

		int hashInt = Integer.parseInt(partMD5, 16);
		//System.out.println("hash : " + hashInt);
		return hashInt;
	}

	// MD5
	public static String md5(String input) {

		String md5 = null;

		if(null == input) return null;

		try {

			//Create MessageDigest object for MD5
			MessageDigest digest = MessageDigest.getInstance("MD5");

			//Update input string in message digest
			digest.update(input.getBytes(), 0, input.length());

			//Converts message digest value in base 16 (hex) 
			md5 = new BigInteger(1, digest.digest()).toString(16);

		} catch (NoSuchAlgorithmException e) {

			e.printStackTrace();
		}
		return md5;
	}

	//================================================================================
	// Error Handling
	//================================================================================
	public static void printStackTrace(Exception e){
		e.printStackTrace();
		System.exit(1);

	}
}
