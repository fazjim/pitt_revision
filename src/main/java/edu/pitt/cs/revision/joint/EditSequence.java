package edu.pitt.cs.revision.joint;

import java.util.Stack;

import java.util.ArrayList;
import java.util.List;

import edu.pitt.lrdc.cs.revision.model.RevisionPurpose;

public class EditSequence {
	private int oldParagraphStartNo;
	private int oldParagraphEndNo;
	private int newParagraphStartNo;
	private int newParagraphEndNo;

	/**
	 * Get EditStep according to old and new sentence index
	 * 
	 * @param oldIndex
	 * @param newIndex
	 * @return
	 */
	public EditStep getStep(int oldIndex, int newIndex) {
		for (EditStep step : labelSequence) {
			if (step.getCurrentD1() == oldIndex
					&& step.getCurrentD2() == newIndex) {
				return step;
			}
		}
		return null;
	}

	/**
	 * Get steps according to the old index
	 * 
	 * @param oldIndex
	 * @return
	 */
	public List<EditStep> getStepsFromOld(int oldIndex) {
		List<EditStep> steps = new ArrayList<EditStep>();
		for (EditStep step : labelSequence) {
			if (step.getCurrentD1() == oldIndex) {
				steps.add(step);
			}
		}
		return steps;
	}

	/**
	 * Get steps according to the new index
	 * 
	 * @param newIndex
	 * @return
	 */
	public List<EditStep> getStepsFromNew(int newIndex) {
		List<EditStep> steps = new ArrayList<EditStep>();
		for (EditStep step : labelSequence) {
			if (step.getCurrentD2() == newIndex) {
				steps.add(step);
			}
		}
		return steps;
	}

	public String toString() {
		String str = "Sequence from : OLD(" + oldParagraphStartNo + ","
				+ oldParagraphEndNo + "), NEW(" + newParagraphStartNo + ","
				+ newParagraphEndNo + ")" + "\n";
		for (EditStep step : labelSequence) {
			str += step.toString() + "\n";
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

	public void addStep(EditStep step) {
		this.labelSequence.add(step);
	}

	public EditSequence copy() {
		EditSequence copy = new EditSequence();
		copy.setOldParagraphStartNo(this.getOldParagraphStartNo());
		copy.setOldParagraphEndNo(this.getOldParagrahpEndNo());
		copy.setNewParagraphStartNo(this.getNewParagraphStartNo());
		copy.setNewParagraphEndNo(this.getNewParagraphEndNo());
		for (EditStep step : labelSequence) {
			copy.addStep(step.copy());
		}
		return copy;
	}

	public void setLabels(List<Integer> labels) {
		for (int i = 0; i < labelSequence.size(); i++) {
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

	public void reverseOrder() {
		List<EditStep> newSequence = new ArrayList<EditStep>();
		for (int i = labelSequence.size() - 1; i >= 0; i--) {
			newSequence.add(labelSequence.get(i));
		}
		labelSequence = newSequence;
	}
}
