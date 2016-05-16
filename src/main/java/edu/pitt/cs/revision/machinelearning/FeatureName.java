package edu.pitt.cs.revision.machinelearning;

import java.util.ArrayList;
import java.util.Hashtable;

public class FeatureName {
	public ArrayList<String> features;
	private Hashtable<String,Integer> featureIndex;
	private Hashtable<Integer,Class>  featureTypes;
	private Hashtable<Integer, ArrayList<Object>> featureNominals;
	//public ArrayList<Double> values;
	
	private ArrayList<String> category;//For classification
	
	
	public FeatureName() {
		features = new ArrayList<String>();
		featureIndex = new Hashtable<String,Integer>();
		featureTypes = new Hashtable<Integer,Class>();
		category = new ArrayList<String>();
		featureNominals = new Hashtable<Integer, ArrayList<Object>>();
	}
	
	public String getFeatureName(int featureIndex) {
		return features.get(featureIndex);
	}
	
	public void insertFeature(String featureName, Class type) {
		features.add(featureName);
		featureIndex.put(featureName, features.size()-1);
		featureTypes.put(features.size()-1, type);
	}
	
	public void insertFeature(String featureName, ArrayList<Object> nominalGroup) {
		features.add(featureName);
		featureIndex.put(featureName, features.size()-1);
		featureTypes.put(features.size()-1, ArrayList.class);
		featureNominals.put(features.size()-1, nominalGroup);
	}

	public ArrayList<Object> getNominalGroups(int index) {
		return featureNominals.get(index);
	}
	/**
	 * Get the index of features
	 * @param featureName
	 * @return
	 */
	public int getIndex(String featureName) {
		if(!featureIndex.containsKey(featureName)) System.err.println(featureName);
		if(featureIndex==null) System.err.println("WTF!!!!!");
		return featureIndex.get(featureName);
	}
	
	public Class getType(int index) {
		return featureTypes.get(index);
	}
	
	public int getSize() {
		return features.size();
	}
	
	public void addCategoryType(String cat) {
		this.category.add(cat);
	}
	
	public ArrayList<String> getCategory() {
		return this.category;
	}
	
	public int getCatIndex(String cat) {
		for(int i = 0;i<category.size();i++) {
			if(category.get(i).equals(cat)) return i;
		}
		return -1;
	}
}
