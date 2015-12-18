package edu.pitt.cs.revision.purpose;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.StringTokenizer;

import weka.classifiers.Classifier;
import weka.core.Instance;
import weka.core.Instances;
import edu.pitt.cs.revision.machinelearning.WekaAssist;
import edu.pitt.lrdc.cs.revision.evaluate.EvaluateTool;
import edu.pitt.lrdc.cs.revision.evaluate.ResultInfoRow;
import edu.pitt.lrdc.cs.revision.io.RevisionDocumentReader;
import edu.pitt.lrdc.cs.revision.model.RevisionDocument;
import edu.pitt.lrdc.cs.revision.model.RevisionOp;
import edu.pitt.lrdc.cs.revision.model.RevisionPurpose;
import edu.pitt.lrdc.cs.revision.model.RevisionUnit;

/**
 * Labelling the revisions at the basic level Thinking about refining the code
 * later
 * 
 * @author zhangfan
 *
 */

public class RevisionPurposePredicter {
	int SURFACECLASSIFY = 1;
	int ALLCLASSIFY = 2;
	RevisionPurposeClassifier rpc = new RevisionPurposeClassifier();

	public static String buildID(RevisionDocument doc, RevisionUnit ru) {
		return doc.getDocumentName() + "," + ru.getRevision_index();
	}

	public static String[] parseID(String ID) {
		String[] tokens = ID.split(",");
		return tokens;
	}

	String batchPath = "/batch";

	/**
	 * Where the revisions only has one major revision type
	 * 
	 * @param trainDocs
	 * @param testDocs
	 * @param usingNgram
	 * @param option
	 * @throws Exception
	 */
	public void predictRevisionsSolo(Instances trainInstances,
			ArrayList<RevisionDocument> trainDocs,
			ArrayList<RevisionDocument> testDocs, boolean usingNgram, int option)
			throws Exception {
		FeatureExtractor fe = new FeatureExtractor();
		ArrayList<String> categories = new ArrayList<String>();
		WekaAssist wa = new WekaAssist();
		rpc.addPurposeCategories(categories);
		fe.buildFeatures(usingNgram, categories);
		Instances trainData = wa.buildInstances(fe.features, usingNgram);
		for (RevisionDocument doc : trainDocs) {
			ArrayList<RevisionUnit> basicUnits = doc.getRoot()
					.getRevisionUnitAtLevel(0);
			for (RevisionUnit ru : basicUnits) {
				Object[] features = fe.extractFeatures(doc, ru, usingNgram);
				wa.addInstance(
						features,
						fe.features,
						usingNgram,
						trainData,
						RevisionPurpose.getPurposeName(ru.getRevision_purpose()),
						"dummy");
			}
		}
		trainInstances = trainData;
		predictRevisionsSolo(trainInstances, testDocs, usingNgram, option);
	}

	public String getCategory(RevisionUnit ru) {
		int purpose = ru.getRevision_purpose();
		if (purpose == RevisionPurpose.CLAIMS_IDEAS) {
			return RevisionPurpose.getPurposeName(purpose);
		} else if (purpose == RevisionPurpose.CD_WARRANT_REASONING_BACKING) {
			return RevisionPurpose.getPurposeName(purpose);
		} else if (purpose == RevisionPurpose.EVIDENCE) {
			return RevisionPurpose.getPurposeName(RevisionPurpose.EVIDENCE);
		} else if (purpose == RevisionPurpose.CD_GENERAL_CONTENT_DEVELOPMENT) {
			return RevisionPurpose
					.getPurposeName(RevisionPurpose.CD_GENERAL_CONTENT_DEVELOPMENT);
		} else if (purpose == RevisionPurpose.CD_REBUTTAL_RESERVATION) {
			return "";
		} else {
			return RevisionPurpose.getPurposeName(RevisionPurpose.SURFACE);
		}
	}

