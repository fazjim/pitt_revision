package edu.pitt.lrdc.cs.revision.evaluate;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import edu.pitt.cs.revision.purpose.PDTBEntityGrid;
import edu.pitt.cs.revision.purpose.PDTBFeatureExtractorV4;
import edu.pitt.cs.revision.purpose.PDTBGraph;
import edu.pitt.cs.revision.purpose.PDTBRelation;
import edu.pitt.cs.revision.purpose.PDTBTree;
import edu.pitt.cs.revision.purpose.pdtb.ManualParseResultFile;
import edu.pitt.cs.revision.purpose.pdtb.ManualParseResultReader;
import edu.pitt.cs.revision.purpose.pdtb.ModificationRemover;
import edu.pitt.cs.revision.purpose.pdtb.ParseResultFile;
import edu.pitt.cs.revision.purpose.pdtb.ParseResultReader;
import edu.pitt.cs.revision.purpose.pdtb.PipeAttribute;
import edu.pitt.cs.revision.purpose.pdtb.PipeUnit;
import edu.pitt.lrdc.cs.revision.io.RevisionDocumentReader;
import edu.pitt.lrdc.cs.revision.model.RevisionDocument;

public class TreeFValueCalculator {
	public static String entRelType = "EntRel";
	public static String altLexType = "AltLex";
	public static String explicitRelType = "Explicit";
	public static String implicitRelType = "Implicit";
	public static String comparisonType = "Comparison";
	public static String contingencyType = "Contingency";
	public static String temporalType = "Temporal";
	public static String expansionType = "Expansion";

	public static int IND_entRelType = 1;
	public static int IND_comparisonType_EXP = 2;
	public static int IND_comparisonType_IMP = 3;
	public static int IND_contingencyType_EXP = 4;
	public static int IND_contingencyType_IMP = 5;
	public static int IND_temporalType_EXP = 6;
	public static int IND_temporalType_IMP = 7;
	public static int IND_expansionType_EXP = 8;
	public static int IND_expansionType_IMP = 9;
	public static int IND_altLexType = 10;

	public static void main(String[] args) throws Exception {
		TreeFValueCalculator tfc = new TreeFValueCalculator();
		String docPath = "C:\\Not Backed Up\\data\\naaclData\\C1";
		ArrayList<RevisionDocument> docs = RevisionDocumentReader
				.readDocs(docPath);
		String autoPath = "C:\\Not Backed Up\\discourse_parse_results\\litman_corpus\\Braverman";
		String manualPath = "C:\\Not Backed Up\\discourse_parse_results\\manual2";
		Hashtable<String, PDTBTree> manualTreeTable = tfc.readManual(
				manualPath, docs);
		Hashtable<String, PDTBTree> autoTreeTable = tfc
				.readAuto(autoPath, docs);

		System.out.println("Lvl1: "
				+ tfc.countFValue(manualTreeTable, autoTreeTable, 1));
		System.out.println("Lvl2: "
				+ tfc.countFValue(manualTreeTable, autoTreeTable, 2));
		System.out.println("Lvl3: "
				+ tfc.countFValue(manualTreeTable, autoTreeTable, 3));
		System.out.println("Lvl4: "
				+ tfc.countFValue(manualTreeTable, autoTreeTable, 4));
	}

	public String getRealNameTxt(String fileName) {
		File f = new File(fileName);
		String fName = f.getName();
		int index = fName.indexOf(".txt");
		fName = fName.substring(0, index);
		if (fName.contains("-")) {
			fName = fName.substring(0, fName.indexOf("-"));
		}
		fName = fName.replaceAll("_", " ");
		return fName.trim();
	}

	public String getRealNamePipe(String fileName) {
		File f = new File(fileName);
		String fName = f.getName();
		fName = fName.replace("Annotation_", "");
		int index = fName.indexOf(".txt");
		fName = fName.substring(0, index);
		if (fName.contains("-")) {
			fName = fName.substring(0, fName.indexOf("-"));
		}
		fName = fName.replaceAll("_", " ");
		fName = fName.replaceAll(" ", "");
		fName = fName.toLowerCase();
		return fName.trim();
	}

