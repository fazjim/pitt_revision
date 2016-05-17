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
import edu.pitt.cs.revision.machinelearning.MalletAssist;
import edu.pitt.cs.revision.purpose.pdtb.ManualParseResultFile;
import edu.pitt.cs.revision.purpose.pdtb.ManualParseResultReader;
import edu.pitt.cs.revision.purpose.pdtb.ModificationRemover;
import edu.pitt.cs.revision.purpose.pdtb.ParseResultFile;
import edu.pitt.cs.revision.purpose.pdtb.ParseResultReader;
import edu.pitt.cs.revision.purpose.pdtb.PipeUnit;
import edu.pitt.lrdc.cs.revision.alignment.distance.LDCalculator;
import edu.pitt.lrdc.cs.revision.io.MyLogger;
import edu.pitt.lrdc.cs.revision.model.RevisionDocument;

public class PDTBFeatureExtractorV4 {
	private static PDTBFeatureExtractorV4 instance;
	private Hashtable<String, Hashtable<Integer, Integer>> pdtbArg1Results_OLD;
	private Hashtable<String, Hashtable<Integer, Integer>> pdtbArg2Results_OLD;
	private Hashtable<String, Hashtable<Integer, Integer>> pdtbArg1Results_NEW;
	private Hashtable<String, Hashtable<Integer, Integer>> pdtbArg2Results_NEW;
	private Hashtable<String, Hashtable<Integer, Integer>> pdtbOwnResults_OLD;
	private Hashtable<String, Hashtable<Integer, Integer>> pdtbOwnResults_NEW;

	private Hashtable<String, ParseResultFile> resultMap_OLD;
	private Hashtable<String, ParseResultFile> resultMap_NEW;
	private String path = "C:\\Not Backed Up\\discourse_parse_results\\litman_corpus\\Braverman";
	private Hashtable<String, String> oldTextTable;
	private Hashtable<String, String> newTextTable;

	private Hashtable<String, Hashtable<String, PDTBEntityGrid>> gridTables; // Binds
																				// the
																				// file
																				// and
																				// the
																				// tables
	private int topicNum = 2; // The list of topic names
	private int entityWords = 200;// number of entity words in each topic
	String filePath = "C:\\Not Backed Up\\discourse_parse_results\\litman_corpus\\Braverman\\allTxt";

	private Hashtable<String, HashSet<String>> topicEntities; // The set of
																// words in the
																// topic
	private Hashtable<String, Hashtable<Integer, PDTBGraph>> graphDocIndex_OLD;
	private Hashtable<String, Hashtable<Integer, PDTBGraph>> graphDocIndex_NEW;
	
	private Hashtable<String, Hashtable<Integer, PDTBTree>> treeDocIndex_OLD;
	private Hashtable<String, Hashtable<Integer, PDTBTree>> treeDocIndex_NEW;

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

	public static String[] getTypeSense(int index) {
		String[] values = new String[2];
		if (index == IND_entRelType) {
			values[0] = entRelType;
			values[1] = "";
		} else if (index == IND_altLexType) {
			values[0] = altLexType;
			values[1] = "";
		} else if (index == IND_comparisonType_EXP) {
			values[0] = explicitRelType;
			values[1] = comparisonType;
		} else if (index == IND_comparisonType_IMP) {
			values[1] = comparisonType;
			values[0] = implicitRelType;
		} else if (index == IND_contingencyType_EXP) {
			values[1] = contingencyType;
			values[0] = explicitRelType;
		} else if (index == IND_contingencyType_IMP) {
			values[1] = contingencyType;
			values[0] = implicitRelType;
		} else if (index == IND_expansionType_EXP) {
			values[1] = expansionType;
			values[0] = explicitRelType;
		} else if (index == IND_expansionType_IMP) {
			values[1] = expansionType;
			values[0] = implicitRelType;
		} else if (index == IND_temporalType_EXP) {
			values[1] = temporalType;
			values[0] = explicitRelType;
		} else if (index == IND_temporalType_IMP) {
			values[1] = temporalType;
			values[0] = implicitRelType;
		}
		return values;
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
		
		//Find the most similar one
		Iterator<String> it2 = lineMap.keySet().iterator();
		int sim = Integer.MAX_VALUE;
		int index = -1;
		while(it2.hasNext()) {
			String line = it2.next();
			int val = LDCalculator.calc(line, searchStr);
			if(val<sim) {
				index = lineMap.get(line);
				val = sim;
			}
		}
		return index;
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
			Hashtable<String, ParseResultFile> resultMap,
			ArrayList<String> sentences, Hashtable<Integer, Integer> pdtbArg1,
			Hashtable<Integer, Integer> pdtbArg2,
			Hashtable<Integer, Integer> pdtbOwn,
			Hashtable<String, String> txtTable) {
		// Map of lines
		Hashtable<String, Integer> lineMap = new Hashtable<String, Integer>();
		// Map the index of pdtb and old and new sentences in revision document
		Hashtable<Integer, Integer> pdtb_line_map = new Hashtable<Integer, Integer>();
		String name = getRealNameRevision(doc.getDocumentName());
		String text = txtTable.get(name);

		for (int i = 0; i < sentences.size(); i++) {
			System.err.println("Compressed sentences:"+compressStr(sentences.get(i)));
			lineMap.put(compressStr(sentences.get(i)), i + 1);
		}

		ParseResultFile file = resultMap.get(name);
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
			String semClass = pipe.getRelationType();

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

			String arg1RawText = pipe.getRange1TxtAuto();
			String arg2RawText = pipe.getRange2TxtAuto();
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

			if(realLineArg1No == -1) {
				System.err.println(searchStrArg1);
			}
			if(realLineArg2No == -1) {
				System.err.println(searchStrArg2);
			}
			
			if (realLineArg1No != realLineArg2No) {
				if (realLineArg1No != -1)
					pdtbArg1.put(realLineArg1No, argType);

				/*
				 * if (!pdtb_line_map.containsKey(arg2LineNo)) { String
				 * searchStrArg2 = compressStr(arg2RawText); realLineArg2No =
				 * getRealArgNo(lineMap, searchStrArg2, arg2LineNo, text,
				 * arg2Span); pdtb_line_map.put(arg2LineNo, realLineArg2No); }
				 * 
				 * realLineArg2No = pdtb_line_map.get(arg2LineNo);
				 */
				if (realLineArg2No != -1)
					pdtbArg2.put(realLineArg2No, argType);
			} else {
				pdtbOwn.put(realLineArg2No, argType);
			}

			pipe.setArg1SentenceIndex(realLineArg1No);
			pipe.setArg2SentenceIndex(realLineArg2No);
		}

