package edu.pitt.lrdc.cs.revision.gui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import java.util.*;

import edu.pitt.lrdc.cs.revision.model.RevisionDocument;
import edu.pitt.lrdc.cs.revision.model.RevisionUnit;

public class BaseLevelPanel extends JPanel implements LevelPanel {
	JList sentenceList;
	RevisionDocument doc; // Data model
	JButton changeAlignmentButton; //Change alignment
	Box sentenceBox;
	AnnotateBox annotateBox;// For annotate the purposes and operations
	ContentBox annotateContentDetail;
	LevelDemoPanel levelPanel;

	private boolean isOldPanel = false;

	ArrayList<RevisionUnit> currentRU = null;

	public void registerRevision() {
		ArrayList<SelectionUnit> sus = annotateBox.getSelectedUnits();
		if (currentRU == null || currentRU.size() == 0) {
			// do nothing
			System.err.println("Do nothing");
		} else {
			ArrayList<Integer> oldSentenceIndex = currentRU.get(0).getOldSentenceIndex();
			ArrayList<Integer> newSentenceIndex = currentRU.get(0).getNewSentenceIndex();

			// the same units will not be processed
			// the new units will be registered and the old units will be
			// removed

			// First remove the unexisting old
			for (RevisionUnit ru : currentRU) {
				boolean isExist = false;
				for (SelectionUnit su : sus) {
					if (su.revision_op == ru.getRevision_op()
							&& su.revision_purpose == ru.getRevision_purpose()) {
						isExist = true;
						break;
					}
				}
				if (!isExist) {
					ru.setAbandoned();
				}
			}

			// Now add the new stuff
			for (SelectionUnit su : sus) {
				boolean isExist = false;
				for (RevisionUnit ru : currentRU) {
					if (su.revision_op == ru.getRevision_op()
							&& su.revision_purpose == ru.getRevision_purpose()) {
						isExist = true;
						break;
					}
				}
				if (!isExist) {
					RevisionUnit newUnit = new RevisionUnit(doc.getRoot());
					newUnit.setOldSentenceIndex(oldSentenceIndex);
					if (oldSentenceIndex!=null && oldSentenceIndex.size() != 0) {
						String oldSentence = "";
						for(Integer oldIndex: oldSentenceIndex) {
							if(oldIndex!=-1)
							oldSentence += doc.getOldSentence(oldIndex)+"\n";
						}
						newUnit.setOldSentence(oldSentence);
					}
					newUnit.setNewSentenceIndex(newSentenceIndex);
					if (newSentenceIndex!=null && newSentenceIndex.size() != 0) {
						String newSentence = "";
						for(Integer newIndex: newSentenceIndex) {
							if(newIndex!=-1)
							newSentence += doc.getNewSentence(newIndex)+"\n";
						}
						newUnit.setNewSentence(newSentence);
					}
					newUnit.setRevision_op(su.revision_op);
					newUnit.setRevision_purpose(su.revision_purpose);
					
					newUnit.setRevision_level(0);
					newUnit.setRevision_index(doc.getRoot().getNextIndexAtLevel(0));
					doc.getRoot().addUnit(newUnit);
				}
			}
		}
		doc.getRoot().clear();
	}

