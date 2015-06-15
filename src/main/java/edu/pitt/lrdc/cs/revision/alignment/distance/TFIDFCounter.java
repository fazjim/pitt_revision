package edu.pitt.lrdc.cs.revision.alignment.distance;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;

import edu.pitt.lrdc.cs.revision.alignment.model.Document;
import edu.pitt.lrdc.cs.revision.alignment.model.DocumentPair;
import edu.pitt.lrdc.cs.revision.alignment.model.Sentence;

public class TFIDFCounter extends SimCalculator{
	Hashtable<String,Double> freqTable = new Hashtable<String,Double>();
	Hashtable<String,Integer> idfTable = new Hashtable<String,Integer>();
	int total = 0;
	
	
	
	public Hashtable<String, Double> getFreqTable() {
		return freqTable;
	}
	public void setFreqTable(Hashtable<String, Double> freqTable) {
		this.freqTable = freqTable;
	}
	public Hashtable<String, Integer> getIdfTable() {
		return idfTable;
	}
	public void setIdfTable(Hashtable<String, Integer> idfTable) {
		this.idfTable = idfTable;
	}
	public int getTotal() {
		return total;
	}
	public void setTotal(int total) {
		this.total = total;
	}
	public void countFile(Document doc) {
		//For IDF Counting
		HashSet<String> words = new HashSet<String>();
		ArrayList<Sentence> sens = doc.getSentences();
		for(int j = 0;j<sens.size();j++) {
			Sentence s = sens.get(j);
			String content = s.getContent().toLowerCase();
			content = content.replaceAll("\\.", " ");
			content = content.replaceAll(",", " ");
			content = content.replaceAll("!", " ");
			
			String[] tokens = content.split(" ");
			for(int k = 0;k<tokens.length;k++) {
				words.add(tokens[k]);
				if(freqTable.containsKey(tokens[k])) {
					freqTable.put(tokens[k], freqTable.get(tokens[k])+1);
				} else {
					freqTable.put(tokens[k], 1.0);
				}
				total ++;
			}
		}
		Iterator<String> it = words.iterator();
		while(it.hasNext()) {
			String key = it.next();
			if(idfTable.containsKey(key)) {
				idfTable.put(key, idfTable.get(key)+1);
			} else {
				idfTable.put(key, 1);
			}
		}
	}
	public void loadTable(ArrayList<DocumentPair> docPairs) {
		for(int i = 0;i<docPairs.size();i++) {
			DocumentPair dp = docPairs.get(i);
			Document src = dp.getSrc();
			Document dst = dp.getModified();
			
			countFile(src);
			countFile(dst);
		}
		Iterator<String> it = freqTable.keySet().iterator();
		while(it.hasNext()) {
			String word = it.next();
			double freq = freqTable.get(word);
			freq = freq/total;
			double idf = (docPairs.size()*2.0)/idfTable.get(word);
			idf = Math.log(idf);
			double tfIdf = freq*idf;
			freqTable.put(word, tfIdf);
		}
	}
	
	public double calcOverlap(Sentence srcS,Sentence dstS) {
		String src = srcS.getContent();
		String dst = dstS.getContent();
		src = src.toLowerCase();
		dst = dst.toLowerCase();
		src = src.replaceAll("\\.", " ");
		src = src.replaceAll(",", " ");
		src = src.replaceAll("!", " ");

		dst = dst.replaceAll("\\.", " ");
		dst = dst.replaceAll(",", " ");
		dst = dst.replaceAll("!", " ");

		String[] srcTokens = src.split(" ");
		String[] dstTokens = dst.split(" ");
		
		double val = 0;
		for(int i = 0;i<srcTokens.length;i++) {
			String word = srcTokens[i];
			for(int j = 0;j<dstTokens.length;j++) {
				if(dstTokens[j].equals(word)) {
					val += 1;
				}
			}
		}
		return val/(srcTokens.length);
	}
	
	public double calc(Sentence srcS, Sentence dstS) {
		String src = srcS.getContent();
		String dst = dstS.getContent();
		src = src.toLowerCase();
		dst = dst.toLowerCase();
		src = src.replaceAll("\\.", " ");
		src = src.replaceAll(",", " ");
		src = src.replaceAll("!", " ");

		dst = dst.replaceAll("\\.", " ");
		dst = dst.replaceAll(",", " ");
		dst = dst.replaceAll("!", " ");

		String[] srcTokens = src.split(" ");
		String[] dstTokens = dst.split(" ");
		
		double val = 0;
		for(int i = 0;i<srcTokens.length;i++) {
			String word = srcTokens[i];
			
			for(int j = 0;j<dstTokens.length;j++) {
				if(dstTokens[j].equals(word)) {
					double freq1 = freqTable.get(word);
					val += freq1*freq1;
				}
			}
		}
		
		double realLength = 0;
		for(int i = 0;i<srcTokens.length;i++) {
			String word = srcTokens[i];
			//if(word == null) System.err.println("SB");
			double freq1 = 0;
			if(freqTable.containsKey(word)) freq1 = freqTable.get(word);
			realLength += freq1*freq1;
		}
		realLength = Math.sqrt(realLength);
		
		double realLength2 = 0;
		for(int j = 0;j<dstTokens.length;j++) {
			String word = dstTokens[j];
			double freq2 = 0;
			if(freqTable.containsKey(word)) freq2 = freqTable.get(word);
			realLength2 += freq2*freq2;
		}
		realLength2 = Math.sqrt(realLength2);
		//return val;
		//return val/srcTokens.length;
		//return val/(srcTokens.length*dstTokens.length);
		return val/(realLength*realLength2);
	}
}
