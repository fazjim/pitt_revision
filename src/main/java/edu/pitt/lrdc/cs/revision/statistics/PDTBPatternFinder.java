package edu.pitt.lrdc.cs.revision.statistics;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import edu.pitt.cs.revision.machinelearning.FeatureName;
import edu.pitt.cs.revision.purpose.pdtb.ManualParseResultFile;
import edu.pitt.cs.revision.purpose.pdtb.ManualParseResultReader;
import edu.pitt.cs.revision.purpose.pdtb.ModificationRemover;
import edu.pitt.cs.revision.purpose.pdtb.PipeAttribute;
import edu.pitt.cs.revision.purpose.pdtb.PipeUnit;
import edu.pitt.lrdc.cs.revision.io.RevisionDocumentReader;
import edu.pitt.lrdc.cs.revision.model.RevisionDocument;
import edu.pitt.lrdc.cs.revision.model.RevisionPurpose;
import edu.pitt.lrdc.cs.revision.model.RevisionUnit;

public class PDTBPatternFinder {
	public static String entRelType = "EntRel";
	public static String explicitRelType = "Explicit";
	public static String implicitRelType = "Implicit";
	public static String comparisonType = "Comparison";
	public static String contingencyType = "Contingency";
	public static String temporalType = "Temporal";
	public static String expansionType = "Expansion";
	public static String altLexType = "AltLex";

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

	Hashtable<String, Integer> patternCounts = new Hashtable<String, Integer>();
	Hashtable<String, Integer> bindingCounts = new Hashtable<String, Integer>();

	Hashtable<String, Integer> patternCountsNew = new Hashtable<String, Integer>();
	Hashtable<String, Integer> bindingCountsNew = new Hashtable<String, Integer>();

	Hashtable<String, Integer> changeBinding = new Hashtable<String, Integer>();

	public void addCount(Hashtable<String, Integer> table, String value) {
		int count = 0;
		if (table.containsKey(value)) {
			count = table.get(value);
		}
		count++;
		table.put(value, count);
	}

	Hashtable<String, ManualParseResultFile> resultMap_OLD;
	Hashtable<String, ManualParseResultFile> resultMap_NEW;

	Hashtable<String, Hashtable<Integer, Integer>> pdtbArg1Results_OLD;
	Hashtable<String, Hashtable<Integer, Integer>> pdtbArg1Results_NEW;
	Hashtable<String, Hashtable<Integer, Integer>> pdtbArg2Results_OLD;
	Hashtable<String, Hashtable<Integer, Integer>> pdtbArg2Results_NEW;

	Hashtable<String, String> oldTextTable;
	Hashtable<String, String> newTextTable;

	public static void main(String[] args) throws Exception {
		PDTBPatternFinder finder = new PDTBPatternFinder();
		String path = "C:\\Not Backed Up\\discourse_parse_results\\manual2";
		String revisionDocumentPath = "C:\\Not Backed Up\\data\\naaclData\\C1";
		finder.initialize(path, revisionDocumentPath);
		finder.printPatterns();
	}

	public void extractPatterns(RevisionDocument doc) {
		ArrayList<RevisionUnit> units = doc.getRoot().getRevisionUnitAtLevel(0);
		System.err.println("_________" + doc.getDocumentName() + "___________");
		for (RevisionUnit unit : units) {
			String revPurpose = RevisionPurpose.getPurposeName(unit
					.getRevision_purpose());
			String[] patterns = extractPattern(doc, unit.getNewSentenceIndex(),
					unit.getOldSentenceIndex(), 3);
			addCount(patternCounts, patterns[0]);
			addCount(patternCountsNew, patterns[1]);
			addCount(bindingCounts, revPurpose + ":" + patterns[0]);
			addCount(bindingCountsNew, revPurpose + ":" + patterns[1]);
			addCount(changeBinding, revPurpose + ":" + patterns[0] + ":"
					+ patterns[1]);
		}
	}

	public void readAll(String root) throws IOException {
		File rootFile = new File(root);
		Stack<File> fileStack = new Stack<File>();
		fileStack.push(rootFile);
		while (!fileStack.isEmpty()) {
			File pop = fileStack.pop();
			if (pop.isDirectory()) {
				File[] subs = pop.listFiles();
				for (File sub : subs) {
					fileStack.push(sub);
				}
			} else {
				String fileName = pop.getAbsolutePath();
				if (fileName.endsWith(".txt")) {
					if (fileName.contains("draft1")) {
						readTxt(fileName, oldTextTable);
					} else if (fileName.contains("draft2")) {
						readTxt(fileName, newTextTable);
					}
				}
			}
		}
	}

