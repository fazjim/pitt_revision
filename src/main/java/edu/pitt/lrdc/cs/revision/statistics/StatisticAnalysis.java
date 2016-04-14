package edu.pitt.lrdc.cs.revision.statistics;

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import edu.pitt.lrdc.cs.revision.io.RevisionDocumentReader;
import edu.pitt.lrdc.cs.revision.model.RevisionDocument;
import edu.pitt.lrdc.cs.revision.model.RevisionOp;
import edu.pitt.lrdc.cs.revision.model.RevisionPurpose;
import edu.pitt.lrdc.cs.revision.model.RevisionUnit;

public class StatisticAnalysis {
	public static void countIES(String folderPath) throws Exception {
		// String folderPath =
		// "C:\\Not Backed Up\\data\\allNewData\\Fan\\All-jiaoyang";
		ArrayList<RevisionDocument> trainDocs = RevisionDocumentReader
				.readDocs(folderPath);
		String scorePath = "C:\\Not Backed Up\\data\\expert-grades-ies.xlsx";

		XSSFWorkbook xwb = new XSSFWorkbook(scorePath);
		XSSFSheet sheet0 = xwb.getSheetAt(0);

		XSSFRow header0 = sheet0.getRow(0);
		int nameIndex = 0;
		int e1Index = 0;
		int e2Index = 0;
		int w1Index = 0;
		int w2Index = 0;
		int typeIndex = 0;
		for (int i = 0; i < header0.getPhysicalNumberOfCells(); i++) {
			String cellName = header0.getCell(i).getStringCellValue();
			if (cellName.trim().equals("Author"))
				nameIndex = i;
			else if (cellName.trim().equals("Average.Draft1"))
				e1Index = i;
			else if (cellName.trim().equals("Average.Draft2"))
				e2Index = i;
			else if (cellName.trim().equals("Writing.Style.Draft1"))
				w1Index = i;
			else if (cellName.trim().equals("Writing.Style.Draft2"))
				w2Index = i;
			else if (cellName.trim().equals("Essay.Draft1")) {
				typeIndex = i;
			}
		}

		Hashtable<String, Double> e1Scores = new Hashtable<String, Double>();
		Hashtable<String, Double> e2Scores = new Hashtable<String, Double>();
		Hashtable<String, Double> w1Scores = new Hashtable<String, Double>();
		Hashtable<String, Double> w2Scores = new Hashtable<String, Double>();
		for (int i = 1; i < sheet0.getPhysicalNumberOfRows(); i++) {
			XSSFRow row = sheet0.getRow(i);
			boolean search = false;
			try {
				String cellType = row.getCell(typeIndex).getStringCellValue();
				if (cellType.trim().equals("Louv"))
					search = true;
			} catch (Exception e) {

			}
			search = true;
			if (search && row.getCell(nameIndex) != null) {
				String name = row.getCell(nameIndex).getStringCellValue();

				if (row.getCell(e1Index) != null) {
					try {
						double val = row.getCell(e1Index).getNumericCellValue();
						// double val =
						// Double.parseDouble(row.getCell(e1Index).getStringCellValue());
						e1Scores.put(name, val);
					} catch (Exception exp) {

					}
				}
				if (row.getCell(e2Index) != null) {
					try {
						double val = row.getCell(e2Index).getNumericCellValue();
						e2Scores.put(name, val);
					} catch (Exception exp) {

					}
				}
				if (row.getCell(w1Index) != null) {
					try {
						double val = row.getCell(w1Index).getNumericCellValue();
						w1Scores.put(name, val);
					} catch (Exception exp) {

					}
				}
				if (row.getCell(w2Index) != null) {
					try {
						double val = row.getCell(w2Index).getNumericCellValue();
						w2Scores.put(name, val);
					} catch (Exception exp) {

					}
				}
			}
		}
		// trainDocs.addAll(testDocs);
		printAllInfoUnique(trainDocs, e1Scores, e2Scores, w1Scores, w2Scores);
	}

	public static void main(String[] args) throws Exception {
		String path = "C:\\Not Backed Up\\data\\naaclData\\C1";
		// String path =
		// "C:\\Not Backed Up\\data\\allNewData\\Fan\\kappa3\\jiaoyang";
		// printAllInfoCompleteUnique(RevisionDocumentReader.readDocs(path));
		// countIES(path);
		// ArrayList<RevisionDocument> trainDocs = reader
		// .readDocs("D:\\annotationTool\\annotated\\revisedClass3");
		/*
		 * ArrayList<RevisionDocument> trainDocs =
		 * reader.readDocs("C:\\Not Backed Up\\data\\annotated\\revisedClass3");
		 * ArrayList<RevisionDocument> testDocs = reader
		 * .readDocs("D:\\annotationTool\\annotated\\class4");
		 */
		ArrayList<RevisionDocument> testDocs = RevisionDocumentReader
				.readDocs(path);
		printAllInfoOp(testDocs);
		// for(int i =
		// RevisionPurpose.START;i<=RevisionPurpose.WORDUSAGE_CLARITY;i++) {
		// printCatgories(testDocs,i);
		// }
		// ArrayList<RevisionDocument> testDocs2 = reader
		// .readDocs("D:\\annotationTool\\annotated\\class2");
		// testDocs.addAll(testDocs2);
		// trainDocs.addAll(testDocs);
		// analyzePatternCorrelation(trainDocs);
		// analyzeSpecificCategoryCorrelation(trainDocs);
		// analyzeSpecificCategoryCorrelationRatio(trainDocs);
	}

