package edu.pitt.cs.revision.purpose.pdtb;

/**
 * PipeAttribute index
 * @author zhangfan
 *
 */
public class PipeAttribute {
	public static int NUM_ATTRs = 48;
	
	public static int RELATION_TYPE = 0; //Col 0: Relation type (Explicit/Implicit/AltLex/EntRel/NoRel)
	public static int SECTION_NUMBER = 1; //Col 1: Section number (0-24)
	public static int FILE_NUMBER = 2; // Col 2: File number (0-99)
	public static int CONN_SPANLIST = 3; //Col 3: Connective/AltLex SpanList (only for Explicit and AltLex)
	public static int CONN_GORNADDRESS = 4; //Col 4: Connective/AltLex GornAddressList (only for Explicit and AltLex)
	public static int CONN_RAWTEXT = 5; //Col 5: Connective/AltLex RawText (only for Explicit and AltLex)
	public static int STRING_POSITION = 6; //Col 6: String position (only for Implicit, EntRel and NoRel)
	public static int SENTENCE_NUMBER = 7; //Col 7: Sentence number (only for Implicit, EntRel and NoRel)
	public static int CONN_HEAD = 8; // Col 8: ConnHead (only for Explicit)
	public static int CONN1 = 9; //Col 9: Conn1 (only for Implicit)
	public static int CONN2 = 10; //Col 10: Conn2 (only for Implicit)
	public static int FIRST_SEMCLASS_CONN = 11; //Col 11: 1st Semantic Class corresponding to ConnHead, Conn1 or AltLex span (only for Explicit, Implicit and AltLex)
	public static int SECOND_SEMCLASS_CONN = 12; //Col 12: 2nd Semantic Class corresponding to ConnHead, Conn1 or AltLex span (only for Explicit, Implicit and AltLex)
	public static int FIRST_SEMCLASS_CONN2 = 13; //Col 13: 1st Semantic Class corresponding to Conn2 (only for Implicit)
	public static int SECOND_SEMCLASS_CONN2 = 14; //Col 14: 2nd Semantic Class corresponding to Conn2 (only for Implicit)
	public static int RELATION_ATTR_SRC = 15; //Col 15: Relation-level attribution: Source (only for Explicit, Implicit and AltLex)
	public static int RELATION_ATTR_TYPE = 16; //Col 16: Relation-level attribution: Type (only for Explicit, Implicit and AltLex)
	public static int RELATION_ATTR_POLARITY = 17; //Col 17: Relation-level attribution: Polarity (only for Explicit, Implicit and AltLex)
	public static int RELATION_ATTR_DETERMINACY = 18; //Col 18: Relation-level attribution: Determinacy (only for Explicit, Implicit and AltLex)
	public static int RELATION_ATTR_SPANLIST = 19; //Col 19: Relation-level attribution: SpanList (only for Explicit, Implicit and AltLex)
	public static int RELATION_ATTR_GORNADDRLIST = 20; //Col 20: Relation-level attribution: GornAddressList (only for Explicit, Implicit and AltLex)
	public static int RELATION_ATTR_RAWTEXT = 21;//Col 21: Relation-level attribution: RawText (only for Explicit, Implicit and AltLex)
	public static int ARG1_SPANLIST = 22; //Col 22: Arg1 SpanList
	public static int ARG1_GORNADDRESS = 23; //Col 23: Arg1 GornAddress
	public static int ARG1_RAWTEXT = 24; //Col 24: Arg1 RawText
	public static int ARG1_ATTR_SRC = 25; //Col 25: Arg1 attribution: Source (only for Explicit, Implicit and AltLex)
	public static int ARG1_ATTR_TYPE = 26; //Col 26: Arg1 attribution: Type (only for Explicit, Implicit and AltLex)
	public static int ARG1_ATTR_POLARITY = 27; //Col 27: Arg1 attribution: Polarity (only for Explicit, Implicit and AltLex)
	public static int ARG1_ATTR_DETERMINACY = 28; //Col 28: Arg1 attribution: Determinacy (only for Explicit, Implicit and AltLex)
	public static int ARG1_ATTR_SPANLIST = 29; //Col 29: Arg1 attribution: SpanList (only for Explicit, Implicit and AltLex)
	public static int ARG1_ATTR_GORNADDRESSLIST = 30; //Col 30: Arg1 attribution: GornAddressList (only for Explicit, Implicit and AltLex)
	public static int ARG1_ATTR_RAWTEXT = 31; //Col 31: Arg1 attribution: RawText (only for Explicit, Implicit and AltLex)
	public static int ARG2_SPANLIST = 32; //Col 32: Arg2 SpanList
	public static int ARG2_GORNADDRESS = 33; //Col 33: Arg2 GornAddress
	public static int ARG2_RAWTEXT = 34; //Col 34: Arg2 RawText
	public static int ARG2_ATTR_SRC = 35; //Col 35: Arg2 attribution: Source (only for Explicit, Implicit and AltLex)
	public static int ARG2_ATTR_TYPE = 36; //Col 36: Arg2 attribution: Type (only for Explicit, Implicit and AltLex)
	public static int ARG2_ATTR_POLARITY = 37; //Col 37: Arg2 attribution: Polarity (only for Explicit, Implicit and AltLex)
	public static int ARG2_ATTR_DETERMINACY = 38; //Col 38: Arg2 attribution: Determinacy (only for Explicit, Implicit and AltLex)
	public static int ARG2_ATTR_SPANLIST = 39; //Col 39: Arg2 attribution: SpanList (only for Explicit, Implicit and AltLex)
	public static int ARG2_ATTR_GORNADDRESSLIST = 40; //Col 40: Arg2 attribution: GornAddressList (only for Explicit, Implicit and AltLex)
	public static int ARG2_ATTR_RAWTEXT = 41; //Col 41: Arg2 attribution: RawText (only for Explicit, Implicit and AltLex)
	public static int SUP1_SPANLIST = 42; //Col 42: Sup1 SpanList (only for Explicit, Implicit and AltLex)
	public static int SUP1_GORNADDRESS = 43; //Col 43: Sup1 GornAddress (only for Explicit, Implicit and AltLex)
	public static int SUP1_RAWTEXT = 44; //Col 44: Sup1 RawText (only for Explicit, Implicit and AltLex)
	public static int SUP2_SPANLIST = 45; //Col 45: Sup2 SpanList (only for Explicit, Implicit and AltLex)
	public static int SUP2_GORNADDRESS = 46; //Col 46: Sup2 GornAddress (only for Explicit, Implicit and AltLex)
	public static int SUP2_RAWTEXT = 47; //Col 47: Sup2 RawText (only for Explicit, Implicit and AltLex)
}
