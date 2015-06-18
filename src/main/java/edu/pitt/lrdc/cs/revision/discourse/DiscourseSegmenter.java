package edu.pitt.lrdc.cs.revision.discourse;

import java.util.ArrayList;

import edu.arizona.sista.discourse.rstparser.DiscourseTree;
import edu.arizona.sista.processors.Document;
import edu.arizona.sista.processors.Processor;
import edu.arizona.sista.processors.fastnlp.FastNLPProcessor;

public class DiscourseSegmenter {
	private static DiscourseSegmenter ds;
	private Processor proc;

	private DiscourseSegmenter() {
		proc = new FastNLPProcessor(false, false, true, true);
	}

	public static DiscourseSegmenter getInstance() {
		if (ds == null) {
			ds = new DiscourseSegmenter();
		}
		return ds;
	}

	public ArrayList<String> getEdus(String text) {
		ArrayList<String> edus = new ArrayList<String>();
		mergeBelow = false;
		Document doc = proc.annotate(text);
		proc.discourse(doc);
		DiscourseTree root = doc.discourseTree().get();
		addEdus(edus, root);
		return edus;
	}

	private boolean mergeBelow = false;

	public void addSingleEdu(ArrayList<String> edus, String text) {
		if (mergeBelow == true && edus.size()!=0) {
			String mergedText = edus.get(edus.size() - 1) + " " + text;
			edus.set(edus.size() - 1, mergedText);
		} else {
			edus.add(text);
		}
	}

	public void mergeAdd(ArrayList<String> edus, String rawText) {
		rawText = rawText.trim();
		int code = mergeCode(rawText);
		if (code == -1) {
			// Do not merge unless if already said merge below before, leave the
			// following alone
			addSingleEdu(edus, rawText);
			mergeBelow = false;
		} else if (code == 1) {
			// merge up, inform the adder that needs to merge
			mergeBelow = true;
			addSingleEdu(edus, rawText);
			mergeBelow = false; //Set the status back when merged
		} else if (code == 2) {
			// merge down, add and inform the adder that needs to merge
			addSingleEdu(edus, rawText);
			mergeBelow = true;
		}
	}

	public void addEdus(ArrayList<String> edus, DiscourseTree dt) {
		if (dt.children() == null) {
			String rawText = dt.rawText();
			rawText = rawText.replaceAll("-LRB- ", "(");
			rawText = rawText.replaceAll(" -RRB-", ")");

			rawText = rawText.trim();
			if (rawText.length() < 20) {
				// Avoid checking everycase, for this case, check if needs merge
				mergeAdd(edus, rawText);
			} else {
				// In other cases, the clause is likely to be segmented
				// correctly by the segmenter
				if (rawText.contains(",") || rawText.contains(";")) {
					breakClauses(edus, rawText);
				} else {
					addSingleEdu(edus, rawText);
					mergeBelow = false;
				}
			}
		} else {
			DiscourseTree[] childs = dt.children();
			DiscourseTree left = childs[0];
			DiscourseTree right = childs[1];
			addEdus(edus, left);
			addEdus(edus, right);
		}
	}

	public int mergeCode(String text) {
		int numOfspace = 0;
		for (int i = 0; i < text.length(); i++) {
			if (text.charAt(i) == ' ') {
				numOfspace++;
			}
		}
		if (numOfspace == 0 || numOfspace == 1) {
			// merge
			char startChar = text.charAt(0);
			if (!Character.isUpperCase(startChar) || startChar == '(') {
				return 1; // merge up
			} else {
				return 2; // merge down
			}
		} else {
			return -1; // do not merge
		}
	}

	public void breakClauses(ArrayList<String> edus, String text) {
		if (text.contains(";")) {
			// Must break;
			String[] splits = text.split(";");
			for(String split: splits) {
				mergeAdd(edus, split);
			}
		} else {
			String[] splits = text.split(",");
			for(String split: splits) {
				mergeAdd(edus, split); //Let the mergeAdd take care of this
			}
			
			/*
			boolean doSplit = true;
			for(String split: splits) {
				if(split.split(" ").length<=2) doSplit = false; 
			}
			if(doSplit == true) {
				for(String split: splits) {
					mergeAdd(edus, split);   //These might contain small cases? 
				}
			} else {
				mergeAdd(edus, text);
			}*/
		}
	}
}
