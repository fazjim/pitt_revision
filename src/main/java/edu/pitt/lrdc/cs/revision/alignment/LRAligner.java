package edu.pitt.lrdc.cs.revision.alignment;

import weka.classifiers.Classifier;
import weka.classifiers.functions.Logistic;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SerializationHelper;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Random;

import edu.pitt.lrdc.cs.revision.alignment.*;
import edu.pitt.lrdc.cs.revision.alignment.distance.LDCalculator;
import edu.pitt.lrdc.cs.revision.alignment.distance.TFIDFCounter;
import edu.pitt.lrdc.cs.revision.alignment.model.Document;
import edu.pitt.lrdc.cs.revision.alignment.model.DocumentPair;
import edu.pitt.lrdc.cs.revision.alignment.model.Sentence;

/**
 * Logisitic regression trainer
 * 
 * @author zhangfan
 * 
 */
public class LRAligner {
	private Logistic logClassifier;
	private LDCalculator ldc = new LDCalculator();
	private TFIDFCounter counter = new TFIDFCounter();

	public void persistClassifier() throws Exception {
		String classifierPath = "aligner.model";
		SerializationHelper.write(classifierPath, logClassifier);
	}

	public void loadClassifier() throws Exception {
		Classifier cl = (Classifier) weka.core.SerializationHelper
				.read("aligner.model");
		logClassifier = (Logistic) cl;
	}

	public Logistic getLogClassifier() {
		return logClassifier;
	}

	public void setLogClassifier(Logistic logClassifier) {
		this.logClassifier = logClassifier;
	}

	public LDCalculator getLdc() {
		return ldc;
	}

	public void setLdc(LDCalculator ldc) {
		this.ldc = ldc;
	}

	public TFIDFCounter getCounter() {
		return counter;
	}

	public void setCounter(TFIDFCounter counter) {
		this.counter = counter;
	}

	public LRAligner() {
		logClassifier = new Logistic();
	}

	public void train(ArrayList<DocumentPair> dps, int option) throws Exception {
		System.out.println("Building instances...");
		Instances ins = buildInstances(dps, option);
		System.out.println("Start training...");
		logClassifier.buildClassifier(ins);
	}

	public double classifySingle(Sentence a, Sentence b, int option)
			throws Exception {
		double[] atts = new double[2];
		atts[0] = calc(a, b, option);
		atts[1] = 0;
		Instance instance = new DenseInstance(1.0, atts);
		return logClassifier.distributionForInstance(instance)[0];
	}

	/**
	 * If two sentences are the same without the punctuation information
	 * 
	 * @param src
	 * @param dst
	 * @return
	 */
	public boolean exactMatch(Sentence src, Sentence dst) {
		String srcContent = src.getContent();
		String dstContent = dst.getContent();
		srcContent = srcContent.trim();
		dstContent = dstContent.trim();
		srcContent = srcContent.replaceAll("\\.", " ");
		srcContent = srcContent.replaceAll(",", " ");
		srcContent = srcContent.replaceAll("!", " ");

		dstContent = dstContent.replaceAll("\\.", " ");
		dstContent = dstContent.replaceAll(",", " ");
		dstContent = dstContent.replaceAll("!", " ");

		String[] srcTokens = srcContent.split(" ");
		String[] dstTokens = dstContent.split(" ");

		if (srcTokens.length != dstTokens.length)
			return false;
		for (int i = 0; i < srcTokens.length; i++) {
			if (!srcTokens[i].equals(dstTokens[i]))
				return false;
		}
		return true;
	}