	public String getRealNameRevision(String fileName) {
		File f = new File(fileName);
		String fName = f.getName();
		if (fName.contains(".txt")) {
			int index = fName.indexOf(".txt");
			fName = fName.substring(0, index);
		}
		if (fName.contains(".xlsx")) {
			int index = fName.indexOf(".xlsx");
			fName = fName.substring(0, index);
		}
		if (fName.contains("Annotation_")) {
			String annotationStr = "Annotation_";
			int index = fName.indexOf(annotationStr) + annotationStr.length();
			fName = fName.substring(index);
		}
		if (fName.contains(" - ")) {
			int index = fName.indexOf("-");
			fName = fName.substring(0, index).trim();
		}

		String[] strToTrim = { "Fan", "Christian", "Fian" };
		for (String str : strToTrim) {
			if (fName.endsWith(str)) {
				fName = fName.substring(0, fName.indexOf(str));
			}
		}
		fName = fName.replaceAll("_", " ");
		fName = fName.replaceAll(" ", "");
		fName = fName.toLowerCase();
		return fName.trim();
	}

	public Hashtable<String, PDTBTree> readManual(String path,
			ArrayList<RevisionDocument> docs) throws IOException {
		List<ManualParseResultFile> results = ManualParseResultReader
				.readFiles(path);
		String refPath = "C:\\Not Backed Up\\discourse_parse_results\\litman_corpus\\Braverman\\Braverman_raw_txt";

		Hashtable<String, PDTBTree> treeTable = new Hashtable<String, PDTBTree>();

		Hashtable<String, ManualParseResultFile> resultMap_OLD = new Hashtable<String, ManualParseResultFile>();
		Hashtable<String, ManualParseResultFile> resultMap_NEW = new Hashtable<String, ManualParseResultFile>();

		for (ManualParseResultFile result : results) {
			if (result.isPDTB1()) {
				ModificationRemover.feedTxtInfo(result, refPath);
				String fileName = result.getFileName();
				if (fileName.contains("draft1")) {
					fileName = getRealNamePipe(fileName);
					resultMap_OLD.put(fileName, result);
				} else if (fileName.contains("draft2")) {
					fileName = getRealNamePipe(fileName);
					resultMap_NEW.put(fileName, result);
				}
			}
		}

		for (RevisionDocument doc : docs) {
			String name = getRealNameRevision(doc.getDocumentName());
			ManualParseResultFile oldFile = resultMap_OLD.get(name);
			ManualParseResultFile newFile = resultMap_NEW.get(name);

			setInfoManual(doc, oldFile, doc.getOldDraftSentences());
			setInfoManual(doc, newFile, doc.getNewDraftSentences());

			Hashtable<Integer, PDTBTree> treeOld = new Hashtable<Integer, PDTBTree>();
			Hashtable<Integer, PDTBTree> treeNew = new Hashtable<Integer, PDTBTree>();
			readTreeManual(doc, oldFile, treeOld, true);
			readTreeManual(doc, newFile, treeNew, false);

			Iterator<Integer> treeOldIter = treeOld.keySet().iterator();
			while (treeOldIter.hasNext()) {
				int paragraphNum = treeOldIter.next();
				String key = "OLD_" + name + "-" + paragraphNum;
				treeTable.put(key, treeOld.get(paragraphNum));
			}

			Iterator<Integer> treeNewIter = treeNew.keySet().iterator();
			while (treeNewIter.hasNext()) {
				int paragraphNum = treeNewIter.next();
				String key = "NEW_" + name + "-" + paragraphNum;
				treeTable.put(key, treeNew.get(paragraphNum));
			}
		}
		return treeTable;
	}

