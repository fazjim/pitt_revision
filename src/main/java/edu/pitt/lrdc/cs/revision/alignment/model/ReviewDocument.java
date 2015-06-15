package edu.pitt.lrdc.cs.revision.alignment.model;

import java.util.ArrayList;

public class ReviewDocument implements Cloneable{
	private String author;
	private ArrayList<Reviewer> reviewers = new ArrayList<Reviewer>();
	
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public ArrayList<Reviewer> getReviewers() {
		return reviewers;
	}
	public void setReviewers(ArrayList<Reviewer> reviewers) {
		this.reviewers = reviewers;
	}
	
	public void addReviewer(Reviewer reviewer) throws CloneNotSupportedException {
		this.reviewers.add((Reviewer)reviewer.clone());
	}
	
	public Object clone() throws CloneNotSupportedException{
		ReviewDocument doc = (ReviewDocument)super.clone();
		ArrayList<Reviewer> revs = new ArrayList<Reviewer>();
		for(int i = 0;i<reviewers.size();i++) {
			Reviewer rev = (Reviewer)reviewers.get(i).clone();
			revs.add(rev);
		}
		doc.setReviewers(revs);
		return doc;
	}
	
	public String toString() {
		String msg = "Author:"+author+"\n";
		for(int i = 0;i<reviewers.size();i++) {
			msg += reviewers.get(i).toString()+"\n";
		}
		return msg;
	}
}
