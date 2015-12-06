package edu.pitt.lrdc.cs.revision.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.commons.lang.ArrayUtils;

import edu.pitt.cs.revision.util.RevisionMapFileGenerator;
import edu.pitt.lrdc.cs.revision.alignment.model.HeatMapUnit;
import edu.pitt.lrdc.cs.revision.gui.AdvBaseLevelPanelV2.ListSelectionHandler;
import edu.pitt.lrdc.cs.revision.model.RevisionDocument;
import edu.pitt.lrdc.cs.revision.model.RevisionOp;
import edu.pitt.lrdc.cs.revision.model.RevisionPurpose;
import edu.pitt.lrdc.cs.revision.model.RevisionUnit;

/**
 * Wrapps the lists of sentences
 * 
 * @author zhangfan
 *
 */

public class ColoredListWrapperV2 {
	private JList<MyListItem> oldSentenceList;
	private JList<MyListItem> newSentenceList;
	private Hashtable<Integer, ArrayList<Integer>> oldRealLookMapping;
	private Hashtable<Integer, ArrayList<Integer>> newRealLookMapping;
	private ArrayList<MyListItem> oldData;
	private ArrayList<MyListItem> newData;

	private RevisionDocument doc;
	private AdvBaseLevelPanelV4 parentPanel;

	public JList<MyListItem> getOldSentenceList() {
		return this.oldSentenceList;
	}

	public JList<MyListItem> getNewSentenceList() {
		return this.newSentenceList;
	}

	public ColoredListWrapperV2(RevisionDocument doc) {
		this.doc = doc;
		oldSentenceList = new JList<MyListItem>();
		newSentenceList = new JList<MyListItem>();
		oldRealLookMapping = new Hashtable<Integer, ArrayList<Integer>>();
		newRealLookMapping = new Hashtable<Integer, ArrayList<Integer>>();
		oldData = new ArrayList<MyListItem>();
		newData = new ArrayList<MyListItem>();
	}

	public ColoredListWrapperV2(AdvBaseLevelPanelV4 parent) {
		this.parentPanel = parent;
		this.doc = parent.doc;
		oldSentenceList = new JList<MyListItem>();
		newSentenceList = new JList<MyListItem>();
		oldSentenceList.setCellRenderer(new MyListItemRenderer());
		newSentenceList.setCellRenderer(new MyListItemRenderer());

		oldRealLookMapping = new Hashtable<Integer, ArrayList<Integer>>();
		newRealLookMapping = new Hashtable<Integer, ArrayList<Integer>>();
		oldData = new ArrayList<MyListItem>();
		newData = new ArrayList<MyListItem>();
	}

	public void addBlankLine() {
		MyListItem oldItem = new MyListItem();
		MyListItem newItem = new MyListItem();
		oldItem.indexOutside = oldData.size();
		oldItem.realIndexInDoc = -1;
		oldItem.revisionPurpose = -1;
		oldItem.sentenceStr = " ";
		oldItem.revisionOp = RevisionOp.NOCHANGE;

		newItem.indexOutside = newData.size();
		newItem.realIndexInDoc = -1;
		newItem.revisionPurpose = -1;
		newItem.sentenceStr = " ";
		newItem.revisionOp = RevisionOp.NOCHANGE;

		oldData.add(oldItem);
		newData.add(newItem);
	}

	public void changePurpose(RevisionUnit unit) {
		ArrayList<Integer> oldIndexes = unit.getOldSentenceIndex();
		ArrayList<Integer> newIndexes = unit.getNewSentenceIndex();

		HashSet<Integer> outOldIndices = new HashSet<Integer>();
		HashSet<Integer> newOutIndices = new HashSet<Integer>();

		for (Integer oldIndex : oldIndexes) {
			ArrayList<Integer> oldOuts = oldRealLookMapping.get(oldIndex);
			for (Integer oldOut : oldOuts) {
				outOldIndices.add(oldOut);
			}
		}
		for (Integer newIndex : newIndexes) {
			ArrayList<Integer> newOuts = newRealLookMapping.get(newIndex);
			for (Integer newOut : newOuts) {
				newOutIndices.add(newOut);
			}
		}
		for (Integer index : outOldIndices) {
			this.oldSentenceList.getModel().getElementAt(index).revisionPurpose = unit
					.getRevision_purpose();

		}
		for (Integer index : newOutIndices) {
			this.newSentenceList.getModel().getElementAt(index).revisionPurpose = unit
					.getRevision_purpose();
		}
	}

