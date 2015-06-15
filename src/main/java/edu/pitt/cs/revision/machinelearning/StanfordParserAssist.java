package edu.pitt.cs.revision.machinelearning;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;


import edu.stanford.nlp.ling.CoreAnnotations.NamedEntityTagAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

public class StanfordParserAssist {
	StanfordCoreNLP pipelines; // should be singleton mode
	
	private static StanfordParserAssist instance = null;
	public static StanfordParserAssist getInstance() {
		if(instance == null) {
			instance = new StanfordParserAssist();
		}
		return instance;
	}
	private StanfordParserAssist() {
		pipelines = buildPipeLines();
		// pipelines = buildDefaultPipelines();
	}
	
	public StanfordCoreNLP buildDefaultPipelines() {
		return new StanfordCoreNLP();
	}

	public StanfordCoreNLP buildPipeLines() {
		Properties props = new Properties();
		props.put("annotators", "tokenize,ssplit,pos,lemma,ner,parse");
		return new StanfordCoreNLP(props);
	}

	public List<CoreMap> annotate(String text) {
		Annotation doc = new Annotation(text);
		pipelines.annotate(doc);
		return doc.get(SentencesAnnotation.class);
	}

	public CoreMap annotateSingleSentence(String text) {
		if (text == null || text.trim().equals(""))
			return null;
		return annotate(text).get(0);
	}

	public ArrayList<String> collectNER(CoreMap cm) {
		ArrayList<String> ners = new ArrayList<String>();
		if (cm != null) {
			for (CoreLabel token : cm.get(TokensAnnotation.class)) {
				String word = token.get(TextAnnotation.class);
				String ner = token.get(NamedEntityTagAnnotation.class);
				if (ner.equals("PERSON")) {
					ners.add("PERSON_" + word);
				} else if (ner.equals("LOCATION")) {
					ners.add("LOCATION_" + word);
				}
			}
		}
		return ners;
	}

	/**
	 * Starts with a specific pos tag
	 * 
	 * @param cm
	 * @param tag
	 * @return
	 */
	public boolean startsWith(CoreMap cm, String tag) {
		List<CoreLabel> poses = cm.get(TokensAnnotation.class);
		if (poses.size() != 0) {
			CoreLabel token = poses.get(0);
			String pos = token.get(PartOfSpeechAnnotation.class);
			if (pos.startsWith(tag)) {
				return true;
			}
		}
		return false;
	}

	public boolean patternMatch(ArrayList<String> sequence, String pattern) {
		// No need to write a KMP for this
		String sequenceStr = "";
		for (String seq : sequence) {
			sequenceStr += "<" + seq + ">";
		}

		if (sequenceStr.contains(pattern))
			return true;
		return false;
	}

	public boolean containPattern(CoreMap cm, String tagPattern) {
		ArrayList<String> tags = new ArrayList<String>();
		for (CoreLabel token : cm.get(TokensAnnotation.class)) {
			String pos = token.get(PartOfSpeechAnnotation.class);
			tags.add(pos);
		}
		return patternMatch(tags, tagPattern);
	}

	/**
	 * Simple POS Ratio Only contains N, V, ADJ, ADV
	 */
	public Hashtable<String, Double> collectSimplePOSRatio(CoreMap cm) {
		Hashtable<String, Double> posTable = new Hashtable<String, Double>();
		posTable.put("JJ", 0.0);
		posTable.put("RB", 0.0);
		posTable.put("NN", 0.0);
		posTable.put("VB", 0.0);
		if (cm != null) {
			int total = 0;

			for (CoreLabel token : cm.get(TokensAnnotation.class)) {
				String word = token.get(TextAnnotation.class);
				String pos = token.get(PartOfSpeechAnnotation.class);
				total += 1;
				if (pos.startsWith("JJ")) {
					posTable.put("JJ", posTable.get("JJ") + 1);
				} else if (pos.startsWith("RB")) {
					posTable.put("RB", posTable.get("RB") + 1);
				} else if (pos.startsWith("NN")) {
					posTable.put("NN", posTable.get("NN") + 1);
				} else if (pos.startsWith("VB")) {
					posTable.put("VB", posTable.get("VB") + 1);
				} else {
					total -= 1;
				}
			}

			double jjRatio = posTable.get("JJ") / total;
			double rbRatio = posTable.get("RB") / total;
			double nnRatio = posTable.get("NN") / total;
			double vbRatio = posTable.get("VB") / total;
			// for(String pos: posTable.keySet()) {
			// posTable.put(pos+"_RATIO", posTable.get(pos)/total);
			// }

			posTable.put("JJ_RATIO", jjRatio);
			posTable.put("RB_RATIO", rbRatio);
			posTable.put("NN_RATIO", nnRatio);
			posTable.put("VB_RATIO", vbRatio);
		}
		return posTable;
	}