	public String getArgName(RevisionDocument doc, boolean isOld, int index,
			int indent, Hashtable<Integer, Integer> pdtbArgs,
			Hashtable<Integer, Integer> pdtbArgs2) {
		String name = "NULL";
		int testIndex = index;
		int paraNo = -1;
		int currentParaNo = -1;

		int sentenceNum = 0;
		if (isOld) {
			sentenceNum = doc.getOldDraftSentences().size();
		} else {
			sentenceNum = doc.getNewDraftSentences().size();
		}

		if (testIndex < 1 || testIndex > sentenceNum) {
			return name;
		}

		if (isOld) {
			paraNo = doc.getParaNoOfOldSentence(testIndex);
			currentParaNo = doc.getParaNoOfOldSentence(indent);
		} else {
			paraNo = doc.getParaNoOfNewSentence(testIndex);
			currentParaNo = doc.getParaNoOfNewSentence(indent);
		}

		if (paraNo != currentParaNo) {
			System.err.println(paraNo);
			System.err.println(currentParaNo);
			return name;
		}
		if (!pdtbArgs.containsKey(testIndex)) {
			if (pdtbArgs2.containsKey(testIndex - 1))
				name = getTypeStr(pdtbArgs2.get(testIndex - 1));
			return name;
		} else {
			int tag = pdtbArgs.get(testIndex);
			name = getTypeStr(tag);
			return name;
		}
	}

	public String getTypeStr(int tag) {
		String name = "NULL";
		if (tag == IND_entRelType) {
			name = "EntRel";
		} else if (tag == IND_comparisonType_EXP
				|| tag == IND_comparisonType_IMP) {
			name = "Comparison";
		} else if (tag == IND_contingencyType_EXP
				|| tag == IND_contingencyType_IMP) {
			name = "Contingency";
		} else if (tag == IND_expansionType_EXP || tag == IND_expansionType_IMP) {
			name = "Expansion";
		} else if (tag == IND_temporalType_EXP || tag == IND_temporalType_IMP) {
			name = "Temporal";
		} else if (tag == IND_altLexType) {
			name = "AltLex";
		}
		return name;
	}

