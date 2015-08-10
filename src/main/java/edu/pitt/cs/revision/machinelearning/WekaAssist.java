package edu.pitt.cs.revision.machinelearning;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import weka.attributeSelection.ASEvaluation;
import weka.attributeSelection.ASSearch;
import weka.attributeSelection.CfsSubsetEval;
import weka.attributeSelection.GainRatioAttributeEval;
import weka.attributeSelection.GreedyStepwise;
import weka.attributeSelection.Ranker;
import weka.classifiers.Classifier;
import weka.classifiers.CostMatrix;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.SMO;
import weka.classifiers.meta.CostSensitiveClassifier;
import weka.classifiers.meta.FilteredClassifier;
import weka.classifiers.rules.ZeroR;
import weka.classifiers.trees.RandomForest;
import weka.core.Attribute;
import weka.core.Capabilities;
import weka.core.DenseInstance;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Range;
import weka.filters.Filter;
import weka.filters.supervised.attribute.AttributeSelection;
import weka.filters.unsupervised.attribute.PrincipalComponents;
import weka.filters.unsupervised.attribute.Remove;

/**
 * Assist the use of weka
 * 
 * @author zhangfan
 *
 */
public class WekaAssist {

	// Customize this to build the dataset
	public Instances buildInstances(FeatureName featureTable, boolean usingNgram) {
		FastVector atts = new FastVector(); // all attributes

		/*
		 * if(usingNgram) { atts.addElement(new Attribute("Text", (FastVector)
		 * null));// text }
		 */

		// The first feature always being the category
		FastVector attVals = new FastVector(); // nominals
		ArrayList<String> category = featureTable.getCategory();
		for (String cat : category) {
			attVals.addElement(cat);
		}
		atts.addElement(new Attribute("category", attVals));

		// The other customized features
		for (String name : featureTable.features) {
			int index = featureTable.getIndex(name);
			if (featureTable.getType(index).equals(String.class)) {
				atts.addElement(new Attribute(name, (FastVector) null));
			} else if (featureTable.getType(index).equals(Boolean.class)) {
				FastVector attValBinary = new FastVector();
				attValBinary.addElement(Boolean.toString(true));
				attValBinary.addElement(Boolean.toString(false));
				atts.addElement(new Attribute(name, attValBinary));
			} else if (featureTable.getType(index).equals(ArrayList.class)) {
				FastVector attValArrs = new FastVector(); // nominals
				ArrayList<Object> list = featureTable.getNominalGroups(index);
				for (Object o : list)
					attValArrs.addElement(o);
				atts.addElement(new Attribute(name, attValArrs));
			} else {
				atts.addElement(new Attribute(name));
			}
		}

		// Setup the last attribute as the markup index
		atts.addElement(new Attribute("ID", (FastVector) null));

		// Setup the class index
		Instances insts = new Instances("MyRelation", atts, 0);
		insts.setClass(insts.attribute("category"));

		return insts;
	}

	/**
	 * @deprecated
	 * @param features
	 * @param featureTable
	 * @param usingNgram
	 * @param insts
	 * @param cat
	 * @param index
	 *            index to mark the the instance
	 */
	public void addInstance(Object[] features, FeatureName featureTable,
			boolean usingNgram, Instances insts, String cat, String ID) {
		double[] vals = new double[insts.numAttributes()];
		int startIndex = 0;
		vals[0] = insts.attribute(0).indexOfValue(cat);

		for (int i = startIndex; i < features.length; i++) {
			if (featureTable.getType(i).equals(String.class)) {
				vals[i + 1] = insts.attribute(i + 1).addStringValue(
						(String) features[i]);
			} else if (featureTable.getType(i).equals(Boolean.TYPE)) {
				String name = featureTable.getFeatureName(i);
				vals[i + 1] = insts.attribute(name).indexOfValue(
						(String) features[i]);
			} else if (featureTable.getType(i).equals(ArrayList.class)) {
				String name = featureTable.getFeatureName(i);
				vals[i + 1] = insts.attribute(name).indexOfValue(
						(String) features[i]);
			} else {
				// System.out.println(featureTable.getFeatureName(i));
				// System.out.println(featureTable.getType(i).toString());
				vals[i + 1] = (Double) features[i];
			}
		}
		vals[vals.length - 1] = insts.attribute(vals.length - 1)
				.addStringValue(ID);
		Instance instance = new Instance(1.0, vals);
		insts.add(instance);
	}

