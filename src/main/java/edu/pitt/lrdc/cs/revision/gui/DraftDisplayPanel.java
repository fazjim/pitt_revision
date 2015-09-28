package edu.pitt.lrdc.cs.revision.gui;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Hashtable;

import javax.swing.BoxLayout;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Document;
import javax.swing.text.Highlighter;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import edu.pitt.lrdc.cs.revision.model.RevisionDocument;
import edu.pitt.lrdc.cs.revision.model.RevisionPurpose;
import edu.pitt.lrdc.cs.revision.model.RevisionUnit;

public class DraftDisplayPanel extends JPanel {
	// private JEditorPane oldDraftPane;
	// private JEditorPane newDraftPane;
	private JTextPane oldDraftPane;
	private JTextPane newDraftPane;
	private String oldDraftText;
	private String newDraftText;
	private Hashtable<Integer, Style> oldDraftStyle = new Hashtable<Integer, Style>();
	private Hashtable<Integer, Style> newDraftStyle = new Hashtable<Integer, Style>();
	private Hashtable<Integer, Integer> oldPurposeIndices = new Hashtable<Integer, Integer>();
	private Hashtable<Integer, Integer> newPurposeIndices = new Hashtable<Integer, Integer>();

	public DraftDisplayPanel(String oldDraft, String newDraft) {
		oldDraftText = oldDraft;
		newDraftText = newDraft;
		oldDraftPane = new JTextPane();
		oldDraftPane.setText(oldDraft);
		//oldDraftPane.setSize(400, 400);
		newDraftPane = new JTextPane();
		//newDraftPane.setSize(400, 400);
		newDraftPane.setText(newDraft);
		
		

		JScrollPane oldSp = new JScrollPane(oldDraftPane);
		JScrollPane newSp = new JScrollPane(newDraftPane);
		this.add(oldSp);
		this.add(newSp);
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
	}

	public void clearTxt() {
		oldDraftPane.setText("");
		newDraftPane.setText("");
	}

