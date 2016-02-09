package edu.pitt.lrdc.cs.revision.evaluate;

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;

import weka.classifiers.evaluation.Evaluation;
import edu.pitt.lrdc.cs.revision.model.RevisionDocument;
import edu.pitt.lrdc.cs.revision.model.RevisionPurpose;
import edu.pitt.lrdc.cs.revision.model.RevisionUnit;

/**
 * Copied a lot of methods from Evaluation of weka class
 * 
 * @author zhf4pal
 *
 */

public class EvaluateUtil {

	/**
	 * Record stuff into table
	 * 
	 * This is for the case where the alignments have already been decided
	 * 
	 * @param docs
	 * @param golds
	 */
	public void compareResults(ArrayList<RevisionDocument> docs,
			ArrayList<RevisionDocument> golds, int revisionPurpose) {
		m_NumClasses = 2;
		m_ConfusionMatrix = new int[m_NumClasses][m_NumClasses];
		for (int i = 0; i < docs.size(); i++) {
			RevisionDocument predictedDoc = docs.get(i);
			RevisionDocument gold = findMatchedDoc(predictedDoc, golds);

			ArrayList<RevisionUnit> predictedUnits = predictedDoc.getRoot()
					.getRevisionUnitAtLevel(0);
			ArrayList<RevisionUnit> goldUnits = gold.getRoot()
					.getRevisionUnitAtLevel(0);
			Hashtable<String, Integer> predictedIndex = new Hashtable<String, Integer>();
			Hashtable<String, Integer> goldIndex = new Hashtable<String, Integer>();

			if (revisionPurpose == -1) {
				predictedIndex = getLabeledInformation(predictedUnits);
				goldIndex = getLabeledInformation(goldUnits);
			} else {
				predictedIndex = getLabeledInformation(predictedUnits,
						revisionPurpose);
				goldIndex = getLabeledInformation(goldUnits, revisionPurpose);
			}
			Iterator<String> it = predictedIndex.keySet().iterator();
			while (it.hasNext()) {
				String key = it.next();
				m_ConfusionMatrix[predictedIndex.get(key)][goldIndex.get(key)] += 1;
			}
		}
	}

	
	private Hashtable<String, Integer> getLabeledInformation(
			ArrayList<RevisionUnit> units, int revisionPurpose) {
		Hashtable<String, Integer> labelIndex = new Hashtable<String, Integer>();
		for (RevisionUnit unit : units) {
			int revOp = unit.getRevision_purpose();
			if (revOp == revisionPurpose)
				labelIndex.put(unit.getIndexLabel(), 1);
			else {
				if (!labelIndex.containsKey(unit.getIndexLabel()))
					labelIndex.put(unit.getIndexLabel(), 0);
			}
		}
		return labelIndex;
	}

	private Hashtable<String, Integer> getLabeledInformation(
			ArrayList<RevisionUnit> units) {
		Hashtable<String, Integer> labelIndex = new Hashtable<String, Integer>();
		for (RevisionUnit unit : units) {
			int revOp = unit.getRevision_purpose();
			if (revOp == RevisionPurpose.WORDUSAGE_CLARITY
					|| revOp == RevisionPurpose.CONVENTIONS_GRAMMAR_SPELLING
					|| revOp == RevisionPurpose.ORGANIZATION)
				labelIndex.put(unit.getIndexLabel(), 1);
			else {
				if (!labelIndex.containsKey(unit.getIndexLabel()))
					labelIndex.put(unit.getIndexLabel(), 0);
			}
		}
		return labelIndex;
	}

	public RevisionDocument findMatchedDoc(RevisionDocument doc,
			ArrayList<RevisionDocument> golds) {
		File f = new File(doc.getDocumentName());
		String fileName = f.getName();
		for (RevisionDocument gold : golds) {
			File goldF = new File(gold.getDocumentName());
			if (goldF.getName().equals(fileName))
				return gold;
		}
		return null;
	}