	public static String genIndiceStr(ArrayList<Integer> indices) {
		String indexStr = "";
		for (Integer index : indices) {
			indexStr += index + "-";
		}
		return indexStr;
	}

	public static void printAllInfoUnique(ArrayList<RevisionDocument> docs) {
		System.out.print("Username\t");
		for (int i = RevisionPurpose.START; i <= RevisionPurpose.END; i++) {
			System.out.print(RevisionPurpose.getPurposeName(i) + "\t");
		}
		for (int i = RevisionOp.START; i <= RevisionOp.MODIFY; i++) {
			System.out.print(RevisionOp.getOpName(i) + "\t");
		}
		System.out.println();

		for (RevisionDocument doc : docs) {
			System.out.print(doc.getDocumentName() + "\t");
			double[] distribution = new double[9];
			double[] distribution2 = new double[3];
			int total = 0;
			ArrayList<RevisionUnit> rus = doc.getRoot().getRevisionUnitAtLevel(
					0);
			Hashtable<String, Integer> revPurposesMap = new Hashtable<String, Integer>();
			// Hashtable<String,Integer> surfaceRevMap = new Hashtable<String,
			// Integer>();
			for (RevisionUnit ru : rus) {
				if (ru.getRevision_purpose() > RevisionPurpose.CD_GENERAL_CONTENT_DEVELOPMENT) {
					distribution[ru.getRevision_purpose() - 1] += 1;
					distribution2[ru.getRevision_op() - 1] += 1;
					total += 1;
				} else {
					ArrayList<Integer> oldIndices = ru.getOldSentenceIndex();
					ArrayList<Integer> newIndices = ru.getNewSentenceIndex();

					String id = ru.getRevision_op() + ":"
							+ genIndiceStr(oldIndices) + ":"
							+ genIndiceStr(newIndices);
					int currentPurpose = ru.getRevision_purpose();
					if (revPurposesMap.containsKey(id)) {
						int purpose = revPurposesMap.get(id);
						// Put more important categories in
						if (currentPurpose < purpose)
							revPurposesMap.put(id, currentPurpose);
					} else {
						revPurposesMap.put(id, currentPurpose);
					}
				}
			}

			Iterator<String> it = revPurposesMap.keySet().iterator();
			while (it.hasNext()) {
				String name = it.next();
				int purpose = revPurposesMap.get(name);
				String[] splits = name.split(":");
				int op = Integer.parseInt(splits[0]);
				distribution[purpose - 1] += 1;
				distribution2[op - 1] += 1;
				total += 1;
			}
			for (int i = RevisionPurpose.START; i <= RevisionPurpose.END; i++) {
				System.out.print(distribution[i - 1] + "\t");

			}
			for (int i = RevisionOp.START; i <= RevisionOp.MODIFY; i++) {
				System.out.print(distribution2[i - 1] + "\t");

			}
			System.out.println();
			/*
			 * for(int i = RevisionPurpose.START;i<=RevisionPurpose.END;i++) {
			 * System
			 * .out.println(RevisionPurpose.getPurposeName(i)+":"+distribution
			 * [i-1]); }
			 */
		}
	}

	public static void printAllInfoCompleteUnique(
			ArrayList<RevisionDocument> docs) {
		System.out.print("Username\t");
		for (int i = RevisionPurpose.START; i <= RevisionPurpose.END; i++) {
			for (int j = RevisionOp.START; j <= RevisionOp.MODIFY; j++) {
				System.out.print(RevisionPurpose.getPurposeName(i) + "-"
						+ RevisionOp.getOpName(j) + "\t");
			}
		}
		System.out.print("#SentencesD1\t");
		System.out.print("#SentencesD2\t");
		System.out.println();

		for (RevisionDocument doc : docs) {
			System.out.print(doc.getDocumentName() + "\t");
			double[] distribution = new double[27];
			double[] distribution2 = new double[3];
			int total = 0;
			ArrayList<RevisionUnit> rus = doc.getRoot().getRevisionUnitAtLevel(
					0);
			Hashtable<String, Integer> revPurposesMap = new Hashtable<String, Integer>();
			// Hashtable<String,Integer> surfaceRevMap = new Hashtable<String,
			// Integer>();
			for (RevisionUnit ru : rus) {
				if (ru.getRevision_purpose() <= RevisionPurpose.END) {
					if (ru.getRevision_purpose() > RevisionPurpose.CD_GENERAL_CONTENT_DEVELOPMENT) {
						distribution[(ru.getRevision_purpose() - 1) * 3
								+ (ru.getRevision_op() - 1)] += 1;
						// distribution2[ru.getRevision_op() - 1] += 1;
						total += 1;
					} else {
						ArrayList<Integer> oldIndices = ru
								.getOldSentenceIndex();
						ArrayList<Integer> newIndices = ru
								.getNewSentenceIndex();

						String id = ru.getRevision_op() + ":"
								+ genIndiceStr(oldIndices) + ":"
								+ genIndiceStr(newIndices);
						int currentPurpose = ru.getRevision_purpose();
						if (revPurposesMap.containsKey(id)) {
							int purpose = revPurposesMap.get(id);
							// Put more important categories in
							if (currentPurpose < purpose)
								revPurposesMap.put(id, currentPurpose);
						} else {
							revPurposesMap.put(id, currentPurpose);
						}
					}
				}
			}

			Iterator<String> it = revPurposesMap.keySet().iterator();
			while (it.hasNext()) {
				String name = it.next();
				int purpose = revPurposesMap.get(name);
				String[] splits = name.split(":");
				int op = Integer.parseInt(splits[0]);
				distribution[(purpose - 1) * 3 + (op - 1)] += 1;
				// distribution[purpose-1] += 1;
				// distribution2[op-1] += 1;
				total += 1;
			}
			for (int i = RevisionPurpose.START; i <= RevisionPurpose.END; i++) {
				for (int j = RevisionOp.START; j <= RevisionOp.MODIFY; j++) {
					System.out
							.print(distribution[(i - 1) * 3 + (j - 1)] + "\t");
				}
			}
			System.out.print(doc.getOldDraftSentences().size() + "\t");
			System.out.print(doc.getNewDraftSentences().size() + "\t");
			System.out.println();
			/*
			 * for(int i = RevisionPurpose.START;i<=RevisionPurpose.END;i++) {
			 * System
			 * .out.println(RevisionPurpose.getPurposeName(i)+":"+distribution
			 * [i-1]); }
			 */
		}
	}

