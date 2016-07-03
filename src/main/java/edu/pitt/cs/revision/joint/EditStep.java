package edu.pitt.cs.revision.joint;

import edu.pitt.lrdc.cs.revision.model.RevisionPurpose;

public class EditStep {
	public static int EDIT_MOVE = 1;
	public static int EDIT_KEEP = 2;
	
	private int d1Move;
	private int d2Move;
	private int currentD1; //current sentence index in D1
	private int currentD2; //current sentence index in D2	
	
	public String getStr(int move) {
		if(move == EDIT_MOVE) return "M";
		else  return "K";
	}
	
	public EditStep copy() {
		EditStep copy = new EditStep();
		copy.setD1Move(this.getD1Move());
		copy.setD2Move(this.getD2Move());
		copy.setCurrentD1(this.getCurrentD1());
		copy.setCurrentD2(this.getCurrentD2());
		copy.setType(this.getType());
		return copy;
	}
	
	public String toString() {
		String str = "";
		str += getStr(d1Move) + ":" + currentD1;
		str += "," + getStr(d2Move) + ":" + currentD2;
		str += "-" + RevisionPurpose.getPurposeName(type);
		return str;
	}
	
	public String toID() {
		String str = "";
		//str += getStr(d1Move) + ":" + currentD1;
		//str += "_" + getStr(d2Move) + ":" + currentD2;
		str = currentD1 + "-"+currentD2;
		return str;
	}
	
	public String toLabel() {
	   String label = getStr(d1Move) + "-" + getStr(d2Move) + "-" + type;
	   return label;
	}
 	
	private int type = RevisionPurpose.NOCHANGE;//types

	public int getD1Move() {
		return d1Move;
	}

	public void setD1Move(int d1Move) {
		this.d1Move = d1Move;
	}

	public int getD2Move() {
		return d2Move;
	}

	public void setD2Move(int d2Move) {
		this.d2Move = d2Move;
	}

	public int getCurrentD1() {
		return currentD1;
	}

	public void setCurrentD1(int currentD1) {
		this.currentD1 = currentD1;
	}

	public int getCurrentD2() {
		return currentD2;
	}

	public void setCurrentD2(int currentD2) {
		this.currentD2 = currentD2;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
}
