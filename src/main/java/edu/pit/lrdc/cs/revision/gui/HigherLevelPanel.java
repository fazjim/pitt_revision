package edu.pit.lrdc.cs.revision.gui;

/**
 * Higher level panel for annotation
 * @author zhangfan
 * 
 * @version 1.1
 * Change the layout
 */
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.ArrayList;
import java.util.Hashtable;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import edu.pitt.lrdc.cs.revision.model.RevisionDocument;
import edu.pitt.lrdc.cs.revision.model.RevisionOp;
import edu.pitt.lrdc.cs.revision.model.RevisionPurpose;
import edu.pitt.lrdc.cs.revision.model.RevisionUnit;

public class HigherLevelPanel extends JPanel implements LevelPanel {
	private JList candidates = new JList(); // candidates that can be merged
	private JButton mergeButton = new JButton("Merge"); // button for merging
														// candidates to the
														// specified revision
														// unit
	private JButton createNewButton = new JButton("Create New");
	private JButton removeButton = new JButton("Remove");

	private JList currentLevelUnits = new JList(); // the list of units at the
													// current level
	private JButton dismissButton = new JButton("Release"); // dismiss
															// the
															// specified
															// revision
															// unit
															// from
															// the
															// current
															// unit
	private JList childList = new JList(); // the units of this revision unit
	private RevisionDocument doc;
	private LevelDemoPanel ldp;
	private int currentLevel;

	private Hashtable<String, RevisionUnit> ruTable = new Hashtable<String, RevisionUnit>();

	HigherAnnotateBox annotateBox = new HigherAnnotateBox();

	ContentDemoBox demoBox = new ContentDemoBox(BoxLayout.Y_AXIS);

	private RevisionUnit currentRevision = null;

	public void loadCandidates() {
		ArrayList<RevisionUnit> candidateUnits = doc.getRoot()
				.getCandidateUnitsAtLevel(currentLevel);
		ArrayList<String> labels = new ArrayList<String>();

		for (int i = candidateUnits.size() - 1; i >= 0; i--) {
			RevisionUnit ru = candidateUnits.get(i);
			String label = ru.getLabel();
			ruTable.put(label, ru);
			labels.add(label);
		}

		if (labels.size() == 0) {
			String[] labelArr = new String[1];
			labelArr[0] = "No units at this level";
			candidates.setListData(labelArr);
		} else {
			String[] labelArr = new String[labels.size()];
			labelArr = labels.toArray(labelArr);
			candidates.setListData(labelArr);
		}
	}

	public void loadCurrents() {
		ArrayList<RevisionUnit> candidateUnits = doc.getRoot()
				.getRevisionUnitAtLevel(currentLevel);
		ArrayList<String> labels = new ArrayList<String>();

		for (int i = candidateUnits.size() - 1; i >= 0; i--) {
			RevisionUnit ru = candidateUnits.get(i);
			String label = ru.getLabel();
			ruTable.put(label, ru);
			labels.add(label);
		}
		if (labels.size() == 0) {
			String[] labelArr = new String[1];
			labelArr[0] = "No units at this level";
			currentLevelUnits.setListData(labelArr);
		} else {
			String[] labelArr = new String[labels.size()];
			labelArr = labels.toArray(labelArr);
			currentLevelUnits.setListData(labelArr);
		}
	}

	public void loadChilds(RevisionUnit selected) {
		ArrayList<RevisionUnit> candidateUnits = selected.getUnits();
		ArrayList<String> labels = new ArrayList<String>();

		for (int i = 0; i < candidateUnits.size(); i++) {
			RevisionUnit ru = candidateUnits.get(i);
			if (ru.getParent_level() == selected.getRevision_level()) {
				String label = ru.getLabel();
				ruTable.put(label, ru);
				labels.add(label);
			}
		}
		if (labels.size() == 0) {
			String[] labelArr = new String[1];
			labelArr[0] = "No units at this level";
			childList.setListData(labelArr);
		} else {
			String[] labelArr = new String[labels.size()];
			labelArr = labels.toArray(labelArr);
			childList.setListData(labelArr);
		}
	}