	public void addInstance(Instances insts, Object[] features,
			FeatureName featureTable, String catName) {
		double[] vals = new double[insts.numAttributes()];
		int startIndex = 0;
		for (int i = startIndex; i < features.length; i++) {
			if (featureTable.getType(i).equals(String.class)) {
				if (featureTable.features.get(i).equals(catName)) {
					vals[i] = insts.attribute(0).indexOfValue( // If this is a
																// classification
																// problem
							(String) features[i]);
				} else {
					vals[i] = insts.attribute(i).addStringValue(
							(String) features[i]);
				}
			} else if (featureTable.getType(i).equals(Boolean.class)) {
				String name = featureTable.getFeatureName(i);
				vals[i] = insts.attribute(name).indexOfValue(
						(String) features[i]);
			} else if (featureTable.getType(i).equals(ArrayList.class)) {
				String name = featureTable.getFeatureName(i);
				vals[i] = insts.attribute(name).indexOfValue(
						(String) features[i]);
			} else {
				vals[i] = (Double) features[i];
			}
		}
		Instance instance = new Instance(1.0, vals);
		insts.add(instance);
	}

	// Adding ngrams
	public Instances addNgram(Instances ins) throws Exception {
		StringVectorWrapper ngramWrapper = new StringVectorWrapper();
		InstancesPair p = ngramWrapper.applyStringVectorFilter(ins, "Text",
				null);
		ins = p.a;
		return ins;
	}

	// Adding ngrams2
	public Instances[] addNgram(Instances train, Instances test)
			throws Exception {
		StringVectorWrapper ngramWrapper = new StringVectorWrapper();
		InstancesPair p = ngramWrapper.applyStringVectorFilter(train, "Text",
				"TEXTDIFF", test);
		Instances[] ins = new Instances[2];
		ins[0] = p.a;
		ins[1] = p.b;
		return ins;
	}

	// Write instances to file
	public static void saveInstances(Instances dataset, String file)
			throws IOException {
		BufferedWriter writer = new BufferedWriter(new FileWriter(file));
		writer.write(dataset.toString());
		writer.flush();
		writer.close();
		// ArffSaver saver = new ArffSaver();
		// saver.setInstances(dataset);
		// saver.setFile(new File(file));
		// saver.writeBatch();
	}

	// Load instances from file
	public static Instances loadData(String data) throws Exception {
		BufferedReader reader = new BufferedReader(new FileReader(data));
		Instances dataset = new Instances(reader);
		reader.close();

		// setting class attribute
		dataset.setClassIndex(dataset.numAttributes() - 1);
		return dataset;
	}

	// printing the result
	public static void printResult(Evaluation eval) throws Exception {
		System.out.println(eval.toClassDetailsString());
		System.out.println(eval.toMatrixString());
		System.out.println("Kappa is:" + eval.kappa());
	}

	// Get the evaluation
	public static Evaluation getTrainTest(Instances trainset, Instances testset)
			throws Exception {
		Classifier[] classifiers = {
		// new Bagging(),
		// new AdaBoostM1(),
		// new IBk(),
		// new IBk(3),
		// new IBk(5),
		new NaiveBayes(),
		// new J48(),
		// new SMO(),
		// newVote(),
		// new R1()
		};

		for (int i = 0; i < classifiers.length; i++) {
			Classifier classifier = classifiers[i];
			classifier.buildClassifier(trainset);
			System.out.println("Classifier is done!");
			Evaluation eval = new Evaluation(trainset);
			eval.evaluateModel(classifier, testset);
			printResult(eval);
			return eval;
		}
		return null;
	}

	public static void getPredictionDistribution(Classifier classifier,
			Instances testset, String output) throws Exception {

		// output the labels
		BufferedWriter writer = new BufferedWriter(new FileWriter(output));

		writer.write("True" + "\t");
		for (int i = 0; i < testset.classAttribute().numValues(); i++) {
			String label = testset.classAttribute().value(i);
			writer.write(label + "\t");
		}
		writer.newLine();

		Instances labeled = new Instances(testset);

		for (int j = 0; j < testset.numInstances(); j++) {
			Instance instance = testset.instance(j);

			String label = labeled.classAttribute().value(
					(int) instance.classValue());
			writer.write(label + "\t");

			double clslable = classifier.classifyInstance(instance);
			double p[] = classifier.distributionForInstance(instance);

			for (int i = 0; i < p.length; i++) {
				writer.write(Double.toString(p[i]) + "\t");
				writer.write(String.format("%.3f", p[i]) + "\t");
			}

			writer.newLine();
		}

		writer.flush();
		writer.close();
	}

