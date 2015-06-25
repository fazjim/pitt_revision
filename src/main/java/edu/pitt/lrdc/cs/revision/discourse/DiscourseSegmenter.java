package edu.pitt.lrdc.cs.revision.discourse;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

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
		if (mergeBelow == true && edus.size() != 0) {
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
			mergeBelow = false; // Set the status back when merged
		} else if (code == 2) {
			// merge down, add and inform the adder that needs to merge
			addSingleEdu(edus, rawText);
			mergeBelow = true;
		}
	}

	public boolean contains(String text, String[] breakers) {
		for (String breaker : breakers) {
			if (text.contains(breaker))
				return true;
		}
		return false;
	}

	public void addEdus(ArrayList<String> edus, DiscourseTree dt) {
		if (dt.children() == null) {
			String rawText = dt.rawText();
			rawText = rawText.replaceAll("-LRB- ", "(");
			rawText = rawText.replaceAll(" -RRB-", ")");
			rawText = rawText.replaceAll(" \\.", ".");
			rawText = rawText.replaceAll(" ,", ",");
			rawText = rawText.replaceAll( " \\?", "?");
			rawText = rawText.replaceAll( " \\!", "!");
			rawText = rawText.trim();
			if (rawText.length() < 20) {
				// Avoid checking everycase, for this case, check if needs merge
				mergeAdd(edus, rawText);
			} else {
				// In other cases, the clause is likely to be segmented
				// correctly by the segmenter

				// breakers from: coordinating conjunctions and subordinating
				// conjunctions
				// http://www.yourdictionary.com/index.php/pdf/articles/149.conjunctionschart.pdf
				// for such a huge list, might think about optimizing the algorithm and only break the related ones
				String[] breakers = { ",", ";", " and ", " or ", "(", ")",
						" for ", " nor ", " but ", " yet ", " so ",
						" because ", " before ", " even ", " even if ",
						" even though ", " after ", " although ", " as ",
						" as if ", " as long as ", " as much as ",
						" as soon as ", " as though ", " if ", " if only ",
						" if when ", " if then ", " inasmuch ",
						" in order that ", " just as ", " lest ", " now ",
						" now ", " since ", " now that ", " now when ",
						" once ", " provided ", " provided that ",
						" rather than ", " since ", " so that ", " supposing ",
						" than ", " that ", " though ", " til ", " unless ",
						" until ", " when ", " whenever ", " where ",
						" whereas ", " where if ", " wherever ", " whether ",
						" which ", " while ", " who ", " whoever ", " why " };
				/*
				 * if (rawText.contains(",") || rawText.contains(";") ) {
				 * breakClauses(edus, rawText); } else { addSingleEdu(edus,
				 * rawText); mergeBelow = false; }
				 */
				//if (contains(rawText, breakers)) {
					// System.out.println("BREAKING:"+rawText);
					breakClauses(edus, rawText, breakers);
				//}
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
		/*
		int numOfspace = 0;
		for (int i = 0; i < text.length(); i++) {
			if (text.charAt(i) == ' ') {
				numOfspace++;
			}
		}*/
		int numOfspace = text.split(" ").length;
		if (numOfspace <= 3) {
			// merge
			char startChar = text.charAt(0);
			if (!Character.isUpperCase(startChar) || startChar == '(') {
				if (startChar == '(' && !text.contains(")"))
					return 2;// Special case when parenthesis are broken
				return 1; // merge up
			} else {
				return 2; // merge down
			}
		} else {
			return -1; // do not merge
		}
	}

	// Sometimes it is not enough just to clause level or there is clause
	// segmentation error
	public void breakClauses(ArrayList<String> edus, String text) {
		if (text.contains(";")) {
			// Must break;
			String[] splits = text.split(";");
			for (String split : splits) {
				mergeAdd(edus, split);
			}
		} else {
			String[] splits = text.split(",");
			for (String split : splits) {
				mergeAdd(edus, split); // Let the mergeAdd take care of this
			}

			/*
			 * boolean doSplit = true; for(String split: splits) {
			 * if(split.split(" ").length<=2) doSplit = false; } if(doSplit ==
			 * true) { for(String split: splits) { mergeAdd(edus, split);
			 * //These might contain small cases? } } else { mergeAdd(edus,
			 * text); }
			 */
		}
	}

	public void breakClauses(ArrayList<String> edus, String text,
			String[] breakers) {
		ArrayList<String> segments = breakSegmentsBetter(text, breakers);
		for (String segment : segments) {
			mergeAdd(edus, segment);
		}
	}

	// This breaks text to segments according to the specified breakers
	// The algorithm implemented here relies on Java gc() to clean all the
	// created temporary arrays
	// A more wise way is to use a queue to do the implementation, which has
	// lower space complexity
	public ArrayList<String> breakSegments(String text, String[] breakers) {
		ArrayList<String> segments = new ArrayList<String>();
		segments.add(text);
		for (String breaker : breakers) {
			ArrayList<String> tmpSegments = new ArrayList<String>();
			for (String segment : segments) {
				ArrayList<String> tmp = new ArrayList<String>();
				breakSegment(tmp, segment, breaker);
				tmpSegments.addAll(tmp);
			}
			segments = tmpSegments;
		}
		return segments;
	}

	// This version saves more memory
	public ArrayList<String> breakSegmentsBetter(String text, String[] breakers) {
		Queue<String> segmentQ = new LinkedList<String>();
		segmentQ.add(text);
		int sliceNum = 1; // start with 1;
		int i = 0;
		ArrayList<String> tmp = new ArrayList<String>();
		while (i < breakers.length) {
			String breaker = breakers[i];
			int nextSliceNum = 0;
			int sliceIndex = 0;
			while (sliceIndex < sliceNum) {
				String segment = segmentQ.poll();
				breakSegment(tmp, segment, breaker);
				nextSliceNum += tmp.size();
				for (String s : tmp) {
					segmentQ.add(s);
				}
				tmp.clear();
				sliceIndex++;
			}
			// Start the next cutting
			i++;
			sliceNum = nextSliceNum;
		}
		ArrayList<String> segments = new ArrayList<String>();
		while (!segmentQ.isEmpty()) {
			segments.add(segmentQ.poll());
		}
		return segments;
	}

	public void breakSegment(ArrayList<String> segments, String text,
			String breaker) {
		if (breaker.equals(",") || breaker.equals(";") || breaker.equals(")")) { // only
																					// these
																					// two
																					// punctuations
			breakSegmentPunctuation(segments, text, breaker);
		} else {
			breakSegmentConjunction(segments, text, breaker);
		}
	}

	// For punctuation, we want the punctuation to follow the followed segment
	// (To keep the sentence as it is when we recover the original sentence)
	public void breakSegmentPunctuation(ArrayList<String> segments,
			String text, String breaker) {
		int loc = 0;
		String segment = "";
		while (loc < text.length() && loc != -1) {
			int startIndex = 1; // avoid the first match
			if (loc != 0)
				startIndex = loc;
			int nextLoc = text.indexOf(breaker, startIndex); // we have already
																// moved
			// loc to after the
			// punctuation

			if (nextLoc == -1 || nextLoc == text.length() - breaker.length()) {
				segment = text.substring(loc); // haven't found any, so just
												// start from the loc
				loc = -1;
			} else {
				segment = text.substring(loc, nextLoc + 1).trim(); // contains
																	// the
																	// punctuation
				loc = nextLoc + 1; // It is punctuation, just one character, so
									// only needs to move the loc
			}
			segments.add(segment);
		}
	}

	// For conjunction words, we want the conjunction to go with the following
	// segment
	public void breakSegmentConjunction(ArrayList<String> segments,
			String text, String breaker) {
		int loc = 0;
		String segment = "";
		while (loc < text.length() && loc != -1) {
			int startIndex = 0;

			startIndex = loc + breaker.length(); // If we have found one, we
													// don't want to still
													// search the found one
			int nextLoc = text.indexOf(breaker, startIndex);

			if (nextLoc == -1 || nextLoc == text.length() - breaker.length()) {
				// It is the end of the breaker
				segment = text.substring(loc);
				loc = -1; // tell the program to jump out of the loop
			} else {
				segment = text.substring(loc, nextLoc).trim();
				loc = nextLoc;// loc still starts from the found breaker as the
								// breaker would go with the following segment
			}
			segments.add(segment);
		}
	}

}
