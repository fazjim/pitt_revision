package edu.pitt.lrdc.cs.revision.gui;

import javax.swing.*;

public class ContentBox extends Box{
	JTextArea newSentence;
	JTextArea oldSentence;
	public ContentBox(int axis) {
		super(axis);
		
		newSentence = new JTextArea("Sentence from NEW version:\n");
		oldSentence = new JTextArea("Sentence from the OLD version:\n");
		newSentence.setRows(3);
		oldSentence.setRows(3);
		oldSentence.setLineWrap(true);
		newSentence.setLineWrap(true);
		newSentence.setEditable(false);
		oldSentence.setEditable(false);
		
		JScrollPane newPane = new JScrollPane(newSentence);
		JScrollPane oldPane = new JScrollPane(oldSentence);
	
		add(oldPane);
		add(newPane);
	}
	
	public void setNewSentence(String sent) {
		this.newSentence.setText("Sentence from NEW version:\n"+sent);
	}
	
	public void setOldSentence(String sent) {
		this.oldSentence.setText("Sentence from the OLD version:\n"+sent);
	}
}
