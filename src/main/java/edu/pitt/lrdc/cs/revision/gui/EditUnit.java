package edu.pitt.lrdc.cs.revision.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.*;

import edu.pitt.lrdc.cs.revision.model.RevisionOp;

public class EditUnit extends JPanel {
	JLabel name;
	String purpose;
	JCheckBox checkBox;
	JRadioButton addButton = new JRadioButton("Add", false);
	JRadioButton deleteButton = new JRadioButton("Delete", false);
	JRadioButton modifyButton = new JRadioButton("Modify", false);
	JRadioButton nochangeButton = new JRadioButton("Nochange", false);

	ButtonGroup bgroup = new ButtonGroup();

	ItemListener itemListener = new ItemListener() {
		@Override
		public void itemStateChanged(ItemEvent e) {
			// TODO Auto-generated method stub
			if (addButton.isEnabled()) {
				addButton.setEnabled(false);
				deleteButton.setEnabled(false);
				modifyButton.setEnabled(false);
				nochangeButton.setEnabled(false);
			} else {
				addButton.setEnabled(true);
				deleteButton.setEnabled(true);
				modifyButton.setEnabled(true);
				nochangeButton.setEnabled(true);
			}
		}
	};

	public EditUnit(String name, Color color) {
		this.name = new JLabel();
		this.name.setText(name);
		this.name.setBackground(color);
		//this.name.setForeground(color);
		checkBox = new JCheckBox();
		checkBox.addItemListener(itemListener);
		this.purpose = name;

		addButton.setEnabled(false);
		addButton.setBackground(color);
		deleteButton.setEnabled(false);
		deleteButton.setBackground(color);
		modifyButton.setEnabled(false);
		modifyButton.setBackground(color);
		nochangeButton.setEnabled(false);
		nochangeButton.setBackground(color);;

		bgroup.add(addButton);
		bgroup.add(deleteButton);
		bgroup.add(modifyButton);
		bgroup.add(nochangeButton);
		
		add(checkBox);
		add(this.name);
		add(addButton);
		add(deleteButton);
		add(modifyButton);
		add(nochangeButton);
	}
	
	public EditUnit(String name) {
		this.name = new JLabel();
		this.name.setText(name);
		checkBox = new JCheckBox();
		checkBox.addItemListener(itemListener);
		this.purpose = name;

		addButton.setEnabled(false);
		deleteButton.setEnabled(false);
		modifyButton.setEnabled(false);
		nochangeButton.setEnabled(false);

		bgroup.add(addButton);
		bgroup.add(deleteButton);
		bgroup.add(modifyButton);
		bgroup.add(nochangeButton);
		
		add(checkBox);
		add(this.name);
		add(addButton);
		add(deleteButton);
		add(modifyButton);
		add(nochangeButton);
	}
	public void setEnabled(boolean enabled) {
		this.checkBox.setEnabled(enabled);
	}
	
	public void reload(int revisionOp) {
		this.bgroup.clearSelection();
		this.checkBox.setSelected(true);
		if(revisionOp == RevisionOp.ADD) {
			this.bgroup.setSelected(addButton.getModel(), true);
		} else if(revisionOp == RevisionOp.DELETE) {
			this.bgroup.setSelected(deleteButton.getModel(), true);
		} else if(revisionOp == RevisionOp.MODIFY) {
			this.bgroup.setSelected(modifyButton.getModel(), true);
		} else if(revisionOp == RevisionOp.NOCHANGE) {
			this.bgroup.setSelected(nochangeButton.getModel(), true);
		} else {
			//do nothing
			this.checkBox.setSelected(false);
		}
	}
	
	public int getSelectedOp() {
		if(checkBox.isSelected()) {
			if(addButton.isSelected()) {
				return RevisionOp.ADD;
			} else if(deleteButton.isSelected()) {
				return RevisionOp.DELETE;
			} else if(modifyButton.isSelected()) {
				return RevisionOp.MODIFY;
			} else if(nochangeButton.isSelected()) {
				return RevisionOp.NOCHANGE;
			} else {
				return -1;
			}
 		} else {
			return -1;
		}
	}
}
