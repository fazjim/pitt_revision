package edu.pitt.cs.revision.purpose;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.PriorityQueue;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import edu.pitt.cs.revision.batch.BatchFeatureReader;
import edu.pitt.cs.revision.batch.BatchFeatureWriter;
import edu.pitt.cs.revision.batch.InfoStore;
import edu.pitt.cs.revision.batch.SentenceInfo;
import edu.pitt.cs.revision.joint.EditStep;
import edu.pitt.cs.revision.machinelearning.FeatureName;
import edu.pitt.cs.revision.machinelearning.JOrthoAssist;
import edu.pitt.cs.revision.machinelearning.StanfordParserAssist;
import edu.pitt.lrdc.cs.revision.alignment.model.HeatMapUnit;
import edu.pitt.lrdc.cs.revision.model.RevisionDocument;
import edu.pitt.lrdc.cs.revision.model.RevisionOp;
import edu.pitt.lrdc.cs.revision.model.RevisionPurpose;
import edu.pitt.lrdc.cs.revision.model.RevisionUnit;
import edu.stanford.nlp.util.CoreMap;

/**
 * Extracts a given record into a feature vector
 * 
 * @author zhangfan
 *
 */
public class SequenceFeatureExtractor {
	StanfordParserAssist spa;
	JOrthoAssist ja;
	FeatureName features;
	Object[] featureVector;

	public FeatureName getFeatures() {
		return features;
	}

	public SequenceFeatureExtractor() {
		// spa = new StanfordParserAssist();
		// ja = new JOrthoAssist();
		features = new FeatureName();
	}

	private boolean batchMode = false;
	private InfoStore store;

	public void openBatchMode(String path) throws IOException {
		store = new InfoStore();
		store = BatchFeatureReader.readInfo(path);
		batchMode = true;
	}

	public String extractOldSentence(RevisionDocument doc,
			ArrayList<Integer> oldIndexes, int k) {
		String sentence = "";
		if (oldIndexes != null) {
			for (Integer oldIndex : oldIndexes) {
				oldIndex = oldIndex + (k - 1);
				if (oldIndex > 0
						&& oldIndex <= doc.getOldDraftSentences().size()) {
					sentence += doc.getOldSentence(oldIndex);
				}
			}
		}
		return sentence;
	}

	public String extractNewSentence(RevisionDocument doc,
			ArrayList<Integer> newIndexes, int k) {
		String sentence = "";
		if (newIndexes != null) {
			for (Integer newIndex : newIndexes) {
				newIndex = newIndex + (k - 1);
				if (newIndex > 0
						&& newIndex <= doc.getNewDraftSentences().size()) {
					sentence += doc.getNewSentence(newIndex);
				}
			}
		}
		return sentence;
	}

	public String extractSentence(RevisionDocument doc,
			ArrayList<Integer> newIndexes, ArrayList<Integer> oldIndexes, int k) {
		String sentence = "";
		if (newIndexes != null) {
			for (Integer newIndex : newIndexes) {
				newIndex = newIndex + (k - 1);
				if (newIndex > 0
						&& newIndex <= doc.getNewDraftSentences().size()) {
					sentence += doc.getNewSentence(newIndex);
				}
			}
		}
		if(sentence.length()>0) sentence += " ";
		if (oldIndexes != null) {
			for (Integer oldIndex : oldIndexes) {
				oldIndex = oldIndex + (k - 1);
				if (oldIndex > 0
						&& oldIndex <= doc.getOldDraftSentences().size()) {
					sentence += doc.getOldSentence(oldIndex);
				}
			}
		}

		if (sentence.length() == 0)
			sentence = "Dummy";
		return sentence;
	}

	/**
	 * Extracting the sentence from the text
	 * 
	 * @param doc
	 * @param ru
	 * @return
	 */
	public String extractSentence(RevisionDocument doc,
			ArrayList<Integer> newIndexes, ArrayList<Integer> oldIndexes,
			int k, boolean isNew) {
		String sentence = "";
		if (isNew && newIndexes != null) {
			for (Integer newIndex : newIndexes) {
				newIndex = newIndex + (k - 1);
				if (newIndex > 0
						&& newIndex <= doc.getNewDraftSentences().size()) {
					sentence += doc.getNewSentence(newIndex);
				}
			}
		} else if (!isNew && oldIndexes != null) {
			if (oldIndexes != null) {
				for (Integer oldIndex : oldIndexes) {
					oldIndex = oldIndex + (k - 1);
					if (oldIndex > 0
							&& oldIndex <= doc.getOldDraftSentences().size()) {
						sentence += doc.getOldSentence(oldIndex);
					}
				}
			}
		} else {

		}
		if (sentence.length() == 0)
			sentence = "Dummy";
		return sentence;
	}

	// --------------------------incoprate Johannes Daxenberger ad Iryna
	// Gurevych ----------------------------------
	public void insertDaTextualFeature(int k) {
		for (int i = 1; i <= k; i++) {
			for (int j = 1; j <= k; j++) {
				String postFix = "_" + i + "_" + j;
				features.insertFeature("DIFF_CHANGED" + postFix, Boolean.TYPE);
				features.insertFeature("DIFF_CAPITAL" + postFix, Double.TYPE);
				features.insertFeature("DIFF_DIGIT" + postFix, Double.TYPE);
				features.insertFeature("DIFF_SPECIAL" + postFix, Double.TYPE);
				features.insertFeature("DIFF_WHITESPACE" + postFix, Double.TYPE);
				features.insertFeature("DIFF_CHARACTER" + postFix, Double.TYPE);
				features.insertFeature("DIFF_TOKENS" + postFix, Double.TYPE);
				features.insertFeature("DIFF_REPEATED_CHARACTERS" + postFix,
						Double.TYPE);
				features.insertFeature("DIFF_REPEATED_TOKENS" + postFix,
						Double.TYPE);
				features.insertFeature("LEVEN_DIST" + postFix, Double.TYPE);
				features.insertFeature("COSINE_SIM" + postFix, Double.TYPE);
				features.insertFeature("OPTIMAL_ALIGNMENT_DIST" + postFix,
						Double.TYPE);
			}
		}

		features.insertFeature("RATIO_PARAGRAPH_CHAR", Double.TYPE);
		features.insertFeature("RATIO_PARAGRAPH_TOKEN", Double.TYPE);
	}

