package edu.pitt.cs.revision.purpose;

import java.util.HashSet;
import java.util.Hashtable;

import edu.pitt.cs.revision.machinelearning.FeatureName;

class RelationGrid {
	private boolean isAltLex_ARG1 = false;
	private boolean isAltLex_ARG2 = false;

	private boolean isEntRel_ARG1 = false;
	private boolean isEntRel_ARG2 = false;

	private boolean isComparison_Explicit_ARG1 = false;
	private boolean isComparison_Implicit_ARG1 = false;
	private boolean isComparison_Explicit_ARG2 = false;
	private boolean isComparison_Implicit_ARG2 = false;

	private boolean isContingency_Explicit_ARG1 = false;
	private boolean isContingency_Implicit_ARG1 = false;
	private boolean isContingency_Explicit_ARG2 = false;
	private boolean isContingency_Implicit_ARG2 = false;

	private boolean isExpansion_Explicit_ARG1 = false;
	private boolean isExpansion_Implicit_ARG1 = false;
	private boolean isExpansion_Explicit_ARG2 = false;
	private boolean isExpansion_Implicit_ARG2 = false;

	private boolean isTemporal_Explicit_ARG1 = false;
	private boolean isTemporal_Implicit_ARG1 = false;
	private boolean isTemporal_Explicit_ARG2 = false;
	private boolean isTemporal_Implicit_ARG2 = false;

	public void merge(RelationGrid mergeGrid) {
		this.setAltLex_ARG1((this.isAltLex_ARG1() || mergeGrid.isAltLex_ARG1()));
		this.setAltLex_ARG2((this.isAltLex_ARG2() || mergeGrid.isAltLex_ARG2()));

		this.setEntRel_ARG1((this.isEntRel_ARG1() || mergeGrid.isEntRel_ARG1()));
		this.setEntRel_ARG2((this.isEntRel_ARG2() || mergeGrid.isEntRel_ARG2()));

		this.setComparison_Explicit_ARG1((this.isComparison_Explicit_ARG1() || mergeGrid
				.isComparison_Explicit_ARG1()));
		this.setComparison_Explicit_ARG2((this.isComparison_Explicit_ARG2() || mergeGrid
				.isComparison_Explicit_ARG2()));
		this.setComparison_Implicit_ARG1((this.isComparison_Implicit_ARG1() || mergeGrid
				.isComparison_Implicit_ARG1()));
		this.setComparison_Implicit_ARG2((this.isComparison_Implicit_ARG2() || mergeGrid
				.isComparison_Implicit_ARG2()));

		this.setContingency_Explicit_ARG1((this.isContingency_Explicit_ARG1() || mergeGrid
				.isContingency_Explicit_ARG1()));
		this.setContingency_Explicit_ARG2((this.isContingency_Explicit_ARG2() || mergeGrid
				.isContingency_Explicit_ARG2()));
		this.setContingency_Implicit_ARG1((this.isContingency_Implicit_ARG1() || mergeGrid
				.isContingency_Implicit_ARG1()));
		this.setContingency_Implicit_ARG2((this.isContingency_Implicit_ARG2() || mergeGrid
				.isContingency_Implicit_ARG2()));

		this.setExpansion_Explicit_ARG1((this.isExpansion_Explicit_ARG1() || mergeGrid
				.isExpansion_Explicit_ARG1()));
		this.setExpansion_Explicit_ARG2((this.isExpansion_Explicit_ARG2() || mergeGrid
				.isExpansion_Explicit_ARG2()));
		this.setExpansion_Implicit_ARG1((this.isExpansion_Implicit_ARG1() || mergeGrid
				.isExpansion_Implicit_ARG1()));
		this.setExpansion_Implicit_ARG2((this.isExpansion_Implicit_ARG2() || mergeGrid
				.isExpansion_Implicit_ARG2()));

		this.setTemporal_Explicit_ARG1((this.isTemporal_Explicit_ARG1() || mergeGrid
				.isTemporal_Explicit_ARG1()));
		this.setTemporal_Explicit_ARG2((this.isTemporal_Explicit_ARG2() || mergeGrid
				.isTemporal_Explicit_ARG2()));
		this.setTemporal_Implicit_ARG1((this.isTemporal_Implicit_ARG1() || mergeGrid
				.isTemporal_Implicit_ARG1()));
		this.setTemporal_Implicit_ARG2((this.isTemporal_Implicit_ARG2() || mergeGrid
				.isTemporal_Implicit_ARG2()));
	}

