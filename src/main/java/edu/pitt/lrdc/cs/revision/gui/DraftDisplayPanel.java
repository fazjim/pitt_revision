package edu.pitt.lrdc.cs.revision.gui;

import javax.swing.BoxLayout;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;

public class DraftDisplayPanel extends JPanel{
	private JEditorPane oldDraftPane;
	private JEditorPane newDraftPane;
	private String oldDraftText;
	private String newDraftText;
	
	
	public DraftDisplayPanel(String oldDraft, String newDraft) {
		oldDraftText = oldDraft;
		newDraftText = newDraft;	
		oldDraftPane = new JEditorPane();
		oldDraftPane.setText(oldDraft);
		oldDraftPane.setSize(400,400);
		newDraftPane = new JEditorPane();
		newDraftPane.setSize(400,400);
		newDraftPane.setText(newDraft);
		
		JScrollPane oldSp = new JScrollPane(oldDraftPane);
		JScrollPane newSp = new JScrollPane(newDraftPane);
		this.add(oldSp);
		this.add(newSp);
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
	}
	
	
	
		public void highLight(boolean isOld, String find)
		{
			Highlighter hl;
			if(isOld) {
				hl = oldDraftPane.getHighlighter();
				//oldDraftPane.requestFocusInWindow();
			} else {
				hl = newDraftPane.getHighlighter();
				//newDraftPane.requestFocusInWindow();
			}
		  
		    hl.removeAllHighlights();
		   
		    String text;
		    if(isOld) {
		    	text = oldDraftPane.getText();
		    } else {
		    	text = newDraftPane.getText();
		    }
		    
		    int p0 = text.indexOf(find);
		    int p1 = p0+find.length();
		    try {
				hl.addHighlight(p0, p1,  DefaultHighlighter.DefaultPainter);
				//if(isOld) oldDraftPane.requestFocusInWindow();
				//else newDraftPane.requestFocusInWindow();
			} catch (BadLocationException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			}
		}
}