	public void extractDaTextualFeature(String oldSentence, String newSentence,
			String postFix) {
		/**
		 * I believe putting these together into one function would save a lot
		 * of computation cost
		 * 
		 * But right now I choose to trust the power of the machine
		 */
		int DIFF_CHANGED = features.getIndex("DIFF_CHANGED" + postFix);
		if (oldSentence.equals(newSentence)) {
			featureVector[DIFF_CHANGED] = Boolean.toString(true);
		} else {
			featureVector[DIFF_CHANGED] = Boolean.toString(false);
		}

		int oldCapitalNum = OtherAssist.getCapitalNum(oldSentence);
		int newCapitalNum = OtherAssist.getCapitalNum(newSentence);
		int numDiffCapital = Math.abs(newCapitalNum - oldCapitalNum);
		int DIFF_CAP = features.getIndex("DIFF_CAPITAL" + postFix);
		featureVector[DIFF_CAP] = numDiffCapital * 1.0;

		int oldDigitNum = OtherAssist.getDigitNum(oldSentence);
		int newDigitNum = OtherAssist.getDigitNum(newSentence);
		int numDiffDigit = Math.abs(newDigitNum - oldDigitNum);
		int DIFF_DIGIT = features.getIndex("DIFF_DIGIT" + postFix);
		featureVector[DIFF_DIGIT] = numDiffDigit * 1.0;

		int oldSpecialNum = OtherAssist.getSpecialCharacterNum(oldSentence);
		int newSpecialNum = OtherAssist.getSpecialCharacterNum(newSentence);
		int numDiffSpecial = Math.abs(newSpecialNum - oldSpecialNum);
		int DIFF_SPECIAL = features.getIndex("DIFF_SPECIAL" + postFix);
		featureVector[DIFF_SPECIAL] = numDiffSpecial * 1.0;

		int oldSpaceNum = OtherAssist.getSpaceNum(oldSentence);
		int newSpaceNum = OtherAssist.getSpaceNum(newSentence);
		int numDiffSpace = Math.abs(newSpaceNum - oldSpaceNum);
		int DIFF_SPACE = features.getIndex("DIFF_WHITESPACE" + postFix);
		featureVector[DIFF_SPACE] = numDiffSpace * 1.0;

		int oldCharacterNum = oldSentence.length();
		int newCharacterNum = newSentence.length();
		int numDiffChar = Math.abs(newCharacterNum - oldCharacterNum);
		int DIFF_CHAR = features.getIndex("DIFF_CHARACTER" + postFix);
		featureVector[DIFF_CHAR] = numDiffChar * 1.0;

		/*
		 * I think it is the same as space here
		 */
		int tokenDiffNum = numDiffSpace;
		int DIFF_TOKENS = features.getIndex("DIFF_TOKENS" + postFix);
		featureVector[DIFF_TOKENS] = tokenDiffNum * 1.0;

		/*
		 * I don't think these two make too much sense
		 */
		int diffRepeatedCharacters = 0;
		int diffRepeatedTokens = 0;
		int DIFF_REPEATEDCHAR = features.getIndex("DIFF_REPEATED_CHARACTERS"
				+ postFix);
		featureVector[DIFF_REPEATEDCHAR] = diffRepeatedCharacters * 1.0;
		int DIFF_REPEATEDTOKEN = features.getIndex("DIFF_REPEATED_TOKENS"
				+ postFix);
		featureVector[DIFF_REPEATEDTOKEN] = diffRepeatedTokens * 1.0;

		double levenDistance = OtherAssist.getLevenDistance(oldSentence,
				newSentence);
		int LEVEN_DIST = features.getIndex("LEVEN_DIST" + postFix);
		featureVector[LEVEN_DIST] = levenDistance * 1.0;

		// Notice the cosine here is not normalized
		double cosineSim = OtherAssist.getCosine(oldSentence, newSentence);
		int COSINE_SIM = features.getIndex("COSINE_SIM" + postFix);
		featureVector[COSINE_SIM] = cosineSim * 1.0;

		double oAlignDist = OtherAssist.getOptimalAlignDistance(oldSentence,
				newSentence);
		int OPTIMAL_ALIGNMENT_DIST = features.getIndex("OPTIMAL_ALIGNMENT_DIST"
				+ postFix);
		featureVector[OPTIMAL_ALIGNMENT_DIST] = oAlignDist * 1.0;

		int RATIO_PARAGRAPH_CHAR = features.getIndex("RATIO_PARAGRAPH_CHAR");
		featureVector[RATIO_PARAGRAPH_CHAR] = 0 * 1.0;

		int RATIO_PARAGRAPH_TOKEN = features.getIndex("RATIO_PARAGRAPH_TOKEN");
		featureVector[RATIO_PARAGRAPH_TOKEN] = 0.0 * 1.0;
	}

	public void extractDaTextualFeature(RevisionDocument doc,
			ArrayList<Integer> newIndexes, ArrayList<Integer> oldIndexes, int k) {
		for (int i = 1; i <= k; i++) {
			for (int j = 1; j <= k; j++) {
				String oldSentence = extractOldSentence(doc, oldIndexes, i);
				String newSentence = extractNewSentence(doc, newIndexes, j);
				String postFix = "_" + i + "_" + j;
				extractDaTextualFeature(oldSentence, newSentence, postFix);
			}
		}
	}

	// --------------------------inserting location features
	// -------------------------------
	public void insertLocationFeature(int k) {
		for (int i = 1; i <= k; i++) {
			String postFix = "_" + i;
			features.insertFeature("LOC_PAR_OLD" + postFix, Double.TYPE);
			features.insertFeature("LOC_WHOLE_OLD" + postFix, Double.TYPE);
			features.insertFeature("LOC_ISFIRSTPAR_OLD" + postFix, Boolean.TYPE);
			features.insertFeature("LOC_ISLASTPARA_OLD" + postFix, Boolean.TYPE);
			features.insertFeature("LOC_ISFIRSTSEN_OLD" + postFix, Boolean.TYPE);
			features.insertFeature("LOC_ISLASTSEN_OLD" + postFix, Boolean.TYPE);

			features.insertFeature("LOC_PAR_NEW" + postFix, Double.TYPE);
			features.insertFeature("LOC_WHOLE_NEW" + postFix, Double.TYPE);
			features.insertFeature("LOC_ISFIRSTPAR_NEW" + postFix, Boolean.TYPE);
			features.insertFeature("LOC_ISLASTPARA_NEW" + postFix, Boolean.TYPE);
			features.insertFeature("LOC_ISFIRSTSEN_NEW" + postFix, Boolean.TYPE);
			features.insertFeature("LOC_ISLASTSEN_NEW" + postFix, Boolean.TYPE);

			features.insertFeature("LOC_PAR_DIFF" + postFix, Double.TYPE);
			features.insertFeature("LOC_WHOLE_DIFF" + postFix, Double.TYPE);
		}
	}

