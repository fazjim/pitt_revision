package edu.pitt.lrdc.cs.revision.gui;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Hashtable;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import edu.pitt.lrdc.cs.revision.model.RevisionDocument;
import edu.pitt.lrdc.cs.revision.model.RevisionUnit;

public class AlignmentChangePanelV2P extends JPanel {
	private JList<MyListItem> oldSentenceList;
	private JList<MyListItem> newSentenceList;
	//private JList unHandledList;
	private JTextArea selectedSentence;
	private JButton confirmButton;
	private JButton cancelButton;
	//private JButton removeButton;
	private RevisionDocument doc;
	private ColoredListWrapperV2 clw;
	private AlignmentListWrapper ownWrapper;
	
	public void setListWrapper(ColoredListWrapperV2 clw) {
		this.clw = clw;
	}

	Hashtable<String, Integer> sentenceIndex = new Hashtable<String, Integer>();


	public AlignmentChangePanelV2P(RevisionDocument doc) {
		this.doc = doc;
		ownWrapper = new AlignmentListWrapper(doc);
		selectedSentence = new JTextArea();
		ownWrapper.setDisplay(selectedSentence);
		confirmButton = new JButton("Confirm new alignment");
		confirmButton.setToolTipText("Confirm and save the current selection");
		cancelButton = new JButton("Remove alignment");
		cancelButton.setToolTipText("Cancel the current selections");
		
		oldSentenceList = ownWrapper.getOldSentenceList();
		newSentenceList = ownWrapper.getNewSentenceList();
		ownWrapper.paint();
		JScrollPane pane = new JScrollPane(oldSentenceList);
		JScrollPane newPane = new JScrollPane(newSentenceList);
		
		Box sentenceBox = new Box(BoxLayout.X_AXIS);
		sentenceBox.add(pane);
		sentenceBox.add(newPane);
		
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		Box buttonBox = new Box(BoxLayout.X_AXIS);
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
				remove();
			}
			
		});
		buttonBox.add(confirmButton);
		buttonBox.add(cancelButton);
		add(sentenceBox);
		add(buttonBox);
		add(selectedSentence);
	}

	

	public void close() {
		JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
		topFrame.dispatchEvent(new WindowEvent(topFrame, WindowEvent.WINDOW_CLOSING));
		clw.repaint();
	}
	
	public void save() {
		boolean saveSuccess = ownWrapper.changeAlignment();
		if(saveSuccess) {
			ownWrapper.repaint();
			ownWrapper.changeAlignment = false;
			clw.repaint();
		} else {
			JOptionPane.showMessageDialog(this, "N to N alignment not allowed");
		}
	}

	
	public void remove() {
		ownWrapper.removeAlignment();
		ownWrapper.repaint();
		ownWrapper.changeAlignment = false;
		clw.repaint();
	}
}
