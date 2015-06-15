package edu.pitt.lrdc.cs.revision.alignment;

import java.util.ArrayList;

import edu.pitt.lrdc.cs.revision.alignment.model.Document;
import edu.pitt.lrdc.cs.revision.alignment.model.DocumentPair;
import edu.pitt.lrdc.cs.revision.model.RevisionDocument;


/**
 * Includes a bunch of functions to transform the RevisionDocument class for the alignment tool
 * @author zhangfan
 *
 */
public class RevisionDocumentAdapter {
	/**
	 * Get the DocumentPair object from a RevisionDocument
	 * @param doc
	 * @return
	 */
	public static DocumentPair getDocumentPairFromDoc(RevisionDocument doc) {
		DocumentPair dp = new DocumentPair();
		dp.setFileName(doc.getDocumentName());
		Document src = new Document();
		Document dst = new Document();
		
		String[] oldSents = doc.getOldSentencesArray();
		String[] newSents = doc.getNewSentencesArray();
		
		
		//Index starting from 1
		int index = 1;
		for(String oldSent: oldSents) {
			src.addSentence(index, oldSent);
			index++;
		}
		
//		index = 1;
//		for(String newSent: newSents) {
//			dst.addSentence(index, newSent);
//			index++;
//		}
		
		for(int i = 1;i<=newSents.length;i++) {
			ArrayList<Integer> aligned = doc.getOldFromNew(i);
			if(aligned == null||aligned.size()==0||(aligned.size()==1&&aligned.get(0)==-1)) {
				dst.addAlignedSentence(i, doc.getNewSentence(i), "");
			} else {
				String aligns = "";
				for(Integer a: aligned) {
					if(a!=-1) {
						aligns+=a+",";
					}
				}
				dst.addAlignedSentence(i, doc.getNewSentence(i), aligns);
			}
		}
		dp.setSrc(src);
		dp.setModified(dst);
		
		
		return dp;
	}
	
	/**
	 * Get a group of DocumentPairs from documents
	 * @param docs
	 * @return
	 */
	public static ArrayList<DocumentPair> getDocumentPairsFromDocs(ArrayList<RevisionDocument> docs) {
		ArrayList<DocumentPair> dps = new ArrayList<DocumentPair>();
		for(RevisionDocument doc: docs) {
			dps.add(getDocumentPairFromDoc(doc));
		}
		return dps;
	}
}
