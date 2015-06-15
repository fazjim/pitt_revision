package edu.pitt.lrdc.cs.revision.alignment.model;

/**
 * The data structure used for alignment:  Document, Sentence, DocumentPair
 */
import java.util.ArrayList;

public class Sentence {
	private String content;
	private int index;
	private ArrayList<Integer> alignedIndex;
	private ArrayList<Integer> predictedIndex;
	
	
	public ArrayList<Integer> getPredictedIndex() {
		return predictedIndex;
	}
	public void setPredictedIndex(ArrayList<Integer> predictedIndex) {
		this.predictedIndex = predictedIndex;
	}
	public ArrayList<Integer> getAlignedIndex() {
		return alignedIndex;
	}
	public void setAlignedIndex(ArrayList<Integer> alignedIndex) {
		this.alignedIndex = alignedIndex;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
}
