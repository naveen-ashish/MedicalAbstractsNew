/*
 * @module: Identifies tell sentences in abstracts
 * @author: Naveen Ashish
 * @contact: naveen.ashish@gmail.com
 * @last updated: 07/08/2014
 */
package tell_sentence;


import io.ReadResources;

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
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.*;

import utilities.IO;
import utilities.SentenceLitHarmReplacer;
import utilities.SentenceTypeClassifier;


public class TellSentenceGenerator {

	private static SentenceLitHarmReplacer slhr = new SentenceLitHarmReplacer();
	
	private static HashMap hmDir = new HashMap();
	
	private static HashMap hmLitAbbreviations = new HashMap();
		
	private static Stack stkAnywhere = new Stack();
	
	private static Stack stkBegin = new Stack();
	
	private static Stack stkCause = new Stack();
	
	private static Stack stkBigrams1=new Stack();
	
	private static Stack stkBigrams2=new Stack();
	
	private static HashMap hmBigrams1=new HashMap();
	
	private static HashMap hmBigrams2=new HashMap();
	
	private static String allabstractsLoc;
	
	private static String traDataLoc;
	
	private static String harmSynLoc;
	
	private static String litSupLoc;
	
	private static String litSynLoc;
	
	private static String dictLoc;
	
	private static String litAbbrevLoc;
	
	private static Integer tType=2;
	
	private static HashMap hmOneConfoundingLitagion = new HashMap();
	
	private static HashMap hmNoTells = new HashMap();
	
	private static Stack stkRemove = new Stack();
	
	HashMap<String, ArrayList<String>> harmMap ;
	
	HashMap<String, ArrayList<String>> litagionSuperStringMap ;
	
	HashMap<String, ArrayList<String>> litagionSynonymMap;
	//HashMap<String, ArrayList<String>> dictionaryMap = loadDataDictionary();
	HashMap<String, ArrayList<String>> dictionaryMap ;
	
	
	/*
	 * @functionality main execution steps 
	 */
	public void execute(io.ReadResources RR){

		//io.ReadResources RR = new io.ReadResources();
		
		//RR.readAllResources();
		
		HashMap abstractMap=RR.abstractMap;
		
		HashMap trainingInfoMap = RR.trainingInfoMap;
		
		hmDir = RR.hmDir;
		
		harmMap = RR.harmMap;
		
		litagionSuperStringMap = RR.litagionSuperStringMap;
		
		litagionSynonymMap = RR.litagionSynonymMap;
		
		dictionaryMap = RR.dictionaryMap;
		
		hmLitAbbreviations=RR.readAbbreviations();
		
		replaceContentFirstPass(abstractMap, trainingInfoMap);
		
		System.out.println("------------------");
		
		System.out.println(abstractMap.size());
		
		replaceContentSecondPass(trainingInfoMap);
		
		//replaceContentThirdPass(trainingInfoMap);
		
		replaceContentFourthPass(trainingInfoMap);
	}
	
	
	
