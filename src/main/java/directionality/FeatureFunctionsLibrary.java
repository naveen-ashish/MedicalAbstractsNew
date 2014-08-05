/*
 * @module: Library of all feature functions
 * @author: Naveen Ashish
 * @contact: naveen.ashish@gmail.com
 * @last updated: 03/26/2014
 */

package directionality;

import java.util.Stack;





import utilities.IO;
import utlities.nlp.NLParser;

public class FeatureFunctionsLibrary {
	
	private static Stack stkTriggerWords = new Stack();
	
	public static void initialize() {
		
		stkTriggerWords = IO.readFileStk("lexicons/trigger.txt");
		
		NLParser.initialize();
	}

	/*
	 * @param: sentence is a regular sentence
	 * @functionality: generate a parse tree representation of the sentence
	 */
	public static void tagText(String sentence){
		
		NLParser.tagText(sentence);
	}
	
	
	/*
	 * @param: sent is a sentence
	 * @return: true/false depending on whether the sentence has negation or not
	 */
	public static boolean isNegatedSentence(String sent){
		
		return NLParser.isNegatedSentence(sent);
	}
	
	/*
	 * @param: sent is a sentence
	 * @return: true/false depending on whether the sentence has a negated indicator clause or not
	 */
	public static boolean isNegatedIndicator(String sent){
		
		//NLParser.tagText(sent);
		
		Stack stkAction = NLParser.getAction();
		
		if (stkAction.contains(" --> NEGIND")) return true;

		return false;
	}
	
	/*
	 * @param: sent is a sentence
	 * @return: true/false depending on whether the sentence has a negated causation clause or not
	 */
	public static boolean isNegatedCause(String sent){
		
		//NLParser.tagText(sent);
		
		Stack stkAction = NLParser.getAction();
		
		if (stkAction.contains(" --> NEGCAUSE")) return true;

		return false;
	}
	
	/*
	 * @param: sent is a sentence
	 * @return: true/false depending on whether the sentence has a "trigger" clause or not
	 */
	public static boolean isTriggerSentence(String sent) {
		
		if (!isNegatedSentence(sent)){
			
			int S = stkTriggerWords.size();
			
			for (int i=0; i <S; ++i) {
				
				String trigger = stkTriggerWords.elementAt(i).toString();
				
				if (sent.toLowerCase().indexOf(trigger)>-1) return true;
			}
		}
		
		return false;
	}
	
	/*
	 * @param: sent is a sentence
	 * @return: true/false depending on whether the sentence has a mention of the specified litagion or not
	 */
	public static boolean containsSpecLitagion(String sent) {
		
		return (sent.indexOf("SPECLITAGION")>-1);
	}
	
	/*
	 * @param: sent is a sentence
	 * @return: true/false depending on whether the sentence has a mention of the specified harm or not
	 */
	public static boolean containsSpecHarm(String sent) {
		
		return (sent.indexOf("SPECHARM")>-1);
	}
	
	/*
	 * @param: text is any unit of text
	 * @return: length of provided text in number of words
	 */
	public static int getLengthWords(String text){
		
		String[] parts = text.split(" ");
		
		return parts.length;
	}
}