	public Hashtable<String, PDTBTree> readAuto(String path,
			ArrayList<RevisionDocument> docs) throws IOException {
		List<ParseResultFile> results = ParseResultReader.readFiles(path);
		String refPath = "C:\\Not Backed Up\\discourse_parse_results\\litman_corpus\\Braverman\\Braverman_raw_txt";
		Hashtable<String, PDTBTree> treeTable = new Hashtable<String, PDTBTree>();

		Hashtable<String, ParseResultFile> resultMap_OLD = new Hashtable<String, ParseResultFile>();
		Hashtable<String, ParseResultFile> resultMap_NEW = new Hashtable<String, ParseResultFile>();

		for (ParseResultFile result : results) {
			if (result.isPDTB1()) {
				ModificationRemover.feedTxtInfo(result, refPath);
				String fileName = result.getFileName();
				if (fileName.contains("draft1")) {
					fileName = getRealNamePipe(fileName);
					resultMap_OLD.put(fileName, result);
				} else if (fileName.contains("draft2")) {
					fileName = getRealNamePipe(fileName);
					resultMap_NEW.put(fileName, result);
				}
			}
		}

		for (RevisionDocument doc : docs) {
			String name = getRealNameRevision(doc.getDocumentName());
			ParseResultFile oldFile = resultMap_OLD.get(name);
			ParseResultFile newFile = resultMap_NEW.get(name);
			setInfoAuto(doc, oldFile, doc.getOldDraftSentences());
			setInfoAuto(doc, newFile, doc.getNewDraftSentences());

			Hashtable<Integer, PDTBTree> treeOld = new Hashtable<Integer, PDTBTree>();
			Hashtable<Integer, PDTBTree> treeNew = new Hashtable<Integer, PDTBTree>();

			readTree(doc, oldFile, treeOld, true);
			readTree(doc, newFile, treeNew, false);

			Iterator<Integer> treeOldIter = treeOld.keySet().iterator();
			while (treeOldIter.hasNext()) {
				int paragraphNum = treeOldIter.next();
				String key = "OLD_" + name + "-" + paragraphNum;
				treeTable.put(key, treeOld.get(paragraphNum));
			}

			Iterator<Integer> treeNewIter = treeNew.keySet().iterator();
			while (treeNewIter.hasNext()) {
				int paragraphNum = treeNewIter.next();
				String key = "NEW_" + name + "-" + paragraphNum;
				treeTable.put(key, treeNew.get(paragraphNum));
			}
		}
		return treeTable;
	}

	public String compressStr(String str) {
		/*
		 * StringBuffer newStr = new StringBuffer(); for (int i = 0; i <
		 * str.length(); i++) { char tmp = str.charAt(i); if
		 * (Character.isLetterOrDigit(tmp)) { newStr.append(tmp); } } return
		 * newStr.toString();
		 */
		str = str.replaceAll("[^a-zA-Z]", "");
		str = str.replaceAll(" ", "");
		return str;
	}

	public int getRealArgNo(Hashtable<String, Integer> lineMap, String searchStr) {
		Iterator<String> it = lineMap.keySet().iterator();
		while (it.hasNext()) {
			String line = it.next();
			// Arg1 matches the search line
			if (line.equals(searchStr) || line.contains(searchStr)
					|| searchStr.contains(line)) {
				return lineMap.get(line);
			}
		}
		return -1;
	}

