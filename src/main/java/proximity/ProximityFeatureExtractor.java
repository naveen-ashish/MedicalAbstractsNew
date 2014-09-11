/*
 * @module: Generates feature vectors for proximity
 * @author: Naveen Ashish
 * @contact: naveen.ashish@gmail.com
 * @last updated: 07/08/2014
 */
package proximity;

import java.util.*;

import models.FeatureValues;
import models.FeatureVector;

public class ProximityFeatureExtractor {
	
	private static Stack stkSelectedMesh = new Stack();

	private static Stack stkSelectedMesh1 = new Stack();
	
	private static Stack stkSelectedMesh2 = new Stack();
	
	private static Stack stkSelectedMesh3 = new Stack();
	
	private static Stack stkAllTreenos=new Stack();
	
	private static Stack stkCell = new Stack();
	
	private static Stack stkInvitro = new Stack();
	
	private static Stack stkHuman = new Stack();
	
	private static Stack stkAnimal = new Stack();
	
	private static Stack stkMammal = new Stack();
	
	private static Stack stkRoots = new Stack();
	
	private static Stack stkUniqueTreenums = new Stack();
	
	private static HashMap hmMeshTree = new HashMap();
	
	private static HashMap hmMeshTreeDepth = new HashMap();
	
	private static HashMap meshDataAuto = new HashMap();
	
	private static int SM, SM1,SM2,SM3,SM4,SM5,SM6,SM7,SM8;
	
	private static io.ReadResources RR;
	
	private static void readAutoMesh(){
		
		HashMap meshAuto=new HashMap();
		
		HashMap meshReal = new HashMap();
		
		//Stack stk = utilities.IO.readFileStk("resources/AutoMeshStored.txt");
		
		Stack stk = RR.autoMeshStored;
		
		int S=stk.size();
		
		//System.out.println(S);
		
		for (int i=0; i <S; ++i){
			
			String mesh = stk.elementAt(i).toString();
			
			String[] parts=mesh.split("\t");
			
			String pmid=parts[0].trim();
			
			String meshterms=parts[1].trim().toLowerCase();
			
			meshDataAuto.put(pmid,meshterms);
			
			parts=meshterms.split("#");
			
			int P=parts.length;
			
			Stack stkM=new Stack();
			
			for (int j=0; j <P; ++j){
				
				String meshTerm=parts[j].trim();
				
				if (meshTerm.length()>2){
					
					if (!stkM.contains(meshTerm)) stkM.push(meshTerm);
				}
			}
			
			meshAuto.put(pmid,stkM);
		}
		
		//stk = utilities.IO.readFileStk("resources/AllAbstractsMesh.txt");
		
		stk=RR.allAbstractsMeshStk;
		
		S=stk.size();
		
		for (int i=0; i <S; ++i){
			
			String mesh = stk.elementAt(i).toString();
			
			String[] parts=mesh.split("\t");
			
			String pmid=parts[0].trim();
			
			String meshterms=parts[2].trim().toLowerCase();
			
			//meshDataAuto.put(pmid,meshterms);
			
			parts=meshterms.split("#");
			
			int P=parts.length;
			
			Stack stkM=new Stack();
			
			for (int j=0; j <P; ++j){
				
				String meshTerm=parts[j].trim();
				
				if (meshTerm.length()>2){
					
					if (!stkM.contains(meshTerm)) stkM.push(meshTerm);
				}
			}
			
			meshReal.put(pmid,stkM);
		}
	}
	
	
	public static void readtrees(){		
		
		//Stack stk=utilities.IO.readFileStk("resources/MeSH_data/mtrees2014.bin");
		
		Stack stk=RR.mtrees2014Stk;
		
		int S=stk.size();
		
		for (int i=0; i <S; ++i){
			
			String str=stk.elementAt(i).toString();
			
			String[] parts=str.split(":");
			
			if (parts.length>1){
				
				String term=parts[0].trim();
				
				String treenos=parts[1].trim();
				
				String[] nosparts=treenos.split(",");
				
				int N=nosparts.length;
				
				for (int j=0; j <N; ++j){
					
					String treeNo=nosparts[j].trim();
					
					//System.out.println(treeNo + " | "+getParent(treeNo));
					
					stkAllTreenos.push(treeNo);									
				}
			}
		}
	}
	
