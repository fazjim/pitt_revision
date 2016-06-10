package edu.pitt.cs.revision.purpose;

public class PDTBRelation {
	int preIndex;
	int postIndex;
	String sense;
	String elementType;
	boolean isParallel;

	public String toString() {
		return preIndex+":"+postIndex+", Type:"+elementType+", Sense:"+sense;
	}
	public String getElementType() {
		return elementType;
	}

	public void setElementType(String elementType) {
		this.elementType = elementType;
	}

	public int getPreIndex() {
		return preIndex;
	}

	public void setPreIndex(int preIndex) {
		this.preIndex = preIndex;
	}

	public int getPostIndex() {
		return postIndex;
	}

	public void setPostIndex(int postIndex) {
		this.postIndex = postIndex;
	}

	public String getSense() {
		return sense;
	}

	public void setSense(String sense) {
		this.sense = sense;
	}

	public boolean isParallel() {
		return isParallel;
	}

	public void setParallel(boolean isParallel) {
		this.isParallel = isParallel;
	}
}