	public static void getPrediction(Classifier classifier, Instances testset,
			String output) throws Exception {
		// output the labels
		BufferedWriter writer = new BufferedWriter(new FileWriter(output));
		writer.write("True" + "\t" + "Predict");
		writer.newLine();

		Instances labeled = new Instances(testset);

		for (int j = 0; j < testset.numInstances(); j++) {
			Instance instance = testset.instance(j);

			String label = labeled.classAttribute().value(
					(int) instance.classValue());
			// writer.write(instance.stringValue(1)+"\t"+label + "\t");
			// writer.write(instance.stringValue(1)+"\t"+label + "\t");
			writer.write(label + "\t");
			double clslable = classifier.classifyInstance(instance);

			// instance.setClassValue(clslable);
			label = labeled.classAttribute().value((int) clslable);

			writer.write(label);
			writer.newLine();
		}

		writer.flush();
		writer.close();
	}

	public static void GetPridiction(Classifier classifier, Instances testset,
			Instances oriset, String output) throws Exception {
		// output the labels
		BufferedWriter writer = new BufferedWriter(new FileWriter(output));
		writer.write("True" + "\t" + "Predict");
		writer.newLine();

		Instances labeled = new Instances(testset);

		for (int j = 0; j < testset.numInstances(); j++) {
			Instance instance = testset.instance(j);
			Instance oriinstance = oriset.instance(j);
			String label = labeled.classAttribute().value(
					(int) instance.classValue());
			writer.write(oriinstance.stringValue(0) + "\t" + label + "\t");

			double clslable = classifier.classifyInstance(instance);

			// instance.setClassValue(clslable);
			label = labeled.classAttribute().value((int) clslable);

			writer.write(label);
			writer.newLine();
		}

		writer.flush();
		writer.close();
	}

	public static Classifier train(Instances trainSet) throws Exception {
		/*
		 * Classifier[] classifiers = { // new Bagging(), // new AdaBoostM1(),
		 * // new IBk(), // new IBk(3), // new IBk(5), // new NaiveBayes(), //
		 * new J48(), new SMO(), // newVote(), // new R1() };
		 */
		Classifier classifier = new RandomForest();
		classifier = setTagFilter(classifier, trainSet);

		CostSensitiveClassifier csc = new CostSensitiveClassifier();
		csc.setClassifier(classifier);
		double w = getWeight(trainSet);
		System.out.println("Weight:" + w);
		CostMatrix newCostMatrix = new CostMatrix(2);
		newCostMatrix.setCell(0, 1, w);
		newCostMatrix.setCell(1, 0, 1.0);
		csc.setCostMatrix(newCostMatrix);
		classifier = csc;
		csc.buildClassifier(trainSet);
		// classifier.buildClassifier(trainSet);

		return csc;
		// return classifier;
	}

	public static double getWeight(Instances trainset) {
		int w1 = 0;
		int total = trainset.numInstances();
		for (int i = 0; i < total; i++) {
			Instance instance = trainset.instance(i);
			if (instance.classValue() == 0) {
				w1++;
			}
		}
		double weight = (total - w1) * 1.0 / w1;
		return weight;
	}

	public static Evaluation majorityTrainTest(Instances trainset,
			Instances testset) throws Exception {
		Classifier classifier = new ZeroR();
		CostSensitiveClassifier csc = new CostSensitiveClassifier();
		csc.setClassifier(classifier);
		double w = getWeight(trainset);
		System.out.println("Weight:" + w);
		CostMatrix newCostMatrix = new CostMatrix(2);
		newCostMatrix.setCell(0, 1, w);
		newCostMatrix.setCell(1, 0, 1.0);
		csc.setCostMatrix(newCostMatrix);
		classifier = csc;
		// -----end of balancing------------//
		classifier = setTagFilter(classifier, trainset);
		classifier.buildClassifier(trainset);
		System.out.println("Classifier is done!");
		Evaluation eval = new Evaluation(trainset);
		eval.evaluateModel(classifier, testset);
		return eval;
	}

