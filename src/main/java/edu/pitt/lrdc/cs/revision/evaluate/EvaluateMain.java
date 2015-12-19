package edu.pitt.lrdc.cs.revision.evaluate;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;

import weka.classifiers.Evaluation;
import weka.core.Instances;
import edu.pitt.cs.revision.purpose.RevisionPurposeClassifier;
import edu.pitt.cs.revision.purpose.RevisionPurposePredicter;
import edu.pitt.cs.revision.purpose.RevisionPurposeTagger;
import edu.pitt.lrdc.cs.revision.alignment.Aligner;
import edu.pitt.lrdc.cs.revision.io.LatexTableWriter;
import edu.pitt.lrdc.cs.revision.io.PredictedRevisionStat;
import edu.pitt.lrdc.cs.revision.io.ResultInfoWriter;
import edu.pitt.lrdc.cs.revision.io.RevisionDocumentReader;
import edu.pitt.lrdc.cs.revision.model.RevisionDocument;
import edu.pitt.lrdc.cs.revision.model.RevisionPurpose;
import edu.pitt.lrdc.cs.revision.model.RevisionUnit;

/**
 * The main function for all the evaluations
 * 
 * In later version, modify to allow the tester to input args to do the specific
 * evaluation
 * 
 * @author zhangfan
 * @version 1.0
 */
public class EvaluateMain {
	static int ALIGN = 1;
	static int SURFACECLASSIFY = 2;
	static int ALIGNCLASSIFY = 3;
	static int CLASSIFY = 4;

	static int TRAINTEST = 1;
	static int CROSSVALIDATION = 2;

	public static void main(String[] args) throws Exception {
		int option = CLASSIFY; // modify later to allow human input
		int evaluateMethod = 4;
		// String trainPath = "D:/annotationTool/annotated/class3";
		// String trainPath = "/Users/faz23/Desktop/34/annotated/allData";
		// String trainPath = "C:\\Not Backed Up\\data\\trainData2";
		String trainPath = "C:\\Not Backed Up\\data\\allNewData\\Fan\\temp_alldata";
		trainPath = "C:\\Not Backed Up\\data\\naaclData\\C1";
		// String testPath = "D:/annotationTool/annotated/class4";
		// String testPath = "/Users/faz23/Desktop/34/annotated/allData2";
		String testPath = "C:\\Not Backed Up\\data\\trainData";
		testPath = "C:\\Not Backed Up\\data\\naaclData\\C2";
		String clausePath = "C:\\Not Backed Up\\data_phrase_science\\BarnettPhraseAlign";
		// String anotherPath = "D:/annotationTool/annotated/class2";
		ArrayList<RevisionDocument> trainFolder = RevisionDocumentReader
				.readDocs(trainPath);
		ArrayList<RevisionDocument> testFolder = RevisionDocumentReader
				.readDocs(testPath);
		// ArrayList<RevisionDocument> clauseFolder = RevisionDocumentReader
		// .readDocs(clausePath);
		// ArrayList<RevisionDocument> anotherFolder =
		// RevisionDocumentReader.readDocs(anotherPath);

		ArrayList<RevisionDocument> allData = new ArrayList<RevisionDocument>();
		// allData.addAll(trainFolder);
		// allData.addAll(testFolder);
		allData.addAll(trainFolder);
		String resultPath = "dummy";
		if (option == ALIGN) {
			evaluateMethod = 2;// modify later to allow human input
			System.out.println("Baseline");
			crossValidateAlign(allData, 9, 1);
			if (evaluateMethod == TRAINTEST) {

			} else {
				int folder = 10;// modify later to allow human input
				crossValidateAlign(allData, folder, 2);
			}
		} else if (option == SURFACECLASSIFY) {
			int folder = 10;
			resultPath = "C:\\Not Backed Up\\surface.xlsx";
			crossValidateClassify(allData, folder, true, resultPath);
		} else if (option == ALIGNCLASSIFY) {
			int folder = 10;
			crossValidateAlignClassify(allData, folder, 2);
		} else if (option == CLASSIFY) {
			int folder = 10;
			// Open this for cross surface classification
			// resultPath = "C:\\Not Backed Up\\data\\surfaceAllOp.xlsx";
			resultPath = "C:\\Not Backed Up\\allResults";
			// crossValidateClassify(allData, folder, true, resultPath);
			boolean autoAligned = false;
			boolean highLevel = false;
			// crossValidateClassifyCorrelation(allData, folder, 1, autoAligned,
			// highLevel);
			// Open this for jumbo classification
			// resultPath = "/Users/faz23/Desktop/34/annotated/allResults2";
			crossValidateClassifyJumbo2(allData, folder, true, resultPath);
			// trainTestClassifyJumbo2(testFolder, trainFolder, true,
			// resultPath);
		}
	}

