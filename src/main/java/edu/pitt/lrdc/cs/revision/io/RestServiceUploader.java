package edu.pitt.lrdc.cs.revision.io;

import java.io.File;
import java.io.IOException;
import java.util.List;


public class RestServiceUploader {
	public static String uploadFile(String url, String username, String fileName) {
		 String charset = "UTF-8";
	        File uploadFile = new File(fileName);
	        //String requestURL = "http://localhost:8080/FileUploadSpringMVC/uploadFile.do";
	        String requestURL = url;
	        try {
	            MultipartUtility multipart = new MultipartUtility(requestURL, charset);
	             
	            multipart.addHeaderField("User-Agent", "Pitt-AnnotatorTool");
	            multipart.addHeaderField("Test-Header", "Header-Value");
	             
	            multipart.addFormField("username", username);
	            //multipart.addFormField("keywords", "Java,upload,Spring");
	             
	            multipart.addFilePart("file", uploadFile);
	 
	            List<String> response = multipart.finish();
	             
	            String output = "";   
	            for (String line : response) {
	               output += line + "\n";
	            }
	            return output;
	        } catch (IOException ex) {
	        	ex.printStackTrace();
	           return ex.getMessage();
	        }
	        
	}
}
