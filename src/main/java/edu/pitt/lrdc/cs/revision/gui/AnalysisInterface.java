package edu.pitt.lrdc.cs.revision.gui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Stack;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import org.apache.commons.io.FileUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.xmlbeans.XmlException;

import weka.classifiers.Classifier;
import weka.core.Instances;
import edu.pitt.cs.revision.batch.BatchFeatureWriter;
import edu.pitt.cs.revision.purpose.RevisionPurposeClassifier;
import edu.pitt.cs.revision.purpose.RevisionPurposePredicter;
import edu.pitt.lrdc.cs.revision.alignment.Aligner;
import edu.pitt.lrdc.cs.revision.alignment.PhraseSentenceMerger;
import edu.pitt.lrdc.cs.revision.io.PredictedRevisionStat;
import edu.pitt.lrdc.cs.revision.io.RevisionDocumentReader;
import edu.pitt.lrdc.cs.revision.io.RevisionDocumentWriter;
import edu.pitt.lrdc.cs.revision.model.RevisionDocument;
import edu.pitt.lrdc.cs.revision.process.BatchProcessor;
import edu.pitt.lrdc.cs.revision.process.Docx2TxtTransformer;
import edu.pitt.lrdc.cs.revision.process.Excel2AnnotationTransformer;
import edu.pitt.lrdc.cs.revision.process.InfoAdder;
import edu.pitt.lrdc.cs.revision.process.Line2ExcelTransformer;
import edu.pitt.lrdc.cs.revision.process.Txt2LineTransformer;

/**
 * Interface for analysis
 * 
 * Well... right now the code is unbelievably ugly.... might spend time to fix
 * it later
 * 
 * @author zhf4pal
 *
 */
public class AnalysisInterface extends JPanel {
	private FilePicker trainPathPicker;
	private FilePicker docPathPicker;
	private FilePicker outputPathPicker;

	private JCheckBox useFeatures;

	private JButton classifyRawButton;
	private JButton classifyButton;

	private ButtonGroup group;
	private JRadioButton docxButton;
	private JRadioButton txtButton;
	private JRadioButton alignedButton;

	private JButton analyzeButton;
	private JButton analyzeAllButton;
	private JCheckBox useLightWeight;

	private JTextArea messageBox;
	private String fileOption;

	String batchPath = "batch";