	public static void crossValidateAlign(ArrayList<RevisionDocument> docs,
			int folder, int distOption) throws Exception {
		ArrayList<ArrayList<ArrayList<RevisionDocument>>> crossCuts = EvaluateTool
				.getCrossCut(docs, folder);
		for (int i = 0; i < folder; i++) {
			ArrayList<RevisionDocument> trainDocs = crossCuts.get(i).get(0);
			ArrayList<RevisionDocument> testDocs = crossCuts.get(i).get(1);
			Aligner aligner = new Aligner();
			aligner.align(trainDocs, testDocs, distOption);
		}
		System.out.println("ACCURACY:"
				+ AlignmentEvaluator.getAlignmentAccuracyAvg(docs));
		System.out.println("KAPPA:"
				+ AlignmentEvaluator.getAlignmentKappaAvg(docs));
	}

	public static void allAddRow(Hashtable<String, LatexTableWriter> writers,
			String row) {
		Iterator<String> it = writers.keySet().iterator();
		while (it.hasNext()) {
			String key = it.next();
			LatexTableWriter writer = writers.get(key);
			writer.addRow(row);
		}
	}

	public static void allAddRow(Hashtable<String, LatexTableWriter> writers,
			ArrayList<String> rows) {
		for (String row : rows)
			allAddRow(writers, row);
	}

	public static void allPrint(Hashtable<String, LatexTableWriter> writers,
			String resultPath) throws IOException {
		Iterator<String> it = writers.keySet().iterator();
		while (it.hasNext()) {
			String key = it.next();
			LatexTableWriter writer = writers.get(key);
			writer.setOutputPath(resultPath);
			writer.print();
		}
	}

	public static void allAddColumn(
			Hashtable<String, LatexTableWriter> writers, String column) {
		Iterator<String> it = writers.keySet().iterator();
		while (it.hasNext()) {
			String key = it.next();
			LatexTableWriter writer = writers.get(key);
			writer.addColumn(column);
		}
	}

	public static void allAddColumn(
			Hashtable<String, LatexTableWriter> writers,
			ArrayList<String> columns) {
		for (String column : columns)
			allAddColumn(writers, column);
	}

	public static void allMakeTable(Hashtable<String, LatexTableWriter> writers) {
		Iterator<String> it = writers.keySet().iterator();
		while (it.hasNext()) {
			String key = it.next();
			LatexTableWriter writer = writers.get(key);
			writer.makeTable();
		}
	}

