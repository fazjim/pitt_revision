package edu.pitt.lrdc.cs.revision.evaluate;

import java.util.ArrayList;
import java.util.Collections;

import edu.pitt.lrdc.cs.revision.model.RevisionDocument;

/**
 * Provides tool utilities for evaluations
 * @author zhangfan
 *
 */
public class EvaluateTool {
	/**
	 * Get the cross cut for cross evaluation
	 * @param folder
	 * @return
	 */
	public static ArrayList<ArrayList<ArrayList<RevisionDocument>>> getCrossCut(ArrayList<RevisionDocument> docs, int folder) {
		int size = docs.size();
		int topK = (int)Math.round((size*1.0/folder));
		ArrayList<ArrayList<ArrayList<RevisionDocument>>> crossCuts = new ArrayList<ArrayList<ArrayList<RevisionDocument>>>();
		Collections.shuffle(docs); //shuffle and get top K as testSet
		for(int i = 0;i<folder;i++) {
			ArrayList<ArrayList<RevisionDocument>> cuts = new ArrayList<ArrayList<RevisionDocument>>();
			ArrayList<RevisionDocument> test = new ArrayList<RevisionDocument>();
			ArrayList<RevisionDocument> train = new ArrayList<RevisionDocument>();
			
			int startIndex = topK*i;
			int endIndex = topK*(i+1)-1;
			for(int j = 0;j<docs.size();j++) {
				if(j>=startIndex&&j<=endIndex) {
					test.add(docs.get(j));
				} else {
					train.add(docs.get(j));
				}
			}
			cuts.add(train);
			cuts.add(test);
			crossCuts.add(cuts);
		}
		return crossCuts;
	}
	
	public static void printEvaluation(ArrayList<ConfusionMatrix> matrice) {
		System.out.println(getOverallConfusionMatrixStr(matrice));
		System.out.println("\nOVERALL Performance");
		System.out.println("Avg Prec:" + getAvgPrec(matrice));
		System.out.println("Avg Recall: " + getAvgRecall(matrice));
		System.out.println("Avg FMeasure: "+getAvgFMeasure(matrice));
		System.out.println("Each category");
		ArrayList<String> attrs = matrice.get(0).getAttrs();
		for(String attr: attrs) {
			System.out.println(attr);
			System.out.println("PRECISION:"+getAvgPrec(attr,matrice));
			System.out.println("RECALL:"+getAvgRecall(attr,matrice));
			System.out.println("F Measure:"+getAvgFMeasure(attr,matrice));
			System.out.println("UnWeighted F:"+getAvgUnweightedFMeasure(attr,matrice));
		}
	}
	
	public static double getAvgUnweightedFMeasure(String name, ArrayList<ConfusionMatrix> matrice) {
		double fAll = 0;
		int cnt = 0;
		for(ConfusionMatrix matrix: matrice) {
			double f = matrix.getUnWeightedFMeasure(name);
			if(f!=-1) {
				fAll += f;
				cnt ++;
			}
		}
		if(cnt == 0) return -1;
		return fAll/cnt;
	}
	
	public static double getAvgPrec(String name, ArrayList<ConfusionMatrix> matrice) {
		double precAll = 0;
		int cnt = 0;
		for(ConfusionMatrix matrix: matrice) {
			double p = matrix.getPrec(name);
			if(p!=-1) {
				precAll += p;
				cnt ++;
			}
		}
		if(cnt == 0) return -1;
		return precAll/cnt;
	}
	
	public static double getAvgRecall(String name, ArrayList<ConfusionMatrix> matrice) {
		double recallAll = 0;
		int cnt = 0;
		for(ConfusionMatrix matrix: matrice) {
			double r = matrix.getRecall(name);
			if(r!=-1) {
				recallAll += r;
				cnt ++;
			}
		}
		if(cnt == 0) return -1;
		return recallAll/cnt;
	}
	
	public static double getAvgFMeasure(String name, ArrayList<ConfusionMatrix> matrice) {
		double fAll = 0;
		int cnt = 0;
		for(ConfusionMatrix matrix: matrice) {
			double f = matrix.getRecall(name);
			if(f!=-1) {
				fAll += f;
				cnt ++;
			}
		}
		if(cnt == 0) return -1;
		return fAll/cnt;
	}
	
	public static double getAvgPrec(ArrayList<ConfusionMatrix> matrice) {
		ArrayList<String> attrs = matrice.get(0).getAttrs();
		double pAll = 0;
		int pCnt = 0;
		for(String attr: attrs) {
			double p = getAvgPrec(attr,matrice);
			if(p!=-1) {
				pAll += p;
				pCnt += 1;
			}
		}
		if(pCnt!=0) return pAll/pCnt;
		return -1;
	}
	
	public static double getAvgRecall(ArrayList<ConfusionMatrix> matrice) {
		ArrayList<String> attrs = matrice.get(0).getAttrs();
		double rAll = 0;
		int rCnt = 0;
		for(String attr: attrs) {
			double r = getAvgRecall(attr,matrice);
			if(r!=-1) {
				rAll += r;
				rCnt += 1;
			}
		}
		if(rCnt!=0) return rAll/rCnt;
		return -1;
	}
	
	public static double getAvgFMeasure(ArrayList<ConfusionMatrix> matrice) {
		ArrayList<String> attrs = matrice.get(0).getAttrs();
		double fAll = 0;
		int fCnt = 0;
		for(String attr: attrs) {
			double f = getAvgFMeasure(attr,matrice);
			if(f!=-1) {
				fAll += f;
				fCnt += 1;
			}
		}
		if(fCnt!=0) return fAll/fCnt;
		return -1;
	}
	
	
	public static String getOverallConfusionMatrixStr(ArrayList<ConfusionMatrix> matrice) {
		ConfusionMatrix cm = matrice.get(0);
		for(int i =1;i<matrice.size();i++) {
			cm.merge(matrice.get(i));
		}
		return cm.toString();
	}
	
	public static double getAvgUnweightedFMeasure(ArrayList<ConfusionMatrix> matrice) {
		ArrayList<String> attrs = matrice.get(0).getAttrs();
		double fAll = 0;
		int fCnt = 0;
		for(String attr: attrs) {
			double f = getAvgUnweightedFMeasure(attr,matrice);
			if(f!=-1) {
				fAll += f;
				fCnt ++;
			}
		}
		if(fCnt!=0) return fAll/fCnt;
		return -1;
	}
	
	public static double getOverallPrec(String name, ArrayList<ConfusionMatrix> matrice) {
		ConfusionMatrix cm = matrice.get(0);
		for(int i =1;i<matrice.size();i++) {
			cm.merge(matrice.get(i));
		}
		return cm.getPrec(name);
	}
	
	public static double getOverallRecall(String name, ArrayList<ConfusionMatrix> matrice) {
		ConfusionMatrix cm  = matrice.get(0);
		for(int i = 1;i<matrice.size();i++) {
			cm.merge(matrice.get(i));
		}
		return cm.getRecall(name);
	}
	
	public static double getOverallFMeasure(String name, ArrayList<ConfusionMatrix> matrice) {
		ConfusionMatrix cm  = matrice.get(0);
		for(int i = 1;i<matrice.size();i++) {
			cm.merge(matrice.get(i));
		}
		return cm.getFMeasure(name);
	}
	
	
	public static double getOverallUnweightedF(String name, ArrayList<ConfusionMatrix> matrice) {
		ConfusionMatrix cm  = matrice.get(0);
		for(int i = 1;i<matrice.size();i++) {
			cm.merge(matrice.get(i));
		}
		return cm.getUnWeightedFMeasure(name);
	}
}
