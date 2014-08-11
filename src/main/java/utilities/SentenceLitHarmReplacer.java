package utilities;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Stack;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import utlities.nlp.Segmenter;

import com.jayway.jsonpath.JsonPath;

public class SentenceLitHarmReplacer {
	private final String FREEBASE_API_KEY = "AIzaSyAAZHVqpUvpIvUwOtUwYzU2FMVxhcxCCzE";
	private String TOPIC_ID = "";
	
	private HashMap hmIndHarm = new HashMap();
	
	private HashMap hmIndLit = new HashMap();
	
	private IO io = new IO();
	
	public void init(){
		
		readIndicators();
		
		//System.exit(1);
	}
	
	private void readIndicators() {
		
		Stack stk = io.readFileStk("resources/HarmIndicators.txt");
		
		while (!stk.isEmpty()) {
			
			String str=stk.pop().toString();
			
			//System.out.println(str);
			
			int ind = str.indexOf("\t");
			
			String hClass = str.substring(0,ind);
			
			String variants = str.substring(ind+1);
			
			//System.out.println(hClass+"|||"+variants);
			
			hmIndHarm.put(hClass,variants);
		}
		
		stk = io.readFileStk("resources/LitIndicators.txt");
		
		while (!stk.isEmpty()) {
			
			String str=stk.pop().toString();
			
			//System.out.println(str);
			
			int ind = str.indexOf("\t");
			
			String variants = str.substring(ind+1);
			
			
			String lit = str.substring(0,ind);
			
			int ind1 = lit.indexOf("_[");
			
			lit = lit.substring(ind1+2);
			
			ind1 = lit.indexOf("]");
			
			if (ind1>0) lit = lit.substring(0,ind1);
			
			
			//System.out.println(lit+">>>>"+variants);
			
			hmIndLit.put(lit,variants);
		}
	}
	
	public String tagIndicatorsBased(String post, String specHarm, String specLit) {
		
		if (specLit.indexOf("|")>-1) {
			
			int in = specLit.indexOf("|");
			specLit = specLit.substring(0,in);
		}
		
		//System.out.println("TAG: "+specLit);
		
		try {
		
			post = post.replaceAll(specHarm, "SPECHARM");
		} catch (Exception e){}
		
		post = post.replaceAll(specLit, "SPECLIT");
		
		specHarm=specHarm.toLowerCase();
		
		if (hmIndHarm.get(specHarm) !=null) {
			
			String variants = hmIndHarm.get(specHarm).toString();
			
			String[] parts = variants.split("\t");
			
			int L= parts.length;
			
			for (int i=0 ;i <L ; ++i) {
				
				String var = parts[i].trim();
				
				//System.out.println(var);
				
				//if (post.indexOf(var)>-1)System.out.println("HARM !!!!!!!!!!!!!!!"+var);
				
				post = post.replaceAll(var, "SPEC_HARM");
			}
		}
		
		if (hmIndLit.get(specLit) !=null) {
			
			String variants = hmIndLit.get(specLit).toString();
			
			String[] parts = variants.split("\t");
			
			int L= parts.length;
			
			for (int i=0 ;i <L ; ++i) {
				
				String var = parts[i].trim();
				
				//System.out.println(var);
				
				//if (post.indexOf(var)>-1)System.out.println("LIT !!!!!!!!!!!!!!!"+var);
				
				post = post.replaceAll(var, "SPEC_LIT");
			}
		}
		
		return post;
	}
	
	public String replace(String sentence, String specifiedLitagion, String specifiedHarmCondition){
		HashSet<String> litagionSet = new HashSet<String>();
		HashSet<String> harmConditionSet = new HashSet<String>();
		getFreebaseAlias(specifiedLitagion, litagionSet);
		getFreebaseAlias(specifiedHarmCondition, harmConditionSet);

		sentence = sentence.trim().toLowerCase();
		
		Segmenter checker = new Segmenter();
		HashSet<String> tokens = checker.tokenizeSentence(sentence);
		
		for(String token : tokens){
			if(checkEquals(token, litagionSet)){
				sentence = sentence.replace(token, specifiedLitagion);
			}
			
			if(checkEquals(token, harmConditionSet)){
				sentence = sentence.replace(token, specifiedHarmCondition);
			}
		}
		System.out.println(sentence);
		return sentence;
	}
	
	public boolean checkEquals(String token, HashSet<String> set){
		for(String entry : set){
			if(entry.equals(token)){
				return true;
			}
		}
		return false;
	}
	
	
	public void getFreebaseAlias(String searchTerm, HashSet<String> set){
		getFreebaseSearchResult(searchTerm, set);
		getFreebaseTopicResult(set);
	}
	
	public void getFreebaseSearchResult(String searchTerm, HashSet<String> set){
		String FREEBASE_SEARCH_URL = "https://www.googleapis.com/freebase/v1/search";
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("query", searchTerm));
		params.add(new BasicNameValuePair("key", FREEBASE_API_KEY));
		
		
		String url = FREEBASE_SEARCH_URL + "?" + URLEncodedUtils.format(params, "UTF-8"); 
		JSONArray array = getJSONResultsArray(url, "search");
		for(int i = 0 ; i < array.size() ; i++){
			Object result = array.get(i);
			try{
			if(i == 0){
				TOPIC_ID = JsonPath.read(result,"$.id").toString();
			}
			String related = JsonPath.read(result,"$.name").toString();
			if(!related.trim().equals("") && related.trim().length() > 0){
				set.add(related.trim().toLowerCase());
			}
			} catch (Exception e){}
		}
	}

	public void getFreebaseTopicResult(HashSet<String> set){
		String FREEBASE_TOPIC_URL = "https://www.googleapis.com/freebase/v1/topic" + TOPIC_ID;
				
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("key", FREEBASE_API_KEY));
		
		String url = FREEBASE_TOPIC_URL + "?" + URLEncodedUtils.format(params, "UTF-8"); 
		JSONArray array = getJSONResultsArray(url, "topic");
		
		for(int i = 0 ; i < array.size() ; i++){
			Object result = array.get(i);
			String related = JsonPath.read(result,"$.text").toString();
			if(!related.trim().equals("") && related.trim().length() > 0){
				set.add(related.trim().toLowerCase());
			}
			
		}
	}
	
	
	public JSONArray getJSONResultsArray(String url, String type){
		HttpClient httpclient = new DefaultHttpClient();
		JSONParser parser = new JSONParser();
		try {
			HttpResponse httpResponse = httpclient.execute(new HttpGet(url));
			JSONObject response = (JSONObject)parser.parse(EntityUtils.toString(httpResponse.getEntity()));
			if(type.equals("search")){
				return (JSONArray)response.get("result");
			}else if(type.equals("topic")){
				return (JSONArray)((JSONObject)((JSONObject)response.get("property"))
						.get("/common/topic/alias")).get("values");
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (org.json.simple.parser.ParseException e) {
			e.printStackTrace();
		}	
		return null;
	}
	
	public static void main(String[] args) {
		String sentence = "Usually people have kidney issues when they have too much NaCl";
		String specifiedLitagion = "Dietary Salt";
		specifiedLitagion = "Cadmium";
		String specifiedHarmCondition = "Renal";
		specifiedHarmCondition = "Vascular";
		
		new SentenceLitHarmReplacer().replace(sentence, specifiedLitagion, specifiedHarmCondition);
	}

}