	/**
	 * All the revision purposes in a jumbo
	 * 
	 * @param docs
	 * @param folder
	 * @param usingNgram
	 * @param resultPath
	 * @throws Exception
	 */
	public static void crossValidateClassifyJumbo2(
			ArrayList<RevisionDocument> docs, int folder, boolean usingNgram,
			String resultPath) throws Exception {
		ArrayList<ArrayList<ArrayList<RevisionDocument>>> crossCuts = EvaluateTool
				.getCrossCut(docs, folder);
		ArrayList<ArrayList<ResultInfoRow>> allResults = new ArrayList<ArrayList<ResultInfoRow>>();

		/*
		 * Hashtable<String, LatexTableWriter> writers = new Hashtable<String,
		 * LatexTableWriter>(); Field[] fields =
		 * ResultInfo.class.getDeclaredFields(); for (Field field : fields) {
		 * String name = field.getName(); LatexTableWriter latexWriter = new
		 * LatexTableWriter(name); writers.put(name, latexWriter); }
		 */

		// Generate a result for each individual purpose
		/*
		 * for (int i = RevisionPurpose.START; i <= RevisionPurpose.END; i++) {
		 * if (i == RevisionPurpose.CD_REBUTTAL_RESERVATION) continue; String
		 * purposeName = RevisionPurpose.getPurposeName(i);
		 * allAddColumn(writers, purposeName); }
		 */

		ArrayList<String> experiments = new ArrayList<String>();
		ArrayList<Integer> options = new ArrayList<Integer>();
		Hashtable<String, ArrayList<ConfusionMatrix>> cmsTable = new Hashtable<String, ArrayList<ConfusionMatrix>>();
		Hashtable<String, ArrayList<ConfusionMatrix>> cmsParaTable = new Hashtable<String, ArrayList<ConfusionMatrix>>();

		experiments.add("Majority");
		options.add(100);
		experiments.add("Unigram");
		options.add(-1);
		experiments.add("All features-OLD");
		options.add(11);

		experiments.add("PDTB+OLD");
		options.add(2);
		// experiments.add("Language features");
		// options.add(4);

		experiments.add("Embedding features");
		options.add(3);
		// experiments.add("Textual+unigram"); options.add(1);
		
		
		experiments.add("All features");
		options.add(10);
		// experiments.add("Location+unigram"); options.add(0);

		// allAddRow(writers, experiments);
		// allMakeTable(writers);

		ArrayList<ResultInfoRow> resultsDT = new ArrayList<ResultInfoRow>();
		ArrayList<ResultInfoRow> resultsRF = new ArrayList<ResultInfoRow>();
		ArrayList<ResultInfoRow> resultsSVM = new ArrayList<ResultInfoRow>();

		double[][] confusionMatrixDT = new double[5][5];
		double[][] confusionMatrixRF = new double[5][5];
		double[][] confusionMatrixSVM = new double[5][5];

		for (String experiment : experiments) {
			if (!experiment.equals("Majority") && !experiment.equals("Unigram")) {
				ArrayList<ConfusionMatrix> cms = new ArrayList<ConfusionMatrix>();
				ArrayList<ConfusionMatrix> cmsPara = new ArrayList<ConfusionMatrix>();
				cmsTable.put(experiment, cms);
				cmsParaTable.put(experiment, cmsPara);
			}
		}
		for (int j = 0; j < folder; j++) { // Do a cross validation
			ResultInfoRow resultRow;
			ArrayList<RevisionDocument> trainDocs = crossCuts.get(j).get(0);
			ArrayList<RevisionDocument> testDocs = crossCuts.get(j).get(1);

			RevisionPurposeClassifier rpc = new RevisionPurposeClassifier();
			String[] classifiers = { "DT", "SVM", "RF" };
			// String[] classifiers = { "SVM" };
			for (String classifier : classifiers) {
				resultRow = new ResultInfoRow();
				for (int k = 0; k < experiments.size(); k++) { // Try a group of
																// features
					String experiment = experiments.get(k);
					System.out.println("**********PROCESSSING EXPERIMENT: "+experiment+" ***********");
					Evaluation eval = rpc.classifyADRevisionPurposeSolo(
							trainDocs, testDocs, usingNgram, options.get(k),
							classifier);
					resultRow.addExperiment(experiment);

					if (experiment.equals("All features-OLD")
							|| experiment.equals("All features")) {
						double[][] cm = eval.confusionMatrix();
						for (int ii = 0; ii < cm.length; ii++) {
							for (int jj = 0; jj < cm.length; jj++) {
								if (classifier.equals("DT")) {
									confusionMatrixDT[ii][jj] += cm[ii][jj];
								} else if (classifier.equals("RF")) {
									confusionMatrixRF[ii][jj] += cm[ii][jj];
								} else if (classifier.equals("SVM")) {
									confusionMatrixSVM[ii][jj] += cm[ii][jj];
								}
							}
						}
					}
					resultRow.getResult(experiment).fromEvaluation(eval, 5);

					if (cmsTable.containsKey(experiment)) {
						RevisionPurposeTagger.getInstance()
								.setTagLevelParagraph(false);
						Instances[] instances = RevisionPurposeTagger
								.getInstance().prepareForLabelling(trainDocs,
										testDocs, usingNgram, options.get(k));
						String trainPath = "C:\\Not Backed Up\\trainCrf.txt";
						String testPath = "C:\\Not Backed Up\\testCrf.txt";
						String modelPath = "C:\\Not Backed Up\\crf.model";
						String testPath2 = "C:\\Not Backed Up\\testPredictCrf.txt";
						RevisionPurposeTagger.getInstance()
								.transformToTxtForCRFTrain(instances[0],
										trainDocs, trainPath);
						RevisionPurposeTagger.getInstance()
								.transformToTxtForCRF(instances[1], testDocs,
										testPath);
						RevisionPurposeTagger.getInstance().trainAndTag(
								trainPath, modelPath, testPath, testPath2);
						RevisionPurposeTagger.getInstance().readResultToDocs(
								testDocs, testPath2);
						cmsTable.get(experiment)
								.add(PurposeEvaluator
										.getConfusionMatrixOneSurface(testDocs));
						for (RevisionDocument testDoc : testDocs) {
							ArrayList<RevisionUnit> predictedUnits = testDoc
									.getPredictedRoot().getRevisionUnitAtLevel(
											0);
							for (RevisionUnit unit : predictedUnits) {
								unit.setAbandoned();
							}
						}

						RevisionPurposeTagger.getInstance()
								.setTagLevelParagraph(true);
						Instances[] instancesPara = RevisionPurposeTagger
								.getInstance().prepareForLabelling(trainDocs,
										testDocs, usingNgram, options.get(k));
						String trainPathPara = "C:\\Not Backed Up\\trainCrfPara.txt";
						String testPathPara = "C:\\Not Backed Up\\testCrfPara.txt";
						String modelPathPara = "C:\\Not Backed Up\\crfPara.model";
						String testPath2Para = "C:\\Not Backed Up\\testPredictCrf.txt";
						RevisionPurposeTagger.getInstance()
								.transformToTxtForCRFTrain(instancesPara[0],
										trainDocs, trainPathPara);
						RevisionPurposeTagger.getInstance()
								.transformToTxtForCRF(instancesPara[1],
										testDocs, testPathPara);
						RevisionPurposeTagger.getInstance().trainAndTag(
								trainPathPara, modelPathPara, testPathPara,
								testPath2Para);
						RevisionPurposeTagger.getInstance().readResultToDocs(
								testDocs, testPath2Para);
						cmsParaTable
								.get(experiment)
								.add(PurposeEvaluator
										.getConfusionMatrixOneSurface(testDocs));
						for (RevisionDocument testDoc : testDocs) {
							ArrayList<RevisionUnit> predictedUnits = testDoc
									.getPredictedRoot().getRevisionUnitAtLevel(
											0);
							for (RevisionUnit unit : predictedUnits) {
								unit.setAbandoned();
							}
						}

					}

				}
				if (classifier.equals("DT")) {
					resultsDT.add(resultRow);
				} else if (classifier.equals("RF")) {
					resultsRF.add(resultRow);
				} else if (classifier.equals("SVM")) {
					resultsSVM.add(resultRow);
				}
			}

		}

		Iterator<String> it = cmsTable.keySet().iterator();
		while (it.hasNext()) {
			String experiment = it.next();
			System.out
					.println("+++++++++Experiment:" + experiment + "++++++++");
			System.out.println("Essay");
			EvaluateTool.printEvaluation(cmsTable.get(experiment));
			System.out.println("Paragraph");
			EvaluateTool.printEvaluation(cmsParaTable.get(experiment));
		}
		ResultInfoWriter.persist(resultsDT, null, "DT-Groups", resultPath);
		ResultInfoWriter.persist(resultsRF, null, "RF-Groups", resultPath);
		ResultInfoWriter.persist(resultsSVM, null, "SVM-Groups", resultPath);

		// Generate a result for surface vs. text-based

		// printCM("DT", confusionMatrixDT);
		printCM("SVM", confusionMatrixSVM);
		// printCM("RF", confusionMatrixRF);
	}

