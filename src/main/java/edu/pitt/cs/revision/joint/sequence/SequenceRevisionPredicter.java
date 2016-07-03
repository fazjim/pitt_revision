package edu.pitt.cs.revision.joint.sequence;

import java.util.Stack;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.io.File;

import com.github.jcrfsuite.CrfTagger;
import com.github.jcrfsuite.CrfTrainer;
import com.github.jcrfsuite.util.Pair;

import weka.core.Instances;
import edu.pitt.cs.revision.joint.EditSequence;
import edu.pitt.cs.revision.joint.EditStep;
import edu.pitt.cs.revision.joint.SequenceTransformer;
import edu.pitt.cs.revision.machinelearning.CRFTagger2;
import edu.pitt.cs.revision.machinelearning.SequenceFeaturePreparer;
import edu.pitt.cs.revision.machinelearning.SequenceProcessor;
import edu.pitt.lrdc.cs.revision.evaluate.RevisionDocumentComparer;
import edu.pitt.lrdc.cs.revision.io.RevisionDocumentReader;
import edu.pitt.lrdc.cs.revision.io.RevisionDocumentWriter;
import edu.pitt.lrdc.cs.revision.model.RevisionDocument;
import edu.pitt.lrdc.cs.revision.model.RevisionUnit;

public class SequenceRevisionPredicter {
	private String sequenceWorkPath = "C:\\Not Backed Up\\sequenceWorkPath\\test";
	private String trainPath = "C:\\Not Backed Up\\sequenceWorkPath\\train.txt";
	private String trainModelPath = "C:\\Not Backed Up\\sequenceWorkPath\\train.model";

	private Instances trainInstances;
	private SequenceProcessor sp;
	private SequenceFeaturePreparer fp;

	private boolean useMutation = false;

	public static void main(String[] args) throws Exception {
		ArrayList<RevisionDocument> trainDocs = RevisionDocumentReader
				.readDocs("C:\\Not Backed Up\\data\\naaclData\\C1");
		ArrayList<RevisionDocument> testDocs = RevisionDocumentReader
				.readDocs("C:\\Not Backed Up\\data\\naaclData\\C2");

		// Just for test
		ArrayList<RevisionDocument> testtrainDocs = new ArrayList<RevisionDocument>();
		ArrayList<RevisionDocument> testtestDocs = new ArrayList<RevisionDocument>();
		for (int i = 0; i < 40; i++) {
			testtrainDocs.add(trainDocs.get(i));
		}
		for (int i = 40; i < trainDocs.size(); i++) {
			testtestDocs.add(trainDocs.get(i));
		}

		SequenceRevisionPredicter srp = new SequenceRevisionPredicter();
		int k = 2;
		int remove = 11;
		int option = 3;
		boolean usingNgram = false;
		int typeNum = 3;
		if (option == 1) {
			typeNum = 2;
		} else if (option == 2) {
			typeNum = 3;
		} else if (option == 3) {
			typeNum = 5;
		}

		int[][] cm = new int[typeNum + 1][typeNum + 1];
		int totalAgreedNum = 0;
		int totalofTotal = 0;
		int topK = 5;
		Hashtable<String, RevisionDocument> predictedDocs = srp.predict(
				testtrainDocs, testtestDocs, k, remove, option, usingNgram,
				topK);
		for (RevisionDocument testDoc : testtestDocs) {
			String docName = testDoc.getDocumentName();
			RevisionDocument predictedDoc = predictedDocs.get(docName);

			File f = new File(docName);
			String docFileName = f.getName();
			String testPath = "C:\\Not Backed Up\\temp\\testground2\\"
					+ docFileName;
			RevisionDocumentWriter.writeToDoc(predictedDoc, testPath);
			System.err.println(docFileName);
			int agreedNum = RevisionDocumentComparer.getAlignmentAgreements(
					testDoc, predictedDoc);
			int total = testDoc.getOldDraftSentences().size()
					+ testDoc.getNewDraftSentences().size();
			totalAgreedNum += agreedNum;
			totalofTotal += total;

			int[][] cmDoc = RevisionDocumentComparer.getPurposeAgreements(
					typeNum, testDoc, predictedDoc);
			for (int i = 0; i < cm.length; i++) {
				for (int j = 0; j < cm.length; j++) {
					cm[i][j] += cmDoc[i][j];
				}
			}
		}

		double alignmentAccuracy = totalAgreedNum * 1.0 / totalofTotal;
		System.out.println("Alignment accuracy: " + alignmentAccuracy);
		double overallPrec = RevisionDocumentComparer.getOverallPrecison(cm);
		double overallRecall = RevisionDocumentComparer.getOverallRecall(cm);
		System.out.println("Precision: " + overallPrec);
		System.out.println("Recall: " + overallRecall);
	}

	/**
	 * To be implemented For online server version
	 * 
	 * @param draft1Txt
	 * @param draft2Txt
	 * @return
	 */
	public RevisionDocument onlinePredict(String draft1Txt, String draft2Txt) {
		RevisionDocument doc = null;
		return doc;
	}

