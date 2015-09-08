package edu.pitt.lrdc.cs.revision.process;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

import org.apache.poi.POITextExtractor;
import org.apache.poi.extractor.ExtractorFactory;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.tika.io.FilenameUtils;
import org.apache.xmlbeans.XmlException;

public class Docx2TxtTransformer {
	public static void transform(String srcPath, String dstPath) throws IOException, InvalidFormatException, OpenXML4JException, XmlException {
		InputStream fis = new FileInputStream(srcPath);
		POITextExtractor extractor;
		//System.out.println("SRC:"+srcPath);
		// if docx
		if (srcPath.toLowerCase().endsWith(".docx")) {
		    XWPFDocument doc = new XWPFDocument(fis);
		    extractor = new XWPFWordExtractor(doc);
		} else {
		    // if doc
		    POIFSFileSystem fileSystem = new POIFSFileSystem(fis);
		    extractor = ExtractorFactory.createExtractor(fileSystem);
		}
		String extractedText = extractor.getText();
		File f = new File(dstPath);
		if(!f.getParentFile().exists()) f.getParentFile().mkdirs();
		BufferedWriter writer = new BufferedWriter(new FileWriter(dstPath));
		writer.write(extractedText);
		writer.close();
	}
	
	public static void transformFolder(String srcFolderPath, String dstFolderPath) throws InvalidFormatException, IOException, OpenXML4JException, XmlException {
		String srcFolderPath1 = srcFolderPath + "/draft1";
		String srcFolderPath2 = srcFolderPath + "/draft2";
		String dstFolderPath1 = dstFolderPath + "/draft1";
		String dstFolderPath2 = dstFolderPath + "/draft2";
		
		transformSingleFolder(srcFolderPath1,dstFolderPath1);
		transformSingleFolder(srcFolderPath2,dstFolderPath2);
	}
	
	public static void transformSingleFolder(String srcFolderPath, String dstFolderPath) throws InvalidFormatException, IOException, OpenXML4JException, XmlException {
		File root = new File(srcFolderPath);
		File[] files = root.listFiles();
		for(File file: files) {
			String dstFile = dstFolderPath + "/" +  file.getName().replace(".docx", ".txt");
			transform(file.getAbsolutePath(),dstFile);
		}
	}
	
	public static void main(String[] args) throws InvalidFormatException, IOException, OpenXML4JException, XmlException {
		transformFolder("C:\\Not Backed Up\\data\\Braverman_raw","C:\\Not Backed Up\\data\\Braverman_raw_txt");
	}
}
