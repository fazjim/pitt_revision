package edu.pitt.cs.revision.purpose;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

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
import edu.pitt.lrdc.cs.revision.io.RevisionDocumentReader;
import edu.pitt.lrdc.cs.revision.model.RevisionDocument;
import edu.pitt.lrdc.cs.revision.model.RevisionPurpose;

/**
 * This works as a sequential tagger version for purpose labeling
 * 
 * @author zhangfan
 *
 */
public class RevisionPurposeTagger {
	private ArrayList<ArrayList<ArrayList<HeatMapUnit>>> essays;
	private boolean tagLevelParagraph = true;// Each paragraph is a
												// segment,Otherwise each essay
												// is a segment
	private boolean ignoreSurface = true;

	public Instances[] prepareForLabelling(
			ArrayList<RevisionDocument> trainDocs,
			ArrayList<RevisionDocument> testDocs, boolean usingNgram, int remove)
			throws Exception {
		WekaAssist wa = new WekaAssist();
		CRFFeatureExtractor fe = new CRFFeatureExtractor();

		ArrayList<String> categories = new ArrayList<String>();
		for (int i = RevisionPurpose.START; i <= RevisionPurpose.END; i++) {
			if(ignoreSurface&&(i==RevisionPurpose.WORDUSAGE_CLARITY||i==RevisionPurpose.CONVENTIONS_GRAMMAR_SPELLING||i==RevisionPurpose.WORDUSAGE_CLARITY_CASCADED)){
				//do nothing
			} else {
				categories.add(RevisionPurpose.getPurposeName(i));
			}
		}
		if(ignoreSurface) categories.add("Surface");
		categories.add("NOCHANGE");
		fe.buildFeatures(usingNgram, categories);

		Instances trainData = wa.buildInstances(fe.features, usingNgram);
		Instances testData = wa.buildInstances(fe.features, usingNgram);

		for (RevisionDocument doc : trainDocs) {
			ArrayList<ArrayList<HeatMapUnit>> units = RevisionMapFileGenerator
					.getUnits4CRF(doc);
			ArrayList<Object[]> featureArr = fe.extractFeatures(units,
					usingNgram);
			int index = 0;
			for (ArrayList<HeatMapUnit> paragraph : units) {
				for (HeatMapUnit hmu : paragraph) {
					Object[] features = featureArr.get(index);
					if (hmu.revType.equals("Nochange")) {
						wa.addInstance(features, fe.features, usingNgram,
								trainData, "NOCHANGE", "dummy");
					} else {
						wa.addInstance(features, fe.features, usingNgram,
								trainData, hmu.revPurpose, "dummy");
					}
					index++;
				}
			}
		}

		for (RevisionDocument doc : testDocs) {
			ArrayList<ArrayList<HeatMapUnit>> units = RevisionMapFileGenerator
					.getUnits4CRF(doc);
			ArrayList<Object[]> featureArr = fe.extractFeatures(units,
					usingNgram);
			int index = 0;
			for (ArrayList<HeatMapUnit> paragraph : units) {
				for (HeatMapUnit hmu : paragraph) {
					Object[] features = featureArr.get(index);
					if (hmu.revType.equals("Nochange")) {
						wa.addInstance(features, fe.features, usingNgram,
								testData, "NOCHANGE", "dummy");
					} else {
						wa.addInstance(features, fe.features, usingNgram,
								testData, hmu.revPurpose, "dummy");
					}
					index++;
				}
			}
		}
		Instances[] trainTestInstances = new Instances[2];
		if(usingNgram) {
			trainTestInstances = wa.addNgram(trainData, testData);
		} else {
			trainTestInstances[0] = trainData;
			trainTestInstances[1] = testData;
		}
		return trainTestInstances;
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
		String folderPath = "C:\\Not Backed Up\\data\\trainData";
		ArrayList<RevisionDocument> docs = RevisionDocumentReader
				.readDocs(folderPath);
		ArrayList<RevisionDocument> trainDocs = new ArrayList<RevisionDocument>();
		ArrayList<RevisionDocument> testDocs = new ArrayList<RevisionDocument>();
		for (int i = 0; i < docs.size() - 2; i++) {
			trainDocs.add(docs.get(i));
		}
		for (int i = docs.size() - 2; i < docs.size(); i++) {
			testDocs.add(docs.get(i));
		}
		boolean usingNgram = false;
		Instances[] data = tagger.prepareForLabelling(trainDocs, testDocs,
				usingNgram, -1);
		tagger.transformToTxtForCRF(data[0], trainDocs,
				"C:\\Not Backed Up\\trainCrf.txt");
		tagger.transformToTxtForCRF(data[1], testDocs,
				"C:\\Not Backed Up\\testCrf.txt");
	}
}
