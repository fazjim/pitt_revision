package edu.pitt.lrdc.cs.revision.statistics;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;

import edu.pitt.lrdc.cs.revision.io.RevisionDocumentReader;
import edu.pitt.lrdc.cs.revision.model.RevisionDocument;
import edu.pitt.lrdc.cs.revision.model.RevisionOp;
import edu.pitt.lrdc.cs.revision.model.RevisionPurpose;
import edu.pitt.lrdc.cs.revision.model.RevisionUnit;

public class DataStatistics {
	public static double getStd(ArrayList<Double> values) {
		double std = 0.0;
		double avg = 0.0;
		for(double val: values) {
			avg += val;
		}
		avg = avg/values.size();
		for(double val: values) {
			std += (val-avg)*(val-avg);
		}
		std = std/values.size();
		return Math.sqrt(std);
	}
	
	public static void stat(ArrayList<RevisionDocument> docs) {
		double oldAverageSent = 0;
		double newAverageSent = 0;
		ArrayList<Double> oldSentNums = new ArrayList<Double>();
		ArrayList<Double> newSentNums = new ArrayList<Double>();
		
		for (RevisionDocument doc : docs) {
			oldAverageSent += doc.getOldDraftSentences().size();
			newAverageSent += doc.getNewDraftSentences().size();
			ArrayList<RevisionDocument> tmp = new ArrayList<RevisionDocument>();
			tmp.add(doc);
			System.out.println(doc.getDocumentName());
			System.out.println("=========All ops==========");
			statEditOps(tmp);
			System.out.println("=========All cats==========");
			statEditTypes(tmp);
			
			oldSentNums.add(doc.getOldDraftSentences().size()*1.0);
			newSentNums.add(doc.getNewDraftSentences().size()*1.0);
		}
		System.out.println("=============All Operations==============");
		statEditOps(docs);
		System.out.println("=============All Categories==============");
		statEditTypes(docs);
		System.out
		.println("=============Operations and Categories=============");
		statAllTypes(docs);
		System.out
				.println("=============Operations and Categories=============");
		statAllTypesMerge(docs);
		System.out.println("=============Multi revisions=================");
		statMultiRevisions(docs);
		
		System.out.println("OLD:"+oldAverageSent/docs.size());
		System.out.println("NEW:"+newAverageSent/docs.size());
		System.out.println("OLD STD:"+getStd(oldSentNums));
		System.out.println("NEW STD:"+getStd(newSentNums));
	}

	public static String generateIndiceStr(RevisionUnit ru) {
		ArrayList<Integer> newIndices = ru.getNewSentenceIndex();
		ArrayList<Integer> oldIndices = ru.getOldSentenceIndex();
		String str = "";
		str += "NEW:";
		if (newIndices != null) {
			for (Integer newIndex : newIndices) {
				if (newIndex != -1)
					str += newIndex + "|";
			}
		}
		str += "OLD";
		if (oldIndices != null) {
			for (Integer oldIndex : oldIndices) {
				if (oldIndex != -1)
					str += oldIndex + "|";
			}
		}
		return str;
	}

	public static void statMultiRevisions(ArrayList<RevisionDocument> docs) {
		int total = 0;
		for (RevisionDocument doc : docs) {
			Hashtable<String, String> revisionSet = new Hashtable<String, String>();
			Hashtable<String, String> revisionDuplicates = new Hashtable<String, String>();
			ArrayList<RevisionUnit> rus = doc.getRoot().getRevisionUnitAtLevel(
					0);
			// System.out.println(doc.getDocumentName());
			for (RevisionUnit ru : rus) {
				String pair = generateIndiceStr(ru);
				// System.out.println(pair);
				if (!pair.equals("NEW:OLD")) {
					if (revisionSet.containsKey(pair)) {
						// System.out.println(pair);
						if (!revisionDuplicates.containsKey(pair)) {
							revisionDuplicates.put(
									pair,
									revisionSet.get(pair)
											+ ","
											+ RevisionPurpose.getPurposeName(ru
													.getRevision_purpose()));
						} else {
							revisionDuplicates.put(
									pair,
									revisionDuplicates.get(pair)
											+ ","
											+ RevisionPurpose.getPurposeName(ru
													.getRevision_purpose()));
						}
					} else {
						revisionSet.put(pair, RevisionPurpose.getPurposeName(ru
								.getRevision_purpose()));
					}
				} else {
					// System.out.println(ru.getRevision_index());
				}
			}
			total += revisionDuplicates.size();
			System.out.println("Total duplicate:" + total);
			Iterator<String> it = revisionDuplicates.keySet().iterator();
			while (it.hasNext()) {
				System.out.println(revisionDuplicates.get(it.next()));
			}
		}

	}

