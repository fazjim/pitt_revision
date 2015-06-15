package edu.pit.lrdc.cs.revision.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import edu.pitt.lrdc.cs.revision.evaluate.EvaluateUtil;
import edu.pitt.lrdc.cs.revision.io.RevisionDocumentReader;
import edu.pitt.lrdc.cs.revision.model.RevisionDocument;
import edu.pitt.lrdc.cs.revision.model.RevisionPurpose;

/**
 * This interface does not support cross validation yet, to do that, you have to
 * use the code edu.pitt.lrdc.cs.revision.evaluate.EvaluateMain.java
 * 
 * @author zhf4pal
 *
 */
public class EvaluatePanel extends JPanel {
	private FilePicker goldPathPicker;
	private FilePicker predictPathPicker;
	private JButton classifyEvaluate;
	private JButton pipelineEvaluate;
	private JButton alignEvaluate;

	private JTextArea messageBox;

	public EvaluatePanel() {
		goldPathPicker = new FilePicker(
				"Select the path of the gold standard documents", "Browse");
		predictPathPicker = new FilePicker(
				"Select the path of the predicted documents", "Browse");
		classifyEvaluate = new JButton("Evaluate Classification");
		pipelineEvaluate = new JButton("Evaluete Pipeline Classification");
		alignEvaluate = new JButton("Evaluate Alignment");
		messageBox = new JTextArea();
		messageBox.setRows(40);
		Box buttonBox = new Box(BoxLayout.X_AXIS);
		buttonBox.add(alignEvaluate);
		alignEvaluate.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent paramActionEvent) {
				// TODO Auto-generated method stub
				try {
					evaluateAlignment();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					showResult(e.getStackTrace().toString());
				}
			}
		});
		
		buttonBox.add(classifyEvaluate);
		classifyEvaluate.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent paramActionEvent) {
				// TODO Auto-generated method stub
				try {
					evaluateClassify();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					showResult(e.getStackTrace().toString());
				}
			}
		});

		buttonBox.add(pipelineEvaluate);

		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.add(goldPathPicker);
		this.add(predictPathPicker);
		this.add(buttonBox);
		JScrollPane sp = new JScrollPane(messageBox);
		this.add(sp);
	}

	public void showResult(String text) {
		messageBox.setText(text);
	}

	public RevisionDocument findMatchedDoc(RevisionDocument doc,
			ArrayList<RevisionDocument> golds) {
		File f = new File(doc.getDocumentName());
		String fileName = f.getName();
		for (RevisionDocument gold : golds) {
			File goldF = new File(gold.getDocumentName());
			if (goldF.getName().equals(fileName))
				return gold;
		}
		return null;
	}

	public void evaluateAlignment() throws Exception {
		String goldPath = goldPathPicker.getSelectedFilePath();
		String predictPath = predictPathPicker.getSelectedFilePath();
		if (goldPath == null || predictPath == null || goldPath.trim().equals("")||predictPath.trim().equals("")) {
			showResult("Please make sure the folder has been selected!");
		} else {
			int allCnt = 0;
			int correct = 0;
			ArrayList<RevisionDocument> golds = RevisionDocumentReader
					.readDocs(goldPath);
			ArrayList<RevisionDocument> docs = RevisionDocumentReader
					.readDocs(predictPath);
			for (RevisionDocument doc : docs) {
				RevisionDocument matchedGold = findMatchedDoc(doc, golds);
				int sentenceNewNum = doc.getNewSentencesArray().length;
				allCnt += sentenceNewNum;
				for (int i = 1; i <= sentenceNewNum; i++) {
					ArrayList<Integer> predictedAligned = doc.getOldFromNew(i);
					ArrayList<Integer> goldAligned = matchedGold
							.getPredictedOldFromNew(i);
					if (goldAligned == null
							|| goldAligned.size() == 0
							|| (goldAligned.size() == 1 && goldAligned.get(0) == -1)) {
						if (predictedAligned == null
								|| predictedAligned.size() == 0
								|| (predictedAligned.size() == 1 && predictedAligned
										.get(0) == -1)) {
							correct++;
						}
					} else {
						if (compareArr(goldAligned, predictedAligned)) {
							correct++;
						}
					}
				}
			}
			double accuracy = (correct * 1.0) /allCnt;
			showResult("The accuracy is : " +  accuracy);
		}
	}

	private boolean compareArr(ArrayList<Integer> a1, ArrayList<Integer> a2) {
		if (a1 == null)
			a1 = new ArrayList<Integer>();
		if (a2 == null)
			a2 = new ArrayList<Integer>();
		HashSet<Integer> set = new HashSet<Integer>();
		for (Integer i : a1) {
			if (i != -1)
				set.add(i);
		}

		HashSet<Integer> set2 = new HashSet<Integer>();
		for (Integer i : a2) {
			if (i != -1)
				set2.add(i);
		}

		if (set.size() != set2.size())
			return false;
		for (Integer i : set) {
			if (!set2.contains(i))
				return false;
		}
		return true;
	}

	/**
	 * Evaluate classification with alignments
	 * 
	 * @throws Exception
	 */
	public void evaluateClassify() throws Exception {
		String goldPath = goldPathPicker.getSelectedFilePath();
		String predictPath = predictPathPicker.getSelectedFilePath();
		if (goldPath == null || predictPath == null || goldPath.trim().equals("")||predictPath.trim().equals("")) {
			showResult("Please make sure the folder has been selected!");
		} else {
			ArrayList<RevisionDocument> golds = RevisionDocumentReader
					.readDocs(goldPath);
			ArrayList<RevisionDocument> docs = RevisionDocumentReader
					.readDocs(predictPath);
			String result = "";
			for (int i = RevisionPurpose.START; i <= RevisionPurpose.END; i++) {
				EvaluateUtil eu = new EvaluateUtil();
				eu.compareResults(docs, golds, i);
				result += RevisionPurpose.getPurposeName(i) + ":";
				result += "Precision 0:" + eu.precision(0) + ", Precision 1:"
						+ eu.precision(1) + ", Recall 0:" + eu.recall(0)
						+ ", Recall 1:" + eu.recall(1) + " , unweighted F:"
						+ eu.unweightedMacroFmeasure() + ", Kappa:"
						+ eu.kappa();
				result += "\n";
			}
			showResult(result);
		}
	}
	
	public void evaluateAlignClassify() {
		
	}
}
