package edu.pitt.lrdc.cs.revision.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.*;

import edu.pitt.lrdc.cs.revision.model.RevisionOp;

public class EditUnitV2 extends JPanel {
	JLabel name;
	JLabel colorBoard;
	String purpose;
	JCheckBox checkBox;
	

	public EditUnitV2(String name, Color color) {
		this.name = new JLabel();
		this.colorBoard = new JLabel("->");
		
		this.name.setText(name);
		this.colorBoard.setBackground(color);
		this.colorBoard.setOpaque(true);
		this.colorBoard.setForeground(color);
		//this.name.setForeground(color);
		checkBox = new JCheckBox();
		checkBox.setSelected(false);
		this.purpose = name;

		add(colorBoard);
		add(checkBox);
		add(this.name);
		this.setBorder(BorderFactory.createDashedBorder(Color.black));
	
	}
	/*
	public EditUnitV2(String name) {
		this.name = new JLabel();
		this.name.setText(name);
		checkBox = new JCheckBox();
		this.purpose = name;
		add(checkBox);
		add(this.name);
	}*/
	
	public void setEnabled(boolean enabled) {
		this.checkBox.setEnabled(enabled);
	}
	
	public boolean isSelected() {
		return this.checkBox.isSelected();
	}
	
	public void reload(int revisionOp) {
		if(revisionOp == -1) {
			this.checkBox.setSelected(false);
		} else {
			this.checkBox.setSelected(true);
		}
	}
}