	private int m_NumClasses;
	private int[][] m_ConfusionMatrix;

	/**
	 * Calculate the number of true positives with respect to a particular
	 * class. This is defined as
	 * <p/>
	 * 
	 * <pre>
	 * correctly classified positives
	 * </pre>
	 * 
	 * @param classIndex
	 *            the index of the class to consider as "positive"
	 * @return the true positive rate
	 */
	public double numTruePositives(int classIndex) {

		double correct = 0;
		for (int j = 0; j < m_NumClasses; j++) {
			if (j == classIndex) {
				correct += m_ConfusionMatrix[classIndex][j];
			}
		}
		return correct;
	}

	/**
	 * Calculate the true positive rate with respect to a particular class. This
	 * is defined as
	 * <p/>
	 * 
	 * <pre>
	 * correctly classified positives
	 * ------------------------------
	 *       total positives
	 * </pre>
	 * 
	 * @param classIndex
	 *            the index of the class to consider as "positive"
	 * @return the true positive rate
	 */
	public double truePositiveRate(int classIndex) {

		double correct = 0, total = 0;
		for (int j = 0; j < m_NumClasses; j++) {
			if (j == classIndex) {
				correct += m_ConfusionMatrix[classIndex][j];
			}
			total += m_ConfusionMatrix[classIndex][j];
		}
		if (total == 0) {
			return 0;
		}
		return correct / total;
	}

	/**
	 * Calculates the weighted (by class size) true positive rate.
	 * 
	 * @return the weighted true positive rate.
	 */
	public double weightedTruePositiveRate() {
		double[] classCounts = new double[m_NumClasses];
		double classCountSum = 0;

		for (int i = 0; i < m_NumClasses; i++) {
			for (int j = 0; j < m_NumClasses; j++) {
				classCounts[i] += m_ConfusionMatrix[i][j];
			}
			classCountSum += classCounts[i];
		}

		double truePosTotal = 0;
		for (int i = 0; i < m_NumClasses; i++) {
			double temp = truePositiveRate(i);
			truePosTotal += (temp * classCounts[i]);
		}

		return truePosTotal / classCountSum;
	}

	/**
	 * Calculate the number of true negatives with respect to a particular
	 * class. This is defined as
	 * <p/>
	 * 
	 * <pre>
	 * correctly classified negatives
	 * </pre>
	 * 
	 * @param classIndex
	 *            the index of the class to consider as "positive"
	 * @return the true positive rate
	 */
	public double numTrueNegatives(int classIndex) {

		double correct = 0;
		for (int i = 0; i < m_NumClasses; i++) {
			if (i != classIndex) {
				for (int j = 0; j < m_NumClasses; j++) {
					if (j != classIndex) {
						correct += m_ConfusionMatrix[i][j];
					}
				}
			}
		}
		return correct;
	}

	/**
	 * Calculate the true negative rate with respect to a particular class. This
	 * is defined as
	 * <p/>
	 * 
	 * <pre>
	 * correctly classified negatives
	 * ------------------------------
	 *       total negatives
	 * </pre>
	 * 
	 * @param classIndex
	 *            the index of the class to consider as "positive"
	 * @return the true positive rate
	 */
	public double trueNegativeRate(int classIndex) {

		double correct = 0, total = 0;
		for (int i = 0; i < m_NumClasses; i++) {
			if (i != classIndex) {
				for (int j = 0; j < m_NumClasses; j++) {
					if (j != classIndex) {
						correct += m_ConfusionMatrix[i][j];
					}
					total += m_ConfusionMatrix[i][j];
				}
			}
		}
		if (total == 0) {
			return 0;
		}
		return correct / total;
	}

