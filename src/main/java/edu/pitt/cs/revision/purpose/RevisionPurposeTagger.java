package edu.pitt.cs.revision.purpose;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

//import scala.collection.Iterator;
import java.util.Iterator;

import com.github.jcrfsuite.CrfTagger;
import com.github.jcrfsuite.CrfTrainer;
import com.github.jcrfsuite.util.Pair;

import cc.mallet.fst.CRF;
import cc.mallet.fst.CRFOptimizableByLabelLikelihood;
import cc.mallet.fst.CRFTrainerByValueGradients;
import cc.mallet.fst.CRFWriter;
import cc.mallet.fst.MultiSegmentationEvaluator;
import cc.mallet.fst.TransducerEvaluator;
import cc.mallet.fst.TransducerTrainer;
import cc.mallet.optimize.Optimizable;
import cc.mallet.types.InstanceList;
import weka.classifiers.Evaluation;
import weka.core.Instance;
import weka.core.Instances;
import edu.pitt.cs.revision.machinelearning.WekaAssist;
import edu.pitt.cs.revision.util.RevisionMapFileGenerator;
import edu.pitt.lrdc.cs.revision.alignment.model.HeatMapUnit;
import edu.pitt.lrdc.cs.revision.evaluate.ConfusionMatrix;
import edu.pitt.lrdc.cs.revision.evaluate.EvaluateTool;
import edu.pitt.lrdc.cs.revision.evaluate.PurposeEvaluator;
import edu.pitt.lrdc.cs.revision.io.RevisionDocumentReader;
import edu.pitt.lrdc.cs.revision.io.RevisionDocumentWriter;
import edu.pitt.lrdc.cs.revision.model.RevisionDocument;
import edu.pitt.lrdc.cs.revision.model.RevisionOp;
import edu.pitt.lrdc.cs.revision.model.RevisionPurpose;
import edu.pitt.lrdc.cs.revision.model.RevisionUnit;

/**
 * This works as a sequential tagger version for purpose labeling
 * 
 * @author zhangfan
 *
 */
public class RevisionPurposeTagger {
	private ArrayList<ArrayList<ArrayList<HeatMapUnit>>> essays;
	private boolean tagLevelParagraph = false;// Each paragraph is a
												// segment,Otherwise each essay
												// is a segment
	private boolean ignoreSurface = true;

	private WekaAssist wa;
	private CRFFeatureExtractor fe;
	private CrfTagger tagger;

	// private static BufferedWriter errorLogger;

	private static RevisionPurposeTagger instance;

	private ArrayList<RevisionDocument> trainDocs;
	private ArrayList<String> categories;

	private RevisionPurposeTagger() {
		wa = new WekaAssist();
		fe = new CRFFeatureExtractor();
	}

	public void setIgnoreSurface(boolean ignoreSurface) {
		this.ignoreSurface = ignoreSurface;
	}

	public void setTagLevelParagraph(boolean tagLevelParagraph) {
		this.tagLevelParagraph = tagLevelParagraph;
	}

	public static RevisionPurposeTagger getInstance() {
		if (instance == null) {
			instance = new RevisionPurposeTagger();
		}
		return instance;
	}

	public Instances[] prepareForLabelling(ArrayList<RevisionDocument> docs,
			boolean usingNgram, int remove) throws Exception {
		if (trainDocs != null) {
			return prepareForLabelling(trainDocs, docs, usingNgram, remove);
		} else {
			throw new Exception("Training data not set yet");
		}
	}

