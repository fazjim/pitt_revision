package edu.pitt.lrdc.cs.revision.gui;

import java.util.Hashtable;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import edu.pitt.lrdc.cs.revision.model.RevisionOp;
import edu.pitt.lrdc.cs.revision.model.RevisionPurpose;
import edu.pitt.lrdc.cs.revision.model.RevisionUnit;

public class HigherAnnotateBox extends JPanel {
	/**
	 * For annotation only
	 */
	private JComboBox purposeList = new JComboBox();
	private Hashtable<Integer, Integer> optionTable = new Hashtable<Integer, Integer>();
	private Hashtable<Integer, Integer> indexTable = new Hashtable<Integer, Integer>();
	private JRadioButton addButton = new JRadioButton("Add", false);
	private JRadioButton deleteButton = new JRadioButton("Delete", false);
	private JRadioButton modifyButton = new JRadioButton("Modify", false);
	private JRadioButton nochangeButton = new JRadioButton("Nochange", false);
	ButtonGroup bgroup = new ButtonGroup();

	public void initializeList() {
		int index = 0;
		for (int i = RevisionPurpose.START; i <= RevisionPurpose.END; i++) {
			optionTable.put(index, i);
			indexTable.put(i, index);
			purposeList.addItem(RevisionPurpose.getPurposeName(i));
			index++;
		}
		bgroup.add(addButton);
		bgroup.add(deleteButton);
		bgroup.add(modifyButton);
		bgroup.add(nochangeButton);
	}

	public void reloadInfo(int revPurpose, int revisionOp) {
		this.bgroup.clearSelection();
		if (revisionOp == RevisionOp.ADD) {
			this.bgroup.setSelected(addButton.getModel(), true);
		} else if (revisionOp == RevisionOp.DELETE) {
			this.bgroup.setSelected(deleteButton.getModel(), true);
		} else if (revisionOp == RevisionOp.MODIFY) {
			this.bgroup.setSelected(modifyButton.getModel(), true);
		} else if (revisionOp == RevisionOp.NOCHANGE) {
			this.bgroup.setSelected(nochangeButton.getModel(), true);
		} else {
			// do nothing
		}
	}

	public HigherAnnotateBox() {
		this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		initializeList();
		this.add(purposeList);
		Box buttonBox = new Box(BoxLayout.Y_AXIS);
		buttonBox.add(addButton);
		buttonBox.add(deleteButton);
		buttonBox.add(modifyButton);
		buttonBox.add(nochangeButton);
		this.add(buttonBox);
	}

	public int getSelectedOp() {
		if (addButton.isSelected()) {
			return RevisionOp.ADD;
		} else if (deleteButton.isSelected()) {
			return RevisionOp.DELETE;
		} else if (modifyButton.isSelected()) {
			return RevisionOp.MODIFY;
		} else if (nochangeButton.isSelected()) {
			return RevisionOp.NOCHANGE;
		} else {
			return -1;
		}
	}

	public int getSelectedPurpose() {
		if (purposeList.getSelectedItem() != null) {
			return optionTable.get(purposeList.getSelectedIndex());
		}
		return -1;
	}

	private void setSelectedOp(int revisionOp) {
		if (revisionOp == RevisionOp.ADD) {
			this.bgroup.setSelected(addButton.getModel(), true);
		} else if (revisionOp == RevisionOp.DELETE) {
			this.bgroup.setSelected(deleteButton.getModel(), true);
		} else if (revisionOp == RevisionOp.MODIFY) {
			this.bgroup.setSelected(modifyButton.getModel(), true);
		} else if (revisionOp == RevisionOp.NOCHANGE) {
			this.bgroup.setSelected(nochangeButton.getModel(), true);
		}
	}

	public void loadRevisionUnit(RevisionUnit rev) {
		if (rev != null && rev.getRevision_purpose()!=-1) {
			int revPurpose = rev.getRevision_purpose();
			int index = indexTable.get(revPurpose);
			purposeList.setSelectedIndex(index);
			setSelectedOp(rev.getRevision_op());
		}
	}
}
