package edu.pitt.cs.revision.purpose.pdtb;

import java.util.StringTokenizer;

/**
 * Basic data structure of each line of results
 * @author zhangfan
 *
 */
public class PipeUnit {
	private String[] attrs = new String[PipeAttribute.NUM_ATTRs]; 
	
	/**
	 * Constructor, pass in the line to construct all the attributes
	 * @param line
	 */
	public PipeUnit(String line) {
		/*StringTokenizer stk = new StringTokenizer(line, "|");
		int index = 0;
		while(stk.hasMoreTokens()&&index<PipeAttribute.NUM_ATTRs) {
			attrs[index] = stk.nextToken();
			index++;
		}*/
		line = line.replaceAll("\\|", " \\|");
		StringTokenizer stk = new StringTokenizer(line, "|");
		int index = 0;
		while(stk.hasMoreTokens()&&index<PipeAttribute.NUM_ATTRs) {
			attrs[index] = stk.nextToken().trim();
			index++;
		}
	}
	
	/**
	 * get attribute
	 * @param attr  The index of the attribute PipeAttribute.PROPNAME 
	 * @return
	 */
	public String getAttr(int attr) {
		return attrs[attr];
	}
	
	/*
	private String RelationType;// (Explicit/Implicit/AltLex/EntRel/NoRel)
	private int sectionNumber; // (0-24)
	private int fileNumber; //(0-99)
	private String connectiveAltLexSpanList;// (only for Explicit and AltLex)
	private String connectiveAltLexGornAddressList;// (only for Explicit and AltLex)
	private String connectiveAltLexRawText;// (only for Explicit and AltLex)
	private String stringPosition;// (only for Implicit, EntRel and NoRel)
	private int sentenceNumber; // (only for Implicit, EntRel and NoRel)
	private String connHead;// (only for Explicit)
	private String conn1;// (only for Implicit)
	private String conn2;// (only for Implicit)
	private String semClass1; // 1st Semantic Class corresponding to ConnHead, Conn1 or AltLex span (only for Explicit, Implicit and AltLex)
	private String semClass2;// 2nd Semantic Class corresponding to ConnHead, Conn1 or AltLex span (only for Explicit, Implicit and AltLex)
	private String semClass1Conn2; // 1st Semantic Class corresponding to Conn2 (only for Implicit)
	private String semClass2Conn2; // 2nd Semantic Class corresponding to Conn2 (only for Implicit)
	private String relationLevelAttributionSource; //Relation-level attribution: Source (only for Explicit, Implicit and AltLex)
	private String relationLevelAttributionType; //Relation-level attribution: Type (only for Explicit, Implicit and AltLex)
	private String relationLevelAttributionPolarity; // Relation-level attribution: Polarity (only for Explicit, Implicit and AltLex)
	private String relationLevelAttributionDetermincy;//Relation-level attribution: Determinacy (only for Explicit, Implicit and AltLex)
	private String relationLevelAttributionSpanList; // Relation-level attribution: SpanList (only for Explicit, Implicit and AltLex)
	private String relationLevelAttributionGornAddressList; // Relation-level attribution: GornAddressList (only for Explicit, Implicit and AltLex)
	private String realtionLevelAttributionRawText; //Relation-level attribution: RawText (only for Explicit, Implicit and AltLex)
	private String arg1SpanList; // arg1 SpanList
	private String arg1GornAddress; // Arg1 GornAddress
	private String arg1RawText; // Arg1 RawText
	private String arg1AttributionSource; // Arg1 attribution: Source (only for Explicit, Implicit and AltLex)
	private String arg1AttributionType; // Arg1 attribution: Source (only for Explicit, Implicit and AltLex)
	private String Arg1 attribution: Polarity (only for Explicit, Implicit and AltLex)
	Col 28: Arg1 attribution: Determinacy (only for Explicit, Implicit and AltLex)
	Col 29: Arg1 attribution: SpanList (only for Explicit, Implicit and AltLex)
	Col 30: Arg1 attribution: GornAddressList (only for Explicit, Implicit and AltLex)
	Col 31: Arg1 attribution: RawText (only for Explicit, Implicit and AltLex)
	Col 32: Arg2 SpanList
	Col 33: Arg2 GornAddress
	Col 34: Arg2 RawText
	Col 35: Arg2 attribution: Source (only for Explicit, Implicit and AltLex)
	Col 36: Arg2 attribution: Type (only for Explicit, Implicit and AltLex)
	Col 37: Arg2 attribution: Polarity (only for Explicit, Implicit and AltLex)
	Col 38: Arg2 attribution: Determinacy (only for Explicit, Implicit and AltLex)
	Col 39: Arg2 attribution: SpanList (only for Explicit, Implicit and AltLex)
	Col 40: Arg2 attribution: GornAddressList (only for Explicit, Implicit and AltLex)
	Col 41: Arg2 attribution: RawText (only for Explicit, Implicit and AltLex)
	Col 42: Sup1 SpanList (only for Explicit, Implicit and AltLex)
	Col 43: Sup1 GornAddress (only for Explicit, Implicit and AltLex)
	Col 44: Sup1 RawText (only for Explicit, Implicit and AltLex)
	Col 45: Sup2 SpanList (only for Explicit, Implicit and AltLex)
	Col 46: Sup2 GornAddress (only for Explicit, Implicit and AltLex)
	Col 47: Sup2 RawText (only for Explicit, Implicit and AltLex)*/
}
