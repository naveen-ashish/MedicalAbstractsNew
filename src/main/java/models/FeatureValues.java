package models;

import java.util.LinkedHashSet;

public class FeatureValues {
	private String abstractId;
	private String proximity;
	private String ivMention;
	private LinkedHashSet<String> values = new LinkedHashSet<String>();
	
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
	public LinkedHashSet<String> getValues() {
		return values;
	}
	public void setValues(LinkedHashSet<String> values) {
		this.values = values;
	}
}
