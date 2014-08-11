package utilities;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.Stack;
import java.util.Vector;


public class IO {
	
	public Stack readFileStk(String fname) {
		
		  Stack stk = new Stack();
		
		  try{

			    FileInputStream fstream = new FileInputStream(fname);
			    // Get the object of DataInputStream
			    DataInputStream in = new DataInputStream(fstream);
			        BufferedReader br = new BufferedReader(new InputStreamReader(in));
			    String strLine="";
			    //Read File Line By Line
			    while ((strLine = br.readLine()) != null)   {
			    	
			    	stk.push(strLine.trim());

			    }
			    //Close the input stream
			    in.close();
			    }catch (Exception e){//Catch exception if any
			      System.err.println("Error: " + e.getMessage());
			    }
			    
			    return stk;
	}
	
	public void writeFile_Basic(String fname,Vector<String> trace) {
		
		int V=trace.size();
		
		 try{
		     // Create file 
		     FileWriter fstream = new FileWriter(fname);
		        
		     BufferedWriter out = new BufferedWriter(fstream);
		     
		     for (int v=0; v<V; ++v) out.write(trace.elementAt(v).toString()+"\n");

		     //Close the output stream
		     out.close();
		     }catch (Exception e){//Catch exception if any
		       System.err.println("Error: " + e.getMessage());
		     }
		 
	}
	
public void writeFile_BasicAppend(String fname,Vector<String> trace) {
		
		int V=trace.size();
		
		 try{
		     // Create file 
		     FileWriter fstream = new FileWriter(fname,true);
		        
		     BufferedWriter out = new BufferedWriter(fstream);
		     
		     
		     for (int v=0; v<V; ++v) out.append(trace.elementAt(v).toString()+"\n");

		     //Close the output stream
		     out.close();
		     }catch (Exception e){//Catch exception if any
		       System.err.println("Error: " + e.getMessage());
		     }
		 
	}
	
}