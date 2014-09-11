package models;

import java.util.ArrayList;
import java.util.LinkedList;

public class FeatureVector {
	private ArrayList<FeatureValues> vector = new ArrayList<FeatureValues>();
	private String jobId;
	private String timestamp;
	private LinkedList<String> columns = new LinkedList<String>();
	
	public LinkedList<String> getColumns() {
		return columns;
	}
	public void setColumns(LinkedList<String> columns) {
		this.columns = columns;
	}
	
	public String getJobId() {
		return jobId;
	}

	public void setJobId(String jobId) {
		this.jobId = jobId;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public ArrayList<FeatureValues> getVector() {
		return vector;
	}

	public void setVector(ArrayList<FeatureValues> vector) {
		this.vector = vector;
	}
}
