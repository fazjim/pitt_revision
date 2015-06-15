package edu.pitt.cs.revision.batch;

import java.io.File;
import java.io.IOException;
import java.util.Hashtable;

public class BatchFeatureReader {
	public static InfoStore readInfo(String path) throws IOException {
		InfoStore store = new InfoStore();
		File root = new File(path);
		File[] annFiles = root.listFiles();
		for (int i = 0; i < annFiles.length; i++) {
			File annFile = annFiles[i];
			String name = annFile.getName();
			if (!name.startsWith(".DS_Store")) { // For Mac
				Hashtable<Integer, SentenceInfo> oldDraft = new Hashtable<Integer, SentenceInfo>();
				Hashtable<Integer, SentenceInfo> newDraft = new Hashtable<Integer, SentenceInfo>();
				String posFolder = annFile.getAbsolutePath() + "/" + "POS";
				String errorFolder = annFile.getAbsolutePath() + "/" + "ERROR";
				readPOSInfo(posFolder, oldDraft, newDraft);
				//readErrInfo(errorFolder, oldDraft, newDraft);
				store.getOldDraft().put(name, oldDraft);
				store.getNewDraft().put(name, newDraft);
			}
		}
		return store;
	}

	public static void readPOSInfo(String path,
			Hashtable<Integer, SentenceInfo> oldDraft,
			Hashtable<Integer, SentenceInfo> newDraft) throws IOException {
		String oldFolder = path + "/" + "OLD";
		String newFolder = path + "/" + "NEW";
		File oldFolderFile = new File(oldFolder);
		File newFolderFile = new File(newFolder);

		// System.out.println(oldFolder);
		File[] olds = oldFolderFile.listFiles();
		// System.out.println(oldFolderFile);
		for (File file : olds) {
			if (file.getName().startsWith(".DS_Store"))
				continue;
			int index = Integer.parseInt(file.getName().substring(0,
					file.getName().indexOf(".txt")));
			if (!oldDraft.containsKey(index)) {
				SentenceInfo si = new SentenceInfo();
				oldDraft.put(index, si);
			}
			SentenceInfo si = oldDraft.get(index);
			POSTagInfo posInfo = new POSTagInfo();
			posInfo.fromFile(file.getAbsolutePath());
			si.setPosInfo(posInfo);
		}

		File[] news = newFolderFile.listFiles();
		for (File file : news) {
			if (file.getName().startsWith(".DS_Store"))
				continue;
			int index = Integer.parseInt(file.getName().substring(0,
					file.getName().indexOf(".txt")));
			if (!newDraft.containsKey(index)) {
				SentenceInfo si = new SentenceInfo();
				newDraft.put(index, si);
			}
			SentenceInfo si = newDraft.get(index);
			POSTagInfo posInfo = new POSTagInfo();
			posInfo.fromFile(file.getAbsolutePath());
			si.setPosInfo(posInfo);
		}
	}

	public static void readErrInfo(String path,
			Hashtable<Integer, SentenceInfo> oldDraft,
			Hashtable<Integer, SentenceInfo> newDraft) throws IOException {
		String oldFolder = path + "/" + "OLD";
		String newFolder = path + "/" + "NEW";
		File oldFolderFile = new File(oldFolder);
		File newFolderFile = new File(newFolder);

		File[] olds = oldFolderFile.listFiles();
		for (File file : olds) {
			int index = Integer.parseInt(file.getName().substring(0,
					file.getName().indexOf(".txt")));
			if (!oldDraft.containsKey(index)) {
				SentenceInfo si = new SentenceInfo();
				oldDraft.put(index, si);
			}
			SentenceInfo si = oldDraft.get(index);
			ErrorInfo errInfo = new ErrorInfo();
			errInfo.fromFile(file.getAbsolutePath());
			si.setErrInfo(errInfo);
		}

		File[] news = newFolderFile.listFiles();
		for (File file : news) {
			int index = Integer.parseInt(file.getName().substring(0,
					file.getName().indexOf(".txt")));
			if (!newDraft.containsKey(index)) {
				SentenceInfo si = new SentenceInfo();
				newDraft.put(index, si);
			}
			SentenceInfo si = newDraft.get(index);
			ErrorInfo errInfo = new ErrorInfo();
			errInfo.fromFile(file.getAbsolutePath());
			si.setErrInfo(errInfo);
		}
	}
}
