package edu.pitt.lrdc.cs.revision.process;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.Writer;
import java.util.List;



import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.Sentence;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.process.WordTokenFactory;

/**
 * Code borrowed from Homa, basically process the Arrow files into txt lines with line after line 
 * 
 * @author zhangfan
 *
 */
public class Txt2LineTransformer {
	
	boolean onOldFile = false;
	
	public String genSenFromList(List<HasWord> sentence) {
		String sentenceStr = "";
		for(HasWord word: sentence) {
			String wordStr = word.toString();
			if(wordStr.equals(".")||wordStr.equals(",")||wordStr.equals("!")||wordStr.equals("?")) {
				sentenceStr +=  wordStr;
			} else {
				sentenceStr += " " + wordStr;
			}
		}
		return sentenceStr.trim();
	}
	
	
	//Prepare the files
	// Seems that I found something wrong with Homa's code, the "." added could be unnecessary
	//In later versions, we should use this method to process the lines
	//Another issue there are lines broken because of line break
	public void processFileDiscourse(String path, String destination) throws IOException {
		File file = new File(path);
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF8"));
		String line, linePrevious = "";
		String buffer = "";
		String fileName = file.toString().substring(file.toString().indexOf("-") + 1, file.toString().indexOf(".txt")).trim();
		String originalText = "";
		while((line = br.readLine()) != null) {
			// this part remover headers and footer from raw text files
			if(line.startsWith(fileName + " - ") && line.contains(". Page ")){
				String temp = line.substring(0, line.indexOf("of ", line.indexOf(". Page ")) + 4);
				line = line.replace(temp , "");
				line = line.replaceFirst("\\d+", "");
			}										
			
			if(line.trim().endsWith(".")||line.trim().endsWith("?")||line.trim().endsWith("!")) {
				originalText += line.trim() + "\n";
			} else {
				originalText += line.trim() + " ";
			}
			// remove section headers such as Abstract,Introduction ,...
			// this condition is only for putting together all the information before abstract
			if(line.trim().toLowerCase().equals("abstract")){
				line = ". " + line + ". ";
				buffer = buffer.replace(".", " ");
			}
			else if(line.length() < 50 && line.length()> 2 && isAlpha(line)){
				if (linePrevious.length()>1 && !linePrevious.endsWith("."))
					line = ". " + line.trim() + ". ";
				else
					line = line.trim() + ". ";
			}		
			buffer = buffer + line + "\n";
			linePrevious = line.trim();
		}
		br.close();
		
		// using Stanford parser to tokenize lines and separate sentences into each line
		//StringReader reader = new StringReader(buffer);
		//----modified by Fan here
		StringReader reader;
		if(onOldFile) { //For the corpus that has been annotated already, they seems fine, so keep the old method for splitting
			reader = new StringReader(buffer); 
		} else { //On scientific corpus, there seems to have problems introduced, use this one
			reader = new StringReader(originalText);
		}
		DocumentPreprocessor docPreprocessor = new DocumentPreprocessor(reader);
			   
		boolean printSentenceLengths = false;
		String options = "normalizeParentheses=false,normalizeOtherBrackets=false,escapeForwardSlashAsterisk=false";
		docPreprocessor.setTokenizerFactory(PTBTokenizer.factory(new WordTokenFactory(),options));
       
		int numSents = 0;
		System.out.println(file.toString());
		String preProcessedFileName = file.toString().replace("draft1", "draft1-preprocessed-discourse");
		preProcessedFileName = preProcessedFileName.replace("draft2", "draft2-preprocessed-discourse");
		
		String preProcessedFileName2 = file.toString().replace("draft1", "draft1-preprocessed");
		preProcessedFileName2 = preProcessedFileName2.replace("draft2", "draft2-preprocessed");
		
		Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(preProcessedFileName + "-sentences.txt"), "UTF-8"));
		Writer out2 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(preProcessedFileName2 + "-sentences.txt"), "UTF-8"));
		
		for (List<HasWord> sentence : docPreprocessor) {
	      numSents++;
	      if (printSentenceLengths) {
	        System.err.println("Length:\t" + sentence.size());
	      }
	      String tmpSentence = genSenFromList(sentence);
	      out.write(tmpSentence);
	      int followingIndex = originalText.indexOf(tmpSentence)+tmpSentence.length();
	      if(followingIndex >= originalText.length() || originalText.charAt(followingIndex) == '\n') out.write("<p>");
	      else out.write("<s>");
	      out2.write(tmpSentence);
	      out2.write("\n");
	    }
	    out.close();
	    out2.close();
	    
	    System.err.println("Read in " + numSents + " sentences.");
	    reader.close();

	}
	
	public void processFile(String path, String destination) throws IOException {
		File file = new File(path);
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF8"));
		String line, linePrevious = "";
		String buffer = "";
		String fileName = file.toString().substring(file.toString().indexOf("-") + 1, file.toString().indexOf(".txt")).trim();
		while((line = br.readLine()) != null) {
			// this part remover headers and footer from raw text files
			if(line.startsWith(fileName + " - ") && line.contains(". Page ")){
				String temp = line.substring(0, line.indexOf("of ", line.indexOf(". Page ")) + 4);
				line = line.replace(temp , "");
				line = line.replaceFirst("\\d+", "");
			}										
			
			
			// remove section headers such as Abstract,Introduction ,...
			// this condition is only for putting together all the information before abstract
			if(line.trim().toLowerCase().equals("abstract")){
				line = ". " + line + ". ";
				buffer = buffer.replace(".", " ");
			}
			else if(line.length() < 50 && line.length()> 2 && isAlpha(line)){
				if (linePrevious.length()>1 && !linePrevious.endsWith("."))
					line = ". " + line.trim() + ". ";
				else
					line = line.trim() + ". ";
			}		
			buffer = buffer + line + "\n";
			linePrevious = line.trim();
		}
		br.close();
		
		// using Stanford parser to tokenize lines and separate sentences into each line
		StringReader reader = new StringReader(buffer);
		DocumentPreprocessor docPreprocessor = new DocumentPreprocessor(reader);
			   
		boolean printSentenceLengths = false;
		String options = "normalizeParentheses=false,normalizeOtherBrackets=false,escapeForwardSlashAsterisk=false";
		docPreprocessor.setTokenizerFactory(PTBTokenizer.factory(new WordTokenFactory(),options));
       
		int numSents = 0;
		System.out.println(file.toString());
		String preProcessedFileName = file.toString().replace("draft1", "draft1-preprocessed");
		preProcessedFileName = preProcessedFileName.replace("draft2", "draft2-preprocessed");
		
		Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(preProcessedFileName + "-sentences.txt"), "UTF-8"));
		
		for (List<HasWord> sentence : docPreprocessor) {
	      numSents++;
	      if (printSentenceLengths) {
	        System.err.println("Length:\t" + sentence.size());
	      }
	      boolean printSpace = false;
	      for (HasWord word : sentence) {
		       if (!word.toString().equals(",")&& printSpace) out.write(" ");
		       printSpace = true;
		       out.write(word.word());
	      }
	      out.write("\n");
	    }
	    out.close();
	    System.err.println("Read in " + numSents + " sentences.");
	    reader.close();
	}
	
	/**
	 * input: String
	 * checks whether the string contains only letters not numbers and special characters
	 * @param name
	 * @return
	 */
	public boolean isAlpha(String name) {
	    char[] chars = name.toCharArray();
	    for (char c : chars) {
	        if(!Character.isLetter(c) && c != ' ') {
	            return false;
	        }
	    }
	    return true;
	}
	
}