	public Hashtable<String, Double> collectSimplePOSDiff(CoreMap cm,
			CoreMap cm2) {
		Hashtable<String, Double> posTable = new Hashtable<String, Double>();
		posTable.put("JJ", 0.0);
		posTable.put("RB", 0.0);
		posTable.put("NN", 0.0);
		posTable.put("VB", 0.0);
		if (cm != null) {
			for (CoreLabel token : cm.get(TokensAnnotation.class)) {
				String word = token.get(TextAnnotation.class);
				String pos = token.get(PartOfSpeechAnnotation.class);

				if (pos.startsWith("JJ")) {
					posTable.put("JJ", posTable.get("JJ") + 1);
				} else if (pos.startsWith("RB")) {
					posTable.put("RB", posTable.get("RB") + 1);
				} else if (pos.startsWith("NN")) {
					posTable.put("NN", posTable.get("NN") + 1);
				} else if (pos.startsWith("VB")) {
					posTable.put("VB", posTable.get("VB") + 1);
				} else {
				}
			}
		}
		if (cm2 != null) {
			for (CoreLabel token : cm2.get(TokensAnnotation.class)) {
				String word = token.get(TextAnnotation.class);
				String pos = token.get(PartOfSpeechAnnotation.class);

				if (pos.startsWith("JJ")) {
					posTable.put("JJ", posTable.get("JJ") - 1);
				} else if (pos.startsWith("RB")) {
					posTable.put("RB", posTable.get("RB") - 1);
				} else if (pos.startsWith("NN")) {
					posTable.put("NN", posTable.get("NN") - 1);
				} else if (pos.startsWith("VB")) {
					posTable.put("VB", posTable.get("VB") - 1);
				} else {
				}
			}
		}
		return posTable;
	}

	/**
	 * Get all the content words of the sentence
	 * 
	 * @param cm
	 * @return
	 */
	public Hashtable<String, Integer> getContentWords(CoreMap cm) {
		Hashtable<String, Integer> contentWords = new Hashtable<String, Integer>();
		for (CoreLabel token : cm.get(TokensAnnotation.class)) {
			String word = token.get(TextAnnotation.class);
			String pos = token.get(PartOfSpeechAnnotation.class);
			if (pos.startsWith("JJ") || pos.startsWith("NN")
					|| pos.startsWith("VB")) {
				if (contentWords.containsKey(word)) {
					contentWords.put(word, contentWords.get(word) + 1);
				} else {
					contentWords.put(word, 1);
				}
			}
		}
		return contentWords;
	}

	public static void main(String[] args) {
		// test usage
		String txt = "I have seen Michael jumping through the ultimate hole to another space groundhog playground";
		StanfordParserAssist spa = new StanfordParserAssist();
		CoreMap cm = spa.annotateSingleSentence(txt);
		for (CoreLabel token : cm.get(TokensAnnotation.class)) {
			String word = token.get(TextAnnotation.class);
			String pos = token.get(PartOfSpeechAnnotation.class);
			String ner = token.get(NamedEntityTagAnnotation.class);

			System.out.println(word + ":" + pos + ":" + ner);
		}
	}
}