	public static void printAllInfoUnique(ArrayList<RevisionDocument> docs,
			Hashtable<String, Double> e1Scores,
			Hashtable<String, Double> e2Scores,
			Hashtable<String, Double> w1Scores,
			Hashtable<String, Double> w2Scores) {
		System.out.print("Username\t");
		for (int i = RevisionPurpose.START; i <= RevisionPurpose.END; i++) {
			System.out.print(RevisionPurpose.getPurposeName(i) + "\t");
		}
		for (int i = RevisionOp.START; i <= RevisionOp.MODIFY; i++) {
			System.out.print(RevisionOp.getOpName(i) + "\t");
		}
		System.out.print("Essay1\t");
		System.out.print("Essay2\t");
		System.out.print("Writing 1\t");
		System.out.print("Writing 2\t");
		System.out.println();

		for (RevisionDocument doc : docs) {
			System.out.print(doc.getDocumentName() + "\t");
			String realName = new File(doc.getDocumentName()).getName();
			// realName = realName.replaceAll("Annotation_","");
			if (realName.contains(" ")) {
				realName = realName.substring(realName.indexOf("_") + 1,
						realName.indexOf(" ")).trim();
			} else {
				realName = realName.substring(realName.indexOf("_") + 1,
						realName.indexOf(".txt")).trim();
			}
			double[] distribution = new double[9];
			double[] distribution2 = new double[3];
			int total = 0;
			ArrayList<RevisionUnit> rus = doc.getRoot().getRevisionUnitAtLevel(
					0);
			Hashtable<String, Integer> revPurposesMap = new Hashtable<String, Integer>();
			// Hashtable<String,Integer> surfaceRevMap = new Hashtable<String,
			// Integer>();
			for (RevisionUnit ru : rus) {
				if (ru.getRevision_purpose() != RevisionPurpose.PRECISION
						&& ru.getRevision_purpose() > RevisionPurpose.CD_GENERAL_CONTENT_DEVELOPMENT) {
					distribution[ru.getRevision_purpose() - 1] += 1;
					distribution2[ru.getRevision_op() - 1] += 1;
					total += 1;
				} else {
					ArrayList<Integer> oldIndices = ru.getOldSentenceIndex();
					ArrayList<Integer> newIndices = ru.getNewSentenceIndex();

					String id = ru.getRevision_op() + ":"
							+ genIndiceStr(oldIndices) + ":"
							+ genIndiceStr(newIndices);
					int currentPurpose = ru.getRevision_purpose();
					if (revPurposesMap.containsKey(id)) {
						int purpose = revPurposesMap.get(id);
						// Put more important categories in
						if (currentPurpose < purpose)
							revPurposesMap.put(id, currentPurpose);
					} else {
						revPurposesMap.put(id, currentPurpose);
					}
				}
			}

			Iterator<String> it = revPurposesMap.keySet().iterator();
			while (it.hasNext()) {
				String name = it.next();
				int purpose = revPurposesMap.get(name);
				String[] splits = name.split(":");
				int op = Integer.parseInt(splits[0]);
				if (purpose == RevisionPurpose.PRECISION)
					purpose = RevisionPurpose.WORDUSAGE_CLARITY;
				distribution[purpose - 1] += 1;
				distribution2[op - 1] += 1;
				total += 1;
			}
			for (int i = RevisionPurpose.START; i <= RevisionPurpose.END; i++) {
				System.out.print(distribution[i - 1] + "\t");

			}
			for (int i = RevisionOp.START; i <= RevisionOp.MODIFY; i++) {
				System.out.print(distribution2[i - 1] + "\t");

			}
			if (e1Scores.containsKey(realName))
				System.out.print(e1Scores.get(realName) + "\t");
			else
				System.out.print("N/A\t");
			if (e2Scores.containsKey(realName))
				System.out.print(e2Scores.get(realName) + "\t");
			else
				System.out.print("N/A\t");
			if (w1Scores.containsKey(realName))
				System.out.print(w1Scores.get(realName) + "\t");
			else
				System.out.print("N/A\t");
			if (w2Scores.containsKey(realName))
				System.out.print(w2Scores.get(realName) + "\t");
			else
				System.out.print("N/A\t");
			System.out.println();
			/*
			 * for(int i = RevisionPurpose.START;i<=RevisionPurpose.END;i++) {
			 * System
			 * .out.println(RevisionPurpose.getPurposeName(i)+":"+distribution
			 * [i-1]); }
			 */
		}
	}