	/**
	 * Consider two sentences are the same if they are 80% identical to each
	 * other
	 * 
	 * @param src
	 * @param dst
	 * @return
	 */
	public boolean exactMatch2(Sentence src, Sentence dst) {
		String srcContent = src.getContent();
		String dstContent = dst.getContent();
		srcContent = srcContent.trim();
		dstContent = dstContent.trim();
		srcContent = srcContent.replaceAll("\\.", " ");
		srcContent = srcContent.replaceAll(",", " ");
		srcContent = srcContent.replaceAll("!", " ");

		dstContent = dstContent.replaceAll("\\.", " ");
		dstContent = dstContent.replaceAll(",", " ");
		dstContent = dstContent.replaceAll("!", " ");

		srcContent = srcContent.toLowerCase().trim();
		dstContent = dstContent.toLowerCase().trim();
		String[] srcTokens = srcContent.split(" ");
		String[] dstTokens = dstContent.split(" ");

		Hashtable<String, Integer> srcTable = new Hashtable<String, Integer>();
		Hashtable<String, Integer> dstTable = new Hashtable<String, Integer>();

		for (int i = 0; i < srcTokens.length; i++) {
			if (srcTable.containsKey(srcTokens[i])) {
				srcTable.put(srcTokens[i], srcTable.get(srcTokens[i]) + 1);
			} else {
				srcTable.put(srcTokens[i], 1);
			}
		}

		for (int i = 0; i < dstTokens.length; i++) {
			if (dstTable.containsKey(dstTokens[i])) {
				dstTable.put(dstTokens[i], dstTable.get(dstTokens[i]) + 1);
			} else {
				dstTable.put(dstTokens[i], 1);
			}
		}

		Iterator<String> it = srcTable.keySet().iterator();
		int covers = 0;
		while (it.hasNext()) {
			String key = it.next();
			int a = srcTable.get(key);
			int b = 0;
			if (dstTable.containsKey(key)) {
				b = dstTable.get(key);
			}

			if (a < b) {
				covers += a;
			} else {
				covers += b;
			}
		}

		double ratio = covers * 1.0 / srcTokens.length;
		if (ratio > 0.8) {
			return true;
		} else {
			return false;
		}
	}

	public ArrayList<double[][]> classifyDocuments(ArrayList<DocumentPair> dps,
			int option) throws Exception {
		ArrayList<double[][]> vals = new ArrayList<double[][]>();
		if (option == -1) {
			// exact match baseline method
			for (int i = 0; i < dps.size(); i++) {
				DocumentPair dp = dps.get(i);
				Document src = dp.getSrc();
				Document dest = dp.getModified();
				double[][] val = new double[src.getSentences().size()][dest
						.getSentences().size()];
				for (int j = 0; j < src.getSentences().size(); j++) {
					for (int k = 0; k < dest.getSentences().size(); k++) {
						if (exactMatch2(src.getSentences().get(j), dest
								.getSentences().get(k))) {
							val[j][k] = 1;
						} else {
							val[j][k] = 0;
						}
					}
				}
				vals.add(val);
			}
		} else {
			for (int i = 0; i < dps.size(); i++) {
				DocumentPair dp = dps.get(i);
				Document src = dp.getSrc();
				Document dest = dp.getModified();
				double[][] val = new double[src.getSentences().size()][dest
						.getSentences().size()];
				for (int j = 0; j < src.getSentences().size(); j++) {
					for (int k = 0; k < dest.getSentences().size(); k++) {
						double[] atts = new double[2];
						atts[0] = calc(src.getSentences().get(j), dest
								.getSentences().get(k), option);
						atts[1] = 0;
						Instance instance = new DenseInstance(1.0, atts);
						val[j][k] = (logClassifier
								.distributionForInstance(instance))[0];
					}
				}
				vals.add(val);
			}
		}
		return vals;
	}

	public boolean compare(ArrayList<Integer> orig, ArrayList<Integer> dst) {
		if (orig.size() != dst.size()) {
			return false;
		} else {
			for (int i = 0; i < orig.size(); i++) {
				if (orig.get(i) != dst.get(i))
					return false;
			}
		}
		return true;
	}

