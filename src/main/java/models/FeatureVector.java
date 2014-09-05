package models;

import java.util.ArrayList;

public class FeatureVector {
	private ArrayList<FeatureValues> vector = new ArrayList<FeatureValues>();

	public ArrayList<FeatureValues> getVector() {
		return vector;
	}

	public void setVector(ArrayList<FeatureValues> vector) {
		this.vector = vector;
	}
}
