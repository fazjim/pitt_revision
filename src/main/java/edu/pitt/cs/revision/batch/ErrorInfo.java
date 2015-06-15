package edu.pitt.cs.revision.batch;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.StringTokenizer;

public class ErrorInfo {
	private int spellErrorNo;
	private int grammarErrorNo;
	
	public void setSpellErrNum(int num) {
		spellErrorNo = num;
	}
	public void setGrammarErrNum(int num) {
		grammarErrorNo = num;
	}
	
	public int getNumofSpellError() {
		return this.spellErrorNo;
	}
	
	public int getNumOfGrammarError() {
		return this.grammarErrorNo;
	}
	
	public void toFile(String path) throws IOException {
		File file = new File(path);
		if(!file.getParentFile().exists()) file.getParentFile().mkdirs();
		BufferedWriter writer = new BufferedWriter(new FileWriter(path));
		String sent = "";
		sent += "SPELL:"+spellErrorNo;
		sent += "\t";
		sent += "GRAMMAR:"+grammarErrorNo;
		writer.write(sent);
		writer.close();
	}
	
	public void fromFile(String path) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(path));
		String sentence = reader.readLine();
		StringTokenizer stk = new StringTokenizer(sentence, "\t");
		while(stk.hasMoreTokens()) {
			String tag = stk.nextToken();
			if(tag.startsWith("SPELL:")) {
				int val = Integer.parseInt(tag.substring(tag.lastIndexOf(":")+1));
				this.setSpellErrNum(val);
			} else if(tag.startsWith("GRAMMAR:")) {
				int val = Integer.parseInt(tag.substring(tag.lastIndexOf(":")+1));
				this.setGrammarErrNum(val);
			}
		}
		reader.close();
	}
}