	public void printSentences(Document doc, Sentence sen) {
		System.out.println(sen.getContent());
		System.out.println("GOLD STANDARD:");
		for (int i = 0; i < sen.getAlignedIndex().size(); i++) {
			System.out.print(sen.getAlignedIndex().get(i) + "\t");
		}
		System.out.println();
		for (int i = 0; i < sen.getAlignedIndex().size(); i++) {
			System.out.println(doc.getSentences()
					.get(sen.getAlignedIndex().get(i) - 1).getContent());
		}
		System.out.println("ANNOTATED:");
		for (int i = 0; i < sen.getPredictedIndex().size(); i++) {
			System.out.print(sen.getPredictedIndex().get(i) + "\t");
		}
		System.out.println();
		for (int i = 0; i < sen.getPredictedIndex().size(); i++) {
			System.out.println(doc.getSentences()
					.get(sen.getPredictedIndex().get(i) - 1).getContent());
		}
	}

	public void evaluate2(ArrayList<double[][]> results,
			ArrayList<DocumentPair> dps) {
		int total = 0;
		int tp = 0;
		for (int i = 0; i < results.size(); i++) {
			double[][] val = results.get(i);
			DocumentPair dp = dps.get(i);
			Document src = dp.getSrc();
			Document dst = dp.getModified();
			for (int j = 0; j < dst.getSentences().size(); j++) {
				total++;
				ArrayList<Integer> predictedIndex = new ArrayList<Integer>();
				for (int k = 0; k < val.length; k++) {
					if (val[k][j] >= 0.5) {
						predictedIndex.add(k + 1);
					}
				}
				dst.getSentences().get(j).setPredictedIndex(predictedIndex);
				if (compare(dst.getSentences().get(j).getAlignedIndex(), dst
						.getSentences().get(j).getPredictedIndex())) {
					tp++;
				} else {
					System.out.println("==============");
					System.out.println("ERROR:");
					// printSentences(src, dst.getSentences().get(j));
					System.out.println("==============");
				}
			}
		}
		System.out.println("Accuracy is: " + tp + " /" + total + "="
				+ (tp * 1.0) / total);

		/*
		 * DocumentPair dp = dps.get(0); Document src = dp.getSrc(); Document
		 * dst = dp.getModified(); for(int i = 0;i<20;i++) {
		 * System.out.print((i+1)+":"); for(int j =
		 * 0;j<dst.getSentences().get(i).getPredictedIndex().size();j++) {
		 * System
		 * .out.print(dst.getSentences().get(i).getPredictedIndex().get(j)+
		 * " "); } System.out.println(); } for(int i = 0;i<20;i++) {
		 * System.out.print((i+1)+":"); for(int j =
		 * 0;j<dst.getSentences().get(i).getAlignedIndex().size();j++) {
		 * System.out.print(dst.getSentences().get(i).getAlignedIndex().get(j)+
		 * " "); } System.out.println(); }
		 */
	}

	public void evaluateVote(ArrayList<double[][]> results,
			ArrayList<double[][]> results1, ArrayList<double[][]> results2,
			ArrayList<DocumentPair> dps) {
		int total = 0;
		int tp = 0;
		for (int i = 0; i < results.size(); i++) {
			double[][] val = results.get(i);
			double[][] val1 = results1.get(i);
			double[][] val2 = results2.get(i);
			DocumentPair dp = dps.get(i);
			Document src = dp.getSrc();
			Document dst = dp.getModified();
			for (int j = 0; j < dst.getSentences().size(); j++) {
				total++;
				ArrayList<Integer> predictedIndex = new ArrayList<Integer>();
				for (int k = 0; k < val.length; k++) {
					if (val[k][j] >= 0.5 && val1[k][j] >= 0.5
							&& val2[k][j] >= 0.5) {
						predictedIndex.add(k + 1);
					}
				}
				dst.getSentences().get(j).setPredictedIndex(predictedIndex);
				if (compare(dst.getSentences().get(j).getAlignedIndex(), dst
						.getSentences().get(j).getPredictedIndex())) {
					tp++;
				} else {
					System.out.println("==============");
					System.out.println("ERROR:");
					printSentences(src, dst.getSentences().get(j));
					System.out.println("==============");
				}
			}
		}
		System.out.println("Accuracy is: " + tp + " /" + total + "="
				+ (tp * 1.0) / total);
	}