	public void extractLocFeature(RevisionDocument doc,
			ArrayList<Integer> sentenceIndices,
			ArrayList<Integer> oldSentIndices, int k) {
		for (int i = 1; i <= k; i++) {
			String postFix = "_" + i;
			int LOC_PAR = features.getIndex("LOC_PAR_NEW" + postFix);
			int LOC_WHOLE = features.getIndex("LOC_WHOLE_NEW" + postFix);
			int LOC_ISFIRSTPAR = features.getIndex("LOC_ISFIRSTPAR_NEW"
					+ postFix);
			int LOC_ISLASTPARA = features.getIndex("LOC_ISLASTPARA_NEW"
					+ postFix);
			int LOC_ISFIRSTSEN = features.getIndex("LOC_ISFIRSTSEN_NEW"
					+ postFix);
			int LOC_ISLASTSEN = features
					.getIndex("LOC_ISLASTSEN_NEW" + postFix);

			double val_par = 0.0;
			double val_whole = 0.0;

			if (sentenceIndices.size() != 0) {
				int first = sentenceIndices.get(0);
				first = first + (k - 1);
				if (first != -1) {
					int paragraphNo = doc.getParaNoOfNewSentence(first);
					int start = doc.getFirstOfNewParagraph(paragraphNo);
					int end = doc.getLastOfNewParagraph(paragraphNo);
					val_par = (first - start) * 1.0 / (end - start);
					val_whole = paragraphNo * 1.0 / doc.getOldParagraphNum();

					if (paragraphNo == 1) {
						featureVector[LOC_ISFIRSTPAR] = Boolean.toString(true);
					} else {
						featureVector[LOC_ISFIRSTPAR] = Boolean.toString(false);
					}

					int lastParaNo = doc.getNewParagraphNum();
					if (paragraphNo == lastParaNo) {
						featureVector[LOC_ISLASTPARA] = Boolean.toString(true);
					} else {
						featureVector[LOC_ISLASTPARA] = Boolean.toString(false);
					}

					if (first == start) {
						featureVector[LOC_ISFIRSTSEN] = Boolean.toString(true);
					} else {
						featureVector[LOC_ISFIRSTSEN] = Boolean.toString(false);
					}

					if (first == end) {
						featureVector[LOC_ISLASTSEN] = Boolean.toString(true);
					} else {
						featureVector[LOC_ISLASTSEN] = Boolean.toString(false);
					}

				}
			} else {
				featureVector[LOC_ISFIRSTPAR] = Boolean.toString(false);
				featureVector[LOC_ISLASTPARA] = Boolean.toString(false);
				featureVector[LOC_ISFIRSTSEN] = Boolean.toString(false);
				featureVector[LOC_ISLASTSEN] = Boolean.toString(false);
			}

			featureVector[LOC_PAR] = val_par;
			featureVector[LOC_WHOLE] = val_whole;

			LOC_PAR = features.getIndex("LOC_PAR_OLD" + postFix);
			LOC_WHOLE = features.getIndex("LOC_WHOLE_OLD" + postFix);
			LOC_ISFIRSTPAR = features.getIndex("LOC_ISFIRSTPAR_OLD" + postFix);
			LOC_ISLASTPARA = features.getIndex("LOC_ISLASTPARA_OLD" + postFix);
			LOC_ISFIRSTSEN = features.getIndex("LOC_ISFIRSTSEN_OLD" + postFix);
			LOC_ISLASTSEN = features.getIndex("LOC_ISLASTSEN_OLD" + postFix);

			double val_par_old = 0.0;
			double val_whole_old = 0.0;
			if (oldSentIndices.size() != 0) {
				int first = oldSentIndices.get(0);
				// System.out.println(doc.getDocumentName());
				// System.out.println(first);
				int paragraphNo = doc.getParaNoOfOldSentence(first);
				int start = doc.getFirstOfOldParagraph(paragraphNo);
				int end = doc.getLastOfOldParagraph(paragraphNo);
				val_par_old = (first - start) * 1.0 / (end - start);
				val_whole_old = paragraphNo * 1.0 / doc.getNewParagraphNum();

				if (paragraphNo == 1) {
					featureVector[LOC_ISFIRSTPAR] = Boolean.toString(true);
				} else {
					featureVector[LOC_ISFIRSTPAR] = Boolean.toString(false);
				}

				int lastParaNo = doc.getOldParagraphNum();
				if (paragraphNo == lastParaNo) {
					featureVector[LOC_ISLASTPARA] = Boolean.toString(true);
				} else {
					featureVector[LOC_ISLASTPARA] = Boolean.toString(false);
				}

				if (first == start) {
					featureVector[LOC_ISFIRSTSEN] = Boolean.toString(true);
				} else {
					featureVector[LOC_ISFIRSTSEN] = Boolean.toString(false);
				}

				if (first == end) {
					featureVector[LOC_ISLASTSEN] = Boolean.toString(true);
				} else {
					featureVector[LOC_ISLASTSEN] = Boolean.toString(false);
				}
			} else {
				featureVector[LOC_ISFIRSTPAR] = Boolean.toString(false);
				featureVector[LOC_ISLASTPARA] = Boolean.toString(false);
				featureVector[LOC_ISFIRSTSEN] = Boolean.toString(false);
				featureVector[LOC_ISLASTSEN] = Boolean.toString(false);
			}

			featureVector[LOC_PAR] = val_par_old;
			featureVector[LOC_WHOLE] = val_whole_old;

			int LOC_PAR_DIFF = features.getIndex("LOC_PAR_DIFF" + postFix);
			int LOC_WHOLE_DIFF = features.getIndex("LOC_WHOLE_DIFF" + postFix);
			featureVector[LOC_PAR_DIFF] = val_par - val_par_old;
			featureVector[LOC_WHOLE_DIFF] = val_whole - val_whole_old;
		}
	}

	// --------------------------inserting LEN
	// features----------------------------------------
	public void insertLenFeature(int k) {
		for (int i = 1; i <= k; i++) {
			String postFix = "_" + i;
			features.insertFeature("LEN_SEN_OLD" + postFix, Double.TYPE);
			features.insertFeature("LEN_SEN_NEW" + postFix, Double.TYPE);
		}

		for (int i = 1; i <= k; i++) {
			for (int j = 1; j <= k; j++) {
				String postFix = "_" + i + "_" + j;
				features.insertFeature("LEN_SEN_DIFF" + postFix, Double.TYPE);
			}
		}
	}

