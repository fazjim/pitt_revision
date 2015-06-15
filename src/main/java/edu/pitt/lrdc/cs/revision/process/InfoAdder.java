package edu.pitt.lrdc.cs.revision.process;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import edu.pitt.cs.revision.batch.BatchFeatureWriter;
import edu.pitt.cs.revision.purpose.RevisionPurposePredicter;
import edu.pitt.lrdc.cs.revision.alignment.Aligner;
import edu.pitt.lrdc.cs.revision.io.RevisionDocumentReader;
import edu.pitt.lrdc.cs.revision.io.RevisionDocumentWriter;
import edu.pitt.lrdc.cs.revision.model.RevisionDocument;

/**
 * Process the file to get the paragraphNo information Adding the automatic
 * alignment information
 * 
 * 
 * @author zhangfan
 * @version 1.0
 */
public class InfoAdder {
	private RevisionDocumentReader reader = new RevisionDocumentReader();

	private boolean doAlignment = false;
	private Aligner aligner = new Aligner();
	String alignTrainPath = "";

	private boolean doRevisionLabeling = false;
	private RevisionPurposePredicter predictor = new RevisionPurposePredicter();
	String predictTrainPath = "";

	/**
	 * Will add alignment information
	 * 
	 * @param alignTrainPath
	 */
	public void setAlignment(String alignTrainPath) {
		doAlignment = true;
		this.alignTrainPath = alignTrainPath;
	}

	/**
	 * Will add revision information
	 * 
	 * @param predictTrainPath
	 */
	public void setPredict(String predictTrainPath) {
		doRevisionLabeling = true;
		this.predictTrainPath = predictTrainPath;
	}

	/**
	 * Adding paragraph Info
	 * 
	 * @param doc
	 * @param referenceFolder
	 * @throws IOException
	 */
	public void addParagraphInfo(RevisionDocument doc, String referenceFolder)
			throws IOException {
		String refDraft1Folder = referenceFolder + "/draft1";
		String refDraft2Folder = referenceFolder + "/draft2";

		File refDraft1 = new File(refDraft1Folder);
		File refDraft2 = new File(refDraft2Folder);

		File[] refD1s = refDraft1.listFiles();
		File[] refD2s = refDraft2.listFiles();

		String docName = doc.getDocumentName();
		docName = docName.substring(docName.lastIndexOf("Annotation_") + 11);
		docName = docName.substring(0, docName.indexOf(".xlsx"));
		docName = docName.trim();
	
		File d1 = null, d2 = null;
		for (File f : refD1s) {
			if (getRealFileName(f.getName()).equals(docName)) {
				d1 = f;
				break;
			}
		}
		for (File f : refD2s) {
			if (getRealFileName(f.getName()).equals(docName)) {
				d2 = f;
				break;
			}
		}

		if (d1 != null && d2 != null) {
			addParagraphInfo(doc, d1, d2);
		} else {
			System.err
					.println("Something is wrong for finding the reference file of paragraph info");
			System.err.println(docName);
		}
	}

	public void addParagraphInfoDiscourse(RevisionDocument doc,
			String referenceFolder) throws IOException {
		String refDraft1Folder = referenceFolder
				+ "/draft1-preprocessed-discourse";
		String refDraft2Folder = referenceFolder
				+ "/draft2-preprocessed-discourse";

		File refDraft1 = new File(refDraft1Folder);
		File refDraft2 = new File(refDraft2Folder);

		File[] refD1s = refDraft1.listFiles();
		File[] refD2s = refDraft2.listFiles();

		String docName = doc.getDocumentName();
		docName = docName.substring(docName.lastIndexOf("Annotation_") + 11);
		docName = docName.substring(0, docName.indexOf(".xlsx"));
		docName = docName.substring(docName.indexOf("-")+1);
		docName = docName.trim();
		//System.out.println("DOCName:"+docName);
		File d1 = null, d2 = null;
		for (File f : refD1s) {
			if (getRealFileName(f.getName()).equals(docName)) {
				d1 = f;
				break;
			}
		}
		for (File f : refD2s) {
			if (getRealFileName(f.getName()).equals(docName)) {
				d2 = f;
				break;
			}
		}

		if (d1 != null && d2 != null) {
			addParagraphInfoDiscourse(doc, d1, d2);
		} else {
			System.err
					.println("Something is wrong for finding the reference file of paragraph info");
			System.err.println(docName);
		}
	}