	public Hashtable<String, RevisionDocument> predict(
			ArrayList<RevisionDocument> trainDocs,
			ArrayList<RevisionDocument> testDocs, int k, int remove,
			int option, boolean usingNgram, int topK) throws Exception {
		Hashtable<String, RevisionDocument> predictedDocs = new Hashtable<String, RevisionDocument>();
		sp = new SequenceProcessor();
		fp = new SequenceFeaturePreparer();
		Instances testInstances = null;
		Hashtable<String, String> realTagTable = new Hashtable<String, String>();

		trainInstances = sp.getInstances(trainDocs, realTagTable, k,
				usingNgram, option, remove);
		testInstances = sp
				.getInstances(testDocs, usingNgram, option, k, remove);
		// testInstances = sp.getInstancesTest(testDocs, realTagTable, k,
		// usingNgram,
		// option, remove);

		fp.prepareFeatures(trainInstances, testInstances, k, usingNgram);
		// Now we have indexes the features for all the possible cases in the
		// test set

		// Persist the training features into files to train the model
		persistTrainingSequences(fp, trainPath, trainDocs);
		CRFTagger2 tagger = trainTagger(trainPath, trainModelPath);

		for (RevisionDocument doc : testDocs) {
			String docName = doc.getDocumentName();
			List<List<EditSequence>> sequences = SequenceTransformer
					.tranformToKPossibleSequences(doc, topK);
			List<EditSequence> finalSequences = new ArrayList<EditSequence>();

			if (!useMutation) {
				// Baseline without mutation
				for (int i = 0; i < sequences.size(); i++) {
					List<EditSequence> sequenceCandiates = sequences.get(i);
					double prob = 0;
					EditSequence bestSeq = null;
					int id = 0;
					for (EditSequence candidate : sequenceCandiates) {
						double p = tag(tagger, fp, sp, doc, candidate,
								Integer.toString(id), option);
						if (p > prob) {
							prob = p;
							bestSeq = candidate;
						}
						id++;
					}
					finalSequences.add(bestSeq);
				}
			} else {
				// Model1 with mutation
				for (int i = 0; i < sequences.size(); i++) {
					List<EditSequence> sequenceCandidates = sequences.get(i);
					EditSequence candidate = sequenceCandidates.get(0);
					int id = 0;
					finalSequences.add(tagBest(tagger, fp, sp, doc, candidate,
							Integer.toString(id), option));
				}
			}

			RevisionDocument predictedDoc = SequenceTransformer
					.transformToDocument(doc, finalSequences);
			predictedDocs.put(docName, predictedDoc);
		}
		return predictedDocs;
	}

	public CRFTagger2 trainTagger(String trainPath, String modelPath)
			throws IOException {
		CrfTrainer.train(trainPath, modelPath);
		CRFTagger2 tagger = new CRFTagger2(modelPath);
		return tagger;
	}

	public double tag(CRFTagger2 tagger, SequenceFeaturePreparer fp,
			SequenceProcessor sp, RevisionDocument doc, EditSequence sequence,
			String id, int option) throws IOException {
		String fileName = persistSequenceToFeatureFiles(fp, sequence, doc,
				sequenceWorkPath, id);
		List<Double> probs = tagger.tagProb(fileName);
		List<List<Pair<String, Double>>> tags = tagger.tag(fileName);

		/*
		 * System.err.println(fileName); for (List<Pair<String, Double>> tagPair
		 * : tags) { for (Pair<String, Double> pair : tagPair) {
		 * System.err.print(pair.getFirst() + "," + pair.getSecond() + "," +
		 * sp.getCategoryName(Double.parseDouble(pair.getFirst())) + "\t"); }
		 * System.err.println(); }
		 */

		List<Pair<String, Double>> pairs = tags.get(0);
		double prob = probs.get(0);
		List<Integer> labels = new ArrayList<Integer>();
		for (int i = 0; i < pairs.size(); i++) {
			Pair<String, Double> pair = pairs.get(i);
			String tag = pair.getFirst();
			double val = Double.parseDouble(tag);

			labels.add(sp.getRevType(sp.getCategoryName(val), option));
			System.err.println(sp.getRevType(sp.getCategoryName(val), option));
		}
		System.err.println(labels.size() + ":"
				+ sequence.getLabelSequence().size());
		sequence.setLabels(labels);
		System.err.println(sequence.toString());
		return prob;
	}

