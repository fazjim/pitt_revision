package edu.pitt.lrdc.cs.revision.agreement;

import edu.pitt.lrdc.cs.revision.model.RevisionDocument;

/**
 * Data structure for a couple of kappa
 * @author zhangfan
 *
 */
public class ComparePair {
	RevisionDocument r1;
	RevisionDocument r2;
	
	public ComparePair() {
		
	}
	/**
	 * Constructor
	 * @param r1
	 * @param r2
	 */
	public ComparePair(RevisionDocument r1, RevisionDocument r2) {
		this.r1 = r1;
		this.r2 = r2;
	}
	
	
	/**
	 * Getter for r1
	 * @return r1, an annotated document
	 */
	public RevisionDocument getR1() {
		return r1;
	}
	
	/**
	 * Getter for r2
	 * @return r2, an annotated document
	 */
	public RevisionDocument getR2() {
		return r2;
	}
	/**
	 * Setter for r1
	 * @param r1
	 */
	public void setR1(RevisionDocument r1) {
		this.r1 = r1;
	}
	
	/**
	 * Setter for r2
	 * @param r2
	 */
	public void setR2(RevisionDocument r2) {
		this.r2 = r2;
	}
}
