package edu.pitt.lrdc.cs.revision.gui;

import javax.swing.*;

public class ContentBox extends Box{
	JTextArea newSentence;
	JTextArea oldSentence;
	public ContentBox(int axis) {
		super(axis);
		newSentence = new JTextArea("Sentence from current version:\n");
		oldSentence = new JTextArea("Sentence from the last version:\n");
		newSentence.setEditable(false);
		oldSentence.setEditable(false);
		
		JScrollPane newPane = new JScrollPane(newSentence);
		JScrollPane oldPane = new JScrollPane(oldSentence);
	
		add(newPane);
		add(oldPane);
	}
	
	public void setNewSentence(String sent) {
		this.newSentence.setText("Sentence from current version:\n"+sent);
	}
	
	public void setOldSentence(String sent) {
		this.oldSentence.setText("Sentence from the last version:\n"+sent);
	}
}
