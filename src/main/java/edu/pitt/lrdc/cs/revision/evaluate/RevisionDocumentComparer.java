package edu.pitt.lrdc.cs.revision.evaluate;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;

import edu.pitt.lrdc.cs.revision.model.RevisionDocument;
import edu.pitt.lrdc.cs.revision.model.RevisionPurpose;
import edu.pitt.lrdc.cs.revision.model.RevisionUnit;

public class RevisionDocumentComparer {
	public static int getAlignmentAgreements(RevisionDocument docOriginal,
			RevisionDocument docNew) {
		int oldDraftNum = docOriginal.getOldDraftSentences().size();
		int newDraftNum = docOriginal.getNewDraftSentences().size();
		int agreement = 0;
		for (int i = 1; i <= oldDraftNum; i++) {
			ArrayList<Integer> newAligned = docOriginal.getNewFromOld(i);
			ArrayList<Integer> compare = docNew.getNewFromOld(i);
			if (newAligned.size() == 0) {
				if (compare.size() == 0) {
					agreement++;
				}
			} else if (newAligned.size() == 1) {
				if (compare.size() == 1 && compare.get(0) == newAligned.get(0)) {
					agreement++;
				} else {
					if (compare.size() == 0) {
						System.err
								.println("For OLD:" + i
										+ ", missing alignment of "
										+ newAligned.get(0));
					} else {
						System.err.println("For OLD:" + i
								+ ", wrong alignment: " + compare.get(0) + ":"
								+ newAligned.get(0));
					}
				}
			} else {
				for (Integer j : newAligned) {
					if (docNew.getOldFromNew(j).size() == 0
							|| docNew.getOldFromNew(j).get(0) == i) {
						agreement++;
					}
				}
			}
		}
		for (int i = 1; i <= newDraftNum; i++) {
			ArrayList<Integer> oldAligned = docOriginal.getOldFromNew(i);
			ArrayList<Integer> compare = docNew.getOldFromNew(i);
			if (oldAligned.size() == 0) {
				if (compare.size() == 0) {
					agreement++;
				}
			} else if (oldAligned.size() == 1) {
				if (compare.size() == 1 && compare.get(0) == oldAligned.get(0)) {
					agreement++;
				} else {
					if (compare.size() == 0) {
						System.err
								.println("For NEW:" + i
										+ ", missing alignment of "
										+ oldAligned.get(0));
					} else {
						System.err.println("For NEW:" + i
								+ ", wrong alignment: " + compare.get(0) + ":"
								+ oldAligned.get(0));
					}
				}
			} else {
				for (Integer j : oldAligned) {
					if (docNew.getNewFromOld(j).size() == 0
							|| docNew.getNewFromOld(j).get(0) == i) {
						agreement++;
					}
				}
			}
		}
		return agreement;
	}

	/**
	 * get the agreement of purposes
	 * 
	 * @param types
	 *            2 or 3 or 5
	 * @return
	 */
	public static int[][] getPurposeAgreements(int typeNum,
			RevisionDocument originalDoc, RevisionDocument compareDoc) {
		int[][] confusionMatrix = new int[typeNum + 1][typeNum + 1];
		ArrayList<RevisionUnit> originalUnits = originalDoc.getRoot()
				.getRevisionUnitAtLevel(0);
		ArrayList<RevisionUnit> compareUnits = compareDoc.getRoot()
				.getRevisionUnitAtLevel(0);
		Hashtable<String, Integer> originalMap = new Hashtable<String, Integer>();
		Hashtable<String, Integer> compareMap = new Hashtable<String, Integer>();

		for (RevisionUnit unit : originalUnits) {
			String label = unit.getIndexLabel();
			originalMap.put(label, unit.getRevision_purpose());
		}

		for (RevisionUnit unit : compareUnits) {
			String label = unit.getIndexLabel();
			compareMap.put(label, unit.getRevision_purpose());
		}

		Iterator<String> iterOrigin = originalMap.keySet().iterator();
		while (iterOrigin.hasNext()) {
			String key = iterOrigin.next();
			int revType = originalMap.get(key);
			if (compareMap.containsKey(key)) {
				int revType2 = compareMap.get(key);
				confusionMatrix[getIndex(typeNum, revType)][getIndex(typeNum,
						revType2)] += 1;
			} else {
				confusionMatrix[getIndex(typeNum, revType)][typeNum] += 1;
			}
		}
		Iterator<String> iterCompare = compareMap.keySet().iterator();
		while (iterCompare.hasNext()) {
			String key = iterCompare.next();
			int revType = compareMap.get(key);
			if (!originalMap.containsKey(key)) {
				confusionMatrix[typeNum][getIndex(typeNum, revType)] += 1;
			}
		}
		return confusionMatrix;
	}

	public static double getOverallRecall(int[][] confusionMatrix) {
		int allOriginal = 0;
		int agreement = 0;
		for (int i = 0; i < confusionMatrix.length - 1; i++) {
			for (int j = 0; j < confusionMatrix.length; j++) {
				allOriginal += confusionMatrix[i][j];
				if (i == j) {
					agreement += confusionMatrix[i][j];
				}
			}
		}
		double recall = (agreement * 1.0) / allOriginal;
		return recall;
	}

	public static double getOverallPrecison(int[][] confusionMatrix) {
		int allCompare = 0;
		int agreement = 0;
		for (int i = 0; i < confusionMatrix.length; i++) {
			for (int j = 0; j < confusionMatrix.length - 1; j++) {
				allCompare += confusionMatrix[i][j];
				if (i == j) {
					agreement += confusionMatrix[i][j];
				}
			}
		}
		double prec = (agreement * 1.0) / allCompare;
		return prec;
	}

	public static int getIndex(int revTypes, int revType) {
		if (revTypes == 2) {
			if (revType == RevisionPurpose.SURFACE) {
				return 1;
			} else {
				return 0;
			}
		} else if (revTypes == 3) {
			if (revType == RevisionPurpose.CLAIMS_IDEAS) {
				return 0;
			} else if (revType == RevisionPurpose.CD_WARRANT_REASONING_BACKING
					|| revType == RevisionPurpose.EVIDENCE
					|| revType == RevisionPurpose.CD_GENERAL_CONTENT_DEVELOPMENT) {
				return 1;
			} else if (revType == RevisionPurpose.SURFACE) {
				return 2;
			}
		} else {
			if (revType == RevisionPurpose.CLAIMS_IDEAS) {
				return 0;
			} else if (revType == RevisionPurpose.CD_WARRANT_REASONING_BACKING) {
				return 1;
			} else if (revType == RevisionPurpose.EVIDENCE) {
				return 2;
			} else if (revType == RevisionPurpose.CD_GENERAL_CONTENT_DEVELOPMENT) {
				return 3;
			} else if (revType == RevisionPurpose.SURFACE) {
				return 4;
			}
		}
		return revTypes;
	}
}
