package edu.pitt.cs.revision.purpose;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import edu.pitt.cs.revision.batch.BatchFeatureWriter;
import edu.pitt.cs.revision.batch.SentenceInfo;
import edu.pitt.cs.revision.machinelearning.FeatureName;
import edu.pitt.cs.revision.machinelearning.StanfordParserAssist;
import edu.pitt.lrdc.cs.revision.alignment.model.HeatMapUnit;
import edu.pitt.lrdc.cs.revision.model.RevisionDocument;
import edu.pitt.lrdc.cs.revision.model.RevisionOp;
import edu.pitt.lrdc.cs.revision.model.RevisionUnit;

/**
 * Extract features for CRF from heatmapunit objects, batch extraction here
 * 
 * Modify the feature extraction methods
 * 
 * @author zhangfan
 *
 */

class EssayInfo {
	int pD1Num;
	int pD2Num;
	int[] pS1Num;
	int[] pS2Num;
	boolean parsed = false;
}

public class CRFFeatureExtractor extends FeatureExtractor {

	private EssayInfo essayInfo = new EssayInfo();

	public EssayInfo getEssayInfo(ArrayList<ArrayList<HeatMapUnit>> essay) {
		if (essayInfo.parsed == false) {
			int pD1Num = 1;
			int pD2Num = 1;
			int tempS1Num = 1;
			int tempS2Num = 1;
			ArrayList<Integer> sentences1 = new ArrayList<Integer>();
			ArrayList<Integer> sentences2 = new ArrayList<Integer>();
			for (ArrayList<HeatMapUnit> paragraph : essay) {
				for (HeatMapUnit unit : paragraph) {
					if (unit.pD1 > pD1Num) {
						sentences1.add(tempS1Num);
						pD1Num = unit.pD1;
						tempS1Num = unit.sD1;
					} else {
						if (unit.sD1 > tempS1Num)
							tempS1Num = unit.sD1;
					}

					if (unit.pD2 > pD2Num) {
						sentences2.add(tempS2Num);
						pD2Num = unit.pD2;
						tempS2Num = unit.sD2;
					} else {
						if (unit.sD2 > tempS2Num)
							tempS2Num = unit.sD2;
					}
				}
			}
			sentences1.add(tempS1Num);
			sentences2.add(tempS2Num);

			essayInfo.pD1Num = pD1Num;
			essayInfo.pD2Num = pD2Num;

			int[] pS1Num = new int[pD1Num];
			int[] pS2Num = new int[pD2Num];
			for (int i = 0; i < sentences1.size(); i++) {
				pS1Num[i] = sentences1.get(i);
			}
			for (int i = 0; i < sentences2.size(); i++) {
				pS2Num[i] = sentences2.get(i);
			}
			essayInfo.pS1Num = pS1Num;
			essayInfo.pS2Num = pS2Num;
			essayInfo.parsed = true;
		}
		return essayInfo;
	}

	public void extractTextFeatures(String text) {
		int TEXT = features.getIndex("Text");
		featureVector[TEXT] = text;
	}

