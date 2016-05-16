package edu.pitt.cs.revision.purpose;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.maltparserx.core.helper.HashSet;

import edu.pitt.cs.revision.purpose.pdtb.PipeUnit;
import edu.pitt.lrdc.cs.revision.io.MyLogger;
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
	String elementType;
	boolean isParallel;

	public String getElementType() {
		return elementType;
	}

	public void setElementType(String elementType) {
		this.elementType = elementType;
	}

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

class ImpactStruct {
	private double entRelRatio;
	private double altLexRatio;
	private double comparisonRatio;
	private double contingencyRatio;
	private double expansionRatio;
	private double temporalRatio;

	public double getEntRelRatio() {
		return entRelRatio;
	}

	public void setEntRelRatio(double entRelRatio) {
		this.entRelRatio = entRelRatio;
	}

	public double getAltLexRatio() {
		return altLexRatio;
	}

	public void setAltLexRatio(double altLexRatio) {
		this.altLexRatio = altLexRatio;
	}

	public double getComparisonRatio() {
		return comparisonRatio;
	}

	public void setComparisonRatio(double comparisonRatio) {
		this.comparisonRatio = comparisonRatio;
	}

	public double getContingencyRatio() {
		return contingencyRatio;
	}

	public void setContingencyRatio(double contingencyRatio) {
		this.contingencyRatio = contingencyRatio;
	}

	public double getExpansionRatio() {
		return expansionRatio;
	}

	public void setExpansionRatio(double expansionRatio) {
		this.expansionRatio = expansionRatio;
	}

	public double getTemporalRatio() {
		return temporalRatio;
	}

	public void setTemporalRatio(double temporalRatio) {
		this.temporalRatio = temporalRatio;
	}
}

class Group {
	HashSet<Integer> nodes = new HashSet<Integer>();

	public boolean hasNode(int nodeIndex) {
		return nodes.contains(nodeIndex);
	}

	public HashSet<Integer> getNodes() {
		return nodes;
	}

	public void addNode(int index) {
		nodes.add(index);
	}

	// group relations
	HashSet<String> relations = new HashSet<String>();

	public void addRelation(String key) {
		relations.add(key);
	}