	public void addOldMapping(int realOldIndex, int outIndex) {
		ArrayList<Integer> mappings = oldRealLookMapping.get(realOldIndex);
		if (mappings == null) {
			mappings = new ArrayList<Integer>();
			oldRealLookMapping.put(realOldIndex, mappings);
		}
		mappings.add(outIndex);
	}

	public void addNewMapping(int realNewIndex, int outIndex) {
		ArrayList<Integer> mappings = newRealLookMapping.get(realNewIndex);
		if (mappings == null) {
			mappings = new ArrayList<Integer>();
			newRealLookMapping.put(realNewIndex, mappings);
		}
		mappings.add(outIndex);
	}

	public int[] addLine(HeatMapUnit unit, int currentOldRealIndex,
			int currentNewRealIndex) {
		MyListItem oldItem = new MyListItem();
		MyListItem newItem = new MyListItem();
		oldItem.indexOutside = oldData.size();
		oldItem.revisionOp = RevisionOp.getOpIndex(unit.getRType());
		newItem.revisionOp = RevisionOp.getOpIndex(unit.getRType());
		int[] newIndices = new int[2];
		if (unit.scD1 == null || unit.scD1.trim().length() == 0) {
			oldItem.realIndexInDoc = -1;
			oldItem.revisionPurpose = -1;
			oldItem.sentenceStr = " ";
			oldItem.revisionOp = RevisionOp.NOCHANGE;
			newIndices[0] = -1;
		} else {
			if (oldData.size() > 1
					&& unit.scD1
							.equals(oldData.get(oldData.size() - 1).sentenceStr)) {
				// This is the case of multiple alignment
				oldItem.sentenceStr = " ";
				oldItem.realIndexInDoc = currentOldRealIndex - 1;
				addOldMapping(currentOldRealIndex - 1, oldItem.indexOutside);
				if (unit.rPurpose.trim().length() > 0)
					oldItem.revisionPurpose = RevisionPurpose
							.getPurposeIndex(unit.rPurpose);
				else {
					if (unit.rPurposeOld.trim().length() > 0) {
						oldItem.revisionPurpose = RevisionPurpose
								.getPurposeIndex(unit.rPurposeOld);
					} else {
						oldItem.revisionPurpose = -1;
					}
				}
				newIndices[0] = -1;
			} else {
				oldItem.sentenceStr = unit.scD1;
				oldItem.realIndexInDoc = currentOldRealIndex;
				addOldMapping(currentOldRealIndex, oldItem.indexOutside);
				if (unit.rPurpose.trim().length() > 0)
					oldItem.revisionPurpose = RevisionPurpose
							.getPurposeIndex(unit.rPurpose);
				else {
					if (unit.rPurposeOld.trim().length() > 0) {
						oldItem.revisionPurpose = RevisionPurpose
								.getPurposeIndex(unit.rPurposeOld);
					} else {
						oldItem.revisionPurpose = -1;
					}
				}
				newIndices[0] = currentOldRealIndex + 1;
			}
		}

		newItem.indexOutside = newData.size();
		if (unit.scD2 == null || unit.scD2.trim().length() == 0) {
			newItem.realIndexInDoc = -1;
			newItem.revisionPurpose = -1;
			newItem.sentenceStr = " ";
			newItem.revisionOp = RevisionOp.NOCHANGE;
			newIndices[1] = -1;
		} else {
			if (newData.size() > 1
					&& unit.scD2
							.equals(newData.get(newData.size() - 1).sentenceStr)) {
				// This is the case of multiple alignment
				newItem.sentenceStr = " ";
				newItem.realIndexInDoc = currentNewRealIndex - 1;
				addNewMapping(currentNewRealIndex - 1, newItem.indexOutside);
				if (unit.rPurpose.trim().length() > 0)
					newItem.revisionPurpose = RevisionPurpose
							.getPurposeIndex(unit.rPurpose);
				else {
					if (unit.rPurposeNew.trim().length() > 0) {
						newItem.revisionPurpose = RevisionPurpose
								.getPurposeIndex(unit.rPurposeNew);
					} else {
						newItem.revisionPurpose = -1;
					}
				}
				newIndices[1] = -1;
			} else {
				newItem.sentenceStr = unit.scD2;
				newItem.realIndexInDoc = currentNewRealIndex;
				addNewMapping(currentNewRealIndex, newItem.indexOutside);
				if (unit.rPurpose.trim().length() > 0)
					newItem.revisionPurpose = RevisionPurpose
							.getPurposeIndex(unit.rPurpose);
				else {
					if (unit.rPurposeNew.trim().length() > 0) {
						newItem.revisionPurpose = RevisionPurpose
								.getPurposeIndex(unit.rPurposeNew);
					} else {
						newItem.revisionPurpose = -1;
					}
				}
				newIndices[1] = currentNewRealIndex + 1;
			}
		}

		oldData.add(oldItem);
		newData.add(newItem);
		return newIndices;
	}