	public static void printAllInfoOp(ArrayList<RevisionDocument> docs) {
		System.out.print("Username\t");
		for (int i = RevisionPurpose.START; i <= RevisionPurpose.END; i++) {
			for (int j = RevisionOp.START; j <= RevisionOp.MODIFY; j++) {
				System.out.print(RevisionPurpose.getPurposeName(i) + "_"
						+ RevisionOp.getOpName(j) + "\t");
			}
		}
		System.out.println();
		for (RevisionDocument doc : docs) {
			System.out.print(doc.getDocumentName() + "\t");
			double[] distribution = new double[27];
			ArrayList<RevisionUnit> rus = doc.getRoot().getRevisionUnitAtLevel(
					0);
			for (RevisionUnit ru : rus) {
				distribution[(ru.getRevision_purpose() - 1) * 3
						+ ru.getRevision_op() - 1] += 1;
			}

			for (double val : distribution) {
				System.out.print(val + "\t");
			}
			System.out.println();
		}

	}

	public static void printAllInfo(ArrayList<RevisionDocument> docs) {
		System.out.print("Username\t");
		for (int i = RevisionPurpose.START; i <= RevisionPurpose.END; i++) {
			System.out.print(RevisionPurpose.getPurposeName(i) + "\t");
		}
		for (int i = RevisionOp.START; i <= RevisionOp.MODIFY; i++) {
			System.out.print(RevisionOp.getOpName(i) + "\t");
		}

		System.out.println();

		String theRebuttalOp = "";
		for (RevisionDocument doc : docs) {
			System.out.print(doc.getDocumentName() + "\t");
			double[] distribution = new double[9];
			double[] distribution2 = new double[3];
			int total = 0;
			ArrayList<RevisionUnit> rus = doc.getRoot().getRevisionUnitAtLevel(
					0);
			for (RevisionUnit ru : rus) {

				distribution[ru.getRevision_purpose() - 1] += 1;
				distribution2[ru.getRevision_op() - 1] += 1;
				if (ru.getRevision_purpose() == RevisionPurpose.CD_REBUTTAL_RESERVATION)
					theRebuttalOp = RevisionOp.getOpName(ru.getRevision_op());
				total += 1;
			}
			for (int i = RevisionPurpose.START; i <= RevisionPurpose.END; i++) {
				System.out.print(distribution[i - 1] + "\t");

			}
			for (int i = RevisionOp.START; i <= RevisionOp.MODIFY; i++) {
				System.out.print(distribution2[i - 1] + "\t");

			}
			System.out.println();
			/*
			 * for(int i = RevisionPurpose.START;i<=RevisionPurpose.END;i++) {
			 * System
			 * .out.println(RevisionPurpose.getPurposeName(i)+":"+distribution
			 * [i-1]); }
			 */
		}
		System.out.println(theRebuttalOp);
	}

