package edu.pitt.lrdc.cs.revision.io;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import edu.pitt.lrdc.cs.revision.agreement.KappaCalc;
import edu.pitt.lrdc.cs.revision.model.RevisionDocument;
import edu.pitt.lrdc.cs.revision.model.RevisionOp;
import edu.pitt.lrdc.cs.revision.model.RevisionPurpose;
import edu.pitt.lrdc.cs.revision.model.RevisionUnit;

/**
 * Reads from the file to generate the revision document
 * 
 * @author zhangfan
 * @version 1.0
 */
public class RevisionDocumentReader {
	private static boolean printVerbose = false;

	/**
	 * For finding the parent level
	 * 
	 * @param row
	 * @param index
	 * @param end
	 * @return
	 */
	public static int findNextLevel(XSSFRow row, int index, int currentLevel,
			int levels) {
		int nextLayer = index + 3;
		int nextLevel = currentLevel + 1;
		while (nextLevel < levels) {
			if (row.getCell(nextLayer + 2) != null)
				return nextLevel;
			nextLayer += 3;
			nextLevel += 1;
		}
		return nextLevel;
	}

	// Comment: changed the code, now first creates redundant unit row by row,
	// next merge unit column by column

	public static void addRowOfRevisions(XSSFRow row, int startIndex,
			int levels, ArrayList<RevisionUnit> allRevisions,
			RevisionUnit root, boolean isOld) {
		int currentLevel = 0;
		while (currentLevel < levels) {
			// System.out.println(startIndex);
			XSSFCell purposeCell = row.getCell(startIndex + currentLevel * 3);
			XSSFCell operationCell = row.getCell(startIndex + currentLevel * 3
					+ 1);
			XSSFCell indexCell = row.getCell(startIndex + currentLevel * 3 + 2);

			if (indexCell != null && !indexCell.toString().trim().equals("")) {
				String indexValStr = indexCell.toString();
				// System.out.println(indexValStr);
				// System.out.println(indexValStr.length());
				String[] indexes = indexValStr.split(",");
				for (int k = 0; k < indexes.length; k++) {
					RevisionUnit ru = new RevisionUnit(root);
					ru.setRevision_index((int) Double.parseDouble(indexes[k])); // set
																				// the
																				// revision
																				// index
					if (purposeCell != null
							&& !purposeCell.toString().trim().equals("")) { // set
																			// the
																			// purpose
						String purposeVal = purposeCell.getStringCellValue();
						String[] purposes = purposeVal.split(",");
						ru.setRevision_purpose(RevisionPurpose
								.getPurposeIndex(purposes[k]));
					}
					if (operationCell != null
							&& !operationCell.toString().trim().equals("")) { // set
																				// the
																				// op
						String operationVal = operationCell
								.getStringCellValue();
						String[] operations = operationVal.split(",");
						ru.setRevision_op(RevisionOp.getOpIndex(operations[k]));
					}
					if (currentLevel == 0) { // First level have the sentence
												// indexes
						// Set up the sentence indexes
						if (ru.getRevision_op() == RevisionOp.DELETE && isOld) {
							// Get in sheet 0
							int oldSentenceIndex = (int) row.getCell(0)
									.getNumericCellValue();
							ArrayList<Integer> olds = new ArrayList<Integer>();
							olds.add(oldSentenceIndex);
							ru.setOldSentenceIndex(olds);
							// ru.setNewSentenceIndex(-1);
						} else if (!isOld) {
							int newSentenceIndex = (int) row.getCell(0)
									.getNumericCellValue();
							ArrayList<Integer> newSentenceIndexes = new ArrayList<Integer>();
							newSentenceIndexes.add(newSentenceIndex);
							String oldSent = row.getCell(2).toString();
							ArrayList<Integer> oldSentenceIndex = new ArrayList<Integer>();
							String[] olds = oldSent.split(",");
							for (int i = 0; i < olds.length; i++) {
								try {
									oldSentenceIndex.add((int) Double
											.parseDouble(olds[i]));
								} catch (Exception exp) {
									// do nothing
								}
							}
							ru.setOldSentenceIndex(oldSentenceIndex);
							ru.setNewSentenceIndex(newSentenceIndexes);
						} else {
							// Old but not delete, has already been logged
						}
					}

					// build the revision unit
					ru.setRevision_level(currentLevel);
					int nextLevel = findNextLevel(row, startIndex
							+ currentLevel * 3, currentLevel, levels);
					if (nextLevel < levels) {
						XSSFCell nextIndex = row.getCell(startIndex + nextLevel
								* 3 + 2);
						String indexVal = nextIndex.toString();
						try {
							int groupIndex = (int) Double.parseDouble(indexVal);
							ru.setParent_index(groupIndex);
						} catch (Exception exp) {
							String[] parentIndexes = indexVal.split(",");
							if (printVerbose)
								System.out.println(indexVal);
							ru.setParent_index(Integer
									.parseInt(parentIndexes[k]));
						}
					}

					ru.setParent_level(nextLevel);
					allRevisions.add(ru);
					currentLevel = nextLevel; // Scan the next revision unit
												// in this row
				}
			} else {
				currentLevel = levels;
			}

		}
	}