	public void paint() {
		/*
		 * List<HeatMapUnit> units = RevisionMapFileGenerator
		 * .generateUnits4Tagging(doc);
		 */
		List<HeatMapUnit> units = RevisionMapFileGenerator
				.generateUnitsGeneric(doc);
		RevisionMapFileGenerator.adjustUnits(units);
		int realOldIndex = 1;
		int realNewIndex = 1;
		int lastAVR = 0;

		for (HeatMapUnit unit : units) {
			if (unit.aVR - lastAVR > 1)
				addBlankLine();
			lastAVR = unit.aVR;

			addLine(unit, unit.oldIndex, unit.newIndex);
			/*
			 * int[] indices = addLine(unit, realOldIndex, realNewIndex); if
			 * (indices[0] != -1) realOldIndex = indices[0]; if (indices[1] !=
			 * -1) realNewIndex = indices[1];
			 */
		}

		oldSentenceList.setListData(oldData.toArray(new MyListItem[oldData
				.size()]));
		newSentenceList.setListData(newData.toArray(new MyListItem[newData
				.size()]));
		ListSelectionHandler listHandler = new ListSelectionHandler();
		oldSentenceList.getSelectionModel().addListSelectionListener(
				listHandler);
		newSentenceList.getSelectionModel().addListSelectionListener(
				listHandler);
	}

	public ArrayList<Integer> getOldSelectedIndexes() {

		ArrayList<Integer> realOldIndices = new ArrayList<Integer>();
		if (!oldSentenceList.isSelectionEmpty()) {
			List<MyListItem> oldIndices = this.oldSentenceList
					.getSelectedValuesList();
			for (MyListItem item : oldIndices) {
				if (item.realIndexInDoc != -1)
					realOldIndices.add(item.realIndexInDoc);
			}
		}
		return realOldIndices;
	}

	public void selectOldSelectedIndices(ArrayList<Integer> oldIndices) {
		this.oldSentenceList.clearSelection();
		ArrayList<Integer> selectOldIndices = new ArrayList<Integer>();
		for (Integer oldIndex : oldIndices) {
			ArrayList<Integer> oldLookIndices = oldRealLookMapping
					.get(oldIndex);
			selectOldIndices.addAll(oldLookIndices);
		}

		int[] selectIndices = ArrayUtils.toPrimitive(selectOldIndices
				.toArray(new Integer[selectOldIndices.size()]));
		this.oldSentenceList
				.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		this.oldSentenceList.setSelectedIndices(selectIndices);
		this.oldSentenceList.ensureIndexIsVisible(oldSentenceList
				.getSelectedIndex());

	}

	public void selectNewSelectedIndices(ArrayList<Integer> newIndices) {
		this.newSentenceList.clearSelection();
		ArrayList<Integer> selectNewIndices = new ArrayList<Integer>();
		for (Integer newIndex : newIndices) {
			ArrayList<Integer> newLookIndices = newRealLookMapping
					.get(newIndex);
			selectNewIndices.addAll(newLookIndices);
		}

		int[] selectIndices = ArrayUtils.toPrimitive(selectNewIndices
				.toArray(new Integer[selectNewIndices.size()]));
		this.newSentenceList
				.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		this.newSentenceList.setSelectedIndices(selectIndices);
		this.newSentenceList.ensureIndexIsVisible(this.newSentenceList
				.getSelectedIndex());
	}

