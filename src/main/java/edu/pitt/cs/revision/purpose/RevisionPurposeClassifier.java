package edu.pitt.cs.revision.purpose;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;

import scala.collection.mutable.HashSet;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Instances;
import weka.core.SerializationHelper;
import weka.core.converters.ArffSaver;
import edu.pitt.cs.revision.machinelearning.FeatureName;
import edu.pitt.cs.revision.machinelearning.WekaAssist;
import edu.pitt.lrdc.cs.revision.io.RevisionDocumentReader;
import edu.pitt.lrdc.cs.revision.model.RevisionDocument;
import edu.pitt.lrdc.cs.revision.model.RevisionOp;
import edu.pitt.lrdc.cs.revision.model.RevisionPurpose;
import edu.pitt.lrdc.cs.revision.model.RevisionUnit;

/**
 * The current code does not have a neat design, which makes the code redundant
 * and messy, thinking about refining it later
 * 
 * @author zhf4pal
 *
 */
public class RevisionPurposeClassifier {
	boolean modifyOnly = false;

	public static int SURFACECLASSIFY = 1;
	public static int ALLCLASSIFY = 2;

	String trainedFeatures = "/features.arff";

	/**
	 * 
	 * @param docs
	 * @param option
	 * @param usingNgram
	 *            If uses n-gram, it means the classifier cannot be saved
	 * @param featurePath
	 *            We can save the instances
	 * @return
	 * @throws IOException
	 */
	public boolean generateTrainingFeatures(ArrayList<RevisionDocument> docs,
			int option, boolean usingNgram, String featurePath)
			throws IOException {
		if (option == SURFACECLASSIFY) {
			// Only uses modify operations
			ArrayList<String> categories = new ArrayList<String>();
			WekaAssist wa = new WekaAssist();
			// categories.add(SURFACE_CHANGE);
			// categories.add(CONTENT_CHANGE);
			FeatureExtractor fe = new FeatureExtractor();
			//Comment this for the moment, skip the POS tagging part
			//fe.openBatchMode(batchPath);

			addPurposeCategories2(categories);
			fe.buildFeatures(usingNgram, categories);
			Instances data = wa.buildInstances(fe.features, usingNgram);
			Instances testData = wa.buildInstances(fe.features, usingNgram);
			for (RevisionDocument doc : docs) {
				ArrayList<RevisionUnit> basicUnits = doc.getRoot()
						.getRevisionUnitAtLevel(0);
				for (RevisionUnit ru : basicUnits) {
					if (ru.getRevision_op() == RevisionOp.MODIFY) {
						Object[] features = fe.extractFeatures(doc, ru,
								usingNgram);
						wa.addInstance(features, fe.features, usingNgram, data,
								transformPurpose2(ru), "dummy");
					}
				}
			}
			/*
			ArffSaver saver = new ArffSaver();
			saver.setInstances(data);
			System.err.println("Path is"+new File(trainedFeatures).getAbsolutePath());
			saver.setFile(new File(trainedFeatures));
			saver.writeBatch();*/
		} else {
			ArrayList<String> categories = new ArrayList<String>();

			WekaAssist wa = new WekaAssist();
			for (int i = RevisionPurpose.START; i <= RevisionPurpose.END; i++) {
				categories = new ArrayList<String>();
				FeatureExtractor fe = new FeatureExtractor();
				fe.openBatchMode(batchPath);
				addPurposeCategoriesBinary(categories);
				// fe.buildFeatures(usingNgram, categories);
				fe.buildFeatures(usingNgram, categories);// do not use ngram
				Instances data = wa.buildInstances(fe.features, usingNgram);
				Instances testData = wa.buildInstances(fe.features, usingNgram);
				for (RevisionDocument doc : docs) {
					ArrayList<RevisionUnit> basicUnits = doc.getRoot()
							.getRevisionUnitAtLevel(0);
					for (RevisionUnit ru : basicUnits) {
						Object[] features = fe.extractFeatures(doc, ru,
								usingNgram);
						wa.addInstance(features, fe.features, usingNgram, data,
								transformPurposeBinary(ru, i), "dummy");
					}
				}
				/*
				ArffSaver saver = new ArffSaver();
				saver.setInstances(data);
				String featureFile = "/features_" + i + ".arff";
				saver.setFile(new File(featureFile));
				saver.writeBatch();*/
			}
		}
		return true;
	}

	public boolean generateLightweightBinaryClassifier(Instances train)
			throws Exception {
		WekaAssist wa = new WekaAssist();
		Classifier cl = wa.train(train);
		String classifierPath = "classifier.model";
		SerializationHelper.write(classifierPath, cl);
		return true;
	}

	public boolean generateLightweightClassifier(
			Hashtable<Integer, Instances> trains) throws Exception {
		for (int i = RevisionPurpose.START; i <= RevisionPurpose.END; i++) {
			WekaAssist wa = new WekaAssist();
			Classifier cl = wa.train(trains.get(i));
			String classifierPath = "classifier_" + i + ".model";
			SerializationHelper.write(classifierPath, cl);
		}
		return true;
	}

