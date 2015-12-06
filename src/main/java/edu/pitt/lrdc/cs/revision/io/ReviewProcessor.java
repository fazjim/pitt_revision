package edu.pitt.lrdc.cs.revision.io;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.Iterator;
import edu.pitt.lrdc.cs.revision.model.ReviewDocument;
import edu.pitt.lrdc.cs.revision.model.ReviewRevision;

public class ReviewProcessor {
	public static ReviewDocument readReviewDocument(String path)
			throws IOException {
		ReviewDocument revDoc = new ReviewDocument();
		XSSFWorkbook xwb = new XSSFWorkbook(path);
		XSSFSheet sheet0 = xwb.getSheetAt(0);

		revDoc.setDocName(path);
		for (int i = 1; i < sheet0.getPhysicalNumberOfRows(); i++) {
			ReviewRevision rr = new ReviewRevision();
			XSSFRow row = sheet0.getRow(i);
			String reviewNoStr = row.getCell(0).getRawValue();
			String reviewContentStr = row.getCell(1).getStringCellValue();
			String revisionNoStr = row.getCell(2).getRawValue();
			String docStr = row.getCell(3).getStringCellValue();
			String oldIndiceStr = row.getCell(4).getStringCellValue();
			String newIndiceStr = row.getCell(5).getStringCellValue();

			rr.setDocName(docStr);
			rr.setOldIndices(oldIndiceStr);
			rr.setNewIndices(newIndiceStr);
			rr.setReviewNo((int)Double.parseDouble(reviewNoStr));
			rr.setReviewStr(reviewContentStr);
			rr.setRevisionNo((int)Double.parseDouble(revisionNoStr));

			revDoc.addReview(rr);
		}
		return revDoc;
	}

	public static void writeReviewDocument(ReviewDocument revDoc, String path)
			throws IOException {
		ArrayList<ReviewRevision> reviews = revDoc.getReviews();

		FileOutputStream fileOut = new FileOutputStream(path);
		ArrayList<String> cols = new ArrayList<String>();
		cols.add("Review No");
		cols.add("Review Content");
		cols.add("Revision No");
		cols.add("Document Name");
		cols.add("Old Index Str");
		cols.add("New Index Str");

		XSSFWorkbook xwb = new XSSFWorkbook();
		XSSFSheet sheet0 = xwb.createSheet("Review");
		// Set up headers
		XSSFRow header0 = sheet0.createRow(0);

		for (int i = 0; i < cols.size(); i++) {
			header0.createCell(i).setCellValue(cols.get(i));
		}

		// Set up sentence contents and sentence index
		for (int i = 1; i <= reviews.size(); i++) {
			ReviewRevision rev = reviews.get(i - 1);
			XSSFRow row0 = sheet0.createRow(i);
			row0.createCell(0).setCellValue(rev.getReviewNo());
			row0.createCell(1).setCellValue(rev.getReviewStr());
			row0.createCell(2).setCellValue(rev.getRevisionNo());
			row0.createCell(3).setCellValue(rev.getDocName());
			row0.createCell(4).setCellValue(rev.getOldIndiceStr());
			row0.createCell(5).setCellValue(rev.getNewIndiceStr());
		}

		xwb.write(fileOut);
		fileOut.flush();
		fileOut.close();
	}

	public static Hashtable<String, Double[]> getValues(
			String implementationXLSX) throws IOException {
		XSSFWorkbook xwb = new XSSFWorkbook(implementationXLSX);
		Hashtable<String, Double[]> values = new Hashtable<String, Double[]>();
		XSSFSheet sheet0 = xwb.getSheetAt(0);
		for (int i = 1; i < sheet0.getPhysicalNumberOfRows(); i++) {
			XSSFRow row = sheet0.getRow(i);
			int authorIndex = 1;
			int d1Index = 11;
			int d2Index = 20;
			String authorStr = row.getCell(authorIndex).getStringCellValue();
			/*
			 * String d1Str = row.getCell(d1Index).getStringCellValue(); String
			 * d2Str = row.getCell(d2Index).getStringCellValue(); double d1 =
			 * Double.parseDouble(d1Str); double d2 = Double.parseDouble(d2Str);
			 */
			String d1Str = row.getCell(d1Index).getRawValue();
			String d2Str = row.getCell(d2Index).getRawValue();
			try {
				double d1 = Double.parseDouble(d1Str);
				double d2 = Double.parseDouble(d2Str);
				Double[] vals = { d1, d2 };
				values.put(authorStr, vals);
			} catch (Exception exp) {

			}
			// double d1 = row.getCell(d1Index).getNumericCellValue();
			// double d2 = row.getCell(d2Index).getNumericCellValue();
		}
		return values;
	}

