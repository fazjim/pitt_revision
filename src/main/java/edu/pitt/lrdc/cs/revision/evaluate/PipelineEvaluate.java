package edu.pitt.lrdc.cs.revision.evaluate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;

import edu.pitt.lrdc.cs.revision.io.RevisionDocumentReader;
import edu.pitt.lrdc.cs.revision.model.RevisionDocument;
import edu.pitt.lrdc.cs.revision.model.RevisionPurpose;
import edu.pitt.lrdc.cs.revision.model.RevisionUnit;

public class PipelineEvaluate {
	public static void main(String[] args) throws Exception {
		String fanFileReal = "C:\\Not Backed Up\\data\\NSFStudy\\revision drafts\\fan\\corrected\\Annotation_fan_test.txt.xlsx";
		String fanFilePredict = "C:\\Not Backed Up\\data\\NSFStudy\\revision drafts\\fan\\auto\\Annotation_fan_test.txt.xlsx";

		String humaFileReal = "C:\\Not Backed Up\\data\\NSFStudy\\revision drafts\\huma\\corrected\\Annotation_huma_test.txt.xlsx";
		String humaFilePredict = "C:\\Not Backed Up\\data\\NSFStudy\\revision drafts\\huma\\auto\\Annotation_huma_test.txt.xlsx";

		String rebeccaFileReal = "C:\\Not Backed Up\\data\\NSFStudy\\revision drafts\\rebecca\\corrected\\Annotation_rebecca_test.txt.xlsx";
		String rebeccaFilePredict = "C:\\Not Backed Up\\data\\NSFStudy\\revision drafts\\rebecca\\auto\\Annotation_rebecca_test.txt.xlsx";

		String tazinFileReal = "C:\\Not Backed Up\\data\\NSFStudy\\revision drafts\\tazin\\corrected\\Annotation_tazin_test.txt.xlsx";
		String tazinFilePredict = "C:\\Not Backed Up\\data\\NSFStudy\\revision drafts\\tazin\\auto\\Annotation_tazin_test.txt.xlsx";

		evaluate(RevisionDocumentReader.readDoc(fanFileReal),
				RevisionDocumentReader.readDoc(fanFilePredict));
		evaluate(RevisionDocumentReader.readDoc(humaFileReal),
				RevisionDocumentReader.readDoc(humaFilePredict));
		evaluate(RevisionDocumentReader.readDoc(rebeccaFileReal),
				RevisionDocumentReader.readDoc(rebeccaFilePredict));
		evaluate(RevisionDocumentReader.readDoc(tazinFileReal),
				RevisionDocumentReader.readDoc(tazinFilePredict));
	}

	public static String getString(ArrayList<Integer> indices) {
		if (indices == null)
			return " ";
		Collections.sort(indices);
		String str = " ";
		for (Integer index : indices) {
			if (index != -1)
				;
			str += Integer.toString(index) + "_";
		}
		return str;
	}

	public static String getString(RevisionUnit ru) {
		String oldIndiceStr = getString(ru.getOldSentenceIndex());
		String newIndiceStr = getString(ru.getNewSentenceIndex());
		return "OLD_" + oldIndiceStr + "_NEW_" + newIndiceStr;
	}