	public void trainAndTag(String trainPath, String modelPath,
			String testPath, String testOutputPath) throws IOException {
		CrfTrainer.train(trainPath, modelPath);
		tagger = new CrfTagger(modelPath);

		List<List<Pair<String, Double>>> tagProbLists = tagger.tag(testPath);

		// Compute accuracy
		int total = 0;
		int correct = 0;

		BufferedReader br = new BufferedReader(new FileReader(testPath));
		BufferedWriter writer = new BufferedWriter(new FileWriter(
				testOutputPath));
		String line;
		for (List<Pair<String, Double>> tagProbs : tagProbLists) {
			for (Pair<String, Double> tagProb : tagProbs) {
				String prediction = tagProb.first;
				Double prob = tagProb.second;
				line = br.readLine();
				while (line.trim().length() == 0) {
					writer.write("\n");
					line = br.readLine();
				}
				writer.write(prediction + "\t" + line + "\n");
			}
		}
		br.close();
		writer.close();
	}

	public void tag(String testPath, String testOutputPath) throws IOException {
		if (tagger != null) {
			List<List<Pair<String, Double>>> tagProbLists = tagger
					.tag(testPath);
			BufferedReader br = new BufferedReader(new FileReader(testPath));
			BufferedWriter writer = new BufferedWriter(new FileWriter(
					testOutputPath));
			String line;
			for (List<Pair<String, Double>> tagProbs : tagProbLists) {
				for (Pair<String, Double> tagProb : tagProbs) {
					String prediction = tagProb.first;
					Double prob = tagProb.second;
					line = br.readLine();
					while (line.trim().length() == 0) {
						writer.write("\n");
						line = br.readLine();
					}
					writer.write(prediction + "\t" + line + "\n");
				}
			}
			writer.close();
			br.close();
		}
	}

	public void tag(String testPath) throws IOException {
		if (tagger != null) {
			List<List<Pair<String, Double>>> tagProbLists = tagger
					.tag(testPath);
			BufferedReader br = new BufferedReader(new FileReader(testPath));
			List<String> lines = new ArrayList<String>();
			String line;
			for (List<Pair<String, Double>> tagProbs : tagProbLists) {
				for (Pair<String, Double> tagProb : tagProbs) {
					String prediction = tagProb.first;
					Double prob = tagProb.second;

					line = br.readLine();
					while (line.trim().length() == 0) {
						lines.add("\n");
						line = br.readLine();
					}
					line = line.substring(line.indexOf("\t") + 1);
					line = prediction + "\t" + line;
					lines.add(line);
				}
			}

			BufferedWriter writer = new BufferedWriter(new FileWriter(testPath));
			for (String newLine : lines) {
				writer.write(newLine + "\n");
			}
			writer.close();
			br.close();
		}
	}

