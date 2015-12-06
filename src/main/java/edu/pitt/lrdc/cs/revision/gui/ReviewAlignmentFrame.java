package edu.pitt.lrdc.cs.revision.gui;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BoxLayout;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;

import edu.pitt.lrdc.cs.revision.io.ReviewProcessor;
import edu.pitt.lrdc.cs.revision.io.RevisionDocumentReader;
import edu.pitt.lrdc.cs.revision.model.ReviewDocument;
import edu.pitt.lrdc.cs.revision.model.RevisionDocument;

public class ReviewAlignmentFrame extends JFrame {
	JFileChooser fc = new JFileChooser();
	RevisionDocument rd;
	ReviewAlignmentPanel panel;

	public ReviewAlignmentFrame() {
		setTitle("Review Annotation Tool");

		GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment()
				.getDefaultScreenDevice();
		int width = gd.getDisplayMode().getWidth();
		int height = gd.getDisplayMode().getHeight();
		width = (int) (width * 0.95);
		height = (int) (height * 0.95);
		setSize(width, height);
		// setSize(1000, 800);
		// pack();
		JMenuBar menuBar = new JMenuBar();
		JMenu menu = new JMenu("File");
		JMenuItem item = new JMenuItem("Load File");
		item.setToolTipText("Load the spreadsheet file for annotation");
		JMenuItem saveItem = new JMenuItem("Save");
		saveItem.setToolTipText("Save the annotation to the current file");
		item.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				int returnVal = fc.showOpenDialog(ReviewAlignmentFrame.this);

				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File file = fc.getSelectedFile();
					try {
						ReviewAlignmentFrame.this.load(file.getAbsolutePath());
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				} else {

				}
			}

		});
		menu.add(item);
		menu.add(saveItem);
		menuBar.add(menu);
		this.setJMenuBar(menuBar);
	}

	public String findMatchedFile(String fileName, String path) {
		fileName = fileName.replaceAll(".txt.xlsx","");
		if(fileName.contains("Annotation_")) {
		fileName = fileName.replaceAll("Annotation_", "");
		}
		if(fileName.contains("-"))
		fileName = fileName.substring(0,fileName.indexOf("-")).trim();
		File folder = new File(path);
		File[] subs = folder.listFiles();
		for(File sub: subs) {
			if(sub.getName().contains(fileName)) {
				return sub.getAbsolutePath();
			}
		}
		return null;
	}

	public void load(String path) throws Exception {
		// RevisionDocument rd = RevisionDocumentReader
		// .readDoc("E:\\independent study\\Revision\\Braverman\\agreement\\Annotation__bravesfavstudent Christian.xlsx");
		// RevisionDocument rd = null;
		// if(new File(path).exists())
		RevisionDocument rd = RevisionDocumentReader.readDoc(path);
		this.rd = rd;
		File f = new File(path);
		File parentFolder = f.getParentFile();
		String reviewPath = parentFolder.getAbsolutePath()+"/review";
		String rPath = findMatchedFile(f.getName(),reviewPath);
		ReviewDocument reviewD = ReviewProcessor.readReviewDocument(rPath);
		getContentPane().setLayout(
				new BoxLayout(getContentPane(), BoxLayout.X_AXIS));
		/*
		 * Container contentPane = getContentPane(); contentPane.removeAll();
		 * JTabbedPane tabbedPane = new JTabbedPane();
		 * tabbedPane.addTab("Old Draft", new BaseLevelPanel(rd, true));
		 * tabbedPane.addTab("New Draft", new BaseLevelPanel(rd, false));
		 * contentPane.add(tabbedPane);
		 */
		if (panel == null) {
			panel = new ReviewAlignmentPanel(rd, reviewD);
			// this.add(panel);
		} else {
			this.remove(panel);
			panel = new ReviewAlignmentPanel(rd, reviewD);
			// this.add(panel);
		}
		this.add(panel);
		this.setTitle(rd.getDocumentName());
		this.show();
	}

	public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		ReviewAlignmentFrame rf = new ReviewAlignmentFrame();
		// mf.load("dummy");
		rf.show();
		rf.setTitle("Review Interface");
		rf.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	}
}
