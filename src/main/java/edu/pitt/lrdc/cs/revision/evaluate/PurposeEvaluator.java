package edu.pitt.lrdc.cs.revision.evaluate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;

import edu.pitt.lrdc.cs.revision.model.RevisionDocument;
import edu.pitt.lrdc.cs.revision.model.RevisionOp;
import edu.pitt.lrdc.cs.revision.model.RevisionPurpose;
import edu.pitt.lrdc.cs.revision.model.RevisionUnit;
import edu.pitt.lrdc.cs.revision.statistics.ApacheStatAssist;

class Eval {
	double precision;
	double recall;
	double f;
	double unweightedP;
	double unweightedR;
	double unweightedF;
	double accuracy;
}

class CorrEval {
	ArrayList<Integer> predictedNums = new ArrayList<Integer>();
	ArrayList<Integer> realNums = new ArrayList<Integer>();

	public int getPredictNum() {
		return predictedNums.size();
	}

	public int getRealNum() {
		return realNums.size();
	}

	public void addPredict(int num) {
		predictedNums.add(num);
	}

	public void addReal(int num) {
		realNums.add(num);
	}

	public int getPredict(int index) {
		return predictedNums.get(index);
	}

	public int getReal(int index) {
		return realNums.get(index);
	}
}

public class PurposeEvaluator {
	static boolean printVerbose = false;

	public static String printCorr(double[] vals) {
		return "Correlation: "+vals[0]+", p-value:"+vals[1];
	}
	public static double[] getCorr(CorrEval ce) {
		if (printVerbose) {
			System.out.println("===========PREDICTED=============");
			for (int i = 0; i < ce.getPredictNum(); i++) {
				System.out.println(ce.getPredict(i) + " ");
			}
			System.out.println("===========REAL=============");
			for (int i = 0; i < ce.getRealNum(); i++) {
				System.out.println(ce.getReal(i) + " ");
			}
		}

		double[] predicts = new double[ce.getPredictNum()];
		double[] reals = new double[ce.getRealNum()];
		for (int i = 0; i < ce.getPredictNum(); i++) {
			predicts[i] = ce.getPredict(i) * 1.0;
		}
		for (int i = 0; i < ce.getRealNum(); i++) {
			reals[i] = ce.getReal(i) * 1.0;
		}
		
		try {
			return ApacheStatAssist.pearsonCorrelationP(predicts, reals);
		} catch (Exception exp) {
			return null;
		}
	}

	public static void getCorrMatrixParagraph(ArrayList<RevisionDocument> docs,
			CorrEval eval, int revType) {
		for (RevisionDocument doc : docs) {
			ArrayList<RevisionUnit> predictedUnits = doc.getPredictedRoot()
					.getRevisionUnitAtLevel(0);
			ArrayList<RevisionUnit> realUnits = doc.getRoot()
					.getRevisionUnitAtLevel(0);
			Hashtable<String, Integer> predictedCounts = getNumofRevisionsPara(
					revType, predictedUnits, doc);
			Hashtable<String, Integer> realCounts = getNumofRevisionsPara(
					revType, realUnits, doc);

			Iterator<String> it = predictedCounts.keySet().iterator();
			while (it.hasNext()) {
				String para = it.next();
				int predictedCount = predictedCounts.get(para);
				int realCount = 0;
				if (realCounts.containsKey(para))
					realCount = realCounts.get(para);
				eval.addPredict(predictedCount);
				eval.addReal(realCount);
			}
			it = realCounts.keySet().iterator();
			while (it.hasNext()) {
				String para = it.next();
				int realCount = realCounts.get(para);
				if (!predictedCounts.containsKey(para)) {
					eval.addPredict(0);
					eval.addReal(realCount);
				}
			}
		}
	}

	public static void getCorrMatrixEssay(ArrayList<RevisionDocument> docs,
			CorrEval eval, int revType) {
		for (RevisionDocument doc : docs) {
			ArrayList<RevisionUnit> predictedUnits = doc.getPredictedRoot()
					.getRevisionUnitAtLevel(0);
			ArrayList<RevisionUnit> realUnits = doc.getRoot()
					.getRevisionUnitAtLevel(0);
			eval.addPredict(getNumofRevisions(revType, predictedUnits));
			eval.addReal(getNumofRevisions(revType, realUnits));
		}
	}