		// Setting up things, can build the graph now
	}

	public void readInfo(RevisionDocument doc) throws IOException {
		String name = getRealNameRevision(doc.getDocumentName());
		Hashtable<Integer, Integer> pdtbArg1_OLD = pdtbArg1Results_OLD
				.get(name);
		Hashtable<Integer, Integer> pdtbArg2_OLD = pdtbArg2Results_OLD
				.get(name);
		Hashtable<Integer, Integer> pdtbArg1_NEW = pdtbArg1Results_NEW
				.get(name);
		Hashtable<Integer, Integer> pdtbArg2_NEW = pdtbArg2Results_NEW
				.get(name);

		Hashtable<Integer, Integer> pdtbOwn_OLD = pdtbOwnResults_OLD.get(name);
		Hashtable<Integer, Integer> pdtbOwn_NEW = pdtbOwnResults_NEW.get(name);

		Hashtable<String, PDTBEntityGrid> gridTable = gridTables.get(name);

		ArrayList<String> oldSents = doc.getOldDraftSentences();
		ArrayList<String> newSents = doc.getNewDraftSentences();

		readInfo(doc, resultMap_OLD, oldSents, pdtbArg1_OLD, pdtbArg2_OLD,
				pdtbOwn_OLD, oldTextTable);
		readInfo(doc, resultMap_NEW, newSents, pdtbArg1_NEW, pdtbArg2_NEW,
				pdtbOwn_NEW, newTextTable);

		readGrid(doc, gridTable, topicNum, topicEntities, pdtbArg1_OLD,
				pdtbArg2_OLD, pdtbArg1_NEW, pdtbArg2_NEW, pdtbOwn_OLD,
				pdtbOwn_NEW);

		Hashtable<Integer, PDTBGraph> graphTableOld = graphDocIndex_OLD
				.get(name);
		Hashtable<Integer, PDTBGraph> graphTableNew = graphDocIndex_NEW
				.get(name);
		readGraph(doc, resultMap_OLD, resultMap_NEW, graphTableOld,
				graphTableNew);
		
		Hashtable<Integer, PDTBTree> treeTableOld = treeDocIndex_OLD
				.get(name);
		Hashtable<Integer, PDTBTree> treeTableNew = treeDocIndex_NEW
				.get(name);
		readTree(doc, resultMap_OLD, resultMap_NEW, treeTableOld,
				treeTableNew);
	}

	/**
	 * Constructing the graphs
	 * 
	 * @param doc
	 * @param resultMap_OLD
	 * @param resultMap_NEW
	 * @param graphOld
	 * @param graphNew
	 * @throws IOException
	 */
	public void readGraph(RevisionDocument doc,
			Hashtable<String, ParseResultFile> resultMap_OLD,
			Hashtable<String, ParseResultFile> resultMap_NEW,
			Hashtable<Integer, PDTBGraph> graphOld,
			Hashtable<Integer, PDTBGraph> graphNew) throws IOException {
		String name = getRealNameRevision(doc.getDocumentName());
		ParseResultFile oldFile = resultMap_OLD.get(name);
		ParseResultFile newFile = resultMap_NEW.get(name);
		readGraph(doc, oldFile, graphOld, true);
		readGraph(doc, newFile, graphNew, false);
	}

	
	/**
	 * Constructing the tree
	 * 
	 * @param doc
	 * @param resultMap_OLD
	 * @param resultMap_NEW
	 * @param graphOld
	 * @param graphNew
	 * @throws IOException
	 */
	public void readTree(RevisionDocument doc,
			Hashtable<String, ParseResultFile> resultMap_OLD,
			Hashtable<String, ParseResultFile> resultMap_NEW,
			Hashtable<Integer, PDTBTree> treeOld,
			Hashtable<Integer, PDTBTree> treeNew) throws IOException {
		String name = getRealNameRevision(doc.getDocumentName());
		ParseResultFile oldFile = resultMap_OLD.get(name);
		ParseResultFile newFile = resultMap_NEW.get(name);
		readTree(doc, oldFile, treeOld, true);
		readTree(doc, newFile, treeNew, false);
	}
	
	public void readGraph(RevisionDocument doc,
			ParseResultFile parseFile,
			Hashtable<Integer, PDTBGraph> graphIndex, boolean isOld)
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
			PDTBGraph graph = new PDTBGraph();

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
			graph.setStartSentenceIndex(firstIndex);
			graph.setEndSentenceIndex(lastIndex);
			graph.buildGraph(units, doc, isOld, sentences);
			String headline = doc.getDocumentName() + ", isOld:"+ isOld + "\n";
			//MyLogger.getInstance().log(headline + graph.toString());
			graphIndex.put(i, graph);
		}
	}

	public void readTree(RevisionDocument doc,
			ParseResultFile parseFile,
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
			//graph.setStartSentenceIndex(firstIndex);
			//graph.setEndSentenceIndex(lastIndex);
			tree.buildTree(units, doc, isOld, sentences);
			treeIndex.put(i, tree);
		}
	}

	
	/**
	 * Read in all the grids
	 * 
	 * @param doc
	 * @param gridTable
	 * @param topicNum
	 * @param topicEntities
	 */
	public void readGrid(RevisionDocument doc,
			Hashtable<String, PDTBEntityGrid> gridTable, int topicNum,
			Hashtable<String, HashSet<String>> topicEntities,
			Hashtable<Integer, Integer> pdtbArg1_OLD,
			Hashtable<Integer, Integer> pdtbArg2_OLD,
			Hashtable<Integer, Integer> pdtbArg1_NEW,
			Hashtable<Integer, Integer> pdtbArg2_NEW,
			Hashtable<Integer, Integer> pdtbOwn_OLD,
			Hashtable<Integer, Integer> pdtbOwn_NEW) {
		PDTBEntityGrid mergedGrid = gridTable.get("Merged");
		for (int i = 0; i < topicNum; i++) {
			String topic = "Topic-" + i;
			HashSet<String> entities = topicEntities.get(topic);
			PDTBEntityGrid grid = gridTable.get(topic);
			fillInGrid(doc, grid, entities, pdtbArg1_OLD, pdtbArg2_OLD,
					pdtbOwn_OLD, true, mergedGrid);
			fillInGrid(doc, grid, entities, pdtbArg1_NEW, pdtbArg2_NEW,
					pdtbOwn_NEW, false, mergedGrid);
		}
	}

	/**
	 * Fill in the grid Search the str and fill in the grid at paragraphs
	 * 
	 * @param doc
	 * @param grid
	 * @param entities
	 * @param pdtbArg1
	 * @param pdtbArg2
	 * @param isOld
	 */
	public void fillInGrid(RevisionDocument doc, PDTBEntityGrid grid,
			HashSet<String> entities, Hashtable<Integer, Integer> pdtbArg1,
			Hashtable<Integer, Integer> pdtbArg2,
			Hashtable<Integer, Integer> pdtbOwn, boolean isOld,
			PDTBEntityGrid mergedGrid) {
		Hashtable<Integer, List<Integer>> paragraphIndices = new Hashtable<Integer, List<Integer>>();

		if (isOld) {
			int sentenceNum = doc.getOldDraftSentences().size();
			for (int i = 1; i <= sentenceNum; i++) {
				int paragraphNo = doc.getParaNoOfOldSentence(i);
				if (!paragraphIndices.containsKey(paragraphNo)) {
					int oldStart = doc.getFirstOfOldParagraph(paragraphNo);
					int oldEnd = doc.getLastOfOldParagraph(paragraphNo);
					List<Integer> sentences = new ArrayList<Integer>();
					for (int j = oldStart; j <= oldEnd; j++) {
						sentences.add(j);
					}
					paragraphIndices.put(paragraphNo, sentences);
				}

				List<Integer> sentencesPara = paragraphIndices.get(paragraphNo);
				if (hasWord(doc, i, entities, isOld)) {
					if (pdtbArg1.containsKey(i)) {
						int arg1 = pdtbArg1.get(i);
						String[] values = getTypeSense(arg1);
						grid.getGrid(i, isOld).setValue(values[0], values[1],
								true, 1);
					}
					if (pdtbArg2.containsKey(i)) {
						int arg2 = pdtbArg2.get(i);
						String[] values = getTypeSense(arg2);
						grid.getGrid(i, isOld).setValue(values[0], values[1],
								false, 1);
					}
					if (pdtbOwn.containsKey(i)) {
						int arg = pdtbOwn.get(i);
						String[] values = getTypeSense(arg);
						grid.getGrid(i, isOld).setValue(values[0], values[1],
								true, 0.5);
						grid.getGrid(i, isOld).setValue(values[0], values[1],
								false, 0.5);
					}
				}

				int beforeIndex = getRelatedSentenceBefore(sentencesPara, i,
						doc, entities, isOld);
				int afterIndex = getRelatedSentenceAfter(sentencesPara, i, doc,
						entities, isOld);

				if (beforeIndex != -1) {
					if (pdtbArg1.containsKey(beforeIndex)) {
						int arg2 = pdtbArg1.get(beforeIndex);
						String[] values = getTypeSense(arg2);
						grid.getGrid(i, isOld).setValue(values[0], values[1],
								false, (i - beforeIndex));

					}
				}
				if (afterIndex != -1) {
					if (pdtbArg2.containsKey(afterIndex)) {
						int arg1 = pdtbArg2.get(afterIndex);
						String[] values = getTypeSense(arg1);
						grid.getGrid(i, isOld).setValue(values[0], values[1],
								true, (afterIndex - i));

					}
				}
				mergedGrid.getGrid(i, isOld).merge(grid.getGrid(i, isOld));
			}
		} else {
			int sentenceNum = doc.getNewDraftSentences().size();
			for (int i = 1; i <= sentenceNum; i++) {
				int paragraphNo = doc.getParaNoOfNewSentence(i);
				if (!paragraphIndices.containsKey(paragraphNo)) {
					int newStart = doc.getFirstOfNewParagraph(paragraphNo);
					int newEnd = doc.getLastOfNewParagraph(paragraphNo);
					List<Integer> sentences = new ArrayList<Integer>();
					for (int j = newStart; j <= newEnd; j++) {
						sentences.add(j);
					}
					paragraphIndices.put(paragraphNo, sentences);
				}

				List<Integer> sentencesPara = paragraphIndices.get(paragraphNo);

				if (hasWord(doc, i, entities, isOld)) {
					if (pdtbArg1.containsKey(i)) {
						int arg1 = pdtbArg1.get(i);
						String[] values = getTypeSense(arg1);
						grid.getGrid(i, isOld).setValue(values[0], values[1],
								true, 1);

					}
					if (pdtbArg2.containsKey(i)) {
						int arg2 = pdtbArg2.get(i);
						String[] values = getTypeSense(arg2);
						grid.getGrid(i, isOld).setValue(values[0], values[1],
								false, 1);

					}

					if (pdtbOwn.containsKey(i)) {
						int arg = pdtbOwn.get(i);
						String[] values = getTypeSense(arg);
						grid.getGrid(i, isOld).setValue(values[0], values[1],
								true, 0.5);
						grid.getGrid(i, isOld).setValue(values[0], values[1],
								false, 0.5);
					}
				}

				int beforeIndex = getRelatedSentenceBefore(sentencesPara, i,
						doc, entities, isOld);
				int afterIndex = getRelatedSentenceAfter(sentencesPara, i, doc,
						entities, isOld);

				if (beforeIndex != -1) {
					if (pdtbArg1.containsKey(beforeIndex)) {
						int arg2 = pdtbArg1.get(beforeIndex);
						String[] values = getTypeSense(arg2);
						grid.getGrid(i, isOld).setValue(values[0], values[1],
								false, (i - beforeIndex));

					}
				}
				if (afterIndex != -1) {
					if (pdtbArg2.containsKey(afterIndex)) {
						int arg1 = pdtbArg2.get(afterIndex);
						String[] values = getTypeSense(arg1);
						grid.getGrid(i, isOld).setValue(values[0], values[1],
								true, (afterIndex - i));

					}
				}
				mergedGrid.getGrid(i, isOld).merge(grid.getGrid(i, isOld));
			}
		}
	}

	public boolean hasWord(RevisionDocument doc, int index,
			HashSet<String> entityWords, boolean isOld) {
		String sentence = "";
		if (isOld) {
			sentence = doc.getOldSentence(index);
		} else {
			sentence = doc.getNewSentence(index);
		}
		String[] tokens = sentence.split(" ");
		boolean hasTokenOfTopic = false;
		for (String token : tokens) {
			token = token.toLowerCase().trim();
			if (token.contains("'"))
				token = token.substring(0, token.indexOf("'"));
			if (entityWords.contains(token)) {
				hasTokenOfTopic = true;
				break;
			}
		}
		return hasTokenOfTopic;
	}

	public int getRelatedSentenceBefore(List<Integer> sentences, int index,
			RevisionDocument doc, HashSet<String> entityWords, boolean isOld) {
		int value = -1;
		if (hasWord(doc, index, entityWords, isOld)) {
			for (int i = 0; i < sentences.size(); i++) {
				int sentenceIndex = sentences.get(i);
				if (sentenceIndex < index) {
					if (hasWord(doc, sentenceIndex, entityWords, isOld)) {
						value = sentenceIndex;
					}
				} else if (sentenceIndex >= index) {
					return value;
				}
			}
		} else {
			return -1;
		}
		return value;
	}

	public int getRelatedSentenceAfter(List<Integer> sentences, int index,
			RevisionDocument doc, HashSet<String> entityWords, boolean isOld) {
		int value = -1;
		if (hasWord(doc, index, entityWords, isOld)) {
			for (int i = sentences.size() - 1; i >= 0; i--) {
				int sentenceIndex = sentences.get(i);
				if (sentenceIndex > index) {
					if (hasWord(doc, sentenceIndex, entityWords, isOld)) {
						value = sentenceIndex;
					}
				} else if (sentenceIndex <= index) {
					return value;
				}
			}
		} else {
			return -1;
		}
		return value;
	}

	/**
	 * Constructing the topic entities
	 * 
	 * @throws Exception
	 */
	public void constructTopicEntitites(String docPath) throws Exception {
		topicEntities = new Hashtable<String, HashSet<String>>();
		// adding the topic entity words below
		List<HashSet<String>> topicWords = MalletAssist.trainTopicModel(
				docPath, topicNum, entityWords);
		for (int i = 0; i < topicNum; i++) {
			String topicID = "Topic-" + i;
			topicEntities.put(topicID, topicWords.get(i));
		}
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

	private PDTBFeatureExtractorV4() throws Exception {
		// List<ManualParseResultFile> results =
		// ManualParseResultReader.readFiles(path);
		List<ParseResultFile> results = ParseResultReader
				.readFiles(path);
		String refPath = "C:\\Not Backed Up\\discourse_parse_results\\litman_corpus\\Braverman\\Braverman_raw_txt";
		constructTopicEntitites(filePath);
		resultMap_OLD = new Hashtable<String, ParseResultFile>();
		resultMap_NEW = new Hashtable<String, ParseResultFile>();
		pdtbArg1Results_OLD = new Hashtable<String, Hashtable<Integer, Integer>>();
		pdtbArg2Results_OLD = new Hashtable<String, Hashtable<Integer, Integer>>();
		pdtbArg1Results_NEW = new Hashtable<String, Hashtable<Integer, Integer>>();
		pdtbArg2Results_NEW = new Hashtable<String, Hashtable<Integer, Integer>>();
		pdtbOwnResults_OLD = new Hashtable<String, Hashtable<Integer, Integer>>();
		pdtbOwnResults_NEW = new Hashtable<String, Hashtable<Integer, Integer>>();
		oldTextTable = new Hashtable<String, String>();
		newTextTable = new Hashtable<String, String>();
		graphDocIndex_OLD = new Hashtable<String, Hashtable<Integer, PDTBGraph>>();
		graphDocIndex_NEW = new Hashtable<String, Hashtable<Integer, PDTBGraph>>();

		treeDocIndex_OLD = new Hashtable<String, Hashtable<Integer, PDTBTree>>();
		treeDocIndex_NEW = new Hashtable<String, Hashtable<Integer, PDTBTree>>();
		
		gridTables = new Hashtable<String, Hashtable<String, PDTBEntityGrid>>();

		readAll(path);
		for (ParseResultFile result : results) {
			if (result.isPDTB1()) {
				ModificationRemover.feedTxtInfo(result, refPath);
				String fileName = result.getFileName();

				Hashtable<String, PDTBEntityGrid> gridTable = new Hashtable<String, PDTBEntityGrid>();
				for (int i = 0; i < topicNum; i++) {
					String topic = "Topic-" + i;
					gridTable.put(topic, new PDTBEntityGrid());
				}
				gridTable.put("Merged", new PDTBEntityGrid());
				gridTables.put(getRealNamePipe(fileName), gridTable);

				if (fileName.contains("draft1")) {
					fileName = getRealNamePipe(fileName);
					System.out.println("PIPE NAME:" + fileName);
					Hashtable<Integer, Integer> types = new Hashtable<Integer, Integer>();
					pdtbArg1Results_OLD.put(fileName, types);
					Hashtable<Integer, Integer> types2 = new Hashtable<Integer, Integer>();
					pdtbArg2Results_OLD.put(fileName, types2);
					Hashtable<Integer, Integer> types3 = new Hashtable<Integer, Integer>();
					pdtbOwnResults_OLD.put(fileName, types3);
					resultMap_OLD.put(fileName, result);

					Hashtable<Integer, PDTBGraph> graphIndex = new Hashtable<Integer, PDTBGraph>();
					graphDocIndex_OLD.put(fileName, graphIndex);
					Hashtable<Integer, PDTBTree> treeIndex = new Hashtable<Integer, PDTBTree>();
					treeDocIndex_OLD.put(fileName, treeIndex);
				} else if (fileName.contains("draft2")) {
					fileName = getRealNamePipe(fileName);
					System.out.println("PIPE NAME:" + fileName);
					Hashtable<Integer, Integer> types = new Hashtable<Integer, Integer>();
					pdtbArg1Results_NEW.put(fileName, types);
					Hashtable<Integer, Integer> types2 = new Hashtable<Integer, Integer>();
					pdtbArg2Results_NEW.put(fileName, types2);
					Hashtable<Integer, Integer> types3 = new Hashtable<Integer, Integer>();
					pdtbOwnResults_NEW.put(fileName, types3);
					resultMap_NEW.put(fileName, result);

					Hashtable<Integer, PDTBGraph> graphIndex = new Hashtable<Integer, PDTBGraph>();
					graphDocIndex_NEW.put(fileName, graphIndex);
					Hashtable<Integer, PDTBTree> treeIndex = new Hashtable<Integer, PDTBTree>();
					treeDocIndex_NEW.put(fileName, treeIndex);
				}
			}
		}
	}

	public static PDTBFeatureExtractorV4 getInstance() throws Exception {
		if (instance == null) {
			instance = new PDTBFeatureExtractorV4();
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
			int oldEnd = oldIndexes.get(oldIndexes.size() - 1);
			if (pdtbArg1_OLD.containsKey(oldEnd))
				oldArg1 = Integer.toString(pdtbArg1_OLD.get(oldEnd));
		}

		if (newIndexes != null && newIndexes.size() > 0) {
			int newStart = newIndexes.get(0);
			if (pdtbArg2_NEW.containsKey(newStart))
				newArg2 = Integer.toString(pdtbArg2_NEW.get(newStart));
			int newEnd = newIndexes.get(newIndexes.size() - 1);
			if (pdtbArg1_NEW.containsKey(newEnd))
				newArg1 = Integer.toString(pdtbArg1_NEW.get(newEnd));
		}

		int fIndex = features.getIndex("OLD_PDTB_ARG2_ARG1");
		featureVector[fIndex] = oldArg2 + "_" + oldArg1;
		fIndex = features.getIndex("NEW_PDTB_ARG2_ARG1");
		featureVector[fIndex] = newArg2 + "_" + newArg1;

		if (oldArg2.equals("0") && oldArg1.equals("0") && newArg2.equals("0")
				&& newArg1.equals("0")) {
			System.err.println(doc.getDocumentName() + "Extracted:" + name);
			for (Integer oldIndex : oldIndexes)
				System.err.print(oldIndex + ",");
			System.err.println();
			System.err.println(doc.getOldSentences(oldIndexes));
			for (Integer newIndex : newIndexes)
				System.err.print(newIndex + ",");
			System.err.println();
			System.err.println(doc.getNewSentences(newIndexes));
		}
	}

	public void insertFeature(FeatureName features) {
		// features.insertFeature("OLD_PDTB_IsExpansion", Boolean.TYPE);
		// // features.insertFeature("OLD_PDTB_IsTemporal", Boolean.TYPE);
		// features.insertFeature("OLD_PDTB_IsContingency", Boolean.TYPE);
		// // features.insertFeature("OLD_PDTB_IsComparison", Boolean.TYPE);
		// features.insertFeature("NEW_PDTB_IsExpansion", Boolean.TYPE);
		// // features.insertFeature("NEW_PDTB_IsTemporal", Boolean.TYPE);
		// features.insertFeature("NEW_PDTB_IsContingency", Boolean.TYPE);
		// // features.insertFeature("NEW_PDTB_IsComparison", Boolean.TYPE);
		// features.insertFeature("OLD_PDTB_IsEntRel", Boolean.TYPE);
		// features.insertFeature("NEW_PDTB_IsEntRel", Boolean.TYPE);
		// features.insertFeature("OLD_PDTB_IsExplicit", Boolean.TYPE);
		// features.insertFeature("NEW_PDTB_IsExplicit", Boolean.TYPE);

		// insertPatternFeature(features, 3);
		insertSelectedFeatures(features);
		insertSelectedFeaturesPost(features);
		insertPDTBVectorFeature(features);
	}

	public void insertPDTBEntityFeatures(FeatureName features) {
		for (int i = 0; i < topicNum; i++) {
			// String tagName = "Topic-" + i;
			// PDTBEntityGrid.insertFeature(features, tagName);
			String tagName = "Topic-" + i + "-Weight-";
			PDTBEntityGrid.insertFeatureWeight(features, tagName);
			PDTBEntityGrid.insertFeatureWeightDiff(features, tagName);
		}
		// String tagName = "Merged";
		// PDTBEntityGrid.insertFeature(features, tagName);
		// PDTBEntityGrid.insertCountFeature(features, "MergeCount");
		// PDTBEntityGrid.insertCountFeature(features, "MergeWeight");
		String tagName = "Merged-Weight-";
		PDTBEntityGrid.insertFeatureWeight(features, tagName);
		PDTBEntityGrid.insertFeatureWeightDiff(features, tagName);
	}

	public void insertSelectedFeatures(FeatureName features) {
		features.insertFeature("OLD_PDTB_IsEntRel", Boolean.TYPE);
		features.insertFeature("NEW_PDTB_IsEntRel", Boolean.TYPE);

		features.insertFeature("OLD_PDTB_IsAltLex", Boolean.TYPE);
		features.insertFeature("NEW_PDTB_IsAltLex", Boolean.TYPE);

		features.insertFeature("OLD_PDTB_IsComparison_Explicit", Boolean.TYPE);
		features.insertFeature("OLD_PDTB_IsComparison_Implicit", Boolean.TYPE);
		features.insertFeature("NEW_PDTB_IsComparison_Explicit", Boolean.TYPE);
		features.insertFeature("NEW_PDTB_IsComparison_Implicit", Boolean.TYPE);

		features.insertFeature("OLD_PDTB_IsContingency_Explicit", Boolean.TYPE);
		features.insertFeature("OLD_PDTB_IsContingency_Implicit", Boolean.TYPE);
		features.insertFeature("NEW_PDTB_IsContingency_Explicit", Boolean.TYPE);
		features.insertFeature("NEW_PDTB_IsContingency_Implicit", Boolean.TYPE);

		features.insertFeature("OLD_PDTB_IsExpansion_Explicit", Boolean.TYPE);
		features.insertFeature("OLD_PDTB_IsExpansion_Implicit", Boolean.TYPE);
		features.insertFeature("NEW_PDTB_IsExpansion_Explicit", Boolean.TYPE);
		features.insertFeature("NEW_PDTB_IsExpansion_Implicit", Boolean.TYPE);

		features.insertFeature("OLD_PDTB_IsTemporal_Explicit", Boolean.TYPE);
		features.insertFeature("OLD_PDTB_IsTemporal_Implicit", Boolean.TYPE);
		features.insertFeature("NEW_PDTB_IsTemporal_Explicit", Boolean.TYPE);
		features.insertFeature("NEW_PDTB_IsTemporal_Implicit", Boolean.TYPE);

	}

	public void insertSelectedFeaturesPost(FeatureName features) {
		features.insertFeature("OLD_PDTB_IsEntRel_Post", Boolean.TYPE);
		features.insertFeature("NEW_PDTB_IsEntRel_Post", Boolean.TYPE);

		features.insertFeature("OLD_PDTB_IsAltLex_Post", Boolean.TYPE);
		features.insertFeature("NEW_PDTB_IsAltLex_Post", Boolean.TYPE);

		features.insertFeature("OLD_PDTB_IsComparison_Explicit_Post",
				Boolean.TYPE);
		features.insertFeature("OLD_PDTB_IsComparison_Implicit_Post",
				Boolean.TYPE);
		features.insertFeature("NEW_PDTB_IsComparison_Explicit_Post",
				Boolean.TYPE);
		features.insertFeature("NEW_PDTB_IsComparison_Implicit_Post",
				Boolean.TYPE);

		features.insertFeature("OLD_PDTB_IsContingency_Explicit_Post",
				Boolean.TYPE);
		features.insertFeature("OLD_PDTB_IsContingency_Implicit_Post",
				Boolean.TYPE);
		features.insertFeature("NEW_PDTB_IsContingency_Explicit_Post",
				Boolean.TYPE);
		features.insertFeature("NEW_PDTB_IsContingency_Implicit_Post",
				Boolean.TYPE);

		features.insertFeature("OLD_PDTB_IsExpansion_Explicit_Post",
				Boolean.TYPE);
		features.insertFeature("OLD_PDTB_IsExpansion_Implicit_Post",
				Boolean.TYPE);
		features.insertFeature("NEW_PDTB_IsExpansion_Explicit_Post",
				Boolean.TYPE);
		features.insertFeature("NEW_PDTB_IsExpansion_Implicit_Post",
				Boolean.TYPE);

		features.insertFeature("OLD_PDTB_IsTemporal_Explicit_Post",
				Boolean.TYPE);
		features.insertFeature("OLD_PDTB_IsTemporal_Implicit_Post",
				Boolean.TYPE);
		features.insertFeature("NEW_PDTB_IsTemporal_Explicit_Post",
				Boolean.TYPE);
		features.insertFeature("NEW_PDTB_IsTemporal_Implicit_Post",
				Boolean.TYPE);
	}

	public void insertSelectedFeaturesGroupWeighted(FeatureName features) {
		features.insertFeature("OLD_PDTB_IsEntRel_Weighted_Group", Double.TYPE);
		features.insertFeature("NEW_PDTB_IsEntRel_Weighted_Group", Double.TYPE);

		features.insertFeature("OLD_PDTB_IsAltLex_Weighted_Group", Double.TYPE);
		features.insertFeature("NEW_PDTB_IsAltLex_Weighted_Group", Double.TYPE);

		features.insertFeature("OLD_PDTB_IsComparison_Weighted_Group", Double.TYPE);
		/*
		 * features.insertFeature("OLD_PDTB_IsComparison_Explicit_Weighted",
		 * Double.TYPE);
		 * features.insertFeature("OLD_PDTB_IsComparison_Implicit_Weighted",
		 * Double.TYPE);
		 */

		features.insertFeature("NEW_PDTB_IsComparison_Weighted_Group", Double.TYPE);
		/*
		 * features.insertFeature("NEW_PDTB_IsComparison_Explicit_Weighted",
		 * Double.TYPE);
		 * features.insertFeature("NEW_PDTB_IsComparison_Implicit_Weighted",
		 * Double.TYPE);
		 */

		features.insertFeature("OLD_PDTB_IsContingency_Weighted_Group", Double.TYPE);
		/*
		 * features.insertFeature("OLD_PDTB_IsContingency_Explicit_Weighted",
		 * Double.TYPE);
		 * features.insertFeature("OLD_PDTB_IsContingency_Implicit_Weighted",
		 * Double.TYPE);
		 */

		features.insertFeature("NEW_PDTB_IsContingency_Weighted_Group", Double.TYPE);
		/*
		 * features.insertFeature("NEW_PDTB_IsContingency_Explicit_Weighted",
		 * Double.TYPE);
		 * features.insertFeature("NEW_PDTB_IsContingency_Implicit_Weighted",
		 * Double.TYPE);
		 */

		features.insertFeature("OLD_PDTB_IsExpansion_Weighted_Group", Double.TYPE);
		/*
		 * features.insertFeature("OLD_PDTB_IsExpansion_Explicit_Weighted",
		 * Double.TYPE);
		 * features.insertFeature("OLD_PDTB_IsExpansion_Implicit_Weighted",
		 * Double.TYPE);
		 */

		features.insertFeature("NEW_PDTB_IsExpansion_Weighted_Group", Double.TYPE);
		/*
		 * features.insertFeature("NEW_PDTB_IsExpansion_Explicit_Weighted",
		 * Double.TYPE);
		 * features.insertFeature("NEW_PDTB_IsExpansion_Implicit_Weighted",
		 * Double.TYPE);
		 */

		features.insertFeature("OLD_PDTB_IsTemporal_Weighted_Group", Double.TYPE);
		/*
		 * features.insertFeature("OLD_PDTB_IsTemporal_Explicit_Weighted",
		 * Double.TYPE);
		 * features.insertFeature("OLD_PDTB_IsTemporal_Implicit_Weighted",
		 * Double.TYPE);
		 */

		features.insertFeature("NEW_PDTB_IsTemporal_Weighted_Group", Double.TYPE);
		/*
		 * features.insertFeature("NEW_PDTB_IsTemporal_Explicit_Weighted",
		 * Double.TYPE);
		 * features.insertFeature("NEW_PDTB_IsTemporal_Implicit_Weighted",
		 * Double.TYPE);
		 */
	}
	
	public void insertSelectedFeaturesWeighted(FeatureName features) {
		features.insertFeature("OLD_PDTB_IsEntRel_Weighted", Double.TYPE);
		features.insertFeature("NEW_PDTB_IsEntRel_Weighted", Double.TYPE);

		features.insertFeature("OLD_PDTB_IsAltLex_Weighted", Double.TYPE);
		features.insertFeature("NEW_PDTB_IsAltLex_Weighted", Double.TYPE);

		features.insertFeature("OLD_PDTB_IsComparison_Weighted", Double.TYPE);
		/*
		 * features.insertFeature("OLD_PDTB_IsComparison_Explicit_Weighted",
		 * Double.TYPE);
		 * features.insertFeature("OLD_PDTB_IsComparison_Implicit_Weighted",
		 * Double.TYPE);
		 */

		features.insertFeature("NEW_PDTB_IsComparison_Weighted", Double.TYPE);
		/*
		 * features.insertFeature("NEW_PDTB_IsComparison_Explicit_Weighted",
		 * Double.TYPE);
		 * features.insertFeature("NEW_PDTB_IsComparison_Implicit_Weighted",
		 * Double.TYPE);
		 */

		features.insertFeature("OLD_PDTB_IsContingency_Weighted", Double.TYPE);
		/*
		 * features.insertFeature("OLD_PDTB_IsContingency_Explicit_Weighted",
		 * Double.TYPE);
		 * features.insertFeature("OLD_PDTB_IsContingency_Implicit_Weighted",
		 * Double.TYPE);
		 */

		features.insertFeature("NEW_PDTB_IsContingency_Weighted", Double.TYPE);
		/*
		 * features.insertFeature("NEW_PDTB_IsContingency_Explicit_Weighted",
		 * Double.TYPE);
		 * features.insertFeature("NEW_PDTB_IsContingency_Implicit_Weighted",
		 * Double.TYPE);
		 */

		features.insertFeature("OLD_PDTB_IsExpansion_Weighted", Double.TYPE);
		/*
		 * features.insertFeature("OLD_PDTB_IsExpansion_Explicit_Weighted",
		 * Double.TYPE);
		 * features.insertFeature("OLD_PDTB_IsExpansion_Implicit_Weighted",
		 * Double.TYPE);
		 */

		features.insertFeature("NEW_PDTB_IsExpansion_Weighted", Double.TYPE);
		/*
		 * features.insertFeature("NEW_PDTB_IsExpansion_Explicit_Weighted",
		 * Double.TYPE);
		 * features.insertFeature("NEW_PDTB_IsExpansion_Implicit_Weighted",
		 * Double.TYPE);
		 */

		features.insertFeature("OLD_PDTB_IsTemporal_Weighted", Double.TYPE);
		/*
		 * features.insertFeature("OLD_PDTB_IsTemporal_Explicit_Weighted",
		 * Double.TYPE);
		 * features.insertFeature("OLD_PDTB_IsTemporal_Implicit_Weighted",
		 * Double.TYPE);
		 */

		features.insertFeature("NEW_PDTB_IsTemporal_Weighted", Double.TYPE);
		/*
		 * features.insertFeature("NEW_PDTB_IsTemporal_Explicit_Weighted",
		 * Double.TYPE);
		 * features.insertFeature("NEW_PDTB_IsTemporal_Implicit_Weighted",
		 * Double.TYPE);
		 */
	}

	public void insertSelectedFeaturesWeightedTree(FeatureName features,
			int levels) {
		for (int i = 1; i <= levels; i++) {
			String key = "_Level_" + i;
			features.insertFeature("OLD_PDTB_IsEntRel_Weighted" + key,
					Double.TYPE);
			features.insertFeature("NEW_PDTB_IsEntRel_Weighted" + key,
					Double.TYPE);

			features.insertFeature("OLD_PDTB_IsAltLex_Weighted" + key,
					Double.TYPE);
			features.insertFeature("NEW_PDTB_IsAltLex_Weighted" + key,
					Double.TYPE);

			features.insertFeature("OLD_PDTB_IsComparison_Weighted" + key,
					Double.TYPE);
			/*
			 * features.insertFeature("OLD_PDTB_IsComparison_Explicit_Weighted",
			 * Double.TYPE);
			 * features.insertFeature("OLD_PDTB_IsComparison_Implicit_Weighted",
			 * Double.TYPE);
			 */

			features.insertFeature("NEW_PDTB_IsComparison_Weighted" + key,
					Double.TYPE);
			/*
			 * features.insertFeature("NEW_PDTB_IsComparison_Explicit_Weighted",
			 * Double.TYPE);
			 * features.insertFeature("NEW_PDTB_IsComparison_Implicit_Weighted",
			 * Double.TYPE);
			 */

			features.insertFeature("OLD_PDTB_IsContingency_Weighted" + key,
					Double.TYPE);
			/*
			 * features.insertFeature("OLD_PDTB_IsContingency_Explicit_Weighted",
			 * Double.TYPE);
			 * features.insertFeature("OLD_PDTB_IsContingency_Implicit_Weighted"
			 * , Double.TYPE);
			 */

			features.insertFeature("NEW_PDTB_IsContingency_Weighted" + key,
					Double.TYPE);
			/*
			 * features.insertFeature("NEW_PDTB_IsContingency_Explicit_Weighted",
			 * Double.TYPE);
			 * features.insertFeature("NEW_PDTB_IsContingency_Implicit_Weighted"
			 * , Double.TYPE);
			 */

			features.insertFeature("OLD_PDTB_IsExpansion_Weighted" + key,
					Double.TYPE);
			/*
			 * features.insertFeature("OLD_PDTB_IsExpansion_Explicit_Weighted",
			 * Double.TYPE);
			 * features.insertFeature("OLD_PDTB_IsExpansion_Implicit_Weighted",
			 * Double.TYPE);
			 */

			features.insertFeature("NEW_PDTB_IsExpansion_Weighted" + key,
					Double.TYPE);
			/*
			 * features.insertFeature("NEW_PDTB_IsExpansion_Explicit_Weighted",
			 * Double.TYPE);
			 * features.insertFeature("NEW_PDTB_IsExpansion_Implicit_Weighted",
			 * Double.TYPE);
			 */

			features.insertFeature("OLD_PDTB_IsTemporal_Weighted" + key,
					Double.TYPE);
			/*
			 * features.insertFeature("OLD_PDTB_IsTemporal_Explicit_Weighted",
			 * Double.TYPE);
			 * features.insertFeature("OLD_PDTB_IsTemporal_Implicit_Weighted",
			 * Double.TYPE);
			 */

			features.insertFeature("NEW_PDTB_IsTemporal_Weighted" + key,
					Double.TYPE);
			/*
			 * features.insertFeature("NEW_PDTB_IsTemporal_Explicit_Weighted",
			 * Double.TYPE);
			 * features.insertFeature("NEW_PDTB_IsTemporal_Implicit_Weighted",
			 * Double.TYPE);
			 */
		}
	}

	
	public void insertSelectedFeaturesPostGroupWeighted(FeatureName features) {
		features.insertFeature("OLD_PDTB_IsEntRel_Post_Weighted_Group", Double.TYPE);
		features.insertFeature("NEW_PDTB_IsEntRel_Post_Weighted_Group", Double.TYPE);

		features.insertFeature("OLD_PDTB_IsAltLex_Post_Weighted_Group", Double.TYPE);
		features.insertFeature("NEW_PDTB_IsAltLex_Post_Weighted_Group", Double.TYPE);

		features.insertFeature("OLD_PDTB_IsComparison_Post_Weighted_Group",
				Double.TYPE);
		/*
		 * features.insertFeature("OLD_PDTB_IsComparison_Explicit_Post_Weighted",
		 * Double.TYPE);
		 * features.insertFeature("OLD_PDTB_IsComparison_Implicit_Post_Weighted"
		 * , Double.TYPE);
		 */

		features.insertFeature("NEW_PDTB_IsComparison_Post_Weighted_Group",
				Double.TYPE);
		/*
		 * features.insertFeature("NEW_PDTB_IsComparison_Explicit_Post_Weighted",
		 * Double.TYPE);
		 * features.insertFeature("NEW_PDTB_IsComparison_Implicit_Post_Weighted"
		 * , Double.TYPE);
		 */

		features.insertFeature("OLD_PDTB_IsContingency_Post_Weighted_Group",
				Double.TYPE);
		/*
		 * features.insertFeature("OLD_PDTB_IsContingency_Explicit_Post_Weighted"
		 * , Double.TYPE);
		 * features.insertFeature("OLD_PDTB_IsContingency_Implicit_Post_Weighted"
		 * , Double.TYPE);
		 */

		features.insertFeature("NEW_PDTB_IsContingency_Post_Weighted_Group",
				Double.TYPE);
		/*
		 * features.insertFeature("NEW_PDTB_IsContingency_Explicit_Post_Weighted"
		 * , Double.TYPE);
		 * features.insertFeature("NEW_PDTB_IsContingency_Implicit_Post_Weighted"
		 * , Double.TYPE);
		 */

		features.insertFeature("OLD_PDTB_IsExpansion_Post_Weighted_Group",
				Double.TYPE);
		/*
		 * features.insertFeature("OLD_PDTB_IsExpansion_Explicit_Post_Weighted",
		 * Double.TYPE);
		 * features.insertFeature("OLD_PDTB_IsExpansion_Implicit_Post_Weighted",
		 * Double.TYPE);
		 */

		features.insertFeature("NEW_PDTB_IsExpansion_Post_Weighted_Group",
				Double.TYPE);
		/*
		 * features.insertFeature("NEW_PDTB_IsExpansion_Explicit_Post_Weighted",
		 * Double.TYPE);
		 * features.insertFeature("NEW_PDTB_IsExpansion_Implicit_Post_Weighted",
		 * Double.TYPE);
		 */

		features.insertFeature("OLD_PDTB_IsTemporal_Post_Weighted_Group", Double.TYPE);
		/*
		 * features.insertFeature("OLD_PDTB_IsTemporal_Explicit_Post_Weighted",
		 * Double.TYPE);
		 * features.insertFeature("OLD_PDTB_IsTemporal_Implicit_Post_Weighted",
		 * Double.TYPE);
		 */

		features.insertFeature("NEW_PDTB_IsTemporal_Post_Weighted_Group", Double.TYPE);
		/*
		 * features.insertFeature("NEW_PDTB_IsTemporal_Explicit_Post_Weighted",
		 * Double.TYPE);
		 * features.insertFeature("NEW_PDTB_IsTemporal_Implicit_Post_Weighted",
		 * Double.TYPE);
		 */
	}
	
	public void insertSelectedFeaturesPostWeightedTree(FeatureName features,
			int levels) {
		for (int i = 1; i <= levels; i++) {
			String key = "_Level_" + i;
			features.insertFeature("OLD_PDTB_IsEntRel_Post_Weighted" + key,
					Double.TYPE);
			features.insertFeature("NEW_PDTB_IsEntRel_Post_Weighted" + key,
					Double.TYPE);

			features.insertFeature("OLD_PDTB_IsAltLex_Post_Weighted" + key,
					Double.TYPE);
			features.insertFeature("NEW_PDTB_IsAltLex_Post_Weighted" + key,
					Double.TYPE);

			features.insertFeature("OLD_PDTB_IsComparison_Post_Weighted" + key,
					Double.TYPE);
			/*
			 * features.insertFeature("OLD_PDTB_IsComparison_Explicit_Post_Weighted"
			 * , Double.TYPE);
			 * features.insertFeature("OLD_PDTB_IsComparison_Implicit_Post_Weighted"
			 * , Double.TYPE);
			 */

			features.insertFeature("NEW_PDTB_IsComparison_Post_Weighted" + key,
					Double.TYPE);
			/*
			 * features.insertFeature("NEW_PDTB_IsComparison_Explicit_Post_Weighted"
			 * , Double.TYPE);
			 * features.insertFeature("NEW_PDTB_IsComparison_Implicit_Post_Weighted"
			 * , Double.TYPE);
			 */

			features.insertFeature(
					"OLD_PDTB_IsContingency_Post_Weighted" + key, Double.TYPE);
			/*
			 * features.insertFeature(
			 * "OLD_PDTB_IsContingency_Explicit_Post_Weighted" , Double.TYPE);
			 * features
			 * .insertFeature("OLD_PDTB_IsContingency_Implicit_Post_Weighted" ,
			 * Double.TYPE);
			 */

			features.insertFeature(
					"NEW_PDTB_IsContingency_Post_Weighted" + key, Double.TYPE);
			/*
			 * features.insertFeature(
			 * "NEW_PDTB_IsContingency_Explicit_Post_Weighted" , Double.TYPE);
			 * features
			 * .insertFeature("NEW_PDTB_IsContingency_Implicit_Post_Weighted" ,
			 * Double.TYPE);
			 */

			features.insertFeature("OLD_PDTB_IsExpansion_Post_Weighted" + key,
					Double.TYPE);
			/*
			 * features.insertFeature("OLD_PDTB_IsExpansion_Explicit_Post_Weighted"
			 * , Double.TYPE);
			 * features.insertFeature("OLD_PDTB_IsExpansion_Implicit_Post_Weighted"
			 * , Double.TYPE);
			 */

			features.insertFeature("NEW_PDTB_IsExpansion_Post_Weighted" + key,
					Double.TYPE);
			/*
			 * features.insertFeature("NEW_PDTB_IsExpansion_Explicit_Post_Weighted"
			 * , Double.TYPE);
			 * features.insertFeature("NEW_PDTB_IsExpansion_Implicit_Post_Weighted"
			 * , Double.TYPE);
			 */

			features.insertFeature("OLD_PDTB_IsTemporal_Post_Weighted" + key,
					Double.TYPE);
			/*
			 * features.insertFeature("OLD_PDTB_IsTemporal_Explicit_Post_Weighted"
			 * , Double.TYPE);
			 * features.insertFeature("OLD_PDTB_IsTemporal_Implicit_Post_Weighted"
			 * , Double.TYPE);
			 */

			features.insertFeature("NEW_PDTB_IsTemporal_Post_Weighted" + key,
					Double.TYPE);
			/*
			 * features.insertFeature("NEW_PDTB_IsTemporal_Explicit_Post_Weighted"
			 * , Double.TYPE);
			 * features.insertFeature("NEW_PDTB_IsTemporal_Implicit_Post_Weighted"
			 * , Double.TYPE);
			 */
		}
	}

	
	public void insertSelectedFeaturesPostWeighted(FeatureName features) {
		features.insertFeature("OLD_PDTB_IsEntRel_Post_Weighted", Double.TYPE);
		features.insertFeature("NEW_PDTB_IsEntRel_Post_Weighted", Double.TYPE);

		features.insertFeature("OLD_PDTB_IsAltLex_Post_Weighted", Double.TYPE);
		features.insertFeature("NEW_PDTB_IsAltLex_Post_Weighted", Double.TYPE);

		features.insertFeature("OLD_PDTB_IsComparison_Post_Weighted",
				Double.TYPE);
		/*
		 * features.insertFeature("OLD_PDTB_IsComparison_Explicit_Post_Weighted",
		 * Double.TYPE);
		 * features.insertFeature("OLD_PDTB_IsComparison_Implicit_Post_Weighted"
		 * , Double.TYPE);
		 */

		features.insertFeature("NEW_PDTB_IsComparison_Post_Weighted",
				Double.TYPE);
		/*
		 * features.insertFeature("NEW_PDTB_IsComparison_Explicit_Post_Weighted",
		 * Double.TYPE);
		 * features.insertFeature("NEW_PDTB_IsComparison_Implicit_Post_Weighted"
		 * , Double.TYPE);
		 */

		features.insertFeature("OLD_PDTB_IsContingency_Post_Weighted",
				Double.TYPE);
		/*
		 * features.insertFeature("OLD_PDTB_IsContingency_Explicit_Post_Weighted"
		 * , Double.TYPE);
		 * features.insertFeature("OLD_PDTB_IsContingency_Implicit_Post_Weighted"
		 * , Double.TYPE);
		 */

		features.insertFeature("NEW_PDTB_IsContingency_Post_Weighted",
				Double.TYPE);
		/*
		 * features.insertFeature("NEW_PDTB_IsContingency_Explicit_Post_Weighted"
		 * , Double.TYPE);
		 * features.insertFeature("NEW_PDTB_IsContingency_Implicit_Post_Weighted"
		 * , Double.TYPE);
		 */

		features.insertFeature("OLD_PDTB_IsExpansion_Post_Weighted",
				Double.TYPE);
		/*
		 * features.insertFeature("OLD_PDTB_IsExpansion_Explicit_Post_Weighted",
		 * Double.TYPE);
		 * features.insertFeature("OLD_PDTB_IsExpansion_Implicit_Post_Weighted",
		 * Double.TYPE);
		 */

		features.insertFeature("NEW_PDTB_IsExpansion_Post_Weighted",
				Double.TYPE);
		/*
		 * features.insertFeature("NEW_PDTB_IsExpansion_Explicit_Post_Weighted",
		 * Double.TYPE);
		 * features.insertFeature("NEW_PDTB_IsExpansion_Implicit_Post_Weighted",
		 * Double.TYPE);
		 */

		features.insertFeature("OLD_PDTB_IsTemporal_Post_Weighted", Double.TYPE);
		/*
		 * features.insertFeature("OLD_PDTB_IsTemporal_Explicit_Post_Weighted",
		 * Double.TYPE);
		 * features.insertFeature("OLD_PDTB_IsTemporal_Implicit_Post_Weighted",
		 * Double.TYPE);
		 */

		features.insertFeature("NEW_PDTB_IsTemporal_Post_Weighted", Double.TYPE);
		/*
		 * features.insertFeature("NEW_PDTB_IsTemporal_Explicit_Post_Weighted",
		 * Double.TYPE);
		 * features.insertFeature("NEW_PDTB_IsTemporal_Implicit_Post_Weighted",
		 * Double.TYPE);
		 */
	}

	public void insertSelectedFeaturesDiffWeighted(FeatureName features) {
		features.insertFeature("DIFF_PDTB_IsEntRel_Weighted", Double.TYPE);

		features.insertFeature("DIFF_PDTB_IsAltLex_Weighted", Double.TYPE);

		features.insertFeature("DIFF_PDTB_IsComparison_Weighted", Double.TYPE);
		/*
		 * features.insertFeature("OLD_PDTB_IsComparison_Explicit_Post_Weighted",
		 * Double.TYPE);
		 * features.insertFeature("OLD_PDTB_IsComparison_Implicit_Post_Weighted"
		 * , Double.TYPE);
		 */
		/*
		 * features.insertFeature("NEW_PDTB_IsComparison_Explicit_Post_Weighted",
		 * Double.TYPE);
		 * features.insertFeature("NEW_PDTB_IsComparison_Implicit_Post_Weighted"
		 * , Double.TYPE);
		 */

		features.insertFeature("DIFF_PDTB_IsContingency_Weighted", Double.TYPE);
		/*
		 * features.insertFeature("OLD_PDTB_IsContingency_Explicit_Post_Weighted"
		 * , Double.TYPE);
		 * features.insertFeature("OLD_PDTB_IsContingency_Implicit_Post_Weighted"
		 * , Double.TYPE);
		 */
		/*
		 * features.insertFeature("NEW_PDTB_IsContingency_Explicit_Post_Weighted"
		 * , Double.TYPE);
		 * features.insertFeature("NEW_PDTB_IsContingency_Implicit_Post_Weighted"
		 * , Double.TYPE);
		 */

		features.insertFeature("DIFF_PDTB_IsExpansion_Weighted", Double.TYPE);
		/*
		 * features.insertFeature("OLD_PDTB_IsExpansion_Explicit_Post_Weighted",
		 * Double.TYPE);
		 * features.insertFeature("OLD_PDTB_IsExpansion_Implicit_Post_Weighted",
		 * Double.TYPE);
		 */
		/*
		 * features.insertFeature("NEW_PDTB_IsExpansion_Explicit_Post_Weighted",
		 * Double.TYPE);
		 * features.insertFeature("NEW_PDTB_IsExpansion_Implicit_Post_Weighted",
		 * Double.TYPE);
		 */

		features.insertFeature("DIFF_PDTB_IsTemporal_Weighted", Double.TYPE);
		/*
		 * features.insertFeature("OLD_PDTB_IsTemporal_Explicit_Post_Weighted",
		 * Double.TYPE);
		 * features.insertFeature("OLD_PDTB_IsTemporal_Implicit_Post_Weighted",
		 * Double.TYPE);
		 */
		/*
		 * features.insertFeature("NEW_PDTB_IsTemporal_Explicit_Post_Weighted",
		 * Double.TYPE);
		 * features.insertFeature("NEW_PDTB_IsTemporal_Implicit_Post_Weighted",
		 * Double.TYPE);
		 */
	}

	public void insertSelectedFeaturesDiffWeightedPost(FeatureName features) {
		features.insertFeature("DIFF_PDTB_IsEntRel_Post_Weighted", Double.TYPE);

		features.insertFeature("DIFF_PDTB_IsAltLex_Post_Weighted", Double.TYPE);

		features.insertFeature("DIFF_PDTB_IsComparison_Post_Weighted",
				Double.TYPE);
		/*
		 * features.insertFeature("OLD_PDTB_IsComparison_Explicit_Post_Weighted",
		 * Double.TYPE);
		 * features.insertFeature("OLD_PDTB_IsComparison_Implicit_Post_Weighted"
		 * , Double.TYPE);
		 */
		/*
		 * features.insertFeature("NEW_PDTB_IsComparison_Explicit_Post_Weighted",
		 * Double.TYPE);
		 * features.insertFeature("NEW_PDTB_IsComparison_Implicit_Post_Weighted"
		 * , Double.TYPE);
		 */

		features.insertFeature("DIFF_PDTB_IsContingency_Post_Weighted",
				Double.TYPE);
		/*
		 * features.insertFeature("OLD_PDTB_IsContingency_Explicit_Post_Weighted"
		 * , Double.TYPE);
		 * features.insertFeature("OLD_PDTB_IsContingency_Implicit_Post_Weighted"
		 * , Double.TYPE);
		 */
		/*
		 * features.insertFeature("NEW_PDTB_IsContingency_Explicit_Post_Weighted"
		 * , Double.TYPE);
		 * features.insertFeature("NEW_PDTB_IsContingency_Implicit_Post_Weighted"
		 * , Double.TYPE);
		 */

		features.insertFeature("DIFF_PDTB_IsExpansion_Post_Weighted",
				Double.TYPE);
		/*
		 * features.insertFeature("OLD_PDTB_IsExpansion_Explicit_Post_Weighted",
		 * Double.TYPE);
		 * features.insertFeature("OLD_PDTB_IsExpansion_Implicit_Post_Weighted",
		 * Double.TYPE);
		 */
		/*
		 * features.insertFeature("NEW_PDTB_IsExpansion_Explicit_Post_Weighted",
		 * Double.TYPE);
		 * features.insertFeature("NEW_PDTB_IsExpansion_Implicit_Post_Weighted",
		 * Double.TYPE);
		 */

		features.insertFeature("DIFF_PDTB_IsTemporal_Post_Weighted",
				Double.TYPE);
		/*
		 * features.insertFeature("OLD_PDTB_IsTemporal_Explicit_Post_Weighted",
		 * Double.TYPE);
		 * features.insertFeature("OLD_PDTB_IsTemporal_Implicit_Post_Weighted",
		 * Double.TYPE);
		 */
		/*
		 * features.insertFeature("NEW_PDTB_IsTemporal_Explicit_Post_Weighted",
		 * Double.TYPE);
		 * features.insertFeature("NEW_PDTB_IsTemporal_Implicit_Post_Weighted",
		 * Double.TYPE);
		 */
	}

	public void fillInVector(FeatureName features, Object[] featureVector,
			String featureName, HashSet<Integer> set, int type) {
		int fIndex = features.getIndex(featureName);
		if (set.contains(type)) {
			featureVector[fIndex] = Boolean.toString(true);
		} else {
			featureVector[fIndex] = Boolean.toString(false);
		}
	}

	public void fillInVector(FeatureName features, Object[] featureVector,
			String featureName, double value) {
		int fIndex = features.getIndex(featureName);
		featureVector[fIndex] = value;
	}

	public void extractSelectedFeatureWeightedGroup(FeatureName features,
			Object[] featureVector, PDTBGraph graph, String prefix,
			String postFix, boolean upwards, int sentenceIndex) {
		// IsEntRel, IsAltLex, IsComparison_Explicit, IsComparison_Implicit,
		// IsContigency_Explicit, IsContigency_Implicit, IsExpansion_Explicit,
		// IsExpansion_Implicit, IsTemporal_Explicit, IsTemporal_Implicit
		if (graph != null) {
			Hashtable<String, Double> weights = null;
			if (upwards)
				//weights = graph.getValueArg2(sentenceIndex);
				weights = graph.getOutGroupValueArg2(sentenceIndex);
			else
				//weights = graph.getValueArg1(sentenceIndex);
				weights = graph.getOutGroupValueArg1(sentenceIndex);
			
			String logStr = "index:"+sentenceIndex + "\n";
			Iterator<String> it = weights.keySet().iterator();
			while (it.hasNext()) {
				String key = it.next();
				double value = weights.get(key);
				logStr += key + ":" + value + ",";
			}
			//MyLogger.getInstance().log(logStr);
			
			fillInVector(features, featureVector, prefix + "PDTB_IsEntRel"
					+ postFix + "_Group", weights.get("EntRel"));
			fillInVector(features, featureVector, prefix + "PDTB_IsAltLex"
					+ postFix+ "_Group", weights.get("AltLex"));
			fillInVector(features, featureVector, prefix + "PDTB_IsComparison" + postFix+ "_Group",
					weights.get("Comparison"));
			fillInVector(features, featureVector, prefix + "PDTB_IsContingency" + postFix+ "_Group",
					weights.get("Contingency"));
			fillInVector(features, featureVector, prefix + "PDTB_IsExpansion" + postFix+ "_Group",
					weights.get("Expansion"));
			fillInVector(features, featureVector, prefix + "PDTB_IsTemporal" + postFix+ "_Group",
					weights.get("Temporal"));
		} else {
			fillInVector(features, featureVector, prefix + "PDTB_IsEntRel"
					+ postFix+ "_Group", 0);
			fillInVector(features, featureVector, prefix + "PDTB_IsAltLex"
					+ postFix+ "_Group", 0);
			fillInVector(features, featureVector, prefix + "PDTB_IsComparison" + postFix+ "_Group", 0);
			fillInVector(features, featureVector, prefix + "PDTB_IsContingency" + postFix+ "_Group", 0);
			fillInVector(features, featureVector, prefix + "PDTB_IsExpansion" + postFix+ "_Group", 0);
			fillInVector(features, featureVector, prefix + "PDTB_IsTemporal" + postFix+ "_Group", 0);
		}
	}
	
	public void extractSelectedFeature(FeatureName features,
			Object[] featureVector, HashSet<Integer> arg2Type_OLD,
			HashSet<Integer> arg2Type_NEW) {
		fillInVector(features, featureVector, "OLD_PDTB_IsEntRel",
				arg2Type_OLD, IND_entRelType);
		fillInVector(features, featureVector, "NEW_PDTB_IsEntRel",
				arg2Type_NEW, IND_entRelType);

		fillInVector(features, featureVector, "OLD_PDTB_IsAltLex",
				arg2Type_OLD, IND_altLexType);
		fillInVector(features, featureVector, "NEW_PDTB_IsAltLex",
				arg2Type_NEW, IND_altLexType);

		fillInVector(features, featureVector, "OLD_PDTB_IsComparison_Explicit",
				arg2Type_OLD, IND_comparisonType_EXP);
		fillInVector(features, featureVector, "OLD_PDTB_IsComparison_Implicit",
				arg2Type_OLD, IND_comparisonType_IMP);
		fillInVector(features, featureVector, "NEW_PDTB_IsComparison_Explicit",
				arg2Type_NEW, IND_comparisonType_EXP);
		fillInVector(features, featureVector, "NEW_PDTB_IsComparison_Implicit",
				arg2Type_NEW, IND_comparisonType_IMP);

		fillInVector(features, featureVector,
				"OLD_PDTB_IsContingency_Explicit", arg2Type_OLD,
				IND_contingencyType_EXP);
		fillInVector(features, featureVector,
				"OLD_PDTB_IsContingency_Implicit", arg2Type_OLD,
				IND_contingencyType_IMP);
		fillInVector(features, featureVector,
				"NEW_PDTB_IsContingency_Explicit", arg2Type_NEW,
				IND_contingencyType_EXP);
		fillInVector(features, featureVector,
				"NEW_PDTB_IsContingency_Implicit", arg2Type_NEW,
				IND_contingencyType_IMP);

		fillInVector(features, featureVector, "OLD_PDTB_IsExpansion_Explicit",
				arg2Type_OLD, IND_expansionType_EXP);
		fillInVector(features, featureVector, "OLD_PDTB_IsExpansion_Implicit",
				arg2Type_OLD, IND_expansionType_IMP);
		fillInVector(features, featureVector, "NEW_PDTB_IsExpansion_Explicit",
				arg2Type_NEW, IND_expansionType_EXP);
		fillInVector(features, featureVector, "NEW_PDTB_IsExpansion_Implicit",
				arg2Type_NEW, IND_expansionType_IMP);

		fillInVector(features, featureVector, "OLD_PDTB_IsTemporal_Explicit",
				arg2Type_OLD, IND_temporalType_EXP);
		fillInVector(features, featureVector, "OLD_PDTB_IsTemporal_Implicit",
				arg2Type_OLD, IND_temporalType_IMP);
		fillInVector(features, featureVector, "NEW_PDTB_IsTemporal_Explicit",
				arg2Type_NEW, IND_temporalType_EXP);
		fillInVector(features, featureVector, "NEW_PDTB_IsTemporal_Implicit",
				arg2Type_NEW, IND_temporalType_IMP);
	}

	public void extractSelectedFeaturePost(FeatureName features,
			Object[] featureVector, HashSet<Integer> arg1Type_OLD,
			HashSet<Integer> arg1Type_NEW) {
		fillInVector(features, featureVector, "OLD_PDTB_IsEntRel_Post",
				arg1Type_OLD, IND_entRelType);
		fillInVector(features, featureVector, "NEW_PDTB_IsEntRel_Post",
				arg1Type_NEW, IND_entRelType);

		fillInVector(features, featureVector, "OLD_PDTB_IsAltLex_Post",
				arg1Type_OLD, IND_altLexType);
		fillInVector(features, featureVector, "NEW_PDTB_IsAltLex_Post",
				arg1Type_NEW, IND_altLexType);

		fillInVector(features, featureVector,
				"OLD_PDTB_IsComparison_Explicit_Post", arg1Type_OLD,
				IND_comparisonType_EXP);
		fillInVector(features, featureVector,
				"OLD_PDTB_IsComparison_Implicit_Post", arg1Type_OLD,
				IND_comparisonType_IMP);
		fillInVector(features, featureVector,
				"NEW_PDTB_IsComparison_Explicit_Post", arg1Type_NEW,
				IND_comparisonType_EXP);
		fillInVector(features, featureVector,
				"NEW_PDTB_IsComparison_Implicit_Post", arg1Type_NEW,
				IND_comparisonType_IMP);

		fillInVector(features, featureVector,
				"OLD_PDTB_IsContingency_Explicit_Post", arg1Type_OLD,
				IND_contingencyType_EXP);
		fillInVector(features, featureVector,
				"OLD_PDTB_IsContingency_Implicit_Post", arg1Type_OLD,
				IND_contingencyType_IMP);
		fillInVector(features, featureVector,
				"NEW_PDTB_IsContingency_Explicit_Post", arg1Type_NEW,
				IND_contingencyType_EXP);
		fillInVector(features, featureVector,
				"NEW_PDTB_IsContingency_Implicit_Post", arg1Type_NEW,
				IND_contingencyType_IMP);

		fillInVector(features, featureVector,
				"OLD_PDTB_IsExpansion_Explicit_Post", arg1Type_OLD,
				IND_expansionType_EXP);
		fillInVector(features, featureVector,
				"OLD_PDTB_IsExpansion_Implicit_Post", arg1Type_OLD,
				IND_expansionType_IMP);
		fillInVector(features, featureVector,
				"NEW_PDTB_IsExpansion_Explicit_Post", arg1Type_NEW,
				IND_expansionType_EXP);
		fillInVector(features, featureVector,
				"NEW_PDTB_IsExpansion_Implicit_Post", arg1Type_NEW,
				IND_expansionType_IMP);

		fillInVector(features, featureVector,
				"OLD_PDTB_IsTemporal_Explicit_Post", arg1Type_OLD,
				IND_temporalType_EXP);
		fillInVector(features, featureVector,
				"OLD_PDTB_IsTemporal_Implicit_Post", arg1Type_OLD,
				IND_temporalType_IMP);
		fillInVector(features, featureVector,
				"NEW_PDTB_IsTemporal_Explicit_Post", arg1Type_NEW,
				IND_temporalType_EXP);
		fillInVector(features, featureVector,
				"NEW_PDTB_IsTemporal_Implicit_Post", arg1Type_NEW,
				IND_temporalType_IMP);
	}

	public void extractWeightedFeature(FeatureName features,
			Object[] featureVector, RevisionDocument doc,
			ArrayList<Integer> newIndexes, ArrayList<Integer> oldIndexes)
			throws IOException {
		String name = getRealNameRevision(doc.getDocumentName());
		// System.out.println(name);
		if (pdtbArg1Results_OLD.get(name).size() == 0
				&& pdtbArg2Results_OLD.get(name).size() == 0
				&& pdtbArg1Results_NEW.get(name).size() == 0
				&& pdtbArg2Results_NEW.get(name).size() == 0) {
			readInfo(doc);
		}

		int oldIndex = -1;
		int newIndex = -1;
		if (oldIndexes != null) {
			Collections.sort(oldIndexes);
			for (Integer i : oldIndexes) {
				if (i != -1) {
					oldIndex = i;
					break;
				}
			}
		}
		if (newIndexes != null) {
			Collections.sort(newIndexes);
			for (Integer i : newIndexes) {
				if (i != -1) {
					newIndex = i;
					break;
				}
			}
		}

//		Hashtable<Integer, Integer> pdtbArg1_OLD = pdtbArg1Results_OLD
//				.get(name);
//		Hashtable<Integer, Integer> pdtbArg2_OLD = pdtbArg2Results_OLD
//				.get(name);
//		Hashtable<Integer, Integer> pdtbArg1_NEW = pdtbArg1Results_NEW
//				.get(name);
//		Hashtable<Integer, Integer> pdtbArg2_NEW = pdtbArg2Results_NEW
//				.get(name);
		Hashtable<Integer, PDTBGraph> graph_OLD = graphDocIndex_OLD.get(name);
		Hashtable<Integer, PDTBGraph> graph_NEW = graphDocIndex_NEW.get(name);

		PDTBGraph oldGraph = null;
		PDTBGraph newGraph = null;
		if (oldIndex != -1) {
			int oldParagraph = doc.getParaNoOfOldSentence(oldIndex);
			oldGraph = graph_OLD.get(oldParagraph);
		}
		if (newIndex != -1) {
			int newParagraph = doc.getParaNoOfNewSentence(newIndex);
			newGraph = graph_NEW.get(newParagraph);
		}

		// Act as arg2, old
		String prefix = "OLD_";
		String postFix = "_Weighted";
		boolean upwards = true;
		extractSelectedFeatureWeighted(features, featureVector, oldGraph,
				prefix, postFix, upwards, oldIndex);
		extractSelectedFeatureWeightedGroup(features, featureVector, oldGraph,
				prefix, postFix, upwards, oldIndex);


		// Act as arg2, new
		prefix = "NEW_";
		postFix = "_Weighted";
		extractSelectedFeatureWeighted(features, featureVector, newGraph,
				prefix, postFix, upwards, newIndex);
		extractSelectedFeatureWeightedGroup(features, featureVector, newGraph,
				prefix, postFix, upwards, newIndex);

		prefix = "DIFF_";
		postFix = "_Weighted";
		extractSelectedFeatureWeightedDiff(features, featureVector, oldGraph,
				newGraph, prefix, postFix, upwards, oldIndex, newIndex);

		// Act as arg1, old
		prefix = "OLD_";
		postFix = "_Post_Weighted";
		upwards = false;
		extractSelectedFeatureWeighted(features, featureVector, oldGraph,
				prefix, postFix, upwards, oldIndex);
		extractSelectedFeatureWeightedGroup(features, featureVector, oldGraph,
				prefix, postFix, upwards, oldIndex);

		prefix = "NEW_";
		postFix = "_Post_Weighted";
		// Act as arg1, new
		extractSelectedFeatureWeighted(features, featureVector, newGraph,
				prefix, postFix, upwards, newIndex);
		extractSelectedFeatureWeightedGroup(features, featureVector, newGraph,
				prefix, postFix, upwards, newIndex);

		prefix = "DIFF_";
		postFix = "_Post_Weighted";
		extractSelectedFeatureWeightedDiff(features, featureVector, oldGraph,
				newGraph, prefix, postFix, upwards, oldIndex, newIndex);

	}

	public void extractWeightedFeatureTree(FeatureName features,
			Object[] featureVector, RevisionDocument doc,
			ArrayList<Integer> newIndexes, ArrayList<Integer> oldIndexes,
			int levels) throws IOException {
		String name = getRealNameRevision(doc.getDocumentName());
		// System.out.println(name);
		if (pdtbArg1Results_OLD.get(name).size() == 0
				&& pdtbArg2Results_OLD.get(name).size() == 0
				&& pdtbArg1Results_NEW.get(name).size() == 0
				&& pdtbArg2Results_NEW.get(name).size() == 0) {
			readInfo(doc);
		}

		int oldIndex = -1;
		int newIndex = -1;
		if (oldIndexes != null) {
			Collections.sort(oldIndexes);
			for (Integer i : oldIndexes) {
				if (i != -1) {
					oldIndex = i;
					break;
				}
			}
		}
		if (newIndexes != null) {
			Collections.sort(newIndexes);
			for (Integer i : newIndexes) {
				if (i != -1) {
					newIndex = i;
					break;
				}
			}
		}

		// Hashtable<Integer, Integer> pdtbArg1_OLD = pdtbArg1Results_OLD
		// .get(name);
		// Hashtable<Integer, Integer> pdtbArg2_OLD = pdtbArg2Results_OLD
		// .get(name);
		// Hashtable<Integer, Integer> pdtbArg1_NEW = pdtbArg1Results_NEW
		// .get(name);
		// Hashtable<Integer, Integer> pdtbArg2_NEW = pdtbArg2Results_NEW
		// .get(name);
		Hashtable<Integer, PDTBTree> tree_OLD = treeDocIndex_OLD.get(name);
		Hashtable<Integer, PDTBTree> tree_NEW = treeDocIndex_NEW.get(name);

		PDTBTree oldTree = null;
		PDTBTree newTree = null;
		if (oldIndex != -1) {
			int oldParagraph = doc.getParaNoOfOldSentence(oldIndex);
			oldTree = tree_OLD.get(oldParagraph);
		}
		if (newIndex != -1) {
			int newParagraph = doc.getParaNoOfNewSentence(newIndex);
			newTree = tree_NEW.get(newParagraph);
		}

		// Act as arg2, old
		String prefix = "OLD_";
		String postFix = "_Weighted";
		boolean upwards = true;
		extractSelectedFeatureWeightedTree(features, featureVector, oldTree,
				prefix, postFix, upwards, oldIndex, levels);

		// Act as arg2, new
		prefix = "NEW_";
		postFix = "_Weighted";
		extractSelectedFeatureWeightedTree(features, featureVector, newTree,
				prefix, postFix, upwards, newIndex, levels);

		// Act as arg1, old
		prefix = "OLD_";
		postFix = "_Post_Weighted";
		upwards = false;
		extractSelectedFeatureWeightedTree(features, featureVector, oldTree,
				prefix, postFix, upwards, oldIndex, levels);

		prefix = "NEW_";
		postFix = "_Post_Weighted";
		// Act as arg1, new
		extractSelectedFeatureWeightedTree(features, featureVector, newTree,
				prefix, postFix, upwards, newIndex, levels);
	}
	
	public void extractSelectedFeatureWeighted(FeatureName features,
			Object[] featureVector, PDTBGraph graph, String prefix,
			String postFix, boolean upwards, int sentenceIndex) {
		// IsEntRel, IsAltLex, IsComparison_Explicit, IsComparison_Implicit,
		// IsContigency_Explicit, IsContigency_Implicit, IsExpansion_Explicit,
		// IsExpansion_Implicit, IsTemporal_Explicit, IsTemporal_Implicit
		if (graph != null) {
			Hashtable<String, Double> weights = null;
			if (upwards)
				weights = graph.getValueArg2(sentenceIndex);
			else
				weights = graph.getValueArg1(sentenceIndex);
			
			String logStr = "index:"+sentenceIndex + "\n";
			Iterator<String> it = weights.keySet().iterator();
			while (it.hasNext()) {
				String key = it.next();
				double value = weights.get(key);
				logStr += key + ":" + value + ",";
			}
			//MyLogger.getInstance().log(logStr);
			
			fillInVector(features, featureVector, prefix + "PDTB_IsEntRel"
					+ postFix, weights.get("EntRel"));
			fillInVector(features, featureVector, prefix + "PDTB_IsAltLex"
					+ postFix, weights.get("AltLex"));
			fillInVector(features, featureVector, prefix + "PDTB_IsComparison" + postFix,
					weights.get("Comparison"));
			fillInVector(features, featureVector, prefix + "PDTB_IsContingency" + postFix,
					weights.get("Contingency"));
			fillInVector(features, featureVector, prefix + "PDTB_IsExpansion" + postFix,
					weights.get("Expansion"));
			fillInVector(features, featureVector, prefix + "PDTB_IsTemporal" + postFix,
					weights.get("Temporal"));
		} else {
			fillInVector(features, featureVector, prefix + "PDTB_IsEntRel"
					+ postFix, 0);
			fillInVector(features, featureVector, prefix + "PDTB_IsAltLex"
					+ postFix, 0);
			fillInVector(features, featureVector, prefix + "PDTB_IsComparison" + postFix, 0);
			fillInVector(features, featureVector, prefix + "PDTB_IsContingency" + postFix, 0);
			fillInVector(features, featureVector, prefix + "PDTB_IsExpansion" + postFix, 0);
			fillInVector(features, featureVector, prefix + "PDTB_IsTemporal" + postFix, 0);
		}
	}

	public double getValue(Hashtable<String, Double> table, String key) {
		if (table.containsKey(key)) {
			return table.get(key);
		} else {
			return 0;
		}
	}
	
	public void extractSelectedFeatureWeightedTree(FeatureName features,
			Object[] featureVector, PDTBTree tree, String prefix,
			String postFix, boolean upwards, int sentenceIndex, int levels) {
		// IsEntRel, IsAltLex, IsComparison_Explicit, IsComparison_Implicit,
		// IsContigency_Explicit, IsContigency_Implicit, IsExpansion_Explicit,
		// IsExpansion_Implicit, IsTemporal_Explicit, IsTemporal_Implicit
		if (tree != null) {
			Hashtable<String, Double> weights = null;
			if (upwards)
				weights = tree.getValueArg2(sentenceIndex);
			else
				weights = tree.getValueArg1(sentenceIndex);

			String logStr = "Weights: index:" + sentenceIndex + "\n";
			Iterator<String> it = weights.keySet().iterator();
			while (it.hasNext()) {
				String key = it.next();
				double value = weights.get(key);
				logStr += key + ":" + value + ",";
			}
			MyLogger.getInstance().log(logStr);
			for (int i = 1; i <= levels; i++) {
				String key = "_Level_" + i;
				fillInVector(features, featureVector, prefix + "PDTB_IsEntRel"
						+ postFix + key, getValue(weights, "EntRel-" + i));
				fillInVector(features, featureVector, prefix + "PDTB_IsAltLex"
						+ postFix + key, getValue(weights, "AltLex-" + i));
				fillInVector(features, featureVector, prefix
						+ "PDTB_IsComparison" + postFix + key,
						getValue(weights, "Comparison-" + i));
				fillInVector(features, featureVector, prefix
						+ "PDTB_IsContingency" + postFix + key,
						getValue(weights, "Contingency-" + i));
				fillInVector(features, featureVector, prefix
						+ "PDTB_IsExpansion" + postFix + key,
						getValue(weights, "Expansion-" + i));
				fillInVector(features, featureVector, prefix
						+ "PDTB_IsTemporal" + postFix + key,
						getValue(weights, "Temporal-" + i));
			}
		} else {
			for (int i = 1; i <= levels; i++) {
				String key = "_Level_" + i;
				fillInVector(features, featureVector, prefix + "PDTB_IsEntRel"
						+ postFix + key, 0);
				fillInVector(features, featureVector, prefix + "PDTB_IsAltLex"
						+ postFix + key, 0);
				fillInVector(features, featureVector, prefix
						+ "PDTB_IsComparison" + postFix + key, 0);
				fillInVector(features, featureVector, prefix
						+ "PDTB_IsContingency" + postFix + key, 0);
				fillInVector(features, featureVector, prefix
						+ "PDTB_IsExpansion" + postFix + key, 0);
				fillInVector(features, featureVector, prefix
						+ "PDTB_IsTemporal" + postFix + key, 0);
			}
		}
	}
	
	public void extractSelectedFeatureWeightedDiff(FeatureName features,
			Object[] featureVector, PDTBGraph oldGraph, PDTBGraph newGraph, String prefix,
			String postFix, boolean upwards, int oldIndex, int newIndex) {
		// IsEntRel, IsAltLex, IsComparison_Explicit, IsComparison_Implicit,
		// IsContigency_Explicit, IsContigency_Implicit, IsExpansion_Explicit,
		// IsExpansion_Implicit, IsTemporal_Explicit, IsTemporal_Implicit

		double entRel_OLD = 0;
		double altLex_OLD = 0;
		double comparison_OLD = 0;
		double contingency_OLD = 0;
		double expansion_OLD = 0;
		double temporal_OLD = 0;
		
		double entRel_NEW = 0;
		double altLex_NEW = 0;
		double comparison_NEW = 0;
		double contingency_NEW = 0;
		double expansion_NEW = 0;
		double temporal_NEW = 0;
		
		if(oldGraph!=null) {
			Hashtable<String, Double> weights = null;
			if(upwards) weights = oldGraph.getValueArg2(oldIndex);
			else weights = oldGraph.getValueArg1(oldIndex);
			entRel_OLD = weights.get("EntRel");
			altLex_OLD = weights.get("AltLex");
			comparison_OLD = weights.get("Comparison");
			contingency_OLD = weights.get("Contingency");
			expansion_OLD = weights.get("Expansion");
			temporal_OLD = weights.get("Temporal");
		}
		
		if(newGraph!=null) {
			Hashtable<String, Double> weights = null;
			if(upwards) weights = newGraph.getValueArg2(newIndex);
			else weights = newGraph.getValueArg1(newIndex);
			entRel_NEW = weights.get("EntRel");
			altLex_NEW = weights.get("AltLex");
			comparison_NEW = weights.get("Comparison");
			contingency_NEW = weights.get("Contingency");
			expansion_NEW = weights.get("Expansion");
			temporal_NEW = weights.get("Temporal");
		}
		fillInVector(features, featureVector, prefix + "PDTB_IsEntRel"
				+ postFix, entRel_NEW-entRel_OLD);
		fillInVector(features, featureVector, prefix + "PDTB_IsAltLex"
				+ postFix, altLex_NEW - altLex_OLD);
		fillInVector(features, featureVector, prefix + "PDTB_IsComparison" + postFix,
				comparison_NEW - comparison_OLD);
		fillInVector(features, featureVector, prefix + "PDTB_IsContingency" + postFix,
				contingency_NEW - contingency_OLD);
		fillInVector(features, featureVector, prefix + "PDTB_IsExpansion" + postFix, expansion_NEW-expansion_OLD);
		fillInVector(features, featureVector, prefix + "PDTB_IsTemporal" + postFix, temporal_NEW-temporal_OLD);
	}

	public void extractFeature(FeatureName features, Object[] featureVector,
			RevisionDocument doc, ArrayList<Integer> newIndexes,
			ArrayList<Integer> oldIndexes) throws IOException {
		String name = getRealNameRevision(doc.getDocumentName());
		// System.out.println(name);
		if (pdtbArg1Results_OLD.get(name).size() == 0
				&& pdtbArg2Results_OLD.get(name).size() == 0
				&& pdtbArg1Results_NEW.get(name).size() == 0
				&& pdtbArg2Results_NEW.get(name).size() == 0) {
			readInfo(doc);
		}

		if (oldIndexes.size() == 0 && newIndexes.size() == 0) {
			// System.out.println("UNEXPECTED CASES IN :"+
			// doc.getDocumentName());
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
		Hashtable<Integer, Integer> pdtbOwn_OLD = pdtbOwnResults_NEW.get(name);
		Hashtable<Integer, Integer> pdtbOwn_NEW = pdtbOwnResults_NEW.get(name);

		Hashtable<Integer, PDTBGraph> graph_OLD = graphDocIndex_OLD.get(name);
		Hashtable<Integer, PDTBGraph> graph_NEW = graphDocIndex_NEW.get(name);

		for (Integer oldIndex : oldIndexes) {
			arg1Type_OLD.add(pdtbArg1_OLD.get(oldIndex));
			arg2Type_OLD.add(pdtbArg2_OLD.get(oldIndex));

			arg1Type_OLD.add(pdtbOwn_OLD.get(oldIndex));
			arg2Type_OLD.add(pdtbOwn_OLD.get(oldIndex));

		}
		for (Integer newIndex : newIndexes) {
			arg1Type_NEW.add(pdtbArg1_NEW.get(newIndex));
			arg2Type_NEW.add(pdtbArg2_NEW.get(newIndex));

			arg1Type_NEW.add(pdtbOwn_NEW.get(newIndex));
			arg2Type_NEW.add(pdtbOwn_NEW.get(newIndex));
		}

		extractSelectedFeature(features, featureVector, arg2Type_OLD,
				arg2Type_NEW);
		extractSelectedFeaturePost(features, featureVector, arg1Type_OLD,
				arg1Type_NEW);
		extractPDTBVectorFeature(features, featureVector, doc, newIndexes,
				oldIndexes);
		/*
		 * extractPDTBEntityGridFeature(features, featureVector, doc,
		 * newIndexes, oldIndexes);
		 */
	}

	public void extractPDTBEntityGridFeature(FeatureName features,
			Object[] featureVector, RevisionDocument doc,
			ArrayList<Integer> newIndexes, ArrayList<Integer> oldIndexes)
			throws IOException {
		String name = getRealNameRevision(doc.getDocumentName());
		System.out.println("FILE NAME:" + name);
		if (pdtbArg1Results_OLD.get(name).size() == 0
				&& pdtbArg2Results_OLD.get(name).size() == 0
				&& pdtbArg1Results_NEW.get(name).size() == 0
				&& pdtbArg2Results_NEW.get(name).size() == 0) {
			readInfo(doc);
		}

		int old_index = 0;
		Collections.sort(oldIndexes);
		for (Integer oldIndex : oldIndexes) {
			if (oldIndex != -1) {
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

		Hashtable<String, PDTBEntityGrid> gridTable = gridTables.get(name);

		for (int i = 0; i < topicNum; i++) {
			String topicName = "Topic-" + i;
			PDTBEntityGrid grid = gridTable.get(topicName);
			System.out.println("TOPIC NAME:" + topicName);
			String tagName = topicName + "-Weight-";
			// grid.setValue(features, featureVector, topicName, old_index,
			// true);
			// grid.setValue(features, featureVector, topicName, new_index,
			// false);
			grid.setValueWeight(features, featureVector, tagName, old_index,
					true);
			grid.setValueWeight(features, featureVector, tagName, new_index,
					false);
			grid.setValueWeightDiff(features, featureVector, tagName,
					old_index, new_index);
		}
		PDTBEntityGrid grid = gridTable.get("Merged");
		// String topicName = "Merged";
		// grid.setValue(features, featureVector, topicName, old_index, true);
		// grid.setValue(features, featureVector, topicName, new_index, false);
		// grid.setValueCount(features, featureVector, "MergeCount", old_index,
		// true);
		// grid.setValueCount(features, featureVector, "MergeCount", new_index,
		// false);
		String tagName = "Merged" + "-Weight-";
		grid.setValueWeight(features, featureVector, tagName, old_index, true);
		grid.setValueWeight(features, featureVector, tagName, new_index, false);
		grid.setValueWeightDiff(features, featureVector, tagName, old_index,
				new_index);
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

	public void insertPatternFeature(FeatureName features, int patternLength) {
		ArrayList<Object> options = new ArrayList<Object>();
		List<String> strOptions = new ArrayList<String>();
		List<String> singleOptions = new ArrayList<String>();
		singleOptions.add("NULL");
		singleOptions.add("EntRel");
		singleOptions.add("Contingency");
		singleOptions.add("Expansion");
		singleOptions.add("Comparison");
		singleOptions.add("Temporal");
		singleOptions.add("AltLex");
		int patternLengthHalf = patternLength / 2 + 1;
		strOptions = generatePatterns(strOptions, singleOptions,
				patternLengthHalf);
		for (String str : strOptions) {
			options.add(str);
		}
		features.insertFeature("OLD_PDTB_PATTERN_PRIOR", options);
		features.insertFeature("NEW_PDTB_PATTERN_PRIOR", options);
		features.insertFeature("OLD_PDTB_PATTERN_POST", options);
		features.insertFeature("NEW_PDTB_PATTERN_POST", options);
	}

	public void insertPDTBVectorFeature(FeatureName features) {
		features.insertFeature("OLD_PDTB_DIM_ENTREL", Double.class);
		features.insertFeature("OLD_PDTB_DIM_ALTLEX", Double.class);
		features.insertFeature("OLD_PDTB_DIM_COMPARISON", Double.class);
		features.insertFeature("OLD_PDTB_DIM_CONTINGENCY", Double.class);
		features.insertFeature("OLD_PDTB_DIM_EXPANSION", Double.class);
		features.insertFeature("OLD_PDTB_DIM_TEMPORAL", Double.class);

		features.insertFeature("NEW_PDTB_DIM_ENTREL", Double.class);
		features.insertFeature("NEW_PDTB_DIM_ALTLEX", Double.class);
		features.insertFeature("NEW_PDTB_DIM_COMPARISON", Double.class);
		features.insertFeature("NEW_PDTB_DIM_CONTINGENCY", Double.class);
		features.insertFeature("NEW_PDTB_DIM_EXPANSION", Double.class);
		features.insertFeature("NEW_PDTB_DIM_TEMPORAL", Double.class);

		features.insertFeature("CHANGE_PDTB_DIM_ENTREL", Double.class);
		features.insertFeature("CHANGE_PDTB_DIM_ALTLEX", Double.class);
		features.insertFeature("CHANGE_PDTB_DIM_COMPARISON", Double.class);
		features.insertFeature("CHANGE_PDTB_DIM_CONTINGENCY", Double.class);
		features.insertFeature("CHANGE_PDTB_DIM_EXPANSION", Double.class);
		features.insertFeature("CHANGE_PDTB_DIM_TEMPORAL", Double.class);
	}

	public void extractPDTBVectorFeature(FeatureName features,
			Object[] featureVector, RevisionDocument doc,
			ArrayList<Integer> newIndexes, ArrayList<Integer> oldIndexes)
			throws IOException {
		String name = getRealNameRevision(doc.getDocumentName());
		System.out.println("FILE NAME:" + name);
		if (pdtbArg1Results_OLD.get(name).size() == 0
				&& pdtbArg2Results_OLD.get(name).size() == 0
				&& pdtbArg1Results_NEW.get(name).size() == 0
				&& pdtbArg2Results_NEW.get(name).size() == 0) {
			readInfo(doc);
		}

		Hashtable<Integer, Integer> pdtbArg1_OLD = pdtbArg1Results_OLD
				.get(name);
		Hashtable<Integer, Integer> pdtbArg2_OLD = pdtbArg2Results_OLD
				.get(name);
		Hashtable<Integer, Integer> pdtbArg1_NEW = pdtbArg1Results_NEW
				.get(name);
		Hashtable<Integer, Integer> pdtbArg2_NEW = pdtbArg2Results_NEW
				.get(name);

		int old_index = 0;
		Collections.sort(oldIndexes);
		for (Integer oldIndex : oldIndexes) {
			if (oldIndex != -1) {
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

		double[] oldVecs = new double[6];
		double[] newVecs = new double[6];
		double[] changeVecs = new double[6];
		if (old_index > 0) {
			oldVecs = extractVector(doc, old_index, true, pdtbArg1_OLD,
					pdtbArg2_OLD);
			changeVecs = extractChangeVector(doc, old_index, true,
					pdtbArg1_OLD, pdtbArg2_OLD);
		}

		if (new_index > 0) {
			newVecs = extractVector(doc, new_index, false, pdtbArg1_NEW,
					pdtbArg2_NEW);
			if (changeVecs == null)
				changeVecs = extractChangeVector(doc, new_index, false,
						pdtbArg1_NEW, pdtbArg2_NEW);
		}

		for (double vec : newVecs) {
			System.out.print(vec + "\t");
		}
		System.out.println();

		String[] names = { "ENTREL", "ALTLEX", "COMPARISON", "CONTINGENCY",
				"EXPANSION", "TEMPORAL" };
		for (int i = 0; i < 6; i++) {
			String fName = "OLD_PDTB_DIM_" + names[i];
			int fIndex = features.getIndex(fName);
			featureVector[fIndex] = oldVecs[i];
		}
		for (int i = 0; i < 6; i++) {
			String fName = "NEW_PDTB_DIM_" + names[i];
			int fIndex = features.getIndex(fName);
			featureVector[fIndex] = newVecs[i];
		}

		for (int i = 0; i < 6; i++) {
			String fName = "CHANGE_PDTB_DIM_" + names[i];
			int fIndex = features.getIndex(fName);
			featureVector[fIndex] = changeVecs[i];
		}
	}

	public void setVector(double[] vector, int argTag) {
		if (argTag == IND_entRelType) {
			vector[0] = vector[0] + 1;
		} else if (argTag == IND_altLexType) {
			vector[1] = vector[1] + 1;
		} else if (argTag == IND_comparisonType_EXP
				|| argTag == IND_comparisonType_IMP) {
			vector[2] = vector[2] + 1;
		} else if (argTag == IND_contingencyType_EXP
				|| argTag == IND_contingencyType_IMP) {
			vector[3] = vector[3] + 1;
		} else if (argTag == IND_expansionType_EXP
				|| argTag == IND_expansionType_IMP) {
			vector[4] = vector[4] + 1;
		} else if (argTag == IND_temporalType_EXP
				|| argTag == IND_temporalType_IMP) {
			vector[5] = vector[5] + 1;
		}
	}

	public void normalize(double[] vector) {
		double divide = 0;
		for (double value : vector) {
			divide += value * value;
		}
		if (divide == 0)
			return;
		divide = Math.sqrt(divide);
		for (int i = 0; i < vector.length; i++) {
			vector[i] = vector[i] / divide;
		}
	}

	public double[] extractVector(RevisionDocument doc, int index,
			boolean isOld, Hashtable<Integer, Integer> arg1Relations,
			Hashtable<Integer, Integer> arg2Relations) {
		List<Integer> indices = getParagraphBeforeIndex(doc, index, isOld);
		double[] vector = new double[6];
		for (Integer i : indices) {
			int argTag = -1;
			if (arg1Relations.containsKey(i)) {
				argTag = arg1Relations.get(i);
			} else {
				if (arg2Relations.containsKey(i + 1)) {
					argTag = arg2Relations.get(i + 1);
				}
			}
			setVector(vector, argTag);
		}
		// normalize(vector);
		return vector;
	}

	public double[] extractChangeVector(RevisionDocument doc, int index,
			boolean isOld, Hashtable<Integer, Integer> arg1Relations,
			Hashtable<Integer, Integer> arg2Relations) {
		List<Integer> otherSide = getIndicesOfTheOtherside(doc, index, isOld);

		double[] vec = null;

		if (otherSide == null || otherSide.size() == 0) {
			vec = extractVector(doc, index, isOld, arg1Relations, arg2Relations);
		} else {
			Collections.sort(otherSide);
			int otherIndex = otherSide.get(0);
			double[] vecA = extractVector(doc, otherIndex, !isOld,
					arg1Relations, arg2Relations);
			double[] vecB = extractVector(doc, index, isOld, arg1Relations,
					arg2Relations);
			if (isOld) {
				vec = minusVec(vecA, vecB);
			} else {
				vec = minusVec(vecB, vecA);
			}
		}
		normalize(vec);
		return vec;
	}

	public double[] minusVec(double[] a, double[] b) {
		double[] c = new double[a.length];
		for (int i = 0; i < a.length; i++) {
			c[i] = a[i] - b[i];
		}
		return c;
	}

	public List<Integer> getIndicesOfTheOtherside(RevisionDocument doc,
			int index, boolean isOld) {
		ArrayList<Integer> otherSide = null;
		List<Integer> befores = getParagraphBeforeIndex(doc, index, isOld);
		Collections.sort(befores);
		int currentIndex = index;
		int step = 0;
		if (isOld) {
			while (otherSide == null) {
				otherSide = doc.getNewFromOld(currentIndex);
				if (otherSide == null || otherSide.size() == 0
						|| (otherSide.size() == 1 && otherSide.get(0) == -1)) {
					otherSide = null;
					int i = befores.size() - step - 1;
					if (i < 0)
						break;
					else {
						currentIndex = befores.get(i);
						step++;
					}
				}
			}
		} else {
			otherSide = doc.getOldFromNew(currentIndex);
			while (otherSide == null) {
				otherSide = doc.getOldFromNew(currentIndex);
				if (otherSide == null || otherSide.size() == 0
						|| (otherSide.size() == 1 && otherSide.get(0) == -1)) {
					otherSide = null;
					int i = befores.size() - step - 1;
					if (i < 0)
						break;
					else {
						currentIndex = befores.get(i);
						step++;
					}
				}
			}
		}

		if (otherSide != null) {
			Iterator<Integer> it = otherSide.iterator();
			while (it.hasNext()) {
				int value = it.next();
				if (value == -1)
					it.remove();
			}
		}
		return otherSide;
	}

	public List<Integer> getParagraphBeforeIndex(RevisionDocument doc,
			int index, boolean isOld) {
		List<Integer> indices = new ArrayList<Integer>();
		if (isOld) {
			int paragraphNo = doc.getParaNoOfOldSentence(index);
			int paraStart = doc.getFirstOfOldParagraph(paragraphNo);
			while (paraStart < index) {
				indices.add(paraStart);
				paraStart++;
			}
		} else {
			int paragraphNo = doc.getParaNoOfNewSentence(index);
			int paraStart = doc.getFirstOfNewParagraph(paragraphNo);
			while (paraStart < index) {
				indices.add(paraStart);
				paraStart++;
			}
		}
		return indices;
	}

	public void extractPatternFeature(FeatureName features,
			Object[] featureVector, RevisionDocument doc,
			ArrayList<Integer> newIndexes, ArrayList<Integer> oldIndexes,
			int patternLength) throws IOException {
		String name = getRealNameRevision(doc.getDocumentName());
		System.out.println("FILE NAME:" + name);
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
			if (oldIndex != -1) {
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
			sbOld.append(getArgName(doc, true, i, old_index, pdtbArg1_OLD));
			if (i != old_index) {
				sbOld.append("-");
			}
		}
		String patternOld = sbOld.toString();
		String fName = "OLD_PDTB_PATTERN_PRIOR";
		int fIndex = features.getIndex(fName);
		featureVector[fIndex] = patternOld;

		StringBuffer sbOldPost = new StringBuffer();
		for (int i = old_index; i <= old_index + upwards; i++) {
			sbOldPost.append(getArgName(doc, true, i, old_index, pdtbArg1_OLD));
			if (i != old_index + upwards) {
				sbOldPost.append("-");
			}
		}
		String patternOldPost = sbOldPost.toString();
		fName = "OLD_PDTB_PATTERN_POST";
		fIndex = features.getIndex(fName);
		featureVector[fIndex] = patternOldPost;

		StringBuffer sbNew = new StringBuffer();
		for (int i = new_index - upwards; i <= new_index; i++) {
			int indent = 0;
			sbNew.append(getArgName(doc, false, i, new_index, pdtbArg1_NEW));
			if (i != new_index) {
				sbNew.append("-");
			}
		}
		String patternNew = sbNew.toString();
		fName = "NEW_PDTB_PATTERN_PRIOR";
		fIndex = features.getIndex(fName);
		featureVector[fIndex] = patternNew;

		StringBuffer sbNewPost = new StringBuffer();
		for (int i = new_index; i <= new_index + upwards; i++) {
			int indent = 0;
			sbNewPost
					.append(getArgName(doc, false, i, new_index, pdtbArg1_NEW));
			if (i != new_index + upwards) {
				sbNewPost.append("-");
			}
		}
		String patternNewPost = sbNewPost.toString();
		fName = "NEW_PDTB_PATTERN_POST";
		fIndex = features.getIndex(fName);
		featureVector[fIndex] = patternNewPost;
	}

	public String getArgName(RevisionDocument doc, boolean isOld, int index,
			int indent, Hashtable<Integer, Integer> pdtbArgs) {
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

		if (testIndex < 1 || testIndex > sentenceNum)
			return name;

		if (isOld) {
			paraNo = doc.getParaNoOfOldSentence(testIndex);
			currentParaNo = doc.getParaNoOfOldSentence(indent);
		} else {
			paraNo = doc.getParaNoOfNewSentence(testIndex);
			currentParaNo = doc.getParaNoOfNewSentence(indent);
		}

		if (paraNo != currentParaNo)
			return name;
		if (!pdtbArgs.containsKey(testIndex)) {
			return name;
		} else {
			int tag = pdtbArgs.get(testIndex);
			if (tag == IND_entRelType) {
				name = "EntRel";
			} else if (tag == IND_comparisonType_EXP
					|| tag == IND_comparisonType_IMP) {
				name = "Comparison";
			} else if (tag == IND_contingencyType_EXP
					|| tag == IND_contingencyType_IMP) {
				name = "Contingency";
			} else if (tag == IND_expansionType_EXP
					|| tag == IND_expansionType_IMP) {
				name = "Expansion";
			} else if (tag == IND_temporalType_EXP
					|| tag == IND_temporalType_IMP) {
				name = "Temporal";
			} else if (tag == IND_altLexType) {
				name = "AltLex";
			}
			return name;
		}
	}

	public List<String> generatePatterns(List<String> options,
			List<String> patterns, int length) {
		if (length == 0) {
			return options;
		} else {
			List<String> newOptions = new ArrayList<String>();
			for (String option : options) {
				for (String pattern : patterns) {
					newOptions.add(option + "-" + pattern);
				}
			}
			if (options.size() == 0) {
				for (String pattern : patterns) {
					newOptions.add(pattern);
				}
			}
			return generatePatterns(newOptions, patterns, length - 1);
		}
	}

	public void extractFeatureARG1ARG2(FeatureName features,
			Object[] featureVector, RevisionDocument doc,
			ArrayList<Integer> newIndexes, ArrayList<Integer> oldIndexes)
			throws IOException {
		String name = getRealNameRevision(doc.getDocumentName());
		System.out.println("FILE NAME:" + name);
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
			ArrayList<Integer> oldIndexes) throws IOException {
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