	public static void statEditTypes(ArrayList<RevisionDocument> docs) {
		Hashtable<Integer, Integer> revisionTypes = new Hashtable<Integer, Integer>();
		for (RevisionDocument doc : docs) {
			ArrayList<RevisionUnit> rus = doc.getRoot().getRevisionUnitAtLevel(
					0);
			for (RevisionUnit ru : rus) {
				if (revisionTypes.containsKey(ru.getRevision_purpose())) {
					revisionTypes.put(ru.getRevision_purpose(),
							revisionTypes.get(ru.getRevision_purpose()) + 1);
				} else {
					revisionTypes.put(ru.getRevision_purpose(), 1);
				}
			}
		}

		for (int i = RevisionPurpose.START; i <= RevisionPurpose.END; i++) {
			System.out.println(RevisionPurpose.getPurposeName(i) + ":"
					+ revisionTypes.get(i));
		}
	}

	public static void statAllTypes(ArrayList<RevisionDocument> docs) {
		Hashtable<String, Integer> revisionAllTypes = new Hashtable<String, Integer>();
		for (RevisionDocument doc : docs) {
			ArrayList<RevisionUnit> rus = doc.getRoot().getRevisionUnitAtLevel(
					0);
			for (RevisionUnit ru : rus) {
				String cat = RevisionPurpose.getPurposeName(ru
						.getRevision_purpose())
						+ "-"
						+ RevisionOp.getOpName(ru.getRevision_op());
				if (revisionAllTypes.containsKey(cat)) {
					revisionAllTypes.put(cat, revisionAllTypes.get(cat) + 1);
				} else {
					revisionAllTypes.put(cat, 1);
				}
			}
		}

		Iterator<String> it = revisionAllTypes.keySet().iterator();
		while (it.hasNext()) {
			String cat = it.next();
			System.out.println(cat + ":" + revisionAllTypes.get(cat));
		}
	}

