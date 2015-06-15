package edu.pitt.lrdc.cs.revision.test;

/**
 * @deprecated
 */
import edu.pitt.lrdc.cs.revision.model.RevisionOp;
import edu.pitt.lrdc.cs.revision.model.RevisionPurpose;
import edu.pitt.lrdc.cs.revision.model.RevisionUnit;
public class RevisionExample {
	
}
/*
public class RevisionExample {
	public static void main(String[] args) throws Exception{
		RevisionUnit root = new RevisionUnit(true);
		RevisionUnit ru1 = new RevisionUnit(1, 1, RevisionOp.MODIFY, RevisionPurpose.CONVENTIONS_GRAMMAR_SPELLING,root);
		RevisionUnit ru2 = new RevisionUnit(1, 2, RevisionOp.MODIFY, RevisionPurpose.CONVENTIONS_GRAMMAR_SPELLING,root);
		RevisionUnit ru3 = new RevisionUnit(-1, 3, RevisionOp.ADD, RevisionPurpose.CD_GENERAL_CONTENT_DEVELOPMENT,root);
		RevisionUnit ru4 = new RevisionUnit(3, 4, RevisionOp.MODIFY, RevisionPurpose.CD_GENERAL_CONTENT_DEVELOPMENT,root);
		RevisionUnit ru5 = new RevisionUnit(-1, 10, RevisionOp.MODIFY, RevisionPurpose.CLAIMS_IDEAS,root);
		RevisionUnit ru6 = new RevisionUnit(-1, 14, RevisionOp.ADD, RevisionPurpose.EVIDENCE,root);
		RevisionUnit ru7 = new RevisionUnit(-1, 15, RevisionOp.ADD, RevisionPurpose.EVIDENCE,root);
		RevisionUnit ru8 = new RevisionUnit(-1, 16, RevisionOp.ADD, RevisionPurpose.CLAIMS_IDEAS,root);
		RevisionUnit ru9 = new RevisionUnit(-1, 17, RevisionOp.ADD, RevisionPurpose.EVIDENCE,root);
		RevisionUnit ru10 = new RevisionUnit(-1, 18, RevisionOp.ADD, RevisionPurpose.EVIDENCE,root);
		RevisionUnit ru11 = new RevisionUnit(-1, 23, RevisionOp.ADD, RevisionPurpose.CD_WARRANT_REASONING_BACKING,root);
		RevisionUnit ru12 = new RevisionUnit(15, 24, RevisionOp.MODIFY, RevisionPurpose.CONVENTIONS_GRAMMAR_SPELLING,root);
		RevisionUnit ru13 = new RevisionUnit(17, 25, RevisionOp.MODIFY, RevisionPurpose.WORDUSAGE_CLARITY,root);
		RevisionUnit ru14 = new RevisionUnit(-1, 27, RevisionOp.ADD, RevisionPurpose.CD_WARRANT_REASONING_BACKING,root);
		
		
		RevisionUnit ru2_1 = new RevisionUnit(-1,-1,RevisionOp.MODIFY,RevisionPurpose.CD_GENERAL_CONTENT_DEVELOPMENT,root);
		ru2_1.addUnit(ru1);
		ru2_1.addUnit(ru2);
		ru2_1.addUnit(ru3);
		ru2_1.addUnit(ru4);
		
		RevisionUnit ru2_2 = new RevisionUnit(-1,-1,RevisionOp.ADD,RevisionPurpose.EVIDENCE,root);
		ru2_2.addUnit(ru6);
		ru2_2.addUnit(ru7);
		
		RevisionUnit ru2_4 = new RevisionUnit(-1,-1,RevisionOp.ADD,RevisionPurpose.EVIDENCE,root);
		ru2_4.addUnit(ru9);
		ru2_4.addUnit(ru10);
		
		RevisionUnit ru2_5 = new RevisionUnit(-1,-1, RevisionOp.NOCHANGE, RevisionPurpose.CLAIMS_IDEAS,root);
		ru2_5.addUnit(ru11);
		
		RevisionUnit ru2_6 = new RevisionUnit(-1,-1, RevisionOp.NOCHANGE,RevisionPurpose.CD_WARRANT_REASONING_BACKING,root);
		ru2_6.addUnit(ru13);
		ru2_6.addUnit(ru14);
		
		RevisionUnit ru3_1 = new RevisionUnit(-1,-1, RevisionOp.MODIFY,RevisionPurpose.CLAIMS_IDEAS,root);
		ru3_1.addUnit(ru5);
		ru3_1.addUnit(ru2_2);
		
		RevisionUnit ru3_2 = new RevisionUnit(-1,-1,RevisionOp.ADD, RevisionPurpose.CLAIMS_IDEAS,root);
		ru3_2.addUnit(ru8);
		ru3_2.addUnit(ru2_4);
		
		RevisionUnit ru3_3 = new RevisionUnit(-1,-1,RevisionOp.NOCHANGE,RevisionPurpose.CLAIMS_IDEAS,root);
		ru3_3.addUnit(ru2_5);
		ru3_3.addUnit(ru2_6);
		
		
		root.addUnit(ru2_1);
		root.addUnit(ru3_1);
		root.addUnit(ru3_2);
		root.addUnit(ru3_3);
		
		System.out.println(root);
	}
}*/
