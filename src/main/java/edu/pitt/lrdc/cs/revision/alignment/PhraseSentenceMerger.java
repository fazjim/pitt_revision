package edu.pitt.lrdc.cs.revision.alignment;

import java.io.File;
import java.util.ArrayList;

import edu.pitt.lrdc.cs.revision.model.RevisionDocument;

public class PhraseSentenceMerger {
	//The purpose of this is to identify the false alignments
	public static void adjustAlignment(RevisionDocument phraseDoc, RevisionDocument sentenceDoc) {
		//If just the revision work could not help
	}
	
	public static String getRealFileName(String name) {
		String info = name.substring(name.indexOf("-") + 1);
		// info = info.substring(0,info.indexOf(".txt"));
		info = info.replaceAll("-sentences.txt", "");
		//info = info.replaceAll(".txt","");
		//System.out.println(info);
		return info.trim();
	}
	
	
	public static void adjustAlignment(RevisionDocument doc, String referenceFolder) {
		String refDraft1Folder = referenceFolder + "/draft1";
		String refDraft2Folder = referenceFolder + "/draft2";

		File refDraft1 = new File(refDraft1Folder);
		File refDraft2 = new File(refDraft2Folder);

		File[] refD1s = refDraft1.listFiles();
		File[] refD2s = refDraft2.listFiles();

		String docName = doc.getDocumentName();
		//docName = docName.substring(docName.lastIndexOf("Annotation_") + 11);
		docName = docName.replaceAll("Annotation_", "");
		//docName = docName.substring(0, docName.indexOf(".xlsx"));
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
	
	public static void adjustAlignment(RevisionDocument doc, File d1, File d2) {
		
	}
	
	//Use the referenceFolder to find the text
	public static void adjustAlignment(ArrayList<RevisionDocument> docs, String referenceFolder) {
		for(RevisionDocument doc: docs) {
			adjustAlignment(doc,referenceFolder);
		}
	}
}
