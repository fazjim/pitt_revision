package edu.pitt.lrdc.cs.revision.alignment.model;

import java.util.ArrayList;

public class Reviewer implements Cloneable{
	private String reviewerName;
	private ArrayList<Review> reviews = new ArrayList<Review>();
	public String getReviewerName() {
		return reviewerName;
	}
	public void setReviewerName(String reviewerName) {
		this.reviewerName = reviewerName;
	}
	public ArrayList<Review> getReviews() {
		return reviews;
	}
	public void setReviews(ArrayList<Review> reviews) {
		this.reviews = reviews;
	}
	
	public void addReview(Review rev) throws CloneNotSupportedException {
		this.reviews.add((Review)rev.clone());
	}
	
	public Object clone() throws CloneNotSupportedException{
		Reviewer rv = (Reviewer)super.clone();
		ArrayList<Review> newCopy = new ArrayList<Review>();
		for(int i = 0;i<this.getReviews().size();i++) {
			Review tmp = (Review)(this.getReviews().get(i).clone());
			newCopy.add(tmp);
		}
		rv.setReviews(newCopy);
		return rv;
	}
	public String toString() {
		String msg = "Reviewer:"+reviewerName+"\n";
		for(int i = 0;i<reviews.size();i++) {
			msg += reviews.get(i).toString()+"\n";
		}
		return msg;
	}
}
