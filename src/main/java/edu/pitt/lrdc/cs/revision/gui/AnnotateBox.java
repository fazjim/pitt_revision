package edu.pitt.lrdc.cs.revision.gui;

import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import edu.pitt.lrdc.cs.revision.model.RevisionOp;
import edu.pitt.lrdc.cs.revision.model.RevisionPurpose;
import edu.pitt.lrdc.cs.revision.model.RevisionUnit;

public class AnnotateBox extends JPanel{
	private JLabel statusDisplay = new JLabel("--------------------------------");
	private Hashtable<String,EditUnit> table = new Hashtable<String,EditUnit>();
	
	public void loadTable() {
		for(int i = RevisionPurpose.START;i<=RevisionPurpose.END;i++) {
			String purposeName = RevisionPurpose.getPurposeName(i);
			table.put(purposeName, new EditUnit(purposeName,ColorConstants.getColor(i)));
		}
	}
	
	public void display(String text) {
		this.statusDisplay.setText(text);
	}
	
	public AnnotateBox() {
		this.setLayout(new GridLayout(0,1));
		loadTable();
		//Iterator<String> it = table.keySet().iterator();
		add(statusDisplay);
		JLabel dummyDisplay = new JLabel("-----------------------------");
		add(dummyDisplay);
		//while(it.hasNext()) {
		//	String purposeName = it.next();
		//	add(table.get(purposeName));
		//}
		for(int i = RevisionPurpose.START;i<=RevisionPurpose.END;i++) {
			String purposeName = RevisionPurpose.getPurposeName(i);
			add(table.get(purposeName));
		}
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
			EditUnit eu = table.get(key);
			if(eu.getSelectedOp() != -1) {
				SelectionUnit su = new SelectionUnit(eu.getSelectedOp(),revision_purpose);
				sus.add(su);
			}
		}
		
		return sus;
	}
}