	public void setInfoAuto(RevisionDocument doc, ParseResultFile file,
			ArrayList<String> sentences) {
		// Map of lines
		Hashtable<String, Integer> lineMap = new Hashtable<String, Integer>();
		// Map the index of pdtb and old and new sentences in revision document
		Hashtable<Integer, Integer> pdtb_line_map = new Hashtable<Integer, Integer>();
		String name = getRealNameRevision(doc.getDocumentName());

		for (int i = 0; i < sentences.size(); i++) {
			lineMap.put(compressStr(sentences.get(i)), i + 1);
		}
		List<PipeUnit> pipes = file.getPipes();
		for (PipeUnit pipe : pipes) {
			/*
			 * int relationType = PipeAttribute.RELATION_TYPE; int semClassType
			 * = PipeAttribute.FIRST_SEMCLASS_CONN; String relation =
			 * pipe.getAttr(relationType); String semClass =
			 * pipe.getAttr(semClassType);
			 */
			String relation = pipe.getElementType();
			String semClass = pipe.getManualRelationType();

			// Get the argument type
			int argType = -1;
			if (relation.equals(entRelType)) {
				argType = IND_entRelType;
			} else if (relation.equals(altLexType)) {
				argType = IND_altLexType;
			} else {
				if (relation.equals(explicitRelType)) {
					if (semClass.equals(comparisonType)) {
						argType = IND_comparisonType_EXP;
					} else if (semClass.equals(contingencyType)) {
						argType = IND_contingencyType_EXP;
					} else if (semClass.equals(temporalType)) {
						argType = IND_temporalType_EXP;
					} else if (semClass.equals(expansionType)) {
						argType = IND_expansionType_EXP;
					}
				} else {
					if (semClass.equals(comparisonType)) {
						argType = IND_comparisonType_IMP;
					} else if (semClass.equals(contingencyType)) {
						argType = IND_contingencyType_IMP;
					} else if (semClass.equals(temporalType)) {
						argType = IND_temporalType_IMP;
					} else if (semClass.equals(expansionType)) {
						argType = IND_expansionType_IMP;
					}
				}
			}

			// Get the index
			int arg1IndexIND = PipeAttribute.ARG1_GORNADDRESS;
			int arg1RawTextIND = PipeAttribute.ARG1_RAWTEXT;
			int arg2IndexIND = PipeAttribute.ARG2_GORNADDRESS;
			int arg2RawTextIND = PipeAttribute.ARG2_RAWTEXT;

			int arg1SpanListIND = PipeAttribute.ARG1_SPANLIST;
			int arg2SpanListIND = PipeAttribute.ARG2_SPANLIST;

			// Get the real line number for arg1 and arg2
			int realLineArg1No = -1;
			int realLineArg2No = -1;

			String arg1RawText = pipe.getRange1TxtAuto();
			String arg2RawText = pipe.getRange2TxtAuto();

			String searchStrArg1 = compressStr(arg1RawText);
			String searchStrArg2 = compressStr(arg2RawText);

			realLineArg1No = getRealArgNo(lineMap, searchStrArg1);
			realLineArg2No = getRealArgNo(lineMap, searchStrArg2);

			pipe.setArg1SentenceIndex(realLineArg1No);
			pipe.setArg2SentenceIndex(realLineArg2No);
		}

		// Setting up things, can build the graph now
	}

	public void setInfoManual(RevisionDocument doc, ManualParseResultFile file,
			ArrayList<String> sentences) {
		// Map of lines
		Hashtable<String, Integer> lineMap = new Hashtable<String, Integer>();
		// Map the index of pdtb and old and new sentences in revision document
		Hashtable<Integer, Integer> pdtb_line_map = new Hashtable<Integer, Integer>();
		String name = getRealNameRevision(doc.getDocumentName());

		for (int i = 0; i < sentences.size(); i++) {
			lineMap.put(compressStr(sentences.get(i)), i + 1);
		}
		List<PipeUnit> pipes = file.getPipes();
		for (PipeUnit pipe : pipes) {
			/*
			 * int relationType = PipeAttribute.RELATION_TYPE; int semClassType
			 * = PipeAttribute.FIRST_SEMCLASS_CONN; String relation =
			 * pipe.getAttr(relationType); String semClass =
			 * pipe.getAttr(semClassType);
			 */
			String relation = pipe.getElementType();
			String semClass = pipe.getManualRelationType();

			// Get the argument type
			int argType = -1;
			if (relation.equals(entRelType)) {
				argType = IND_entRelType;
			} else if (relation.equals(altLexType)) {
				argType = IND_altLexType;
			} else {
				if (relation.equals(explicitRelType)) {
					if (semClass.equals(comparisonType)) {
						argType = IND_comparisonType_EXP;
					} else if (semClass.equals(contingencyType)) {
						argType = IND_contingencyType_EXP;
					} else if (semClass.equals(temporalType)) {
						argType = IND_temporalType_EXP;
					} else if (semClass.equals(expansionType)) {
						argType = IND_expansionType_EXP;
					}
				} else {
					if (semClass.equals(comparisonType)) {
						argType = IND_comparisonType_IMP;
					} else if (semClass.equals(contingencyType)) {
						argType = IND_contingencyType_IMP;
					} else if (semClass.equals(temporalType)) {
						argType = IND_temporalType_IMP;
					} else if (semClass.equals(expansionType)) {
						argType = IND_expansionType_IMP;
					}
				}
			}

			// Get the index
			int arg1IndexIND = PipeAttribute.ARG1_GORNADDRESS;
			int arg1RawTextIND = PipeAttribute.ARG1_RAWTEXT;
			int arg2IndexIND = PipeAttribute.ARG2_GORNADDRESS;
			int arg2RawTextIND = PipeAttribute.ARG2_RAWTEXT;

			int arg1SpanListIND = PipeAttribute.ARG1_SPANLIST;
			int arg2SpanListIND = PipeAttribute.ARG2_SPANLIST;

			// Get the real line number for arg1 and arg2
			int realLineArg1No = -1;
			int realLineArg2No = -1;

			String arg1RawText = pipe.getRange1Txt();
			String arg2RawText = pipe.getRange2Txt();

			String searchStrArg1 = compressStr(arg1RawText);
			String searchStrArg2 = compressStr(arg2RawText);

			realLineArg1No = getRealArgNo(lineMap, searchStrArg1);
			realLineArg2No = getRealArgNo(lineMap, searchStrArg2);

			pipe.setArg1SentenceIndex(realLineArg1No);
			pipe.setArg2SentenceIndex(realLineArg2No);
		}

		// Setting up things, can build the graph now
	}

