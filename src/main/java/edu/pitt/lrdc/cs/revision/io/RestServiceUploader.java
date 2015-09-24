package edu.pitt.lrdc.cs.revision.io;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

public class RestServiceUploader {
	public void uploadFile(String url, String username, String fileName) {
		 String charset = "UTF-8";
	        File uploadFile = new File(fileName);
	        //String requestURL = "http://localhost:8080/FileUploadSpringMVC/uploadFile.do";
	        String requestURL = url+"?username="+username;
	        try {
	            MultipartUtility multipart = new MultipartUtility(requestURL, charset);
	             
	            multipart.addHeaderField("User-Agent", "Pitt-AnnotatorTool");
	            multipart.addHeaderField("Test-Header", "Header-Value");
	             
	            //multipart.addFormField("description", "Cool Pictures");
	            //multipart.addFormField("keywords", "Java,upload,Spring");
	             
	            multipart.addFilePart("file", uploadFile);
	 
	            List<String> response = multipart.finish();
	             
	            System.out.println("SERVER REPLIED:");
	             
	            for (String line : response) {
	                System.out.println(line);
	            }
	        } catch (IOException ex) {
	            System.err.println(ex);
	        }
	}
}