	public static void trainTestClassifyJumbo2(
			ArrayList<RevisionDocument> trainDocs,
			ArrayList<RevisionDocument> testDocs, boolean usingNgram,
			String resultPath) throws Exception {

		ArrayList<ArrayList<ResultInfoRow>> allResults = new ArrayList<ArrayList<ResultInfoRow>>();

		/*
		 * Hashtable<String, LatexTableWriter> writers = new Hashtable<String,
		 * LatexTableWriter>(); Field[] fields =
		 * ResultInfo.class.getDeclaredFields(); for (Field field : fields) {
		 * String name = field.getName(); LatexTableWriter latexWriter = new
		 * LatexTableWriter(name); writers.put(name, latexWriter); }
		 */

		// Generate a result for each individual purpose
		/*
		 * for (int i = RevisionPurpose.START; i <= RevisionPurpose.END; i++) {
		 * if (i == RevisionPurpose.CD_REBUTTAL_RESERVATION) continue; String
		 * purposeName = RevisionPurpose.getPurposeName(i);
		 * allAddColumn(writers, purposeName); }
		 */

		ArrayList<String> experiments = new ArrayList<String>();
		ArrayList<Integer> options = new ArrayList<Integer>();
		experiments.add("Majority");
		options.add(100);
		experiments.add("Unigram");
		options.add(-1);
		experiments.add("All features-OLD");
		options.add(11);
		// experiments.add("All features");
		// options.add(10);
		// experiments.add("Language features");
		// options.add(4);

		 experiments.add("Embedding features"); options.add(3);
		// experiments.add("Textual+unigram"); options.add(1);
		 experiments.add("PDTB+OLD"); options.add(2);
		// experiments.add("Location+unigram"); options.add(0);

		// allAddRow(writers, experiments);
		// allMakeTable(writers);

		Hashtable<String, ArrayList<ConfusionMatrix>> cmsTable = new Hashtable<String, ArrayList<ConfusionMatrix>>();
		Hashtable<String, ArrayList<ConfusionMatrix>> cmsParaTable = new Hashtable<String, ArrayList<ConfusionMatrix>>();

		for (String experiment : experiments) {
			if (!experiment.equals("Majority") && !experiment.equals("Unigram")) {
				ArrayList<ConfusionMatrix> cms = new ArrayList<ConfusionMatrix>();
				ArrayList<ConfusionMatrix> cmsPara = new ArrayList<ConfusionMatrix>();
				cmsTable.put(experiment, cms);
				cmsParaTable.put(experiment, cmsPara);
			}
		}
		
		ArrayList<ResultInfoRow> resultsDT = new ArrayList<ResultInfoRow>();
		ArrayList<ResultInfoRow> resultsRF = new ArrayList<ResultInfoRow>();
		ArrayList<ResultInfoRow> resultsSVM = new ArrayList<ResultInfoRow>();

		double[][] confusionMatrixDT = new double[5][5];
		double[][] confusionMatrixRF = new double[5][5];
		double[][] confusionMatrixSVM = new double[5][5];

		ArrayList<ConfusionMatrix> cms = new ArrayList<ConfusionMatrix>();
		ArrayList<ConfusionMatrix> cmsPara = new ArrayList<ConfusionMatrix>();

		ResultInfoRow resultRow;

		RevisionPurposeClassifier rpc = new RevisionPurposeClassifier();
		String[] classifiers = { "DT", "SVM", "RF" };
		// String[] classifiers = { "SVM" };
		for (String classifier : classifiers) {
			resultRow = new ResultInfoRow();
			for (int k = 0; k < experiments.size(); k++) { // Try a group of
															// features
				String experiment = experiments.get(k);

				Evaluation eval = rpc.classifyADRevisionPurposeSolo(trainDocs,
						testDocs, usingNgram, options.get(k), classifier);
				resultRow.addExperiment(experiment);

				if (experiment.equals("All features-OLD")
						|| experiment.equals("All features")) {
					double[][] cm = eval.confusionMatrix();
					for (int ii = 0; ii < cm.length; ii++) {
						for (int jj = 0; jj < cm.length; jj++) {
							if (classifier.equals("DT")) {
								confusionMatrixDT[ii][jj] += cm[ii][jj];
							} else if (classifier.equals("RF")) {
								confusionMatrixRF[ii][jj] += cm[ii][jj];
							} else if (classifier.equals("SVM")) {
								confusionMatrixSVM[ii][jj] += cm[ii][jj];
							}
						}
					}
				}
				resultRow.getResult(experiment).fromEvaluation(eval, 5);
				if (cmsTable.containsKey(experiment)) {
					RevisionPurposeTagger.getInstance()
							.setTagLevelParagraph(false);
					Instances[] instances = RevisionPurposeTagger
							.getInstance().prepareForLabelling(trainDocs,
									testDocs, usingNgram, options.get(k));
					String trainPath = "C:\\Not Backed Up\\trainCrf.txt";
					String testPath = "C:\\Not Backed Up\\testCrf.txt";
					String modelPath = "C:\\Not Backed Up\\crf.model";
					String testPath2 = "C:\\Not Backed Up\\testPredictCrf.txt";
					RevisionPurposeTagger.getInstance()
							.transformToTxtForCRFTrain(instances[0],
									trainDocs, trainPath);
					RevisionPurposeTagger.getInstance()
							.transformToTxtForCRF(instances[1], testDocs,
									testPath);
					RevisionPurposeTagger.getInstance().trainAndTag(
							trainPath, modelPath, testPath, testPath2);
					RevisionPurposeTagger.getInstance().readResultToDocs(
							testDocs, testPath2);
					cmsTable.get(experiment)
							.add(PurposeEvaluator
									.getConfusionMatrixOneSurface(testDocs));
					for (RevisionDocument testDoc : testDocs) {
						ArrayList<RevisionUnit> predictedUnits = testDoc
								.getPredictedRoot().getRevisionUnitAtLevel(
										0);
						for (RevisionUnit unit : predictedUnits) {
							unit.setAbandoned();
						}
					}

					RevisionPurposeTagger.getInstance()
							.setTagLevelParagraph(true);
					Instances[] instancesPara = RevisionPurposeTagger
							.getInstance().prepareForLabelling(trainDocs,
									testDocs, usingNgram, options.get(k));
					String trainPathPara = "C:\\Not Backed Up\\trainCrfPara.txt";
					String testPathPara = "C:\\Not Backed Up\\testCrfPara.txt";
					String modelPathPara = "C:\\Not Backed Up\\crfPara.model";
					String testPath2Para = "C:\\Not Backed Up\\testPredictCrf.txt";
					RevisionPurposeTagger.getInstance()
							.transformToTxtForCRFTrain(instancesPara[0],
									trainDocs, trainPathPara);
					RevisionPurposeTagger.getInstance()
							.transformToTxtForCRF(instancesPara[1],
									testDocs, testPathPara);
					RevisionPurposeTagger.getInstance().trainAndTag(
							trainPathPara, modelPathPara, testPathPara,
							testPath2Para);
					RevisionPurposeTagger.getInstance().readResultToDocs(
							testDocs, testPath2Para);
					cmsParaTable
							.get(experiment)
							.add(PurposeEvaluator
									.getConfusionMatrixOneSurface(testDocs));
					for (RevisionDocument testDoc : testDocs) {
						ArrayList<RevisionUnit> predictedUnits = testDoc
								.getPredictedRoot().getRevisionUnitAtLevel(
										0);
						for (RevisionUnit unit : predictedUnits) {
							unit.setAbandoned();
						}
					}

				}


			}
			if (classifier.equals("DT")) {
				resultsDT.add(resultRow);
			} else if (classifier.equals("RF")) {
				resultsRF.add(resultRow);
			} else if (classifier.equals("SVM")) {
				resultsSVM.add(resultRow);
			}
		}

		
		Iterator<String> it = cmsTable.keySet().iterator();
		while (it.hasNext()) {
			String experiment = it.next();
			System.out
					.println("+++++++++Experiment:" + experiment + "++++++++");
			System.out.println("Essay");
			EvaluateTool.printEvaluation(cmsTable.get(experiment));
			System.out.println("Paragraph");
			EvaluateTool.printEvaluation(cmsParaTable.get(experiment));
		}

		ResultInfoWriter.persist(resultsDT, null, "DT-Groups", resultPath);
		ResultInfoWriter.persist(resultsRF, null, "RF-Groups", resultPath);
		ResultInfoWriter.persist(resultsSVM, null, "SVM-Groups", resultPath);
		// Generate a result for surface vs. text-based
		// printCM("DT", confusionMatrixDT);
		printCM("SVM", confusionMatrixSVM);
		// printCM("RF", confusionMatrixRF);
	}