	public void readTreeManual(RevisionDocument doc,
			ManualParseResultFile parseFile,
			Hashtable<Integer, PDTBTree> treeIndex, boolean isOld)
			throws IOException {
		int paragraphNums = 0;
		if (isOld) {
			paragraphNums = doc.getOldParagraphNum();
		} else {
			paragraphNums = doc.getNewParagraphNum();
		}

		List<PipeUnit> allUnits = parseFile.getPipes();
		for (int i = 1; i <= paragraphNums; i++) {
			int firstIndex = 0;
			int lastIndex = 0;
			if (isOld) {
				firstIndex = doc.getFirstOfOldParagraph(i);
				lastIndex = doc.getLastOfOldParagraph(i);
			} else {
				firstIndex = doc.getFirstOfNewParagraph(i);
				lastIndex = doc.getLastOfNewParagraph(i);
			}
			PDTBTree tree = new PDTBTree();

			List<PipeUnit> units = new ArrayList<PipeUnit>();
			List<Integer> sentences = new ArrayList<Integer>();
			for (int j = firstIndex; j <= lastIndex; j++) {
				sentences.add(j);
			}
			for (PipeUnit unit : allUnits) {
				if (unit.getArg1SentenceIndex() >= firstIndex
						&& unit.getArg2SentenceIndex() <= lastIndex) {
					units.add(unit);
				}
			}
			// graph.setStartSentenceIndex(firstIndex);
			// graph.setEndSentenceIndex(lastIndex);
			tree.buildTree(units, doc, isOld, sentences);
			treeIndex.put(i, tree);
		}
	}

	public void readTree(RevisionDocument doc, ParseResultFile parseFile,
			Hashtable<Integer, PDTBTree> treeIndex, boolean isOld)
			throws IOException {
		int paragraphNums = 0;
		if (isOld) {
			paragraphNums = doc.getOldParagraphNum();
		} else {
			paragraphNums = doc.getNewParagraphNum();
		}

		List<PipeUnit> allUnits = parseFile.getPipes();
		for (int i = 1; i <= paragraphNums; i++) {
			int firstIndex = 0;
			int lastIndex = 0;
			if (isOld) {
				firstIndex = doc.getFirstOfOldParagraph(i);
				lastIndex = doc.getLastOfOldParagraph(i);
			} else {
				firstIndex = doc.getFirstOfNewParagraph(i);
				lastIndex = doc.getLastOfNewParagraph(i);
			}
			PDTBTree tree = new PDTBTree();

			List<PipeUnit> units = new ArrayList<PipeUnit>();
			List<Integer> sentences = new ArrayList<Integer>();
			for (int j = firstIndex; j <= lastIndex; j++) {
				sentences.add(j);
			}
			for (PipeUnit unit : allUnits) {
				if (unit.getArg1SentenceIndex() >= firstIndex
						&& unit.getArg2SentenceIndex() <= lastIndex) {
					units.add(unit);
				}
			}
			// graph.setStartSentenceIndex(firstIndex);
			// graph.setEndSentenceIndex(lastIndex);
			tree.buildTree(units, doc, isOld, sentences);
			treeIndex.put(i, tree);
		}
	}

