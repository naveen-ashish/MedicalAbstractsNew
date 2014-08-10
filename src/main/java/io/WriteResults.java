package io;

import java.util.Date;
import java.util.*;

public class WriteResults{
	
	public static void writeTells(boolean mutexTells, Vector vTells){
		
		Date myDate = new Date();
		
		String mdt = myDate.toGMTString().replaceAll("[ :]","-");
		
		String tType="NoMutex";
		
		if (mutexTells) tType="YesMutex";
				
		String tfname="output/"+tType+"-tell-sentences-"+mdt+".txt";
		
		System.out.println(">>>> "+tfname);
		
		utilities.IO.writeFile_Basic(tfname, vTells);
	}
	
	
	public static void writeProximityFeatures(Integer proxchoice,Vector vvFeat) {
		

		utilities.IO.writeFile_Basic("output/NewProxFeatures-"+proxchoice+".csv", vvFeat);
		
		//utilities.IO.writeFile_Basic("output/InferredMesh.txt", vv);
	}
}