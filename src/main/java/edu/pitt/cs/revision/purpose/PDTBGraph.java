package edu.pitt.cs.revision.purpose;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.maltparserx.core.helper.HashSet;

import edu.pitt.cs.revision.purpose.pdtb.PipeUnit;
import edu.pitt.lrdc.cs.revision.model.RevisionDocument;

/**
 * Not going to use this one
 * 
 * @author zhangfan
 *
 */
class PDTBRelationStruct {
	PDTBRelation relation;
	int level;

	public PDTBRelation getRelation() {
		return relation;
	}

	public void setRelation(PDTBRelation relation) {
		this.relation = relation;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public PDTBRelationStruct() {
	}

	public PDTBRelationStruct(PDTBRelation relation, int level) {
		this.relation = relation;
		this.level = level;
	}
}

class PDTBRelation {
	int preIndex;
	int postIndex;
	String sense;
	boolean isParallel;

	public int getPreIndex() {
		return preIndex;
	}

	public void setPreIndex(int preIndex) {
		this.preIndex = preIndex;
	}

	public int getPostIndex() {
		return postIndex;
	}

	public void setPostIndex(int postIndex) {
		this.postIndex = postIndex;
	}

	public String getSense() {
		return sense;
	}

	public void setSense(String sense) {
		this.sense = sense;
	}

	public boolean isParallel() {
		return isParallel;
	}

	public void setParallel(boolean isParallel) {
		this.isParallel = isParallel;
	}
}

class PDTBNode {
	int sentenceIndex;
	String sentence;
	String argType = null;

	public int getSentenceIndex() {
		return sentenceIndex;
	}

	public void setSentenceIndex(int sentenceIndex) {
		this.sentenceIndex = sentenceIndex;
	}

	public String getSentence() {
		return sentence;
	}

	public void setSentence(String sentence) {
		this.sentence = sentence;
	}

	public String getArgType() {
		return argType;
	}

	public void setArgType(String argType) {
		this.argType = argType;
	}

	public boolean isMerged() {
		return isMerged;
	}

	public void setMerged(boolean isMerged) {
		this.isMerged = isMerged;
	}

	public void setRelations(List<PDTBRelation> relations) {
		this.relations = relations;
	}

	boolean isMerged;
	List<PDTBRelation> relations = new ArrayList<PDTBRelation>();

	public List<PDTBRelation> getRelations() {
		return relations;
	}

	public PDTBNode(int sentenceIndex, String sentence) {
		this.sentenceIndex = sentenceIndex;
		this.sentence = sentence;
	}

	public PDTBNode() {

	}
}

/**
 * Each graph is for a paragraph in the essay (one draft)
 * 
 * @author zhangfan
 *
 */
public class PDTBGraph {
	int startSentenceIndex;
	int endSentenceIndex;

	public int getStartSentenceIndex() {
		return startSentenceIndex;
	}

	public void setStartSentenceIndex(int startSentenceIndex) {
		this.startSentenceIndex = startSentenceIndex;
	}

	public int getEndSentenceIndex() {
		return endSentenceIndex;
	}

	public void setEndSentenceIndex(int endSentenceIndex) {
		this.endSentenceIndex = endSentenceIndex;
	}

	public Hashtable<Integer, PDTBNode> getNodeIndex() {
		return nodeIndex;
	}

	public void setNodeIndex(Hashtable<Integer, PDTBNode> nodeIndex) {
		this.nodeIndex = nodeIndex;
	}

	public Hashtable<String, PDTBRelation> getRelationIndex() {
		return relationIndex;
	}

	public void setRelationIndex(Hashtable<String, PDTBRelation> relationIndex) {
		this.relationIndex = relationIndex;
	}

	Hashtable<Integer, PDTBNode> nodeIndex = new Hashtable<Integer, PDTBNode>();
	Hashtable<String, PDTBRelation> relationIndex = new Hashtable<String, PDTBRelation>();

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("GROUP(");
		for (int i = startSentenceIndex; i <= endSentenceIndex; i++) {
			if (i == endSentenceIndex) {
				sb.append(")");
			} else {
				String key = i + "-" + (i + 1);
				if (!relationIndex.containsKey(key)) {
					sb.append(i);
					sb.append(")");
					sb.append("---" + "NoRel"
							+ "--->" + "Group(");
				} else {
					if (!relationIndex.get(key).isParallel()) {
						sb.append(i);
						sb.append(")");
						sb.append("---" + relationIndex.get(key).getSense()
								+ "--->" + "Group(");
					} else {
						sb.append(i);
						sb.append("-");
						sb.append(relationIndex.get(key).getSense() + "->");
					}
				}
			}
		}
		return sb.toString();
	}