	public void writeRevision(BufferedWriter writer, RevisionUnit ru, RevisionDocument doc, String realCat, String predictedCat) throws IOException {
		ArrayList<Integer> oldIndices = ru.getOldSentenceIndex();
		ArrayList<Integer> newIndices = ru.getNewSentenceIndex();
		Collections.sort(oldIndices);
		Collections.sort(newIndices);
		int oldIndex = -1;
		if(oldIndices.size()!=0&&oldIndices.get(0)>oldIndex) oldIndex = oldIndices.get(0);
		int newIndex = -1;
		if(newIndices.size()!=0 && newIndices.get(0)>newIndex) newIndex = newIndices.get(0);
		
		writer.write(realCat+"\t");
		writer.write(predictedCat +"\t");
		writer.write(doc.getDocumentName()+"\t");
		writer.write(RevisionOp.getOpName(ru.getRevision_op())+"\t");
		
		int oldParaNo = -1;
		int newParaNo = -1;
		if(oldIndex!=-1) oldParaNo = doc.getParaNoOfOldSentence(oldIndex);
		if(newIndex!=-1) newParaNo = doc.getParaNoOfNewSentence(newIndex);
		writer.write(oldParaNo+"\t");
		writer.write(oldIndex+"\t");
		if(oldParaNo!=-1) {
		writer.write(doc.getFirstOfOldParagraph(oldParaNo)+"\t");
		} else {
			writer.write("-1"+"\t");
		}
		writer.write(newParaNo+"\t");
		writer.write(newIndex+"\t");
		if(newParaNo!=-1) {
		writer.write(doc.getFirstOfNewParagraph(newParaNo)+"\t");
		} else {
			writer.write("-1"+"\t");
		}
		String oldSent = " ";
		if(oldIndex!=-1) oldSent = doc.getOldSentence(oldIndex);
		String newSent = " ";
		if(newIndex!=-1) newSent = doc.getNewSentence(newIndex);
		writer.write(oldSent+"\t");
		writer.write(newSent+"\t\n");
	}
	
	public void outputResult(BufferedWriter writer,
			ArrayList<RevisionDocument> predictedDocs) throws IOException {
		for (RevisionDocument doc : predictedDocs) {
			Hashtable<String, String> tags = new Hashtable<String, String>();
			ArrayList<RevisionUnit> realRevisions = doc.getRoot()
					.getRevisionUnitAtLevel(0);
			ArrayList<RevisionUnit> predictedRevisions = doc.getPredictedRoot()
					.getRevisionUnitAtLevel(0);
			for (RevisionUnit ru : realRevisions) {
				String tag = ru.getUniqueID();
				String category = getCategory(ru);
				tags.put(tag, category);
			}
			for (RevisionUnit ru : predictedRevisions) {
				String tag = ru.getUniqueID();
				String category = getCategory(ru);
				if(tags.containsKey(tag)) {
					String realCategory = tags.get(tag);
					if(!category.equals(realCategory)) {
						writeRevision(writer, ru, doc, realCategory, category);
					}
				}
			}
		}
	}

