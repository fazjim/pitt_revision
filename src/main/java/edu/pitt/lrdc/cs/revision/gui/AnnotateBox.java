package edu.pitt.lrdc.cs.revision.gui;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import edu.pitt.lrdc.cs.revision.model.RevisionOp;
import edu.pitt.lrdc.cs.revision.model.RevisionPurpose;
import edu.pitt.lrdc.cs.revision.model.RevisionUnit;

public class AnnotateBox extends JPanel{
	private JLabel statusDisplay = new JLabel("--------------------------------");
	private JTextArea reviewDisplay = new JTextArea();
	private Hashtable<String,EditUnitV2> table = new Hashtable<String,EditUnitV2>();
	
	private JPanel surfaceEUGroup;
	private JPanel contentEUGroup;
	private JPanel bonusGroup;
	
	public void loadTable() {
		for(int i = RevisionPurpose.START;i<=RevisionPurpose.END;i++) {
			String purposeName = RevisionPurpose.getPurposeName(i);
			table.put(purposeName, new EditUnitV2(purposeName,ColorConstants.getColor(i)));
		}
		String purposeName = RevisionPurpose.getPurposeName(RevisionPurpose.PRECISION);
		table.put(purposeName, new EditUnitV2(purposeName,ColorConstants.getColor(RevisionPurpose.PRECISION)));
		purposeName = RevisionPurpose.getPurposeName(RevisionPurpose.UNKNOWN);
		table.put(purposeName, new EditUnitV2(purposeName,ColorConstants.getColor(RevisionPurpose.UNKNOWN)));
	}
	
	public void display(String text) {
		this.statusDisplay.setText(text);
	}
	
	public void displayReviews(String text) {
		this.reviewDisplay.setText(text);
	}
	
	public AnnotateBox() {
		this.setLayout(new GridLayout(0,1));
		loadTable();
		//Iterator<String> it = table.keySet().iterator();
		add(statusDisplay);
		JLabel dummyDisplay = new JLabel("-----------------------------");
		add(dummyDisplay);
		reviewDisplay.setRows(3);
		JScrollPane spReview = new JScrollPane(reviewDisplay);
		add(spReview);
		//while(it.hasNext()) {
		//	String purposeName = it.next();
		//	add(table.get(purposeName));
		//}
		
		surfaceEUGroup = new JPanel();
		surfaceEUGroup.setBorder(BorderFactory.createTitledBorder("Surface Revisions"));
		bonusGroup = new JPanel();
		bonusGroup.setBorder(BorderFactory.createTitledBorder("Surface Bonus"));
		contentEUGroup = new JPanel();
		contentEUGroup.setBorder(BorderFactory.createTitledBorder("Content Revisions"));
		
		/*for(int i = RevisionPurpose.START;i<=RevisionPurpose.END;i++) {
			String purposeName = RevisionPurpose.getPurposeName(i);
			add(table.get(purposeName));
		}*/
		Box boxSurface = new Box(BoxLayout.X_AXIS);
		for(int i = RevisionPurpose.END;i>RevisionPurpose.WORDUSAGE_CLARITY_CASCADED;i--) {
			String purposeName = RevisionPurpose.getPurposeName(i);
			boxSurface.add(table.get(purposeName));
			boxSurface.add(Box.createRigidArea(new Dimension(5,0)));
		}
		surfaceEUGroup.add(boxSurface);
		Box bonusSurface = new Box(BoxLayout.X_AXIS);
		bonusSurface.add(table.get(RevisionPurpose.getPurposeName(RevisionPurpose.WORDUSAGE_CLARITY_CASCADED)));
		bonusSurface.add(Box.createRigidArea(new Dimension(5,0)));
		bonusSurface.add(table.get(RevisionPurpose.getPurposeName(RevisionPurpose.PRECISION)));
		bonusSurface.add(Box.createRigidArea(new Dimension(5,0)));
		bonusSurface.add(table.get(RevisionPurpose.getPurposeName(RevisionPurpose.UNKNOWN)));
		bonusSurface.add(Box.createRigidArea(new Dimension(5,0)));
		bonusGroup.add(bonusSurface);
		
		Box boxContent = new Box(BoxLayout.X_AXIS);
		for(int i = RevisionPurpose.START;i<RevisionPurpose.WORDUSAGE_CLARITY_CASCADED;i++) {
			String purposeName = RevisionPurpose.getPurposeName(i);
			boxContent.add(table.get(purposeName));
			boxContent.add(Box.createRigidArea(new Dimension(5,0)));
		}
		contentEUGroup.add(boxContent);
		
		add(surfaceEUGroup);
		add(bonusGroup);
		add(contentEUGroup);
	}
	
	public void setEnabled(boolean enabled) {
		Iterator<String> it = table.keySet().iterator();
		while(it.hasNext()) {
			table.get(it.next()).setEnabled(enabled);
		}
	}
	
	/**
	 * Reload the options
	 * @param rus
	 */
	public void reload(ArrayList<RevisionUnit> rus) {
		Iterator<String> it = table.keySet().iterator();
		while(it.hasNext()) {
			table.get(it.next()).reload(-1);
		}
		for(int i = 0;i<rus.size();i++) {
			RevisionUnit ru = rus.get(i);
			String name = RevisionPurpose.getPurposeName(ru.getRevision_purpose());
			table.get(name).reload(ru.getRevision_op());
		}
	}
	
	public ArrayList<SelectionUnit> getSelectedUnits() {
		ArrayList<SelectionUnit> sus = new ArrayList<SelectionUnit>();
		
		Iterator<String> it = table.keySet().iterator();
		while(it.hasNext()) {
			String key = it.next();
			int revision_purpose = RevisionPurpose.getPurposeIndex(key);
			EditUnitV2 eu = table.get(key);
			if(eu.isSelected()) {
				SelectionUnit su = new SelectionUnit(-1,revision_purpose);
				sus.add(su);
			}
		}
		
		return sus;
	}
}
