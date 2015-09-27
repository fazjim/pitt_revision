package edu.pitt.cs.revision.purpose;

import java.util.ArrayList;
import java.util.HashSet;

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

public class CRFFeatureExtractor extends FeatureExtractor{

	private EssayInfo essayInfo = new EssayInfo();
	
	public EssayInfo getEssayInfo(ArrayList<ArrayList<HeatMapUnit>> essay) {
		if(essayInfo.parsed==false) {
			int pD1Num = 1;
			int pD2Num = 1;
			int tempS1Num = 1;
			int tempS2Num = 1;
			ArrayList<Integer> sentences1 = new ArrayList<Integer>();
			ArrayList<Integer> sentences2 = new ArrayList<Integer>();
			for(ArrayList<HeatMapUnit> paragraph: essay) {
				for(HeatMapUnit unit: paragraph) {
					if(unit.pD1>pD1Num) {
						sentences1.add(tempS1Num);
						pD1Num = unit.pD1;
						tempS1Num = unit.sD1;
					} else {
						if(unit.sD1>tempS1Num) tempS1Num = unit.sD1;
					}
					
					if(unit.pD2>pD2Num) {
						sentences2.add(tempS2Num);
						pD2Num = unit.pD2;
						tempS2Num = unit.sD2;
					} else {
						if(unit.sD2>tempS2Num) tempS2Num = unit.sD2;
					}
				}
			}
			sentences1.add(tempS1Num);
			sentences2.add(tempS2Num);
			
			essayInfo.pD1Num = pD1Num;
			essayInfo.pD2Num = pD2Num;
			
			int[] pS1Num = new int[pD1Num];
			int[] pS2Num = new int[pD2Num];
			for(int i = 0;i<sentences1.size();i++) {
				pS1Num[i] = sentences1.get(i);
			}
			for(int i = 0;i<sentences2.size();i++) {
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

	//Extract diff
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
	

	
	public void extractLocGroup(HeatMapUnit hmu, ArrayList<ArrayList<HeatMapUnit>> essay) {
		extractLocFeature(hmu, essay);
	}
	
	public void extractLocFeature(HeatMapUnit hmu, ArrayList<ArrayList<HeatMapUnit>> essay) {
		// ru.getNewParagraphNo();
		int LOC_PAR = features.getIndex("LOC_PAR_NEW");
		int LOC_WHOLE = features.getIndex("LOC_WHOLE_NEW");
		int LOC_ISFIRSTPAR = features.getIndex("LOC_ISFIRSTPAR_NEW");
		int LOC_ISLASTPARA = features.getIndex("LOC_ISLASTPARA_NEW");
		int LOC_ISFIRSTSEN = features.getIndex("LOC_ISFIRSTSEN_NEW");
		int LOC_ISLASTSEN = features.getIndex("LOC_ISLASTSEN_NEW");

		double val_par = 0.0;
		double val_whole = 0.0;
		

		if (hmu.sD2!=-1) {
			
				int paragraphNo = hmu.pD2;
				val_par = hmu.sD2 * 1.0/ getEssayInfo(essay).pS2Num[paragraphNo-1];
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

				if (hmu.sD2 == getEssayInfo(essay).pS2Num[hmu.pD2-1]) {
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
			val_par_old = hmu.sD1 * 1.0/  getEssayInfo(essay).pS1Num[paragraphNo-1];
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

			if (hmu.sD1 == getEssayInfo(essay).pS1Num[paragraphNo-1]) {
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
	
	public void extractTextGroup(HeatMapUnit hmu, ArrayList<ArrayList<HeatMapUnit>> essay) {
		extractClaimKeywords(hmu, essay);
		extractEvidenceKeywords(hmu, essay);
		extractRebuttalKeywords(hmu, essay);
		extractReasoningKeywords(hmu, essay);
		extractCommaFeatures(hmu, essay);
		extractOpFeatures(hmu, essay);
		extractLENFeature(hmu, essay);
		extractDaTextualFeature(hmu, essay);
		//extractNERFeature(doc, ru);
		// extractOverlapFeature(doc, ru);
	}
	
	public void extractClaimKeywords(HeatMapUnit hmu, ArrayList<ArrayList<HeatMapUnit>> essay) {
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
	
	public void extractEvidenceKeywords(HeatMapUnit hmu, ArrayList<ArrayList<HeatMapUnit>> essay) {
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
	public void extractRebuttalKeywords(HeatMapUnit hmu, ArrayList<ArrayList<HeatMapUnit>> essay) {
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

	public void extractReasoningKeywords(HeatMapUnit hmu, ArrayList<ArrayList<HeatMapUnit>> essay) {
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

	public void extractCommaFeatures(HeatMapUnit hmu, ArrayList<ArrayList<HeatMapUnit>> essay) {
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

	public void extractOpFeatures(HeatMapUnit hmu, ArrayList<ArrayList<HeatMapUnit>> essay) {
		int featureIndex = features.getIndex("REVISION_OP");
		double val = 0;
		if (hmu.rType.equals("Add")) {
			val = 1;
		} else if (hmu.rType.equals("Delete")) {
			val = 0;
		} else if(hmu.rType.equals("Modify")) {
			val = -1;
		}
		else {
			val = 2;
		}
		featureVector[featureIndex] = val;
	}

	public void extractLENFeature(HeatMapUnit hmu, ArrayList<ArrayList<HeatMapUnit>> essay) {
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

	public void extractDaTextualFeature(HeatMapUnit hmu, ArrayList<ArrayList<HeatMapUnit>> essay) {
		/**
		 * I believe putting these together into one function would save a lot
		 * of computation cost
		 * 
		 * But right now I trust the power of the machine
		 */
		
		String oldSentence = hmu.scD1;
		String newSentence = hmu.scD2;
		
		int DIFF_CHANGED = features.getIndex("DIFF_CHANGED");
		if(oldSentence.equals(newSentence)) {
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

	
	public Object[] extractFeatures(HeatMapUnit hmu, ArrayList<ArrayList<HeatMapUnit>> essay,
			boolean usingNgram) {
		featureVector = new Object[features.getSize()];
		if (usingNgram) {
			extractTextFeatures(hmu.scD1 + hmu.scD2);
			extractTextFeatures2(hmu.scD1, hmu.scD2);
		}
		extractLocGroup(hmu, essay);
		extractTextGroup(hmu, essay);
		//extractLanguageGroup(doc, ru);
		// extractMetaGroup(doc, ru);
		// extractOtherGroup(doc, ru);
		return featureVector;
	}

	public ArrayList<Object[]> extractFeatures(ArrayList<ArrayList<HeatMapUnit>> essay,
			boolean usingNgram) {
		essayInfo = new EssayInfo();
		essayInfo.parsed = false;
		ArrayList<Object[]> features = new ArrayList<Object[]>();
		for(ArrayList<HeatMapUnit> paragraph: essay) {
			for(HeatMapUnit hmu: paragraph) {
				features.add(extractFeatures(hmu,essay,usingNgram));
			}
		}
		return features;
	}
}