	public boolean isAltLex_ARG1() {
		return isAltLex_ARG1;
	}

	public void setAltLex_ARG1(boolean isAltLex_ARG1) {
		this.isAltLex_ARG2 = isAltLex_ARG1;
	}

	public boolean isAltLex_ARG2() {
		return isAltLex_ARG2;
	}

	public void setAltLex_ARG2(boolean isAltLex_ARG2) {
		this.isAltLex_ARG2 = isAltLex_ARG2;
	}

	public boolean isEntRel_ARG1() {
		return isEntRel_ARG1;
	}

	public void setEntRel_ARG1(boolean isEntRel_ARG1) {
		this.isEntRel_ARG1 = isEntRel_ARG1;
	}

	public boolean isEntRel_ARG2() {
		return isEntRel_ARG2;
	}

	public void setEntRel_ARG2(boolean isEntRel_ARG2) {
		this.isEntRel_ARG2 = isEntRel_ARG2;
	}

	public boolean isComparison_Explicit_ARG1() {
		return isComparison_Explicit_ARG1;
	}

	public void setComparison_Explicit_ARG1(boolean isComparison_Explicit_ARG1) {
		this.isComparison_Explicit_ARG1 = isComparison_Explicit_ARG1;
	}

	public boolean isComparison_Implicit_ARG1() {
		return isComparison_Implicit_ARG1;
	}

	public void setComparison_Implicit_ARG1(boolean isComparison_Implicit_ARG1) {
		this.isComparison_Implicit_ARG1 = isComparison_Implicit_ARG1;
	}

	public boolean isComparison_Explicit_ARG2() {
		return isComparison_Explicit_ARG2;
	}

	public void setComparison_Explicit_ARG2(boolean isComparison_Explicit_ARG2) {
		this.isComparison_Explicit_ARG2 = isComparison_Explicit_ARG2;
	}

	public boolean isComparison_Implicit_ARG2() {
		return isComparison_Implicit_ARG2;
	}

	public void setComparison_Implicit_ARG2(boolean isComparison_Implicit_ARG2) {
		this.isComparison_Implicit_ARG2 = isComparison_Implicit_ARG2;
	}

	public boolean isContingency_Explicit_ARG1() {
		return isContingency_Explicit_ARG1;
	}

	public void setContingency_Explicit_ARG1(boolean isContingency_Explicit_ARG1) {
		this.isContingency_Explicit_ARG1 = isContingency_Explicit_ARG1;
	}

	public boolean isContingency_Implicit_ARG1() {
		return isContingency_Implicit_ARG1;
	}

	public void setContingency_Implicit_ARG1(boolean isContingency_Implicit_ARG1) {
		this.isContingency_Implicit_ARG1 = isContingency_Implicit_ARG1;
	}

	public boolean isContingency_Explicit_ARG2() {
		return isContingency_Explicit_ARG2;
	}

	public void setContingency_Explicit_ARG2(boolean isContingency_Explicit_ARG2) {
		this.isContingency_Explicit_ARG2 = isContingency_Explicit_ARG2;
	}

	public boolean isContingency_Implicit_ARG2() {
		return isContingency_Implicit_ARG2;
	}

	public void setContingency_Implicit_ARG2(boolean isContingency_Implicit_ARG2) {
		this.isContingency_Implicit_ARG2 = isContingency_Implicit_ARG2;
	}

	public boolean isExpansion_Explicit_ARG1() {
		return isExpansion_Explicit_ARG1;
	}

	public void setExpansion_Explicit_ARG1(boolean isExpansion_Explicit_ARG1) {
		this.isExpansion_Explicit_ARG1 = isExpansion_Explicit_ARG1;
	}

	public boolean isExpansion_Implicit_ARG1() {
		return isExpansion_Implicit_ARG1;
	}

	public void setExpansion_Implicit_ARG1(boolean isExpansion_Implicit_ARG1) {
		this.isExpansion_Implicit_ARG1 = isExpansion_Implicit_ARG1;
	}

	public boolean isExpansion_Explicit_ARG2() {
		return isExpansion_Explicit_ARG2;
	}

