package edu.pitt.cs.revision.reviewLinking;

import java.util.ArrayList;
import java.util.List;

public class ReviewItem {
	private int start;
	private int end;
	private String content;
	private List<ReviewTarget> targets;
	private List<ReviewSolution> solutions;
	private String type;
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

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
	
	public void addTarget(ReviewTarget target) {
		this.targets.add(target);
	}
	
	public void addSolution(ReviewSolution solution) {
		this.solutions.add(solution);
	}
	
	public String toString() {
		String str = "Review Content:"+this.content+"\n";
		for(ReviewTarget target: targets) {
			str += "Review Target:"+target.toString()+"\n";
		}
		for(ReviewSolution solution: solutions) {
			str+= "Review Solution:"+solution.toString()+"\n";
		}
		return str;
	}
}
