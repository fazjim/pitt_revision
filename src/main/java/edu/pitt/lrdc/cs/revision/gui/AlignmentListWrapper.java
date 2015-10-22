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
import javax.swing.JTextArea;
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

class AlignmentListItemRenderer extends JTextField implements
		ListCellRenderer<MyListItem> {
	public boolean paintColor = false;

	public void setPaintColor(boolean paintColor) {
		this.paintColor = paintColor;
	}

	@Override
	public Component getListCellRendererComponent(
			JList<? extends MyListItem> list, MyListItem item, int index,
			boolean isSelected, boolean cellHasFocus) {
		String sentenceStr = item.sentenceStr;
		setText(sentenceStr);
		setOpaque(true);
		if (paintColor == true) {
			if (item.revisionPurpose >= RevisionPurpose.START
					&& item.revisionPurpose <= RevisionPurpose.END) {
				// setForeground(ColorConstants.getColor(item.revisionPurpose));
				setBackground(ColorConstants.getColor(item.revisionPurpose));
			} else {
				if (item.revisionOp != RevisionOp.NOCHANGE) {
					setBackground(Color.GRAY);
				} else {
					setBackground(Color.white);
				}
			}
		} else {
			setBackground(Color.white);
		}
		if (isSelected) {
			Font font = new Font("Courier", Font.BOLD, 14);
			setFont(font);
			// setBackground(Color.cyan);
		} else {
			Font font = UIManager.getDefaults().getFont("TabbedPane.font");
			setFont(font);
		}
		// setSelectionColor(Color.BLUE);
		// getCaret().setSelectionVisisble(true);
		return this;
	}

}

public class AlignmentListWrapper {
	private JList<MyListItem> oldSentenceList;
	private JList<MyListItem> newSentenceList;
	private Hashtable<Integer, ArrayList<Integer>> oldRealLookMapping;
	private Hashtable<Integer, ArrayList<Integer>> newRealLookMapping;
	private ArrayList<MyListItem> oldData;
	private ArrayList<MyListItem> newData;
	private JTextArea displayArea;

	private RevisionDocument doc;
	//private AdvBaseLevelPanelV3 parentPanel;

	public void setDisplay(JTextArea display) {
		this.displayArea = display;
		display.setLineWrap(true);
	}
	
	public JList<MyListItem> getOldSentenceList() {
		return this.oldSentenceList;
	}

	public JList<MyListItem> getNewSentenceList() {
		return this.newSentenceList;
	}

	public AlignmentListWrapper(RevisionDocument doc) {
		this.doc = doc;
		oldSentenceList = new JList<MyListItem>();
		newSentenceList = new JList<MyListItem>();
		oldSentenceList.setVisibleRowCount(25);
		newSentenceList.setVisibleRowCount(25);
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
				else
					oldItem.revisionPurpose = -1;
				newIndices[0] = -1;
			} else {
				oldItem.sentenceStr = unit.scD1;
				oldItem.realIndexInDoc = currentOldRealIndex;
				addOldMapping(currentOldRealIndex, oldItem.indexOutside);
				if (unit.rPurpose.trim().length() > 0)
					oldItem.revisionPurpose = RevisionPurpose
							.getPurposeIndex(unit.rPurpose);
				else
					oldItem.revisionPurpose = -1;
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
				else
					newItem.revisionPurpose = -1;
				newIndices[1] = -1;
			} else {
				newItem.sentenceStr = unit.scD2;
				newItem.realIndexInDoc = currentNewRealIndex;
				addNewMapping(currentNewRealIndex, newItem.indexOutside);
				if (unit.rPurpose.trim().length() > 0)
					newItem.revisionPurpose = RevisionPurpose
							.getPurposeIndex(unit.rPurpose);
				else
					newItem.revisionPurpose = -1;
				newIndices[1] = currentNewRealIndex + 1;
			}
		}

		oldData.add(oldItem);
		newData.add(newItem);
		return newIndices;
	}

	public void paint() {
		List<HeatMapUnit> units = RevisionMapFileGenerator
				.generateUnits4Tagging(doc);
		RevisionMapFileGenerator.adjustUnits(units);
		int realOldIndex = 1;
		int realNewIndex = 1;
		int lastAVR = 0;

		for (HeatMapUnit unit : units) {
			if (unit.aVR - lastAVR > 1)
				addBlankLine();
			lastAVR = unit.aVR;
			int[] indices = addLine(unit, realOldIndex, realNewIndex);
			if (indices[0] != -1)
				realOldIndex = indices[0];
			if (indices[1] != -1)
				realNewIndex = indices[1];
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
		// this.oldSentenceList.clearSelection();
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
		// this.newSentenceList.clearSelection();
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

	boolean changeAlignment = false;
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

	public boolean changeAlignment() {
		ArrayList<Integer> oldIndices = this.getOldSelectedIndexes();
		ArrayList<Integer> newIndices = this.getNewSelectedIndexes();
		removeNegativeOne(oldIndices);
		removeNegativeOne(newIndices);
		if(oldIndices.size()==1) {
			doc.changeOldAlignment(oldIndices.get(0), newIndices);
		} else if(newIndices.size()==1){
			doc.changeNewAlignment(newIndices.get(0), oldIndices);
		} else {
			return false;
		}
		doc.check();
		return true;
	}
	
	public boolean removeAlignment() {
		ArrayList<Integer> oldIndices = this.getOldSelectedIndexes();
		ArrayList<Integer> newIndices = this.getNewSelectedIndexes();
		removeNegativeOne(oldIndices);
		removeNegativeOne(newIndices);
		for(Integer oldIndex:oldIndices) {
			doc.changeOldAlignment(oldIndex, new ArrayList<Integer>());
		}
		for(Integer newIndex:newIndices) {
			doc.changeNewAlignment(newIndex, new ArrayList<Integer>());
		}
		doc.check();
		return true;
	}
	
	class ListSelectionHandler implements ListSelectionListener {
		private void changeTheSelection(ListSelectionEvent e) {
			ListSelectionModel lsm = (ListSelectionModel) e.getSource();
			if (!changeAlignment) {
				changeAlignment = true;
				if (lsm.equals(oldSentenceList.getSelectionModel())) {
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
				} else if (lsm.equals(newSentenceList.getSelectionModel())) {
			
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
			} else {
				// changing alignment
			}
			// highlight();
		}
		public void valueChanged(ListSelectionEvent e) {
			ListSelectionModel lsm = (ListSelectionModel) e.getSource();
			changeTheSelection(e);
			String oldSent = doc.getOldSentences(getOldSelectedIndexes());
			String newSent = doc.getNewSentences(getNewSelectedIndexes());
			displayArea.setText("OLD:\n"+oldSent+"\n\nNEW:\n"+newSent);
		}
	}
}
