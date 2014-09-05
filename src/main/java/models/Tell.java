package models;

import java.util.ArrayList;

public class Tell {
	private boolean multualExclusionAcrossPasses;
	private ArrayList<TellDetail> tellSentences = new ArrayList<TellDetail>();
	
	public boolean isMultualExclusionAcrossPasses() {
		return multualExclusionAcrossPasses;
	}
	public void setMultualExclusionAcrossPasses(boolean multualExclusionAcrossPasses) {
		this.multualExclusionAcrossPasses = multualExclusionAcrossPasses;
	}
	public ArrayList<TellDetail> getTellSentences() {
		return tellSentences;
	}
	public void setTellSentences(ArrayList<TellDetail> tellSentences) {
		this.tellSentences = tellSentences;
	}
}