	public static Hashtable<String, Integer> getNumofRevisionsPara(int revType,
			ArrayList<RevisionUnit> revisions, RevisionDocument doc) {
		Hashtable<String, Integer> paraRevs = new Hashtable<String, Integer>();
		// First we need to find out the modified old paragraphs and new added
		// paragraphs
		Hashtable<Integer, HashSet<Integer>> newParaMapping = new Hashtable<Integer, HashSet<Integer>>();
		int oldDraftSentNum = doc.getOldDraftSentences().size();
		for (int i = 0; i < oldDraftSentNum; i++) {
			int index = i + 1;
			int oldParaIndex = doc.getParaNoOfOldSentence(index);
			String paraTag = "OLD:" + oldParaIndex;
			if (!paraRevs.containsKey(paraTag)) {
				paraRevs.put(paraTag, 0);
			}
		}

		int newDraftSentNum = doc.getNewDraftSentences().size();
		for (int i = 0; i < newDraftSentNum; i++) {
			int index = i + 1;
			int newParaIndex = doc.getParaNoOfNewSentence(index);
			if (!newParaMapping.containsKey(newParaIndex))
				newParaMapping.put(newParaIndex, new HashSet<Integer>());
			ArrayList<Integer> oldMappings = doc.getOldFromNew(index);
			if (oldMappings!=null && oldMappings.size() != 0) {
				for(Integer oldIndex: oldMappings) {
					int oldParaIndex = doc.getParaNoOfOldSentence(oldIndex);
					newParaMapping.get(newParaIndex).add(oldParaIndex);
				}
			}
		}
		
		Iterator<Integer> it = newParaMapping.keySet().iterator();
		while(it.hasNext()) {
			int newParaIndex = it.next();
			if(newParaMapping.get(newParaIndex).isEmpty()) {
				String paraTag = "NEW:"+newParaIndex;
				paraRevs.put(paraTag, 0);
			}
		}

		for (RevisionUnit rev : revisions) {
			String tag = "";
			ArrayList<Integer> oldIndices = rev.getOldSentenceIndex();
			if(oldIndices==null||oldIndices.size()==0||(oldIndices.size()==1&&oldIndices.get(0)==-1)) {
				ArrayList<Integer> newIndices = rev.getNewSentenceIndex();
				HashSet<Integer> mappedOlds = new HashSet<Integer>();
				int paraIndex = -1;
				for(Integer newIndex: newIndices) {
					if(newIndex!=-1) {
						int newParaIndex = doc.getParaNoOfNewSentence(newIndex);
						if(newParaIndex > paraIndex) paraIndex = newParaIndex;
						HashSet<Integer> olds = newParaMapping.get(newParaIndex);
						for(Integer oldP: olds) {
							mappedOlds.add(oldP);
						}
					}
				}
				if(mappedOlds.size()==0) {//A new paragraph
					 tag = "NEW:"+paraIndex;
				} else {
					paraIndex = -1;
					for(Integer oldP: mappedOlds) {
						if(oldP > paraIndex) paraIndex = oldP;
					}
					tag = "OLD:"+paraIndex;
				}
			} else {
				int paraIndex = -1;
				for(Integer oldIndex: oldIndices) {
					if(oldIndex!=-1) {
						int oldParaIndex = doc.getParaNoOfOldSentence(oldIndex);
						if(oldParaIndex > paraIndex) paraIndex = oldParaIndex;
					}
				}
				tag = "OLD:"+paraIndex;
			}
			
			int count = 0;
			if (paraRevs.containsKey(tag))
				count = paraRevs.get(tag);
			if (revType == -1) {
				count++;
				paraRevs.put(tag, count);
			} else if (revType == -2) {
				// Content only
				if (rev.getRevision_purpose() != RevisionPurpose.WORDUSAGE_CLARITY
						&& rev.getRevision_purpose() != RevisionPurpose.CONVENTIONS_GRAMMAR_SPELLING) {
					count++;
					paraRevs.put(tag, count);
				}
			} else {
				if (rev.getRevision_purpose() == revType) {
					count++;
					paraRevs.put(tag, count);
				}
			}
		}
		return paraRevs;

	}

	public static int getNumofRevisions(int revType,
			ArrayList<RevisionUnit> revisions) {
		int count = 0;
		for (RevisionUnit rev : revisions) {
			if (revType == -1) {
				count++;
			} else if (revType == -2) {
				// Content only
				if (rev.getRevision_purpose() != RevisionPurpose.WORDUSAGE_CLARITY
						&& rev.getRevision_purpose() != RevisionPurpose.CONVENTIONS_GRAMMAR_SPELLING) {
					count++;
				}
			} else {
				if (rev.getRevision_purpose() == revType) {
					count++;
				}
			}
		}
		return count++;
	}