	public void buildGraph(List<PipeUnit> units, RevisionDocument doc,
			boolean isOld, List<Integer> sentences) throws IOException {
		for (PipeUnit unit : units) {
			int arg1SentenceIndex = unit.getArg1SentenceIndex();
			int arg2SentenceIndex = unit.getArg2SentenceIndex();
			String elementType = unit.getElementType();
			String senseType = unit.getRelationType();
			String arg1Sentence = "";
			String arg2Sentence = "";
			if (isOld) {
				arg1Sentence = doc.getOldSentence(arg1SentenceIndex);
				arg2Sentence = doc.getOldSentence(arg2SentenceIndex);
			} else {
				arg1Sentence = doc.getNewSentence(arg1SentenceIndex);
				arg2Sentence = doc.getNewSentence(arg2SentenceIndex);
			}
			if (arg1SentenceIndex == arg2SentenceIndex) {
				PDTBNode node = new PDTBNode();
				node.setArgType(senseType);
				node.setSentenceIndex(arg1SentenceIndex);
				if (isOld) {
					node.setSentence(arg1Sentence);
				} else {
					node.setSentence(arg1Sentence);
				}
				if (nodeIndex.containsKey(arg1SentenceIndex))
					nodeIndex.put(arg1SentenceIndex, node);
			} else {
				if (!nodeIndex.containsKey(arg1SentenceIndex)) {
					nodeIndex.put(arg1SentenceIndex, new PDTBNode(
							arg1SentenceIndex, arg1Sentence));
				}
				if (!nodeIndex.containsKey(arg2SentenceIndex)) {
					nodeIndex.put(arg2SentenceIndex, new PDTBNode(
							arg2SentenceIndex, arg2Sentence));
				}
				if (elementType.equals("Explicit")) {
					PDTBRelation relation = new PDTBRelation();
					relation.setParallel(false);
					relation.setPreIndex(arg1SentenceIndex);
					relation.setPostIndex(arg2SentenceIndex);
					relation.setSense(senseType);
					String key = arg1SentenceIndex + "-" + arg2SentenceIndex;
					relationIndex.put(key, relation);

				} else {
					// Try to see if it can be merged
					PDTBRelation relation = new PDTBRelation();
					relation.setParallel(true);
					relation.setPreIndex(arg1SentenceIndex);
					relation.setPostIndex(arg2SentenceIndex);
					relation.setSense(senseType);
					String key = arg1SentenceIndex + "-" + arg2SentenceIndex;
					relationIndex.put(key, relation);
				}
			}
		}
		List<String> segments = segmentText(sentences, doc, isOld);
		for (String key : segments) {
			if (relationIndex.containsKey(key)) {
				relationIndex.get(key).setParallel(false);
			} else {
				String[] indices = key.split("-");
				PDTBRelation relation = new PDTBRelation();
				relation.setParallel(false);
				relation.setPreIndex(Integer.parseInt(indices[0]));
				relation.setPostIndex(Integer.parseInt(indices[1]));
				relation.setSense("NoRel");
				relationIndex.put(key, relation);
			}
		}
	}

	/**
	 * adapted 1-window text-tiling algorithm
	 * 
	 * @param sentences
	 * @param doc
	 * @param isOld
	 * @return
	 * @throws IOException
	 */
	public List<String> segmentText(List<Integer> sentences,
			RevisionDocument doc, boolean isOld) throws IOException {
		List<Double> sims = new ArrayList<Double>();
		List<String> segments = new ArrayList<String>();
		for (int i = 0; i < sentences.size() - 1; i++) {
			String sentence1 = "";
			String sentence2 = "";
			if (isOld) {
				sentence1 = doc.getOldSentence(sentences.get(i));
				sentence2 = doc.getOldSentence(sentences.get(i + 1));
			} else {
				sentence1 = doc.getNewSentence(sentences.get(i));
				sentence2 = doc.getNewSentence(sentences.get(i + 1));
			}

			double sim = SentenceEmbeddingFeatureExtractor.getInstance()
					.calculateSim(sentence1, sentence2);
			sims.add(sim);
		}

		for (int j = 0; j < sims.size(); j++) {
			double lastSim = 0;
			double nextSim = 0;
			double currentSim = sims.get(j);
			if (j == 0) {
				lastSim = 0;
			} else {
				lastSim = sims.get(j - 1);
			}

			if (j == sims.size() - 1) {
				nextSim = 0;
			} else {
				nextSim = sims.get(j + 1);
			}

			if (currentSim < lastSim && currentSim < nextSim) {
				int pre = j;
				int post = j + 1;
				int preIndex = sentences.get(pre);
				int postIndex = sentences.get(post);
				String key = preIndex + "-" + postIndex;
				segments.add(key);
			}
		}
		return segments;
	}

