package edu.pitt.lrdc.cs.revision.alignment;

import java.io.File;
import java.util.ArrayList;

import edu.pitt.lrdc.cs.revision.model.RevisionDocument;

public class PhraseSentenceMerger {
	// The purpose of this is to identify the false alignments
	public static void adjustAlignment(RevisionDocument phraseDoc,
			RevisionDocument sentenceDoc) {
		// If just the revision work could not help
	}

	public static String getRealFileName(String name) {
		String info = name.substring(name.indexOf("-") + 1);
		// info = info.substring(0,info.indexOf(".txt"));
		info = info.replaceAll("-sentences.txt", "");
		// info = info.replaceAll(".txt","");
		// System.out.println(info);
		return info.trim();
	}

	public static void adjustAlignment(RevisionDocument doc,
			String referenceFolder) {
		String refDraft1Folder = referenceFolder + "/draft1";
		String refDraft2Folder = referenceFolder + "/draft2";

		File refDraft1 = new File(refDraft1Folder);
		File refDraft2 = new File(refDraft2Folder);

		File[] refD1s = refDraft1.listFiles();
		File[] refD2s = refDraft2.listFiles();

		String docName = doc.getDocumentName();
		// docName = docName.substring(docName.lastIndexOf("Annotation_") + 11);
		docName = docName.replaceAll("Annotation_", "");
		// docName = docName.substring(0, docName.indexOf(".xlsx"));
		docName = docName.replaceAll("\\.xlsx", "");
		docName = docName.trim();

		File d1 = null, d2 = null;
		for (File f : refD1s) {
			if (getRealFileName(f.getName()).equals(docName)) {
				d1 = f;
				break;
			}
		}
		for (File f : refD2s) {
			if (getRealFileName(f.getName()).equals(docName)) {
				d2 = f;
				break;
			}
		}

		if (d1 != null && d2 != null) {
			adjustAlignment(doc, d1, d2);
		} else {
			System.err
					.println("Something is wrong for finding the reference file of paragraph info");
			System.err.println(docName);
		}
	}

	/**
	 * Adding the paragraph info and merging the clauses
	 * 
	 * @param doc
	 * @param d1
	 * @param d2
	 */
	public static void adjustAlignment(RevisionDocument doc, File d1, File d2) {
		// Adding paragraph info first
		addParagraphInfo(doc, d1, d2);
		adjustNew(doc);
		adjustOld(doc);
	}

	public static void addParagraphInfo(RevisionDocument doc, File d1, File d2) {

	}

	public static void adjustNew(RevisionDocument doc) {
		int i = 1; // My previous bizzare setting that the document index starts
					// from 1
		while (i <= doc.getNewSentencesArray().length) {
			ArrayList<Integer> aligned = doc.getNewFromOld(i);
			//the previous design inconsistency caused the following check
			if (aligned == null || aligned.size() == 0
					|| (aligned.size() == 1 && aligned.get(0) == -1)) { 
				
			}
		}
	}

	public static void adjustOld(RevisionDocument doc) {

	}

	/**
	 * Once you do any merging, you have to realign the aligned sentences
	 * 
	 * Update: 1. In the clause-level setting, there is only one-to-one
	 * alignment, just use ArrayList to be compatible with old setting 2. Once
	 * we merge several sentences, the indices above them will not change and
	 * also the alignments, the following indices will shift
	 * 
	 * @param doc
	 */
	public static void merge(RevisionDocument doc, int oldIndexStart,
			int oldNum, int newIndexStart, int newNum) {
		// we suppose the merge will not cross paragraph (Probably it won't)
		// TBD: How do we deal with the cross-paragraph case???
		int oldParaNo = doc.getParaNoOfOldSentence(oldIndexStart);
		int newParaNo = doc.getParaNoOfNewSentence(newIndexStart);
		mergeOld(doc, oldIndexStart, oldNum);
		mergeNew(doc, newIndexStart, newNum);
		ArrayList<Integer> oldIndices = new ArrayList<Integer>();
		oldIndices.add(oldIndexStart);
		doc.changeNewAlignment(newIndexStart, oldIndices);
	}

	public static void mergeOld(RevisionDocument doc, int oldIndexStart, int num) {
		doc.mergeOldSentences(oldIndexStart, num);
		doc.shiftAlignmentOfNew(oldIndexStart, num - 1);
	}

	public static void mergeNew(RevisionDocument doc, int newIndexStart, int num) {
		doc.mergeNewSentences(newIndexStart, num);
		doc.shiftAlignmentofOld(newIndexStart, num - 1);
	}

	// Use the referenceFolder to find the text
	public static void adjustAlignment(ArrayList<RevisionDocument> docs,
			String referenceFolder) {
		for (RevisionDocument doc : docs) {
			adjustAlignment(doc, referenceFolder);
		}
	}
}