	public static void analyzeSpecificCategoryCorrelation(
			ArrayList<RevisionDocument> docs) {
		// String[] improvedGroup = {"beachbabe", "honeybooboo",
		// "Maria_Destler", "pioneer3", "TuckerDidIt", "Vinvannob"};
		// String[] deprovedGroup = {"lively01", "RawDawg", "Taligate"};
		//
		// String[] improvedGroup2 = {"Nepa", "Mr",
		// "Psedu","franks","Jimmyyyyy"};
		// String[] deprovedGroup2 = {};

		// String[] improvedGroup = {"lightbulb", "MickJagger", "Taligate"};
		// String[] deprovedGroup = {"beachbabe", "honeybooboo", "pioneer",
		// "Raw","Rex", "TuckerDidIt"};
		//
		// String[] improvedGroup2 = {"scarlett", "Mr",
		// "petzl","Jimmyyyy","Jimmyyyyy"};
		// String[] deprovedGroup2 = {"Psedu", "franks", "Captain"};

		/* G1 */
		String[] improvedGroup = { "honeybooboo", "lightbulb", "Maria", "Mick",
				"Vivan" };
		String[] deprovedGroup = { "lively01", "Rex" };

		String[] improvedGroup2 = { "Mr", "Jimmyyyyy" };
		String[] deprovedGroup2 = { "Definitely", "definately", "Dr",
				"Captian", "Definitly" };

		/* G2 */
		// String[] improvedGroup = {"lightbulb","Taligate","Mick"};
		// String[] deprovedGroup = {"honeybooboo","beachbabe", "pioneer",
		// "Raw","Rex","TuckerDidIt"};
		//
		// String[] improvedGroup2 = {"Mr","Jimmyyyyy", "petzl", "scarlett"};
		// String[] deprovedGroup2 = {"Captian", "frankschickens", "Psedu"};

		/* G3 */
		// String[] improvedGroup = {"honeybooboo", "lightbulb","pioneer",
		// "Maria","Mick","Vivan"};
		// String[] deprovedGroup = {"lively01", "Power", "Smile",
		// "Rex","Tailgate", "TheGreen"};
		//
		// String[] improvedGroup2 = {"Mr","Jimmyyyyy", "Nepa",
		// "Psedu","frankschickens"};
		// String[] deprovedGroup2 = {};

		/* G4 */
		// String[] improvedGroup = {"honeybooboo", "lightbulb","pioneer",
		// "Maria","Mick","Vivan"};
		// String[] deprovedGroup = {"lively01", "Power", "Smile",
		// "Rex","Taligate", "TheGreen"};
		//
		// String[] improvedGroup2 = {"Mr","Jimmyyyyy", "Nepa",
		// "Psedu","frankschickens"};
		// String[] deprovedGroup2 = {"Captian", "Definitely",
		// "definately","Definitly","Dr"};

		ArrayList<RevisionDocument> improved = new ArrayList<RevisionDocument>();
		ArrayList<RevisionDocument> deproved = new ArrayList<RevisionDocument>();
		ArrayList<RevisionDocument> notChanged = new ArrayList<RevisionDocument>();

		for (RevisionDocument doc : docs) {
			if (isInArr(doc.getDocumentName(), improvedGroup)
					|| isInArr(doc.getDocumentName(), improvedGroup2)) {
				improved.add(doc);
			} else if (isInArr(doc.getDocumentName(), deprovedGroup)
					|| isInArr(doc.getDocumentName(), deprovedGroup2)) {
				deproved.add(doc);
			} else {
				notChanged.add(doc);
			}
		}
		// notChanged.addAll(deproved);
		for (int i = RevisionPurpose.START; i <= RevisionPurpose.WORDUSAGE_CLARITY; i++) {
			System.out.println(RevisionPurpose.getPurposeName(i) + ":");
			ArrayList<Integer> improvedVals = getValsForEach(improved, i);
			ArrayList<Integer> deprovedVals = getValsForEach(deproved, i);
			ArrayList<Integer> notImprovedVals = getValsForEach(notChanged, i);

			double[] improvedArrs = arrayList2Double(improvedVals);
			double[] deprovedArrs = arrayList2Double(deprovedVals);
			double[] notImprovedArrs = arrayList2Double(notImprovedVals);

			ArrayList<double[]> listArrs = new ArrayList<double[]>();
			listArrs.add(improvedArrs);
			listArrs.add(deprovedArrs);
			listArrs.add(notImprovedArrs);

			System.out.println("p-value"
					+ ApacheStatAssist.onewayAnova(listArrs));

			double avg1 = 0;
			double avg2 = 0;
			double avg3 = 0;
			for (double a : improvedArrs) {
				avg1 += a;
			}

			for (double a : deprovedArrs) {
				avg2 += a;
			}

			for (double a : notImprovedArrs) {
				avg3 += a;
			}
			avg1 = avg1 / improvedArrs.length;
			avg2 = avg2 / deprovedArrs.length;
			avg3 = avg3 / notImprovedArrs.length;

			System.out.println("Improved:" + avg1);
			System.out.println("Deproved:" + avg2);
			System.out.println("Not improved:" + avg3);
			/*
			 * printDistribution(improvedArrs);
			 * printDistribution(notImprovedArrs);
			 * System.out.print("Improved vs. deImproved:"+
			 * ApacheStatAssist.unpairedTTest(improvedArrs, deprovedArrs));
			 * double avg1 = 0; for(double a: improvedArrs) { avg1 += a; } avg1
			 * = avg1/improvedArrs.length; double avg2 = 0; for(double a:
			 * deprovedArrs) { avg2 += a; } avg2 = avg2/deprovedArrs.length;
			 * System.out.println("\tImproved Avg:"+avg1
			 * +", Deproved Avg:"+avg2);
			 * System.out.print("Improved vs. notImproved:"+
			 * ApacheStatAssist.unpairedTTest(improvedArrs, notImprovedArrs));
			 * double avg3 = 0; for(double a: notImprovedArrs) { avg3 += a; }
			 * avg3 = avg3/notImprovedArrs.length;
			 * System.out.println("\tImproved Avg:"+avg1 +
			 * ", Not improved Avg:"+avg3);
			 * 
			 * System.out.print("Deproved vs. notImproved:"+
			 * ApacheStatAssist.unpairedTTest(deprovedArrs, notImprovedArrs));
			 * System.out.println("\tDeproved Avg:"+avg2 +
			 * ", Not improved Avg:"+avg3);
			 */
		}

	}