	public Instances[] prepareForLabelling(
			ArrayList<RevisionDocument> trainDocs,
			ArrayList<RevisionDocument> testDocs, boolean usingNgram, int remove)
			throws Exception {
		/*
		 * ArrayList<String> categories = new ArrayList<String>(); for (int i =
		 * RevisionPurpose.START; i <= RevisionPurpose.END; i++) { if
		 * (ignoreSurface && (i == RevisionPurpose.WORDUSAGE_CLARITY || i ==
		 * RevisionPurpose.CONVENTIONS_GRAMMAR_SPELLING || i ==
		 * RevisionPurpose.WORDUSAGE_CLARITY_CASCADED)) { // do nothing } else {
		 * categories.add(RevisionPurpose.getPurposeName(i)); } } if
		 * (ignoreSurface) categories.add("Surface");
		 * categories.add("NOCHANGE");
		 */
		System.err.println("LALAALALLA:"+remove);
		if (categories == null) {
			if (ignoreSurface)
				categories = CategoryFactory
						.buildCategories(CategoryFactory.CONTENTSURFACE_MODE);
			else
				categories = CategoryFactory
						.buildCategories(CategoryFactory.CONTENTSURFACE_MODE);
		}
		fe.buildFeatures(usingNgram, categories,remove);
		
		Instances trainData = wa.buildInstances(fe.features, usingNgram);
		Instances testData = wa.buildInstances(fe.features, usingNgram);

		for (RevisionDocument doc : trainDocs) {
			ArrayList<ArrayList<HeatMapUnit>> units = RevisionMapFileGenerator
					.getUnits4CRF(doc);
			ArrayList<Object[]> featureArr = null;
			try {
				featureArr = fe.extractFeatures(units, doc,usingNgram,remove);
			} catch (Exception exp) {
				exp.printStackTrace();
				System.out.println(doc.getDocumentName());
			}
			int index = 0;
			for (ArrayList<HeatMapUnit> paragraph : units) {
				for (HeatMapUnit hmu : paragraph) {
					Object[] features = featureArr.get(index);
					if (hmu.rType.equals(RevisionPurpose
							.getPurposeName(RevisionPurpose.NOCHANGE))) {
						wa.addInstance(
								features,
								fe.features,
								usingNgram,
								trainData,
								RevisionPurpose
										.getPurposeName(RevisionPurpose.NOCHANGE),
								"dummy");
					} else {
						// For training data, trim the unlabeled ones
						if (hmu.rPurpose.trim().length() > 0) {
							String purpose = hmu.rPurpose;
							if (purpose
									.equals(RevisionPurpose
											.getPurposeName(RevisionPurpose.WORDUSAGE_CLARITY))
									|| purpose
											.equals(RevisionPurpose
													.getPurposeName(RevisionPurpose.ORGANIZATION))
									|| purpose
											.equals(RevisionPurpose
													.getPurposeName(RevisionPurpose.WORDUSAGE_CLARITY_CASCADED))
									|| purpose
											.equals(RevisionPurpose
													.getPurposeName(RevisionPurpose.PRECISION))
									|| purpose
											.equals(RevisionPurpose
													.getPurposeName(RevisionPurpose.CONVENTIONS_GRAMMAR_SPELLING))) {
								purpose = RevisionPurpose
										.getPurposeName(RevisionPurpose.SURFACE);
							}
							wa.addInstance(features, fe.features, usingNgram,
									trainData, purpose, "dummy");
						}

					}
					index++;
					if (trainData.size() > 0
							&& trainData.instance(trainData.size() - 1)
									.classValue() == -1)
						System.out.println("PURPOSE:" + hmu.rPurpose);
				}
			}
		}

		for (RevisionDocument doc : testDocs) {
			ArrayList<ArrayList<HeatMapUnit>> units = RevisionMapFileGenerator
					.getUnits4CRF(doc);
			ArrayList<Object[]> featureArr = fe.extractFeatures(units, doc,
					usingNgram,remove);
			int index = 0;
			for (ArrayList<HeatMapUnit> paragraph : units) {
				for (HeatMapUnit hmu : paragraph) {
					Object[] features = featureArr.get(index);
					if (hmu.rType.equals(RevisionPurpose
							.getPurposeName(RevisionPurpose.NOCHANGE))) {
						wa.addInstance(
								features,
								fe.features,
								usingNgram,
								testData,
								RevisionPurpose
										.getPurposeName(RevisionPurpose.NOCHANGE),
								"dummy");
					} else {
						if (hmu.rPurpose.trim().length() == 0) {
							wa.addInstance(
									features,
									fe.features,
									usingNgram,
									testData,
									RevisionPurpose
											.getPurposeName(RevisionPurpose.NOCHANGE),
									"dummy");
						} else {
							String purpose = hmu.rPurpose;
							if (purpose
									.equals(RevisionPurpose
											.getPurposeName(RevisionPurpose.WORDUSAGE_CLARITY))
									|| purpose
											.equals(RevisionPurpose
													.getPurposeName(RevisionPurpose.ORGANIZATION))
									|| purpose
											.equals(RevisionPurpose
													.getPurposeName(RevisionPurpose.WORDUSAGE_CLARITY_CASCADED))
									|| purpose
											.equals(RevisionPurpose
													.getPurposeName(RevisionPurpose.PRECISION))
									|| purpose
											.equals(RevisionPurpose
													.getPurposeName(RevisionPurpose.CONVENTIONS_GRAMMAR_SPELLING))) {
								purpose = RevisionPurpose
										.getPurposeName(RevisionPurpose.SURFACE);
							}
							wa.addInstance(features, fe.features, usingNgram,
									testData, purpose, "dummy");

						}
					}
					index++;
				}
			}
		}
		Instances[] trainTestInstances = new Instances[2];
		if (usingNgram) {
			trainTestInstances = wa.addNgram(trainData, testData);
			Instances trainDataRem = WekaAssist.removeID(trainTestInstances[0]);
			Instances testDataRem = WekaAssist.removeID(trainTestInstances[1]);

			// trainTestInstances[0] = trainDataRem;
			// trainTestInstances[1] = testDataRem;
			trainTestInstances = WekaAssist.selectFeatures(trainDataRem,
					testDataRem);
		} else {
			trainTestInstances[0] = trainData;
			trainTestInstances[1] = testData;
		}

		return trainTestInstances;
	}

