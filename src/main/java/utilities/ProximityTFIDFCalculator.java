package utilities;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Stack;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import edu.stanford.nlp.tagger.maxent.MaxentTagger;


public class ProximityTFIDFCalculator {
	private HashMap<String, Stack<String>> hmInd;
	private Vector<String> vInd;
	private IO io = new IO();
	
	/*
	 *************************************************************
	 * Main Methods to execute the program
	 *************************************************************
	 */
	
	public void executeCalculate(){
		Connection conn = mysqlConnect();
		HashSet<String> litagionSet = extractNouns(conn);
		hmInd = new HashMap<String, Stack<String>>();
		vInd = new Vector<String>();
		calculate(conn, litagionSet);
		printFinal("results-proximity");
		hmInd = new HashMap<String, Stack<String>>();
		vInd = new Vector<String>();
		calculateLit(conn, litagionSet);
		printFinal("results-litagion");
		
	}
	
	
	/*
	 *************************************************************
	 * Methods for computing TF-IDF for proximity and litagion
	 *************************************************************
	 */
	public void calculate(Connection conn, HashSet<String> litagionSet){
		try {
			System.out.println("--> Nouns Extracted !!");
				for(String litagionTerm : litagionSet){
					if (litagionTerm.length() > 2){
					HashMap<String, Double> hm = fetchTermDistribution(conn,litagionTerm);
					processDistribution(litagionTerm,hm);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void calculateLit(Connection conn, HashSet<String> litagionSet){
		try {
				for(String litagionTerm : litagionSet){
					if (litagionTerm.length() > 1){
					HashMap<String, Double> hm = fetchTermDistributionLit(conn,litagionTerm);
					processDistribution(litagionTerm,hm);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/*
	 *************************************************************
	 * Methods to Calculate TF-IDF
	 *************************************************************
	 */
	
	public HashSet<String> extractNouns(Connection conn){
		HashSet<String> litagionList = new HashSet<String>();
		try{
			FileReader fr = new FileReader("resources/proximityNouns.txt");
			BufferedReader bf = new BufferedReader(fr);
			String line = "";
			while((line = bf.readLine()) != null){
				litagionList.add(line.trim());
			}
		} catch (IOException e){
			e.printStackTrace();
		}
		return litagionList;
	}
	
	public HashMap<String, Double> fetchTermDistribution(Connection conn, String term){
		HashMap<String, Double> hm = new HashMap<String, Double>();
		double noOfDocumentsInClassWithTerm = 0.0;
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT proximity, COUNT(*) FROM abstract WHERE content LIKE '%" + term + "%' group by proximity");
			while (rs.next()) {
				noOfDocumentsInClassWithTerm = (double) rs.getInt(2);
				String hc = rs.getString(1);
				System.out.println(hc + " --> " + noOfDocumentsInClassWithTerm);
				hm.put(hc, noOfDocumentsInClassWithTerm);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return hm;
	}
	
	public HashMap<String, Double> fetchTermDistributionLit(Connection conn, String term){
		HashMap<String, Double> hm = new HashMap<String, Double>();
		double noOfDocumentsInClassWithTerm = 0.0;
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT litagion,COUNT(*) FROM abstract WHERE content LIKE '%" + term + "%' group by litagion");
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
	
	private void processDistribution(String term, HashMap<String, Double> hm){
		Collection c = hm.keySet();
		Iterator it = c.iterator();
		Double max=0.0, total=0.0;
		String maxclass="";
		String dist="";
		while (it.hasNext()) {
			String key = it.next().toString();
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
		}
	}
	
	private void addClass(String hClass,String term){
		if (hmInd.get(hClass)==null) {
			Stack<String> stk = new Stack<String>();
			stk.push(term);
			hmInd.put(hClass, stk);
		} else {
			Stack<String> stk=(Stack<String>) hmInd.get(hClass);
			Stack<String> stk2=(Stack<String>) stk.clone();
			stk2.push(term);
			hmInd.put(hClass,stk2);
		}
	}
	
	private void printFinal(String fileName){
		Collection c = hmInd.keySet();
		Iterator it = c.iterator();
		while (it.hasNext()) {
			String hClass = it.next().toString();
			Stack<String> stk = (Stack<String>)hmInd.get(hClass);
			String str=hClass;
			while (!stk.isEmpty()) {
				str = str+"\t"+stk.pop().toString();
			}
			vInd.add(str);
			System.out.println(vInd.size());
		}
		io.writeFile_Basic("resources/" + fileName + ".txt", vInd);
	}
	
	
	/*
	 *************************************************************
	 * Methods to Connect DB and Load Taggers
	 *************************************************************
	 */
	public MaxentTagger loadMaxentTagger(){
		MaxentTagger tagger = null;
		try {
			tagger = new MaxentTagger("resources/left3words-wsj-0-18.tagger");
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return tagger;
	}
	private Connection mysqlConnect(){
		try {
			Class.forName("com.mysql.jdbc.Driver");
			return DriverManager.getConnection("jdbc:mysql://localhost:3306/TFIDFStore","root", "root");
		} catch (SQLException e) {
			e.printStackTrace();
		}catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	/*
	 *************************************************************
	 * Methods for Loading data into Database and creating Nouns list
	 *************************************************************
	 */
	public void addProximityAndPubmedInfoToDatabase(String pathToXmlAbstracts){
		try{
			MaxentTagger tagger = loadMaxentTagger();
			HashSet<String> nounsSet = new HashSet<String>();
			ArrayList<String> pubmedIDList = new ArrayList<String>();
			HashMap<String, String> map = new HashMap<String, String>();
			Connection conn = mysqlConnect();
			FileReader fr = new FileReader(new File("resources/trainingData_naveenProject1.csv"));
			BufferedReader bf = new BufferedReader(fr);
			String line = "";
			while((line = bf.readLine()) != null){
				String[] splitter = line.split(",");
				if(splitter.length == 6 && !splitter[4].equals("NULL")){
					String pubmedID = splitter[0].trim();
					String litagion = splitter[1].trim();
					String proximity = splitter[4].trim();
					String pubmedAbstract = null;
					if(!pubmedIDList.contains(pubmedID)){
						pubmedAbstract = fetchXMLAbtract(pathToXmlAbstracts,pubmedID);
						if(pubmedAbstract != null){
							executeInsert(conn, pubmedAbstract, proximity, pubmedID, litagion);
							nounsSet.addAll(fetchPOSTags(pubmedAbstract, tagger));
							pubmedIDList.add(pubmedID);
						}
					}
				}
			}
			conn.close();
			writeNounsList(nounsSet);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public String fetchXMLAbtract(String pathToXmlAbstracts,String id){
		try{
			File fXmlFile = new File(pathToXmlAbstracts+"/"+ id + ".xml");
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
			
			doc.getDocumentElement().normalize();
			
			NodeList nList = doc.getElementsByTagName("Abstract");
			
			for (int temp = 0; temp < nList.getLength(); temp++) {
				Node nNode = nList.item(temp);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;
					return eElement.getElementsByTagName("AbstractText").item(0).getTextContent().toString().trim();
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	public void executeInsert(Connection conn, String abstractText, String proximity, String id, String litagion){
		try{
			Statement st = (Statement) conn.createStatement();
			st.executeUpdate("INSERT INTO abstract (id, litagion, proximity, content) VALUES (" + id + ",\"" + litagion + "\", " + proximity + ", \"" + abstractText + "\")");
			System.out.println("--> " + id + " has been entered!");
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public HashSet<String> fetchPOSTags(String text, MaxentTagger tagger){
		HashSet<String> set = new HashSet<String>();
		String tagged = tagger.tagString(text);
		String[] splitter = tagged.split(" ");
		for(String term : splitter){
			if(term.contains("/NN")){
				set.add(term.substring(0, term.indexOf("/")));
			}
		}
		return set;
	}
	
	public void writeNounsList(HashSet<String> nounsSet){
		try {
			FileWriter fw = new FileWriter("resources/proximityNouns.txt");
			BufferedWriter bw = new BufferedWriter(fw);
			for(String noun : nounsSet){
				bw.write(noun + "\n");
				bw.flush();
			}
			bw.close();
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		new ProximityTFIDFCalculator().executeCalculate();
	}

}