	public void readTxt(String fileName, Hashtable<String, String> table)
			throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(fileName));
		String txt = "";
		String line = reader.readLine();
		while (line != null) {
			txt += line + "\n";
			line = reader.readLine();
		}
		table.put(getRealNameTxt(fileName), txt);
		reader.close();
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

	/**
	 * Compressing all the string for comparison (PDTB ones has been tokenized
	 * so compress them for locating)
	 * 
	 * only keep the characters
	 * 
	 * @param str
	 * @return
	 */
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

	/**
	 * 
	 * @param doc
	 * @param resultMap
	 * @param sentences
	 * @param pdtbArg1
	 *            key: sentence index, value: argument type
	 * @param pdtbArg2
	 */
	public void readInfo(RevisionDocument doc,
			Hashtable<String, ManualParseResultFile> resultMap,
			ArrayList<String> sentences, Hashtable<Integer, Integer> pdtbArg1,
			Hashtable<Integer, Integer> pdtbArg2,
			Hashtable<String, String> txtTable) {
		// Map of lines
		Hashtable<String, Integer> lineMap = new Hashtable<String, Integer>();
		// Map the index of pdtb and old and new sentences in revision document
		Hashtable<Integer, Integer> pdtb_line_map = new Hashtable<Integer, Integer>();
		String name = getRealNameRevision(doc.getDocumentName());
		String text = txtTable.get(name);

		for (int i = 0; i < sentences.size(); i++) {
			lineMap.put(compressStr(sentences.get(i)), i + 1);
		}

		ManualParseResultFile file = resultMap.get(name);
		if (file == null) {
			System.err.println(name);
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
			/*
			 * String arg1Val = pipe.getAttr(arg1IndexIND); String arg1RawText =
			 * pipe.getAttr(arg1RawTextIND); String arg2Val =
			 * pipe.getAttr(arg2IndexIND); String arg2RawText =
			 * pipe.getAttr(arg2RawTextIND);
			 * 
			 * String arg1Span = pipe.getAttr(arg1SpanListIND); String arg2Span
			 * = pipe.getAttr(arg2SpanListIND);
			 * 
			 * int arg1LineNo = Integer.parseInt(arg1Val); int arg2LineNo =
			 * Integer.parseInt(arg2Val);
			 */

			// Get the real line number for arg1 and arg2
			int realLineArg1No = -1;
			int realLineArg2No = -1;

			String arg1RawText = pipe.getRange1Txt();
			String arg2RawText = pipe.getRange2Txt();
			/*
			 * if (!pdtb_line_map.containsKey(arg1LineNo)) { String
			 * searchStrArg1 = compressStr(arg1RawText); realLineArg1No =
			 * getRealArgNo(lineMap, searchStrArg1, arg1LineNo, text, arg1Span);
			 * pdtb_line_map.put(arg1LineNo, realLineArg1No); } realLineArg1No =
			 * pdtb_line_map.get(arg1LineNo);
			 */

			String searchStrArg1 = compressStr(arg1RawText);
			String searchStrArg2 = compressStr(arg2RawText);

			realLineArg1No = getRealArgNo(lineMap, searchStrArg1);
			realLineArg2No = getRealArgNo(lineMap, searchStrArg2);

			if (realLineArg1No != -1)
				pdtbArg1.put(realLineArg1No, argType);
			else {
				System.err.println(name + ":" + searchStrArg1);
			}
			/*
			 * if (!pdtb_line_map.containsKey(arg2LineNo)) { String
			 * searchStrArg2 = compressStr(arg2RawText); realLineArg2No =
			 * getRealArgNo(lineMap, searchStrArg2, arg2LineNo, text, arg2Span);
			 * pdtb_line_map.put(arg2LineNo, realLineArg2No); }
			 * 
			 * realLineArg2No = pdtb_line_map.get(arg2LineNo);
			 */
			if (realLineArg2No != -1)
				pdtbArg2.put(realLineArg2No, argType);
			else {
				System.err.println(name + ":" + searchStrArg2);
			}
		}

	}

	public void readInfo(RevisionDocument doc) {
		String name = getRealNameRevision(doc.getDocumentName());
		Hashtable<Integer, Integer> pdtbArg1_OLD = pdtbArg1Results_OLD
				.get(name);
		Hashtable<Integer, Integer> pdtbArg2_OLD = pdtbArg2Results_OLD
				.get(name);
		Hashtable<Integer, Integer> pdtbArg1_NEW = pdtbArg1Results_NEW
				.get(name);
		Hashtable<Integer, Integer> pdtbArg2_NEW = pdtbArg2Results_NEW
				.get(name);

		ArrayList<String> oldSents = doc.getOldDraftSentences();
		ArrayList<String> newSents = doc.getNewDraftSentences();

		readInfo(doc, resultMap_OLD, oldSents, pdtbArg1_OLD, pdtbArg2_OLD,
				oldTextTable);
		readInfo(doc, resultMap_NEW, newSents, pdtbArg1_NEW, pdtbArg2_NEW,
				newTextTable);
	}

	public void initialize(String path, String revisionDocumentPath)
			throws Exception {
		List<ManualParseResultFile> results = ManualParseResultReader
				.readFiles(path);
		String refPath = "C:\\Not Backed Up\\discourse_parse_results\\litman_corpus\\Braverman\\Braverman_raw_txt";
		resultMap_OLD = new Hashtable<String, ManualParseResultFile>();
		resultMap_NEW = new Hashtable<String, ManualParseResultFile>();
		pdtbArg1Results_OLD = new Hashtable<String, Hashtable<Integer, Integer>>();
		pdtbArg2Results_OLD = new Hashtable<String, Hashtable<Integer, Integer>>();
		pdtbArg1Results_NEW = new Hashtable<String, Hashtable<Integer, Integer>>();
		pdtbArg2Results_NEW = new Hashtable<String, Hashtable<Integer, Integer>>();
		oldTextTable = new Hashtable<String, String>();
		newTextTable = new Hashtable<String, String>();
		readAll(path);
		for (ManualParseResultFile result : results) {
			if (result.isPDTB1()) {
				ModificationRemover.feedTxtInfo(result, refPath);
				String fileName = result.getFileName();
				if (fileName.contains("draft1")) {
					fileName = getRealNamePipe(fileName);
					System.out.println("PIPE NAME:" + fileName);
					Hashtable<Integer, Integer> types = new Hashtable<Integer, Integer>();
					pdtbArg1Results_OLD.put(fileName, types);
					Hashtable<Integer, Integer> types2 = new Hashtable<Integer, Integer>();
					pdtbArg2Results_OLD.put(fileName, types2);
					resultMap_OLD.put(fileName, result);
				} else if (fileName.contains("draft2")) {
					fileName = getRealNamePipe(fileName);
					System.out.println("PIPE NAME:" + fileName);
					Hashtable<Integer, Integer> types = new Hashtable<Integer, Integer>();
					pdtbArg1Results_NEW.put(fileName, types);
					Hashtable<Integer, Integer> types2 = new Hashtable<Integer, Integer>();
					pdtbArg2Results_NEW.put(fileName, types2);
					resultMap_NEW.put(fileName, result);
				}
			}
		}

		ArrayList<RevisionDocument> docs = RevisionDocumentReader
				.readDocs(revisionDocumentPath);
		for (RevisionDocument doc : docs) {
			String name = getRealNameRevision(doc.getDocumentName());
			// System.out.println(name);
			if (pdtbArg1Results_OLD.get(name).size() == 0
					&& pdtbArg2Results_OLD.get(name).size() == 0
					&& pdtbArg1Results_NEW.get(name).size() == 0
					&& pdtbArg2Results_NEW.get(name).size() == 0) {
				readInfo(doc);
			}
		}

		for (RevisionDocument doc : docs) {
			extractPatterns(doc);
		}
	}

	public void printPatterns() {
		System.out.println("**************PATTERN OLD**************");
		printTable(patternCounts);
		System.out.println("**************PATTERN NEW**************");
		printTable(patternCountsNew);
		System.out.println("**************BINDING OLD**************");
		printTable(bindingCounts);
		System.out.println("**************BINDING NEW**************");
		printTable(bindingCountsNew);
		System.out.println("**************BINDING CHANGE**************");
		printTable(changeBinding);
	}

	public void printTable(Hashtable<String, Integer> patternCounts) {
		Iterator<String> it = patternCounts.keySet().iterator();
		while (it.hasNext()) {
			String key = it.next();
			System.out.print(key);
			System.out.print("\t");
			System.out.print(patternCounts.get(key));
			System.out.println();
		}
	}

	public String[] extractPattern(RevisionDocument doc,
			ArrayList<Integer> newIndexes, ArrayList<Integer> oldIndexes,
			int patternLength) {
		String name = getRealNameRevision(doc.getDocumentName());

		HashSet<Integer> arg1Type_OLD = new HashSet<Integer>();
		HashSet<Integer> arg2Type_OLD = new HashSet<Integer>();
		HashSet<Integer> arg1Type_NEW = new HashSet<Integer>();
		HashSet<Integer> arg2Type_NEW = new HashSet<Integer>();

		Hashtable<Integer, Integer> pdtbArg1_OLD = pdtbArg1Results_OLD
				.get(name);
		Hashtable<Integer, Integer> pdtbArg2_OLD = pdtbArg2Results_OLD
				.get(name);
		Hashtable<Integer, Integer> pdtbArg1_NEW = pdtbArg1Results_NEW
				.get(name);
		Hashtable<Integer, Integer> pdtbArg2_NEW = pdtbArg2Results_NEW
				.get(name);

		/*
		 * for (Integer oldIndex : oldIndexes) {
		 * arg1Type_OLD.add(pdtbArg1_OLD.get(oldIndex));
		 * arg2Type_OLD.add(pdtbArg2_OLD.get(oldIndex)); } for (Integer newIndex
		 * : newIndexes) { arg1Type_NEW.add(pdtbArg1_NEW.get(newIndex));
		 * arg2Type_NEW.add(pdtbArg2_NEW.get(newIndex)); }
		 */
		int old_index = 0;
		Collections.sort(oldIndexes);
		for (Integer oldIndex : oldIndexes) {
			if (oldIndex > 0) {
				old_index = oldIndex;
				break;
			}
		}
		int new_index = 0;
		Collections.sort(newIndexes);
		for (Integer newIndex : newIndexes) {
			if (newIndex != -1) {
				new_index = newIndex;
				break;
			}
		}

		int upwards = (patternLength - 1) / 2;
		StringBuffer sbOld = new StringBuffer();
		for (int i = old_index - upwards; i <= old_index; i++) {
			int indent = 0;
			sbOld.append(getArgName(doc, true, i, old_index, pdtbArg1_OLD,
					pdtbArg2_OLD));
			if (i != old_index) {
				sbOld.append("-");
			}
		}
		String patternOld = sbOld.toString();

		StringBuffer sbNew = new StringBuffer();
		for (int i = new_index - upwards; i <= new_index; i++) {
			sbNew.append(getArgName(doc, false, i, new_index, pdtbArg1_NEW,
					pdtbArg2_NEW));
			if (i != new_index) {
				sbNew.append("-");
			}
		}
		String patternNew = sbNew.toString();

		String[] patterns = new String[2];
		patterns[0] = patternOld;
		patterns[1] = patternNew;
		return patterns;
	}
}