	/*
	 * @param	proxchoice	is 1/2/3 depending on whether we want human, animal, invitro
	 * 
	 */
	private static void generateFeatures(Integer proxchoice) throws Exception {
			
		HashMap hmMesh=new HashMap();
		
		HashMap hmProx=new HashMap();
		
		HashMap hmIV=new HashMap();
		
		readAutoMesh();

		int FS=stkRoots.size();
		
		String ivMention="F";
				
		Vector vvFeat = new Vector();
		ArrayList<FeatureValues> featureValuesList = new ArrayList<FeatureValues>();
		LinkedList<String> columnNames = new LinkedList<String>();
		
		Vector vv = new Vector();
		
		String col="";
		
		for (Integer I=0; I <FS;++I){
			col=col+stkRoots.elementAt(I).toString()+",";
			columnNames.add(stkRoots.elementAt(I).toString());
		}
		
		col=col+"IVMENTION"+",";
		
		col=col+"A"+(Integer)(FS+2);
		
		col=col+",PMID";
		
		vvFeat.add(col);
		
		Stack stk=new Stack(), stk2=new Stack();
	
		try {
			//stk=utilities.IO.readFileStk("resources/all_abstracts2.txt");
			
			stk=RR.allAbstracts2Stk;
			
			//stk2=utilities.IO.readFileStk("resources/AllAbstractsMesh.txt");
			
			stk2=RR.allAbstractsMeshStk;
		} catch (Exception e){e.printStackTrace();}
		
		int S = stk.size();
		
		int S2=stk2.size();
		
		while (!stk2.isEmpty()) {
			
			String str=stk2.pop().toString();
			
			String[] parts = str.split("\t");
			
			String pmid=parts[0].trim(); 
			
			String prox=parts[1].trim();
			
			String mesht=parts[2].trim();
			
			hmProx.put(pmid, prox);
			
			if (mesht.toLowerCase().contains("in vitro")) {
				
				hmIV.put(pmid,"T");
			} else {
				
				hmIV.put(pmid,"F");
			}		
		}
		
		String[] feat = new String[FS];
		
		Collection c =meshDataAuto.keySet();
		
		Iterator it = c.iterator();
		
		while (it.hasNext()){
		
			for (int j=0; j<FS; ++j) feat[j]="0";
			
			String pmid=it.next().toString();
			
			String prox="";

			try {

				prox=hmProx.get(pmid).toString();
			} catch (Exception e){System.out.println(pmid);}
			
			if (hmProx.get(pmid)!=null){
			
				try {
					
					prox=hmProx.get(pmid).toString();
				} catch (Exception e){System.out.println(pmid);}
				
				ivMention=hmIV.get(pmid).toString();
	
				if (meshDataAuto.get(pmid)!=null){
				
					String mesht=meshDataAuto.get(pmid).toString();
					
					vv.add(pmid+"\t"+mesht);
								
					mesht=mesht.replaceAll("adult", "");
					
					String[] meshterms = mesht.split("#");
					
					int M=meshterms.length;
		
					for (int j=0; j <M; ++j){
						
						String meshTerm = meshterms[j].trim();
						
						if (meshTerm.length()>2) {
						
							if (hmMeshTree.get(meshTerm)!=null){
											
								String treenum=hmMeshTree.get(meshTerm).toString();
										
								String depth=hmMeshTreeDepth.get(meshTerm).toString();		
								
								if (meshTerm.contains("humans")) {
									
									depth="1.0";
								}
								
								int index=stkRoots.indexOf(treenum);
								
								feat[index]=depth;
											
							} 
						}			
					}				
					
					if (proxchoice==1){
						if (prox.equals("1")) prox="Y";
						if (prox.equals("2")) prox="N";
						if (prox.equals("3")) prox="N";
						if (prox.equals("4")) prox="Y";
						if (prox.equals("5")) prox="Y";
						if (prox.equals("6")) prox="N";
					}
					
					if (proxchoice==2){
					
						if (prox.equals("1")) prox="N";
						if (prox.equals("2")) prox="Y";
						if (prox.equals("3")) prox="N";
						if (prox.equals("4")) prox="Y";
						if (prox.equals("5")) prox="N";
						if (prox.equals("6")) prox="Y";
					}
					if (proxchoice==3){
						if (prox.equals("1")) prox="N";
						if (prox.equals("2")) prox="N";
						if (prox.equals("3")) prox="Y";
						if (prox.equals("4")) prox="N";
						if (prox.equals("5")) prox="Y";
						if (prox.equals("6")) prox="Y";
					}
					
					String res="";
					LinkedList<String> values = new LinkedList<String>();
					for (int j=0; j <FS; ++j){
						values.add(feat[j]);
						res=res+feat[j]+",";
					}
					
					FeatureValues featureValues = new FeatureValues();
					featureValues.setValues(values);
					featureValues.setAbstractId(pmid);
					featureValues.setIvMention(ivMention);
					featureValues.setProximity(prox);
					
					res=res+ivMention+","+prox+","+pmid;
					//System.out.println(res);
					featureValuesList.add(featureValues);
					
					vvFeat.add(res);
		
				}
			}
		}
		FeatureVector featureVector = new FeatureVector();
		featureVector.setVector(featureValuesList);
		featureVector.setJobId("");
		featureVector.setTimestamp("");
		featureVector.setColumns(columnNames);
		io.WriteResults.writeProximityFeatures(featureVector);
		
		io.WriteResults.writeProximityFeatures(proxchoice, vvFeat);
	}
	
	
	private static void getUniqueTreeNumbers(){
		
		Stack stk=new Stack();
		
		String[] lexC={"Mesh_Human","Mesh_Animal", "Mesh_InVitro"};
		
		System.out.println(stkRoots.size());
		
		Stack newStk=getCuratedRoots();
		
		stkRoots.addAll(newStk);
			
		//stk=utilities.IO.readFileStk("resources/MeSH_terms/MeshNaresh.txt-treenumber.txt");
			
		stk=RR.meshNareshNumber;
		
		int S = stk.size();
			
		for (int i=0; i <S; ++i){
				
			String str=stk.elementAt(i).toString();
					
				String[] parts=str.split(":");
				
				if (parts.length>1){
					
					String node=parts[0].trim();
					
					String treenum=parts[1].trim();	
					
					if (treenum.indexOf(",")>-1) treenum=treenum.split(",")[0].trim();
					
					String anctreenum=getAncestor(treenum);
					
					anctreenum = specialCaseTreeNumbers(treenum, anctreenum);
						
					if (anctreenum !=null){
					
						hmMeshTree.put(node, anctreenum);
						
						int depth=getDepth(treenum,anctreenum);
						
						int absdepth=getTotalDepth(treenum);
						
						float reldepth=(float)depth/(float)absdepth;
						
						hmMeshTreeDepth.put(node,reldepth);
						
						//System.out.println(depth+" "+absdepth+" "+reldepth);
						
						if (!stkUniqueTreenums.contains(anctreenum)) stkUniqueTreenums.push(anctreenum);
					} else {
						
						hmMeshTreeDepth.put(node,0);
					}
				}
			}
	}

