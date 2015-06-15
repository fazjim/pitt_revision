package edu.pitt.lrdc.cs.revision.model;

/**
 * Just the statistic info model for Alok's request
 * @author zhangfan
 *
 */
public class StatisticInfo {
	public String getPseudoname() {
		return pseudoname;
	}
	public void setPseudoname(String pseudoname) {
		this.pseudoname = pseudoname;
	}
	public int getAdditions() {
		return additions;
	}
	public void setAdditions(int additions) {
		this.additions = additions;
	}
	public int getDeletions() {
		return deletions;
	}
	public void setDeletions(int deletions) {
		this.deletions = deletions;
	}
	public int getModifications() {
		return modifications;
	}
	public void setModifications(int modifications) {
		this.modifications = modifications;
	}
	public double getEditedPerecent() {
		return editedPerecent;
	}
	public void setEditedPerecent(double editedPerecent) {
		this.editedPerecent = editedPerecent;
	}
	public int getSurfaceEdits() {
		return surfaceEdits;
	}
	public void setSurfaceEdits(int surfaceEdits) {
		this.surfaceEdits = surfaceEdits;
	}
	public int getContentEdits() {
		return contentEdits;
	}
	public void setContentEdits(int contentEdits) {
		this.contentEdits = contentEdits;
	}
	String pseudoname;
	int additions;
	int deletions;
	int modifications;
	double editedPerecent;
	public double getAddPercent() {
		return addPercent;
	}
	public void setAddPercent(double addPercent) {
		this.addPercent = addPercent;
	}
	public double getDeletePercent() {
		return deletePercent;
	}
	public void setDeletePercent(double deletePercent) {
		this.deletePercent = deletePercent;
	}
	double addPercent;
	double deletePercent;
	int surfaceEdits;
	int contentEdits;
}