	public void extractLENFeature(RevisionDocument doc,
			ArrayList<Integer> newIndexes, ArrayList<Integer> oldIndexes, int k) {
		for (int i = 1; i <= k; i++) {
			String postFix = "_" + i;
			String oldSent = extractOldSentence(doc, oldIndexes, i);
			String newSent = extractNewSentence(doc, newIndexes, i);

			int LEN_SEN = features.getIndex("LEN_SEN_OLD" + postFix);
			featureVector[LEN_SEN] = oldSent.length() * 1.0;
			int LEN_SEN_NEW = features.getIndex("LEN_SEN_NEW" + postFix);
			featureVector[LEN_SEN_NEW] = newSent.length() * 1.0;
		}
		for (int i = 1; i <= k; i++) {
			for (int j = 1; j <= k; j++) {
				String postFix = "_" + i + "_" + j;
				String oldSent = extractOldSentence(doc, oldIndexes, i);
				String newSent = extractNewSentence(doc, newIndexes, j);
				int LEN_SEN_DIFF = features.getIndex("LEN_SEN_DIFF" + postFix);
				featureVector[LEN_SEN_DIFF] = 1.0 * (newSent.length() - oldSent
						.length());
			}
		}
	}

	// -------------------------insert reasoning keyword features from
	// Falak------------------------------

	String rk = "REASON_KEYWORD_OLD";
	String ck = "CLAIM_KEYWORD_OLD";
	String ek = "EVIDENCE_KEYWORD_OLD";
	String rebK = "REBUT_KEYWORD_OLD";
	String rkN = "REASON_KEYWORD_NEW";
	String ckN = "CLAIM_KEYWORD_NEW";
	String ekN = "EVIDENCE_KEYWORD_NEW";
	String rebKN = "REBUT_KEYWORD_NEW";

	public String[] claimKeywords = { "thesis ", "I ", "agree ", "disagree ",
			"I believe ", "I think ", "conclusion ", "conclude " };
	public String[] evidenceKeywords = { "for example", "such as" };
	public String[] rebutKeywords = { "although ", "though ", "however ",
			"despite ", "but " };
	public String[] reasonKeywords = { "reason ", "reasons ", "because ",
			"result ", "results ", "conclusion ", "since ", "due ", "should ",
			"must ", "might ", "caused ", "led ", "lead ", "affected " };

	public void insertReasoningKeywords(int k) {
		for (int i = 1; i <= k; i++) {
			String postFix = "_" + i;
			features.insertFeature(rk + postFix, Double.TYPE);
			features.insertFeature(rkN + postFix, Double.TYPE);
		}
	}

	public void extractReasoningKeywords(RevisionDocument doc,
			ArrayList<Integer> newIndexes, ArrayList<Integer> oldIndexes, int k) {
		for (int i = 1; i <= k; i++) {
			String postFix = "_" + i;
			String newText = extractNewSentence(doc, newIndexes, i);
			double isReasoning = 0.0;
			for (String word : reasonKeywords) {
				if (newText.contains(word)) {
					isReasoning = 1.0;
					break;
				}
			}
			int featureIndex = features.getIndex(rkN + postFix);
			featureVector[featureIndex] = isReasoning;

			String oldText = extractOldSentence(doc, oldIndexes, i);
			isReasoning = 0.0;
			for (String word : reasonKeywords) {
				if (oldText.contains(word)) {
					isReasoning = 1.0;
					break;
				}
			}
			featureIndex = features.getIndex(rk + postFix);
			featureVector[featureIndex] = isReasoning;
		}
	}

	public void insertClaimKeywords(int k) {
		for (int i = 1; i <= k; i++) {
			String postFix = "_" + i;
			features.insertFeature(ck + postFix, Double.TYPE);
			features.insertFeature(ckN + postFix, Double.TYPE);
		}
	}

	public void extractClaimKeywords(RevisionDocument doc,
			ArrayList<Integer> newIndexes, ArrayList<Integer> oldIndexes, int k) {
		for (int i = 1; i <= k; i++) {
			String postFix = "_" + i;
			String newText = extractNewSentence(doc, newIndexes, i);
			double isReasoning = 0.0;
			for (String word : claimKeywords) {
				if (newText.contains(word)) {
					isReasoning = 1.0;
					break;
				}
			}
			int featureIndex = features.getIndex(ckN + postFix);
			featureVector[featureIndex] = isReasoning;

			String oldText = extractOldSentence(doc, oldIndexes, i);
			isReasoning = 0.0;
			for (String word : claimKeywords) {
				if (oldText.contains(word)) {
					isReasoning = 1.0;
					break;
				}
			}
			featureIndex = features.getIndex(ck + postFix);
			featureVector[featureIndex] = isReasoning;
		}
	}

	public void insertEvidenceKeywords(int k) {
		for (int i = 1; i <= k; i++) {
			String postFix = "_" + i;
			features.insertFeature(ek + postFix, Double.TYPE);
			features.insertFeature(ekN + postFix, Double.TYPE);
		}
	}

	public void extractEvidenceKeywords(RevisionDocument doc,
			ArrayList<Integer> newIndexes, ArrayList<Integer> oldIndexes, int k) {
		for (int i = 1; i <= k; i++) {
			String postFix = "_" + i;
			String newText = extractNewSentence(doc, newIndexes, i);
			double isReasoning = 0.0;
			for (String word : evidenceKeywords) {
				if (newText.contains(word)) {
					isReasoning = 1.0;
					break;
				}
			}
			int featureIndex = features.getIndex(ekN + postFix);
			featureVector[featureIndex] = isReasoning;

			String oldText = extractOldSentence(doc, oldIndexes, i);
			isReasoning = 0.0;
			for (String word : evidenceKeywords) {
				if (oldText.contains(word)) {
					isReasoning = 1.0;
					break;
				}
			}
			featureIndex = features.getIndex(ek + postFix);
			featureVector[featureIndex] = isReasoning;
		}
	}

	public void insertRebuttalKeywords(int k) {
		for (int i = 1; i <= k; i++) {
			String postFix = "_" + i;
			features.insertFeature(rebK + postFix, Double.TYPE);
			features.insertFeature(rebKN + postFix, Double.TYPE);
		}
	}

	public void extractRebuttalKeywords(RevisionDocument doc,
			ArrayList<Integer> newIndexes, ArrayList<Integer> oldIndexes, int k) {
		for (int i = 1; i <= k; i++) {
			String postFix = "_" + i;
			String newText = extractNewSentence(doc, newIndexes, i);
			double isReasoning = 0.0;
			for (String word : rebutKeywords) {
				if (newText.contains(word)) {
					isReasoning = 1.0;
					break;
				}
			}
			int featureIndex = features.getIndex(rebKN + postFix);
			featureVector[featureIndex] = isReasoning;

			String oldText = extractOldSentence(doc, oldIndexes, i);
			isReasoning = 0.0;
			for (String word : rebutKeywords) {
				if (oldText.contains(word)) {
					isReasoning = 1.0;
					break;
				}
			}
			featureIndex = features.getIndex(rebK + postFix);
			featureVector[featureIndex] = isReasoning;
		}
	}