	public AnalysisInterface() {
		trainPathPicker = new FilePicker(
				"Set the path of the training documents (Folder)", "Browse");
		trainPathPicker.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		useFeatures = new JCheckBox("Using trained feature file?");
		useFeatures.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				if (useFeatures.isSelected()) {
					trainPathPicker.disable();
				} else {
					trainPathPicker.enable();
				}
			}
		});

		JPanel trainingBox = new JPanel();
		Box box1 = new Box(BoxLayout.Y_AXIS);
		box1.add(trainPathPicker);
		box1.add(useFeatures);
		trainingBox.add(box1);
		trainingBox.setBorder(BorderFactory
				.createTitledBorder("Training Setup"));

		group = new ButtonGroup();
		docxButton = new JRadioButton(".docx files");
		txtButton = new JRadioButton(".txt files");
		alignedButton = new JRadioButton(".xlsx files");
		group.add(docxButton);
		group.add(txtButton);
		group.add(alignedButton);

		docxButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				selectRadio();
				fileOption = "DOCX";
			}
		});
		txtButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				selectRadio();
				fileOption = "TXT";
			}
		});
		alignedButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				selectRadio();
				fileOption = "ALIGNED";
			}
		});

		Box radioBox = new Box(BoxLayout.X_AXIS);
		radioBox.add(docxButton);
		radioBox.add(txtButton);
		radioBox.add(alignedButton);

		docPathPicker = new FilePicker(
				"Set the path of the documents (Folder) to be analyzed, should contain 2 folders draft1, draft2",
				"Browse");
		docPathPicker.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		// docPathPicker.setAlignmentX(Component.LEFT_ALIGNMENT);
		docPathPicker.disable();

		JPanel workBox = new JPanel();
		Box box2 = new Box(BoxLayout.Y_AXIS);
		box2.add(docPathPicker);
		box2.add(radioBox);
		workBox.add(box2);
		workBox.setBorder(BorderFactory.createTitledBorder("Document Setup"));

		outputPathPicker = new FilePicker(
				"Set the path to output the analyzed documents", "Browse");
		outputPathPicker.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		// outputPathPicker.setAlignmentX(Component.LEFT_ALIGNMENT);
		// classifyRawButton = new JButton("Classify the raw documents");
		// classifyRawButton.addActionListener(new ActionListener() {
		//
		// @Override
		// public void actionPerformed(ActionEvent e) {
		// // TODO Auto-generated method stub
		// classifyRaw();
		// }
		// });
		// classifyButton = new JButton("Classify the labeled documents");
		// classifyButton.setEnabled(false);
		analyzeButton = new JButton("Analyze (Binary Surface vs. Text-based)");
		analyzeAllButton = new JButton("Analyze (All categories)");

		analyzeButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				// analyzeData();
				// SwingUtilities.invokeLater(new Runnable() {
				// public void run() {
				// analyzeBinary();
				// }
				// });
				LongJobBinaryThread t = new LongJobBinaryThread();
				t.start();
			}

		});

		analyzeAllButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				// analyzeAll();
				LongJobAllThread t = new LongJobAllThread();
				t.start();
			}

		});
		useLightWeight = new JCheckBox(
				"Use lighter version? (Much faster but with lower performance)");

		messageBox = new JTextArea("Parsing status");
		messageBox.setRows(20);

		Box allBox = new Box(BoxLayout.Y_AXIS);
		// allBox.add(trainPathPicker);
		// allBox.add(useFeatures);
		// allBox.add(docPathPicker);
		// allBox.add(radioBox);
		// allBox.add(outputPathPicker);
		allBox.add(trainingBox);
		allBox.add(workBox);

		Box buttonBox = new Box(BoxLayout.X_AXIS);
		buttonBox.add(analyzeButton);
		buttonBox.add(analyzeAllButton);
		buttonBox.add(useLightWeight);
		JPanel resultBox = new JPanel();
		Box box3 = new Box(BoxLayout.Y_AXIS);
		box3.add(outputPathPicker);
		box3.add(buttonBox);
		resultBox.add(box3);
		resultBox.setBorder(BorderFactory.createTitledBorder("Result Setup"));

		allBox.add(resultBox);
		JScrollPane sp = new JScrollPane(messageBox);
		allBox.add(sp);

		this.add(allBox);
	}

	public void classifyRaw() {
		String trainPath = trainPathPicker.getSelectedFilePath();
		String docPath = docPathPicker.getSelectedFilePath();
		String outputPath = outputPathPicker.getSelectedFilePath();
		if (trainPath == null) {
			addMessage("Training path is not correct");
		} else if (docPath == null) {
			addMessage("Document path is not correct");
		} else if (outputPath == null) {
			addMessage("Output path is not correct");
		} else {
			try {
				generateData(trainPath, docPath, outputPath);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				addMessage(e.getMessage());
			}
		}
	}

	String msgTxt = "";
	public void addMessage(String text) {
		// this.messageBox.setText(this.messageBox.getText() + "\n" + text);
		msgTxt += "\n" + text;
		SwingUtilities.invokeLater(new Runnable() {
			
			public void run() {
	
		// this.messageBox.setCaretPosition(messageBox.getText().length()-1);
		messageBox.setText(msgTxt);
		messageBox.setCaretPosition(messageBox.getText().length()-1);
		}
		});
	}

	public void clearMessage() {
		this.messageBox.setText("");
	}

	public void selectRadio() {
		docPathPicker.enable();
	}

	public void generateData(String trainPath, String docPath, String outputPath)
			throws Exception {
		BatchFeatureWriter.writeBatch(
				RevisionDocumentReader.readDocs(trainPath), batchPath);

		BatchProcessor bp = new BatchProcessor();
		bp.setAlign(trainPath);
		bp.setRevLabel(trainPath);
		addMessage("Starts prediction.... this will take a couple of minutes for the first time");
		bp.transform(docPath, outputPath, "draft1", "draft2");
		analyzeFilePath = outputPath;
		addMessage("Prediction complete!");
	}

	String analyzeFilePath = null;

	public void analyzeData() {
		if (analyzeFilePath == null)
			analyzeFilePath = docPathPicker.getSelectedFilePath();
		String outputPath = outputPathPicker.getSelectedFilePath();
		try {
			PredictedRevisionStat.persistToFileReal(
					RevisionDocumentReader.readDocs(analyzeFilePath),
					outputPath + "/analysis.xlsx");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			addMessage(e.getMessage());
		}
	}

	public void printException(Exception e) {
		StackTraceElement[] elements = e.getStackTrace();
		for (StackTraceElement element : elements) {
			addMessage(element.toString());
		}
		e.printStackTrace();
	}

	public void transformDocx(String srcFolderPath, String dstFolder) {
		addMessage("Transforming docx to txt files...");
		try {
			Docx2TxtTransformer.transformFolder(srcFolderPath, dstFolder);
			addMessage("Transformation done!");
		} catch (InvalidFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			printException(e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			printException(e);
		} catch (OpenXML4JException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			printException(e);
		} catch (XmlException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			printException(e);
		} catch (Exception e) {
			e.printStackTrace();
			printException(e);
		}
	}

	public void createFolder(String folder) {
		File f = new File(folder);
		if(!f.exists()) f.mkdir();
	}
	
	public void transformExcel(String srcFolderPath, String dstFolder) {
		String draft1FolderName = "draft1";
		String draft2FolderName = "draft2";
		String srcFolderD1 = srcFolderPath + "/" + draft1FolderName;
		String srcFolderD2 = srcFolderPath + "/" + draft2FolderName;

		String header1 = "";
		String header2 = "";

		String lineFolderD1 = srcFolderPath + "/" + "draft1-preprocessed";
		String lineFolderD2 = srcFolderPath + "/" + "draft2-preprocessed";
		String lineFolderD1D = srcFolderPath + "/" + "draft1-preprocessed-discourse";
		String lineFolderD2D = srcFolderPath + "/" + "draft2-preprocessed-discourse";
//		File folderCreater = new File(lineFolderD1);
//		if (!folderCreater.exists())
//			folderCreater.mkdir();
//		folderCreater = new File(lineFolderD2);
//		if (!folderCreater.exists())
//			folderCreater.mkdir();
		createFolder(lineFolderD1);
		createFolder(lineFolderD2);
		createFolder(lineFolderD1D);
		createFolder(lineFolderD2D);
		

		addMessage("Transforming txt files to excel files");
		try {
			// Step 1. Transform the files to lines
			// After this step, new folders "draft1-preprocessed",
			// "draft2-preprocessed" will be generated
			Txt2LineTransformer t2l = new Txt2LineTransformer();
			File d1Src = new File(srcFolderD1);
			File d2Src = new File(srcFolderD2);
			File[] subD1 = d1Src.listFiles();
			File[] subD2 = d2Src.listFiles();
			for (File f : subD1) {
				if(!f.getName().endsWith(".txt")) continue;
				if (header1.equals("")) {
					header1 = extractHeaderName(f.getName());
				}
				t2l.processFileDiscourse(f.getAbsolutePath(), "");
				//t2l.processFile(f.getAbsolutePath(), "");
			}
			for (File f : subD2) {
				if(!f.getName().endsWith(".txt")) continue;
				if (header2.equals("")) {
					header2 = extractHeaderName(f.getName());
				}
				t2l.processFileDiscourse(f.getAbsolutePath(), "");
				//t2l.processFile(f.getAbsolutePath(), "");
			}

			// Step 2. Generating excel files
			File processFolder = new File(srcFolderPath + "/data_process");
			if (!processFolder.exists())
				processFolder.mkdir();
			processFolder = new File(srcFolderPath + "/data_processed");
			if (!processFolder.exists())
				processFolder.mkdir();
			Line2ExcelTransformer l2e = new Line2ExcelTransformer();
			l2e.genAnnFile(lineFolderD1, lineFolderD2, srcFolderPath + "/"
					+ "data_process", header1, header2);

			// Step3. Generating the annotation file from the excel file
			Excel2AnnotationTransformer e2a = new Excel2AnnotationTransformer();
			File srcExcelPath = new File(srcFolderPath + "/data_process");
			File[] processFiles = srcExcelPath.listFiles();
			for (File f : processFiles) {
				e2a.formatFile(f.getAbsolutePath(), srcFolderPath
						+ "/data_processed");
			}

			InfoAdder ia = new InfoAdder();
			ArrayList<RevisionDocument> docs = RevisionDocumentReader
					.readDocs(srcFolderPath + "/data_processed");
			/*
			for (RevisionDocument doc : docs) {
				System.out.println("Adding paragraph:" + doc.getDocumentName());
				//ia.addParagraphInfoDiscourse(doc, srcFolderPath);
				ia.addParagraphInfo(doc, srcFolderPath);
			}*/

//			folderCreater = new File(dstFolder);
//			if (!folderCreater.exists())
//				folderCreater.mkdir();
			createFolder(dstFolder);
			RevisionDocumentWriter writer = new RevisionDocumentWriter();
			for (RevisionDocument doc : docs) {
				File f = new File(doc.getDocumentName());
				String path = dstFolder + "/" + f.getName();
				System.out.println("writing file:" + path);
				writer.writeToDoc(doc, path);
			}
			addMessage("Txt files transformed to annotatable files!");
		} catch (Exception e) {
			printException(e);
		}
	}
	

	
	public void prepareFeatures(String trainPath, String destPath)
			throws IOException, Exception {
		addMessage("Preprocessing and Preparing features...");
		if (trainPath != null) {
			BatchFeatureWriter.writeBatch(
					RevisionDocumentReader.readDocs(trainPath), batchPath);
		}
		BatchFeatureWriter.writeBatch(
				RevisionDocumentReader.readDocs(destPath), batchPath);
		addMessage("Preprocess complete!");
	}

	String tmpFolder = "TempDocs";

	/**
	 * Cleans everything in the temporary folder
	 */
	public void cleanTmp() {
		addMessage("Doing cleaning up....");
		File tmp = new File(tmpFolder);
		try {
			FileUtils.deleteDirectory(tmp);
			addMessage("Finished cleaning!");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			addMessage("Had problem cleaning the temporary folder:(");
		}
	}

	/**
	 * prepare the data
	 * 
	 * @return returns the path of the xlsx files for analyzing
	 */
	public String prepare() {
		String documentPath = docPathPicker.getSelectedFilePath();
		String outputPath = outputPathPicker.getSelectedFilePath();
		if (documentPath == null || outputPath == null || fileOption == null) {
			addMessage("Document path is not selected");
			return null;
		} else {
			if (fileOption.equals("XLSX")) {
				// Does not need preprocessing
				return documentPath;
			}
			if (fileOption.equals("DOCX")) {
				String dstPath = tmpFolder + "/txt";
				transformDocx(documentPath, dstPath);
			}
			String srcPath = tmpFolder + "/txt";
			if (fileOption.equals("TXT")) {
				srcPath = documentPath;
			}
			referenceFolder = srcPath; //Set the variable for future reference
			transformExcel(srcPath, outputPath);
			return outputPath;
		}
	}

	String referenceFolder = null;
	/**
	 * Loading the binary or train the binary features then\
	 * 
	 * Ahhh....rumbling for the messy code....:-c
	 */
	public void analyzeBinary() {
		String analyzePath = prepare(); // The path would be used for analysis
		if (analyzePath == null) {
			addMessage("Cannot do analysis because of missing document path");
		} else {
			// Runnable runnable = new ValidateThread();
			// Thread thread = new Thread(runnable);
			// thread.start();
			String outputPath = outputPathPicker.getSelectedFilePath();
			try {
				ArrayList<RevisionDocument> docs = RevisionDocumentReader
						.readDocs(analyzePath);
				RevisionPurposeClassifier rpc = new RevisionPurposeClassifier();
				RevisionPurposePredicter rpp = new RevisionPurposePredicter();
				if (useFeatures.isSelected()) { // Use the existing features or
					// classifiers
					// comment prepareFeatures for this moment, the pos tagging and error are way too time consuming....
					// prepareFeatures(null, analyzePath); // batch should already
														// be there
					Aligner aligner = new Aligner();
					addMessage("Aligning...");
					aligner.align(docs);
					
					
					for (RevisionDocument doc : docs) {
						doc.materializeAlignment();
					}
					addMessage("First round alignment finished");
					PhraseSentenceMerger.adjustAlignment(docs, referenceFolder);
					addMessage("Second round alignment fixing complete");
					
					addMessage("Alignment done!");
					addMessage("Predict revisions...");
					if (useLightWeight.isSelected()) {
						// Use the trained classifier directly
//						if(!rpc.existClassifier()) {
//							Instances trainData = rpc.loadBinaryInstances();
//							rpc.generateLightweightBinaryClassifier(trainData);
//						}
						Classifier cl = rpc.loadBinaryClassifier();
						rpp.predictRevisions(cl, docs);

					} else {
						Instances trainData = rpc.loadBinaryInstances();
						rpp.predictRevisions(trainData, docs);
					}
					for (RevisionDocument doc : docs) {
						doc.materializeRevisionPurpose();
					}
					addMessage("Prediction done...");
				} else { // Train and test
					Aligner aligner = new Aligner();
					String train = trainPathPicker.getSelectedFilePath();

					if (train == null) {
						addMessage("Train path is not set...");
					} else {
						//prepareFeatures(train, analyzePath);
						ArrayList<RevisionDocument> trainDocs = RevisionDocumentReader
								.readDocs(train);
						addMessage("Align revisions...");
						// aligner.align(trainDocs, docs, 2);
						aligner.align(train, docs);// This will also persist the
													// aligner
						
						
						for (RevisionDocument doc : docs) {
							doc.materializeAlignment();
						}
						
						addMessage("First round alignment finished");
						PhraseSentenceMerger.adjustAlignment(docs, referenceFolder);
						addMessage("Second round alignment fixing complete");
						addMessage("Alignment complete!");
						if (useLightWeight.isSelected()) {
							rpc.generateLightWeightClassifier(trainDocs,
									RevisionPurposeClassifier.SURFACECLASSIFY);
							rpp.predictRevisions(rpc.loadBinaryClassifier(),
									docs);
						} else {
							rpc.generateTrainingFeatures(trainDocs,
									RevisionPurposeClassifier.SURFACECLASSIFY,
									true, null);
							rpp.predictRevisions(rpc.loadBinaryInstances(),
									docs);
						}
						for (RevisionDocument doc : docs) {
							doc.materializeRevisionPurpose();
						}
					}
				}
				addMessage("Predicion done, write into the file system...");
				for (RevisionDocument doc : docs) {
					File f = new File(doc.getDocumentName());
					String path = outputPath + "/" + f.getName();
					RevisionDocumentWriter.writeToDoc(doc, path);
				}
				addMessage("Files written!");
				
				addMessage("Write the analysis result now...");
				PredictedRevisionStat.persistToFileReal(docs, outputPath+"/predicted_analysis.xlsx");
				addMessage("Result written");
			} catch (Exception e) {
				addMessage("Cannot do analysis, see the following traces");
				printException(e);
			} finally {
				cleanTmp();
			}

		}
	}

	public void analyzeAll() {
		String analyzePath = prepare();
		if (analyzePath == null) {
			addMessage("Cannot do analysis because of missing document path");
		} else {
			// Runnable runnable = new ValidateThread();
			// Thread thread = new Thread(runnable);
			// thread.start();
			String outputPath = outputPathPicker.getSelectedFilePath();
			try {
				ArrayList<RevisionDocument> docs = RevisionDocumentReader
						.readDocs(analyzePath);
				RevisionPurposeClassifier rpc = new RevisionPurposeClassifier();
				RevisionPurposePredicter rpp = new RevisionPurposePredicter();
				if (useFeatures.isSelected()) { // Use the existing features or
					// classifiers
					prepareFeatures(null, analyzePath);
					addMessage("Aligning...");
					Aligner aligner = new Aligner();
					aligner.align(docs);
					
					for (RevisionDocument doc : docs) {
						doc.materializeAlignment();
					}
					addMessage("First round alignment finished");
					PhraseSentenceMerger.adjustAlignment(docs, referenceFolder);
					addMessage("Second round alignment fixing complete");
					
					addMessage("Alignment complete!");
					addMessage("Predicting revision...");
					if (useLightWeight.isSelected()) {
						// Use the trained classifier directly
						// Classifier cl = rpc.loadBinaryClassifier();
//						if(!rpc.existAllClassifiers()) {
//							Hashtable<Integer, Instances> insts = rpc
//									.loadInstances();
//							rpc.generateLightweightClassifier(insts);
//						}
						Hashtable<Integer, Classifier> cls = rpc
								.loadAllClassifiers();
						rpp.predictRevisionsNoTrain(cls, docs);

					} else {
						// Instances trainData = rpc.loadBinaryInstances();
						Hashtable<Integer, Instances> insts = rpc
								.loadInstances();
						rpp.predictRevisions(insts, docs);
						
					}
					for (RevisionDocument doc : docs) {
						doc.materializeRevisionPurpose();
					}
					addMessage("Prediction complete!");
				} else { // Train and test
					Aligner aligner = new Aligner();
					String train = trainPathPicker.getSelectedFilePath();
					if (train == null) {
						addMessage("Train path is not set...");
					} else {
						prepareFeatures(train, analyzePath);
						ArrayList<RevisionDocument> trainDocs = RevisionDocumentReader
								.readDocs(train);
						// aligner.align(trainDocs, docs, 2);
						addMessage("Aligning...");
						aligner.align(train, docs);
						for (RevisionDocument doc : docs) {
							doc.materializeAlignment();
						}
						addMessage("First round alignment finished");
						PhraseSentenceMerger.adjustAlignment(docs, referenceFolder);
						addMessage("Second round alignment fixing complete");
						addMessage("Alignment complete!");
						addMessage("Predicting revision...");
						if (useLightWeight.isSelected()) {
							rpc.generateLightWeightClassifier(trainDocs,
									RevisionPurposeClassifier.ALLCLASSIFY);
							rpp.predictRevisionsNoTrain(
									rpc.loadAllClassifiers(), docs);
						} else {
							rpc.generateTrainingFeatures(trainDocs,
									RevisionPurposeClassifier.ALLCLASSIFY,
									true, null);
							rpp.predictRevisions(rpc.loadInstances(), docs);
							;
						}
						for (RevisionDocument doc : docs) {
							doc.materializeRevisionPurpose();
						}
						addMessage("Prediction complete!");
					}
				}
				addMessage("Write document ot files");
				for (RevisionDocument doc : docs) {
					File f = new File(doc.getDocumentName());
					String path = outputPath + "/" + f.getName();
					RevisionDocumentWriter.writeToDoc(doc, path);
				}
				addMessage("Files written");
				
				addMessage("Write the analysis result now...");
				PredictedRevisionStat.persistToFileReal(docs, outputPath+"/predicted_analysis.xlsx");
				addMessage("Result written");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				addMessage("Cannot do analysis, see the following traces");
				printException(e);
			} finally {
				cleanTmp();
			}
		}
	}

	public String extractHeaderName(String path) {
		if (path.contains("-")) {
			String header = path.substring(0, path.indexOf("-")).trim();
			return header;
		} else {
			return path;
		}
	}


	class LongJobBinaryThread extends Thread {
		public void run() {
			analyzeBinary();
		}
	}

	class LongJobAllThread extends Thread {
		public void run() {
			analyzeAll();
		}
	}

}
