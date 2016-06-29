package edu.pitt.cs.revision.machinelearning;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import weka.core.Instances;
import edu.pitt.cs.revision.joint.EditSequence;
import edu.pitt.cs.revision.joint.EditStep;
import edu.pitt.cs.revision.joint.SequenceTransformer;
import edu.pitt.cs.revision.purpose.CRFFeatureExtractor;
import edu.pitt.cs.revision.purpose.SequenceFeatureExtractor;
import edu.pitt.lrdc.cs.revision.model.RevisionDocument;
import edu.pitt.lrdc.cs.revision.model.RevisionPurpose;

/**
 * Given a sequence, label the sequence and return the probablility
 * 
 * @author zhangfan
 *
 */
public class SequenceProcessor {
	SequenceFeatureExtractor fe;
	WekaAssist wa;
	Hashtable<Integer, String> indexBindings = new Hashtable<Integer, String>();

	public SequenceProcessor() {
		fe = new SequenceFeatureExtractor();
		wa = new WekaAssist();
	}

	public int getRevType(String categoryName, int option) {
		if(option == 1) {
			if(categoryName.endsWith("Content")) {
				return RevisionPurpose.CLAIMS_IDEAS;
			} else if(categoryName.endsWith("Surface")) {
				return RevisionPurpose.WORDUSAGE_CLARITY;
			} else if(categoryName.endsWith("Nochange")) {
				return RevisionPurpose.NOCHANGE;
			}
		} else if(option == 2) {
			if(categoryName.endsWith(RevisionPurpose.getPurposeName(RevisionPurpose.CLAIMS_IDEAS))) {
				return RevisionPurpose.CLAIMS_IDEAS;
			} else if(categoryName.endsWith("Support")) {
				return RevisionPurpose.CD_WARRANT_REASONING_BACKING;
			} else if(categoryName.endsWith("Surface")) {
				return RevisionPurpose.WORDUSAGE_CLARITY;
			} else if(categoryName.endsWith("Nochange")) {
				return RevisionPurpose.NOCHANGE;
			}
		} else if(option == 3) {
			if(categoryName.endsWith("Nochange")) {
				return RevisionPurpose.NOCHANGE;
			} else if(categoryName.endsWith(RevisionPurpose
						.getPurposeName(RevisionPurpose.CD_WARRANT_REASONING_BACKING))) {
				return RevisionPurpose.CD_WARRANT_REASONING_BACKING;
			} else if(categoryName.endsWith(RevisionPurpose.getPurposeName(RevisionPurpose.EVIDENCE))) {
				return RevisionPurpose.EVIDENCE;
			} else if(categoryName.endsWith(RevisionPurpose
					.getPurposeName(RevisionPurpose.CD_GENERAL_CONTENT_DEVELOPMENT))) {
				return RevisionPurpose.CD_GENERAL_CONTENT_DEVELOPMENT;
			} else if(categoryName.endsWith(RevisionPurpose
					.getPurposeName(RevisionPurpose.CLAIMS_IDEAS))) {
				return RevisionPurpose.CLAIMS_IDEAS;
			} else if(categoryName.endsWith(RevisionPurpose.getPurposeName(RevisionPurpose.SURFACE))) {
				return RevisionPurpose.SURFACE;
			}
		}
		return RevisionPurpose.NOCHANGE;
	}
	
	public String getCategoryName(double val) {
		int valInt = (int) val;
		return indexBindings.get(valInt);
	}
	public String getCategoryName(double val, int option) {
		int valInt = (int) val;
		if (option == 1) {
			if (valInt == 0) {
				return "Content";
			} else if (valInt == 1) {
				return "Surface";
			} else if (valInt == 2) {
				return "Nochange";
			}
		} else if (option == 2) {
			if (valInt == 0) {
				return RevisionPurpose
						.getPurposeName(RevisionPurpose.CLAIMS_IDEAS);
			} else if (valInt == 1) {
				return "Support";
			} else if (valInt == 2) {
				return "Surface";
			} else if (valInt == 3) {
				return "Nochange";
			}
		} else if (option == 3) {
			if (valInt == 0) {
				return RevisionPurpose
						.getPurposeName(RevisionPurpose.CLAIMS_IDEAS);
			} else if (valInt == 1) {
				return RevisionPurpose
						.getPurposeName(RevisionPurpose.CD_WARRANT_REASONING_BACKING);
			} else if (valInt == 2) {
				return RevisionPurpose.getPurposeName(RevisionPurpose.EVIDENCE);
			} else if (valInt == 3) {
				return RevisionPurpose
						.getPurposeName(RevisionPurpose.CD_GENERAL_CONTENT_DEVELOPMENT);
			} else if (valInt == 4) {
				return RevisionPurpose.getPurposeName(RevisionPurpose.SURFACE);
			} else if (valInt == 5) {
				return "Nochange";
			}
		}
		return "Nochange";
	}