	/**
	 * Add paragraph info given files
	 * 
	 * @param doc
	 * @param d1
	 * @param d2
	 * @throws IOException
	 */
	public void addParagraphInfo(RevisionDocument doc, File d1, File d2)
			throws IOException {
		BufferedReader d1Reader = new BufferedReader(new FileReader(d1));
		BufferedReader d2Reader = new BufferedReader(new FileReader(d2));

		ArrayList<String> lines = new ArrayList<String>();
		String line = d1Reader.readLine();
		StringBuffer buffer = new StringBuffer();
		while (line != null) {
			line = line.trim();
			if (line.length() > 1) { // Extreme case where a "." is left at end
				buffer.append(line);
			} else {
				if (buffer.toString().trim().length() > 1) {
					lines.add(buffer.toString());
				}
			}
			line = d1Reader.readLine();
		}

		ArrayList<String> lines2 = new ArrayList<String>();
		String line2 = d2Reader.readLine();
		buffer = new StringBuffer();
		while (line2 != null) {
			line2 = line2.trim();
			if (line2.length() > 1) {
				buffer.append(line2);
			} else {
				if (buffer.toString().trim().length() > 1) {
					lines2.add(buffer.toString());
				}
			}
			line2 = d2Reader.readLine();
		}
		addParagraphInfo(doc, lines, lines2);
	}

	public void addParagraphInfoDiscourse(RevisionDocument doc, File d1, File d2)
			throws IOException {
		BufferedReader d1Reader = new BufferedReader(new FileReader(d1));
		BufferedReader d2Reader = new BufferedReader(new FileReader(d2));

		String line = d1Reader.readLine();
		StringBuffer buffer = new StringBuffer();
		while (line != null) {
			line = line.trim();
			buffer.append(line);
			line = d1Reader.readLine();
		}

		String discourseTxt1 = buffer.toString();
		
		String line2 = d2Reader.readLine();
		buffer = new StringBuffer();
		while (line2 != null) {
			line2 = line2.trim();
			buffer.append(line2);
			line2 = d2Reader.readLine();
		}

		String discourseTxt2 = buffer.toString();

		addParagraphInfoDiscourse(doc, discourseTxt1, discourseTxt2);
	}

	public void indexParagraphs(ArrayList<Integer> indices, String txt) {
		boolean isEnd = false;
		int fromIndex = 0;
		while (!isEnd) {
			int index = txt.indexOf("<p>", fromIndex);
			if (index == -1)
				isEnd = true;
			else {
				indices.add(index);
				fromIndex = index + 1;
			}
		}
	}

	public int searchIndex(ArrayList<Integer> indices, int start, int end,
			int index) {
		if (end - start <= 1)
			return end;
		int mid = (start + end) / 2;
		if (index < indices.get(mid))
			return searchIndex(indices, start, mid, index);
		return searchIndex(indices, mid, end, index);
	}

	public int findIndex(String txt, String search, ArrayList<Integer> indices) {
		int index = txt.indexOf(search);
		return searchIndex(indices, 0, indices.size() - 1, index);
	}

	public void addParagraphInfoDiscourse(RevisionDocument doc, String txt1,
			String txt2) {
		ArrayList<Integer> indices1 = new ArrayList<Integer>();
		ArrayList<Integer> indices2 = new ArrayList<Integer>();

		indexParagraphs(indices1, txt1);
		indexParagraphs(indices2, txt2);

		int currentParaNo = -1;
		String[] oldSents = doc.getOldSentencesArray();
		String[] newSents = doc.getNewSentencesArray();

		for (int i = 1; i <= oldSents.length; i++) {
			String sent = oldSents[i - 1].trim();
			if(sent.endsWith(" .")) sent = sent.replace(" .", "");
			int oldParaNo = findIndex(txt1, sent, indices1);
			if (oldParaNo != -1) {
				currentParaNo = oldParaNo;
			}
			doc.addOldSentenceParaMap(i, currentParaNo);
		}

		currentParaNo = -1;
		for (int i = 1; i <= newSents.length; i++) {
			String sent = newSents[i - 1].trim();
			int newParaNo = findIndex(txt2, sent, indices2);
			if (newParaNo != -1) {
				currentParaNo = newParaNo;
			}
			doc.addNewSentenceParaMap(i, currentParaNo);
		}
	}

