package edu.pitt.lrdc.cs.revision.alignment;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;

import edu.pitt.lrdc.cs.revision.alignment.distance.LDCalculator;
import edu.pitt.lrdc.cs.revision.alignment.distance.TFIDFCounter;
import edu.pitt.lrdc.cs.revision.alignment.model.Document;
import edu.pitt.lrdc.cs.revision.alignment.model.DocumentPair;
import edu.pitt.lrdc.cs.revision.alignment.model.Sentence;
import edu.pitt.lrdc.cs.revision.model.RevisionDocument;
import weka.core.DenseInstance;
import weka.core.Instance;

public class LDAligner {
	LDCalculator ldc = new LDCalculator();
	TFIDFCounter counter = new TFIDFCounter();
	ArrayList<DocumentPair> docPairs;

	LRAligner aligner = new LRAligner();

	public LRAligner getAligner() {
		return this.aligner;
	}

	private boolean printVerbose = false;

	/**
	 * 
	 * @param docs
	 */
	public void load(ArrayList<RevisionDocument> docs) {
		docPairs = RevisionDocumentAdapter.getDocumentPairsFromDocs(docs);
		counter.loadTable(docPairs);
		aligner.setCounter(counter);
		if (printVerbose)
			System.out.println("Finished loading the alignment tool");
	}

	/**
	 * Generate the alignment probability matrix for each document pair with a
	 * logistic regression classifier
	 * 
	 * @param trainDocs
	 * @param testDocs
	 * @param option
	 * @return
	 * @throws Exception
	 */
	public Hashtable<DocumentPair, double[][]> getProbMatrixTable(
			ArrayList<RevisionDocument> trainDocs,
			ArrayList<RevisionDocument> testDocs, int option) throws Exception {
		if (printVerbose)
			System.out.println("LOADING...");
		docPairs = new ArrayList<DocumentPair>();
		ArrayList<DocumentPair> trainPairs = RevisionDocumentAdapter
				.getDocumentPairsFromDocs(trainDocs);
		ArrayList<DocumentPair> testDps = RevisionDocumentAdapter
				.getDocumentPairsFromDocs(testDocs);
		docPairs.addAll(trainPairs);
		docPairs.addAll(testDps);

		counter.loadTable(docPairs);
		aligner.setCounter(counter);
		if (printVerbose)
			System.out.println("DATA LOADED...");

		aligner.train(trainPairs, option);
		if (printVerbose) {
			System.out.println("Aligner trained...");
			System.out.println("Classifying....");
		}
		// aligner.classify(trainAndTest.get(1),option);
		// ArrayList<double[][]> vals = aligner.classifyDocuments(docPairs,
		// option);
		// aligner.evaluate(vals, docPairs);
		// aligner.classify(trainAndTest.get(1),option);
		ArrayList<double[][]> vals = aligner.classifyDocuments(testDps, option);
		Hashtable<DocumentPair, double[][]> ann = new Hashtable<DocumentPair, double[][]>();
		for (int i = 0; i < testDps.size(); i++) {
			ann.put(testDps.get(i), vals.get(i));
		}
		return ann;
	}

	
	/**
	 * Just use Levenshtein distance
	 * @param testDocs
	 * @param option
	 * @return
	 * @throws Exception
	 */
	public Hashtable<DocumentPair, double[][]> getProbMatrixTable(
			ArrayList<RevisionDocument> testDocs) throws Exception {
		if (printVerbose)
			System.out.println("LOADING...");
		docPairs = new ArrayList<DocumentPair>();

		ArrayList<DocumentPair> testDps = RevisionDocumentAdapter
				.getDocumentPairsFromDocs(testDocs);
	
		aligner.loadClassifier();
		if (printVerbose) {
			System.out.println("Aligner trained...");
			System.out.println("Classifying....");
		}
		// aligner.classify(trainAndTest.get(1),option);
		// ArrayList<double[][]> vals = aligner.classifyDocuments(docPairs,
		// option);
		// aligner.evaluate(vals, docPairs);
		// aligner.classify(trainAndTest.get(1),option);
		ArrayList<double[][]> vals = aligner.classifyDocuments(testDps, 0);
		Hashtable<DocumentPair, double[][]> ann = new Hashtable<DocumentPair, double[][]>();
		for (int i = 0; i < testDps.size(); i++) {
			ann.put(testDps.get(i), vals.get(i));
		}
		return ann;
	}


	
	/**
	 * Generate a new distribution of alignments with Dynamic programming to
	 * consider the sequence order information
	 * 
	 * @param docPair
	 * @param sim
	 * @param option
	 * @return
	 */
	public double[][] alignWithDP(DocumentPair docPair, double[][] sim,
			int option) {
		Document src = docPair.getSrc();
		Document dst = docPair.getModified();
		ArrayList<Sentence> srcSentences = src.getSentences();
		ArrayList<Sentence> dstSentences = dst.getSentences();
		int srcSize = srcSentences.size();
		int dstSize = dstSentences.size();

		double d = 0.1;

		double[][] dp = new double[srcSize][dstSize];
		double[][] aligns = new double[srcSize][dstSize];
		for (int i = 0; i < srcSize; i++) {
			for (int j = 0; j < dstSize; j++) {
				if (i == 0) {
					dp[0][j] = j * d;
				} else if (j == 0) {
					dp[i][0] = i * d;
				} else {
					double max = Integer.MIN_VALUE;
					double a, b, c = Integer.MIN_VALUE;
					if (i >= 1) {
						// a = dp[i - 1][j] + sim[i][j];
						a = dp[i - 1][j] + d;
						if (a > max)
							max = a;
					}
					if (j >= 1) {
						// b = dp[i][j - 1] + sim[i][j];
						b = dp[i][j - 1] + d;
						if (b > max)
							max = b;
					}
					if (i >= 1 && j >= 1) {
						c = dp[i - 1][j - 1] + sim[i][j];
						// c = dp[i - 1][j - 1] + d;
						if (c > max)
							max = c;
					}
					dp[i][j] = max;
				}
			}
		}

		int i = srcSize - 1;
		int j = dstSize - 1;
		while (i > 0 && j > 0) {
			if (i > 0 && j > 0 && dp[i][j] == dp[i - 1][j - 1] + sim[i][j]) {
				aligns[i][j] = 1;
				i--;
				j--;
			} else if (i > 0 && dp[i][j] == dp[i - 1][j] + d) {
				i = i - 1;
			} else if (j > 0 && dp[i][j] == dp[i][j - 1] + d) {
				j = j - 1;
			}
		}
		aligns[0][0] = 1;
		return aligns;
	}

