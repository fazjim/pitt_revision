package edu.pitt.lrdc.cs.revision.evaluate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

import edu.pitt.lrdc.cs.revision.model.RevisionDocument;
import edu.pitt.lrdc.cs.revision.model.RevisionOp;
import edu.pitt.lrdc.cs.revision.model.RevisionPurpose;
import edu.pitt.lrdc.cs.revision.model.RevisionUnit;

class Eval {
	double precision;
	double recall;
	double f;
	double unweightedP;
	double unweightedR;
	double unweightedF;
	double accuracy;
}

public class PurposeEvaluator {
	static boolean printVerbose = false;

	/**
	 * Of all the revisions, how many of the revisions has been identified
	 * 
	 * precision: how many of the predicts are correct recall: how many of the
	 * gold standard are extracted
	 * 
	 * @return Eval class (precision, recall)
	 */
	public static Eval evaluatePurposeBinary(RevisionDocument doc) {
		ArrayList<RevisionUnit> basicUnits = doc.getRoot()
				.getRevisionUnitAtLevel(0);
		ArrayList<RevisionUnit> predictedUnits = doc.getPredictedRoot()
				.getRevisionUnitAtLevel(0);

		HashSet<String> baseSet = transformUnit2Str(basicUnits, true);
		HashSet<String> predictedSet = transformUnit2Str(predictedUnits, true);

		int predicted_total = predictedSet.size();
		int gold_total = baseSet.size();
		int corr_Predict = 0;
		for (String str : predictedSet) {
			if (baseSet.contains(str))
				corr_Predict++;
		}
		double prec = corr_Predict * 1.0 / predicted_total;
		double recall = corr_Predict * 1.0 / gold_total;

		Eval eval = new Eval();
		eval.precision = prec;
		eval.recall = recall;
		return eval;
	}

	/**
	 * Get the average performance
	 * 
	 * @param docs
	 * @return
	 */
	public static Eval evaluatePurposeBinaryAvg(ArrayList<RevisionDocument> docs) {
		Eval eval = new Eval();
		for (RevisionDocument doc : docs) {
			Eval temp = evaluatePurposeBinary(doc);
			eval.precision += temp.precision;
			eval.recall += temp.recall;
		}
		eval.precision = eval.precision / docs.size();
		eval.recall = eval.recall / docs.size();
		return eval;
	}

