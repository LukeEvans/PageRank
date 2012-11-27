package cs555.dht.wireformats;

import cs555.dht.utilities.Constants;

public class PredessesorLeaving extends RegisterRequest{

	//================================================================================
	// Overridden Constructors
	//================================================================================
	public PredessesorLeaving(String h, int p, int i){
		super.init(h, p, i);
		type = Constants.Predessesor_Leaving;
		
	}
	
	
	public PredessesorLeaving(){
		super.init("", 0, -1);
		type = Constants.Predessesor_Leaving;
	}
}