	public static void analyzeSpecificCategoryCorrelationRatio(
			ArrayList<RevisionDocument> docs) {
		// String[] improvedGroup = {"beachbabe", "honeybooboo",
		// "Maria_Destler", "pioneer3", "TuckerDidIt", "Vinvannob"};
		// String[] deprovedGroup = {"lively01", "RawDawg", "Taligate"};
		//
		// String[] improvedGroup2 = {"Nepa", "Mr",
		// "Psedu","franks","Jimmyyyyy"};
		// String[] deprovedGroup2 = {};

		// String[] improvedGroup = {"lightbulb", "MickJagger", "Taligate"};
		// String[] deprovedGroup = {"beachbabe", "honeybooboo", "pioneer",
		// "Raw","Rex", "TuckerDidIt"};
		//
		// String[] improvedGroup2 = {"scarlett", "Mr",
		// "petzl","Jimmyyyy","Jimmyyyyy"};
		// String[] deprovedGroup2 = {"Psedu", "franks", "Captain"};

		/* G1 */
		// String[] improvedGroup = {"honeybooboo", "lightbulb",
		// "Maria","Mick","Vivan"};
		// String[] deprovedGroup = {"lively01", "Rex"};
		//
		// String[] improvedGroup2 = {"Mr","Jimmyyyyy"};
		// String[] deprovedGroup2 = {"Definitely", "definately", "Dr",
		// "Captian", "Definitly"};

		/* G3 */
		// String[] improvedGroup = {"honeybooboo", "lightbulb","pioneer",
		// "Maria","Mick","Vivan"};
		// String[] deprovedGroup = {"lively01", "Power", "Smile",
		// "Rex","Taligate", "TheGreen"};
		//
		// String[] improvedGroup2 = {"Mr","Jimmyyyyy", "Nepa",
		// "Psedu","frankschickens"};
		// String[] deprovedGroup2 = {};

		/* G4 */
		String[] improvedGroup = { "honeybooboo", "lightbulb", "pioneer",
				"Maria", "Mick", "Vivan" };
		String[] deprovedGroup = { "lively01", "Power", "Smile", "Rex",
				"Taligate", "TheGreen" };

		String[] improvedGroup2 = { "Mr", "Jimmyyyyy", "Nepa", "Psedu",
				"frankschickens" };
		String[] deprovedGroup2 = { "Captian", "Definitely", "definately",
				"Definitly", "Dr" };

		ArrayList<RevisionDocument> improved = new ArrayList<RevisionDocument>();
		ArrayList<RevisionDocument> deproved = new ArrayList<RevisionDocument>();
		ArrayList<RevisionDocument> notChanged = new ArrayList<RevisionDocument>();

		for (RevisionDocument doc : docs) {
			if (isInArr(doc.getDocumentName(), improvedGroup)
					|| isInArr(doc.getDocumentName(), improvedGroup2)) {
				improved.add(doc);
			} else if (isInArr(doc.getDocumentName(), deprovedGroup)
					|| isInArr(doc.getDocumentName(), deprovedGroup2)) {
				deproved.add(doc);
			} else {
				notChanged.add(doc);
			}
		}
		// notChanged.addAll(deproved);
		for (int i = RevisionPurpose.START; i <= RevisionPurpose.WORDUSAGE_CLARITY; i++) {
			System.out.println(RevisionPurpose.getPurposeName(i) + ":");
			ArrayList<Double> improvedVals = getValsForEachDouble(improved, i);
			ArrayList<Double> deprovedVals = getValsForEachDouble(deproved, i);
			ArrayList<Double> notImprovedVals = getValsForEachDouble(
					notChanged, i);

			double[] improvedArrs = arrayList2DoubleRatio(improvedVals);
			double[] deprovedArrs = arrayList2DoubleRatio(deprovedVals);
			double[] notImprovedArrs = arrayList2DoubleRatio(notImprovedVals);

			double avg1 = 0;
			double avg2 = 0;
			double avg3 = 0;
			for (double a : improvedArrs) {
				avg1 += a;
			}

			for (double a : deprovedArrs) {
				avg2 += a;
			}

			for (double a : notImprovedArrs) {
				avg3 += a;
			}
			avg1 = avg1 / improvedArrs.length;
			avg2 = avg2 / deprovedArrs.length;
			avg3 = avg3 / notImprovedArrs.length;

			System.out.println("Improved:" + avg1);
			System.out.println("Deproved:" + avg2);
			System.out.println("Not improved:" + avg3);

			ArrayList<double[]> lists = new ArrayList<double[]>();
			lists.add(improvedArrs);
			lists.add(deprovedArrs);
			lists.add(notImprovedArrs);
			System.out
					.println("p-value:" + ApacheStatAssist.onewayAnova(lists));
			// printDistribution(improvedArrs);
			// printDistribution(notImprovedArrs);
			/*
			 * System.out.print("Improved vs. deImproved:"+
			 * ApacheStatAssist.unpairedTTest(improvedArrs, deprovedArrs));
			 * double avg1 = 0; for(double a: improvedArrs) { avg1 += a; } avg1
			 * = avg1/improvedArrs.length; double avg2 = 0; for(double a:
			 * deprovedArrs) { avg2 += a; } avg2 = avg2/deprovedArrs.length;
			 * System.out.println("\tImproved Avg:"+avg1
			 * +", Deproved Avg:"+avg2);
			 * System.out.print("Improved vs. notImproved:"+
			 * ApacheStatAssist.unpairedTTest(improvedArrs, notImprovedArrs));
			 * double avg3 = 0; for(double a: notImprovedArrs) { avg3 += a; }
			 * avg3 = avg3/notImprovedArrs.length;
			 * System.out.println("\tImproved Avg:"+avg1 +
			 * ", Not improved Avg:"+avg3);
			 * 
			 * System.out.print("Deproved vs. notImproved:"+
			 * ApacheStatAssist.unpairedTTest(deprovedArrs, notImprovedArrs));
			 * System.out.println("\tDeproved Avg:"+avg2 +
			 * ", Not improved Avg:"+avg3);
			 */
		}

	}

	public static void printCatgories(ArrayList<RevisionDocument> docs,
			int category) {
		System.out.println("Revision Purpose:"
				+ RevisionPurpose.getPurposeName(category));
		for (RevisionDocument doc : docs) {
			int total = 0;
			ArrayList<RevisionUnit> rus = doc.getRoot().getRevisionUnitAtLevel(
					0);
			for (RevisionUnit ru : rus) {
				if (ru.getRevision_purpose() == category) {
					total += 1;
				}
			}
			System.out.println(doc.getDocumentName() + ":" + total);
		}
	}

	public static double[] arrayList2Double(ArrayList<Integer> vals) {
		double[] valDs = new double[vals.size()];
		double total = 0;
		for (int i = 0; i < valDs.length; i++) {
			valDs[i] = vals.get(i) * 1.0;
			// total += vals.get(i);
		}
		return valDs;
	}

	public static double[] arrayList2DoubleRatio(ArrayList<Double> vals) {
		double[] valDs = new double[vals.size()];
		double total = 0;
		for (int i = 0; i < valDs.length; i++) {
			valDs[i] = vals.get(i);
			// total += vals.get(i);
		}
		return valDs;
	}