	/*
	 * @functionality: replace content with tags according to supplied dictionaries
	 * @param	abstractMap	is a hash map of abstracts (content)
	 * @param	trainingInfoMap	is a hash map of proximity tags (training data)
	 */
	public Vector replaceContentFirstPass(HashMap<String, String> abstractMap, HashMap<String, String> trainingInfoMap){
		
		int CX=0,C1=0,C2=0,CA=0,C0=0,P1=0,P2=0;
		
		Vector vvRes = new Vector();
		
		try{
			
			int K=0;
			
			Iterator it = abstractMap.entrySet().iterator();
			
			//while ((it.hasNext()) ){
		    while ((it.hasNext()) && (K<100)){
		    	++K;
		    	
		    	boolean titleOnly=false;
		    	
		        Map.Entry pairs = (Map.Entry)it.next();
		    
		        String abstractID = pairs.getKey().toString();
		        
		        String id = pairs.getValue().toString().split("##")[0];
		        
		        String abstractText = pairs.getValue().toString().split("##")[1];
		            
		        //abstractText="Modulation of renal CNG-A3 sodium channel in rats subjected to low- and high-sodium diets.                 In this work, we studied the mRNA distribution of CNG-A3, an amiloride-sensitive sodium channel that belongs to the cyclic nucleotide-gated (CNG) family of channels, along the rat nephron. The possible involvement of aldosterone in this process was also studied. We also evaluated its expression in rats subjected to diets with different concentrations of sodium or to alterations in aldosterone plasma levels. Total RNA isolated from whole kidney and/or dissected nephron segments of Wistar rats subjected to low- and high-sodium diets, furosemide treatment, adrenalectomy, and adrenalectomy with replacement by aldosterone were analyzed by the use of Western blot, ribonuclease protection assay (RPA) and/or reverse transcription followed by semi-quantitative polymerase chain reaction (RT-PCR). CNG-A3 sodium channel mRNA and protein expression, in whole kidneys of rats subjected to high-Na+ diet, were lower than those in animals given a low-salt diet. Renal CNG-A3 mRNA expression was also decreased in adrenalectomized rats, and was normalized by aldosterone replacement. Moreover, a CNG-A3 mRNA expression study in different nephron segments revealed that aldosterone modulation is present in the cortical thick ascending loop (cTAL) and cortical collecting duct (CCD). This result suggests that CNG-A3 is responsive to the same hormone signaling as the amiloride sensitive sodium channel ENaC and suggests the CNG-A3 may have a physiological role in sodium reabsorption.";
	        
		        if(trainingInfoMap.containsKey(abstractID)){
		    
		        	ArrayList<String> sentencesList = new ArrayList<String>();
				       
		        	++C0;
		        	
		        	String info = trainingInfoMap.get(abstractID);
		        	
		        	String litagion = info.split("##")[0];
		        	
		        	String harmClass = info.split("##")[1];
		        	
		        	String origlitagion=litagion;
		        	
		        	String[] sentences = abstractText.split("[\\.;]");
		        	
		        	int S = sentences.length;
		        	
		        	if (S==1) titleOnly=true;
		        	
		        	String origsent="";
		        	
		        	String str="";
		        	
		        	//String litagionVariant = getVariant(litagion, sentences, S);
		        	
		        	String litagionVariant=getStoredVariant(litagion);
		        	
		        	for (int i=0; i <S; ++i){
		        	
		        		String sent=sentences[i].trim();
		        		
		        		//sent="In contrast to many other peroxisome proliferating agents, APFO did not possess hypolipidemic activity.";
		        		//sent="However, a reduction in postnatal body weights was seen in offspring from mothers with pregestational exposure.";
		        		//sent="It was concluded that both pyrethroids produce their different syndromes of toxicity predominantly by their action on the spinal cord.";	
		        		//litagion="[UnknownCAS]_[Pyrethrins]";
		        		
		        		sent=sent.replaceAll("\\-", " ").trim();
		        		
		        		litagion=litagion.replaceAll("\\-", " ").trim();
		      
		        		origsent=sent;
		        		
			        	sent = replaceHarmClass(harmClass, sent, harmMap);
			        	
			        	sent = replaceSpecifiedLitagion(litagion, sent, litagionSuperStringMap, litagionSynonymMap);
			        	
			        	if (litagionVariant.length()>1){
	
			        		litagionVariant=litagionVariant.replaceAll("\\-", " ").trim();
			        	
			        		String bsent=sent;
			        	
			        		//System.out.println(sent);
			        		
			        		sent = sent.replaceAll("[^a-zA-Z0-9]"+litagionVariant+"[^a-zA-Z0-9]","SPEC_LITAGION");
			        		  		
			        		sent = sent.replaceAll("[^a-zA-Z0-9]"+litagionVariant.toLowerCase()+"[^a-zA-Z0-9]","SPEC_LITAGION");
	
			        		//if (!bsent.equals(sent)) System.out.println("VARN: "+litagionVariant);
			        	}
			        	
			        	sent = replaceDictionaryTerm(sent, dictionaryMap);
			        	
			        	sentencesList.add(sent);
			        	
			        	if (hmDir.get(abstractID+litagion+harmClass)!=null){
			        
			        		String directionality=hmDir.get(abstractID+litagion+harmClass).toString();
			        		//tellBigrams(sent,directionality);
			        	}
			        	
			        	//sent = replaceAnyLitagion(sent, litagionSuperStringMap, litagionSynonymMap);
			        			        	
			        	//System.out.println(sent);
			        	
			        	//System.exit(1);
			        	
			        	if (isTellSentence(tType,sent,i,S)){
			        	//if (isTellSentence(sent)){
			        		
			        		//origsent = replaceHarmClass(harmClass, origsent, harmMap);
				        	
				        	//origsent = replaceLitagion(litagion, origsent, litagionSuperStringMap, litagionSynonymMap);
				        	
				        	//origsent = replaceDictionaryTerm(origsent, dictionaryMap);
				        				        	
			        		//if (litagionVariant.length()>1) origsent = origsent.replaceAll("[^a-zA-Z0-9]"+litagionVariant.toLowerCase()+"[^a-zA-Z0-9]","SPEC_LITAGION");
				        		        		
				        	str=str+origsent+"|";
			        	} 
			        	
			        	//System.out.println(sent); System.exit(1);
		        	}
		        	
		        	//System.out.println(sentencesList.toString());
		        	
		            String features=new SentenceTypeClassifier().getNewFeatures(sentencesList);
		        		
		        	litagion=origlitagion;
		        	
		        	if (hmDir.get(abstractID+litagion+harmClass)!=null){
		        	
		        		++CA;
			          
		        		if (str.length()<2)  {
		        			
		        			String directionality=hmDir.get(abstractID+litagion+harmClass).toString();
		        			//System.out.println("No tells: "+abstractID+" : "+abstractText);
		        			
		        			hmNoTells.put(abstractID, abstractText);
		        			
		        			if (hasOneConfoundingLitagion(litagion,abstractText,litagionSuperStringMap, litagionSynonymMap)) hmOneConfoundingLitagion.put(abstractID, abstractText);
		        		
		        			//System.out.println(hasOneConfoundingLitagion(litagion,abstractText,litagionSuperStringMap, litagionSynonymMap));
		        			
		        			if (directionality.equals("1")|| directionality.equals("2")) {
		        				
		        				vvRes.add(directionality+"\t"+abstractID+"\t"+litagion+"\t"+harmClass+"\t"+str+"\n");
		        				
		        			}
				        	
			        	} else {
			        		String directionality=hmDir.get(abstractID+litagion+harmClass).toString();
			        		
			        		//if (directionality.equals("2")) {
			        			
			        		if (directionality.equals("1")|| directionality.equals("2")) {
			        			
			        			vvRes.add(directionality+"\t"+abstractID+"\t"+litagion+"\t"+harmClass+"\t"+str+"\n");
		        				
			        			//System.out.println(directionality+"\t"+abstractID+"\t"+litagion+"\t"+harmClass+"\t"+str+"\n");
		        				
			        			
		        				String dirn="";
		        				
		        				if (directionality.equals("1")) dirn="T"; 
		        				
		        				if (directionality.equals("2")) dirn="F"; 
		        				
		        				++C1;
		        			}
				        	
			        	}
		        	}
		        }
		    }
		 
		    io.WriteResults.writeTells(tType, "First", vvRes);
		    
			 System.out.println("Done !!\nTell Sentences generated and placed in <project folder>/<type>-tell-sentences-<datestamp>.txt");
		   
		   // System.out.println(CX+" abstracts excluded ");
		    System.out.println(C1+" abstracts provided explicit tell sentences");
		   // System.out.println(C2+" has no negation");
		   // System.out.println(P1+" , "+P2);
		}catch(Exception e){
			e.printStackTrace();
		}
		
	
		return vvRes;
	}

	
	
