package edu.pitt.lrdc.cs.revision.alignment.model;

public class Review implements Cloneable{
	private String documentName;
	private String author;
	private String reviewer;
	private String shortCommentName;
	private String comment;
	private String backEvalComment;
	private int backEvalScore;
	public String getDocumentName() {
		return documentName;
	}
	public void setDocumentName(String documentName) {
		this.documentName = documentName;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public String getReviewer() {
		return reviewer;
	}
	public void setReviewer(String reviewer) {
		this.reviewer = reviewer;
	}
	public String getShortCommentName() {
		return shortCommentName;
	}
	public void setShortCommentName(String shortCommentName) {
		this.shortCommentName = shortCommentName;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public String getBackEvalComment() {
		return backEvalComment;
	}
	public void setBackEvalComment(String backEvalComment) {
		this.backEvalComment = backEvalComment;
	}
	public int getBackEvalScore() {
		return backEvalScore;
	}
	public void setBackEvalScore(int backEvalScore) {
		this.backEvalScore = backEvalScore;
	}
	
	public String toString() {
		return "comment Name:"+this.shortCommentName+",comment:"+this.comment+",backEvalScore:"+this.backEvalScore;
	}
	
	public Object clone() throws CloneNotSupportedException{
		return super.clone();
	}
}
