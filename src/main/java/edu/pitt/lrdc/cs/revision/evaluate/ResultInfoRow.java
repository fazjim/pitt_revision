package edu.pitt.lrdc.cs.revision.evaluate;

import java.util.Hashtable;
import java.util.Set;

public class ResultInfoRow {
	private Hashtable<String, ResultInfo> row;
	public ResultInfoRow() {
		row = new Hashtable<String, ResultInfo>();
	}
	
	/**
	 * 
	 */
	public void addExperiment(String name) {
		row.put(name, new ResultInfo());
	}
	
	public ResultInfo getResult(String name) {
		return row.get(name);
	}
	
	public Set<String> getKeys() {
		return row.keySet();
	}
	
}