	public static void main(String[] args) throws IOException {
		processReviews("C:\\Not Backed Up\\implementation.xlsx",
				"C:\\Not Backed Up\\data\\implementation",
				"C:\\Not Backed UP\\data\\expert-grades-ies.xlsx");
	}

	// Only read in the implemented reviews
	public static void processReviews(String path, String outputPath,
			String valueXLSX) throws IOException {
		XSSFWorkbook xwb = new XSSFWorkbook(path);
		XSSFSheet sheet0 = xwb.getSheetAt(0);

		Hashtable<String, ReviewDocument> docTable = new Hashtable<String, ReviewDocument>();
		Hashtable<String, Double[]> vals = getValues(valueXLSX);

		for (int i = 1; i < sheet0.getPhysicalNumberOfRows(); i++) {
			try {
				XSSFRow row = sheet0.getRow(i);
				String reviewNoStr = row.getCell(0).getRawValue();
				String authorStr = row.getCell(1).getStringCellValue();
				String reviewStr = row.getCell(5).getStringCellValue();
				String compareStr = row.getCell(6).getStringCellValue();

				ReviewDocument document;
				if (docTable.containsKey(authorStr)) {
					document = docTable.get(authorStr);
				} else {
					document = new ReviewDocument();
					docTable.put(authorStr, document);
				}
				document.setDocName(authorStr);
				if (compareStr.equals("y")) {
					ReviewRevision rr = new ReviewRevision();
					rr.setDocName(authorStr);
					rr.setReviewNo(Integer.parseInt(reviewNoStr));
					rr.setReviewStr(reviewStr);
					document.addReview(rr);
					document.addImplemented();
					document.addAllCount();
				} else {
					if (compareStr.equals("n")) {
						document.addAllCount();
					}
				}
			} catch (Exception exp) {

			}
		}

		ArrayList<String> reviewDocNames = new ArrayList<String>();
		Iterator<String> it = docTable.keySet().iterator();
		while (it.hasNext()) {
			String key = it.next();
			ReviewDocument doc = docTable.get(key);
			String output = outputPath + "/" + doc.getDocName();
			if(!output.endsWith(".xlsx")) output += ".xlsx";
			writeReviewDocument(doc, output);
			reviewDocNames.add(doc.getDocName());
		}

		FileOutputStream fileOut = new FileOutputStream(outputPath+"/stat.xlsx");
		ArrayList<String> cols = new ArrayList<String>();
		cols.add("docName");
		cols.add("D1Score");
		cols.add("D2Score");
		cols.add("ImplementedCnt");
		cols.add("Ratio");

		XSSFWorkbook xwbAnalysis = new XSSFWorkbook();
		XSSFSheet sheet0Analysis = xwbAnalysis.createSheet("Review");
		// Set up headers
		XSSFRow header0 = sheet0.createRow(0);
		for (int i = 0; i < cols.size(); i++) {
			header0.createCell(i).setCellValue(cols.get(i));
		}

		// Set up sentence contents and sentence index
		for (int i = 1; i <= reviewDocNames.size(); i++) {
			String revName = reviewDocNames.get(i - 1);
			System.out.println(revName);
			XSSFRow row0 = sheet0Analysis.createRow(i);
			row0.createCell(0).setCellValue(revName);
			row0.createCell(1).setCellValue(vals.get(revName)[0]);
			row0.createCell(2).setCellValue(vals.get(revName)[1]);
			row0.createCell(3).setCellValue(
					docTable.get(revName).getImplementedCnt());
			int impCnt = docTable.get(revName).getImplementedCnt();
			int allCnt = docTable.get(revName).getAllCnt();
			double ratio = impCnt * 1.0 / allCnt;
			row0.createCell(4).setCellValue(allCnt);
			row0.createCell(5).setCellValue(ratio);
		}

		xwbAnalysis.write(fileOut);
		fileOut.flush();
		fileOut.close();

	}
}
