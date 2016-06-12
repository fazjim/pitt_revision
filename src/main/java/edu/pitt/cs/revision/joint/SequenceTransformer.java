package edu.pitt.cs.revision.joint;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Stack;

import edu.pitt.lrdc.cs.revision.model.RevisionDocument;

public class SequenceTransformer {
	public static void findSequenceSegments(RevisionDocument doc, Hashtable<Integer, HashSet<Integer>> oldParagraphBinding, Hashtable<Integer, HashSet<Integer>> newParagraphBinding) {
		int oldParagraphNum = doc.getOldParagraphNum();
		int newParagraphNum = doc.getNewParagraphNum();

		for (int i = 1; i <= oldParagraphNum; i++) {
			int oldStart = doc.getFirstOfOldParagraph(i);
			int oldEnd = doc.getLastOfOldParagraph(i);
			HashSet<Integer> newAlignedParagraphs = new HashSet<Integer>();
			for (int j = oldStart; j <= oldEnd; j++) {
				ArrayList<Integer> aligns = doc.getNewFromOld(j);
				if (aligns != null && aligns.size() > 0) {
					for (Integer align : aligns) {
						if (align != -1) {
							int newParaNo = doc.getParaNoOfNewSentence(align);
							newAlignedParagraphs.add(newParaNo);
						}
					}
				}
			}
			oldParagraphBinding.put(i, newAlignedParagraphs);
		}

		for (int i = 1; i <= newParagraphNum; i++) {
			int newStart = doc.getFirstOfNewParagraph(i);
			int newEnd = doc.getLastOfNewParagraph(i);
			HashSet<Integer> oldAlignedParagraphs = new HashSet<Integer>();
			for(int j = newStart; j<=newEnd;j++) {
				ArrayList<Integer> aligns = doc.getOldFromNew(j);
				if(aligns != null && aligns.size()>0) {
					for(Integer align: aligns) {
						if(align != -1) {
							int oldParaNo = doc.getParaNoOfOldSentence(align);
							oldAlignedParagraphs.add(oldParaNo);
						}
					}
				}
			}
			newParagraphBinding.put(i, oldAlignedParagraphs);
		}
	}

	public static EditSequence getSequence(RevisionDocument doc, int oldStart, int oldEnd, int newStart, int newEnd) {
		EditSequence sequence = new EditSequence();
		sequence.setOldParagraphStartNo(oldStart);
		sequence.setOldParagrahEndNo(oldEnd);
		sequence.setNewParagraphStartNo(newStart);
		sequence.setNewParagraphEndNo(newEnd);
		
		
		
		return sequence;
	}
	
	//There should be smarter solutions with clearer logic
	public static List<EditSequence> transformToSequence(RevisionDocument doc) {
		List<EditSequence> editSequences = new ArrayList<EditSequence>();
		int leftPointer = 1;
		int rightPointer = 1;
		int oldLength = doc.getOldDraftSentences().size();
		int newLength = doc.getNewDraftSentences().size();
		int oldParaNum = doc.getOldParagraphNum();
		int newParaNum = doc.getNewParagraphNum();
		
		Hashtable<Integer, HashSet<Integer>> oldParagraphBinding = new Hashtable<Integer, HashSet<Integer>>();
		Hashtable<Integer, HashSet<Integer>> newParagraphBinding = new Hashtable<Integer, HashSet<Integer>>();
		findSequenceSegments(doc, oldParagraphBinding, newParagraphBinding);
		
		//Hashtable<String, EditSequence> sequenceIndex = new Hashtable<String, EditSequence>();
		int oldParagraphNo = 1;
		int newParagraphNo = 1;
		while(oldParagraphNo<=oldParaNum || newParagraphNo <= newParaNum) {
			if(oldParagraphNo>oldParaNum && newParagraphNo > newParaNum) break;
			HashSet<Integer> newBindings = null;
			if(oldParagraphNo<=oldParaNum) {
				newBindings = oldParagraphBinding.get(oldParagraphNo);
			}
			HashSet<Integer> oldBindings = null;
			if(newParagraphNo<=newParaNum) {
				oldBindings = newParagraphBinding.get(newParagraphNo);
			}
			
			if(newBindings!=null && newBindings.size()==0) {
				//Deleted paragraph
				EditSequence sequence = getSequence(doc, oldParagraphNo, oldParagraphNo, -1, -1);
				editSequences.add(sequence);
				oldParagraphNo++;
			} else if(oldBindings!=null && oldBindings.size() == 0) {
				//Added paragraph
				EditSequence sequence = getSequence(doc, -1, -1, newParagraphNo, newParagraphNo);
				editSequences.add(sequence);
				newParagraphNo++;
			} else {
				if(newBindings.contains(newParagraphNo) && oldBindings.contains(oldParagraphNo)) {
					HashSet<Integer> oldVisited = new HashSet<Integer>();
					HashSet<Integer> newVisited = new HashSet<Integer>();
					Stack<Integer> oldStack = new Stack<Integer>();
					Stack<Integer> newStack = new Stack<Integer>();
					oldStack.push(oldParagraphNo);
					newStack.push(newParagraphNo);
					oldVisited.add(oldParagraphNo);
					newVisited.add(newParagraphNo);
					
					int oldStart = oldParagraphNo;
					int newStart = newParagraphNo;
					int oldEnd = oldParagraphNo;
					int newEnd = newParagraphNo;
					while(!oldStack.isEmpty()&&!newStack.isEmpty()) {
						if(!oldStack.isEmpty()) {
							int oldPara = oldStack.pop();
							oldVisited.add(oldPara);
							HashSet<Integer> alignedParas = oldParagraphBinding.get(oldPara);
							for(Integer alignedPara: alignedParas) {
								if(!newVisited.contains(alignedPara)) {
									if(alignedPara < newStart) newStart = alignedPara;
									if(alignedPara > newEnd) newEnd = alignedPara;
									newStack.push(alignedPara);
								}
							}
						}
						if(!newStack.isEmpty()) {
							int newPara = newStack.pop();
							newVisited.add(newPara);
							HashSet<Integer> alignedParas = newParagraphBinding.get(newPara);
							for(Integer alignedPara: alignedParas) {
								if(!oldVisited.contains(alignedPara)) {
									if(alignedPara < oldStart) oldStart = alignedPara;
									if(alignedPara > oldEnd) oldEnd = alignedPara;
									oldStack.push(alignedPara);
								}
							}
						}
					}
					EditSequence sequence = getSequence(doc, oldStart, oldEnd, newStart, newEnd);
					editSequences.add(sequence);
					oldParagraphNo = oldEnd + 1;
					newParagraphNo = newEnd + 1;
				} else {
					System.err.println("Something is wrong:"+oldParagraphNo + ", "+newParagraphNo);
				}
			}
		}
		
		return editSequences;
	}

	public static void transformToDocument(RevisionDocument doc,
			List<EditSequence> sequences) {

	}
}
