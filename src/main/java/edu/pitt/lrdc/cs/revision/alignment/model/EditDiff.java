package edu.pitt.lrdc.cs.revision.alignment.model;

import java.util.ArrayList;
import java.util.Collections;

class EditDiffUnit implements Comparable {
	String origNo;
	String lineNo;
	String sent;
	String diff;

	public EditDiffUnit() {

	}

	public EditDiffUnit(String origNo, String lineNo, String sent, String diff) {
		super();
		this.origNo = origNo;
		this.lineNo = lineNo;
		this.sent = sent;
		this.diff = diff;
	}

	public String getOrigNo() {
		return origNo;
	}

	public void setOrigNo(String origNo) {
		this.origNo = origNo;
	}

	public String getLineNo() {
		return lineNo;
	}

	public void setLineNo(String lineNo) {
		this.lineNo = lineNo;
	}

	public String getSent() {
		return sent;
	}

	public void setSent(String sent) {
		this.sent = sent;
	}

	public String getDiff() {
		return diff;
	}

	public void setDiff(String diff) {
		this.diff = diff;
	}

	public String toString() {
		String str = "";
		str += "Orig Line:" + origNo + ", New Line:" + lineNo + "\n";
		str += "New Sentence: " + sent + "\n";
		str += "Diff: " + diff + "\n";
		return str;
	}

	@Override
	public int compareTo(Object o) {
		// TODO Auto-generated method stub
		EditDiffUnit dst = (EditDiffUnit) o;
		String dstOrigNo = dst.getOrigNo();
		int currNo = getLeadIndex(this.getOrigNo());
		int compareNo = getLeadIndex(dstOrigNo);
		return currNo - compareNo;
	}

	public int getLeadIndex(String index) {
		//System.out.println(index);
		String[] indexes = index.split(",");
		String leadIndex = indexes[0];
		int leadNo = Integer.parseInt(leadIndex);
		return leadNo;
	}
}

public class EditDiff {
	private String fileName = "";

	public EditDiff(String fileName) {
		this.fileName = fileName;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	private ArrayList<EditDiffUnit> diffUnits = new ArrayList<EditDiffUnit>();

	public void addUnit(String origNo, String lineNo, String sent, String diff) {
		EditDiffUnit edu = new EditDiffUnit(origNo, lineNo, sent, diff);
		diffUnits.add(edu);
	}

	public void arrange() {
		Collections.sort(diffUnits);
		ArrayList<EditDiffUnit> newDiffUnits = new ArrayList<EditDiffUnit>();
		int lastIndex = 0;
		EditDiffUnit mergeUnit = null;
		String lastOrig = "-1";
		for (int i = 1; i < diffUnits.size(); i++) {
			EditDiffUnit curr = diffUnits.get(i);
			if (!curr.getOrigNo().equals(lastOrig) || curr.getDiff().equals("ADD")) {
				if (mergeUnit == null) {
					newDiffUnits.add(diffUnits.get(lastIndex));
				} else {
					newDiffUnits.add(new EditDiffUnit(mergeUnit.origNo,
							mergeUnit.lineNo, mergeUnit.sent, mergeUnit.diff));
					mergeUnit = null;
				}
				lastOrig = diffUnits.get(lastIndex).getOrigNo();
				lastIndex++;
			} else {
				if (mergeUnit == null) {
					mergeUnit = new EditDiffUnit();
					mergeUnit.setOrigNo(curr.getOrigNo());
					mergeUnit.sent = diffUnits.get(lastIndex).sent;
					mergeUnit.lineNo = diffUnits.get(lastIndex).lineNo;
				}

				String lineNo = mergeUnit.getLineNo() + "," + curr.getLineNo();
				String sent = mergeUnit.sent + "\n" + curr.getLineNo();
				mergeUnit.diff = "split";
				mergeUnit.setLineNo(lineNo);
				mergeUnit.sent = sent;
				lastIndex++;
			}
		}

		Collections.sort(newDiffUnits);
		diffUnits = newDiffUnits;
	}

	public String toString() {
		String str = "==================FILE:" + fileName
				+ "==================\n";
		for (int i = 0; i < diffUnits.size(); i++) {
			EditDiffUnit edu = diffUnits.get(i);
			str += "**************************\n";
			str += edu.toString();
		}
		return str;
	}
}
