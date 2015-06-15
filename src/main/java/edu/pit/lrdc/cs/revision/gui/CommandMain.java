package edu.pit.lrdc.cs.revision.gui;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import edu.pitt.cs.revision.purpose.RevisionPurposePredicter;
import edu.pitt.lrdc.cs.revision.agreement.KappaCalc;
import edu.pitt.lrdc.cs.revision.alignment.Aligner;
import edu.pitt.lrdc.cs.revision.io.RevisionDocumentReader;
import edu.pitt.lrdc.cs.revision.io.RevisionDocumentWriter;
import edu.pitt.lrdc.cs.revision.model.RevisionDocument;
import edu.pitt.lrdc.cs.revision.model.RevisionPurpose;
import edu.pitt.lrdc.cs.revision.process.BatchProcessor;
import edu.pitt.lrdc.cs.revision.statistics.DataStatistics;

/**
 * Code for command control
 * 
 * @author Administrator
 *
 */
public class CommandMain {
	static boolean usingNgram;
	static ArrayList<RevisionDocument> trainAlignDocs;
	static ArrayList<RevisionDocument> trainPredictDocs;

	public static void loadSettings() {
		Scanner sc = new Scanner(System.in);
		boolean gotInput = false;
		while (!gotInput) {
			System.out
					.println("Please enter the path of the documents for alignment training");
			String trainAlignPaths = sc.nextLine();
			try {
				trainAlignDocs = RevisionDocumentReader
						.readDocs(trainAlignPaths);
				System.out.println("Training data loaded");
				gotInput = true;
			} catch (Exception exp) {
				gotInput = false;
			}
		}
		gotInput = false;
		while (!gotInput) {
			System.out
					.println("Please enter the path of the documents for revision prediction training");
			String trainPredictPaths = sc.nextLine();
			try {
				trainPredictDocs = RevisionDocumentReader
						.readDocs(trainPredictPaths);
				System.out.println("Training data loaded");
				gotInput = true;
			} catch (Exception exp) {
				gotInput = false;
			}
		}
	}

	public static int getInput(ArrayList<String> options) {
		System.out.println("Please input the index of your choice");
		for (int i = 0; i < options.size(); i++) {
			System.out.println(i + ": " + options.get(i));
		}
		System.out.println(options.size() + ": EXIT");
		Scanner sc = new Scanner(System.in);
		int i = sc.nextInt();
		sc.close();
		if (i >= options.size() || i < 0) {
			return -1;
		} else {
			return i;
		}
	}

	public static void outputResult(String content) throws IOException {
		String file = "result.txt";
		BufferedWriter writer = new BufferedWriter(new FileWriter(file));
		writer.write(content);
		writer.close();
	}

	static ArrayList<String> options = new ArrayList<String>();

	public static void start() {
		System.out.println("What do you want?");
		options.add("Calculate Kappa (Input: annotated revision documents, Output: Kappa score");
		options.add("Transform Txt files to splitted line excel files (Input: txt files, Output: excel files with split lines automatically aligned");
		;
		options.add("Predict revision types");
		options.add("Complete revision analysis (Input: txt files, Output: Report file of revision type and revision purposes");

		int option = getInput(options);

		switch (option) {
		case 0:
			kappaCalc();
			break;
		case 1:
			alignDocuments();
			break;
		case 2:
			predictRevisions();
			break;
		case 3:
			allInOne();
			break;
		default:
			start();
		}
		options.clear();
	}

	/**
	 * Calculate the kappa of annotated files
	 */
	public static void kappaCalc() {
		System.out.println("Calculating Kappa");
		Scanner sc = new Scanner(System.in);
		boolean gotInput = false;
		ArrayList<RevisionDocument> docs = null;
		ArrayList<RevisionDocument> docs2 = null;
		while (!gotInput) {
			System.out
					.println("Input the path of the folder containing files from one annotator");
			String path1 = "";

			path1 = sc.nextLine();
			gotInput = true;
			try {
				docs = RevisionDocumentReader.readDocs(path1);
				System.out.println("Documents loaded");
			} catch (Exception exp) {
				System.out
						.println("Input path is not valid, check whether the file path is correct or the file is in standard form");
				gotInput = false;
			}
		}
		gotInput = false;
		while (!gotInput) {
			System.out
					.println("Input the path of the folder containing files from the other annotator");
			String path2 = "";
			path2 = sc.nextLine();
			gotInput = true;
			try {
				docs2 = RevisionDocumentReader.readDocs(path2);
				System.out.println("Documents loaded");
			} catch (Exception exp) {
				System.out
						.println("Input path is not valid, check whether the file path is correct or the file is in standard form");
				gotInput = false;
			}
		}
		KappaCalc kc = new KappaCalc();
		int levels = 3;
		for (int i = 0; i <= levels; i++) {
			System.out.println("Kappa at Level: " + i);
			int[][] matrix = kc.buildRUMatrixAtLevelBinary(docs, docs2, 1,
					RevisionPurpose.CD_GENERAL_CONTENT_DEVELOPMENT);
			System.out.println(kc.kappaCalc(matrix));
		}
		sc.close();
	}