	// Recursively build up the structure, not efficient, fix it later
	public static RevisionUnit buildHierarchicalUnit(
			ArrayList<RevisionUnit> allUnits, RevisionUnit root,
			RevisionUnit ultraRoot) {
		HashSet<String> units = new HashSet<String>();
		for (int i = 0; i < allUnits.size(); i++) {
			if (allUnits.get(i).getParent_index() == root.getRevision_index()
					&& allUnits.get(i).getParent_level() == root
							.getRevision_level()) {
				RevisionUnit temp = allUnits.get(i);
				int unitIndex = temp.getRevision_index();
				int unitLevel = temp.getRevision_level();
				String mark = unitLevel + "-" + unitIndex;
				int revPurpose = temp.getRevision_purpose();
				int revOp = temp.getRevision_op();
				if (revOp != -1) {
					if (unitLevel == 0) {
						root.addUnit(temp);
					} else {
						if (!units.contains(mark)) {
							RevisionUnit ru = new RevisionUnit(ultraRoot);
							ru.setRevision_index(unitIndex);
							ru.setRevision_level(unitLevel);
							ru.setRevision_op(revOp);
							ru.setRevision_purpose(revPurpose);
							units.add(mark);
							ru = buildHierarchicalUnit(allUnits, ru, ultraRoot);
							root.addUnit(ru);
						}
					}
				}
			}
		}
		return root;
	}

	static boolean useFix = false; // Temporarily for the current files, close
									// this when annotating on the other file
	static String fixFolderPath = "D:\\independent study\\Revision\\Braverman\\data_process";