	// --------------------------insert comma features from
	// Falak------------------------

	String hasCommaOld = "HAS_COMMA_OLD";
	String hasCommaNew = "HAS_COMMA_NEW";
	String numCommaOld = "NUM_COMMA_OLD";
	String numCommaNew = "NUM_COMMA_NEW";
	String commaDiff = "NUM_COMMA_DIFF";

	public void insertComma(int k) {
		for (int i = 1; i <= k; i++) {
			String postFix = "_" + i;
			features.insertFeature(hasCommaOld + postFix, Double.TYPE);
			features.insertFeature(hasCommaNew + postFix, Double.TYPE);
			features.insertFeature(numCommaOld + postFix, Double.TYPE);
			features.insertFeature(numCommaNew + postFix, Double.TYPE);
		}
		for (int i = 1; i <= k; i++) {
			for (int j = 1; j <= k; j++) {
				String postFix = "_" + i + "_" + j;
				features.insertFeature(commaDiff + postFix, Double.TYPE);
			}
		}
	}

	public void extractCommaFeatures(RevisionDocument doc,
			ArrayList<Integer> newIndexes, ArrayList<Integer> oldIndexes, int k) {
		double hasCommaOldVal = 0.0;
		double numCommaOldVal = 0.0;
		double hasCommaNewVal = 0.0;
		double numCommaNewVal = 0.0;
		double numCommaDiff = 0.0;

		for (int j = 1; j <= k; j++) {
			String postFix = "_" + j;
			String oldText = extractOldSentence(doc, oldIndexes, j);
			String newText = extractNewSentence(doc, newIndexes, j);

			if (oldText.contains(",")) {
				hasCommaOldVal = 1.0;
				for (int i = 0; i < oldText.length(); i++) {
					if (oldText.charAt(i) == ',')
						numCommaOldVal++;
				}
			}

			if (newText.contains(",")) {
				hasCommaNewVal = 1.0;
				for (int i = 0; i < newText.length(); i++) {
					if (newText.charAt(i) == ',')
						numCommaNewVal++;
				}
			}

			numCommaDiff = numCommaNewVal - numCommaOldVal;
			int featureIndex = features.getIndex(hasCommaOld + postFix);
			featureVector[featureIndex] = hasCommaOldVal;
			featureIndex = features.getIndex(numCommaOld + postFix);
			featureVector[featureIndex] = numCommaOldVal;

			featureIndex = features.getIndex(hasCommaNew + postFix);
			featureVector[featureIndex] = hasCommaNewVal;
			featureIndex = features.getIndex(numCommaNew + postFix);
			featureVector[featureIndex] = numCommaNewVal;
		}

		for (int i = 1; i <= k; i++) {
			for (int j = 1; j <= k; j++) {
				String postFix = "_" + i + "_" + j;
				String oldText = extractOldSentence(doc, oldIndexes, i);
				String newText = extractNewSentence(doc, newIndexes, j);

				if (oldText.contains(",")) {
					hasCommaOldVal = 1.0;
					for (int m = 0; m < oldText.length(); m++) {
						if (oldText.charAt(m) == ',')
							numCommaOldVal++;
					}
				}

				if (newText.contains(",")) {
					hasCommaNewVal = 1.0;
					for (int m = 0; m < newText.length(); m++) {
						if (newText.charAt(m) == ',')
							numCommaNewVal++;
					}
				}

				numCommaDiff = numCommaNewVal - numCommaOldVal;
				int featureIndex = features.getIndex(commaDiff + postFix);
				featureVector[featureIndex] = numCommaDiff;
			}
		}
	}

	// --------------------------insert Text for generating ngram
	// features-------------------------
	public void insertText(int k) {
		features.insertFeature("Text", String.class);
		for (int i = 1; i <= k; i++) {
			for (int j = 1; j <= k; j++) {
				String postFix = "_" + i + "_" + j;
				features.insertFeature("TEXTDIFF" + postFix, String.class);
			}
		}
	}

	public void extractTextFeatures(String text) {
		int TEXT = features.getIndex("Text");
		text = text.replaceAll(",", " ");
		text = text.replaceAll("_", " ");
		featureVector[TEXT] = text;
	}

	public void extractTextFeatures2(RevisionDocument doc,
			ArrayList<Integer> newIndexes, ArrayList<Integer> oldIndexes, int k) {
		for (int i = 1; i <= k; i++) {
			for (int j = 1; j <= k; j++) {
				String postFix = "_" + i + "_" + j;
				String oldSent = extractOldSentence(doc, oldIndexes, i);
				String newSent = extractNewSentence(doc, newIndexes, j);

				String[] tokens = oldSent.split(" ");
				String[] newTokens = newSent.split(" ");
				String diff = "";
				HashSet<String> tS = new HashSet<String>();
				HashSet<String> nS = new HashSet<String>();
				for (String token : tokens)
					tS.add(token.toLowerCase());
				for (String nToken : newTokens)
					nS.add(nToken.toLowerCase());

				for (String token : tS) {
					if (!nS.contains(token))
						diff += token + " ";
				}
				for (String token : nS) {
					if (!tS.contains(token))
						diff += token + " ";
				}
				int featureIndex = features.getIndex("TEXTDIFF" + postFix);
				featureVector[featureIndex] = diff;
			}
		}

	}

	// ---------------------------insert revision op
	// features--------------------
	public void insertOp() {
		features.insertFeature("REVISION_OP", Double.class);
	}

	public void extractOpFeatures(ArrayList<Integer> newIndices,
			ArrayList<Integer> oldIndices) {
		int featureIndex = features.getIndex("REVISION_OP");
		double val = 0;
		if (newIndices == null || newIndices.size() == 0
				|| (newIndices.size() == 1 && newIndices.get(0) == -1)) {
			val = 0;
		} else if (oldIndices == null || oldIndices.size() == 0
				|| (oldIndices.size() == 1 && oldIndices.get(0) == -1)) {
			val = 1;
		} else {
			val = -1;
		}
		featureVector[featureIndex] = val;
	}

	public void extractOpFeatures2(int op1, int op2) {
		int featureIndex = features.getIndex("REVISION_OP");
		double val = 0;
		/*
		 * if ((op1 == EditStep.EDIT_MOVE && op2 == EditStep.EDIT_MOVE)) { val =
		 * 0; } else if (op1 == EditStep.EDIT_KEEP && op2 == EditStep.EDIT_MOVE)
		 * { val = 1; } else { val = -1; }
		 */
		// Actually, when we are predicting the value, the op information should
		// not be available
		featureVector[featureIndex] = val;
	}