	public HashSet<String> getRelations() {
		return relations;
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

	ImpactStruct[][] impactMatrix;
	ImpactStruct[][] inGroupMatrix;
	ImpactStruct[][] outGroupMatrix;
	Hashtable<Integer, Integer> sentenceMap = new Hashtable<Integer, Integer>();

	
	
	/**
	 * 6-dimension: EntRel/AltLex/Comparison/Contingency/Expansion/Temporal
	 * 
	 * @param sentenceIndex
	 * @return
	 */
	public Hashtable<String, Double> getValueArg1(int sentenceIndex) {
		if (sentenceMap.get(sentenceIndex) == null) {
			System.err.println(sentenceIndex);
			System.err.println("SENTENCE MAP:");
			Iterator<Integer> it = sentenceMap.keySet().iterator();
			while (it.hasNext()) {
				int i = it.next();
				System.err.print(i + ":" + sentenceMap.get(i) + "\t");
			}
			System.err.println();
		}
		int index = sentenceMap.get(sentenceIndex);
		Hashtable<String, Double> values = new Hashtable<String, Double>();
		values.put("EntRel", 0.0);
		values.put("AltLex", 0.0);
		values.put("Comparison", 0.0);
		values.put("Contingency", 0.0);
		values.put("Expansion", 0.0);
		values.put("Temporal", 0.0);
		for (int j = index; j < impactMatrix.length; j++) {
			ImpactStruct impact = impactMatrix[index][j];
			if (impact.getEntRelRatio() > values.get("EntRel")) {
				values.put("EntRel", impact.getEntRelRatio());
			}
			if (impact.getAltLexRatio() > values.get("AltLex")) {
				values.put("AltLex", impact.getAltLexRatio());
			}
			if (impact.getComparisonRatio() > values.get("Comparison")) {
				values.put("Comparison", impact.getComparisonRatio());
			}
			if (impact.getContingencyRatio() > values.get("Contingency")) {
				values.put("Contingency", impact.getContingencyRatio());
			}
			if (impact.getExpansionRatio() > values.get("Expansion")) {
				values.put("Expansion", impact.getExpansionRatio());
			}
			if (impact.getTemporalRatio() > values.get("Temporal")) {
				values.put("Temporal", impact.getTemporalRatio());
			}
		}

		return values;
	}

	
	/**
	 * 6-dimension: EntRel/AltLex/Comparison/Contingency/Expansion/Temporal
	 * 
	 * @param sentenceIndex
	 * @return
	 */
	public Hashtable<String, Double> getOutGroupValueArg1(int sentenceIndex) {
		if (sentenceMap.get(sentenceIndex) == null) {
			System.err.println(sentenceIndex);
			System.err.println("SENTENCE MAP:");
			Iterator<Integer> it = sentenceMap.keySet().iterator();
			while (it.hasNext()) {
				int i = it.next();
				System.err.print(i + ":" + sentenceMap.get(i) + "\t");
			}
			System.err.println();
		}
		int index = sentenceMap.get(sentenceIndex);
		Hashtable<String, Double> values = new Hashtable<String, Double>();
		values.put("EntRel", 0.0);
		values.put("AltLex", 0.0);
		values.put("Comparison", 0.0);
		values.put("Contingency", 0.0);
		values.put("Expansion", 0.0);
		values.put("Temporal", 0.0);
		for (int j = index; j < outGroupMatrix.length; j++) {
			ImpactStruct impact = outGroupMatrix[index][j];
			if (impact.getEntRelRatio() > values.get("EntRel")) {
				values.put("EntRel", impact.getEntRelRatio());
			}
			if (impact.getAltLexRatio() > values.get("AltLex")) {
				values.put("AltLex", impact.getAltLexRatio());
			}
			if (impact.getComparisonRatio() > values.get("Comparison")) {
				values.put("Comparison", impact.getComparisonRatio());
			}
			if (impact.getContingencyRatio() > values.get("Contingency")) {
				values.put("Contingency", impact.getContingencyRatio());
			}
			if (impact.getExpansionRatio() > values.get("Expansion")) {
				values.put("Expansion", impact.getExpansionRatio());
			}
			if (impact.getTemporalRatio() > values.get("Temporal")) {
				values.put("Temporal", impact.getTemporalRatio());
			}
		}

		return values;
	}
	
	
	
	/**
	 * 6-dimension: EntRel/AltLex/Comparison/Contingency/Expansion/Temporal
	 * 
	 * @param sentenceIndex
	 * @return
	 */
	public Hashtable<String, Double> getValueArg2(int sentenceIndex) {
		if (sentenceMap.get(sentenceIndex) == null) {
			System.err.println(sentenceIndex);
			System.err.println("SENTENCE MAP:");
			Iterator<Integer> it = sentenceMap.keySet().iterator();
			while (it.hasNext()) {
				int i = it.next();
				System.err.print(i + ":" + sentenceMap.get(i) + "\t");
			}
			System.err.println();
		}
		int index = sentenceMap.get(sentenceIndex);
		Hashtable<String, Double> values = new Hashtable<String, Double>();
		values.put("EntRel", 0.0);
		values.put("AltLex", 0.0);
		values.put("Comparison", 0.0);
		values.put("Contingency", 0.0);
		values.put("Expansion", 0.0);
		values.put("Temporal", 0.0);
		for (int j = 0; j <= index; j++) {
			ImpactStruct impact = impactMatrix[j][index];
			if (impact.getEntRelRatio() > values.get("EntRel")) {
				values.put("EntRel", impact.getEntRelRatio());
			}
			if (impact.getAltLexRatio() > values.get("AltLex")) {
				values.put("AltLex", impact.getAltLexRatio());
			}
			if (impact.getComparisonRatio() > values.get("Comparison")) {
				values.put("Comparison", impact.getComparisonRatio());
			}
			if (impact.getContingencyRatio() > values.get("Contingency")) {
				values.put("Contingency", impact.getContingencyRatio());
			}
			if (impact.getExpansionRatio() > values.get("Expansion")) {
				values.put("Expansion", impact.getExpansionRatio());
			}
			if (impact.getTemporalRatio() > values.get("Temporal")) {
				values.put("Temporal", impact.getTemporalRatio());
			}
		}

		String logStr = "";
		Iterator<String> it = values.keySet().iterator();
		while (it.hasNext()) {
			String key = it.next();
			double value = values.get(key);
			logStr += key + ":" + value + ",";
		}
		//MyLogger.getInstance().log(sentenceIndex + ":" + logStr);
		return values;
	}

	/**
	 * 6-dimension: EntRel/AltLex/Comparison/Contingency/Expansion/Temporal
	 * 
	 * @param sentenceIndex
	 * @return
	 */
	public Hashtable<String, Double> getOutGroupValueArg2(int sentenceIndex) {
		if (sentenceMap.get(sentenceIndex) == null) {
			System.err.println(sentenceIndex);
			System.err.println("SENTENCE MAP:");
			Iterator<Integer> it = sentenceMap.keySet().iterator();
			while (it.hasNext()) {
				int i = it.next();
				System.err.print(i + ":" + sentenceMap.get(i) + "\t");
			}
			System.err.println();
		}
		int index = sentenceMap.get(sentenceIndex);
		Hashtable<String, Double> values = new Hashtable<String, Double>();
		values.put("EntRel", 0.0);
		values.put("AltLex", 0.0);
		values.put("Comparison", 0.0);
		values.put("Contingency", 0.0);
		values.put("Expansion", 0.0);
		values.put("Temporal", 0.0);
		for (int j = 0; j <= index; j++) {
			ImpactStruct impact = outGroupMatrix[j][index];
			if (impact.getEntRelRatio() > values.get("EntRel")) {
				values.put("EntRel", impact.getEntRelRatio());
			}
			if (impact.getAltLexRatio() > values.get("AltLex")) {
				values.put("AltLex", impact.getAltLexRatio());
			}
			if (impact.getComparisonRatio() > values.get("Comparison")) {
				values.put("Comparison", impact.getComparisonRatio());
			}
			if (impact.getContingencyRatio() > values.get("Contingency")) {
				values.put("Contingency", impact.getContingencyRatio());
			}
			if (impact.getExpansionRatio() > values.get("Expansion")) {
				values.put("Expansion", impact.getExpansionRatio());
			}
			if (impact.getTemporalRatio() > values.get("Temporal")) {
				values.put("Temporal", impact.getTemporalRatio());
			}
		}

		String logStr = "";
		Iterator<String> it = values.keySet().iterator();
		while (it.hasNext()) {
			String key = it.next();
			double value = values.get(key);
			logStr += key + ":" + value + ",";
		}
		//MyLogger.getInstance().log(sentenceIndex + ":" + logStr);
		return values;
	}

	
	
	public void constructGroupAndRelations(Hashtable<Integer, Group> groupIndex) {
		Group currentGroup = new Group();
		for (int i = startSentenceIndex; i <= endSentenceIndex; i++) {
			if (i == endSentenceIndex) {
				currentGroup.addNode(i);
				groupIndex.put(i, currentGroup);
			} else {
				String key = i + "-" + (i + 1);
				if (!relationIndex.containsKey(key)) { // No connection between
														// the two
					currentGroup.addNode(i);
					groupIndex.put(i, currentGroup);
					currentGroup = new Group();
				} else {
					if (!relationIndex.get(key).isParallel()) { // explicit
																// difference
																// between the
																// two
						currentGroup.addNode(i);
						groupIndex.put(i, currentGroup);
						currentGroup.addRelation(key);
						currentGroup = new Group();
					} else {
						currentGroup.addNode(i);
						groupIndex.put(i, currentGroup);
					}
				}
			}
		}
		Iterator<String> it = relationIndex.keySet().iterator();
		while (it.hasNext()) {
			String key = it.next();
			String[] splits = key.split("-");
			int start = Integer.parseInt(splits[0]);
			int end = Integer.parseInt(splits[1]);
			if (end - start > 1 && !relationIndex.get(key).isParallel()) {
				if (groupIndex.get(start).equals(groupIndex.get(end))) {
					Group newGroup = new Group();
					for (int i = start + 1; i <= end; i++) {
						newGroup.addNode(i);
						groupIndex.get(start).getNodes().remove(i);
						groupIndex.put(i, newGroup);
					}
				}
				groupIndex.get(start).addRelation(key);
			}
		}
	}

	public void constructGroupAndRelationsAlpha(
			Hashtable<Integer, Group> groupIndex) {

		Iterator<String> it = relationIndex.keySet().iterator();
		while (it.hasNext()) {
			String key = it.next();
			if (relationIndex.get(key).isParallel()) {
				String[] splits = key.split("-");
				int start = Integer.parseInt(splits[0]);
				int end = Integer.parseInt(splits[1]);
				Group group1 = groupIndex.get(start);
				Group group2 = groupIndex.get(end);
				if (group1 == null && group2 == null) {
					Group group = new Group();
					group.addNode(start);
					group.addNode(end);
					groupIndex.put(start, group);
					groupIndex.put(end, group);
				} else if (group1 == null && group2 != null) {
					group2.addNode(start);
					groupIndex.put(start, group2);
				} else if (group1 != null && group2 == null) {
					group1.addNode(end);
					groupIndex.put(end, group1);
				} else {
					if (group1.equals(group2)) {
						// do nothing
					} else {
						// merge group2 to group1
						HashSet<Integer> nodes = group2.getNodes();
						for (Integer node : nodes) {
							group1.addNode(node);
							groupIndex.put(node, group1);
						}
						groupIndex.remove(group2);
					}
				}
			}
		}

		// Setting up relations between groups
		it = relationIndex.keySet().iterator();
		while (it.hasNext()) {
			String key = it.next();
			String[] splits = key.split("-");
			int start = Integer.parseInt(splits[0]);
			int end = Integer.parseInt(splits[1]);
			if (!relationIndex.get(key).isParallel()) {
				if (!groupIndex.containsKey(start)) {
					Group newGroup = new Group(); // single group
					newGroup.addNode(start);
					groupIndex.put(start, newGroup);
				}
				if (!groupIndex.containsKey(end)) {
					Group newGroup = new Group();
					newGroup.addNode(end);
					groupIndex.put(end, newGroup);
				}
				if (!relationIndex.get(key).getSense().equals("NoRel")) {
					// splitting groups
					if (groupIndex.get(start).equals(groupIndex.get(end))) {
						Group group = groupIndex.get(start);
						Group newGroup = new Group();
						newGroup.addNode(end);
						Iterator<Integer> nodeIt = group.getNodes().iterator();
						while (nodeIt.hasNext()) {
							int value = nodeIt.next();
							if (!relationIndexEnd.containsKey(value)
									|| relationIndexEnd.get(value).size() == 0
									|| value >= end) {
								newGroup.addNode(value);
								groupIndex.put(value, newGroup);
								nodeIt.remove();
							}
						}

						Iterator<String> relationIt = groupIndex.get(start)
								.getRelations().iterator();
						while (relationIt.hasNext()) {
							String relationKey = relationIt.next();
							int relationStart = Integer.parseInt(relationKey
									.split("-")[0]);
							if (newGroup.getNodes().contains(relationStart)) {
								newGroup.addRelation(relationKey);
								relationIt.remove();
							}
						}
					}
					groupIndex.get(start).addRelation(key);
				}
			}
		}

	}

	public String getMatrixStr(ImpactStruct[][] matrix, int startIndex) {
		int num = matrix.length;
		String str = "";
		for (int i = 0; i < num; i++) {
			for (int j = i; j < num; j++) {
				str += "[" + (startIndex + i) + "," + (startIndex + j) + "]:";
				str += "EntRel:" + matrix[i][j].getEntRelRatio() + ", AltLex:"
						+ matrix[i][j].getAltLexRatio() + ", Comparison:"
						+ matrix[i][j].getComparisonRatio() + ", Contingency:"
						+ matrix[i][j].getContingencyRatio() + ", Expansion:"
						+ matrix[i][j].getExpansionRatio() + ", Temporal: "
						+ matrix[i][j].getTemporalRatio();
				str += "\n";
			}
		}
		return str;
	}

	public void buildMatrix() {
		int num = endSentenceIndex - startSentenceIndex + 1;
		impactMatrix = new ImpactStruct[num][num];
		inGroupMatrix = new ImpactStruct[num][num];
		outGroupMatrix = new ImpactStruct[num][num];
		int index = 0;
		for (int i = startSentenceIndex; i <= endSentenceIndex; i++) {
			sentenceMap.put(i, index);
			index++;
		}
		for (int i = 0; i < num; i++) {
			for (int j = i; j < num; j++) {
				impactMatrix[i][j] = new ImpactStruct();
				inGroupMatrix[i][j] = new ImpactStruct();
				outGroupMatrix[i][j] = new ImpactStruct();
			}
		}

		// Setting up groups
		// index of group and their id
		Hashtable<Integer, Group> groupIndex = new Hashtable<Integer, Group>();
		constructGroupAndRelations(groupIndex);

		for (int i = startSentenceIndex; i <= endSentenceIndex; i++) {
			ImpactStruct struct = impactMatrix[sentenceMap.get(i)][sentenceMap
					.get(i)];
			String ownKey = i + "-" + i;
			if (relationIndex.containsKey(ownKey)) {
				PDTBRelation relation = relationIndex.get(ownKey);
				String tag = relation.getSense();
				if (relation.getElementType() != null
						&& (relation.getElementType().equals("EntRel") || relation
								.getElementType().equals("AltLex"))) {
					tag = relation.getElementType();
				} else {
					tag = relation.getSense();
				}
				setRatio(tag, struct, 1.0);
			}
		}
		// Now setting up the matrix
		for (int i = startSentenceIndex; i <= endSentenceIndex; i++) {
			for (int j = i + 1; j <= endSentenceIndex; j++) {
				if (!groupIndex.containsKey(i)) {
					Group newGroup = new Group();
					newGroup.addNode(i);
					groupIndex.put(i, newGroup);
				}
				if (!groupIndex.containsKey(j)) {
					Group newGroup = new Group();
					newGroup.addNode(j);
					groupIndex.put(j, newGroup);
				}
				List<PDTBRelation> relations = getLink(groupIndex, i, j);
				ImpactStruct struct = impactMatrix[sentenceMap.get(i)][sentenceMap
						.get(j)];
				ImpactStruct inGroupStruct = inGroupMatrix[sentenceMap.get(i)][sentenceMap
				                               						.get(j)];
				ImpactStruct outGroupStruct = outGroupMatrix[sentenceMap.get(i)][sentenceMap
				                               						.get(j)];
				int groupDist = 0;
				int withinGroupDist = 0;
				String tag = "";
				if (relations != null && relations.size() > 0) {
					PDTBRelation start = relations.get(0);
					if (start.getPreIndex() == i) {
						if (start.getElementType().equals("EntRel")
								|| start.getElementType().equals("AltLex")) {
							tag = start.getElementType();
						} else {
							tag = start.getSense();
						}
					}
					double ratio = 1;
					if (tag.equals("NoRel"))
						ratio = 0;
					for (int ri = 0; ri < relations.size(); ri++) {
						PDTBRelation relation = relations.get(ri);
						if (relation.isParallel()) {
							withinGroupDist++;
							if (ri == relations.size() - 1) {
								if (tag.length() > 0) {
									if (groupDist > 0)
										withinGroupDist++;
									if (groupDist == 0)
										groupDist = 1;
									ratio = ratio
											/ (withinGroupDist * groupDist * 1.0);
									if(groupDist>1) ratio = 0;
									setRatio(tag, struct, ratio);
								}
							}
						} else {
							withinGroupDist++;
							ratio = ratio / (withinGroupDist * 1.0); // Calculate
																		// the
																		// distance
																		// within
																		// group
							if (groupDist == 0) { // Only use the first group's
													// type
								if (relation.getElementType() != null
										&& (relation.getElementType().equals(
												"EntRel") || relation
												.getElementType().equals(
														"AltLex"))) {
									tag = relation.getElementType();
								} else {
									tag = relation.getSense();
								}
								if (tag.equals("NoRel"))
									ratio = 0;
								setRatio(tag, outGroupStruct, ratio);
							}
							groupDist++;
							withinGroupDist = 0; // Reset withingroup
							if (ri == relations.size() - 1) {
								if (tag.length() > 0) {
									ratio = ratio / (groupDist * 1.0);
									setRatio(tag, struct, ratio);
								}
							}
						}
					}
				}
			}
		}
		MyLogger.getInstance().log(
				getMatrixStr(impactMatrix, startSentenceIndex) + "\n");
	}

	public void setRatio(String tag, ImpactStruct struct, double ratio) {
		if (tag.equals("Comparison")) {
			struct.setComparisonRatio(ratio);
		} else if (tag.equals("Contingency")) {
			struct.setContingencyRatio(ratio);
		} else if (tag.equals("Expansion")) {
			struct.setExpansionRatio(ratio);
		} else if (tag.equals("Temporal")) {
			struct.setTemporalRatio(ratio);
		} else if (tag.equals("EntRel")) {
			struct.setEntRelRatio(ratio);
		} else if (tag.equals("AltLex")) {
			struct.setAltLexRatio(ratio);
		}
	}

	/**
	 * Get relation chain within one group
	 * 
	 * @param start
	 * @param end
	 * @return
	 */
	public List<PDTBRelation> getParallelRelations(int start, int end) {
		System.err.println("START:" + start + ",END:" + end);
		if (start > end)
			return null;
		if (start == end)
			return new ArrayList<PDTBRelation>();
		List<PDTBRelation> relations = new ArrayList<PDTBRelation>();
		String key = start + "-" + end;
		if (relationIndex.containsKey(key)
				&& relationIndex.get(key).isParallel()) {
			relations.add(relationIndex.get(key));
			return relations;
		} else {
			List<Integer> ends = relationIndexStart.get(start);
			if (ends == null) {
				System.err.println(start);
				System.err.println("Pay attention if this pops up a lot!!!!");
				return null;
			}
			for (Integer nextStart : ends) {
				if (nextStart > start) { //to prevent circling to itself
					String nextKey = start + "-" + nextStart;
					if (relationIndex.get(nextKey).isParallel()) {
						List<PDTBRelation> nextRelation = getParallelRelations(
								nextStart, end);
						if (nextRelation != null) {
							relations.add(relationIndex.get(nextKey));
							relations.addAll(nextRelation);
							return relations;
						}
					}
				}
			}
			return null;
		}
	}

	public List<PDTBRelation> getLink(Hashtable<Integer, Group> groupIndex,
			int start, int end) {
		System.err.println("Start:" + start + ", End:" + end);
		if (start > end) {
			if (groupIndex.get(start).equals(groupIndex.get(end))) {
				return getParallelRelations(end, start);
			} else {
				return null;
			}
		}
		if (start == end) {
			String key = start + "-" + end;
			List<PDTBRelation> relations = new ArrayList<PDTBRelation>();
			if (relationIndex.containsKey(key)) {
				relations.add(relationIndex.get(key));
			}
			return relations;
		}
		String key = start + "-" + end;
		if (relationIndex.containsKey(key)) {
			List<PDTBRelation> relations = new ArrayList<PDTBRelation>();
			PDTBRelation relation = relationIndex.get(key);
			if (!relation.getElementType().equals("NoRel")) {
				relations.add(relation);
				return relations;
			}
		}
		List<PDTBRelation> relations;
		if (groupIndex.get(start).equals(groupIndex.get(end))) {
			// Is parallel
			relations = getParallelRelations(start, end);
			if (relations == null) {
				System.err.println("Something is not correct, START:" + start
						+ ", END:" + end);
			} else {
				return relations;
			}
		} else {
			HashSet<String> groupRelations = groupIndex.get(start)
					.getRelations();
			for (String str : groupRelations) {
				System.err.println("Relation:" + str);
				String[] splits = str.split("-");
				int currentEnd = Integer.parseInt(splits[0]);
				int nextStart = Integer.parseInt(splits[1]);
				if (nextStart == end) {// direct connect
					relations = new ArrayList<PDTBRelation>();
					relations.add(relationIndex.get(str));
					return relations;
				}
				if (start < currentEnd) {
					relations = getParallelRelations(start, currentEnd);
				} else if (start > currentEnd) {
					relations = getParallelRelations(currentEnd, start);
				} else {
					String ownKey = start + "-" + currentEnd;

					relations = new ArrayList<PDTBRelation>(); // is where the
																// path going
																// out
					if (relationIndex.containsKey(ownKey)) {
						relations.add(relationIndex.get(ownKey));
					}
				}
				if (relations != null) {
					List<PDTBRelation> results = getLink(groupIndex, nextStart,
							end);
					if (results != null) {
						relations.addAll(results);
						return relations;
					}
				}
			}
		}
		return null;
	}

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
	Hashtable<Integer, List<Integer>> relationIndexStart = new Hashtable<Integer, List<Integer>>();
	Hashtable<Integer, List<Integer>> relationIndexEnd = new Hashtable<Integer, List<Integer>>();

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("GROUP(");
		for (int i = startSentenceIndex; i <= endSentenceIndex; i++) {
			if (i == endSentenceIndex) {
				sb.append(i);
				sb.append(")");
			} else {
				String key = i + "-" + (i + 1);
				if (!relationIndex.containsKey(key)) {
					sb.append(i);
					sb.append(")");
					sb.append("---" + "NoRel" + "--->" + "Group(");
				} else {
					if (!relationIndex.get(key).isParallel()) {
						sb.append(i);
						sb.append(")");
						sb.append("---"
								+ relationIndex.get(key).getElementType() + ":"
								+ relationIndex.get(key).getSense() + "--->"
								+ "Group(");
					} else {
						sb.append(i);
						sb.append("-");
						sb.append(relationIndex.get(key).getElementType() + ":"
								+ relationIndex.get(key).getSense() + "->");
					}
				}
			}
		}
		Iterator<String> it = relationIndex.keySet().iterator();
		while (it.hasNext()) {
			String key = it.next();
			String[] splits = key.split("-");
			if (Integer.parseInt(splits[1]) - Integer.parseInt(splits[0]) > 1) {
				sb.append("\n");
				sb.append(key + "," + relationIndex.get(key).getElementType()
						+ ":" + relationIndex.get(key).getSense());
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

				PDTBRelation relation = new PDTBRelation();

				relation.setPreIndex(arg1SentenceIndex);
				relation.setPostIndex(arg2SentenceIndex);
				relation.setSense(senseType);
				relation.setElementType(elementType);
				/*
				 * if (elementType.equals("Explicit")) {
				 * relation.setParallel(false); } else {
				 * relation.setParallel(true); }
				 */
				relation.setParallel(true);
				relationIndex.put(arg1SentenceIndex + "-" + arg2SentenceIndex,
						relation);
			} else {
				if (!nodeIndex.containsKey(arg1SentenceIndex)) {
					nodeIndex.put(arg1SentenceIndex, new PDTBNode(
							arg1SentenceIndex, arg1Sentence));
				}
				if (!nodeIndex.containsKey(arg2SentenceIndex)) {
					nodeIndex.put(arg2SentenceIndex, new PDTBNode(
							arg2SentenceIndex, arg2Sentence));
				}

				PDTBRelation relation = new PDTBRelation();

				relation.setPreIndex(arg1SentenceIndex);
				relation.setPostIndex(arg2SentenceIndex);
				relation.setSense(senseType);
				relation.setElementType(elementType);
				/*
				 * if (elementType.equals("Explicit")) {
				 * relation.setParallel(false); } else {
				 * relation.setParallel(true); }
				 */
				relation.setParallel(true);
				String key = arg1SentenceIndex + "-" + arg2SentenceIndex;
				relationIndex.put(key, relation);
				if (!relationIndexStart.containsKey(arg1SentenceIndex)) {
					List<Integer> relationTargets = new ArrayList<Integer>();
					relationTargets.add(arg2SentenceIndex);
					relationIndexStart.put(arg1SentenceIndex, relationTargets);
					System.err.println("Sth added:" + arg1SentenceIndex);
				} else {
					relationIndexStart.get(arg1SentenceIndex).add(
							arg2SentenceIndex);
				}

				if (!relationIndexEnd.containsKey(arg2SentenceIndex)) {
					List<Integer> relationSrc = new ArrayList<Integer>();
					relationSrc.add(arg1SentenceIndex);
					relationIndexEnd.put(arg2SentenceIndex, relationSrc);
					System.err.println("Sth added:" + arg1SentenceIndex);
				} else {
					relationIndexEnd.get(arg2SentenceIndex).add(
							arg1SentenceIndex);
				}
			}
		}
		List<String> segments = segmentText(sentences, doc, isOld);
		for (String key : segments) {
			if (relationIndex.containsKey(key)) {
				relationIndex.get(key).setParallel(false);
			} else {
				/*
				 * String[] indices = key.split("-"); PDTBRelation relation =
				 * new PDTBRelation(); relation.setParallel(false);
				 * relation.setPreIndex(Integer.parseInt(indices[0]));
				 * relation.setPostIndex(Integer.parseInt(indices[1]));
				 * relation.setSense("NoRel"); relation.setElementType("NoRel");
				 * relationIndex.put(key, relation);
				 */
			}
		}

		// build matrix
		buildMatrix();
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
		double avg = 0;
		double std = 0;
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
			avg += sim;
		}
		avg = avg/sims.size();
		for(double sim: sims) {
			std += (sim-avg)*(sim-avg);
		}
		std /= sims.size();
		std = Math.sqrt(std);
		double cutoff = (avg-std)/2;
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

			//if (currentSim < cutoff || (currentSim < (avg-std) && currentSim < lastSim && currentSim < nextSim)) {
			if (currentSim < cutoff ||((currentSim < lastSim  && currentSim < nextSim))) {
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
						valueIndex.put(i, weight);
					}
				}
			}
		}
		if (nextLayer.size() > 0)
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
