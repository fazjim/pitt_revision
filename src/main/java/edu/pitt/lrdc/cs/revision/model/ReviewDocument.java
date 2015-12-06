package edu.pitt.lrdc.cs.revision.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;

public class ReviewDocument {
	private String docName;
	private ArrayList<ReviewRevision> reviews;

	private Hashtable<Integer, String> reviewMap;
	private Hashtable<String, Integer> reverseReviewMap;

	private Hashtable<Integer, HashSet<Integer>> reviewIndexOld;
	private Hashtable<Integer, HashSet<Integer>> reviewIndexNew;

	private int cntAll = 0;
	private int implementedCnt = 0;

	public boolean isReviewMatched(ReviewRevision rr, HashSet<Integer> oldIndicesMap, HashSet<Integer> newIndicesMap) {
		ArrayList<Integer> oldIndices = rr.getOldIndices();
		ArrayList<Integer> newIndices = rr.getNewIndices();
		for(Integer oldIndex: oldIndices) {
			if(oldIndicesMap.contains(oldIndex)) {
				return true;
			}
		}
		for(Integer newIndex: newIndices) {
			if(newIndicesMap.contains(newIndex)) {
				return true;
			}
		}
		return false;
	}
	
	public void clearReviews(ArrayList<Integer> oldIndices, ArrayList<Integer> newIndices) {
		int i = 0;
		HashSet<Integer> oldIndicesMap = new HashSet<Integer>();
		HashSet<Integer> newIndicesMap = new HashSet<Integer>();
		for(Integer oldIndex: oldIndices) {
			oldIndicesMap.add(oldIndex);
		}
		for(Integer newIndex: newIndices) {
			newIndicesMap.add(newIndex);
		}
		while(i<reviews.size()) {
			ReviewRevision rr = reviews.get(i);
			if(isReviewMatched(rr,oldIndicesMap,newIndicesMap)) {
				reviews.remove(i);
				i--;
			} 
			i++;
		}
		for(Integer oldIndex: oldIndices) {
			reviewIndexOld.remove(oldIndex);
		}
		for(Integer newIndex: newIndices) {
			reviewIndexNew.remove(newIndex);
		}
	}
	
	public ArrayList<String> getReviewStrs() {
		ArrayList<String> reviewStrs = new ArrayList<String>();
		Iterator<String> it = reverseReviewMap.keySet().iterator();
		while (it.hasNext()) {
			reviewStrs.add(it.next());
		}
		return reviewStrs;
	}

	public HashSet<String> getReviewStrs(ArrayList<Integer> oldIndices,
			ArrayList<Integer> newIndices) {
		HashSet<Integer> reviews = new HashSet<Integer>();
		for (Integer oldIndex : oldIndices) {
			HashSet<Integer> reviewNoOlds = reviewIndexOld.get(oldIndex);
			if (reviewNoOlds != null) {
				for (Integer reviewNo : reviewNoOlds) {
					reviews.add(reviewNo);
				}
			}
		}
		for (Integer newIndex : newIndices) {
			HashSet<Integer> reviewNoNews = reviewIndexNew.get(newIndex);
			if (reviewNoNews != null) {
				for (Integer reviewNo : reviewNoNews) {
					reviews.add(reviewNo);
				}
			}
		}

		HashSet<String> reviewStrs = new HashSet<String>();
		for (Integer reviewNo : reviews) {
			reviewStrs.add(reviewMap.get(reviewNo));
		}
		return reviewStrs;
	}

	public void addImplemented() {
		implementedCnt++;
	}

	public void addAllCount() {
		cntAll++;
	}

	public int getAllCnt() {
		return cntAll;
	}

	public int getImplementedCnt() {
		return implementedCnt;
	}

	public String getDocName() {
		return docName;
	}

	public void setDocName(String docName) {
		this.docName = docName;
	}

	public ReviewDocument() {
		reviews = new ArrayList<ReviewRevision>();
		reviewMap = new Hashtable<Integer, String>();
		reverseReviewMap = new Hashtable<String, Integer>();
		reviewIndexOld = new Hashtable<Integer, HashSet<Integer>>();
		reviewIndexNew = new Hashtable<Integer, HashSet<Integer>>();
	}

	public String getReviewContent(int reviewNo) {
		return reviewMap.get(reviewNo);
	}

	public int getReviewNo(String reviewStr) {
		return reverseReviewMap.get(reviewStr);
	}

	public void addReviewStr(int id, String reviewStr) {
		if (!reviewMap.containsKey(id))
			reviewMap.put(id, reviewStr);
		if (!reverseReviewMap.containsKey(reviewStr))
			reverseReviewMap.put(reviewStr, id);
	}

	public void addReview(int revisionNo, int reviewNo, String reviewStr,
			ArrayList<Integer> oldIndices, ArrayList<Integer> newIndices) {
		ReviewRevision rr = new ReviewRevision();
		rr.setDocName(this.docName);
		rr.setNewIndices(newIndices);
		rr.setOldIndices(oldIndices);
		rr.setReviewNo(reviewNo);
		rr.setReviewStr(reviewStr);
		rr.setRevisionNo(revisionNo);
		addReview(rr);
	}

	public void addReview(ReviewRevision review) {
		this.reviews.add(review);
		addReviewStr(review.getReviewNo(), review.getReviewStr());
		ArrayList<Integer> oldIndices = review.getOldIndices();
		ArrayList<Integer> newIndices = review.getNewIndices();
		for (Integer oldIndex : oldIndices) {
			HashSet<Integer> oldSet;
			if (!reviewIndexOld.containsKey(oldIndex)) {
				oldSet = new HashSet<Integer>();
				reviewIndexOld.put(oldIndex, oldSet);
			}
			oldSet = reviewIndexOld.get(oldIndex);
			oldSet.add(review.getReviewNo());
		}
		for (Integer newIndex : newIndices) {
			HashSet<Integer> newSet;
			if (!reviewIndexNew.containsKey(newIndex)) {
				newSet = new HashSet<Integer>();
				reviewIndexNew.put(newIndex, newSet);
			}
			newSet = reviewIndexNew.get(newIndex);
			newSet.add(review.getReviewNo());
		}
	}

	public HashSet<Integer> getReviewsOld(Integer oldIndex) {
		return reviewIndexOld.get(oldIndex);
	}

	public HashSet<Integer> getReviewsNew(Integer newIndex) {
		return reviewIndexNew.get(newIndex);
	}

	public ArrayList<ReviewRevision> getReviews() {
		return reviews;
	}

	public void setReviews(ArrayList<ReviewRevision> reviews) {
		this.reviews = reviews;
	}
}
