package edu.pit.lrdc.cs.revision.gui;

import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

/**
 * @deprecated
 * A group of Radio buttons
 * @author zhf4pal
 *
 */
public class JRadioGroup extends JPanel{
	private ArrayList<String> options = new ArrayList<String>();
	private ArrayList<JRadioButton> buttons = new ArrayList<JRadioButton>();
	private ButtonGroup group = new ButtonGroup();

	
	public void addButton(String option) {
		options.add(option);
		JRadioButton button = new JRadioButton(option);
		buttons.add(button);
		group.add(button);
	}
	
	public JRadioGroup() {
		this.setLayout(new BoxLayout(this,BoxLayout.X_AXIS));
	}

}
