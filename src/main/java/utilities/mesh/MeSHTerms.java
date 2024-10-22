/*
 * @module: Core MeSH utilities
 * @author: Naveen Ashish
 * @contact: naveen.ashish@gmail.com
 * @lastupdated: 07/08/2014
 */

package utilities.mesh;

import java.io.BufferedReader;

import java.util.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;


public class MeSHTerms {

	public static Map<String, String> meshData = new HashMap();
	
	public static Map<String, String> meshDataAuto = new HashMap();
	
	public static Map<String, Stack> meshEntryTerms = new HashMap();
	
	private static io.ReadResources RR = new io.ReadResources();
		
	public static void main(String[] args) throws IOException {
		
		//readMeshData();

		
		meshData = RR.meshData;
		
		meshEntryTerms=RR.meshEntryTerms;
		
		//System.out.println(meshData.size());
		//System.out.println(meshEntryTerms.size());
		//System.exit(1);
		
		
		//readMeshDataAutoTagged2();
//		printAutoMesh();
//		System.exit(1);
		
		processMeshTermFile("resources/MeSH_terms/MeshNaresh.txt");
		processMeshTermFile("resources/MeSH_terms/AllMesh.txt");
		processMeshTermFile("resources/MeSH_terms/AllPMIDMesh.txt");
		processMeshTermFile("resources/MeSH_terms/MeSH_Animal.txt");
		processMeshTermFile("resources/MeSH_terms/MeSH_CellCulture.txt");
		processMeshTermFile("resources/MeSH_terms/MeSH_Human.txt");
		processMeshTermFile("resources/MeSH_terms/MeSH_InVitro.txt");
		processMeshTermFile("resources/MeSH_terms/MeSH_Mammal.txt");
		processMeshTermFile("resources/MeSH_terms/SelectedMesh-1.txt");
		processMeshTermFile("resources/MeSH_terms/SelectedMesh-2.txt");
		processMeshTermFile("resources/MeSH_terms/SelectedMesh-3.txt");
		
		//inferMeshTerms();

	}
	
	/*
	 * @functionality	write out auto mesh tags
	 */
	private static void printAutoMesh(){
		
		Vector vv = new Vector();
		
		Collection c = meshDataAuto.keySet();
		
		Iterator it = c.iterator();
		
		while (it.hasNext()){
			
			String pmid=it.next().toString();
			
			String mesht = meshDataAuto.get(pmid).toString();
			
			vv.add(pmid+"\t"+mesht);
		}
		
		utilities.IO.writeFile_Basic("resources/AutoMeshStored.txt", vv);
	}
	
	/*
	 * @functionality	read in Mesh terms from file dump
	 * @see	Mesh, NIH
	 */
	public static void readMeshData() throws IOException {
		
		BufferedReader br = new BufferedReader(new FileReader("resources/MeSH_data/d2014.bin"));
		
		String line;
		
		String currentTerm = null;
		
		while ((line = br.readLine()) != null) {
		
			if(line.startsWith("MH =")) {
			
				currentTerm = line.substring(5); // the terms begin from index 5 of the line
		   } else if (line.startsWith("MN =")) {
			   
			   meshData.put(currentTerm.toLowerCase(), readNextTreeNumbers(line.substring(5), br));
		   } else if (line.contains("ENTRY =")){
			   
			   Stack stk = new Stack();
			   
			   stk.push(currentTerm.toLowerCase());
			    
			   int ind=line.indexOf("ENTRY =")+8;
			   
			   String term=line.toLowerCase().substring(ind).trim();
			   
			   ind = term.indexOf("|");
			   
			   if (ind>-1) term=term.substring(0,ind);
			   
			   //System.out.println(line+"\n"+term);
			  
			 if (term.length()>2) if (!stk.contains(term)) stk.push(term);
			   
			
			   if (meshEntryTerms.get(currentTerm.toLowerCase())==null){
				      
				   meshEntryTerms.put(currentTerm.toLowerCase(), stk);
			   } else {
				   
				   Stack origstk=meshEntryTerms.get(currentTerm.toLowerCase());
				  
				   while (!stk.isEmpty()) origstk.add(stk.pop());
				   
				   meshEntryTerms.put(currentTerm.toLowerCase(), origstk);
				  
			   }
		   }
		}
		br.close();		
	}
	
	
	
	public static void readMeshDataAutoTagged(){
		
		Vector vv = new Vector(), vv2=new Vector();
		
		//Stack stk = utils.IO.readFileStk("resources/OutBalancedSet.txt");
		Stack stk = utilities.IO.readFileStk("resources/TextOut6000.txt");
		
		int S=stk.size();
		
		
		for (int i=0; i <S; ++i) {
			
			String str = stk.elementAt(i).toString();
			
			String[] parts = str.split("\t");
			
			if (parts.length>2){
			
			String pmid=parts[0].trim();
			
			String meshTerm=parts[1].trim().toLowerCase();
							
			if(!isMeshTerm(meshTerm)) {
				
				String newmesht=getMeshTermByEntryTerm(meshTerm);

				if (newmesht !=null) {				
					
					if (newmesht.length()>2) if (!newmesht.equals(meshTerm)) {
						
						System.out.println(meshTerm+"("+newmesht+")");
						
						String ent=meshTerm+"("+newmesht+")";
						
						if (!vv2.contains(ent)) vv2.add(ent);
					}
				
				}	
				
				meshTerm=newmesht;
			}
							
			if (meshDataAuto.containsKey(pmid)) {
							
				String mesht = meshDataAuto.get(pmid).toString();
						
				if (meshTerm.length()>2) if (!mesht.contains(meshTerm)){
							
					mesht=mesht+meshTerm+"#";
								
					meshDataAuto.put(pmid,mesht);
							
					//System.out.println(pmid+": "+ mesht);
							
				}		
				
			} else {
								
					String mesht=meshTerm+"#";
							
					meshDataAuto.put(pmid,mesht);
			
					//System.out.println(pmid+": "+ mesht);				
			}
	
			if (!vv.contains(meshTerm)) vv.add(meshTerm);
	
			}
		}
		
		utilities.IO.writeFile_Basic("resources/MeSH_terms/MeshNaresh.txt", vv);
		
		utilities.IO.writeFile_Basic("StillEntryTerms.txt", vv2);
	}

