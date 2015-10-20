package edu.pitt.lrdc.cs.revision.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import java.util.Iterator;

import edu.pitt.lrdc.cs.revision.agreement.KappaCalc;
import edu.pitt.lrdc.cs.revision.evaluate.EvaluateUtil;
import edu.pitt.lrdc.cs.revision.io.RevisionDocumentReader;
import edu.pitt.lrdc.cs.revision.model.RevisionDocument;
import edu.pitt.lrdc.cs.revision.model.RevisionPurpose;
import edu.pitt.lrdc.cs.revision.model.RevisionUnit;

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
	private JButton kappaEvaluate;
	private JButton binaryKappaEvaluate;
	private JButton showStatistics;

	private JTextArea messageBox;

	public EvaluatePanel() {
		goldPathPicker = new FilePicker(
				"Select the path of the gold standard documents", "Browse");
		predictPathPicker = new FilePicker(
				"Select the path of the predicted documents", "Browse");
		goldPathPicker.setChooseBoth();
		predictPathPicker.setChooseBoth();

		kappaEvaluate = new JButton("Calculate Kappa");
		binaryKappaEvaluate = new JButton("Calculate Binary Kappa");
		showStatistics = new JButton("Show statistics");

		classifyEvaluate = new JButton("Evaluate Classification");
		pipelineEvaluate = new JButton("Evaluete Pipeline Classification");
		alignEvaluate = new JButton("Evaluate Alignment");
		messageBox = new JTextArea();
		messageBox.setRows(40);
		Box bBox = new Box(BoxLayout.X_AXIS);
		bBox.add(kappaEvaluate);
		kappaEvaluate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent paramActionEvent) {
				showKappa();
			}
		});
		binaryKappaEvaluate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent paramActionEvent) {

			}
		});
		showStatistics.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent paramActionEvent) {
				showStatistic();
			}
		});

		bBox.add(binaryKappaEvaluate);
		bBox.add(showStatistics);

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
		this.add(bBox);
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
		if (goldPath == null || predictPath == null
				|| goldPath.trim().equals("") || predictPath.trim().equals("")) {
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
			double accuracy = (correct * 1.0) / allCnt;
			showResult("The accuracy is : " + accuracy);
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
		if (goldPath == null || predictPath == null
				|| goldPath.trim().equals("") || predictPath.trim().equals("")) {
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

	public void showStatistic() {
		String txt = "";

		try {
			String goldPath = goldPathPicker.getSelectedFilePath();
			String predictPath = predictPathPicker.getSelectedFilePath();

			if (goldPath != null && goldPath.trim().length() != 0) {
				txt += "FOR:" + goldPath + "\n";
				int fileNum = 0;
				int draft1Num = 0;
				int draft2Num = 0;
				int revNum = 0;
				File f = new File(goldPath);
				if (f.isFile()) {
					RevisionDocument doc = RevisionDocumentReader
							.readDoc(goldPath);
					fileNum = 1;
					draft1Num = doc.getOldDraftSentences().size();
					draft2Num = doc.getNewDraftSentences().size();
					revNum = doc.getRoot().getRevisionUnitAtLevel(0).size();
				} else {
					ArrayList<RevisionDocument> docs = RevisionDocumentReader
							.readDocs(goldPath);
					fileNum = docs.size();
					for (RevisionDocument doc : docs) {
						draft1Num += doc.getOldDraftSentences().size();
						draft2Num += doc.getNewDraftSentences().size();
						revNum += doc.getRoot().getRevisionUnitAtLevel(0).size();
					}
				}

				txt += "# of Files: " + fileNum + "\n";
				txt += "# of Draft 1 sentences:" + draft1Num + "\n";
				txt += "# of Draft 2 sentences:" + draft2Num + "\n";
				double avgD1 = draft1Num * 1.0 / fileNum;
				double avgD2 = draft2Num * 1.0 / fileNum;
				txt += "Avg D1: "+ avgD1 + ", Avg D2:"+avgD2+"\n";
				txt += "# of revision:"+revNum;
			}

			if (predictPath != null && predictPath.trim().length() != 0) {
				txt += "FOR:" + predictPath + "\n";
				int fileNum = 0;
				int draft1Num = 0;
				int draft2Num = 0;
				int revNum = 0;
				File f = new File(predictPath);
				if (f.isFile()) {
					RevisionDocument doc = RevisionDocumentReader
							.readDoc(predictPath);
					fileNum = 1;
					draft1Num = doc.getOldDraftSentences().size();
					draft2Num = doc.getNewDraftSentences().size();
					revNum = doc.getRoot().getRevisionUnitAtLevel(0).size();
				} else {
					ArrayList<RevisionDocument> docs = RevisionDocumentReader
							.readDocs(predictPath);
					fileNum = docs.size();
					for (RevisionDocument doc : docs) {
						draft1Num += doc.getOldDraftSentences().size();
						draft2Num += doc.getNewDraftSentences().size();
						revNum += doc.getRoot().getRevisionUnitAtLevel(0).size();
					}
				}

				txt += "# of Files: " + fileNum + "\n";
				txt += "# of Draft 1 sentences:" + draft1Num + "\n";
				txt += "# of Draft 2 sentences:" + draft2Num + "\n";
				double avgD1 = draft1Num * 1.0 / fileNum;
				double avgD2 = draft2Num * 1.0 / fileNum;
				txt += "Avg D1: "+ avgD1 + ", Avg D2:"+avgD2+"\n";
				txt += "# of revision:"+revNum;
			}
			showResult(txt);

		} catch (Exception exp) {
			showResult(exp.getMessage());
		}
	}

	public void buildMap(RevisionDocument doc, Hashtable<String, Integer> map) {
		ArrayList<RevisionUnit> revisions = doc.getRoot()
				.getRevisionUnitAtLevel(0);
		for (RevisionUnit unit : revisions) {
			ArrayList<Integer> oldIndices = unit.getOldSentenceIndex();
			ArrayList<Integer> newIndices = unit.getNewSentenceIndex();
			String oldIndiceStr = "OLD:";
			String newIndiceStr = "NEW:";
			for (Integer oldIndex : oldIndices) {
				oldIndiceStr += oldIndex + "-";
			}
			for (Integer newIndex : newIndices) {
				newIndiceStr += newIndex + "-";
			}
			String key = oldIndiceStr + "," + newIndiceStr;
			int purpose = unit.getRevision_purpose();
			if (purpose > RevisionPurpose.CD_GENERAL_CONTENT_DEVELOPMENT) {
				map.put(key, 4);
			} else {
				if (purpose > RevisionPurpose.CD_REBUTTAL_RESERVATION) {
					if (map.containsKey(key)) {
						if (purpose < map.get(key))
							map.put(key, purpose - 2);
					} else {
						map.put(key, purpose - 2);
					}
				} else if (purpose < RevisionPurpose.CD_REBUTTAL_RESERVATION) {
					if (map.containsKey(key)) {
						if (purpose < map.get(key))
							map.put(key, purpose - 1);
					} else {
						map.put(key, purpose - 1);
					}
				}
			}
		}
	}

	public double calculateKappa(RevisionDocument doc1, RevisionDocument doc2) {
		Hashtable<String, Integer> rev1Map = new Hashtable<String, Integer>();
		Hashtable<String, Integer> rev2Map = new Hashtable<String, Integer>();

		buildMap(doc1, rev1Map);
		buildMap(doc2, rev2Map);

		int[][] matrix = new int[5][5];
		Iterator<String> it = rev1Map.keySet().iterator();
		while (it.hasNext()) {
			String key = it.next();
			int revP1 = rev1Map.get(key);
			if (rev2Map.containsKey(key)) {
				int revP2 = rev2Map.get(key);
				matrix[revP1][revP2] += 1;
			}
		}
		return KappaCalc.kappaCalc(matrix);
	}

	public ArrayList<RevisionDocument[]> findMatchedFiles(File f1, File f2)
			throws Exception {
		ArrayList<RevisionDocument[]> list = new ArrayList<RevisionDocument[]>();
		if (f1.isFile()) {
			String fileName = f1.getName();
			RevisionDocument d1 = RevisionDocumentReader.readDoc(f1
					.getAbsolutePath());
			File[] subs = f2.listFiles();
			for (File sub : subs) {
				if (sub.getName().equals(fileName)) {
					RevisionDocument d2 = RevisionDocumentReader.readDoc(sub
							.getAbsolutePath());
					RevisionDocument[] docs = new RevisionDocument[2];
					docs[0] = d1;
					docs[1] = d2;
					list.add(docs);
					return list;
				}
			}
		} else if (f2.isFile()) {
			String fileName = f2.getName();
			RevisionDocument d2 = RevisionDocumentReader.readDoc(f2
					.getAbsolutePath());
			File[] subs = f1.listFiles();
			for (File sub : subs) {
				if (sub.getName().equals(fileName)) {
					RevisionDocument d1 = RevisionDocumentReader.readDoc(sub
							.getAbsolutePath());
					RevisionDocument[] docs = new RevisionDocument[2];
					docs[0] = d1;
					docs[1] = d2;
					list.add(docs);
					return list;
				}
			}
		} else {
			File[] subs1 = f1.listFiles();
			File[] subs2 = f2.listFiles();
			for (File sub : subs1) {
				for (File sub2 : subs2) {
					if (sub.getName().equals(sub2.getName())) {
						RevisionDocument d1 = RevisionDocumentReader
								.readDoc(sub.getAbsolutePath());
						RevisionDocument d2 = RevisionDocumentReader
								.readDoc(sub2.getAbsolutePath());
						RevisionDocument[] docs = new RevisionDocument[2];
						docs[0] = d1;
						docs[1] = d2;
						list.add(docs);
					}
				}
			}
		}
		return list;
	}

	public void showKappa() {
		String txt = "";
		String goldPath = goldPathPicker.getSelectedFilePath();
		String predictPath = predictPathPicker.getSelectedFilePath();
		try {
			if (goldPath == null || predictPath == null) {
				txt = "Folders not complete";
			} else {
				File file = new File(goldPath);
				File file2 = new File(predictPath);
				if (file.isDirectory() || file2.isDirectory()) {
					ArrayList<RevisionDocument[]> docs = findMatchedFiles(file,
							file2);
					for (RevisionDocument[] docArr : docs) {
						RevisionDocument doc1 = docArr[0];
						RevisionDocument doc2 = docArr[1];
						String name = doc1.getDocumentName();
						txt += "Kappa:" + "\t" + name + "\t"
								+ calculateKappa(doc1, doc2) + "\n";
					}
				} else {
					RevisionDocument doc1 = RevisionDocumentReader
							.readDoc(goldPath);
					RevisionDocument doc2 = RevisionDocumentReader
							.readDoc(predictPath);
					txt += "Kappa:" + "\t" + doc1.getDocumentName() + "\t"
							+ calculateKappa(doc1, doc2) + "\n";
				}
			}
			showResult(txt);
		} catch (Exception exp) {
			showResult(exp.getMessage());
		}
	}

	public void evaluateAlignClassify() {

	}
}
