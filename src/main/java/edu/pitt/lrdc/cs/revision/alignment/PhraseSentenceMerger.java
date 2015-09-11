package edu.pitt.lrdc.cs.revision.alignment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;

import com.google.common.io.Files;

import edu.pitt.lrdc.cs.revision.alignment.distance.LDCalculator;
import edu.pitt.lrdc.cs.revision.model.RevisionDocument;

public class PhraseSentenceMerger {
	// The purpose of this is to identify the false alignments
	public static void adjustAlignment(RevisionDocument phraseDoc,
			RevisionDocument sentenceDoc) {
		// If just the revision work could not help
	}

	public static String getRealFileName(String name) {
		String info = name;
		//String info = name.substring(name.indexOf("-") + 1);
		// info = info.substring(0,info.indexOf(".txt"));
		info = info.replaceAll("-sentences.txt", "");
		// info = info.replaceAll(".txt","");
		// System.out.println(info);
		return info.trim();
	}

	public static void adjustAlignment(RevisionDocument doc,
			String referenceFolder) throws IOException {
		String refDraft1Folder = referenceFolder + "/draft1";
		String refDraft2Folder = referenceFolder + "/draft2";

		File refDraft1 = new File(refDraft1Folder);
		File refDraft2 = new File(refDraft2Folder);

		File[] refD1s = refDraft1.listFiles();
		File[] refD2s = refDraft2.listFiles();

		String docName = doc.getDocumentName();
		docName = new File(docName).getName();
		// docName = docName.substring(docName.lastIndexOf("Annotation_") + 11);
		docName = docName.replaceAll("Annotation_", "");
		// docName = docName.substring(0, docName.indexOf(".xlsx"));
		docName = docName.replaceAll("\\.xlsx", "");
		docName = docName.trim();

		File d1 = null, d2 = null;
		System.out.println(refDraft1.getAbsolutePath());
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
	 * @throws IOException 
	 */
	public static void adjustAlignment(RevisionDocument doc, File d1, File d2) throws IOException {
		// Adding paragraph info first
		addParagraphInfo(doc, d1, d2);
		//adjustNew(doc);
		//adjustOld(doc);
	}

	public static void addParagraphInfo(RevisionDocument doc, File d1, File d2) throws IOException {
		ArrayList<String> paras1 = new ArrayList<String>();
		ArrayList<String> paras2 = new ArrayList<String>();
		BufferedReader reader = Files.newReader(d1,getCorrectCharsetToApply());
		BufferedReader reader2 = Files.newReader(d2,getCorrectCharsetToApply());
		String line = reader.readLine();
		boolean titleTrimmed= false; //add the tag to trim all the titles
		while(line!=null) {
			if(!titleTrimmed&& (line.endsWith(".")||line.endsWith("!")||line.endsWith("?")||line.trim().split(" ").length>4)) titleTrimmed = true;
			if(titleTrimmed && line.trim().length()>0&&!line.equals("\r")&&!line.equals("\n")&&!line.equals("\r\n")) {
			 paras1.add(line);}
			line = reader.readLine();
		}
		line = null;
		titleTrimmed = false;
		line = reader2.readLine();
		while(line!=null) {
			if(!titleTrimmed&& (line.endsWith(".")||line.endsWith("!")||line.endsWith("?")||line.trim().split(" ").length>4)) titleTrimmed = true;
			if(titleTrimmed&& line.trim().length()>0&&!line.equals("\r")&&!line.equals("\n")&&!line.equals("\r\n"))
				paras2.add(line);
			line = reader2.readLine();
		}
		
		ArrayList<String> lines = doc.getOldDraftSentences();
		ArrayList<String> newLines = doc.getNewDraftSentences();
		int oldParaStart = 0;
		System.out.println("LINE SIZE:"+lines.size());
		for(int i = 0;i<lines.size();i++) {
			for(int j = oldParaStart;j<paras1.size();j++) {
				if(paras1.get(j).contains(lines.get(i))) {
					doc.addOldSentenceParaMap(i+1, j+1);
					oldParaStart = j;
					System.out.println("INDEX: "+i);
					System.out.println("PARAGRAPH: "+j);
					break;
				}
			}
		}
		
		int newParaStart = 0;
		for(int i = 0;i<newLines.size();i++) {
			for(int j = newParaStart;j<paras2.size();j++) {
				if(paras2.get(j).contains(newLines.get(i))) {
					doc.addNewSentenceParaMap(i+1, j+1);
					newParaStart = j;
					break;
				}
			}
		}
	}

	
	private static Charset getCorrectCharsetToApply() {
		// TODO Auto-generated method stub
		//return Charset.forName("Windows-1252");
		return Charset.forName("UTF-8");
	}

	public static void adjustNew(RevisionDocument doc) {
		int i = 1; // My previous bizzare setting that the document index starts
					// from 1
		while (i <= doc.getNewSentencesArray().length) {
			ArrayList<Integer> aligned = doc.getNewFromOld(i);
			// the previous design inconsistency caused the following check
			if (aligned == null || aligned.size() == 0
					|| (aligned.size() == 1 && aligned.get(0) < 1)) {
				// When there is an add, check if this add is caused by over
				// splitting

				// Check if merge up
				while(mergeUpNew(doc, i));
				while(mergeDownNew(doc, i));
			}
			i++;
		}
	}

	/**
	 * Merge up for new draft
	 * 
	 * @param doc
	 * @param newIndex
	 */
	public static boolean mergeUpNew(RevisionDocument doc, int newIndex) {
		String currentSent = doc.getNewSentence(newIndex);
		int up = newIndex - 1;
		String empty = "";
		if (up >= 1) {
			ArrayList<Integer> alignedOld = doc.getOldFromNew(up);
			if(alignedOld == null || alignedOld.size() == 0 || (alignedOld.size()==1 && alignedOld.get(0) < 1)) return false;
			int oldIndexStart = Integer.MAX_VALUE;
			for (Integer oldIndex : alignedOld) {
				if (oldIndex > 0 && oldIndex < oldIndexStart)
					oldIndexStart = oldIndex;
			}
			String newSent = doc.getNewSentence(up);
			String oldSent = doc.getOldSentences(alignedOld);
			oldSent = oldSent.replaceAll("\\n", " ");

//			int currentLD = LDCalculator.calc(empty, currentSent)
//					+ LDCalculator.calc(newSent, oldSent);
			int currentLD = LDCalculator.calc(newSent, oldSent);
			int mergedLD = LDCalculator.calc(newSent + " " + currentSent,
					oldSent);
			if (mergedLD < currentLD) {
				merge(doc, oldIndexStart, 1, up, 2); // Merged the upper
														// sentence
				System.out.println("MERGE UP NEW:"+oldIndexStart+", "+up);
				System.out.println("++++++++++++++++++++OLD++++++++++++++++++++++++");
				System.out.println(oldSent);
				System.out.println("++++++++++++++++++++NEW++++++++++++++++++++++++");
				System.out.println(newSent + " " + currentSent);
				return true;
			}
		}
		return false;
	}

	public static boolean mergeDownNew(RevisionDocument doc, int newIndex) {
		String currentSent = doc.getNewSentence(newIndex);
		int down = newIndex + 1;
		String empty = "";
		if (down <= doc.getNewDraftSentences().size()) {
			ArrayList<Integer> alignedOld = doc.getOldFromNew(down);
			if(alignedOld == null || alignedOld.size() == 0 || (alignedOld.size()==1 && alignedOld.get(0) < 1)) return false;
			int oldIndexStart = Integer.MAX_VALUE;
			for (Integer oldIndex : alignedOld) {
				if (oldIndex > 0 && oldIndex < oldIndexStart)
					oldIndexStart = oldIndex;
			}
			String newSent = doc.getNewSentence(down);
			String oldSent = doc.getOldSentences(alignedOld);
			oldSent = oldSent.replaceAll("\\n", " ");

			//int currentLD = LDCalculator.calc(empty, currentSent)
			//		+ LDCalculator.calc(newSent, oldSent);
  			int currentLD = LDCalculator.calc(newSent, oldSent);
			int mergedLD = LDCalculator.calc(currentSent + " " + newSent,
					oldSent);
			if (mergedLD < currentLD) {
				merge(doc, oldIndexStart, 1, newIndex, 2); // Merged the upper
															// sentence
				System.out.println("MERGE DOWN NEW:"+oldIndexStart+", "+newIndex);
				System.out.println("++++++++++++++++++++OLD++++++++++++++++++++++++");
				System.out.println(oldSent);
				System.out.println("++++++++++++++++++++NEW++++++++++++++++++++++++");
				System.out.println(currentSent + " " + newSent);
				return true;
			}
		}
		return false;
	}

	public static boolean mergeUpOld(RevisionDocument doc, int oldIndex) {
		String currentSent = doc.getOldSentence(oldIndex);
		int up = oldIndex - 1;
		String empty = "";
		if (up >= 1) {
			ArrayList<Integer> alignedNew = doc.getNewFromOld(up);
			if(alignedNew == null || alignedNew.size() == 0 || (alignedNew.size()==1 && alignedNew.get(0) < 1)) return false;
			int newIndexStart = Integer.MAX_VALUE;
			for (Integer newIndex : alignedNew) {
				if (newIndex > 0 && newIndex < newIndexStart)
					newIndexStart = newIndex;
			}
			String newSent = doc.getNewSentences(alignedNew);
			String oldSent = doc.getOldSentence(up);
			newSent = newSent.replaceAll("\\n", " ");

//			int currentLD = LDCalculator.calc(empty, currentSent)
//					+ LDCalculator.calc(newSent, oldSent);
			int currentLD = LDCalculator.calc(newSent, oldSent);
			int mergedLD = LDCalculator.calc(newSent, oldSent + " " + currentSent);
			if (mergedLD < currentLD) {
				merge(doc, up, 2, newIndexStart, 1); // Merged the upper
														// sentence
				System.out.println("MERGE UP OLD:"+up+", "+newIndexStart);
				System.out.println("++++++++++++++++++++OLD++++++++++++++++++++++++");
				System.out.println(oldSent + " " + currentSent);
				System.out.println("++++++++++++++++++++NEW++++++++++++++++++++++++");
				System.out.println(newSent);
				return true;
			}
		}
		return false;
	}

	public static boolean mergeDownOld(RevisionDocument doc, int oldIndex) {
		String currentSent = doc.getOldSentence(oldIndex);
		int down = oldIndex + 1;
		String empty = "";
		if (down <= doc.getOldDraftSentences().size()) {
			ArrayList<Integer> alignedNew = doc.getNewFromOld(down);
			if(alignedNew == null || alignedNew.size() == 0 || (alignedNew.size()==1 && alignedNew.get(0) < 1)) return false;
			int newIndexStart = Integer.MAX_VALUE;
			for (Integer newIndex : alignedNew) {
				if (newIndex > 0 && newIndex < newIndexStart)
					newIndexStart = newIndex;
			}
			String oldSent = doc.getOldSentence(down);
			String newSent = doc.getNewSentences(alignedNew);

			newSent = newSent.replaceAll("\\n", " ");

//			int currentLD = LDCalculator.calc(empty, currentSent)
//					+ LDCalculator.calc(newSent, oldSent);
			int currentLD = LDCalculator.calc(newSent, oldSent);
			int mergedLD = LDCalculator.calc(currentSent + " " + oldSent,
					newSent);
			if (mergedLD < currentLD) {
				merge(doc, oldIndex, 2, newIndexStart, 1); // Merged the
															// sentence below
				System.out.println("MERGE DOWN OLD:"+oldIndex+", "+newIndexStart);
				System.out.println("++++++++++++++++++++OLD++++++++++++++++++++++++");
				System.out.println(currentSent + " " + oldSent);
				System.out.println("++++++++++++++++++++NEW++++++++++++++++++++++++");
				System.out.println(newSent);
				return true;
			}
		}
		return false;
	}

	public static void adjustOld(RevisionDocument doc) {
		int i = 1; // My previous bizzare setting that the document index starts
		// from 1
		while (i <= doc.getOldSentencesArray().length) {
			ArrayList<Integer> aligned = doc.getOldFromNew(i);
			// the previous design inconsistency caused the following check
			if (aligned == null || aligned.size() == 0
					|| (aligned.size() == 1 && aligned.get(0) < 1)) {
				// When there is an add, check if this add is caused by over
				// splitting

				// Check if merge up
				while(mergeUpOld(doc, i));
				while(mergeDownOld(doc, i));
			}
			i++;
		}
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
		doc.addOldSentenceParaMap(oldIndexStart, oldParaNo);
		doc.addNewSentenceParaMap(newIndexStart, newParaNo);
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
			String referenceFolder) throws IOException {
		for (RevisionDocument doc : docs) {
			adjustAlignment(doc, referenceFolder);
		}
	}
}