	// --------------------------insert categories for
	// classification------------------
	public void insertCategory(ArrayList<String> categories) {
		for (String category : categories) {
			features.addCategoryType(category);
		}
		/*
		 * features.addCategoryType(RevisionPurpose
		 * .getPurposeName(RevisionPurpose.CD_GENERAL_CONTENT_DEVELOPMENT));
		 * features.addCategoryType(RevisionPurpose
		 * .getPurposeName(RevisionPurpose.CD_REBUTTAL_RESERVATION));
		 * features.addCategoryType(RevisionPurpose
		 * .getPurposeName(RevisionPurpose.CD_WARRANT_REASONING_BACKING));
		 * features.addCategoryType(RevisionPurpose
		 * .getPurposeName(RevisionPurpose.CLAIMS_IDEAS));
		 * features.addCategoryType(RevisionPurpose
		 * .getPurposeName(RevisionPurpose.EVIDENCE));
		 */
	}

	/**
	 * Grouping the features
	 */
	// ----------------------Location group-----------------------------
	public void insertLocGroup(int k) {
		insertLocationFeature(k);
	}

	public void extractLocGroup(RevisionDocument doc,
			ArrayList<Integer> newIndexes, ArrayList<Integer> oldIndexes, int k) {
		extractLocFeature(doc, newIndexes, oldIndexes, k);
	}

	// ----------------------Text group---------------------------------
	public void insertTextGroup(int k) {
		insertClaimKeywords(k);
		insertEvidenceKeywords(k);
		insertRebuttalKeywords(k);
		insertReasoningKeywords(k);
		insertComma(k);
		insertLenFeature(k);
		insertDaTextualFeature(k);
		// insertNERFeature();
		// insertOverlapFeature();
	}

	public void extractTextGroup(RevisionDocument doc,
			ArrayList<Integer> newIndexes, ArrayList<Integer> oldIndexes, int k) {
		extractClaimKeywords(doc, newIndexes, oldIndexes, k);
		extractEvidenceKeywords(doc, newIndexes, oldIndexes, k);
		extractRebuttalKeywords(doc, newIndexes, oldIndexes, k);
		extractReasoningKeywords(doc, newIndexes, oldIndexes, k);
		extractCommaFeatures(doc, newIndexes, oldIndexes, k);
		extractLENFeature(doc, newIndexes, oldIndexes, k);
		extractDaTextualFeature(doc, newIndexes, oldIndexes, k);
		// extractNERFeature(doc, newIndexes, oldIndexes);
		// extractOverlapFeature(doc, ru);
	}

	boolean isOnline = true;

	public void setOnline(boolean isOnline) {
		this.isOnline = isOnline;
	}

	// -------------------------Putting everything
	// together-------------------------------
	// buildup features
	public void buildFeatures(boolean usingNgram, ArrayList<String> categories,
			int k) throws IOException {
		insertCategory(categories);
		if (usingNgram)
			insertText(k); // Text always start from the first

		insertLocGroup(k);
		insertTextGroup(k);
		// insertLanguageGroup();
		//
		// insertMetaGroup();
		if (!isOnline) {
			// SentenceEmbeddingFeatureExtractor.getInstance().insertFeature(
			// features);

			// SentenceEmbeddingFeatureExtractor.getInstance().insertCohesion(
			// features);
			// insertOtherGroup();
			PDTBFeatureExtractor.getInstance().insertFeature(features);
			PDTBFeatureExtractor.getInstance().insertARG1ARG2(features);
		}
	}

	public Object[] extractFeatures(RevisionDocument doc,
			ArrayList<Integer> newIndexes, ArrayList<Integer> oldIndexes,
			boolean usingNgram, int k) throws IOException {
		featureVector = new Object[features.getSize()];
		if (usingNgram) {
			String sentence = extractSentence(doc, newIndexes, oldIndexes, k);
			extractTextFeatures(sentence);
			extractTextFeatures2(doc, newIndexes, oldIndexes, k);
		}
		extractLocGroup(doc, newIndexes, oldIndexes, k);
		extractTextGroup(doc, newIndexes, oldIndexes, k);
		// extractLanguageGroup(doc, newIndexes, oldIndexes);
		// extractOtherGroup(doc, ru);
		// PDTBFeatureExtractor.getInstance().extractFeature(features,
		// featureVector, doc, newIndexes, oldIndexes);
		if (!isOnline) {

			SentenceEmbeddingFeatureExtractor.getInstance().extractCohesion(
					features, featureVector, doc, newIndexes, oldIndexes);
			PDTBFeatureExtractor.getInstance().extractFeature(features,
					featureVector, doc, newIndexes, oldIndexes);
			PDTBFeatureExtractor.getInstance().extractFeatureARG1ARG2(features,
					featureVector, doc, newIndexes, oldIndexes);

		}
		return featureVector;
	}

	public Object[] extractFeatures(RevisionDocument doc,
			ArrayList<Integer> newIndexes, ArrayList<Integer> oldIndexes,
			int newStep, int oldStep, boolean usingNgram, int k, int option)
			throws Exception {
		featureVector = new Object[features.getSize()];
		if (usingNgram) {
			String sentence = extractSentence(doc, newIndexes, oldIndexes, k);
			extractTextFeatures(sentence);
			extractTextFeatures2(doc, newIndexes, oldIndexes, k);
		}
		extractLocGroup(doc, newIndexes, oldIndexes, k);
		extractTextGroup(doc, newIndexes, oldIndexes, k);
		if (!isOnline) {
			if (option == 3 || option == 10) {
				SentenceEmbeddingFeatureExtractor.getInstance()
						.extractCohesion(features, featureVector, doc,
								newIndexes, oldIndexes);
			}
			// extractOtherGroup(doc, ru);
			if (option == 2 || option == 10) {
				PDTBFeatureExtractorV2.getInstance().extractFeature(features,
						featureVector, doc, newIndexes, oldIndexes);
				PDTBFeatureExtractorV2.getInstance().extractFeatureARG1ARG2(
						features, featureVector, doc, newIndexes, oldIndexes);
			}
		}
		return featureVector;
	}

