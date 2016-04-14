package edu.pitt.cs.revision.purpose;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Stack;
import java.io.File;
import java.util.Iterator;

import edu.pitt.cs.revision.purpose.pdtb.PipeAttribute;
import edu.pitt.cs.revision.machinelearning.FeatureName;
import edu.pitt.cs.revision.purpose.pdtb.ManualParseResultFile;
import edu.pitt.cs.revision.purpose.pdtb.ManualParseResultReader;
import edu.pitt.cs.revision.purpose.pdtb.ModificationRemover;
import edu.pitt.cs.revision.purpose.pdtb.ParseResultFile;
import edu.pitt.cs.revision.purpose.pdtb.ParseResultReader;
import edu.pitt.cs.revision.purpose.pdtb.PipeUnit;
import edu.pitt.lrdc.cs.revision.model.RevisionDocument;

public class PDTBFeatureExtractorV2 {
	private static PDTBFeatureExtractorV2 instance;
	private Hashtable<String, Hashtable<Integer, Integer>> pdtbArg1Results_OLD;
	private Hashtable<String, Hashtable<Integer, Integer>> pdtbArg2Results_OLD;
	private Hashtable<String, Hashtable<Integer, Integer>> pdtbArg1Results_NEW;
	private Hashtable<String, Hashtable<Integer, Integer>> pdtbArg2Results_NEW;
	private Hashtable<String, ManualParseResultFile> resultMap_OLD;
	private Hashtable<String, ManualParseResultFile> resultMap_NEW;
	private String path = "C:\\Not Backed Up\\discourse_parse_results\\manual2";
	private Hashtable<String, String> oldTextTable;
	private Hashtable<String, String> newTextTable;

	public static String entRelType = "EntRel";
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

