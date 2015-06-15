package edu.pitt.lrdc.cs.revision.process;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import edu.pitt.lrdc.cs.revision.io.RevisionDocumentReader;
import edu.pitt.lrdc.cs.revision.io.StatisticInfoWriter;
import edu.pitt.lrdc.cs.revision.model.RevisionDocument;
import edu.pitt.lrdc.cs.revision.model.StatisticInfo;
import edu.pitt.lrdc.cs.revision.statistics.DataStatistics;

/**
 * Combines the other classes in this package, and generates the annotation files
 * And insert the paragraph information
 * 
 * @author zhangfan
 * @version 1.0
 *
 */
public class BatchProcessor {
	public String extractHeaderName(String path) {
		String header = path.substring(0,path.indexOf("-")).trim();
		return header;
	}
	
	private boolean doAlign = false;
	private boolean doRevLabel = false;
	private String alignPathTrain = "";
	private String revLabelPathTrain = "";
	
	
	public void setAlign(String alignPathTrain) {
		doAlign = true;
		this.alignPathTrain = alignPathTrain;
	}
	
	public void setRevLabel(String revLabelPathTrain) {
		doRevLabel = true;
		this.revLabelPathTrain = revLabelPathTrain;
	}
	/**
	 * Do all the transformation
	 * @param srcFolderPath
	 * @param destFolderPath
	 * @param draft1FolderName  (Just use draft1)
	 * @param draft2FolderName  (Just use draft2)
	 * @throws Exception 
	 */
	public void transform(String srcFolderPath, String destFolderPath, String draft1FolderName, String draft2FolderName) throws Exception {
		draft1FolderName = "draft1";
		draft2FolderName = "draft2";
		String srcFolderD1 = srcFolderPath + "/" + draft1FolderName;
		String srcFolderD2 = srcFolderPath + "/" + draft2FolderName;
		
		String header1 = "";
		String header2 = "";
		
		String lineFolderD1 = srcFolderPath + "/" + "draft1-preprocessed";
		String lineFolderD2 = srcFolderPath + "/" + "draft2-preprocessed";
		File folderCreater = new File(lineFolderD1);
		if(!folderCreater.exists()) folderCreater.mkdir();
		folderCreater = new File(lineFolderD2);
		if(!folderCreater.exists()) folderCreater.mkdir();
		
		lineFolderD1 = srcFolderPath + "/" + "draft1-preprocessed-discourse";
		lineFolderD2 = srcFolderPath + "/" + "draft2-preprocessed-discourse";
		folderCreater = new File(lineFolderD1);
		if(!folderCreater.exists()) folderCreater.mkdir();
		folderCreater = new File(lineFolderD2);
		if(!folderCreater.exists()) folderCreater.mkdir();
		
		//Step 1. Transform the files to lines
		//After this step, new folders "draft1-preprocessed", "draft2-preprocessed" will be generated
		Txt2LineTransformer t2l = new Txt2LineTransformer();
		File d1Src = new File(srcFolderD1);
		File d2Src = new File(srcFolderD2);
		File[] subD1 = d1Src.listFiles();
		File[] subD2 = d2Src.listFiles();
		for(File f: subD1) {
			if(header1.equals("")) {
				header1 = extractHeaderName(f.getName());
			}
			t2l.processFileDiscourse(f.getAbsolutePath(), "");
		}
		for(File f: subD2) {
			if(header2.equals("")) {
				header2 = extractHeaderName(f.getName());
			}
			t2l.processFileDiscourse(f.getAbsolutePath(), "");
		}
		
		//Step 2. Generating excel files
		File processFolder = new File(srcFolderPath + "/data_process");
		if(!processFolder.exists()) processFolder.mkdir();
		processFolder = new File(srcFolderPath+"/data_processed");
		if(!processFolder.exists()) processFolder.mkdir();
		Line2ExcelTransformer l2e = new Line2ExcelTransformer();
		l2e.genAnnFile(lineFolderD1, lineFolderD2, srcFolderPath+"/"+"data_process", header1, header2);
		
		folderCreater = new File(destFolderPath);
		if(!folderCreater.exists()) folderCreater.mkdir();
		
		//Step3. Generating the annotation file from the excel file
		Excel2AnnotationTransformer e2a = new Excel2AnnotationTransformer();
		File srcExcelPath = new File(srcFolderPath + "/data_process");
		File[] processFiles = srcExcelPath.listFiles();
		for(File f: processFiles) {
			e2a.formatFile(f.getAbsolutePath(), srcFolderPath+"/data_processed");
		}
		
		//Step4. Adding the paragraph information
		InfoAdder ia = new InfoAdder();
		if(doAlign) ia.setAlignment(alignPathTrain);
		if(doRevLabel) ia.setPredict(revLabelPathTrain);
		ia.addInfo(srcFolderPath+"/data_processed", destFolderPath, srcFolderPath);
	}
	
	public static void main(String[] args) throws Exception {
		BatchProcessor bp = new BatchProcessor();
		String alignTrain = "C:\\Not Backed Up\\data\\alok_task\\train";
		String revTrain = "C:\\Not Backed Up\\data\\alok_task\\train";
		//bp.setAlign("C:\\Not Backed Up\\data\\annotated\\class3");
		//bp.setRevLabel("");
		bp.setAlign(alignTrain);
		//bp.setRevLabel(revTrain);
		//bp.transform("C:\\Not Backed Up\\test\\condition1", "C:\\Not Backed Up\\test\\annotation1", "draft1", "draft2");
		//bp.transform("C:\\Not Backed Up\\test\\condition2", "C:\\Not Backed Up\\test\\annotation2", "draft1", "draft2");
		bp.transform("C:\\Not Backed Up\\data\\lit\\annotationFiles", "C:\\Not Backed Up\\data\\lit\\annotate", "draft1", "draft2");
		
		/*RevisionDocumentReader reader = new RevisionDocumentReader();
		ArrayList<RevisionDocument> docs1 = reader.readDocs("C:\\Not Backed Up\\test\\annotation1");
		ArrayList<RevisionDocument> docs2 = reader.readDocs( "C:\\Not Backed Up\\test\\annotation2");
		docs1.addAll(docs2);*/
		/*
		DataStatistics ds = new DataStatistics();
		ds.stat(docs);*/
		/*ArrayList<StatisticInfo> stats = new ArrayList<StatisticInfo>();
		for(RevisionDocument doc : docs1) {
			stats.add(doc.toStatisticInfo());
		}
		StatisticInfoWriter.writeStaticInfo(stats, "C:\\Not Backed Up\\test\\allInfo.xlsx");*/
	}
}
