package io;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;
import java.util.Vector;

import org.apache.commons.lang3.StringUtils;

import utilities.IO;

public class ReadResources {

	private String allabstractsLoc = "resources/all_abstracts2.txt";
	// allabstractsLoc="resources/all_abstracts3.txt";
	private String traDataLoc = "resources/trainingData_naveenProject1.csv";
	// traDataLoc="resources/holdback.csv";
	private String harmSynLoc = "resources/harmSynonyms.csv";
	private String litSupLoc = "resources/litagionSuperstrings.csv";
	private String litSynLoc = "resources/litagionSynonyms.csv";
	// dictLoc="resources/aarDictionary_tag.csv";
	private String dictLoc = "resources/aarDictionary_tell2.csv";
	private String litAbbrevLoc = "resources/LitAbbreviations.txt";

	public HashMap hmDir = new HashMap();

	public HashMap hmLitAbbreviations = new HashMap();

	public HashMap harmMap;

	public HashMap litagionSuperStringMap;

	public HashMap litagionSynonymMap;

	public HashMap dictionaryMap, abstractMap, trainingInfoMap;

	public HashMap meshData, meshEntryTerms, meshDataAuto = new HashMap();

	public Stack mtrees2014Stk, meshData2014Stk, meshHumanNumber, meshAnimalNumber, meshCellNumber, meshInvitroNumber,
			autoMeshStored = new Stack();

	public Stack meshMammalNumber, meshAllMeshNumber, meshNareshNumber, SM1, SM2, SM3 = new Stack();

	public Stack topNodesAnimalStk, topNodesHumanStk, topNodesIVStk, textout6000Stk, allAbstracts2Stk,
			allAbstractsMeshStk, selectedMeshStk = new Stack();

	private IO io = new IO();

	public void readAllResources() {

		readTellDictionaries();

		readMeshResources();

		readAbstractData();

	}

	private void readAbstractData() {
		allAbstracts2Stk = io.readFileStk("resources/all_abstracts2.txt");

		allAbstractsMeshStk = io.readFileStk("resources/AllAbstractsMesh.txt");
	}

	private void readTellDictionaries() {
		abstractMap = fetchAbstracts();

		trainingInfoMap = extractTrainingData();

		harmMap = loadHarmClass();

		litagionSuperStringMap = loadLitagionSuperStrings();

		litagionSynonymMap = loadLitagionSynonym();

		dictionaryMap = loadDataDictionary();

		hmLitAbbreviations = readAbbreviations();
	}

	private void readMeshResources() {
		mtrees2014Stk = io.readFileStk("resources/MeSH_data/mtrees2014.bin");

		meshAnimalNumber = io.readFileStk("resources/MeSH_terms/Mesh_Animal.txt-treenumber.txt");

		meshHumanNumber = io.readFileStk("resources/MeSH_terms/Mesh_Human.txt-treenumber.txt");

		meshInvitroNumber = io.readFileStk("resources/MeSH_terms/Mesh_InVitro.txt-treenumber.txt");

		meshCellNumber = io.readFileStk("resources/MeSH_terms/Mesh_CellCulture.txt-treenumber.txt");

		meshMammalNumber = io.readFileStk("resources/MeSH_terms/Mesh_Mammal.txt-treenumber.txt");

		meshAllMeshNumber = io.readFileStk("resources/MeSH_terms/AllMesh.txt-treenumber.txt");

		SM1 = io.readFileStk("resources/MeSH_terms/SelectedMesh-1.txt-treenumber.txt");

		SM2 = io.readFileStk("resources/MeSH_terms/SelectedMesh-2.txt-treenumber.txt");

		SM3 = io.readFileStk("resources/MeSH_terms/SelectedMesh-3.txt-treenumber.txt");

		autoMeshStored = io.readFileStk("resources/AutoMeshStored.txt");

		selectedMeshStk = io.readFileStk("resources/SelectedMesh.txt");

		meshNareshNumber = io.readFileStk("resources/MeSH_terms/MeshNaresh.txt-treenumber.txt");

		topNodesAnimalStk = io.readFileStk("resources/topNodes_animal.txt");

		topNodesHumanStk = io.readFileStk("resources/topNodes_human.txt");

		topNodesIVStk = io.readFileStk("resources/topNodes_invitro.txt");
	}

