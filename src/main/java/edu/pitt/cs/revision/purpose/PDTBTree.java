package edu.pitt.cs.revision.purpose;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import edu.pitt.cs.revision.purpose.pdtb.PipeUnit;
import edu.pitt.lrdc.cs.revision.io.MyLogger;
import edu.pitt.lrdc.cs.revision.model.RevisionDocument;

class PDTBTreeNode {
	int sentenceIndex;
	String sentence;
	int startingIndex;
	int endingIndex;

	public String toString() {
		if (startingIndex == endingIndex)
			return "(" + startingIndex + ")";
		else {
			String str = "(";
			for (int i = startingIndex; i <= endingIndex; i++) {
				str += i + ",";
			}
			str = str.substring(0, str.length() - 1);
			str += ")";
			return str;
		}
	}

	public int getStartingIndex() {
		return startingIndex;
	}

	public void setStartingIndex(int startingIndex) {
		this.startingIndex = startingIndex;
	}

	public int getEndingIndex() {
		return endingIndex;
	}

	public void setEndingIndex(int endingIndex) {
		this.endingIndex = endingIndex;
	}

	boolean isLeaf = false;

	public void setLeaf() {
		isLeaf = true;
	}

	public boolean isLeaf() {
		return this.isLeaf;
	}

	public int getSentenceIndex() {
		return sentenceIndex;
	}

	public void setSentenceIndex(int sentenceIndex) {
		this.sentenceIndex = sentenceIndex;
		this.startingIndex = sentenceIndex;
		this.endingIndex = sentenceIndex;
	}

	public String getSentence() {
		return sentence;
	}

	public void setSentence(String sentence) {
		this.sentence = sentence;
	}

	public PDTBTreeNode getLeft() {
		return left;
	}

	public void setLeft(PDTBTreeNode left) {
		this.left = left;
		if (left != null) {
			this.setStartingIndex(left.getStartingIndex());
		}
		if (right == null) {
			if (left != null)
				this.setEndingIndex(left.getEndingIndex());
		}
	}

	public PDTBTreeNode getRight() {
		return right;
	}

	public void setRight(PDTBTreeNode right) {
		this.right = right;
		if (right != null) {
			this.setEndingIndex(right.getEndingIndex());
		}
		if (left == null) {
			if (right != null) {
				this.setStartingIndex(right.getStartingIndex());
			}
		}
	}

	public List<PDTBRelation> getRelations() {
		return relations;
	}

	public void setRelations(List<PDTBRelation> relations) {
		this.relations = relations;
	}

	PDTBTreeNode left;
	PDTBTreeNode right;

	List<PDTBRelation> relations;

	public PDTBTreeNode() {
		relations = new ArrayList<PDTBRelation>();
	}
}

public class PDTBTree {
	PDTBTreeNode rootNode;

	public PDTBTreeNode getRoot() {
		return rootNode;
	}
	
	public PDTBTreeNode getRootNode() {
		return rootNode;
	}

	public void setRootNode(PDTBTreeNode rootNode) {
		this.rootNode = rootNode;
	}

	public Hashtable<String, PDTBRelation> getRelationIndex() {
		return relationIndex;
	}

	public void setRelationIndex(Hashtable<String, PDTBRelation> relationIndex) {
		this.relationIndex = relationIndex;
	}

	public Hashtable<Integer, HashSet<Integer>> getRelationIndexStart() {
		return relationIndexStart;
	}

	public void setRelationIndexStart(
			Hashtable<Integer, HashSet<Integer>> relationIndexStart) {
		this.relationIndexStart = relationIndexStart;
	}

	public Hashtable<Integer, HashSet<Integer>> getRelationIndexEnd() {
		return relationIndexEnd;
	}

	public void setRelationIndexEnd(
			Hashtable<Integer, HashSet<Integer>> relationIndexEnd) {
		this.relationIndexEnd = relationIndexEnd;
	}

	public Hashtable<String, Integer> getRelationLevelInformation() {
		return relationLevelInformation;
	}

	public void setRelationLevelInformation(
			Hashtable<String, Integer> relationLevelInformation) {
		this.relationLevelInformation = relationLevelInformation;
	}

	
	public HashSet<PDTBRelation> getTopLevelRelations(int level) {
		HashSet<PDTBRelation> relations = new HashSet<PDTBRelation>();
		Iterator<String> it = relationLevelInformation.keySet().iterator();
		while(it.hasNext()) {
			String key = it.next();
			int relation = relationLevelInformation.get(key);
			if(relation==level) {
				relations.add(relationIndex.get(key));
			}
		}
		return relations;
	}
	
