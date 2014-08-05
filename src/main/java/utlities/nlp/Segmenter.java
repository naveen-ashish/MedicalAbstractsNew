/*
 * @module: NOT IN USE
 */


package utlities.nlp;


import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.commons.collections.CollectionUtils;

import edu.stanford.nlp.tagger.maxent.MaxentTagger;


public class Segmenter {
	private int WORD_DISTANCE = 6;
	
	public boolean isTellSentence(String sentence){
		Object val = getLitagion(tokenizeSentence(sentence), populateLitagionList());
		if(val != null){
			return checkVerbPresence(val.toString(), tagPOS(sentence));
		}
		return false;
	}
	
	
	public boolean checkVerbPresence(String litagion, String posTaggedSentence){
		int cnt = -1;
		StringTokenizer st = new StringTokenizer(posTaggedSentence);
		while (st.hasMoreTokens() && cnt < WORD_DISTANCE) {
			String[] splitter = st.nextToken().split("_");
			String token = splitter[0];
			String pos = splitter[1];
			if(cnt > 0){
				cnt++;
			}
			
			if(token.equals(litagion)){
				cnt = 1;
			}
			
			if(cnt > 1 && pos.startsWith("VB")){
				return true;
			}
		}
		return false;
	}
	
	
	public HashSet<String> populateLitagionList(){
		HashSet<String> litagionList = new HashSet<String>();
		litagionList.add("Mancozeb");
		return litagionList;
	}
	
	
	public HashSet<String> tokenizeSentence(String sentence){
		HashSet<String> tokensList = new HashSet<String>();
		StringTokenizer tokenizer = new StringTokenizer(sentence);
		while (tokenizer.hasMoreElements()) {
			String text = tokenizer.nextElement().toString();
			tokensList.add(text);
		}
		return tokensList;
	}
	
	public Object getLitagion(HashSet<String> tokensList, HashSet<String> litagionList){
		if(CollectionUtils.containsAny(litagionList, tokensList)){
			Set<String> intersectionList = (Set<String>) CollectionUtils.intersection(tokensList, litagionList);
			return intersectionList.toArray()[0];

		}
		return null;
	}
	
	public String tagPOS(String sentence){
		MaxentTagger tagger = new 
				MaxentTagger("resources/english-left3words-distsim.tagger");
		return tagger.tagString(sentence);
	}
	
	public static void main(String[] args) {
		System.out.println(new Segmenter().isTellSentence("We have seen that Mancozeb under certain conditions alters blood pressure"));
	}
}
