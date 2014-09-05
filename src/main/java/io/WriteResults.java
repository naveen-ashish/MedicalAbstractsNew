package io;

import java.io.IOException;
import java.io.StringWriter;
import java.util.*;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.jayway.jsonpath.JsonModel.ObjectMappingModelReader;

import models.FeatureVector;
import models.Tell;

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
	
	public static void writeTell(Tell tell){
		StringWriter writer = new StringWriter();
		ObjectMapper mapper = new ObjectMapper();
		try {
			mapper.writeValue(writer, tell);
			System.out.println(writer.toString());
		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void writeProximityFeatures(FeatureVector vector) {
		StringWriter writer = new StringWriter();
		ObjectMapper mapper = new ObjectMapper();
		try {
			mapper.writeValue(writer, vector);
			System.out.println(writer.toString());
		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public static void writeProximityFeatures(Integer proxchoice,Vector vvFeat) {
		

		utilities.IO.writeFile_Basic("output/NewProxFeatures-"+proxchoice+".csv", vvFeat);
		
		//utilities.IO.writeFile_Basic("output/InferredMesh.txt", vv);
	}
}