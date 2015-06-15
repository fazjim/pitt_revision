package edu.pitt.lrdc.cs.revision.gui;

import javax.swing.JFrame;
import javax.swing.WindowConstants;


public class AnalysisMain extends JFrame{
	public AnalysisMain() {
		AnalysisInterface panel = new AnalysisInterface();
		this.add(panel);
		this.setSize(1000, 800);
	}
	
	public static void main(String[] args) {
		AnalysisMain mf = new AnalysisMain();
		// mf.load("dummy");
		mf.show();
		mf.setTitle("Analyze");
		mf.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	}
}
