package edu.pitt.lrdc.cs.revision.process;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * generating the annotation file
 * @author zhangfan
 *
 */
public class Excel2AnnotationTransformer {
	public static void main(String[] args) throws IOException {
		String srcPath = "E:\\independent study\\Revision\\Braverman\\data_process/class4";
		String rootPath = "E:\\independent study\\Revision\\Braverman\\data_process/class4_New";
		
		Excel2AnnotationTransformer aff = new Excel2AnnotationTransformer();
		File src = new File(srcPath);
		File[] files = src.listFiles();
		for(int  i = 0;i<files.length;i++) {
			aff.formatFile(files[i].getAbsolutePath(),rootPath);
		}
	}
	
	public void formatFile(String filePath, String rootPath) throws IOException 
	{
		File src = new File(filePath);
		String fileName = src.getName();
		fileName = "Annotation_"+fileName;
		String destFile = rootPath + "/" + fileName;
		
		FileInputStream fis = new FileInputStream(new File(filePath));
		XSSFWorkbook xwb = new XSSFWorkbook(fis);
		
		String col0 = "Sentence Index";
		String col1 = "Sentence Content";
		String col2 = "Aligned Index";
		String col3 = "Original Paragraph No";
		String col4 = "New Paragraph No";
		String col5 = "Revision Purpose";
		String col6 = "Revision Operation";//ADD,DELETE,MODIFY
		String col7 = "Revision Index";
		String col8 = "Revision Purpose-2nd layer";
		String col9 = "Revision Operation-2nd layer";
		String col10 = "Revision Index-2nd layer";
		String col11 = "Revision Purpose-3rd layer";
		String col12 = "Revision Operation-3rd layer";
		String col13 = "Revision Index-3rd layer";
		
		
		//Original document
		XSSFSheet sheet0 = xwb.getSheetAt(0);
		XSSFSheet sheet1 = xwb.getSheetAt(1);
		
		XSSFWorkbook xwb2 = new XSSFWorkbook();
		XSSFSheet sheet0_New = xwb2.createSheet();
		XSSFSheet sheet1_New = xwb2.createSheet();
		
		//Setup the headers for sheet 0
		XSSFRow header0 = sheet0_New.createRow(0);
		header0.createCell(0).setCellValue(col0);
		header0.createCell(1).setCellValue(col1);
		
		//Setup the headers for sheet 1
		XSSFRow header1 = sheet1_New.createRow(0);
		header1.createCell(0).setCellValue(col0);
		header1.createCell(1).setCellValue(col1);
		header1.createCell(2).setCellValue(col2);
		header1.createCell(3).setCellValue(col3);
		header1.createCell(4).setCellValue(col4);
		header1.createCell(5).setCellValue(col5);
		header1.createCell(6).setCellValue(col6);
		header1.createCell(7).setCellValue(col7);
		header1.createCell(8).setCellValue(col8);
		header1.createCell(9).setCellValue(col9);
		header1.createCell(10).setCellValue(col10);
		header1.createCell(11).setCellValue(col11);
		header1.createCell(12).setCellValue(col12);
		header1.createCell(13).setCellValue(col13);
		
		//Fill in the values
		for(int i = 0;i<sheet0.getPhysicalNumberOfRows();i++) 
		{
			XSSFRow row = sheet0_New.createRow(i+1);
			row.createCell(0).setCellValue(i+1);
			if(sheet0.getRow(i).getCell(0)!=null)
			row.createCell(1).setCellValue(sheet0.getRow(i).getCell(0).toString());
		}
		
	
		for(int i = 0;i<sheet1.getPhysicalNumberOfRows();i++)
		{
			XSSFRow row = sheet1_New.createRow(i+1);
			row.createCell(0).setCellValue(i+1); //index
			if(sheet1.getRow(i).getCell(0)!=null) 
				row.createCell(1).setCellValue(sheet1.getRow(i).getCell(0).getStringCellValue()); //content
			if(sheet1.getRow(i).getCell(1)!=null)  //aligned index
			{
				//String value = sheet1.getRow(i).getCell(1).toString();
				String value = sheet1.getRow(i).getCell(1).toString();
				
				try {
					double val = Double.parseDouble(value);
					int valStr = (int)val;
					row.createCell(2).setCellValue(valStr); 
				} catch(Exception exp) {
					row.createCell(2).setCellValue(value);
				} 		
			}
			if(sheet1.getRow(i).getCell(2)!=null) 
			{
				String value = sheet1.getRow(i).getCell(2).toString();
				//String value = sheet1.getRow(i).getCell(2).getRichStringCellValue().;
				try {
					double val = Double.parseDouble(value);
					int valStr = (int)val;
					row.createCell(3).setCellValue(valStr); 
				} catch(Exception exp) {
					row.createCell(3).setCellValue(value);
				} 
				//row.createCell(3).setCellValue((int)sheet1.getRow(i).getCell(2).getNumericCellValue()); //paragraph 0
				//row.createCell(3).setCellValue((int)sheet1.getRow(i).getCell(2).getNumericCellValue()); //paragraph 0
			}
			if(sheet1.getRow(i).getCell(3)!=null) { 
				String value = sheet1.getRow(i).getCell(3).toString();
				try {
					double val = Double.parseDouble(value);
					int valStr = (int)val;
					row.createCell(4).setCellValue(valStr); 
				} catch(Exception exp) {
					row.createCell(4).setCellValue(value);
				}
				//row.createCell(4).setCellValue((int)sheet1.getRow(i).getCell(3).getNumericCellValue()); //paragraph 1
			}
		}
		
		
		FileOutputStream fileOut = new FileOutputStream(destFile);
		xwb2.write(fileOut);
		fileOut.flush();
		fileOut.close();
	}
}