	public void setExpansion_Explicit_ARG2(boolean isExpansion_Explicit_ARG2) {
		this.isExpansion_Explicit_ARG2 = isExpansion_Explicit_ARG2;
	}

	public boolean isExpansion_Implicit_ARG2() {
		return isExpansion_Implicit_ARG2;
	}

	public void setExpansion_Implicit_ARG2(boolean isExpansion_Implicit_ARG2) {
		this.isExpansion_Implicit_ARG2 = isExpansion_Implicit_ARG2;
	}

	public boolean isTemporal_Explicit_ARG1() {
		return isTemporal_Explicit_ARG1;
	}

	public void setTemporal_Explicit_ARG1(boolean isTemporal_Explicit_ARG1) {
		this.isTemporal_Explicit_ARG1 = isTemporal_Explicit_ARG1;
	}

	public boolean isTemporal_Implicit_ARG1() {
		return isTemporal_Implicit_ARG1;
	}

	public void setTemporal_Implicit_ARG1(boolean isTemporal_Implicit_ARG1) {
		this.isTemporal_Implicit_ARG1 = isTemporal_Implicit_ARG1;
	}

	public boolean isTemporal_Explicit_ARG2() {
		return isTemporal_Explicit_ARG2;
	}

	public void setTemporal_Explicit_ARG2(boolean isTemporal_Explicit_ARG2) {
		this.isTemporal_Explicit_ARG2 = isTemporal_Explicit_ARG2;
	}

	public boolean isTemporal_Implicit_ARG2() {
		return isTemporal_Implicit_ARG2;
	}

	public void setTemporal_Implicit_ARG2(boolean isTemporal_Implicit_ARG2) {
		this.isTemporal_Implicit_ARG2 = isTemporal_Implicit_ARG2;
	}

	public void setValue(String relationType, String senseType, boolean isArg1) {
		if (relationType.equals("EntRel")) {
			if (isArg1)
				this.setEntRel_ARG1(true);
			else
				this.setEntRel_ARG2(true);
		} else if (relationType.equals("AltLex")) {
			if (isArg1)
				this.setAltLex_ARG1(true);
			else
				this.setAltLex_ARG2(true);
		} else if (relationType.equals("Explicit")) {
			if (isArg1) {
				if (senseType.equals("Comparison")) {
					this.setComparison_Explicit_ARG1(true);
				} else if (senseType.equals("Contingency")) {
					this.setContingency_Explicit_ARG1(true);
				} else if (senseType.equals("Expansion")) {
					this.setExpansion_Explicit_ARG1(true);
				} else if (senseType.equals("Temporal")) {
					this.setTemporal_Explicit_ARG1(true);
				}
			} else {
				if (senseType.equals("Comparison")) {
					this.setComparison_Explicit_ARG2(true);
				} else if (senseType.equals("Contingency")) {
					this.setContingency_Explicit_ARG2(true);
				} else if (senseType.equals("Expansion")) {
					this.setExpansion_Explicit_ARG2(true);
				} else if (senseType.equals("Temporal")) {
					this.setTemporal_Explicit_ARG2(true);
				}
			}
		} else if (relationType.equals("Implicit")) {
			if (isArg1) {
				if (senseType.equals("Comparison")) {
					this.setComparison_Implicit_ARG1(true);
				} else if (senseType.equals("Contingency")) {
					this.setContingency_Implicit_ARG1(true);
				} else if (senseType.equals("Expansion")) {
					this.setExpansion_Implicit_ARG1(true);
				} else if (senseType.equals("Temporal")) {
					this.setTemporal_Implicit_ARG1(true);
				}
			} else {
				if (senseType.equals("Comparison")) {
					this.setComparison_Implicit_ARG2(true);
				} else if (senseType.equals("Contingency")) {
					this.setContingency_Implicit_ARG2(true);
				} else if (senseType.equals("Expansion")) {
					this.setExpansion_Implicit_ARG2(true);
				} else if (senseType.equals("Temporal")) {
					this.setTemporal_Implicit_ARG2(true);
				}
			}
		}
	}
}

public class PDTBEntityGrid {
	Hashtable<Integer, RelationGrid> entityOldGrid = new Hashtable<Integer, RelationGrid>();
	Hashtable<Integer, RelationGrid> entityNewGrid = new Hashtable<Integer, RelationGrid>();

