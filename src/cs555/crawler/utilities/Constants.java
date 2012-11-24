package cs555.crawler.utilities;

import java.util.Arrays;
import java.util.List;


// Constants for message types
public class Constants {

	// Message types
	public static final int Payload = 1;
	public static final int Verification = 2;
	public static final int Node_Results = 3;
	public static final int Node_Complete = 4;
	public static final int Election_Message = 5;
	public static final int Fetch_Request = 6;
	public static final int Fetch_Response = 7;
	public static final int Handoff_Lookup = 8;
	public static final int Handoff_Reply = 9;
	public static final int Page_Rank_init = 10;
	public static final int Page_Rank_Transmit = 11;
	
	// URL states
	public static final int URL_Ready = 5;
	public static final int URL_Pending = 6;
	public static final int URL_Complete = 7;
	public static final int URL_Error = 8;
	
	public static final int Continue = 98;
	public static final int Failure = 99;
	public static final int Success = 100;
	
	public static final int depth = 5;
	
	public static final int Page_Rank_Rounds = 1;
	
	public static final int Default_Thread_Count = 5;
	
	// Message sizes
	public static final int LEN_BYTES = 3072;
	
	// Domains
	public static final List<String> domains = Arrays.asList("bmb.colostate.edu", "biology.colostate.edu", "chem.colostate.edu", "cs.colostate.edu", "math.colostate.edu", "physics.colostate.edu", "colostate.edu/Depts/Psychology", "stat.colostate.edu");
	
	// File basepath
	public static final String base_path = "/tmp/evansl/crawlData/";
	
	public static final String pageRank = "pageRank";
}