	/**
	 * Generate all the candidate feature sets for later inquiry
	 * 
	 * @param doc
	 * @param sequence
	 * @param usingNgram
	 * @param option
	 * @param k
	 *            (The context to look at)
	 * @param remove
	 * @param realTagTable
	 * @return
	 * @throws Exception
	 */
	public Instances getInstances(ArrayList<RevisionDocument> docs,
			boolean usingNgram, int option, int k, int remove) throws Exception {
		ArrayList<String> categories = new ArrayList<String>();
		if (option == 1) { // Binary
			categories.add("Content");
			categories.add("Surface");
			categories.add("Nochange");
		} else if (option == 2) {
			categories.add(RevisionPurpose
					.getPurposeName(RevisionPurpose.CLAIMS_IDEAS));
			categories.add("Support");
			categories.add("Surface");
			categories.add("Nochange");
		} else if (option == 3) {
			categories.add(RevisionPurpose
					.getPurposeName(RevisionPurpose.CLAIMS_IDEAS));
			categories
					.add(RevisionPurpose
							.getPurposeName(RevisionPurpose.CD_WARRANT_REASONING_BACKING));
			categories.add(RevisionPurpose
					.getPurposeName(RevisionPurpose.EVIDENCE));
			categories
					.add(RevisionPurpose
							.getPurposeName(RevisionPurpose.CD_GENERAL_CONTENT_DEVELOPMENT));
			categories.add(RevisionPurpose
					.getPurposeName(RevisionPurpose.SURFACE));
			categories.add("Nochange");
		}
		ArrayList<String> newCategories = new ArrayList<String>();
		indexBindings = new Hashtable<Integer, String>();
		String[] moves = { "M-M", "M-K", "K-M" };
		int categoryIndex = 0;
		for (String move : moves) {
			for (String category : categories) {
				String newTag = move + "-" + category;
				newCategories.add(newTag);
				indexBindings.put(categoryIndex, newTag);
				categoryIndex++;
			}
		}
		fe.buildFeatures(usingNgram, newCategories, k, remove);
		Instances dataset = wa.buildInstances(fe.getFeatures(), usingNgram);
		for (RevisionDocument doc : docs) {
			int oldDraftSentences = doc.getOldDraftSentences().size();
			int newDraftSentences = doc.getNewDraftSentences().size();
			for (int i = 1; i <= oldDraftSentences; i++) {
				for (int j = 1; j <= newDraftSentences; j++) {
					ArrayList<Integer> newIndexes = new ArrayList<Integer>();
					ArrayList<Integer> oldIndexes = new ArrayList<Integer>();
					newIndexes.add(j);
					oldIndexes.add(i);
					Object[] features = fe.extractFeatures(doc, newIndexes,
							oldIndexes, 0, 0, usingNgram, k, option);

					String revTag = "M-M-Nochange";
					String id = i + "-" + j;
					wa.addInstance(features, fe.getFeatures(), usingNgram,
							dataset, revTag, doc.getDocumentName() + "_" + id);
				}
			}
		}
		return dataset;
	}