	public static Evaluation crossTrainTest(Instances trainset,
			Instances testset) throws Exception {
		Classifier classifier = new RandomForest();
		boolean sample = false;
		if (sample == true) {
			CostSensitiveClassifier csc = new CostSensitiveClassifier();
			csc.setClassifier(classifier);
			double w = getWeight(trainset);
			System.out.println("Weight:" + w);
			CostMatrix newCostMatrix = new CostMatrix(2);
			newCostMatrix.setCell(0, 1, w);
			newCostMatrix.setCell(1, 0, 1.0);
			csc.setCostMatrix(newCostMatrix);
			classifier = csc;
		}
		// -----end of balancing------------//
		classifier = setTagFilter(classifier, trainset);
		classifier.buildClassifier(trainset);
		System.out.println("Classifier is done!");
		Evaluation eval = new Evaluation(trainset);
		 eval.evaluateModel(classifier, testset);
		WekaAssist.printResult(eval);
		detailedErrorAnalysis(eval, classifier, testset);
		return eval;
	}

	public static void detailedErrorAnalysis(Evaluation eval,
			Classifier classifier, Instances testSet) throws Exception {
		int falsePositive = 0;
		int trueNegative = 0;
		int textIndex = 0;
		int index = testSet.attribute("ID").index();
		int classIndex = testSet.classAttribute().index();
		
		System.out.println("+++++++++++++++++++++++++++++++++++++++++++");
		for (int i = 0; i < testSet.numInstances(); i++) {
			Instance inst = testSet.instance(i);
			double predict = eval.evaluateModelOnce(classifier, inst);
			if (predict == 0.0 && inst.value(classIndex) == 1.0) {
				falsePositive++;
				System.out.println("False positive");
				System.out.println(inst.stringValue(index));
			} else if (predict == 1.0 && inst.value(classIndex) == 0.0) {
				trueNegative++;
				System.out.println("True negative");
				System.out.println(inst.stringValue(index));
			}
		}
		System.out.println("FP COUNT: " + falsePositive);
		System.out.println("TN COUNT: " + trueNegative);
	}

	public static void trainTest(Instances trainset, Instances testset,
			String test) throws Exception {
		Classifier[] classifiers = {
		// new Bagging(),
		// new AdaBoostM1(),
		// new IBk(),
		// new IBk(3),
		// new IBk(5),
		// new NaiveBayes(),
		// new J48(),
		new RandomForest(),
		// newVote(),
		// new R1()
		};

		for (int i = 0; i < classifiers.length; i++) {
			Classifier classifier = classifiers[i];

			// ---balancing------//
			CostSensitiveClassifier csc = new CostSensitiveClassifier();
			csc.setClassifier(classifier);
			CostMatrix newCostMatrix = new CostMatrix(2);
			newCostMatrix.setCell(0, 1, 2.0);
			newCostMatrix.setCell(1, 0, 1.0);
			csc.setCostMatrix(newCostMatrix);
			classifier = csc;
			// -----end of balancing------------//
			classifier = setTagFilter(classifier, trainset);
			classifier.buildClassifier(trainset);
			System.out.println("Classifier is done!");
			Evaluation eval = new Evaluation(trainset);
			eval.evaluateModel(classifier, testset);
			printResult(eval);
			getPrediction(classifier, testset, test + ".label");
		}
	}

	public static void trainTestWithFile(String train, String test)
			throws Exception {
		System.out.print(train + "\t");
		Instances trainset = loadData(train);
		System.out.println(trainset.numAttributes());

		System.out.print(test + "\t");
		Instances testset = loadData(test);
		System.out.println(trainset.numInstances());

		trainTest(trainset, testset, test);
	}

	public static Instances selectFeature(Instances data) throws Exception {
		AttributeSelection filter = new AttributeSelection(); // package
																// weka.filters.supervised.attribute!
		CfsSubsetEval eval = new CfsSubsetEval();
		GreedyStepwise search = new GreedyStepwise();

		search.setSearchBackwards(true);
		filter.setEvaluator(eval);
		filter.setSearch(search);
		filter.setInputFormat(data);

		// generate new data
		Instances newData = Filter.useFilter(data, filter);

		return newData;
	}

