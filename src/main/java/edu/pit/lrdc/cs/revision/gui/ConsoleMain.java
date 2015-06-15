package edu.pit.lrdc.cs.revision.gui;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

public class ConsoleMain extends JFrame{
	private JButton annotationButton;
	private JButton analysisButton;
	private JButton evaluateButton;
	
	public ConsoleMain() {
		annotationButton = new JButton("Annotation Panel");
		annotationButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				showAnnotation();
			}
		});
		analysisButton = new JButton("Analysis Panel");
		analysisButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				showAnalysis();
			}
		});
		evaluateButton = new JButton("Evaluate Panel");
		evaluateButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				showEvaluate();
			}
		});
		GridLayout layout = new GridLayout(3,0);
		//this.setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		this.setLayout(layout);
		this.add(annotationButton);
		this.add(analysisButton);
		this.add(evaluateButton);
		this.setSize(400, 400);
	}
	
	public static void main(String[] args) {
		ConsoleMain mf = new ConsoleMain();
		mf.setTitle("Console");
		// mf.load("dummy");
		mf.show();
		mf.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	}
	
	public void showAnnotation() {
		MainFrame mf = new MainFrame();
		mf.show();
	}
	
	public void showAnalysis() {
		AnalysisMain mf = new AnalysisMain();
		mf.setTitle("Document Analysis Tool");
		mf.show();
	}
	
	public void showEvaluate() {
		EvaluateMain mf = new EvaluateMain();
		mf.setTitle("Evaluation Tool");
		mf.show();
	}
}
