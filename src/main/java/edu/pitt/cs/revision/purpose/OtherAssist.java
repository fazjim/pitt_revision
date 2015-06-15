package edu.pitt.cs.revision.purpose;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;

import edu.pitt.lrdc.cs.revision.model.RevisionDocument;
import edu.pitt.lrdc.cs.revision.model.RevisionUnit;

/**
 * Provide additional toolkits for use
 * @author zhangfan
 *
 */
public class OtherAssist {
	/**
	 * Get the sentences of the unit
	 * @param doc
	 * @param ru
	 * @return
	 */
	public static String[] getSentences(RevisionDocument doc, RevisionUnit ru) {
		String[] sentences = new String[2];
		String oldSentence = "";
		String newSentence = "";
		ArrayList<Integer> oldIndices = ru.getOldSentenceIndex();
		for(Integer oldIndex: oldIndices) {
			if(oldIndex!=-1) {
				oldSentence += doc.getOldSentence(oldIndex)+"\n";
			}
		}
		ArrayList<Integer> newIndices = ru.getNewSentenceIndex();
		for(Integer newIndex: newIndices) {
			if(newIndex!=-1) {
				newSentence += doc.getNewSentence(newIndex) + "\n";
			}
		}
		sentences[0] = oldSentence;
		sentences[1] = newSentence;
		return sentences;
	}
	
	private static int minimum(int a,int b, int c) {
		return Math.min(Math.min(a, b), c);
	}
	
	//Get LD distance, code got from wiki directly
	public static double getLevenDistance(String oldSentence, String newSentence) {
		/*String[] sentences = getSentences(doc, ru);
		String oldSentence = sentences[0];
		String newSentence = sentences[1];*/
		int[][] distance = new int[oldSentence.length() + 1][newSentence.length() + 1];        
		 
        for (int i = 0; i <= oldSentence.length(); i++)                                 
            distance[i][0] = i;                                                  
        for (int j = 1; j <= newSentence.length(); j++)                                 
            distance[0][j] = j;                                                  
 
        for (int i = 1; i <= oldSentence.length(); i++)                                 
            for (int j = 1; j <= newSentence.length(); j++)                             
                distance[i][j] = minimum(                                        
                        distance[i - 1][j] + 1,                                  
                        distance[i][j - 1] + 1,                                  
                        distance[i - 1][j - 1] + ((oldSentence.charAt(i - 1) == newSentence.charAt(j - 1)) ? 0 : 1));
 
        return distance[oldSentence.length()][newSentence.length()]; 
	}
	
	//Get damerau-levenshtein distance, adding the order switching here
	public static double getOptimalAlignDistance(String oldSentence, String newSentence) {
		/*String[] sentences = getSentences(doc, ru);
		String oldSentence = sentences[0];
		String newSentence = sentences[1];*/
		int[][] distance = new int[oldSentence.length() + 1][newSentence.length() + 1];        
		 
        for (int i = 0; i <= oldSentence.length(); i++)                                 
            distance[i][0] = i;                                                  
        for (int j = 1; j <= newSentence.length(); j++)                                 
            distance[0][j] = j;                                                  
 
        for (int i = 1; i <= oldSentence.length(); i++) {                                 
            for (int j = 1; j <= newSentence.length(); j++) {                             
                distance[i][j] = minimum(                                        
                        distance[i - 1][j] + 1,                                  
                        distance[i][j - 1] + 1,                                  
                        distance[i - 1][j - 1] + ((oldSentence.charAt(i - 1) == newSentence.charAt(j - 1)) ? 0 : 1));
                if(i>1 && j>1 && i < oldSentence.length() &&  j< newSentence.length() && oldSentence.charAt(i) == newSentence.charAt(j-1) && oldSentence.charAt(i-1) == newSentence.charAt(j)) {
                	distance[i][j] = Math.min(distance[i][j], distance[i-2][j-2]+1);
                }
            }
        }
        return distance[oldSentence.length()][newSentence.length()];
	}
	
	/**
	 * Get the cosine similarity
	 * @param doc
	 * @param ru
	 * @return
	 */
	public static double getCosine(String oldSentence, String newSentence) {
		//String[] sentences = getSentences(doc, ru);
		//String oldSentence = sentences[0];
		//String newSentence = sentences[1];
		int oldSize = oldSentence.toLowerCase().trim().split(" ").length;
		int newSize = newSentence.toLowerCase().trim().split(" ").length;
		
		String[] oldTokens = oldSentence.toLowerCase().trim().split(" ");
		String[] newTokens = newSentence.toLowerCase().trim().split(" ");
		
		Hashtable<String, Integer> oldMap = new Hashtable<String,Integer>();
		Hashtable<String,Integer> newMap = new Hashtable<String,Integer>();
		
		for(String token: oldTokens) {
			if(oldMap.containsKey(token)) {
				oldMap.put(token, oldMap.get(token)+1);
			} else {
				oldMap.put(token, 1);
			}
		}
		
		for(String token: newTokens) {
			if(newMap.containsKey(token)) {
				newMap.put(token, newMap.get(token)+1);
			} else {
				newMap.put(token, 1);
			}
		}
		
		double val = 0.0;
		double lenOld = 0.0;
		Iterator<String> it = oldMap.keySet().iterator();
		while(it.hasNext()) {
			String word = it.next();
			if(newMap.containsKey(word)) {
				val += oldMap.get(word)*newMap.get(word);
			}
			lenOld += Math.pow(oldMap.get(word),2);
		}
		
		double lenNew = 0.0;
		Iterator<String> it2 = newMap.keySet().iterator();
		while(it.hasNext()) {
			String word = it.next();
			lenNew += Math.pow(newMap.get(word),2);
		}
		
		double result = val/ Math.sqrt(lenOld)*Math.sqrt(lenNew);
		return result;
	}
	
	/**
	 * Get the number of capital characters
	 * @param sentence
	 * @return
	 */
	public static int getCapitalNum(String sentence) {
		int numCapital = 0;
		for(int i = 0;i<sentence.length();i++) {
			if(sentence.charAt(i)>='A'&&sentence.charAt(i)<='Z') {
				numCapital++;
			}
		}
		return numCapital;
	}
	
	/**
	 * Get the number of digit numbers
	 * @param sentence
	 * @return
	 */
	public static int getDigitNum(String sentence) {
		int numDigit = 0;
		for(int i = 0;i<sentence.length();i++) {
			if(Character.isDigit(sentence.charAt(i))) numDigit++;
		}
		return numDigit;
	}
	
	/**
	 * Get the number of special characters
	 * @param sentence
	 * @return
	 */
	public static int getSpecialCharacterNum(String sentence) {
		int numSpecial = 0;
		for(int i = 0;i<sentence.length();i++) {
			char c = sentence.charAt(i);
			if(c == '&' || c == '_' || c == '^') {  //expanding the list later
				numSpecial++;
			}
		}
		return numSpecial;
	}
	
	/**
	 * Get the number of space
	 * @param sentence
	 * @return
	 */
	public static int getSpaceNum(String sentence) {
		int numSpace = 0;
		for(int i =0;i<sentence.length();i++) {
			char c = sentence.charAt(i);
			if(c == ' ') {
				numSpace++;
			}
		}
		return numSpace;
	}
}
