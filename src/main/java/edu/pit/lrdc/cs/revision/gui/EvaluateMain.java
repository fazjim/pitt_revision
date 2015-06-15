package edu.pit.lrdc.cs.revision.gui;

import javax.swing.JFrame;
import javax.swing.WindowConstants;


public class EvaluateMain extends JFrame{
	public EvaluateMain() {
		EvaluatePanel panel = new EvaluatePanel();
		this.add(panel);
		this.setSize(1000, 800);
	}
	
	public static void main(String[] args) {
		EvaluateMain mf = new EvaluateMain();
		// mf.load("dummy");
		mf.show();
		mf.setTitle("Evaluate");
		mf.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	}
}
