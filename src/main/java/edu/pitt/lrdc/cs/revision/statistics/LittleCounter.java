package edu.pitt.lrdc.cs.revision.statistics;

import java.util.ArrayList;

import edu.pitt.lrdc.cs.revision.io.RevisionDocumentReader;
import edu.pitt.lrdc.cs.revision.model.RevisionDocument;
import edu.pitt.lrdc.cs.revision.model.RevisionUnit;

/**
 * Just a small temporary tool for analysis
 * @author zhangfan
 *
 */
public class LittleCounter {
	public static void main(String[] args) throws Exception {
		String filePath = "C:\\Not Backed Up\\data\\eagerstudy\\temp\\draft2-draft3";
		ArrayList<RevisionDocument> docs = RevisionDocumentReader.readDocs(filePath);
		for(RevisionDocument doc: docs) {
			System.out.println(doc.getDocumentName());
			int draft1WordCount = 0;
			int draft2WordCount = 0;
			int revCount = 0;
			ArrayList<String> oldSents = doc.getOldDraftSentences();
			ArrayList<String> newSents = doc.getNewDraftSentences();
			for(String oldSent: oldSents) {
				draft1WordCount += oldSent.split(" ").length;
			}
			for(String newSent: newSents) {
				draft2WordCount += newSent.split(" ").length;
			}
			int oldSentenceCount = oldSents.size();
			int newSentenceCount = newSents.size();
			
			System.out.println("Draft 1 Words: "+ draft1WordCount + ", Sents: "+ oldSentenceCount + ", Paragraphs: " + doc.getOldParagraphNum());
			System.out.println("Draft 2 Words: "+ draft2WordCount + ", Sents: "+ newSentenceCount + ", Paragraphs: " + doc.getNewParagraphNum());

			ArrayList<RevisionUnit> revisions = doc.getRoot().getRevisionUnitAtLevel(0);
			System.out.println("Number of revision: " + revisions.size());
			for(RevisionUnit rev: revisions) {
				System.out.println(rev.toStringBrief());
			}
		}
	}
}
