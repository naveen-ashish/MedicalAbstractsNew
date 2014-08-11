/*
 * @module: Code for TFIDF analysis as required
 * @author: Naveen Ashish
 * @contact: naveen.ashish@gmail.com
 * @last updated: 03/26/2014
 */

package utilities;

import java.io.BufferedReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;




public class TFIDFCalculatorFn {
	
	private HashMap hmInd = new HashMap();
	
	private Vector vInd = new Vector();
	
	private Stemmer stem = new Stemmer();
	
	private Statement stmt = null;
	
	private int K=0;
	
	private IO io = new IO();
	
	private void addClass(String hClass,String term){
		
		if (hmInd.get(hClass)==null) {
			
			Stack stk = new Stack();
			
			stk.push(term);
			
			hmInd.put(hClass, stk);
		} else {
			
			Stack stk=(Stack) hmInd.get(hClass);
			
			Stack stk2=(Stack) stk.clone();
			
			stk2.push(term);
			
			hmInd.put(hClass,stk2);
		}
	}
	
	public void calculate(){
		try {
			
			System.out.println("Entering....");
			
			HashSet<String> litagionSet = extractNouns();
			
			Connection conn = connectDB();
			
			HashSet<String> harmClassSet = extractHarmClass(conn);
			
			for(String harmClass : harmClassSet){
			
				System.out.println("Processing Harm Class  --- " + harmClass);
				
				FileWriter fw = new FileWriter(new File("results/" + harmClass + ".csv"));
				
				for(String litagionTerm : litagionSet){
			
					if (litagionTerm.length()>2){
					
					System.out.println("Processing Term --  " + litagionTerm);
					
					if(fetchNoDocumentsInClassWithTerm(conn, harmClass, litagionTerm) != 0.0){
					
						double value = calculateFTC(fetchNoDocumentsInClassWithTerm(conn, harmClass, litagionTerm), 
								fetchNoDocumentsInClass(harmClass, conn), litagionTerm);
						
						double tf = getTFScore(value);
						
						double idf = getIDFScore(value);
						
						fw.write(litagionTerm + "," + harmClass + "," + value + "," + tf + "," + idf + "," + (tf * idf) + ",\n");
						
						fw.flush();
					}
				}
				}
				fw.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void calculate2(){
		try {
			
			HashSet<String> litagionSet = extractNouns();
			
			Connection conn = connectDB();
			
			HashSet<String> harmClassSet = extractHarmClass(conn);
			
				//vInd.add("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<indicators>");
				
				for(String litagionTerm : litagionSet){
				
					if (litagionTerm.length()>2){
					
					//System.out.println("Processing Term --  " + litagionTerm);
					
					double value2Total=0.0;
					
					HashMap hm = fetchTermDistribution(conn,litagionTerm);
						
					processDistribution(litagionTerm,hm);
					
					
				}
				}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void calculateLit(){
		try {
			
			HashSet<String> litagionSet = extractNouns();
			
			Connection conn = connectDB();
			
			HashSet<String> harmClassSet = extractHarmClass(conn);
			
				//vInd.add("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<indicators>");
				
				for(String litagionTerm : litagionSet){
				
					if (litagionTerm.length()>1){
					
					//System.out.println("Processing Term --  " + litagionTerm);
					
					double value2Total=0.0;
					
					HashMap hm = fetchTermDistributionLit(conn,litagionTerm);
						
					processDistribution(litagionTerm,hm);
					
					
				}
				}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public HashSet<String> extractNouns(){
		HashSet<String> set = new HashSet<String>();
		int C=0;
		
		Stack stk=new Stack();
		
		try {
			FileReader fr = new FileReader(new File("litagionList.txt"));
			BufferedReader bf = new BufferedReader(fr);
			String line = "";
			while((line = bf.readLine()) != null){
				
				String word=line.trim().toLowerCase();
				
				String nword=word.replaceAll("[0-9]", "");
				
				if (word.equals(nword)) {
					
					int T=word.length();
					
					
					for (int j=0; j <T; ++j) stem.add(word.charAt(j));
						
					stem.stem();
					
					//Check the stem word of the term also !
					String stemWord=stem.toString();
					
					
					
					if (word.length()>3) {
						
						if (!stk.contains(stemWord)){
							
							System.out.println(stemWord);
							
							stk.push(stemWord);
							
							set.add(stemWord);
						}
					}
				}
				
				//set.add(line.trim().toLowerCase());
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println(stk.size());
		
		//System.exit(1);
		
		return set;
	}
	
	public HashSet<String> extractHarmClass(Connection conn){
		//Statement stmt = null;
		ResultSet rs = null;
		HashSet<String> harmClassSet = new HashSet<String>();
		try {
			stmt = conn.createStatement();
			
			System.out.println("hhhhh");
			
			rs = stmt.executeQuery("SELECT DISTINCT harmclass FROM cognie.abstracts");
			while (rs.next()) {
				harmClassSet.add(rs.getString("harmclass"));
				System.out.println(rs.getString("harmclass"));
			}
		} catch (Exception e) {
			
			System.out.println("hhhhh");
			e.printStackTrace();
		}
		return harmClassSet;
	}
	
	
	public double getTFScore(double value){
		return (0.5 + value);
	}
	
	public double getIDFScore(double value){
		return (Math.log(40.0/value));
	}

	public double fetchNoDocumentsInClass(String harmClass, Connection conn){
		//Statement stmt = null;
		ResultSet rs = null;
		double noOfDocumentsInClass = 0.0;
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery("SELECT COUNT(*) FROM cognie.abstracts WHERE harmclass='" + harmClass + "'");
			if (rs.next()) {
				noOfDocumentsInClass = (double) rs.getInt(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return noOfDocumentsInClass;
	}
	
	
	public double fetchNoDocumentsInClassWithTerm(Connection conn, String harmClass, String term){
		//Statement stmt = null;
		ResultSet rs = null;
		double noOfDocumentsInClassWithTerm = 0.0;
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery("SELECT COUNT(*) FROM cognie.abstracts WHERE content LIKE '%" + term + "%' and harmclass = '" + harmClass + "'");
			if (rs.next()) {
				noOfDocumentsInClassWithTerm = (double) rs.getInt(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return noOfDocumentsInClassWithTerm;
	}
	
	public HashMap fetchTermDistribution(Connection conn, String term){
		//Statement stmt = null;
		ResultSet rs = null;
		
		HashMap hm = new HashMap();
		double noOfDocumentsInClassWithTerm = 0.0;
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery("SELECT harmclass,COUNT(*) FROM cognie.abstracts WHERE content LIKE '%" + term + "%' group by harmclass");
			while (rs.next()) {
				noOfDocumentsInClassWithTerm = (double) rs.getInt(2);
				String hc = rs.getString(1);
				
				hm.put(hc, noOfDocumentsInClassWithTerm);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return hm;
	}
	
	
	public HashMap fetchTermDistributionLit(Connection conn, String term){
		
		ResultSet rs = null;
		
		System.out.println(K+" ; "+term);
		
		++K;
		
		HashMap hm = new HashMap();
		double noOfDocumentsInClassWithTerm = 0.0;
		try {
			
			//rs = stmt.executeQuery("SELECT litagion,COUNT(*) FROM cognie.abstracts3 WHERE content LIKE '%" + term + "%' group by litagion");
			rs = stmt.executeQuery("SELECT specharm,COUNT(*) FROM cognie.topharms2 WHERE content LIKE '%" + term + "%' group by specharm");
			
			while (rs.next()) {
				noOfDocumentsInClassWithTerm = (double) rs.getInt(2);
				String hc = rs.getString(1);
				
				hm.put(hc, noOfDocumentsInClassWithTerm);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return hm;
	}
	
	private void processDistribution(String term, HashMap hm){
		
		Collection c = hm.keySet();
		
		Iterator it = c.iterator();
		
		Double max=0.0, total=0.0;
		
		String maxclass="";
		
		String dist="";
		
		while (it.hasNext()) {
			
			String key = it.next().toString();
			
			//System.out.println("KEY "+key);
			
			Double count = (Double) hm.get(key);
			
			total = total+count;
			
			dist=dist+"("+key+":"+count+")";
			
			if (count >max) {
				max=count;
				maxclass=key;
			}
		}
		
		if (max>5) if ((max/total)>0.7) {
			
			addClass(maxclass,term);
			
			//System.out.println(term+" - "+maxclass+" : "+(max/total)+" "+dist);
		}
	}
	
	private void printFinal(){
		
		Collection c= hmInd.keySet();
		
		Iterator it = c.iterator();
		
		while (it.hasNext()) {
			
			String hClass = it.next().toString();
			
			Stack stk = (Stack)hmInd.get(hClass);
			
			String str=hClass;
			
			
			//System.out.println("\n"+hClass);
			
			//vInd.add("<Indicator>\n<HarmClass>\n"+hClass+"</HarmClass>");
			
			while (!stk.isEmpty()) {
				
				//vInd.add("<Term>"+stk.pop().toString()+"</Term>");
				
				str = str+"\t"+stk.pop().toString();
			}
			
			vInd.add(str);
		//	vInd.add("</Indicator>");
		}
		
		//vInd.add("</indicators>");
		
		io.writeFile_Basic("HarmIndicators3.xml", vInd);
		
		//IO.writeFile_Basic("resources/LitIndicators.txt", vInd);
	}
	
	public Connection connectDB(){
		Connection conn = null;
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			String connectionUrl = "jdbc:mysql://localhost:3306/cognie";
			String connectionUser = "root";
			String connectionPassword = "calcutta";
			conn = DriverManager.getConnection(connectionUrl, connectionUser, connectionPassword);
			stmt = conn.createStatement();
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		return conn;
	}
	
	public double calculateFTC(double noOfDocumentsInClassWithTerm, double noOfDocumentsInClass, String term){
		double ftcScore = (noOfDocumentsInClassWithTerm/noOfDocumentsInClass);
		return ftcScore;
	}
	
	public static void main(String[] args) {
		//new TFIDFCalculator().calculate2();
		
		TFIDFCalculatorFn tfidfCalculatorFn = new TFIDFCalculatorFn();
		tfidfCalculatorFn.calculateLit();
		
		tfidfCalculatorFn.printFinal();
	}

}