	/**
	 * Calculates the weighted (by class size) true negative rate.
	 * 
	 * @return the weighted true negative rate.
	 */
	public double weightedTrueNegativeRate() {
		double[] classCounts = new double[m_NumClasses];
		double classCountSum = 0;

		for (int i = 0; i < m_NumClasses; i++) {
			for (int j = 0; j < m_NumClasses; j++) {
				classCounts[i] += m_ConfusionMatrix[i][j];
			}
			classCountSum += classCounts[i];
		}

		double trueNegTotal = 0;
		for (int i = 0; i < m_NumClasses; i++) {
			double temp = trueNegativeRate(i);
			trueNegTotal += (temp * classCounts[i]);
		}

		return trueNegTotal / classCountSum;
	}

	/**
	 * Calculate number of false positives with respect to a particular class.
	 * This is defined as
	 * <p/>
	 * 
	 * <pre>
	 * incorrectly classified negatives
	 * </pre>
	 * 
	 * @param classIndex
	 *            the index of the class to consider as "positive"
	 * @return the false positive rate
	 */
	public double numFalsePositives(int classIndex) {

		double incorrect = 0;
		for (int i = 0; i < m_NumClasses; i++) {
			if (i != classIndex) {
				for (int j = 0; j < m_NumClasses; j++) {
					if (j == classIndex) {
						incorrect += m_ConfusionMatrix[i][j];
					}
				}
			}
		}
		return incorrect;
	}

	/**
	 * Calculate the false positive rate with respect to a particular class.
	 * This is defined as
	 * <p/>
	 * 
	 * <pre>
	 * incorrectly classified negatives
	 * --------------------------------
	 *        total negatives
	 * </pre>
	 * 
	 * @param classIndex
	 *            the index of the class to consider as "positive"
	 * @return the false positive rate
	 */
	public double falsePositiveRate(int classIndex) {

		double incorrect = 0, total = 0;
		for (int i = 0; i < m_NumClasses; i++) {
			if (i != classIndex) {
				for (int j = 0; j < m_NumClasses; j++) {
					if (j == classIndex) {
						incorrect += m_ConfusionMatrix[i][j];
					}
					total += m_ConfusionMatrix[i][j];
				}
			}
		}
		if (total == 0) {
			return 0;
		}
		return incorrect / total;
	}

	/**
	 * Calculates the weighted (by class size) false positive rate.
	 * 
	 * @return the weighted false positive rate.
	 */
	public double weightedFalsePositiveRate() {
		double[] classCounts = new double[m_NumClasses];
		double classCountSum = 0;

		for (int i = 0; i < m_NumClasses; i++) {
			for (int j = 0; j < m_NumClasses; j++) {
				classCounts[i] += m_ConfusionMatrix[i][j];
			}
			classCountSum += classCounts[i];
		}

		double falsePosTotal = 0;
		for (int i = 0; i < m_NumClasses; i++) {
			double temp = falsePositiveRate(i);
			falsePosTotal += (temp * classCounts[i]);
		}

		return falsePosTotal / classCountSum;
	}

	/**
	 * Calculate number of false negatives with respect to a particular class.
	 * This is defined as
	 * <p/>
	 * 
	 * <pre>
	 * incorrectly classified positives
	 * </pre>
	 * 
	 * @param classIndex
	 *            the index of the class to consider as "positive"
	 * @return the false positive rate
	 */
	public double numFalseNegatives(int classIndex) {

		double incorrect = 0;
		for (int i = 0; i < m_NumClasses; i++) {
			if (i == classIndex) {
				for (int j = 0; j < m_NumClasses; j++) {
					if (j != classIndex) {
						incorrect += m_ConfusionMatrix[i][j];
					}
				}
			}
		}
		return incorrect;
	}

	/**
	 * Calculate the false negative rate with respect to a particular class.
	 * This is defined as
	 * <p/>
	 * 
	 * <pre>
	 * incorrectly classified positives
	 * --------------------------------
	 *        total positives
	 * </pre>
	 * 
	 * @param classIndex
	 *            the index of the class to consider as "positive"
	 * @return the false positive rate
	 */
	public double falseNegativeRate(int classIndex) {

		double incorrect = 0, total = 0;
		for (int i = 0; i < m_NumClasses; i++) {
			if (i == classIndex) {
				for (int j = 0; j < m_NumClasses; j++) {
					if (j != classIndex) {
						incorrect += m_ConfusionMatrix[i][j];
					}
					total += m_ConfusionMatrix[i][j];
				}
			}
		}
		if (total == 0) {
			return 0;
		}
		return incorrect / total;
	}

