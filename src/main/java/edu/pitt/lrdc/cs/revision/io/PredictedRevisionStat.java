package edu.pitt.lrdc.cs.revision.io;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import edu.pitt.cs.revision.batch.BatchFeatureWriter;
import edu.pitt.cs.revision.purpose.TmpInfoStore;
import edu.pitt.lrdc.cs.revision.model.RevisionDocument;
import edu.pitt.lrdc.cs.revision.model.RevisionPurpose;
import edu.pitt.lrdc.cs.revision.model.RevisionUnit;

public class PredictedRevisionStat {
	public static  void persistToFile(ArrayList<RevisionDocument> docs, String path) throws IOException {
		XSSFWorkbook xwb = new XSSFWorkbook();
		FileOutputStream fileOut = new FileOutputStream(path);
		XSSFSheet sheet0 = xwb.createSheet("surface");
		int index = 0;
		XSSFRow row = sheet0.createRow(index);
		row.createCell(0).setCellValue("name");
		row.createCell(1).setCellValue("surface");
		row.createCell(2).setCellValue("content");;
		row.createCell(3).setCellValue("all");
		row.createCell(4).setCellValue("d1score");
		row.createCell(5).setCellValue("d2score");
		index++;
		TmpInfoStore store = new TmpInfoStore();
		for(RevisionDocument doc: docs) {
			String name = doc.getDocumentName();
			int surfaceCounts = 0;
			int contentCounts = 0;
			int totalCounts = 0;
			ArrayList<RevisionUnit> rus = doc.getPredictedRoot().getUnits();
			for(RevisionUnit ru: rus) {
				if(ru.getRevision_purpose() == RevisionPurpose.CONVENTIONS_GRAMMAR_SPELLING) surfaceCounts++;
				else contentCounts++;
				totalCounts ++;
			}
			XSSFRow tmpR = sheet0.createRow(index);
			tmpR.createCell(0).setCellValue(name);
			tmpR.createCell(1).setCellValue(surfaceCounts);
			tmpR.createCell(2).setCellValue(contentCounts);
			tmpR.createCell(3).setCellValue(totalCounts);
			String rName = BatchFeatureWriter.getFileName(name)+".xlsx";
			System.out.println(rName);
			tmpR.createCell(4).setCellValue(store.getD1Score(rName));
			tmpR.createCell(5).setCellValue(store.getD2Score(rName));
			index++;
		}
		
		xwb.write(fileOut);
		fileOut.flush();
		fileOut.close();
	}
	
	public static void main(String[] args) throws IOException, Exception {
		persistToFileReal(RevisionDocumentReader.readDocs("/Users/faz23/Desktop/annotation/12"),"/Users/faz23/Desktop/annotation/stats.xlsx");
	}
 	
	public static  void persistToFileReal(ArrayList<RevisionDocument> docs, String path) throws IOException {
		XSSFWorkbook xwb = new XSSFWorkbook();
		FileOutputStream fileOut = new FileOutputStream(path);
		XSSFSheet sheet0 = xwb.createSheet("surface");
		int index = 0;
		XSSFRow row = sheet0.createRow(index);
		row.createCell(0).setCellValue("name");
		row.createCell(1).setCellValue("#Claims/Ideas");
		row.createCell(2).setCellValue("#Warrant/ReasoningBacking");
		row.createCell(3).setCellValue("#General Content Development");
		row.createCell(4).setCellValue("#Evidence");
		row.createCell(5).setCellValue("#Organization");
		row.createCell(6).setCellValue("#Conventions/Grammar/Spelling");
		row.createCell(7).setCellValue("#Word Usage/Clarity");
		row.createCell(8).setCellValue("#surface");
		row.createCell(9).setCellValue("#content");;
		row.createCell(10).setCellValue("#all");	
		row.createCell(11).setCellValue("d1score");
		row.createCell(12).setCellValue("d2score");
		index++;
		TmpInfoStore store = new TmpInfoStore();
		for(RevisionDocument doc: docs) {
			String name = doc.getDocumentName();
			int surfaceCounts = 0;
			int contentCounts = 0;
			int totalCounts = 0;
			
			int count0 = 0;
			int count1 = 0;
			int count2 = 0;
			int count3 = 0;
			int count4 = 0;
			int count5 = 0;
			int count6 = 0;
			int count7 = 0;
			ArrayList<RevisionUnit> rus = doc.getRoot().getUnits();
			for(RevisionUnit ru: rus) {
				if(ru.getRevision_purpose() == RevisionPurpose.CD_GENERAL_CONTENT_DEVELOPMENT) {
					count0++;
					contentCounts++;
				} else if(ru.getRevision_purpose() == RevisionPurpose.CD_REBUTTAL_RESERVATION) {
					count1++;
					contentCounts++;
				} else if(ru.getRevision_purpose() == RevisionPurpose.CD_WARRANT_REASONING_BACKING) {
					count2++;
					contentCounts++;
				} else if(ru.getRevision_purpose() == RevisionPurpose.CLAIMS_IDEAS) {
					count3++;
					contentCounts++;
				} else if(ru.getRevision_purpose() == RevisionPurpose.CONVENTIONS_GRAMMAR_SPELLING) {
					count4++;
					surfaceCounts++;
				} else if(ru.getRevision_purpose() == RevisionPurpose.EVIDENCE) {
					count5++;
					contentCounts++;
				} else if(ru.getRevision_purpose() == RevisionPurpose.ORGANIZATION) {
					count6++;
					surfaceCounts++;
				} else if(ru.getRevision_purpose() == RevisionPurpose.WORDUSAGE_CLARITY) {
					count7++;
					surfaceCounts++;
				}

				totalCounts ++;
			}
			XSSFRow tmpR = sheet0.createRow(index);
			tmpR.createCell(0).setCellValue(name);
			tmpR.createCell(1).setCellValue(count3);
			tmpR.createCell(2).setCellValue(count2);
			tmpR.createCell(3).setCellValue(count0);
			tmpR.createCell(4).setCellValue(count5);
			tmpR.createCell(5).setCellValue(count6);
			tmpR.createCell(6).setCellValue(count4);
			tmpR.createCell(7).setCellValue(count7);
			tmpR.createCell(8).setCellValue(surfaceCounts);
			tmpR.createCell(9).setCellValue(contentCounts);;
			tmpR.createCell(10).setCellValue(totalCounts);	
			tmpR.createCell(11).setCellValue("d1score");
			tmpR.createCell(12).setCellValue("d2score");
			
//			tmpR.createCell(3).setCellValue(totalCounts);
//			String rName = BatchFeatureWriter.getFileName(name)+".xlsx";
//			System.out.println(rName);
//			tmpR.createCell(4).setCellValue(store.getD1Score(rName));
//			tmpR.createCell(5).setCellValue(store.getD2Score(rName));
			index++;
		}
		
		xwb.write(fileOut);
		fileOut.flush();
		fileOut.close();
	}
}