	public Object[] extractFeatures(RevisionDocument doc,
			ArrayList<Integer> newIndexes, ArrayList<Integer> oldIndexes,
			boolean usingNgram, int k, int option) throws Exception {
		featureVector = new Object[features.getSize()];
		if (usingNgram) {
			String sentence = extractSentence(doc, newIndexes, oldIndexes, k);
			extractTextFeatures(sentence);
			extractTextFeatures2(doc, newIndexes, oldIndexes, k);
		}
		extractLocGroup(doc, newIndexes, oldIndexes, k);
		extractTextGroup(doc, newIndexes, oldIndexes, k);

		if (!isOnline) {
			if (option == 3 || option == 10) {
				SentenceEmbeddingFeatureExtractor.getInstance()
						.extractCohesion(features, featureVector, doc,
								newIndexes, oldIndexes);
			}
			// extractOtherGroup(doc, ru);
			if (option == 2 || option == 10) {
				PDTBFeatureExtractorV2.getInstance().extractFeature(features,
						featureVector, doc, newIndexes, oldIndexes);
				PDTBFeatureExtractorV2.getInstance().extractFeatureARG1ARG2(
						features, featureVector, doc, newIndexes, oldIndexes);
			}
		}
		return featureVector;
	}

	public void buildFeatures(boolean usingNgram, ArrayList<String> categories,
			int k, int remove) throws Exception {
		features = new FeatureName();
		insertCategory(categories);
		System.out.println("=======================REMOVE IS:" + remove);
		if (usingNgram)
			insertText(k); // Text always start from the first
		if (remove == -1)
			return;
		if (remove == 0 || remove == 10 || remove == 11 || remove == 2
				|| remove == 3 || remove == 6 || remove == 7 || remove == 8
				|| remove == 9 || remove == 12 || remove == 14 || remove == 15
				|| remove == 16 || remove == 17 || remove == 18 || remove == 19)
			insertLocGroup(k);
		if (remove == 1 || remove == 10 || remove == 11 || remove == 2
				|| remove == 3 || remove == 6 || remove == 7 || remove == 8
				|| remove == 9 || remove == 12 || remove == 14 || remove == 15
				|| remove == 16 || remove == 17 || remove == 18 || remove == 19)
			insertTextGroup(k);
		if (remove == 2 || remove == 10 || remove == 22) {
			// PDTBFeatureExtractor.getInstance().insertARG1ARG2(features);
			PDTBFeatureExtractorV2.getInstance().insertFeature(features);
		}
		if (remove == 10 || remove == 23 || remove == 8) {
			// PDTBFeatureExtractor.getInstance().insertARG1ARG2(features);
			PDTBFeatureExtractorV4.getInstance().insertFeature(features);
		}
		if (remove == 6 || remove == 10 || remove == 14 || remove == 16) {
			// PDTBFeatureExtractorV2.getInstance().insertPDTBEntityFeatures(features);
			PDTBFeatureExtractorV2.getInstance()
					.insertSelectedFeaturesWeighted(features);
			PDTBFeatureExtractorV2.getInstance()
					.insertSelectedFeaturesGroupWeighted(features);
			PDTBFeatureExtractorV2.getInstance()
					.insertSelectedFeaturesPostWeighted(features);
			PDTBFeatureExtractorV2.getInstance()
					.insertSelectedFeaturesPostGroupWeighted(features);
		}
		if (remove == 16 || remove == 14) {
			PDTBFeatureExtractorV2.getInstance()
					.insertSelectedFeaturesDiffWeighted(features);
			PDTBFeatureExtractorV2.getInstance()
					.insertSelectedFeaturesDiffWeightedPost(features);
		}
		if (remove == 7 || remove == 10 || remove == 15 || remove == 17) {
			// PDTBFeatureExtractorV2.getInstance().insertPDTBEntityFeatures(features);
			PDTBFeatureExtractorV4.getInstance()
					.insertSelectedFeaturesWeighted(features);
			PDTBFeatureExtractorV4.getInstance()
					.insertSelectedFeaturesPostWeighted(features);
			PDTBFeatureExtractorV4.getInstance()
					.insertSelectedFeaturesPostGroupWeighted(features);
			PDTBFeatureExtractorV4.getInstance()
					.insertSelectedFeaturesGroupWeighted(features);
		}
		if (remove == 17 || remove == 15) {
			PDTBFeatureExtractorV4.getInstance()
					.insertSelectedFeaturesDiffWeighted(features);
			PDTBFeatureExtractorV4.getInstance()
					.insertSelectedFeaturesDiffWeightedPost(features);
		}
		if (remove == 9 || remove == 10 || remove == 14 || remove == 18) {
			PDTBFeatureExtractorV2.getInstance()
					.insertSelectedFeaturesWeightedTree(features, 4);
			PDTBFeatureExtractorV2.getInstance()
					.insertSelectedFeaturesPostWeightedTree(features, 4);
		}
		if (remove == 18 || remove == 14) {
			PDTBFeatureExtractorV2.getInstance()
					.insertSelectedFeaturesWeightedTreeDiff(features, 4);
			PDTBFeatureExtractorV2.getInstance()
					.insertSelectedFeaturesPostWeightedTreeDiff(features, 4);
		}
		if (remove == 12 || remove == 10 || remove == 15 || remove == 19) {
			PDTBFeatureExtractorV4.getInstance()
					.insertSelectedFeaturesWeightedTree(features, 4);
			PDTBFeatureExtractorV4.getInstance()
					.insertSelectedFeaturesPostWeightedTree(features, 4);
		}
		if (remove == 19 || remove == 15) {
			PDTBFeatureExtractorV4.getInstance()
					.insertSelectedFeaturesWeightedTreeDiff(features, 4);
			PDTBFeatureExtractorV4.getInstance()
					.insertSelectedFeaturesPostWeightedTreeDiff(features, 4);
		}

		if (remove == 3 || remove == 10) {
			// insertMetaGroup();
			SentenceEmbeddingFeatureExtractor.getInstance().insertCohesion(
					features);
			SentenceEmbeddingFeatureExtractor.getInstance().insertFeature(
					features);
		}
		if (remove == 4 || remove == 10 || remove == 2) {
			// insertPriorPostFeatures(1);
		}
		if (remove == 5 || remove == 10) {
			// PDTBFeatureExtractor.getInstance().insertARG1ARG2(features);
			// PDTBFeatureExtractor.getInstance().insertFeature(features);
			ArgumentZoningFeatureExtractor.getInstance()
					.insertFeature(features);
		}
	}

