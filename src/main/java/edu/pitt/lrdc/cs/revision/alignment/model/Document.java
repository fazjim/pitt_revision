package edu.pitt.lrdc.cs.revision.alignment.model;

import java.util.*;

/**
 * Reusing my old code, the structure represents the sentences of a draft
 * @author zhangfan
 *
 */

public class Document {
	private ArrayList<Sentence> sentences = new ArrayList<Sentence>();

	public ArrayList<Sentence> getSentences() {
		return sentences;
	}

	public void setSentences(ArrayList<Sentence> sentences) {
		this.sentences = sentences;
	}

	public void addSentence(int index, String content) {
		Sentence sen = new Sentence();
		sen.setIndex(index);
		sen.setContent(content);
		sentences.add(sen);
	}

	public void addAlignedSentence(int index, String content, String aligns) {
		Sentence sen = new Sentence();
		sen.setIndex(index);
		sen.setContent(content);
		String[] indexes = aligns.split(",");
		ArrayList<Integer> indice = new ArrayList<Integer>();
		for (int i = 0; i < indexes.length; i++) {
			try {
				indice.add((int) Double.parseDouble(indexes[i]));
			} catch (Exception exp) {

			}
		}
		sen.setAlignedIndex(indice);
		sentences.add(sen);
	}
}
