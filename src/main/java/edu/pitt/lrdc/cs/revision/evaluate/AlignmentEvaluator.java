package edu.pitt.lrdc.cs.revision.evaluate;

import java.util.ArrayList;
import java.util.HashSet;

import edu.pitt.lrdc.cs.revision.agreement.KappaCalc;
import edu.pitt.lrdc.cs.revision.model.RevisionDocument;

public class AlignmentEvaluator {
	/**
	 * Compare whether two arrays are equal or not, -1 is removed as this is
	 * considered a non-alignment
	 * 
	 * @param a1
	 * @param a2
	 * @return
	 */
	static boolean compareArr(ArrayList<Integer> a1, ArrayList<Integer> a2) {
		if (a1 == null)
			a1 = new ArrayList<Integer>();
		if (a2 == null)
			a2 = new ArrayList<Integer>();
		HashSet<Integer> set = new HashSet<Integer>();
		for (Integer i : a1) {
			if (i != -1)
				set.add(i);
		}

		HashSet<Integer> set2 = new HashSet<Integer>();
		for (Integer i : a2) {
			if (i != -1)
				set2.add(i);
		}

		if (set.size() != set2.size())
			return false;
		for (Integer i : set) {
			if (!set2.contains(i))
				return false;
		}
		return true;
	}

	/**
	 * The accuracy of alignment based on the new draft
	 * 
	 * @param doc
	 * @return
	 */
	public static double getAlignmentAccuracy(RevisionDocument doc) {
		int sentenceNewNum = doc.getNewSentencesArray().length;
		int correct = 0;
		for (int i = 1; i <= sentenceNewNum; i++) {
			ArrayList<Integer> oldAligned = doc.getOldFromNew(i);
			ArrayList<Integer> predictedAligned = doc.getPredictedOldFromNew(i);
			if (oldAligned == null || oldAligned.size() == 0
					|| (oldAligned.size() == 1 && oldAligned.get(0) == -1)) {
				if (predictedAligned == null
						|| predictedAligned.size() == 0
						|| (predictedAligned.size() == 1 && predictedAligned
								.get(0) == -1)) {
					correct++;
				}
			} else {
				if (compareArr(oldAligned, predictedAligned)) {
					correct++;
				}
			}
		}
		return correct * 1.0 / sentenceNewNum;
	}

	/**
	 * The kappa will be higher than it really is
	 * @param doc
	 * @return
	 */
	public static double getAlignmentKappa(RevisionDocument doc) {
		int sentNewNum = doc.getNewSentencesArray().length;
		int[][] matrix = new int[2][2];
		for (int i = 1;i<= sentNewNum;i++) {
			ArrayList<Integer> oldAligned = doc.getOldFromNew(i);
			ArrayList<Integer> predictedAligned = doc.getPredictedOldFromNew(i);
			int oldIndex = 0;
			if (oldAligned == null || oldAligned.size() == 0
					|| (oldAligned.size() == 1 && oldAligned.get(0) == -1))  oldIndex = 1;
			int newIndex = 0;
			if (predictedAligned == null
					|| predictedAligned.size() == 0
					|| (predictedAligned.size() == 1 && predictedAligned
							.get(0) == -1)) newIndex = 1;
			matrix[oldIndex][newIndex] += 1;
		}
		
		double kappa = KappaCalc.kappaCalc(matrix);
		return kappa;
	}
	
	/**
	 * The average alignment
	 * 
	 * @param docs
	 * @return
	 */
	public static double getAlignmentAccuracyAvg(
			ArrayList<RevisionDocument> docs) {
		int num = docs.size();
		double total = 0.0;
		for (RevisionDocument doc : docs) {
			total += getAlignmentAccuracy(doc);
		}
		return total / num;
	}
	
	public static double getAlignmentKappaAvg(
			ArrayList<RevisionDocument> docs) {
		int num = docs.size();
		double total = 0.0;
		for (RevisionDocument doc : docs) {
			System.out.println("Kappa:"+getAlignmentKappa(doc));
			total += getAlignmentKappa(doc);
		}
		return total / num;
	}

	/**
	 * The weighted average alignment
	 * 
	 * @param docs
	 * @return
	 */
	public static double getAlignmentAccuracyTotal(
			ArrayList<RevisionDocument> docs) {
		ArrayList<Double> allNums = new ArrayList<Double>();
		int allNum = 0;
		for (RevisionDocument doc : docs) {
			double length = (double) doc.getNewSentencesArray().length;
			allNums.add(length);
			allNum += length;
		}
		for (int i = 0; i < allNums.size(); i++) {
			double val = allNums.get(i) / allNum;
			allNums.set(i, val);
		}

		double totalAcc = 0.0;
		for (int i = 0; i < allNums.size(); i++) {
			totalAcc += allNums.get(i) * getAlignmentAccuracy(docs.get(i));
		}
		return totalAcc;
	}
	
	/**
	 * The weighted average alignment
	 * 
	 * @param docs
	 * @return
	 */
	public static double getAlignmentKappaTotal(
			ArrayList<RevisionDocument> docs) {
		ArrayList<Double> allNums = new ArrayList<Double>();
		int allNum = 0;
		for (RevisionDocument doc : docs) {
			double length = (double) doc.getNewSentencesArray().length;
			allNums.add(length);
			allNum += length;
		}
		for (int i = 0; i < allNums.size(); i++) {
			double val = allNums.get(i) / allNum;
			allNums.set(i, val);
		}

		double totalAcc = 0.0;
		for (int i = 0; i < allNums.size(); i++) {
			totalAcc += allNums.get(i) * getAlignmentKappa(docs.get(i));
		}
		return totalAcc;
	}
}
