package edu.pitt.cs.revision.machinelearning;

public class SequenceItem {
	private String label;
	private Object data;
	private double[] features;
	private int featureNum;
	
	public int getFeatureNum() {
		return featureNum;
	}
	public void setFeatureNum(int featureNum) {
		this.featureNum = featureNum;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public Object getData() {
		return data;
	}
	public void setData(Object data) {
		this.data = data;
	}
	public double[] getFeatures() {
		return features;
	}
	public void setFeatures(double[] features) {
		this.features = features;
	}
}