	/*
	 * @param	treenum	is a treenumber
	 * @return	String which is the ancestor of the treenumber but special handled
	 */
	private static String specialCaseTreeNumbers(String treenum,
			String anctreenum) {
		if (treenum.indexOf("B01.050.150.900.649.801.400.112.400.400")>-1) anctreenum="B01.050.150.900.649.801.400.112.400.400";
		
		if (treenum.indexOf("F01.145.113")>-1) anctreenum="F01.145.113";
		
		if (treenum.indexOf("F01.145.113")>-1) anctreenum="F01.145.113";
		
		return anctreenum;
	}
	
	
	private static Stack getCuratedRoots(){
		
		Stack finStk=new Stack();
		
		//Stack stk=utilities.IO.readFileStk("resources/topNodes_human.txt");
		
		Stack stk=RR.topNodesHumanStk;
		
		int S=stk.size();
		
		for (int i=0; i <S; ++i){
			
			String str=stk.elementAt(i).toString();
			
			String[] parts=str.split(":");
			
			String treenos=parts[1].trim();
			
			String[] noparts=treenos.split(",");
			
			int N = noparts.length;
			
			for (int j=0; j <N; ++j){
				
				String treenum=noparts[j].trim();
				
				finStk.push(treenum);
			}
		}
		
		//stk=utilities.IO.readFileStk("resources/topNodes_animal.txt");
		
		stk = RR.topNodesAnimalStk;
		
		S=stk.size();
		
		for (int i=0; i <S; ++i){
			
			String str=stk.elementAt(i).toString();
			
			String[] parts=str.split(":");
			
			String treenos=parts[1].trim();
			
			String[] noparts=treenos.split(",");
			
			int N = noparts.length;
			
			for (int j=0; j <N; ++j){
				
				String treenum=noparts[j].trim();
				
				finStk.push(treenum);
			}
		}
		
		//Stack stk=utilities.IO.readFileStk("resources/topNodes_invitro.txt");
		
		stk=RR.topNodesIVStk;
		
		S=stk.size();
		
		for (int i=0; i <S; ++i){
			
			String str=stk.elementAt(i).toString();
			
			String[] parts=str.split(":");
			
			String treenos=parts[1].trim();
			
			String[] noparts=treenos.split(",");
			
			int N = noparts.length;
			
			for (int j=0; j <N; ++j){
				
				String treenum=noparts[j].trim();
				
				finStk.push(treenum);
			}
		}
	
		return finStk;
	
	}
	