	public int getRealArgNo(Hashtable<String, Integer> lineMap,
			String searchStr) {
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
	
	public int getRealArgNo(Hashtable<String, Integer> lineMap,
			String searchStr, int argNo, String txt, String argSpan) {
		ArrayList<Integer> argOptions = new ArrayList<Integer>();
		Iterator<String> it = lineMap.keySet().iterator();
		while (it.hasNext()) {
			String line = it.next();
			// Arg1 matches the search line
			if (line.equals(searchStr) || line.contains(searchStr)
					|| searchStr.contains(line)) {
				argOptions.add(lineMap.get(line));
			}
		}

		if (argOptions.size() == 0) {
			if (argSpan != null && argSpan.length() != 0) {
				/*
				 * ArrayList<String> text = getTextFromSpan(txt, argSpan); if
				 * (text != null && text.size() > 0) { String firstText =
				 * text.get(0); it = lineMap.keySet().iterator(); while
				 * (it.hasNext()) { String line = it.next(); // Arg1 matches the
				 * search line if (line.equals(firstText) ||
				 * line.contains(firstText) || firstText.contains(line)) {
				 * argOptions.add(lineMap.get(line)); } } }
				 */
				String text = getTextFromSpanLonger(txt, argSpan);
				if (text != null) {
					String compressed = compressStr(text);
					it = lineMap.keySet().iterator();
					while (it.hasNext()) {
						String line = it.next();
						// Arg1 matches the search line
						if (line.equals(compressed)
								|| line.contains(compressed)
								|| compressed.contains(line)) {
							argOptions.add(lineMap.get(line));
						}
					}
				}
			}
		}
		if (argOptions.size() == 0) {
			System.err.println("Searched:" + searchStr);
		}

		int realArgNo = -1;
		// Assume that there are few coincidence, and if multiple options exist,
		// select the closest one to pdtb index
		if (argOptions.size() == 0) {
			realArgNo = -1;
		} else if (argOptions.size() == 1) {
			realArgNo = argOptions.get(0);
		} else {
			int minIndex = 0;
			int minGap = Math.abs(argOptions.get(minIndex) - argNo);
			for (int i = 1; i < argOptions.size(); i++) {
				int gap = Math.abs(argOptions.get(i) - argNo);
				if (gap < minGap) {
					minGap = gap;
					minIndex = i;
				}
			}
			realArgNo = argOptions.get(minIndex);
		}
		return realArgNo;
	}
	
	public int getRealArgNo2(Hashtable<String, Integer> lineMap,
			String searchStr, int argNo, String txt, String argSpan) {
		ArrayList<Integer> argOptions = new ArrayList<Integer>();
		Iterator<String> it = lineMap.keySet().iterator();
		while (it.hasNext()) {
			String line = it.next();
			// Arg1 matches the search line
			if (line.equals(searchStr) || line.contains(searchStr)
					|| searchStr.contains(line)) {
				argOptions.add(lineMap.get(line));
			}
		}

		if (argOptions.size() == 0) {
			if (argSpan != null && argSpan.length() != 0) {
				/*
				 * ArrayList<String> text = getTextFromSpan(txt, argSpan); if
				 * (text != null && text.size() > 0) { String firstText =
				 * text.get(0); it = lineMap.keySet().iterator(); while
				 * (it.hasNext()) { String line = it.next(); // Arg1 matches the
				 * search line if (line.equals(firstText) ||
				 * line.contains(firstText) || firstText.contains(line)) {
				 * argOptions.add(lineMap.get(line)); } } }
				 */
				String text = getTextFromSpanLonger(txt, argSpan);
				if (text != null) {
					String compressed = compressStr(text);
					it = lineMap.keySet().iterator();
					while (it.hasNext()) {
						String line = it.next();
						// Arg1 matches the search line
						if (line.equals(compressed)
								|| line.contains(compressed)
								|| compressed.contains(line)) {
							argOptions.add(lineMap.get(line));
						}
					}
				}
			}
		}
		if (argOptions.size() == 0) {
			System.err.println("Searched:" + searchStr);
		}

		int realArgNo = -1;
		// Assume that there are few coincidence, and if multiple options exist,
		// select the closest one to pdtb index
		if (argOptions.size() == 0) {
			realArgNo = -1;
		} else if (argOptions.size() == 1) {
			realArgNo = argOptions.get(0);
		} else {
			int minIndex = 0;
			int minGap = Math.abs(argOptions.get(minIndex) - argNo);
			for (int i = 1; i < argOptions.size(); i++) {
				int gap = Math.abs(argOptions.get(i) - argNo);
				if (gap < minGap) {
					minGap = gap;
					minIndex = i;
				}
			}
			realArgNo = argOptions.get(minIndex);
		}
		return realArgNo;
	}


	public ArrayList<String> getTextFromSpan(String txt, String argSpan) {
		String[] spanList = argSpan.split(";");
		ArrayList<String> textList = new ArrayList<String>();
		for (String span : spanList) {
			String[] splits = span.split("\\.\\.");
			// System.out.println(span);
			int start = Integer.parseInt(splits[0].trim());
			int end = Integer.parseInt(splits[1].trim());
			String textSpan = txt.substring(start, end);
			textList.add(textSpan);
		}
		return textList;
	}

	public String getTextFromSpanLonger(String txt, String argSpan) {
		String[] spanList = argSpan.split(";");
		String longText = null;
		for (String span : spanList) {
			String[] splits = span.split("\\.\\.");
			// System.out.println(span);
			int start = Integer.parseInt(splits[0].trim());
			int end = Integer.parseInt(splits[1].trim());
			String textSpan = txt.substring(start, end);
			if (longText == null)
				longText = textSpan;
			else {
				if (textSpan.length() > longText.length()) {
					longText = textSpan;
				}
			}
		}

		return longText;
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
		if(file==null) {
			System.err.println(name);
		}
		List<PipeUnit> pipes = file.getPipes();
		for (PipeUnit pipe : pipes) {
			/*int relationType = PipeAttribute.RELATION_TYPE;
			int semClassType = PipeAttribute.FIRST_SEMCLASS_CONN;
			String relation = pipe.getAttr(relationType);
			String semClass = pipe.getAttr(semClassType);*/
			String relation = pipe.getElementType();
			String semClass = pipe.getManualRelationType();

			// Get the argument type
			int argType = -1;
			if (relation.equals(entRelType)) {
				argType = IND_entRelType;
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
			String arg1Val = pipe.getAttr(arg1IndexIND);
			String arg1RawText = pipe.getAttr(arg1RawTextIND);
			String arg2Val = pipe.getAttr(arg2IndexIND);
			String arg2RawText = pipe.getAttr(arg2RawTextIND);

			String arg1Span = pipe.getAttr(arg1SpanListIND);
			String arg2Span = pipe.getAttr(arg2SpanListIND);

			int arg1LineNo = Integer.parseInt(arg1Val);
			int arg2LineNo = Integer.parseInt(arg2Val);*/

			// Get the real line number for arg1 and arg2
			int realLineArg1No = -1;
			int realLineArg2No = -1;

			String arg1RawText = pipe.getRange1Txt();
			String arg2RawText = pipe.getRange2Txt();
			/*if (!pdtb_line_map.containsKey(arg1LineNo)) {
				String searchStrArg1 = compressStr(arg1RawText);
				realLineArg1No = getRealArgNo(lineMap, searchStrArg1,
						arg1LineNo, text, arg1Span);
				pdtb_line_map.put(arg1LineNo, realLineArg1No);
			}
			realLineArg1No = pdtb_line_map.get(arg1LineNo);*/
			
			String searchStrArg1 = compressStr(arg1RawText);
			String searchStrArg2 = compressStr(arg2RawText);
			
			realLineArg1No = getRealArgNo(lineMap, searchStrArg1);
			realLineArg2No = getRealArgNo(lineMap, searchStrArg2);
			if (realLineArg1No != -1)
				pdtbArg1.put(realLineArg1No, argType);

			/*
			if (!pdtb_line_map.containsKey(arg2LineNo)) {
				String searchStrArg2 = compressStr(arg2RawText);
				realLineArg2No = getRealArgNo(lineMap, searchStrArg2,
						arg2LineNo, text, arg2Span);
				pdtb_line_map.put(arg2LineNo, realLineArg2No);
			}
			
			realLineArg2No = pdtb_line_map.get(arg2LineNo);*/
			if (realLineArg2No != -1)
				pdtbArg2.put(realLineArg2No, argType);
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
		/*StringBuffer newStr = new StringBuffer();
		for (int i = 0; i < str.length(); i++) {
			char tmp = str.charAt(i);
			if (Character.isLetterOrDigit(tmp)) {
				newStr.append(tmp);
			}
		}
		return newStr.toString();*/
		str = str.replaceAll("[^a-zA-Z]", "");
		str = str.replaceAll(" ", "");
		return str;
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

	private PDTBFeatureExtractorV2() throws IOException {
		// List<ManualParseResultFile> results =
		// ManualParseResultReader.readFiles(path);
		List<ManualParseResultFile> results = ManualParseResultReader.readFiles(path);
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
					System.out.println("PIPE NAME:"+fileName);
					Hashtable<Integer, Integer> types = new Hashtable<Integer, Integer>();
					pdtbArg1Results_OLD.put(fileName, types);
					Hashtable<Integer, Integer> types2 = new Hashtable<Integer, Integer>();
					pdtbArg2Results_OLD.put(fileName, types2);
					resultMap_OLD.put(fileName, result);
				} else if (fileName.contains("draft2")) {
					fileName = getRealNamePipe(fileName);
					System.out.println("PIPE NAME:"+fileName);
					Hashtable<Integer, Integer> types = new Hashtable<Integer, Integer>();
					pdtbArg1Results_NEW.put(fileName, types);
					Hashtable<Integer, Integer> types2 = new Hashtable<Integer, Integer>();
					pdtbArg2Results_NEW.put(fileName, types2);
					resultMap_NEW.put(fileName, result);
				}
			}
		}
	}

	public static PDTBFeatureExtractorV2 getInstance() throws IOException {
		if (instance == null) {
			instance = new PDTBFeatureExtractorV2();
		}
		return instance;
	}

	public void insertARG1ARG2(FeatureName features) {
		ArrayList<Object> options = new ArrayList<Object>();
		for (int i = 0; i <= 9; i++) {
			for (int j = 0; j <= 9; j++) {
				options.add(Integer.toString(i) + "_" + Integer.toString(j));
			}
		}
		features.insertFeature("OLD_PDTB_ARG2_ARG1", options);
		features.insertFeature("NEW_PDTB_ARG2_ARG1", options);
	}

	public void extractFeatureARG1ARG2(FeatureName features,
			Object[] featureVector, RevisionDocument doc,
			ArrayList<Integer> newIndexes, ArrayList<Integer> oldIndexes,
			String name) {
		Hashtable<Integer, Integer> pdtbArg1_OLD = pdtbArg1Results_OLD
				.get(name);
		Hashtable<Integer, Integer> pdtbArg2_OLD = pdtbArg2Results_OLD
				.get(name);
		Hashtable<Integer, Integer> pdtbArg1_NEW = pdtbArg1Results_NEW
				.get(name);
		Hashtable<Integer, Integer> pdtbArg2_NEW = pdtbArg2Results_NEW
				.get(name);

		Collections.sort(newIndexes);
		Collections.sort(oldIndexes);

		String oldArg2 = Integer.toString(0);
		String oldArg1 = Integer.toString(0);
		String newArg2 = Integer.toString(0);
		String newArg1 = Integer.toString(0);

		if (oldIndexes != null && oldIndexes.size() > 0) {
			int oldStart = oldIndexes.get(0);
			if (pdtbArg2_OLD.containsKey(oldStart))
				oldArg2 = Integer.toString(pdtbArg2_OLD.get(oldStart));
			int oldEnd = oldIndexes.get(oldIndexes.size()-1);
			if (pdtbArg1_OLD.containsKey(oldEnd))
				oldArg1 = Integer.toString(pdtbArg1_OLD.get(oldEnd));
		}

		if (newIndexes != null && newIndexes.size() > 0) {
			int newStart = newIndexes.get(0);
			if (pdtbArg2_NEW.containsKey(newStart))
				newArg2 = Integer.toString(pdtbArg2_NEW.get(newStart));
			int newEnd = newIndexes.get(newIndexes.size()-1);
			if (pdtbArg1_NEW.containsKey(newEnd))
				newArg1 = Integer.toString(pdtbArg1_NEW.get(newEnd));
		}

		int fIndex = features.getIndex("OLD_PDTB_ARG2_ARG1");
		featureVector[fIndex] = oldArg2 + "_" + oldArg1;
		fIndex = features.getIndex("NEW_PDTB_ARG2_ARG1");
		featureVector[fIndex] = newArg2 + "_" + newArg1;
		
		if(oldArg2.equals("0")&&oldArg1.equals("0")&&newArg2.equals("0")&&newArg1.equals("0")) {
			System.err.println(doc.getDocumentName()+"Extracted:"+name);
			for(Integer oldIndex: oldIndexes) System.err.print(oldIndex+",");
			System.err.println();
			System.err.println(doc.getOldSentences(oldIndexes));
			for(Integer newIndex: newIndexes) System.err.print(newIndex+",");
			System.err.println();
			System.err.println(doc.getNewSentences(newIndexes));
		}
	}

	public void insertFeature(FeatureName features) {
		features.insertFeature("OLD_PDTB_IsExpansion", Boolean.TYPE);
		//features.insertFeature("OLD_PDTB_IsTemporal", Boolean.TYPE);
		features.insertFeature("OLD_PDTB_IsContingency", Boolean.TYPE);
		//features.insertFeature("OLD_PDTB_IsComparison", Boolean.TYPE);
		features.insertFeature("NEW_PDTB_IsExpansion", Boolean.TYPE);
		//features.insertFeature("NEW_PDTB_IsTemporal", Boolean.TYPE);
		features.insertFeature("NEW_PDTB_IsContingency", Boolean.TYPE);
		//features.insertFeature("NEW_PDTB_IsComparison", Boolean.TYPE);
	}
	
	public void extractFeature(FeatureName features, Object[] featureVector,
			RevisionDocument doc, ArrayList<Integer> newIndexes,
			ArrayList<Integer> oldIndexes) {
		String name = getRealNameRevision(doc.getDocumentName());
		// System.out.println(name);
		if (pdtbArg1Results_OLD.get(name).size() == 0
				&& pdtbArg2Results_OLD.get(name).size() == 0
				&& pdtbArg1Results_NEW.get(name).size() == 0
				&& pdtbArg2Results_NEW.get(name).size() == 0) {
			readInfo(doc);
		}
		
		if(oldIndexes.size()==0&& newIndexes.size()==0) {
		//	System.out.println("UNEXPECTED CASES IN :"+ doc.getDocumentName());
		}

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

		for (Integer oldIndex : oldIndexes) {
			arg1Type_OLD.add(pdtbArg1_OLD.get(oldIndex));
			arg2Type_OLD.add(pdtbArg2_OLD.get(oldIndex));
		}
		for (Integer newIndex : newIndexes) {
			arg1Type_NEW.add(pdtbArg1_NEW.get(newIndex));
			arg2Type_NEW.add(pdtbArg2_NEW.get(newIndex));
		}

	
			String fName = "OLD_PDTB_IsExpansion";
			int fIndex = features.getIndex(fName);
			if (arg1Type_OLD.contains(IND_expansionType_EXP)||arg1Type_OLD.contains(IND_expansionType_IMP)) {
				featureVector[fIndex] = Boolean.toString(true);
			} else {
				featureVector[fIndex] = Boolean.toString(false);
			}
			/*
			fName = "OLD_PDTB_IsTemporal";
			fIndex = features.getIndex(fName);
			if (arg1Type_OLD.contains(IND_temporalType_EXP)||arg1Type_OLD.contains(IND_temporalType_IMP)) {
				featureVector[fIndex] = Boolean.toString(true);
			} else {
				featureVector[fIndex] = Boolean.toString(false);
			}*/
			
			fName = "OLD_PDTB_IsContingency";
			fIndex = features.getIndex(fName);
			if (arg1Type_OLD.contains(IND_contingencyType_EXP)||arg1Type_OLD.contains(IND_contingencyType_IMP)) {
				featureVector[fIndex] = Boolean.toString(true);
			} else {
				featureVector[fIndex] = Boolean.toString(false);
			}
			
			/*
			fName = "OLD_PDTB_IsComparison";
			fIndex = features.getIndex(fName);
			if (arg1Type_OLD.contains(IND_comparisonType_EXP)||arg1Type_OLD.contains(IND_comparisonType_IMP)) {
				featureVector[fIndex] = Boolean.toString(true);
			} else {
				featureVector[fIndex] = Boolean.toString(false);
			}
			*/
			
			fName = "NEW_PDTB_IsExpansion";
			fIndex = features.getIndex(fName);
			if (arg1Type_NEW.contains(IND_expansionType_EXP)||arg1Type_NEW.contains(IND_expansionType_IMP)) {
				featureVector[fIndex] = Boolean.toString(true);
			} else {
				featureVector[fIndex] = Boolean.toString(false);
			}
			
			/*
			fName = "NEW_PDTB_IsTemporal";
			fIndex = features.getIndex(fName);
			if (arg1Type_NEW.contains(IND_temporalType_EXP)||arg1Type_NEW.contains(IND_temporalType_IMP)) {
				featureVector[fIndex] = Boolean.toString(true);
			} else {
				featureVector[fIndex] = Boolean.toString(false);
			}*/
			
			fName = "NEW_PDTB_IsContingency";
			fIndex = features.getIndex(fName);
			if (arg1Type_NEW.contains(IND_contingencyType_EXP)||arg1Type_NEW.contains(IND_contingencyType_IMP)) {
				featureVector[fIndex] = Boolean.toString(true);
			} else {
				featureVector[fIndex] = Boolean.toString(false);
			}
			
			/*
			fName = "NEW_PDTB_IsComparison";
			fIndex = features.getIndex(fName);
			if (arg1Type_NEW.contains(IND_comparisonType_EXP)||arg1Type_NEW.contains(IND_comparisonType_IMP)) {
				featureVector[fIndex] = Boolean.toString(true);
			} else {
				featureVector[fIndex] = Boolean.toString(false);
			}*/
	}
	
	public void insertFeatureAll(FeatureName features) {
		// First test that this does not work
		for (int i = 1; i <= 9; i++) {
			features.insertFeature("OLD_PDTB_ARG1_" + i, Boolean.TYPE);
			features.insertFeature("OLD_PDTB_ARG2_" + i, Boolean.TYPE);
			features.insertFeature("NEW_PDTB_ARG1_" + i, Boolean.TYPE);
			features.insertFeature("NEW_PDTB_ARG2_" + i, Boolean.TYPE);
		}

		// insertARG1ARG2(features);
		/*
		 * ArrayList<Object> options = new ArrayList<Object>(); for(int i =
		 * 1;i<=9;i++) { options.add(Integer.toString(i)); }
		 * features.insertFeature("OLD_PDTB_ARG1", options);
		 * features.insertFeature("OLD_PDTB_ARG2", options);
		 * features.insertFeature("NEW_PDTB_ARG1", options);
		 * features.insertFeature("NEW_PDTB_ARG2", options);
		 */
	}

	public void extractFeatureARG1ARG2(FeatureName features,
			Object[] featureVector, RevisionDocument doc,
			ArrayList<Integer> newIndexes, ArrayList<Integer> oldIndexes) {
		String name = getRealNameRevision(doc.getDocumentName());
		System.out.println("FILE NAME:"+name);
		if (pdtbArg1Results_OLD.get(name).size() == 0
				&& pdtbArg2Results_OLD.get(name).size() == 0
				&& pdtbArg1Results_NEW.get(name).size() == 0
				&& pdtbArg2Results_NEW.get(name).size() == 0) {
			readInfo(doc);
		}
		extractFeatureARG1ARG2(features, featureVector, doc, newIndexes,
				oldIndexes, name);
	}

	public void extractFeatureAll(FeatureName features, Object[] featureVector,
			RevisionDocument doc, ArrayList<Integer> newIndexes,
			ArrayList<Integer> oldIndexes) {
		String name = getRealNameRevision(doc.getDocumentName());
		// System.out.println(name);
		if (pdtbArg1Results_OLD.get(name).size() == 0
				&& pdtbArg2Results_OLD.get(name).size() == 0
				&& pdtbArg1Results_NEW.get(name).size() == 0
				&& pdtbArg2Results_NEW.get(name).size() == 0) {
			readInfo(doc);
		}

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

		for (Integer oldIndex : oldIndexes) {
			arg1Type_OLD.add(pdtbArg1_OLD.get(oldIndex));
			arg2Type_OLD.add(pdtbArg2_OLD.get(oldIndex));
		}
		for (Integer newIndex : newIndexes) {
			arg1Type_NEW.add(pdtbArg1_NEW.get(newIndex));
			arg2Type_NEW.add(pdtbArg2_NEW.get(newIndex));
		}

		for (int i = 1; i <= 9; i++) {
			String fName = "OLD_PDTB_ARG1_" + i;
			int fIndex = features.getIndex(fName);
			if (arg1Type_OLD.contains(i)) {
				featureVector[fIndex] = Boolean.toString(true);
			} else {
				featureVector[fIndex] = Boolean.toString(false);
			}

			fName = "OLD_PDTB_ARG2_" + i;
			fIndex = features.getIndex(fName);
			if (arg2Type_OLD.contains(i)) {
				featureVector[fIndex] = Boolean.toString(true);
			} else {
				featureVector[fIndex] = Boolean.toString(false);
			}

			fName = "NEW_PDTB_ARG1_" + i;
			fIndex = features.getIndex(fName);
			if (arg1Type_NEW.contains(i)) {
				featureVector[fIndex] = Boolean.toString(true);
			} else {
				featureVector[fIndex] = Boolean.toString(false);
			}

			fName = "NEW_PDTB_ARG2_" + i;
			fIndex = features.getIndex(fName);
			if (arg2Type_NEW.contains(i)) {
				featureVector[fIndex] = Boolean.toString(true);
			} else {
				featureVector[fIndex] = Boolean.toString(false);
			}
		}
	}
}