	// static String fixFolderPath = "data_process";
	// This is a tricky one! @#$!#$@#!@#! Need a neat
	// solution!~(～o￣▽￣)～o
	// This method is very space inefficient, might review it later
	public static RevisionDocument readDoc(String filePath) throws Exception {
		System.out.println("Reading:" + filePath);
		RevisionDocument doc = new RevisionDocument();
		RevisionUnit rootUnit = new RevisionUnit(true);

		XSSFWorkbook xwb = new XSSFWorkbook(filePath);
		XSSFSheet sheet0 = xwb.getSheetAt(0); // original draft, contains the
												// delete operations
		XSSFSheet sheet1 = xwb.getSheetAt(1); // new draft, contain other
												// operations

		XSSFRow header0 = sheet0.getRow(0);
		XSSFRow header1 = sheet1.getRow(0);
		int r0Index = -1;
		int r1Index = -1;
		doc.setDocumentName(filePath);
		for (int i = 0; i < header0.getPhysicalNumberOfCells(); i++) {
			String cellName = header0.getCell(i).getStringCellValue();
			if (cellName.trim().equals("Revision Purpose")
					|| cellName.trim().equals("Revision Purpose Level 0"))
				r0Index = i;
		}
		for (int i = 0; i < header1.getPhysicalNumberOfCells(); i++) {
			String cellName = header1.getCell(i).getStringCellValue();
			if (cellName.trim().equals("Revision Purpose")
					|| cellName.trim().equals("Revision Purpose Level 0"))
				r1Index = i;
		}
		int sheet2End = header1.getLastCellNum();
		int levels = (sheet2End - r1Index) / 3;

		// Now starting to putting the sentence contents
		for (int i = 1; i < sheet0.getPhysicalNumberOfRows(); i++) {
			XSSFRow row = sheet0.getRow(i);
			String rowVal = row.getCell(1).getStringCellValue();
			doc.addOldSentence(rowVal);

			// Add paragraph information
			int cellIndex = 4;
			if (row.getCell(cellIndex) != null) {
				if (!row.getCell(cellIndex).toString().equals("")) {
					try {
						int paraNo = Integer.parseInt(row.getCell(cellIndex)
								.toString());
						doc.addOldSentenceParaMap(i, paraNo);
					} catch (Exception exp) {
						int paraNo = (int) Double.parseDouble(row.getCell(
								cellIndex).toString());
						doc.addOldSentenceParaMap(i, paraNo);
					}
				}
			}
		}
		for (int i = 1; i < sheet1.getPhysicalNumberOfRows(); i++) {
			XSSFRow row = sheet1.getRow(i);
			String rowVal = row.getCell(1).getStringCellValue();
			doc.addNewSentence(rowVal);

			// Add the mapping info
			int newSentenceIndex = (int) row.getCell(0).getNumericCellValue();
			if (row.getCell(2) != null) {
				String oldSent = row.getCell(2).toString();
			
				if(oldSent.contains(",")) {
					String[] oldSents = oldSent.split(",");
					for(String oldIndexSent: oldSents) {
						int oldSentenceIndex = -1;
						try {
							oldSentenceIndex = (int) Double.parseDouble(oldIndexSent);
						} catch (Exception exp) {
							// do nothing
						}
						if(oldSentenceIndex!=-1) {
						doc.addNewMappingIndex(newSentenceIndex, oldSentenceIndex);
						doc.addOldMappingIndex(oldSentenceIndex, newSentenceIndex);
						}
					}
				} else {
					int oldSentenceIndex = -1;
					try {
						oldSentenceIndex = (int) Double.parseDouble(oldSent);
					} catch (Exception exp) {
						// do nothing
					}
					if(oldSentenceIndex!=-1) {
					doc.addNewMappingIndex(newSentenceIndex, oldSentenceIndex);
					doc.addOldMappingIndex(oldSentenceIndex, newSentenceIndex);
					}
				}
				
			}
			// Add paragraph information
			int cellIndex = 5;
			if (row.getCell(cellIndex) != null) {
				if (!row.getCell(cellIndex).toString().equals("")) {
					try {
						int paraNo = Integer.parseInt(row.getCell(cellIndex)
								.toString());
						doc.addNewSentenceParaMap(i, paraNo);
					} catch (Exception exp) {
						int paraNo = (int) Double.parseDouble(row.getCell(
								cellIndex).toString());
						doc.addNewSentenceParaMap(i, paraNo);
					}
				}
			}
		}

		ArrayList<RevisionUnit> allUnits = new ArrayList<RevisionUnit>();
		// Putting in all the revisions
		// Starting with sheet0
		if (r0Index != -1) {
			for (int i = 1; i < sheet0.getPhysicalNumberOfRows(); i++) {
				XSSFRow row = sheet0.getRow(i);
				addRowOfRevisions(row, r0Index, levels, allUnits, rootUnit,
						true);
			}
		}
		// Then sheet1
		if (r1Index != -1) {
			for (int i = 1; i < sheet1.getPhysicalNumberOfRows(); i++) {
				XSSFRow row = sheet1.getRow(i);
				addRowOfRevisions(row, r1Index, levels, allUnits, rootUnit,
						false);
			}
		}
		// hyper node has a higher level
		rootUnit.setRevision_level(levels);
		rootUnit.setRevision_index(-1);
		if (printVerbose) {
			for (RevisionUnit ru : allUnits) {
				System.out.println(ru.getRevision_level() + "-"
						+ ru.getRevision_index() + ",Parent:"
						+ ru.getParent_level() + "-" + ru.getParent_index());
			}
		}
		allUnits = mergeUnit(allUnits);
		buildHierarchicalUnit(allUnits, rootUnit, rootUnit);
		doc.setRoot(rootUnit);
		
		/**Add a predicted root*/
		RevisionUnit predictedRoot = new RevisionUnit(true);
		predictedRoot.setRevision_level(3); // Default level to 3
		doc.setPredictedRoot(predictedRoot);

		/* Just use it for now */
		if (useFix)
			TemporaryAnnotationFixer.addParagraphInfo(fixFolderPath, filePath,
					doc);
		doc.setPromptContent(tmpPromptInfo);
		return doc;
	}

