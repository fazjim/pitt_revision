package edu.pit.lrdc.cs.revision.gui;

import javax.swing.*;

public class ContentDemoBox extends Box{
	private JTextArea demo = new JTextArea("Info of the selected revision unit");
	public ContentDemoBox(int axis) {
		super(axis);
		JScrollPane sp = new JScrollPane(demo);
		add(sp);
	}
	
	public void setText(String text) {
		String[] lines = text.split("\n");
		this.demo.setText(text);
	}
}