	/**
	 * Calculates the weighted (by class size) false negative rate.
	 * 
	 * @return the weighted false negative rate.
	 */
	public double weightedFalseNegativeRate() {
		double[] classCounts = new double[m_NumClasses];
		double classCountSum = 0;

		for (int i = 0; i < m_NumClasses; i++) {
			for (int j = 0; j < m_NumClasses; j++) {
				classCounts[i] += m_ConfusionMatrix[i][j];
			}
			classCountSum += classCounts[i];
		}

		double falseNegTotal = 0;
		for (int i = 0; i < m_NumClasses; i++) {
			double temp = falseNegativeRate(i);
			falseNegTotal += (temp * classCounts[i]);
		}

		return falseNegTotal / classCountSum;
	}

	/**
	 * Calculates the matthews correlation coefficient (sometimes called phi
	 * coefficient) for the supplied class
	 * 
	 * @param classIndex
	 *            the index of the class to compute the matthews correlation
	 *            coefficient for
	 * 
	 * @return the mathews correlation coefficient
	 */
	public double matthewsCorrelationCoefficient(int classIndex) {
		double numTP = numTruePositives(classIndex);
		double numTN = numTrueNegatives(classIndex);
		double numFP = numFalsePositives(classIndex);
		double numFN = numFalseNegatives(classIndex);
		double n = (numTP * numTN) - (numFP * numFN);
		double d = (numTP + numFP) * (numTP + numFN) * (numTN + numFP)
				* (numTN + numFN);
		d = Math.sqrt(d);
		if (d == 0) {
			d = 1;
		}

		return n / d;
	}

	/**
	 * Calculate the recall with respect to a particular class. This is defined
	 * as
	 * <p/>
	 * 
	 * <pre>
	 * correctly classified positives
	 * ------------------------------
	 *       total positives
	 * </pre>
	 * <p/>
	 * (Which is also the same as the truePositiveRate.)
	 * 
	 * @param classIndex
	 *            the index of the class to consider as "positive"
	 * @return the recall
	 */
	public double recall(int classIndex) {

		return truePositiveRate(classIndex);
	}

	/**
	 * Calculates the weighted (by class size) recall.
	 * 
	 * @return the weighted recall.
	 */
	public double weightedRecall() {
		return weightedTruePositiveRate();
	}

	/**
	 * Calculate the precision with respect to a particular class. This is
	 * defined as
	 * <p/>
	 * 
	 * <pre>
	 * correctly classified positives
	 * ------------------------------
	 *  total predicted as positive
	 * </pre>
	 * 
	 * @param classIndex
	 *            the index of the class to consider as "positive"
	 * @return the precision
	 */
	public double precision(int classIndex) {

		double correct = 0, total = 0;
		for (int i = 0; i < m_NumClasses; i++) {
			if (i == classIndex) {
				correct += m_ConfusionMatrix[i][classIndex];
			}
			total += m_ConfusionMatrix[i][classIndex];
		}
		if (total == 0) {
			return 0;
		}
		return correct / total;
	}

	/**
	 * Calculates the weighted (by class size) precision.
	 * 
	 * @return the weighted precision.
	 */
	public double weightedPrecision() {
		double[] classCounts = new double[m_NumClasses];
		double classCountSum = 0;

		for (int i = 0; i < m_NumClasses; i++) {
			for (int j = 0; j < m_NumClasses; j++) {
				classCounts[i] += m_ConfusionMatrix[i][j];
			}
			classCountSum += classCounts[i];
		}

		double precisionTotal = 0;
		for (int i = 0; i < m_NumClasses; i++) {
			double temp = precision(i);
			precisionTotal += (temp * classCounts[i]);
		}

		return precisionTotal / classCountSum;
	}

