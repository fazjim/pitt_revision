package edu.pitt.lrdc.cs.revision.evaluate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;

import edu.pitt.lrdc.cs.revision.model.RevisionDocument;

public class PipelineEvaluate {
	public static void main(String[] args) {

	}

	public static String getString(ArrayList<Integer> indices) {
		if (indices == null)
			return " ";
		Collections.sort(indices);
		String str = " ";
		for (Integer index : indices) {
			if (index != -1)
				;
			str += Integer.toString(index) + "_";
		}
		return str;
	}

	public static void evaluate(RevisionDocument realDoc,
			RevisionDocument predictedDoc) {
		double sentenceNumD1 = 0;
		double sentenceNumD2 = 0;
		double paragraphNumD1 = 0;
		double paragraphNumD2 = 0;
		double avgSentParaD1 = 0;
		double avgSentParaD2 = 0;
		double avgWordNumD1 = 0;
		double avgWordNumD2 = 0;
		double realRevCount = 0;
		double predictedRevCount = 0;

		double alignmentAccuracy = 0;

		double realCount_Claim = 0;
		double realCount_Evidence = 0;
		double realCount_General = 0;
		double realCount_Warrant = 0;
		double realCount_Surface = 0;

		double predictCount_Claim = 0;
		double predictCount_Evidence = 0;
		double predictCount_General = 0;
		double predictCount_Warrant = 0;
		double predictCount_Surface = 0;

		double prec_Claim = 0;
		double prec_Evidence = 0;
		double prec_General = 0;
		double prec_Warrant = 0;
		double prec_Surface = 0;

		double recall_Claim = 0;
		double recall_Evidence = 0;
		double recall_General = 0;
		double recall_Warrant = 0;
		double recall_Surface = 0;

		sentenceNumD1 = realDoc.getOldDraftSentences().size();
		sentenceNumD2 = realDoc.getNewDraftSentences().size();

		paragraphNumD1 = realDoc.getOldParagraphNum();
		paragraphNumD2 = realDoc.getNewParagraphNum();

		avgSentParaD1 = sentenceNumD1 / paragraphNumD1;
		avgSentParaD2 = sentenceNumD2 / paragraphNumD2;

		Hashtable<Integer, String> oldAlignmentIndice = new Hashtable<Integer, String>();
		Hashtable<Integer, String> newAlignmentIndice = new Hashtable<Integer, String>();

		double oldAccuracy = 0;
		for (int i = 0; i < realDoc.getOldDraftSentences().size(); i++) {
			int index = i + 1;
			ArrayList<Integer> aligned = realDoc.getNewFromOld(index);
			String sent = realDoc.getOldSentence(index);
			int wordNum = sent.split(" ").length;
			avgWordNumD1 += wordNum;
			oldAlignmentIndice.put(index, getString(aligned));
		}

		for (int i = 0; i < predictedDoc.getOldDraftSentences().size(); i++) {
			int index = i + 1;
			ArrayList<Integer> aligned = predictedDoc.getNewFromOld(index);
			if (oldAlignmentIndice.containsKey(index)
					&& getString(aligned).equals(oldAlignmentIndice.get(index))) {
				oldAccuracy += 1;
			}
		}
		oldAccuracy = oldAccuracy / sentenceNumD1;

		double newAccuracy = 0;
		for (int i = 0; i < realDoc.getNewDraftSentences().size(); i++) {
			int index = i + 1;
			ArrayList<Integer> aligned = realDoc.getOldFromNew(index);
			String sent = realDoc.getNewSentence(index);
			int wordNum = sent.split(" ").length;
			avgWordNumD2 += wordNum;
			newAlignmentIndice.put(index, getString(aligned));
		}

		for (int i = 0; i < predictedDoc.getNewDraftSentences().size(); i++) {
			int index = i + 1;
			ArrayList<Integer> aligned = predictedDoc.getOldFromNew(index);
			if (newAlignmentIndice.containsKey(index)
					&& getString(aligned).equals(newAlignmentIndice.get(index))) {
				newAccuracy += 1;
			}
		}
		newAccuracy = newAccuracy / sentenceNumD2;

		alignmentAccuracy = (oldAccuracy + newAccuracy) / 2;
		avgWordNumD1 = avgWordNumD1 / sentenceNumD1;
		avgWordNumD2 = avgWordNumD2 / sentenceNumD2;

	}
}