	private Hashtable<String, PDTBRelation> relationIndex;
	private Hashtable<Integer, HashSet<Integer>> relationIndexStart;
	private Hashtable<Integer, HashSet<Integer>> relationIndexEnd;

	private Hashtable<String, Integer> relationLevelInformation;

	public PDTBTree() {
		relationIndex = new Hashtable<String, PDTBRelation>();
		relationIndexStart = new Hashtable<Integer, HashSet<Integer>>();
		relationIndexEnd = new Hashtable<Integer, HashSet<Integer>>();

		relationLevelInformation = new Hashtable<String, Integer>();
	}

	public void fillInRelationLevelInformation(PDTBTreeNode root, int level) {
		if (root.isLeaf()) {
			int sentenceIndex = root.getSentenceIndex();
			String key = sentenceIndex + "-" + sentenceIndex;
			if(relationIndex.containsKey(key)) {
				relationLevelInformation.put(key, level);
			}
		} else {
			PDTBTreeNode left = root.getLeft();
			PDTBTreeNode right = root.getRight();
			List<PDTBRelation> relations = getRelations(left, right);
			for (PDTBRelation relation : relations) {
				String key = relation.getPreIndex() + "-"
						+ relation.getPostIndex();
				relationLevelInformation.put(key, level);
			}
			fillInRelationLevelInformation(left, level + 1);
			fillInRelationLevelInformation(right, level + 1);
		}
	}

	public int getParents(int sentenceIndex, PDTBTreeNode root,
			Hashtable<Integer, PDTBTreeNode> nodeTable) {
		if (root.isLeaf() && root.getSentenceIndex() == sentenceIndex) {
			return 0;
		} else {
			PDTBTreeNode left = root.getLeft();
			PDTBTreeNode right = root.getRight();
			if (left.getStartingIndex() <= sentenceIndex
					&& left.getEndingIndex() >= sentenceIndex) {
				int level = getParents(sentenceIndex, left, nodeTable);
				nodeTable.put(-(level + 1), root);
				return level + 1;
			} else {
				int level = getParents(sentenceIndex, right, nodeTable);
				nodeTable.put(level + 1, root);
				return level + 1;
			}
		}

	}

	public Hashtable<String, Double> getValueArg1(int sentenceIndex) {
		Hashtable<Integer, PDTBTreeNode> parentTable = new Hashtable<Integer, PDTBTreeNode>();
		getParents(sentenceIndex, rootNode, parentTable);
		Iterator<Integer> it = parentTable.keySet().iterator();
		Hashtable<String, Double> valueMatrix = new Hashtable<String, Double>();
		int levelMax = 0;
		while (it.hasNext()) {
			int l = it.next();
			if (l < levelMax)
				levelMax = l;
		}

		for (int level = -1; level >= levelMax; level--) {
			boolean shouldStop = false;
			PDTBTreeNode node = parentTable.get(level);
			if (node != null) {
				List<PDTBRelation> relations = node.getRelations();
				for (PDTBRelation relation : relations) {
					String relationKey = relation.getPreIndex() + "-"
							+ relation.getPostIndex();
					int relationLevel = relationLevelInformation
							.get(relationKey);
					String key = "NoRel";
					if (relation.getElementType().equals("NoRel")
							|| relation.getSense().equals("NoRel")) {
						key = "NoRel";
						shouldStop = true;
					} else if (relation.getElementType().equals("AltLex")) {
						key = "AltLex";
					} else if (relation.getElementType().equals("EntRel")) {
						key = "EntRel";
					} else {
						if (relation.getSense().trim().length() > 0) {
							key = relation.getSense();
						}
					}
					if (relationLevel > 3)
						relationLevel = 3;
					String newKey = key + "-" + relationLevel;
					double val = 1.0 / (Math.pow(2, (-level) - 1));
					if(relation.getPreIndex()==sentenceIndex) val = 1.0;
					if (!valueMatrix.containsKey(newKey)
							|| valueMatrix.get(newKey) < val)
						valueMatrix.put(newKey, val);
				}
				if (shouldStop)
					break;
			}
		}
		return valueMatrix;
	}

