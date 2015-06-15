package edu.pit.lrdc.cs.revision.gui;

import java.awt.Container;

import javax.swing.JFrame;

import edu.pitt.lrdc.cs.revision.model.RevisionDocument;

public class HigherLevelFrame extends JFrame{
	public HigherLevelFrame(RevisionDocument doc, int level) {
		setTitle("Revision Annotation Tool - Level:"+level);
		setSize(1000,800);
		Container contentPane = getContentPane();
		contentPane.add(new HigherLevelPanel(doc,level));
	}
}