	/**
	 * This one does not need unigram So just store the classifier
	 * 
	 * @param featurePath
	 * @param option
	 * @return
	 * @throws Exception
	 */
	public boolean generateLightWeightClassifier(
			ArrayList<RevisionDocument> docs, int option) throws Exception {
		if (option == SURFACECLASSIFY) {
			// Only uses modify operations
			ArrayList<String> categories = new ArrayList<String>();
			WekaAssist wa = new WekaAssist();
			// categories.add(SURFACE_CHANGE);
			// categories.add(CONTENT_CHANGE);
			FeatureExtractor fe = new FeatureExtractor();
			fe.openBatchMode(batchPath);

			addPurposeCategories2(categories);
			fe.buildFeatures(false, categories);
			Instances data = wa.buildInstances(fe.features, false);
			for (RevisionDocument doc : docs) {
				ArrayList<RevisionUnit> basicUnits = doc.getRoot()
						.getRevisionUnitAtLevel(0);
				for (RevisionUnit ru : basicUnits) {
					if (ru.getRevision_op() == RevisionOp.MODIFY) {
						Object[] features = fe.extractFeatures(doc, ru, false);
						wa.addInstance(features, fe.features, false, data,
								transformPurpose2(ru), "dummy");
					}
				}
			}
			Classifier cl = wa.train(data);

			String classifierPath = "classifier.model";
			SerializationHelper.write(classifierPath, cl);
		} else {
			ArrayList<String> categories = new ArrayList<String>();

			WekaAssist wa = new WekaAssist();
			for (int i = RevisionPurpose.START; i <= RevisionPurpose.END; i++) {
				FeatureExtractor fe = new FeatureExtractor();
				fe.openBatchMode(batchPath);
				categories = new ArrayList<String>();
				addPurposeCategoriesBinary(categories);
				// fe.buildFeatures(false, categories);
				fe.buildFeatures(false, categories);// do not use ngram
				Instances data = wa.buildInstances(fe.features, false);
				Instances testData = wa.buildInstances(fe.features, false);
				for (RevisionDocument doc : docs) {
					ArrayList<RevisionUnit> basicUnits = doc.getRoot()
							.getRevisionUnitAtLevel(0);
					for (RevisionUnit ru : basicUnits) {
						Object[] features = fe.extractFeatures(doc, ru, false);
						wa.addInstance(features, fe.features, false, data,
								transformPurposeBinary(ru, i), "dummy");
					}
				}
				Classifier cl = wa.train(data);

				String classifierPath = "classifier_" + i + ".model";
				SerializationHelper.write(classifierPath, cl);
			}
		}
		return true;
	}

