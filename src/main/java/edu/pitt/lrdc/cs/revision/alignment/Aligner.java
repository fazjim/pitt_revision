package edu.pitt.lrdc.cs.revision.alignment;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;

import weka.core.Instance;
import weka.core.Instances;
import edu.pitt.cs.revision.purpose.AlignStruct;
import edu.pitt.cs.revision.purpose.RevisionPurposeClassifier;
import edu.pitt.lrdc.cs.revision.alignment.model.DocumentPair;
import edu.pitt.lrdc.cs.revision.io.RevisionDocumentReader;
/**
 * Automatically generate an alignment of sentences
 */
import edu.pitt.lrdc.cs.revision.model.RevisionDocument;
import edu.pitt.lrdc.cs.revision.model.RevisionOp;
import edu.pitt.lrdc.cs.revision.model.RevisionPurpose;
import edu.pitt.lrdc.cs.revision.model.RevisionUnit;

public class Aligner {
	private LDAligner aligner = new LDAligner();

	/**
	 * Generate the alignment of sentences, the test revision documents would be
	 * modified at sentence alignment
	 * 
	 * @param trainDocs
	 * @param testDocs
	 * @param option
	 * @throws Exception
	 */
	public void align(ArrayList<RevisionDocument> trainDocs,
			ArrayList<RevisionDocument> testDocs, int option) throws Exception {
		Hashtable<DocumentPair, double[][]> probMatrix = aligner
				.getProbMatrixTable(trainDocs, testDocs, option);
		Iterator<DocumentPair> it = probMatrix.keySet().iterator();
		while (it.hasNext()) {
			DocumentPair dp = it.next();
			String name = dp.getFileName();
			for (RevisionDocument doc : testDocs) {
				if (doc.getDocumentName().equals(name)) {
					double[][] sim = probMatrix.get(dp);
					double[][] fixedSim = aligner.alignWithDP(dp, sim, option);
					alignSingle(doc, sim, fixedSim, option);
				}
			}
		}
	}
	
	public void align(ArrayList<RevisionDocument> testDocs) throws Exception {
		Hashtable<DocumentPair, double[][]> probMatrix = aligner
				.getProbMatrixTable(testDocs);
		Iterator<DocumentPair> it = probMatrix.keySet().iterator();
		while (it.hasNext()) {
			DocumentPair dp = it.next();
			String name = dp.getFileName();
			for (RevisionDocument doc : testDocs) {
				if (doc.getDocumentName().equals(name)) {
					double[][] sim = probMatrix.get(dp);
					double[][] fixedSim = aligner.alignWithDP(dp, sim, 0);
					alignSingle(doc, sim, fixedSim, 0);
				}
			}
		}
	}

	public void align(String trainPath, ArrayList<RevisionDocument> docs)
			throws Exception {
		Hashtable<DocumentPair, double[][]> probMatrix = new Hashtable<DocumentPair, double[][]>();
		if (trainPath != null) {
			probMatrix = aligner.getProbMatrixTable(
					RevisionDocumentReader.readDocs(trainPath), docs, 0);
			aligner.aligner.persistClassifier();
			probMatrix = aligner.getProbMatrixTable(
					RevisionDocumentReader.readDocs(trainPath), docs, 2);
			Iterator<DocumentPair> it = probMatrix.keySet().iterator();
			while (it.hasNext()) {
				DocumentPair dp = it.next();
				String name = dp.getFileName();
				for (RevisionDocument doc : docs) {
					if (doc.getDocumentName().equals(name)) {
						double[][] sim = probMatrix.get(dp);
						double[][] fixedSim = aligner.alignWithDP(dp, sim, 2);
						alignSingle(doc, sim, fixedSim, 2);
					}
				}
			}

		} else {
			probMatrix = aligner.getProbMatrixTable(docs);

			Iterator<DocumentPair> it = probMatrix.keySet().iterator();
			while (it.hasNext()) {
				DocumentPair dp = it.next();
				String name = dp.getFileName();
				for (RevisionDocument doc : docs) {
					if (doc.getDocumentName().equals(name)) {
						double[][] sim = probMatrix.get(dp);
						double[][] fixedSim = aligner.alignWithDP(dp, sim, 0);
						alignSingle(doc, sim, fixedSim, 0);
					}
				}
			}
		}
	}

	/**
	 * Fake align, actually copy the gold standard alignment (For the extrinsic
	 * evaluation task)
	 * 
	 * @param docs
	 * @throws Exception
	 */
	public void repeatAlign(ArrayList<RevisionDocument> docs) throws Exception {
		Hashtable<String, RevisionDocument> table = new Hashtable<String, RevisionDocument>();
		for (RevisionDocument doc : docs) {
			RevisionUnit predictedRoot = new RevisionUnit(true);
			predictedRoot.setRevision_level(3); // Default level to 3
			doc.setPredictedRoot(predictedRoot);
			table.put(doc.getDocumentName(), doc);
			ArrayList<RevisionUnit> rus = doc.getRoot().getRevisionUnitAtLevel(
					0);
			for (RevisionUnit ru : rus) {
				predictedRoot.addUnit(ru.copy(predictedRoot));
			}
		}
	}

