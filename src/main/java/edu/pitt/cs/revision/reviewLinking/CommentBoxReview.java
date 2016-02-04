package edu.pitt.cs.revision.reviewLinking;

import java.util.ArrayList;
import java.util.List;

public class CommentBoxReview {
	private String content;
	private List<ReviewItem> reviews;
	
	public CommentBoxReview() {
		reviews = new ArrayList<ReviewItem>();
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public List<ReviewItem> getReviews() {
		return reviews;
	}

	public void setReviews(List<ReviewItem> reviews) {
		this.reviews = reviews;
	}
	
	public void addReview(ReviewItem item) {
		this.reviews.add(item);
	}
	
	public ReviewItem getReview(int startLoc, int endLoc) {
		for(int i = 0;i<reviews.size();i++) {
			ReviewItem item = reviews.get(i);
			if(startLoc>=item.getStart()&& endLoc<=item.getEnd()) {
				return item;
			}
		}
		
		System.out.println(startLoc);
		System.out.println(endLoc);
		return null;
	}
	
	
	public String toString() {
		String str = content+"\n";
		for(ReviewItem item: reviews) {
			str+= item.toString()+"\n";
		}
		return str;
	}
}
