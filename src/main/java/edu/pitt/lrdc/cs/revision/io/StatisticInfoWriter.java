package edu.pitt.lrdc.cs.revision.io;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import edu.pitt.lrdc.cs.revision.model.StatisticInfo;

/**
 * Writing the statistic info results for aloc
 * @author zhangfan
 *
 */
public class StatisticInfoWriter {
	public static void writeStaticInfo(ArrayList<StatisticInfo> stats, String path) throws IOException {
		XSSFWorkbook xwb = new XSSFWorkbook();
		XSSFSheet writeSheet = xwb.createSheet();
		
		
		String pseudoname;
		int additions;
		int deletions;
		int modifications;
		double editedPerecent;
		int surfaceEdits;
		int contentEdits;
		
		// Set up headers
		XSSFRow header0 = writeSheet.createRow(0);
		header0.createCell(0).setCellValue("pseudoname");
		header0.createCell(1).setCellValue("Number of Additions");
		header0.createCell(2).setCellValue("Number of Deletions");
		header0.createCell(3).setCellValue("Number of Modifications");
		header0.createCell(4).setCellValue("Percentage of Edits(modified sentences among all sentences in draft 1)");
		header0.createCell(5).setCellValue("Perecentage of Adds(added sentences among all sentences in draft 2)");
		header0.createCell(6).setCellValue("Percentange of Dels(deleted sentences among all sentences in draft 1)");
		header0.createCell(7).setCellValue("Percentage of edits in draft 1");
		header0.createCell(8).setCellValue("Number of Surface Edits");
		header0.createCell(9).setCellValue("Number of Content Edits");
		
		int rowID = 1;
		for(StatisticInfo info: stats) {
			XSSFRow row = writeSheet.createRow(rowID);
			row.createCell(0).setCellValue(info.getPseudoname());
			row.createCell(1).setCellValue(info.getAdditions());
			row.createCell(2).setCellValue(info.getDeletions());
			row.createCell(3).setCellValue(info.getModifications());
			row.createCell(4).setCellValue(info.getEditedPerecent());
			row.createCell(5).setCellValue(info.getAddPercent());
			row.createCell(6).setCellValue(info.getDeletePercent());
			row.createCell(7).setCellValue(info.getEditedPerecent()+info.getDeletePercent());
			row.createCell(8).setCellValue(info.getSurfaceEdits());
			row.createCell(9).setCellValue(info.getContentEdits());
			rowID++;
		}
		
		FileOutputStream fileOut = new FileOutputStream(path);
		xwb.write(fileOut);
		fileOut.flush();
		fileOut.close();
	}
}