	private boolean firstSelected = true;

	class ListSelectionHandler implements ListSelectionListener {
		public void valueChanged(ListSelectionEvent e) {
			registerRevision();
			if (currentLevelUnits.getSelectedValue() != null) {
				String label = currentLevelUnits.getSelectedValue().toString();
				if (ruTable.containsKey(label)) {
					RevisionUnit ru = ruTable.get(label);
					loadChilds(ru);
					annotateBox.loadRevisionUnit(ru);
					demoBox.setText(ru.getDetailContent(doc));
					currentRevision = ru;

					// firstSelected = false;
				}
			}

		}
	}

	class CandidateListSelectionHandler implements ListSelectionListener {
		public void valueChanged(ListSelectionEvent e) {
			if (candidates.getSelectedValue() != null) {
				String label = candidates.getSelectedValue().toString();
				if (ruTable.containsKey(label)) {
					RevisionUnit ru = ruTable.get(label);
					demoBox.setText(ru.getDetailContent(doc));
				}
			}
		}
	}

	class ChildListSelectionHandler implements ListSelectionListener {
		public void valueChanged(ListSelectionEvent e) {
			if (childList.getSelectedValue() != null) {
				String label = childList.getSelectedValue().toString();
				if (ruTable.containsKey(label)) {
					RevisionUnit ru = ruTable.get(label);
					demoBox.setText(ru.getDetailContent(doc));
				}
			}
		}
	}

