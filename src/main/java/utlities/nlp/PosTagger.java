/*
 * @module: Provides the POS tagging functionality
 * @author: Naveen Ashish
 * @contact: naveen.ashish@gmail.com
 * @last updated: 07/08/2014
 */


package utlities.nlp;

import edu.stanford.nlp.tagger.maxent.MaxentTagger;


public class PosTagger  {

	private static MaxentTagger tagger;


	public static void init() throws Exception {

		String loc =  "./resources/left3words-wsj-0-18.tagger";

		tagger = new MaxentTagger(loc);
	}


	/*
	 * Performs POS tagging of a string
	 */
	public static String tagIt(String sent) {

		String tagged="";

		if (sent !=null ) {
			tagged = tagger.tagString(sent);
		}

		tagged = tagged.replaceAll("_", "/");
		
		return tagged;
	}

	public static void main(String[] args) throws Exception {

		init();

		String sentence = "How light can it be?";
		
		sentence ="IBM will sue Amazon in New York.";

		String sentTagged = PosTagger.tagIt(sentence);
		
		System.out.println(sentTagged);

	}
}