	/*
	 * @functionality read in harm synonyms
	 * 
	 * @return HashMap of harm synonyms
	 */
	public HashMap<String, ArrayList<String>> loadHarmClass() {

		HashMap<String, ArrayList<String>> map = new HashMap<String, ArrayList<String>>();

		try {

			FileReader fr = new FileReader(harmSynLoc);

			BufferedReader bf = new BufferedReader(fr);

			String line = "";

			while ((line = bf.readLine()) != null) {

				String[] splitter = line.split(",");

				String harm = splitter[0].trim().toLowerCase();

				String synonym = splitter[1].trim().toLowerCase();

				if (map.containsKey(harm)) {

					ArrayList<String> list = map.get(harm);

					list.add(synonym);

					map.remove(harm);

					map.put(harm, list);
				} else {

					ArrayList<String> list = new ArrayList<String>();

					list.add(synonym);

					list.add(harm);

					map.put(harm, list);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return map;
	}

	/*
	 * @functionality read in litagion super strings
	 * 
	 * @return HashMap of litagion super strings
	 */
	public HashMap<String, ArrayList<String>> loadLitagionSuperStrings() {

		HashMap<String, ArrayList<String>> map = new HashMap<String, ArrayList<String>>();

		try {

			FileReader fr = new FileReader(litSupLoc);

			BufferedReader bf = new BufferedReader(fr);

			String line = "";

			while ((line = bf.readLine()) != null) {

				String litagion = "";

				String synonym = "";

				if (line.contains("\",\"")) {

					String[] splitter = line.split("\",\"");

					litagion = splitter[0].trim().toLowerCase().replaceAll("\"", "");

					synonym = splitter[1].trim().toLowerCase().replaceAll("\"", "");
				} else if (line.contains(",\"")) {

					String[] splitter = line.split(",\"");

					litagion = splitter[0].trim().toLowerCase().replaceAll("\"", "");

					synonym = splitter[1].trim().toLowerCase().replaceAll("\"", "");
				} else if (line.contains("\",")) {

					String[] splitter = line.split("\",");

					litagion = splitter[0].trim().toLowerCase().replaceAll("\"", "");

					synonym = splitter[1].trim().toLowerCase().replaceAll("\"", "");
				} else {

					String[] splitter = line.split(",");

					litagion = splitter[0].trim().toLowerCase();

					synonym = splitter[1].trim().toLowerCase();
				}

				if (map.containsKey(litagion)) {

					ArrayList<String> list = map.get(litagion);

					list.add(synonym);

					map.remove(litagion);

					map.put(litagion, list);
				} else {

					ArrayList<String> list = new ArrayList<String>();

					list.add(synonym);

					list.add(litagion);

					map.put(litagion, list);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return map;
	}

	/*
	 * @functionality read in litagion synonyms
	 * 
	 * @return HashMap of litagion synonyms
	 */
	public HashMap<String, ArrayList<String>> loadLitagionSynonym() {

		HashMap<String, ArrayList<String>> map = new HashMap<String, ArrayList<String>>();

		try {

			FileReader fr = new FileReader(litSynLoc);

			BufferedReader bf = new BufferedReader(fr);

			String line = "";

			while ((line = bf.readLine()) != null) {

				String litagion = "";

				String synonym = "";

				if (line.contains("\",\"")) {

					String[] splitter = line.split("\",\"");

					litagion = splitter[0].trim().toLowerCase().replaceAll("\"", "");

					synonym = splitter[1].trim().toLowerCase().replaceAll("\"", "");
				} else if (line.contains(",\"")) {

					String[] splitter = line.split(",\"");

					litagion = splitter[0].trim().toLowerCase().replaceAll("\"", "");

					synonym = splitter[1].trim().toLowerCase().replaceAll("\"", "");
				} else if (line.contains("\",")) {

					String[] splitter = line.split("\",");

					litagion = splitter[0].trim().toLowerCase().replaceAll("\"", "");

					synonym = splitter[1].trim().toLowerCase().replaceAll("\"", "");
				} else {

					String[] splitter = line.split(",");

					litagion = splitter[0].trim().toLowerCase();

					synonym = splitter[1].trim().toLowerCase();
				}

				if (map.containsKey(litagion)) {

					ArrayList<String> list = map.get(litagion);

					list.add(synonym);

					map.remove(litagion);

					map.put(litagion, list);
				} else {

					ArrayList<String> list = new ArrayList<String>();

					list.add(synonym);

					list.add(litagion);

					map.put(litagion, list);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return map;
	}

	public HashMap<String, ArrayList<String>> loadDataDictionaryOld() {

		HashMap<String, ArrayList<String>> map = new HashMap<String, ArrayList<String>>();

		try {

			FileReader fr = new FileReader("resources/directionDic.csv");

			BufferedReader bf = new BufferedReader(fr);

			String line = "";

			while ((line = bf.readLine()) != null) {

				String[] splitter = line.split(",");

				if (splitter.length == 14) {
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
		} catch (IOException e) {
			e.printStackTrace();
		}
		return map;
	}

	/*
	 * @functionality read in dictionary
	 * 
	 * @return HashMap of dictionary terms
	 */
	public HashMap<String, ArrayList<String>> loadDataDictionary() {

		HashMap<String, ArrayList<String>> map = new HashMap<String, ArrayList<String>>();

		try {

			FileReader fr = new FileReader(dictLoc);

			BufferedReader bf = new BufferedReader(fr);

			String line = "";

			while ((line = bf.readLine()) != null) {

				line = " " + line + " ";

				String[] splitter = line.split(",");

				if (splitter.length == 18) {
					addToMap("IS_ARTICLE", splitter[0].trim().toLowerCase(), map);
					addToMap("IS_ARTICLE_PHRASE", splitter[1].trim().toLowerCase(), map);
					addToMap("IS_FINDING", splitter[2].trim().toLowerCase(), map);
					addToMap("IS_INDICATOR", splitter[3].trim().toLowerCase(), map);
					addToMap("IS_INDICATOR_NEG", splitter[4].trim().toLowerCase(), map);
					addToMap("IS_LITAGION", splitter[5].trim().toLowerCase(), map);
					addToMap("IS_ACTION", splitter[6].trim().toLowerCase(), map);
					addToMap("IS_ACTION_NEG", splitter[7].trim().toLowerCase(), map);
					addToMap("IS_NEG_MOD", splitter[10].trim().toLowerCase(), map);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		// System.exit(1);
		return map;
	}

	public void addToMap(String key, String value, HashMap<String, ArrayList<String>> map) {
		if (!value.equals("")) {
			if (map.containsKey(key)) {
				ArrayList<String> list = map.get(key);
				list.add(value);
				map.remove(key);
				map.put(key, list);
			} else {
				ArrayList<String> list = new ArrayList<String>();
				list.add(value);
				map.put(key, list);

			}
			// .out.println(key+"|"+value);
		}
	}

	public HashMap readAbbreviations() {

		HashMap hm = new HashMap();

		Stack stk = io.readFileStk(litAbbrevLoc);

		while (!stk.isEmpty()) {

			String str = stk.pop().toString();

			String[] parts = str.split(":");

			String lit = parts[0];

			String abbrev = parts[1];

			// System.out.println(lit+","+abbrev);

			hm.put(lit, abbrev);
		}

		return hm;
	}

	/*
	 * @functionality: get the abstracts
	 * 
	 * @return Hashmap hash map of abstracts
	 */
	private HashMap<String, String> fetchAbstracts() {

		HashMap<String, String> map = new HashMap<String, String>();

		try {

			FileReader fr = new FileReader(allabstractsLoc);

			BufferedReader bf = new BufferedReader(fr);

			String line = "";

			while ((line = bf.readLine()) != null) {

				String[] splitter = line.split("\t");

				if (splitter[2].trim().length() > 5) {

					if (!map.containsKey(splitter[0].trim())) {

						map.put(splitter[0].trim(), splitter[1] + "##" + splitter[2].trim());
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return map;
	}

	/*
	 * @functionality: get training data tags
	 * 
	 * @return HashMap hash map of (directionality) tags
	 */
	public HashMap<String, String> extractTrainingData() {

		HashMap<String, String> map = new HashMap<String, String>();

		try {

			FileReader fr = new FileReader(traDataLoc);

			BufferedReader bf = new BufferedReader(fr);

			String line = "";

			while ((line = bf.readLine()) != null) {

				if (line.contains("\"")) {

					String[] splitter = line.split(",");

					hmDir.put(splitter[0].trim() + splitter[1].trim() + splitter[2].trim(), splitter[3].trim());

					map.put(splitter[0].trim(),
							splitter[1].trim().replace("\"", "") + splitter[2].trim().replace("\"", "") + "##"
									+ splitter[3].trim());
				} else {
					String[] splitter = line.split(",");

					hmDir.put(splitter[0].trim() + splitter[1].trim() + splitter[2].trim(), splitter[3].trim());

					map.put(splitter[0].trim(), splitter[1].trim() + "##" + splitter[2].trim());
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return map;
	}

	public void readMeshData() throws IOException {

		BufferedReader br = new BufferedReader(new FileReader("resources/MeSH_data/d2014.bin"));

		String line;

		String currentTerm = null;

		while ((line = br.readLine()) != null) {

			if (line.startsWith("MH =")) {

				currentTerm = line.substring(5); // the terms begin from index 5
													// of the line
			} else if (line.startsWith("MN =")) {

				meshData.put(currentTerm.toLowerCase(), readNextTreeNumbers(line.substring(5), br));
			} else if (line.contains("ENTRY =")) {

				Stack stk = new Stack();

				stk.push(currentTerm.toLowerCase());

				int ind = line.indexOf("ENTRY =") + 8;

				String term = line.toLowerCase().substring(ind).trim();

				ind = term.indexOf("|");

				if (ind > -1)
					term = term.substring(0, ind);

				// System.out.println(line+"\n"+term);

				if (term.length() > 2)
					if (!stk.contains(term))
						stk.push(term);

				if (meshEntryTerms.get(currentTerm.toLowerCase()) == null) {

					meshEntryTerms.put(currentTerm.toLowerCase(), stk);
				} else {

					Stack origstk = (Stack) meshEntryTerms.get(currentTerm.toLowerCase());

					while (!stk.isEmpty())
						origstk.add(stk.pop());

					meshEntryTerms.put(currentTerm.toLowerCase(), origstk);

				}
			}
		}
		br.close();
	}

	/*
	 * @param firstTreeNumber is a provided mesh tree number
	 * 
	 * @param br is the mesh file handle
	 * 
	 * @return String the following mesh tree number
	 */
	private String readNextTreeNumbers(String firstTreeNumber, BufferedReader br) throws IOException {

		List<String> treeNumbers = new ArrayList();

		treeNumbers.add(firstTreeNumber);

		String line;

		while ((line = br.readLine()) != null && line.startsWith("MN =")) {

			treeNumbers.add(line.substring(5));
		}

		return StringUtils.join(treeNumbers, ", ");
	}

	public void readMeshDataAutoTagged() {

		Vector vv = new Vector(), vv2 = new Vector();

		// Stack stk = utils.IO.readFileStk("resources/OutBalancedSet.txt");
		Stack stk = io.readFileStk("resources/TextOut6000.txt");

		int S = stk.size();

		for (int i = 0; i < S; ++i) {

			String str = stk.elementAt(i).toString();

			String[] parts = str.split("\t");

			if (parts.length > 2) {

				String pmid = parts[0].trim();

				String meshTerm = parts[1].trim().toLowerCase();

				if (!isMeshTerm(meshTerm)) {

					String newmesht = getMeshTermByEntryTerm(meshTerm);

					if (newmesht != null) {

						if (newmesht.length() > 2)
							if (!newmesht.equals(meshTerm)) {

								System.out.println(meshTerm + "(" + newmesht + ")");

								String ent = meshTerm + "(" + newmesht + ")";

								if (!vv2.contains(ent))
									vv2.add(ent);
							}

					}

					meshTerm = newmesht;
				}

				if (meshDataAuto.containsKey(pmid)) {

					String mesht = meshDataAuto.get(pmid).toString();

					if (meshTerm.length() > 2)
						if (!mesht.contains(meshTerm)) {

							mesht = mesht + meshTerm + "#";

							meshDataAuto.put(pmid, mesht);

							// System.out.println(pmid+": "+ mesht);

						}

				} else {

					String mesht = meshTerm + "#";

					meshDataAuto.put(pmid, mesht);

					// System.out.println(pmid+": "+ mesht);
				}

				if (!vv.contains(meshTerm))
					vv.add(meshTerm);

			}
		}

		io.writeFile_Basic("resources/MeSH_terms/MeshNaresh.txt", vv);

		io.writeFile_Basic("StillEntryTerms.txt", vv2);
	}

	public String getMeshTermByEntryTerm(String term) {

		Collection c = meshEntryTerms.keySet();

		Iterator it = c.iterator();

		while (it.hasNext()) {

			String mesht = it.next().toString();

			Stack stk = (Stack) meshEntryTerms.get(mesht);

			// System.out.println(stk.size());

			if (mesht.contains("menstrual")) {

				int S = stk.size();

				// for (int j=0; j<S; ++j)
				// System.out.print(stk.elementAt(j).toString()+" : ");

				// System.out.println("");
			}

			if (stk.contains(term))
				return mesht;
		}

		return "";
	}

	public boolean isMeshTerm(String term) {

		return meshData.containsKey(term);
	}

	public static void main(String[] args) {

		new ReadResources().readAllResources();
	}
}