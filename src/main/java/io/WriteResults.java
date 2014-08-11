package io;

import java.util.Date;
import java.util.Vector;

import utilities.IO;

public class WriteResults{
	
	private IO io = new IO();
	
	public void writeTells(boolean mutexTells, Vector vTells){
		
		Date myDate = new Date();
		
		String mdt = myDate.toGMTString().replaceAll("[ :]","-");
		
		String tType="NoMutex";
		
		if (mutexTells) tType="YesMutex";
				
		String tfname="output/"+tType+"-tell-sentences-"+mdt+".txt";
		
		System.out.println(">>>> "+tfname);
		
		io.writeFile_Basic(tfname, vTells);
	}
	
	
	public void writeProximityFeatures(Integer proxchoice,Vector vvFeat) {
		

		io.writeFile_Basic("output/NewProxFeatures-"+proxchoice+".csv", vvFeat);
		
		//io.writeFile_Basic("output/InferredMesh.txt", vv);
	}
}