	public List<PDTBRelationStruct> getRelatedRelations(int sentenceIndex) {
		List<PDTBNode> parallelNodes = getParallelNodes(sentenceIndex);

		HashSet<Integer> nodeSet = new HashSet<Integer>();
		for (PDTBNode node : parallelNodes) {
			nodeSet.add(node.getSentenceIndex());
		}

		List<PDTBRelationStruct> preRelations = new ArrayList<PDTBRelationStruct>();
		List<PDTBRelationStruct> postRelations = new ArrayList<PDTBRelationStruct>();
		getRelatedRelationsPre(preRelations, nodeSet, -1);
		getRelatedRelationsPost(postRelations, nodeSet, 1);

		preRelations.addAll(postRelations);
		return preRelations;
	}

	/**
	 * 
	 * @param itemsLayer
	 *            All the sentences of the current layer (calculated as the next
	 *            layer from the last iteration)
	 * @param valueIndex
	 *            Keep track of the weight for each sentence
	 * @param upwards
	 *            whether the direction goes up or downwards
	 */
	public void checkValueForLayer(HashSet<Integer> itemsLayer,
			Hashtable<Integer, Double> valueIndex, boolean upwards) {
		HashSet<Integer> nextLayer = new HashSet<Integer>();
		// The weight of the current layer has been set already in value
		// index
		// Two tasks
		// Task 1: Infer all the parallel nodes and their weights
		// Task 2: Infer all the next layer nodes and their weights
		for (Integer sentenceIndex : itemsLayer) {
			// Infer all the parallel nodes
			double originalWeight = valueIndex.get(sentenceIndex);
			List<PDTBNode> parallelNodes = getParallelNodes(sentenceIndex,
					upwards);
			for (PDTBNode node : parallelNodes) {
				// Setting the parallel node weight
				int index = node.getSentenceIndex();
				int distance = 0;
				if (upwards) {
					distance = sentenceIndex - index;
				} else {
					distance = index - sentenceIndex;
				}
				double weight = 1.0;
				if (distance != 0)
					weight = weight / distance;
				weight = weight * originalWeight;
				if (valueIndex.containsKey(index)
						&& valueIndex.get(index) > weight) {
					// keep the original
				} else {
					valueIndex.put(index, weight);
				}

				// finish setting, search next layer
				List<Integer> nextLevelNodes = getNextLevelNode(index, upwards);
				for (Integer i : nextLevelNodes) {
					nextLayer.add(i);
					if (valueIndex.containsKey(i) && valueIndex.get(i) > weight) {
						// keep the original
					} else {
						valueIndex.put(index, weight);
					}
				}
			}
		}

		checkValueForLayer(nextLayer, valueIndex, upwards);
	}

	/**
	 * Get the weight matrix for the specified sentence
	 * 
	 * @param sentenceIndex
	 * @param upwards
	 * @return
	 */
	public Hashtable<Integer, Double> getValueIndex(int sentenceIndex,
			boolean upwards) {
		// To invoke the checkVlaueForLayer method, first create the
		// starting matrix and the next layer
		List<PDTBNode> parallels = getParallelNodes(sentenceIndex, upwards);
		Hashtable<Integer, Double> valueIndex = new Hashtable<Integer, Double>();
		valueIndex.put(sentenceIndex, 1.0);
		HashSet<Integer> nextLayer = new HashSet<Integer>();
		for (PDTBNode node : parallels) { // Get all parallels
			int index = node.getSentenceIndex();
			int distance = 0;
			if (upwards) {
				distance = sentenceIndex - index;
			} else {
				distance = index - sentenceIndex;
			}
			double weight = 1.0;
			if (distance != 0)
				weight = weight / distance;
			if (!valueIndex.containsKey(index)
					|| valueIndex.get(index) < weight) {
				valueIndex.put(index, weight);
			}
			List<Integer> nextLevelNodes = getNextLevelNode(index, upwards);

			for (Integer i : nextLevelNodes) {
				if (!valueIndex.containsKey(i) || valueIndex.get(i) < weight) {
					valueIndex.put(i, weight);
				}
				nextLayer.add(i);
			}
		}
		checkValueForLayer(nextLayer, valueIndex, upwards);
		return valueIndex;
	}