	public static boolean isIn(ArrayList<Integer> arr, int target) {
		for (Integer i : arr) {
			if (i == target)
				return true;
		}
		return false;
	}

	public static ArrayList<RevisionUnit> mergeUnit(
			ArrayList<RevisionUnit> units) {
		Hashtable<Integer, RevisionUnit> unitMerging = new Hashtable<Integer, RevisionUnit>();
		for (RevisionUnit ru : units) {
			if (!ru.isAbandoned() && ru.getRevision_level() == 0) {
				int index = ru.getRevision_index();
				if (unitMerging.containsKey(index)) {
					RevisionUnit existing = unitMerging.get(index);
					ArrayList<Integer> oldIndices = existing
							.getOldSentenceIndex();
					ArrayList<Integer> newIndices = existing
							.getNewSentenceIndex();

					ArrayList<Integer> oldTempIndices = ru
							.getOldSentenceIndex();
					ArrayList<Integer> newTempIndices = ru
							.getNewSentenceIndex();

					if (oldIndices == null) {
						oldIndices = new ArrayList<Integer>();
					}

					if (newIndices == null) {
						newIndices = new ArrayList<Integer>();
					}
					if (oldTempIndices != null) {
						for (Integer oldIndex : oldTempIndices) {
							if (!isIn(oldIndices, oldIndex)) {
								oldIndices.add(oldIndex);
							}
						}
					}
					if (newTempIndices != null) {
						for (Integer newIndex : newTempIndices) {
							if (!isIn(newIndices, newIndex)) {
								newIndices.add(newIndex);
							}
						}
					}

					existing.setOldSentenceIndex(oldIndices);
					existing.setNewSentenceIndex(newIndices);
				} else {
					unitMerging.put(index, ru);
				}
			}
		}
		ArrayList<RevisionUnit> rus = new ArrayList<RevisionUnit>();
		Iterator<Integer> it = unitMerging.keySet().iterator();
		while (it.hasNext()) {
			int key = it.next();
			rus.add(unitMerging.get(key));
		}
		return rus;
	}

	/**
	 * Read a list of the revision documents
	 * 
	 * @param folderPath
	 * @return list of revision documents
	 * @throws IOException
	 */
	public static ArrayList<RevisionDocument> readDocs(String folderPath)
			throws Exception {
		ArrayList<RevisionDocument> revDocs = new ArrayList<RevisionDocument>();
		File folder = new File(folderPath);
		File[] files = folder.listFiles();
		for (int i = 0; i < files.length; i++) {
			if(files[i].getName().endsWith(".xlsx"))
			revDocs.add(readDoc(files[i].getAbsolutePath()));
		}
		return revDocs;
	}

	static String tmpPromptInfo = "#1 Now that we are seven cantos and five levels into Hell; you should be able to correlate sinnerts and punishments that Dante feels appropriate.  Your task is to construct a well written, concise essay placing contemporaries into each level and specifically justify why each modern-day person appropriately fits...at least according to your thought process.  Be certain to cite evidence from the text as needed! \n #2 This is to be a final and complete writing of your first SWoRD writing.  The original assignment was:to construct a well written, concise essay placing contemporaries into each level and specifically justify why each modern-day person appropriately fits...at least according to your thought process.  Be certain to cite evidence from the text as needed! Now, based on the reviews you have received you must finalize this writing to the best of your ability and utilize the constructive comments! ";
	
	// Just to test
	public static void main(String[] args) throws Exception {
		RevisionDocument rd1 = RevisionDocumentReader
				.readDoc("C:\\Not Backed Up\\data\\Braverman_sentence_alignment/Braverman_sentence_alignment/toolV1.0/Chrisfolder/Annotation__elaine12 Christian.xlsx");
		// System.out.println(rd1.getRoot().toString());

		RevisionDocument rd2 = RevisionDocumentReader
				.readDoc("C:\\Not Backed Up\\data\\Braverman_sentence_alignment/Braverman_sentence_alignment/toolV1.0/Fanfolder/elaine.xlsx");
		KappaCalc kc = new KappaCalc();
		kc.getKappas(rd1, rd2);
		// System.out.println("Kappa:" + kc.getKappaAtLevel(rd1, rd2, 0, 1));
	}