	/*
	 * @param	firstTreeNumber is a provided mesh tree number
	 * @param	br is the mesh file handle
	 * @return	String 	the following mesh tree number
	 */
	private static String readNextTreeNumbers(String firstTreeNumber, BufferedReader br) throws IOException {
		
		List<String> treeNumbers = new ArrayList();
		
		treeNumbers.add(firstTreeNumber);
		
		String line;
		
		while ((line = br.readLine()) != null && line.startsWith("MN =")) {
		
			treeNumbers.add(line.substring(5));
		}
		
		return StringUtils.join(treeNumbers, ", ");
	}

	/*
	 * @param filePath	is the location of the MeSH dump
	 */
	private static void processMeshTermFile(String filePath) throws IOException {
		
		List<String> terms = readTerms(filePath);
		
		List<String> treeNumbers = lookupTreeNumbers(terms);
		
		generateTreeNumberFile(treeNumbers, filePath);
	}

	private static void generateTreeNumberFile(List<String> treeNumbers, String meshFilePath) throws IOException {
		
		BufferedWriter bw = new BufferedWriter(new FileWriter(new File(meshFilePath + "-treenumber.txt"), false));
		
		for(String str: treeNumbers) {
		
			bw.write(str);
			
			bw.newLine();
		}

		bw.close();
	}

	/*
	 * @param	terms	is a list of mesh terms
	 * @return	List	of corresponding mesh tree numbers
	 */
	private static List<String> lookupTreeNumbers(List<String> terms) {
	
		List<String> treeNumbers = new ArrayList(terms.size());
		
		for (String t : terms) {
		
			t = t.toLowerCase();
			
			treeNumbers.add(t + ": " + (meshData.get(t) == null? "" : meshData.get(t)));
		}
		
		return treeNumbers;
	}

	private static List<String> readTerms(String filePath) throws IOException {
		
		BufferedReader br = new BufferedReader(new FileReader(filePath));
		
		List<String> terms = new ArrayList<String>();
		
		String t;
		
		while ((t = br.readLine()) != null) {
		
			terms.add(t);
		}

		br.close();
		
		return terms;
	}
	
	private static void inferMeshTerms(){
		
		//Stack stk=utilities.IO.readFileStk("all_abstracts2.txt");
		
		Stack stk = RR.allAbstracts2Stk;
		
		int S=stk.size();
		
		for (int i=0; i <S; ++i){
			
			String str = stk.elementAt(i).toString();
			
			String[] parts = str.split("\t");
			
			String abstractText=parts[2].trim().toLowerCase();
			
			getMeshTerms(abstractText);
		}
	}
	
	public static String getMeshTerms(String abstractText){
		
		abstractText=abstractText.toLowerCase();
		
		SortedMap sm = new TreeMap();
		
		String res="#";
		
		Collection c=meshEntryTerms.keySet();
		
		Iterator it =c.iterator();
		
		//System.out.println("");
		
		//System.out.println(meshEntryTerms.size());
		
		//System.exit(1);
		
		while (it.hasNext()){
			
			String key = it.next().toString();
			
			Stack entryStk=meshEntryTerms.get(key);
			
			//System.out.println(entryStk.size());
			
			float mscore=scoreMeshMatch(abstractText,entryStk);
			
			if (mscore>0)
				res=res+key+"#";
		}
		
		//System.out.println("\n"+sm.size()+"\n");
		
		return res;
	}
	
	private static float scoreMeshMatch(String abstractText,Stack stk){
		
		int S=stk.size();
		
		int K=0;
		
		for (int i=0; i <S; ++i){
			
			String word=stk.elementAt(i).toString();
			
			if (abstractText.contains(word)){
				
				return 1;
	
			}
		}
		
		return 0;
	}
	
	public static boolean isMeshTerm(String term){
		
		return meshData.containsKey(term);
	}
	
	public static String getMeshTermByEntryTerm(String term){
		
		Collection c = meshEntryTerms.keySet();
		
		Iterator it = c.iterator();
		
		while (it.hasNext()){
			
			String mesht=it.next().toString();
			
			Stack stk=(Stack) meshEntryTerms.get(mesht);
			
			//System.out.println(stk.size());
			
			if (mesht.contains("menstrual")){
				
				int S=stk.size();
				
				//for (int j=0; j<S; ++j) System.out.print(stk.elementAt(j).toString()+" : ");
				
				//System.out.println("");
			}
			
			if (stk.contains(term)) return mesht;
		}
		
		return "";
	}
}
