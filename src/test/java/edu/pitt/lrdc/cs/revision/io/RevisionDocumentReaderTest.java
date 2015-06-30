package edu.pitt.lrdc.cs.revision.io;

import java.util.ArrayList;

import edu.pitt.lrdc.cs.revision.model.RevisionDocument;

public class RevisionDocumentReaderTest {
	public static void main(String[] args) throws Exception {
		String path = "";

		ArrayList<RevisionDocument> docs = RevisionDocumentReader.readDocs("C:\\Not Backed Up\\data_phrase_science\\intro\\gibson-sentence");
		int d1Clauses = 0;
		int d2Clauses = 0;
		
		int numWordsD1 = 0;
		int numWordsD2 = 0;
		
		for(RevisionDocument doc: docs) {
			String[] oldClauses = doc.getOldSentencesArray();
			String[] newClauses = doc.getNewSentencesArray();
			d1Clauses += oldClauses.length;
			d2Clauses += newClauses.length;
			
			for(String oldClause: oldClauses) {
				numWordsD1 += oldClause.split(" ").length + 1;
			}
			
			for(String newClause: newClauses) {
				numWordsD2 += newClause.split(" ").length + 1;
			}
		}
		
		double avg1 = d1Clauses* 1.0/docs.size();
		double avg2 = d2Clauses* 1.0/docs.size();
		System.out.println("D1:" + avg1);
		System.out.println("D2:" + avg2);
		
		double avgW1 = numWordsD1 * 1.0 / d1Clauses;
		double avgW2 = numWordsD2 * 1.0 / d2Clauses;
		System.out.println("W1:"+ avgW1);
		System.out.println("W2:" + avgW2);
	}
}