	/**
	 * Aligning the documents
	 */
	public static void alignDocuments() {
		System.out.println("Automatically align the documents");
		String path = "";
		String outputPath = "";
		Scanner sc = new Scanner(System.in);
		boolean gotInput = false;
		while (!gotInput) {
			System.out
					.println("Input the path of the root folder of all txt files");
			System.out.println("******Note**********");
			System.out
					.println("The root folder should contain two folders, one named 'draft1' and one named 'draft2'");
			System.out
					.println("draft1 contains the text files of the original draft, while draft2 contains the text files of the second draft");

			path = sc.nextLine();
			gotInput = true;
			if (path != null) {
				File fileChecker = new File(path);
				if (!fileChecker.exists() || !fileChecker.isDirectory()) {
					System.out.println("Folder does not exist");
					gotInput = false;
				}
			} else {// should not happen
				gotInput = false;
			}
		}

		gotInput = false;
		while (!gotInput) {
			System.out.println("Input the output path");
			outputPath = sc.nextLine();
			gotInput = true;
			if (outputPath != null) {
				File fileChecker = new File(outputPath);
				if (!fileChecker.exists() || !fileChecker.isDirectory()) {
					System.out.println("Folder does not exist");
					gotInput = false;
				}
			} else {// should not happen
				gotInput = false;
			}
		}

		BatchProcessor bp = new BatchProcessor();
		try {
			bp.transform(path, outputPath, null, null);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			alignDocuments();
		}
		ArrayList<RevisionDocument> alignDocs;
		try {
			alignDocs = RevisionDocumentReader.readDocs(outputPath);
			Aligner aligner = new Aligner();
			aligner.align(trainAlignDocs, alignDocs, 2, usingNgram);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		sc.close();
	}

	/**
	 * Predict the revisions
	 */
	public static void predictRevisions() {
		System.out.println("Automatically predicting revisions");

		String path = "";
		ArrayList<RevisionDocument> docs = null;

		Scanner sc = new Scanner(System.in);
		boolean gotInput = false;
		while (!gotInput) {
			System.out
					.println("Provide the path of the folder of the excel files");
			path = sc.nextLine();
			try {
				docs = RevisionDocumentReader.readDocs(path);
				RevisionPurposePredicter rpp = new RevisionPurposePredicter();

				/** Have to find a trained document first */
				rpp.predictRevisions(trainPredictDocs, docs, true, 1);

				gotInput = true;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				gotInput = false;
				e.printStackTrace();
			}
		}

		gotInput = false;
		String outputPath = "";
		while (!gotInput) {
			System.out
					.println("Do you want to the cover the original files(Y/N)");
			String option = sc.nextLine();
			if (option.trim().equals("Y") || option.trim().equals("y")) {
				outputPath = path;
				gotInput = true;
			} else {
				System.out.println("Please enter the destination folder path");
				outputPath = sc.nextLine();
			}

			try {
				for (RevisionDocument doc : docs)
					RevisionDocumentWriter.writeToDoc(doc, outputPath + "/"
							+ new File(doc.getDocumentName()).getName());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				gotInput = false;
			}
		}
		sc.close();
	}

	public static void allInOne() {
		System.out.println("Revision analysis");
		System.out
				.println("Provide the path of the text files and the revisions will be analyzed");
		Scanner sc = new Scanner(System.in);
		String path = "";
		boolean gotInput = false;
		while (!gotInput) {
			path = sc.nextLine();
			File file = new File(path);
			if (!file.exists() && !file.isDirectory()) {
				gotInput = false;
				System.out
						.println("The file does not exist or is not a directory");
			} else {
				gotInput = true;
				System.out
						.println("Enter the destination directory path to store the revision files");
				String newPath = sc.nextLine();
				BatchProcessor bp = new BatchProcessor();
				try {
					bp.transform(path, newPath, null, null);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					ArrayList<RevisionDocument> docs = RevisionDocumentReader
							.readDocs(newPath);
					Aligner aligner = new Aligner();
					aligner.align(trainAlignDocs, docs, 2, usingNgram);
					// aligner.repeatAlign(testDocs);
					RevisionPurposePredicter predictor = new RevisionPurposePredicter();
					predictor.predictRevisions(trainPredictDocs, docs, true, 1);
					DataStatistics.stat(docs);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}

	}

	public static void main(String[] args) {
		start();
	}
}