	public ArrayList<Integer> getNewSelectedIndexes() {

		ArrayList<Integer> realNewIndices = new ArrayList<Integer>();
		if (!newSentenceList.isSelectionEmpty()) {
			List<MyListItem> newIndices = this.newSentenceList
					.getSelectedValuesList();
			for (MyListItem item : newIndices) {
				if (item.realIndexInDoc != -1)
					realNewIndices.add(item.realIndexInDoc);
			}
		}
		return realNewIndices;
	}

	public void repaint() {
		oldSentenceList.removeAll();
		newSentenceList.removeAll();
		clearSelectionState();
		oldData.clear();
		newData.clear();
		oldRealLookMapping.clear();
		newRealLookMapping.clear();
		paint();
	}

	// boolean changeAlignment = false;
	// signal for select control
	boolean isOldSelected = false; // initially the signal is off, and once
									// clicked, the signal is turned on, when
									// the other list is triggered to be
									// selected, it will not trigger the
									// selection here since it turned on already
	boolean isNewSelected = false;

	public void clearSelectionState() {
		isOldSelected = false;
		isNewSelected = false;
	}

	boolean compareList(List<Integer> a1, List<Integer> a2) {
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

	
	public void removeNegativeOne(ArrayList<Integer> array) {
		int index = 0;
		while (index < array.size()) {
			if (array.get(index) == -1) {
				array.remove(index);
			} else {
				index++;
			}
		}
	}

	class ListSelectionHandler implements ListSelectionListener {
		private void changeTheSelection(ListSelectionEvent e) {

			ListSelectionModel lsm = (ListSelectionModel) e.getSource();

			if (lsm.equals(oldSentenceList.getSelectionModel())) {
				// System.out.println("Entered");
				// System.out.println("old");
				// int selectIndex = sentenceList.getSelectedIndex();
				if (!isOldSelected) {
					isOldSelected = true;
					if (!isNewSelected) {
						ArrayList<Integer> selectIndices = getOldSelectedIndexes();
						ArrayList<Integer> newIndexes = new ArrayList<Integer>();
						for (Integer selectIndex : selectIndices) {
							if (doc.getNewFromOld(selectIndex) != null)
								newIndexes.addAll(doc
										.getNewFromOld(selectIndex));
						}

						removeNegativeOne(newIndexes);
						if (newIndexes == null || newIndexes.size() == 0) {
							newSentenceList.clearSelection();
						} else {
							ArrayList<Integer> currentSelection = getNewSelectedIndexes();
							if (!compareList(newIndexes, currentSelection)) {
								Collections.sort(newIndexes);
								selectNewSelectedIndices(newIndexes);
							}
						}
					}
					isNewSelected = false;
					isOldSelected = false;
				}
			} else if (lsm.equals(newSentenceList.getSelectionModel())) {
				// System.out.println("new");
				// int selectIndex = newSentenceList.getSelectedIndex();
				if (!isNewSelected) {
					isNewSelected = true;

					if (!isOldSelected) {
						ArrayList<Integer> selectIndices = getNewSelectedIndexes();
						ArrayList<Integer> oldIndexes = new ArrayList<Integer>();
						for (Integer selectIndex : selectIndices) {
							if (doc.getOldFromNew(selectIndex) != null) {
								oldIndexes.addAll(doc
										.getOldFromNew(selectIndex));
							}
						}
						removeNegativeOne(oldIndexes);
						if (oldIndexes == null || oldIndexes.size() == 0) {
							oldSentenceList.clearSelection();
						} else {
							ArrayList<Integer> currentSelection = getOldSelectedIndexes();
							if (!compareList(oldIndexes, currentSelection)) {
								Collections.sort(oldIndexes);
								selectOldSelectedIndices(oldIndexes);
							}
						}
					}
					isOldSelected = false;
					isNewSelected = false;
				}
			}
		}

		public void valueChanged(ListSelectionEvent e) {
			ListSelectionModel lsm = (ListSelectionModel) e.getSource();
			if (!lsm.isSelectionEmpty()) {
				parentPanel.registerRevision();
			}
			//
			// First treat the ones that have been treated
			changeTheSelection(e);

			if (lsm.isSelectionEmpty()) {
				//
			} else {
				// Find out which indexes are selected.
				// int maxIndex = lsm.getMaxSelectionIndex();

				String newSentence = "";
				String oldSentence = "";

				ArrayList<Integer> oldSelectIndices = getOldSelectedIndexes();
				ArrayList<Integer> newSelectIndices = getNewSelectedIndexes();

				ArrayList<RevisionUnit> rus = new ArrayList<RevisionUnit>();
				for (Integer oldIndex : oldSelectIndices) {
					if (oldIndex != -1) {
						oldSentence += doc.getOldSentence(oldIndex) + " ";
						rus.addAll(doc.getRoot().getRevisionUnitOldAtLevel(0,
								oldIndex));
					} else {
						oldSentence = "Add";
					}
				}
				parentPanel.highlightOld = oldSentence.trim();
				if (parentPanel.highlightOld.equals("Add"))
					parentPanel.highlightOld = "";

				for (Integer newIndex : newSelectIndices) {
					if (newIndex != -1) {
						newSentence += doc.getNewSentence(newIndex) + " ";
						rus.addAll(doc.getRoot().getRevisionUnitNewAtLevel(0,
								newIndex));
					} else {
						newSentence = "Delete";
					}
				}
				parentPanel.highlightNew = newSentence.trim();
				if (parentPanel.highlightNew.equals("Delete"))
					parentPanel.highlightNew = "";

				parentPanel.currentRU = rus;
				parentPanel.annotateBox.reload(rus);

				if (rus == null || parentPanel.currentRU.size() == 0) {
					RevisionUnit newRU = new RevisionUnit(doc.getRoot());
					newRU.setRevision_level(0);
					// ArrayList<Integer> newList = new ArrayList<Integer>();
					// newList.add(newSelectIndex + 1);
					ArrayList<Integer> newList = new ArrayList<Integer>();
					for (Integer newI : newSelectIndices) {
						newList.add(newI);
					}

					ArrayList<Integer> oldList = new ArrayList<Integer>();
					for (Integer oldI : oldSelectIndices) {
						oldList.add(oldI);
					}
					newRU.setNewSentenceIndex(newList);
					newRU.setOldSentenceIndex(oldList);
					if (newList == null || newList.size() == 0)
						newRU.setRevision_op(RevisionOp.DELETE);
					else if (oldList == null || oldList.size() == 0)
						newRU.setRevision_op(RevisionOp.ADD);
					else
						newRU.setRevision_op(RevisionOp.MODIFY);
					parentPanel.currentRU.add(newRU);
				}

				parentPanel.annotateContentDetail.setOldSentence(oldSentence);
				parentPanel.annotateContentDetail.setNewSentence(newSentence);
				if (oldSentence.trim().equals(newSentence.trim())) {
					parentPanel.annotateBox.setEnabled(false);
					parentPanel.annotateBox
							.display("Two sentences are identical");
				} else {
					parentPanel.annotateBox.setEnabled(true);
					parentPanel.annotateBox
							.display("Please annotate the revision");
					parentPanel.showReviews();
					/*
					HashSet<Integer> reviews = null;
					for (Integer oldIndex : oldSelectIndices) {
						HashSet<Integer> temp = parentPanel.reviewDoc
								.getReviewsOld(oldIndex);
						if (temp != null) {
							if (reviews == null) {
								reviews = temp;
							} else {
								for (Integer t : temp) {
									reviews.add(t);
								}
							}
						}
					}
					for (Integer newIndex : newSelectIndices) {
						HashSet<Integer> temp = parentPanel.reviewDoc
								.getReviewsOld(newIndex);
						if (temp != null) {
							if (reviews == null) {
								reviews = temp;
							} else {
								for (Integer t : temp) {
									reviews.add(t);
								}
							}
						}
					}
				*/

				}
			}
			parentPanel.highlight();
		}
	}
}