	class NewRUListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			RevisionUnit ru = new RevisionUnit(doc.getRoot());
			ru.setRevision_level(currentLevel);
			ru.setParent_level(Integer.MAX_VALUE);
			ru.setRevision_index(doc.getRoot()
					.getNextIndexAtLevel(currentLevel));
			doc.getRoot().addUnit(ru);
			loadCurrents();
		}

	}

	class MergeListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			if (candidates.getSelectedValue() != null
					&& currentLevelUnits.getSelectedValue() != null) {
				String mergeUnitLabel = candidates.getSelectedValue()
						.toString();
				String parentUnitLabel = currentLevelUnits.getSelectedValue()
						.toString();

				if (ruTable.containsKey(mergeUnitLabel)
						&& ruTable.containsKey(parentUnitLabel)) {
					RevisionUnit mergeUnit = ruTable.get(mergeUnitLabel);
					RevisionUnit parentUnit = ruTable.get(parentUnitLabel);
					parentUnit.addUnit(mergeUnit);
					mergeUnit.setParent_index(parentUnit.getRevision_index());
					mergeUnit.setParent_level(parentUnit.getRevision_level());

					loadCandidates();
					loadCurrents();
					loadChilds(parentUnit);
				}
			}
		}

	}

	class RemoveListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			if (candidates.getSelectedValue() != null) {
				String rmUnitLabel = candidates.getSelectedValue().toString();

				if (ruTable.containsKey(rmUnitLabel)) {
					RevisionUnit rmUnit = ruTable.get(rmUnitLabel);
					doc.getRoot().removeUnit(rmUnit);

					loadCandidates();
				}
			}
		}

	}

	class ReleaseListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			if (currentLevelUnits.getSelectedValue() != null
					&& childList.getSelectedValue() != null) {
				String releaseUnitLabel = childList.getSelectedValue()
						.toString();
				String parentUnitLabel = currentLevelUnits.getSelectedValue()
						.toString();

				if (ruTable.containsKey(releaseUnitLabel)
						&& ruTable.containsKey(parentUnitLabel)) {
					RevisionUnit releaseUnit = ruTable.get(releaseUnitLabel);
					RevisionUnit parentUnit = ruTable.get(parentUnitLabel);
					parentUnit.getUnits().remove(releaseUnit);
					releaseUnit.release();

					loadCandidates();
					loadCurrents();
					loadChilds(parentUnit);
				}
			}
		}

	}

	public HigherLevelPanel(RevisionDocument doc, int level) {
		this.doc = doc;
		this.currentLevel = level;
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		Box candidateBox = new Box(BoxLayout.X_AXIS);
		loadCandidates();
		loadCurrents();

		Border loweredetched = BorderFactory
				.createEtchedBorder(EtchedBorder.LOWERED);
		TitledBorder title = BorderFactory.createTitledBorder(loweredetched,
				"Unassigned revisions to this level");
		title.setTitleJustification(TitledBorder.LEFT);
		// candidates.setBorder(title);
		candidates
				.addListSelectionListener(new CandidateListSelectionHandler());
		JScrollPane spCandidate = new JScrollPane(candidates);
		spCandidate.setSize(100, 300);
		candidateBox.add(spCandidate);
		Box bbBox = new Box(BoxLayout.Y_AXIS);
		bbBox.add(mergeButton);
		bbBox.add(removeButton);
		candidateBox.add(bbBox);
		candidateBox.setBorder(title);

		Box currentBox = new Box(BoxLayout.X_AXIS);
		Box buttonBox = new Box(BoxLayout.Y_AXIS);
		buttonBox.add(createNewButton);
		buttonBox.add(annotateBox);
		// listBox.add(mergeButton);
		// listBox.add(createNewButton);

		createNewButton.addActionListener(new NewRUListener());
		createNewButton
				.setToolTipText("Creates a new Revision Unit to group the candidate revisions");
		mergeButton.addActionListener(new MergeListener());
		mergeButton
				.setToolTipText("Merge the selected candidate to the selected group");
		removeButton.addActionListener(new RemoveListener());
		removeButton
				.setToolTipText("Remove the selected candidate, the candidate will not show up again");

		TitledBorder title2 = BorderFactory.createTitledBorder(loweredetched,
				"Unassigned revisions to this level");
		title2.setTitleJustification(TitledBorder.LEFT);
		title2.setTitle("Revision Units at this level");
		// currentLevelUnits.setBorder(title2);
		currentLevelUnits.addListSelectionListener(new ListSelectionHandler());
		JScrollPane spCurrent = new JScrollPane(currentLevelUnits);
		spCurrent.setSize(100, 300);

		currentBox.add(spCurrent);
		currentBox.add(buttonBox);
		currentBox.setBorder(title2);
		Box childBox = new Box(BoxLayout.X_AXIS);
		TitledBorder title3 = BorderFactory.createTitledBorder(loweredetched,
				"Unassigned revisions to this level");
		title3.setTitleJustification(TitledBorder.LEFT);
		title3.setTitle("Members of the selected revision unit");
		// childList.setBorder(title3);
		childList.addListSelectionListener(new ChildListSelectionHandler());
		JScrollPane spChild = new JScrollPane(childList);
		spChild.setSize(100, 300);

		dismissButton
				.setToolTipText("Release the selected child from the current group to candidates");
		dismissButton.addActionListener(new ReleaseListener());
		childBox.add(spChild);
		childBox.add(dismissButton);
		childBox.setBorder(title3);

		ldp = new LevelDemoPanel(doc, currentLevel);
		ldp.boundPanel(this);
		add(candidateBox);
		add(currentBox);
		add(childBox);
		add(demoBox);
		add(ldp);
	}

	@Override
	public void registerRevision() {
		// TODO Auto-generated method stub
		if (currentRevision != null) {
			currentRevision.setRevision_op(annotateBox.getSelectedOp());
			currentRevision.setRevision_purpose(annotateBox
					.getSelectedPurpose());
			// if (currentLevelUnits.getSelectedValue() != null) {
			// int selectedIndex = currentLevelUnits.getSelectedIndex();
			// loadCurrents();
			// currentLevelUnits.setSelectedIndex(selectedIndex);
			// }
			// loadCurrents();
		}
	}
}
