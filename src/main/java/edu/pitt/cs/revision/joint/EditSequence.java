package edu.pitt.cs.revision.joint;

import java.util.ArrayList;
import java.util.List;

import edu.pitt.lrdc.cs.revision.model.RevisionPurpose;

public class EditSequence {
	private int oldParagraphStartNo;
	private int oldParagrahEndNo;
	private int newParagraphStartNo;
	private int newParagraphEndNo;
	
	public int getOldParagraphStartNo() {
		return oldParagraphStartNo;
	}

	public void setOldParagraphStartNo(int oldParagraphStartNo) {
		this.oldParagraphStartNo = oldParagraphStartNo;
	}

	public int getOldParagrahEndNo() {
		return oldParagrahEndNo;
	}

	public void setOldParagrahEndNo(int oldParagrahEndNo) {
		this.oldParagrahEndNo = oldParagrahEndNo;
	}

	public int getNewParagraphStartNo() {
		return newParagraphStartNo;
	}

	public void setNewParagraphStartNo(int newParagraphStartNo) {
		this.newParagraphStartNo = newParagraphStartNo;
	}

	public int getNewParagraphEndNo() {
		return newParagraphEndNo;
	}

	public void setNewParagraphEndNo(int newParagraphEndNo) {
		this.newParagraphEndNo = newParagraphEndNo;
	}

	private List<EditStep> labelSequence = new ArrayList<EditStep>();
	
	public List<EditStep> getLabelSequence() {
		return labelSequence;
	}
	
	public void addAdd(int oldIndex, int newIndex, int revPurpose) {
		EditStep step = new EditStep();
		step.setCurrentD1(oldIndex);
		step.setCurrentD2(newIndex);
		step.setD1Move(EditStep.EDIT_KEEP);
		step.setD2Move(EditStep.EDIT_MOVE);
		step.setType(revPurpose);
		labelSequence.add(step);
	}
	
	public void addDelete(int oldIndex, int newIndex, int revPurpose) {
		EditStep step = new EditStep();
		step.setCurrentD1(oldIndex);
		step.setCurrentD2(newIndex);
		step.setD1Move(EditStep.EDIT_MOVE);
		step.setD2Move(EditStep.EDIT_KEEP);
		step.setType(revPurpose);
		labelSequence.add(step);
	}
	
	public void addModify(int oldIndex, int newIndex, int revPurpose) {
		EditStep step = new EditStep();
		step.setCurrentD1(oldIndex);
		step.setCurrentD2(newIndex);
		step.setD1Move(EditStep.EDIT_MOVE);
		step.setD2Move(EditStep.EDIT_MOVE);
		step.setType(revPurpose);
		labelSequence.add(step);
	}
	
	public void addNochange(int oldIndex, int newIndex, int revPurpose) {
		EditStep step = new EditStep();
		step.setCurrentD1(oldIndex);
		step.setCurrentD2(newIndex);
		step.setD1Move(EditStep.EDIT_MOVE);
		step.setD2Move(EditStep.EDIT_MOVE);
		step.setType(RevisionPurpose.NOCHANGE);
		labelSequence.add(step);
	}
}