	/**
	 * Generate the alignment of sentences, the test revision documents would be
	 * modified at sentence alignment, creating the candidate revisions
	 * 
	 * @param trainDocs
	 * @param testDocs
	 * @param option
	 * @throws Exception
	 */
	public void align(ArrayList<RevisionDocument> trainDocs,
			ArrayList<RevisionDocument> testDocs, int option, boolean usingNgram)
			throws Exception {
		Hashtable<DocumentPair, double[][]> probMatrix = aligner
				.getProbMatrixTable(trainDocs, testDocs, option);
		Iterator<DocumentPair> it = probMatrix.keySet().iterator();
		while (it.hasNext()) {
			DocumentPair dp = it.next();
			String name = dp.getFileName();
			for (RevisionDocument doc : testDocs) {
				if (doc.getDocumentName().equals(name)) {
					double[][] sim = probMatrix.get(dp);
					double[][] fixedSim = aligner.alignWithDP(dp, sim, option);
					alignSingle(doc, sim, fixedSim, option);
				}
			}
		}
		Hashtable<String, RevisionDocument> table = new Hashtable<String, RevisionDocument>();
		for (RevisionDocument doc : testDocs) {
			RevisionUnit predictedRoot = new RevisionUnit(true);
			predictedRoot.setRevision_level(3); // Default level to 3
			doc.setPredictedRoot(predictedRoot);
			table.put(doc.getDocumentName(), doc);
		}
		RevisionPurposeClassifier rpc = new RevisionPurposeClassifier();
		Instances data = rpc.createInstances(testDocs, usingNgram);
		Hashtable<String, Integer> revIndexTable = new Hashtable<String, Integer>();
		int dataSize = data.numInstances();
		for (int j = 0;j<dataSize;j++) {
			Instance instance = data.instance(j);
			int ID_index = data.attribute("ID").index();
			// String ID =
			// data.instance(ID_index).stringValue(instance.attribute(ID_index));
			String ID = instance.stringValue(instance.attribute(ID_index));
			AlignStruct as = AlignStruct.parseID(ID);
			// System.out.println(ID);
			RevisionDocument doc = table.get(as.documentpath);
			RevisionUnit ru = new RevisionUnit(doc.getPredictedRoot());
			ru.setNewSentenceIndex(as.newIndices);
			ru.setOldSentenceIndex(as.oldIndices);
			if (as.newIndices == null || as.newIndices.size() == 0) {
				ru.setRevision_op(RevisionOp.DELETE);
			} else if (as.oldIndices == null || as.oldIndices.size() == 0) {
				ru.setRevision_op(RevisionOp.ADD);
			} else {
				ru.setRevision_op(RevisionOp.MODIFY);
			}

			ru.setRevision_purpose(RevisionPurpose.CLAIMS_IDEAS); // default of
																	// ADD and
																	// Delete
																	// are
																	// content
																	// edits

			ru.setRevision_level(0);
			if (revIndexTable.containsKey(as.documentpath)) {
				ru.setRevision_index(revIndexTable.get(as.documentpath));
				revIndexTable.put(as.documentpath,
						revIndexTable.get(as.documentpath) + 1);
			} else {
				ru.setRevision_index(1);
				revIndexTable.put(as.documentpath, 2);
			}
			doc.getPredictedRoot().addUnit(ru);
		}
	}

	/**
	 * Given the probMatrix of the alignment tool, modify the revision document
	 * object of its sentence alignment
	 * 
	 * @param doc
	 * @param probMatrix
	 * @throws Exception
	 */
	private void alignSingle(RevisionDocument doc, double[][] probMatrix,
			double[][] fixedMatrix, int option) throws Exception {
		int oldLength = probMatrix.length;
		int newLength = probMatrix[0].length;
		if (doc.getOldSentencesArray().length != oldLength
				|| doc.getNewSentencesArray().length != newLength) {
			throw new Exception("Alignment sentence does not match");
		} else {
			/*
			 * Rules for alignment 1. Allows one to many and many to one
			 * alignment 2. No many to many alignments (which would make the
			 * annotation even more difficult)
			 */
			if (option == -1) { // Baseline 1, using the exact match baseline
				for (int i = 0; i < oldLength; i++) {
					for (int j = 0; j < newLength; j++) {
						if (probMatrix[i][j] == 1) {
							doc.predictNewMappingIndex(j + 1, i + 1);
							doc.predictOldMappingIndex(i + 1, j + 1);
						}
					}
				}
			} else if (option == -2) { // Baseline 2, using the similarity
										// baseline
				for (int i = 0; i < oldLength; i++) {
					for (int j = 0; j < newLength; j++) {
						if (probMatrix[i][j] > 0.5) {
							doc.predictNewMappingIndex(j + 1, i + 1);
							doc.predictOldMappingIndex(i + 1, j + 1);
						}
					}
				}
			} else { // current best approach
				for (int i = 0; i < oldLength; i++) {
					for (int j = 0; j < newLength; j++) {
						if (fixedMatrix[i][j] == 1) {
							doc.predictNewMappingIndex(j + 1, i + 1);
							doc.predictOldMappingIndex(i + 1, j + 1);
						}
					}
				}

				// Code for improving
			}
		}
	}

	/**
	 * Set the predicts to be the true alignment
	 * 
	 * Not implemented
	 * 
	 * @param doc
	 */
	public void implementPredicts(RevisionDocument doc) {

	}

}
