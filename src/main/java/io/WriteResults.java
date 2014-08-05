package io;

import java.util.Date;
import java.util.*;

public class WriteResults{
	
	public static void writeTells(Integer tType, String pass, Vector vTells){
		
		Date myDate = new Date();
		
		String mdt = myDate.toGMTString().replaceAll("[ :]","-");
				
		String tfname="output/"+tType+"-tell-sentences-"+pass+"-"+mdt+".txt";
		
		System.out.println(">>>> "+tfname);
		
		utilities.IO.writeFile_Basic(tfname, vTells);
	}
	
	
	public static void writeProximityFeatures(Integer proxchoice,Vector vvFeat) {
		

		utilities.IO.writeFile_Basic("output/NewProxFeatures-"+proxchoice+".csv", vvFeat);
		
		//utilities.IO.writeFile_Basic("output/InferredMesh.txt", vv);
	}
}