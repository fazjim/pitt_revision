package edu.pitt.cs.revision.reviewLinking;

public class ReviewTarget {
	private String content;
	private int start;
	private int end;
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public int getStart() {
		return start;
	}
	public void setStart(int start) {
		this.start = start;
	}
	public int getEnd() {
		return end;
	}
	public void setEnd(int end) {
		this.end = end;
	}
	
	public String toString() {
		return content;
	}
}