	public static void statAllTypesMerge(ArrayList<RevisionDocument> docs) {
		Hashtable<String, Integer> revisionAllTypes = new Hashtable<String, Integer>();
		Hashtable<String, Integer> tagTable = new Hashtable<String, Integer>();
		Hashtable<String, String> opTable = new Hashtable<String, String>();
		for (RevisionDocument doc : docs) {
			ArrayList<RevisionUnit> rus = doc.getRoot().getRevisionUnitAtLevel(
					0);
			for (RevisionUnit ru : rus) {
				String key = doc.getDocumentName() + ru.getUniqueID();
				if (tagTable.containsKey(key)) {
					int revP = tagTable.get(key);
					int revC = ru.getRevision_purpose();
					if (revP > 5 && revC <= 5) {
						String tag = RevisionPurpose.getPurposeName(revC);
						String cat = tag + "-"
								+ RevisionOp.getOpName(ru.getRevision_op());
						tagTable.put(key, revC);
						opTable.put(key, cat);
					} else if (revP <= 5 && revC <= 5) {
						if (revC == RevisionPurpose.CLAIMS_IDEAS) {
							String tag = RevisionPurpose.getPurposeName(revC);
							String cat = tag + "-"
									+ RevisionOp.getOpName(ru.getRevision_op());
							tagTable.put(key, revC);
							opTable.put(key, cat);
						} else if (revC == RevisionPurpose.CD_WARRANT_REASONING_BACKING) {
							if (revP != RevisionPurpose.CLAIMS_IDEAS) {
								String tag = RevisionPurpose
										.getPurposeName(revC);
								String cat = tag
										+ "-"
										+ RevisionOp.getOpName(ru
												.getRevision_op());
								tagTable.put(key, revC);
								opTable.put(key, cat);
							}
						} else if (revC == RevisionPurpose.EVIDENCE) {
							if (revP != RevisionPurpose.CLAIMS_IDEAS
									&& revP != RevisionPurpose.CD_WARRANT_REASONING_BACKING) {
								String tag = RevisionPurpose
										.getPurposeName(revC);
								String cat = tag
										+ "-"
										+ RevisionOp.getOpName(ru
												.getRevision_op());
								tagTable.put(key, revC);
								opTable.put(key, cat);
							}
						} else if (revC == RevisionPurpose.CD_GENERAL_CONTENT_DEVELOPMENT) {
							//do nothing
						}
					} else {
						// do nothing
					}
				} else {
					tagTable.put(key, ru.getRevision_purpose());
					int revC = ru.getRevision_purpose();
					String tag = "Surface";
					if (revC == RevisionPurpose.CLAIMS_IDEAS) {
						tag = RevisionPurpose
								.getPurposeName(RevisionPurpose.CLAIMS_IDEAS);
					} else if (revC == RevisionPurpose.CD_WARRANT_REASONING_BACKING
							|| revC == RevisionPurpose.CD_REBUTTAL_RESERVATION) {
						tag = RevisionPurpose
								.getPurposeName(RevisionPurpose.CD_WARRANT_REASONING_BACKING);
					} else if (revC == RevisionPurpose.EVIDENCE) {
						tag = RevisionPurpose
								.getPurposeName(RevisionPurpose.EVIDENCE);
					} else if (revC == RevisionPurpose.CD_GENERAL_CONTENT_DEVELOPMENT) {
						tag = RevisionPurpose
								.getPurposeName(RevisionPurpose.CD_GENERAL_CONTENT_DEVELOPMENT);
					} else {
						tag = RevisionPurpose
								.getPurposeName(RevisionPurpose.SURFACE);
					}

					String cat = tag + "-"
							+ RevisionOp.getOpName(ru.getRevision_op());
					opTable.put(key, cat);
				}
			}
		}

		Iterator<String> opIt = opTable.keySet().iterator();
		while(opIt.hasNext()) {
			String key = opIt.next();
			String cat = opTable.get(key);
			if(revisionAllTypes.containsKey(cat)) {
				revisionAllTypes.put(cat, revisionAllTypes.get(cat)+1);
			} else {
				revisionAllTypes.put(cat, 1);
			}
		}
		
		Iterator<String> it = revisionAllTypes.keySet().iterator();
		while (it.hasNext()) {
			String cat = it.next();
			System.out.println(cat + ":" + revisionAllTypes.get(cat));
		}
	}

	public static void statEditOps(ArrayList<RevisionDocument> docs) {
		int totalADD = 0;
		int totalDel = 0;
		int totalMod = 0;

		for (RevisionDocument doc : docs) {
			ArrayList<RevisionUnit> rus = doc.getRoot().getRevisionUnitAtLevel(
					0);
			for (RevisionUnit ru : rus) {
				if (ru.getRevision_op() == RevisionOp.ADD) {
					totalADD++;
				} else if (ru.getRevision_op() == RevisionOp.DELETE) {
					totalDel++;
				} else if (ru.getRevision_op() == RevisionOp.MODIFY) {
					totalMod++;
				}
			}
		}

		System.out.println("ADD:" + totalADD);
		System.out.println("DELETE:" + totalDel);
		System.out.println("MODIFY:" + totalMod);
	}

	public static void main(String[] args) throws Exception {
		DataStatistics ds = new DataStatistics();
		/*
		 * RevisionDocumentReader rd = new RevisionDocumentReader(); //
		 * ArrayList<RevisionDocument> docs = // rd.readDocs(
		 * "C:\\Not Backed Up\\data\\Braverman_sentence_alignment\\Braverman_sentence_alignment\\annotation_revision_full\\class4"
		 * ); //String class3 = "D:\\annotationTool\\annotated\\class3"; String
		 * class3 = "C:\\Not Backed Up\\data\\annotated\\revisedClass3"; String
		 * class4 = "C:\\Not Backed Up\\data\\annotated\\revisedClass4";
		 * //String class2 = "D:\\annotationTool\\annotated\\class2";
		 * ArrayList<RevisionDocument> docs = rd.readDocs(class3);
		 * ArrayList<RevisionDocument> docs2 = rd.readDocs(class4);
		 * //ArrayList<RevisionDocument> docs3 = rd.readDocs(class2);
		 * docs.addAll(docs2); //docs.addAll(docs3);
		 */
		String path = "C:\\Not Backed Up\\data\\naaclData\\C1";
		path = "C:\\Not Backed Up\\data_phrase_science\\BarnettPhraseAlign";
		ds.stat(RevisionDocumentReader.readDocs(path));
	}
}