	class ListSelectionHandler implements ListSelectionListener {
		public void valueChanged(ListSelectionEvent e) {

			ListSelectionModel lsm = (ListSelectionModel) e.getSource();

			//
			// First treat the ones that have been treated

			if (lsm.isSelectionEmpty()) {
				//
			} else {
				// Find out which indexes are selected.
				// int maxIndex = lsm.getMaxSelectionIndex();
				registerRevision();
				String newSentence = "";
				String oldSentence = "";
				int selectIndex = lsm.getMinSelectionIndex();
				if (isOldPanel) {
					oldSentence = doc.getOldSentence(selectIndex + 1);
					ArrayList<Integer> newIndexes = doc
							.getNewFromOld(selectIndex + 1);
					if (newIndexes != null) {
						for (Integer newIndex : newIndexes) {
							if (newIndex != -1) {
								newSentence += doc.getNewSentence(newIndex)
										+ "\n";
							} else {
								newSentence = "Delete";
							}
						}
						ArrayList<RevisionUnit> rus = doc.getRoot()
								.getRevisionUnitOldAtLevel(0, selectIndex + 1);
						currentRU = rus;
						annotateBox.reload(rus);
						if(rus == null||currentRU.size()==0) {
							RevisionUnit newRU = new RevisionUnit(doc.getRoot());
							newRU.setRevision_level(0);
							ArrayList<Integer> oldList = new ArrayList<Integer>();
							oldList.add(selectIndex+1);
							newRU.setNewSentenceIndex(newIndexes);
							newRU.setOldSentenceIndex(oldList);
							currentRU.add(newRU);
						}
					}
				} else {
					newSentence = doc.getNewSentence(selectIndex + 1);
					ArrayList<Integer> oldIndexes = doc
							.getOldFromNew(selectIndex + 1);
					if (oldIndexes != null) {
						for (Integer oldIndex : oldIndexes) {
							if (oldIndex != -1) {
								oldSentence += doc.getOldSentence(oldIndex)
										+ "\n";
							} else {
								oldSentence = "Add";
							}
						}
						ArrayList<RevisionUnit> rus = doc.getRoot()
								.getRevisionUnitNewAtLevel(0, selectIndex + 1);
						currentRU = rus;
						annotateBox.reload(rus);
						if(rus == null||currentRU.size()==0) {
							RevisionUnit newRU = new RevisionUnit(doc.getRoot());
							newRU.setRevision_level(0);
							ArrayList<Integer> newList = new ArrayList<Integer>();
							newList.add(selectIndex+1);
							newRU.setNewSentenceIndex(newList);
							newRU.setOldSentenceIndex(oldIndexes);
							currentRU.add(newRU);
						}
					}
				}
				annotateContentDetail.setOldSentence(oldSentence);
				annotateContentDetail.setNewSentence(newSentence);
				if (oldSentence.trim().equals(newSentence.trim())) {
					annotateBox.setEnabled(false);
					annotateBox.display("Two sentences are identical");
				} else {
					annotateBox.setEnabled(true);
					annotateBox.display("Please annotate the revision");
				}
				// System.out.println("SB");
			}
		}
	}

	
	public void showAlign() {
		JFrame frame = new JFrame("Change alignment");
		frame.setContentPane(new AlignmentChangePanel(doc, currentRU.get(0)));
		frame.show();
	}
	public BaseLevelPanel(RevisionDocument doc, boolean isOldPanel) {
		this.isOldPanel = isOldPanel;
		String[] sentences = doc.getNewSentencesArray();
		if(isOldPanel) sentences = doc.getOldSentencesArray();
		this.doc = doc;
		sentenceList = new JList(sentences);
		sentenceList.setVisibleRowCount(15);
		sentenceList.getSelectionModel().addListSelectionListener(
				new ListSelectionHandler());
		JScrollPane pane = new JScrollPane(sentenceList);
		pane.setSize(this.getWidth(), this.getHeight() / 2);
		annotateBox = new AnnotateBox();
		sentenceBox = new Box(BoxLayout.Y_AXIS);
		sentenceBox.add(pane);
		annotateContentDetail = new ContentBox(BoxLayout.Y_AXIS);
		levelPanel = new LevelDemoPanel(doc, 0);
		levelPanel.boundPanel(this);
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		// GridBagConstraints c = new GridBagConstraints();
		// c.fill = GridBagConstraints.HORIZONTAL;
		// c.gridx = 0;
		// c.gridy = 0;
		// c.gridheight = 4;
		changeAlignmentButton = new JButton("Change alignment");
		changeAlignmentButton.setToolTipText("Not implemented yet, change the sentence alignment");
		//changeAlignmentButton.setEnabled(false);
		changeAlignmentButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				/*JFrame frame = new JFrame("Change alignment");
				frame.setContentPane(new AlignmentChangePanel(doc, currentRU.get(0)));
				frame.show();*/
				showAlign();
			}
		});
		
		add(sentenceBox);
		add(changeAlignmentButton);
		// c.fill = GridBagConstraints.HORIZONTAL;
		// c.gridx = 0;
		// c.gridy = 4;
		// c.gridheight = 4;
		add(annotateBox);

		// c.fill = GridBagConstraints.HORIZONTAL;
		// c.gridx = 0;
		// c.gridy = 8;
		// c.gridheight = 2;
		add(annotateContentDetail);

		// c.gridheight = 1;
		// c.fill = GridBagConstraints.HORIZONTAL;
		// c.gridx = 0;
		// c.gridy = 10;
		add(levelPanel);
	}

}