	/*
	 * // --------------------------------Functions below this line are used for
	 * // the old alignment project, just for reference ---------------------//
	 * public void load(String path) throws IOException {
	 * System.out.println("LOADING..."); docPairs = new
	 * ArrayList<DocumentPair>(); ExcelReader reader = new ExcelReader();
	 * 
	 * File folder = new File(path); File[] subs = folder.listFiles(); for (int
	 * i = 0; i < subs.length; i++) { DocumentPair dp =
	 * reader.readDocs(subs[i].getAbsolutePath()); docPairs.add(dp); }
	 * counter.loadTable(docPairs); aligner.setCounter(counter);
	 * System.out.println("DATA LOADED...");
	 * 
	 * }
	 * 
	 * public void align(String path, String path2, int option, String
	 * resultPath) throws IOException { ExcelReader reader = new ExcelReader();
	 * ArrayList<DocumentPair> testDps = new ArrayList<DocumentPair>(); File
	 * folder2 = new File(path2); File[] subs2 = folder2.listFiles(); for (int i
	 * = 0; i < subs2.length; i++) { DocumentPair dp =
	 * reader.readDocs(subs2[i].getAbsolutePath()); // docPairs.add(dp);
	 * testDps.add(dp); } // Start Aligning ArrayList<ArrayList<String>>
	 * sequences = alignOnSequenceDP(testDps); for (int i = 0; i <
	 * sequences.size(); i++) { String fileName = resultPath + "/" + i + ".txt";
	 * writeSequence2(fileName, sequences.get(i)); } }
	 * 
	 * 
	 * public ArrayList<ArrayList<String>> alignOnSequenceDP(
	 * ArrayList<DocumentPair> dps) { ArrayList<ArrayList<String>> editSequences
	 * = new ArrayList<ArrayList<String>>(); for (int i = 0; i < dps.size();
	 * i++) { DocumentPair dp = dps.get(i); Document srcDoc = dp.getSrc();
	 * Document destDoc = dp.getModified(); //
	 * srcDoc.getSentences().get(i).getAlignedIndex(); ArrayList<Integer>
	 * srcIndexes = new ArrayList<Integer>(); for (int j = 0; j <
	 * srcDoc.getSentences().size(); j++) { srcIndexes.add(j + 1); }
	 * 
	 * ArrayList<Integer> destIndexes = new ArrayList<Integer>(); for (int j =
	 * 0; j < destDoc.getSentences().size(); j++) { if
	 * (destDoc.getSentences().get(j).getAlignedIndex().size() > 0) {
	 * destIndexes.add(destDoc.getSentences().get(j) .getAlignedIndex().get(0));
	 * } else { destIndexes.add(-1); } }
	 * 
	 * int addcost = 1; int deletecost = 1; int modcost = 1;
	 * 
	 * int srcSize = srcIndexes.size(); int destSize = destIndexes.size();
	 * 
	 * int[][] dpCost = new int[srcSize][destSize]; String[][] oper = new
	 * String[srcSize][destSize]; for (int m = 0; m < srcSize; m++) { for (int n
	 * = 0; n < destSize; n++) { if (m == 0 || n == 0) { dpCost[m][n] = (m + n)
	 * * addcost; oper[m][n] = "ADD"; } else { int add = dpCost[m][n - 1] +
	 * addcost; int delete = dpCost[m - 1][n] + deletecost; int modify =
	 * dpCost[m - 1][n - 1] + modcost; int nochange = Integer.MAX_VALUE; if
	 * (srcIndexes.get(m) == destIndexes.get(n)) { nochange = dpCost[m - 1][n -
	 * 1]; }
	 * 
	 * int smallest = Integer.MAX_VALUE; if (add < smallest) smallest = add; if
	 * (delete < smallest) smallest = delete; if (modify < smallest) smallest =
	 * modify; if (nochange < smallest) smallest = nochange;
	 * 
	 * if (add == smallest) { oper[m][n] = "ADD"; } else if (delete == smallest)
	 * { oper[m][n] = "DELETE"; } else if (modify == smallest) { oper[m][n] =
	 * "MODIFY"; } else if (modify == nochange) { oper[m][n] = "KEEP"; }
	 * dpCost[m][n] = smallest; } } }
	 * 
	 * ArrayList<String> editSequence = new ArrayList<String>(); int m = srcSize
	 * - 1; int n = destSize - 1; while (m >= 1 || n >= 1) { if (dpCost[m][n] ==
	 * dpCost[m][n - 1] + addcost) { editSequence.add("ADD"); n--; } else if
	 * (dpCost[m][n] == dpCost[m - 1][n] + deletecost) {
	 * editSequence.add("DELETE"); m--; } else if (dpCost[m][n] == dpCost[m -
	 * 1][n - 1] + modcost) { editSequence.add("REPLACE"); m--; n--; } else {
	 * editSequence.add("KEEP"); m--; n--; } } ArrayList<String> editSequenceRev
	 * = new ArrayList<String>(); for (int l = editSequence.size() - 1; l >= 0;
	 * l--) { editSequenceRev.add(editSequence.get(l)); }
	 * editSequences.add(editSequenceRev); } return editSequences; }
	 * 
	 * public void alignExactMatchBaseline(String path) throws IOException {
	 * docPairs = new ArrayList<DocumentPair>(); ExcelReader reader = new
	 * ExcelReader(); File folder = new File(path); File[] subs =
	 * folder.listFiles();
	 * 
	 * for (int i = 0; i < subs.length; i++) { DocumentPair dp =
	 * reader.readDocs(subs[i].getAbsolutePath()); docPairs.add(dp); }
	 * 
	 * int total = 0; int originalTotal = 0; int tp = 0; int adds = 0; for (int
	 * i = 0; i < docPairs.size(); i++) { DocumentPair docPair =
	 * docPairs.get(i); Document dst = docPair.getModified(); Document src =
	 * docPair.getSrc(); originalTotal += src.getSentences().size(); for (int j
	 * = 0; j < dst.getSentences().size(); j++) { ArrayList<Integer>
	 * predictedIndex = new ArrayList<Integer>(); for (int k = 0; k <
	 * src.getSentences().size(); k++) { if
	 * (exactMatch(dst.getSentences().get(j), src .getSentences().get(k))) {
	 * predictedIndex.add(k + 1); } }
	 * dst.getSentences().get(j).setPredictedIndex(predictedIndex); total++; if
	 * (aligner.compare( dst.getSentences().get(j).getAlignedIndex(), dst
	 * .getSentences().get(j).getPredictedIndex())) { tp++; } if
	 * (dst.getSentences().get(j).getAlignedIndex().size() == 0) { adds++; } } }
	 * System.out.println("Original sentences:" + originalTotal);
	 * System.out.println("ADDS:" + adds); System.out.println("Accuracy is: " +
	 * tp + " /" + total + "=" + (tp * 1.0) / total); }
	 * 
	 * public Hashtable<DocumentPair, double[][]> annotate(String path, String
	 * path2, int option) throws Exception { System.out.println("LOADING...");
	 * docPairs = new ArrayList<DocumentPair>(); ArrayList<DocumentPair>
	 * trainPairs = new ArrayList<DocumentPair>(); ExcelReader reader = new
	 * ExcelReader(); File folder = new File(path); File[] subs =
	 * folder.listFiles(); for (int i = 0; i < subs.length; i++) { DocumentPair
	 * dp = reader.readDocs(subs[i].getAbsolutePath()); docPairs.add(dp);
	 * trainPairs.add(dp); }
	 * 
	 * ArrayList<DocumentPair> testDps = new ArrayList<DocumentPair>(); File
	 * folder2 = new File(path2); File[] subs2 = folder2.listFiles(); for (int i
	 * = 0; i < subs2.length; i++) { DocumentPair dp =
	 * reader.readDocs(subs2[i].getAbsolutePath()); docPairs.add(dp);
	 * testDps.add(dp); } counter.loadTable(docPairs);
	 * aligner.setCounter(counter); System.out.println("DATA LOADED...");
	 * 
	 * aligner.train(trainPairs, option);
	 * System.out.println("Aligner trained...");
	 * System.out.println("Classifying....");
	 * 
	 * // aligner.classify(trainAndTest.get(1),option); // ArrayList<double[][]>
	 * vals = aligner.classifyDocuments(docPairs, // option); //
	 * aligner.evaluate(vals, docPairs); //
	 * aligner.classify(trainAndTest.get(1),option); ArrayList<double[][]> vals
	 * = aligner.classifyDocuments(testDps, option); Hashtable<DocumentPair,
	 * double[][]> ann = new Hashtable<DocumentPair, double[][]>(); for (int i =
	 * 0; i < testDps.size(); i++) { ann.put(testDps.get(i), vals.get(i)); }
	 * return ann; }
	 * 
	 * public void testOnDifferentTopic(String path, String path2, int option)
	 * throws Exception { System.out.println("LOADING..."); docPairs = new
	 * ArrayList<DocumentPair>(); ArrayList<DocumentPair> trainPairs = new
	 * ArrayList<DocumentPair>(); ExcelReader reader = new ExcelReader(); File
	 * folder = new File(path); File[] subs = folder.listFiles(); for (int i =
	 * 0; i < subs.length; i++) { DocumentPair dp =
	 * reader.readDocs(subs[i].getAbsolutePath()); docPairs.add(dp);
	 * trainPairs.add(dp); }
	 * 
	 * ArrayList<DocumentPair> testDps = new ArrayList<DocumentPair>(); File
	 * folder2 = new File(path2); File[] subs2 = folder2.listFiles(); for (int i
	 * = 0; i < subs2.length; i++) { DocumentPair dp =
	 * reader.readDocs(subs2[i].getAbsolutePath()); docPairs.add(dp);
	 * testDps.add(dp); } counter.loadTable(docPairs);
	 * aligner.setCounter(counter); System.out.println("DATA LOADED...");
	 * 
	 * aligner.train(trainPairs, option);
	 * System.out.println("Aligner trained...");
	 * System.out.println("Classifying....");
	 * 
	 * // aligner.classify(trainAndTest.get(1),option); // ArrayList<double[][]>
	 * vals = aligner.classifyDocuments(docPairs, // option); //
	 * aligner.evaluate(vals, docPairs); //
	 * aligner.classify(trainAndTest.get(1),option); ArrayList<double[][]> vals
	 * = aligner.classifyDocuments(testDps, option); aligner.evaluate2(vals,
	 * testDps); System.out.println("Classification Done!");
	 * 
	 * }
	 * 
	 * public void testOnDifferentTopicVote(String path, String path2, int
	 * option) throws Exception { System.out.println("LOADING..."); docPairs =
	 * new ArrayList<DocumentPair>(); ArrayList<DocumentPair> trainPairs = new
	 * ArrayList<DocumentPair>(); ExcelReader reader = new ExcelReader(); File
	 * folder = new File(path); File[] subs = folder.listFiles(); for (int i =
	 * 0; i < subs.length; i++) { DocumentPair dp =
	 * reader.readDocs(subs[i].getAbsolutePath()); docPairs.add(dp);
	 * trainPairs.add(dp); }
	 * 
	 * ArrayList<DocumentPair> testDps = new ArrayList<DocumentPair>(); File
	 * folder2 = new File(path2); File[] subs2 = folder2.listFiles(); for (int i
	 * = 0; i < subs2.length; i++) { DocumentPair dp =
	 * reader.readDocs(subs2[i].getAbsolutePath()); docPairs.add(dp);
	 * testDps.add(dp); } counter.loadTable(docPairs);
	 * aligner.setCounter(counter); System.out.println("DATA LOADED...");
	 * 
	 * option = 0; aligner.train(trainPairs, option);
	 * System.out.println("Aligner trained...");
	 * System.out.println("Classifying....");
	 * 
	 * // aligner.classify(trainAndTest.get(1),option); // ArrayList<double[][]>
	 * vals = aligner.classifyDocuments(docPairs, // option); //
	 * aligner.evaluate(vals, docPairs); //
	 * aligner.classify(trainAndTest.get(1),option); ArrayList<double[][]> vals
	 * = aligner.classifyDocuments(testDps, option);
	 * 
	 * option = 1; aligner.train(trainPairs, option);
	 * System.out.println("Aligner trained...");
	 * System.out.println("Classifying....");
	 * 
	 * ArrayList<double[][]> vals1 = aligner .classifyDocuments(testDps,
	 * option);
	 * 
	 * option = 2; aligner.train(trainPairs, option);
	 * System.out.println("Aligner trained...");
	 * System.out.println("Classifying....");
	 * 
	 * // aligner.classify(trainAndTest.get(1),option); // ArrayList<double[][]>
	 * vals = aligner.classifyDocuments(docPairs, // option); //
	 * aligner.evaluate(vals, docPairs); //
	 * aligner.classify(trainAndTest.get(1),option); ArrayList<double[][]> vals2
	 * = aligner .classifyDocuments(testDps, option);
	 * 
	 * aligner.evaluateVote(vals, vals1, vals2, testDps);
	 * System.out.println("Classification Done!");
	 * 
	 * }
	 * 
	 * public void testOnDifferentTopicMix(String path, String path2, int[]
	 * options) throws Exception { System.out.println("LOADING..."); docPairs =
	 * new ArrayList<DocumentPair>(); ArrayList<DocumentPair> trainPairs = new
	 * ArrayList<DocumentPair>(); ExcelReader reader = new ExcelReader(); File
	 * folder = new File(path); File[] subs = folder.listFiles(); for (int i =
	 * 0; i < subs.length; i++) { DocumentPair dp =
	 * reader.readDocs(subs[i].getAbsolutePath()); docPairs.add(dp);
	 * trainPairs.add(dp); }
	 * 
	 * ArrayList<DocumentPair> testDps = new ArrayList<DocumentPair>(); File
	 * folder2 = new File(path2); File[] subs2 = folder2.listFiles(); for (int i
	 * = 0; i < subs2.length; i++) { DocumentPair dp =
	 * reader.readDocs(subs2[i].getAbsolutePath()); docPairs.add(dp);
	 * testDps.add(dp); } counter.loadTable(docPairs);
	 * aligner.setCounter(counter); System.out.println("DATA LOADED...");
	 * 
	 * aligner.trainMix(trainPairs, options);
	 * System.out.println("Aligner trained...");
	 * System.out.println("Classifying....");
	 * 
	 * // aligner.classify(trainAndTest.get(1),option); // ArrayList<double[][]>
	 * vals = aligner.classifyDocuments(docPairs, // option); //
	 * aligner.evaluate(vals, docPairs); //
	 * aligner.classify(trainAndTest.get(1),option); ArrayList<double[][]> vals
	 * = aligner.classifyDocumentsMix(testDps, options); aligner.evaluate2(vals,
	 * testDps); System.out.println("Classification Done!");
	 * 
	 * }
	 * 
	 * public int getModCost(ArrayList<Sentence> srcSentences,
	 * ArrayList<Sentence> sentences, int originalIndex, int modifiedIndex) {
	 * ArrayList<Integer> predictedIndex = sentences.get(modifiedIndex)
	 * .getPredictedIndex(); for (int i = 0; i < predictedIndex.size(); i++) {
	 * int matchedIndex = predictedIndex.get(i); if (matchedIndex - 1 ==
	 * originalIndex) { if (exactMatch(srcSentences.get(originalIndex),
	 * sentences.get(modifiedIndex))) { return 0; } else { return 1; } } }
	 * return 1000; }
	 * 
	 * public ArrayList<ArrayList<String>> alignOnSequence3(
	 * ArrayList<DocumentPair> dps) { ArrayList<ArrayList<String>> editSequences
	 * = new ArrayList<ArrayList<String>>(); for (int i = 0; i < dps.size();
	 * i++) { DocumentPair dp = dps.get(i); ArrayList<String> editSequence = new
	 * ArrayList<String>(); Document modified = dp.getModified(); Document org =
	 * dp.getSrc(); int orgLength = org.getSentences().size(); int newLength =
	 * modified.getSentences().size(); int j = 0; int k = 0; while (j <
	 * orgLength || k < newLength) { ArrayList<Integer> alignedIndex =
	 * modified.getSentences() .get(k).getAlignedIndex(); boolean found = false;
	 * boolean isSmaller = false; for (int index = 0; index <
	 * alignedIndex.size(); index++) { int predictedIndex =
	 * alignedIndex.get(index); if (predictedIndex - 1 == j) found = true; if
	 * (predictedIndex - 1 > j) isSmaller = true; } if (found == true) { if
	 * (exactMatch(org.getSentences().get(j), modified .getSentences().get(k)))
	 * { editSequence.add("KEEP"); } else { editSequence.add("MODIFY"); } j++;
	 * k++; } else { if (isSmaller) { editSequence.add("DELETE"); j++; } else {
	 * editSequence.add("ADD"); k++; } } } editSequences.add(editSequence); }
	 * return editSequences; }
	 * 
	 * public ArrayList<ArrayList<String>> alignOnSequence2(
	 * ArrayList<DocumentPair> dps) { ArrayList<ArrayList<String>> editSequences
	 * = new ArrayList<ArrayList<String>>(); for (int i = 0; i < dps.size();
	 * i++) { DocumentPair dp = dps.get(i); ArrayList<String> editSequence = new
	 * ArrayList<String>(); Document modified = dp.getModified(); Document org =
	 * dp.getSrc(); int orgLength = org.getSentences().size(); int newLength =
	 * modified.getSentences().size(); int j = 0; int k = 0; while (j <
	 * orgLength && k < newLength) { ArrayList<Integer> alignedIndex =
	 * modified.getSentences() .get(k).getPredictedIndex(); boolean found =
	 * false; boolean isSmaller = false; for (int index = 0; index <
	 * alignedIndex.size(); index++) { int predictedIndex =
	 * alignedIndex.get(index); if (predictedIndex - 1 == j) found = true; if
	 * (predictedIndex - 1 > j) isSmaller = true; } if (found == true) { if
	 * (exactMatch(org.getSentences().get(j), modified .getSentences().get(k)))
	 * { editSequence.add("KEEP"); } else { editSequence.add("MODIFY"); } j++;
	 * k++; } else { if (isSmaller) { editSequence.add("DELETE"); j++; } else {
	 * editSequence.add("ADD"); k++; } } } editSequences.add(editSequence); }
	 * return editSequences; }
	 * 
	 * public ArrayList<ArrayList<String>> generateSequenceDP(
	 * ArrayList<DocumentPair> dps) throws IOException {
	 * ArrayList<ArrayList<String>> editSequences = new
	 * ArrayList<ArrayList<String>>(); for (int i = 0; i < dps.size(); i++) {
	 * DocumentPair dp = dps.get(i); ArrayList<String> editSequence = new
	 * ArrayList<String>();
	 * 
	 * } return editSequences; }
	 * 
	 * public ArrayList<ArrayList<String>> alignOnSequence(
	 * ArrayList<DocumentPair> dps) throws IOException { int ADDCOST = 1; int
	 * DELCOST = 1; ArrayList<ArrayList<String>> editSequences = new
	 * ArrayList<ArrayList<String>>(); for (int i = 0; i < dps.size(); i++) {
	 * DocumentPair dp = dps.get(i); ArrayList<String> editSequence = new
	 * ArrayList<String>(); Document modified = dp.getModified(); Document org =
	 * dp.getSrc(); int orgLength = org.getSentences().size(); int newLength =
	 * modified.getSentences().size(); int[][] editMatrix = new
	 * int[orgLength][newLength];
	 * 
	 * for (int j = 0; j < orgLength; j++) { for (int k = 0; k < newLength; k++)
	 * { if (j == 0) { editMatrix[j][k] = k * ADDCOST; } else if (k == 0) {
	 * editMatrix[j][k] = j * ADDCOST; } else { int addCost = editMatrix[j][k -
	 * 1] + ADDCOST; int deleteCost = editMatrix[j - 1][k] + DELCOST; int
	 * modCost = getModCost(org.getSentences(), modified.getSentences(), j - 1,
	 * k - 1); int minCost = addCost; if (deleteCost < minCost) minCost =
	 * deleteCost; if (modCost < 2) modCost = editMatrix[j - 1][k - 1] +
	 * modCost; if (modCost < minCost) minCost = modCost; editMatrix[j][k] =
	 * minCost; } } }
	 * 
	 * // Generate sequence int markerSrc = orgLength - 1; int markerMod =
	 * newLength - 1; while (markerSrc > 0 && markerMod > 0) { if (markerSrc > 0
	 * && markerMod > 0 && (editMatrix[markerSrc][markerMod] ==
	 * editMatrix[markerSrc - 1][markerMod - 1] + 1)) { markerSrc--;
	 * markerMod--; editSequence.add("MODIFY"); } else if (markerSrc > 0 &&
	 * markerMod > 0 && (editMatrix[markerSrc][markerMod] ==
	 * editMatrix[markerSrc - 1][markerMod - 1])) { markerSrc--; markerMod--;
	 * editSequence.add("KEEP"); } else if (markerSrc > 0 &&
	 * editMatrix[markerSrc][markerMod] == editMatrix[markerSrc - 1][markerMod]
	 * + DELCOST) { markerSrc = markerSrc - 1; editSequence.add("ADD"); } else
	 * if (markerMod > 0 && editMatrix[markerSrc][markerMod] ==
	 * editMatrix[markerSrc][markerMod - 1] + ADDCOST) { markerMod = markerMod -
	 * 1; editSequence.add("DELETE"); } } if (markerSrc == 0 && markerMod == 0
	 * && editMatrix[1][1] == 0) { editSequence.add("KEEP"); } else {
	 * editSequence.add("MODIFY"); } ArrayList<String> reversedSequence = new
	 * ArrayList<String>(); for (int m = editSequence.size() - 1; m >= 0; m--) {
	 * reversedSequence.add(editSequence.get(m)); }
	 * editSequences.add(reversedSequence);
	 * 
	 * } return editSequences; }
	 * 
	 * public ArrayList<String> alignOnSequenceDirectly(DocumentPair docPair,
	 * double[][] sim, int option) throws IOException { int ADDCOST = 2; int
	 * DELCOST = 2; ArrayList<String> editSequence = new ArrayList<String>();
	 * Document src = docPair.getSrc(); Document dst = docPair.getModified();
	 * ArrayList<Sentence> srcSentences = src.getSentences();
	 * ArrayList<Sentence> dstSentences = dst.getSentences(); int srcSize =
	 * srcSentences.size(); int dstSize = dstSentences.size();
	 * 
	 * // double[][] sim = new double[srcSize][dstSize]; // for(int i =
	 * 0;i<srcSize;i++) { // for(int j = 0;j<dstSize;j++) { // sim[i][j] =
	 * calc(srcSentences.get(i),dstSentences.get(j),option); // if(option == 0)
	 * { // sim[i][j] = 0 - sim[i][j]; // } // } // }
	 * 
	 * double d = 0.1;
	 * 
	 * double[][] dp = new double[srcSize][dstSize]; double[][] aligns = new
	 * double[srcSize][dstSize]; for (int i = 0; i < srcSize; i++) { for (int j
	 * = 0; j < dstSize; j++) { if (i == 0) { dp[0][j] = j * d; } else if (j ==
	 * 0) { dp[i][0] = i * d; } else { double max = Integer.MIN_VALUE; double a,
	 * b, c = Integer.MIN_VALUE; if (i >= 1) { // a = dp[i - 1][j] + sim[i][j];
	 * a = dp[i - 1][j] + d; if (a > max) max = a; } if (j >= 1) { // b =
	 * dp[i][j - 1] + sim[i][j]; b = dp[i][j - 1] + d; if (b > max) max = b; }
	 * if (i >= 1 && j >= 1) { c = dp[i - 1][j - 1] + sim[i][j]; // c = dp[i -
	 * 1][j - 1] + d; if (c > max) max = c; } dp[i][j] = max; } } }
	 * 
	 * int i = srcSize - 1; int j = dstSize - 1; while (i > 0 && j > 0) { if (i
	 * > 0 && j > 0 && dp[i][j] == dp[i - 1][j - 1] + sim[i][j]) { aligns[i][j]
	 * = 1; if (exactMatch(srcSentences.get(i - 1), dstSentences.get(j - 1))) {
	 * editSequence.add("KEEP"); } else { editSequence.add("MODIFY"); } i--;
	 * j--; } else if (i > 0 && dp[i][j] == dp[i - 1][j] + d) { i = i - 1;
	 * editSequence.add("DELETE"); } else if (j > 0 && dp[i][j] == dp[i][j - 1]
	 * + d) { j = j - 1; editSequence.add("ADD"); } } editSequence.add("KEEP");
	 * ArrayList<String> reversedSequence = new ArrayList<String>(); for (int m
	 * = editSequence.size() - 1; m >= 0; m--) {
	 * reversedSequence.add(editSequence.get(m)); } aligns[0][0] = 1; return
	 * reversedSequence; }
	 * 
	 * public void alignSequences(String path, String path2, int option, String
	 * resultPath) throws Exception { System.out.println("LOADING..."); docPairs
	 * = new ArrayList<DocumentPair>(); ArrayList<DocumentPair> trainPairs = new
	 * ArrayList<DocumentPair>(); ExcelReader reader = new ExcelReader(); File
	 * folder = new File(path); File[] subs = folder.listFiles(); for (int i =
	 * 0; i < subs.length; i++) { DocumentPair dp =
	 * reader.readDocs(subs[i].getAbsolutePath()); docPairs.add(dp);
	 * trainPairs.add(dp); }
	 * 
	 * ArrayList<DocumentPair> testDps = new ArrayList<DocumentPair>(); File
	 * folder2 = new File(path2); File[] subs2 = folder2.listFiles(); for (int i
	 * = 0; i < subs2.length; i++) { DocumentPair dp =
	 * reader.readDocs(subs2[i].getAbsolutePath()); docPairs.add(dp);
	 * testDps.add(dp); } counter.loadTable(docPairs);
	 * aligner.setCounter(counter); System.out.println("DATA LOADED...");
	 * 
	 * aligner.train(trainPairs, option);
	 * System.out.println("Aligner trained...");
	 * System.out.println("Classifying....");
	 * 
	 * // aligner.classify(trainAndTest.get(1),option); ArrayList<double[][]>
	 * vals2 = aligner .classifyDocuments(testDps, option); //
	 * aligner.evaluate(vals, docPairs); //
	 * aligner.classify(trainAndTest.get(1),option); // ArrayList<double[][]>
	 * vals = aligner.classifyDocuments(testDps, // option);
	 * ArrayList<double[][]> vals = new ArrayList<double[][]>(); for (int i = 0;
	 * i < testDps.size(); i++) { double[][] result = align_dp2(testDps.get(i),
	 * vals2.get(i), option); vals.add(result); } aligner.evaluate2(vals,
	 * testDps); System.out.println("Classification Done!");
	 * 
	 * // Start Aligning ArrayList<ArrayList<String>> sequences =
	 * alignOnSequence(testDps); for (int i = 0; i < sequences.size(); i++) {
	 * String fileName = resultPath + "/" + i + ".txt"; writeSequence(fileName,
	 * sequences.get(i)); } }
	 * 
	 * public void alignSequences2(String path, String path2, int option, String
	 * resultPath) throws Exception { System.out.println("LOADING..."); docPairs
	 * = new ArrayList<DocumentPair>(); ArrayList<DocumentPair> trainPairs = new
	 * ArrayList<DocumentPair>(); ExcelReader reader = new ExcelReader(); File
	 * folder = new File(path); File[] subs = folder.listFiles(); for (int i =
	 * 0; i < subs.length; i++) { DocumentPair dp =
	 * reader.readDocs(subs[i].getAbsolutePath()); docPairs.add(dp);
	 * trainPairs.add(dp); }
	 * 
	 * ArrayList<DocumentPair> testDps = new ArrayList<DocumentPair>(); File
	 * folder2 = new File(path2); File[] subs2 = folder2.listFiles(); for (int i
	 * = 0; i < subs2.length; i++) { DocumentPair dp =
	 * reader.readDocs(subs2[i].getAbsolutePath()); docPairs.add(dp);
	 * testDps.add(dp); } counter.loadTable(docPairs);
	 * aligner.setCounter(counter); System.out.println("DATA LOADED...");
	 * 
	 * aligner.train(trainPairs, option);
	 * System.out.println("Aligner trained...");
	 * System.out.println("Classifying....");
	 * 
	 * // aligner.classify(trainAndTest.get(1),option); ArrayList<double[][]>
	 * vals2 = aligner .classifyDocuments(testDps, option); //
	 * aligner.evaluate(vals, docPairs); //
	 * aligner.classify(trainAndTest.get(1),option); // ArrayList<double[][]>
	 * vals = aligner.classifyDocuments(testDps, // option);
	 * ArrayList<double[][]> vals = new ArrayList<double[][]>(); for (int i = 0;
	 * i < testDps.size(); i++) { double[][] result = align_dp2(testDps.get(i),
	 * vals2.get(i), option); vals.add(result); } aligner.evaluate2(vals,
	 * testDps); System.out.println("Classification Done!");
	 * 
	 * // Start Aligning ArrayList<ArrayList<String>> sequences =
	 * alignOnSequence2(testDps); for (int i = 0; i < sequences.size(); i++) {
	 * String fileName = resultPath + "/" + i + ".txt"; writeSequence(fileName,
	 * sequences.get(i)); } }
	 * 
	 * public double calcWER(ArrayList<ArrayList<String>> seqs,
	 * ArrayList<ArrayList<String>> seqs2) { int ADDCOST = 1; int DELCOST = 1;
	 * int MODICOST = 1;
	 * 
	 * int cost = 0; int total = 0; for (int i = 0; i < seqs.size(); i++) {
	 * ArrayList<String> seq1 = seqs.get(i); ArrayList<String> seq2 =
	 * seqs2.get(i); int length1 = seq1.size(); int length2 = seq2.size();
	 * 
	 * int[][] dp = new int[length1][length2]; if (length1 == 0) return length1
	 * * ADDCOST; if (length2 == 0) return length2 * ADDCOST; for (int j = 0; j
	 * < length1; j++) { for (int k = 0; k < length2; k++) { if (j == 0 && k ==
	 * 0) { dp[0][0] = 0; } else { int smallest = Integer.MAX_VALUE; int add =
	 * Integer.MAX_VALUE; int delete = Integer.MAX_VALUE; int nomove =
	 * Integer.MAX_VALUE;
	 * 
	 * if (k >= 1) { add = dp[j][k - 1] + ADDCOST; if (add < smallest) smallest
	 * = add; } if (j >= 1) { delete = dp[j - 1][k] + DELCOST; if (delete <
	 * smallest) smallest = delete; }
	 * 
	 * if (j >= 1 && k >= 1) { if (seq1.get(j).equals(seq2.get(k))) { nomove =
	 * dp[j - 1][k - 1]; } else { nomove = dp[j - 1][k - 1] + MODICOST; } if
	 * (nomove < smallest) smallest = nomove; } dp[j][k] = smallest; } } } cost
	 * += dp[length1 - 1][length2 - 1]; total += seq2.size(); } return (cost *
	 * 1.0) / total; }
	 * 
	 * public void calculateWER(String path, String path2, int option, String
	 * resultPath) throws Exception { System.out.println("LOADING..."); docPairs
	 * = new ArrayList<DocumentPair>(); ArrayList<DocumentPair> trainPairs = new
	 * ArrayList<DocumentPair>(); ExcelReader reader = new ExcelReader(); File
	 * folder = new File(path); File[] subs = folder.listFiles(); for (int i =
	 * 0; i < subs.length; i++) { DocumentPair dp =
	 * reader.readDocs(subs[i].getAbsolutePath()); docPairs.add(dp);
	 * trainPairs.add(dp); }
	 * 
	 * ArrayList<DocumentPair> testDps = new ArrayList<DocumentPair>(); File
	 * folder2 = new File(path2); File[] subs2 = folder2.listFiles(); for (int i
	 * = 0; i < subs2.length; i++) { DocumentPair dp =
	 * reader.readDocs(subs2[i].getAbsolutePath()); docPairs.add(dp);
	 * testDps.add(dp); } counter.loadTable(docPairs);
	 * aligner.setCounter(counter); System.out.println("DATA LOADED...");
	 * 
	 * aligner.train(trainPairs, option);
	 * System.out.println("Aligner trained...");
	 * System.out.println("Classifying....");
	 * 
	 * // aligner.classify(trainAndTest.get(1),option); ArrayList<double[][]>
	 * vals2 = aligner .classifyDocuments(testDps, option); //
	 * aligner.evaluate(vals, docPairs); //
	 * aligner.classify(trainAndTest.get(1),option); // ArrayList<double[][]>
	 * vals = aligner.classifyDocuments(testDps, // option);
	 * ArrayList<double[][]> vals = new ArrayList<double[][]>(); for (int i = 0;
	 * i < testDps.size(); i++) { double[][] result = align_dp2(testDps.get(i),
	 * vals2.get(i), option); vals.add(result); } aligner.evaluate2(vals,
	 * testDps); System.out.println("Classification Done!");
	 * 
	 * ArrayList<ArrayList<String>> sequencesDP = alignOnSequence(testDps);
	 * 
	 * ArrayList<ArrayList<String>> sequences2 = alignOnSequence2(testDps); //
	 * Start Aligning ArrayList<ArrayList<String>> sequences =
	 * alignOnSequence3(testDps);
	 * 
	 * ArrayList<ArrayList<String>> sequences4 = alignOnSequence4(path, path2,
	 * option);
	 * 
	 * int add = 0; int delete = 0; int modify = 0; for (int i = 0; i <
	 * sequences.size(); i++) { for (int j = 0; j < sequences.get(i).size();
	 * j++) { String tag = sequences.get(i).get(j); if (tag.equals("ADD")) {
	 * add++; } else if (tag.equals("DELETE")) { delete++; } else if
	 * (tag.equals("MODIFIED")) { modify++; } } } System.out.println("ADD:" +
	 * add + ",DELETE:" + delete + ",MODIFIED:" + modify);
	 * System.out.println("WER is: " + calcWER(sequences2, sequences));
	 * System.out.println("WER is: " + calcWER(sequencesDP, sequences));
	 * System.out.println("WER is: " + calcWER(sequences4, sequences)); }
	 * 
	 * public void alignSequences3(String path, String path2, int option, String
	 * resultPath) throws Exception { System.out.println("LOADING..."); docPairs
	 * = new ArrayList<DocumentPair>(); ArrayList<DocumentPair> trainPairs = new
	 * ArrayList<DocumentPair>(); ExcelReader reader = new ExcelReader(); File
	 * folder = new File(path); File[] subs = folder.listFiles(); for (int i =
	 * 0; i < subs.length; i++) { DocumentPair dp =
	 * reader.readDocs(subs[i].getAbsolutePath()); docPairs.add(dp);
	 * trainPairs.add(dp); }
	 * 
	 * ArrayList<DocumentPair> testDps = new ArrayList<DocumentPair>(); File
	 * folder2 = new File(path2); File[] subs2 = folder2.listFiles(); for (int i
	 * = 0; i < subs2.length; i++) { DocumentPair dp =
	 * reader.readDocs(subs2[i].getAbsolutePath()); docPairs.add(dp);
	 * testDps.add(dp); } counter.loadTable(docPairs);
	 * aligner.setCounter(counter); System.out.println("DATA LOADED...");
	 * 
	 * aligner.train(trainPairs, option);
	 * System.out.println("Aligner trained...");
	 * System.out.println("Classifying....");
	 * 
	 * // aligner.classify(trainAndTest.get(1),option); ArrayList<double[][]>
	 * vals2 = aligner .classifyDocuments(testDps, option); //
	 * aligner.evaluate(vals, docPairs); //
	 * aligner.classify(trainAndTest.get(1),option); // ArrayList<double[][]>
	 * vals = aligner.classifyDocuments(testDps, // option);
	 * ArrayList<double[][]> vals = new ArrayList<double[][]>(); for (int i = 0;
	 * i < testDps.size(); i++) { double[][] result = align_dp2(testDps.get(i),
	 * vals2.get(i), option); vals.add(result); } aligner.evaluate2(vals,
	 * testDps); System.out.println("Classification Done!");
	 * 
	 * // Start Aligning ArrayList<ArrayList<String>> sequences =
	 * alignOnSequence(testDps); for (int i = 0; i < sequences.size(); i++) {
	 * String fileName = resultPath + "/" + i + ".txt"; writeSequence2(fileName,
	 * sequences.get(i)); } }
	 * 
	 * public ArrayList<ArrayList<String>> alignOnSequence4(String path, String
	 * path2, int option) throws Exception { System.out.println("LOADING...");
	 * docPairs = new ArrayList<DocumentPair>(); ArrayList<DocumentPair>
	 * trainPairs = new ArrayList<DocumentPair>(); ExcelReader reader = new
	 * ExcelReader(); File folder = new File(path); File[] subs =
	 * folder.listFiles(); for (int i = 0; i < subs.length; i++) { DocumentPair
	 * dp = reader.readDocs(subs[i].getAbsolutePath()); docPairs.add(dp);
	 * trainPairs.add(dp); }
	 * 
	 * ArrayList<DocumentPair> testDps = new ArrayList<DocumentPair>(); File
	 * folder2 = new File(path2); File[] subs2 = folder2.listFiles(); for (int i
	 * = 0; i < subs2.length; i++) { DocumentPair dp =
	 * reader.readDocs(subs2[i].getAbsolutePath()); docPairs.add(dp);
	 * testDps.add(dp); } counter.loadTable(docPairs);
	 * aligner.setCounter(counter); System.out.println("DATA LOADED...");
	 * 
	 * aligner.train(trainPairs, option);
	 * System.out.println("Aligner trained...");
	 * System.out.println("Classifying....");
	 * 
	 * // aligner.classify(trainAndTest.get(1),option); ArrayList<double[][]>
	 * vals2 = aligner .classifyDocuments(testDps, option); //
	 * aligner.evaluate(vals, docPairs); //
	 * aligner.classify(trainAndTest.get(1),option); // ArrayList<double[][]>
	 * vals = aligner.classifyDocuments(testDps, // option); //
	 * ArrayList<double[][]> vals = new ArrayList<double[][]>();
	 * ArrayList<ArrayList<String>> sequences = new
	 * ArrayList<ArrayList<String>>(); for (int i = 0; i < testDps.size(); i++)
	 * { ArrayList<String> sequence = alignOnSequenceDirectly( testDps.get(i),
	 * vals2.get(i), option); sequences.add(sequence); }
	 * 
	 * return sequences; }
	 * 
	 * public void writeSequence(String fileName, ArrayList<String> sequence)
	 * throws IOException { BufferedWriter writer = new BufferedWriter(new
	 * FileWriter(fileName)); int len = sequence.size() - 1; for (int i = len; i
	 * >= 0; i--) { writer.append(sequence.get(i) + "\n"); } writer.close(); }
	 * 
	 * public void writeSequence2(String fileName, ArrayList<String> sequence)
	 * throws IOException { BufferedWriter writer = new BufferedWriter(new
	 * FileWriter(fileName)); int len = sequence.size(); for (int i = 0; i <
	 * len; i++) { writer.append(sequence.get(i) + "\n"); } writer.close(); }
	 * 
	 * public void testOnDifferentTopicDP(String path, String path2, int option)
	 * throws Exception { System.out.println("LOADING..."); docPairs = new
	 * ArrayList<DocumentPair>(); ArrayList<DocumentPair> trainPairs = new
	 * ArrayList<DocumentPair>(); ExcelReader reader = new ExcelReader(); File
	 * folder = new File(path); File[] subs = folder.listFiles(); for (int i =
	 * 0; i < subs.length; i++) { DocumentPair dp =
	 * reader.readDocs(subs[i].getAbsolutePath()); docPairs.add(dp);
	 * trainPairs.add(dp); }
	 * 
	 * ArrayList<DocumentPair> testDps = new ArrayList<DocumentPair>(); File
	 * folder2 = new File(path2); File[] subs2 = folder2.listFiles(); for (int i
	 * = 0; i < subs2.length; i++) { DocumentPair dp =
	 * reader.readDocs(subs2[i].getAbsolutePath()); docPairs.add(dp);
	 * testDps.add(dp); } counter.loadTable(docPairs);
	 * aligner.setCounter(counter); System.out.println("DATA LOADED...");
	 * 
	 * aligner.train(trainPairs, option);
	 * System.out.println("Aligner trained...");
	 * System.out.println("Classifying....");
	 * 
	 * // aligner.classify(trainAndTest.get(1),option); ArrayList<double[][]>
	 * vals2 = aligner .classifyDocuments(testDps, option); //
	 * aligner.evaluate(vals, docPairs); //
	 * aligner.classify(trainAndTest.get(1),option); // ArrayList<double[][]>
	 * vals = aligner.classifyDocuments(testDps, // option);
	 * ArrayList<double[][]> vals = new ArrayList<double[][]>(); for (int i = 0;
	 * i < testDps.size(); i++) { double[][] result = align_dp2(testDps.get(i),
	 * vals2.get(i), option); vals.add(result); } aligner.evaluate2(vals,
	 * testDps); System.out.println("Classification Done!");
	 * 
	 * }
	 * 
	 * public double calc(Sentence src, Sentence dst, int option) { if (option
	 * == 0) { return ldc.calcSen(src, dst); } else if (option == 1) { return
	 * counter.calcOverlap(src, dst); } else if (option == 2) { return
	 * counter.calc(src, dst); } return 0; }
	 * 
	 * public ArrayList<ArrayList<DocumentPair>> splitPairs(
	 * ArrayList<DocumentPair> dps, double ratio) {
	 * ArrayList<ArrayList<DocumentPair>> pairs = new
	 * ArrayList<ArrayList<DocumentPair>>(); int size = dps.size(); int train =
	 * (int) (size * ratio); ArrayList<DocumentPair> trainPairs = new
	 * ArrayList<DocumentPair>(); for (int i = 0; i < train; i++) {
	 * trainPairs.add(dps.get(i)); } ArrayList<DocumentPair> testPairs = new
	 * ArrayList<DocumentPair>(); for (int i = train; i < size; i++) {
	 * testPairs.add(dps.get(i)); } pairs.add(trainPairs); pairs.add(testPairs);
	 * return pairs; }
	 * 
	 * public Instance generateInstance(Sentence src, Sentence dst, int option)
	 * { double sim = calc(src, dst, option); double[] vals = new double[2];
	 * vals[0] = sim; vals[1] = 0; Instance instance = new DenseInstance(1.0,
	 * vals); return instance; }
	 * 
	 * public void align(int option) throws Exception {
	 * ArrayList<ArrayList<DocumentPair>> trainAndTest = splitPairs(docPairs,
	 * 0.3); System.out.println("Train the aligner...");
	 * aligner.train(trainAndTest.get(0), option);
	 * System.out.println("Aligner trained...");
	 * System.out.println("Classifying....");
	 * 
	 * // aligner.classify(trainAndTest.get(1),option); // ArrayList<double[][]>
	 * vals = aligner.classifyDocuments(docPairs, // option); //
	 * aligner.evaluate(vals, docPairs); //
	 * aligner.classify(trainAndTest.get(1),option); ArrayList<double[][]> vals
	 * = aligner.classifyDocuments( trainAndTest.get(1), option);
	 * aligner.evaluate2(vals, trainAndTest.get(1));
	 * System.out.println("Classification Done!"); }
	 * 
	 * public void alignDP(int option) throws Exception {
	 * ArrayList<ArrayList<DocumentPair>> trainAndTest = splitPairs(docPairs,
	 * 0.3); System.out.println("Train the aligner...");
	 * aligner.train(trainAndTest.get(0), option);
	 * System.out.println("Aligner trained...");
	 * System.out.println("Classifying...."); ArrayList<double[][]> vals = new
	 * ArrayList<double[][]>(); for (int i = 0; i < trainAndTest.get(1).size();
	 * i++) { double[][] result = align_dp(trainAndTest.get(1).get(i), option);
	 * vals.add(result); } aligner.evaluate2(vals, trainAndTest.get(1));
	 * System.out.println("Classification Done!"); }
	 * 
	 * public void alignDP2(int option) throws Exception {
	 * ArrayList<ArrayList<DocumentPair>> trainAndTest = splitPairs(docPairs,
	 * 0.3); System.out.println("Train the aligner...");
	 * aligner.train(trainAndTest.get(0), option);
	 * System.out.println("Aligner trained...");
	 * System.out.println("Classifying...."); ArrayList<double[][]> valsA =
	 * aligner.classifyDocuments( trainAndTest.get(1), option);
	 * ArrayList<double[][]> vals = new ArrayList<double[][]>(); for (int i = 0;
	 * i < trainAndTest.get(1).size(); i++) { double[][] result =
	 * align_dp2(trainAndTest.get(1).get(i), valsA.get(i), option);
	 * vals.add(result); } aligner.evaluate2(vals, trainAndTest.get(1));
	 * System.out.println("Classification Done!"); }
	 * 
	 * 
	 * 
	 * // generate new similarities public double[][] align_dp(DocumentPair
	 * docPair, int option) { Document src = docPair.getSrc(); Document dst =
	 * docPair.getModified(); ArrayList<Sentence> srcSentences =
	 * src.getSentences(); ArrayList<Sentence> dstSentences =
	 * dst.getSentences(); int srcSize = srcSentences.size(); int dstSize =
	 * dstSentences.size();
	 * 
	 * double[][] sim = new double[srcSize][dstSize]; for (int i = 0; i <
	 * srcSize; i++) { for (int j = 0; j < dstSize; j++) { sim[i][j] =
	 * calc(srcSentences.get(i), dstSentences.get(j), option); if (option == 0)
	 * { sim[i][j] = 0 - sim[i][j]; } } }
	 * 
	 * double[][] dp = new double[srcSize][dstSize]; double[][] aligns = new
	 * double[srcSize][dstSize]; for (int i = 0; i < srcSize; i++) { for (int j
	 * = 0; j < dstSize; j++) { if (i == 0 && j == 0) { dp[0][0] = 0; } else {
	 * double max = Integer.MIN_VALUE; double a, b, c = Integer.MIN_VALUE; if (i
	 * >= 1) { a = dp[i - 1][j] + sim[i][j]; if (a > max) max = a; } if (j >= 1)
	 * { b = dp[i][j - 1] + sim[i][j]; if (b > max) max = b; } if (i >= 1 && j
	 * >= 1) { c = dp[i - 1][j - 1] + sim[i][j]; if (c > max) max = c; }
	 * dp[i][j] = max; } } }
	 * 
	 * int i = srcSize - 1; int j = dstSize - 1; while (i > 0 && j > 0) { if (i
	 * > 0 && j > 0 && dp[i][j] == dp[i - 1][j - 1] + sim[i][j]) { aligns[i][j]
	 * = 1; i--; j--; } else if (i > 0 && dp[i][j] == dp[i - 1][j] + sim[i][j])
	 * { i = i - 1; } else if (j > 0 && dp[i][j] == dp[i][j - 1] + sim[i][j]) {
	 * j = j - 1; } } aligns[0][0] = 1; return aligns; }
	 * 
	 * // generate new similarities public double[][] align_dp2(DocumentPair
	 * docPair, double[][] sim, int option) { Document src = docPair.getSrc();
	 * Document dst = docPair.getModified(); ArrayList<Sentence> srcSentences =
	 * src.getSentences(); ArrayList<Sentence> dstSentences =
	 * dst.getSentences(); int srcSize = srcSentences.size(); int dstSize =
	 * dstSentences.size();
	 * 
	 * // double[][] sim = new double[srcSize][dstSize]; // for(int i =
	 * 0;i<srcSize;i++) { // for(int j = 0;j<dstSize;j++) { // sim[i][j] =
	 * calc(srcSentences.get(i),dstSentences.get(j),option); // if(option == 0)
	 * { // sim[i][j] = 0 - sim[i][j]; // } // } // }
	 * 
	 * double d = 0.1;
	 * 
	 * double[][] dp = new double[srcSize][dstSize]; double[][] aligns = new
	 * double[srcSize][dstSize]; for (int i = 0; i < srcSize; i++) { for (int j
	 * = 0; j < dstSize; j++) { if (i == 0) { dp[0][j] = j * d; } else if (j ==
	 * 0) { dp[i][0] = i * d; } else { double max = Integer.MIN_VALUE; double a,
	 * b, c = Integer.MIN_VALUE; if (i >= 1) { // a = dp[i - 1][j] + sim[i][j];
	 * a = dp[i - 1][j] + d; if (a > max) max = a; } if (j >= 1) { // b =
	 * dp[i][j - 1] + sim[i][j]; b = dp[i][j - 1] + d; if (b > max) max = b; }
	 * if (i >= 1 && j >= 1) { c = dp[i - 1][j - 1] + sim[i][j]; // c = dp[i -
	 * 1][j - 1] + d; if (c > max) max = c; } dp[i][j] = max; } } }
	 * 
	 * int i = srcSize - 1; int j = dstSize - 1; while (i > 0 && j > 0) { if (i
	 * > 0 && j > 0 && dp[i][j] == dp[i - 1][j - 1] + sim[i][j]) { aligns[i][j]
	 * = 1; i--; j--; } else if (i > 0 && dp[i][j] == dp[i - 1][j] + d) { i = i
	 * - 1; } else if (j > 0 && dp[i][j] == dp[i][j - 1] + d) { j = j - 1; } }
	 * aligns[0][0] = 1; return aligns; }
	 */

