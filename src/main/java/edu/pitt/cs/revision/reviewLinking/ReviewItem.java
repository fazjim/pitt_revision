package edu.pitt.cs.revision.reviewLinking;

import java.util.ArrayList;
import java.util.List;

public class ReviewItem {
	private int start;
	private int end;
	private String content;
	private List<ReviewTarget> targets;
	private List<ReviewSolution> solutions;

	public ReviewItem () {
		targets = new ArrayList<ReviewTarget>();
		solutions = new ArrayList<ReviewSolution>();
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

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public List<ReviewTarget> getTargets() {
		return targets;
	}

	public void setTargets(List<ReviewTarget> targets) {
		this.targets = targets;
	}

	public List<ReviewSolution> getSolutions() {
		return solutions;
	}

	public void setSolutions(List<ReviewSolution> solutions) {
		this.solutions = solutions;
	}
}