	public void getRelatedRelationsPre(List<PDTBRelationStruct> relations,
			HashSet<Integer> nodeSet, int currentLayer) {
		Iterator<String> it = relationIndex.keySet().iterator();
		HashSet<Integer> nextSet = new HashSet<Integer>();
		while (it.hasNext()) {
			String key = it.next();
			String[] split = key.split("-");
			int start = Integer.parseInt(split[0]);
			int end = Integer.parseInt(split[1]);

			if (nodeSet.contains(end) && !nodeSet.contains(start)) {
				PDTBRelationStruct newStruct = new PDTBRelationStruct(
						relationIndex.get(key), currentLayer);
				relations.add(newStruct);
				nextSet.add(start);
			}
		}
		getRelatedRelationsPre(relations, nextSet, currentLayer--);
	}

	public void getRelatedRelationsPost(List<PDTBRelationStruct> relations,
			HashSet<Integer> nodeSet, int currentLayer) {
		Iterator<String> it = relationIndex.keySet().iterator();
		HashSet<Integer> nextSet = new HashSet<Integer>();
		while (it.hasNext()) {
			String key = it.next();
			String[] split = key.split("-");
			int start = Integer.parseInt(split[0]);
			int end = Integer.parseInt(split[1]);

			if (nodeSet.contains(start) && !nodeSet.contains(end)) {
				PDTBRelationStruct newStruct = new PDTBRelationStruct(
						relationIndex.get(key), currentLayer);
				relations.add(newStruct);
				nextSet.add(end);
			}
		}
		getRelatedRelationsPost(relations, nextSet, currentLayer++);
	}

	public List<Integer> getNextLevelNode(int sentenceIndex, boolean upwards) {
		List<Integer> nextLevelNodes = new ArrayList<Integer>();
		Iterator<String> it = relationIndex.keySet().iterator();
		while (it.hasNext()) {
			String key = it.next();
			PDTBRelation relation = relationIndex.get(key);
			String[] splits = key.split("-");
			int start = Integer.parseInt(splits[0]);
			int end = Integer.parseInt(splits[1]);
			if (upwards && end == sentenceIndex) {
				if (!relation.isParallel()) {
					nextLevelNodes.add(start);
				}
			} else if (!upwards && start == sentenceIndex) {
				if (!relation.isParallel()) {
					nextLevelNodes.add(end);
				}
			}
		}
		return nextLevelNodes;
	}

	List<PDTBNode> getParallelNodes(int sentenceIndex, boolean upwards) {
		boolean isRelationEnd = false;
		List<PDTBNode> parallelNodes = new ArrayList<PDTBNode>();
		parallelNodes.add(nodeIndex.get(sentenceIndex));// add itself in
		int indent = -1;
		if (!upwards)
			indent = 1;

		int index = sentenceIndex + indent;
		int currentIndex = sentenceIndex;

		while (!isRelationEnd) {
			String key = "";
			if (index < currentIndex) {
				key = index + "-" + currentIndex;
			} else {
				key = currentIndex + "-" + index;
			}

			if (!relationIndex.containsKey(key)) {
				isRelationEnd = true;
			} else {
				PDTBRelation relation = relationIndex.get(key);
				if (relation.isParallel()) {
					parallelNodes.add(nodeIndex.get(index));
					index += indent;
					currentIndex += indent;
				} else {
					isRelationEnd = true;
				}
			}
		}
		return parallelNodes;
	}

	List<PDTBNode> getParallelNodes(int sentenceIndex) {
		boolean isPreRelationEnd = false;
		boolean isPostRelationEnd = false;

		List<PDTBNode> parallelNodes = new ArrayList<PDTBNode>();

		int preIndex = sentenceIndex - 1;
		int postIndex = sentenceIndex + 1;
		int currentIndex = sentenceIndex;

		while (!isPreRelationEnd) {
			String key = Integer.toString(preIndex) + "-"
					+ Integer.toString(currentIndex);
			if (!relationIndex.containsKey(key)) {
				isPreRelationEnd = true;
			} else {
				PDTBRelation relation = relationIndex.get(key);
				if (relation.isParallel()) {
					parallelNodes.add(nodeIndex.get(preIndex));
					preIndex--;
					currentIndex--;
				} else {
					isPreRelationEnd = true;
				}
			}
		}

		currentIndex = sentenceIndex;
		while (!isPostRelationEnd) {
			String key = Integer.toString(currentIndex) + "-"
					+ Integer.toString(postIndex);
			if (!relationIndex.containsKey(key)) {
				isPostRelationEnd = true;
			} else {
				PDTBRelation relation = relationIndex.get(key);
				if (relation.isParallel()) {
					parallelNodes.add(nodeIndex.get(postIndex));
					postIndex++;
					currentIndex++;
				} else {
					isPostRelationEnd = true;
				}
			}
		}

		return parallelNodes;
	}
}
