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

	private int isAltLex_ARG1_cnt = 0;
	private int isAltLex_ARG2_cnt = 0;

	private int isEntRel_ARG1_cnt = 0;
	private int isEntRel_ARG2_cnt = 0;

	private int isComparison_Explicit_ARG1_cnt = 0;
	private int isComparison_Implicit_ARG1_cnt = 0;
	private int isComparison_Explicit_ARG2_cnt = 0;
	private int isComparison_Implicit_ARG2_cnt = 0;

	private int isContingency_Explicit_ARG1_cnt = 0;
	private int isContingency_Implicit_ARG1_cnt = 0;
	private int isContingency_Explicit_ARG2_cnt = 0;
	private int isContingency_Implicit_ARG2_cnt = 0;

	private int isExpansion_Explicit_ARG1_cnt = 0;
	private int isExpansion_Implicit_ARG1_cnt = 0;
	private int isExpansion_Explicit_ARG2_cnt = 0;
	private int isExpansion_Implicit_ARG2_cnt = 0;

	private int isTemporal_Explicit_ARG1_cnt = 0;
	private int isTemporal_Implicit_ARG1_cnt = 0;
	private int isTemporal_Explicit_ARG2_cnt = 0;
	private int isTemporal_Implicit_ARG2_cnt = 0;

	private double isAltLex_ARG1_distance = 100;
	private double isAltLex_ARG2_distance = 100;

	private double isEntRel_ARG1_distance = 100;
	private double isEntRel_ARG2_distance = 100;

	private double isComparison_Explicit_ARG1_distance = 100;
	private double isComparison_Implicit_ARG1_distance = 100;
	private double isComparison_Explicit_ARG2_distance = 100;
	private double isComparison_Implicit_ARG2_distance = 100;

	private double isContingency_Explicit_ARG1_distance = 100;
	private double isContingency_Implicit_ARG1_distance = 100;
	private double isContingency_Explicit_ARG2_distance = 100;
	private double isContingency_Implicit_ARG2_distance = 100;

	private double isExpansion_Explicit_ARG1_distance = 100;
	private double isExpansion_Implicit_ARG1_distance = 100;
	private double isExpansion_Explicit_ARG2_distance = 100;
	private double isExpansion_Implicit_ARG2_distance = 100;

	private double isTemporal_Explicit_ARG1_distance = 100;
	private double isTemporal_Implicit_ARG1_distance = 100;
	private double isTemporal_Explicit_ARG2_distance = 100;
	private double isTemporal_Implicit_ARG2_distance = 100;

	public double getIsAltLex_ARG1_distance() {
		return isAltLex_ARG1_distance;
	}

	public void setIsAltLex_ARG1_distance(double isAltLex_ARG1_distance) {
		this.isAltLex_ARG1_distance = isAltLex_ARG1_distance;
	}

	public double getIsAltLex_ARG2_distance() {
		return isAltLex_ARG2_distance;
	}

	public void setIsAltLex_ARG2_distance(double isAltLex_ARG2_distance) {
		this.isAltLex_ARG2_distance = isAltLex_ARG2_distance;
	}

	public double getIsEntRel_ARG1_distance() {
		return isEntRel_ARG1_distance;
	}

	public void setIsEntRel_ARG1_distance(double isEntRel_ARG1_distance) {
		this.isEntRel_ARG1_distance = isEntRel_ARG1_distance;
	}

	public double getIsEntRel_ARG2_distance() {
		return isEntRel_ARG2_distance;
	}

	public void setIsEntRel_ARG2_distance(double isEntRel_ARG2_distance) {
		this.isEntRel_ARG2_distance = isEntRel_ARG2_distance;
	}

	public double getIsComparison_Explicit_ARG1_distance() {
		return isComparison_Explicit_ARG1_distance;
	}

	public void setIsComparison_Explicit_ARG1_distance(
			double isComparison_Explicit_ARG1_distance) {
		this.isComparison_Explicit_ARG1_distance = isComparison_Explicit_ARG1_distance;
	}

	public double getIsComparison_Implicit_ARG1_distance() {
		return isComparison_Implicit_ARG1_distance;
	}

	public void setIsComparison_Implicit_ARG1_distance(
			double isComparison_Implicit_ARG1_distance) {
		this.isComparison_Implicit_ARG1_distance = isComparison_Implicit_ARG1_distance;
	}

	public double getIsComparison_Explicit_ARG2_distance() {
		return isComparison_Explicit_ARG2_distance;
	}

	public void setIsComparison_Explicit_ARG2_distance(
			double isComparison_Explicit_ARG2_distance) {
		this.isComparison_Explicit_ARG2_distance = isComparison_Explicit_ARG2_distance;
	}

	public double getIsComparison_Implicit_ARG2_distance() {
		return isComparison_Implicit_ARG2_distance;
	}

	public void setIsComparison_Implicit_ARG2_distance(
			double isComparison_Implicit_ARG2_distance) {
		this.isComparison_Implicit_ARG2_distance = isComparison_Implicit_ARG2_distance;
	}

	public double getIsContingency_Explicit_ARG1_distance() {
		return isContingency_Explicit_ARG1_distance;
	}

	public void setIsContingency_Explicit_ARG1_distance(
			double isContingency_Explicit_ARG1_distance) {
		this.isContingency_Explicit_ARG1_distance = isContingency_Explicit_ARG1_distance;
	}

	public double getIsContingency_Implicit_ARG1_distance() {
		return isContingency_Implicit_ARG1_distance;
	}

	public void setIsContingency_Implicit_ARG1_distance(
			double isContingency_Implicit_ARG1_distance) {
		this.isContingency_Implicit_ARG1_distance = isContingency_Implicit_ARG1_distance;
	}

	public double getIsContingency_Explicit_ARG2_distance() {
		return isContingency_Explicit_ARG2_distance;
	}

	public void setIsContingency_Explicit_ARG2_distance(
			double isContingency_Explicit_ARG2_distance) {
		this.isContingency_Explicit_ARG2_distance = isContingency_Explicit_ARG2_distance;
	}

	public double getIsContingency_Implicit_ARG2_distance() {
		return isContingency_Implicit_ARG2_distance;
	}

	public void setIsContingency_Implicit_ARG2_distance(
			double isContingency_Implicit_ARG2_distance) {
		this.isContingency_Implicit_ARG2_distance = isContingency_Implicit_ARG2_distance;
	}

	public double getIsExpansion_Explicit_ARG1_distance() {
		return isExpansion_Explicit_ARG1_distance;
	}

	public void setIsExpansion_Explicit_ARG1_distance(
			double isExpansion_Explicit_ARG1_distance) {
		this.isExpansion_Explicit_ARG1_distance = isExpansion_Explicit_ARG1_distance;
	}

	public double getIsExpansion_Implicit_ARG1_distance() {
		return isExpansion_Implicit_ARG1_distance;
	}

	public void setIsExpansion_Implicit_ARG1_distance(
			double isExpansion_Implicit_ARG1_distance) {
		this.isExpansion_Implicit_ARG1_distance = isExpansion_Implicit_ARG1_distance;
	}

	public double getIsExpansion_Explicit_ARG2_distance() {
		return isExpansion_Explicit_ARG2_distance;
	}

	public void setIsExpansion_Explicit_ARG2_distance(
			double isExpansion_Explicit_ARG2_distance) {
		this.isExpansion_Explicit_ARG2_distance = isExpansion_Explicit_ARG2_distance;
	}

	public double getIsExpansion_Implicit_ARG2_distance() {
		return isExpansion_Implicit_ARG2_distance;
	}

	public void setIsExpansion_Implicit_ARG2_distance(
			double isExpansion_Implicit_ARG2_distance) {
		this.isExpansion_Implicit_ARG2_distance = isExpansion_Implicit_ARG2_distance;
	}

	public double getIsTemporal_Explicit_ARG1_distance() {
		return isTemporal_Explicit_ARG1_distance;
	}

	public void setIsTemporal_Explicit_ARG1_distance(
			double isTemporal_Explicit_ARG1_distance) {
		this.isTemporal_Explicit_ARG1_distance = isTemporal_Explicit_ARG1_distance;
	}

	public double getIsTemporal_Implicit_ARG1_distance() {
		return isTemporal_Implicit_ARG1_distance;
	}

	public void setIsTemporal_Implicit_ARG1_distance(
			double isTemporal_Implicit_ARG1_distance) {
		this.isTemporal_Implicit_ARG1_distance = isTemporal_Implicit_ARG1_distance;
	}

	public double getIsTemporal_Explicit_ARG2_distance() {
		return isTemporal_Explicit_ARG2_distance;
	}

	public void setIsTemporal_Explicit_ARG2_distance(
			double isTemporal_Explicit_ARG2_distance) {
		this.isTemporal_Explicit_ARG2_distance = isTemporal_Explicit_ARG2_distance;
	}

	public double getIsTemporal_Implicit_ARG2_distance() {
		return isTemporal_Implicit_ARG2_distance;
	}

	public void setIsTemporal_Implicit_ARG2_distance(
			double isTemporal_Implicit_ARG2_distance) {
		this.isTemporal_Implicit_ARG2_distance = isTemporal_Implicit_ARG2_distance;
	}

	public int getIsAltLex_ARG1_cnt() {
		return isAltLex_ARG1_cnt;
	}

	public void setIsAltLex_ARG1_cnt(int isAltLex_ARG1_cnt) {
		this.isAltLex_ARG1_cnt = isAltLex_ARG1_cnt;
	}

	public int getIsAltLex_ARG2_cnt() {
		return isAltLex_ARG2_cnt;
	}

	public void setIsAltLex_ARG2_cnt(int isAltLex_ARG2_cnt) {
		this.isAltLex_ARG2_cnt = isAltLex_ARG2_cnt;
	}

	public int getIsEntRel_ARG1_cnt() {
		return isEntRel_ARG1_cnt;
	}

	public void setIsEntRel_ARG1_cnt(int isEntRel_ARG1_cnt) {
		this.isEntRel_ARG1_cnt = isEntRel_ARG1_cnt;
	}

	public int getIsEntRel_ARG2_cnt() {
		return isEntRel_ARG2_cnt;
	}

	public void setIsEntRel_ARG2_cnt(int isEntRel_ARG2_cnt) {
		this.isEntRel_ARG2_cnt = isEntRel_ARG2_cnt;
	}

	public int getIsComparison_Explicit_ARG1_cnt() {
		return isComparison_Explicit_ARG1_cnt;
	}

	public void setIsComparison_Explicit_ARG1_cnt(
			int isComparison_Explicit_ARG1_cnt) {
		this.isComparison_Explicit_ARG1_cnt = isComparison_Explicit_ARG1_cnt;
	}

	public int getIsComparison_Implicit_ARG1_cnt() {
		return isComparison_Implicit_ARG1_cnt;
	}

	public void setIsComparison_Implicit_ARG1_cnt(
			int isComparison_Implicit_ARG1_cnt) {
		this.isComparison_Implicit_ARG1_cnt = isComparison_Implicit_ARG1_cnt;
	}

	public int getIsComparison_Explicit_ARG2_cnt() {
		return isComparison_Explicit_ARG2_cnt;
	}

	public void setIsComparison_Explicit_ARG2_cnt(
			int isComparison_Explicit_ARG2_cnt) {
		this.isComparison_Explicit_ARG2_cnt = isComparison_Explicit_ARG2_cnt;
	}

	public int getIsComparison_Implicit_ARG2_cnt() {
		return isComparison_Implicit_ARG2_cnt;
	}

	public void setIsComparison_Implicit_ARG2_cnt(
			int isComparison_Implicit_ARG2_cnt) {
		this.isComparison_Implicit_ARG2_cnt = isComparison_Implicit_ARG2_cnt;
	}

	public int getIsContingency_Explicit_ARG1_cnt() {
		return isContingency_Explicit_ARG1_cnt;
	}

	public void setIsContingency_Explicit_ARG1_cnt(
			int isContingency_Explicit_ARG1_cnt) {
		this.isContingency_Explicit_ARG1_cnt = isContingency_Explicit_ARG1_cnt;
	}

	public int getIsContingency_Implicit_ARG1_cnt() {
		return isContingency_Implicit_ARG1_cnt;
	}

	public void setIsContingency_Implicit_ARG1_cnt(
			int isContingency_Implicit_ARG1_cnt) {
		this.isContingency_Implicit_ARG1_cnt = isContingency_Implicit_ARG1_cnt;
	}

	public int getIsContingency_Explicit_ARG2_cnt() {
		return isContingency_Explicit_ARG2_cnt;
	}

	public void setIsContingency_Explicit_ARG2_cnt(
			int isContingency_Explicit_ARG2_cnt) {
		this.isContingency_Explicit_ARG2_cnt = isContingency_Explicit_ARG2_cnt;
	}

	public int getIsContingency_Implicit_ARG2_cnt() {
		return isContingency_Implicit_ARG2_cnt;
	}

	public void setIsContingency_Implicit_ARG2_cnt(
			int isContingency_Implicit_ARG2_cnt) {
		this.isContingency_Implicit_ARG2_cnt = isContingency_Implicit_ARG2_cnt;
	}

	public int getIsExpansion_Explicit_ARG1_cnt() {
		return isExpansion_Explicit_ARG1_cnt;
	}

	public void setIsExpansion_Explicit_ARG1_cnt(
			int isExpansion_Explicit_ARG1_cnt) {
		this.isExpansion_Explicit_ARG1_cnt = isExpansion_Explicit_ARG1_cnt;
	}

	public int getIsExpansion_Implicit_ARG1_cnt() {
		return isExpansion_Implicit_ARG1_cnt;
	}

	public void setIsExpansion_Implicit_ARG1_cnt(
			int isExpansion_Implicit_ARG1_cnt) {
		this.isExpansion_Implicit_ARG1_cnt = isExpansion_Implicit_ARG1_cnt;
	}

	public int getIsExpansion_Explicit_ARG2_cnt() {
		return isExpansion_Explicit_ARG2_cnt;
	}

	public void setIsExpansion_Explicit_ARG2_cnt(
			int isExpansion_Explicit_ARG2_cnt) {
		this.isExpansion_Explicit_ARG2_cnt = isExpansion_Explicit_ARG2_cnt;
	}

	public int getIsExpansion_Implicit_ARG2_cnt() {
		return isExpansion_Implicit_ARG2_cnt;
	}

	public void setIsExpansion_Implicit_ARG2_cnt(
			int isExpansion_Implicit_ARG2_cnt) {
		this.isExpansion_Implicit_ARG2_cnt = isExpansion_Implicit_ARG2_cnt;
	}

	public int getIsTemporal_Explicit_ARG1_cnt() {
		return isTemporal_Explicit_ARG1_cnt;
	}

	public void setIsTemporal_Explicit_ARG1_cnt(int isTemporal_Explicit_ARG1_cnt) {
		this.isTemporal_Explicit_ARG1_cnt = isTemporal_Explicit_ARG1_cnt;
	}

	public int getIsTemporal_Implicit_ARG1_cnt() {
		return isTemporal_Implicit_ARG1_cnt;
	}

	public void setIsTemporal_Implicit_ARG1_cnt(int isTemporal_Implicit_ARG1_cnt) {
		this.isTemporal_Implicit_ARG1_cnt = isTemporal_Implicit_ARG1_cnt;
	}

	public int getIsTemporal_Explicit_ARG2_cnt() {
		return isTemporal_Explicit_ARG2_cnt;
	}

	public void setIsTemporal_Explicit_ARG2_cnt(int isTemporal_Explicit_ARG2_cnt) {
		this.isTemporal_Explicit_ARG2_cnt = isTemporal_Explicit_ARG2_cnt;
	}

	public int getIsTemporal_Implicit_ARG2_cnt() {
		return isTemporal_Implicit_ARG2_cnt;
	}

	public void setIsTemporal_Implicit_ARG2_cnt(int isTemporal_Implicit_ARG2_cnt) {
		this.isTemporal_Implicit_ARG2_cnt = isTemporal_Implicit_ARG2_cnt;
	}

	public String toString() {
		String str = this.isAltLex_ARG1_cnt + "\t" + this.isAltLex_ARG2_cnt
				+ "\t" + this.isComparison_Explicit_ARG1_cnt + "\t"
				+ this.isComparison_Explicit_ARG2_cnt + "\t"
				+ this.isComparison_Implicit_ARG1_cnt + "\t"
				+ this.isComparison_Implicit_ARG2_cnt + "\t"
				+ this.isContingency_Explicit_ARG1_cnt + "\t"
				+ this.isContingency_Explicit_ARG2_cnt + "\t"
				+ this.isContingency_Implicit_ARG1_cnt + "\t"
				+ this.isContingency_Implicit_ARG2_cnt + "\t"
				+ this.isExpansion_Explicit_ARG1_cnt + "\t"
				+ this.isExpansion_Explicit_ARG2_cnt + "\t"
				+ this.isExpansion_Implicit_ARG1_cnt + "\t"
				+ this.isExpansion_Implicit_ARG2_cnt + "\t"
				+ this.isTemporal_Explicit_ARG1_cnt + "\t"
				+ this.isTemporal_Explicit_ARG2_cnt + "\t"
				+ this.isTemporal_Implicit_ARG1_cnt + "\t"
				+ this.isTemporal_Implicit_ARG2_cnt;
		return str;
	}

	public void merge(RelationGrid mergeGrid) {
		if (mergeGrid.isAltLex_ARG1()) {
			this.setAltLex_ARG1(true);
			this.setIsAltLex_ARG1_cnt(this.getIsAltLex_ARG1_cnt() + 1);
			if (mergeGrid.getIsAltLex_ARG1_distance() < this
					.getIsAltLex_ARG1_distance())
				this.setIsAltLex_ARG1_distance(mergeGrid
						.getIsAltLex_ARG1_distance());
		}
		if (mergeGrid.isAltLex_ARG2()) {
			this.setAltLex_ARG2(true);
			this.setIsAltLex_ARG2_cnt(this.getIsAltLex_ARG2_cnt() + 1);
			if (mergeGrid.getIsAltLex_ARG2_distance() < this
					.getIsAltLex_ARG2_distance()) {
				this.setIsAltLex_ARG2_distance(mergeGrid
						.getIsAltLex_ARG2_distance());
			}
		}

		if (mergeGrid.isEntRel_ARG1()) {
			this.setEntRel_ARG1(true);
			this.setIsEntRel_ARG1_cnt(this.getIsAltLex_ARG1_cnt() + 1);
			if (mergeGrid.getIsEntRel_ARG1_distance() < this
					.getIsEntRel_ARG1_distance()) {
				this.setIsEntRel_ARG1_distance(mergeGrid
						.getIsAltLex_ARG1_distance());
			}
		}
		if (mergeGrid.isEntRel_ARG2()) {
			this.setEntRel_ARG2(true);
			this.setIsEntRel_ARG2_cnt(this.getIsEntRel_ARG2_cnt() + 1);
			if (mergeGrid.getIsEntRel_ARG2_distance() < this
					.getIsEntRel_ARG2_distance()) {
				this.setIsEntRel_ARG2_distance(mergeGrid
						.getIsAltLex_ARG2_distance());
			}
		}

		if (mergeGrid.isComparison_Explicit_ARG1()) {
			this.setComparison_Explicit_ARG1(true);
			this.setIsComparison_Explicit_ARG1_cnt(this
					.getIsComparison_Explicit_ARG1_cnt() + 1);
			if (mergeGrid.getIsComparison_Explicit_ARG1_distance() < this
					.getIsComparison_Explicit_ARG1_distance()) {
				this.setIsComparison_Explicit_ARG1_distance(mergeGrid
						.getIsComparison_Explicit_ARG1_distance());
			}
		}
		if (mergeGrid.isComparison_Explicit_ARG2()) {
			this.setComparison_Explicit_ARG2(true);
			this.setIsComparison_Explicit_ARG2_cnt(this
					.getIsComparison_Explicit_ARG2_cnt() + 1);
			if (mergeGrid.getIsComparison_Explicit_ARG2_distance() < this
					.getIsComparison_Explicit_ARG2_distance()) {
				this.setIsComparison_Explicit_ARG2_distance(mergeGrid
						.getIsComparison_Explicit_ARG2_distance());
			}
		}
		if (mergeGrid.isComparison_Implicit_ARG1()) {
			this.setComparison_Implicit_ARG1(true);
			this.setIsComparison_Implicit_ARG1_cnt(this
					.getIsComparison_Implicit_ARG1_cnt() + 1);
			if (mergeGrid.getIsComparison_Implicit_ARG1_distance() < this
					.getIsComparison_Implicit_ARG1_distance()) {
				this.setIsComparison_Implicit_ARG1_distance(mergeGrid
						.getIsComparison_Implicit_ARG1_distance());
			}
		}
		if (mergeGrid.isComparison_Implicit_ARG2()) {
			this.setComparison_Implicit_ARG2(true);
			this.setIsComparison_Implicit_ARG2_cnt(this
					.getIsComparison_Implicit_ARG2_cnt() + 1);
			if (mergeGrid.getIsComparison_Implicit_ARG2_distance() < this
					.getIsComparison_Implicit_ARG2_distance()) {
				this.setIsComparison_Implicit_ARG2_distance(mergeGrid
						.getIsComparison_Implicit_ARG2_distance());
			}
		}

		if (mergeGrid.isContingency_Explicit_ARG1()) {
			this.setContingency_Explicit_ARG1(true);
			this.setIsContingency_Explicit_ARG1_cnt(this
					.getIsContingency_Explicit_ARG1_cnt() + 1);
			if (mergeGrid.getIsContingency_Explicit_ARG1_distance() < this
					.getIsContingency_Explicit_ARG1_distance()) {
				this.setIsContingency_Explicit_ARG1_distance(mergeGrid
						.getIsContingency_Explicit_ARG1_distance());
			}
		}
		if (mergeGrid.isContingency_Explicit_ARG2()) {
			this.setContingency_Explicit_ARG2(true);
			this.setIsContingency_Explicit_ARG2_cnt(this
					.getIsContingency_Explicit_ARG2_cnt() + 1);
			if (mergeGrid.getIsContingency_Explicit_ARG2_distance() < this
					.getIsContingency_Explicit_ARG2_distance()) {
				this.setIsContingency_Explicit_ARG2_distance(mergeGrid
						.getIsContingency_Implicit_ARG2_distance());
			}
		}
		if (mergeGrid.isContingency_Implicit_ARG1()) {
			this.setContingency_Implicit_ARG1(true);
			this.setIsContingency_Implicit_ARG1_cnt(this
					.getIsContingency_Implicit_ARG1_cnt() + 1);
			if (mergeGrid.getIsContingency_Implicit_ARG1_distance() < this
					.getIsContingency_Implicit_ARG1_distance()) {
				this.setIsContingency_Implicit_ARG1_distance(mergeGrid
						.getIsContingency_Implicit_ARG1_distance());
			}
		}
		if (mergeGrid.isContingency_Implicit_ARG2()) {
			this.setContingency_Implicit_ARG2(true);
			this.setIsContingency_Implicit_ARG2_cnt(this
					.getIsContingency_Implicit_ARG2_cnt() + 1);
			if (mergeGrid.getIsContingency_Implicit_ARG2_distance() < this
					.getIsContingency_Implicit_ARG2_distance()) {
				this.setIsContingency_Implicit_ARG2_distance(mergeGrid
						.getIsContingency_Implicit_ARG2_distance());
			}
		}

		if (mergeGrid.isExpansion_Explicit_ARG1()) {
			this.setExpansion_Explicit_ARG1(true);
			this.setIsExpansion_Explicit_ARG1_cnt(this
					.getIsExpansion_Explicit_ARG1_cnt() + 1);
			if (mergeGrid.getIsExpansion_Explicit_ARG1_distance() < this
					.getIsExpansion_Explicit_ARG1_distance()) {
				this.setIsExpansion_Explicit_ARG1_distance(mergeGrid
						.getIsExpansion_Explicit_ARG1_distance());
			}
		}
		if (mergeGrid.isExpansion_Explicit_ARG2()) {
			this.setExpansion_Explicit_ARG2(true);
			this.setIsExpansion_Explicit_ARG2_cnt(this
					.getIsExpansion_Explicit_ARG2_cnt() + 1);
			if (mergeGrid.getIsExpansion_Explicit_ARG2_distance() < this
					.getIsExpansion_Explicit_ARG2_distance()) {
				this.setIsExpansion_Explicit_ARG2_distance(mergeGrid
						.getIsExpansion_Explicit_ARG2_distance());
			}
		}
		if (mergeGrid.isExpansion_Implicit_ARG1()) {
			this.setExpansion_Implicit_ARG1(true);
			this.setIsExpansion_Implicit_ARG1_cnt(this
					.getIsExpansion_Implicit_ARG1_cnt() + 1);
			if (mergeGrid.getIsExpansion_Implicit_ARG1_distance() < this
					.getIsExpansion_Implicit_ARG1_distance()) {
				this.setIsExpansion_Implicit_ARG1_distance(mergeGrid
						.getIsExpansion_Implicit_ARG1_distance());
			}
		}
		if (mergeGrid.isExpansion_Implicit_ARG2()) {
			this.setExpansion_Implicit_ARG2(true);
			this.setIsExpansion_Implicit_ARG2_cnt(this
					.getIsExpansion_Implicit_ARG2_cnt() + 1);
			if (mergeGrid.getIsExpansion_Implicit_ARG2_distance() < this
					.getIsExpansion_Implicit_ARG2_distance()) {
				this.setIsExpansion_Implicit_ARG2_distance(mergeGrid
						.getIsExpansion_Implicit_ARG2_distance());
			}
		}

		if (mergeGrid.isTemporal_Explicit_ARG1()) {
			this.setTemporal_Explicit_ARG1(true);
			this.setIsTemporal_Explicit_ARG1_cnt(this
					.getIsTemporal_Explicit_ARG1_cnt() + 1);
			if (mergeGrid.getIsTemporal_Explicit_ARG1_distance() < this
					.getIsTemporal_Explicit_ARG1_distance()) {
				this.setIsTemporal_Explicit_ARG1_distance(mergeGrid
						.getIsTemporal_Explicit_ARG1_distance());
			}
		}
		if (mergeGrid.isTemporal_Explicit_ARG2()) {
			this.setTemporal_Explicit_ARG2(true);
			this.setIsTemporal_Explicit_ARG2_cnt(this
					.getIsTemporal_Explicit_ARG2_cnt() + 1);
			if (mergeGrid.getIsTemporal_Explicit_ARG2_distance() < this
					.getIsTemporal_Explicit_ARG2_distance()) {
				this.setIsTemporal_Explicit_ARG2_distance(mergeGrid
						.getIsTemporal_Explicit_ARG2_distance());
			}
		}
		if (mergeGrid.isTemporal_Implicit_ARG1()) {
			this.setTemporal_Implicit_ARG1(true);
			this.setIsTemporal_Implicit_ARG1_cnt(this
					.getIsTemporal_Implicit_ARG1_cnt() + 1);
			if (mergeGrid.getIsTemporal_Implicit_ARG1_distance() < this
					.getIsTemporal_Implicit_ARG1_distance()) {
				this.setIsTemporal_Implicit_ARG1_distance(mergeGrid
						.getIsTemporal_Implicit_ARG1_distance());
			}
		}
		if (mergeGrid.isTemporal_Implicit_ARG2()) {
			this.setTemporal_Implicit_ARG2(true);
			this.setIsTemporal_Implicit_ARG2_cnt(this
					.getIsTemporal_Implicit_ARG2_cnt() + 1);
			if (mergeGrid.getIsTemporal_Implicit_ARG2_distance() < this
					.getIsTemporal_Implicit_ARG2_distance()) {
				this.setIsTemporal_Implicit_ARG2_distance(mergeGrid
						.getIsTemporal_Implicit_ARG2_distance());
			}
		}
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

	public void setValue(String relationType, String senseType, boolean isArg1,
			double distance) {
		if(relationType==null) {
			System.err.println("Wrong output:"+senseType);
			return;
		}
		if (relationType.equals("EntRel")) {
			if (isArg1) {
				this.setEntRel_ARG1(true);
				this.setIsEntRel_ARG1_distance(distance);
			} else {
				this.setEntRel_ARG2(true);
				this.setIsEntRel_ARG2_distance(distance);
			}
		} else if (relationType.equals("AltLex")) {
			if (isArg1) {
				this.setAltLex_ARG1(true);
				this.setIsAltLex_ARG1_distance(distance);
			} else {
				this.setAltLex_ARG2(true);
				this.setIsAltLex_ARG2_distance(distance);
			}
		} else if (relationType.equals("Explicit")) {
			if (isArg1) {
				if (senseType.equals("Comparison")) {
					this.setComparison_Explicit_ARG1(true);
					this.setIsComparison_Explicit_ARG1_distance(distance);
				} else if (senseType.equals("Contingency")) {
					this.setContingency_Explicit_ARG1(true);
					this.setIsContingency_Explicit_ARG1_distance(distance);
				} else if (senseType.equals("Expansion")) {
					this.setExpansion_Explicit_ARG1(true);
					this.setIsExpansion_Explicit_ARG1_distance(distance);
				} else if (senseType.equals("Temporal")) {
					this.setTemporal_Explicit_ARG1(true);
					this.setIsTemporal_Explicit_ARG1_distance(distance);
				}
			} else {
				if (senseType.equals("Comparison")) {
					this.setComparison_Explicit_ARG2(true);
					this.setIsComparison_Explicit_ARG2_distance(distance);
				} else if (senseType.equals("Contingency")) {
					this.setContingency_Explicit_ARG2(true);
					this.setIsContingency_Explicit_ARG2_distance(distance);
				} else if (senseType.equals("Expansion")) {
					this.setExpansion_Explicit_ARG2(true);
					this.setIsExpansion_Explicit_ARG2_distance(distance);
				} else if (senseType.equals("Temporal")) {
					this.setTemporal_Explicit_ARG2(true);
					this.setIsTemporal_Explicit_ARG2_distance(distance);
				}
			}
		} else if (relationType.equals("Implicit")) {
			if (isArg1) {
				if (senseType.equals("Comparison")) {
					this.setComparison_Implicit_ARG1(true);
					this.setIsComparison_Implicit_ARG1_distance(distance);
				} else if (senseType.equals("Contingency")) {
					this.setContingency_Implicit_ARG1(true);
					this.setIsContingency_Implicit_ARG1_distance(distance);
				} else if (senseType.equals("Expansion")) {
					this.setExpansion_Implicit_ARG1(true);
					this.setIsExpansion_Implicit_ARG1_distance(distance);
				} else if (senseType.equals("Temporal")) {
					this.setTemporal_Implicit_ARG1(true);
					this.setIsTemporal_Implicit_ARG1_distance(distance);
				}
			} else {
				if (senseType.equals("Comparison")) {
					this.setComparison_Implicit_ARG2(true);
					this.setIsComparison_Implicit_ARG2_distance(distance);
				} else if (senseType.equals("Contingency")) {
					this.setContingency_Implicit_ARG2(true);
					this.setIsContingency_Implicit_ARG2_distance(distance);
				} else if (senseType.equals("Expansion")) {
					this.setExpansion_Implicit_ARG2(true);
					this.setIsExpansion_Implicit_ARG2_distance(distance);
				} else if (senseType.equals("Temporal")) {
					this.setTemporal_Implicit_ARG2(true);
					this.setIsTemporal_Implicit_ARG2_distance(distance);
				}
			}
		}
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
	private String topic;

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public String getTopic() {
		return this.topic;
	}

	Hashtable<Integer, RelationGrid> entityOldGrid = new Hashtable<Integer, RelationGrid>();
	Hashtable<Integer, RelationGrid> entityNewGrid = new Hashtable<Integer, RelationGrid>();

	public RelationGrid getGrid(int index, boolean isOld) {
		if (isOld) {
			if (!entityOldGrid.containsKey(index)) {
				RelationGrid rg = new RelationGrid();
				entityOldGrid.put(index, rg);
			}
			return entityOldGrid.get(index);
		} else {
			if (!entityNewGrid.containsKey(index)) {
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

	public void fillInVector(FeatureName features, Object[] featureVector,
			String featureName, double value) {
		int fIndex = features.getIndex(featureName);
		featureVector[fIndex] = (double) value;
	}

	public static void insertCountFeature(FeatureName features, String tagName) {
		features.insertFeature(tagName + "_OLD_" + "EntRel_ARG1", Double.TYPE);
		features.insertFeature(tagName + "_OLD_" + "EntRel_ARG2", Double.TYPE);
		features.insertFeature(tagName + "_NEW_" + "EntRel_ARG1", Double.TYPE);
		features.insertFeature(tagName + "_NEW_" + "EntRel_ARG2", Double.TYPE);

		features.insertFeature(tagName + "_OLD_" + "AltLex_ARG1", Double.TYPE);
		features.insertFeature(tagName + "_OLD_" + "AltLex_ARG2", Double.TYPE);
		features.insertFeature(tagName + "_NEW_" + "AltLex_ARG1", Double.TYPE);
		features.insertFeature(tagName + "_NEW_" + "AltLex_ARG2", Double.TYPE);

		features.insertFeature(tagName + "_OLD_" + "Explict_Comparison_ARG1",
				Double.TYPE);
		features.insertFeature(tagName + "_OLD_" + "Explict_Comparison_ARG2",
				Double.TYPE);
		features.insertFeature(tagName + "_NEW_" + "Explict_Comparison_ARG1",
				Double.TYPE);
		features.insertFeature(tagName + "_NEW_" + "Explict_Comparison_ARG2",
				Double.TYPE);

		features.insertFeature(tagName + "_OLD_" + "Explict_Contingency_ARG1",
				Double.TYPE);
		features.insertFeature(tagName + "_OLD_" + "Explict_Contingency_ARG2",
				Double.TYPE);
		features.insertFeature(tagName + "_NEW_" + "Explict_Contingency_ARG1",
				Double.TYPE);
		features.insertFeature(tagName + "_NEW_" + "Explict_Contingency_ARG2",
				Double.TYPE);

		features.insertFeature(tagName + "_OLD_" + "Explict_Expansion_ARG1",
				Double.TYPE);
		features.insertFeature(tagName + "_OLD_" + "Explict_Expansion_ARG2",
				Double.TYPE);
		features.insertFeature(tagName + "_NEW_" + "Explict_Expansion_ARG1",
				Double.TYPE);
		features.insertFeature(tagName + "_NEW_" + "Explict_Expansion_ARG2",
				Double.TYPE);

		features.insertFeature(tagName + "_OLD_" + "Explict_Temporal_ARG1",
				Double.TYPE);
		features.insertFeature(tagName + "_OLD_" + "Explict_Temporal_ARG2",
				Double.TYPE);
		features.insertFeature(tagName + "_NEW_" + "Explict_Temporal_ARG1",
				Double.TYPE);
		features.insertFeature(tagName + "_NEW_" + "Explict_Temporal_ARG2",
				Double.TYPE);

		features.insertFeature(tagName + "_OLD_" + "Implict_Comparison_ARG1",
				Double.TYPE);
		features.insertFeature(tagName + "_OLD_" + "Implict_Comparison_ARG2",
				Double.TYPE);
		features.insertFeature(tagName + "_NEW_" + "Implict_Comparison_ARG1",
				Double.TYPE);
		features.insertFeature(tagName + "_NEW_" + "Implict_Comparison_ARG2",
				Double.TYPE);

		features.insertFeature(tagName + "_OLD_" + "Implict_Contingency_ARG1",
				Double.TYPE);
		features.insertFeature(tagName + "_OLD_" + "Implict_Contingency_ARG2",
				Double.TYPE);
		features.insertFeature(tagName + "_NEW_" + "Implict_Contingency_ARG1",
				Double.TYPE);
		features.insertFeature(tagName + "_NEW_" + "Implict_Contingency_ARG2",
				Double.TYPE);

		features.insertFeature(tagName + "_OLD_" + "Implict_Expansion_ARG1",
				Double.TYPE);
		features.insertFeature(tagName + "_OLD_" + "Implict_Expansion_ARG2",
				Double.TYPE);
		features.insertFeature(tagName + "_NEW_" + "Implict_Expansion_ARG1",
				Double.TYPE);
		features.insertFeature(tagName + "_NEW_" + "Implict_Expansion_ARG2",
				Double.TYPE);

		features.insertFeature(tagName + "_OLD_" + "Implict_Temporal_ARG1",
				Double.TYPE);
		features.insertFeature(tagName + "_OLD_" + "Implict_Temporal_ARG2",
				Double.TYPE);
		features.insertFeature(tagName + "_NEW_" + "Implict_Temporal_ARG1",
				Double.TYPE);
		features.insertFeature(tagName + "_NEW_" + "Implict_Temporal_ARG2",
				Double.TYPE);

	}

	public static void insertFeatureWeight(FeatureName features, String tagName) {
		features.insertFeature(tagName + "_OLD_" + "EntRel_ARG1", Double.TYPE);
		features.insertFeature(tagName + "_OLD_" + "EntRel_ARG2", Double.TYPE);
		features.insertFeature(tagName + "_NEW_" + "EntRel_ARG1", Double.TYPE);
		features.insertFeature(tagName + "_NEW_" + "EntRel_ARG2", Double.TYPE);

		features.insertFeature(tagName + "_OLD_" + "AltLex_ARG1", Double.TYPE);
		features.insertFeature(tagName + "_OLD_" + "AltLex_ARG2", Double.TYPE);
		features.insertFeature(tagName + "_NEW_" + "AltLex_ARG1", Double.TYPE);
		features.insertFeature(tagName + "_NEW_" + "AltLex_ARG2", Double.TYPE);

		features.insertFeature(tagName + "_OLD_" + "Explict_Comparison_ARG1",
				Double.TYPE);
		features.insertFeature(tagName + "_OLD_" + "Explict_Comparison_ARG2",
				Double.TYPE);
		features.insertFeature(tagName + "_NEW_" + "Explict_Comparison_ARG1",
				Double.TYPE);
		features.insertFeature(tagName + "_NEW_" + "Explict_Comparison_ARG2",
				Double.TYPE);

		features.insertFeature(tagName + "_OLD_" + "Explict_Contingency_ARG1",
				Double.TYPE);
		features.insertFeature(tagName + "_OLD_" + "Explict_Contingency_ARG2",
				Double.TYPE);
		features.insertFeature(tagName + "_NEW_" + "Explict_Contingency_ARG1",
				Double.TYPE);
		features.insertFeature(tagName + "_NEW_" + "Explict_Contingency_ARG2",
				Double.TYPE);

		features.insertFeature(tagName + "_OLD_" + "Explict_Expansion_ARG1",
				Double.TYPE);
		features.insertFeature(tagName + "_OLD_" + "Explict_Expansion_ARG2",
				Double.TYPE);
		features.insertFeature(tagName + "_NEW_" + "Explict_Expansion_ARG1",
				Double.TYPE);
		features.insertFeature(tagName + "_NEW_" + "Explict_Expansion_ARG2",
				Double.TYPE);

		features.insertFeature(tagName + "_OLD_" + "Explict_Temporal_ARG1",
				Double.TYPE);
		features.insertFeature(tagName + "_OLD_" + "Explict_Temporal_ARG2",
				Double.TYPE);
		features.insertFeature(tagName + "_NEW_" + "Explict_Temporal_ARG1",
				Double.TYPE);
		features.insertFeature(tagName + "_NEW_" + "Explict_Temporal_ARG2",
				Double.TYPE);

		features.insertFeature(tagName + "_OLD_" + "Implict_Comparison_ARG1",
				Double.TYPE);
		features.insertFeature(tagName + "_OLD_" + "Implict_Comparison_ARG2",
				Double.TYPE);
		features.insertFeature(tagName + "_NEW_" + "Implict_Comparison_ARG1",
				Double.TYPE);
		features.insertFeature(tagName + "_NEW_" + "Implict_Comparison_ARG2",
				Double.TYPE);

		features.insertFeature(tagName + "_OLD_" + "Implict_Contingency_ARG1",
				Double.TYPE);
		features.insertFeature(tagName + "_OLD_" + "Implict_Contingency_ARG2",
				Double.TYPE);
		features.insertFeature(tagName + "_NEW_" + "Implict_Contingency_ARG1",
				Double.TYPE);
		features.insertFeature(tagName + "_NEW_" + "Implict_Contingency_ARG2",
				Double.TYPE);

		features.insertFeature(tagName + "_OLD_" + "Implict_Expansion_ARG1",
				Double.TYPE);
		features.insertFeature(tagName + "_OLD_" + "Implict_Expansion_ARG2",
				Double.TYPE);
		features.insertFeature(tagName + "_NEW_" + "Implict_Expansion_ARG1",
				Double.TYPE);
		features.insertFeature(tagName + "_NEW_" + "Implict_Expansion_ARG2",
				Double.TYPE);

		features.insertFeature(tagName + "_OLD_" + "Implict_Temporal_ARG1",
				Double.TYPE);
		features.insertFeature(tagName + "_OLD_" + "Implict_Temporal_ARG2",
				Double.TYPE);
		features.insertFeature(tagName + "_NEW_" + "Implict_Temporal_ARG1",
				Double.TYPE);
		features.insertFeature(tagName + "_NEW_" + "Implict_Temporal_ARG2",
				Double.TYPE);
	}

	public static void insertFeatureWeightDiff(FeatureName features, String tagName) {
		features.insertFeature(tagName + "_DIFF_" + "EntRel_ARG1", Double.TYPE);
		features.insertFeature(tagName + "_DIFF_" + "EntRel_ARG2", Double.TYPE);
		
		features.insertFeature(tagName + "_DIFF_" + "AltLex_ARG1", Double.TYPE);
		features.insertFeature(tagName + "_DIFF_" + "AltLex_ARG2", Double.TYPE);
		
		features.insertFeature(tagName + "_DIFF_" + "Explict_Comparison_ARG1",
				Double.TYPE);
		features.insertFeature(tagName + "_DIFF_" + "Explict_Comparison_ARG2",
				Double.TYPE);
		
		features.insertFeature(tagName + "_DIFF_" + "Explict_Contingency_ARG1",
				Double.TYPE);
		features.insertFeature(tagName + "_DIFF_" + "Explict_Contingency_ARG2",
				Double.TYPE);
		
		features.insertFeature(tagName + "_DIFF_" + "Explict_Expansion_ARG1",
				Double.TYPE);
		features.insertFeature(tagName + "_DIFF_" + "Explict_Expansion_ARG2",
				Double.TYPE);
		
		features.insertFeature(tagName + "_DIFF_" + "Explict_Temporal_ARG1",
				Double.TYPE);
		features.insertFeature(tagName + "_DIFF_" + "Explict_Temporal_ARG2",
				Double.TYPE);
		
		features.insertFeature(tagName + "_DIFF_" + "Implict_Comparison_ARG1",
				Double.TYPE);
		features.insertFeature(tagName + "_DIFF_" + "Implict_Comparison_ARG2",
				Double.TYPE);
		
		features.insertFeature(tagName + "_DIFF_" + "Implict_Contingency_ARG1",
				Double.TYPE);
		features.insertFeature(tagName + "_DIFF_" + "Implict_Contingency_ARG2",
				Double.TYPE);
		
		features.insertFeature(tagName + "_DIFF_" + "Implict_Expansion_ARG1",
				Double.TYPE);
		features.insertFeature(tagName + "_DIFF_" + "Implict_Expansion_ARG2",
				Double.TYPE);
		
		features.insertFeature(tagName + "_DIFF_" + "Implict_Temporal_ARG1",
				Double.TYPE);
		features.insertFeature(tagName + "_DIFF_" + "Implict_Temporal_ARG2",
				Double.TYPE);
		
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
		RelationGrid grid = getGrid(index, isOld);
		String start = tagName;
		if (isOld) {
			// grid = entityOldGrid.get(index);
			start = start + "_OLD_";
		} else {
			// grid = entityNewGrid.get(index);
			start = start + "_NEW_";
		}
		fillInVector(features, featureVector, start + "EntRel_ARG1",
				grid.isEntRel_ARG1());
		fillInVector(features, featureVector, start + "EntRel_ARG2",
				grid.isEntRel_ARG2());

		fillInVector(features, featureVector, start + "AltLex_ARG1",
				grid.isAltLex_ARG1());
		fillInVector(features, featureVector, start + "AltLex_ARG2",
				grid.isAltLex_ARG2());

		fillInVector(features, featureVector,
				start + "Explict_Comparison_ARG1",
				grid.isComparison_Explicit_ARG1());
		fillInVector(features, featureVector,
				start + "Explict_Comparison_ARG2",
				grid.isComparison_Explicit_ARG2());
		fillInVector(features, featureVector,
				start + "Implict_Comparison_ARG1",
				grid.isComparison_Implicit_ARG1());
		fillInVector(features, featureVector,
				start + "Implict_Comparison_ARG2",
				grid.isComparison_Implicit_ARG2());

		fillInVector(features, featureVector, start
				+ "Explict_Contingency_ARG1",
				grid.isContingency_Explicit_ARG1());
		fillInVector(features, featureVector, start
				+ "Explict_Contingency_ARG2",
				grid.isContingency_Explicit_ARG2());
		fillInVector(features, featureVector, start
				+ "Implict_Contingency_ARG1",
				grid.isContingency_Implicit_ARG1());
		fillInVector(features, featureVector, start
				+ "Implict_Contingency_ARG2",
				grid.isContingency_Implicit_ARG2());

		fillInVector(features, featureVector, start + "Explict_Expansion_ARG1",
				grid.isExpansion_Explicit_ARG1());
		fillInVector(features, featureVector, start + "Explict_Expansion_ARG2",
				grid.isExpansion_Explicit_ARG2());
		fillInVector(features, featureVector, start + "Implict_Expansion_ARG1",
				grid.isExpansion_Implicit_ARG1());
		fillInVector(features, featureVector, start + "Implict_Expansion_ARG2",
				grid.isExpansion_Implicit_ARG2());

		fillInVector(features, featureVector, start + "Explict_Temporal_ARG1",
				grid.isTemporal_Explicit_ARG1());
		fillInVector(features, featureVector, start + "Explict_Temporal_ARG2",
				grid.isTemporal_Explicit_ARG2());
		fillInVector(features, featureVector, start + "Implict_Temporal_ARG1",
				grid.isTemporal_Implicit_ARG1());
		fillInVector(features, featureVector, start + "Implict_Temporal_ARG2",
				grid.isTemporal_Implicit_ARG2());
	}

	public void setValueCount(FeatureName features, Object[] featureVector,
			String tagName, int index, boolean isOld) {
		RelationGrid grid = getGrid(index, isOld);
		String start = tagName;
		if (isOld) {
			// grid = entityOldGrid.get(index);
			start = start + "_OLD_";
		} else {
			// grid = entityNewGrid.get(index);
			start = start + "_NEW_";
		}
		fillInVector(features, featureVector, start + "EntRel_ARG1",
				grid.getIsEntRel_ARG1_cnt());
		fillInVector(features, featureVector, start + "EntRel_ARG2",
				grid.getIsAltLex_ARG2_cnt());

		fillInVector(features, featureVector, start + "AltLex_ARG1",
				grid.getIsAltLex_ARG1_cnt());
		fillInVector(features, featureVector, start + "AltLex_ARG2",
				grid.getIsAltLex_ARG2_cnt());

		fillInVector(features, featureVector,
				start + "Explict_Comparison_ARG1",
				grid.getIsComparison_Explicit_ARG1_cnt());
		fillInVector(features, featureVector,
				start + "Explict_Comparison_ARG2",
				grid.getIsComparison_Explicit_ARG2_cnt());
		fillInVector(features, featureVector,
				start + "Implict_Comparison_ARG1",
				grid.getIsComparison_Implicit_ARG1_cnt());
		fillInVector(features, featureVector,
				start + "Implict_Comparison_ARG2",
				grid.getIsComparison_Implicit_ARG2_cnt());

		fillInVector(features, featureVector, start
				+ "Explict_Contingency_ARG1",
				grid.getIsContingency_Explicit_ARG1_cnt());
		fillInVector(features, featureVector, start
				+ "Explict_Contingency_ARG2",
				grid.getIsContingency_Explicit_ARG2_cnt());
		fillInVector(features, featureVector, start
				+ "Implict_Contingency_ARG1",
				grid.getIsContingency_Implicit_ARG1_cnt());
		fillInVector(features, featureVector, start
				+ "Implict_Contingency_ARG2",
				grid.getIsContingency_Implicit_ARG2_cnt());

		fillInVector(features, featureVector, start + "Explict_Expansion_ARG1",
				grid.getIsExpansion_Explicit_ARG1_cnt());
		fillInVector(features, featureVector, start + "Explict_Expansion_ARG2",
				grid.getIsExpansion_Explicit_ARG2_cnt());
		fillInVector(features, featureVector, start + "Implict_Expansion_ARG1",
				grid.getIsExpansion_Implicit_ARG1_cnt());
		fillInVector(features, featureVector, start + "Implict_Expansion_ARG2",
				grid.getIsExpansion_Implicit_ARG2_cnt());

		fillInVector(features, featureVector, start + "Explict_Temporal_ARG1",
				grid.getIsTemporal_Explicit_ARG1_cnt());
		fillInVector(features, featureVector, start + "Explict_Temporal_ARG2",
				grid.getIsTemporal_Explicit_ARG2_cnt());
		fillInVector(features, featureVector, start + "Implict_Temporal_ARG1",
				grid.getIsTemporal_Implicit_ARG1_cnt());
		fillInVector(features, featureVector, start + "Implict_Temporal_ARG2",
				grid.getIsTemporal_Implicit_ARG2_cnt());
		System.out.println(grid.toString());
	}

	public void setValueWeight(FeatureName features, Object[] featureVector,
			String tagName, int index, boolean isOld) {
		RelationGrid grid = getGrid(index, isOld);
		String start = tagName;
		if (isOld) {
			// grid = entityOldGrid.get(index);
			start = start + "_OLD_";
		} else {
			// grid = entityNewGrid.get(index);
			start = start + "_NEW_";
		}
		fillInVector(features, featureVector, start + "EntRel_ARG1",
				1 / grid.getIsEntRel_ARG1_distance());
		fillInVector(features, featureVector, start + "EntRel_ARG2",
				1 / grid.getIsAltLex_ARG2_distance());

		fillInVector(features, featureVector, start + "AltLex_ARG1",
				1 / grid.getIsAltLex_ARG1_distance());
		fillInVector(features, featureVector, start + "AltLex_ARG2",
				1 / grid.getIsAltLex_ARG2_distance());

		fillInVector(features, featureVector,
				start + "Explict_Comparison_ARG1",
				1 / grid.getIsComparison_Explicit_ARG1_distance());
		fillInVector(features, featureVector,
				start + "Explict_Comparison_ARG2",
				1 / grid.getIsComparison_Explicit_ARG2_distance());
		fillInVector(features, featureVector,
				start + "Implict_Comparison_ARG1",
				1 / grid.getIsComparison_Implicit_ARG1_distance());
		fillInVector(features, featureVector,
				start + "Implict_Comparison_ARG2",
				1 / grid.getIsComparison_Implicit_ARG2_distance());

		fillInVector(features, featureVector, start
				+ "Explict_Contingency_ARG1",
				1 / grid.getIsContingency_Explicit_ARG1_distance());
		fillInVector(features, featureVector, start
				+ "Explict_Contingency_ARG2",
				1 / grid.getIsContingency_Explicit_ARG2_distance());
		fillInVector(features, featureVector, start
				+ "Implict_Contingency_ARG1",
				1 / grid.getIsContingency_Implicit_ARG1_distance());
		fillInVector(features, featureVector, start
				+ "Implict_Contingency_ARG2",
				1 / grid.getIsContingency_Implicit_ARG2_distance());

		fillInVector(features, featureVector, start + "Explict_Expansion_ARG1",
				1 / grid.getIsExpansion_Explicit_ARG1_distance());
		fillInVector(features, featureVector, start + "Explict_Expansion_ARG2",
				1 / grid.getIsExpansion_Explicit_ARG2_distance());
		fillInVector(features, featureVector, start + "Implict_Expansion_ARG1",
				1 / grid.getIsExpansion_Implicit_ARG1_distance());
		fillInVector(features, featureVector, start + "Implict_Expansion_ARG2",
				1 / grid.getIsExpansion_Implicit_ARG2_distance());

		fillInVector(features, featureVector, start + "Explict_Temporal_ARG1",
				1 / grid.getIsTemporal_Explicit_ARG1_distance());
		fillInVector(features, featureVector, start + "Explict_Temporal_ARG2",
				1 / grid.getIsTemporal_Explicit_ARG2_distance());
		fillInVector(features, featureVector, start + "Implict_Temporal_ARG1",
				1 / grid.getIsTemporal_Implicit_ARG1_distance());
		fillInVector(features, featureVector, start + "Implict_Temporal_ARG2",
				1 / grid.getIsTemporal_Implicit_ARG2_distance());
	}
	
	
	public void setValueWeightDiff(FeatureName features, Object[] featureVector,
			String tagName, int old_index, int new_index) {
		RelationGrid oldGrid = getGrid(old_index, true);
		RelationGrid newGrid = getGrid(new_index, false);
		String start = tagName + "_DIFF_";
		fillInVector(features, featureVector, start + "EntRel_ARG1",
				1 / newGrid.getIsEntRel_ARG1_distance() - 1/oldGrid.getIsEntRel_ARG1_distance());
		fillInVector(features, featureVector, start + "EntRel_ARG2",
				1 / newGrid.getIsAltLex_ARG2_distance() - 1/oldGrid.getIsAltLex_ARG2_distance());

		fillInVector(features, featureVector, start + "AltLex_ARG1",
				1 / newGrid.getIsAltLex_ARG1_distance() - 1/oldGrid.getIsAltLex_ARG1_distance());
		fillInVector(features, featureVector, start + "AltLex_ARG2",
				1 / newGrid.getIsAltLex_ARG2_distance() - 1/oldGrid.getIsAltLex_ARG2_distance());

		fillInVector(features, featureVector,
				start + "Explict_Comparison_ARG1",
				1 / newGrid.getIsComparison_Explicit_ARG1_distance() - 1 / oldGrid.getIsComparison_Explicit_ARG1_distance());
		fillInVector(features, featureVector,
				start + "Explict_Comparison_ARG2",
				1 / newGrid.getIsComparison_Explicit_ARG2_distance() - 1 / oldGrid.getIsComparison_Explicit_ARG2_distance());
		fillInVector(features, featureVector,
				start + "Implict_Comparison_ARG1",
				1 / newGrid.getIsComparison_Implicit_ARG1_distance() - 1 / oldGrid.getIsComparison_Implicit_ARG1_distance());
		fillInVector(features, featureVector,
				start + "Implict_Comparison_ARG2",
				1 / newGrid.getIsComparison_Implicit_ARG2_distance() - 1 / oldGrid.getIsComparison_Implicit_ARG2_distance());

		fillInVector(features, featureVector, start
				+ "Explict_Contingency_ARG1",
				1 / newGrid.getIsContingency_Explicit_ARG1_distance() - 1 / oldGrid.getIsContingency_Explicit_ARG1_distance());
		fillInVector(features, featureVector, start
				+ "Explict_Contingency_ARG2",
				1 / newGrid.getIsContingency_Explicit_ARG2_distance() - 1 / oldGrid.getIsContingency_Explicit_ARG2_distance());
		fillInVector(features, featureVector, start
				+ "Implict_Contingency_ARG1",
				1 / newGrid.getIsContingency_Implicit_ARG1_distance() - 1 / oldGrid.getIsContingency_Implicit_ARG1_distance());
		fillInVector(features, featureVector, start
				+ "Implict_Contingency_ARG2",
				1 / newGrid.getIsContingency_Implicit_ARG2_distance() - 1 / oldGrid.getIsContingency_Implicit_ARG2_distance());

		fillInVector(features, featureVector, start + "Explict_Expansion_ARG1",
				1 / newGrid.getIsExpansion_Explicit_ARG1_distance() - 1 / oldGrid.getIsExpansion_Explicit_ARG1_distance());
		fillInVector(features, featureVector, start + "Explict_Expansion_ARG2",
				1 / newGrid.getIsExpansion_Explicit_ARG2_distance() - 1 / oldGrid.getIsExpansion_Explicit_ARG2_distance());
		fillInVector(features, featureVector, start + "Implict_Expansion_ARG1",
				1 / newGrid.getIsExpansion_Implicit_ARG1_distance() - 1 / oldGrid.getIsExpansion_Implicit_ARG1_distance());
		fillInVector(features, featureVector, start + "Implict_Expansion_ARG2",
				1 / newGrid.getIsExpansion_Implicit_ARG2_distance() - 1 / oldGrid.getIsExpansion_Implicit_ARG2_distance());

		fillInVector(features, featureVector, start + "Explict_Temporal_ARG1",
				1 / newGrid.getIsTemporal_Explicit_ARG1_distance() - 1 / oldGrid.getIsTemporal_Explicit_ARG1_distance());
		fillInVector(features, featureVector, start + "Explict_Temporal_ARG2",
				1 / newGrid.getIsTemporal_Explicit_ARG2_distance() - 1 / oldGrid.getIsTemporal_Explicit_ARG2_distance());
		fillInVector(features, featureVector, start + "Implict_Temporal_ARG1",
				1 / newGrid.getIsTemporal_Implicit_ARG1_distance() - 1 / oldGrid.getIsTemporal_Implicit_ARG1_distance());
		fillInVector(features, featureVector, start + "Implict_Temporal_ARG2",
				1 / newGrid.getIsTemporal_Implicit_ARG2_distance() - 1 / oldGrid.getIsTemporal_Implicit_ARG2_distance());
	}

}