	public void addSent(StyledDocument doc, String sentence, int purpose,
			Hashtable<Integer, Style> styleIndex) {
		try {

			doc.insertString(doc.getLength(), sentence, styleIndex.get(purpose));
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}

	private RevisionDocument doc;

	public void reload() {
		clearTxt();
		doc.check();
		if (doc != null) {

			ArrayList<RevisionUnit> units = doc.getRoot()
					.getRevisionUnitAtLevel(0);
			for (RevisionUnit unit : units) {
				int revPurpose = unit.getRevision_purpose();
				ArrayList<Integer> oldIndices = unit.getOldSentenceIndex();
				ArrayList<Integer> newIndices = unit.getNewSentenceIndex();
				if (oldIndices != null) {
					for (Integer oldIndex : oldIndices) {
						oldPurposeIndices.put(oldIndex, revPurpose);
					}
				}
				if (newIndices != null) {
					for (Integer newIndex : newIndices) {
						newPurposeIndices.put(newIndex, revPurpose);
					}
				}
			}
			oldDraftPane.setSelectedTextColor(new Color(51, 51, 255));
			newDraftPane.setSelectedTextColor(new Color(51, 51, 255));

			ArrayList<String> oldSentences = doc.getOldDraftSentences();
			ArrayList<String> newSentences = doc.getNewDraftSentences();
			int currP = 0;
			for (int i = 0; i < oldSentences.size(); i++) {
				int index = i + 1;
				int paraNo = doc.getParaNoOfOldSentence(index);
				String oldSent = doc.getOldSentence(index).trim();
				if (paraNo > currP) {
					if (currP == 0) {
						addSent(oldDraftPane.getStyledDocument(), "        ",
								-2, oldDraftStyle);
					} else {
						addSent(oldDraftPane.getStyledDocument(), "\n        ",
								-2, oldDraftStyle);
					}
					currP = paraNo;
				} else {
					addSent(oldDraftPane.getStyledDocument(), " ", -2,
							oldDraftStyle);
				}
				if (oldPurposeIndices.containsKey(index)) {
					addSent(oldDraftPane.getStyledDocument(), oldSent,
							oldPurposeIndices.get(index), oldDraftStyle);
				} else {
					ArrayList<Integer> newIndices = doc.getNewFromOld(index);
					if (newIndices == null
							|| newIndices.size() == 0
							|| (newIndices.size() == 1 && newIndices.get(0) == -1)) {
						addSent(oldDraftPane.getStyledDocument(), oldSent, -1,
								oldDraftStyle);
					} else {
						String newSent = doc.getNewSentences(
								doc.getNewFromOld(index)).trim();
						if (oldSent.equals(newSent)) {
							addSent(oldDraftPane.getStyledDocument(), oldSent,
									-2, oldDraftStyle);
						} else {
							addSent(oldDraftPane.getStyledDocument(), oldSent,
									-1, oldDraftStyle);
						}
					}
				}
			}
			currP = 0;
			for (int i = 0; i < newSentences.size(); i++) {
				int index = i + 1;
				int paraNo = doc.getParaNoOfNewSentence(index);
				if (paraNo > currP) {
					if (currP == 0) {
						addSent(newDraftPane.getStyledDocument(), "        ",
								-2, newDraftStyle);
					} else {
						addSent(newDraftPane.getStyledDocument(), "\n        ",
								-2, newDraftStyle);
					}
					currP = paraNo;
				} else {
					addSent(newDraftPane.getStyledDocument(), " ", -2,
							newDraftStyle);
				}
				if (newPurposeIndices.containsKey(index)) {
					String newSent = doc.getNewSentence(index);
					addSent(newDraftPane.getStyledDocument(), newSent,
							newPurposeIndices.get(index), newDraftStyle);
				} else {
					String newSent = doc.getNewSentence(index).trim();
					ArrayList<Integer> oldIndices = doc.getOldFromNew(index);
					if (oldIndices == null
							|| oldIndices.size() == 0
							|| (oldIndices.size() == 1 && oldIndices.get(0) == -1)) {
						addSent(newDraftPane.getStyledDocument(), newSent, -1,
								newDraftStyle);
					} else {
						String oldSent = doc.getOldSentences(
								doc.getOldFromNew(index)).trim();
						if (oldSent.equals(newSent)) {
							addSent(newDraftPane.getStyledDocument(), newSent,
									-2, newDraftStyle);
						} else {
							addSent(newDraftPane.getStyledDocument(), newSent,
									-1, newDraftStyle);
						}
					}
				}
			}
		}
	}

	// Styled Version
	public DraftDisplayPanel(RevisionDocument doc) {
		oldDraftPane = new JTextPane();
		//oldDraftPane.setSize(400, 400);
		newDraftPane = new JTextPane();
		//newDraftPane.setSize(400, 400);
		
        this.doc = doc;
		clearTxt();
		setStyles(oldDraftPane, oldDraftStyle);
		setStyles(newDraftPane, newDraftStyle);

		ArrayList<RevisionUnit> units = doc.getRoot().getRevisionUnitAtLevel(0);
		for (RevisionUnit unit : units) {
			int revPurpose = unit.getRevision_purpose();
			ArrayList<Integer> oldIndices = unit.getOldSentenceIndex();
			ArrayList<Integer> newIndices = unit.getNewSentenceIndex();
			if (oldIndices != null) {
				for (Integer oldIndex : oldIndices) {
					oldPurposeIndices.put(oldIndex, revPurpose);
				}
			}
			if (newIndices != null) {
				for (Integer newIndex : newIndices) {
					newPurposeIndices.put(newIndex, revPurpose);
				}
			}
		}
		oldDraftPane.setSelectedTextColor(new Color(51, 51, 255));

		newDraftPane.setSelectedTextColor(new Color(51, 51, 255));

		ArrayList<String> oldSentences = doc.getOldDraftSentences();
		ArrayList<String> newSentences = doc.getNewDraftSentences();
		int currP = 0;
		for (int i = 0; i < oldSentences.size(); i++) {
			int index = i + 1;
			int paraNo = doc.getParaNoOfOldSentence(index);
			String oldSent = doc.getOldSentence(index).trim();
			if (paraNo > currP) {
				if (currP == 0) {
					addSent(oldDraftPane.getStyledDocument(), "        ", -2,
							oldDraftStyle);
				} else {
					addSent(oldDraftPane.getStyledDocument(), "\n        ", -2,
							oldDraftStyle);
				}
				currP = paraNo;
			} else {
				addSent(oldDraftPane.getStyledDocument(), " ", -2,
						oldDraftStyle);
			}
			if (oldPurposeIndices.containsKey(index)) {
				addSent(oldDraftPane.getStyledDocument(), oldSent,
						oldPurposeIndices.get(index), oldDraftStyle);
			} else {
				ArrayList<Integer> newIndices = doc.getNewFromOld(index);
				if (newIndices == null || newIndices.size() == 0
						|| (newIndices.size() == 1 && newIndices.get(0) == -1)) {
					addSent(oldDraftPane.getStyledDocument(), oldSent, -1,
							oldDraftStyle);
				} else {
					String newSent = doc.getNewSentences(
							doc.getNewFromOld(index)).trim();
					if (oldSent.equals(newSent)) {
						addSent(oldDraftPane.getStyledDocument(), oldSent, -2,
								oldDraftStyle);
					} else {
						addSent(oldDraftPane.getStyledDocument(), oldSent, -1,
								oldDraftStyle);
					}
				}
			}
		}

		currP = 0;
		for (int i = 0; i < newSentences.size(); i++) {
			int index = i + 1;
			int paraNo = doc.getParaNoOfNewSentence(index);
			if (paraNo > currP) {
				if (currP == 0) {
					addSent(newDraftPane.getStyledDocument(), "        ", -2,
							newDraftStyle);
				} else {
					addSent(newDraftPane.getStyledDocument(), "\n        ", -2,
							newDraftStyle);
				}
				currP = paraNo;
			} else {
				addSent(newDraftPane.getStyledDocument(), " ", -2,
						newDraftStyle);
			}
			if (newPurposeIndices.containsKey(index)) {
				String newSent = doc.getNewSentence(index);
				addSent(newDraftPane.getStyledDocument(), newSent,
						newPurposeIndices.get(index), newDraftStyle);
			} else {
				String newSent = doc.getNewSentence(index).trim();
				ArrayList<Integer> oldIndices = doc.getOldFromNew(index);
				if (oldIndices == null || oldIndices.size() == 0
						|| (oldIndices.size() == 1 && oldIndices.get(0) == -1)) {
					addSent(newDraftPane.getStyledDocument(), newSent, -1,
							newDraftStyle);
				} else {
					String oldSent = doc.getOldSentences(
							doc.getOldFromNew(index)).trim();
					if (oldSent.equals(newSent)) {
						addSent(newDraftPane.getStyledDocument(), newSent, -2,
								newDraftStyle);
					} else {
						addSent(newDraftPane.getStyledDocument(), newSent, -1,
								newDraftStyle);
					}
				}
			}
		}
		
		/*Style fontSize = oldDraftPane.addStyle("fontSize", null);
        StyleConstants.setFontSize(fontSize, 18);
        //Setting the font Size
        oldDraftPane.getStyledDocument().setCharacterAttributes(0, oldDraftPane.getStyledDocument().getLength(), fontSize, false);

        Style fontSize2 = newDraftPane.addStyle("fontSize", null);
        StyleConstants.setFontSize(fontSize2, 18);
        //Setting the font Size
        newDraftPane.getStyledDocument().setCharacterAttributes(0, newDraftPane.getStyledDocument().getLength(), fontSize2, false);
		*/
        
		JScrollPane oldSp = new JScrollPane(oldDraftPane);
		JScrollPane newSp = new JScrollPane(newDraftPane);
		this.add(oldSp);
		this.add(newSp);
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
	}

	public void setStyles(JTextPane oldDraftPane,
			Hashtable<Integer, Style> oldDraftStyle) {

		Style normalStyle = oldDraftPane.addStyle("NormalStyle", null);
		StyleConstants.setBackground(normalStyle, Color.WHITE);
		StyleConstants.setBold(normalStyle, false);
		oldDraftStyle.put(-2, normalStyle);

		Style claimStyle = oldDraftPane.addStyle("claimStyle", null);
		StyleConstants.setBackground(claimStyle, ColorConstants.claimColor);
		StyleConstants.setBold(claimStyle, false);
		oldDraftStyle.put(RevisionPurpose.CLAIMS_IDEAS, claimStyle);

		Style warrantStyle = oldDraftPane.addStyle("warrantStyle", null);
		StyleConstants.setBackground(warrantStyle, ColorConstants.warrantColor);
		StyleConstants.setBold(warrantStyle, false);
		oldDraftStyle.put(RevisionPurpose.CD_WARRANT_REASONING_BACKING,
				warrantStyle);

		Style evidenceStyle = oldDraftPane.addStyle("evidenceStyle", null);
		StyleConstants.setBackground(evidenceStyle,
				ColorConstants.evidenceColor);
		StyleConstants.setBold(evidenceStyle, false);
		oldDraftStyle.put(RevisionPurpose.EVIDENCE, evidenceStyle);

		Style rebuttalStyle = oldDraftPane.addStyle("rebuttalStyle", null);
		StyleConstants.setBackground(rebuttalStyle,
				ColorConstants.rebuttalColor);
		StyleConstants.setBold(rebuttalStyle, false);
		oldDraftStyle.put(RevisionPurpose.CD_REBUTTAL_RESERVATION,
				rebuttalStyle);

		Style generalStyle = oldDraftPane.addStyle("generalStyle", null);
		StyleConstants.setBackground(generalStyle, ColorConstants.generalColor);
		StyleConstants.setBold(generalStyle, false);
		oldDraftStyle.put(RevisionPurpose.CD_GENERAL_CONTENT_DEVELOPMENT,
				generalStyle);

		Style conventionStyle = oldDraftPane.addStyle("conventionStyle", null);
		StyleConstants.setBackground(conventionStyle,
				ColorConstants.conventionColor);
		StyleConstants.setBold(conventionStyle, false);
		oldDraftStyle.put(RevisionPurpose.CONVENTIONS_GRAMMAR_SPELLING,
				conventionStyle);

		Style wordStyle = oldDraftPane.addStyle("wordStyle", null);
		StyleConstants.setBackground(wordStyle, ColorConstants.wordColor);
		StyleConstants.setBold(wordStyle, false);
		oldDraftStyle.put(RevisionPurpose.WORDUSAGE_CLARITY, wordStyle);

		Style cascadedStyle = oldDraftPane.addStyle("cascadedStyle", null);
		StyleConstants.setBackground(cascadedStyle,
				ColorConstants.cascadedColor);
		StyleConstants.setBold(cascadedStyle, false);
		oldDraftStyle.put(RevisionPurpose.WORDUSAGE_CLARITY_CASCADED,
				cascadedStyle);

		Style unannotatedStyle = oldDraftPane.addStyle("uannotatedStyle", null);
		StyleConstants.setBackground(unannotatedStyle,
				ColorConstants.unannotatedColor);
		StyleConstants.setBold(unannotatedStyle, false);
		oldDraftStyle.put(-1, unannotatedStyle);
	}

	public void highLight(boolean isOld, String find) {
		Highlighter hl;

		if (isOld) {
			hl = oldDraftPane.getHighlighter();
			// oldDraftPane.requestFocusInWindow();
		} else {
			hl = newDraftPane.getHighlighter();
			// newDraftPane.requestFocusInWindow();
		}

		hl.removeAllHighlights();

		String text;
		if (isOld) {
			try {
				Document doc = oldDraftPane.getDocument();
				text = doc.getText(0, doc.getLength());
			} catch (Exception exp) {
				text = oldDraftPane.getText();
			}
			// text = oldDraftPane.getText();
		} else {
			try {
				Document doc = newDraftPane.getDocument();
				text = doc.getText(0, doc.getLength());
			} catch (Exception exp) {
				text = newDraftPane.getText();
			}
		}

		int p0 = text.indexOf(find);
		int p1 = p0 + find.length();
		try {
			hl.addHighlight(p0, p1, DefaultHighlighter.DefaultPainter);
			if (isOld) {
				Style boldStyle = oldDraftPane.addStyle("BoldStyle", null);
				StyleConstants.setBold(boldStyle, true);
				StyleConstants.setFontSize(boldStyle, 12);
				oldDraftPane.getStyledDocument().setCharacterAttributes(p0, find.length(),
						boldStyle, true);
				oldDraftPane.setSelectionStart(p0);
				oldDraftPane.setSelectionEnd(p1);
			} else {
				Style boldStyle = newDraftPane.addStyle("BoldStyle", null);
				StyleConstants.setBold(boldStyle, true);
				StyleConstants.setFontSize(boldStyle, 12);
				newDraftPane.getStyledDocument().setCharacterAttributes(p0, find.length(),
						boldStyle, true);
				newDraftPane.setSelectionStart(p0);
				newDraftPane.setSelectionEnd(p1);
			}
			// if(isOld) oldDraftPane.requestFocusInWindow();
			// else newDraftPane.requestFocusInWindow();
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
		}
	}
}
