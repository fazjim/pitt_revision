package edu.pitt.lrdc.cs.revision.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.*;

import edu.pitt.lrdc.cs.revision.model.RevisionDocument;
import edu.pitt.lrdc.cs.revision.model.RevisionUnit;



/**
 * Buttons that link to other levels
 * @author zhangfan
 *
 */
public class LevelDemoPanel extends JPanel{
	class WindowActionListener implements ActionListener {
		RevisionDocument doc;
		int level;
		WindowActionListener(RevisionDocument doc, int level) {
			this.doc = doc;
			this.level = level;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			boundedPanel.registerRevision();
			new HigherLevelFrame(doc,level).show(); 
		}
	}
	
	private ArrayList<JButton> buttonList;
	
	private LevelPanel boundedPanel;
	private RevisionDocument doc;
	
	public void boundPanel(LevelPanel panel) {
		this.boundedPanel = panel;
	}
	
	class AddLevelListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			doc.getRoot().setRevision_level(doc.getRoot().getRevision_level()+1);
			int size = buttonList.size();

			for(int i = size+1;i<=doc.getRoot().getRevision_level();i++) {
				JButton button = new JButton("Level "+i);
				button.addActionListener(new WindowActionListener(doc,i));
				buttonList.add(button);
				add(button);
			}
		}
	}
	
	public LevelDemoPanel(RevisionDocument doc, int currentIndex) {
		buttonList = new ArrayList<JButton>();
		RevisionUnit root = doc.getRoot();
		this.doc = doc;
		
		JButton addLevelButton = new JButton("Add another level");
		addLevelButton.addActionListener(new AddLevelListener());
		add(addLevelButton);
		
		int levels = root.getRevision_level();
		for(int i = 1;i<=levels;i++) {
			JButton button = new JButton("Level "+i);
			button.addActionListener(new WindowActionListener(doc,i));
			buttonList.add(button);
			add(button);
		}
		MainFrame.MaxLevel = levels;
		if(currentIndex!=0)
		buttonList.get(currentIndex-1).setEnabled(false);
	}
	
	
	
	public void showNewWindow(RevisionDocument doc, int level) {
		
	}
}
