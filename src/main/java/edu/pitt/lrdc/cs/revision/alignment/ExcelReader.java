package edu.pitt.lrdc.cs.revision.alignment;

import java.io.File;
import java.io.IOException;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import edu.pitt.lrdc.cs.revision.alignment.model.Document;
import edu.pitt.lrdc.cs.revision.alignment.model.DocumentPair;


/**
 * @deprecated
 * Used for the old aligner, the revision project does not need this class as RevisionDocument is loaded first
 * 
 * @author zhangfan
 * @version 1.0
 *
 */

public class ExcelReader {
	public DocumentPair readDocs(String path) throws IOException {
		DocumentPair dp = new DocumentPair();
		dp.setFileName(new File(path).getName());
		System.out.println("READING..." + dp.getFileName());
		XSSFWorkbook xwb = new XSSFWorkbook(path);
		int wordcnt = 0;
		// Read Src;
		Document src = new Document();
		XSSFSheet sheet = xwb.getSheetAt(0);
		XSSFRow row;
		String cell;
		for (int i = sheet.getFirstRowNum(); i < sheet
				.getPhysicalNumberOfRows(); i++) {
			row = sheet.getRow(i);
			if (row == null)
				break;
			cell = row.getCell(row.getFirstCellNum()).toString();
			src.addSentence(i + 1, cell);
			wordcnt += cell.split(" ").length;
		}

		// Read Dst
		Document dst = new Document();
		XSSFSheet sheet2 = xwb.getSheetAt(1);
		XSSFRow row2;
		String cell2;
		String aligns;
		for (int i = sheet2.getFirstRowNum(); i < sheet2
				.getPhysicalNumberOfRows(); i++) {
			row2 = sheet2.getRow(i);
			if (row2 != null) {
				cell2 = row2.getCell(row2.getFirstCellNum()).toString();
				// System.out.println(cell2);
				if (cell2 != null && cell2.trim().length() != 0) {
					if (row2.getCell(row2.getFirstCellNum() + 1) != null) {
						aligns = row2.getCell(row2.getFirstCellNum() + 1)
								.toString();
						dst.addAlignedSentence(i + 1, cell2, aligns);
					} else {
						dst.addAlignedSentence(i + 1, cell2, "");
					}
				}
			}
		}

		dp.setSrc(src);
		dp.setModified(dst);
		System.out.println(path + ":" + wordcnt);
		return dp;
	}

	public static void main(String[] args) throws IOException {
		ExcelReader reader = new ExcelReader();
		File path = new File("E:\\independent study\\Revision\\test");
		File[] subs = path.listFiles();
		for (int i = 0; i < subs.length; i++) {
			DocumentPair pair = reader.readDocs(subs[i].getAbsolutePath());
			System.out.println("Sent0:"
					+ pair.getSrc().getSentences().get(0).getContent());
			System.out.println("Sent0:"
					+ pair.getModified().getSentences().get(0).getContent());
			// reader.readDocs("E:\\independent study\\sentence alignment\\aligned_result\\aardvark.xlsx");
		}
	}
}