	public Hashtable<String, Double> getValueArg2(int sentenceIndex) {
		Hashtable<Integer, PDTBTreeNode> parentTable = new Hashtable<Integer, PDTBTreeNode>();
		getParents(sentenceIndex, rootNode, parentTable);
		Iterator<Integer> it = parentTable.keySet().iterator();
		int levelMax = 0;
		while (it.hasNext()) {
			int l = it.next();
			if (l > levelMax)
				levelMax = l;
		}
		Hashtable<String, Double> valueMatrix = new Hashtable<String, Double>();
		for (int level = 1; level <= levelMax; level++) {
			boolean shouldStop = false;
			PDTBTreeNode node = parentTable.get(level);
			if (node != null) {
				List<PDTBRelation> relations = node.getRelations();
				for (PDTBRelation relation : relations) {
					String relationKey = relation.getPreIndex() + "-"
							+ relation.getPostIndex();
					int relationLevel = relationLevelInformation
							.get(relationKey);
					String key = "NoRel";
					if (relation.getElementType().equals("NoRel")
							|| relation.getSense().equals("NoRel")) {
						key = "NoRel";
						shouldStop = true;
					} else if (relation.getElementType().equals("AltLex")) {
						key = "AltLex";
					} else if (relation.getElementType().equals("EntRel")) {
						key = "EntRel";
					} else {
						if (relation.getSense().trim().length() > 0) {
							key = relation.getSense();
						}
					}
					if (relationLevel > 3)
						relationLevel = 3;
					String newKey = key + "-" + relationLevel;
					double val = 1.0 / (Math.pow(2, (level - 1)));
					if(relation.getPostIndex() == sentenceIndex) val = 1.0;
					if (!valueMatrix.containsKey(newKey)
							|| valueMatrix.get(newKey) < val)
						valueMatrix.put(newKey, val);

				}
				if (shouldStop)
					break;
			}
		}
		return valueMatrix;
	}

	public void buildTree(List<PipeUnit> units, RevisionDocument doc,
			boolean isOld, List<Integer> sentences) throws IOException {
		for (PipeUnit unit : units) {
			int arg1SentenceIndex = unit.getArg1SentenceIndex();
			int arg2SentenceIndex = unit.getArg2SentenceIndex();
			String elementType = unit.getElementType();
			String senseType = unit.getRelationType();
			PDTBRelation relation = new PDTBRelation();
			relation.setPreIndex(arg1SentenceIndex);
			relation.setPostIndex(arg2SentenceIndex);
			relation.setSense(senseType);
			relation.setElementType(elementType);
			relationIndex.put(arg1SentenceIndex + "-" + arg2SentenceIndex,
					relation);

			HashSet<Integer> relationTargets = null;
			if (!relationIndexStart.containsKey(arg1SentenceIndex)) {
				relationTargets = new HashSet<Integer>();
				relationIndexStart.put(arg1SentenceIndex, relationTargets);
			} else {
				relationTargets = relationIndexStart.get(arg1SentenceIndex);
			}
			relationTargets.add(arg2SentenceIndex);

			HashSet<Integer> relationSources = null;
			if (!relationIndexEnd.containsKey(arg2SentenceIndex)) {
				relationSources = new HashSet<Integer>();
				relationIndexEnd.put(arg2SentenceIndex, relationSources);
			} else {
				relationSources = relationIndexEnd.get(arg2SentenceIndex);
			}
			relationSources.add(arg1SentenceIndex);

		}

		List<PDTBTreeNode> leafNodes = new ArrayList<PDTBTreeNode>();
		for (Integer sentenceIndex : sentences) {
			String sentence = "";
			if (isOld)
				sentence = doc.getOldSentence(sentenceIndex);
			else
				sentence = doc.getNewSentence(sentenceIndex);
			PDTBTreeNode leafNode = new PDTBTreeNode();
			leafNode.setSentenceIndex(sentenceIndex);
			leafNode.setSentence(sentence);
			leafNode.setLeaf();
			leafNodes.add(leafNode);
		}
		rootNode = buildTree(leafNodes);
		String headline = doc.getDocumentName();
		headline += ", isOld: " + isOld;
		headline += "\n";
		MyLogger.getInstance().log(headline + this.toString());

		fillInRelationLevelInformation(rootNode, 1);
		String str = "";
		Iterator<String> relationIt = relationLevelInformation.keySet().iterator();
		while(relationIt.hasNext()) {
			String key = relationIt.next();
			int level = relationLevelInformation.get(key);
			str += key + ":"+ level+"\n";
		}
		MyLogger.getInstance().log(str);
	}

