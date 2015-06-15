package edu.pitt.cs.revision.batch;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import edu.pitt.cs.revision.machinelearning.JOrthoAssist;
import edu.pitt.cs.revision.machinelearning.StanfordParserAssist;
import edu.pitt.lrdc.cs.revision.io.RevisionDocumentReader;
import edu.pitt.lrdc.cs.revision.model.RevisionDocument;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.CoreAnnotations.NamedEntityTagAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.util.CoreMap;

/**
 * Just to solve the problem that we could not have the parser do the parsing everytime
 * 
 * Still, for a convenient version, it seems that it does not worth it to do the pos tagging (Or switch to senna!)
 * 
 * @author zhangfan
 * @version 1.0
 */

public class BatchFeatureWriter {
	public static void main(String[] args) throws Exception {
		RevisionDocumentReader reader = new RevisionDocumentReader();
		//ArrayList<RevisionDocument> trainDocs = reader.readDocs("C:\\Not Backed Up\\data\\annotated\\revisedClass3");
		//ArrayList<RevisionDocument> testDocs = reader.readDocs("C:\\Not Backed Up\\data\\annotated\\revisedClass4");
		//trainDocs.addAll(testDocs);
		//ArrayList<RevisionDocument> trainDocs = reader.readDocs("C:\\Not Backed Up\\data\\annotated\\tmpStorage");
		
		ArrayList<RevisionDocument> docs1 = reader.readDocs("D:\\data\\revision\\class12\\class12");
		ArrayList<RevisionDocument> docs2 = reader.readDocs("D:\\data\\revision\\class34");
		ArrayList<RevisionDocument> docs3 = reader.readDocs("D:\\data\\revision\\newCorpora");
		docs1.addAll(docs2);
		docs1.addAll(docs3);
		writeBatch(docs1,"D:\\data\\revision\\batch");
	}
	
	public static String getFileName(String path) {
		File f = new File(path);
		String fileName = f.getName();
		fileName = fileName.substring(0,fileName.indexOf("."));
		return fileName;
	}
	
	
	static boolean skipError = false;
	static boolean skipPOS = false;
	public static void writeBatch(ArrayList<RevisionDocument> docs, String path) throws IOException {
		for(RevisionDocument doc: docs) {
			String name = getFileName(doc.getDocumentName());
			String folder = path + "/" + name;
			String posFolder = folder + "/" + "POS";
			String errorFolder = folder + "/" + "ERROR";
			File posFolderFile = new File(posFolder);
			File errorFolderFile = new File(errorFolder);
			if(!posFolderFile.exists()) posFolderFile.mkdirs();
			if(!errorFolderFile.exists()) errorFolderFile.mkdirs();
			if(!skipPOS) writePOSInfo(doc,posFolder);
			if(!skipError) writeErrorInfo(doc, errorFolder);
		}
	}
	
	/**
	 * To make things easier, just each sentence a file
	 * @param doc
	 * @param posFolder
	 * @throws IOException 
	 */
	public static void writePOSInfo(RevisionDocument doc, String posFolder) throws IOException {
		String oldFolder = posFolder + "/" + "OLD";
		String[] oldSents = doc.getOldSentencesArray();
		for(int i = 1;i<=oldSents.length;i++) {
			String sent = oldSents[i-1];
			writePOSInfo(sent,oldFolder+"/"+i+".txt");
		}
		String newFolder = posFolder + "/" + "NEW";
		String[] newSents = doc.getNewSentencesArray();
		for(int i = 1;i<=newSents.length;i++) {
			String sent = newSents[i-1];
			writePOSInfo(sent,newFolder+"/"+i+".txt");
		}
	}
	
	public static void writeErrorInfo(RevisionDocument doc, String errorFolder) throws IOException {
		String oldFolder = errorFolder + "/" + "OLD";
		String[] oldSents = doc.getOldSentencesArray();
		for(int i = 1;i<=oldSents.length;i++) {
			String sent = oldSents[i-1];
			writeErrorInfo(sent,oldFolder+"/"+i+".txt");
		}
		String newFolder = errorFolder + "/" + "NEW";
		String[] newSents = doc.getNewSentencesArray();
		for(int i = 1;i<=newSents.length;i++) {
			String sent = newSents[i-1];
			writeErrorInfo(sent,newFolder+"/"+i+".txt");
		}
	}
	
	public static void writePOSInfo(String sentence, String file) throws IOException {
		CoreMap cm = StanfordParserAssist.getInstance().annotateSingleSentence(sentence);
		POSTagInfo posInfo = new POSTagInfo();
		for (CoreLabel token : cm.get(TokensAnnotation.class)) {
			String word = token.get(TextAnnotation.class);
			String pos = token.get(PartOfSpeechAnnotation.class);
			String ner = token.get(NamedEntityTagAnnotation.class);
			
			if (pos.startsWith("JJ")) {
				posInfo.addJJ();
			} else if (pos.startsWith("RB")) {
				posInfo.addRB();
			} else if (pos.startsWith("NN")) {
				posInfo.addNN();
			} else if (pos.startsWith("VB")) {
				posInfo.addVB();
			} 
			
			if (ner.equals("PERSON")) {
				posInfo.addPERSON();
			} else if (ner.equals("LOCATION")) {
				posInfo.addLOCATION();
			}
			posInfo.addTotal();
		}
		posInfo.toFile(file);
	}
	
	public static void writeErrorInfo(String sentence, String file) throws IOException  {
		ErrorInfo errInfo = new ErrorInfo();
		
		errInfo.setSpellErrNum(JOrthoAssist.getInstance().checkSpellingMistakes(sentence));
		errInfo.setGrammarErrNum(JOrthoAssist.getInstance().checkGrammarMistakes(sentence));
		errInfo.toFile(file);
	}
}