	/**
	 * Where the revisions only has one major revision type
	 * 
	 * @param trainDocs
	 * @param testDocs
	 * @param usingNgram
	 * @param option
	 * @throws Exception
	 */
	public void predictRevisionsSolo(Instances trainData,
			ArrayList<RevisionDocument> testDocs, boolean usingNgram, int option)
			throws Exception {
		Hashtable<String, RevisionDocument> table = new Hashtable<String, RevisionDocument>();
		for (RevisionDocument doc : testDocs) {
			// RevisionUnit predictedRoot = new RevisionUnit(true);
			// predictedRoot.setRevision_level(3); // Default level to 3
			// doc.setPredictedRoot(predictedRoot);
			table.put(doc.getDocumentName(), doc);
		}
		Instances data = rpc.createInstancesSOLO(testDocs, usingNgram);

		WekaAssist wa = new WekaAssist();
		Instances[] inst = wa.addNgram(trainData, data);
		trainData = inst[0];
		data = inst[1];

		boolean autoBalance = false;
		Classifier cl = wa.train(trainData, autoBalance);
		int ID_index = data.attribute("ID").index();

		Hashtable<String, Integer> revIndexTable = new Hashtable<String, Integer>();

		int dataSize = data.numInstances();
		for (int j = 0; j < dataSize; j++) {
			Instance instance = data.instance(j);
			double category = cl.classifyInstance(instance);
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

			int purpose = (int) category + 1;
			ru.setRevision_purpose(purpose);
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
	 * Where the revisions only has one major revision type
	 * 
	 * @param trainDocs
	 * @param testDocs
	 * @param usingNgram
	 * @param option
	 * @throws Exception
	 */
	public void predictRevisionsSolo(ArrayList<RevisionDocument> trainDocs,
			ArrayList<RevisionDocument> testDocs, boolean usingNgram, int option)
			throws Exception {
		Hashtable<String, RevisionDocument> table = new Hashtable<String, RevisionDocument>();
		FeatureExtractor fe = new FeatureExtractor();
		ArrayList<String> categories = new ArrayList<String>();
		WekaAssist wa = new WekaAssist();
		boolean modifyOnly = false;

		for (RevisionDocument doc : testDocs) {
			// RevisionUnit predictedRoot = new RevisionUnit(true);
			// predictedRoot.setRevision_level(3); // Default level to 3
			// doc.setPredictedRoot(predictedRoot);
			table.put(doc.getDocumentName(), doc);
		}
		// rpc.addPurposeCategories2(categories);
		rpc.addPurposeCategories(categories);
		fe.setOnline(true);
		fe.buildFeatures(usingNgram, categories);
		Instances trainData = wa.buildInstances(fe.features, usingNgram);
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
								trainData, RevisionPurpose.getPurposeName(ru
										.getRevision_purpose()), "dummy");
					}
				} else {
					Object[] features = fe.extractFeatures(doc, ru, usingNgram);
					wa.addInstance(features, fe.features, usingNgram,
							trainData, RevisionPurpose.getPurposeName(ru
									.getRevision_purpose()), "dummy");
				}
			}
		}
		for (RevisionDocument doc : testDocs) {
			ArrayList<RevisionUnit> basicUnits = doc.getPredictedRoot()
					.getUnits();
			for (RevisionUnit ru : basicUnits) {
				/*
				 * if (modifyOnly) { if (ru.getRevision_op() ==
				 * RevisionOp.MODIFY) { Object[] features =
				 * fe.extractFeatures(doc, ru, usingNgram);
				 * wa.addInstance(features, fe.features, usingNgram, testData,
				 * rpc.transformPurpose2(ru), buildID(doc, ru)); } } else {
				 */
				Object[] features = fe.extractFeatures(doc, ru, usingNgram);
				wa.addInstance(
						features,
						fe.features,
						usingNgram,
						testData,
						RevisionPurpose.getPurposeName(ru.getRevision_purpose()),
						buildID(doc, ru));
				// }
			}
		}

		if (usingNgram) {
			System.out.println("Adding ngrams");
			Instances[] inst = wa.addNgram(trainData, testData);
			trainData = inst[0];
			testData = inst[1];
		}

		boolean autoBalance = false;
		Classifier cl = wa.train(trainData, autoBalance);
		int ID_index = testData.attribute("ID").index();
		int testDataSize = testData.numInstances();
		for (int i = 0; i < testDataSize; i++) {
			Instance instance = testData.instance(i);
			double category = cl.classifyInstance(instance);

			// String ID =
			// data.instance(ID_index).stringValue(instance.attribute(ID_index));
			String ID = instance.stringValue(instance.attribute(ID_index));
			String[] info = parseID(ID);
			RevisionDocument doc = table.get(info[0]);
			for (RevisionUnit ru : doc.getPredictedRoot().getUnits()) {
				if (ru.getRevision_index() == Integer.parseInt(info[1])) {
					int purpose = (int) category + 1;
					ru.setRevision_purpose(purpose);
					break;
				}
			}
		}

	}

	
	public void addPurposeClasses5Class(ArrayList<String> categories) {
		categories.add(RevisionPurpose.getPurposeName(RevisionPurpose.CLAIMS_IDEAS));
		categories.add(RevisionPurpose.getPurposeName(RevisionPurpose.CD_WARRANT_REASONING_BACKING));
		categories.add(RevisionPurpose.getPurposeName(RevisionPurpose.EVIDENCE));
		categories.add(RevisionPurpose.getPurposeName(RevisionPurpose.CD_GENERAL_CONTENT_DEVELOPMENT));
		categories.add(RevisionPurpose.getPurposeName(RevisionPurpose.SURFACE));
	}
	
	public String transform5Classes(int purpose) {
		if(purpose == RevisionPurpose.CLAIMS_IDEAS) {
			return RevisionPurpose.getPurposeName(purpose);
		} else if(purpose == RevisionPurpose.CD_WARRANT_REASONING_BACKING) {
			return RevisionPurpose.getPurposeName(purpose);
		} else if(purpose == RevisionPurpose.EVIDENCE) {
			return RevisionPurpose.getPurposeName(purpose);
		} else if(purpose == RevisionPurpose.CD_GENERAL_CONTENT_DEVELOPMENT) {
			return RevisionPurpose.getPurposeName(purpose);
		} else {
			return RevisionPurpose.getPurposeName(RevisionPurpose.SURFACE);
		}
	}
	
	public int transform5ClassesToRevision(double prediction) {
		int predictInt = (int)prediction;
		if(predictInt == 0) {
			return RevisionPurpose.CLAIMS_IDEAS;
		} else if(predictInt == 1) {
			return RevisionPurpose.CD_WARRANT_REASONING_BACKING;
		} else if(predictInt == 2) {
			return RevisionPurpose.EVIDENCE;
		} else if(predictInt == 3) {
			return RevisionPurpose.CD_GENERAL_CONTENT_DEVELOPMENT;
		} else {
			return RevisionPurpose.EVIDENCE;
		}
	}
	
	public void predictRevisionsSolo5Class(ArrayList<RevisionDocument> trainDocs,
			ArrayList<RevisionDocument> testDocs, boolean usingNgram, int option)
			throws Exception {
		Hashtable<String, RevisionDocument> table = new Hashtable<String, RevisionDocument>();
		FeatureExtractor fe = new FeatureExtractor();
		ArrayList<String> categories = new ArrayList<String>();
		WekaAssist wa = new WekaAssist();
		boolean modifyOnly = false;

		for (RevisionDocument doc : testDocs) {
			// RevisionUnit predictedRoot = new RevisionUnit(true);
			// predictedRoot.setRevision_level(3); // Default level to 3
			// doc.setPredictedRoot(predictedRoot);
			table.put(doc.getDocumentName(), doc);
		}
		// rpc.addPurposeCategories2(categories);
		 //rpc.addPurposeCategories(categories);
		addPurposeClasses5Class(categories);
		fe.setOnline(true);
		fe.buildFeatures(usingNgram, categories);
		Instances trainData = wa.buildInstances(fe.features, usingNgram);
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
								trainData, transform5Classes(ru
										.getRevision_purpose()), "dummy");
					}
				} else {
					Object[] features = fe.extractFeatures(doc, ru, usingNgram);
					wa.addInstance(features, fe.features, usingNgram,
							trainData, transform5Classes(ru
									.getRevision_purpose()), "dummy");
				}
			}
		}
		
		Instances data = rpc.createInstancesSOLO5Class(testDocs, usingNgram);
		Instances[] inst = wa.addNgram(trainData, data);
		trainData = inst[0];
		data = inst[1];

		boolean autoBalance = false;
		Classifier cl = wa.train(trainData, autoBalance);
		int ID_index = data.attribute("ID").index();

		Hashtable<String, Integer> revIndexTable = new Hashtable<String, Integer>();

		int dataSize = data.numInstances();
		for (int j = 0; j < dataSize; j++) {
			Instance instance = data.instance(j);
			double category = cl.classifyInstance(instance);
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

			//int purpose = (int) category + 1;
			ru.setRevision_purpose(transform5ClassesToRevision(category));
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
	 * Predict revisions with training and testing data
	 * 
	 * @param trainDocs
	 * @param docs
	 * @param usingNgram
	 * @param option
	 * @throws Exception
	 */
	public void predictRevisions(ArrayList<RevisionDocument> trainDocs,
			ArrayList<RevisionDocument> testDocs, boolean usingNgram, int option)
			throws Exception {
		/*
		 * Classifier cl = rpc.trainDocs(trainDocs,docs, usingNgram, option);//
		 * surface // classify // == 1, // all classify == 2
		 * predictRevisions(cl, docs, usingNgram, option);
		 */
		Hashtable<String, RevisionDocument> table = new Hashtable<String, RevisionDocument>();
		FeatureExtractor fe = new FeatureExtractor();
		fe.openBatchMode(batchPath);
		ArrayList<String> categories = new ArrayList<String>();
		WekaAssist wa = new WekaAssist();
		boolean modifyOnly = true;

		for (RevisionDocument doc : testDocs) {
			// RevisionUnit predictedRoot = new RevisionUnit(true);
			// predictedRoot.setRevision_level(3); // Default level to 3
			// doc.setPredictedRoot(predictedRoot);
			table.put(doc.getDocumentName(), doc);
		}
		rpc.addPurposeCategories2(categories);
		fe.buildFeatures(usingNgram, categories);
		Instances trainData = wa.buildInstances(fe.features, usingNgram);
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
								trainData, rpc.transformPurpose2(ru), "dummy");
					}
				} else {
					Object[] features = fe.extractFeatures(doc, ru, usingNgram);
					wa.addInstance(features, fe.features, usingNgram,
							trainData, rpc.transformPurpose2(ru), "dummy");
				}
			}
		}
		for (RevisionDocument doc : testDocs) {
			ArrayList<RevisionUnit> basicUnits = doc.getPredictedRoot()
					.getUnits();
			for (RevisionUnit ru : basicUnits) {
				/*
				 * if (modifyOnly) { if (ru.getRevision_op() ==
				 * RevisionOp.MODIFY) { Object[] features =
				 * fe.extractFeatures(doc, ru, usingNgram);
				 * wa.addInstance(features, fe.features, usingNgram, testData,
				 * rpc.transformPurpose2(ru), buildID(doc, ru)); } } else {
				 */
				Object[] features = fe.extractFeatures(doc, ru, usingNgram);
				wa.addInstance(features, fe.features, usingNgram, testData,
						rpc.transformPurpose2(ru), buildID(doc, ru));
				// }
			}
		}

		if (usingNgram) {
			System.out.println("Adding ngrams");
			Instances[] inst = wa.addNgram(trainData, testData);
			trainData = inst[0];
			testData = inst[1];
		}
		Classifier cl = wa.train(trainData);
		int ID_index = testData.attribute("ID").index();
		int testDataSize = testData.numInstances();
		for (int i = 0; i < testDataSize; i++) {
			Instance instance = testData.instance(i);
			double prob = cl.classifyInstance(instance);

			// String ID =
			// data.instance(ID_index).stringValue(instance.attribute(ID_index));
			String ID = instance.stringValue(instance.attribute(ID_index));
			String[] info = parseID(ID);
			RevisionDocument doc = table.get(info[0]);
			for (RevisionUnit ru : doc.getPredictedRoot().getUnits()) {
				if (ru.getRevision_index() == Integer.parseInt(info[1])) {
					if (ru.getRevision_op() == RevisionOp.ADD
							|| ru.getRevision_op() == RevisionOp.DELETE) {
						ru.setRevision_purpose(RevisionPurpose.CLAIMS_IDEAS);
					} else {
						if (prob < 0.5) {
							// surface
							ru.setRevision_purpose(RevisionPurpose.CONVENTIONS_GRAMMAR_SPELLING);
						} else {
							ru.setRevision_purpose(RevisionPurpose.CLAIMS_IDEAS);
						}
					}
					break;
				}
			}
		}
	}

	/**
	 * @deprecated Predict revisions with trained classifier Note: This is only
	 *             for Surface/Content Classification
	 * 
	 * @param cl
	 * @param docs
	 * @param usingNgram
	 * @param option
	 * @throws Exception
	 */
	public void predictRevisions(Classifier cl,
			ArrayList<RevisionDocument> docs, boolean usingNgram, int option)
			throws Exception {
		Hashtable<String, RevisionDocument> table = new Hashtable<String, RevisionDocument>();
		for (RevisionDocument doc : docs) {
			// RevisionUnit predictedRoot = new RevisionUnit(true);
			// predictedRoot.setRevision_level(3); // Default level to 3
			// doc.setPredictedRoot(predictedRoot);
			table.put(doc.getDocumentName(), doc);
		}

		Instances data = rpc.createInstances(docs, usingNgram);
		int ID_index = data.attribute("ID").index();

		Hashtable<String, Integer> revIndexTable = new Hashtable<String, Integer>();
		if (option == ALLCLASSIFY) {

		} else {
			int dataSize = data.numInstances();
			for (int i = 0; i < dataSize; i++) {
				Instance instance = data.instance(i);
				double prob = cl.classifyInstance(instance);
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

				/*
				 * Right now we only select two categories to represent the two
				 * general categories
				 */

				if (option == SURFACECLASSIFY) {
					if (ru.getRevision_op() == RevisionOp.ADD
							|| ru.getRevision_op() == RevisionOp.DELETE) {
						ru.setRevision_purpose(RevisionPurpose.CLAIMS_IDEAS);
					} else {
						if (prob < 0.5) {
							// surface
							ru.setRevision_purpose(RevisionPurpose.CONVENTIONS_GRAMMAR_SPELLING);
						} else {
							ru.setRevision_purpose(RevisionPurpose.CLAIMS_IDEAS);
						}
					}
				}

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

	}

	/**
	 * This one is for multiple purposes Uses trained classifiers
	 * 
	 * @param cls
	 * @param docs
	 * @throws Exception
	 */
	public void predictRevisionsNoTrain(Hashtable<Integer, Classifier> cls,
			ArrayList<RevisionDocument> docs) throws Exception {
		Hashtable<String, Integer> revIndexTable = new Hashtable<String, Integer>();
		Hashtable<String, Integer> unAssignedRev = new Hashtable<String, Integer>();
		Hashtable<String, Double> unAssignedRevDouble = new Hashtable<String, Double>();

		Hashtable<String, RevisionDocument> table = new Hashtable<String, RevisionDocument>();
		for (RevisionDocument doc : docs) {
			// RevisionUnit predictedRoot = new RevisionUnit(true);
			// predictedRoot.setRevision_level(3); // Default level to 3
			// doc.setPredictedRoot(predictedRoot);
			table.put(doc.getDocumentName(), doc);
		}

		for (int i = RevisionPurpose.START; i <= RevisionPurpose.END; i++) {
			Classifier cl = cls.get(i);

			Instances data = rpc.createInstances(docs, false);
			int ID_index = data.attribute("ID").index();
			int dataSize = data.numInstances();
			for (int j = 0; j < dataSize; j++) {
				Instance instance = data.instance(j);
				double prob = cl.classifyInstance(instance);
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

				if (prob < 0.5) { // actually prob is just a nominal value(:L)
					ru.setRevision_purpose(i);
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
					if (unAssignedRev.containsKey(ID)) { // if found, then no
															// need to worry
						unAssignedRev.put(ID, 100);
						unAssignedRevDouble.put(ID, 100.0);
					}
				} else {
					// there could be the case where no classifier recognize it,
					// in that case, we need to select the most likely one
					double realProb = cl.distributionForInstance(instance)[0];
					if (!unAssignedRev.containsKey(ID)
							|| (realProb > unAssignedRevDouble.get(ID))) {
						unAssignedRev.put(ID, i);
						unAssignedRevDouble.put(ID, realProb);
					}
				}

			}
		}

		Iterator<String> it = unAssignedRev.keySet().iterator();
		while (it.hasNext()) {
			String key = it.next();
			double prob = unAssignedRevDouble.get(key);
			if (prob < 1) { // The revision has not been added before
				AlignStruct as = AlignStruct.parseID(key);
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

				ru.setRevision_purpose(unAssignedRev.get(key));
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
	}

	/**
	 * This is for fine grained classification
	 * 
	 * using trained instances
	 * 
	 * @param trainDataTable
	 * @param docs
	 * @throws Exception
	 */
	public void predictRevisions(Hashtable<Integer, Instances> trainDataTable,
			ArrayList<RevisionDocument> docs) throws Exception {
		Hashtable<String, Integer> revIndexTable = new Hashtable<String, Integer>();
		Hashtable<String, Integer> unAssignedRev = new Hashtable<String, Integer>();
		Hashtable<String, Double> unAssignedRevDouble = new Hashtable<String, Double>();

		Hashtable<String, RevisionDocument> table = new Hashtable<String, RevisionDocument>();
		for (RevisionDocument doc : docs) {
			// RevisionUnit predictedRoot = new RevisionUnit(true);
			// predictedRoot.setRevision_level(3); // Default level to 3
			// doc.setPredictedRoot(predictedRoot);
			table.put(doc.getDocumentName(), doc);
		}

		for (int i = RevisionPurpose.START; i <= RevisionPurpose.END; i++) {

			Instances data = rpc.createInstances(docs, true);

			WekaAssist wa = new WekaAssist();
			Instances trainData = trainDataTable.get(i);
			Instances[] inst = wa.addNgram(trainData, data);
			trainData = inst[0];
			data = inst[1];

			Classifier cl = wa.train(trainData);
			int ID_index = data.attribute("ID").index();

			int dataSize = data.numInstances();
			for (int j = 0; j < dataSize; j++) {
				Instance instance = data.instance(j);
				double prob = cl.classifyInstance(instance); // *&*&* well, this
																// actually is
																// not the prob,
																// is the
																// category
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

				if (prob < 0.5) { // actually prob is just a nominal value(:L),
									// 0 is YES, 1 is NO
					ru.setRevision_purpose(i);
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

					if (unAssignedRev.containsKey(ID)) { // if found, then no
															// need to worry
						unAssignedRev.put(ID, 100);
						unAssignedRevDouble.put(ID, 100.0);
					}
				} else {
					// there could be the case where no classifier recognize it,
					// in that case, we need to select the most likely one
					double realProb = cl.distributionForInstance(instance)[0];
					if (!unAssignedRev.containsKey(ID)
							|| (realProb > unAssignedRevDouble.get(ID))) {
						unAssignedRev.put(ID, i);
						unAssignedRevDouble.put(ID, realProb);
					}
				}
			}
		}

		Iterator<String> it = unAssignedRev.keySet().iterator();
		while (it.hasNext()) {
			String key = it.next();
			double prob = unAssignedRevDouble.get(key);
			if (prob < 1) { // The revision has not been added before
				AlignStruct as = AlignStruct.parseID(key);
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

				ru.setRevision_purpose(unAssignedRev.get(key));
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
	}

	/**
	 * For binary classification of surface vs. text-based Using instances
	 * 
	 * @param trainData
	 * @param docs
	 * @throws Exception
	 */
	public void predictRevisions(Instances trainData,
			ArrayList<RevisionDocument> docs) throws Exception {
		Hashtable<String, RevisionDocument> table = new Hashtable<String, RevisionDocument>();
		for (RevisionDocument doc : docs) {
			// RevisionUnit predictedRoot = new RevisionUnit(true);
			// predictedRoot.setRevision_level(3); // Default level to 3
			// doc.setPredictedRoot(predictedRoot);
			table.put(doc.getDocumentName(), doc);
		}
		Instances data = rpc.createInstances(docs, true);

		WekaAssist wa = new WekaAssist();
		Instances[] inst = wa.addNgram(trainData, data);
		trainData = inst[0];
		data = inst[1];

		Classifier cl = wa.train(trainData);
		int ID_index = data.attribute("ID").index();

		Hashtable<String, Integer> revIndexTable = new Hashtable<String, Integer>();

		int dataSize = data.numInstances();
		for (int j = 0; j < dataSize; j++) {
			Instance instance = data.instance(j);
			double prob = cl.classifyInstance(instance);
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

			if (ru.getRevision_op() == RevisionOp.ADD
					|| ru.getRevision_op() == RevisionOp.DELETE) {
				ru.setRevision_purpose(RevisionPurpose.CLAIMS_IDEAS);
			} else {
				if (prob < 0.5) {
					// surface
					ru.setRevision_purpose(RevisionPurpose.CONVENTIONS_GRAMMAR_SPELLING);
				} else {
					ru.setRevision_purpose(RevisionPurpose.CLAIMS_IDEAS);
				}
			}
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
	 * For binary classification of surface vs. text-based The trained
	 * classifier is used
	 * 
	 * @param cl
	 * @param docs
	 * @throws Exception
	 */
	public void predictRevisions(Classifier cl, ArrayList<RevisionDocument> docs)
			throws Exception {
		Hashtable<String, RevisionDocument> table = new Hashtable<String, RevisionDocument>();
		for (RevisionDocument doc : docs) {
			// RevisionUnit predictedRoot = new RevisionUnit(true);
			// predictedRoot.setRevision_level(3); // Default level to 3
			// doc.setPredictedRoot(predictedRoot);
			table.put(doc.getDocumentName(), doc);
		}

		Instances data = rpc.createInstances(docs, false);
		int ID_index = data.attribute("ID").index();

		Hashtable<String, Integer> revIndexTable = new Hashtable<String, Integer>();

		int dataSize = data.numInstances();
		for (int j = 0; j < dataSize; j++) {
			Instance instance = data.instance(j);
			double prob = cl.classifyInstance(instance);
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

			if (ru.getRevision_op() == RevisionOp.ADD
					|| ru.getRevision_op() == RevisionOp.DELETE) {
				ru.setRevision_purpose(RevisionPurpose.CLAIMS_IDEAS);
			} else {
				if (prob < 0.5) {
					// surface
					ru.setRevision_purpose(RevisionPurpose.CONVENTIONS_GRAMMAR_SPELLING);
				} else {
					ru.setRevision_purpose(RevisionPurpose.CLAIMS_IDEAS);
				}
			}
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

	public static String generateID(RevisionDocument doc,
			ArrayList<Integer> newIndices, ArrayList<Integer> oldIndices) {
		String newIndiceStr = "_";
		String oldIndiceStr = "_";
		for (Integer newIndex : newIndices) {
			newIndiceStr += newIndex + "_";
		}
		for (Integer oldIndex : oldIndices) {
			oldIndiceStr += oldIndex + "_";
		}
		String ID = doc.getDocumentName() + "|" + newIndiceStr + "|"
				+ oldIndiceStr;
		return ID;
	}

	public static void main(String[] args) throws Exception {
		String t1 = "C:\\Not Backed Up\\test\\alok\\data_processed\\Annotation_juan-orange-87.txt.xlsx|46_|";
		String t2 = "C:\\Not Backed Up\\test\\alok\\data_processed\\Annotation_julian-purple-31.txt.xlsx||16_";
		RevisionPurposePredicter rpp = new RevisionPurposePredicter();
		/*AlignStruct as = AlignStruct.parseID(t1);
		System.out.println("NEW:" + as.newIndices);
		System.out.println("OLD:" + as.oldIndices);
		AlignStruct as2 = AlignStruct.parseID(t2);
		System.out.println("NEW:" + as2.newIndices);
		System.out.println("OLD:" + as2.oldIndices);*/
		String folderPath = "C:\\Not Backed Up\\data\\naaclData\\C1";
		ArrayList<RevisionDocument> docs = RevisionDocumentReader.readDocs(folderPath);
		int folder = 10;
		ArrayList<ArrayList<ArrayList<RevisionDocument>>> crossCuts = EvaluateTool
				.getCrossCut(docs, folder);
		for (int j = 0; j < folder; j++) { // Do a cross validation
			ArrayList<RevisionDocument> trainDocs = crossCuts.get(j).get(0);
			ArrayList<RevisionDocument> testDocs = crossCuts.get(j).get(1);
			rpp.predictRevisionsSolo5Class(trainDocs, testDocs, true, -1);
		}
		String output = "C:\\Not Backed Up\\allResults\\C1Results.txt";
		BufferedWriter writer = new BufferedWriter(new FileWriter(output));
		writer.write("REAL"+"\t");
		writer.write("PREDICTED" +"\t");
		writer.write("DOCNAME"+"\t");
		writer.write("REVOP"+"\t");
		writer.write("OLDPARANO"+"\t");
		writer.write("OLDINDEX"+"\t");
		writer.write("PARAOLDFIRST"+"\t");
		writer.write("NEWPARANO"+"\t");
		writer.write("NEWINDEX"+"\t");
		writer.write("PARANEWFIRST"+"\t");
		writer.write("OLDSENT"+"\t");
		writer.write("NEWSENT"+"\t\n");
		rpp.outputResult(writer, docs);
		writer.close();
	}
}