	public Instances loadBinaryInstances() throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(
				"features.arff"));
		Instances data = new Instances(reader);
		data.setClass(data.attribute("category"));
		reader.close();
		return data;
	}

	public Hashtable<Integer, Instances> loadInstances() throws IOException {
		Hashtable<Integer, Instances> instanceTable = new Hashtable<Integer, Instances>();
		for (int i = RevisionPurpose.START; i <= RevisionPurpose.END; i++) {
			BufferedReader reader = new BufferedReader(new FileReader(
					"features_" + i + ".arff"));
			Instances data = new Instances(reader);
			data.setClass(data.attribute("category"));
			reader.close();
			instanceTable.put(i, data);
		}
		return instanceTable;
	}

	/**
	 * Only for light weight classifiers
	 * 
	 * @return
	 * @throws Exception
	 */
	public Hashtable<Integer, Classifier> loadAllClassifiers() throws Exception {
		Hashtable<Integer, Classifier> classifiers = new Hashtable<Integer, Classifier>();
		for (int i = RevisionPurpose.START; i <= RevisionPurpose.END; i++) {
			Classifier cls = (Classifier) weka.core.SerializationHelper
					.read("classifier_" + i + ".model");
			classifiers.put(i, cls);
		}
		return classifiers;
	}

	public boolean existAllClassifiers() {
		for (int i = RevisionPurpose.START; i <= RevisionPurpose.END; i++) {
			File f = new File("classifier_" + i + ".model");
			if (!f.exists())
				return false;
		}
		return true;
	}

	public boolean existClassifier() {
		File f = new File("classifier.model");
		return f.exists();
	}

	/**
	 * Load a binary classifier
	 * 
	 * @return
	 * @throws Exception
	 */
	public Classifier loadBinaryClassifier() throws Exception {
		return (Classifier) weka.core.SerializationHelper
				.read("classifier.model");
	}

	public Classifier trainDocs(ArrayList<RevisionDocument> trainDocs,
			ArrayList<RevisionDocument> testDocs, boolean usingNgram, int option)
			throws Exception {
		WekaAssist wa = new WekaAssist();
		FeatureExtractor fe = new FeatureExtractor();
		fe.openBatchMode("C:\\Not Backed Up\\data\\annotated\\batch");
		if (option == SURFACECLASSIFY) {
			ArrayList<String> categories = new ArrayList<String>();
			// categories.add(SURFACE_CHANGE);
			// categories.add(CONTENT_CHANGE);
			addPurposeCategories2(categories);
			fe.buildFeatures(usingNgram, categories);
			Instances data = wa.buildInstances(fe.features, usingNgram);
			Instances testData = wa.buildInstances(fe.features, usingNgram);
			for (RevisionDocument doc : trainDocs) {
				ArrayList<RevisionUnit> basicUnits = doc.getRoot()
						.getRevisionUnitAtLevel(0);
				for (RevisionUnit ru : basicUnits) {
					if (modifyOnly) {
						if (ru.getRevision_op() == RevisionOp.MODIFY) {
							Object[] features = fe.extractFeatures(doc, ru,
									usingNgram);
							wa.addInstance(features, fe.features, usingNgram,
									data, transformPurpose2(ru), "dummy");
						}
					} else {
						Object[] features = fe.extractFeatures(doc, ru,
								usingNgram);
						wa.addInstance(features, fe.features, usingNgram, data,
								transformPurpose2(ru), "dummy");
					}
				}
			}
			for (RevisionDocument doc : testDocs) {
				ArrayList<RevisionUnit> basicUnits = doc.getPredictedRoot()
						.getUnits();
				for (RevisionUnit ru : basicUnits) {
					if (modifyOnly) {
						if (ru.getRevision_op() == RevisionOp.MODIFY) {
							Object[] features = fe.extractFeatures(doc, ru,
									usingNgram);
							wa.addInstance(features, fe.features, usingNgram,
									testData, transformPurpose2(ru), "dummy");
						}
					} else {
						Object[] features = fe.extractFeatures(doc, ru,
								usingNgram);
						wa.addInstance(features, fe.features, usingNgram, data,
								transformPurpose2(ru), "dummy");
					}
				}
			}

			if (usingNgram) {
				System.out.println("Adding ngrams");
				Instances[] inst = wa.addNgram(data, testData);
				data = inst[0];
				testData = inst[1];
			}
			Classifier cl = wa.train(data);
			return cl;
		} else {
			return null;// modify the code later
		}
	}

	public Classifier trainAllDocs(ArrayList<RevisionDocument> trainDocs,
			boolean usingNgram, int option) throws Exception {
		WekaAssist wa = new WekaAssist();
		FeatureExtractor fe = new FeatureExtractor();

		ArrayList<String> categories = new ArrayList<String>();
		for (int i = RevisionPurpose.START; i <= RevisionPurpose.END; i++) {
			categories.add(RevisionPurpose.getPurposeName(i));
		}
		fe.buildFeatures(usingNgram, categories);
		Instances data = wa.buildInstances(fe.features, usingNgram);
		for (RevisionDocument doc : trainDocs) {
			ArrayList<RevisionUnit> basicUnits = doc.getRoot()
					.getRevisionUnitAtLevel(0);
			for (RevisionUnit ru : basicUnits) {
				if (modifyOnly) {
					if (ru.getRevision_op() == RevisionOp.MODIFY) {
						Object[] features = fe.extractFeatures(doc, ru,
								usingNgram);
						wa.addInstance(features, fe.features, usingNgram, data,
								transformPurpose3(ru), "dummy");
					}
				} else {
					Object[] features = fe.extractFeatures(doc, ru, usingNgram);
					wa.addInstance(features, fe.features, usingNgram, data,
							transformPurpose3(ru), "dummy");
				}
			}
		}
		Classifier cl = wa.train(data);
		return cl;

	}

	/**
	 * Create the instances of uannotated dataset
	 * 
	 * @param doc
	 * @param usingNgram
	 * @return
	 * @throws IOException
	 */
	public Instances createInstances(ArrayList<RevisionDocument> docs,
			boolean usingNgram) throws IOException {
		FeatureExtractor fe = new FeatureExtractor();
		//fe.openBatchMode(batchPath);
		WekaAssist wa = new WekaAssist();
		ArrayList<String> categories = new ArrayList<String>();
		// categories.add(SURFACE_CHANGE);
		// categories.add(CONTENT_CHANGE);
		addPurposeCategories2(categories);
		fe.buildFeatures(usingNgram, categories);
		Instances data = wa.buildInstances(fe.features, usingNgram);
		for (RevisionDocument doc : docs) {
			ArrayList<ArrayList<ArrayList<Integer>>> pairs = doc
					.getPredictedAlignedIndices();
			for (ArrayList<ArrayList<Integer>> pair : pairs) {
				ArrayList<Integer> oldIndices = pair.get(0);
				ArrayList<Integer> newIndices = pair.get(1);
				Object[] features = fe.extractFeatures(doc, newIndices,
						oldIndices, usingNgram);
				String ID = RevisionPurposePredicter.generateID(doc,
						newIndices, oldIndices);
				wa.addInstance(features, fe.features, usingNgram, data,
						CONTENT_CHANGE, ID);
			}
		}
		return data;
	}

	// Simple classification task, AD for revision purpose classification
	public void classifyADRevisionPurpose(ArrayList<RevisionDocument> docs,
			boolean usingNgram) throws Exception {
		WekaAssist wa = new WekaAssist();
		FeatureExtractor fe = new FeatureExtractor();

		ArrayList<String> categories = new ArrayList<String>();

		categories
				.add(RevisionPurpose
						.getPurposeName(RevisionPurpose.CD_GENERAL_CONTENT_DEVELOPMENT));
		//categories.add(RevisionPurpose
		//		.getPurposeName(RevisionPurpose.CD_REBUTTAL_RESERVATION));
		categories.add(RevisionPurpose
				.getPurposeName(RevisionPurpose.CD_WARRANT_REASONING_BACKING));
		categories.add(RevisionPurpose
				.getPurposeName(RevisionPurpose.CLAIMS_IDEAS));
		categories
				.add(RevisionPurpose.getPurposeName(RevisionPurpose.EVIDENCE));
		categories.add(RevisionPurpose.getPurposeName(RevisionPurpose.SURFACE));
		
		fe.buildFeatures(usingNgram, categories);// do not use ngram

		// First step, build up instances
		Instances data = wa.buildInstances(fe.features, usingNgram); // Using
																		// the
																		// feature
																		// table
																		// to
																		// build
																		// the
																		// structure

		HashSet<String> ids = new HashSet<String>();
		// Collect all the revision units
		// ArrayList<RevisionUnit> rus = new ArrayList<RevisionUnit>();
		for (RevisionDocument doc : docs) {
			ArrayList<RevisionUnit> basicUnits = doc.getRoot()
					.getRevisionUnitAtLevel(0);
			for (RevisionUnit ru : basicUnits) {
				//if (ru.getRevision_op() == RevisionOp.ADD
				//		|| ru.getRevision_op() == RevisionOp.DELETE) {
				String id = doc.getDocumentName()+"_"+ru.getUniqueID();
				if(!ids.contains(id)) {
					ids.add(id);
					Object[] features = fe.extractFeatures(doc, ru, usingNgram);
					int p = ru.getRevision_purpose();
					if(p!=RevisionPurpose.CD_REBUTTAL_RESERVATION) {
					String purposeName = RevisionPurpose.getPurposeName(p);
					if(p==RevisionPurpose.WORDUSAGE_CLARITY_CASCADED||p==RevisionPurpose.WORDUSAGE_CLARITY||p==RevisionPurpose.CONVENTIONS_GRAMMAR_SPELLING||p ==RevisionPurpose.ORGANIZATION)
						purposeName = RevisionPurpose.getPurposeName(RevisionPurpose.SURFACE);
					/*boolean found = false;
					for(int i = 0;i<categories.size();i++) {
						if(categories.get(i).equals(purposeName)) found = true;
					}
					if(found == false) throw new Exception("Error is "+purposeName);*/
					
					wa.addInstance(features, fe.features, usingNgram, data,
							purposeName, "dummy");
					}
				}
				//}
			}
		}
		//wa.saveInstances(data, "C:\\Not Backed Up\\test1.txt");
		if (usingNgram)
			data = wa.addNgram(data);
		
		//wa.saveInstances(data, "C:\\Not Backed Up\\test.txt");
		data = WekaAssist.removeID(data);
		//data = WekaAssist.selectFeatures(data);
		WekaAssist.crossvalidataion(data, 10);
	}

	public void classifyADRevisionPurpose2(ArrayList<RevisionDocument> trainDocs, ArrayList<RevisionDocument> testDocs,boolean usingNgram) throws Exception {
		WekaAssist wa = new WekaAssist();
		FeatureExtractor fe = new FeatureExtractor();

		ArrayList<String> categories = new ArrayList<String>();

		categories
				.add(RevisionPurpose
						.getPurposeName(RevisionPurpose.CD_GENERAL_CONTENT_DEVELOPMENT));
		//categories.add(RevisionPurpose
		//		.getPurposeName(RevisionPurpose.CD_REBUTTAL_RESERVATION));
		categories.add(RevisionPurpose
				.getPurposeName(RevisionPurpose.CD_WARRANT_REASONING_BACKING));
		categories.add(RevisionPurpose
				.getPurposeName(RevisionPurpose.CLAIMS_IDEAS));
		categories
				.add(RevisionPurpose.getPurposeName(RevisionPurpose.EVIDENCE));
		categories.add(RevisionPurpose.getPurposeName(RevisionPurpose.SURFACE));
		
		fe.buildFeatures(usingNgram, categories);// do not use ngram

		// First step, build up instances
		Instances trainData = wa.buildInstances(fe.features, usingNgram); // Using
																		// the
																		// feature
																		// table
																		// to
																		// build
																		// the
																		// structure
		Instances testData = wa.buildInstances(fe.features, usingNgram);
		//HashSet<String> ids = new HashSet<String>();
		// Collect all the revision units
		// ArrayList<RevisionUnit> rus = new ArrayList<RevisionUnit>();
		for (RevisionDocument doc : trainDocs) {
			HashSet<String> ids = new HashSet<String>();
			ArrayList<RevisionUnit> basicUnits = doc.getRoot()
					.getRevisionUnitAtLevel(0);
			for (RevisionUnit ru : basicUnits) {
				//if (ru.getRevision_op() == RevisionOp.ADD
				//		|| ru.getRevision_op() == RevisionOp.DELETE) {
				String id = doc.getDocumentName()+"_"+ru.getUniqueID();
				if(!ids.contains(id)) {
					ids.add(id);
					Object[] features = fe.extractFeatures(doc, ru, usingNgram);
					int p = ru.getRevision_purpose();
					if(p!=RevisionPurpose.CD_REBUTTAL_RESERVATION) {
					String purposeName = RevisionPurpose.getPurposeName(p);
					if(p==RevisionPurpose.WORDUSAGE_CLARITY_CASCADED||p==RevisionPurpose.WORDUSAGE_CLARITY||p==RevisionPurpose.CONVENTIONS_GRAMMAR_SPELLING||p ==RevisionPurpose.ORGANIZATION)
						purposeName = RevisionPurpose.getPurposeName(RevisionPurpose.SURFACE);
					/*boolean found = false;
					for(int i = 0;i<categories.size();i++) {
						if(categories.get(i).equals(purposeName)) found = true;
					}
					if(found == false) throw new Exception("Error is "+purposeName);*/
					
					wa.addInstance(features, fe.features, usingNgram, trainData,
							purposeName, "dummy");
					}
				}
				//}
			}
		}
		for (RevisionDocument doc : testDocs) {
			HashSet<String> ids = new HashSet<String>();
			ArrayList<RevisionUnit> basicUnits = doc.getRoot()
					.getRevisionUnitAtLevel(0);
			for (RevisionUnit ru : basicUnits) {
				//if (ru.getRevision_op() == RevisionOp.ADD
				//		|| ru.getRevision_op() == RevisionOp.DELETE) {
				String id = doc.getDocumentName()+"_"+ru.getUniqueID();
				if(!ids.contains(id)) {
					ids.add(id);
					Object[] features = fe.extractFeatures(doc, ru, usingNgram);
					int p = ru.getRevision_purpose();
					if(p!=RevisionPurpose.CD_REBUTTAL_RESERVATION) {
					String purposeName = RevisionPurpose.getPurposeName(p);
					if(p==RevisionPurpose.WORDUSAGE_CLARITY_CASCADED||p==RevisionPurpose.WORDUSAGE_CLARITY||p==RevisionPurpose.CONVENTIONS_GRAMMAR_SPELLING||p ==RevisionPurpose.ORGANIZATION)
						purposeName = RevisionPurpose.getPurposeName(RevisionPurpose.SURFACE);
					/*boolean found = false;
					for(int i = 0;i<categories.size();i++) {
						if(categories.get(i).equals(purposeName)) found = true;
					}
					if(found == false) throw new Exception("Error is "+purposeName);*/
					
					wa.addInstance(features, fe.features, usingNgram, testData,
							purposeName, "dummy");
					}
				}
				//}
			}
		}
		//wa.saveInstances(data, "C:\\Not Backed Up\\test1.txt");
		if (usingNgram) {
			Instances[] newData = wa.addNgram(trainData, testData);
			trainData = newData[0];
			testData = newData[1];
		}

		//wa.saveInstances(data, "C:\\Not Backed Up\\test.txt");
		//WekaAssist.crossvalidataion(data, 10);
		WekaAssist.crossTrainTest(trainData, testData);
	}
	
	public void addPurposeCategories(ArrayList<String> categories) {
		for (int i = RevisionPurpose.START; i <= RevisionPurpose.END; i++) {
			categories.add(RevisionPurpose.getPurposeName(i));
		}
	}

	public void addPurposeCategories2(ArrayList<String> categories) {
		categories.add(SURFACE_CHANGE);
		categories.add(CONTENT_CHANGE);
	}

	public void addPurposeCategoriesCross(ArrayList<String> categories) {
		for (int i = RevisionOp.START; i <= RevisionOp.END; i++) {
			for (int j = RevisionPurpose.START; j <= RevisionPurpose.END; j++) {
				categories.add(RevisionOp.getOpName(i) + "-"
						+ RevisionPurpose.getPurposeName(j));
			}
		}
	}

	public int transformPurpose(int revPurpose) {
		if (revPurpose == RevisionPurpose.CD_REBUTTAL_RESERVATION
				|| revPurpose == RevisionPurpose.CD_WARRANT_REASONING_BACKING
				|| revPurpose == RevisionPurpose.CD_GENERAL_CONTENT_DEVELOPMENT) {
			return RevisionPurpose.CD_GENERAL_CONTENT_DEVELOPMENT;
		} else {
			return revPurpose;
		}
	}

	public String transformPurpose2(RevisionUnit ru) {
		int revPurpose = ru.getRevision_purpose();
		if (revPurpose == RevisionPurpose.CD_REBUTTAL_RESERVATION
				|| revPurpose == RevisionPurpose.CD_WARRANT_REASONING_BACKING
				|| revPurpose == RevisionPurpose.CD_GENERAL_CONTENT_DEVELOPMENT
				|| revPurpose == RevisionPurpose.CLAIMS_IDEAS
				|| revPurpose == RevisionPurpose.EVIDENCE) {
			return CONTENT_CHANGE;
		} else {
			// return revPurpose;
			return SURFACE_CHANGE;
		}
	}

	public String transformPurposeBinary(RevisionUnit ru, int revOp) {
		int revPurpose = ru.getRevision_purpose();
		if (revPurpose == revOp) {
			return "1";
		} else {
			// return revPurpose;
			return "0";
		}
	}

	public void addPurposeCategoriesBinary(ArrayList<String> categories) {
		categories.add("1");
		categories.add("0");
	}

	public String transformPurpose3(RevisionUnit ru) {
		return RevisionPurpose.getPurposeName(ru.getRevision_purpose());
	}

	public String transformPurposeCross(RevisionUnit ru) {
		return RevisionOp.getOpName(ru.getRevision_op()) + "-"
				+ RevisionPurpose.getPurposeName(ru.getRevision_purpose());
	}

	int ALLOP = 1;
	int MODIFYOPONLY = 2;
	int ADDDELETEONLY = 3;
	int CONTENTONLY = 4;

	/**
	 * To control what revision to use
	 * 
	 * @param revOp
	 * @param option
	 * @return
	 */
	public boolean filterRevision(RevisionUnit ru, int option) {
		int revOp = ru.getRevision_op();
		if (option == ALLOP) {
			return true;
		} else if (option == MODIFYOPONLY) {
			if (revOp == RevisionOp.MODIFY) {
				return true;
			}
		} else if (option == ADDDELETEONLY) {
			if (revOp != RevisionOp.MODIFY) {
				return true;
			}
		} else if (option == CONTENTONLY) {
			int revPurpose = ru.getRevision_purpose();
			if (revPurpose == RevisionPurpose.CD_GENERAL_CONTENT_DEVELOPMENT
					|| revPurpose == RevisionPurpose.CD_WARRANT_REASONING_BACKING
					|| revPurpose == RevisionPurpose.CLAIMS_IDEAS
					|| revPurpose == RevisionPurpose.EVIDENCE) {
				return true;
			}
		}
		return false;
	}

	String SURFACE_CHANGE = "Surface";
	String CONTENT_CHANGE = "Content";

	// Simple classification task, AD for revision purpose classification
	public void classifyADRevisionPurpose(
			ArrayList<RevisionDocument> trainDocs,
			ArrayList<RevisionDocument> testDocs, boolean usingNgram)
			throws Exception {
		WekaAssist wa = new WekaAssist();
		FeatureExtractor fe = new FeatureExtractor();
		fe.openBatchMode(batchPath);
		ArrayList<String> categories = new ArrayList<String>();

		/*
		 * categories .add(RevisionPurpose
		 * .getPurposeName(RevisionPurpose.CD_GENERAL_CONTENT_DEVELOPMENT));
		 * categories.add(RevisionPurpose
		 * .getPurposeName(RevisionPurpose.CD_REBUTTAL_RESERVATION));
		 * categories.add(RevisionPurpose
		 * .getPurposeName(RevisionPurpose.CD_WARRANT_REASONING_BACKING));
		 * categories.add(RevisionPurpose
		 * .getPurposeName(RevisionPurpose.CLAIMS_IDEAS)); categories
		 * .add(RevisionPurpose.getPurposeName(RevisionPurpose.EVIDENCE));
		 */

		// addPurposeCategories2(categories);
		addPurposeCategoriesBinary(categories);
		/*
		 * for (int i = RevisionPurpose.START; i <= RevisionPurpose.END; i++) {
		 * categories.add(RevisionPurpose.getPurposeName(i)); }
		 */

		fe.buildFeatures(usingNgram, categories);// do not use ngram
		System.out.println("Loading data");
		// First step, build up instances
		Instances trainData = wa.buildInstances(fe.features, usingNgram); // Using
																			// the
																			// feature
																			// table
																			// to
																			// build
																			// the
																			// structure

		Instances testData = wa.buildInstances(fe.features, usingNgram);

		int buildOp = ALLOP;
		// Collect all the revision units
		// ArrayList<RevisionUnit> rus = new ArrayList<RevisionUnit>();
		for (RevisionDocument doc : trainDocs) {
			ArrayList<RevisionUnit> basicUnits = doc.getRoot()
					.getRevisionUnitAtLevel(0);
			for (RevisionUnit ru : basicUnits) {
				if (filterRevision(ru, buildOp)) {
					Object[] features = fe.extractFeatures(doc, ru, usingNgram);
					wa.addInstance(
							features,
							fe.features,
							usingNgram,
							trainData,
							transformPurposeBinary(
									ru,
									RevisionPurpose.CONVENTIONS_GRAMMAR_SPELLING),
							"dummy");
					// wa.addInstance(features, fe.features, usingNgram,
					// trainData, transformPurpose2(ru), "dummy");
				}
			}
		}
		System.out.println("Train data loaded");
		for (RevisionDocument doc : testDocs) {
			ArrayList<RevisionUnit> basicUnits = doc.getRoot()
					.getRevisionUnitAtLevel(0);
			for (RevisionUnit ru : basicUnits) {
				if (filterRevision(ru, buildOp)) {
					Object[] features = fe.extractFeatures(doc, ru, usingNgram);
					wa.addInstance(
							features,
							fe.features,
							usingNgram,
							testData,
							transformPurposeBinary(
									ru,
									RevisionPurpose.CONVENTIONS_GRAMMAR_SPELLING),
							"dummy");
					// wa.addInstance(features, fe.features, usingNgram,
					// testData,
					// transformPurpose2(ru), "dummy");
				}
			}
		}
		System.out.println("Test data loaded");
		System.out.println("Data loaded");

		if (usingNgram) {
			System.out.println("Adding ngrams");
			Instances[] inst = wa.addNgram(trainData, testData);
			trainData = inst[0];
			testData = inst[1];
		}
		System.out.println("Starts classification");
		// Temporarily change this to 10 fold
		// trainData.addAll(testData);
		WekaAssist.saveInstances(trainData, "C:\\Not Backed Up\\features.arff");
		// WekaAssist.trainTest(trainData, testData, "performance");
		WekaAssist.crossvalidataion(trainData, 10);
	}

	public Evaluation classifyADAlignRevisionPurpose(
			ArrayList<RevisionDocument> trainDocs,
			ArrayList<RevisionDocument> testDocs, boolean usingNgram, int remove)
			throws Exception {
		WekaAssist wa = new WekaAssist();
		FeatureExtractor fe = new FeatureExtractor();
		fe.openBatchMode("C:\\Not Backed Up\\data\\annotated\\batch");
		ArrayList<String> categories = new ArrayList<String>();

		// addPurposeCategories2(categories);
		addPurposeCategoriesBinary(categories);

		fe.buildFeatures(usingNgram, categories, remove);// do not use ngram
		System.out.println("Loading data");
		// First step, build up instances
		Instances trainData = wa.buildInstances(fe.features, usingNgram); // Using
																			// the
																			// feature
																			// table
																			// to
																			// build
																			// the
																			// structure

		Instances testData = wa.buildInstances(fe.features, usingNgram);

		int buildOp = ALLOP;
		// Collect all the revision units
		// ArrayList<RevisionUnit> rus = new ArrayList<RevisionUnit>();
		for (RevisionDocument doc : trainDocs) {
			ArrayList<RevisionUnit> basicUnits = doc.getRoot()
					.getRevisionUnitAtLevel(0);
			for (RevisionUnit ru : basicUnits) {
				if (filterRevision(ru, buildOp)) {
					Object[] features = fe.extractFeatures(doc, ru, usingNgram,
							remove);
					wa.addInstance(
							features,
							fe.features,
							usingNgram,
							trainData,
							transformPurposeBinary(ru,
									RevisionPurpose.CLAIMS_IDEAS), "dummy");
					// wa.addInstance(features, fe.features, usingNgram,
					// trainData, transformPurpose2(ru), "dummy");
					// System.out.println("Instance added");
				}
			}
		}
		System.out.println("Train data loaded");
		for (RevisionDocument doc : testDocs) {
			ArrayList<RevisionUnit> basicUnits = doc.getPredictedRoot()
					.getUnits();
			for (RevisionUnit ru : basicUnits) {
				if (filterRevision(ru, buildOp)) {
					Object[] features = fe.extractFeatures(doc, ru, usingNgram,
							remove);
					wa.addInstance(
							features,
							fe.features,
							usingNgram,
							testData,
							transformPurposeBinary(ru,
									RevisionPurpose.CLAIMS_IDEAS), "dummy");
					// wa.addInstance(features, fe.features, usingNgram,
					// testData,
					// transformPurpose2(ru), "dummy");
				}
			}
		}
		System.out.println("Test data loaded");
		System.out.println("Data loaded");

		if (usingNgram) {
			System.out.println("Adding ngrams");
			Instances[] inst = wa.addNgram(trainData, testData);
			trainData = inst[0];
			testData = inst[1];
		}
		System.out.println("Starts classification");
		// Temporarily change this to 10 fold
		// trainData.addAll(testData);
		WekaAssist.saveInstances(trainData, "C:\\Not Backed Up\\features.arff");
		if (remove == 5)
			return WekaAssist.majorityTrainTest(trainData, testData);
		return WekaAssist.crossTrainTest(trainData, testData);
		// WekaAssist.crossvalidataion(trainData, 10);
	}

	String batchPath = "batch";

	/**
	 * Classification for a specific revision Purpose, the operation is always
	 * ALLOP
	 * 
	 * @param trainDocs
	 * @param testDocs
	 * @param usingNgram
	 * @param revPurpose
	 * @param remove
	 * @return
	 * @throws Exception
	 */
	public Evaluation classifyADRevisionPurpose(
			ArrayList<RevisionDocument> trainDocs,
			ArrayList<RevisionDocument> testDocs, boolean usingNgram,
			int revPurpose, int remove) throws Exception {
		WekaAssist wa = new WekaAssist();
		FeatureExtractor fe = new FeatureExtractor();
		//fe.openBatchMode(batchPath);
		ArrayList<String> categories = new ArrayList<String>();

		// addPurposeCategories2(categories);
		addPurposeCategoriesBinary(categories);
		/*
		 * for (int i = RevisionPurpose.START; i <= RevisionPurpose.END; i++) {
		 * categories.add(RevisionPurpose.getPurposeName(i)); }
		 */

		fe.buildFeatures(usingNgram, categories, remove);// do not use ngram
		System.out.println("Loading data");
		// First step, build up instances
		Instances trainData = wa.buildInstances(fe.features, usingNgram); // Using
																			// the
																			// feature
																			// table
																			// to
																			// build
																			// the
																			// structure

		Instances testData = wa.buildInstances(fe.features, usingNgram);

		int buildOp = ALLOP;
		// Collect all the revision units
		// ArrayList<RevisionUnit> rus = new ArrayList<RevisionUnit>();
		for (RevisionDocument doc : trainDocs) {
			ArrayList<RevisionUnit> basicUnits = doc.getRoot()
					.getRevisionUnitAtLevel(0);
			for (RevisionUnit ru : basicUnits) {
				if (filterRevision(ru, buildOp)) {
					Object[] features = fe.extractFeatures(doc, ru, usingNgram,
							remove);
					wa.addInstance(features, fe.features, usingNgram,
							trainData, transformPurposeBinary(ru, revPurpose),
							"dummy");
				}
			}
		}
		System.out.println("Train data loaded");
		for (RevisionDocument doc : testDocs) {
			ArrayList<RevisionUnit> basicUnits = doc.getRoot()
					.getRevisionUnitAtLevel(0);
			for (RevisionUnit ru : basicUnits) {
				if (filterRevision(ru, buildOp)) {
					Object[] features = fe.extractFeatures(doc, ru, usingNgram,
							remove);
					wa.addInstance(features, fe.features, usingNgram, testData,
							transformPurposeBinary(ru, revPurpose), "dummy");
				}
			}
		}
		System.out.println("Test data loaded");
		System.out.println("Data loaded");

		if (usingNgram) {
			System.out.println("Adding ngrams");
			Instances[] inst = wa.addNgram(trainData, testData);
			trainData = inst[0];
			testData = inst[1];
		}
		System.out.println("Starts classification");
		// Temporarily change this to 10 fold
		// trainData.addAll(testData);
		WekaAssist.saveInstances(trainData, "tmp.arff");
		if (remove == 5)
			return WekaAssist.majorityTrainTest(trainData, testData);
		return WekaAssist.crossTrainTest(trainData, testData);
		// WekaAssist.crossvalidataion(trainData, 10);
	}


	/**
	 * Surface vs. text
	 * 
	 * @param trainDocs
	 * @param testDocs
	 * @param usingNgram
	 * @param remove
	 * @return
	 * @throws Exception
	 */
	public Evaluation classifyADRevisionPurpose(
			ArrayList<RevisionDocument> trainDocs,
			ArrayList<RevisionDocument> testDocs, boolean usingNgram, int remove)
			throws Exception {
		WekaAssist wa = new WekaAssist();
		FeatureExtractor fe = new FeatureExtractor();
		//fe.openBatchMode(batchPath);
		ArrayList<String> categories = new ArrayList<String>();

		/*
		 * categories .add(RevisionPurpose
		 * .getPurposeName(RevisionPurpose.CD_GENERAL_CONTENT_DEVELOPMENT));
		 * categories.add(RevisionPurpose
		 * .getPurposeName(RevisionPurpose.CD_REBUTTAL_RESERVATION));
		 * categories.add(RevisionPurpose
		 * .getPurposeName(RevisionPurpose.CD_WARRANT_REASONING_BACKING));
		 * categories.add(RevisionPurpose
		 * .getPurposeName(RevisionPurpose.CLAIMS_IDEAS)); categories
		 * .add(RevisionPurpose.getPurposeName(RevisionPurpose.EVIDENCE));
		 */

		addPurposeCategories2(categories);
		//addPurposeCategoriesBinary(categories);
		//addPurposeCategories(categories);
		/*
		 * for (int i = RevisionPurpose.START; i <= RevisionPurpose.END; i++) {
		 * categories.add(RevisionPurpose.getPurposeName(i)); }
		 */

		fe.buildFeatures(usingNgram, categories, remove);// do not use ngram
		System.out.println("Loading data");
		// First step, build up instances
		Instances trainData = wa.buildInstances(fe.features, usingNgram); // Using
																			// the
																			// feature
																			// table
																			// to
																			// build
																			// the
																			// structure

		Instances testData = wa.buildInstances(fe.features, usingNgram);

		//int buildOp = ALLOP;
		int buildOp = MODIFYOPONLY;
		// Collect all the revision units
		// ArrayList<RevisionUnit> rus = new ArrayList<RevisionUnit>();
		for (RevisionDocument doc : trainDocs) {
			ArrayList<RevisionUnit> basicUnits = doc.getRoot()
					.getRevisionUnitAtLevel(0);
			for (RevisionUnit ru : basicUnits) {
				if (filterRevision(ru, buildOp)) {
					Object[] features = fe.extractFeatures(doc, ru, usingNgram,
							remove);
					 /*wa.addInstance(features, fe.features, usingNgram,
					 trainData,
					 transformPurposeBinary(ru,RevisionPurpose.CONVENTIONS_GRAMMAR_SPELLING),
					 ru.getDetailContent(doc));*/
					
					wa.addInstance(features, fe.features, usingNgram,
							trainData, transformPurpose2(ru), ru.getDetailContent(doc));
					// System.out.println("Instance added");
					/*
					wa.addInstance(features, fe.features, usingNgram,
							 trainData,
							 RevisionPurpose.getPurposeName(ru.getRevision_purpose()),
							 ru.getDetailContent(doc));*/
				}
			}
		}
		System.out.println("Train data loaded");
		for (RevisionDocument doc : testDocs) {
			ArrayList<RevisionUnit> basicUnits = doc.getRoot()
					.getRevisionUnitAtLevel(0);
			for (RevisionUnit ru : basicUnits) {
				if (filterRevision(ru, buildOp)) {
					Object[] features = fe.extractFeatures(doc, ru, usingNgram,
							remove);
					// wa.addInstance(features, fe.features, usingNgram,
					// testData,
					// transformPurposeBinary(ru,RevisionPurpose.WORDUSAGE_CLARITY),
					// "dummy");
					/* wa.addInstance(features, fe.features, usingNgram,
							 testData,
							 transformPurposeBinary(ru,RevisionPurpose.CONVENTIONS_GRAMMAR_SPELLING),
							 ru.getDetailContent(doc));*/
					wa.addInstance(features, fe.features, usingNgram, testData,
							transformPurpose2(ru), ru.getDetailContent(doc));
					/*wa.addInstance(features, fe.features, usingNgram,
							 testData,
							 RevisionPurpose.getPurposeName(ru.getRevision_purpose()),
							 ru.getDetailContent(doc));*/
				}
			}
		}
		System.out.println("Test data loaded");
		System.out.println("Data loaded");

		if (usingNgram) {
			System.out.println("Adding ngrams");
			Instances[] inst = wa.addNgram(trainData, testData);
			trainData = inst[0];
			testData = inst[1];
		}
		System.out.println("Starts classification");
		// Temporarily change this to 10 fold
		// trainData.addAll(testData);
		WekaAssist
				.saveInstances(trainData, "features.arff");
		if (remove == 5)
			return WekaAssist.majorityTrainTest(trainData, testData);
		return WekaAssist.crossTrainTest(trainData, testData);
		// WekaAssist.crossvalidataion(trainData, 10);
	}

	// Each single category, classify yes or no, kappa, might try multi-label
	// classification later
	public void classifySingle(ArrayList<RevisionDocument> docs,
			boolean usingNgram, int rev_purpose, WekaAssist wa)
			throws Exception {
		FeatureExtractor fe = new FeatureExtractor();
		ArrayList<String> categories = new ArrayList<String>();
		categories.add("yes");
		categories.add("no");

		fe.buildFeatures(usingNgram, categories);

		// First step, build up instance
		Instances data = wa.buildInstances(fe.features, usingNgram);

		// Collect all the revision units
		for (RevisionDocument doc : docs) {
			ArrayList<RevisionUnit> basicUnits = doc.getRoot()
					.getRevisionUnitAtLevel(0);
			for (RevisionUnit ru : basicUnits) {
				if (ru.getRevision_op() == RevisionOp.ADD
						|| ru.getRevision_op() == RevisionOp.DELETE || 1 == 1) {
					Object[] features = fe.extractFeatures(doc, ru, usingNgram);
					String cat = "no";
					if (ru.getRevision_purpose() == rev_purpose) {
						cat = "yes";
					}
					wa.addInstance(features, fe.features, usingNgram, data,
							cat, "dummy");
				}
			}
		}

		if (usingNgram)
			data = wa.addNgram(data);
		WekaAssist.crossvalidataion(data, 5);
	}

	// Each single category, classify yes or no, kappa, might try multi-label
	// classification later, train and test
	public void classifySingle(ArrayList<RevisionDocument> trainDocs,
			ArrayList<RevisionDocument> testDocs, boolean usingNgram,
			int rev_purpose, WekaAssist wa) throws Exception {
		FeatureExtractor fe = new FeatureExtractor();
		ArrayList<String> categories = new ArrayList<String>();
		categories.add("yes");
		categories.add("no");

		fe.buildFeatures(usingNgram, categories);

		// First step, build up instance
		Instances trainData = wa.buildInstances(fe.features, usingNgram);
		Instances testData = wa.buildInstances(fe.features, usingNgram);

		// Collect all the revision units
		for (RevisionDocument doc : trainDocs) {
			ArrayList<RevisionUnit> basicUnits = doc.getRoot()
					.getRevisionUnitAtLevel(0);
			for (RevisionUnit ru : basicUnits) {
				if (ru.getRevision_op() == RevisionOp.ADD
						|| ru.getRevision_op() == RevisionOp.DELETE || 1 == 1) {
					Object[] features = fe.extractFeatures(doc, ru, usingNgram);
					String cat = "no";
					if (ru.getRevision_purpose() == rev_purpose) {
						cat = "yes";
					}
					wa.addInstance(features, fe.features, usingNgram,
							trainData, cat, "dummy");
				}
			}
		}

		for (RevisionDocument doc : testDocs) {
			ArrayList<RevisionUnit> basicUnits = doc.getRoot()
					.getRevisionUnitAtLevel(0);
			for (RevisionUnit ru : basicUnits) {
				if (ru.getRevision_op() == RevisionOp.ADD
						|| ru.getRevision_op() == RevisionOp.DELETE || 1 == 1) {
					Object[] features = fe.extractFeatures(doc, ru, usingNgram);
					String cat = "no";
					if (ru.getRevision_purpose() == rev_purpose) {
						cat = "yes";
					}
					wa.addInstance(features, fe.features, usingNgram, testData,
							cat, "dummy");
				}
			}
		}

		if (usingNgram) {
			// wa.addNgram(trainData);
			// wa.addNgram(testData);
			Instances[] ins = wa.addNgram(trainData, testData);
			trainData = ins[0];
			testData = ins[1];
		}
		wa.saveInstances(trainData, "C:\\Not Backed Up\\test1.txt");

		WekaAssist.trainTest(trainData, testData, "testResult");
	}

	public static void main(String[] args) throws Exception {
		RevisionDocumentReader reader = new RevisionDocumentReader();
		// ArrayList<RevisionDocument> trainDocs = reader
		// .readDocs("D:\\annotationTool\\annotated\\class3");
		ArrayList<RevisionDocument> trainDocs = reader
				.readDocs("C:\\Not Backed Up\\data\\annotated\\revisedClass3");
		ArrayList<RevisionDocument> testDocs = reader
				.readDocs("C:\\Not Backed Up\\data\\annotated\\revisedClass4");
		
		//ArrayList<RevisionDocument> allDocs = reader.readDocs("C:\\Not Backed Up\\data\\selectedNew");
		// ArrayList<RevisionDocument> testDocs = reader
		// .readDocs("D:\\annotationTool\\annotated\\class4");
		/*
		 * ArrayList<RevisionDocument> testDocs2 = reader
		 * .readDocs("D:\\annotationTool\\annotated\\class2");
		 */
		// testDocs.addAll(testDocs2);
		String tpath = "C:\\Not Backed Up\\data\\trainDataCRFVersion";
		String path2 = "C:\\Not Backed Up\\data\\allNewData\\Fan\\All-jiaoyang";
		ArrayList<RevisionDocument> allDocs = reader.readDocs(path2);
		ArrayList<RevisionDocument> trDocs = reader.readDocs(tpath);
		RevisionPurposeClassifier rpc = new RevisionPurposeClassifier();
		WekaAssist wa = new WekaAssist();
		//rpc.classifyADRevisionPurpose(trainDocs, testDocs, false);
		//rpc.classifyADRevisionPurpose(allDocs, testDocs, false);
		rpc.classifyADRevisionPurpose(trDocs,false); 
		//rpc.classifyADRevisionPurpose2(trDocs, allDocs, true);
		/*
		 * int[] revPurposes = { RevisionPurpose.CD_GENERAL_CONTENT_DEVELOPMENT,
		 * RevisionPurpose.CD_WARRANT_REASONING_BACKING,
		 * RevisionPurpose.CLAIMS_IDEAS, RevisionPurpose.EVIDENCE }; for
		 * (Integer revP : revPurposes) { System.out.println("Rev Purpose:" +
		 * RevisionPurpose.getPurposeName(revP)); rpc.classifySingle(trainDocs,
		 * testDocs, true, revP, wa); }
		 */
	}
}
