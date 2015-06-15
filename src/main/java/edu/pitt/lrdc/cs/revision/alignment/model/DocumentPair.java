package edu.pitt.lrdc.cs.revision.alignment.model;

/**
 * This represents a paired draft
 * @author zhangfan
 *
 */
public class DocumentPair {
	String fileName = "";
	
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	Document src = new Document();
	Document modified = new Document();
	public Document getSrc() {
		return src;
	}
	public void setSrc(Document src) {
		this.src = src;
	}
	public Document getModified() {
		return modified;
	}
	public void setModified(Document modified) {
		this.modified = modified;
	}
}
