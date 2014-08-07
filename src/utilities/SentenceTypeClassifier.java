package utilities;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;


public class SentenceTypeClassifier {
	int WORD_DISTANCE = 4;
	
	//public ArrayList<List<Integer>> classify(ArrayList<String> sentenceList){
	public String classify(ArrayList<String> sentenceList){
		ArrayList<List<Integer>> resultArray = new ArrayList<List<Integer>>();
		Integer[] subArray_A = new Integer[32];
		Integer[] subArray_B = new Integer[32];
		Integer[] subArray_C = new Integer[32];
		Arrays.fill(subArray_A,0);
		Arrays.fill(subArray_B,0);
		Arrays.fill(subArray_C,0);
		for(String sentence : sentenceList){
			String pattern = checkPattern(sentence);
			int encoding = getBinaryEncoding(sentence);
			
			if(pattern.equals("A")){
				subArray_A[encoding] = 1;
			}else if(pattern.equals("B")){
				subArray_B[encoding] = 1;
			}else if(pattern.equals("C")){
				subArray_C[encoding] = 1;
			}
		}
		//System.out.println(Arrays.asList(subArray_A).toString());
		//System.out.println(Arrays.asList(subArray_B).toString());
		//System.out.println(Arrays.asList(subArray_C).toString());
		
		resultArray.add(0, Arrays.asList(subArray_A));
		resultArray.add(1, Arrays.asList(subArray_B));
		resultArray.add(2, Arrays.asList(subArray_C));
		
		String res=resultArray.toString();
		
		res=res.replaceAll("\\[", "");
		res=res.replaceAll("\\]", "");
		res=res.replaceAll(" ", "");
		//return resultArray;
		
		//System.out.println(res);
		
		return res;
	}
	
	public String checkPattern(String sentence){
		int numberOfWords = 0;
		boolean matched=false;
	    if(sentence.contains("IS_NEG_MOD")){
	    	StringTokenizer st = new StringTokenizer(sentence);
	    	boolean start = false;
	    	while(st.hasMoreTokens()){
		        String token = st.nextToken();
		        if(token.equals("IS_NEG_MOD")){
		            start = true;
		            continue;
		        }
		        if(start) {
		            if(token.equals("IS_ACTION") || token.equals("IS_FINDING") || token.equals("IS_INDICATOR")){
		                start = false;
		                matched=true;
		            }
		            else {
		                numberOfWords++;
		            }
		        }
		    }
	    }
	    
	    if (matched){
			if(numberOfWords == 0){
				return "A";
			}else if (numberOfWords >= 1 && numberOfWords <= WORD_DISTANCE){
				return "B";
			}
	    }
		
		if(sentence.contains("IS_INDICATOR_NEG") || sentence.contains("IS_ACTION_NEG")){
			return "C";
		}
		return "";
	}
	
	public int getBinaryEncoding(String sentence){
		StringBuffer sb = new StringBuffer();
		if(sentence.contains("IS_ARTICLE") ||sentence.contains("IS_ARTICLE_PHRASE")){
			sb.append(1);
		}else{
			sb.append(0);
		}
		
		if(sentence.contains("IS_FINDING") ||sentence.contains("IS_INDICATOR")){
			sb.append(1);
		}else{
			sb.append(0);
		}
		
		if(sentence.contains("IS_ACTION")){
			sb.append(1);
		}else{
			sb.append(0);
		}
		
		if(sentence.contains("SPEC_LITAGION")){
			sb.append(1);
		}else{
			sb.append(0);
		}
		
		if(sentence.contains("SPEC_HARM")){
			sb.append(1);
		}else{
			sb.append(0);
		}
		return Integer.parseInt(sb.toString(), 2);
	}
	
	
	public String getNewFeatures(ArrayList<String> sentencesList){
		
		return this.classify(sentencesList);
	}
	
	public static void main(String[] args) {
		ArrayList<String> sentencesList = new ArrayList<String>();
		sentencesList.add("this did IS_NEG_MOD IS_ACTION an increase in SPEC_LITAGION.");
		sentencesList.add("there is IS_NEG_MOD significant IS_INDICATOR that SPEC_LITAGION causes SPEC_HARM.");
		new SentenceTypeClassifier().classify(sentencesList);
	}

}
