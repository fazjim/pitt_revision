package edu.pitt.cs.revision.purpose.pdtb;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.StringUtils;

public class parseTree {
	
	public static LinkedList<String> revertToTokens(String parse) {
		/*
		 * Convert a parse tree to original list of tokens
		 */
		Tree T = Tree.valueOf(parse);		
		List<Tree> Leaves = T.getLeaves();
		
		LinkedList<String> Tokens = new LinkedList<>();
        for (Tree l : Leaves) {
            Tokens.add(l.nodeString());
        }
        return Tokens;
	}
	
	public static String revertToString(String parse) {
		/*
		 * Convert a parse tree to original text
		 */
		LinkedList<String> Tokens = revertToTokens(parse);
		return StringUtils.join(Tokens, " ");
	}
	
	public static LinkedList<String> revertParseFile(String infile) throws IOException {
		/*
		 * Read a parse file, sentences a delimited by blank line
		 * For each parse tree, extract the original sentence
		 * Return a list of sentences
		 */
		System.out.println("[INFO] readParseFile " + infile);

		BufferedReader bf = new BufferedReader(new FileReader(infile));
		LinkedList<String> sentences = new LinkedList<>();
		String aline = "";
		String parse = "";		
		
		while ((aline = bf.readLine()) != null) {
			if (aline.trim().isEmpty()) {
				if (!parse.isEmpty()) {
					sentences.add(revertToString(parse));
					parse = "";
				}
			}
			else {
				parse += aline + " ";
			}
		}
		bf.close();
		if (!parse.isEmpty()) {
			sentences.add(revertToString(parse));
			parse = "";
		}
		System.out.println("[--->] Done. " + sentences.size());
		return sentences;
	}

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		String infile = "d:/Downloads/parser.out/90s_kidd.txt.ptree";
		revertParseFile(infile);
	}
}
