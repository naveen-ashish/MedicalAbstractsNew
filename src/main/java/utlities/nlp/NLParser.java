/*
 * @module: Provides the NLP functionality, based on parsing the text
 * @author: Naveen Ashish
 * @contact: naveen.ashish@gmail.com
 * @last updated: 07/08/2014
 */

package utlities.nlp;

import java.util.ArrayList;
import java.util.Stack;

import utilities.IO;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.trees.Tree;

public class NLParser

{
	// private LexicalizedParser lp = new
	// LexicalizedParser("models/englishPCFG.ser.gz");
	private LexicalizedParser lp;

	private Stack stkSuper = new Stack();

	private Stack stkMaster = new Stack();

	public Stack getAction() {

		return stkSuper;
	}

	private Stack stkIndicatorWords;

	private Stack stkCauseWords;
	
	private IO io = new IO();

	public void initialize() {

		stkIndicatorWords = io.readFileStk("lexicons/IndicatorWords.txt");

		stkCauseWords = io.readFileStk("lexicons/CauseWords.txt");
	}

	/*
	 * @param: inputSentence is any sentence
	 * 
	 * @return: the parser tree representation of the sentence
	 */

	public String getParseTree(String sent) {

		lp.setOptionFlags(new String[] { "-maxLength", "80", "-retainTmpSubcategories" });

		// Tree parse = (Tree) lp.apply(sent);

		// return parse.toString();
		return "";
	}

	/*
	 * @param inputSentence is the sentence (text)
	 * 
	 * @return String with POS tags for sentence
	 */
	public String tagText(String inputSentence) {
		/*
		 * lp.setOptionFlags(new String[] { "-maxLength", "80",
		 * "-retainTmpSubcategories" });
		 * 
		 * // Tree parse = (Tree) lp.apply(inputSentence);
		 * 
		 * stkSuper.clear();
		 * 
		 * stkMaster.clear();
		 * 
		 * // ArrayList<String> nounsList = chunkSentence(parse);
		 * 
		 * // printPhrases();
		 * 
		 * String nounsChunckedSentence = chunkNouns(inputSentence, nounsList);
		 * 
		 * return chunkRemaining(inputSentence, nounsList,
		 * nounsChunckedSentence);
		 */
		return "";
	}

	private void printPhrases() {

		int S = stkSuper.size();

		for (int i = 0; i < S; ++i)
			System.out.println("[" + stkSuper.elementAt(i).toString() + "]");
	}

	/*
	 * @functionality: chunk noun clauses together
	 */
	private String chunkNouns(String inputSentence, ArrayList<String> nounsList) {

		String nounsChunckedSentence = inputSentence;

		try {
			for (String s : nounsList) {

				nounsChunckedSentence = nounsChunckedSentence.replaceAll(s, "[" + s + "]");
			}
		} catch (Exception e) {
		}

		return nounsChunckedSentence;
	}

	private String chunkRemaining(String inputSentence, ArrayList<String> nounsList, String nounsChunckedSentence) {

		String allChunckedSentence = nounsChunckedSentence;

		try {
			for (String s : nounsList) {

				inputSentence = inputSentence.replaceAll(s, "##");
			}
		} catch (Exception e) {
		}

		String[] splitter = inputSentence.split("##");

		try {
			for (String s : splitter) {

				if (!s.trim().equals("")) {

					allChunckedSentence = allChunckedSentence.replaceAll(s.trim(), "[" + s.trim() + "]");
				}
			}
		} catch (Exception e) {
		}

		return allChunckedSentence;
	}

	/*
	 * @functionality: create groups of chunks/clauses of sentence by type
	 * 
	 * @param t is the sentence parse tree
	 * 
	 * @return ArralyList an array of chunks
	 */
	private ArrayList<String> chunkSentence(Tree t) {

		int M = stkMaster.size();

		ArrayList<String> nounsList = new ArrayList<String>();

		for (int i = 0; i < M; ++i) {

			String phrase = stkMaster.elementAt(i).toString();

			if (phrase.indexOf(t.toString()) > -1) {

				if (t.label().value().equals("NP")) {

					nounsList.add(t.yield().toString().trim());
				} else {

					for (Tree child : t.children())

						nounsList.addAll(chunkSentence(child));
				}

				return nounsList;
			}
		}

		// System.out.println(t.toString());
		// System.out.println(t.label().value());
		if (t.label().value().equals("VP")) {

			if (isNegated(t.toString())) {
				// System.out.println("NEG: "+t.toString());

				String sign = " --> NEG";

				if (isIndicator(t.toString().toLowerCase())) {

					sign = sign + "IND";

					// System.out.println("IND: "+t.toString());

					stkMaster.push(t.toString());
				} else {

					sign = sign + "CAUSE";

					// System.out.println("CAUSE: "+t.toString());

					stkMaster.push(t.toString());

				}

				stkSuper.push(sign);
			}

			processAction(t.toString());
		}

		if (t.label().value().equals("NP")) {

			if (isNegatedEntity(t.toString())) {

				String sign = " --> NEG";

				if (isIndicator(t.toString().toLowerCase())) {

					sign = sign + "IND";

					// System.out.println("IND: "+t.toString());

					stkMaster.push(t.toString());
				} else {

					sign = sign + "CAUSE";

					// System.out.println("CAUSE: "+t.toString());

					stkMaster.push(t.toString());

				}

				stkSuper.push(sign);
			}

			nounsList.add(t.yield().toString().trim());
		} else {

			for (Tree child : t.children())

				nounsList.addAll(chunkSentence(child));
		}

		return nounsList;
	}