	public RelationGrid getGrid(int index, boolean isOld) {
		if(isOld) {
			if(!entityOldGrid.containsKey(index)) {
				RelationGrid rg = new RelationGrid();
				entityOldGrid.put(index, rg);
			}
			return entityOldGrid.get(index);
		} else {
			if(!entityNewGrid.containsKey(index)) {
				RelationGrid rg = new RelationGrid();
				entityNewGrid.put(index, rg);
			}
			return entityNewGrid.get(index);
		}
	}
	

	
	public void fillInVector(FeatureName features, Object[] featureVector,
			String featureName, boolean value) {
		int fIndex = features.getIndex(featureName);
		featureVector[fIndex] = Boolean.toString(value);
	}

	public static void insertFeature(FeatureName features, String tagName) {
		features.insertFeature(tagName + "_OLD_" + "EntRel_ARG1", Boolean.TYPE);
		features.insertFeature(tagName + "_OLD_" + "EntRel_ARG2", Boolean.TYPE);
		features.insertFeature(tagName + "_NEW_" + "EntRel_ARG1", Boolean.TYPE);
		features.insertFeature(tagName + "_NEW_" + "EntRel_ARG2", Boolean.TYPE);

		features.insertFeature(tagName + "_OLD_" + "AltLex_ARG1", Boolean.TYPE);
		features.insertFeature(tagName + "_OLD_" + "AltLex_ARG2", Boolean.TYPE);
		features.insertFeature(tagName + "_NEW_" + "AltLex_ARG1", Boolean.TYPE);
		features.insertFeature(tagName + "_NEW_" + "AltLex_ARG2", Boolean.TYPE);

		features.insertFeature(tagName + "_OLD_" + "Explict_Comparison_ARG1",
				Boolean.TYPE);
		features.insertFeature(tagName + "_OLD_" + "Explict_Comparison_ARG2",
				Boolean.TYPE);
		features.insertFeature(tagName + "_NEW_" + "Explict_Comparison_ARG1",
				Boolean.TYPE);
		features.insertFeature(tagName + "_NEW_" + "Explict_Comparison_ARG2",
				Boolean.TYPE);

		features.insertFeature(tagName + "_OLD_" + "Explict_Contingency_ARG1",
				Boolean.TYPE);
		features.insertFeature(tagName + "_OLD_" + "Explict_Contingency_ARG2",
				Boolean.TYPE);
		features.insertFeature(tagName + "_NEW_" + "Explict_Contingency_ARG1",
				Boolean.TYPE);
		features.insertFeature(tagName + "_NEW_" + "Explict_Contingency_ARG2",
				Boolean.TYPE);

		features.insertFeature(tagName + "_OLD_" + "Explict_Expansion_ARG1",
				Boolean.TYPE);
		features.insertFeature(tagName + "_OLD_" + "Explict_Expansion_ARG2",
				Boolean.TYPE);
		features.insertFeature(tagName + "_NEW_" + "Explict_Expansion_ARG1",
				Boolean.TYPE);
		features.insertFeature(tagName + "_NEW_" + "Explict_Expansion_ARG2",
				Boolean.TYPE);

		features.insertFeature(tagName + "_OLD_" + "Explict_Temporal_ARG1",
				Boolean.TYPE);
		features.insertFeature(tagName + "_OLD_" + "Explict_Temporal_ARG2",
				Boolean.TYPE);
		features.insertFeature(tagName + "_NEW_" + "Explict_Temporal_ARG1",
				Boolean.TYPE);
		features.insertFeature(tagName + "_NEW_" + "Explict_Temporal_ARG2",
				Boolean.TYPE);

		features.insertFeature(tagName + "_OLD_" + "Implict_Comparison_ARG1",
				Boolean.TYPE);
		features.insertFeature(tagName + "_OLD_" + "Implict_Comparison_ARG2",
				Boolean.TYPE);
		features.insertFeature(tagName + "_NEW_" + "Implict_Comparison_ARG1",
				Boolean.TYPE);
		features.insertFeature(tagName + "_NEW_" + "Implict_Comparison_ARG2",
				Boolean.TYPE);

		features.insertFeature(tagName + "_OLD_" + "Implict_Contingency_ARG1",
				Boolean.TYPE);
		features.insertFeature(tagName + "_OLD_" + "Implict_Contingency_ARG2",
				Boolean.TYPE);
		features.insertFeature(tagName + "_NEW_" + "Implict_Contingency_ARG1",
				Boolean.TYPE);
		features.insertFeature(tagName + "_NEW_" + "Implict_Contingency_ARG2",
				Boolean.TYPE);

		features.insertFeature(tagName + "_OLD_" + "Implict_Expansion_ARG1",
				Boolean.TYPE);
		features.insertFeature(tagName + "_OLD_" + "Implict_Expansion_ARG2",
				Boolean.TYPE);
		features.insertFeature(tagName + "_NEW_" + "Implict_Expansion_ARG1",
				Boolean.TYPE);
		features.insertFeature(tagName + "_NEW_" + "Implict_Expansion_ARG2",
				Boolean.TYPE);

		features.insertFeature(tagName + "_OLD_" + "Implict_Temporal_ARG1",
				Boolean.TYPE);
		features.insertFeature(tagName + "_OLD_" + "Implict_Temporal_ARG2",
				Boolean.TYPE);
		features.insertFeature(tagName + "_NEW_" + "Implict_Temporal_ARG1",
				Boolean.TYPE);
		features.insertFeature(tagName + "_NEW_" + "Implict_Temporal_ARG2",
				Boolean.TYPE);
	}