	public static Instances performPCA(Instances dataset, int k)
			throws Exception {
		PrincipalComponents pca = new PrincipalComponents();
		pca.setInputFormat(dataset);
		pca.setMaximumAttributes(k);
		Instances newData = Filter.useFilter(dataset, pca);
		return newData;
	}

	public static Instances addWeight(Instances dataset, String weightfile)
			throws Exception {
		Instances weights = loadData(weightfile);
		Attribute wid = weights.attribute("Weight");

		for (int i = 0; i < weights.numInstances(); i++) {
			Instance ins_w = weights.instance(i);
			double weight = ins_w.value(wid);
			dataset.instance(i).setWeight(weight);
		}

		return dataset;
	}

	/**
	 * Filter the attributes
	 * 
	 * Right now for removing markup ID only
	 * 
	 * @param dataset
	 */
	public static Classifier setTagFilter(Classifier classifier,
			Instances dataset) {
		FilteredClassifier fc = new FilteredClassifier();
		int index = dataset.attribute("ID").index();
		Remove rm = new Remove();
		int[] indices = new int[1];
		indices[0] = index;
		rm.setAttributeIndicesArray(indices);
		// rm.setAttributeIndices(Integer.toString(index));
		fc.setClassifier(classifier);
		fc.setFilter(rm);
		return fc;
	}

	public static void showFeatures() {
	}

	public static void crossvalidataion(Instances dataset, int folder)
			throws Exception {
		Boolean OutputPredictionFlag = false;

		System.out.println(dataset.numInstances());

		// LibLINEAR liblinear = new LibLINEAR();
		// liblinear.setOptions(weka.core.Utils.splitOptions("-S 1 -C 1.0 -E 0.01 -B 1.0"));
		// SMO smo = new SMO();
		// smo.setOptions(weka.core.Utils.splitOptions("-C 1.0 -L 0.001 -P 1.0E-12 -N 0 -V -1 -W 1 -K \"weka.classifiers.functions.supportVector.RBFKernel -C 250007 -G 0.01\""));

		// WLSVM libsvm = new WLSVM();
		// libsvm.setOptions(weka.core.Utils.splitOptions("-S 1 -C 1.0 -E 0.01 -B 1.0"));

		/*
		 * FilteredClassifier fc = new FilteredClassifier();
		 * fc.setClassifier(new SMO());
		 * 
		 * Attribute id = dataset.attribute("ID"); if (id != null) { int
		 * attributes[] = new int[1]; attributes[0] = id.index();
		 * 
		 * Remove filter = new Remove();
		 * filter.setAttributeIndicesArray(attributes); fc.setFilter(filter); }
		 */

		Classifier[] classifiers = { new RandomForest(), new ZeroR()
		// new J48(),
		// new SMO(),
		// fc,
		// libsvm,
		// smo,
		// new IBk(3),
		// new Logistic(),
		// liblinear,
		// new AdaBoostM1(),
		// new Bagging(),
		};

		for (int i = 0; i < classifiers.length; i++) {
			Classifier classifier = classifiers[i];
			classifier = setTagFilter(classifier, dataset);
			// CostSensitiveClassifier csClassifier = new
			// CostSensitiveClassifier();
			// csClassifier.setClassifier(classifier);
			classifier.buildClassifier(dataset);
			// csClassifier.buildClassifier(dataset);
			Evaluation eval = new Evaluation(dataset);

			if (OutputPredictionFlag) {
				StringBuffer forPredictionsPrinting = new StringBuffer();

				// the prediction format is: StringBuffer, Attribute Range,
				// setOutputDistribution
				eval.crossValidateModel(classifier, dataset, folder,
						new Random(1), forPredictionsPrinting, new Range(
								"first"), false);
				printResult(eval);

				ArrayList<ArrayList<String>> predictions = new ArrayList<ArrayList<String>>();

				String[] lines = forPredictionsPrinting.toString().split("\n");
				System.out.println(lines[0]);
				Pattern pattern = Pattern
						.compile("\\d+\\s+\\d+:(\\w+)\\s+\\d+:(\\w+).*\\((\\d+)\\)");
				for (String line : lines) {
					Matcher m = pattern.matcher(line);
					if (m.find()) {
						ArrayList<String> tmp = new ArrayList<String>();
						tmp.add(m.group(1));
						tmp.add(m.group(2));
						tmp.add(m.group(3));
						predictions.add(tmp);
						// System.out.println(m.group(1) + "\t" + m.group(2) +
						// "\t" + m.group(3));
					}
				}

				// sort according to ID
				Collections.sort(predictions,
						new Comparator<ArrayList<String>>() {
							public int compare(ArrayList<String> ins1,
									ArrayList<String> ins2) {
								int id1 = Integer.parseInt(ins1.get(2));
								int id2 = Integer.parseInt(ins2.get(2));
								return id1 - id2;
							}
						});

				for (ArrayList<String> predict : predictions) {
					for (String entry : predict) {
						System.out.print(entry + "\t");
					}
					System.out.println();
				}

			} else {
				eval.crossValidateModel(classifier, dataset, folder,
						new Random(1));
				printResult(eval);

				System.out.println(eval.toSummaryString());
			}

			// System.out.println(classifierOutput.getAttributes());
			// System.out.println(classifierOutput.getDisplay());
		}
	}