	public int getIndex(String type, String sense) {
		if (type.equals("EntRel")) {
			return 0;
		} else if (type.equals("AlLex")) {
			return 1;
		} else if (type.equals("NoRel") || sense.equals("NoRel")) {
			return -1;
		} else if (sense.equals("Comparison")) {
			return 2;
		} else if (sense.equals("Contingency")) {
			return 3;
		} else if (sense.equals("Expansion")) {
			return 4;
		} else if (sense.equals("Temporal")) {
			return 5;
		}
		return -1;
	}

	public double calcF(int[][] table) {
		int size = table.length;
		double[] precs = new double[size];
		double[] recalls = new double[size];
		double allCorrect = 0;
		double allAuto = 0;
		int total = size;
		for (int i = 0; i < size; i++) {
			double correct = table[i][i];
			allCorrect += correct;
			int allManuals = 0;
			int allAutos = 0;
			for (int j = 0; j < size; j++) {
				allManuals += table[i][j];
				allAutos += table[j][i];
				allAuto += table[j][i];
			}
			if (allAutos == 0 || allManuals == 0) {
				precs[i] = 0;
				recalls[i] = 0;
				total--;
			} else {
				precs[i] = correct / allAutos;
				recalls[i] = correct / allManuals;
			}
		}
		double avgF = 0;
		for (int i = 0; i < size; i++) {
			double p = precs[i];
			double r = recalls[i];
			if (p != 0 || r != 0) {
				double f = 2 * p * r / (p + r);
				avgF += f;
			}
		}
		avgF = avgF / total;
		
		
		return allCorrect/allAuto;
		//return avgF;
	}

	public double countFValue(Hashtable<String, PDTBTree> manualTreeTable,
			Hashtable<String, PDTBTree> autoTreeTable, int level) {
		int[][] table = new int[7][7];
		// EntRel, AltLex, NoRel, Comparision, Contingency, Expansion, Temporal
		Iterator<String> it = manualTreeTable.keySet().iterator();
		int allCounts = 0;
		while (it.hasNext()) {
			String key = it.next();
			PDTBTree manualTree = manualTreeTable.get(key);
			PDTBTree autoTree = autoTreeTable.get(key);

			HashSet<PDTBRelation> autoRelations = autoTree
					.getTopLevelRelations(level);
			allCounts += autoRelations.size();
			for (PDTBRelation autoRelation : autoRelations) {
				if (autoRelation == null) {
					System.err.println("What happened???" + level);
				} else {
					if (autoRelation.getPreIndex() != autoRelation
							.getPostIndex()) {
						String relationKey = autoRelation.getPreIndex() + "-"
								+ autoRelation.getPostIndex();
						if (manualTree.getRelationIndex().containsKey(
								relationKey)) {
							PDTBRelation manualRelation = manualTree
									.getRelationIndex().get(relationKey);
							String elementType = manualRelation
									.getElementType();
							String sense = manualRelation.getSense();

							int manualIndex = getIndex(elementType, sense);

							String autoType = autoRelation.getElementType();
							String autoSense = autoRelation.getSense();

							int autoIndex = getIndex(autoType, autoSense);
							if (manualIndex != -1 && autoIndex != -1)
								table[manualIndex][autoIndex]++;
						}
					}
				}
			}
		}
		System.out.println("Number of relations:" + allCounts);
		return calcF(table);
	}

}