	public static void analyzePatternCorrelation(
			ArrayList<RevisionDocument> docs) {
		// String[] improvedGroup = {"beachbabe", "honeybooboo",
		// "Maria_Destler", "pioneer3", "TuckerDidIt", "Vinvannob"};
		// String[] deprovedGroup = {"lively01", "RawDawg", "Taligate"};
		//
		// String[] improvedGroup2 = {"Nepa", "Mr",
		// "Psedu","franks","Jimmyyyyy"};
		// String[] deprovedGroup2 = {};

		// String[] improvedGroup = {"lightbulb", "MickJagger", "Taligate"};
		// String[] deprovedGroup = {"beachbabe", "honeybooboo", "pioneer",
		// "Raw","Rex", "TuckerDidIt"};
		//
		// String[] improvedGroup2 = {"scarlett", "Mr",
		// "petzl","Jimmyyyy","Jimmyyyyy"};
		// String[] deprovedGroup2 = {"Psedu", "franks", "Captain"};

		// String[] improvedGroup = {"honeybooboo", "lightbulb",
		// "Maria","Mick","Vivan"};
		// String[] deprovedGroup = {"lively01", "Rex"};
		//
		// String[] improvedGroup2 = {"Mr","Jimmyyyyy"};
		// String[] deprovedGroup2 = {"Definitely", "definitely", "Dr",
		// "Captain", "Definitely"};

		/* G1 */
		// String[] improvedGroup = {"honeybooboo", "lightbulb",
		// "Maria","Mick","Vivan"};
		// String[] deprovedGroup = {"lively01", "Rex"};
		//
		// String[] improvedGroup2 = {"Mr","Jimmyyyyy"};
		// String[] deprovedGroup2 = {"Definitely", "definately", "Dr",
		// "Captian", "Definitly"};

		/* G2 */
		// String[] improvedGroup = {"lightbulb","Taligate","Mick"};
		// String[] deprovedGroup = {"honeybooboo","beachbabe", "pioneer",
		// "Raw","Rex","TuckerDidIt"};
		//
		// String[] improvedGroup2 = {"Mr","Jimmyyyyy", "petzl", "scarlett"};
		// String[] deprovedGroup2 = {"Captian", "frankschickens", "Psedu"};

		/* G3 */
		// String[] improvedGroup = {"honeybooboo", "lightbulb","pioneer",
		// "Maria","Mick","Vivan"};
		// String[] deprovedGroup = {"lively01", "Power", "Smile",
		// "Rex","Taligate", "TheGreen"};
		//
		// String[] improvedGroup2 = {"Mr","Jimmyyyyy", "Nepa",
		// "Psedu","frankschickens"};
		// String[] deprovedGroup2 = {};

		/* G4 */
		String[] improvedGroup = { "honeybooboo", "lightbulb", "pioneer",
				"Maria", "Mick", "Vivan" };
		String[] deprovedGroup = { "lively01", "Power", "Smile", "Rex",
				"Taligate", "TheGreen" };

		String[] improvedGroup2 = { "Mr", "Jimmyyyyy", "Nepa", "Psedu",
				"frankschickens" };
		String[] deprovedGroup2 = { "Captian", "Definitely", "definately",
				"Definitly", "Dr" };

		// stat all
		System.out.println("Statting all");
		ArrayList<RevisionDocument> improved = new ArrayList<RevisionDocument>();
		ArrayList<RevisionDocument> deproved = new ArrayList<RevisionDocument>();
		ArrayList<RevisionDocument> notChanged = new ArrayList<RevisionDocument>();

		for (RevisionDocument doc : docs) {
			if (isInArr(doc.getDocumentName(), improvedGroup)
					|| isInArr(doc.getDocumentName(), improvedGroup2)) {
				improved.add(doc);
			} else if (isInArr(doc.getDocumentName(), deprovedGroup)
					|| isInArr(doc.getDocumentName(), deprovedGroup2)) {
				deproved.add(doc);
			} else {
				notChanged.add(doc);
			}
		}

		double[] improvedDistribute = removeRebuttal(calculateCategoryDistribution(improved));
		double[] deprovedDistribute = removeRebuttal(calculateCategoryDistribution(deproved));
		double[] notChangedDistribute = removeRebuttal(calculateCategoryDistribution(notChanged));

		double[] improvedOpDistribute = removeRebuttal(calculateOpDistribution(improved));
		double[] deprovedOpDistribute = removeRebuttal(calculateOpDistribution(deproved));
		double[] notChangedOpDistribute = removeRebuttal(calculateOpDistribution(notChanged));

		notChanged.addAll(deproved);
		double[] notChangedDistribute2 = removeRebuttal(calculateCategoryDistribution(notChanged));
		double[] notChangedOpDistribute2 = removeRebuttal(calculateOpDistribution(notChanged));

		printPurposeLabels();
		System.out.println("IMPROVED");
		printDistribution(improvedDistribute);
		System.out.println("WORSE");
		printDistribution(deprovedDistribute);
		System.out.println("NOT CHANGED");
		printDistribution(notChangedDistribute);
		System.out.println("NOT IMRPOVED");
		printDistribution(notChangedDistribute2);
		// double r = ApacheStatAssist.pearsonCorrelation(improvedDistribute,
		// deprovedDistribute);
		double chi = ApacheStatAssist.chiSquareTest(improvedDistribute,
				deprovedDistribute);
		System.out.println("I vs. D,Corr: " + chi);
		chi = ApacheStatAssist.chiSquareTest(improvedDistribute,
				notChangedDistribute);
		System.out.println("I vs. N, Corr: " + chi);
		chi = ApacheStatAssist.chiSquareTest(deprovedDistribute,
				notChangedDistribute);
		System.out.println("D vs. N, Corr: " + chi);
		chi = ApacheStatAssist.chiSquareTest(improvedDistribute,
				notChangedDistribute2);
		System.out.println("I vs. DN, Corr: " + chi);
		// System.out.println("Significance: "+
		// ApacheStatAssist.getCorrSignificance(r, docs.size()));
		/*
		 * printOpLabels(); System.out.println("IMPROVED");
		 * printDistribution(improvedOpDistribute); System.out.println("WORSE");
		 * printDistribution(deprovedOpDistribute);
		 * System.out.println("NOT CHANGED");
		 * printDistribution(notChangedOpDistribute);
		 * System.out.println("NOT IMRPOVED");
		 * printDistribution(notChangedOpDistribute2); //double rOp =
		 * ApacheStatAssist.pearsonCorrelation(improvedOpDistribute,
		 * deprovedOpDistribute); chi =
		 * ApacheStatAssist.chiSquareTest(improvedOpDistribute,
		 * deprovedOpDistribute); System.out.println("Corr: "+ chi);
		 * //System.out.println("Significance: "+
		 * ApacheStatAssist.getCorrSignificance(rOp, docs.size()));
		 */
	}

