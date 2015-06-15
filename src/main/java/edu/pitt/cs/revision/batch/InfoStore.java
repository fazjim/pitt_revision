package edu.pitt.cs.revision.batch;

import java.util.Hashtable;
public class InfoStore {
	private Hashtable<String, Hashtable<Integer,SentenceInfo>> oldDraft;
	private Hashtable<String, Hashtable<Integer,SentenceInfo>> newDraft;
	
	public InfoStore() {
		oldDraft = new Hashtable<String, Hashtable<Integer,SentenceInfo>>();
		newDraft = new Hashtable<String, Hashtable<Integer, SentenceInfo>>();
	}

	public Hashtable<String, Hashtable<Integer, SentenceInfo>> getOldDraft() {
		return oldDraft;
	}

	public void setOldDraft(
			Hashtable<String, Hashtable<Integer, SentenceInfo>> oldDraft) {
		this.oldDraft = oldDraft;
	}

	public Hashtable<String, Hashtable<Integer, SentenceInfo>> getNewDraft() {
		return newDraft;
	}

	public void setNewDraft(
			Hashtable<String, Hashtable<Integer, SentenceInfo>> newDraft) {
		this.newDraft = newDraft;
	}
	
	
	
}
