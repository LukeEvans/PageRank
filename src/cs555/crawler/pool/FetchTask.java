package cs555.crawler.pool;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import cs555.crawler.node.Worker;
import cs555.crawler.url.Page;
import cs555.crawler.utilities.Constants;
import cs555.crawler.wireformats.FetchRequest;

public class FetchTask implements Task {

	int runningThread;


	// URL
	String urlString;

	// Text
	String urlText;

	// Request 
	FetchRequest request;

	Worker node;
	Page page;


	//================================================================================
	// Constructor
	//================================================================================
	public FetchTask(Page p, FetchRequest urlReq, Worker w){
		urlString = urlReq.url;
		request = urlReq;

		node = w;
		page = p;
	}

	//================================================================================
	// Run
	//================================================================================
	public void run() {

		try {

			ArrayList<String> urls = new ArrayList<String>();

			Document doc = Jsoup.connect(urlString).ignoreHttpErrors(true).timeout(6000).get();
			Elements links = doc.select("a[href]");
			

			String text = "";
			
			if (doc.head() != null) {
				text += doc.head().text() + "\n";
			}
			
			if (doc.body() != null) {
				text += doc.body().text();
			}
			
			for (Element link : links) {
				urls.add(link.attr("abs:href"));
			}
			
			ArrayList<String> freshLinks = removeBadDomains(urls);
			node.linkComplete(page, freshLinks, getFileMap(urls));

			//SaveTask saver = new SaveTask(urlString, text);
			//saver.save();

			

		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			//System.out.println("Error : " + e);
			node.linkErrored(page);
			return;
		}

		return;
	}


	//================================================================================
	// Parse
	//================================================================================
	public HashMap<String, Integer> getFileMap(ArrayList<String> links) {
		int html = 0;
		int htm = 0;
		int doc = 0;
		int pdf = 0;
		int cfm = 0;
		int aspx = 0;
		int asp = 0;
		int php = 0;
		int ps = 0;
		int tar = 0;
		int gz = 0; 
		int zip = 0;
		int avi = 0;
		int ppt = 0;
		int txt = 0;
		int text = 0;
		int pptx = 0;
		int pps = 0;
		int wmv = 0;
		int swf = 0;
		int docx = 0;
		int mp3 = 0;
		
		for (String s : links) {

			String copy = new String(s);
			
			if (s.endsWith(".html")) html++;
			else if (s.endsWith(".htm")) htm++;
			else if (s.endsWith(".doc")) doc++;
			else if (s.endsWith(".pdf")) pdf++;
			else if (s.endsWith(".cfm")) cfm++;
			else if (s.endsWith(".aspx")) aspx++;
			else if (s.endsWith(".asp")) asp++;
			else if (s.endsWith(".php")) php++;
			else if (s.endsWith(".ps")) ps++;
			else if (s.endsWith(".tar")) tar++;
			else if (s.endsWith(".gz")) gz++;
			else if (s.endsWith(".zip")) zip++;
			else if (s.endsWith(".avi")) avi++;
			else if (s.endsWith(".ppt")) ppt++;
			else if (s.endsWith(".txt")) txt++;
			else if (s.endsWith(".text")) text++;
			else if (s.endsWith(".pptx")) pptx++;
			else if (s.endsWith(".pps")) pps++;
			else if (s.endsWith(".wmv")) wmv++;
			else if (s.endsWith(".swf")) swf++;
			else if (s.endsWith(".docx")) docx++;
			else if (s.endsWith(".mp3")) mp3++;
		
			copy = copy.replace("www.", "");
			copy = copy.replace(request.domain, "");
			
		}


		HashMap<String, Integer> fileMap = new HashMap<String, Integer>();

		fileMap.put("html", html);
		fileMap.put("htm", htm);
		fileMap.put("doc", doc);
		fileMap.put("pdf", pdf);
		fileMap.put("cfm", cfm);
		fileMap.put("aspx", aspx);
		fileMap.put("asp", asp);
		fileMap.put("php", php);

		fileMap.put("ps", ps);
		fileMap.put("tar", tar);
		fileMap.put("gz", gz);
		fileMap.put("zip", zip);
		fileMap.put("avi", avi);
		fileMap.put("ppt", ppt);
		fileMap.put("txt", txt);
		fileMap.put("text", text);
		fileMap.put("pptx", pptx);
		fileMap.put("pps", pps);
		fileMap.put("wmv", wmv);
		fileMap.put("swf", swf);
		fileMap.put("docx", docx);
		fileMap.put("mp3", mp3);
		
		return fileMap;
	}

	public ArrayList<String> stringURLs(URL[] urls) {
		ArrayList<String> strings = new ArrayList<String>();

		for (URL u : urls) {
			strings.add(u.toString());
		}

		return strings;
	}

	public ArrayList<String> removeBadDomains(ArrayList<String> original) {
		ArrayList<String> newList = new ArrayList<String>();

		for (String s : original) {
			for (String d : Constants.domains) {
				if (s.contains("." + d)) {

					if (!linkIsFile(s)) { 
						newList.add(trim(s));
						continue;
					}
				}
			}
		}

		return newList;
	}

	public String trim(String s) {
		if (s.endsWith("/")) {
			return s.substring(0, s.length()-1);
		}
		
		return s;
	}
	
	public boolean linkIsFile(String link) {
		List<String> ext = Arrays.asList("doc", "pdf", "jpg", "png", "gif", "z", "ps", "gz", "zip", "dvi", "avi", "jpeg", "ppt", "text", "txt", "tex", "tar", "tgz", "ogg", "au", "pptx", "m", "mkv", "pps", "eps", "pgm", "c", "docx", "cl", "tcl", "wmv", "swf", "mp3", "exe", "flv");

		for (String e : ext) {
			if (link.toLowerCase().endsWith("." + e)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public void setRunning(int i) {
		// TODO Auto-generated method stub

	}


}