	/**
	 * Calculate the F-Measure with respect to a particular class. This is
	 * defined as
	 * <p/>
	 * 
	 * <pre>
	 * 2 * recall * precision
	 * ----------------------
	 *   recall + precision
	 * </pre>
	 * 
	 * @param classIndex
	 *            the index of the class to consider as "positive"
	 * @return the F-Measure
	 */
	public double fMeasure(int classIndex) {

		double precision = precision(classIndex);
		double recall = recall(classIndex);
		if ((precision + recall) == 0) {
			return 0;
		}
		return 2 * precision * recall / (precision + recall);
	}

	/**
	 * Calculates the macro weighted (by class size) average F-Measure.
	 * 
	 * @return the weighted F-Measure.
	 */
	public double weightedFMeasure() {
		double[] classCounts = new double[m_NumClasses];
		double classCountSum = 0;

		for (int i = 0; i < m_NumClasses; i++) {
			for (int j = 0; j < m_NumClasses; j++) {
				classCounts[i] += m_ConfusionMatrix[i][j];
			}
			classCountSum += classCounts[i];
		}

		double fMeasureTotal = 0;
		for (int i = 0; i < m_NumClasses; i++) {
			double temp = fMeasure(i);
			fMeasureTotal += (temp * classCounts[i]);
		}

		return fMeasureTotal / classCountSum;
	}

	/**
	 * Unweighted macro-averaged F-measure. If some classes not present in the
	 * test set, they're just skipped (since recall is undefined there anyway) .
	 * 
	 * @return unweighted macro-averaged F-measure.
	 * */
	public double unweightedMacroFmeasure() {
		weka.experiment.Stats rr = new weka.experiment.Stats();
		for (int c = 0; c < m_NumClasses; c++) {
			// skip if no testing positive cases of this class
			if (numTruePositives(c) + numFalseNegatives(c) > 0) {
				rr.add(fMeasure(c));
			}
		}
		rr.calculateDerived();
		return rr.mean;
	}

	/**
	 * Unweighted micro-averaged F-measure. If some classes not present in the
	 * test set, they have no effect.
	 * 
	 * Note: if the test set is *single-label*, then this is the same as
	 * accuracy.
	 * 
	 * @return unweighted micro-averaged F-measure.
	 */
	public double unweightedMicroFmeasure() {
		double tp = 0;
		double fn = 0;
		double fp = 0;
		for (int c = 0; c < m_NumClasses; c++) {
			tp += numTruePositives(c);
			fn += numFalseNegatives(c);
			fp += numFalsePositives(c);
		}
		return 2 * tp / (2 * tp + fn + fp);
	}

	public final double kappa() {

		double[] sumRows = new double[m_ConfusionMatrix.length];
		double[] sumColumns = new double[m_ConfusionMatrix.length];
		double sumOfWeights = 0;
		for (int i = 0; i < m_ConfusionMatrix.length; i++) {
			for (int j = 0; j < m_ConfusionMatrix.length; j++) {
				sumRows[i] += m_ConfusionMatrix[i][j];
				sumColumns[j] += m_ConfusionMatrix[i][j];
				sumOfWeights += m_ConfusionMatrix[i][j];
			}
		}
		double correct = 0, chanceAgreement = 0;
		for (int i = 0; i < m_ConfusionMatrix.length; i++) {
			chanceAgreement += (sumRows[i] * sumColumns[i]);
			correct += m_ConfusionMatrix[i][i];
		}
		chanceAgreement /= (sumOfWeights * sumOfWeights);
		correct /= sumOfWeights;

		if (chanceAgreement < 1) {
			return (correct - chanceAgreement) / (1 - chanceAgreement);
		} else {
			return 1;
		}
	}

}
