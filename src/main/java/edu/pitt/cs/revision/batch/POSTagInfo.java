package edu.pitt.cs.revision.batch;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.StringTokenizer;

/**
 * Store all the info parsed by POS Tagger
 * @author zhangfan
 *
 */
public class POSTagInfo {
	private int JJCount;
	private int NNCount;
	private int RBCount;
	private int VBCount;
	
	private int totalCount;
	
	private int PERSONCount;
	private int LOCATIONCount;
	
	public int getJJCount() {
		return JJCount;
	}
	public void setJJCount(int jJCount) {
		JJCount = jJCount;
	}
	public void addJJ() {
		this.JJCount+=1;
	}
	
	public int getNNCount() {
		return NNCount;
	}
	public void setNNCount(int nNCount) {
		NNCount = nNCount;
	}
	public void addNN() {
		this.NNCount+=1;
	}
	
	public int getRBCount() {
		return RBCount;
	}
	public void setRBCount(int rBCount) {
		RBCount = rBCount;
	}
	public void addRB() {
		this.RBCount+=1;
	}
	
	public int getVBCount() {
		return VBCount;
	}
	public void setVBCount(int vBCount) {
		VBCount = vBCount;
	}
	public void addVB() {
		this.VBCount+=1;
	}
	
	public int getPERSONCount() {
		return PERSONCount;
	}
	public void setPERSONCount(int pERSONCount) {
		PERSONCount = pERSONCount;
	}
	public void addPERSON() {
		this.PERSONCount+=1;
	}
	
	public int getLOCATIONCount() {
		return LOCATIONCount;
	}
	public void setLOCATIONCount(int lOCATIONCount) {
		LOCATIONCount = lOCATIONCount;
	}
	public void addLOCATION() {
		this.LOCATIONCount+=1;
	}
	
	public int getTotalCount() {
		return totalCount;
	}
	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}
	public void addTotal() {
		this.totalCount+=1;
	}
	
	public void toFile(String path) throws IOException {
		File file = new File(path);
		if(!file.getParentFile().exists()) file.getParentFile().mkdirs();
		BufferedWriter writer = new BufferedWriter(new FileWriter(path));
		String sent = "";
		sent += "JJ:"+this.JJCount;
		sent += "\t" + "NN:"+this.NNCount;
		sent += "\t" + "RB:"+this.RBCount;
		sent += "\t" + "VB:"+this.VBCount;
		sent += "\t" + "TOTAL:"+ this.totalCount;
		sent += "\t" + "PERSON:"+this.PERSONCount;
		sent += "\t" + "LOCATION:"+this.PERSONCount;
		writer.write(sent);
		writer.close();
	}
	
	public void fromFile(String file) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(file));
		String sent = reader.readLine();
		StringTokenizer stk = new StringTokenizer(sent, "\t");
		while(stk.hasMoreTokens()) {
			String tag = stk.nextToken();
			if(tag.startsWith("JJ:")) {
				int val = Integer.parseInt(tag.substring(tag.lastIndexOf(":")+1));
				this.setJJCount(val);
			} else if(tag.startsWith("NN:")) {
				int val = Integer.parseInt(tag.substring(tag.lastIndexOf(":")+1));
				this.setNNCount(val);
			} else if(tag.startsWith("RB:")) {
				int val = Integer.parseInt(tag.substring(tag.lastIndexOf(":")+1));
				this.setRBCount(val);
			} else if(tag.startsWith("VB:")) {
				int val = Integer.parseInt(tag.substring(tag.lastIndexOf(":")+1));
				this.setVBCount(val);
			} else if(tag.startsWith("TOTAL:")) {
				int val = Integer.parseInt(tag.substring(tag.lastIndexOf(":")+1));
				this.setTotalCount(val);
			} else if(tag.startsWith("PERSON:")) {
				int val = Integer.parseInt(tag.substring(tag.lastIndexOf(":")+1));
				this.setPERSONCount(val);
			} else if(tag.startsWith("LOCATION:")) {
				int val = Integer.parseInt(tag.substring(tag.lastIndexOf(":")+1));
				this.setLOCATIONCount(val);
			}
		}
		reader.close();
	}
}