	public void setValue(FeatureName features, Object[] featureVector,
			String tagName, int index, boolean isOld) {
		RelationGrid grid = null;
		String start = tagName;
		if(isOld) {
			grid = entityOldGrid.get(index);
			start = start + "_OLD_";
		} else {
			grid = entityNewGrid.get(index);
			start = start + "_NEW_";
		}
		
		fillInVector(features, featureVector, start+"EntRel_ARG1", grid.isEntRel_ARG1());
		fillInVector(features, featureVector, start+"EntRel_ARG2", grid.isEntRel_ARG2());
		
		fillInVector(features, featureVector, start+"AltLex_ARG1", grid.isAltLex_ARG1());
		fillInVector(features, featureVector, start+"AltLex_ARG2", grid.isAltLex_ARG2());
		
		fillInVector(features, featureVector, start+"Explict_Comparison_ARG1", grid.isComparison_Explicit_ARG1());
		fillInVector(features, featureVector, start+"Explict_Comparison_ARG2", grid.isComparison_Explicit_ARG2());
		fillInVector(features, featureVector, start+"Implict_Comparison_ARG1", grid.isComparison_Implicit_ARG1());
		fillInVector(features, featureVector, start+"Implict_Comparison_ARG2", grid.isComparison_Implicit_ARG2());
		
		fillInVector(features, featureVector, start+"Explict_Contingency_ARG1", grid.isContingency_Explicit_ARG1());
		fillInVector(features, featureVector, start+"Explict_Contingency_ARG2", grid.isContingency_Explicit_ARG2());
		fillInVector(features, featureVector, start+"Implict_Contingency_ARG1", grid.isContingency_Implicit_ARG1());
		fillInVector(features, featureVector, start+"Implict_Contingency_ARG2", grid.isContingency_Implicit_ARG2());
		
		fillInVector(features, featureVector, start+"Explict_Expansion_ARG1", grid.isExpansion_Explicit_ARG1());
		fillInVector(features, featureVector, start+"Explict_Expansion_ARG2", grid.isExpansion_Explicit_ARG2());
		fillInVector(features, featureVector, start+"Implict_Expansion_ARG1", grid.isExpansion_Implicit_ARG1());
		fillInVector(features, featureVector, start+"Implict_Expansion_ARG2", grid.isExpansion_Implicit_ARG2());
		
		fillInVector(features, featureVector, start+"Explict_Temporal_ARG1", grid.isTemporal_Explicit_ARG1());
		fillInVector(features, featureVector, start+"Explict_Temporal_ARG2", grid.isTemporal_Explicit_ARG2());
		fillInVector(features, featureVector, start+"Implict_Temporal_ARG1", grid.isTemporal_Implicit_ARG1());
		fillInVector(features, featureVector, start+"Implict_Temporal_ARG2", grid.isTemporal_Implicit_ARG2());
	}
}
