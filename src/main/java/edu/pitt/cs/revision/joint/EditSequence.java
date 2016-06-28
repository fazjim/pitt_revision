package edu.pitt.cs.revision.joint;

import java.util.ArrayList;
import java.util.List;

import edu.pitt.lrdc.cs.revision.model.RevisionPurpose;

public class EditSequence {
	private int oldParagraphStartNo;
	private int oldParagraphEndNo;
	private int newParagraphStartNo;
	private int newParagraphEndNo;

	public String toString() {
		String str = "Sequence from : OLD(" + oldParagraphStartNo +"," +oldParagraphEndNo+"), NEW("+newParagraphStartNo +","
 +newParagraphEndNo+")"+"\n";
		for(EditStep step: labelSequence) {
			str += step.toString() +"\n";
		}
		return str;
	}
	
	public int getOldParagraphStartNo() {
		return oldParagraphStartNo;
	}

	public void setOldParagraphStartNo(int oldParagraphStartNo) {
		this.oldParagraphStartNo = oldParagraphStartNo;
	}

	public int getOldParagrahpEndNo() {
		return oldParagraphEndNo;
	}

	public void setOldParagraphEndNo(int oldParagrahEndNo) {
		this.oldParagraphEndNo = oldParagrahEndNo;
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
	
	public void setLabels(List<Integer> labels) {
		for(int i = 0;i<labelSequence.size();i++) {
			EditStep step = labelSequence.get(i);
			step.setType(labels.get(i));
		}
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
