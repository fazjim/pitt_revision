package edu.pit.lrdc.cs.revision.gui;

/**
 * Just a struct for the selection of gui
 * @author zhangfan
 *
 */
public class SelectionUnit {
	int revision_op;
	int revision_purpose;
	public SelectionUnit() {
		
	}
	public SelectionUnit(int revision_op,int revision_purpose) {
		this.revision_op = revision_op;
		this.revision_purpose = revision_purpose;
	}
}
