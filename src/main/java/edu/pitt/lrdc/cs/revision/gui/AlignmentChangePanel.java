package edu.pitt.lrdc.cs.revision.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Hashtable;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import edu.pitt.lrdc.cs.revision.model.RevisionDocument;
import edu.pitt.lrdc.cs.revision.model.RevisionUnit;

public class AlignmentChangePanel extends JPanel {
	private JList oldSentenceList;
	private JList newSentenceList;
	private JList unHandledList;
	private JTextArea selectedSentence;
	private JButton confirmButton;
	private JButton cancelButton;
	private JButton removeButton;
	private RevisionDocument doc;

	Hashtable<String, Integer> sentenceIndex = new Hashtable<String, Integer>();

	public AlignmentChangePanel(RevisionDocument doc, RevisionUnit ru) {
		this(doc, ru.getOldSentenceIndex(), ru.getNewSentenceIndex());
	}

	public AlignmentChangePanel(RevisionDocument doc,
			ArrayList<Integer> oldIndices, ArrayList<Integer> newIndices) {
		this.doc = doc;
		confirmButton = new JButton("Confirm new alignment");
		confirmButton.setToolTipText("Confirm and save the current selection");
		cancelButton = new JButton("Remove alignment");
		cancelButton.setToolTipText("Cancel the current selections");
		oldSentenceList = new JList(doc.getOldSentencesArray());
		newSentenceList = new JList(doc.getNewSentencesArray());
		oldSentenceList.setVisibleRowCount(15);
		newSentenceList.setVisibleRowCount(15);

		int cnt = 0;
		for (Integer oldIndex : oldIndices) {
			if (oldIndex != -1)
				cnt += 1;
		}
		for (Integer newIndex : newIndices) {
			if (newIndex != -1)
				cnt += 1;
		}
		String[] candidates = new String[cnt];
		int c_index = 0;

		for (Integer oldIndex : oldIndices) {
			if (oldIndex != -1) {
				String content = "OLD:";
				content = content + doc.getOldSentence(oldIndex);
				sentenceIndex.put(content, oldIndex);
				candidates[c_index] = content;
				c_index++;
			}
		}

		for (Integer newIndex : newIndices) {
			if (newIndex != -1) {
				String content = "NEW:";
				content = content + doc.getNewSentence(newIndex);
				sentenceIndex.put(content, newIndex);
				candidates[c_index] = content;
				c_index++;
			}
		}

		unHandledList = new JList(candidates);
		unHandledList.getSelectionModel().addListSelectionListener(
				new ListSelectionListener() {
					@Override
					public void valueChanged(ListSelectionEvent e) {
						// TODO Auto-generated method stub
						String content = unHandledList.getSelectedValue()
								.toString();
						loadContent(content);
					}

				});

		Box sentenceBox = new Box(BoxLayout.Y_AXIS);
		Box buttonBox = new Box(BoxLayout.Y_AXIS);

		confirmButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				save();
			}

		});
		
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				//cancel();
				remove();
			}

		});
		buttonBox.add(confirmButton);
		buttonBox.add(cancelButton);
		sentenceBox.add(unHandledList);
		sentenceBox.add(buttonBox);

		Box alignBox = new Box(BoxLayout.X_AXIS);

		oldSentenceList
				.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		newSentenceList
				.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		JScrollPane oldPane = new JScrollPane(oldSentenceList);
		JScrollPane newPane = new JScrollPane(newSentenceList);
		alignBox.add(oldPane);
		alignBox.add(newPane);
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.add(sentenceBox);
		this.add(alignBox);
		oldSentenceList.setEnabled(false);
		newSentenceList.setEnabled(false);
	}

	/**
	 * Load the current alignment
	 * 
	 * @param sentence
	 */
	public void loadContent(String sentence) {
		int index = sentenceIndex.get(sentence);
		if (sentence.startsWith("OLD")) {
			newSentenceList.setEnabled(true);
			ArrayList<Integer> arr = doc.getNewFromOld(index);
			if(arr == null) arr = new ArrayList<Integer>();
			int length = 0;
			for (Integer i : arr) {
				if (i != -1)
					length++;
			}
			int[] selected = new int[length];
			int s_index = 0;
			for (Integer i : arr) {
				if (i != -1) {
					selected[s_index] = i - 1;
					s_index++;
				}
			}

			newSentenceList.setSelectedIndices(selected);
			oldSentenceList.setSelectedIndex(index - 1);
			oldSentenceList.setEnabled(false);
			oldSentenceList.ensureIndexIsVisible(oldSentenceList
					.getSelectedIndex());
			newSentenceList.ensureIndexIsVisible(newSentenceList
					.getSelectedIndex());
		} else {
			oldSentenceList.setEnabled(true);
			ArrayList<Integer> arr = doc.getOldFromNew(index);
			if(arr == null) arr = new ArrayList<Integer>();
			int length = 0;
			for (Integer i : arr) {
				if (i != -1)
					length++;
			}
			int[] selected = new int[length];
			int s_index = 0;
			for (Integer i : arr) {
				if (i != -1) {
					selected[s_index] = i - 1;
					s_index++;
				}
			}
			oldSentenceList.setSelectedIndices(selected);
			newSentenceList.setSelectedIndex(index - 1);
			newSentenceList.setEnabled(false);

			oldSentenceList.ensureIndexIsVisible(oldSentenceList
					.getSelectedIndex());
			newSentenceList.ensureIndexIsVisible(newSentenceList
					.getSelectedIndex());
		}
	}

	public void save() {
		try {
		if (!oldSentenceList.isEnabled()) {
			int oldIndex = oldSentenceList.getSelectedIndex() + 1;
			int[] newIndiceArr = newSentenceList.getSelectedIndices();
			ArrayList<Integer> newIndices = new ArrayList<Integer>();
			for (int i = 0; i < newIndiceArr.length; i++) {
				newIndices.add(newIndiceArr[i] + 1);
			}
			doc.changeOldAlignment(oldIndex, newIndices);
			JOptionPane.showMessageDialog(this, "Alignment changed.");
		} else if (!newSentenceList.isEnabled()) {
			int newIndex = newSentenceList.getSelectedIndex() + 1;
			int[] oldIndiceArr = oldSentenceList.getSelectedIndices();
			ArrayList<Integer> oldIndices = new ArrayList<Integer>();
			for (int i = 0; i < oldIndiceArr.length; i++) {
				oldIndices.add(oldIndiceArr[i] + 1);
			}
			doc.changeNewAlignment(newIndex, oldIndices);
			JOptionPane.showMessageDialog(this, "Alignment changed.");
		} else {
			// something is wrong
		}
		} catch(Exception exp) {
			JOptionPane.showMessageDialog(this, "Alignment could not be changed.\n"+"Trace: "+exp.getMessage());
		}
		//doc.check();
	}

	
	public void remove() {
		try {
		if (!oldSentenceList.isEnabled()) { //Old List is disabled, so you are changing the alignment of the old sentence
			int oldIndex = oldSentenceList.getSelectedIndex() + 1;
			//int[] newIndiceArr = newSentenceList.getSelectedIndices();
			ArrayList<Integer> newIndices = new ArrayList<Integer>();
			/*for (int i = 0; i < newIndiceArr.length; i++) {
				newIndices.add(newIndiceArr[i] + 1);
			}*/
			doc.changeOldAlignment(oldIndex, newIndices);
			JOptionPane.showMessageDialog(this, "Alignment changed.");
		} else if (!newSentenceList.isEnabled()) {
			int newIndex = newSentenceList.getSelectedIndex() + 1;
			//int[] oldIndiceArr = oldSentenceList.getSelectedIndices();
			ArrayList<Integer> oldIndices = new ArrayList<Integer>();
			/*for (int i = 0; i < oldIndiceArr.length; i++) {
				oldIndices.add(oldIndiceArr[i] + 1);
			}*/
			doc.changeNewAlignment(newIndex, oldIndices);
			JOptionPane.showMessageDialog(this, "Alignment changed.");
		} else {
			// something is wrong
		}
		} catch(Exception exp) {
			JOptionPane.showMessageDialog(this, "Alignment could not be changed.\n"+"Trace: "+exp.getMessage());
		}
		doc.check();
	}
	
	public void cancel() {
		if (!oldSentenceList.isEnabled()) {
			newSentenceList.clearSelection();
		} else if (!newSentenceList.isEnabled()) {
			oldSentenceList.clearSelection();
		} else {
			// something is wrong
		}
	}
}