	/**
	 * Add paragraph info given the array of paragraphs
	 * 
	 * @param doc
	 * @param lines1
	 * @param line2
	 */
	public void addParagraphInfo(RevisionDocument doc,
			ArrayList<String> lines1, ArrayList<String> lines2) {
		String[] oldSents = doc.getOldSentencesArray();
		String[] newSents = doc.getNewSentencesArray();
		int currentParaNo = -1;
		for (int i = 1; i <= oldSents.length; i++) {
			String sent = oldSents[i - 1].trim();
			int oldParaNo = findParagraphNo(sent, lines1);
			if (oldParaNo != -1) {
				currentParaNo = oldParaNo;
			}
			doc.addOldSentenceParaMap(i, currentParaNo);
		}

		currentParaNo = -1;
		for (int i = 1; i <= newSents.length; i++) {
			String sent = newSents[i - 1].trim();
			int newParaNo = findParagraphNo(sent, lines2);
			if (newParaNo != -1) {
				currentParaNo = newParaNo;
			}
			doc.addNewSentenceParaMap(i, currentParaNo);
		}
	}

	public int findParagraphNo(String sentence, ArrayList<String> sents) {
		for (int i = 0; i < sents.size(); i++) {
			String paragraph = sents.get(i);
			if (sentence.contains("."))
				sentence = sentence.substring(0, sentence.lastIndexOf('.'))
						.trim();
			sentence = sentence.replace("?", "").trim();
			sentence = sentence.replace("", "").trim();
			if (paragraph.contains(sentence) || sentence.contains(paragraph)) {
				return (i + 1);
			}
		}
		return -1;
	}

	/**
	 * One thing that concerns me a lot is that these naming mechanisms can have problems if somebody really name their files like this
	 * @param name
	 * @return
	 */
	public String getRealFileName(String name) {
		String info = name.substring(name.indexOf("-") + 1);
		// info = info.substring(0,info.indexOf(".txt"));
		info = info.replaceAll("-sentences.txt", "");
		//info = info.replaceAll(".txt","");
		//System.out.println(info);
		return info.trim();
	}

	String batchPath = "batch";

	/**
	 * Adding all the info
	 * 
	 * @param srcFolder
	 * @param destFolder
	 * @param alignmentTrainFolder
	 * @param referenceFolder
	 * @throws Exception
	 */
	public void addInfo(String srcFolder, String destFolder,
			String referenceFolder) throws Exception {
		ArrayList<RevisionDocument> docs = reader.readDocs(srcFolder);
		// Adding paragraph information
		for (RevisionDocument doc : docs) {
			System.out.println("Adding paragraph:" + doc.getDocumentName());
			addParagraphInfo(doc, referenceFolder);
		}
		// Align the sentences
		if (doAlignment) {
			ArrayList<RevisionDocument> trainDocs = reader
					.readDocs(alignTrainPath);
			aligner.align(trainDocs, docs, 2);
			for (RevisionDocument doc : docs) {
				doc.materializeAlignment();
			}
		}
		BatchFeatureWriter.writeBatch(docs, batchPath);
		// Labeling the revisions
		if (doRevisionLabeling) {
			ArrayList<RevisionDocument> trainDocs = reader
					.readDocs(predictTrainPath);
			predictor.predictRevisions(trainDocs, docs, false, 1);
			for (RevisionDocument doc : docs) {
				doc.materializeRevisionPurpose();
			}
		}

		RevisionDocumentWriter writer = new RevisionDocumentWriter();
		for (RevisionDocument doc : docs) {
			File f = new File(doc.getDocumentName());
			String path = destFolder + "/" + f.getName();
			System.out.println("writing file:" + path);
			writer.writeToDoc(doc, path);
		}
	}
}
