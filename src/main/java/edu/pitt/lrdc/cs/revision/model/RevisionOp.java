package edu.pitt.lrdc.cs.revision.model;

public class RevisionOp {
	public static final int START = 1;
	public static final int ADD = 1;
	public static final int DELETE = 2;
	public static final int MODIFY = 3;
	public static final int NOCHANGE = 4;
	public static final int END = MODIFY;
	
	public static String getOpName(int index) {
		if(index == RevisionOp.ADD)
		{
			return "Add";
		} else if(index == RevisionOp.DELETE) {
			return "Delete";
		} else if(index == RevisionOp.MODIFY) {
			return "Modify";
		} else if(index == RevisionOp.NOCHANGE) {
			return "Nochange";
		}
		return "Dummy";
	}
	
	public static int getOpIndex(String name) {
		name = name.toLowerCase();
		for(int i = ADD;i<=NOCHANGE;i++) {
			if(getOpName(i).toLowerCase().equals(name)) {
				return i;
			}
		}
		return -1;
	}
}
