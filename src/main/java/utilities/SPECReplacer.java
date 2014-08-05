package utilities;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class SPECReplacer {
	public void execute(){
		HashMap<String, String> abstractMap = fetchAbstracts();
		HashMap<String, String> trainingInfoMap = extractTrainingData();
		replaceContent(abstractMap, trainingInfoMap);
	}
	
	private HashMap<String, String> fetchAbstracts(){
		HashMap<String, String> map = new HashMap<String, String>();
		try{
			FileReader fr = new FileReader("resources/all_abstracts.txt");
			BufferedReader bf = new BufferedReader(fr);
			String line = "";
			while((line = bf.readLine()) != null){
				String[] splitter = line.split("\t");
				if(!map.containsKey(splitter[0].trim())){
					map.put(splitter[0].trim(), splitter[1] + "##" + splitter[2].trim());
				}
			}
		}catch(IOException e){
			e.printStackTrace();
		}
		return map;
	}
	
	public HashMap<String, String> extractTrainingData(){
		HashMap<String, String> map = new HashMap<String, String>();
		try{
			FileReader fr = new FileReader("resources/trainingData_naveenProject1.csv");
			BufferedReader bf = new BufferedReader(fr);
			String line = "";
			while((line = bf.readLine()) != null){
				if(line.contains("\"")){
					String[] splitter = line.split(",");
					map.put(splitter[0].trim(), splitter[1].trim().replace("\"", "") + splitter[2].trim().replace("\"", "") + "##" + splitter[3].trim());
				}else{
					String[] splitter = line.split(",");
					map.put(splitter[0].trim(), splitter[1].trim() + "##" + splitter[2].trim());
				}
				
			}
		}catch(IOException e){
			e.printStackTrace();
		}
		return map;
	}
	
	public void replaceContent(HashMap<String, String> abstractMap, HashMap<String, String> trainingInfoMap){
		HashMap<String, ArrayList<String>> harmMap = loadHarmClass();
		HashMap<String, ArrayList<String>> litagionSuperStringMap = loadLitagionSuperStrings();
		HashMap<String, ArrayList<String>> litagionSynonymMap = loadLitagionSynonym();
		HashMap<String, ArrayList<String>> dictionaryMap = loadDataDictionary();
		
		try{
			FileWriter fw = new FileWriter(new File("full-annotated-abstracts.txt"));
			BufferedWriter bw = new BufferedWriter(fw);
			Iterator it = abstractMap.entrySet().iterator();
		    while (it.hasNext()) {
		        Map.Entry pairs = (Map.Entry)it.next();
		        String abstractID = pairs.getKey().toString();
		        String id = pairs.getValue().toString().split("##")[0];
		        String abstractText = pairs.getValue().toString().split("##")[1];
		        if(trainingInfoMap.containsKey(abstractID)){
		        	String info = trainingInfoMap.get(abstractID);
		        	String litagion = info.split("##")[0];
		        	String harmClass = info.split("##")[1];
		        	abstractText = replaceHarmClass(harmClass, abstractText, harmMap);
		        	
		        	System.out.println(abstractText);
		        	
		        	abstractText = replaceLitagion(litagion, abstractText, litagionSuperStringMap, litagionSynonymMap);
		        	abstractText = replaceDictionaryTerm(abstractText, dictionaryMap);
		        	bw.write(abstractID + "\t" + id + "\t" + abstractText + "\n");
		        	bw.flush();
		        }
		    }
		    bw.close();
		    fw.close();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public String replaceHarmClass(String harmClass, String abstractText, HashMap<String, ArrayList<String>> harmMap){
		harmClass = harmClass.toLowerCase();
		abstractText = abstractText.toLowerCase();
		if(harmMap.containsKey(harmClass)){
			ArrayList<String> list = harmMap.get(harmClass);
			for(String synonym : list){
				if(abstractText.contains(synonym)){
					abstractText = abstractText.replaceAll(synonym, "SPEC_HARM");
				}
			}
		}
		return abstractText;
	}
	
	public String replaceLitagion(String litagion, String abstractText, HashMap<String, ArrayList<String>> litagionSuperStringMap, HashMap<String, ArrayList<String>> litagionSynonymMap){
		litagion = litagion.toLowerCase();
		if(litagionSynonymMap.containsKey(litagion)){
			ArrayList<String> list = litagionSynonymMap.get(litagion);
			for(String synonym : list){
				if(abstractText.contains(synonym)){
					abstractText = abstractText.replaceAll(synonym, "SPEC_LITAGION");
				}
			}
		}
		litagion = litagion.substring(litagion.indexOf("_[") + 2, litagion.length() - 1);
		if(litagionSuperStringMap.containsKey(litagion)){
			ArrayList<String> list = litagionSuperStringMap.get(litagion);
			for(String synonym : list){
				if(abstractText.contains(synonym)){
					abstractText = abstractText.replaceAll(synonym, "SPEC_LITAGION");
				}
			}
		}
		return abstractText;
	}
	
	public String replaceDictionaryTerm(String abstractText, HashMap<String, ArrayList<String>> dictionaryMap){		
		Iterator it = dictionaryMap.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry pairs = (Map.Entry)it.next();
	        String key = pairs.getKey().toString();
	        ArrayList<String> values = (ArrayList<String>) pairs.getValue();
	        for(String value : values){
	        	if(abstractText.contains(value)){
					abstractText = abstractText.replaceAll(value, key);
				}
	        }
	    }
		return abstractText;
	}
	
	
	public HashMap<String, ArrayList<String>> loadHarmClass(){
		HashMap<String, ArrayList<String>> map = new HashMap<String, ArrayList<String>>();
		try{
			FileReader fr = new FileReader("resources/harmSynonyms.csv");
			BufferedReader bf = new BufferedReader(fr);
			String line = "";
			while((line = bf.readLine()) != null){
				String[] splitter = line.split(",");
				String harm = splitter[0].trim().toLowerCase();
				String synonym = splitter[1].trim().toLowerCase();
				
				if(map.containsKey(harm)){
					ArrayList<String> list = map.get(harm);
					list.add(synonym);
					map.remove(harm);
					map.put(harm, list);
				}else{
					ArrayList<String> list = new ArrayList<String>();
					list.add(synonym);
					list.add(harm);
					map.put(harm, list);
				}
			}
		}catch(IOException e){
			e.printStackTrace();
		}
		return map;
	}
	
	
	public HashMap<String, ArrayList<String>> loadLitagionSuperStrings(){
		HashMap<String, ArrayList<String>> map = new HashMap<String, ArrayList<String>>();
		try{
			FileReader fr = new FileReader("resources/litagionSuperstrings.csv");
			BufferedReader bf = new BufferedReader(fr);
			String line = "";
			while((line = bf.readLine()) != null){
				String litagion = "";
				String synonym = "";
				if(line.contains("\",\"")){
					String[] splitter = line.split("\",\"");
					litagion = splitter[0].trim().toLowerCase().replaceAll("\"", "");
					synonym = splitter[1].trim().toLowerCase().replaceAll("\"", "");
				}else if(line.contains(",\"")){
					String[] splitter = line.split(",\"");
					litagion = splitter[0].trim().toLowerCase().replaceAll("\"", "");
					synonym = splitter[1].trim().toLowerCase().replaceAll("\"", "");
				}else if(line.contains("\",")){
					String[] splitter = line.split("\",");
					litagion = splitter[0].trim().toLowerCase().replaceAll("\"", "");
					synonym = splitter[1].trim().toLowerCase().replaceAll("\"", "");
				}else{
					String[] splitter = line.split(",");
					litagion = splitter[0].trim().toLowerCase();
					synonym = splitter[1].trim().toLowerCase();
				}
				if(map.containsKey(litagion)){
					ArrayList<String> list = map.get(litagion);
					list.add(synonym);
					map.remove(litagion);
					map.put(litagion, list);
				}else{
					ArrayList<String> list = new ArrayList<String>();
					list.add(synonym);
					list.add(litagion);
					map.put(litagion, list);
				}
			}
		}catch(IOException e){
			e.printStackTrace();
		}
		return map;
	}
	
	public HashMap<String, ArrayList<String>> loadLitagionSynonym(){
		HashMap<String, ArrayList<String>> map = new HashMap<String, ArrayList<String>>();
		try{
			FileReader fr = new FileReader("resources/litagionSynonyms.csv");
			BufferedReader bf = new BufferedReader(fr);
			String line = "";
			while((line = bf.readLine()) != null){
				String litagion = "";
				String synonym = "";
				if(line.contains("\",\"")){
					String[] splitter = line.split("\",\"");
					litagion = splitter[0].trim().toLowerCase().replaceAll("\"", "");
					synonym = splitter[1].trim().toLowerCase().replaceAll("\"", "");
				}else if(line.contains(",\"")){
					String[] splitter = line.split(",\"");
					litagion = splitter[0].trim().toLowerCase().replaceAll("\"", "");
					synonym = splitter[1].trim().toLowerCase().replaceAll("\"", "");
				}else if(line.contains("\",")){
					String[] splitter = line.split("\",");
					litagion = splitter[0].trim().toLowerCase().replaceAll("\"", "");
					synonym = splitter[1].trim().toLowerCase().replaceAll("\"", "");
				}else{
					String[] splitter = line.split(",");
					litagion = splitter[0].trim().toLowerCase();
					synonym = splitter[1].trim().toLowerCase();
				}
				if(map.containsKey(litagion)){
					ArrayList<String> list = map.get(litagion);
					list.add(synonym);
					map.remove(litagion);
					map.put(litagion, list);
				}else{
					ArrayList<String> list = new ArrayList<String>();
					list.add(synonym);
					list.add(litagion);
					map.put(litagion, list);
				}
			}
		}catch(IOException e){
			e.printStackTrace();
		}
		return map;
	}
	
	public HashMap<String, ArrayList<String>> loadDataDictionary(){
		HashMap<String, ArrayList<String>> map = new HashMap<String, ArrayList<String>>();
		try{
			FileReader fr = new FileReader("resources/directionDic.csv");
			BufferedReader bf = new BufferedReader(fr);
			String line = "";
			while((line = bf.readLine()) != null){
				String[] splitter = line.split(",");
				if(splitter.length == 14){
					addToMap("IS_ARTICLE", splitter[0].trim().toLowerCase(), map);
					addToMap("IS_INDICATOR_P", splitter[1].trim().toLowerCase(), map);
					addToMap("IS_INDICATOR_N", splitter[2].trim().toLowerCase(), map);
					addToMap("IS_LITAGION", splitter[3].trim().toLowerCase(), map);
					addToMap("IS_HARM", splitter[4].trim().toLowerCase(), map);
					addToMap("IS_CAUSATION_P", splitter[5].trim().toLowerCase(), map);
					addToMap("IS_CAUSATION_N", splitter[6].trim().toLowerCase(), map);
					addToMap("IS_NEG_PHRASE", splitter[7].trim().toLowerCase(), map);
					addToMap("IS_NEG_MOD", splitter[8].trim().toLowerCase(), map);
					addToMap("IS_MAYBE_PHRASE", splitter[9].trim().toLowerCase(), map);
					addToMap("IS_MAYBE_MOD", splitter[10].trim().toLowerCase(), map);
					addToMap("IS_CI", splitter[11].trim().toLowerCase(), map);
					addToMap("IS_IMPORTANT_P", splitter[12].trim().toLowerCase(), map);
					addToMap("IS_IMPORTANT_N", splitter[13].trim().toLowerCase(), map);
				}
			}
		}catch(IOException e){
			e.printStackTrace();
		}
		return map;
	}
	
	public void addToMap(String key, String value, HashMap<String, ArrayList<String>> map){
		if(!value.equals("")){
			if(map.containsKey(key)){
				ArrayList<String> list = map.get(key);
				list.add(value);
				map.remove(key);
				map.put(key, list);
				}else{
				ArrayList<String> list = new ArrayList<String>();
				list.add(value);
				map.put(key, list);
			}
		}
	}
	
	public static void main(String[] args) {
		new SPECReplacer().execute();
	}

}
