package edu.pitt.cs.revision.machinelearning;

import java.io.IOException;
import java.util.List;

import org.languagetool.JLanguageTool;
import org.languagetool.Language;
import org.languagetool.language.*;
import org.languagetool.rules.Rule;
import org.languagetool.rules.RuleMatch;

public class JOrthoAssist {
	JLanguageTool langTool;

	private static JOrthoAssist instance = null;
	
	private JOrthoAssist() throws IOException {
		langTool = new JLanguageTool(new AmericanEnglish());
	}
	
	public static JOrthoAssist getInstance() throws IOException {
		if(instance == null) instance = new JOrthoAssist();
		return instance;
	}

	public int checkGrammarMistakes(String sentence) throws IOException {
		int grammars = 0;
		langTool.activateDefaultPatternRules();
		List<RuleMatch> matches = langTool.check(sentence);
		return matches.size();
		// for (RuleMatch match : matches) {
		// System.out.println("Potential typo at line " +
		// match.getLine() + ", column " +
		// match.getColumn() + ": " + match.getMessage());
		// System.out.println("Suggested correction(s): " +
		// match.getSuggestedReplacements());
		// }
		// return grammars;
	}

	public int checkSpellingMistakes(String sentence) throws IOException {
		int grammars = 0;
		for (Rule rule : langTool.getAllRules()) {
			if (!rule.isSpellingRule()) {
				langTool.disableRule(rule.getId());
			}
		}
		List<RuleMatch> matches = langTool.check(sentence);
		return matches.size();
		// for (RuleMatch match : matches) {
		// System.out.println("Potential typo at line " +
		// match.getLine() + ", column " +
		// match.getColumn() + ": " + match.getMessage());
		// System.out.println("Suggested correction(s): " +
		// match.getSuggestedReplacements());
		// }
		// return grammars;
	}

	public static void main(String[] args) throws IOException {
		JOrthoAssist ja = new JOrthoAssist();
		ja.checkGrammarMistakes("I beats him");
		ja.checkSpellingMistakes("He is a bulshit");
	}
}
