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
	
	private Stack stkTriggerWords = new Stack();
	private NLParser nlParser = new NLParser();
	private IO io = new IO();
	
	public void initialize() {
		
		stkTriggerWords = io.readFileStk("lexicons/trigger.txt");
		
		nlParser.initialize();
	}

	/*
	 * @param: sentence is a regular sentence
	 * @functionality: generate a parse tree representation of the sentence
	 */
	public void tagText(String sentence){
		
		nlParser.tagText(sentence);
	}
	
	
	/*
	 * @param: sent is a sentence
	 * @return: true/false depending on whether the sentence has negation or not
	 */
	public boolean isNegatedSentence(String sent){
		
		return nlParser.isNegatedSentence(sent);
	}
	
	/*
	 * @param: sent is a sentence
	 * @return: true/false depending on whether the sentence has a negated indicator clause or not
	 */
	public boolean isNegatedIndicator(String sent){
		
		//NLParser.tagText(sent);
		
		Stack stkAction = nlParser.getAction();
		
		if (stkAction.contains(" --> NEGIND")) return true;

		return false;
	}
	
	/*
	 * @param: sent is a sentence
	 * @return: true/false depending on whether the sentence has a negated causation clause or not
	 */
	public boolean isNegatedCause(String sent){
		
		//NLParser.tagText(sent);
		
		Stack stkAction = nlParser.getAction();
		
		if (stkAction.contains(" --> NEGCAUSE")) return true;

		return false;
	}
	
	/*
	 * @param: sent is a sentence
	 * @return: true/false depending on whether the sentence has a "trigger" clause or not
	 */
	public boolean isTriggerSentence(String sent) {
		
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
	public boolean containsSpecLitagion(String sent) {
		
		return (sent.indexOf("SPECLITAGION")>-1);
	}
	
	/*
	 * @param: sent is a sentence
	 * @return: true/false depending on whether the sentence has a mention of the specified harm or not
	 */
	public boolean containsSpecHarm(String sent) {
		
		return (sent.indexOf("SPECHARM")>-1);
	}
	
	/*
	 * @param: text is any unit of text
	 * @return: length of provided text in number of words
	 */
	public int getLengthWords(String text){
		
		String[] parts = text.split(" ");
		
		return parts.length;
	}
}