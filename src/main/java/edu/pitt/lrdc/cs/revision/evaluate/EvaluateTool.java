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
}