	/**
	 * Given a sequence of the training data, generate the features
	 * 
	 * @param doc
	 * @param sequence
	 * @param usingNgram
	 * @param option
	 * @param k
	 *            (The context to look at)
	 * @param remove
	 * @param realTagTable
	 * @return
	 * @throws Exception
	 */
	public Instances getInstances(ArrayList<RevisionDocument> docs,
			List<List<EditSequence>> sequences, boolean usingNgram, int option,
			int k, int remove, Hashtable<String, String> realTagTable)
			throws Exception {
		ArrayList<String> categories = new ArrayList<String>();
		if (option == 1) { // Binary
			categories.add("Content");
			categories.add("Surface");
			categories.add("Nochange");
		} else if (option == 2) {
			categories.add(RevisionPurpose
					.getPurposeName(RevisionPurpose.CLAIMS_IDEAS));
			categories.add("Support");
			categories.add("Surface");
			categories.add("Nochange");
		} else if (option == 3) {
			categories.add(RevisionPurpose
					.getPurposeName(RevisionPurpose.CLAIMS_IDEAS));
			categories
					.add(RevisionPurpose
							.getPurposeName(RevisionPurpose.CD_WARRANT_REASONING_BACKING));
			categories.add(RevisionPurpose
					.getPurposeName(RevisionPurpose.EVIDENCE));
			categories
					.add(RevisionPurpose
							.getPurposeName(RevisionPurpose.CD_GENERAL_CONTENT_DEVELOPMENT));
			categories.add(RevisionPurpose
					.getPurposeName(RevisionPurpose.SURFACE));
			categories.add("Nochange");
		}

		ArrayList<String> newCategories = new ArrayList<String>();
		indexBindings = new Hashtable<Integer, String>();
		String[] moves = { "M-M", "M-K", "K-M" };
		int categoryIndex = 0;
		for (String move : moves) {
			for (String category : categories) {
				String newTag = move + "-" + category;
				newCategories.add(newTag);
				indexBindings.put(categoryIndex, newTag);
				categoryIndex++;
			}
		}
		fe.buildFeatures(usingNgram, newCategories, k, remove);
		Instances dataset = wa.buildInstances(fe.getFeatures(), usingNgram);
		for (int i = 0; i < docs.size(); i++) {
			RevisionDocument doc = docs.get(i);
			List<EditSequence> sequenceList = sequences.get(i);
			for (EditSequence sequence : sequenceList) {
				List<EditStep> units = sequence.getLabelSequence();

				for (EditStep step : units) {
					ArrayList<Integer> newIndexes = new ArrayList<Integer>();
					ArrayList<Integer> oldIndexes = new ArrayList<Integer>();
					newIndexes.add(step.getCurrentD2());
					oldIndexes.add(step.getCurrentD1());
					Object[] features = fe.extractFeatures(doc, newIndexes,
							oldIndexes, step.getD1Move(), step.getD2Move(),
							usingNgram, k, option);

					int revPurpose = step.getType();
					String revTag = "Nochange";
					if (revPurpose == RevisionPurpose.NOCHANGE) {
						revTag = "Nochange";
					} else if (revPurpose == RevisionPurpose.SURFACE
							|| revPurpose == RevisionPurpose.CONVENTIONS_GRAMMAR_SPELLING
							|| revPurpose == RevisionPurpose.WORDUSAGE_CLARITY) {
						revTag = "Surface";
					} else {
						if (option == 1) {
							revTag = "Content";
						} else if (option == 2) {
							if (revPurpose == RevisionPurpose.CLAIMS_IDEAS) {
								revTag = RevisionPurpose
										.getPurposeName(RevisionPurpose.CLAIMS_IDEAS);
							} else {
								revTag = "Support";
							}
						} else if (option == 3) {
							if (revPurpose == RevisionPurpose.CLAIMS_IDEAS) {
								revTag = RevisionPurpose
										.getPurposeName(RevisionPurpose.CLAIMS_IDEAS);
							} else if (revPurpose == RevisionPurpose.CD_GENERAL_CONTENT_DEVELOPMENT) {
								revTag = RevisionPurpose
										.getPurposeName(RevisionPurpose.CD_GENERAL_CONTENT_DEVELOPMENT);
							} else if (revPurpose == RevisionPurpose.EVIDENCE) {
								revTag = RevisionPurpose
										.getPurposeName(RevisionPurpose.CD_WARRANT_REASONING_BACKING);
							}
						}
					}

					String instanceID = doc.getDocumentName() + "_"
							+ step.toID();
					String realTag = step.getStr(step.getD1Move()) + "-"
							+ step.getStr(step.getD2Move()) + "-" + revTag;
					realTagTable.put(instanceID, realTag);
					System.err.println(instanceID);
					wa.addInstance(features, fe.getFeatures(), usingNgram,
							dataset, realTag,
							doc.getDocumentName() + "_" + step.toID());
				}
			}
		}
		return dataset;
	}

	/**
	 * 
	 * @param doc
	 * @param realTagTable
	 * @param k
	 *            (context size)
	 * @return
	 * @throws Exception
	 */
	public Instances getInstances(ArrayList<RevisionDocument> docs,
			Hashtable<String, String> realTagTable, int k, boolean usingNgram,
			int option, int remove) throws Exception {
		List<List<EditSequence>> sequenceList = new ArrayList<List<EditSequence>>();
		for (RevisionDocument doc : docs) {
			List<EditSequence> sequences = SequenceTransformer
					.transformToSequence(doc);
			sequenceList.add(sequences);
		}
		Instances instData = getInstances(docs, sequenceList, usingNgram,
				option, k, remove, realTagTable);
		return instData;
	}

	/**
	 * Generate all candiate sequences
	 * 
	 * @param doc
	 * @param realTagTable
	 * @param k
	 *            (context size)
	 * @return
	 * @throws Exception
	 */
	public Instances getInstancesTest(ArrayList<RevisionDocument> docs,
			Hashtable<String, String> realTagTable, int k, boolean usingNgram,
			int option, int remove) throws Exception {
		List<List<EditSequence>> sequenceList = new ArrayList<List<EditSequence>>();
		for (RevisionDocument doc : docs) {
			List<EditSequence> sequences = SequenceTransformer
					.transformToSequence(doc);
			sequenceList.add(sequences);
		}
		Instances instData = getInstances(docs, sequenceList, usingNgram,
				option, k, remove, realTagTable);
		return instData;
	}
}