	public EditSequence tagBest(CRFTagger2 tagger, SequenceFeaturePreparer fp,
			SequenceProcessor sp, RevisionDocument doc, EditSequence sequence,
			String id, int option) throws IOException {
		Stack<EditSequence> candidateSequences = new Stack<EditSequence>();
		EditSequence bestSequence = null;
		candidateSequences.push(sequence);
		double maxProb = 0;
		while (!candidateSequences.isEmpty()) {
			EditSequence seq = candidateSequences.pop();
			String fileName = persistSequenceToFeatureFiles(fp, seq, doc,
					sequenceWorkPath, id);
			List<Double> probs = tagger.tagProb(fileName);
			List<List<Pair<String, Double>>> tags = tagger.tag(fileName);

			/*
			 * System.err.println(fileName); for (List<Pair<String, Double>>
			 * tagPair : tags) { for (Pair<String, Double> pair : tagPair) {
			 * System.err.print(pair.getFirst() + "," + pair.getSecond() + "," +
			 * sp.getCategoryName(Double.parseDouble(pair.getFirst())) + "\t");
			 * } System.err.println(); }
			 */

			List<Pair<String, Double>> pairs = tags.get(0);
			double prob = probs.get(0);
			if (prob > maxProb) {
				bestSequence = seq;
				List<EditSequence> mutates = mutateSequence(seq, pairs, sp,
						option);
				for (EditSequence newCandidate : mutates) {
					candidateSequences.push(newCandidate);
				}
			}
		}
		return bestSequence;
	}

	/**
	 * Set the values and generate new candidates
	 * 
	 * @param sequence
	 * @param pairs
	 * @param sp
	 * @param option
	 * @return
	 */
	public List<EditSequence> mutateSequence(EditSequence sequence,
			List<Pair<String, Double>> pairs, SequenceProcessor sp, int option) {
		List<EditStep> steps = sequence.getLabelSequence();
		List<EditSequence> mutates = new ArrayList<EditSequence>();
		for (int i = 0; i < pairs.size(); i++) {
			EditStep step = steps.get(i);
			Pair<String, Double> pair = pairs.get(i);
			String tag = pair.getFirst();
			double val = Double.parseDouble(tag);
			double tagProb = pair.getSecond();// might be useful in the future

			String categoryName = sp.getCategoryName(val);
			int revType = sp.getRevType(categoryName, option);
			step.setType(revType);

			String prevAlignment = step.getStr(step.getD1Move()) + "-"
					+ step.getStr(step.getD2Move());
			if (categoryName.startsWith("M-M")) {
				// Prediction indicates Modify
				if (prevAlignment.equals("M-M")) {
					// do nothing
				} else if (prevAlignment.equals("M-K")) {
					// Previous was Delete, there are two cases, if the new one
					// is aligned, break the alignment, otherwise just do the
					// alignment

				} else if (prevAlignment.equals("K-M")) {

				}
			} else if (categoryName.startsWith("M-K")) {
				// Prediction indicates Delete
				if (prevAlignment.equals("M-K")) {
					// do nothing
				} else if (prevAlignment.equals("M-M")) {

				} else if (prevAlignment.equals("K-M")) {

				}
			} else if (categoryName.startsWith("K-M")) {
				// Prediction indicates Add
				if (prevAlignment.equals("K-M")) {
					// do nothing
				} else if (prevAlignment.equals("M-M")) {

				} else if (prevAlignment.equals("M-K")) {

				}
			}
		}
		return mutates;
	}

	public void persistTrainingSequences(SequenceFeaturePreparer sp,
			String trainingFile, ArrayList<RevisionDocument> docs)
			throws IOException {
		BufferedWriter writer = new BufferedWriter(new FileWriter(trainingFile));
		for (RevisionDocument doc : docs) {
			List<EditSequence> sequences = SequenceTransformer
					.transformToSequence(doc);
			for (EditSequence seq : sequences) {
				List<EditStep> steps = seq.getLabelSequence();
				for (EditStep step : steps) {
					int i = step.getCurrentD1();
					int j = step.getCurrentD2();
					double[] features = sp.fetchFeaturesBin(doc, i, j);
					String line = "";
					line += features[0];
					for (int index = 1; index < features.length; index++) {
						line += "\t" + "F[" + index + "]=" + features[index];
					}
					writer.write(line + "\n");
				}
				writer.write("\n");
			}
		}
		writer.close();
	}

	/**
	 * Now the features are not continuous values because of crfsuite design
	 * should use another crf package when there is time
	 * 
	 * @param sp
	 * @param sequence
	 * @param doc
	 * @param folder
	 * @param id
	 * @return
	 * @throws IOException
	 */
	public String persistSequenceToFeatureFiles(SequenceFeaturePreparer sp,
			EditSequence sequence, RevisionDocument doc, String folder,
			String id) throws IOException {
		File f = new File(doc.getDocumentName());
		String fileNameOnly = f.getName();
		String fileName = folder + "/" + fileNameOnly + "-" + id
				+ sequence.getOldParagraphStartNo() + "-"
				+ sequence.getOldParagrahpEndNo() + "-"
				+ sequence.getNewParagraphStartNo() + "-"
				+ sequence.getNewParagraphEndNo();
		BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
		List<EditStep> steps = sequence.getLabelSequence();
		String wholeText = "";
		for (EditStep step : steps) {
			int i = step.getCurrentD1();
			int j = step.getCurrentD2();
			double[] features = sp.fetchFeaturesBin(doc, i, j);
			String line = "";
			line += features[0];
			for (int index = 1; index < features.length; index++) {
				line += "\t" + "F[" + index + "]=" + features[index];
			}
			wholeText += line + "\n";
		}
		writer.write(wholeText);
		writer.close();
		return fileName;
	}

}
