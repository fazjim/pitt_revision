package edu.pitt.lrdc.cs.revision.evaluate;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;

import weka.core.Instances;
import edu.pitt.cs.revision.purpose.RevisionPurposePredicter;
import edu.pitt.cs.revision.purpose.RevisionPurposeTagger;
import edu.pitt.lrdc.cs.revision.io.RevisionDocumentReader;
import edu.pitt.lrdc.cs.revision.model.RevisionDocument;
import edu.pitt.lrdc.cs.revision.model.RevisionUnit;

public class ErrorStatistics {
	public static void main(String[] args) throws Exception {
		String corpus = "C:\\Not Backed Up\\data\\naaclData\\C1";
		ArrayList<RevisionDocument> docs = RevisionDocumentReader
				.readDocs(corpus);
		RevisionPurposePredicter rpp = new RevisionPurposePredicter();
		int folder = 10;
		ArrayList<ArrayList<ArrayList<RevisionDocument>>> crossCuts = EvaluateTool
				.getCrossCut(docs, folder);
		for (int j = 0; j < folder; j++) { // Do a cross validation
			ArrayList<RevisionDocument> trainDocs = crossCuts.get(j).get(0);
			ArrayList<RevisionDocument> testDocs = crossCuts.get(j).get(1);
			rpp.predictRevisionsSolo5Class(trainDocs, testDocs, true, 11);
		}
		String output = "C:\\Not Backed Up\\allResults\\C1Results.txt";
		BufferedWriter writer = new BufferedWriter(new FileWriter(output));
		writer.write("REAL" + "\t");
		writer.write("PREDICTED" + "\t");
		writer.write("DOCNAME" + "\t");
		writer.write("REVOP" + "\t");
		writer.write("OLDPARANO" + "\t");
		writer.write("OLDINDEX" + "\t");
		writer.write("PARAOLDFIRST" + "\t");
		writer.write("NEWPARANO" + "\t");
		writer.write("NEWINDEX" + "\t");
		writer.write("PARANEWFIRST" + "\t");
		writer.write("OLDSENT" + "\t");
		writer.write("NEWSENT" + "\t\n");
		rpp.outputResult(writer, docs);
		writer.close();
		
		for(RevisionDocument doc: docs) {
			ArrayList<RevisionUnit> units = doc.getPredictedRoot().getRevisionUnitAtLevel(0);
			for(RevisionUnit unit: units) {
				unit.setAbandoned();
			}
		}
		
		output = "C:\\Not Backed Up\\allResults\\ParagraphCRF.txt";
		for(int j = 0;j<folder;j++) {
			ArrayList<RevisionDocument> trainDocs = crossCuts.get(j).get(0);
			ArrayList<RevisionDocument> testDocs = crossCuts.get(j).get(1);
			Instances[] instances = RevisionPurposeTagger.getInstance()
					.prepareForLabelling(trainDocs, testDocs, true,
							11);
			String trainPath = "C:\\Not Backed Up\\trainCrf.txt";
			String testPath = "C:\\Not Backed Up\\testCrf.txt";
			String modelPath = "C:\\Not Backed Up\\crf.model";
			String testPath2 = "C:\\Not Backed Up\\testPredictCrf.txt";
			RevisionPurposeTagger.getInstance().transformToTxtForCRFTrain(
					instances[0], trainDocs, trainPath);
			RevisionPurposeTagger.getInstance().transformToTxtForCRF(
					instances[1], testDocs, testPath);
			RevisionPurposeTagger.getInstance().trainAndTag(trainPath,
					modelPath, testPath, testPath2);
			RevisionPurposeTagger.getInstance().readResultToDocs(testDocs,
					testPath2);
		}
		RevisionPurposeTagger.getInstance().printErrors(docs, output);
	}
}
