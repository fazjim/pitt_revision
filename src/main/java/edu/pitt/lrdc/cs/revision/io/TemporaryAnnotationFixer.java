package edu.pitt.lrdc.cs.revision.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import edu.pitt.lrdc.cs.revision.model.RevisionDocument;

/**
 * This class is solely for the annotation fitting in the current dataset
 * will deprecate once annotation is made automatic
 * 
 * @author zhangfan
 *
 */
public class TemporaryAnnotationFixer {
	public static void addParagraphInfo(String folderPath, String absolutePath, RevisionDocument doc) throws IOException {
		String matchFilePath = fetchMatchedFile(folderPath,absolutePath);
		ArrayList<Hashtable<Integer,Integer>> paraInfo = getParagraphInfo(matchFilePath);
		Hashtable<Integer,Integer> oldParaInfo = paraInfo.get(0);
		Hashtable<Integer,Integer> newParaInfo = paraInfo.get(1);
		
		Iterator<Integer> it = oldParaInfo.keySet().iterator();
		while(it.hasNext()) {
			int sentenceIndex = it.next();
			int paraNo = oldParaInfo.get(sentenceIndex);
			doc.addOldSentenceParaMap(sentenceIndex, paraNo);
		}
		
		Iterator<Integer> it2 = newParaInfo.keySet().iterator();
		while(it2.hasNext()) {
			int sentenceIndex = it2.next();
			int paraNo = newParaInfo.get(sentenceIndex);
			doc.addNewSentenceParaMap(sentenceIndex, paraNo);
		}
	}
	
	public static ArrayList<Hashtable<Integer,Integer>> getParagraphInfo(String filePath) throws IOException {
		ArrayList<Hashtable<Integer,Integer>> paraInfo = new ArrayList<Hashtable<Integer,Integer>>();
		Hashtable<Integer,Integer> oldParaInfo = new Hashtable<Integer,Integer>();
		Hashtable<Integer,Integer> newParaInfo = new Hashtable<Integer,Integer>();
		paraInfo.add(oldParaInfo);
		paraInfo.add(newParaInfo);
		
		FileInputStream fis = new FileInputStream(new File(filePath));
		XSSFWorkbook xwb = new XSSFWorkbook(fis);
		
		//Original document
		XSSFSheet sheet0 = xwb.getSheetAt(0);
		XSSFSheet sheet1 = xwb.getSheetAt(1);
		
		int sheet0_oldPara = 1;
		int sheet1_oldPara = 2;
		int sheet1_newPara = 3;
		if(sheet0.getRow(0).getCell(2)!=null&&!sheet0.getRow(0).getCell(2).toString().equals("")) {
			sheet0_oldPara = 2;
		}
		
		/**
		 * Fetch the paragraph number from old draft
		 */
		int currParaNo = 1;
		for(int i = 0;i<sheet0.getPhysicalNumberOfRows();i++) 
		{
			XSSFRow row = sheet0.getRow(i);
			if(row.getCell(sheet0_oldPara)!=null) {
				int paraNo = (int)row.getCell(sheet0_oldPara).getNumericCellValue();
				currParaNo = paraNo;
			} 
			paraInfo.get(0).put(i+1, currParaNo);
		}
		
		/**
		 * Fetch the paragraph number from new draft
		 */
		for(int i = 0;i<sheet1.getPhysicalNumberOfRows();i++)
		{
			XSSFRow row = sheet1.getRow(i);
			if(row.getCell(sheet1_newPara)!=null) {
				int paraNo = (int)row.getCell(sheet1_newPara).getNumericCellValue();
				paraInfo.get(1).put(i+1, paraNo);
			}
		}
		return paraInfo;
	}
	
	public static String fetchMatchedFile(String folderPath, String absolutePath) {
		File realFile = new File(absolutePath);
		
		File folder = realFile.getParentFile();
		String classFolder = folder.getName();
		String matchFolder = folderPath+File.separator+classFolder;
		File matchFolderFile = new File(matchFolder);
		String realFileName = realFile.getName();
		String prefix = realFileName.substring(0,realFileName.lastIndexOf(" "));
		if(prefix.contains("Annotation")) {
			prefix = prefix.substring(12);
		}
		File[] candidates = matchFolderFile.listFiles();
		for(int i = 0;i<candidates.length;i++) {
			if(candidates[i].getName().contains(prefix)) {
				return candidates[i].getAbsolutePath();
			}
		}
		return "";
	}
	
	public static void main(String[] args) {
		System.out.println(fetchMatchedFile("D:/independent study\\Revision\\Braverman\\data_process","C:\\Not Backed Up\\data\\annotated\\class4\\Annotation__scarlettxo Fan.xlsx"));
	}
}