	/**
	 * Read the CRF results and set to predict units
	 * 
	 * @param docs
	 * @param fileName
	 * @throws IOException
	 */
	public void readResultToDocs(ArrayList<RevisionDocument> docs,
			String fileName) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(fileName));
		List<String> lines = new ArrayList<String>();
		String line = "";
		line = reader.readLine();
		while (line != null) {
			if (line.trim().length() > 0)
				lines.add(line);
			line = reader.readLine();
		}
		reader.close();

		int index = 0;
		for (RevisionDocument doc : docs) {
			ArrayList<ArrayList<HeatMapUnit>> units = RevisionMapFileGenerator
					.getUnits4CRF(doc);
			for (ArrayList<HeatMapUnit> paragraph : units) {
				for (HeatMapUnit unit : paragraph) {
					String resultLine = lines.get(index);
					String tag = resultLine.split("\t")[0];
					double tagDouble = Double.parseDouble(tag);
					int tagInt = (int) tagDouble;
					unit.setRPurpose(categories.get(tagInt));
					index++;
				}
			}
			setPredictResults(units, doc);
		}
	}

	public void setPredictResults(ArrayList<ArrayList<HeatMapUnit>> units,
			RevisionDocument doc) {
		Hashtable<Integer, ArrayList<Integer>> oldLists = new Hashtable<Integer, ArrayList<Integer>>();
		Hashtable<Integer, ArrayList<Integer>> newLists = new Hashtable<Integer, ArrayList<Integer>>();

		Hashtable<Integer, String> oldRevs = new Hashtable<Integer, String>();
		Hashtable<Integer, String> newRevs = new Hashtable<Integer, String>();

		for (ArrayList<HeatMapUnit> paragraph : units) {
			for (HeatMapUnit unit : paragraph) {
				int oldIndex = unit.oldIndex;
				int newIndex = unit.newIndex;
				if (oldIndex != -1) {
					ArrayList<Integer> newIndices;
					if (!oldLists.containsKey(oldIndex)) {
						newIndices = new ArrayList<Integer>();
						oldLists.put(oldIndex, newIndices);
					} else {
						newIndices = oldLists.get(oldIndex);
					}
					if (newIndex != -1)
						newIndices.add(newIndex);
					oldRevs.put(oldIndex, unit.rPurpose);
				}
				if (newIndex != -1) {
					ArrayList<Integer> oldIndices;
					if (!newLists.containsKey(newIndex)) {
						oldIndices = new ArrayList<Integer>();
						newLists.put(newIndex, oldIndices);
					} else {
						oldIndices = newLists.get(newIndex);
					}
					if (oldIndex != -1)
						oldIndices.add(oldIndex);
					newRevs.put(newIndex, unit.rPurpose);
				}
			}
		}
		int revIndex = 1;
		Iterator<Integer> oldIt = oldLists.keySet().iterator();
		while (oldIt.hasNext()) {
			int oldIndex = oldIt.next();
			ArrayList<Integer> newIndices = oldLists.get(oldIndex);
			ArrayList<Integer> oldIndices = new ArrayList<Integer>();
			oldIndices.add(oldIndex);
			if (newIndices.size() == 0 || newIndices.size() > 1) {
				for (Integer newIndex : newIndices) {
					newLists.remove(newIndex);
				}

				String rPurpose = oldRevs.get(oldIndex);
				if (!rPurpose.equals(RevisionPurpose
						.getPurposeName(RevisionPurpose.NOCHANGE))) {
					if (rPurpose.equals("Surface"))
						rPurpose = RevisionPurpose
								.getPurposeName(RevisionPurpose.WORDUSAGE_CLARITY);
					RevisionUnit ru = new RevisionUnit(doc.getPredictedRoot());
					ru.setNewSentenceIndex(newIndices);
					ru.setOldSentenceIndex(oldIndices);
					if (newIndices == null || newIndices.size() == 0) {
						ru.setRevision_op(RevisionOp.DELETE);
					} else if (oldIndices == null || oldIndices.size() == 0) {
						ru.setRevision_op(RevisionOp.ADD);
					} else {
						ru.setRevision_op(RevisionOp.MODIFY);
					}

					ru.setRevision_purpose(RevisionPurpose
							.getPurposeIndex(rPurpose));
					ru.setRevision_level(0);
					ru.setRevision_index(revIndex);
					doc.getPredictedRoot().addUnit(ru);
					revIndex++;
				}
			}
		}

		Iterator<Integer> newIt = newLists.keySet().iterator();
		while (newIt.hasNext()) {
			int newIndex = newIt.next();
			ArrayList<Integer> oldIndices = newLists.get(newIndex);
			ArrayList<Integer> newIndices = new ArrayList<Integer>();
			newIndices.add(newIndex);
			String rPurpose = newRevs.get(newIndex);
			if (!rPurpose.equals(RevisionPurpose
					.getPurposeName(RevisionPurpose.NOCHANGE))) {
				if (rPurpose.equals("Surface"))
					rPurpose = RevisionPurpose
							.getPurposeName(RevisionPurpose.WORDUSAGE_CLARITY);
				RevisionUnit ru = new RevisionUnit(doc.getPredictedRoot());
				ru.setNewSentenceIndex(newIndices);
				ru.setOldSentenceIndex(oldIndices);
				if (newIndices == null || newIndices.size() == 0) {
					ru.setRevision_op(RevisionOp.DELETE);
				} else if (oldIndices == null || oldIndices.size() == 0) {
					ru.setRevision_op(RevisionOp.ADD);
				} else {
					ru.setRevision_op(RevisionOp.MODIFY);
				}

				ru.setRevision_purpose(RevisionPurpose
						.getPurposeIndex(rPurpose));
				ru.setRevision_level(0);
				ru.setRevision_index(revIndex);
				doc.getPredictedRoot().addUnit(ru);
				revIndex++;
			}
		}

	}

	public void transformToTxtForCRFTrain(Instances inst,
			ArrayList<RevisionDocument> docs, String fileName)
			throws IOException {
		int index = 0;
		int classIndex = inst.classIndex();
		BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
		for (RevisionDocument doc : docs) {
			ArrayList<ArrayList<HeatMapUnit>> units = RevisionMapFileGenerator
					.getUnits4CRF(doc);

			for (ArrayList<HeatMapUnit> paragraph : units) {
				for (HeatMapUnit unit : paragraph) {
					if (unit.rPurpose.trim().length() > 0
							|| unit.rType.equals("Nochange")) {
						Instance instance = inst.instance(index);
						writer.write(instance.classValue() + "\t");
						for (int i = 0; i < instance.numAttributes(); i++) {
							if (i != classIndex) {
								writer.write(instance.attribute(i).name() + "="
										+ instance.value(i) + "\t");
							}
						}
						writer.write("\n");
						index++;
					}
				}
				if (tagLevelParagraph)
					writer.write("\n");
			}
			writer.write("\n");
		}
		writer.close();
	}

	public void transformToTxtForCRF(Instances inst,
			ArrayList<RevisionDocument> docs, String fileName)
			throws IOException {
		int index = 0;
		int classIndex = inst.classIndex();
		BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
		for (RevisionDocument doc : docs) {
			ArrayList<ArrayList<HeatMapUnit>> units = RevisionMapFileGenerator
					.getUnits4CRF(doc);

			for (ArrayList<HeatMapUnit> paragraph : units) {
				for (HeatMapUnit unit : paragraph) {
					Instance instance = inst.instance(index);
					writer.write(instance.classValue() + "\t");
					for (int i = 0; i < instance.numAttributes(); i++) {
						if (i != classIndex) {
							writer.write(instance.attribute(i).name() + "="
									+ instance.value(i) + "\t");
						}
					}
					writer.write("\n");
					index++;
				}
				if (tagLevelParagraph)
					writer.write("\n");
			}
			writer.write("\n");
		}
		writer.close();
	}

	public void trainAndTest(String trainFile, String writeFile) {

	}

	public void trainAndTest(InstanceList trainingData, InstanceList testingData) {
		CRF crf = new CRF(trainingData.getDataAlphabet(),
				trainingData.getTargetAlphabet());
		// construct the finite state machine
		crf.addFullyConnectedStatesForLabels();
		// initialize model's weights
		crf.setWeightsDimensionAsIn(trainingData, false);

		// CRFOptimizableBy* objects (terms in the objective function)
		// objective 1: label likelihood objective
		CRFOptimizableByLabelLikelihood optLabel = new CRFOptimizableByLabelLikelihood(
				crf, trainingData);

		// CRF trainer
		Optimizable.ByGradientValue[] opts = new Optimizable.ByGradientValue[] { optLabel };
		// by default, use L-BFGS as the optimizer
		CRFTrainerByValueGradients crfTrainer = new CRFTrainerByValueGradients(
				crf, opts);

		// *Note*: labels can also be obtained from the target alphabet
		String[] labels = new String[] { "I-PER", "I-LOC", "I-ORG", "I-MISC" };
		TransducerEvaluator evaluator = new MultiSegmentationEvaluator(
				new InstanceList[] { trainingData, testingData }, new String[] {
						"train", "test" }, labels, labels) {
			@Override
			public boolean precondition(TransducerTrainer tt) {
				// evaluate model every 5 training iterations
				return tt.getIteration() % 5 == 0;
			}
		};
		crfTrainer.addEvaluator(evaluator);

		CRFWriter crfWriter = new CRFWriter("ner_crf.model") {
			@Override
			public boolean precondition(TransducerTrainer tt) {
				// save the trained model after training finishes
				return tt.getIteration() % Integer.MAX_VALUE == 0;
			}
		};
		crfTrainer.addEvaluator(crfWriter);

		// all setup done, train until convergence
		crfTrainer.setMaxResets(0);
		crfTrainer.train(trainingData, Integer.MAX_VALUE);
		// evaluate
		evaluator.evaluate(crfTrainer);

		// save the trained model (if CRFWriter is not used)
		// FileOutputStream fos = new FileOutputStream("ner_crf.model");
		// ObjectOutputStream oos = new ObjectOutputStream(fos);
		// oos.writeObject(crf);
	}

	public static void main(String[] args) throws Exception {
		RevisionPurposeTagger tagger = new RevisionPurposeTagger();
		// String trainPathAll = "C:\\Not Backed Up\\data\\trainDataCRFVersion";
		// String folderPath =
		// "C:\\Not Backed Up\\data\\allNewData\\Fan\\All-jiaoyang2";
		tagger.setTagLevelParagraph(true);
		String trainPathAll = "C:\\Not Backed Up\\data\\trainDataCRFVersion";
		// String folderPath =
		// "C:\\Not Backed Up\\data\\allNewData\\Fan\\All-jiaoyang";
		// String folderPath =
		// "C:\\Not Backed Up\\data\\allNewData\\Fan\\temp_alldata";
		// String trainPathAll =
		// "C:\\Not Backed Up\\data\\allNewData\\Fan\\All-jiaoyang2";
		String folderPath = "C:\\Not Backed Up\\data\\naaclData\\C2";

		ArrayList<RevisionDocument> docs = RevisionDocumentReader
				.readDocs(folderPath);
		ArrayList<RevisionDocument> trainDocsAll = RevisionDocumentReader
				.readDocs(trainPathAll);
		int folder = 10;

		ArrayList<ArrayList<ArrayList<RevisionDocument>>> crossCuts = EvaluateTool
				.getCrossCut(docs, folder);

		int option = 1;
		boolean usingNgram = true;
		if (option == 0) {
			ArrayList<ConfusionMatrix> cms = new ArrayList<ConfusionMatrix>();
			Instances[] instances = RevisionPurposeTagger.getInstance()
					.prepareForLabelling(trainDocsAll, docs, usingNgram, -1);
			String trainPath = "C:\\Not Backed Up\\trainCrf.txt";
			String testPath = "C:\\Not Backed Up\\testCrf.txt";
			String modelPath = "C:\\Not Backed Up\\crf.model";
			String testPath2 = "C:\\Not Backed Up\\testPredictCrf.txt";
			RevisionPurposeTagger.getInstance().transformToTxtForCRFTrain(
					instances[0], trainDocsAll, trainPath);
			RevisionPurposeTagger.getInstance().transformToTxtForCRF(
					instances[1], docs, testPath);
			RevisionPurposeTagger.getInstance().trainAndTag(trainPath,
					modelPath, testPath, testPath2);
			RevisionPurposeTagger.getInstance().readResultToDocs(docs,
					testPath2);
			/*
			 * for(RevisionDocument doc: docs) {
			 * doc.materializeRevisionPurpose();
			 * RevisionDocumentWriter.writeToDoc(doc, doc.getDocumentName());; }
			 */
			cms.add(PurposeEvaluator.getConfusionMatrixOneSurface(docs));
			EvaluateTool.printEvaluation(cms);
		} else {
			ArrayList<ConfusionMatrix> cms = new ArrayList<ConfusionMatrix>();
			for (int i = 0; i < folder; i++) {
				ArrayList<RevisionDocument> trainDocs = crossCuts.get(i).get(0);
				ArrayList<RevisionDocument> testDocs = crossCuts.get(i).get(1);

				/*
				 * boolean usingNgram = false; Instances[] data =
				 * tagger.prepareForLabelling(trainDocs, testDocs, usingNgram,
				 * -1); tagger.transformToTxtForCRF(data[0], trainDocs,
				 * "C:\\Not Backed Up\\trainCrf.txt");
				 * tagger.transformToTxtForCRF(data[1], testDocs,
				 * "C:\\Not Backed Up\\testCrf.txt");
				 */

				Instances[] instances = RevisionPurposeTagger.getInstance()
						.prepareForLabelling(trainDocs, testDocs, usingNgram,
								-1);
				String trainPath = "C:\\Not Backed Up\\trainCrf.txt";
				String testPath = "C:\\Not Backed Up\\testCrf.txt";
				String modelPath = "C:\\Not Backed Up\\crf.model";
				String testPath2 = "C:\\Not Backed Up\\testPredictCrf.txt";
				RevisionPurposeTagger.getInstance().transformToTxtForCRFTrain(
						instances[0], trainDocs, trainPath);
				RevisionPurposeTagger.getInstance().transformToTxtForCRF(
						instances[1], testDocs, testPath);
				RevisionPurposeTagger.getInstance().trainAndTag(trainPath,
						modelPath, testPath, testPath2);
				RevisionPurposeTagger.getInstance().readResultToDocs(testDocs,
						testPath2);
				cms.add(PurposeEvaluator.getConfusionMatrixOneSurface(testDocs));
			}
			// PurposeEvaluator.evaluatePurposeCorrelation2(docs);
			EvaluateTool.printEvaluation(cms);
		}

	}
}
