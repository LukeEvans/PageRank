package cs555.crawler.pool;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import cs555.crawler.utilities.Constants;

public class SaveTask {


	String urlString;
	String fileString;

	//================================================================================
	// Constructor
	//================================================================================
	public SaveTask(String url, String text){
		urlString = url;
		fileString = text;
	}


	//================================================================================
	// Run
	//================================================================================
	public void save() {
		String filename = getFilename();

		FileWriter fstream;
		try {
			fstream = new FileWriter(filename);
			BufferedWriter out = new BufferedWriter(fstream);
			out.write(fileString);
			//Close the output stream
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Error Saving file: file name is too long");
		}

	}

	//================================================================================
	// Linter
	//================================================================================
	public String getFilename() {
		String url = urlString.replace("http://www.", "");
		url = url.replace("https://www.", "S");
		url = url.replace("/", "\\");
		return Constants.base_path + url;
	}


}