	public static void printCM(String title, double[][] matrix) {
		System.out.println("Classifier: " + title);
		System.out.print("Claim\t");
		System.out.print("Warrant\t");
		System.out.print("Evidence\t");
		System.out.print("General\t");
		System.out.print("Surface\t\n");
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix.length; j++) {
				System.out.print(matrix[i][j] + "\t");
			}
			System.out.println();
		}
	}

	/**
	 * All the revision purposes in a jumbo
	 * 
	 * @param docs
	 * @param folder
	 * @param usingNgram
	 * @param resultPath
	 * @throws Exception
	 */
	public static void crossValidateClassifyJumbo(
			ArrayList<RevisionDocument> docs, int folder, boolean usingNgram,
			String resultPath) throws Exception {
		ArrayList<ArrayList<ArrayList<RevisionDocument>>> crossCuts = EvaluateTool
				.getCrossCut(docs, folder);
		ArrayList<ArrayList<ResultInfoRow>> allResults = new ArrayList<ArrayList<ResultInfoRow>>();

		Hashtable<String, LatexTableWriter> writers = new Hashtable<String, LatexTableWriter>();
		Field[] fields = ResultInfo.class.getDeclaredFields();
		for (Field field : fields) {
			String name = field.getName();
			LatexTableWriter latexWriter = new LatexTableWriter(name);
			writers.put(name, latexWriter);
		}

		// Generate a result for each individual purpose
		for (int i = RevisionPurpose.START; i <= RevisionPurpose.END; i++) {
			if (i == RevisionPurpose.CD_REBUTTAL_RESERVATION)
				continue;
			String purposeName = RevisionPurpose.getPurposeName(i);
			allAddColumn(writers, purposeName);
		}

		ArrayList<String> experiments = new ArrayList<String>();
		ArrayList<Integer> options = new ArrayList<Integer>();
		experiments.add("Majority");
		options.add(100);
		experiments.add("Unigram");
		options.add(-1);
		experiments.add("All features");
		options.add(10);
		// experiments.add("Language features");
		// options.add(4);
		experiments.add("Embedding features");
		options.add(3);
		experiments.add("Textual+unigram");
		options.add(1);
		experiments.add("PDTB+unigram");
		options.add(2);
		experiments.add("Location+unigram");
		options.add(0);
		allAddRow(writers, experiments);

		allMakeTable(writers);
		ArrayList<String> purposes = new ArrayList<String>();
		for (int i = RevisionPurpose.START; i <= RevisionPurpose.END; i++) { // For
																				// each
																				// rev
																				// purpose
			if (i == RevisionPurpose.CD_REBUTTAL_RESERVATION
					|| i == RevisionPurpose.WORDUSAGE_CLARITY_CASCADED)
				continue;
			purposes.add(RevisionPurpose.getPurposeName(i));
			System.out.println("Running:" + RevisionPurpose.getPurposeName(i));
			ArrayList<ResultInfoRow> results = new ArrayList<ResultInfoRow>();
			for (int j = 0; j < folder; j++) { // Do a cross validation
				ResultInfoRow resultRow = new ResultInfoRow();
				ArrayList<RevisionDocument> trainDocs = crossCuts.get(j).get(0);
				ArrayList<RevisionDocument> testDocs = crossCuts.get(j).get(1);

				RevisionPurposeClassifier rpc = new RevisionPurposeClassifier();
				for (int k = 0; k < experiments.size(); k++) { // Try a group of
																// features
					String experiment = experiments.get(k);
					Evaluation eval = rpc.classifyADRevisionPurpose(trainDocs,
							testDocs, usingNgram, i, options.get(k));
					resultRow.addExperiment(experiment);
					resultRow.getResult(experiment).fromEvaluation(eval, 5);
				}
				results.add(resultRow);
			}
			allResults.add(results);
		}
		for (int i = 0; i < allResults.size(); i++) {
			ResultInfoWriter.persist(allResults.get(i), writers,
					purposes.get(i), resultPath);
		}
		allPrint(writers, resultPath);
		// Generate a result for surface vs. text-based

	}

	/**
	 * Here we want to put the result of each folder into a XSLX file
	 * 
	 * Let's leave this for surface text-based classification only Remember:
	 * when use surface vs. text-based, remove the ADD/DELETE operations, keep
	 * only the MODIFY operations for classification
	 * 
	 * @param docs
	 * @param folder
	 * @param usingNgram
	 * @throws Exception
	 */
	public static void crossValidateClassify(ArrayList<RevisionDocument> docs,
			int folder, boolean usingNgram, String resultPath) throws Exception {
		ArrayList<ArrayList<ArrayList<RevisionDocument>>> crossCuts = EvaluateTool
				.getCrossCut(docs, folder);
		ArrayList<ResultInfoRow> results = new ArrayList<ResultInfoRow>();
		double prec = 0.0;
		double recall = 0.0;
		for (int i = 0; i < folder; i++) {
			ResultInfoRow resultRow = new ResultInfoRow();
			ArrayList<RevisionDocument> trainDocs = crossCuts.get(i).get(0);
			ArrayList<RevisionDocument> testDocs = crossCuts.get(i).get(1);

			// ---------------Open this when trying to use this for pipelined
			// approach
			Aligner aligner = new Aligner();
			// aligner.align(trainDocs,
			// testDocs, 2, usingNgram);
			// //---This is the gold standard alignments
			// aligner.repeatAlign(testDocs);
			// RevisionPurposePredicter predictor = new
			// RevisionPurposePredicter(); predictor.predictRevisions(trainDocs,
			// testDocs, true, 1);
			//
			// Eval eval =
			// PurposeEvaluator.evaluatePurposeBinaryTotal(testDocs);
			// System.out.println("PREC:" + eval.unweightedP);
			// System.out.println("RECALL:" + eval.unweightedR); prec +=
			// eval.unweightedP; recall += eval.unweightedR;
			// System.out.println("FOLDER:" + folder);

			RevisionPurposeClassifier rpc = new RevisionPurposeClassifier();
			Evaluation eval;
			String experiment;
			/*
			 * System.out
			 * .println("*****************Unigram baseline*******************");
			 * String experiment = "Unigram"; Evaluation eval =
			 * rpc.classifyADRevisionPurpose(trainDocs, testDocs, usingNgram,
			 * -1); resultRow.addExperiment(experiment);
			 * resultRow.getResult(experiment).fromEvaluation(eval);
			 * 
			 * System.out
			 * .println("*****************Removing location*******************"
			 * ); eval = rpc.classifyADRevisionPurpose(trainDocs, testDocs,
			 * usingNgram, 0); experiment = " Location";
			 * resultRow.addExperiment(experiment);
			 * resultRow.getResult(experiment).fromEvaluation(eval);
			 * 
			 * System.out
			 * .println("*****************Removing text*******************");
			 * eval = rpc.classifyADRevisionPurpose(trainDocs, testDocs,
			 * usingNgram, 1); experiment = " text";
			 * resultRow.addExperiment(experiment);
			 * resultRow.getResult(experiment).fromEvaluation(eval);
			 * 
			 * System.out
			 * .println("*****************Removing language*******************"
			 * ); eval = rpc.classifyADRevisionPurpose(trainDocs, testDocs,
			 * usingNgram, 2); experiment = "language";
			 * resultRow.addExperiment(experiment);
			 * resultRow.getResult(experiment).fromEvaluation(eval);
			 */

			System.out
					.println("*****************Unigram baseline*******************");
			experiment = "Unigram";
			eval = rpc.classifyADRevisionPurpose(trainDocs, testDocs,
					usingNgram, -1);
			resultRow.addExperiment(experiment);
			resultRow.getResult(experiment).fromEvaluation(eval, 2);

			System.out
					.println("*****************All features*******************");
			eval = rpc.classifyADRevisionPurpose(trainDocs, testDocs,
					usingNgram, 4);
			experiment = "All features";
			resultRow.addExperiment(experiment);
			resultRow.getResult(experiment).fromEvaluation(eval, 2);
			results.add(resultRow);

			System.out.println("*****************Majority*******************");
			eval = rpc.classifyADRevisionPurpose(trainDocs, testDocs,
					usingNgram, 5);
			experiment = "Majority";
			resultRow.addExperiment(experiment);
			resultRow.getResult(experiment).fromEvaluation(eval, 2);

			results.add(resultRow);
			/*
			 * System.out.println("*****************Majority*******************")
			 * ; eval = rpc.classifyADRevisionPurpose(trainDocs, testDocs,
			 * usingNgram, 5); experiment = "Majority";
			 * resultRow.addExperiment(experiment);
			 * resultRow.getResult(experiment).fromEvaluation(eval);
			 * 
			 * results.add(resultRow);
			 */

			/*
			 * These belong to history System.out
			 * .println("*****************Removing meta*******************");
			 * eval = rpc.classifyADRevisionPurpose(trainDocs, testDocs,
			 * usingNgram, 3); experiment = " meta";
			 * resultRow.addExperiment(experiment);
			 * resultRow.getResult(experiment).fromEvaluation(eval);
			 */
		}
		// double avgP = prec /
		// folder; double avgR = recall / folder;
		// System.out.println("AVG PREC:" + avgP);
		// System.out.println("AVG RECALL:" + avgR);
		// PredictedRevisionStat.persistToFile(docs, resultPath);
		ResultInfoWriter.persist(results, resultPath);

	}

	public static void crossValidateClassifyCorrelation(
			ArrayList<RevisionDocument> docs, int folder, int distOption,
			boolean autoAligned, boolean highLevel) throws Exception {
		ArrayList<ArrayList<ArrayList<RevisionDocument>>> crossCuts = EvaluateTool
				.getCrossCut(docs, folder);
		for (int i = 0; i < folder; i++) {
			ArrayList<RevisionDocument> trainDocs = crossCuts.get(i).get(0);
			ArrayList<RevisionDocument> testDocs = crossCuts.get(i).get(1);
			Aligner aligner = new Aligner();
			if (!autoAligned) {
				aligner.repeatAlign(testDocs); // Gold
			} else {
				aligner.align(trainDocs, testDocs, distOption);
			}
			RevisionPurposePredicter predictor = new RevisionPurposePredicter();
			if (highLevel) {
				predictor.predictRevisions(trainDocs, testDocs, false, 1);
			} else {
				predictor.predictRevisionsSolo(trainDocs, testDocs, false, 1);
			}
		}
		// System.out.println(AlignmentEvaluator.getAlignmentAccuracyTotal(docs));
		// Eval eval = PurposeEvaluator.evaluatePurposeBinaryTotal(docs);
		// System.out.println("PREC:" + eval.precision);
		// System.out.println("RECALL:" + eval.recall);
		PurposeEvaluator.evaluatePurposeCorrelation(docs);
	}

	public static void crossValidateAlignClassify(
			ArrayList<RevisionDocument> docs, int folder, int distOption)
			throws Exception {
		ArrayList<ArrayList<ArrayList<RevisionDocument>>> crossCuts = EvaluateTool
				.getCrossCut(docs, folder);
		for (int i = 0; i < folder; i++) {
			ArrayList<RevisionDocument> trainDocs = crossCuts.get(i).get(0);
			ArrayList<RevisionDocument> testDocs = crossCuts.get(i).get(1);
			Aligner aligner = new Aligner();
			aligner.align(trainDocs, testDocs, distOption);
			RevisionPurposePredicter predictor = new RevisionPurposePredicter();
			predictor.predictRevisions(trainDocs, testDocs, false, 1);
		}
		System.out.println(AlignmentEvaluator.getAlignmentAccuracyTotal(docs));
		Eval eval = PurposeEvaluator.evaluatePurposeBinaryTotal(docs);
		System.out.println("PREC:" + eval.precision);
		System.out.println("RECALL:" + eval.recall);
	}

	public static void crossValidateAlignClassify(
			ArrayList<RevisionDocument> docs, int folder, boolean usingNgram,
			String resultPath, int distOption) throws Exception {
		ArrayList<ArrayList<ArrayList<RevisionDocument>>> crossCuts = EvaluateTool
				.getCrossCut(docs, folder);
		double prec = 0.0;
		double recall = 0.0;
		ArrayList<ResultInfoRow> results = new ArrayList<ResultInfoRow>();
		for (int i = 0; i < folder; i++) {
			ResultInfoRow resultRow = new ResultInfoRow();
			ArrayList<RevisionDocument> trainDocs = crossCuts.get(i).get(0);
			ArrayList<RevisionDocument> testDocs = crossCuts.get(i).get(1);
			Aligner aligner = new Aligner();
			aligner.align(trainDocs, testDocs, distOption);
			RevisionPurposePredicter predictor = new RevisionPurposePredicter();
			predictor.predictRevisions(trainDocs, testDocs, true, 1);
			/*
			 * RevisionPurposeClassifier rpc = new RevisionPurposeClassifier();
			 * 
			 * System.out.println(
			 * "*****************Unigram baseline*******************"); String
			 * experiment = "Unigram"; Evaluation eval =
			 * rpc.classifyADAlignRevisionPurpose(trainDocs, testDocs,
			 * usingNgram,-1); resultRow.addExperiment(experiment);
			 * resultRow.getResult(experiment).fromEvaluation(eval);
			 * 
			 * 
			 * System.out.println(
			 * "*****************Removing location*******************"); eval =
			 * rpc.classifyADAlignRevisionPurpose(trainDocs, testDocs,
			 * usingNgram,0); experiment = "Remove Location";
			 * resultRow.addExperiment(experiment);
			 * resultRow.getResult(experiment).fromEvaluation(eval);
			 * 
			 * System.out.println(
			 * "*****************Removing text*******************"); eval =
			 * rpc.classifyADAlignRevisionPurpose(trainDocs, testDocs,
			 * usingNgram,1); experiment = "Remove text";
			 * resultRow.addExperiment(experiment);
			 * resultRow.getResult(experiment).fromEvaluation(eval);
			 * 
			 * System.out.println(
			 * "*****************Removing language*******************"); eval =
			 * rpc.classifyADAlignRevisionPurpose(trainDocs, testDocs,
			 * usingNgram,2); experiment = "Remove language";
			 * resultRow.addExperiment(experiment);
			 * resultRow.getResult(experiment).fromEvaluation(eval);
			 * 
			 * 
			 * System.out.println(
			 * "*****************Removing meta*******************"); eval =
			 * rpc.classifyADAlignRevisionPurpose(trainDocs, testDocs,
			 * usingNgram,3); experiment = "Remove meta";
			 * resultRow.addExperiment(experiment);
			 * resultRow.getResult(experiment).fromEvaluation(eval);
			 * 
			 * System.out.println("*****************All features*******************"
			 * ); eval = rpc.classifyADAlignRevisionPurpose(trainDocs, testDocs,
			 * usingNgram,4); experiment = "All features";
			 * resultRow.addExperiment(experiment);
			 * resultRow.getResult(experiment).fromEvaluation(eval);
			 * 
			 * System.out.println("*****************Majority*******************")
			 * ; eval = rpc.classifyADRevisionPurpose(trainDocs, testDocs,
			 * usingNgram,5); experiment = "Majority";
			 * resultRow.addExperiment(experiment);
			 * resultRow.getResult(experiment).fromEvaluation(eval);
			 * 
			 * results.add(resultRow); }
			 */
			// Eval eval = PurposeEvaluator.evaluatePurposeBinaryTotal(docs);
			// System.out.println("PREC:" + eval.precision);
			// System.out.println("RECALL:" + eval.recall);
			// prec += eval.precision;
			// recall += eval.recall;
		}
		// ResultInfoWriter.persist(results, resultPath);
		PurposeEvaluator.evaluatePurposeBinaryTotal(docs);
		System.out.println("AVG PREC:" + prec / folder);
		System.out.println("AVG RECALL:" + recall / folder);
	}

	public static void extrinsicEvaluate(ArrayList<RevisionDocument> docs) {

	}
}