	public static void printDistribution(double[] val) {
		double avg = 0;
		for (int i = 0; i < val.length; i++) {
			System.out.print(val[i] + "\t");
			avg += val[i];
		}

		System.out.println();
		System.out.println(avg / val.length);
	}

	public static void printPurposeLabels() {
		for (int i = RevisionPurpose.START; i <= RevisionPurpose.WORDUSAGE_CLARITY; i++) {
			System.out.print(RevisionPurpose.getPurposeName(i) + "\t");
		}
		System.out.println();
	}

	public static void printOpLabels() {
		for (int i = RevisionOp.START; i <= RevisionOp.MODIFY; i++) {
			System.out.print(RevisionOp.getOpName(i) + "\t");
		}
		System.out.println();
	}

	public static boolean isInArr(String s, String[] arr) {
		for (String item : arr) {
			if (s.contains(item)) {
				return true;
			}
		}
		return false;
	}

	public static double[] calculateCategoryDistribution(
			ArrayList<RevisionDocument> docs) {
		double[] distribution = new double[8];
		int total = 0;
		for (RevisionDocument doc : docs) {
			ArrayList<RevisionUnit> rus = doc.getRoot().getRevisionUnitAtLevel(
					0);
			for (RevisionUnit ru : rus) {
				distribution[ru.getRevision_purpose() - 1] += 1;
				total += 1;
			}
		}
		for (int i = 0; i < distribution.length; i++) {
			distribution[i] = distribution[i] * 1.0 / total;
		}
		return distribution;
	}

	public static double[] calculateOpDistribution(
			ArrayList<RevisionDocument> docs) {
		double[] distribution = new double[3];
		int total = 0;
		for (RevisionDocument doc : docs) {
			ArrayList<RevisionUnit> rus = doc.getRoot().getRevisionUnitAtLevel(
					0);
			for (RevisionUnit ru : rus) {
				distribution[ru.getRevision_op() - 1] += 1;
				total += 1;
			}
		}
		for (int i = 0; i < distribution.length; i++) {
			distribution[i] = distribution[i] * 1.0 / total;
		}
		return distribution;
	}

	public static double[] removeRebuttal(double[] arr) {
		double[] modified = new double[arr.length - 1];
		for (int i = 0; i < arr.length; i++) {
			if (i + 1 < RevisionPurpose.CD_REBUTTAL_RESERVATION) {
				modified[i] = arr[i];
			} else if (i + 1 == RevisionPurpose.CD_REBUTTAL_RESERVATION) {

			} else {
				modified[i - 1] = arr[i];
			}
		}
		return modified;
	}

	public static ArrayList<Integer> getValsForEach(
			ArrayList<RevisionDocument> docs, int revPurpose) {
		ArrayList<Integer> vals = new ArrayList<Integer>();
		for (RevisionDocument doc : docs) {
			int val = 0;
			ArrayList<RevisionUnit> rus = doc.getRoot().getRevisionUnitAtLevel(
					0);
			for (RevisionUnit ru : rus) {
				if (ru.getRevision_purpose() == revPurpose) {
					// if(ru.getRevision_purpose()>=RevisionPurpose.START&&ru.getRevision_purpose()<=RevisionPurpose.EVIDENCE)
					// {
					val++;
				} else {
					// val++;
				}
			}
			vals.add(val);
		}
		return vals;
	}

	public static ArrayList<Double> getValsForEachDouble(
			ArrayList<RevisionDocument> docs, int revPurpose) {
		ArrayList<Double> vals = new ArrayList<Double>();
		for (RevisionDocument doc : docs) {
			int val = 0;
			int total = 0;
			ArrayList<RevisionUnit> rus = doc.getRoot().getRevisionUnitAtLevel(
					0);
			for (RevisionUnit ru : rus) {
				// if(ru.getRevision_purpose() == revPurpose) {
				if (ru.getRevision_purpose() >= RevisionPurpose.START
						&& ru.getRevision_purpose() <= RevisionPurpose.EVIDENCE) {
					val++;
				} else {
					// val++;
				}
				total++;
			}
			double tmpVal = val * 1.0 / total;
			vals.add(tmpVal);
		}
		return vals;
	}
}
