package edu.pitt.lrdc.cs.revision.gui;

/**
 * 
 * @author zhangfan
 * 
 * version 2.0 
 * put the old draft and new draft in one tab
 */
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

public class AdvBaseLevelPanel extends JPanel implements LevelPanel {
	JList sentenceList; // old
	JList newSentenceList; // new
	
	RevisionDocument doc; // Data model
	JButton changeAlignmentButton; // Change alignment
	Box sentenceBox;
	AnnotateBox annotateBox;// For annotate the purposes and operations
	ContentBox annotateContentDetail;
	LevelDemoPanel levelPanel;

	ArrayList<RevisionUnit> currentRU = null;

	DraftDisplayPanel ddp;
	public void setDisplay(DraftDisplayPanel ddp) {
		this.ddp = ddp;
	}
	
	public void highlight() {
		String oldSentence = "o";
		String newSentence = "l";
		ddp.highLight(true, oldSentence);
		ddp.highLight(false, newSentence);
	}
	
	public void registerRevision() {
		ArrayList<SelectionUnit> sus = annotateBox.getSelectedUnits();
		if (currentRU == null || currentRU.size() == 0) {
			// do nothing
			System.err.println("Do nothing");
		} else {
			ArrayList<Integer> oldSentenceIndex = currentRU.get(0)
					.getOldSentenceIndex();
			ArrayList<Integer> newSentenceIndex = currentRU.get(0)
					.getNewSentenceIndex();

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
					if (oldSentenceIndex != null
							&& oldSentenceIndex.size() != 0) {
						String oldSentence = "";
						for (Integer oldIndex : oldSentenceIndex) {
							if (oldIndex != -1)
								oldSentence += doc.getOldSentence(oldIndex)
										+ "\n";
						}
						newUnit.setOldSentence(oldSentence);
					}
					newUnit.setNewSentenceIndex(newSentenceIndex);
					if (newSentenceIndex != null
							&& newSentenceIndex.size() != 0) {
						String newSentence = "";
						for (Integer newIndex : newSentenceIndex) {
							if (newIndex != -1)
								newSentence += doc.getNewSentence(newIndex)
										+ "\n";
						}
						newUnit.setNewSentence(newSentence);
					}
					newUnit.setRevision_op(su.revision_op);
					newUnit.setRevision_purpose(su.revision_purpose);

					newUnit.setRevision_level(0);
					newUnit.setRevision_index(doc.getRoot()
							.getNextIndexAtLevel(0));
					doc.getRoot().addUnit(newUnit);
				}
			}
		}
		doc.getRoot().clear();
	}

	boolean changeAlignment = false;

	// signal for select control
	boolean isOldSelected = false; // initially the signal is off, and once
									// clicked, the signal is turned on, when
									// the other list is triggered to be
									// selected, it will not trigger the
									// selection here since it turned on already
	boolean isNewSelected = false;

	boolean compareArr(int[] a1, int[] a2) {
		// Debug info
		/*
		 * for (Integer i : a1) System.out.print(i + "\t");
		 * System.out.println(); for (Integer i : a2) System.out.print(i +
		 * "\t"); System.out.println();
		 */
		HashSet<Integer> set = new HashSet<Integer>();
		for (Integer i : a1) {
			if(i!=-1)
			set.add(i);
		}

		HashSet<Integer> set2 = new HashSet<Integer>();
		for (Integer i : a2) {
			if(i!=-1)
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

	class ListSelectionHandler implements ListSelectionListener {
		private void changeSelection(ListSelectionEvent e) {

			ListSelectionModel lsm = (ListSelectionModel) e.getSource();
			if (!changeAlignment) {
				if (lsm.equals(sentenceList.getSelectionModel())) {
					// System.out.println("Entered");
					// System.out.println("old");
					// int selectIndex = sentenceList.getSelectedIndex();
					if (!isOldSelected)
						isOldSelected = true;
					if (!isNewSelected) {
						int[] selectIndices = sentenceList.getSelectedIndices();
						ArrayList<Integer> newIndexes = new ArrayList<Integer>();
						for (Integer selectIndex : selectIndices) {
							if (doc.getNewFromOld(selectIndex + 1) != null)
								newIndexes.addAll(doc
										.getNewFromOld(selectIndex + 1));
						}
						if (newIndexes != null) {
							ArrayList<Integer> newIndexes2 = new ArrayList<Integer>();
							for (Integer i : newIndexes) {
								if (i != -1)
									newIndexes2.add(i);
							}
							newIndexes = newIndexes2;
						}
						if (newIndexes == null || newIndexes.size() == 0) {
							newSentenceList.clearSelection();
						} else {
							int[] newSelectIndices = new int[newIndexes.size()];
							for (int i = 0; i < newIndexes.size(); i++) {
								newSelectIndices[i] = newIndexes.get(i) - 1;

							}
							int[] currentSelection = newSentenceList
									.getSelectedIndices();
							if (!compareArr(newSelectIndices, currentSelection)) {
								newSentenceList.clearSelection();
								newSentenceList
										.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

								newSentenceList
										.setSelectedIndices(newSelectIndices);
								newSentenceList
										.ensureIndexIsVisible(newSentenceList
												.getSelectedIndex());
							}
						}
					}
					isNewSelected = false;

				} else if (lsm.equals(newSentenceList.getSelectionModel())) {
					// System.out.println("new");
					// int selectIndex = newSentenceList.getSelectedIndex();
					if (!isNewSelected)
						isNewSelected = true;
					if (!isOldSelected) {
						int[] selectIndices = newSentenceList
								.getSelectedIndices();
						ArrayList<Integer> oldIndexes = new ArrayList<Integer>();
						for (Integer selectIndex : selectIndices) {
							if (doc.getOldFromNew(selectIndex + 1) != null) {
								oldIndexes.addAll(doc
										.getOldFromNew(selectIndex + 1));
							}
						}
						ArrayList<Integer> oldIndexes2 = new ArrayList<Integer>();
						if (oldIndexes != null) {
							for (Integer i : oldIndexes) {
								if (i != -1)
									oldIndexes2.add(i);
							}
							oldIndexes = oldIndexes2;
						}
						if (oldIndexes == null || oldIndexes.size() == 0) {
							sentenceList.clearSelection();
						} else {
							int[] oldSelectIndices = new int[oldIndexes.size()];
							for (int i = 0; i < oldIndexes.size(); i++) {
								oldSelectIndices[i] = oldIndexes.get(i) - 1;
							}
							int[] currentSelection = sentenceList
									.getSelectedIndices();
							if (!compareArr(oldSelectIndices, currentSelection)) {
								sentenceList.clearSelection();
								sentenceList
										.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

								sentenceList
										.setSelectedIndices(oldSelectIndices);
								sentenceList.ensureIndexIsVisible(sentenceList
										.getSelectedIndex());
							}
						}
					}
					isOldSelected = false;
				}
			} else {
				// changing alignment
			}
			highlight();
		}

		public void valueChanged(ListSelectionEvent e) {
			ListSelectionModel lsm = (ListSelectionModel) e.getSource();
			//
			// First treat the ones that have been treated
			changeSelection(e);
			if (lsm.isSelectionEmpty()) {
				//
			} else {
				// Find out which indexes are selected.
				// int maxIndex = lsm.getMaxSelectionIndex();
				registerRevision();
				String newSentence = "";
				String oldSentence = "";

				/*
				 * int oldSelectIndex = -1; if
				 * (!sentenceList.isSelectionEmpty()) oldSelectIndex =
				 * sentenceList.getSelectedIndex(); int newSelectIndex = -1; if
				 * (!newSentenceList.isSelectionEmpty()) newSelectIndex =
				 * newSentenceList.getSelectedIndex();
				 */

				int[] oldSelectIndices = new int[sentenceList
						.getSelectedIndices().length];
				int[] newSelectIndices = new int[newSentenceList
						.getSelectedIndices().length];
				if (!sentenceList.isSelectionEmpty()) {
					oldSelectIndices = sentenceList.getSelectedIndices();
				}
				if (!newSentenceList.isSelectionEmpty()) {
					newSelectIndices = newSentenceList.getSelectedIndices();
				}

				ArrayList<RevisionUnit> rus = new ArrayList<RevisionUnit>();
				for (Integer oldIndex : oldSelectIndices) {
					if (oldIndex != -1) {
						oldSentence += doc.getOldSentence(oldIndex + 1) + "\n";
						rus.addAll(doc.getRoot().getRevisionUnitOldAtLevel(0,
								oldIndex + 1));
					} else {
						oldSentence = "Add";
					}
				}

				for (Integer newIndex : newSelectIndices) {
					if (newIndex != -1) {
						newSentence += doc.getNewSentence(newIndex + 1) + "\n";
						rus.addAll(doc.getRoot().getRevisionUnitNewAtLevel(0,
								newIndex + 1));
					} else {
						newSentence = "Delete";
					}
				}

				currentRU = rus;
				annotateBox.reload(rus);

				if (rus == null || currentRU.size() == 0) {
					RevisionUnit newRU = new RevisionUnit(doc.getRoot());
					newRU.setRevision_level(0);
					// ArrayList<Integer> newList = new ArrayList<Integer>();
					// newList.add(newSelectIndex + 1);
					ArrayList<Integer> newList = new ArrayList<Integer>();
					for (Integer newI : newSelectIndices) {
						newList.add(newI+1);
					}

					ArrayList<Integer> oldList = new ArrayList<Integer>();
					for (Integer oldI : oldSelectIndices) {
						oldList.add(oldI+1);
					}
					newRU.setNewSentenceIndex(newList);
					newRU.setOldSentenceIndex(oldList);
					currentRU.add(newRU);
				}

				/*
				 * if (newSelectIndex == -1) { oldSentence =
				 * doc.getOldSentence(oldSelectIndex + 1); ArrayList<Integer>
				 * newIndexes = doc .getNewFromOld(oldSelectIndex + 1); if
				 * (newIndexes != null) { for (Integer newIndex : newIndexes) {
				 * if (newIndex != -1) { newSentence +=
				 * doc.getNewSentence(newIndex) + "\n"; } else { newSentence =
				 * "Delete"; } } ArrayList<RevisionUnit> rus = doc.getRoot()
				 * .getRevisionUnitOldAtLevel(0, oldSelectIndex + 1); currentRU
				 * = rus; annotateBox.reload(rus); if (rus == null ||
				 * currentRU.size() == 0) { RevisionUnit newRU = new
				 * RevisionUnit(doc.getRoot()); newRU.setRevision_level(0);
				 * ArrayList<Integer> oldList = new ArrayList<Integer>();
				 * oldList.add(oldSelectIndex + 1);
				 * newRU.setNewSentenceIndex(newIndexes);
				 * newRU.setOldSentenceIndex(oldList); currentRU.add(newRU); } }
				 * } else { newSentence = doc.getNewSentence(newSelectIndex +
				 * 1); ArrayList<Integer> oldIndexes = doc
				 * .getOldFromNew(newSelectIndex + 1); if (oldIndexes != null) {
				 * for (Integer oldIndex : oldIndexes) { if (oldIndex != -1) {
				 * oldSentence += doc.getOldSentence(oldIndex) + "\n"; } else {
				 * oldSentence = "Add"; } } ArrayList<RevisionUnit> rus =
				 * doc.getRoot() .getRevisionUnitNewAtLevel(0, newSelectIndex +
				 * 1); currentRU = rus; annotateBox.reload(rus); if (rus == null
				 * || currentRU.size() == 0) { RevisionUnit newRU = new
				 * RevisionUnit(doc.getRoot()); newRU.setRevision_level(0);
				 * ArrayList<Integer> newList = new ArrayList<Integer>();
				 * newList.add(newSelectIndex + 1);
				 * newRU.setNewSentenceIndex(newList);
				 * newRU.setOldSentenceIndex(oldIndexes); currentRU.add(newRU);
				 * } } }
				 */
				annotateContentDetail.setOldSentence(oldSentence);
				annotateContentDetail.setNewSentence(newSentence);
				if (oldSentence.trim().equals(newSentence.trim())) {
					annotateBox.setEnabled(false);
					annotateBox.display("Two sentences are identical");
				} else {
					annotateBox.setEnabled(true);
					annotateBox.display("Please annotate the revision");
				}
			}
		}
	}

	public AdvBaseLevelPanel(RevisionDocument doc) {
		String[] newSentences = doc.getNewSentencesArray();
		String[] oldSentences = doc.getOldSentencesArray();
		this.doc = doc;
		sentenceList = new JList(oldSentences);
		newSentenceList = new JList(newSentences);

		sentenceList.setVisibleRowCount(15);
		newSentenceList.setVisibleRowCount(15);
		ListSelectionHandler listHandler = new ListSelectionHandler();
		sentenceList.getSelectionModel().addListSelectionListener(listHandler);
		newSentenceList.getSelectionModel().addListSelectionListener(
				listHandler);
		JScrollPane pane = new JScrollPane(sentenceList);
		JScrollPane newPane = new JScrollPane(newSentenceList);
		//pane.setSize(this.getWidth() / 3, this.getHeight() / 2);
		//newPane.setSize(this.getWidth() / 3, this.getHeight() / 2);
		annotateBox = new AnnotateBox();
		
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, pane, newPane);
		splitPane.setDividerLocation(0.5);
		splitPane.setResizeWeight(.5d);
		sentenceBox = new Box(BoxLayout.X_AXIS);
		//sentenceBox.add(pane);
		//sentenceBox.add(newPane);
		sentenceBox.add(splitPane);
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
		changeAlignmentButton
				.setToolTipText("Not implemented yet, change the sentence alignment");
		changeAlignmentButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				/*JFrame frame = new JFrame("Change alignment");
				frame.setSize(800, 600);
				int[] oldIndices = sentenceList.getSelectedIndices();
				int[] newIndices = newSentenceList.getSelectedIndices();
				ArrayList<Integer> oldIndiceArr = new ArrayList<Integer>();
				ArrayList<Integer> newIndiceArr = new ArrayList<Integer>();
				for(Integer oldIndex: oldIndices) oldIndiceArr.add(oldIndex+1);
				for(Integer newIndex: newIndices) newIndiceArr.add(newIndex+1);
				
				frame.setContentPane(new AlignmentChangePanel(doc, oldIndiceArr, newIndiceArr));
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

	public void showAlign() {
		JFrame frame = new JFrame("Change alignment");
		frame.setSize(800, 600);
		int[] oldIndices = sentenceList.getSelectedIndices();
		int[] newIndices = newSentenceList.getSelectedIndices();
		ArrayList<Integer> oldIndiceArr = new ArrayList<Integer>();
		ArrayList<Integer> newIndiceArr = new ArrayList<Integer>();
		for(Integer oldIndex: oldIndices) oldIndiceArr.add(oldIndex+1);
		for(Integer newIndex: newIndices) newIndiceArr.add(newIndex+1);
		
		frame.setContentPane(new AlignmentChangePanel(doc, oldIndiceArr, newIndiceArr));
		frame.show();
	}
}
