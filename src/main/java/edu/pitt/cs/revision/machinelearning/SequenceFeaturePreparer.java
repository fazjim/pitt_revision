package edu.pitt.cs.revision.machinelearning;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import edu.pitt.lrdc.cs.revision.io.RevisionDocumentReader;
import edu.pitt.lrdc.cs.revision.model.RevisionDocument;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;

public class SequenceFeaturePreparer {
	Hashtable<String, double[]> featureTable;
	Hashtable<String, Double> classValues;

	public double[] fetchFeatures(RevisionDocument doc, int oldIndex,
			int newIndex) {
		double[] featuresAndLabels;
		String label = oldIndex + "-" + newIndex;
		label = doc.getDocumentName() + "_" + label;
		double[] features = featureTable.get(label);
		double classValue = classValues.get(label);
		featuresAndLabels = new double[features.length + 1];
		featuresAndLabels[0] = classValue;
		for (int i = 0; i < features.length; i++) {
			featuresAndLabels[i + 1] = features[i];
		}
		return featuresAndLabels;
	}

	public double fetchValue(RevisionDocument doc, int oldIndex, int newIndex) {
		String label = oldIndex + "-" + newIndex;
		label = doc.getDocumentName() + "_" + label;
		if (classValues.containsKey(label)) {
			return classValues.get(label);
		} else {
			return 0.0;
		}
	}

	public void test(ArrayList<RevisionDocument> trainData,
			ArrayList<RevisionDocument> testData, int k, int option,
			boolean usingNgram, int remove) throws Exception {
		SequenceProcessor sp = new SequenceProcessor();
		Instances trainInstances = null;
		Instances testInstances = null;
		Hashtable<String, String> realTagTable = new Hashtable<String, String>();
		trainInstances = sp.getInstances(trainData, realTagTable, k,
				usingNgram, option, remove);
		testInstances = sp.getInstances(testData, realTagTable, k, usingNgram,
				option, remove);
		prepareFeatures(trainInstances, testInstances, k, usingNgram);

		System.out.println("for testing");
	}

	public static void main(String[] args) throws Exception {
		ArrayList<RevisionDocument> trainDocs = RevisionDocumentReader
				.readDocs("C:\\Not Backed Up\\data\\naaclData\\C1");
		ArrayList<RevisionDocument> testDocs = RevisionDocumentReader
				.readDocs("C:\\Not Backed Up\\data\\naaclData\\C2");
		SequenceFeaturePreparer sp = new SequenceFeaturePreparer();
		sp.test(trainDocs, testDocs, 2, 2, true, 11);
	}

	/**
	 * Store the information of the features and the class values
	 * 
	 * @param trainInstances
	 * @param testInstances
	 * @param features
	 * @param classValues
	 * @param usingNgram
	 * @param k
	 *            (the context size)
	 * @throws Exception
	 */
	public void prepareFeatures(Instances trainInstances,
			Instances testInstances, int k, boolean usingNgram)
			throws Exception {
		featureTable = new Hashtable<String, double[]>();
		classValues = new Hashtable<String, Double>();

		List<String> trainIds = new ArrayList<String>();
		List<String> testIds = new ArrayList<String>();

		int featureLength = trainInstances.numAttributes();

		int attrIndexID = -1;
		for (int i = 0; i < featureLength; i++) {
			if (trainInstances.attribute(i).name().equals("ID")) {
				attrIndexID = i;
			}
		}
		for (int i = 0; i < trainInstances.size(); i++) {
			Instance ins = trainInstances.get(i);
			String value = "";
			if (attrIndexID != -1) {
				value = ins.stringValue(attrIndexID);
				// value = ins.attribute(attrIndexID).toString();
			} else {
				System.err.println("Something is wrong!");
			}
			trainIds.add(value);
		}

		for (int i = 0; i < testInstances.size(); i++) {
			Instance ins = testInstances.get(i);
			String value = "";
			if (attrIndexID != -1) {
				value = ins.stringValue(attrIndexID);
			} else {
				System.err.println("Something is wrong!");
			}
			testIds.add(value);
		}

		List<String> textColumns = new ArrayList<String>();
		textColumns.add("Text");
		for (int i = 1; i <= k; i++) {
			for (int j = 1; j <= k; j++) {
				textColumns.add("TEXTDIFF_" + i + "_" + j);
			}
		}
		WekaAssist wa = new WekaAssist();
		// trainInstances = wa.removeID(trainInstances);
		// testInstances = wa.removeID(testInstances);
		Instances[] trainTestInstances = new Instances[2];

		/*
		 * Attribute textAttr = trainInstances.attribute("Text"); for(int i = 0;
		 * i<500; i++) { Instance test = trainInstances.get(i);
		 * System.out.println(test.stringValue(textAttr)); }
		 */
		trainInstances = WekaAssist.removeID(trainInstances);
		testInstances = WekaAssist.removeID(testInstances);
		
		if (usingNgram) {
			trainTestInstances = wa.addNgram(trainInstances, testInstances,
					textColumns);
			trainTestInstances = WekaAssist.selectFeatures(
					trainTestInstances[0], trainTestInstances[1]);
		} else {
			trainTestInstances[0] = trainInstances;
			trainTestInstances[1] = testInstances;
		}

		Instances newTrains = trainTestInstances[0];
		Instances newTest = trainTestInstances[1];
		int featureNum = newTrains.numAttributes();
		for (int i = 0; i < newTrains.size(); i++) {
			Instance ins = newTrains.get(i);
			String id = trainIds.get(i);
			if (!featureTable.containsKey(id)) {
				double[] featureValues = new double[featureNum - 1];
				int index = 0;
				for (int j = 0; j < featureNum; j++) {
					if (j != ins.classIndex()) {
						featureValues[index] = ins.value(j);
						index++;
					}
				}
				featureTable.put(id, featureValues);
				classValues.put(id, ins.classValue());
			}
		}

		for (int i = 0; i < newTest.size(); i++) {
			Instance ins = newTest.get(i);
			String id = testIds.get(i);
			if (!featureTable.containsKey(id)) {
				double[] featureValues = new double[featureNum - 1];
				int index = 0;
				for (int j = 0; j < featureNum; j++) {
					if (j != ins.classIndex()) {
						featureValues[index] = ins.value(j);
						index++;
					}
				}
				featureTable.put(id, featureValues);
				classValues.put(id, ins.classValue());
			}
		}
	}
}