	public static void crossvalidationWithFile(String data, int folder)
			throws Exception {
		Instances dataset = loadData(data);
		System.out.print(data + "\t");
		crossvalidataion(dataset, folder);
	}

	public static void getAvg(ArrayList<Evaluation> evals) {
		double tp_1 = 0;
		double fn_1 = 0;
		double prec_1 = 0;
		double recall_1 = 0;
		double f_1 = 0;

		double tp_0 = 0;
		double fn_0 = 0;
		double prec_0 = 0;
		double recall_0 = 0;
		double f_0 = 0;

		double tp_weighted = 0;
		double fn_weighted = 0;
		double prec_weighted = 0;
		double recall_weighted = 0;
		double f_weighted = 0;

		int total = 0;
		for (int i = 0; i < evals.size(); i++) {
			Evaluation eval = evals.get(i);
			tp_1 += eval.truePositiveRate(1) * eval.numInstances();
			fn_1 += eval.falseNegativeRate(1) * eval.numInstances();
			prec_1 += eval.precision(1) * eval.numInstances();
			recall_1 += eval.recall(1) * eval.numInstances();
			f_1 += eval.fMeasure(1) * eval.numInstances();

			tp_0 += eval.truePositiveRate(0) * eval.numInstances();
			fn_0 += eval.falseNegativeRate(0) * eval.numInstances();
			prec_0 += eval.precision(0) * eval.numInstances();
			recall_0 += eval.recall(0) * eval.numInstances();
			f_0 += eval.fMeasure(0) * eval.numInstances();

			tp_weighted += eval.weightedTruePositiveRate()
					* eval.numInstances();
			fn_weighted += eval.weightedFalseNegativeRate()
					* eval.numInstances();
			prec_weighted += eval.weightedPrecision() * eval.numInstances();
			recall_weighted += eval.weightedRecall() * eval.numInstances();
			f_weighted += eval.weightedFMeasure() * eval.numInstances();

			total += eval.numInstances();
		}

		// total = evals.size()*total;
		tp_1 = tp_1 / total;
		fn_1 = fn_1 / total;
		prec_1 = prec_1 / total;
		recall_1 = recall_1 / total;
		f_1 = f_1 / total;

		tp_0 = tp_0 / total;
		fn_0 = fn_0 / total;
		prec_0 = prec_0 / total;
		recall_0 = recall_0 / total;
		f_0 = f_0 / total;

		tp_weighted = tp_weighted / total;
		fn_weighted = fn_weighted / total;
		prec_weighted = prec_weighted / total;
		recall_weighted = recall_weighted / total;
		f_weighted = f_weighted / total;

		System.out.println("TP Rate\tFP Rate\tPrecision\tRecall\tF-Measure");
		System.out.println(tp_1 + "\t" + fn_1 + "\t" + prec_1 + "\t" + recall_1
				+ "\t" + f_1);
		System.out.println(tp_0 + "\t" + fn_0 + "\t" + prec_0 + "\t" + recall_0
				+ "\t" + f_0);
		System.out.println(tp_weighted + "\t" + fn_weighted + "\t"
				+ prec_weighted + "\t" + recall_weighted + "\t" + f_weighted);
	}
}
