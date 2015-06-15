package edu.pitt.lrdc.cs.revision.alignment.model;

import java.util.ArrayList;

public class ReviewCorpus {
	private String corpusName;
	
	public String getCorpusName() {
		return corpusName;
	}

	public void setCorpusName(String corpusName) {
		this.corpusName = corpusName;
	}

	private ArrayList<ReviewDocument> docs = new ArrayList<ReviewDocument>();

	public ArrayList<ReviewDocument> getDocs() {
		return docs;
	}

	public void setDocs(ArrayList<ReviewDocument> docs) {
		this.docs = docs;
	}
	
	public void addReviewDoc(ReviewDocument doc) throws CloneNotSupportedException {
		this.docs.add((ReviewDocument)doc.clone());
	}
	
	public ReviewDocument getReviewDoc(String authorName) {
		for(int i = 0;i<docs.size();i++) {
			if(docs.get(i).getAuthor().toLowerCase().trim().equals(authorName)) {
				return docs.get(i);
			}
		}
		return null;
	}
	
	public String toString() {
		String msg = "Corpus:"+corpusName+"\n";
		for(int i = 0;i<docs.size();i++) {
			msg += docs.get(i).toString()+"\n";
		}
		return msg;
	}
}
