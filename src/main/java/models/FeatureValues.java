package models;

import java.util.LinkedList;

public class FeatureValues {
	private String abstractId;
	private String proximity;
	private String ivMention;
	private LinkedList<String> values = new LinkedList<String>();
	
	public String getProximity() {
		return proximity;
	}
	public void setProximity(String proximity) {
		this.proximity = proximity;
	}
	public String getIvMention() {
		return ivMention;
	}
	public void setIvMention(String ivMention) {
		this.ivMention = ivMention;
	}
	public String getAbstractId() {
		return abstractId;
	}
	public void setAbstractId(String abstractId) {
		this.abstractId = abstractId;
	}
	public LinkedList<String> getValues() {
		return values;
	}
	public void setValues(LinkedList<String> values) {
		this.values = values;
	}
}
