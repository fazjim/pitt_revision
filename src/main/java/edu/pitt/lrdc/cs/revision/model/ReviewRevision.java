package edu.pitt.lrdc.cs.revision.model;

import java.util.ArrayList;

public class ReviewRevision {
	private int reviewNo;
	private String reviewStr;
	private int revisionNo = -1;
	private String docName;
	private ArrayList<Integer> oldIndices;
	private ArrayList<Integer> newIndices;

	public ReviewRevision() {
		oldIndices = new ArrayList<Integer>();
		newIndices = new ArrayList<Integer>();
	}

	public int getReviewNo() {
		return reviewNo;
	}

	public void setReviewNo(int reviewNo) {
		this.reviewNo = reviewNo;
	}

	public String getReviewStr() {
		return reviewStr;
	}

	public void setReviewStr(String reviewStr) {
		this.reviewStr = reviewStr;
	}

	public int getRevisionNo() {
		return revisionNo;
	}

	public void setRevisionNo(int revisionNo) {
		this.revisionNo = revisionNo;
	}

	public String getDocName() {
		return docName;
	}

	public void setDocName(String docName) {
		this.docName = docName;
	}

	public ArrayList<Integer> getOldIndices() {
		return oldIndices;
	}

	public void setOldIndices(String oldIndiceStr) {
		if (oldIndiceStr.trim().length() > 0) {
			String[] indicesStr = oldIndiceStr.split("\t");
			ArrayList<Integer> indices = new ArrayList<Integer>();
			for (String indexStr : indicesStr) {
				indices.add(Integer.parseInt(indexStr));
			}
			setOldIndices(indices);
		}
	}

	public void setOldIndices(ArrayList<Integer> oldIndices) {
		this.oldIndices = oldIndices;
	}

	public String getOldIndiceStr() {
		String oldIndiceStr = "";
		for (Integer oldIndex : oldIndices) {
			if (oldIndex != -1) {
				oldIndiceStr += Integer.toString(oldIndex) + "\t";
			}
		}
		return oldIndiceStr.trim();
	}

	public ArrayList<Integer> getNewIndices() {
		return newIndices;
	}

	public void setNewIndices(ArrayList<Integer> newIndices) {
		this.newIndices = newIndices;
	}

	public String getNewIndiceStr() {
		String newIndiceStr = "";
		for (Integer newIndex : newIndices) {
			if (newIndex != -1) {
				newIndiceStr += Integer.toString(newIndex) + "\t";
			}
		}
		return newIndiceStr.trim();
	}

	public void setNewIndices(String newIndiceStr) {
		if (newIndiceStr.trim().length() > 0) {
			String[] indicesStr = newIndiceStr.split("\t");
			ArrayList<Integer> indices = new ArrayList<Integer>();
			for (String indexStr : indicesStr) {
				indices.add(Integer.parseInt(indexStr));
			}
			setNewIndices(indices);
		}
	}
}
