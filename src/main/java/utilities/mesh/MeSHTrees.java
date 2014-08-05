/*
 * @module: Core MeSH Tree Navigation utilities
 * @author: Naveen Ashish
 * @contact: naveen.ashish@gmail.com
 * @lastupdated: 07/08/2014
 */

package utilities.mesh;
import java.util.*;

public class MeSHTrees{
	
	private static HashMap<String,Vector> hmEntireTree = new HashMap<String,Vector>();
	
	private static HashMap<String,Vector> hmTree = new HashMap<String,Vector>();
	
	private static HashMap<String,Vector> hmMeshMap = new HashMap<String,Vector>();
	
	private static Stack stkAllTreenos=new Stack();
	
	private static Stack stkMesh = new Stack();
	
	private static io.ReadResources RR = new io.ReadResources();
	
	public static void createGroupsEntireMeshTree(){
		
		//Stack stk=utilities.IO.readFileStk("resources/MeSH_data/mtrees2014.bin");
		
		Stack stk = RR.mtrees2014Stk;
		
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
					
					insertTree(hmEntireTree,treeNo,getParent(treeNo));
				}
			}
		}
	}
	
	
	/*
	 * @param	stk	is a stack of tree nodes
	 */
	public static void createGroups(Stack stk){
			
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
					
					insertTree(hmTree,treeNo,getParent(treeNo));
					
					Vector vvAnc=getAncestors(treeNo);
					
					int V=vvAnc.size();
				}
			}
		}
	}
	
	
	public static void createMeshMap(){
		
		//Stack stk=utilities.IO.readFileStk("resources/MeSH_terms/Mesh-Animal.txt-treenumber.txt");
		
		Stack stk=RR.meshAnimalNumber;
		
		int S=stk.size();
		
		for (int i=0; i <S; ++i){
			
			String str=stk.elementAt(i).toString();
			
			String[] parts=str.split(":");
			
			if (parts.length>1){
				
				String term=parts[0].trim();
				
				String treenos=parts[1].trim();
				
				String[] nosparts=treenos.split(",");
				
				int N=nosparts.length;
				
				Vector vv = new Vector();
				
				for (int j=0; j <N; ++j){
					
					String treeNo=nosparts[j].trim();
					
					vv.add(treeNo);
					
					if (!stkMesh.contains(treeNo)) stkMesh.push(treeNo);
				}
				
				hmMeshMap.put(term, vv);
			}
		}
	}
	
	private static String getParent(String node){
		
		int ind=node.lastIndexOf(".");
		
		String parent="";
		
		if (ind>-1){
			
			parent=node.substring(0,ind);
		}
		
		return parent;
	}
	
	/*
	 * @param	node	is a tree node
	 * @return	Vector	of ancestors of tree node
	 */
	private static Vector getAncestors(String node){
		
		Vector vv = new Vector();
		
		String[] parts=node.split("[\\.]");
		
		int P=parts.length;
		
		String anc=parts[0].trim();
		
		vv.add(anc);
		
		for (int i=1; i <P-1;++i) {
			
			anc=anc+"."+parts[i].trim();
			
			vv.add(anc);
		}
		
		return vv;
	}
	
	/*
	 * @param	hmTree	HashMap of tree nodes
	 * @param	node	is the tree node
	 * @param	parent	is the parent of node
	 */
	private static void insertTree(HashMap hmTree,String node,String parent){
		
		if (hmTree.get(parent)==null){
			
			Vector vv = new Vector();
			
			vv.add(node);
			
			hmTree.put(parent, vv);
		} else {
			
			Vector vv = (Vector) hmTree.get(parent);
			
			if (!vv.contains(node)) {
				
				vv.add(node);
				
				hmTree.put(parent, vv);
			}
			
		}
	}
	
	
	private static Stack printTree(String coll){
		
		Stack stkSeen = new Stack();
		
		Collection c = hmTree.keySet();
		
		Iterator it = c.iterator();
		
		Stack stk=new Stack();
		
		Vector vvRes=new Vector();
		
		while (it.hasNext()) {
			
			String node=it.next().toString();
			
			Vector vv = (Vector) hmTree.get(node);
			
			Vector vvEnt = (Vector) hmEntireTree.get(node);

			int V1=vv.size(); int V2=vvEnt.size();
			
			float r = (float)V1/(float)V2;
			
			if (V1>1) if (r>0.9) {
				
				if (!stkSeen.contains(node)) {
				
					String res=node+" [";
					
					//System.out.print(node+" [");
					
					for (int i=0; i <V1;++i) {
				
						res=res+vv.elementAt(i).toString()+", ";
						
						//System.out.print(vv.elementAt(i).toString()+", ");
					}
					
					res=res+"]";
					
					vvRes.add(res);
					
					//System.out.println("]");
					
					stk.push("term:"+node);
					
					stkSeen.push(node);
				}
			}
			
		}

		vvRes=purgeRedundant(vvRes);
		
		utilities.IO.writeFile_BasicAppend("Generalized-"+coll+".txt", vvRes);
		
		return stk;
	}
	
	
	private static Vector purgeRedundant(Vector vv){
		
		int V=vv.size();
		
		Stack stk=new Stack();
		
		Vector vvNew = new Vector();
		
		for (int i=0; i <V; ++i){
			
			String node = vv.elementAt(i).toString().split("[ ]")[0].trim();
			
			stk.push(node);
		}
		
		int S=stk.size();
		
		for (int i=0; i <V; ++i){
			
			String node = vv.elementAt(i).toString().split("[ ]")[0].trim();
			
			boolean redun=false;
			
			for (int j=0;j<S; ++j){
				
				String node2=stk.elementAt(j).toString();
				
				if (node.indexOf(node2)==0) {
					
					if (node2.length() < node.length()) {
						
						redun=true;
						
						//System.out.println("REDUN: "+node+"<<"+node2);
					}
					
				}
			}
			
			if (!redun) vvNew.add(vv.elementAt(i));
		}
		
		System.out.println(vvNew.size()+" <<<<< "+vv.size());
		
		return vvNew;
	}
	
	public static void main(String[] args){
		
		String coll="MeSH_Human";
		
		processColl(coll);
		
		printTree(coll);
		
		coll="MeSH_Animal";
		
		processColl(coll);
		
		printTree(coll);
		
		coll="MeSH_CellCulture";
		
		processColl(coll);
		
		printTree(coll);
		
		coll="MeSH_InVitro";
		
		processColl(coll);
		
		printTree(coll);
		
		coll="MeSH_Mammal";
		
		processColl(coll);
		
		printTree(coll);
		
		coll="AllMesh";
		
		processColl(coll);
		
		printTree(coll);
		
		coll="SelectedMesh-1";
		
		processColl(coll);
		
		printTree(coll);
		
		coll="SelectedMesh-2";
		
		processColl(coll);
		
		printTree(coll);
		
		coll="SelectedMesh-3";
		
		processColl(coll);
		
		printTree(coll);
		
		//createMeshMap();
		
		//System.out.println(stkMesh.size());
		
		//System.exit(1);
		
		
	}

	private static void processColl(String coll) {
		
		//Stack stk=utilities.IO.readFileStk("resources/MeSH_terms/"+coll+".txt-treenumber.txt");
		
		Stack stk = new Stack();
		
		if (coll.contains("Animal")) stk=RR.meshAnimalNumber;
		
		if (coll.contains("Human")) stk=RR.meshHumanNumber;
		
		if (coll.contains("itro")) stk=RR.meshInvitroNumber;
		
		if (coll.contains("ammal")) stk=RR.meshMammalNumber;
		
		if (coll.contains("Cell")) stk=RR.meshCellNumber;
		
		if (coll.contains("AllMesh")) stk=RR.meshAllMeshNumber;
		
		if (coll.contains("-1")) stk=RR.SM1;
		
		if (coll.contains("-2")) stk=RR.SM2;
		
		if (coll.contains("-2")) stk=RR.SM3;
		
		process(stk);
	}

	private static void process(Stack stk) {
			
		hmTree.clear();
		
		hmEntireTree.clear();
		
		createGroupsEntireMeshTree();
		
		createGroups(stk);

	}
}