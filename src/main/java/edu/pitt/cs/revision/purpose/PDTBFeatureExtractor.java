package edu.pitt.cs.revision.purpose;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.io.File;
import java.util.Iterator;

import edu.pitt.cs.revision.purpose.pdtb.PipeAttribute;
import edu.pitt.cs.revision.machinelearning.FeatureName;
import edu.pitt.cs.revision.purpose.pdtb.ParseResultFile;
import edu.pitt.cs.revision.purpose.pdtb.ParseResultReader;
import edu.pitt.cs.revision.purpose.pdtb.PipeUnit;
import edu.pitt.lrdc.cs.revision.model.RevisionDocument;

public class PDTBFeatureExtractor {
	private static PDTBFeatureExtractor instance;
	private Hashtable<String, Hashtable<Integer, Integer>> pdtbArg1Results_OLD;
	private Hashtable<String, Hashtable<Integer, Integer>> pdtbArg2Results_OLD;
	private Hashtable<String, Hashtable<Integer, Integer>> pdtbArg1Results_NEW;
	private Hashtable<String, Hashtable<Integer, Integer>> pdtbArg2Results_NEW;
	private Hashtable<String, ParseResultFile> resultMap_OLD;
	private Hashtable<String, ParseResultFile> resultMap_NEW;
	private String path = "C:\\Not Backed Up\\discourse_parse_results\\litman_corpus";

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
			String searchStr, int argNo) {
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
			Hashtable<String, ParseResultFile> resultMap,
			ArrayList<String> sentences, Hashtable<Integer, Integer> pdtbArg1,
			Hashtable<Integer, Integer> pdtbArg2) {
		// Map of lines
		Hashtable<String, Integer> lineMap = new Hashtable<String, Integer>();
		// Map the index of pdtb and old and new sentences in revision document
		Hashtable<Integer, Integer> pdtb_line_map = new Hashtable<Integer, Integer>();
		String name = getRealNameRevision(doc.getDocumentName());

		for (int i = 0; i < sentences.size(); i++) {
			lineMap.put(compressStr(sentences.get(i)), i + 1);
		}

		ParseResultFile file = resultMap.get(name);
		List<PipeUnit> pipes = file.getPipes();
		for (PipeUnit pipe : pipes) {
			int relationType = PipeAttribute.RELATION_TYPE;
			int semClassType = PipeAttribute.FIRST_SEMCLASS_CONN;
			String relation = pipe.getAttr(relationType);
			String semClass = pipe.getAttr(semClassType);

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
			int arg1IndexIND = PipeAttribute.ARG1_ATTR_SRC;
			int arg1RawTextIND = PipeAttribute.ARG1_ATTR_RAWTEXT;
			int arg2IndexIND = PipeAttribute.ARG2_ATTR_SRC;
			int arg2RawTextIND = PipeAttribute.ARG2_ATTR_RAWTEXT;

			String arg1Val = pipe.getAttr(arg1IndexIND);
			String arg1RawText = pipe.getAttr(arg1RawTextIND);
			String arg2Val = pipe.getAttr(arg2IndexIND);
			String arg2RawText = pipe.getAttr(arg2RawTextIND);

			int arg1LineNo = Integer.parseInt(arg1Val);
			int arg2LineNo = Integer.parseInt(arg2Val);

			// Get the real line number for arg1 and arg2
			int realLineArg1No = -1;
			int realLineArg2No = -1;

			if (!pdtb_line_map.containsKey(arg1LineNo)) {
				String searchStrArg1 = compressStr(arg1RawText);
				realLineArg1No = getRealArgNo(lineMap, searchStrArg1,
						arg1LineNo);
				pdtb_line_map.put(arg1LineNo, realLineArg1No);
			}
			realLineArg1No = pdtb_line_map.get(arg1LineNo);
			if (realLineArg1No != -1)
				pdtbArg1.put(realLineArg1No, argType);

			if (!pdtb_line_map.containsKey(arg2LineNo)) {
				String searchStrArg2 = compressStr(arg2RawText);
				realLineArg2No = getRealArgNo(lineMap, searchStrArg2,
						arg2LineNo);
				pdtb_line_map.put(arg2LineNo, realLineArg2No);
			}
			realLineArg2No = pdtb_line_map.get(arg2LineNo);
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

		readInfo(doc, resultMap_OLD, oldSents, pdtbArg1_OLD, pdtbArg2_OLD);
		readInfo(doc, resultMap_NEW, newSents, pdtbArg1_NEW, pdtbArg2_NEW);
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
		StringBuffer newStr = new StringBuffer();
		for (int i = 0; i < str.length(); i++) {
			char tmp = str.charAt(i);
			if (Character.isLetterOrDigit(tmp)) {
				newStr.append(tmp);
			}
		}
		return newStr.toString();
	}

	public String getRealNamePipe(String fileName) {
		File f = new File(fileName);
		String fName = f.getName();
		int index = fName.indexOf(".txt");
		fName = fName.substring(0, index);
		return fName;
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
			int index = 0 + annotationStr.length();
			fName = fName.substring(index + 1);
		}
		return fName;
	}

	private PDTBFeatureExtractor() throws IOException {
		List<ParseResultFile> results = ParseResultReader.readFiles(path);
		resultMap_OLD = new Hashtable<String, ParseResultFile>();
		resultMap_NEW = new Hashtable<String, ParseResultFile>();
		for (ParseResultFile result : results) {
			if (result.isPDTB1()) {
				String fileName = result.getFileName();
				if (fileName.contains("draft1")) {
					fileName = getRealNamePipe(fileName);
					Hashtable<Integer, Integer> types = new Hashtable<Integer, Integer>();
					pdtbArg1Results_OLD.put(fileName, types);
					pdtbArg2Results_OLD.put(fileName, types);
					resultMap_OLD.put(fileName, result);
				} else if (fileName.contains("draft2")) {
					fileName = getRealNamePipe(fileName);
					Hashtable<Integer, Integer> types = new Hashtable<Integer, Integer>();
					pdtbArg1Results_NEW.put(fileName, types);
					pdtbArg2Results_NEW.put(fileName, types);
					resultMap_NEW.put(fileName, result);
				}
			}
		}
	}

	public static PDTBFeatureExtractor getInstance() throws IOException {
		if (instance == null) {
			instance = new PDTBFeatureExtractor();
		}
		return instance;
	}

	public void insertFeature(FeatureName features) {
		for (int i = 1; i <= 9; i++) {
			features.insertFeature("OLD_PDTB_ARG1_"+i, Boolean.TYPE);
			features.insertFeature("OLD_PDTB_ARG2_"+i, Boolean.TYPE);
			features.insertFeature("NEW_PDTB_ARG1_"+i, Boolean.TYPE);
			features.insertFeature("NEW_PDTB_ARG2_"+i, Boolean.TYPE);			
		}
	}

	public void extractFeature(FeatureName features, Object[] featureVector,
			RevisionDocument doc, ArrayList<Integer> newIndexes,
			ArrayList<Integer> oldIndexes) {
		String name = getRealNameRevision(doc.getDocumentName());
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
			String fName = "OLD_PDTB_ARG1_"+i;
			int fIndex = features.getIndex(fName);
			if(arg1Type_OLD.contains(i)) {
				featureVector[fIndex] = Boolean.toString(true);
			} else {
				featureVector[fIndex] = Boolean.toString(false);
			}
			
			fName = "OLD_PDTB_ARG2_"+i;
			fIndex = features.getIndex(fName);
			if(arg2Type_OLD.contains(i)) {
				featureVector[fIndex] = Boolean.toString(true);
			} else {
				featureVector[fIndex] = Boolean.toString(false);
			}
			
			fName = "NEW_PDTB_ARG1_"+i;
			fIndex = features.getIndex(fName);
			if(arg1Type_NEW.contains(i)) {
				featureVector[fIndex] = Boolean.toString(true);
			} else {
				featureVector[fIndex] = Boolean.toString(false);
			}
			
			fName = "NEW_PDTB_ARG2_"+i;
			fIndex = features.getIndex(fName);
			if(arg2Type_NEW.contains(i)) {
				featureVector[fIndex] = Boolean.toString(true);
			} else {
				featureVector[fIndex] = Boolean.toString(false);
			}
		}
	}
}
