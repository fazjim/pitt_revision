package edu.pitt.cs.revision.joint;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import edu.pitt.lrdc.cs.revision.evaluate.RevisionDocumentComparer;
import edu.pitt.lrdc.cs.revision.io.RevisionDocumentReader;
import edu.pitt.lrdc.cs.revision.model.RevisionDocument;
import edu.pitt.lrdc.cs.revision.model.RevisionOp;
import edu.pitt.lrdc.cs.revision.model.RevisionPurpose;
import edu.pitt.lrdc.cs.revision.model.RevisionUnit;

public class SequenceTransformer {
	public static void findSequenceSegments(RevisionDocument doc,
			Hashtable<Integer, HashSet<Integer>> oldParagraphBinding,
			Hashtable<Integer, HashSet<Integer>> newParagraphBinding) {
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
			for (int j = newStart; j <= newEnd; j++) {
				ArrayList<Integer> aligns = doc.getOldFromNew(j);
				if (aligns != null && aligns.size() > 0) {
					for (Integer align : aligns) {
						if (align != -1) {
							int oldParaNo = doc.getParaNoOfOldSentence(align);
							oldAlignedParagraphs.add(oldParaNo);
						}
					}
				}
			}
			newParagraphBinding.put(i, oldAlignedParagraphs);
		}
	}

	/**
	 * Generate all possible cases with beam-search
	 * @param doc
	 * @param oldStart
	 * @param oldEnd
	 * @param newStart
	 * @param newEnd
	 * @return
	 */
	public static List<EditSequence> generateAllPossibleSequences(
			RevisionDocument doc, int oldStart, int oldEnd, int newStart,
			int newEnd) {
		List<EditSequence> sequences = new ArrayList<EditSequence>();

		return sequences;
	}

	public static EditSequence getSequence(RevisionDocument doc, int oldStart,
			int oldEnd, int newStart, int newEnd, int extraEnd) {
		//System.err.println(oldStart + "," + oldEnd + "," + newStart + ","
		//		+ newEnd + "," + extraEnd);
		EditSequence sequence = new EditSequence();
		sequence.setOldParagraphStartNo(oldStart);
		sequence.setOldParagraphEndNo(oldEnd);
		sequence.setNewParagraphStartNo(newStart);
		sequence.setNewParagraphEndNo(newEnd);

		int oldCursor = -1;
		int newCursor = -1;
		int oldCursorStart = -1;
		int oldCursorEnd = -1;
		int newCursorStart = -1;
		int newCursorEnd = -1;
		if (oldStart != -1) {
			oldCursorStart = doc.getFirstOfOldParagraph(oldStart);
			oldCursorEnd = doc.getLastOfOldParagraph(oldEnd);
			oldCursor = oldCursorStart;
		}
		if (newStart != -1) {
			newCursorStart = doc.getFirstOfNewParagraph(newStart);
			newCursorEnd = doc.getLastOfNewParagraph(newEnd);
			newCursor = newCursorStart;
		}

		while ((oldCursor <= oldCursorEnd && oldCursor != -1)
				|| (newCursor <= newCursorEnd && newCursor != -1)) {
			// System.err.println(oldCursor + "," + newCursor);
			ArrayList<Integer> newAligned = null;
			ArrayList<Integer> oldAligned = null;
			// System.out.println(oldCursor);
			// System.out.println(newCursor);

			if (oldCursor <= oldCursorEnd && oldCursor != -1)
				newAligned = doc.getNewFromOld(oldCursor);
			if (newCursor <= newCursorEnd && newCursor != -1)
				oldAligned = doc.getOldFromNew(newCursor);

			/*
			 * String str = ""; if(newAligned!=null) { for(Integer i:
			 * newAligned) str += i +",";
			 * System.out.println("OLD aligned to:"+str); }
			 * 
			 * String str2 = ""; if(oldAligned !=null) { for(Integer i :
			 * oldAligned) str += i + ",";
			 * System.out.println("NEW aligned to:"+str); }
			 */

			if (newAligned != null
					&& (newAligned.size() == 0
							|| (newAligned.size() == 1 && newAligned.get(0) == -1) || (newStart == -1 && newEnd == -1))) {
				// delete a sentence
				int revPurpose = doc.getPurposeofOld(oldCursor);
				int extraInfo = newCursor;
				if (newStart == -1 && newEnd == -1 && newCursor <= newCursorEnd) {
					extraInfo = doc.getLastOfNewParagraph(extraEnd);
				}
				if (extraInfo < 0)
					extraInfo = 1;
				sequence.addDelete(oldCursor, extraInfo, revPurpose);
				oldCursor++;
			} else if (oldAligned != null
					&& (oldAligned.size() == 0
							|| (oldAligned.size() == 1 && oldAligned.get(0) == -1) || (oldStart == -1 && oldEnd == -1))) {
				// add a sentence
				int revPurpose = doc.getPurposeofNew(newCursor);

				int extraInfo = oldCursor;
				if (oldStart == -1 && oldEnd == -1 && oldCursor <= oldCursorEnd) {
					extraInfo = doc.getLastOfOldParagraph(extraEnd);
				}
				// if(oldCursor<0) return null;
				if (extraInfo < 0)
					extraInfo = 1;
				sequence.addAdd(extraInfo, newCursor, revPurpose);
				newCursor++;
			} else {
				HashSet<Integer> oldSet = new HashSet<Integer>(); // aligned
																	// olds from
																	// new
				HashSet<Integer> newSet = new HashSet<Integer>(); // aligned
																	// news from
																	// old
				if (newAligned != null) {
					for (Integer i : newAligned) {
						if (i != -1)
							newSet.add(i);
					}
				}
				if (oldAligned != null) {
					for (Integer i : oldAligned) {
						if (i != -1)
							oldSet.add(i);
					}
				}
				if (oldSet.contains(oldCursor) && newSet.contains(newCursor)) {
					// when the two are linking to each other
					int oldLength = oldSet.size();
					int newLength = newSet.size();
					while (oldLength > 0 && newLength > 0) {
						sequence.addModify(oldCursor, newCursor,
								doc.getPurposeofOld(oldCursor));
						oldCursor++;
						newCursor++;
						oldLength--;
						newLength--;
					}
					while (oldLength > 0 && oldSet.contains(oldCursor)) {
						sequence.addDelete(oldCursor, newCursor,
								doc.getPurposeofOld(oldCursor));
						oldCursor++;
						oldLength--;
					}
					while (newLength > 0 && newSet.contains(newCursor)) {
						sequence.addAdd(oldCursor, newCursor,
								doc.getPurposeofNew(newCursor));
						newCursor++;
						newLength--;
					}
				} else {
					// cross reference
					int oldLength = oldSet.size();
					int newLength = newSet.size();

					boolean shouldMoveOld = true;
					boolean shouldMoveNew = true;
					for (Integer oldIndex : oldSet) {
						if (oldIndex > oldCursor)
							shouldMoveNew = false; // for the alignments of new,
													// if new is larger than
													// old, then no need to move
													// new;
					}
					for (Integer newIndex : newSet) {
						if (newIndex > newCursor)
							shouldMoveOld = false; // same to above
					}

					if (!shouldMoveOld && !shouldMoveNew) {
						while (oldLength > 0 && oldCursor <= oldCursorEnd) {
							sequence.addDelete(oldCursor, newCursor,
									doc.getPurposeofOld(oldCursor));
							oldCursor++;
							oldLength--;
						}
						while (newLength > 0 && newCursor <= newCursorEnd) {
							sequence.addAdd(oldCursor, newCursor,
									doc.getPurposeofNew(newCursor));
							newCursor++;
							newLength--;
						}
					} else {
						if (shouldMoveOld) {
							if (oldLength == 0 && oldCursor <= oldCursorEnd) {
								sequence.addDelete(oldCursor, newCursor,
										doc.getPurposeofOld(oldCursor));
								oldCursor++;
								oldLength--;
							} else {
								while (oldLength > 0
										&& oldCursor <= oldCursorEnd) {
									sequence.addDelete(oldCursor, newCursor,
											doc.getPurposeofOld(oldCursor));
									oldCursor++;
									oldLength--;
								}
							}
						}
						if (shouldMoveNew) {
							if (newLength == 0 && newCursor <= newCursorEnd) {
								sequence.addAdd(oldCursor, newCursor,
										doc.getPurposeofNew(newCursor));
								newCursor++;
								newLength--;
							} else {
								while (newLength > 0
										&& newCursor <= newCursorEnd) {
									sequence.addAdd(oldCursor, newCursor,
											doc.getPurposeofNew(newCursor));
									newCursor++;
									newLength--;
								}
							}
						}
					}
				}
			}
		}

		return sequence;
	}

	public static List<List<EditSequence>> tranformToAllPossibleSequences(RevisionDocument doc) {
		List<List<EditSequence>> editSequences = new ArrayList<List<EditSequence>>();
		return editSequences;
	}
	
	/**
	 * Transforming an aligned document to a series of edit sequences
	 * 
	 * @param doc
	 * @return
	 */
	// There should be smarter solutions with clearer logic
	public static List<EditSequence> transformToSequence(RevisionDocument doc) {
		System.err.println(doc.getDocumentName());
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

		// Hashtable<String, EditSequence> sequenceIndex = new Hashtable<String,
		// EditSequence>();
		int oldParagraphNo = 1;
		int newParagraphNo = 1;

		while (oldParagraphNo <= oldParaNum || newParagraphNo <= newParaNum) {
			// System.err.println(oldParagraphNo+","+newParagraphNo);
			if (oldParagraphNo > oldParaNum && newParagraphNo > newParaNum)
				break;
			HashSet<Integer> newBindings = null;
			if (oldParagraphNo <= oldParaNum) {
				newBindings = oldParagraphBinding.get(oldParagraphNo);
			}
			HashSet<Integer> oldBindings = null;
			if (newParagraphNo <= newParaNum) {
				oldBindings = newParagraphBinding.get(newParagraphNo);
			}

			if (newBindings != null && newBindings.size() == 0
					|| newParagraphNo > newParaNum) {
				// Deleted paragraph
				// System.err.println("Method B");
				int extraEndPara = newParaNum;
				if (newParagraphNo < extraEndPara)
					extraEndPara = newParagraphNo - 1;
				EditSequence sequence = getSequence(doc, oldParagraphNo,
						oldParagraphNo, -1, -1, extraEndPara);
				if (newParagraphNo == -1)
					System.err.println("Sth is wrong!!!");
				editSequences.add(sequence);
				oldParagraphNo++;
			} else if (oldBindings != null && oldBindings.size() == 0
					|| oldParagraphNo > oldParaNum) {
				// Added paragraph
				// System.err.println("Method C");
				int extraEndPara = oldParaNum;
				if (oldParagraphNo < extraEndPara)
					extraEndPara = oldParagraphNo - 1;
				EditSequence sequence = getSequence(doc, -1, -1,
						newParagraphNo, newParagraphNo, extraEndPara);
				if (oldParagraphNo == -1)
					System.err.println("Sth is wrong!!!");
				editSequences.add(sequence);
				newParagraphNo++;
			} else {
				if (newBindings == null && oldBindings == null) {
					System.err.println("Error! OLD:" + oldParagraphNo
							+ ", NEW:" + newParagraphNo);
				}
				if (newBindings.contains(newParagraphNo)
						&& oldBindings.contains(oldParagraphNo)) {
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
					while (!oldStack.isEmpty() || !newStack.isEmpty()) {
						if (!oldStack.isEmpty()) {
							int oldPara = oldStack.pop();
							oldVisited.add(oldPara);
							HashSet<Integer> alignedParas = oldParagraphBinding
									.get(oldPara);
							for (Integer alignedPara : alignedParas) {
								if (!newVisited.contains(alignedPara)) {
									if (alignedPara < newStart)
										newStart = alignedPara;
									if (alignedPara > newEnd)
										newEnd = alignedPara;
									newStack.push(alignedPara);
								}
							}
						}
						if (!newStack.isEmpty()) {
							int newPara = newStack.pop();
							newVisited.add(newPara);
							HashSet<Integer> alignedParas = newParagraphBinding
									.get(newPara);
							for (Integer alignedPara : alignedParas) {
								if (!oldVisited.contains(alignedPara)) {
									if (alignedPara < oldStart)
										oldStart = alignedPara;
									if (alignedPara > oldEnd)
										oldEnd = alignedPara;
									oldStack.push(alignedPara);
								}
							}
						}
					}
					// System.err.println("Method A");
					EditSequence sequence = getSequence(doc, oldStart, oldEnd,
							newStart, newEnd, -1);
					editSequences.add(sequence);
					oldParagraphNo = oldEnd + 1;
					newParagraphNo = newEnd + 1;
				} else {
					// System.err.println("Something is wrong:"+oldParagraphNo +
					// ", "+newParagraphNo);
					int largestNew = 0;
					if (newBindings != null) {
						for (Integer index : newBindings) {
							if (index > largestNew) {
								largestNew = index;
							}
						}
					}
					int largestOld = 0;
					if (oldBindings != null) {
						for (Integer index : oldBindings) {
							if (index > largestOld) {
								largestOld = index;
							}
						}
					}
					if (largestNew > newParagraphNo) {
						// System.err.println("Method B1");
						EditSequence sequence = getSequence(doc,
								oldParagraphNo, oldParagraphNo, -1, -1,
								newParagraphNo);
						editSequences.add(sequence);
						oldParagraphNo++;
					} else if (largestOld > oldParagraphNo) {
						// System.err.println("Method B2");
						EditSequence sequence = getSequence(doc, -1, -1,
								newParagraphNo, newParagraphNo, oldParagraphNo);
						editSequences.add(sequence);
						newParagraphNo++;
					} else {
						System.err.println("someting is wrong:"
								+ oldParagraphNo + "," + newParagraphNo);
						System.err.println(largestOld);
						System.err.println(largestNew);
					}
				}
			}
		}

		return editSequences;
	}

	public static RevisionDocument transformToDocument(
			RevisionDocument originalDoc, List<EditSequence> sequences)
			throws Exception {
		RevisionDocument newDoc = originalDoc.copy();
		// Assigning alignments and purposes at the same time
		for (EditSequence sequence : sequences) {
			List<EditStep> steps = sequence.getLabelSequence();
			for (EditStep step : steps) {
				int leftMove = step.getD1Move();
				int rightMove = step.getD2Move();
				if (leftMove == EditStep.EDIT_MOVE
						&& rightMove == EditStep.EDIT_MOVE) {
					if (step.getCurrentD1() != -1 && step.getCurrentD2() != -1) {
						newDoc.addOldMappingIndex(step.getCurrentD1(),
								step.getCurrentD2());
						newDoc.addNewMappingIndex(step.getCurrentD2(),
								step.getCurrentD1());
					}
					if (step.getType() != RevisionPurpose.NOCHANGE) {
						RevisionUnit unit = new RevisionUnit(newDoc.getRoot());
						ArrayList<Integer> oldIndexes = new ArrayList<Integer>();
						ArrayList<Integer> newIndexes = new ArrayList<Integer>();
						oldIndexes.add(step.getCurrentD1());
						newIndexes.add(step.getCurrentD2());
						unit.setOldSentenceIndex(oldIndexes);
						unit.setNewSentenceIndex(newIndexes);
						unit.setRevision_level(0);
						unit.setRevision_op(RevisionOp.MODIFY);
						unit.setRevision_purpose(step.getType());
						newDoc.getRoot().addUnit(unit);
					}
				} else if (leftMove == EditStep.EDIT_KEEP
						&& rightMove == EditStep.EDIT_MOVE) {
					RevisionUnit unit = new RevisionUnit(newDoc.getRoot());
					ArrayList<Integer> oldIndexes = new ArrayList<Integer>();
					ArrayList<Integer> newIndexes = new ArrayList<Integer>();
					newIndexes.add(step.getCurrentD2());
					unit.setOldSentenceIndex(oldIndexes);
					unit.setNewSentenceIndex(newIndexes);
					unit.setRevision_level(0);
					unit.setRevision_op(RevisionOp.ADD);
					unit.setRevision_purpose(step.getType());
					newDoc.getRoot().addUnit(unit);
				} else if (leftMove == EditStep.EDIT_MOVE
						&& rightMove == EditStep.EDIT_KEEP) {
					RevisionUnit unit = new RevisionUnit(newDoc.getRoot());
					ArrayList<Integer> oldIndexes = new ArrayList<Integer>();
					ArrayList<Integer> newIndexes = new ArrayList<Integer>();
					oldIndexes.add(step.getCurrentD1());
					unit.setOldSentenceIndex(oldIndexes);
					unit.setNewSentenceIndex(newIndexes);
					unit.setRevision_level(0);
					unit.setRevision_op(RevisionOp.DELETE);
					unit.setRevision_purpose(step.getType());
					newDoc.getRoot().addUnit(unit);
				} else {
					System.err.println("Something is wrong");
				}
			}
		}
		return newDoc;
	}

	public static void main(String[] args) throws Exception {
		RevisionDocument docTest = RevisionDocumentReader
				.readDoc("C:\\Not Backed Up\\data\\naaclData\\C1\\Annotation__shadowfox Christian.xlsx");
		List<EditSequence> sequences = transformToSequence(docTest);
		for (EditSequence sequence : sequences) {
			System.out.println(sequence);
		}
		RevisionDocument newDocTest = transformToDocument(docTest, sequences);

		ArrayList<RevisionDocument> docs = RevisionDocumentReader
				.readDocs("C:\\Not Backed Up\\data\\naaclData\\C1");

		boolean isOk = true;
		for (RevisionDocument doc : docs) {
			sequences = transformToSequence(doc);
			RevisionDocument newDoc = transformToDocument(doc, sequences);
			int agreedNum = RevisionDocumentComparer.getAlignmentAgreements(
					doc, newDoc);
			int total = doc.getOldDraftSentences().size()
					+ doc.getNewDraftSentences().size();
			if (agreedNum == total) {
				// correct, do nothing
			} else {
				isOk = false;
				System.err.println(doc.getDocumentName() + ",agreed "
						+ agreedNum + " out of " + total);
			}
		}

		if (isOk)
			System.err.println("Transforming alignment process OK");
	}
}