	/*
	 * @param	treeNo	is a tree number
	 * @return	int	is the absolute depth
	 */
	private static int getTotalDepth(String treeNo){
	
		int S=stkAllTreenos.size();
		
		int L=0;
		
		for (int i=0; i <S; ++i){
			
			String node=stkAllTreenos.elementAt(i).toString();
			
			if (node.indexOf(treeNo)==0) {
				
				int L2=node.length();
				
				if (L2>L){
					
					String[] parts=node.split("[\\.]");
					
					int P=parts.length;
					
					if (P>L) L=P;
				}
			}
		}
		
		return L;
	}
	
	
	private static String getAncestor(String treenum){
		
		if (treenum.trim().equals("")) return null;
		
		int S = stkRoots.size();
		
		for (int i=0; i <S; ++i){
			
			String anc=stkRoots.elementAt(i).toString();
			
			if (treenum.indexOf(anc)>-1) return anc;
		}
		
		return null;
	}
	
	private static int getDepth(String node, String anc){
		
		String[] parts1=node.split("\\.");
		
		String[] parts2=anc.split("\\.");
		
		return (parts1.length-parts2.length)+1;
	}
	
	public static void execute(io.ReadResources myRR){
		
		//RR.readAllResources();
		
		RR = myRR;
		
		readtrees();
		
		getUniqueTreeNumbers();
			
		try {
			//generateFeatures(1);
			
			generateFeatures(2);
		} catch (Exception e){e.printStackTrace();}
		
		System.out.println("All done !! Feature vectors placed in <project folder>/output/New-ProxFeatures-1(/2).txt");
	}
	
	public static void main(String[] args){
		
		io.ReadResources RR = new io.ReadResources();
		
		RR.readAllResources();
		
		new ProximityFeatureExtractor().execute(RR);
		
		System.exit(1);
		
		readtrees();
		
		getUniqueTreeNumbers();
			
		try {
			//generateFeatures(1);
			
			generateFeatures(2);
		} catch (Exception e){e.printStackTrace();}
		
		System.out.println("All done !! Feature vectors placed in <project folder>/output/New-ProxFeatures-1(/2).txt");
	}
}