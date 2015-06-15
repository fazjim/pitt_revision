package edu.pitt.lrdc.cs.revision.process;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * Tranform the line files into the excel files containing old and new draft
 * 
 * @author zhangfan
 *
 */

public class Line2ExcelTransformer {
	public boolean isSameAuthor(String srcName, String dstName, String d1Name,
			String d2Name) {
		return srcName.replace(d1Name, d2Name).equals(dstName);
	}

	public void genAnnFile(String d1_Path, String d2_Path, String rootPath,
			String d1Name, String d2Name) throws IOException {
		File srcFolder = new File(d1_Path);
		File dstFolder = new File(d2_Path);
		File[] srcFiles = srcFolder.listFiles();
		File[] dstFiles = dstFolder.listFiles();
		for (int i = 0; i < srcFiles.length; i++) {
			for (int j = 0; j < dstFiles.length; j++) {
				if (isSameAuthor(srcFiles[i].getName(), dstFiles[j].getName(),
						d1Name, d2Name)) {
					writeToAnnFile(srcFiles[i], dstFiles[j], rootPath);
				}
			}
		}
	}

	public void writeToAnnFile(File src, File dst, String rootPath)
			throws IOException {
		ArrayList<String> srcLines = new ArrayList<String>();
		ArrayList<String> dstLines = new ArrayList<String>();

		BufferedReader srcReader = new BufferedReader(new FileReader(src));
		BufferedReader dstReader = new BufferedReader(new FileReader(dst));

		String line = srcReader.readLine();
		while (line != null) {
			srcLines.add(line);
			line = srcReader.readLine();
		}

		String dstLine = dstReader.readLine();
		while (dstLine != null) {
			dstLines.add(dstLine);
			dstLine = dstReader.readLine();
		}

		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet0 = workbook.createSheet();
		XSSFSheet sheet1 = workbook.createSheet();

		for (int i = 0; i < srcLines.size(); i++) {
			XSSFRow row = sheet0.createRow(i);
			XSSFCell cell = row.createCell(0);
			cell.setCellValue(srcLines.get(i));
		}

		for (int i = 0; i < dstLines.size(); i++) {
			XSSFRow row = sheet1.createRow(i);
			XSSFCell cell = row.createCell(0);
			cell.setCellValue(dstLines.get(i));
		}

		String excelFileName = src.getName();
		if (excelFileName.contains("-")) {
			//System.out.println(excelFileName);
			excelFileName = excelFileName.substring(
					0,
					excelFileName.lastIndexOf("-"));
			/*excelFileName = excelFileName.substring(
					excelFileName.indexOf("-") + 1,
					excelFileName.lastIndexOf("-"));*/
		}
		excelFileName = excelFileName.trim();
		excelFileName = rootPath + "/" + excelFileName + ".xlsx";
		FileOutputStream fileOut = new FileOutputStream(excelFileName);
		workbook.write(fileOut);
		fileOut.flush();
		fileOut.close();
		srcReader.close();
		dstReader.close();
	}

	public static void main(String[] args) throws IOException {
		Line2ExcelTransformer afg = new Line2ExcelTransformer();
		afg.genAnnFile(
				"E:\\independent study\\Revision\\Braverman_dataset\\class4\\draft1-preprocessed",
				"E:\\independent study\\Revision\\Braverman_dataset\\class4\\draft2-preprocessed",
				"E:\\independent study\\Revision\\Braverman\\class4", "P1D1",
				"P2D1");
		// afg.genAnnFile("E:\\independent study\\sentence alignment\\Chris_lines\\class2/draft1-preprocessed",
		// "E:\\independent study\\sentence alignment\\Chris_lines\\class2/draft2-preprocessed",
		// "E:\\independent study\\Revision\\all_data\\class2", "P1D1", "P1D2");
	}
}