	/*
	 * Just to place the code I have spent an hour and half here in case I might
	 * need to refer to it and make it look useful private void dummyFunc() {
	 * 
	 * // This adding method might not be the most space efficient, review it //
	 * later // Records units at different levels, each level use a hashtable to
	 * // avoid conflict ArrayList<Hashtable<Integer, RevisionUnit>>
	 * levelsOfUnits = new ArrayList<Hashtable<Integer, RevisionUnit>>();
	 * 
	 * // Sheet 0 only contains the delete units, only record the original //
	 * sentence index for (int i = 1; i < sheet0.getPhysicalNumberOfRows(); i++)
	 * { XSSFRow row = sheet0.getRow(i); if (row != null) { int oldSentenceIndex
	 * = (int) row.getCell(0) .getNumericCellValue(); int lastColIndex =
	 * row.getLastCellNum(); int levels = (lastColIndex - r0Index) / 3; // Once
	 * there is a new level, add a table for that level while
	 * (levelsOfUnits.size() < levels) { Hashtable<Integer, RevisionUnit>
	 * levelTable = new Hashtable<Integer, RevisionUnit>();
	 * levelsOfUnits.add(levelTable); }
	 * 
	 * // For each row, put the units in a temporary list first
	 * ArrayList<ArrayList<RevisionUnit>> tempList = new
	 * ArrayList<ArrayList<RevisionUnit>>();
	 * 
	 * for (int j = 0; j < levels; j++) { XSSFCell purposeCell =
	 * row.getCell(r0Index + j * 3); XSSFCell operationCell =
	 * row.getCell(r0Index + j * 3 + 1); XSSFCell indexCell =
	 * row.getCell(r0Index + j * 3 + 2);
	 * 
	 * ArrayList<RevisionUnit> units = new ArrayList<RevisionUnit>(); // a //
	 * sentence // could // be // annotated // as // two // or // more //
	 * revisions if (j == 0) { // Basic sentence level, don't have the index
	 * issue String purposeVal = purposeCell.getStringCellValue(); String[]
	 * purposes = purposeVal.split(","); String operationVal = operationCell
	 * .getStringCellValue(); String[] operations = operationVal.split(","); for
	 * (int k = 0; k < purposes.length; k++) { RevisionUnit tempUnit = new
	 * RevisionUnit(); tempUnit.setOldSentenceIndex(oldSentenceIndex);
	 * tempUnit.setRevision_purpose(RevisionPurpose
	 * .getPurposeIndex(purposes[k])); tempUnit.setRevision_op(RevisionOp
	 * .getOpIndex(operations[k])); units.add(tempUnit); }
	 * 
	 * } else { if (purposeCell == null ||
	 * purposeCell.getStringCellValue().trim() .equals("")) { String indexVal =
	 * indexCell.toString(); try { int index = (int)
	 * Double.parseDouble(indexVal); // only // one // group
	 * units.add(levelsOfUnits.get(j).get(index)); } catch (Exception exp) {
	 * String[] indexes = indexVal.split(","); for (int m = 0; m <
	 * indexes.length; m++) { units.add(levelsOfUnits.get(j).get(
	 * Integer.parseInt(indexes[m]))); } } } else { // Creating a new one String
	 * purposeVal = purposeCell .getStringCellValue(); String[] purposes =
	 * purposeVal.split(","); String operationVal = operationCell
	 * .getStringCellValue(); String[] operations = operationVal.split(","); for
	 * (int k = 0; k < purposes.length; k++) { RevisionUnit tempUnit = new
	 * RevisionUnit(); tempUnit.setRevision_purpose(RevisionPurpose
	 * .getPurposeIndex(purposes[k])); tempUnit.setRevision_op(RevisionOp
	 * .getOpIndex(operations[k])); units.add(tempUnit); } } }
	 * tempList.add(units); } } }
	 * 
	 * }
	 */
}
