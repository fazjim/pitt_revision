package edu.pitt.lrdc.cs.revision.gui;

import java.awt.Container;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.*;

import edu.pitt.lrdc.cs.revision.io.RevisionDocumentReader;
import edu.pitt.lrdc.cs.revision.io.RevisionDocumentWriter;
import edu.pitt.lrdc.cs.revision.model.RevisionDocument;

public class MainFrame extends JFrame {
	JFileChooser fc = new JFileChooser();
	public static int MaxLevel = 0;
	RevisionDocument rd;
	private AdvBaseLevelPanel panel;
	private DraftDisplayPanel displayPanel;
	private JSplitPane splitPane;
	private String currentPath=null;
	
	public MainFrame() {
		setTitle("Revision Annotation Tool");
		
		GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		int width = gd.getDisplayMode().getWidth();
		int height = gd.getDisplayMode().getHeight();
		width = (int)(width* 0.8);
		height = (int)(height *0.8);
		setSize(width, height);
		//setSize(1000, 800);
		//pack();
		JMenuBar menuBar = new JMenuBar();
		JMenu menu = new JMenu("File");
		JMenuItem item = new JMenuItem("Load File");
		item.setToolTipText("Load the spreadsheet file for annotation");
		JMenuItem item2 = new JMenuItem("Export to File");
		item2.setToolTipText("Export the annotation to a specified location");
		JMenuItem saveItem = new JMenuItem("Save");
		saveItem.setToolTipText("Save the annotation to the current file");
		JMenuItem item3 = new JMenuItem("Import the source file(Old Draft)");
		item3.setToolTipText("Not implemented yet, load the original source file to generate the spreadsheet file");
		JMenuItem item4 = new JMenuItem(
				"Import the destination file(New Draft)");
		item4.setToolTipText("Not implemented yet, load the revised source file to generate the spreadsheet file");
		JMenuItem item5 = new JMenuItem("Generate annotation file");
		item5.setToolTipText("Not implemented yet, Generate the spreadsheet file using the imported files");
		JMenu menuHelp = new JMenu("Help");

		JMenu menuDemo = new JMenu("Run");

		saveItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				try {
					panel.registerRevision();
					boolean isSuccess = save();
					if(isSuccess) {
						JOptionPane.showMessageDialog(MainFrame.this,
							    "File saved.");
					} else {
						JOptionPane.showMessageDialog(MainFrame.this, "File save fail!");
					}
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			
		});
		
		item.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				int returnVal = fc.showOpenDialog(MainFrame.this);

				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File file = fc.getSelectedFile();
					try {
						MainFrame.this.load(file.getAbsolutePath());
						currentPath = file.getAbsolutePath();
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				} else {

				}
			}

		});

		item2.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				int returnVal = fc.showOpenDialog(MainFrame.this);

				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File file = fc.getSelectedFile();
					try {
						panel.registerRevision();
						boolean isSuccess = MainFrame.this.export(file.getAbsolutePath());
						if(isSuccess) {
							JOptionPane.showMessageDialog(MainFrame.this,
								    "File saved.");
						} else {
							JOptionPane.showMessageDialog(MainFrame.this, "File save fail!");
						}
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

				} else {

				}
			}

		});

		menu.add(item);
		menu.add(item2);
		menu.add(saveItem);
		item3.setEnabled(false);
		menu.add(item3);
		item4.setEnabled(false);
		menu.add(item4);
		item5.setEnabled(false);
		menu.add(item5);

		JMenuItem menuCode = new JMenuItem("Coding Manual");
		menuCode.setEnabled(false);
		menuHelp.add(menuCode);
		JMenuItem menuDemoItem = new JMenuItem("Demo annotations");
		menuDemoItem.setEnabled(false);
		menuDemo.add(menuDemoItem);

		menuBar.add(menu);
		menuBar.add(menuHelp);
		menuBar.add(menuDemo);
		this.setJMenuBar(menuBar);
	}

	public void load(String path) throws Exception {
		// RevisionDocument rd = RevisionDocumentReader
		// .readDoc("E:\\independent study\\Revision\\Braverman\\agreement\\Annotation__bravesfavstudent Christian.xlsx");
		// RevisionDocument rd = null;
		// if(new File(path).exists())
		RevisionDocument rd = RevisionDocumentReader.readDoc(path);
		this.rd = rd;
		getContentPane().setLayout(
			    new BoxLayout(getContentPane(), BoxLayout.X_AXIS)
			);
		/*Container contentPane = getContentPane();
		contentPane.removeAll();
		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.addTab("Old Draft", new BaseLevelPanel(rd, true));
		tabbedPane.addTab("New Draft", new BaseLevelPanel(rd, false));
		contentPane.add(tabbedPane);*/
		if(panel == null) {
			panel = new AdvBaseLevelPanel(rd);
			//this.add(panel);
		} else {
			this.remove(panel);
			panel = new AdvBaseLevelPanel(rd);
			//this.add(panel);
		}
		//String[] drafts = rd.regenerateDrafts();
		//displayPanel = new DraftDisplayPanel(drafts[0],drafts[1]);
		displayPanel = new DraftDisplayPanel(rd);
		
		panel.setDisplay(displayPanel);
		if(splitPane!=null)
		this.remove(splitPane);
		splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, panel, displayPanel);
		//splitPane.setDividerLocation(900);
		//splitPane.setResizeWeight(0.5);
		splitPane.setDividerLocation(0.3);
		this.add(splitPane);
		this.setTitle(rd.getDocumentName());
		this.show();
	}

	public boolean export(String path) {
		try {
			RevisionDocumentWriter.writeToDoc(this.rd, path);
		} catch (Exception exp) {
			exp.printStackTrace();
			return false;
		}
		return true;
	}
	
	public boolean save() {
		try {
			RevisionDocumentWriter.writeToDoc(this.rd, currentPath);
		} catch (Exception exp) {
			exp.printStackTrace();
			return false;
		}
		return true;
	}

	public static void main(String[] args) throws Exception {
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		MainFrame mf = new MainFrame();
		// mf.load("dummy");
		mf.show();
		mf.setTitle("Annotation Interface");
		mf.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	}
}
