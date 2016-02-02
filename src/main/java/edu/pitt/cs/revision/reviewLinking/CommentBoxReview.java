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
}