	public static void evaluate(RevisionDocument realDoc,
			RevisionDocument predictedDoc) {
		double sentenceNumD1 = 0;
		double sentenceNumD2 = 0;
		double paragraphNumD1 = 0;
		double paragraphNumD2 = 0;
		double avgSentParaD1 = 0;
		double avgSentParaD2 = 0;
		double avgWordNumD1 = 0;
		double avgWordNumD2 = 0;
		double realRevCount = 0;
		double predictedRevCount = 0;

		double alignmentAccuracy = 0;

		double realCount_Claim = 0;
		double realCount_Evidence = 0;
		double realCount_General = 0;
		double realCount_Warrant = 0;
		double realCount_Surface = 0;

		double predictCount_Claim = 0;
		double predictCount_Evidence = 0;
		double predictCount_General = 0;
		double predictCount_Warrant = 0;
		double predictCount_Surface = 0;

		double prec_Claim = 0;
		double prec_Evidence = 0;
		double prec_General = 0;
		double prec_Warrant = 0;
		double prec_Surface = 0;

		double recall_Claim = 0;
		double recall_Evidence = 0;
		double recall_General = 0;
		double recall_Warrant = 0;
		double recall_Surface = 0;

		sentenceNumD1 = realDoc.getOldDraftSentences().size();
		sentenceNumD2 = realDoc.getNewDraftSentences().size();

		paragraphNumD1 = realDoc.getOldParagraphNum();
		paragraphNumD2 = realDoc.getNewParagraphNum();

		avgSentParaD1 = sentenceNumD1 / paragraphNumD1;
		avgSentParaD2 = sentenceNumD2 / paragraphNumD2;

		Hashtable<Integer, String> oldAlignmentIndice = new Hashtable<Integer, String>();
		Hashtable<Integer, String> newAlignmentIndice = new Hashtable<Integer, String>();

		double oldAccuracy = 0;
		for (int i = 0; i < realDoc.getOldDraftSentences().size(); i++) {
			int index = i + 1;
			ArrayList<Integer> aligned = realDoc.getNewFromOld(index);
			String sent = realDoc.getOldSentence(index);
			int wordNum = sent.split(" ").length;
			avgWordNumD1 += wordNum;
			oldAlignmentIndice.put(index, getString(aligned));
		}

		for (int i = 0; i < predictedDoc.getOldDraftSentences().size(); i++) {
			int index = i + 1;
			ArrayList<Integer> aligned = predictedDoc.getNewFromOld(index);
			if (oldAlignmentIndice.containsKey(index)
					&& getString(aligned).equals(oldAlignmentIndice.get(index))) {
				oldAccuracy += 1;
			}
		}
		oldAccuracy = oldAccuracy / sentenceNumD1;

		double newAccuracy = 0;
		for (int i = 0; i < realDoc.getNewDraftSentences().size(); i++) {
			int index = i + 1;
			ArrayList<Integer> aligned = realDoc.getOldFromNew(index);
			String sent = realDoc.getNewSentence(index);
			int wordNum = sent.split(" ").length;
			avgWordNumD2 += wordNum;
			newAlignmentIndice.put(index, getString(aligned));
		}

		for (int i = 0; i < predictedDoc.getNewDraftSentences().size(); i++) {
			int index = i + 1;
			ArrayList<Integer> aligned = predictedDoc.getOldFromNew(index);
			if (newAlignmentIndice.containsKey(index)
					&& getString(aligned).equals(newAlignmentIndice.get(index))) {
				newAccuracy += 1;
			}
		}
		newAccuracy = newAccuracy / sentenceNumD2;

		alignmentAccuracy = (oldAccuracy + newAccuracy) / 2;
		avgWordNumD1 = avgWordNumD1 / sentenceNumD1;
		avgWordNumD2 = avgWordNumD2 / sentenceNumD2;

		ArrayList<RevisionUnit> revisionsReal = realDoc.getRoot()
				.getRevisionUnitAtLevel(0);
		ArrayList<RevisionUnit> revisionPredict = predictedDoc.getRoot()
				.getRevisionUnitAtLevel(0);
		realRevCount = revisionsReal.size();
		predictedRevCount = revisionPredict.size();

		Hashtable<Integer, HashSet<String>> realTables = new Hashtable<Integer, HashSet<String>>();
		for (int i = 0; i < revisionsReal.size(); i++) {
			RevisionUnit ru = revisionsReal.get(i);
			int revPurpose = ru.getRevision_purpose();
			if (revPurpose == RevisionPurpose.CLAIMS_IDEAS) {
				realCount_Claim++;
			} else if (revPurpose == RevisionPurpose.EVIDENCE) {
				realCount_Evidence++;
			} else if (revPurpose == RevisionPurpose.CD_GENERAL_CONTENT_DEVELOPMENT) {
				realCount_General++;
			} else if (revPurpose == RevisionPurpose.CD_WARRANT_REASONING_BACKING) {
				realCount_Warrant++;
			} else {
				if (revPurpose != RevisionPurpose.CD_REBUTTAL_RESERVATION) {
					realCount_Surface++;
				}
			}
			String str = getString(ru);
			HashSet<String> revs;
			if (!realTables.containsKey(revPurpose)) {
				revs = new HashSet<String>();
				realTables.put(revPurpose, revs);
			} else {
				revs = realTables.get(revPurpose);
			}
			revs.add(str);
		}

		double corr_Claim = 0;
		double corr_Evidence = 0;
		double corr_General = 0;
		double corr_Warrant = 0;
		double corr_Surface = 0;

		for (int i = 0; i < revisionPredict.size(); i++) {
			RevisionUnit ru = revisionPredict.get(i);
			int revPurpose = ru.getRevision_purpose();
			if (revPurpose == RevisionPurpose.CLAIMS_IDEAS) {
				predictCount_Claim++;
				if (realTables.containsKey(revPurpose)
						&& realTables.get(revPurpose).contains(getString(ru))) {
					corr_Claim++;
				}
			} else if (revPurpose == RevisionPurpose.EVIDENCE) {
				predictCount_Evidence++;
				if (realTables.containsKey(revPurpose)
						&& realTables.get(revPurpose).contains(getString(ru))) {
					corr_Evidence++;
				}
			} else if (revPurpose == RevisionPurpose.CD_GENERAL_CONTENT_DEVELOPMENT) {
				predictCount_General++;
				if (realTables.containsKey(revPurpose)
						&& realTables.get(revPurpose).contains(getString(ru))) {
					corr_General++;
				}
			} else if (revPurpose == RevisionPurpose.CD_WARRANT_REASONING_BACKING) {
				predictCount_Warrant++;
				if (realTables.containsKey(revPurpose)
						&& realTables.get(revPurpose).contains(getString(ru))) {
					corr_Warrant++;
				}
			} else {
				if (revPurpose != RevisionPurpose.CD_REBUTTAL_RESERVATION) {
					predictCount_Surface++;
					if (realTables.containsKey(revPurpose)
							&& realTables.get(revPurpose).contains(
									getString(ru))) {
						corr_Surface++;
					}
				}
			}
		}

		prec_Claim = corr_Claim / predictCount_Claim;
		prec_Evidence = corr_Evidence / predictCount_Evidence;
		prec_General = corr_General / predictCount_General;
		prec_Warrant = corr_Warrant / predictCount_Warrant;
		prec_Surface = corr_Surface / predictCount_Surface;

		recall_Claim = corr_Claim / realCount_Claim;
		recall_Evidence = corr_Evidence / realCount_Evidence;
		recall_General = corr_General / realCount_General;
		recall_Warrant = corr_Warrant / realCount_Warrant;
		recall_Surface = corr_Surface / realCount_Surface;

		String str = realDoc.getDocumentName() + "\n";
		str += "Draft 1 sentence num: " + sentenceNumD1 + "\n";
		str += "Draft 1 paragraph num: " + paragraphNumD1 + "\n";
		str += "Draft 2 sentence num: " + sentenceNumD2 + "\n";
		str += "Draft 2 paragraph num: " + paragraphNumD2 + "\n";

		str += "Draft 1, avg sentence in paragraph: " + avgSentParaD1 + "\n";
		str += "Draft 2, avg sentence in paragraph: " + avgSentParaD2 + "\n";

		str += "Draft 1, avg word in sentence: " + avgWordNumD1 + "\n";
		str += "Draft 2, avg word in sentence: " + avgWordNumD2 + "\n";

		str += "Alignment accuracy: " + alignmentAccuracy + "\n";

		str += "Real Revision Count: " + realRevCount + "\n";
		str += "Predict Revision Count: " + predictedRevCount + "\n";

		str += "Claim/Ideas, Precision: " + prec_Claim + ", Recall: "
				+ recall_Claim + ", realCount:" + realCount_Claim + "\n";
		str += "Warrant/Reasoning/Backing, Precision: " + prec_Warrant
				+ ", Recall: " + recall_Warrant + ", realCount:"
				+ realCount_Warrant + "\n";
		str += "Evidence, Precision: " + prec_Evidence + ", Recall: "
				+ recall_Evidence + ", realCount: " + realCount_Evidence + "\n";
		str += "General, Precision: " + prec_General + ", Recall: "
				+ recall_General + ", realCount: " + realCount_General + "\n";
		str += "Surface, Precision: " + prec_Surface + ", Recall: "
				+ recall_Surface + ", realCount: " + realCount_Surface + "\n";

		System.out.println(str);
	}
}
