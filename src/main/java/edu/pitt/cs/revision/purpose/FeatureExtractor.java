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

import edu.pitt.cs.revision.batch.BatchFeatureReader;
import edu.pitt.cs.revision.batch.BatchFeatureWriter;
import edu.pitt.cs.revision.batch.InfoStore;
import edu.pitt.cs.revision.batch.SentenceInfo;
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
public class FeatureExtractor {
	StanfordParserAssist spa;
	JOrthoAssist ja;
	FeatureName features;
	Object[] featureVector;

	public FeatureExtractor() {
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

	/**
	 * Extract the old sentences
	 * 
	 * @param doc
	 * @param ru
	 * @return
	 */
	public String extractOldSentence(RevisionDocument doc, RevisionUnit ru) {
		ArrayList<Integer> oldIndexes = ru.getOldSentenceIndex();
		return extractOldSentence(doc, oldIndexes);
	}

	public String extractOldSentence(RevisionDocument doc,
			ArrayList<Integer> oldIndexes) {
		String sentence = "";
		if (oldIndexes != null) {
			for (Integer oldIndex : oldIndexes) {
				if (oldIndex > 0) {
					sentence += doc.getOldSentence(oldIndex);
				}
			}
		}
		return sentence;
	}

	/**
	 * Extract the new sentences
	 * 
	 * @param doc
	 * @param ru
	 * @return
	 */
	public String extractNewSentence(RevisionDocument doc, RevisionUnit ru) {
		ArrayList<Integer> newIndexes = ru.getNewSentenceIndex();
		return extractNewSentence(doc, newIndexes);
	}

	public String extractNewSentence(RevisionDocument doc,
			ArrayList<Integer> newIndexes) {
		String sentence = "";
		if (newIndexes != null) {
			for (Integer newIndex : newIndexes) {
				if (newIndex > 0) {
					sentence += doc.getNewSentence(newIndex);
				}
			}
		}
		return sentence;
	}

	/**
	 * Extracting the sentence from the text
	 * 
	 * @param doc
	 * @param ru
	 * @return
	 */
	public String extractSentence(RevisionDocument doc, RevisionUnit ru) {
		ArrayList<Integer> oldIndexes = ru.getOldSentenceIndex();
		ArrayList<Integer> newIndexes = ru.getNewSentenceIndex();

		String sentence = "";
		if (newIndexes != null) {
			for (Integer newIndex : newIndexes) {
				if (newIndex != -1) {
					sentence += doc.getNewSentence(newIndex);
				}
			}
		}

		if (oldIndexes != null) {
			for (Integer oldIndex : oldIndexes) {
				if (oldIndex != -1) {
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
			boolean isNew) {
		String sentence = "";
		if (isNew && newIndexes != null) {
			for (Integer newIndex : newIndexes) {
				if (newIndex != -1) {
					sentence += doc.getNewSentence(newIndex);
				}
			}
		} else if (!isNew && oldIndexes != null) {
			if (oldIndexes != null) {
				for (Integer oldIndex : oldIndexes) {
					if (oldIndex != -1) {
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
	public void insertDaTextualFeature() {
		features.insertFeature("DIFF_CHANGED", Boolean.TYPE);
		features.insertFeature("DIFF_CAPITAL", Double.TYPE);
		features.insertFeature("DIFF_DIGIT", Double.TYPE);
		features.insertFeature("DIFF_SPECIAL", Double.TYPE);
		features.insertFeature("DIFF_WHITESPACE", Double.TYPE);
		features.insertFeature("DIFF_CHARACTER", Double.TYPE);
		features.insertFeature("DIFF_TOKENS", Double.TYPE);
		features.insertFeature("DIFF_REPEATED_CHARACTERS", Double.TYPE);
		features.insertFeature("DIFF_REPEATED_TOKENS", Double.TYPE);
		features.insertFeature("LEVEN_DIST", Double.TYPE);
		features.insertFeature("COSINE_SIM", Double.TYPE);
		features.insertFeature("OPTIMAL_ALIGNMENT_DIST", Double.TYPE);
		features.insertFeature("RATIO_PARAGRAPH_CHAR", Double.TYPE);
		// features.insertFeature("RATIO_REVISION", Double.TYPE); ---Does not
		// apply here
		features.insertFeature("RATIO_PARAGRAPH_TOKEN", Double.TYPE);
	}

	public void extractDaTextualFeature(String oldSentence, String newSentence) {
		/**
		 * I believe putting these together into one function would save a lot
		 * of computation cost
		 * 
		 * But right now I choose to trust the power of the machine
		 */
		int DIFF_CHANGED = features.getIndex("DIFF_CHANGED");
		if (oldSentence.equals(newSentence)) {
			featureVector[DIFF_CHANGED] = Boolean.toString(true);
		} else {
			featureVector[DIFF_CHANGED] = Boolean.toString(false);
		}

		int oldCapitalNum = OtherAssist.getCapitalNum(oldSentence);
		int newCapitalNum = OtherAssist.getCapitalNum(newSentence);
		int numDiffCapital = Math.abs(newCapitalNum - oldCapitalNum);
		int DIFF_CAP = features.getIndex("DIFF_CAPITAL");
		featureVector[DIFF_CAP] = numDiffCapital * 1.0;

		int oldDigitNum = OtherAssist.getDigitNum(oldSentence);
		int newDigitNum = OtherAssist.getDigitNum(newSentence);
		int numDiffDigit = Math.abs(newDigitNum - oldDigitNum);
		int DIFF_DIGIT = features.getIndex("DIFF_DIGIT");
		featureVector[DIFF_DIGIT] = numDiffDigit * 1.0;

		int oldSpecialNum = OtherAssist.getSpecialCharacterNum(oldSentence);
		int newSpecialNum = OtherAssist.getSpecialCharacterNum(newSentence);
		int numDiffSpecial = Math.abs(newSpecialNum - oldSpecialNum);
		int DIFF_SPECIAL = features.getIndex("DIFF_SPECIAL");
		featureVector[DIFF_SPECIAL] = numDiffSpecial * 1.0;

		int oldSpaceNum = OtherAssist.getSpaceNum(oldSentence);
		int newSpaceNum = OtherAssist.getSpaceNum(newSentence);
		int numDiffSpace = Math.abs(newSpaceNum - oldSpaceNum);
		int DIFF_SPACE = features.getIndex("DIFF_WHITESPACE");
		featureVector[DIFF_SPACE] = numDiffSpace * 1.0;

		int oldCharacterNum = oldSentence.length();
		int newCharacterNum = newSentence.length();
		int numDiffChar = Math.abs(newCharacterNum - oldCharacterNum);
		int DIFF_CHAR = features.getIndex("DIFF_CHARACTER");
		featureVector[DIFF_CHAR] = numDiffChar * 1.0;

		/*
		 * I think it is the same as space here
		 */
		int tokenDiffNum = numDiffSpace;
		int DIFF_TOKENS = features.getIndex("DIFF_TOKENS");
		featureVector[DIFF_TOKENS] = tokenDiffNum * 1.0;

		/*
		 * I don't think these two make too much sense
		 */
		int diffRepeatedCharacters = 0;
		int diffRepeatedTokens = 0;
		int DIFF_REPEATEDCHAR = features.getIndex("DIFF_REPEATED_CHARACTERS");
		featureVector[DIFF_REPEATEDCHAR] = diffRepeatedCharacters * 1.0;
		int DIFF_REPEATEDTOKEN = features.getIndex("DIFF_REPEATED_TOKENS");
		featureVector[DIFF_REPEATEDTOKEN] = diffRepeatedTokens * 1.0;

		double levenDistance = OtherAssist.getLevenDistance(oldSentence,
				newSentence);
		int LEVEN_DIST = features.getIndex("LEVEN_DIST");
		featureVector[LEVEN_DIST] = levenDistance * 1.0;

		// Notice the cosine here is not normalized
		double cosineSim = OtherAssist.getCosine(oldSentence, newSentence);
		int COSINE_SIM = features.getIndex("COSINE_SIM");
		featureVector[COSINE_SIM] = cosineSim * 1.0;

		double oAlignDist = OtherAssist.getOptimalAlignDistance(oldSentence,
				newSentence);
		int OPTIMAL_ALIGNMENT_DIST = features
				.getIndex("OPTIMAL_ALIGNMENT_DIST");
		featureVector[OPTIMAL_ALIGNMENT_DIST] = oAlignDist * 1.0;

		int RATIO_PARAGRAPH_CHAR = features.getIndex("RATIO_PARAGRAPH_CHAR");
		featureVector[RATIO_PARAGRAPH_CHAR] = 0 * 1.0;

		int RATIO_PARAGRAPH_TOKEN = features.getIndex("RATIO_PARAGRAPH_TOKEN");
		featureVector[RATIO_PARAGRAPH_TOKEN] = 0.0 * 1.0;
	}

	public void extractDaTextualFeature(RevisionDocument doc, RevisionUnit ru) {
		String[] sentences = OtherAssist.getSentences(doc, ru);
		String oldSentence = sentences[0];
		String newSentence = sentences[1];

		extractDaTextualFeature(oldSentence, newSentence);
	}

	public void extractDaTextualFeature(RevisionDocument doc,
			ArrayList<Integer> newIndexes, ArrayList<Integer> oldIndexes) {
		String oldSentence = extractOldSentence(doc, oldIndexes);
		String newSentence = extractNewSentence(doc, newIndexes);

		extractDaTextualFeature(oldSentence, newSentence);
	}

	// --------------------------inserting location features
	// -------------------------------
	public void insertLocationFeature() {
		features.insertFeature("LOC_PAR_OLD", Double.TYPE);
		features.insertFeature("LOC_WHOLE_OLD", Double.TYPE);
		features.insertFeature("LOC_ISFIRSTPAR_OLD", Boolean.TYPE);
		features.insertFeature("LOC_ISLASTPARA_OLD", Boolean.TYPE);
		features.insertFeature("LOC_ISFIRSTSEN_OLD", Boolean.TYPE);
		features.insertFeature("LOC_ISLASTSEN_OLD", Boolean.TYPE);

		features.insertFeature("LOC_PAR_NEW", Double.TYPE);
		features.insertFeature("LOC_WHOLE_NEW", Double.TYPE);
		features.insertFeature("LOC_ISFIRSTPAR_NEW", Boolean.TYPE);
		features.insertFeature("LOC_ISLASTPARA_NEW", Boolean.TYPE);
		features.insertFeature("LOC_ISFIRSTSEN_NEW", Boolean.TYPE);
		features.insertFeature("LOC_ISLASTSEN_NEW", Boolean.TYPE);

		features.insertFeature("LOC_PAR_DIFF", Double.TYPE);
		features.insertFeature("LOC_WHOLE_DIFF", Double.TYPE);
	}

	public void insertLocationFeaturePriorPost(int windowSize) {
		for (int i = 1; i <= windowSize; i++) {
			String tag = "_PRIOR_" + i;
			String tag2 = "_POST_" + i;
			String[] tags = { tag, tag2 };
			for (String tmp : tags) {
				if (tmp.equals(tag)) {
					features.insertFeature("LOC_ISFIRSTPAR_OLD" + tmp,
							Boolean.TYPE);
					features.insertFeature("LOC_ISFIRSTSEN_OLD" + tmp,
							Boolean.TYPE);
					features.insertFeature("LOC_ISFIRSTPAR_NEW" + tmp,
							Boolean.TYPE);
					features.insertFeature("LOC_ISFIRSTSEN_NEW" + tmp,
							Boolean.TYPE);
				} else if (tmp.equals(tag2)) {
					features.insertFeature("LOC_ISLASTPARA_OLD" + tmp,
							Boolean.TYPE);
					features.insertFeature("LOC_ISLASTSEN_OLD" + tmp,
							Boolean.TYPE);
					features.insertFeature("LOC_ISLASTPARA_NEW" + tmp,
							Boolean.TYPE);
					features.insertFeature("LOC_ISLASTSEN_NEW" + tmp,
							Boolean.TYPE);
				}
			}
		}
	}

	public void extractLocFeaturePriorPost(RevisionDocument doc,
			ArrayList<Integer> sentenceIndices,
			ArrayList<Integer> oldSentIndices, int windowSize) {
		// ru.getNewParagraphNo();
		if (sentenceIndices != null) {
			Collections.sort(sentenceIndices);
		}
		if (oldSentIndices != null) {
			Collections.sort(oldSentIndices);
		}
		for (int i = 1; i <= windowSize; i++) {
			String tag = "_PRIOR_" + i;
			String tag2 = "_POST_" + i;
			String[] tags = { tag, tag2 };
			for (String tmp : tags) {
				if (sentenceIndices.size() != 0) {
					if (tmp.equals(tag)) {
						int LOC_ISFIRSTPAR = features
								.getIndex("LOC_ISFIRSTPAR_NEW" + tmp);
						int LOC_ISFIRSTSEN = features
								.getIndex("LOC_ISFIRSTSEN_NEW" + tmp);
						int first = sentenceIndices.get(0) - i;
						if (first > 0) {
							int paragraphNo = doc.getParaNoOfNewSentence(first);
							int start = doc.getFirstOfNewParagraph(paragraphNo);
							if (paragraphNo == 1) {
								featureVector[LOC_ISFIRSTPAR] = Boolean
										.toString(true);
							} else {
								featureVector[LOC_ISFIRSTPAR] = Boolean
										.toString(false);
							}
							if (first == start) {
								featureVector[LOC_ISFIRSTSEN] = Boolean
										.toString(true);
							} else {
								featureVector[LOC_ISFIRSTSEN] = Boolean
										.toString(false);
							}
						} else {
							featureVector[LOC_ISFIRSTPAR] = Boolean
									.toString(false);
							featureVector[LOC_ISFIRSTSEN] = Boolean
									.toString(false);
						}
					} else if (tmp.equals(tag2)) {
						int LOC_ISLASTPARA = features
								.getIndex("LOC_ISLASTPARA_NEW" + tmp);
						int LOC_ISLASTSEN = features
								.getIndex("LOC_ISLASTSEN_NEW" + tmp);
						int last = sentenceIndices
								.get(sentenceIndices.size() - 1);
						last = last + i;
						if (last <= doc.getNewDraftSentences().size()) {
							int lastParaNo = doc.getNewParagraphNum();
							int paragraphNo = doc.getParaNoOfNewSentence(last);
							int end = doc.getLastOfNewParagraph(paragraphNo);
							if (paragraphNo == lastParaNo) {
								featureVector[LOC_ISLASTPARA] = Boolean
										.toString(true);
							} else {
								featureVector[LOC_ISLASTPARA] = Boolean
										.toString(false);
							}
							if (last == end) {
								featureVector[LOC_ISLASTSEN] = Boolean
										.toString(true);
							} else {
								featureVector[LOC_ISLASTSEN] = Boolean
										.toString(false);
							}
						} else {
							featureVector[LOC_ISLASTPARA] = Boolean
									.toString(false);
							featureVector[LOC_ISLASTSEN] = Boolean
									.toString(false);
						}

					} else {
						// should not happen
					}
				} else {
					if (tmp.equals(tag)) {
						int LOC_ISFIRSTPAR = features
								.getIndex("LOC_ISFIRSTPAR_NEW" + tmp);
						int LOC_ISFIRSTSEN = features
								.getIndex("LOC_ISFIRSTSEN_NEW" + tmp);
						featureVector[LOC_ISFIRSTPAR] = Boolean.toString(false);
						featureVector[LOC_ISFIRSTSEN] = Boolean.toString(false);
					} else if (tmp.equals(tag2)) {
						int LOC_ISLASTPARA = features
								.getIndex("LOC_ISLASTPARA_NEW" + tmp);
						int LOC_ISLASTSEN = features
								.getIndex("LOC_ISLASTSEN_NEW" + tmp);
						featureVector[LOC_ISLASTPARA] = Boolean.toString(false);
						featureVector[LOC_ISLASTSEN] = Boolean.toString(false);
					}
				}

				if (oldSentIndices.size() != 0) {
					if (tmp.equals(tag)) {
						int LOC_ISFIRSTPAR = features
								.getIndex("LOC_ISFIRSTPAR_OLD" + tmp);
						int LOC_ISFIRSTSEN = features
								.getIndex("LOC_ISFIRSTSEN_OLD" + tmp);
						int first = oldSentIndices.get(0) - i;
						if (first > 0) {
							int paragraphNo = doc.getParaNoOfOldSentence(first);
							int start = doc.getFirstOfOldParagraph(paragraphNo);
							if (paragraphNo == 1) {
								featureVector[LOC_ISFIRSTPAR] = Boolean
										.toString(true);
							} else {
								featureVector[LOC_ISFIRSTPAR] = Boolean
										.toString(false);
							}
							if (first == start) {
								featureVector[LOC_ISFIRSTSEN] = Boolean
										.toString(true);
							} else {
								featureVector[LOC_ISFIRSTSEN] = Boolean
										.toString(false);
							}
						} else {
							featureVector[LOC_ISFIRSTPAR] = Boolean
									.toString(false);
							featureVector[LOC_ISFIRSTSEN] = Boolean
									.toString(false);
						}
					} else if (tmp.equals(tag2)) {
						int LOC_ISLASTPARA = features
								.getIndex("LOC_ISLASTPARA_OLD" + tmp);
						int LOC_ISLASTSEN = features
								.getIndex("LOC_ISLASTSEN_OLD" + tmp);
						int last = oldSentIndices
								.get(oldSentIndices.size() - 1);
						last = last + i;
						if (last <= doc.getOldDraftSentences().size()) {
							int lastParaNo = doc.getOldParagraphNum();
							int paragraphNo = doc.getParaNoOfOldSentence(last);
							int end = doc.getLastOfOldParagraph(paragraphNo);
							if (paragraphNo == lastParaNo) {
								featureVector[LOC_ISLASTPARA] = Boolean
										.toString(true);
							} else {
								featureVector[LOC_ISLASTPARA] = Boolean
										.toString(false);
							}
							if (last == end) {
								featureVector[LOC_ISLASTSEN] = Boolean
										.toString(true);
							} else {
								featureVector[LOC_ISLASTSEN] = Boolean
										.toString(false);
							}
						} else {
							featureVector[LOC_ISLASTPARA] = Boolean
									.toString(false);
							featureVector[LOC_ISLASTSEN] = Boolean
									.toString(false);
						}

					} else {
						// should not happen
					}
				} else {
					if (tmp.equals(tag)) {
						int LOC_ISFIRSTPAR = features
								.getIndex("LOC_ISFIRSTPAR_OLD" + tmp);
						int LOC_ISFIRSTSEN = features
								.getIndex("LOC_ISFIRSTSEN_OLD" + tmp);
						featureVector[LOC_ISFIRSTPAR] = Boolean.toString(false);
						featureVector[LOC_ISFIRSTSEN] = Boolean.toString(false);
					} else if (tmp.equals(tag2)) {
						int LOC_ISLASTPARA = features
								.getIndex("LOC_ISLASTPARA_OLD" + tmp);
						int LOC_ISLASTSEN = features
								.getIndex("LOC_ISLASTSEN_OLD" + tmp);
						featureVector[LOC_ISLASTPARA] = Boolean.toString(false);
						featureVector[LOC_ISLASTSEN] = Boolean.toString(false);
					}
				}
			}
		}
	}

	public void extractLocFeature(RevisionDocument doc,
			ArrayList<Integer> sentenceIndices,
			ArrayList<Integer> oldSentIndices) {
		// ru.getNewParagraphNo();
		int LOC_PAR = features.getIndex("LOC_PAR_NEW");
		int LOC_WHOLE = features.getIndex("LOC_WHOLE_NEW");
		int LOC_ISFIRSTPAR = features.getIndex("LOC_ISFIRSTPAR_NEW");
		int LOC_ISLASTPARA = features.getIndex("LOC_ISLASTPARA_NEW");
		int LOC_ISFIRSTSEN = features.getIndex("LOC_ISFIRSTSEN_NEW");
		int LOC_ISLASTSEN = features.getIndex("LOC_ISLASTSEN_NEW");

		double val_par = 0.0;
		double val_whole = 0.0;

		if (sentenceIndices.size() != 0) {
			int first = sentenceIndices.get(0);
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

		LOC_PAR = features.getIndex("LOC_PAR_OLD");
		LOC_WHOLE = features.getIndex("LOC_WHOLE_OLD");
		LOC_ISFIRSTPAR = features.getIndex("LOC_ISFIRSTPAR_OLD");
		LOC_ISLASTPARA = features.getIndex("LOC_ISLASTPARA_OLD");
		LOC_ISFIRSTSEN = features.getIndex("LOC_ISFIRSTSEN_OLD");
		LOC_ISLASTSEN = features.getIndex("LOC_ISLASTSEN_OLD");

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

		int LOC_PAR_DIFF = features.getIndex("LOC_PAR_DIFF");
		int LOC_WHOLE_DIFF = features.getIndex("LOC_WHOLE_DIFF");
		featureVector[LOC_PAR_DIFF] = val_par - val_par_old;
		featureVector[LOC_WHOLE_DIFF] = val_whole - val_whole_old;

	}

	public void extractLocFeature(RevisionDocument doc, RevisionUnit ru) {
		ArrayList<Integer> sentenceIndices = ru.getNewSentenceIndex();
		ArrayList<Integer> oldSentIndices = ru.getOldSentenceIndex();

		extractLocFeature(doc, sentenceIndices, oldSentIndices);
	}

	// --------------------------inserting NER
	// features--------------------------------------
	public void insertNERFeature() {
		features.insertFeature("HAS_NER_OLD", Boolean.TYPE);
		features.insertFeature("HAS_LOC_OLD", Boolean.TYPE);
		features.insertFeature("HAS_NER_NEW", Boolean.TYPE);
		features.insertFeature("HAS_LOC_NEW", Boolean.TYPE);
		features.insertFeature("DIFF_NER", Double.TYPE);
		features.insertFeature("DIFF_LOC", Double.TYPE);
	}

	public void insertPriorPostFeatures(int windowSize) {
		insertLocationFeaturePriorPost(windowSize);
		insertTextGroupPriorPost(windowSize);
	}

	public void extractNERFeature(RevisionDocument doc,
			ArrayList<Integer> newIndexes, ArrayList<Integer> oldIndexes) {

		int HAS_NER_OLD = features.getIndex("HAS_NER_OLD");
		int HAS_LOC_OLD = features.getIndex("HAS_LOC_OLD");
		int HAS_NER_NEW = features.getIndex("HAS_NER_NEW");
		int HAS_LOC_NEW = features.getIndex("HAS_LOC_NEW");
		int DIFF_NER = features.getIndex("DIFF_NER");
		int DIFF_LOC = features.getIndex("DIFF_LOC");

		boolean val_person_old = false;
		boolean val_loc_old = false;
		boolean val_person_new = false;
		boolean val_loc_new = false;
		double diff_person = 0.0;
		double diff_loc = 0.0;

		if (!batchMode) {
			String oldSents = extractOldSentence(doc, oldIndexes);
			String newSents = extractNewSentence(doc, newIndexes);
			ArrayList<String> ners = spa.collectNER(spa
					.annotateSingleSentence(oldSents));
			ArrayList<String> ners2 = spa.collectNER(spa
					.annotateSingleSentence(newSents));

			if (ners.size() != 0) {
				for (String ner : ners) {
					if (ner.startsWith("PERSON")) {
						val_person_old = true;
					} else if (ner.startsWith("LOCATION")) {
						val_loc_old = true;
					}
				}
			}

			if (ners2.size() != 0) {
				for (String ner : ners2) {
					if (ner.startsWith("PERSON")) {
						val_person_new = true;
					} else if (ner.startsWith("LOCATION")) {
						val_loc_new = true;
					}
				}
			}
		} else {
			String docName = BatchFeatureWriter.getFileName(doc
					.getDocumentName());
			for (Integer oldIndex : oldIndexes) {
				if (oldIndex != -1) {
					if (store.getOldDraft().get(docName).containsKey(oldIndex)) {
						SentenceInfo sInfo = store.getOldDraft().get(docName)
								.get(oldIndex);
						if (sInfo.getNumofPERSON() > 0) {
							val_person_old = true;
						}
						if (sInfo.getNumofLocation() > 0) {
							val_loc_old = true;
						}
					}
				}
			}

			for (Integer newIndex : newIndexes) {
				if (newIndex != -1) {
					// System.out.println(docName);
					// System.out.println(newIndex);
					if (store.getNewDraft().get(docName).containsKey(newIndex)) {
						SentenceInfo sInfo = store.getNewDraft().get(docName)
								.get(newIndex);
						if (sInfo.getNumofPERSON() > 0) {
							val_person_new = true;
						}
						if (sInfo.getNumofLocation() > 0) {
							val_loc_new = true;
						}
					}
				}
			}
		}

		double val_person_new_num = 0.0;
		double val_person_old_num = 0.0;
		double val_loc_new_num = 0.0;
		double val_loc_old_num = 0.0;
		if (val_person_new == true)
			val_person_new_num = 1.0;
		if (val_person_old == true)
			val_person_old_num = 1.0;
		if (val_loc_new == true)
			val_loc_new_num = 1.0;
		if (val_loc_old == true)
			val_loc_old_num = 1.0;
		diff_person = val_person_new_num - val_person_old_num;
		diff_loc = val_loc_new_num - val_loc_old_num;

		featureVector[HAS_NER_OLD] = Boolean.toString(val_person_old);
		featureVector[HAS_LOC_OLD] = Boolean.toString(val_loc_old);
		featureVector[HAS_NER_NEW] = Boolean.toString(val_person_new);
		featureVector[HAS_LOC_NEW] = Boolean.toString(val_loc_new);
		featureVector[DIFF_NER] = diff_person;
		featureVector[DIFF_LOC] = diff_loc;
	}

	public void extractNERFeature(RevisionDocument doc, RevisionUnit ru) {
		ArrayList<Integer> newIndexes = ru.getNewSentenceIndex();
		ArrayList<Integer> oldIndexes = ru.getOldSentenceIndex();
		extractNERFeature(doc, newIndexes, oldIndexes);
	}

	// --------------------------inserting LEN
	// features----------------------------------------
	public void insertLenFeature() {
		features.insertFeature("LEN_SEN_OLD", Double.TYPE);
		features.insertFeature("LEN_SEN_NEW", Double.TYPE);
		features.insertFeature("LEN_SEN_DIFF", Double.TYPE);
	}

	public void extractLENFeature(RevisionDocument doc, RevisionUnit ru) {
		ArrayList<Integer> newIndexes = ru.getNewSentenceIndex();
		ArrayList<Integer> oldIndexes = ru.getOldSentenceIndex();
		extractLENFeature(doc, newIndexes, oldIndexes);
	}

	public void extractLENFeature(RevisionDocument doc,
			ArrayList<Integer> newIndexes, ArrayList<Integer> oldIndexes) {
		String oldSent = extractOldSentence(doc, oldIndexes);
		String newSent = extractNewSentence(doc, newIndexes);

		int LEN_SEN = features.getIndex("LEN_SEN_OLD");
		featureVector[LEN_SEN] = oldSent.length() * 1.0;
		int LEN_SEN_NEW = features.getIndex("LEN_SEN_NEW");
		featureVector[LEN_SEN_NEW] = newSent.length() * 1.0;
		int LEN_SEN_DIFF = features.getIndex("LEN_SEN_DIFF");
		featureVector[LEN_SEN_DIFF] = 1.0 * (newSent.length() - oldSent
				.length());
	}

	// --------------------------insert complex POS features from
	// Falak--------------------------

	String sd = "StartwithDT";
	String si = "StartwithIN";
	String sn = "StartwithNN";
	String sa = "StartwithAJ";
	String sv = "StartwithVBG";
	String pdtn = "P_DT_N";
	String dtnp = "DT_N_P";
	String dtjjn = "DT_JJ_N";

	public void insertComplexPOSFeatures() {
		features.insertFeature(sd, Double.TYPE);
		features.insertFeature(si, Double.TYPE);
		features.insertFeature(sn, Double.TYPE);
		features.insertFeature(sa, Double.TYPE);
		features.insertFeature(sv, Double.TYPE);
		features.insertFeature(pdtn, Double.TYPE);
		features.insertFeature(dtnp, Double.TYPE);
		features.insertFeature(dtjjn, Double.TYPE);
	}

	public void extractComplexPOSFeatures(String text) {
		int featureIndex = features.getIndex(sd);
		double val = 0.0;
		CoreMap cm = spa.annotateSingleSentence(text);

		String tag = "DT";
		val = getStartVal(cm, tag);
		featureVector[featureIndex] = val;

		tag = "IN";
		featureIndex = features.getIndex(si);
		val = getStartVal(cm, tag);
		featureVector[featureIndex] = val;

		tag = "NN";
		featureIndex = features.getIndex(sn);
		val = getStartVal(cm, tag);
		featureVector[featureIndex] = val;

		tag = "JJ";
		featureIndex = features.getIndex(sa);
		val = getStartVal(cm, tag);
		if (val == 0) {
			tag = "RB";
			val = getStartVal(cm, tag);
		}
		featureVector[featureIndex] = val;

		tag = "VBG";
		featureIndex = features.getIndex(sv);
		val = getStartVal(cm, tag);
		featureVector[featureIndex] = val;

		String pattern_N = "(<PRP>|<CD>|<DT>|<JJ>|<JJS>|<JJR>|<NN>|<NNS>|<NNP>|<NNPS>|<POS>|<PRP>|<RB>|<RBR>|<RBS>|<VBN>|<VBG>)";
		String pattern_P = "(<RB>?(<IN>|<TO>|<RP>)<RB>?)";
		// pdtn
		String pattern_PDTN = pattern_P + "<DT>" + pattern_N;
		pattern_PDTN = transformPattern(pattern_PDTN);

		val = 0.0;
		if (spa.containPattern(cm, pattern_PDTN))
			val = 1.0;
		featureIndex = features.getIndex(pdtn);
		featureVector[featureIndex] = val;

		// dtnp
		String pattern_DTNP = "<DT>" + pattern_N + pattern_P;
		pattern_DTNP = transformPattern(pattern_DTNP);
		val = 0.0;
		if (spa.containPattern(cm, pattern_DTNP))
			val = 1.0;
		featureIndex = features.getIndex(dtnp);
		featureVector[featureIndex] = val;

		// dtjjn
		String pattern_DTJJN = "<DT><JJ>" + pattern_N;
		pattern_DTJJN = transformPattern(pattern_DTJJN);
		val = 0.0;
		if (spa.containPattern(cm, pattern_DTJJN))
			val = 1.0;
		featureIndex = features.getIndex(dtjjn);
		featureVector[featureIndex] = val;
	}

	// --------------------------insert keyword overlap features
	String ko = "KEYWORD_OVERLAP";

	public void insertKeywordOverlap() {
		features.insertFeature(ko, Double.TYPE);
	}

	public void extractKeywordOverlap(String sentence, HashSet<String> keywords) {
		String[] words = sentence.split(" ");
		double overlap = 0;
		for (String word : words) {
			if (keywords.contains(word))
				overlap++;
		}

		int featureIndex = features.getIndex(ko);
		featureVector[featureIndex] = overlap;
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

	public void insertReasoningKeywords() {
		features.insertFeature(rk, Double.TYPE);
		features.insertFeature(rkN, Double.TYPE);
	}

	public void extractReasoningKeywords(RevisionDocument doc, RevisionUnit ru) {
		ArrayList<Integer> newIndexes = ru.getNewSentenceIndex();
		ArrayList<Integer> oldIndexes = ru.getOldSentenceIndex();
		extractReasoningKeywords(doc, newIndexes, oldIndexes);
	}

	public void extractReasoningKeywords(RevisionDocument doc,
			ArrayList<Integer> newIndexes, ArrayList<Integer> oldIndexes) {
		String newText = extractNewSentence(doc, newIndexes);
		double isReasoning = 0.0;
		for (String word : reasonKeywords) {
			if (newText.contains(word)) {
				isReasoning = 1.0;
				break;
			}
		}
		int featureIndex = features.getIndex(rkN);
		featureVector[featureIndex] = isReasoning;

		String oldText = extractOldSentence(doc, oldIndexes);
		isReasoning = 0.0;
		for (String word : reasonKeywords) {
			if (oldText.contains(word)) {
				isReasoning = 1.0;
				break;
			}
		}
		featureIndex = features.getIndex(rk);
		featureVector[featureIndex] = isReasoning;
	}

	public void insertClaimKeywords() {
		features.insertFeature(ck, Double.TYPE);
		features.insertFeature(ckN, Double.TYPE);
	}

	public void extractClaimKeywords(RevisionDocument doc, RevisionUnit ru) {
		ArrayList<Integer> newIndexes = ru.getNewSentenceIndex();
		ArrayList<Integer> oldIndexes = ru.getOldSentenceIndex();
		extractClaimKeywords(doc, newIndexes, oldIndexes);
	}

	public void extractClaimKeywords(RevisionDocument doc,
			ArrayList<Integer> newIndexes, ArrayList<Integer> oldIndexes) {
		String newText = extractNewSentence(doc, newIndexes);
		double isReasoning = 0.0;
		for (String word : claimKeywords) {
			if (newText.contains(word)) {
				isReasoning = 1.0;
				break;
			}
		}
		int featureIndex = features.getIndex(ckN);
		featureVector[featureIndex] = isReasoning;

		String oldText = extractOldSentence(doc, oldIndexes);
		isReasoning = 0.0;
		for (String word : claimKeywords) {
			if (oldText.contains(word)) {
				isReasoning = 1.0;
				break;
			}
		}
		featureIndex = features.getIndex(ck);
		featureVector[featureIndex] = isReasoning;
	}

	public void insertEvidenceKeywords() {
		features.insertFeature(ek, Double.TYPE);
		features.insertFeature(ekN, Double.TYPE);
	}

	public void extractEvidenceKeywords(RevisionDocument doc, RevisionUnit ru) {
		extractEvidenceKeywords(doc, ru.getNewSentenceIndex(),
				ru.getOldSentenceIndex());
	}

	public void extractEvidenceKeywords(RevisionDocument doc,
			ArrayList<Integer> newIndexes, ArrayList<Integer> oldIndexes) {
		String newText = extractNewSentence(doc, newIndexes);
		double isReasoning = 0.0;
		for (String word : evidenceKeywords) {
			if (newText.contains(word)) {
				isReasoning = 1.0;
				break;
			}
		}
		int featureIndex = features.getIndex(ekN);
		featureVector[featureIndex] = isReasoning;

		String oldText = extractOldSentence(doc, oldIndexes);
		isReasoning = 0.0;
		for (String word : evidenceKeywords) {
			if (oldText.contains(word)) {
				isReasoning = 1.0;
				break;
			}
		}
		featureIndex = features.getIndex(ek);
		featureVector[featureIndex] = isReasoning;
	}

	public void insertRebuttalKeywords() {
		features.insertFeature(rebK, Double.TYPE);
		features.insertFeature(rebKN, Double.TYPE);
	}

	public void extractRebuttalKeywords(RevisionDocument doc, RevisionUnit ru) {
		extractRebuttalKeywords(doc, ru.getNewSentenceIndex(),
				ru.getOldSentenceIndex());
	}

	public void extractRebuttalKeywords(RevisionDocument doc,
			ArrayList<Integer> newIndexes, ArrayList<Integer> oldIndexes) {
		String newText = extractNewSentence(doc, newIndexes);
		double isReasoning = 0.0;
		for (String word : rebutKeywords) {
			if (newText.contains(word)) {
				isReasoning = 1.0;
				break;
			}
		}
		int featureIndex = features.getIndex(rebKN);
		featureVector[featureIndex] = isReasoning;

		String oldText = extractOldSentence(doc, oldIndexes);
		isReasoning = 0.0;
		for (String word : rebutKeywords) {
			if (oldText.contains(word)) {
				isReasoning = 1.0;
				break;
			}
		}
		featureIndex = features.getIndex(rebK);
		featureVector[featureIndex] = isReasoning;
	}

	// --------------------------insert comma features from
	// Falak------------------------

	String hasCommaOld = "HAS_COMMA_OLD";
	String hasCommaNew = "HAS_COMMA_NEW";
	String numCommaOld = "NUM_COMMA_OLD";
	String numCommaNew = "NUM_COMMA_NEW";
	String commaDiff = "NUM_COMMA_DIFF";

	public void insertComma() {
		features.insertFeature(hasCommaOld, Double.TYPE);
		features.insertFeature(hasCommaNew, Double.TYPE);
		features.insertFeature(numCommaOld, Double.TYPE);
		features.insertFeature(numCommaNew, Double.TYPE);
		features.insertFeature(commaDiff, Double.TYPE);
	}

	public void extractCommaFeatures(RevisionDocument doc, RevisionUnit ru) {
		extractCommaFeatures(doc, ru.getNewSentenceIndex(),
				ru.getOldSentenceIndex());
	}

	public void extractCommaFeatures(RevisionDocument doc,
			ArrayList<Integer> newIndexes, ArrayList<Integer> oldIndexes) {
		double hasCommaOldVal = 0.0;
		double numCommaOldVal = 0.0;
		double hasCommaNewVal = 0.0;
		double numCommaNewVal = 0.0;
		double numCommaDiff = 0.0;

		String oldText = extractOldSentence(doc, oldIndexes);
		String newText = extractNewSentence(doc, newIndexes);

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
		int featureIndex = features.getIndex(hasCommaOld);
		featureVector[featureIndex] = hasCommaOldVal;
		featureIndex = features.getIndex(numCommaOld);
		featureVector[featureIndex] = numCommaOldVal;

		featureIndex = features.getIndex(hasCommaNew);
		featureVector[featureIndex] = hasCommaNewVal;
		featureIndex = features.getIndex(numCommaNew);
		featureVector[featureIndex] = numCommaNewVal;

		featureIndex = features.getIndex(commaDiff);
		featureVector[featureIndex] = numCommaDiff;
	}

	// --------------------------insert Simple POS
	// features------------------------------
	public void insertSimplePOSFeatureRatio() {
		features.insertFeature("JJ_OLD", Double.TYPE);
		features.insertFeature("NN_OLD", Double.TYPE);
		features.insertFeature("RB_OLD", Double.TYPE);
		features.insertFeature("VB_OLD", Double.TYPE);

		features.insertFeature("JJ_NEW", Double.TYPE);
		features.insertFeature("NN_NEW", Double.TYPE);
		features.insertFeature("RB_NEW", Double.TYPE);
		features.insertFeature("VB_NEW", Double.TYPE);

		features.insertFeature("JJ_RATIO_OLD", Double.TYPE);
		features.insertFeature("NN_RATIO_OLD", Double.TYPE);
		features.insertFeature("RB_RATIO_OLD", Double.TYPE);
		features.insertFeature("VB_RATIO_OLD", Double.TYPE);

		features.insertFeature("JJ_RATIO_NEW", Double.TYPE);
		features.insertFeature("NN_RATIO_NEW", Double.TYPE);
		features.insertFeature("RB_RATIO_NEW", Double.TYPE);
		features.insertFeature("VB_RATIO_NEW", Double.TYPE);
	}

	public void extractSimplePOSFeatures(RevisionDocument doc, RevisionUnit ru) {
		extractSimplePOSFeatures(doc, ru.getNewSentenceIndex(),
				ru.getOldSentenceIndex());
	}

	public void extractSimplePOSFeatures(RevisionDocument doc,
			ArrayList<Integer> newIndexes, ArrayList<Integer> oldIndexes) {
		String name = BatchFeatureWriter.getFileName(doc.getDocumentName());

		if (!batchMode) {
			String oldText = extractOldSentence(doc, oldIndexes);
			String newText = extractNewSentence(doc, newIndexes);
			// if(oldText==null||oldText.length()==0) oldText = "Dummy";
			// if(newText==null||newText.length()==0) newText = "Dummy";
			Hashtable<String, Double> posTable = StanfordParserAssist
					.getInstance().collectSimplePOSRatio(
							StanfordParserAssist.getInstance()
									.annotateSingleSentence(oldText));
			for (String pos : posTable.keySet()) {
				double ratio = posTable.get(pos);
				int index = features.getIndex(pos + "_OLD");
				featureVector[index] = ratio;
			}

			posTable = StanfordParserAssist.getInstance()
					.collectSimplePOSRatio(
							StanfordParserAssist.getInstance()
									.annotateSingleSentence(newText));
			for (String pos : posTable.keySet()) {
				double ratio = posTable.get(pos);
				int index = features.getIndex(pos + "_NEW");
				featureVector[index] = ratio;
			}
		} else {
			Hashtable<Integer, SentenceInfo> oldInfo = store.getOldDraft().get(
					name);
			Hashtable<Integer, SentenceInfo> newInfo = store.getNewDraft().get(
					name);

			double jjOld = 0, jjNew = 0, nnOld = 0, nnNew = 0, rbOld = 0, rbNew = 0, vbOld = 0, vbNew = 0, totalOld = 0, totalNew = 0;

			for (Integer oldIndex : oldIndexes) {
				jjOld += (double) oldInfo.get(oldIndex).getNumofJJ();
				nnOld += (double) oldInfo.get(oldIndex).getNumofNN();
				rbOld += (double) oldInfo.get(oldIndex).getNumofRB();
				vbOld += (double) oldInfo.get(oldIndex).getNumofVB();
				totalOld += (double) oldInfo.get(oldIndex).getTotal();
			}
			for (Integer newIndex : newIndexes) {
				jjNew += (double) newInfo.get(newIndex).getNumofJJ();
				nnNew += (double) newInfo.get(newIndex).getNumofNN();
				rbNew += (double) newInfo.get(newIndex).getNumofRB();
				vbNew += (double) newInfo.get(newIndex).getNumofVB();
				totalNew += (double) newInfo.get(newIndex).getTotal();
			}

			int index = features.getIndex("JJ_OLD");
			featureVector[index] = jjOld;
			index = features.getIndex("NN_OLD");
			featureVector[index] = nnOld;
			index = features.getIndex("RB_OLD");
			featureVector[index] = rbOld;
			index = features.getIndex("VB_OLD");
			featureVector[index] = vbOld;

			index = features.getIndex("JJ_RATIO_OLD");
			featureVector[index] = jjOld / totalOld;
			index = features.getIndex("NN_RATIO_OLD");
			featureVector[index] = nnOld / totalOld;
			index = features.getIndex("RB_RATIO_OLD");
			featureVector[index] = rbOld / totalOld;
			index = features.getIndex("VB_RATIO_OLD");
			featureVector[index] = vbOld / totalOld;

			index = features.getIndex("JJ_NEW");
			featureVector[index] = jjNew;
			index = features.getIndex("NN_NEW");
			featureVector[index] = nnNew;
			index = features.getIndex("RB_NEW");
			featureVector[index] = rbNew;
			index = features.getIndex("VB_NEW");
			featureVector[index] = vbNew;

			index = features.getIndex("JJ_RATIO_NEW");
			featureVector[index] = jjNew / totalNew;
			index = features.getIndex("NN_RATIO_NEW");
			featureVector[index] = nnNew / totalNew;
			index = features.getIndex("RB_RATIO_NEW");
			featureVector[index] = rbNew / totalNew;
			index = features.getIndex("VB_RATIO_NEW");
			featureVector[index] = vbNew / totalNew;
		}
	}

	public void insertPOSDiff() {
		features.insertFeature("JJ_DIFF", Double.TYPE);
		features.insertFeature("NN_DIFF", Double.TYPE);
		features.insertFeature("RB_DIFF", Double.TYPE);
		features.insertFeature("VB_DIFF", Double.TYPE);
	}

	public void extractPOSDiff(RevisionDocument doc,
			ArrayList<Integer> newIndexes, ArrayList<Integer> oldIndexes) {
		if (!batchMode) {
			String oldSentence = extractOldSentence(doc, oldIndexes);
			String newSentence = extractNewSentence(doc, newIndexes);
			if (oldSentence == null || oldSentence.length() == 0)
				oldSentence = "Dummy";
			if (newSentence == null || newSentence.length() == 0)
				newSentence = "Dummy";
			Hashtable<String, Double> posTable = StanfordParserAssist
					.getInstance().collectSimplePOSDiff(
							StanfordParserAssist.getInstance()
									.annotateSingleSentence(oldSentence),
							StanfordParserAssist.getInstance()
									.annotateSingleSentence(newSentence));

			String pos = "JJ";
			int featureIndex = features.getIndex("JJ_DIFF");
			featureVector[featureIndex] = posTable.get(pos);

			pos = "NN";
			featureIndex = features.getIndex("NN_DIFF");
			featureVector[featureIndex] = posTable.get(pos);

			pos = "RB";
			featureIndex = features.getIndex("RB_DIFF");
			featureVector[featureIndex] = posTable.get(pos);

			pos = "VB";
			featureIndex = features.getIndex("VB_DIFF");
			featureVector[featureIndex] = posTable.get(pos);
		} else {
			String docName = BatchFeatureWriter.getFileName(doc
					.getDocumentName());
			Hashtable<Integer, SentenceInfo> oldInfo = store.getOldDraft().get(
					docName);
			Hashtable<Integer, SentenceInfo> newInfo = store.getNewDraft().get(
					docName);

			double jjOld = 0, jjNew = 0, nnOld = 0, nnNew = 0, rbOld = 0, rbNew = 0, vbOld = 0, vbNew = 0, totalOld = 0, totalNew = 0;

			for (Integer oldIndex : oldIndexes) {
				jjOld += (double) oldInfo.get(oldIndex).getNumofJJ();
				nnOld += (double) oldInfo.get(oldIndex).getNumofNN();
				rbOld += (double) oldInfo.get(oldIndex).getNumofRB();
				vbOld += (double) oldInfo.get(oldIndex).getNumofVB();
				totalOld += (double) oldInfo.get(oldIndex).getTotal();
			}
			for (Integer newIndex : newIndexes) {
				jjNew += (double) newInfo.get(newIndex).getNumofJJ();
				nnNew += (double) newInfo.get(newIndex).getNumofNN();
				rbNew += (double) newInfo.get(newIndex).getNumofRB();
				vbNew += (double) newInfo.get(newIndex).getNumofVB();
				totalNew += (double) newInfo.get(newIndex).getTotal();
			}

			int featureIndex = features.getIndex("JJ_DIFF");
			featureVector[featureIndex] = jjNew - jjOld;

			featureIndex = features.getIndex("NN_DIFF");
			featureVector[featureIndex] = nnNew - nnOld;

			featureIndex = features.getIndex("RB_DIFF");
			featureVector[featureIndex] = rbNew - rbOld;

			featureIndex = features.getIndex("VB_DIFF");
			featureVector[featureIndex] = vbNew - vbOld;
		}
	}

	public void extractPOSDiff(RevisionDocument doc, RevisionUnit ru) {
		extractPOSDiff(doc, ru.getNewSentenceIndex(), ru.getOldSentenceIndex());
	}

	/*
	 * public void insertOverlapFeature() {
	 * features.insertFeature("COUNT_OVERLAP_OLD", Double.TYPE);
	 * features.insertFeature("COUNT_OVERLAP_NEW", Double.TYPE); }
	 * 
	 * public void extractOverlapFeature(RevisionDocument doc, RevisionUnit ru)
	 * { String prompt = doc.getPromptContent(); int featureIndex =
	 * features.getIndex("COUNT_OVERLAP_OLD"); String oldSent =
	 * extractOldSentence(doc, ru); double cnt = 0; if (oldSent != null &&
	 * oldSent.trim().length() > 0) { Hashtable<String, Integer> contentWords =
	 * spa.getContentWords(spa .annotateSingleSentence(oldSent)); for (String
	 * contentWord : contentWords.keySet()) { if (prompt.contains(contentWord))
	 * cnt += contentWords.get(contentWord); } } featureVector[featureIndex] =
	 * cnt; featureIndex = features.getIndex("COUNT_OVERLAP_NEW"); String
	 * newSent = extractNewSentence(doc, ru); cnt = 0; if (newSent != null &&
	 * newSent.trim().length() > 0) { Hashtable<String, Integer> contentWords =
	 * spa.getContentWords(spa .annotateSingleSentence(newSent)); for (String
	 * contentWord : contentWords.keySet()) { if (prompt.contains(contentWord))
	 * cnt += contentWords.get(contentWord); } } featureVector[featureIndex] =
	 * cnt; }
	 */

	// --------------------------insert Text for generating ngram
	// features-------------------------
	public void insertText() {
		features.insertFeature("Text", String.class);
		features.insertFeature("TEXTDIFF", String.class);
	}

	public void extractTextFeatures(String text) {
		int TEXT = features.getIndex("Text");
		text = text.replaceAll(",", " ");
		text = text.replaceAll("_", " ");
		featureVector[TEXT] = text;
	}

	public void extractTextFeatures2(RevisionDocument doc, RevisionUnit ru) {
		String oldSent = extractOldSentence(doc, ru);
		String newSent = extractNewSentence(doc, ru);

		oldSent = oldSent.replaceAll(",", " ");
		oldSent = oldSent.replaceAll("_", " ");
		newSent = newSent.replaceAll(",", " ");
		newSent = newSent.replaceAll("_", " ");

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
		int featureIndex = features.getIndex("TEXTDIFF");
		featureVector[featureIndex] = diff;

	}

	public void extractTextFeatures2(RevisionDocument doc,
			ArrayList<Integer> newIndexes, ArrayList<Integer> oldIndexes) {
		String oldSent = extractOldSentence(doc, oldIndexes);
		String newSent = extractNewSentence(doc, newIndexes);

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
		int featureIndex = features.getIndex("TEXTDIFF");
		featureVector[featureIndex] = diff;

	}

	// ---------------------------insert revision op
	// features--------------------
	public void insertOp() {
		features.insertFeature("REVISION_OP", Double.class);
	}

	public void extractOpFeatures(RevisionUnit ru) {
		int featureIndex = features.getIndex("REVISION_OP");
		double val = 0;
		if (ru.getRevision_op() == RevisionOp.ADD) {
			val = 1;
		} else if (ru.getRevision_op() == RevisionOp.DELETE) {
			val = 0;
		} else {
			val = -1;
		}
		featureVector[featureIndex] = val;
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
	public void insertLocGroup() {
		insertLocationFeature();
	}

	public void extractLocGroup(RevisionDocument doc, RevisionUnit ru) {
		extractLocFeature(doc, ru);
	}

	public void extractLocGroup(RevisionDocument doc,
			ArrayList<Integer> newIndexes, ArrayList<Integer> oldIndexes) {
		extractLocFeature(doc, newIndexes, oldIndexes);
	}

	// ----------------------Text group---------------------------------
	public void insertTextGroup() {
		insertClaimKeywords();
		insertEvidenceKeywords();
		insertRebuttalKeywords();
		insertReasoningKeywords();
		insertComma();
		insertOp();
		insertLenFeature();
		insertDaTextualFeature();
		// insertNERFeature();
		// insertOverlapFeature();
	}

	public void insertTextGroupPriorPost(int windowSize) {
		// insertOpPriorPost(windowSize);
		insertLenFeaturePriorPost(windowSize);
		insertDaTextualFeaturePriorPost(windowSize);
	}

	public void insertDaTextualFeaturePriorPost(int windowSize) {
		String tag = "_PRIOR_";
		String tag2 = "_POST_";
		String[] tags = { tag, tag2 };
		for (String tmp : tags) {
			for (int i = 1; i <= windowSize; i++) {
				features.insertFeature("COSINE_SIM" + tmp + i, Double.TYPE);
			}
		}
	}

	public void insertOpPriorPost(int windowSize) {
		String tag = "_PRIOR_";
		String tag2 = "_POST_";
		String[] tags = { tag, tag2 };
		for (String tmp : tags) {
			for (int i = 1; i <= windowSize; i++) {
				features.insertFeature("REVISION_OP" + tmp + i, Double.class);
			}
		}
	}

	public void insertLenFeaturePriorPost(int windowSize) {
		String tag = "_PRIOR_";
		String tag2 = "_POST_";
		String[] tags = { tag, tag2 };
		for (String tmp : tags) {
			for (int i = 1; i <= windowSize; i++) {
				features.insertFeature("LEN_SEN_OLD" + tmp + i, Double.TYPE);
				features.insertFeature("LEN_SEN_NEW" + tmp + i, Double.TYPE);
				features.insertFeature("LEN_SEN_DIFF" + tmp + i, Double.TYPE);
			}
		}
	}

	public int getBefore(ArrayList<Integer> indices, int windowSize) {
		if (indices == null || indices.size() == 0)
			return -1;
		Collections.sort(indices);
		return indices.get(0) - 1;
	}

	public int getAfter(ArrayList<Integer> indices, int windowSize) {
		if (indices == null || indices.size() == 0)
			return -1;
		Collections.sort(indices);
		return indices.get(indices.size() - 1) + 1;
	}

	public void extractTextGroup(RevisionDocument doc, RevisionUnit ru) {
		extractClaimKeywords(doc, ru);
		extractEvidenceKeywords(doc, ru);
		extractRebuttalKeywords(doc, ru);
		extractReasoningKeywords(doc, ru);
		extractCommaFeatures(doc, ru);
		extractOpFeatures(ru);
		extractLENFeature(doc, ru);
		extractDaTextualFeature(doc, ru);
		// extractNERFeature(doc, ru);
		// extractOverlapFeature(doc, ru);
	}

	public void extractTextGroupPriorPost(int windowSize, RevisionDocument doc,
			ArrayList<Integer> newIndexes, ArrayList<Integer> oldIndexes) {
		// extractOpFeaturesPriorPost(windowSize, doc, newIndexes, oldIndexes);
		extractLENFeaturePriorPost(windowSize, doc, newIndexes, oldIndexes);
		extractDaTextualFeaturePriorPost(windowSize, doc, newIndexes,
				oldIndexes);
		// extractNERFeature(doc, ru);
		// extractOverlapFeature(doc, ru);
	}

	public void extractLENFeaturePriorPost(int windowSize,
			RevisionDocument doc, ArrayList<Integer> newIndexes,
			ArrayList<Integer> oldIndexes) {

		String tag = "_PRIOR_";
		String tag2 = "_POST_";
		String[] tags = { tag, tag2 };
		for (String tmp : tags) {
			for (int i = 1; i <= windowSize; i++) {
				if (tmp.equals(tag)) {
					int priorOld = getBefore(oldIndexes, windowSize);
					int priorNew = getBefore(newIndexes, windowSize);
					ArrayList<Integer> tmpOld = new ArrayList<Integer>();
					ArrayList<Integer> tmpNew = new ArrayList<Integer>();
					if (priorOld != -1
							&& priorOld <= doc.getOldDraftSentences().size()) {
						tmpOld.add(priorOld);
					}
					if (priorNew != -1
							&& priorNew <= doc.getNewDraftSentences().size()) {
						tmpNew.add(priorNew);
					}
					String oldSent = extractOldSentence(doc, tmpOld);
					String newSent = extractNewSentence(doc, tmpNew);
					int LEN_SEN = features.getIndex("LEN_SEN_OLD" + tmp + i);
					featureVector[LEN_SEN] = oldSent.length() * 1.0;
					int LEN_SEN_NEW = features
							.getIndex("LEN_SEN_NEW" + tmp + i);
					featureVector[LEN_SEN_NEW] = newSent.length() * 1.0;
					int LEN_SEN_DIFF = features.getIndex("LEN_SEN_DIFF" + tmp
							+ i);
					featureVector[LEN_SEN_DIFF] = 1.0 * (newSent.length() - oldSent
							.length());
				} else if (tmp.equals(tag2)) {
					int postOld = getAfter(oldIndexes, windowSize);
					int postNew = getAfter(newIndexes, windowSize);
					ArrayList<Integer> tmpOld = new ArrayList<Integer>();
					ArrayList<Integer> tmpNew = new ArrayList<Integer>();
					if (postOld != -1
							&& postOld <= doc.getOldDraftSentences().size()) {
						tmpOld.add(postOld);
					}
					if (postNew != -1
							&& postNew <= doc.getNewDraftSentences().size()) {
						tmpNew.add(postNew);
					}
					String oldSent = extractOldSentence(doc, tmpOld);
					String newSent = extractNewSentence(doc, tmpNew);
					int LEN_SEN = features.getIndex("LEN_SEN_OLD" + tmp + i);
					featureVector[LEN_SEN] = oldSent.length() * 1.0;
					int LEN_SEN_NEW = features
							.getIndex("LEN_SEN_NEW" + tmp + i);
					featureVector[LEN_SEN_NEW] = newSent.length() * 1.0;
					int LEN_SEN_DIFF = features.getIndex("LEN_SEN_DIFF" + tmp
							+ i);
					featureVector[LEN_SEN_DIFF] = 1.0 * (newSent.length() - oldSent
							.length());
				}
			}
		}
	}

	public void extractDaTextualFeaturePriorPost(int windowSize,
			RevisionDocument doc, ArrayList<Integer> newIndexes,
			ArrayList<Integer> oldIndexes) {
		// Notice the cosine here is not normalized
		String tag = "_PRIOR_";
		String tag2 = "_POST_";
		String[] tags = { tag, tag2 };
		for (String tmp : tags) {
			for (int i = 1; i <= windowSize; i++) {
				if (tmp.equals(tag)) {
					int priorOld = getBefore(oldIndexes, windowSize);
					int priorNew = getBefore(newIndexes, windowSize);
					ArrayList<Integer> tmpOld = new ArrayList<Integer>();
					ArrayList<Integer> tmpNew = new ArrayList<Integer>();

					if (priorOld != -1
							&& priorOld <= doc.getOldDraftSentences().size()) {
						tmpOld.add(priorOld);
					}
					if (priorNew != -1
							&& priorNew <= doc.getNewDraftSentences().size()) {
						tmpNew.add(priorNew);
					}
					String oldSent = extractOldSentence(doc, tmpOld);
					String newSent = extractNewSentence(doc, tmpNew);

					double cosineSim = OtherAssist.getCosine(oldSent, newSent);
					int COSINE_SIM = features.getIndex("COSINE_SIM" + tmp + i);
					featureVector[COSINE_SIM] = cosineSim * 1.0;
				} else if (tmp.equals(tag2)) {
					int postOld = getAfter(oldIndexes, windowSize);
					int postNew = getAfter(newIndexes, windowSize);
					ArrayList<Integer> tmpOld = new ArrayList<Integer>();
					ArrayList<Integer> tmpNew = new ArrayList<Integer>();
					if (postOld != -1
							&& postOld <= doc.getOldDraftSentences().size()) {
						tmpOld.add(postOld);
					}
					if (postNew != -1
							&& postNew <= doc.getNewDraftSentences().size()) {
						tmpNew.add(postNew);
					}
					String oldSent = extractOldSentence(doc, tmpOld);
					String newSent = extractNewSentence(doc, tmpNew);
					double cosineSim = OtherAssist.getCosine(oldSent, newSent);
					int COSINE_SIM = features.getIndex("COSINE_SIM" + tmp + i);
					featureVector[COSINE_SIM] = cosineSim * 1.0;
				}
			}
		}
	}

	public void extractTextGroup(RevisionDocument doc,
			ArrayList<Integer> newIndexes, ArrayList<Integer> oldIndexes) {
		extractClaimKeywords(doc, newIndexes, oldIndexes);
		extractEvidenceKeywords(doc, newIndexes, oldIndexes);
		extractRebuttalKeywords(doc, newIndexes, oldIndexes);
		extractReasoningKeywords(doc, newIndexes, oldIndexes);
		extractCommaFeatures(doc, newIndexes, oldIndexes);
		extractOpFeatures(newIndexes, oldIndexes);
		extractLENFeature(doc, newIndexes, oldIndexes);
		extractDaTextualFeature(doc, newIndexes, oldIndexes);
		// extractNERFeature(doc, newIndexes, oldIndexes);
		// extractOverlapFeature(doc, ru);
	}

	// ---------------------Language group------------------------------
	public void insertLanguageGroup() {
		// insertSimplePOSFeatureRatio();
		// insertPOSDiff();

		// insertGrammarErrorFeature();
		// insertSpellErrorFeature();
	}

	public void extractLanguageGroup(RevisionDocument doc, RevisionUnit ru) {
		// String sentence = extractSentence(doc, ru);
		// String oldSentence = extractOldSentence(doc, ru);
		// String newSentence = extractNewSentence(doc, ru);

		// extractSimplePOSFeatures(doc, ru);
		// extractPOSDiff(doc, ru);

		// extractGrammarErrorFeature(doc, ru);
		// extractSpellErrorFeature(doc, ru);
	}

	public void extractLanguageGroup(RevisionDocument doc,
			ArrayList<Integer> newIndexes, ArrayList<Integer> oldIndexes) {
		// String sentence = extractSentence(doc, ru);
		// String oldSentence = extractOldSentence(doc, ru);
		// String newSentence = extractNewSentence(doc, ru);
		extractSimplePOSFeatures(doc, newIndexes, oldIndexes);
		extractPOSDiff(doc, newIndexes, oldIndexes);
		extractGrammarErrorFeature(doc, newIndexes, oldIndexes);
		extractSpellErrorFeature(doc, newIndexes, oldIndexes);
	}

	public void insertGrammarErrorFeature() {
		features.insertFeature("GRAMMAR_ERROR_OLD", Double.TYPE);
		features.insertFeature("GRAMMAR_ERROR_NEW", Double.TYPE);
		features.insertFeature("GRAMMAR_ERROR_DIFF", Double.TYPE);
	}

	public void extractGrammarErrorFeature(RevisionDocument doc, RevisionUnit ru) {
		extractGrammarErrorFeature(doc, ru.getNewSentenceIndex(),
				ru.getOldSentenceIndex());
	}

	public void extractGrammarErrorFeature(RevisionDocument doc,
			ArrayList<Integer> newIndexes, ArrayList<Integer> oldIndexes) {
		double val1 = 0.0;
		double val2 = 0.0;
		double val3 = 0.0;

		if (batchMode) {
			String docName = BatchFeatureWriter.getFileName(doc
					.getDocumentName());
			Hashtable<Integer, SentenceInfo> oldInfoTable = store.getOldDraft()
					.get(docName);
			Hashtable<Integer, SentenceInfo> newInfoTable = store.getNewDraft()
					.get(docName);

			for (Integer oldIndex : oldIndexes) {
				if (oldIndex != -1)
					val1 += oldInfoTable.get(oldIndex).getSpellError();
			}
			for (Integer newIndex : newIndexes) {
				if (newIndex != -1)
					val2 += newInfoTable.get(newIndex).getSpellError();
			}
		} else {
			try {
				String oldSent = extractOldSentence(doc, oldIndexes);
				String newSent = extractNewSentence(doc, newIndexes);
				val1 = (double) ja.checkSpellingMistakes(oldSent);
				val2 = (double) ja.checkSpellingMistakes(newSent);
			} catch (Exception exp) {

			}
		}
		val3 = val2 - val1;
		int featureIndex = features.getIndex("GRAMMAR_ERROR_OLD");
		int featureIndex2 = features.getIndex("GRAMMAR_ERROR_NEW");
		int featureIndex3 = features.getIndex("GRAMMAR_ERROR_DIFF");
		featureVector[featureIndex] = val1;
		featureVector[featureIndex2] = val2;
		featureVector[featureIndex3] = val3;

	}

	public void insertSpellErrorFeature() {
		features.insertFeature("SPELL_ERROR_OLD", Double.TYPE);
		features.insertFeature("SPELL_ERROR_NEW", Double.TYPE);
		features.insertFeature("SPELL_ERROR_DIFF", Double.TYPE);
	}

	public void extractSpellErrorFeature(RevisionDocument doc, RevisionUnit ru) {
		extractSpellErrorFeature(doc, ru.getNewSentenceIndex(),
				ru.getOldSentenceIndex());
	}

	public void extractSpellErrorFeature(RevisionDocument doc,
			ArrayList<Integer> newIndexes, ArrayList<Integer> oldIndexes) {
		double val1 = 0.0;
		double val2 = 0.0;
		double val3 = 0.0;

		if (batchMode) {
			String docName = BatchFeatureWriter.getFileName(doc
					.getDocumentName());
			Hashtable<Integer, SentenceInfo> oldInfoTable = store.getOldDraft()
					.get(docName);
			Hashtable<Integer, SentenceInfo> newInfoTable = store.getNewDraft()
					.get(docName);

			for (Integer oldIndex : oldIndexes) {
				if (oldIndex != -1)
					val1 += oldInfoTable.get(oldIndex).getSpellError();
			}
			for (Integer newIndex : newIndexes) {
				if (newIndex != -1)
					val2 += newInfoTable.get(newIndex).getSpellError();
			}
		} else {
			try {
				String oldSent = extractOldSentence(doc, oldIndexes);
				String newSent = extractNewSentence(doc, newIndexes);
				val1 = (double) ja.checkSpellingMistakes(oldSent);
				val2 = (double) ja.checkSpellingMistakes(newSent);
			} catch (Exception exp) {

			}
		}
		val3 = val2 - val1;
		int featureIndex = features.getIndex("SPELL_ERROR_OLD");
		int featureIndex2 = features.getIndex("SPELL_ERROR_NEW");
		int featureIndex3 = features.getIndex("SPELL_ERROR_DIFF");
		featureVector[featureIndex] = val1;
		featureVector[featureIndex2] = val2;
		featureVector[featureIndex3] = val3;

	}

	// ---------------------Meta group---------------------------------
	public void insertMetaGroup() {
		// features.insertFeature("D1SCORE", Double.TYPE);
	}

	TmpInfoStore tis = new TmpInfoStore();

	public void extractMetaGroup(RevisionDocument doc, RevisionUnit ru) {
		/*
		 * int featureIndex = features.getIndex("D1SCORE"); File f = new
		 * File(doc.getDocumentName()); featureVector[featureIndex] =
		 * tis.getD1Score(f.getName());
		 */
	}

	// deprecated Let it gone with the wind
	public void extractMetaGroup(RevisionDocument doc,
			ArrayList<Integer> newIndexes, ArrayList<Integer> oldIndexes) {
		/*
		 * int featureIndex = features.getIndex("D1SCORE"); File f = new
		 * File(doc.getDocumentName()); featureVector[featureIndex] =
		 * tis.getD1Score(f.getName());
		 */
	}

	// -------------------Other group ---------------------------------
	public void insertOtherGroup() {

	}

	public void extractOtherGroup(RevisionDocument doc, RevisionUnit ru) {

	}

	boolean isOnline = true;

	public void setOnline(boolean isOnline) {
		this.isOnline = isOnline;
	}

	// -------------------------Putting everything
	// together-------------------------------
	// buildup features
	public void buildFeatures(boolean usingNgram, ArrayList<String> categories)
			throws IOException {
		insertCategory(categories);
		if (usingNgram)
			insertText(); // Text always start from the first

		insertLocGroup();
		insertTextGroup();
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

	public void extractFeaturesPriorPost(RevisionDocument doc, RevisionUnit ru,
			int windowSize) {
		extractLocFeaturePriorPost(doc, ru.getNewSentenceIndex(),
				ru.getOldSentenceIndex(), windowSize);
		extractTextGroupPriorPost(windowSize, doc, ru.getNewSentenceIndex(),
				ru.getOldSentenceIndex());
	}

	public void extractFeaturesPriorPost(RevisionDocument doc,
			ArrayList<Integer> newIndexes, ArrayList<Integer> oldIndexes,
			int windowSize) {
		extractLocFeaturePriorPost(doc, newIndexes, oldIndexes, windowSize);
		extractTextGroupPriorPost(windowSize, doc, newIndexes, oldIndexes);
	}

	// extract features
	public Object[] extractFeatures(RevisionDocument doc, RevisionUnit ru,
			boolean usingNgram) throws IOException {
		featureVector = new Object[features.getSize()];
		if (usingNgram) {
			String sentence = extractSentence(doc, ru);
			extractTextFeatures(sentence);
			extractTextFeatures2(doc, ru);
		}
		extractLocGroup(doc, ru);
		extractTextGroup(doc, ru);
		// extractLanguageGroup(doc, ru);
		extractMetaGroup(doc, ru);
		if (!isOnline) {
			// extractLanguageGroup(doc, ru);
			SentenceEmbeddingFeatureExtractor.getInstance().extractFeature(
					features, featureVector, doc, ru.getNewSentenceIndex(),
					ru.getOldSentenceIndex());
			// extractOtherGroup(doc, ru);
			PDTBFeatureExtractor.getInstance().extractFeature(features,
					featureVector, doc, ru.getNewSentenceIndex(),
					ru.getOldSentenceIndex());
		}
		return featureVector;
	}

	public Object[] extractFeatures(RevisionDocument doc,
			ArrayList<Integer> newIndexes, ArrayList<Integer> oldIndexes,
			boolean usingNgram) throws IOException {
		featureVector = new Object[features.getSize()];
		if (usingNgram) {
			String sentence = extractSentence(doc, newIndexes, oldIndexes, true);
			extractTextFeatures(sentence);
			extractTextFeatures2(doc, newIndexes, oldIndexes);
		}
		extractLocGroup(doc, newIndexes, oldIndexes);
		extractTextGroup(doc, newIndexes, oldIndexes);
		// extractLanguageGroup(doc, newIndexes, oldIndexes);
		extractMetaGroup(doc, newIndexes, oldIndexes);
		// extractOtherGroup(doc, ru);
		// PDTBFeatureExtractor.getInstance().extractFeature(features,
		// featureVector, doc, newIndexes, oldIndexes);
		if (!isOnline) {

			SentenceEmbeddingFeatureExtractor.getInstance().extractCohesion(
					features, featureVector, doc, newIndexes, oldIndexes);

			extractLanguageGroup(doc, newIndexes, oldIndexes);
			// extractOtherGroup(doc, ru);
			PDTBFeatureExtractor.getInstance().extractFeature(features,
					featureVector, doc, newIndexes, oldIndexes);
			PDTBFeatureExtractor.getInstance().extractFeatureARG1ARG2(features,
					featureVector, doc, newIndexes, oldIndexes);

		}
		return featureVector;
		// String sentence = extractSentence(doc, ru);
		// if (usingNgram)
		// extractTextFeatures(sentence);
		// extractLocFeature(doc, ru);
		// extractNERFeature(sentence);
		// extractLENFeature(sentence);
		// extractSimplePOSFeatures(sentence);
		//
		// extractReasoningKeywords(sentence);
		// extractCommaFeatures(sentence);
		// extractOpFeatures(ru);
		//
		// extractDaTextualFeature(doc, ru);

		// extractComplexPOSFeatures(sentence);
		// extractKeywordOverlap(sentence, extractTopKeywords(doc));
	}

	public Object[] extractFeatures(RevisionDocument doc,
			ArrayList<Integer> newIndexes, ArrayList<Integer> oldIndexes,
			boolean usingNgram, int option) throws IOException {
		featureVector = new Object[features.getSize()];
		if (usingNgram) {
			String sentence = extractSentence(doc, newIndexes, oldIndexes, true);
			extractTextFeatures(sentence);
			extractTextFeatures2(doc, newIndexes, oldIndexes);
		}
		extractLocGroup(doc, newIndexes, oldIndexes);
		extractTextGroup(doc, newIndexes, oldIndexes);
		// extractLanguageGroup(doc, newIndexes, oldIndexes);
		extractMetaGroup(doc, newIndexes, oldIndexes);
		// extractOtherGroup(doc, ru);
		// PDTBFeatureExtractor.getInstance().extractFeature(features,
		// featureVector, doc, newIndexes, oldIndexes);
		if (!isOnline) {
			if (option == 3 || option == 10) {
				SentenceEmbeddingFeatureExtractor.getInstance()
						.extractCohesion(features, featureVector, doc,
								newIndexes, oldIndexes);
			}
			extractLanguageGroup(doc, newIndexes, oldIndexes);
			// extractOtherGroup(doc, ru);
			if (option == 2 || option == 10) {
				PDTBFeatureExtractor.getInstance().extractFeature(features,
						featureVector, doc, newIndexes, oldIndexes);
				PDTBFeatureExtractor.getInstance().extractFeatureARG1ARG2(
						features, featureVector, doc, newIndexes, oldIndexes);
			}
		}
		if (option == 4 || option == 10 || option == 11 || option == 2 || option == 3) {
			extractFeaturesPriorPost(doc, newIndexes, oldIndexes, 1);
		}
		return featureVector;
		// String sentence = extractSentence(doc, ru);
		// if (usingNgram)
		// extractTextFeatures(sentence);
		// extractLocFeature(doc, ru);
		// extractNERFeature(sentence);
		// extractLENFeature(sentence);
		// extractSimplePOSFeatures(sentence);
		//
		// extractReasoningKeywords(sentence);
		// extractCommaFeatures(sentence);
		// extractOpFeatures(ru);
		//
		// extractDaTextualFeature(doc, ru);

		// extractComplexPOSFeatures(sentence);
		// extractKeywordOverlap(sentence, extractTopKeywords(doc));
	}

	public void buildFeatures(boolean usingNgram, ArrayList<String> categories,
			int remove) throws IOException {
		features = new FeatureName();
		insertCategory(categories);
		System.out.println("=======================REMOVE IS:" + remove);
		if (usingNgram)
			insertText(); // Text always start from the first
		if (remove == -1)
			return;
		if (remove == 0 || remove == 10 || remove == 11 || remove == 2
				|| remove == 3)
			insertLocGroup();
		if (remove == 1 || remove == 10 || remove == 11 || remove == 2
				|| remove == 3)
			insertTextGroup();
		if (remove == 2 || remove == 10) {
			// PDTBFeatureExtractor.getInstance().insertARG1ARG2(features);
			//PDTBFeatureExtractor.getInstance().insertFeature(features);
		}
		if (remove == 3 || remove == 10) {
			// insertMetaGroup();
			SentenceEmbeddingFeatureExtractor.getInstance().insertCohesion(
					features);
			SentenceEmbeddingFeatureExtractor.getInstance().insertFeature(
					features);
		}
		if (remove == 4 || remove == 10 || remove == 2 ) {
			insertPriorPostFeatures(1);
		}
	}

	public void buildFeaturesCRF(boolean usingNgram,
			ArrayList<String> categories, int remove) throws IOException {
		features = new FeatureName();
		insertCategory(categories);
		System.out.println("=======================REMOVE IS:" + remove);
		if (usingNgram)
			insertText(); // Text always start from the first
		if (remove == -1)
			return;
		if (remove == 0 || remove == 10 || remove == 11)
			insertLocGroup();
		if (remove == 1 || remove == 10 || remove == 11)
			insertTextGroup();
		if (remove == 2 || remove == 10) {
			// PDTBFeatureExtractor.getInstance().insertARG1ARG2(features);
			//PDTBFeatureExtractor.getInstance().insertFeature(features);
		}
		if (remove == 3 || remove == 10) {
			// insertMetaGroup();
			// SentenceEmbeddingFeatureExtractor.getInstance().insertFeature(
			// features);
			SentenceEmbeddingFeatureExtractor.getInstance().insertCohesion(
					features);
		}
		if (remove == 4 || remove == 10 || remove == 2) {
			insertPriorPostFeatures(1);
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

	// extract features
	public Object[] extractFeatures(RevisionDocument doc, RevisionUnit ru,
			boolean usingNgram, int remove) throws IOException {
		featureVector = new Object[features.getSize()];
		if (usingNgram) {
			String sentence = extractSentence(doc, ru);
			extractTextFeatures(sentence);
			extractTextFeatures2(doc, ru);
		}
		if (remove == -1)
			return featureVector;
		if (remove == 0 || remove == 10 || remove == 11 || remove == 3
				|| remove == 2)
			extractLocGroup(doc, ru);
		if (remove == 1 || remove == 10 || remove == 11 || remove == 3
				|| remove == 2)
			extractTextGroup(doc, ru);
		if (remove == 2 || remove == 10) {
			// extractLanguageGroup(doc, ru);
			//PDTBFeatureExtractor.getInstance().extractFeature(features,
			//		featureVector, doc, ru.getNewSentenceIndex(),
			//		ru.getOldSentenceIndex());
			// PDTBFeatureExtractor.getInstance().extractFeatureARG1ARG2(features,
			// featureVector, doc, ru.getNewSentenceIndex(),
			// ru.getOldSentenceIndex());
		}
		if (remove == 3 || remove == 10)
			// extractMetaGroup(doc, ru);
			// SentenceEmbeddingFeatureExtractor.getInstance().extractFeature(
			// features, featureVector, doc, ru.getNewSentenceIndex(),
			// ru.getOldSentenceIndex());
			SentenceEmbeddingFeatureExtractor.getInstance().extractCohesion(
					features, featureVector, doc, ru.getNewSentenceIndex(),
					ru.getOldSentenceIndex());
		// extractOtherGroup(doc, ru);
		if (remove == 4 || remove == 10 
				|| remove == 2) {
			// extractLanguageGroup(doc, ru);
			extractFeaturesPriorPost(doc, ru.getNewSentenceIndex(),
					ru.getOldSentenceIndex(), 1);
		}
		return featureVector;
		// String sentence = extractSentence(doc, ru);
		// if (usingNgram)
		// extractTextFeatures(sentence);
		// extractLocFeature(doc, ru);
		// extractNERFeature(sentence);
		// extractLENFeature(sentence);
		// extractSimplePOSFeatures(sentence);
		//
		// extractReasoningKeywords(sentence);
		// extractCommaFeatures(sentence);
		// extractOpFeatures(ru);
		//
		// extractDaTextualFeature(doc, ru);

		// extractComplexPOSFeatures(sentence);
		// extractKeywordOverlap(sentence, extractTopKeywords(doc));
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
