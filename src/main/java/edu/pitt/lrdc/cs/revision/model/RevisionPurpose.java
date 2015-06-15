package edu.pitt.lrdc.cs.revision.model;

public class RevisionPurpose {
	public static final int START = 1;
	public static final int CLAIMS_IDEAS = 1;
	public static final int CD_WARRANT_REASONING_BACKING = 2;
	public static final int CD_REBUTTAL_RESERVATION = 3;
	public static final int CD_GENERAL_CONTENT_DEVELOPMENT = 4;
	public static final int EVIDENCE = 5;
	public static final int ORGANIZATION = 6;
	public static final int CONVENTIONS_GRAMMAR_SPELLING = 7;
	public static final int WORDUSAGE_CLARITY = 8;
	public static final int STYLE = 9;
	public static final int WHOLEPAPER = 10;
	public static final int END = 8;
	
	public static String getPurposeName(int index) {
		if(index == RevisionPurpose.CLAIMS_IDEAS)
		{
			return "Claims/Ideas";
		} else if(index == RevisionPurpose.CD_WARRANT_REASONING_BACKING) {
			return "Warrant/Reasoning/Backing";
		} else if(index == RevisionPurpose.CD_REBUTTAL_RESERVATION) {
			return "Rebuttal/Reservation";
		} else if(index == RevisionPurpose.CD_GENERAL_CONTENT_DEVELOPMENT) {
			return "General Content Development";
		} else if(index == RevisionPurpose.EVIDENCE) {
			return "Evidence";
		} else if(index == RevisionPurpose.ORGANIZATION) {
			return "Organization";
		} else if(index == RevisionPurpose.CONVENTIONS_GRAMMAR_SPELLING) {
			return "Conventions/Grammar/Spelling";
		} else if(index == RevisionPurpose.WORDUSAGE_CLARITY) {
			return "Word-Usage/Clarity";
		} else if(index == RevisionPurpose.STYLE) {
			return "Style";
		} else if(index == RevisionPurpose.WHOLEPAPER) {
			return "Whole paper";
		}
		return "Dummy";
	}
	
	public static int getPurposeIndex(String name) {
		name = name.toLowerCase();
		for(int i = CLAIMS_IDEAS;i<=STYLE;i++) {
			if(getPurposeName(i).toLowerCase().equals(name)) {
				return i;
			}
		}
		return -1;
	}
}