	// Extract diff
	public void extractTextFeatures2(String scD1, String scD2) {
		String[] tokens = scD1.split(" ");
		String[] newTokens = scD2.split(" ");
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

	public void extractLocGroup(HeatMapUnit hmu,
			ArrayList<ArrayList<HeatMapUnit>> essay) {
		extractLocFeature(hmu, essay);
	}

	public void extractLocFeature(HeatMapUnit hmu,
			ArrayList<ArrayList<HeatMapUnit>> essay) {
		// ru.getNewParagraphNo();
		int LOC_PAR = features.getIndex("LOC_PAR_NEW");
		int LOC_WHOLE = features.getIndex("LOC_WHOLE_NEW");
		int LOC_ISFIRSTPAR = features.getIndex("LOC_ISFIRSTPAR_NEW");
		int LOC_ISLASTPARA = features.getIndex("LOC_ISLASTPARA_NEW");
		int LOC_ISFIRSTSEN = features.getIndex("LOC_ISFIRSTSEN_NEW");
		int LOC_ISLASTSEN = features.getIndex("LOC_ISLASTSEN_NEW");

		double val_par = 0.0;
		double val_whole = 0.0;

		if (hmu.sD2 != -1) {

			int paragraphNo = hmu.pD2;
			val_par = hmu.sD2 * 1.0
					/ getEssayInfo(essay).pS2Num[paragraphNo - 1];
			val_whole = paragraphNo * 1.0 / getEssayInfo(essay).pD2Num;

			if (paragraphNo == 1) {
				featureVector[LOC_ISFIRSTPAR] = Boolean.toString(true);
			} else {
				featureVector[LOC_ISFIRSTPAR] = Boolean.toString(false);
			}

			int lastParaNo = getEssayInfo(essay).pD2Num;
			if (paragraphNo == lastParaNo) {
				featureVector[LOC_ISLASTPARA] = Boolean.toString(true);
			} else {
				featureVector[LOC_ISLASTPARA] = Boolean.toString(false);
			}

			if (hmu.sD2 == 1) {
				featureVector[LOC_ISFIRSTSEN] = Boolean.toString(true);
			} else {
				featureVector[LOC_ISFIRSTSEN] = Boolean.toString(false);
			}

			if (hmu.sD2 == getEssayInfo(essay).pS2Num[hmu.pD2 - 1]) {
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
		if (hmu.sD1 != -1) {
			int paragraphNo = hmu.pD1;
			val_par_old = hmu.sD1 * 1.0
					/ getEssayInfo(essay).pS1Num[paragraphNo - 1];
			val_whole_old = paragraphNo * 1.0 / getEssayInfo(essay).pD1Num;

			if (paragraphNo == 1) {
				featureVector[LOC_ISFIRSTPAR] = Boolean.toString(true);
			} else {
				featureVector[LOC_ISFIRSTPAR] = Boolean.toString(false);
			}

			if (paragraphNo == getEssayInfo(essay).pD1Num) {
				featureVector[LOC_ISLASTPARA] = Boolean.toString(true);
			} else {
				featureVector[LOC_ISLASTPARA] = Boolean.toString(false);
			}

			if (hmu.sD1 == 1) {
				featureVector[LOC_ISFIRSTSEN] = Boolean.toString(true);
			} else {
				featureVector[LOC_ISFIRSTSEN] = Boolean.toString(false);
			}

			if (hmu.sD1 == getEssayInfo(essay).pS1Num[paragraphNo - 1]) {
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

	public void extractTextGroup(HeatMapUnit hmu,
			ArrayList<ArrayList<HeatMapUnit>> essay) {
		extractClaimKeywords(hmu, essay);
		extractEvidenceKeywords(hmu, essay);
		extractRebuttalKeywords(hmu, essay);
		extractReasoningKeywords(hmu, essay);
		extractCommaFeatures(hmu, essay);
		extractOpFeatures(hmu, essay);
		extractLENFeature(hmu, essay);
		extractDaTextualFeature(hmu, essay);
		// extractNERFeature(doc, ru);
		// extractOverlapFeature(doc, ru);
	}

	public void extractClaimKeywords(HeatMapUnit hmu,
			ArrayList<ArrayList<HeatMapUnit>> essay) {
		String newText = hmu.scD2;
		double isReasoning = 0.0;
		for (String word : claimKeywords) {
			if (newText.contains(word)) {
				isReasoning = 1.0;
				break;
			}
		}
		int featureIndex = features.getIndex(ckN);
		featureVector[featureIndex] = isReasoning;

		String oldText = hmu.scD1;
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

	public void extractEvidenceKeywords(HeatMapUnit hmu,
			ArrayList<ArrayList<HeatMapUnit>> essay) {
		String newText = hmu.scD2;
		double isReasoning = 0.0;
		for (String word : evidenceKeywords) {
			if (newText.contains(word)) {
				isReasoning = 1.0;
				break;
			}
		}
		int featureIndex = features.getIndex(ekN);
		featureVector[featureIndex] = isReasoning;

		String oldText = hmu.scD1;
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

	// extract features
	public void extractRebuttalKeywords(HeatMapUnit hmu,
			ArrayList<ArrayList<HeatMapUnit>> essay) {
		String newText = hmu.scD2;
		double isReasoning = 0.0;
		for (String word : rebutKeywords) {
			if (newText.contains(word)) {
				isReasoning = 1.0;
				break;
			}
		}
		int featureIndex = features.getIndex(rebKN);
		featureVector[featureIndex] = isReasoning;

		String oldText = hmu.scD1;
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

	public void extractReasoningKeywords(HeatMapUnit hmu,
			ArrayList<ArrayList<HeatMapUnit>> essay) {
		String newText = hmu.scD2;
		double isReasoning = 0.0;
		for (String word : reasonKeywords) {
			if (newText.contains(word)) {
				isReasoning = 1.0;
				break;
			}
		}
		int featureIndex = features.getIndex(rkN);
		featureVector[featureIndex] = isReasoning;

		String oldText = hmu.scD1;
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

	public void extractCommaFeatures(HeatMapUnit hmu,
			ArrayList<ArrayList<HeatMapUnit>> essay) {
		double hasCommaOldVal = 0.0;
		double numCommaOldVal = 0.0;
		double hasCommaNewVal = 0.0;
		double numCommaNewVal = 0.0;
		double numCommaDiff = 0.0;

		String oldText = hmu.scD1;
		String newText = hmu.scD2;

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

	public void extractOpFeatures(HeatMapUnit hmu,
			ArrayList<ArrayList<HeatMapUnit>> essay) {
		int featureIndex = features.getIndex("REVISION_OP");
		double val = 0;
		if (hmu.rType.equals("Add")) {
			val = 1;
		} else if (hmu.rType.equals("Delete")) {
			val = 0;
		} else if (hmu.rType.equals("Modify")) {
			val = -1;
		} else {
			val = 2;
		}
		featureVector[featureIndex] = val;
	}

	public void extractLENFeature(HeatMapUnit hmu,
			ArrayList<ArrayList<HeatMapUnit>> essay) {
		String oldSent = hmu.scD1;
		String newSent = hmu.scD2;

		int LEN_SEN = features.getIndex("LEN_SEN_OLD");
		featureVector[LEN_SEN] = oldSent.length() * 1.0;
		int LEN_SEN_NEW = features.getIndex("LEN_SEN_NEW");
		featureVector[LEN_SEN_NEW] = newSent.length() * 1.0;
		int LEN_SEN_DIFF = features.getIndex("LEN_SEN_DIFF");
		featureVector[LEN_SEN_DIFF] = 1.0 * (newSent.length() - oldSent
				.length());
	}

	public void extractDaTextualFeature(HeatMapUnit hmu,
			ArrayList<ArrayList<HeatMapUnit>> essay) {
		/**
		 * I believe putting these together into one function would save a lot
		 * of computation cost
		 * 
		 * But right now I trust the power of the machine
		 */

		String oldSentence = hmu.scD1;
		String newSentence = hmu.scD2;

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

	public Object[] extractFeatures(HeatMapUnit hmu,
			ArrayList<ArrayList<HeatMapUnit>> essay, boolean usingNgram) {
		featureVector = new Object[features.getSize()];
		if (usingNgram) {
			extractTextFeatures(hmu.scD1 + hmu.scD2);
			extractTextFeatures2(hmu.scD1, hmu.scD2);
		}
		extractLocGroup(hmu, essay);
		extractTextGroup(hmu, essay);
		// extractLanguageGroup(doc, ru);
		// extractMetaGroup(doc, ru);
		// extractOtherGroup(doc, ru);
		return featureVector;
	}

	public void extractLanguageGroup(HeatMapUnit hmu,
			ArrayList<ArrayList<HeatMapUnit>> essay) {
		// String sentence = extractSentence(doc, ru);
		// String oldSentence = extractOldSentence(doc, ru);
		// String newSentence = extractNewSentence(doc, ru);
		extractSimplePOSFeatures(hmu, essay);
		extractPOSDiff(hmu, essay);
		// extractGrammarErrorFeature(doc, ru);
		// extractSpellErrorFeature(doc, ru);
	}

	public void extractPOSDiff(HeatMapUnit hmu,
			ArrayList<ArrayList<HeatMapUnit>> essay) {
		String oldSentence = hmu.scD1;
		String newSentence = hmu.scD2;
		if (oldSentence == null || oldSentence.length() == 0)
			oldSentence = "Dummy";
		if (newSentence == null || newSentence.length() == 0)
			newSentence = "Dummy";
		Hashtable<String, Double> posTable = StanfordParserAssist.getInstance()
				.collectSimplePOSDiff(
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
	}

	public void extractSimplePOSFeatures(HeatMapUnit hmu,
			ArrayList<ArrayList<HeatMapUnit>> essay) {
		String oldText = hmu.scD1;
		String newText = hmu.scD2;
		// if(oldText==null||oldText.length()==0) oldText = "Dummy";
		// if(newText==null||newText.length()==0) newText = "Dummy";
		Hashtable<String, Double> posTable = StanfordParserAssist.getInstance()
				.collectSimplePOSRatio(
						StanfordParserAssist.getInstance()
								.annotateSingleSentence(oldText));
		for (String pos : posTable.keySet()) {
			double ratio = posTable.get(pos);
			int index = features.getIndex(pos + "_OLD");
			featureVector[index] = ratio;
		}

		posTable = StanfordParserAssist.getInstance().collectSimplePOSRatio(
				StanfordParserAssist.getInstance().annotateSingleSentence(
						newText));
		for (String pos : posTable.keySet()) {
			double ratio = posTable.get(pos);
			int index = features.getIndex(pos + "_NEW");
			featureVector[index] = ratio;

		}
	}

	public Object[] extractFeatures(HeatMapUnit hmu,
			ArrayList<ArrayList<HeatMapUnit>> essay, RevisionDocument doc,
			boolean usingNgram) throws IOException {
		featureVector = new Object[features.getSize()];
		if (usingNgram) {
			extractTextFeatures(hmu.scD1 + hmu.scD2);
			extractTextFeatures2(hmu.scD1, hmu.scD2);
		}
		extractLocGroup(hmu, essay);
		extractTextGroup(hmu, essay);
		// extractLanguageGroup(hmu, essay);
		// extractMetaGroup(doc, ru);
		// extractOtherGroup(doc, ru);
		ArrayList<Integer> newIndices = new ArrayList<Integer>();
		ArrayList<Integer> oldIndices = new ArrayList<Integer>();
		if (hmu.realNewIndex != -1)
			newIndices.add(hmu.realNewIndex);
		if (hmu.realOldIndex != -1)
			oldIndices.add(hmu.realOldIndex);
		/*
		 * SentenceEmbeddingFeatureExtractor.getInstance().extractFeature(
		 * features, featureVector, doc, newIndices, oldIndices);
		 */
		if (!isOnline) {
			// SentenceEmbeddingFeatureExtractor.getInstance().extractCohesion(
			// features, featureVector, doc, newIndices, oldIndices);
			PDTBFeatureExtractor.getInstance().extractFeature(features,
					featureVector, doc, newIndices, oldIndices);
			PDTBFeatureExtractor.getInstance().extractFeatureARG1ARG2(features,
					featureVector, doc, newIndices, oldIndices);
		}
		return featureVector;
	}

	public Object[] extractFeatures(HeatMapUnit hmu,
			ArrayList<ArrayList<HeatMapUnit>> essay, RevisionDocument doc,
			boolean usingNgram, int remove) throws Exception {
		featureVector = new Object[features.getSize()];
		if (usingNgram) {
			extractTextFeatures(hmu.scD1 + hmu.scD2);
			extractTextFeatures2(hmu.scD1, hmu.scD2);
		}
		if (remove == -1)
			return featureVector;
		if (remove == 10 || remove == 11 || remove == 3 || remove == 2
				|| remove == 5 || remove == 6) {
			extractLocGroup(hmu, essay);
			extractTextGroup(hmu, essay);
		}
		// extractLanguageGroup(doc, ru);
		// extractMetaGroup(doc, ru);
		// extractOtherGroup(doc, ru);
		ArrayList<Integer> newIndices = new ArrayList<Integer>();
		ArrayList<Integer> oldIndices = new ArrayList<Integer>();
		if (hmu.realNewIndex != -1)
			newIndices.add(hmu.realNewIndex);
		if (hmu.realOldIndex != -1)
			oldIndices.add(hmu.realOldIndex);
		if (remove == 2 || remove == 10 || remove == 22 || remove == 6) {
			// extractLanguageGroup(doc, ru);
			PDTBFeatureExtractorV2.getInstance().extractFeature(features,
					featureVector, doc, newIndices, oldIndices);
			/*
			 * PDTBFeatureExtractorV2.getInstance().extractFeatureARG1ARG2(features
			 * , featureVector, doc, newIndices, oldIndices);
			 */

		}
		if (remove == 6 || remove == 10) {
			// PDTBFeatureExtractorV2.getInstance().extractPDTBEntityGridFeature(
			// features, featureVector, doc, newIndices, oldIndices);
			PDTBFeatureExtractorV2.getInstance().extractWeightedFeature(
					features, featureVector, doc, newIndices, oldIndices);
		}
		if (remove == 3 || remove == 10)
			// extractMetaGroup(doc, ru);
			// SentenceEmbeddingFeatureExtractor.getInstance().extractFeature(
			// features, featureVector, doc, ru.getNewSentenceIndex(),
			// ru.getOldSentenceIndex());
			SentenceEmbeddingFeatureExtractor.getInstance().extractCohesion(
					features, featureVector, doc, newIndices, oldIndices);

		if (remove == 4 || remove == 10 || remove == 2) {
			// extractFeaturesPriorPost(hmu, essay, doc, 1);
		}
		if (remove == 5 || remove == 10) {
			// extractLanguageGroup(doc, ru);
			// PDTBFeatureExtractor.getInstance().extractFeature(features,
			// featureVector, doc, newIndices, oldIndices);
			// PDTBFeatureExtractor.getInstance().extractFeatureARG1ARG2(features,
			// featureVector, doc, newIndices,
			// oldIndices);
			ArgumentZoningFeatureExtractor.getInstance().extractFeature(
					features, featureVector, doc, newIndices, oldIndices);
		}
		return featureVector;
	}

	public ArrayList<Object[]> extractFeatures(
			ArrayList<ArrayList<HeatMapUnit>> essay, boolean usingNgram) {
		essayInfo = new EssayInfo();
		essayInfo.parsed = false;
		ArrayList<Object[]> features = new ArrayList<Object[]>();
		for (ArrayList<HeatMapUnit> paragraph : essay) {
			for (HeatMapUnit hmu : paragraph) {
				features.add(extractFeatures(hmu, essay, usingNgram));
			}
		}
		return features;
	}

	public ArrayList<Object[]> extractFeatures(
			ArrayList<ArrayList<HeatMapUnit>> essay, RevisionDocument doc,
			boolean usingNgram) throws IOException {
		essayInfo = new EssayInfo();
		essayInfo.parsed = false;
		ArrayList<Object[]> features = new ArrayList<Object[]>();
		for (ArrayList<HeatMapUnit> paragraph : essay) {
			for (HeatMapUnit hmu : paragraph) {
				features.add(extractFeatures(hmu, essay, doc, usingNgram));
			}
		}
		return features;
	}

	public ArrayList<Object[]> extractFeatures(
			ArrayList<ArrayList<HeatMapUnit>> essay, RevisionDocument doc,
			boolean usingNgram, int remove) throws Exception {
		essayInfo = new EssayInfo();
		essayInfo.parsed = false;
		ArrayList<Object[]> features = new ArrayList<Object[]>();
		for (ArrayList<HeatMapUnit> paragraph : essay) {
			for (HeatMapUnit hmu : paragraph) {
				features.add(extractFeatures(hmu, essay, doc, usingNgram,
						remove));
			}
		}
		return features;
	}

	public void extractTextGroupPriorPost(HeatMapUnit hmu,
			ArrayList<ArrayList<HeatMapUnit>> essay, RevisionDocument doc,
			int windowSize) {
		// extractOpFeaturesPriorPost(windowSize, doc, newIndexes, oldIndexes);
		extractLENFeaturePriorPost(hmu, essay, doc, windowSize);
		extractDaTextualFeaturePriorPost(hmu, essay, doc, windowSize);
		// extractNERFeature(doc, ru);
		// extractOverlapFeature(doc, ru);
	}

	public void extractFeaturesPriorPost(HeatMapUnit hmu,
			ArrayList<ArrayList<HeatMapUnit>> essay, RevisionDocument doc,
			int windowSize) {
		extractLocFeaturePriorPost(hmu, essay, doc, windowSize);
		extractTextGroupPriorPost(hmu, essay, doc, windowSize);
	}

	public void extractLocFeaturePriorPost(HeatMapUnit hmu,
			ArrayList<ArrayList<HeatMapUnit>> essay, RevisionDocument doc,
			int windowSize) {
		// ru.getNewParagraphNo();
		ArrayList<Integer> sentenceIndices = new ArrayList<Integer>();
		ArrayList<Integer> oldSentIndices = new ArrayList<Integer>();
		sentenceIndices.add(hmu.newIndex);
		oldSentIndices.add(hmu.oldIndex);
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

	public void extractLENFeaturePriorPost(HeatMapUnit hmu,
			ArrayList<ArrayList<HeatMapUnit>> essay, RevisionDocument doc,
			int windowSize) {
		ArrayList<Integer> newIndexes = new ArrayList<Integer>();
		ArrayList<Integer> oldIndexes = new ArrayList<Integer>();
		newIndexes.add(hmu.newIndex);
		oldIndexes.add(hmu.oldIndex);
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

	public void extractDaTextualFeaturePriorPost(HeatMapUnit hmu,
			ArrayList<ArrayList<HeatMapUnit>> essay, RevisionDocument doc,
			int windowSize) {
		// Notice the cosine here is not normalized
		String tag = "_PRIOR_";
		String tag2 = "_POST_";
		ArrayList<Integer> newIndexes = new ArrayList<Integer>();
		ArrayList<Integer> oldIndexes = new ArrayList<Integer>();
		newIndexes.add(hmu.newIndex);
		oldIndexes.add(hmu.oldIndex);
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

}