	private void processAction(String phrase) {

		phrase = phrase.replaceAll("[^A-Za-z() ]", "");

		int ind = phrase.indexOf("(NP");

		if (ind > -1)
			phrase = phrase.substring(0, ind);

		phrase = phrase.replaceAll("[(A-Z]+", "");

		phrase = phrase.replaceAll("[)]", "");

		phrase = phrase.replaceAll("[ ]+", " ");

		phrase = phrase.trim();

		int S = stkSuper.size();

		boolean contained = false;

		for (int i = 0; i < S; ++i) {

			String elem = stkSuper.elementAt(i).toString();

			if (elem.indexOf(phrase) > -1)
				contained = true;
		}

		if (!contained)
			stkSuper.push(phrase);

		// System.out.println("["+phrase+"]");

	}

	/*
	 * @param phrase is a string representation of a parse tree chunk
	 * 
	 * @return boolean true/false if the phrase is an indicator phrase/not
	 */
	private boolean isIndicator(String phrase) {

		int S = stkIndicatorWords.size();

		if (phrase.indexOf("(vp") < 0)
			return true;

		for (int i = 0; i < S; ++i) {

			String indword = stkIndicatorWords.elementAt(i).toString();

			if (phrase.indexOf(indword) > -1)
				return true;
		}

		return false;
	}

	/*
	 * @param phrase is a string representation of a parse tree chunk
	 * 
	 * @return boolean true/false if the phrase is a causal phrase/not
	 */
	private boolean isCause(String phrase) {

		int S = stkCauseWords.size();

		for (int i = 0; i < S; ++i) {

			String indword = stkCauseWords.elementAt(i).toString();

			if (phrase.indexOf(indword) > -1)
				return true;
		}

		return false;
	}

	/*
	 * @param phrase is a string representation of a parse tree chunk
	 * 
	 * @return boolean true/false if the phrase is negated/not
	 */
	private boolean isNegated(String phrase) {

		phrase = phrase.replaceAll("[^A-Za-z ]", "");

		phrase = phrase.replaceAll("[ ]+", " ");

		phrase = phrase.trim().toLowerCase();

		String[] parts = phrase.split("[ ]");

		int P = parts.length;

		for (int i = 0; i < P; ++i) {

			String tok = parts[i].trim();

			if (tok.equals("no"))
				return true;

			if (tok.equals("not"))
				return true;
		}

		return false;
	}

	/*
	 * @param: phrase is a string representation of a parse tree chunk
	 * 
	 * @return: true/false if the phrase has a negated entity (noun) / not
	 */
	private boolean isNegatedEntity(String phrase) {

		Stack patterns = new Stack();

		patterns.push("no[a-z 0-9,]+effect");
		patterns.push("no[a-z 0-9,]+difference");
		patterns.push("no[a-z 0-9,]+evidence");
		patterns.push("no[a-z 0-9,]+toxic");
		patterns.push("no[a-z 0-9,]+change");
		patterns.push("none of the");
		patterns.push("failed to");
		patterns.push("lack of");
		patterns.push("show no significant");
		patterns.push("unimportant");
		patterns.push("unlikely");
		patterns.push("uncommon");
		patterns.push("unchanged");
		patterns.push("minor degree");
		patterns.push("neither");
		patterns.push("circumstantial");

		phrase = phrase.replaceAll("[^A-Za-z ]", "");

		phrase = phrase.replaceAll("[ ]+", " ");

		phrase = phrase.trim().toLowerCase();

		String[] parts = phrase.split("[ ]");

		int S = patterns.size();

		for (int i = 0; i < S; ++i) {

			String pat = patterns.elementAt(i).toString();

			if (phrase.indexOf(pat) > -1)
				return true;
		}

		int P = parts.length;

		for (int i = 0; i < P; ++i) {

			String tok = parts[i].trim();

			if (tok.equals("no"))
				return true;

			if (tok.equals("not"))
				return true;

			if (tok.equals("nor"))
				return true;
		}

		return false;
	}

	/*
	 * @param: phrase is a string representation of a parse tree chunk
	 * 
	 * @return: true/false if the phrase is negated/not
	 */
	public boolean isNegatedSentence(String phrase) {

		Stack patterns = new Stack();

		patterns.push("no[a-z 0-9,]+effect");
		patterns.push("no[a-z 0-9,]+difference");
		patterns.push("no[a-z 0-9,]+evidence");
		patterns.push("no[a-z 0-9,]+toxic");
		patterns.push("no[a-z 0-9,]+change");
		patterns.push("unimportant");
		patterns.push("unlikely");
		patterns.push("uncommon");
		patterns.push("unchanged");

		phrase = phrase.trim().toLowerCase();

		String[] parts = phrase.split("[ ]");

		int S = patterns.size();

		for (int i = 0; i < S; ++i) {

			String pat = patterns.elementAt(i).toString();

			if (phrase.indexOf(pat) > -1)
				return true;
		}

		int P = parts.length;

		for (int i = 0; i < P; ++i) {

			String tok = parts[i].trim();

			if (tok.equals("no"))
				return true;

			if (tok.equals("not"))
				return true;
		}

		return false;
	}
}