	public static void main(String[] args) throws Exception {
		LDAligner lda = new LDAligner();
		String trainPath = "E:\\independent study\\sentence alignment\\aligned_result\\selected";
		String trainPath2 = "E:\\independent study\\sentence alignment\\test_align";
		String testPath = "E:\\independent study\\sentence alignment\\schunn\\selected";
		// lda.alignExactMatchBaseline(testPath);
		// lda.load(trainPath);
		// lda.alignDP(0);
		// lda.alignDP2(0);
		// lda.alignDP2(2);
		// lda.load("E:\\independent study\\sentence alignment\\aligned_result\\selected");
		// lda.align(2);
		// lda.alignDP2(2);
		// lda.testOnDifferentTopic("E:\\independent study\\sentence alignment\\aligned_result\\selected","E:\\independent study\\sentence alignment\\test_align",2);
		// lda.load("E:\\independent study\\sentence alignment\\test_align");
		// lda.align(2);
		// lda.testOnDifferentTopic("E:\\independent study\\sentence alignment\\aligned_result\\selected","E:\\independent study\\sentence alignment\\test_align",0);
		// lda.testOnDifferentTopic("E:\\independent study\\sentence alignment\\test_align","E:\\independent study\\sentence alignment\\aligned_result\\selected",2);
		/*
		 * lda.testOnDifferentTopicDP(
		 * "E:\\independent study\\sentence alignment\\test_align",
		 * "E:\\independent study\\sentence alignment\\aligned_result\\selected"
		 * , 1);
		 */

		// lda.testOnDifferentTopicDP(
		// "E:\\independent study\\Revision\\train_sample",
		// "E:\\independent study\\Revision\\copy\\class1", 1);
		// Traditional
		// lda.testOnDifferentTopic(trainPath2, testPath, 0);
		// Traiditonal + DP
		// lda.testOnDifferentTopicDP(trainPath2, testPath, 0);
		// Mix
		// int[] options = { 0,1,2 };
		// lda.testOnDifferentTopicMix(testPath, trainPath, options);
		// Vote
		// lda.testOnDifferentTopicVote(testPath, trainPath, 0);
		// lda.alignSequences3(trainPath, trainPath2, 0,
		// "E:\\independent study\\sentence alignment\\alignedResult");
		// lda.alignSequences3(trainPath, trainPath2, 0,
		// "E:\\independent study\\sentence alignment\\alignedResult");
		// lda.align(trainPath, trainPath2, 0,
		// "E:\\independent study\\sentence alignment\\alignedResult");
		// lda.calculateWER(trainPath, testPath, 0,
		// "E:\\independent study\\sentence alignment\\alignedResult");
		// lda.calculateWER(testPath, trainPath, 0,
		// "E:\\independent study\\sentence alignment\\alignedResult");
	}
}