	/**
	 * Get the total performance evaluation
	 * 
	 * @param docs
	 * @return
	 */
	public static Eval evaluatePurposeBinaryTotal(
			ArrayList<RevisionDocument> docs) {
		int predicted_total = 0;
		int gold_total = 0;
		int corr_Predict = 0;
		
		int predicted_total0 = 0;
		int gold_total0 = 0;
		int corr_Predict0 = 0;
		
		int predicted_total1 = 0;
		int gold_total1 = 0;
		int corr_Predict1 = 0;
		
		
		for (RevisionDocument doc : docs) {
			ArrayList<RevisionUnit> basicUnits = doc.getRoot()
					.getRevisionUnitAtLevel(0);
			ArrayList<RevisionUnit> predictedUnits = doc.getPredictedRoot()
					.getUnits();
			if (printVerbose)
				System.out.println("=================BASE===============");
			HashSet<String> baseSet = transformUnit2Str(basicUnits, true);
			if (printVerbose)
				System.out.println("=================PREDICTED===============");
			HashSet<String> predictedSet = transformUnit2Str(predictedUnits,
					true);

			System.out.println("BASE:" + baseSet.size());
			System.out.println("PREDICTED:" + predictedSet.size());
			predicted_total += predictedSet.size();
			
			for(String str: baseSet) {
				if(str.startsWith("SURFACE")) gold_total0 += 1;
				else if(str.startsWith("CONTENT")) gold_total1+=1;
			}
			
			gold_total += baseSet.size();
			for (String str : predictedSet) {
				System.out.println(str);
				if(str.startsWith("SURFACE")) predicted_total0+=1;
				else if(str.startsWith("CONTENT")) predicted_total1+=1;
				
				if (baseSet.contains(str)) {
					corr_Predict++;
					if(str.startsWith("SURFACE")) corr_Predict0 += 1;
					else if (str.startsWith("CONTENT")) corr_Predict1+=1;
				}
			}
		}

		double prec = corr_Predict * 1.0 / predicted_total;
		//if(predicted_total == 0 && gold_total != 0) prec = 0;
		//if(predicted_total == 0 && gold_total == 0) prec = 1;
		//if(predicted_total == 0 && gold_total == 0) prec = 1;
		if(predicted_total == 0) prec = 0; //Following the weka calculation
		
		double prec0 = corr_Predict0 * 1.0/predicted_total0;
		//if(predicted_total0 == 0 && gold_total0 !=0) prec0 = 0;
		//if(predicted_total0 == 0 && gold_total0 == 0) prec0 = 1;
		//if(predicted_total0 == 0 && gold_total0 == 0) prec0 = 1;
		if(predicted_total0 == 0) prec0 = 0;
		
		double prec1 = corr_Predict1 * 1.0/predicted_total1;
		//if(predicted_total1 == 0 && gold_total1 != 0) prec1 = 0;
		//if(predicted_total1 == 0 && gold_total1 == 0) prec1 = 1;
		//if(predicted_total1 == 0 && gold_total1 == 0) prec1 = 1;
		if(predicted_total1 == 0) prec1 = 0;
		
		double recall = corr_Predict * 1.0 / gold_total;

		
		double recall0 = corr_Predict0 * 1.0 / gold_total0;
		//if(corr_Predict1 == corr_Predict && gold_total0 == 0) recall0 = 1;
		//if(corr_Predict1!=corr_Predict && gold_total0 == 0) recall0 = 0;
		if(gold_total0 == 0) recall0 = 0;
		
		double recall1 = corr_Predict1 * 1.0 / gold_total1;
		//if(corr_Predict0 == corr_Predict && gold_total1 == 0) recall1 = 1;
		//if(corr_Predict0 != corr_Predict && gold_total1 == 0) recall1 = 0;
		if(gold_total1 == 0) recall1 = 0;
		
		Eval eval = new Eval();
		eval.precision = prec;
		eval.recall = recall;
		
		System.out.println("PREC 0:"+prec0);
		System.out.println("PREC 1:"+prec1);
		eval.unweightedP = (prec0+prec1)/2;
		eval.unweightedR = (recall0+recall1)/2;
		return eval;
	}

	/**
	 * Transform the list of revision unit to a list of string for the easy of
	 * comparision
	 * 
	 * @param rus
	 * @return
	 */
	public static HashSet<String> transformUnit2Str(
			ArrayList<RevisionUnit> rus, boolean isBinary) {
		HashSet<String> unitStrs = new HashSet<String>();
		for (RevisionUnit ru : rus) {
			if (ru.getRevision_op() == RevisionOp.MODIFY) {
				String purpose = getRevisionPurposeStr(
						ru.getRevision_purpose(), isBinary);
				ArrayList<Integer> newIndices = ru.getNewSentenceIndex();
				ArrayList<Integer> oldIndices = ru.getOldSentenceIndex();
				String val = purpose + "NEW:" + getArrStr(newIndices) + "OLD:"
						+ getArrStr(oldIndices);
				if (printVerbose)
					System.out.println(val);
				unitStrs.add(val);
			}
		}
		return unitStrs;
	}

	public static String getArrStr(ArrayList<Integer> indices) {
		String arrStr = "";
		if (indices == null || indices.size() == 0
				|| (indices.size() == 1 && indices.get(0) == -1)) {
			arrStr = "EMPTY";
		} else {
			Collections.sort(indices);// make sure the order is the same
			for (Integer index : indices) {
				arrStr += "|" + index;
			}
		}
		return arrStr;
	}

	public static String getRevisionPurposeStr(int revPurpose, boolean isBinary) {
		if (isBinary) {
			if (revPurpose == RevisionPurpose.CONVENTIONS_GRAMMAR_SPELLING
					|| revPurpose == RevisionPurpose.ORGANIZATION
					|| revPurpose == RevisionPurpose.STYLE
					|| revPurpose == RevisionPurpose.WORDUSAGE_CLARITY) {
				return "SURFACE";
			} else {
				return "CONTENT";
			}
		} else {
			return RevisionPurpose.getPurposeName(revPurpose);
		}
	}
}
