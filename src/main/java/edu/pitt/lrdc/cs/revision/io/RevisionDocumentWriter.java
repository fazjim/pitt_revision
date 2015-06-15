package edu.pitt.lrdc.cs.revision.io;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import edu.pitt.lrdc.cs.revision.model.RevisionDocument;
import edu.pitt.lrdc.cs.revision.model.RevisionOp;
import edu.pitt.lrdc.cs.revision.model.RevisionPurpose;
import edu.pitt.lrdc.cs.revision.model.RevisionUnit;

public class RevisionDocumentWriter {
	public static void writeToDoc(RevisionDocument rd, String path)
			throws Exception {
		FileOutputStream fileOut = new FileOutputStream(path);
		int level = rd.getRoot().getRevision_level();
		ArrayList<String> cols = new ArrayList<String>();
		cols.add("Sentence Index");
		cols.add("Sentence Content");
		cols.add("Aligned Index");
		cols.add("Identical?");
		cols.add("Original Paragraph No");
		cols.add("New Paragraph No");

		int start = 6;// The start index of the revisions

		for (int i = 0; i < level; i++) {
			cols.add("Revision Purpose Level " + i);
			cols.add("Revision Operation Level " + i);
			cols.add("Revision Index Level " + i);
		}

		XSSFWorkbook xwb = new XSSFWorkbook();
		XSSFSheet sheet0 = xwb.createSheet("Old Draft");
		XSSFSheet sheet1 = xwb.createSheet("New Draft");

		// Set up headers
		XSSFRow header0 = sheet0.createRow(0);
		XSSFRow header1 = sheet1.createRow(0);

		for (int i = 0; i < cols.size(); i++) {
			header0.createCell(i).setCellValue(cols.get(i));
			header1.createCell(i).setCellValue(cols.get(i));
		}

		// Set up sentence contents and sentence index
		int oldSentenceNum = rd.getOldSentencesArray().length;
		for (int i = 1; i <= oldSentenceNum; i++) {
			XSSFRow row0 = sheet0.createRow(i);
			row0.createCell(0).setCellValue(i);
			row0.createCell(1).setCellValue(rd.getOldSentence(i));
		}

		int newSentenceNum = rd.getNewSentencesArray().length;
		for (int i = 1; i <= newSentenceNum; i++) {
			XSSFRow row1 = sheet1.createRow(i);
			row1.createCell(0).setCellValue(i);
			row1.createCell(1).setCellValue(rd.getNewSentence(i));
		}

		// Set up the basic alignments
		for (int i = 1; i <= oldSentenceNum; i++) {
			ArrayList<Integer> newIndexes = rd.getNewFromOld(i);
			if (newIndexes != null && newIndexes.size() != 0) {
				if (newIndexes.size() == 1 && newIndexes.get(0) == -1) {
					sheet0.getRow(i).createCell(2).setCellValue("DELETE");
				} else {
					if (newIndexes.size() == 1
							&& newIndexes.get(0) != -1
							&& rd.getOldSentence(i).equals(
									rd.getNewSentence(newIndexes.get(0)))) {
						sheet0.getRow(i).createCell(3).setCellValue(1);
					} else {
						sheet0.getRow(i).createCell(3).setCellValue(0);
					}
					String indexStr = "";
					for (Integer newIndex : newIndexes) {
						indexStr += newIndex + ",";
					}
					if (indexStr.endsWith(","))
						indexStr = indexStr.substring(0, indexStr.length() - 1);
					sheet0.getRow(i).createCell(2).setCellValue(indexStr);
				}
			} else {
				sheet0.getRow(i).createCell(2).setCellValue("DELETE");
			}
			sheet0.getRow(i).createCell(4).setCellValue(rd.getParaNoOfOldSentence(i));
		}

		for (int i = 1; i <= newSentenceNum; i++) {
			ArrayList<Integer> oldIndexes = rd.getOldFromNew(i);
			if (oldIndexes != null && oldIndexes.size() != 0) {
				if (oldIndexes.size() == 1 && oldIndexes.get(0) == -1) {
					sheet1.getRow(i).createCell(2).setCellValue("ADD");
				} else {
					if (oldIndexes.size() == 1
							&& oldIndexes.get(0) != -1
							&& rd.getNewSentence(i).equals(
									rd.getOldSentence(oldIndexes.get(0)))) {
						sheet1.getRow(i).createCell(3).setCellValue(1);
					} else {
						sheet1.getRow(i).createCell(3).setCellValue(0);
					}
					String indexStr = "";
					for (Integer oldIndex : oldIndexes) {
						indexStr += oldIndex + ",";
					}
					if (indexStr.endsWith(","))
						indexStr = indexStr.substring(0, indexStr.length() - 1);

					sheet1.getRow(i).createCell(2).setCellValue(indexStr);
				}
			} else {
				sheet1.getRow(i).createCell(2).setCellValue("ADD");
			}
			sheet1.getRow(i).createCell(5).setCellValue(rd.getParaNoOfNewSentence(i));
		}

		/*ArrayList<RevisionUnit> basicRevisions = rd.getRoot()
				.getRevisionUnitAtLevel(0);
		for (RevisionUnit ru : basicRevisions) {
			ArrayList<Integer> oldSentenceIndex = ru.getOldSentenceIndex();
			ArrayList<Integer> newSentenceIndex = ru.getNewSentenceIndex();

			ArrayList<Integer> oldParagraphNo = ru.getOldParagraphNo();
			ArrayList<Integer> newParagraphNo = ru.getNewParagraphNo();

			String oldSentenceStr = "";
			if (oldSentenceIndex != null && oldSentenceIndex.size() != 0) {
				for (Integer oldIndex : oldSentenceIndex) {
					oldSentenceStr += oldIndex + ",";
				}
				if (oldSentenceStr.endsWith(","))
					oldSentenceStr = oldSentenceStr.substring(0,
							oldSentenceStr.length() - 1);
			} else {
				oldSentenceStr = "ADD";
			}

			String oldParaStr = "";
			if (oldParagraphNo != null && oldParagraphNo.size() != 0) {
				for (Integer oldIndex : oldParagraphNo) {
					oldParaStr += oldIndex + ",";
				}
				if (oldParaStr.endsWith(","))
					oldParaStr = oldParaStr.substring(0,
							oldParaStr.length() - 1);
			} else {
				// oldParaStr = "ADD";
			}

			String newSentenceStr = "";
			if (newSentenceIndex != null && newSentenceIndex.size() != 0) {
				for (Integer newIndex : newSentenceIndex) {
					newSentenceStr += newIndex + ",";
				}
				if (newSentenceStr.endsWith(","))
					newSentenceStr = newSentenceStr.substring(0,
							newSentenceStr.length() - 1);
			} else {
				newSentenceStr = "DELETE";
			}

			String newParaStr = "";
			if (newParagraphNo != null && newParagraphNo.size() != 0) {
				for (Integer newIndex : newParagraphNo) {
					newParaStr += newIndex + ",";
				}
				if (newParaStr.endsWith(","))
					newParaStr = newParaStr.substring(0,
							newParaStr.length() - 1);
			} else {
				// newParaStr = "DELETE";
			}

			if (oldSentenceIndex != null && oldSentenceIndex.size() != 0) {
				for (Integer oldIndex : oldSentenceIndex) {
					if (oldIndex != -1) {
						System.out.println("+++++++++++++++++++++++");
						System.out.println(oldIndex);
						System.out.println(RevisionOp.getOpName(ru.getRevision_op()));
						System.out.println(ru.getNewSentenceIndex());
						System.out.println(ru.getOldSentence());
						System.out.println(ru.getNewSentence());
						sheet0.getRow(oldIndex).createCell(2)
								.setCellValue(newSentenceStr);
						sheet0.getRow(oldIndex).createCell(4)
								.setCellValue(oldParaStr);
						sheet0.getRow(oldIndex).createCell(5)
								.setCellValue(newParaStr);
					}
				}
			}

			if (newSentenceIndex != null && newSentenceIndex.size() != 0) {
				for (Integer newIndex : newSentenceIndex) {
					if (newIndex != -1) {
						sheet1.getRow(newIndex).createCell(2)
								.setCellValue(oldSentenceStr);
						sheet1.getRow(newIndex).createCell(4)
								.setCellValue(oldParaStr);
						sheet1.getRow(newIndex).createCell(5)
								.setCellValue(newParaStr);
					}
				}
			}
		}*/

		// Set up the annotations
		for (int i = 0; i < level; i++) {
			ArrayList<RevisionUnit> rus = rd.getRoot()
					.getRevisionUnitAtLevel(i);
			Hashtable<Integer, HashSet<Integer>> oldRevLocations = new Hashtable<Integer, HashSet<Integer>>();
			Hashtable<Integer, HashSet<Integer>> newRevLocations = new Hashtable<Integer, HashSet<Integer>>();
			Hashtable<Integer, Integer> revPurposeIndex = new Hashtable<Integer, Integer>();
			Hashtable<Integer, Integer> revOpIndex = new Hashtable<Integer, Integer>();
			System.out.println("____");
			for (RevisionUnit ru : rus) {
//				System.out.println("LEVEL:" + i + ",INDEX:"
//						+ ru.getRevision_index());
				ArrayList<Integer> oldIndexes = new ArrayList<Integer>();
				ArrayList<Integer> newIndexes = new ArrayList<Integer>();

				ArrayList<RevisionUnit> baseUnitsChilds = ru
						.getRevisionUnitAtLevel(0);

				int revisionOp = ru.getRevision_op();
				int revisionPurpose = ru.getRevision_purpose();
				int revIndex = ru.getRevision_index();
				revPurposeIndex.put(revIndex, revisionPurpose);
				revOpIndex.put(revIndex, revisionOp);

				for (RevisionUnit childRU : baseUnitsChilds) {
					oldIndexes = childRU.getOldSentenceIndex();
					newIndexes = childRU.getNewSentenceIndex();
					if (oldIndexes != null && oldIndexes.size() != 0) {
						for (Integer oldIndex : oldIndexes) {
							if (oldIndex != -1) {
								if (oldRevLocations.containsKey(oldIndex)) {
									oldRevLocations.get(oldIndex).add(
											ru.getRevision_index());
								} else {
									HashSet<Integer> set = new HashSet<Integer>();
									set.add(ru.getRevision_index());
									oldRevLocations.put(oldIndex, set);
								}
							}
						}
					}
					if (newIndexes != null && newIndexes.size() != 0) {
						for (Integer newIndex : newIndexes) {
							if (newIndex != -1) {
								if (newRevLocations.containsKey(newIndex)) {
									newRevLocations.get(newIndex).add(
											ru.getRevision_index());
								} else {
									HashSet<Integer> set = new HashSet<Integer>();
									set.add(ru.getRevision_index());
									newRevLocations.put(newIndex, set);
								}
							}
						}
					}
				}
			}
			Iterator<Integer> it = oldRevLocations.keySet().iterator();
			while (it.hasNext()) {
				int location = it.next();
				String revIndexStr = "";
				String revOpStr = "";
				String revPurposeStr = "";
				HashSet<Integer> indexes = oldRevLocations.get(location);
				for (Integer index : indexes) {
					revIndexStr += index + ",";
					revOpStr += RevisionOp.getOpName(revOpIndex.get(index))
							+ ",";
					revPurposeStr += RevisionPurpose
							.getPurposeName(revPurposeIndex.get(index)) + ",";
				}

				if (revIndexStr.endsWith(","))
					revIndexStr = revIndexStr.substring(0,
							revIndexStr.length() - 1);
				if (revOpStr.endsWith(","))
					revOpStr = revOpStr.substring(0, revOpStr.length() - 1);
				if (revPurposeStr.endsWith(","))
					revPurposeStr = revPurposeStr.substring(0,
							revPurposeStr.length() - 1);
				System.out.println(start + i * 3);
				sheet0.getRow(location).createCell(start + i * 3)
						.setCellValue(revPurposeStr);
				sheet0.getRow(location).createCell(start + i * 3 + 1)
						.setCellValue(revOpStr);
				sheet0.getRow(location).createCell(start + i * 3 + 2)
						.setCellValue(revIndexStr);
			}

			Iterator<Integer> newLocIt = newRevLocations.keySet().iterator();
			while (newLocIt.hasNext()) {
				int newLoc = newLocIt.next();
				String revIndexStr = "";
				String revOpStr = "";
				String revPurposeStr = "";
				HashSet<Integer> indexes = newRevLocations.get(newLoc);
				for (Integer index : indexes) {
					revIndexStr += index + ",";
					revOpStr += RevisionOp.getOpName(revOpIndex.get(index))
							+ ",";
					revPurposeStr += RevisionPurpose
							.getPurposeName(revPurposeIndex.get(index)) + ",";
				}
				if (revIndexStr.endsWith(","))
					revIndexStr = revIndexStr.substring(0,
							revIndexStr.length() - 1);
				if (revOpStr.endsWith(","))
					revOpStr = revOpStr.substring(0, revOpStr.length() - 1);
				if (revPurposeStr.endsWith(","))
					revPurposeStr = revPurposeStr.substring(0,
							revPurposeStr.length() - 1);
				System.out.println("NEWLOC:"+newLoc);
				sheet1.getRow(newLoc).createCell(start + i * 3)
						.setCellValue(revPurposeStr);
				sheet1.getRow(newLoc).createCell(start + i * 3 + 1)
						.setCellValue(revOpStr);
				sheet1.getRow(newLoc).createCell(start + i * 3 + 2)
						.setCellValue(revIndexStr);
			}
		}
		xwb.write(fileOut);
		fileOut.flush();
		fileOut.close();
	}
}