	/**
	 * Build tree
	 * 
	 * @param treeNodes
	 * @return
	 * @throws IOException
	 */
	public PDTBTreeNode buildTree(List<PDTBTreeNode> treeNodes)
			throws IOException {
		if (treeNodes.size() == 1) {
			return treeNodes.get(0);
		} else if (treeNodes.size() == 2) {
			PDTBTreeNode leftNode = treeNodes.get(0);
			PDTBTreeNode rightNode = treeNodes.get(1);
			return mergeNode(leftNode, rightNode);

		} else {
			double max = 0;
			int maxIndex = -1;
			for (int i = 0; i < treeNodes.size() - 1; i++) {
				double val = getSim(treeNodes.get(i), treeNodes.get(i + 1));

				if (val >= max) {
					max = val;
					maxIndex = i;
				}
			}
			PDTBTreeNode leftNode = treeNodes.get(maxIndex);
			PDTBTreeNode rightNode = treeNodes.get(maxIndex + 1);
			PDTBTreeNode newNode = mergeNode(leftNode, rightNode);
			// treeNodes.remove(leftNode);
			treeNodes.remove(rightNode);
			treeNodes.set(maxIndex, newNode);
			// treeNodes.add(newNode);
			return buildTree(treeNodes);
		}
	}

	public String toString() {
		return printTree(rootNode, 0);
	}

	public String printTree(PDTBTreeNode root, int level) {
		String str = "";
		str += root.toString();
		str += "|";
		str += "\n";
		if (!root.isLeaf()) {
			for (int i = 0; i < level; i++) {
				str += "\t";
			}
			PDTBTreeNode left = root.getLeft();
			str += printTree(left, level + 1);
			str += "\n";
			for (int i = 0; i < level; i++) {
				str += "\t";
			}
			PDTBTreeNode right = root.getRight();
			str += printTree(right, level + 1);
			str += "\n";
		}
		return str;
	}

	public PDTBTreeNode mergeNode(PDTBTreeNode left, PDTBTreeNode right) {
		PDTBTreeNode mergeNode = new PDTBTreeNode();
		PDTBTreeNode leftNode, rightNode;
		if (left.getStartingIndex() < right.getStartingIndex()) {
			leftNode = left;
			rightNode = right;
		} else {
			leftNode = right;
			rightNode = left;
		}
		mergeNode.setLeft(leftNode);
		mergeNode.setRight(rightNode);
		mergeNode.setRelations(getRelations(leftNode, rightNode));
		return mergeNode;
	}

	public void getNodesOfTree(PDTBTreeNode root, HashSet<Integer> nodes) {
		if (root == null)
			return;
		if (root.isLeaf()) {
			nodes.add(root.getSentenceIndex());
		} else {
			getNodesOfTree(root.getLeft(), nodes);
			getNodesOfTree(root.getRight(), nodes);
		}
	}

	public List<PDTBRelation> getRelations(PDTBTreeNode left, PDTBTreeNode right) {
		HashSet<Integer> leftNodes = new HashSet<Integer>();
		HashSet<Integer> rightNodes = new HashSet<Integer>();
		getNodesOfTree(left, leftNodes);
		getNodesOfTree(right, rightNodes);
		List<PDTBRelation> relations = new ArrayList<PDTBRelation>();

		for (Integer leftNode : leftNodes) {
			for (Integer rightNode : rightNodes) {
				String key = leftNode + "-" + rightNode;
				if (relationIndex.containsKey(key)) {
					relations.add(relationIndex.get(key));
				}
			}
		}
		return relations;
	}

	public double getSim(PDTBTreeNode node1, PDTBTreeNode node2)
			throws IOException {
		if (node1.isLeaf() && node2.isLeaf()) {
			String sentence1 = node1.getSentence();
			String sentence2 = node2.getSentence();
			double sim = SentenceEmbeddingFeatureExtractor.getInstance()
					.calculateSim(sentence1, sentence2);
			if (sim == 0) {
				System.err.println("SENT1:" + sentence1);
				System.err.println("SENT2:" + sentence2);
			}
			return sim;
		} else {
			if (node1.isLeaf()) {
				double sim1 = getSim(node1, node2.getLeft());
				double sim2 = getSim(node1, node2.getRight());
				double sim = (sim1 + sim2) / 2;
				return sim;
			} else if (node2.isLeaf()) {
				double sim1 = getSim(node1.getLeft(), node2);
				double sim2 = getSim(node1.getRight(), node2);
				return (sim1 + sim2) / 2;
			} else {
				double sim1 = getSim(node1.getLeft(), node2);
				double sim2 = getSim(node1.getRight(), node2);
				return (sim1 + sim2) / 2;
			}
		}
	}
}