	public void evaluate(ArrayList<double[][]> results,
			ArrayList<DocumentPair> dps) {
		int total = 0;
		int tp = 0;
		for (int i = 0; i < results.size(); i++) {
			double[][] val = results.get(i);
			DocumentPair dp = dps.get(i);
			Document src = dp.getSrc();
			Document dst = dp.getModified();
			for (int j = 0; j < dst.getSentences().size(); j++) {
				ArrayList<Integer> indexes = dst.getSentences().get(j)
						.getAlignedIndex();
				for (int l = 0; l < indexes.size(); l++) {
					total++;
					int indexedL = indexes.get(l);
					if (val[indexedL - 1][j] > 0.5) {
						tp++;
					} else {
						System.out.println("ERROR:");
						System.out.println("SRC:"
								+ src.getSentences().get(indexedL - 1)
										.getContent());
						System.out.println("DST:"
								+ dst.getSentences().get(j).getContent());
					}
				}
			}
		}
		System.out.println("Accuracy is: " + tp + " /" + total + "="
				+ (tp * 1.0) / total);
	}

	public void classify(ArrayList<DocumentPair> dps, int option)
			throws Exception {
		Instances ins = buildInstances(dps, option);
		int tp = 0;
		int tn = 0;
		int fp = 0;
		int fn = 0;
		int dataSize = ins.numInstances();
		for (int i = 0; i < dataSize; i++) {
			Instance instance = ins.instance(i);
			double val = logClassifier.classifyInstance(instance);
			double[] results = logClassifier.distributionForInstance(instance);
			for (int j = 0; j < results.length; j++) {
				System.out.print(results[j] + "\t");
			}
			System.out.println();
		}
	}

	public double calc(Sentence src, Sentence dst, int option) {
		if (option == 0) {
			return ldc.calcSen(src, dst);
		} else if (option == 1) {
			return counter.calcOverlap(src, dst);
		} else if (option == 2) {
			return counter.calc(src, dst);
		}
		return 0;
	}

	public void addInstance(DocumentPair dp, FastVector attVals, Instances ins,
			int option) {
		Document src = dp.getSrc();
		Document dst = dp.getModified();

		ArrayList<Sentence> sentences = dst.getSentences();
		int size = src.getSentences().size();

		Random rand = new Random();
		for (int i = 0; i < sentences.size(); i++) {
			Sentence sen = sentences.get(i);
			ArrayList<Integer> alignedIndex = sen.getAlignedIndex();

			// Create one true case
			for (int j = 0; j < alignedIndex.size(); j++) {
				double val = calc(
						src.getSentences().get(alignedIndex.get(j) - 1), sen,
						option);
				double[] vals = new double[ins.numAttributes()];
				vals[0] = val;
				vals[1] = attVals.indexOf("positive");
				Instance instance = new DenseInstance(1.0, vals);
				ins.add(instance);
			}

			// Create randomly generated negative case
			int index = rand.nextInt(size);
			boolean isIn = true;
			while (isIn) {
				isIn = false;
				for (int j = 0; j < alignedIndex.size(); j++) {
					if (index + 1 == alignedIndex.get(j)) {
						isIn = true;
					}
				}
				if (isIn == false) {
					double sim = calc(src.getSentences().get(index),
							sentences.get(i), option);
					double[] vals = new double[ins.numAttributes()];
					vals[0] = sim;
					vals[1] = attVals.indexOf("negative");
					Instance instance = new DenseInstance(1.0, vals);
					ins.add(instance);
				} else {
					index = rand.nextInt(size);
				}
			}
		}
	}

	// build data
	public Instances buildInstances(ArrayList<DocumentPair> dps, int option) {
		FastVector atts = new FastVector(); // all attributes
		atts.addElement(new Attribute("Similarity"));// text
		FastVector fvClassVal = new FastVector();
		fvClassVal.addElement("positive");
		fvClassVal.addElement("negative");
		atts.addElement(new Attribute("category", fvClassVal));

		Instances training = new Instances("MyRelation", atts, 0);
		for (int i = 0; i < dps.size(); i++) {
			DocumentPair dp = dps.get(i);
			addInstance(dp, fvClassVal, training, option);
		}
		training.setClassIndex(1);
		return training;
	}