	public static CorrEval getCorrMatrix(ArrayList<RevisionDocument> docs,
			boolean isParagraphLevel, int revType) {
		CorrEval ce = new CorrEval();
		if (isParagraphLevel)
			getCorrMatrixParagraph(docs, ce, revType);
		else
			getCorrMatrixEssay(docs, ce, revType);
		return ce;
	}

	public static void evaluatePurposeCorrelation(
			ArrayList<RevisionDocument> docs) {
		int type = -1;
		System.out.println("Correlation of all revisions");
		System.out.println("Essay-level:"
				+ printCorr(getCorr(getCorrMatrix(docs, false, type))));
		System.out.println("Paragraph-level:"
				+ printCorr(getCorr(getCorrMatrix(docs, true, type))));
		type = -2;
		System.out.println("Correlation of TEXT revisions");
		System.out.println("Essay-level:"
				+ printCorr(getCorr(getCorrMatrix(docs, false, type))));
		System.out.println("Paragraph-level:"
				+ printCorr(getCorr(getCorrMatrix(docs, true, type))));
		for (int i = RevisionPurpose.START; i <= RevisionPurpose.END; i++) {
			type = i;
			System.out.println("Correlation of revisions:"
					+ RevisionPurpose.getPurposeName(type));
			System.out.println("Essay-level:"
					+ printCorr(getCorr(getCorrMatrix(docs, false, type))));
			System.out.println("Paragraph-level:"
					+ printCorr(getCorr(getCorrMatrix(docs, true, type))));
		}
	}

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

			for (String str : baseSet) {
				if (str.startsWith("SURFACE"))
					gold_total0 += 1;
				else if (str.startsWith("CONTENT"))
					gold_total1 += 1;
			}

			gold_total += baseSet.size();
			for (String str : predictedSet) {
				System.out.println(str);
				if (str.startsWith("SURFACE"))
					predicted_total0 += 1;
				else if (str.startsWith("CONTENT"))
					predicted_total1 += 1;

				if (baseSet.contains(str)) {
					corr_Predict++;
					if (str.startsWith("SURFACE"))
						corr_Predict0 += 1;
					else if (str.startsWith("CONTENT"))
						corr_Predict1 += 1;
				}
			}
		}

		double prec = corr_Predict * 1.0 / predicted_total;
		// if(predicted_total == 0 && gold_total != 0) prec = 0;
		// if(predicted_total == 0 && gold_total == 0) prec = 1;
		// if(predicted_total == 0 && gold_total == 0) prec = 1;
		if (predicted_total == 0)
			prec = 0; // Following the weka calculation

		double prec0 = corr_Predict0 * 1.0 / predicted_total0;
		// if(predicted_total0 == 0 && gold_total0 !=0) prec0 = 0;
		// if(predicted_total0 == 0 && gold_total0 == 0) prec0 = 1;
		// if(predicted_total0 == 0 && gold_total0 == 0) prec0 = 1;
		if (predicted_total0 == 0)
			prec0 = 0;

		double prec1 = corr_Predict1 * 1.0 / predicted_total1;
		// if(predicted_total1 == 0 && gold_total1 != 0) prec1 = 0;
		// if(predicted_total1 == 0 && gold_total1 == 0) prec1 = 1;
		// if(predicted_total1 == 0 && gold_total1 == 0) prec1 = 1;
		if (predicted_total1 == 0)
			prec1 = 0;

		double recall = corr_Predict * 1.0 / gold_total;

		double recall0 = corr_Predict0 * 1.0 / gold_total0;
		// if(corr_Predict1 == corr_Predict && gold_total0 == 0) recall0 = 1;
		// if(corr_Predict1!=corr_Predict && gold_total0 == 0) recall0 = 0;
		if (gold_total0 == 0)
			recall0 = 0;

		double recall1 = corr_Predict1 * 1.0 / gold_total1;
		// if(corr_Predict0 == corr_Predict && gold_total1 == 0) recall1 = 1;
		// if(corr_Predict0 != corr_Predict && gold_total1 == 0) recall1 = 0;
		if (gold_total1 == 0)
			recall1 = 0;

		Eval eval = new Eval();
		eval.precision = prec;
		eval.recall = recall;

		System.out.println("PREC 0:" + prec0);
		System.out.println("PREC 1:" + prec1);
		eval.unweightedP = (prec0 + prec1) / 2;
		eval.unweightedR = (recall0 + recall1) / 2;
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