	public void buildFeaturesCRF(boolean usingNgram,
			ArrayList<String> categories, int k, int remove) throws Exception {
		features = new FeatureName();
		insertCategory(categories);
		System.out.println("=======================REMOVE IS:" + remove);
		if (usingNgram)
			insertText(k); // Text always start from the first
		if (remove == -1)
			return;
		if (remove == 0 || remove == 10 || remove == 11 || remove == 3
				|| remove == 2 || remove == 5 || remove == 6 || remove == 7
				|| remove == 8 || remove == 9 || remove == 12)
			insertLocGroup(k);
		if (remove == 1 || remove == 10 || remove == 11 || remove == 3
				|| remove == 2 || remove == 5 || remove == 6 || remove == 7
				|| remove == 8 || remove == 9 || remove == 12)
			insertTextGroup(k);
		if (remove == 2 || remove == 10 || remove == 22) {
			// PDTBFeatureExtractor.getInstance().insertARG1ARG2(features);
			PDTBFeatureExtractorV2.getInstance().insertFeature(features);
		}
		if (remove == 10 || remove == 23 || remove == 8) {
			// PDTBFeatureExtractor.getInstance().insertARG1ARG2(features);
			PDTBFeatureExtractorV4.getInstance().insertFeature(features);
		}
		if (remove == 6 || remove == 10) {
			// PDTBFeatureExtractorV2.getInstance().insertPDTBEntityFeatures(features);
			PDTBFeatureExtractorV2.getInstance()
					.insertSelectedFeaturesWeighted(features);
			PDTBFeatureExtractorV2.getInstance()
					.insertSelectedFeaturesPostWeighted(features);
			PDTBFeatureExtractorV2.getInstance()
					.insertSelectedFeaturesDiffWeighted(features);
		}
		if (remove == 7 || remove == 10) {
			// PDTBFeatureExtractorV2.getInstance().insertPDTBEntityFeatures(features);
			PDTBFeatureExtractorV4.getInstance()
					.insertSelectedFeaturesWeighted(features);
			PDTBFeatureExtractorV4.getInstance()
					.insertSelectedFeaturesPostWeighted(features);
			PDTBFeatureExtractorV4.getInstance()
					.insertSelectedFeaturesDiffWeighted(features);
		}
		if (remove == 5 || remove == 10) {
			// PDTBFeatureExtractor.getInstance().insertARG1ARG2(features);
			// PDTBFeatureExtractor.getInstance().insertFeature(features);
			ArgumentZoningFeatureExtractor.getInstance()
					.insertFeature(features);
		}
		if (remove == 3 || remove == 10) {
			// insertMetaGroup();
			// SentenceEmbeddingFeatureExtractor.getInstance().insertFeature(
			// features);
			SentenceEmbeddingFeatureExtractor.getInstance().insertCohesion(
					features);
		}
		if (remove == 4 || remove == 10 || remove == 2) {
			// insertPriorPostFeatures(1);
		}
		if (remove == 9 || remove == 10) {
			PDTBFeatureExtractorV2.getInstance()
					.insertSelectedFeaturesWeightedTree(features, 4);
			PDTBFeatureExtractorV2.getInstance()
					.insertSelectedFeaturesPostWeightedTree(features, 4);
			PDTBFeatureExtractorV2.getInstance()
					.insertSelectedFeaturesWeightedTreeDiff(features, 4);
			PDTBFeatureExtractorV2.getInstance()
					.insertSelectedFeaturesPostWeightedTreeDiff(features, 4);
		}
		if (remove == 12 || remove == 10) {
			PDTBFeatureExtractorV4.getInstance()
					.insertSelectedFeaturesWeightedTree(features, 4);
			PDTBFeatureExtractorV4.getInstance()
					.insertSelectedFeaturesPostWeightedTree(features, 4);
			PDTBFeatureExtractorV4.getInstance()
					.insertSelectedFeaturesWeightedTreeDiff(features, 4);
			PDTBFeatureExtractorV4.getInstance()
					.insertSelectedFeaturesPostWeightedTreeDiff(features, 4);
		}
		// insertLanguageGroup();
		// insertOtherGroup();
		// insertLocationFeature();
		// insertNERFeature();
		// insertLenFeature();
		// insertSimplePOSFeatureRatio();
		//
		// insertReasoningKeywords();
		// insertComma();
		// insertOp();
		// insertDaTextualFeature();
		// insertComplexPOSFeatures();
		// insertKeywordOverlap();
	}

	// ---------------------------Utils used in this
	// file-------------------------------
	public double getStartVal(CoreMap cm, String tag) {
		double val = 0.0;
		boolean isStart = spa.startsWith(cm, tag);
		if (isStart) {
			val = 1.0;
		} else {
			val = 0.0;
		}
		return val;
	}

	// extract keywords, just the content keyword frequency
	public HashSet<String> extractTopKeywords(RevisionDocument doc) {
		HashSet<String> topKeywords = new HashSet<String>();
		Hashtable<String, Integer> contentFreq = new Hashtable<String, Integer>();
		for (String sentence : doc.getNewSentencesArray()) {
			Hashtable<String, Integer> tmp = spa.getContentWords(spa
					.annotateSingleSentence(sentence));
			Iterator<String> it = tmp.keySet().iterator();
			while (it.hasNext()) {
				String key = it.next();
				if (contentFreq.containsKey(key)) {
					contentFreq.put(key, contentFreq.get(key) + tmp.get(key));
				} else {
					contentFreq.put(key, tmp.get(key));
				}
			}
		}
		topKeywords = getTopK(contentFreq, 20);
		return topKeywords;
	}

	class CompareItem {
		String word;
		int freq;

		CompareItem(String word, int freq) {
			this.word = word;
			this.freq = freq;
		}
	}

	public HashSet<String> getTopK(Hashtable<String, Integer> table, int K) {
		Comparator<CompareItem> orderIsdn = new Comparator<CompareItem>() {
			public int compare(CompareItem o1, CompareItem o2) {
				// TODO Auto-generated method stub
				int numbera = o1.freq;
				int numberb = o2.freq;
				if (numberb > numbera) {
					return 1;
				} else if (numberb < numbera) {
					return -1;
				} else {
					return 0;
				}

			}
		};
		PriorityQueue<CompareItem> pq = new PriorityQueue<CompareItem>(K,
				orderIsdn);
		Iterator<String> it = table.keySet().iterator();
		while (it.hasNext()) {
			String key = it.next();
			int freq = table.get(key);
			pq.add(new CompareItem(key, freq));
		}

		HashSet<String> topKeywords = new HashSet<String>();
		for (int i = 0; i < K; i++) {
			topKeywords.add(pq.poll().word);
		}
		return topKeywords;
	}

	public String transformPattern(String pattern) {
		ArrayList<String> pats = new ArrayList<String>();
		boolean start = false;
		String word = "";
		String lastWord = "";
		String newPattern = "";
		for (int i = 0; i < pattern.length(); i++) {
			char c = pattern.charAt(i);
			if (start == false) {
				if (c == '<') {
					start = true;
				} else if (c == '*' || c == '?' || c == '|' || c == ')') {
					newPattern += "(" + "<" + lastWord + ">" + ")" + c;
				} else if (c == '(') {
					newPattern += c;
				}
			} else {
				if (c == '>') {
					start = false;
					lastWord = word;
					word = "";
				} else {
					word += c;
				}
			}
		}
		return newPattern;
	}
}