	public void trainMix(ArrayList<DocumentPair> dps, int[] options)
			throws Exception {
		System.out.println("Building instances...");
		Instances ins = buildInstancesMix(dps, options);
		System.out.println("Start training...");
		logClassifier.buildClassifier(ins);
	}

	public Instances buildInstancesMix(ArrayList<DocumentPair> dps,
			int[] options) {
		FastVector atts = new FastVector(); // all attributes
		for (int i = 0; i < options.length; i++) {
			atts.addElement(new Attribute("Similarity" + options[i]));// text
		}
		FastVector fvClassVal = new FastVector();
		fvClassVal.addElement("positive");
		fvClassVal.addElement("negative");
		atts.addElement(new Attribute("category", fvClassVal));

		Instances training = new Instances("MyRelation", atts, 0);
		for (int i = 0; i < dps.size(); i++) {
			DocumentPair dp = dps.get(i);
			addInstanceMix(dp, fvClassVal, training, options);
		}
		training.setClassIndex(options.length);
		return training;
	}

	public void addInstanceMix(DocumentPair dp, FastVector attVals,
			Instances ins, int[] options) {
		Document src = dp.getSrc();
		Document dst = dp.getModified();

		ArrayList<Sentence> sentences = dst.getSentences();
		int size = src.getSentences().size();

		Random rand = new Random();
		for (int i = 0; i < sentences.size(); i++) {
			Sentence sen = sentences.get(i);
			ArrayList<Integer> alignedIndex = sen.getAlignedIndex();

			// Create one true case
			for (int j = 0; j < alignedIndex.size(); j++) {
				double[] vals = new double[ins.numAttributes()];
				for (int k = 0; k < options.length; k++) {
					double val = calc(
							src.getSentences().get(alignedIndex.get(j) - 1),
							sen, options[k]);
					vals[k] = val;
				}
				vals[options.length] = attVals.indexOf("positive");
				Instance instance = new DenseInstance(1.0, vals);
				ins.add(instance);
			}

			// Create randomly generated negative case
			int index = rand.nextInt(size);
			boolean isIn = true;
			while (isIn) {
				isIn = false;
				for (int j = 0; j < alignedIndex.size(); j++) {
					if (index + 1 == alignedIndex.get(j)) {
						isIn = true;
					}
				}
				if (isIn == false) {
					double[] vals = new double[ins.numAttributes()];
					for (int k = 0; k < options.length; k++) {
						double sim = calc(src.getSentences().get(index),
								sentences.get(i), options[k]);
						vals[k] = sim;
					}
					vals[options.length] = attVals.indexOf("negative");
					Instance instance = new DenseInstance(1.0, vals);
					ins.add(instance);
				} else {
					index = rand.nextInt(size);
				}
			}
		}
	}

	public ArrayList<double[][]> classifyDocumentsMix(
			ArrayList<DocumentPair> dps, int[] options) throws Exception {
		ArrayList<double[][]> vals = new ArrayList<double[][]>();
		for (int i = 0; i < dps.size(); i++) {
			DocumentPair dp = dps.get(i);
			Document src = dp.getSrc();
			Document dest = dp.getModified();
			double[][] val = new double[src.getSentences().size()][dest
					.getSentences().size()];
			for (int j = 0; j < src.getSentences().size(); j++) {
				for (int k = 0; k < dest.getSentences().size(); k++) {
					double[] atts = new double[options.length + 1];
					for (int m = 0; m < options.length; m++) {
						atts[m] = calc(src.getSentences().get(j), dest
								.getSentences().get(k), options[m]);
					}
					atts[options.length] = 0;
					Instance instance = new DenseInstance(1.0, atts);
					val[j][k] = (logClassifier
							.distributionForInstance(instance))[0];
				}
			}
			vals.add(val);
		}
		return vals;
	}
}