	/*
	 * @functionality: replace content with tags according to supplied dictionaries
	 * @param	abstractMap	is a hash map of abstracts (content)
	 * @param	trainingInfoMap	is a hash map of proximity tags (training data)
	 */
	public Vector replaceContentSecondPass(HashMap trainingInfoMap){

		Vector vvRes = new Vector();
		
		int CX=0,C1=0,C2=0,CA=0,C0=0,P1=0,P2=0;
		
		try{
			
			int K=0;
			
			Collection c = hmOneConfoundingLitagion.keySet();
			
			Iterator it = c.iterator();
			
			while ((it.hasNext()) ){
		    //while ((it.hasNext()) && (K<1000)){
		    	++K;
		    	
		    	boolean titleOnly=false;
		    	
		        String abstractID = it.next().toString();
		        
		       // String id = pairs.getValue().toString().split("##")[0];
		        
		        String abstractText = hmOneConfoundingLitagion.get(abstractID).toString();
		            
		        //abstractText="Modulation of renal CNG-A3 sodium channel in rats subjected to low- and high-sodium diets.                 In this work, we studied the mRNA distribution of CNG-A3, an amiloride-sensitive sodium channel that belongs to the cyclic nucleotide-gated (CNG) family of channels, along the rat nephron. The possible involvement of aldosterone in this process was also studied. We also evaluated its expression in rats subjected to diets with different concentrations of sodium or to alterations in aldosterone plasma levels. Total RNA isolated from whole kidney and/or dissected nephron segments of Wistar rats subjected to low- and high-sodium diets, furosemide treatment, adrenalectomy, and adrenalectomy with replacement by aldosterone were analyzed by the use of Western blot, ribonuclease protection assay (RPA) and/or reverse transcription followed by semi-quantitative polymerase chain reaction (RT-PCR). CNG-A3 sodium channel mRNA and protein expression, in whole kidneys of rats subjected to high-Na+ diet, were lower than those in animals given a low-salt diet. Renal CNG-A3 mRNA expression was also decreased in adrenalectomized rats, and was normalized by aldosterone replacement. Moreover, a CNG-A3 mRNA expression study in different nephron segments revealed that aldosterone modulation is present in the cortical thick ascending loop (cTAL) and cortical collecting duct (CCD). This result suggests that CNG-A3 is responsive to the same hormone signaling as the amiloride sensitive sodium channel ENaC and suggests the CNG-A3 may have a physiological role in sodium reabsorption.";
	        
		        if(trainingInfoMap.containsKey(abstractID)){
		        	
		        	ArrayList<String> sentencesList = new ArrayList<String>();
				       
		        	++C0;
		        	
		        	String info = trainingInfoMap.get(abstractID).toString();
		        	
		        	String litagion = info.split("##")[0];
		        	
		        	String harmClass = info.split("##")[1];
		        	
		        	String origlitagion=litagion;
		        	
		        	String[] sentences = abstractText.split("[\\.;]");
		        	
		        	int S = sentences.length;
		        	
		        	if (S==1) titleOnly=true;
		        	
		        	String origsent="";
		        	
		        	String str="";
		        	
		        	//String litagionVariant = getVariant(litagion, sentences, S);
		        	
		        	String litagionVariant=getStoredVariant(litagion);
		        	
		        	for (int i=0; i <S; ++i){
		        	
		        		String sent=sentences[i].trim();
		        		
		        		//sent="In contrast to many other peroxisome proliferating agents, APFO did not possess hypolipidemic activity.";
		        		//sent="However, a reduction in postnatal body weights was seen in offspring from mothers with pregestational exposure.";
		        		//sent="It was concluded that both pyrethroids produce their different syndromes of toxicity predominantly by their action on the spinal cord.";	
		        		//litagion="[UnknownCAS]_[Pyrethrins]";
		        		
		        		sent=sent.replaceAll("\\-", " ").trim();
		        		
		        		litagion=litagion.replaceAll("\\-", " ").trim();
		      
		        		origsent=sent;
		        		
			        	sent = replaceHarmClass(harmClass, sent, harmMap);
			        	
			        	sent = replaceSpecifiedLitagion(litagion, sent, litagionSuperStringMap, litagionSynonymMap);
			        	
			        	if (litagionVariant.length()>1){
	
			        		litagionVariant=litagionVariant.replaceAll("\\-", " ").trim();
			        	
			        		String bsent=sent;
			        	
			        		//System.out.println(sent);
			        		
			        		sent = sent.replaceAll("[^a-zA-Z0-9]"+litagionVariant+"[^a-zA-Z0-9]","SPEC_LITAGION");
			        		  		
			        		sent = sent.replaceAll("[^a-zA-Z0-9]"+litagionVariant.toLowerCase()+"[^a-zA-Z0-9]","SPEC_LITAGION");
	
			        		//if (!bsent.equals(sent)) System.out.println("VARN: "+litagionVariant);
			        	}
			        	
			        	sent = replaceDictionaryTerm(sent, dictionaryMap);
			        	
			        	sentencesList.add(sent);
			        	
			        	if (hmDir.get(abstractID+litagion+harmClass)!=null){
			        
			        		String directionality=hmDir.get(abstractID+litagion+harmClass).toString();
			        		//tellBigrams(sent,directionality);
			        	}
			        	
			        	//sent = replaceAnyLitagion(sent, litagionSuperStringMap, litagionSynonymMap);
			        			        	
			        	//System.out.println(sent);
			        	
			        	//System.exit(1);
			        	
			        	if (isTellSentenceSecondPass(tType,sent,i,S)){
			        	//if (isTellSentence(sent)){
			        		
			        		//origsent = replaceHarmClass(harmClass, origsent, harmMap);
				        	
				        	//origsent = replaceLitagion(litagion, origsent, litagionSuperStringMap, litagionSynonymMap);
				        	
				        	//origsent = replaceDictionaryTerm(origsent, dictionaryMap);
				        				        	
			        		//if (litagionVariant.length()>1) origsent = origsent.replaceAll("[^a-zA-Z0-9]"+litagionVariant.toLowerCase()+"[^a-zA-Z0-9]","SPEC_LITAGION");
				        		        		
				        	str=str+origsent+"|";
			        	} 
			        	
			        	//System.out.println(sent); System.exit(1);
		        	}
		        	
		        	//System.out.println(sentencesList.toString());
		        	
		            String features=new SentenceTypeClassifier().getNewFeatures(sentencesList);
		        		
		        	litagion=origlitagion;
		        	
		        	if (hmDir.get(abstractID+litagion+harmClass)!=null){
		        	
		        		++CA;
			          
		        		if (str.length()<2)  {
		        			
				        	
			        	} else {
			        		String directionality=hmDir.get(abstractID+litagion+harmClass).toString();
			        		
			        		//hmNoTells.remove(abstractID);
			        		
			        		stkRemove.push(abstractID);
			        		
			        		//if (directionality.equals("2")) {
			        			
			        		if (directionality.equals("1")|| directionality.equals("2")) {
			        			
			        			vvRes.add(directionality+"\t"+abstractID+"\t"+litagion+"\t"+harmClass+"\t"+str+"\n");
		        				
		        				String dirn="";
		        				
		        				if (directionality.equals("1")) dirn="T"; 
		        				
		        				if (directionality.equals("2")) dirn="F"; 
		        				
		        				++C1;
		        			}
				        	
			        	}
		        	}
		        }
		    }

			  io.WriteResults.writeTells(tType, "Second", vvRes);
			   
			 System.out.println("Done !!\nTell Sentences generated and placed in <project folder>/<type>-tell-sentences-<datestamp>.txt");
		   
		   // System.out.println(CX+" abstracts excluded ");
		    System.out.println(C1+" abstracts provided explicit tell sentences");
		   // System.out.println(C2+" has no negation");
		   // System.out.println(P1+" , "+P2);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return vvRes;
	}

	
	/*
	 * @functionality: replace content with tags according to supplied dictionaries
	 * @param	abstractMap	is a hash map of abstracts (content)
	 * @param	trainingInfoMap	is a hash map of proximity tags (training data)
	 */
	public Vector replaceContentThirdPass(HashMap trainingInfoMap){
		
		Vector vvRes = new Vector();
		
		int CX=0,C1=0,C2=0,CA=0,C0=0,P1=0,P2=0;
		
		try{
			
			int K=0;
			
			Collection c = hmNoTells.keySet();
			
			Iterator it = c.iterator();
			
			while ((it.hasNext()) ){
		    //while ((it.hasNext()) && (K<1000)){
		    	++K;
		    	
		    	boolean titleOnly=false;
		    	
		        String abstractID = it.next().toString();
		        
		       // System.out.println(abstractID);
		        
		       // String id = pairs.getValue().toString().split("##")[0];
		        
		        String abstractText = hmNoTells.get(abstractID).toString();
		            
		        //abstractText="Modulation of renal CNG-A3 sodium channel in rats subjected to low- and high-sodium diets.                 In this work, we studied the mRNA distribution of CNG-A3, an amiloride-sensitive sodium channel that belongs to the cyclic nucleotide-gated (CNG) family of channels, along the rat nephron. The possible involvement of aldosterone in this process was also studied. We also evaluated its expression in rats subjected to diets with different concentrations of sodium or to alterations in aldosterone plasma levels. Total RNA isolated from whole kidney and/or dissected nephron segments of Wistar rats subjected to low- and high-sodium diets, furosemide treatment, adrenalectomy, and adrenalectomy with replacement by aldosterone were analyzed by the use of Western blot, ribonuclease protection assay (RPA) and/or reverse transcription followed by semi-quantitative polymerase chain reaction (RT-PCR). CNG-A3 sodium channel mRNA and protein expression, in whole kidneys of rats subjected to high-Na+ diet, were lower than those in animals given a low-salt diet. Renal CNG-A3 mRNA expression was also decreased in adrenalectomized rats, and was normalized by aldosterone replacement. Moreover, a CNG-A3 mRNA expression study in different nephron segments revealed that aldosterone modulation is present in the cortical thick ascending loop (cTAL) and cortical collecting duct (CCD). This result suggests that CNG-A3 is responsive to the same hormone signaling as the amiloride sensitive sodium channel ENaC and suggests the CNG-A3 may have a physiological role in sodium reabsorption.";
	        
		        if((trainingInfoMap.containsKey(abstractID)) && (!stkRemove.contains(abstractID))){
		        	
		        	ArrayList<String> sentencesList = new ArrayList<String>();
				       
		        	++C0;
		        	
		        	String info = trainingInfoMap.get(abstractID).toString();
		        	
		        	String litagion = info.split("##")[0];
		        	
		        	String harmClass = info.split("##")[1];
		        	
		        	String origlitagion=litagion;
		        	
		        	String[] sentences = abstractText.split("[\\.;]");
		        	
		        	int S = sentences.length;
		        	
		        	if (S==1) titleOnly=true;
		        	
		        	String origsent="";
		        	
		        	String str="";
		        	
		        	//String litagionVariant = getVariant(litagion, sentences, S);
		        	
		        	String litagionVariant=getStoredVariant(litagion);
		        	
		        	
		        		String sent=getTitle(abstractText);

		        		sent=sent.replaceAll("\\-", " ").trim();
		        		
		        		litagion=litagion.replaceAll("\\-", " ").trim();
		      
		        		origsent=sent;
		        		
			        	sent = replaceHarmClass(harmClass, sent, harmMap);
			        	
			        	sent = replaceSpecifiedLitagion(litagion, sent, litagionSuperStringMap, litagionSynonymMap);
			        	
			        	if (litagionVariant.length()>1){
	
			        		litagionVariant=litagionVariant.replaceAll("\\-", " ").trim();
			        	
			        		String bsent=sent;
			        	
			        		//System.out.println(sent);
			        		
			        		sent = sent.replaceAll("[^a-zA-Z0-9]"+litagionVariant+"[^a-zA-Z0-9]","SPEC_LITAGION");
			        		  		
			        		sent = sent.replaceAll("[^a-zA-Z0-9]"+litagionVariant.toLowerCase()+"[^a-zA-Z0-9]","SPEC_LITAGION");
	
			        		//if (!bsent.equals(sent)) System.out.println("VARN: "+litagionVariant);
			        	}
			        	
			        	sent = replaceDictionaryTerm(sent, dictionaryMap);
			        	
			        	sentencesList.add(sent);
			        	
			        	if (hmDir.get(abstractID+litagion+harmClass)!=null){
			        
			        		String directionality=hmDir.get(abstractID+litagion+harmClass).toString();
			        		//tellBigrams(sent,directionality);
			        	}
			        	

			        	
			        	if (isTellSentenceThirdPass(tType,sent,S)){
			             		
				        	str=str+origsent+"|";
			        	} 

		        	
		            String features=new SentenceTypeClassifier().getNewFeatures(sentencesList);
		        		
		        	litagion=origlitagion;
		        	
		        	if (hmDir.get(abstractID+litagion+harmClass)!=null){
		        	
		        		++CA;
			          
		        		if (str.length()<2)  {
		        			
				        	
			        	} else {
			        		String directionality=hmDir.get(abstractID+litagion+harmClass).toString();
			        		
			        		//hmNoTells.remove(abstractID);
			        		
			        		stkRemove.push(abstractID);
			        		
			        		//if (directionality.equals("2")) {
			        			
			        		if (directionality.equals("1")|| directionality.equals("2")) {
		
			        			vvRes.add(directionality+"\t"+abstractID+"\t"+litagion+"\t"+harmClass+"\t"+str+"\n");
		        				
		        				String dirn="";
		        				
		        				if (directionality.equals("1")) dirn="T"; 
		        				
		        				if (directionality.equals("2")) dirn="F"; 
		        				
		        				++C1;
		        			}
				        	
			        	}
		        	}
		        }
		    }
		   
			 io.WriteResults.writeTells(tType, "Third", vvRes);		   
			  
			 System.out.println("Done !!\nTell Sentences generated and placed in <project folder>/<type>-tell-sentences-<datestamp>.txt");
		   
		   // System.out.println(CX+" abstracts excluded ");
		    System.out.println(C1+" abstracts provided explicit tell sentences");
		   // System.out.println(C2+" has no negation");
		   // System.out.println(P1+" , "+P2);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return vvRes;
	}

	/*
	 * @functionality: replace content with tags according to supplied dictionaries
	 * @param	abstractMap	is a hash map of abstracts (content)
	 * @param	trainingInfoMap	is a hash map of proximity tags (training data)
	 */
	public Vector replaceContentFourthPass(HashMap trainingInfoMap){

		int CX=0,C1=0,C2=0,CA=0,C0=0,P1=0,P2=0;
		
		Vector vvRes = new Vector();
		
		try{
			
			int K=0;
			
			Collection c = hmNoTells.keySet();
			
			Iterator it = c.iterator();
			 while ((it.hasNext()) ){
		    //while ((it.hasNext()) && (K<1000)){
		    	++K;
		    	
		    	boolean titleOnly=false;
		    	
		        String abstractID = it.next().toString();
		        
		       // String id = pairs.getValue().toString().split("##")[0];
		        
		        String abstractText = hmNoTells.get(abstractID).toString();
		            
		        //abstractText="Modulation of renal CNG-A3 sodium channel in rats subjected to low- and high-sodium diets.                 In this work, we studied the mRNA distribution of CNG-A3, an amiloride-sensitive sodium channel that belongs to the cyclic nucleotide-gated (CNG) family of channels, along the rat nephron. The possible involvement of aldosterone in this process was also studied. We also evaluated its expression in rats subjected to diets with different concentrations of sodium or to alterations in aldosterone plasma levels. Total RNA isolated from whole kidney and/or dissected nephron segments of Wistar rats subjected to low- and high-sodium diets, furosemide treatment, adrenalectomy, and adrenalectomy with replacement by aldosterone were analyzed by the use of Western blot, ribonuclease protection assay (RPA) and/or reverse transcription followed by semi-quantitative polymerase chain reaction (RT-PCR). CNG-A3 sodium channel mRNA and protein expression, in whole kidneys of rats subjected to high-Na+ diet, were lower than those in animals given a low-salt diet. Renal CNG-A3 mRNA expression was also decreased in adrenalectomized rats, and was normalized by aldosterone replacement. Moreover, a CNG-A3 mRNA expression study in different nephron segments revealed that aldosterone modulation is present in the cortical thick ascending loop (cTAL) and cortical collecting duct (CCD). This result suggests that CNG-A3 is responsive to the same hormone signaling as the amiloride sensitive sodium channel ENaC and suggests the CNG-A3 may have a physiological role in sodium reabsorption.";
	        
		        if((trainingInfoMap.containsKey(abstractID)) && (!stkRemove.contains(abstractID))){
		        	ArrayList<String> sentencesList = new ArrayList<String>();
				       
		        	++C0;
		        	
		        	String info = trainingInfoMap.get(abstractID).toString();
		        	
		        	String litagion = info.split("##")[0];
		        	
		        	String harmClass = info.split("##")[1];
		        	
		        	String origlitagion=litagion;
		        	
		        	String[] sentences = abstractText.split("[\\.;]");
		        	
		        	int S = sentences.length;
		        	
		        	if (S==1) titleOnly=true;
		        	
		        	String origsent="";
		        	
		        	String str="";
		        	
		        	//String litagionVariant = getVariant(litagion, sentences, S);
		        	
		        	String litagionVariant=getStoredVariant(litagion);
		        	
		        	if (S>1){
		        	
		        	for (int i=S-2; i <S; ++i){
		        	
		        		String sent=sentences[i].trim();
		        		
		        		sent=sent.replaceAll("\\-", " ").trim();
		        		
		        		litagion=litagion.replaceAll("\\-", " ").trim();
		      
		        		origsent=sent;
		        		
			        	sent = replaceHarmClass(harmClass, sent, harmMap);
			        	
			        	sent = replaceSpecifiedLitagion(litagion, sent, litagionSuperStringMap, litagionSynonymMap);
			        	
			        	if (litagionVariant.length()>1){
	
			        		litagionVariant=litagionVariant.replaceAll("\\-", " ").trim();
			        	
			        		String bsent=sent;
			        	
			        		//System.out.println(sent);
			        		
			        		sent = sent.replaceAll("[^a-zA-Z0-9]"+litagionVariant+"[^a-zA-Z0-9]","SPEC_LITAGION");
			        		  		
			        		sent = sent.replaceAll("[^a-zA-Z0-9]"+litagionVariant.toLowerCase()+"[^a-zA-Z0-9]","SPEC_LITAGION");
	
			        		//if (!bsent.equals(sent)) System.out.println("VARN: "+litagionVariant);
			        	}
			        	
			        	sent = replaceDictionaryTerm(sent, dictionaryMap);
			        	
			        	sentencesList.add(sent);
			        	
			        	if (hmDir.get(abstractID+litagion+harmClass)!=null){
			        
			        		String directionality=hmDir.get(abstractID+litagion+harmClass).toString();
			        		//tellBigrams(sent,directionality);
			        	}
			        	
			        	
			        	if (isTellSentenceFourthPass(tType,sent,S)){
	        		
				        	str=str+origsent+"|";
			        	} 
			        	
			        	//System.out.println(sent); System.exit(1);
		        	}
		        	
		        	}
		        	//System.out.println(sentencesList.toString());
		        	
		            String features=new SentenceTypeClassifier().getNewFeatures(sentencesList);
		        		
		        	litagion=origlitagion;
		        	
		        	if (hmDir.get(abstractID+litagion+harmClass)!=null){
		        	
		        		++CA;
			          
		        		if (str.length()<2)  {
		        		
				        	
			        	} else {
			        		String directionality=hmDir.get(abstractID+litagion+harmClass).toString();
			        		
			        		//if (directionality.equals("2")) {
			        			
			        		if (directionality.equals("1")|| directionality.equals("2")) {
			        			
			        			vvRes.add(directionality+"\t"+abstractID+"\t"+litagion+"\t"+harmClass+"\t"+str+"\n");
		        					
		        				String dirn="";
		        		
		        				if (directionality.equals("1")) dirn="T"; 
		        				
		        				if (directionality.equals("2")) dirn="F"; 

		        				++C1;
		        				
		        				stkRemove.push(abstractID);
		        			}
				        	
			        	}
		        	}
		        }
		    }
			  
			 io.WriteResults.writeTells(tType, "Fourth", vvRes);
			   

			 System.out.println("Done !!\nTell Sentences generated and placed in <project folder>/<type>-tell-sentences-<datestamp>.txt");
		   
		   // System.out.println(CX+" abstracts excluded ");
		    System.out.println(C1+" abstracts provided explicit tell sentences");
		   // System.out.println(C2+" has no negation");
		   // System.out.println(P1+" , "+P2);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return vvRes;
	}

	
	/*
	 * @functionality: return abbreviation for litagion if any
	 * @param	litagion	is the litagion name
	 * @return	String	of the normalized litagion name
	 */	
	private static String getStoredVariant(String litagion){
		
		litagion = litagion.substring(litagion.indexOf("_[") + 2, litagion.length() - 1);

		if (hmLitAbbreviations.get(litagion)!=null) return hmLitAbbreviations.get(litagion).toString();
		
		return "";
	}
	
	/*
	 * @functionality: Determine variant i.e., abbreviation of litagion
	 * @param	litagion	is a litagion name
	 * @return	String	variant names for litagion if any
	 */
	private String getVariant(String litagion, String[] sentences, int S) {
		
		String firstSent=sentences[0];
		
		String litagionVariant="";
		
		try {
			litagionVariant=getLitagionVariant(firstSent,litagion);
			
			int i=0;
			
			while((litagionVariant.equals("")) && (i<S)){
	
				firstSent=sentences[i];
					
				litagionVariant=getLitagionVariant(firstSent,litagion);
		
				++i;
			}
		
		} catch (Exception e){e.printStackTrace();}
		
		return litagionVariant;
	}
	
	/*
	 * @param	harmClass	is a specific harm class
	 * @param	abstractText	is the abstract content
	 * @param	harmMap	is the hash map of all harms
	 * @return	String	with the specific harm class tagged if present
	 */
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
		
		try{
		
			abstractText = abstractText.replaceAll(harmClass, "SPEC_HARM");
		} catch (Exception e){}
		
		abstractText = slhr.tagIndicatorsBased(abstractText, harmClass,"ZZZZZZZ");
		
		return abstractText;
	}
	
	/*
	 * @param	litagion	is a specific litagion
	 * @param	abstractText	is the abstract content
	 * @param	litagionSuperStringMap	is the hash map of all litagion super strings
	 * @param	litagionSynomyMap	is the hash map of all litagion synonyms
	 * @return	String	with the specific litagion tagged if present
	 */
	public String replaceSpecifiedLitagion(String litagion, String abstractText, HashMap<String, ArrayList<String>> litagionSuperStringMap, HashMap<String, ArrayList<String>> litagionSynonymMap){
		
		litagion = litagion.toLowerCase();
		
		//System.out.println("L: "+litagion);
		
		if(litagionSynonymMap.containsKey(litagion)){
		
			ArrayList<String> list = litagionSynonymMap.get(litagion);
			
			for(String synonym : list){
			
				if(abstractText.contains(synonym)){
				
					abstractText = abstractText.replaceAll(synonym, "SPEC_LITAGION");
				}
			}
		}
		litagion = litagion.substring(litagion.indexOf("_[") + 2, litagion.length() - 1);
		
		abstractText = abstractText.replaceAll(litagion, "SPEC_LITAGION");
		
		//partialMatch(abstractText,litagion);
		
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
	
	
	/*
	 * @param	abstractText	is the abstract content
	 * @param	litagionSuperStringMap	is the hash map of all litagion super strings
	 * @param	litagionSynomyMap	is the hash map of all litagion synonyms
	 * @return	String	with any litagion tagged if present
	 */
	public String replaceAnyLitagion(String abstractText, HashMap<String, ArrayList<String>> litagionSuperStringMap, HashMap<String, ArrayList<String>> litagionSynonymMap){
		
		Collection c = litagionSynonymMap.keySet();
		
		Iterator it = c.iterator();
		
		while (it.hasNext()) {
		
			String litagion=it.next().toString();

			ArrayList<String> list = litagionSynonymMap.get(litagion);
				
			list.add(litagion);
			
			for(String synonym : list){
				
				if (synonym.length()>5){
				if(abstractText.contains(synonym)){
					//System.out.println(synonym);
					try {
						abstractText = abstractText.replaceAll(synonym, "ANYLITAGION");
					} catch (Exception e){}
				}
				}
			}
			
			litagion = litagion.substring(litagion.indexOf("_[") + 2, litagion.length() - 1);
			
			litagion = litagion.replaceAll("-", " ");
			
			try {
				//abstractText = abstractText.replaceAll(litagion, "ANYLITAGION");
			} catch (Exception e){}
		}
		
		//partialMatch(abstractText,litagion);
		
		c = litagionSuperStringMap.keySet();
		
		it = c.iterator();
		
		while (it.hasNext()) {
		
			String litagion=it.next().toString();
			
			ArrayList<String> list = litagionSuperStringMap.get(litagion);
				
			list.add(litagion);
			
			for(String synonym : list){
				
				if (synonym.length()>5){
				if(abstractText.contains(synonym)){
					
					//System.out.println(synonym);
					
					try {
						abstractText = abstractText.replaceAll(synonym, "ANYLITAGION");
					} catch (Exception e){}
				}
				}
			}
			
			litagion = litagion.substring(litagion.indexOf("_[") + 2, litagion.length() - 1);
			
			litagion = litagion.replaceAll("-", " ");
			
			try {
				//abstractText = abstractText.replaceAll(litagion, "ANYLITAGION");
			} catch (Exception e){}
		
		}
		
		return abstractText;
	}
	
	
	/*
	 * @param	abstractText	is the abstract content
	 * @param	litagionSuperStringMap	is the hash map of all litagion super strings
	 * @param	litagionSynomyMap	is the hash map of all litagion synonyms
	 * @return	String	with any litagion tagged if present
	 */
	public boolean hasOneConfoundingLitagion(String speclitagion,String abstractText, HashMap<String, ArrayList<String>> litagionSuperStringMap, HashMap<String, ArrayList<String>> litagionSynonymMap){
		
		abstractText=replaceSpecifiedLitagion(speclitagion, abstractText, litagionSuperStringMap, litagionSynonymMap);
    		
		Collection c = litagionSynonymMap.keySet();
		
		Iterator it = c.iterator();
		
		int K=0;
		
		while (it.hasNext()) {
		
			String litagion=it.next().toString();

			//ArrayList<String> list = litagionSynonymMap.get(litagion);
			
			ArrayList<String> list = new ArrayList();
			
			litagion = litagion.substring(litagion.indexOf("_[") + 2, litagion.length() - 1);
			
			litagion = litagion.replaceAll("-", " ");
				
			list.add(litagion);
			
			for(String synonym : list){
				
				if (synonym.length()>5){
				if(abstractText.contains(synonym)){
					//System.out.println(synonym);
					++K;
					
				}
				}
				
				if (K>1) break;
			}

		}
		
		//partialMatch(abstractText,litagion);
		
		c = litagionSuperStringMap.keySet();
		
		it = c.iterator();
		
		while (it.hasNext()) {
		
			String litagion=it.next().toString();
			
			//ArrayList<String> list = litagionSuperStringMap.get(litagion);
			
			ArrayList<String> list = new ArrayList();
				
			litagion = litagion.substring(litagion.indexOf("_[") + 2, litagion.length() - 1);
			
			litagion = litagion.replaceAll("-", " ");
		
			list.add(litagion);
			
			for(String synonym : list){
				
				if (synonym.length()>5){
				
					if(abstractText.contains(synonym)){
					
					//System.out.println(synonym);
					++K;

				}
				}
				if (K>1) break;
			}

			if (K>1) break;
		
		}
		
		if (K==1) {
			
			//System.out.println("true");
			
			return true;
		}
		
		return false;
	}
	
	/*
	 * @functionality: experimental function for partial (litagion) match
	 */
	private static void partialMatch(String abstractText,String lit){
		
		String[] litparts=lit.split("[ ]");
		
		int L=litparts.length;
		
		if (L>1){
			
			String[] sentences = abstractText.split("\\. ");
			
			int P=sentences.length;
			
			String lastWord=litparts[L-1];
			
			if (lastWord.length()>1) {
			
				for (int i=0; i <P; ++i){
					
					String sent=sentences[i];
					
					if (sent.indexOf(lastWord)>-1) {
						
						//if (sent.indexOf(lit)<0) System.out.println(lit+" ||| "+sent);
					}
					
					if (L>2){
						
						lastWord=litparts[L-2]+" "+litparts[L-1];
						
						if (sent.indexOf(lastWord)>-1) {
							
							if (sent.indexOf(lit)<0) System.out.println(lit+" <<< "+sent);
						}
						
					}
				}
			}
		}
	}
	
	/*
	 * @param	litagion	is a specific litagion
	 * @param	abstractText	is the abstract content
	 * @param	dictionaryMap	is the dictionar hash map
	 * @return	String	with the specific dictionary term tagged if present
	 */
	public String replaceDictionaryTerm(String abstractText, HashMap<String, ArrayList<String>> dictionaryMap){		
		
		Iterator it = dictionaryMap.entrySet().iterator();
	   
		while (it.hasNext()) {
	    
			Map.Entry pairs = (Map.Entry)it.next();
	        
			String key = pairs.getKey().toString();
	        
			ArrayList<String> values = (ArrayList<String>) pairs.getValue();
	        
			for(String value : values){
	        	
	        	//System.out.println(key+" : "+value);
	        	
	        	if(key.indexOf("IS_ARTICLE_PHRASE")>-1){
	        		
		        	if(abstractText.contains(value)){
						
		        		abstractText = abstractText.replaceAll(value, key);
					}
		        	
		        	//System.out.println("SUBSTRING");
					
	        	} else {
	        		
	        		//System.out.println("TOKEN");
	  	
		        	String[] words=abstractText.split("[ ]");
		        	
		        	int W=words.length;
		        	
		        	String[] vparts=value.split("[ ]");
		        	
		        	int V=vparts.length;
		        	
		        	//System.out.println(V);
		        	
		        	if (V==2){
		        		
		        		for (int i=0; i <W-1; ++i){
		        			
		        			String myword=words[i]+" "+words[i+1];
		        			
		        			if (myword.equals(value)) {
		        				
		        				words[i]=key;
		        				
		        				words[i+1]="";
		        				
		        				//System.out.println(myword);
		        			}
		        		}
		        		
		        	} else {
		        		
		        		for (int i=0; i <W; ++i){
		        			
		        			String myword=words[i];
		        			
		        			myword=myword.replaceAll("[^a-zA-Z0-9]", "");
		        			
		        			if (myword.equals(value)) words[i]=key;
		        			
		        			//System.out.print(words[i]+"|");
		        			
		        			//if (key.indexOf("IS_LITAGION")>-1)System.out.println(words[i]+"|||"+value);
		        		}
		        		
		        		//System.out.println("");
		        	}
		        	
		        	abstractText="";
		        	
		        	for (int i=0; i <W; ++i){
		        		
		        		abstractText=abstractText+words[i]+" ";
		        	}
		        	
		        	abstractText=abstractText.trim();
		        	
		        	//System.out.println(abstractText);
		        }
		    }
	        
	    }    
		return abstractText;
	}
	
	
	

	
	
	/*
	 * @functionality Logic for determining tell sentence or not
	 * @param	type	is the type of tell sentence (criteria)
	 * @param	sent	is a sentence (text)
	 * @param	i	is the sentence index (within abstract)
	 * @param	S	is the number of sentences in the abstract
	 * @return	boolean	is this a tell sentence (true/false)
	 */
	private static boolean isTellSentence(int type, String sent, int i, int S){
		
		boolean cr1=false, cr2=false,cr3=false;
		
		switch (type){
		
		case 1: if (sent.indexOf("IS_ARTICLE")>-1) cr1=true;
			
				if (sent.indexOf("IS_ACTION")>-1) cr2=true;
				
				if (sent.indexOf("IS_IND")>-1) cr2=true;
				
				if (sent.indexOf("_LIT")>-1) cr3=true;
				
				if (cr1 && cr2 && cr3)System.out.println("CASE 1  !! "+ sent);
		
				return (cr1 && cr2 && cr3);
				
		case 2: if (sent.indexOf("IS_ACTION")>-1) cr2=true;
		
				if (sent.indexOf("IS_IND")>-1) cr2=true;
		
				if (sent.indexOf("_LIT")>-1) cr3=true;
	
				return (cr2 && cr3);	
				
		case 3: if (sent.indexOf("IS_ACTION")>-1) cr2=true;
		
				if (sent.indexOf("IS_IND")>-1) cr2=true;

				if (((float)(i)/(float)(S))>.74) cr3=true;
				
				if (S==3) if (i==2) cr3=true;
				
				if (S<12) if (i>8) {
									
					cr3=true;
				}
				
				if (cr2 && cr3)System.out.println("CASE 3  !! "+i+"  "+S+"  "+ sent);

				return (cr2 && cr3);
				
		case 4: if (sent.indexOf("SPEC_LIT")>-1) cr1=true;
		
				if (sent.indexOf("SPEC_HARM")>-1) cr2=true;
					
				if (((float)(i)/(float)(S))>0.70) cr3=true;
				
				if (S==3) if (i==2) cr3=true;
				
				return (cr1 && cr2 && cr3);
				
		case 5: 
				if (sent.indexOf("IS_ACTION")>-1) cr2=true;
		
				if (sent.indexOf("IS_IND")>-1) cr2=true;
		
				if (sent.indexOf("_LIT")>-1) cr3=true;
				
				if (sent.indexOf("IS_ARTICLE_PHRASE")>-1) cr3=true;
		
				//if (cr2 && cr3)System.out.println("CASE *  !! "+ sent);

				return (cr2 && cr3);		
		
		case 7: 
				if (sent.indexOf("_NEG")>-1) cr1=true;
				
				return (cr1);		
		}
		return false;
	}
	
	
	/*
	 * @functionality Logic for determining tell sentence or not
	 * @param	type	is the type of tell sentence (criteria)
	 * @param	sent	is a sentence (text)
	 * @param	i	is the sentence index (within abstract)
	 * @param	S	is the number of sentences in the abstract
	 * @return	boolean	is this a tell sentence (true/false)
	 */
	private static boolean isTellSentenceSecondPass(int type, String sent, int i, int S){
		
		boolean cr1=false, cr2=false,cr3=false;
		
		switch (type){
		
		case 1: if (sent.indexOf("IS_ARTICLE")>-1) cr1=true;
			
				if (sent.indexOf("IS_ACTION")>-1) cr2=true;
				
				if (sent.indexOf("IS_IND")>-1) cr2=true;
				
				if (sent.indexOf("_LIT")>-1) cr3=true;
				
				if (cr1 && cr2 && cr3)System.out.println("CASE 1  !! "+ sent);
		
				return (cr1 && cr2 && cr3);
				
		case 2: if (sent.indexOf("IS_ACTION")>-1) cr2=true;
		
				if (sent.indexOf("IS_IND")>-1) cr2=true;
		
				//if (sent.indexOf("_LIT")>-1) cr3=true;
	
				return (cr2);	
				
		
		}
		return false;
	}
	
	
	/*
	 * @functionality Logic for determining tell sentence or not
	 * @param	type	is the type of tell sentence (criteria)
	 * @param	sent	is a sentence (text)
	 * @param	i	is the sentence index (within abstract)
	 * @param	S	is the number of sentences in the abstract
	 * @return	boolean	is this a tell sentence (true/false)
	 */
	private static boolean isTellSentenceThirdPass(int type, String sent, int S){
		
		boolean cr1=false, cr2=false,cr3=false;
		
		switch (type){
		
		case 1: if (sent.indexOf("IS_ARTICLE")>-1) cr1=true;
			
				if (sent.indexOf("IS_ACTION")>-1) cr2=true;
				
				if (sent.indexOf("IS_IND")>-1) cr2=true;
				
				if (sent.indexOf("_LIT")>-1) cr3=true;
				
				if (cr1 && cr2 && cr3)System.out.println("CASE 1  !! "+ sent);
		
				return (cr1 && cr2 && cr3);
				
		case 2: //if (sent.indexOf("IS_ACTION")>-1) cr2=true;
		
				//if (sent.indexOf("IS_IND")>-1) cr2=true;
		
				if (sent.contains("risk")) cr2 = true;
				
				if (sent.contains("toxic")) cr2 = true;
				
				if (sent.contains("damag")) cr2 = true;
				
				if (sent.contains("expose")) cr2 = true;
				
				if (sent.contains("injur")) cr2 = true;
			
				if (sent.contains("mortality")) cr2 = true;
				
				if (sent.indexOf("SPEC_LIT")>-1) cr3=true;
	
				return (cr2 && cr3);	
				
		
		}
		return false;
	}
	
	
	/*
	 * @functionality Logic for determining tell sentence or not
	 * @param	type	is the type of tell sentence (criteria)
	 * @param	sent	is a sentence (text)
	 * @param	i	is the sentence index (within abstract)
	 * @param	S	is the number of sentences in the abstract
	 * @return	boolean	is this a tell sentence (true/false)
	 */
	private static boolean isTellSentenceFourthPass(int type, String sent, int S){
		
		boolean cr1=false, cr2=false,cr3=false;
		
		switch (type){
		
		case 1: if (sent.indexOf("IS_ARTICLE")>-1) cr1=true;
			
				if (sent.indexOf("IS_ACTION")>-1) cr2=true;
				
				if (sent.indexOf("IS_IND")>-1) cr2=true;
				
				if (sent.indexOf("_LIT")>-1) cr3=true;
				
				if (cr1 && cr2 && cr3)System.out.println("CASE 1  !! "+ sent);
		
				return (cr1 && cr2 && cr3);
				
		case 2: 
		
				if (sent.indexOf("IS_IND")>-1) cr2=true;
	
				return (cr2);	
				
		
		}
		return false;
	}
	
	private static String getTitle(String abstractText){
		
		String[] parts=abstractText.split("\\.");
		
		if (parts.length>1) return parts[0];
		
		return abstractText;
	}
	
	/*
	 * @param	firstSent	is the first sentence on the abstract
	 * @param	litharm	is a string with the litagion (and harm) specified for PMID
	 * @return	String	Variant (such as abbreviated) form of litagion
	 */
	private static String getLitagionVariant(String firstSent, String lit){
		
		firstSent=firstSent.replaceAll("\\-", " ");
		
		lit = lit.replaceAll("\\-", " ");
		
		//System.out.println(lit+":"+firstSent);
		
		int ind=lit.indexOf("_[");
		
		lit=lit.substring(ind+2).replaceAll("]", "");
		
		String patStr = "\\("+"[a-zA-Z0-9]+"+"\\)";
	
		Pattern pat = Pattern.compile(patStr);
		
		Matcher match = pat.matcher(firstSent);
		
		while (match.find()) {
			
			String prefix=firstSent.substring(match.start());
			
			String str=firstSent.substring(match.start(),match.end());
			
			int ind2=str.indexOf("(");
			
			str=str.substring(ind2+1).replaceAll("\\)","");
			
			if (firstSent.toLowerCase().indexOf(lit.toLowerCase()+" (")>-1) {
				
				hmLitAbbreviations.put(lit, str);
				
				return str;
			}
		}
		
		return "";
	}
	
	
	private static void printAbbreviations(){
		
		Collection c = hmLitAbbreviations.keySet();
		
		Iterator it = c.iterator();
		
		Vector vv=new Vector();
		
		while (it.hasNext()){
			
			String lit = it.next().toString();
			
			String abbrev = hmLitAbbreviations.get(lit).toString();
			
			//System.out.println(lit+"|"+abbrev);
			
			vv.add(lit.toLowerCase()+":"+abbrev);
		}
		
		utilities.IO.writeFile_Basic("resources/LitAbbreviations.txt", vv);
	}
	
	private static void readAbbreviations(){
		
		Stack stk=utilities.IO.readFileStk(litAbbrevLoc);
		
		while (!stk.isEmpty()){
			
			String str = stk.pop().toString();
			
			String[] parts=str.split(":");
			
			String lit=parts[0]; 
			
			String abbrev=parts[1];
			
			//System.out.println(lit+","+abbrev);
			
			hmLitAbbreviations.put(lit, abbrev);
		}
	}
	
	
	public static void main(String[] args) {

		/*
		allabstractsLoc="resources/all_abstracts2.txt";
		//allabstractsLoc="resources/all_abstracts3.txt";
		traDataLoc="resources/trainingData_naveenProject1.csv";
		//traDataLoc="resources/holdback.csv";
		harmSynLoc="resources/harmSynonyms.csv";
		litSupLoc="resources/litagionSuperstrings.csv";
		litSynLoc="resources/litagionSynonyms.csv";
		//dictLoc="resources/aarDictionary_tag.csv";
		dictLoc="resources/aarDictionary_tell2.csv";
		litAbbrevLoc="resources/LitAbbreviations.txt";

		tType=2;

*/
		io.ReadResources RR = new io.ReadResources();
		
		RR.readAllResources();
	
		System.out.println("Generating tell sentences .....");
		
		new TellSentenceGenerator().execute(RR);
		
		/*
		Collection c = hmNoTells.keySet();
		
		Iterator it = c.iterator();
		
		while ((it.hasNext()) ){
			
			String id = it.next().toString();
			
			if (!stkRemove.contains(id)) System.out.println(id);
		}
		*/
		//printAbbreviations();
	}
	
	
	
	
	private static boolean isAnywhere(String sentence){
		
		sentence = sentence.toLowerCase();
		
		String[] parts = sentence.split("[ ]");
		
		int S = parts.length;
		
		int A = stkAnywhere.size();
		
		for (int j=0; j <A; ++j) {
			
			String pattern = stkAnywhere.elementAt(j).toString();
			
			if (sentence.indexOf(pattern)>-1) return true;
			
			String[] patparts = pattern.split(" ");
			
			int P = patparts.length;
			
			if (P>1) {
				
				String pat1 = patparts[0].trim();
				
				String pat2 = patparts[1].trim();
			
				for (int i=0; i <S-1; ++i) {
					
					String tok1 = parts[i].trim().toLowerCase();
					
					String tok2 = parts[i+1].trim().toLowerCase();
					
					if (tok1.indexOf(pat1)>-1) if (tok2.indexOf(pat2)>-1) return true;
				}
			}
		}
		
		return false;
	}
	
	

}
