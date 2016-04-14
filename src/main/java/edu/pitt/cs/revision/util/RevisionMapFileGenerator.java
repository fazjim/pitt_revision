package edu.pitt.cs.revision.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.traverse.TopologicalOrderIterator;

import com.google.gson.Gson;

import edu.pitt.lrdc.cs.revision.alignment.model.HeatMapUnit;
import edu.pitt.lrdc.cs.revision.io.RevisionDocumentReader;
import edu.pitt.lrdc.cs.revision.model.RevisionDocument;
import edu.pitt.lrdc.cs.revision.model.RevisionOp;
import edu.pitt.lrdc.cs.revision.model.RevisionPurpose;
import edu.pitt.lrdc.cs.revision.model.RevisionUnit;

public class RevisionMapFileGenerator {
	// If true, then do not distinguish between surfaces
	public static boolean ignoreSurface = false;

	/**
	 * If true, then do not distinguish between surfaces
	 * 
	 * @param ignoreSurface
	 */
	public static void setSurfaceIgnore(boolean ignoreSurface) {
		ignoreSurface = ignoreSurface;
	}

	public static String generateTxt(RevisionDocument doc) {
		String txt = "";
		txt = addHeader(txt);
		List<HeatMapUnit> units = generateUnitsGeneric(doc);
		adjustUnits(units);
		for (HeatMapUnit unit : units) {
			txt = addLine(unit, txt);
		}
		return txt;
	}

	public static String generateJson(RevisionDocument doc) {
		List<HeatMapUnit> units = generateUnitsGeneric(doc);
		adjustUnits(units);
		return toJson(units);
	}

	public static ArrayList<ArrayList<HeatMapUnit>> getUnits4CRF(
			RevisionDocument doc) {
		//List<HeatMapUnit> units = generateUnits4Tagging(doc);
		List<HeatMapUnit> units = generateUnitsGeneric(doc);
		adjustUnits(units);
		ArrayList<ArrayList<HeatMapUnit>> segmentedUnits = new ArrayList<ArrayList<HeatMapUnit>>();
		int currentP = -1;
		ArrayList<HeatMapUnit> currentList = null;
		for (HeatMapUnit unit : units) {
			if (unit.aVR - currentP > 1) {
				if (currentList != null)
					segmentedUnits.add(currentList);
				currentList = new ArrayList<HeatMapUnit>();
			}
			currentP = unit.aVR;
			currentList.add(unit);
		}
		segmentedUnits.add(currentList);
		return segmentedUnits;
	}

	public static void generateHeatMapFile(RevisionDocument doc, String filePath)
			throws IOException {
		BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
		String txt = generateTxt(doc);
		writer.write(txt);
		writer.close();
	}

	public static void main(String[] args) throws Exception {
		/*
		 * String path =
		 * "C:\\Not Backed Up\\data\\allNewData\\Fan\\All-jiaoyang\\Annotation_englishguy1 - 18192.txt.xlsx"
		 * ; String outputPath = "C:\\Not Backed Up\\data\\newSample.txt";
		 * 
		 * String testPath =
		 * "C:\\Not Backed Up\\data\\allNewData\\Fan\\All-jiaoyang\\Annotation_englishguy1.xlsx"
		 * ; ArrayList<ArrayList<HeatMapUnit>> units =
		 * RevisionMapFileGenerator.getUnits4CRF
		 * (RevisionDocumentReader.readDoc(testPath));
		 */
		// RevisionDocument doc = RevisionDocumentReader.readDoc(path);
		// generateHeatMapFile(doc, outputPath);
		// String root = "C:\\Not Backed Up\\data\\newSample";
		// String root = "C:\\Not Backed Up\\data\\newData\\tmp\\Jiaoyang";
		String root = "C:\\Not Backed Up\\exampleStudy\\map";
		// String outputPathRoot = "C:\\Not Backed Up\\data\\newSampleMap";
		// String outputPathRoot =
		// "C:\\Not Backed Up\\data\\newData\\tmp\\JiaoyangMap";
		String outputPathRoot = "C:\\Not Backed Up\\exampleStudy\\mapoutput";
		File folder = new File(root);
		File[] files = folder.listFiles();
		for (File tempFile : files) {
			RevisionDocument doc = RevisionDocumentReader.readDoc(tempFile
					.getAbsolutePath());
			String fileName = tempFile.getName();
			String outPath = outputPathRoot + "/"
					+ fileName.substring(0, fileName.length() - 5);
			generateHeatMapFile(doc, outPath);
			/*
			 * ArrayList<ArrayList<HeatMapUnit>> units = getUnits4CRF(doc);
			 * System.out.println(units.size()); for (ArrayList<HeatMapUnit>
			 * unitArr : units) { for (HeatMapUnit unit : unitArr) {
			 * System.out.println("REV:" + unit.revPurpose);
			 * System.out.println("S1:" + unit.scD1); System.out.println("S2:" +
			 * unit.scD2); } System.out.println("=============="); }
			 */
		}
	}

	public static String toJson(List<HeatMapUnit> units) {
		Gson gson = new Gson();
		String json = gson.toJson(units);
		return json;
	}

	public static String addHeader(String txt) {
		txt += "pD1\t";
		txt += "pD2\t";
		txt += "sD1\t";
		txt += "sD2\t";
		txt += "aR\t";
		txt += "aC\t";
		txt += "aVR\t";
		txt += "rType\t";
		txt += "rPurpose\t";
		txt += "scD1\t";
		txt += "scD2";
		txt += "\n";
		return txt;
	}

	public static String addLine(HeatMapUnit unit, String txt) {
		txt += unit.pD1 + "\t";
		txt += unit.pD2 + "\t";
		txt += unit.sD1 + "\t";
		txt += unit.sD2 + "\t";
		txt += unit.aR + "\t";
		txt += unit.aC + "\t";
		txt += unit.aVR + "\t";
		txt += unit.rType + "\t";
		txt += unit.rPurpose + "\t";
		txt += unit.scD1 + "\t";
		txt += unit.scD2;
		txt += "\n";
		return txt;
	}

	public static void shiftUp(List<HeatMapUnit> units, int index) {
		for (int i = index; i < units.size(); i++) {
			units.get(i).aVR = units.get(i).aVR - 2;
		}
	}

	public static void adjustUnitsPass2(List<HeatMapUnit> units) {
		for (int i = 0; i < units.size(); i++) {
			HeatMapUnit unit = units.get(i);
			if (unit.pD2 == -1) {
				int j = i + 1;
				if (j < units.size() && units.get(j).aVR - unit.aVR > 1) {
					while (j < units.size() && units.get(j).pD1 == -1) {
						j++;
					}
					if (j<units.size() && units.get(j).pD1 == unit.pD1) {
						shiftUp(units, i + 1);
					}
				}
			} else if (unit.pD1 == -1) {
				int j = i + 1;
				if (j < units.size() && units.get(j).aVR - unit.aVR > 1) {
					while (j < units.size() && units.get(j).pD2 == -1) {
						j++;
					}
					if (j < units.size() && units.get(j).pD2 == unit.pD2) {
						shiftUp(units, i + 1);
					}
				}
			}
		}
	}

	public static void adjustUnits(List<HeatMapUnit> units) {
		int aR = 1;
		int aC = 0;
		int currentP1 = 1;
		int currentP2 = 1;

		int aVR = 0;

		for (HeatMapUnit hmu : units) {
			int pD1 = hmu.pD1;
			int sD1 = hmu.sD1;
			int pD2 = hmu.pD2;
			int sD2 = hmu.sD2;
			if (pD1 == -1) {
				if (pD2 > currentP2) {
					currentP2 = pD2;
					aR++;
					aVR += 3;
					aC = sD2;
				} else {
					aC++;
					aVR++;
				}
			} else if (pD2 == -1) {
				if (pD1 > currentP1) {
					currentP1 = pD1;
					aR++;
					aVR += 3;
					aC = sD1;
				} else {
					aC++;
					aVR++;
				}
			} else {
				if (pD1 > currentP1) {
					currentP1 = pD1;
					if (pD2 > currentP2)
						currentP2 = pD2;
					aR++;
					aVR += 3;
					aC = sD1;
				} else {
					aC++;
					aVR++;
				}
			}
			hmu.aR = aR;
			hmu.aC = aC;
			hmu.aVR = aVR;
		}

		// Adding another step of vertical view processing
		adjustUnitsPass2(units);
	}

	public static void removeNegativeOne(ArrayList<Integer> indices) {
		if (indices != null) {
			int index = 0;

			while (index < indices.size()) {
				if (indices.get(index) == -1) {
					indices.remove(index);
				} else {
					index++;
				}
			}
		}
	}

	/**
	 * New generic method for both annotation and visualization
	 * 
	 * Handles the case of cross shifting operations
	 * 
	 * 
	 * @param doc
	 * @return
	 */
	public static List<HeatMapUnit> generateUnitsGeneric(RevisionDocument doc) {
		int cursorOld = 1;
		int cursorNew = 1;
		ArrayList<String> oldDraftSentences = doc.getOldDraftSentences();
		ArrayList<String> newDraftSentences = doc.getNewDraftSentences();
		int oldIndexMax = oldDraftSentences.size();
		int newIndexMax = newDraftSentences.size();

		Hashtable<Integer, Integer> oldParagraphSentences = new Hashtable<Integer, Integer>();
		Hashtable<Integer, Integer> newParagraphSentences = new Hashtable<Integer, Integer>();

		Hashtable<Integer, Integer> oldParaIndices = new Hashtable<Integer, Integer>();
		Hashtable<Integer, Integer> newParaIndices = new Hashtable<Integer, Integer>();

		Hashtable<Integer, HashSet<Integer>> oldRevisions = new Hashtable<Integer, HashSet<Integer>>();
		Hashtable<Integer, HashSet<Integer>> newRevisions = new Hashtable<Integer, HashSet<Integer>>();

		ArrayList<RevisionUnit> revisions = doc.getRoot()
				.getRevisionUnitAtLevel(0);
		for (RevisionUnit unit : revisions) {
			ArrayList<Integer> oldIndices = unit.getOldSentenceIndex();
			ArrayList<Integer> newIndices = unit.getNewSentenceIndex();
			for (Integer oldIndex : oldIndices) {
				if (oldIndex != -1) {
					if (!oldRevisions.containsKey(oldIndex)) {
						HashSet<Integer> revisionPurposes = new HashSet<Integer>();
						revisionPurposes.add(unit.getRevision_purpose());
						oldRevisions.put(oldIndex, revisionPurposes);
					} else {
						oldRevisions.get(oldIndex).add(
								unit.getRevision_purpose());
					}
				}
			}

			for (Integer newIndex : newIndices) {
				if (newIndex != -1) {
					if (!newRevisions.containsKey(newIndex)) {
						HashSet<Integer> revisionPurposes = new HashSet<Integer>();
						revisionPurposes.add(unit.getRevision_purpose());
						newRevisions.put(newIndex, revisionPurposes);
					} else {
						newRevisions.get(newIndex).add(
								unit.getRevision_purpose());
					}
				}
			}
		}

		for (int i = 0; i < oldDraftSentences.size(); i++) {
			int index = i + 1;
			int sD1 = 1;
			int oldParaIndex = doc.getParaNoOfOldSentence(index);
			if (!oldParagraphSentences.containsKey(oldParaIndex))
				oldParagraphSentences.put(oldParaIndex, sD1);
			else {
				sD1 = oldParagraphSentences.get(oldParaIndex) + 1;
				oldParagraphSentences.put(oldParaIndex, sD1);
			}
			oldParaIndices.put(index, sD1);
		}

		for (int i = 0; i < newDraftSentences.size(); i++) {
			int index = i + 1;
			int sD2 = 1;
			int newParaIndex = doc.getParaNoOfNewSentence(index);
			if (!newParagraphSentences.containsKey(newParaIndex))
				newParagraphSentences.put(newParaIndex, sD2);
			else {
				sD2 = newParagraphSentences.get(newParaIndex) + 1;
				newParagraphSentences.put(newParaIndex, sD2);
			}
			newParaIndices.put(index, sD2);
		}

		List<HeatMapUnit> unitList = new ArrayList<HeatMapUnit>();
		boolean isOldEnded = false;
		boolean isNewEnded = false;
		while (cursorOld <= oldIndexMax || cursorNew <= newIndexMax) {
			ArrayList<Integer> newIndices = null;
			ArrayList<Integer> oldIndices = null;
			if(!isOldEnded && cursorOld>oldIndexMax) isOldEnded = true;
			if(!isOldEnded) {
				newIndices = doc.getNewFromOld(cursorOld);
			}
			if(!isNewEnded && cursorNew >newIndexMax) isNewEnded = true;
			if(!isNewEnded) {
				oldIndices= doc.getOldFromNew(cursorNew);
			}
			removeNegativeOne(newIndices);
			removeNegativeOne(oldIndices);
			boolean moveOld = false;
			boolean moveNew = false;
			if (!isOldEnded && (newIndices == null || newIndices.size() == 0 || isNewEnded)) {
				moveOld = true;
				HeatMapUnit hmu = new HeatMapUnit();
				if(oldIndices!=null&& oldIndices.size()>0)
					hmu.realOldIndex = oldIndices.get(0);
				else 
					hmu.realOldIndex = -1;
				hmu.realNewIndex = -1;
				hmu.oldIndex = cursorOld;
				hmu.newIndex = -1;
				hmu.sD1 = oldParaIndices.get(cursorOld);
				hmu.sD2 = -1;
				hmu.scD1 = oldDraftSentences.get(cursorOld - 1);
				hmu.scD2 = "";
				hmu.pD1 = doc.getParaNoOfOldSentence(cursorOld);
				hmu.pD2 = -1;
				hmu.rType = RevisionOp.getOpName(RevisionOp.DELETE);
				if (oldRevisions.containsKey(cursorOld)) {
					HashSet<Integer> rPurposesSet = oldRevisions.get(cursorOld);
					String rPurposeStr = "";
					String revisionPurpose = "";
					for (Integer rPurpose : rPurposesSet) {
						if (revisionPurpose.equals("")) {
							revisionPurpose = RevisionPurpose
									.getPurposeName(rPurpose);
						} else {
							if (rPurpose < RevisionPurpose
									.getPurposeIndex(revisionPurpose)) {
								revisionPurpose = RevisionPurpose
										.getPurposeName(rPurpose);
							}
						}
						rPurposeStr += RevisionPurpose.getPurposeName(rPurpose)
								+ "+";
					}
					hmu.rPurpose = revisionPurpose;
					hmu.rPurposeStr = rPurposeStr;
				} else {
					hmu.rPurpose = "";
				}
				cursorOld++;
				unitList.add(hmu);
			}
			if (!isNewEnded && (oldIndices == null || oldIndices.size() == 0 || isOldEnded)) {
				moveNew = true;
				HeatMapUnit hmu = new HeatMapUnit();
				hmu.realOldIndex = -1;
				if(newIndices!=null&&newIndices.size()>0)
				hmu.realNewIndex = newIndices.get(0);
				else 
					hmu.realNewIndex = -1;
				hmu.oldIndex = -1;
				hmu.newIndex = cursorNew;
				hmu.sD1 = -1;
				hmu.sD2 = newParaIndices.get(cursorNew);
				hmu.scD1 = "";
				hmu.scD2 = newDraftSentences.get(cursorNew - 1);
				hmu.pD1 = -1;
				hmu.pD2 = doc.getParaNoOfNewSentence(cursorNew);
				hmu.rType = RevisionOp.getOpName(RevisionOp.ADD);
				if (newRevisions.containsKey(cursorNew)) {
					HashSet<Integer> rPurposesSet = newRevisions.get(cursorNew);
					String rPurposeStr = "";
					String revisionPurpose = "";
					for (Integer rPurpose : rPurposesSet) {
						if (revisionPurpose.equals("")) {
							revisionPurpose = RevisionPurpose
									.getPurposeName(rPurpose);
						} else {
							if (rPurpose < RevisionPurpose
									.getPurposeIndex(revisionPurpose)) {
								revisionPurpose = RevisionPurpose
										.getPurposeName(rPurpose);
							}
						}
						rPurposeStr += RevisionPurpose.getPurposeName(rPurpose)
								+ "+";
					}
					hmu.rPurpose = revisionPurpose;
					hmu.rPurposeStr = rPurposeStr;
				} else {
					hmu.rPurpose = "";
				}
				cursorNew++;
				unitList.add(hmu);
			}
			if (moveOld == false && moveNew == false) {
				HeatMapUnit hmu = new HeatMapUnit();
				if(oldIndices!=null&&oldIndices.size()>0) 
					hmu.realOldIndex = oldIndices.get(0);
				else
					hmu.realOldIndex = -1;
				if(newIndices!=null&&newIndices.size()>0)
					hmu.realNewIndex = newIndices.get(0);
				else 
						hmu.realNewIndex = -1;
				hmu.oldIndex = cursorOld;
				hmu.newIndex = cursorNew;
				hmu.sD1 = oldParaIndices.get(cursorOld);
				hmu.sD2 = newParaIndices.get(cursorNew);
				hmu.scD1 = oldDraftSentences.get(cursorOld - 1);
				hmu.scD2 = newDraftSentences.get(cursorNew - 1);
				hmu.pD1 = doc.getParaNoOfOldSentence(cursorOld);
				hmu.pD2 = doc.getParaNoOfNewSentence(cursorNew);
				if (hmu.scD1.equals(hmu.scD2)) {
					hmu.rType = RevisionPurpose
							.getPurposeName(RevisionPurpose.NOCHANGE);
				} else {
					hmu.rType = RevisionOp.getOpName(RevisionOp.MODIFY); // Can
																			// only
																			// be
																			// modify
																			// anyways
				}

				// Next setting up rPurposes
				if (newIndices.size() == 1 && oldIndices.size() == 1) {
					if (newIndices.get(0) == cursorNew
							&& oldIndices.get(0) == cursorOld) { // Easy case
																	// where all
																	// matches
						if (oldRevisions.containsKey(cursorOld)) {
							HashSet<Integer> rPurposesSet = oldRevisions
									.get(cursorOld);
							String rPurposeStr = "";
							String revisionPurpose = "";
							for (Integer rPurpose : rPurposesSet) {
								if (revisionPurpose.equals("")) {
									revisionPurpose = RevisionPurpose
											.getPurposeName(rPurpose);
								} else {
									if (rPurpose < RevisionPurpose
											.getPurposeIndex(revisionPurpose)) {
										revisionPurpose = RevisionPurpose
												.getPurposeName(rPurpose);
									}
								}
								rPurposeStr += RevisionPurpose
										.getPurposeName(rPurpose) + "+";
							}
							hmu.rPurpose = revisionPurpose;
							hmu.rPurposeStr = rPurposeStr;
						} else {
							hmu.rPurpose = "";
						}
						cursorOld++;
						cursorNew++;
						unitList.add(hmu);
					} else {
						// The weird case where both aligns to a different
						// sentence
						hmu.realOldIndex = oldIndices.get(0);
						hmu.realNewIndex = newIndices.get(0);
						hmu.realOldSC = oldDraftSentences
								.get(hmu.realOldIndex - 1);
						hmu.realNewSC = newDraftSentences
								.get(hmu.realNewIndex - 1);
						if (oldRevisions.containsKey(cursorOld)) {
							HashSet<Integer> rOldPurposesSet = oldRevisions
									.get(cursorOld);
							String rOldPurposeStr = "";
							String revisionPurposeOld = "";
							for (Integer rPurpose : rOldPurposesSet) {
								if (revisionPurposeOld.equals("")) {
									revisionPurposeOld = RevisionPurpose
											.getPurposeName(rPurpose);
								} else {
									if (rPurpose < RevisionPurpose
											.getPurposeIndex(revisionPurposeOld)) {
										revisionPurposeOld = RevisionPurpose
												.getPurposeName(rPurpose);
									}
								}
								rOldPurposeStr += RevisionPurpose
										.getPurposeName(rPurpose) + "+";
							}
							hmu.rPurposeOld = revisionPurposeOld;
							hmu.rPurposeOldStr = rOldPurposeStr;
						} else {
							hmu.rPurposeOld = "";
						}

						if (newRevisions.containsKey(cursorNew)) {
							HashSet<Integer> rNewPurposesSet = newRevisions
									.get(cursorNew);
							String rNewPurposeStr = "";
							String revisionPurposeNew = "";
							for (Integer rPurpose : rNewPurposesSet) {
								if (revisionPurposeNew.equals("")) {
									revisionPurposeNew = RevisionPurpose
											.getPurposeName(rPurpose);
								} else {
									if (rPurpose < RevisionPurpose
											.getPurposeIndex(revisionPurposeNew)) {
										revisionPurposeNew = RevisionPurpose
												.getPurposeName(rPurpose);
									}
								}
								rNewPurposeStr += RevisionPurpose
										.getPurposeName(rPurpose) + "+";
							}
							hmu.rPurposeNew = revisionPurposeNew;
							hmu.rPurposeNewStr = rNewPurposeStr;
						} else {
							hmu.rPurposeNew = "";
						}
						cursorOld++;
						cursorNew++;
						unitList.add(hmu);
					}
				} else {
					// multiple cases
					if (oldIndices.size() == 1 && newIndices.size() > 1) {
						String rPurposeStr = "";
						String revisionPurpose = "";
						// If the list are consecutive, then continue
						// building units, if not, then ignore
						Collections.sort(newIndices);
						if (oldIndices.get(0) == cursorOld) {
							// The case of 1 to N　alignment
							if (oldRevisions.containsKey(cursorOld)) {
								HashSet<Integer> rPurposesSet = oldRevisions
										.get(cursorOld);

								for (Integer rPurpose : rPurposesSet) {
									if (revisionPurpose.equals("")) {
										revisionPurpose = RevisionPurpose
												.getPurposeName(rPurpose);
									} else {
										if (rPurpose < RevisionPurpose
												.getPurposeIndex(revisionPurpose)) {
											revisionPurpose = RevisionPurpose
													.getPurposeName(rPurpose);
										}
									}
									rPurposeStr += RevisionPurpose
											.getPurposeName(rPurpose) + "+";
								}
								hmu.rPurpose = revisionPurpose;
								hmu.rPurposeStr = rPurposeStr;
							} else {
								hmu.rPurpose = "";
							}
							hmu.realOldIndex = oldIndices.get(0);
							hmu.realNewIndex = newIndices.get(0);
							unitList.add(hmu);

							
							for (Integer newIndex : newIndices) {
								if (newIndex != -1 && newIndex!=cursorNew) {
									if (newIndex - cursorNew == 1) {
										cursorNew = newIndex;
										HeatMapUnit hmuNext = new HeatMapUnit();
										hmuNext.realOldIndex = oldIndices.get(0);
										hmuNext.realNewIndex = newIndex;
										hmuNext.oldIndex = cursorOld;
										hmuNext.newIndex = cursorNew;
										hmuNext.sD1 = oldParaIndices
												.get(cursorOld);
										hmuNext.sD2 = newParaIndices
												.get(cursorNew);
										hmuNext.scD1 = oldDraftSentences
												.get(cursorOld - 1);
										hmuNext.scD2 = newDraftSentences
												.get(cursorNew - 1);
										hmuNext.pD1 = doc
												.getParaNoOfOldSentence(cursorOld);
										hmuNext.pD2 = doc
												.getParaNoOfNewSentence(cursorNew);
										hmuNext.rType = RevisionOp
												.getOpName(RevisionOp.MODIFY);
										hmuNext.rPurpose = revisionPurpose;
										hmuNext.rPurposeStr = rPurposeStr;
										unitList.add(hmuNext);
									} else {
										break;
									}
								}
							}
							cursorOld++;
							cursorNew++;
						} else {
							// The case where the sentence are not aligned
							hmu.realOldIndex = oldIndices.get(0);
							hmu.realNewIndex = newIndices.get(0);
							hmu.realOldSC = oldDraftSentences
									.get(hmu.realOldIndex - 1);
							hmu.realNewSC = newDraftSentences
									.get(hmu.realNewIndex - 1);
							if (oldRevisions.containsKey(cursorOld)) {
								HashSet<Integer> rOldPurposesSet = oldRevisions
										.get(cursorOld);
								String rOldPurposeStr = "";
								String revisionPurposeOld = "";
								for (Integer rPurpose : rOldPurposesSet) {
									if (revisionPurposeOld.equals("")) {
										revisionPurposeOld = RevisionPurpose
												.getPurposeName(rPurpose);
									} else {
										if (rPurpose < RevisionPurpose
												.getPurposeIndex(revisionPurposeOld)) {
											revisionPurposeOld = RevisionPurpose
													.getPurposeName(rPurpose);
										}
									}
									rOldPurposeStr += RevisionPurpose
											.getPurposeName(rPurpose) + "+";
								}
								hmu.rPurposeOld = revisionPurposeOld;
								hmu.rPurposeOldStr = rOldPurposeStr;
							} else {
								hmu.rPurpose = "";
							}
							if (newRevisions.containsKey(cursorNew)) {
								HashSet<Integer> rNewPurposesSet = newRevisions
										.get(cursorNew);
								String rNewPurposeStr = "";
								String revisionPurposeNew = "";
								for (Integer rPurpose : rNewPurposesSet) {
									if (revisionPurposeNew.equals("")) {
										revisionPurposeNew = RevisionPurpose
												.getPurposeName(rPurpose);
									} else {
										if (rPurpose < RevisionPurpose
												.getPurposeIndex(revisionPurposeNew)) {
											revisionPurposeNew = RevisionPurpose
													.getPurposeName(rPurpose);
										}
									}
									rNewPurposeStr += RevisionPurpose
											.getPurposeName(rPurpose) + "+";
								}
								hmu.rPurposeNew = revisionPurposeNew;
								hmu.rPurposeNewStr = rNewPurposeStr;
							} else {
								hmu.rPurposeNew = "";
							}
							cursorOld++;
							cursorNew++;
							unitList.add(hmu);
						}
					} else if (oldIndices.size() > 1 && newIndices.size() == 1) {
						String rPurposeStr = "";
						String revisionPurpose = "";
						if (newIndices.get(0) == cursorNew) {
							// The case of N to 1 alignment
							// If the list are consecutive, then continue
							// building units, if not, then ignore
							Collections.sort(oldIndices);
							if (newRevisions.containsKey(cursorNew)) {
								HashSet<Integer> rPurposesSet = newRevisions
										.get(cursorNew);
								for (Integer rPurpose : rPurposesSet) {
									if (revisionPurpose.equals("")) {
										revisionPurpose = RevisionPurpose
												.getPurposeName(rPurpose);
									} else {
										if (rPurpose < RevisionPurpose
												.getPurposeIndex(revisionPurpose)) {
											revisionPurpose = RevisionPurpose
													.getPurposeName(rPurpose);
										}
									}
									rPurposeStr += RevisionPurpose
											.getPurposeName(rPurpose) + "+";
								}
								hmu.rPurpose = revisionPurpose;
								hmu.rPurposeStr = rPurposeStr;
							} else {
								hmu.rPurpose = "";
							}
							hmu.realOldIndex = oldIndices.get(0);
							hmu.realNewIndex = newIndices.get(0);
							unitList.add(hmu);

							
							for (Integer oldIndex : oldIndices) {
								if (oldIndex != -1 && oldIndex!=cursorOld) {
									if (oldIndex - cursorOld == 1) {
										cursorOld = oldIndex;
										HeatMapUnit hmuNext = new HeatMapUnit();
										hmuNext.realOldIndex = oldIndex;
										hmuNext.realNewIndex = newIndices.get(0);
										hmuNext.oldIndex = cursorOld;
										hmuNext.newIndex = cursorNew;
										hmuNext.sD1 = oldParaIndices
												.get(cursorOld);
										hmuNext.sD2 = newParaIndices
												.get(cursorNew);
										hmuNext.scD1 = oldDraftSentences
												.get(cursorOld - 1);
										hmuNext.scD2 = newDraftSentences
												.get(cursorNew - 1);
										hmuNext.pD1 = doc
												.getParaNoOfOldSentence(cursorOld);
										hmuNext.pD2 = doc
												.getParaNoOfNewSentence(cursorNew);
										hmuNext.rType = RevisionOp
												.getOpName(RevisionOp.MODIFY);
										hmuNext.rPurpose = revisionPurpose;
										hmuNext.rPurposeStr = rPurposeStr;
										unitList.add(hmuNext);
									} else {
										break;
									}
								}
							}
							cursorOld++;
							cursorNew++;
						} else {
							hmu.realOldIndex = oldIndices.get(0);
							hmu.realNewIndex = newIndices.get(0);
							hmu.realOldSC = oldDraftSentences
									.get(hmu.realOldIndex - 1);
							hmu.realNewSC = newDraftSentences
									.get(hmu.realNewIndex - 1);
							if (oldRevisions.containsKey(cursorOld)) {
								HashSet<Integer> rOldPurposesSet = oldRevisions
										.get(cursorOld);
								String rOldPurposeStr = "";
								String revisionPurposeOld = "";
								for (Integer rPurpose : rOldPurposesSet) {
									if (revisionPurposeOld.equals("")) {
										revisionPurposeOld = RevisionPurpose
												.getPurposeName(rPurpose);
									} else {
										if (rPurpose < RevisionPurpose
												.getPurposeIndex(revisionPurposeOld)) {
											revisionPurposeOld = RevisionPurpose
													.getPurposeName(rPurpose);
										}
									}
									rOldPurposeStr += RevisionPurpose
											.getPurposeName(rPurpose) + "+";
								}
								hmu.rPurposeOld = revisionPurposeOld;
								hmu.rPurposeOldStr = rOldPurposeStr;
							} else {
								hmu.rPurposeOld = "";
							}
							if (newRevisions.containsKey(cursorNew)) {
								HashSet<Integer> rNewPurposesSet = newRevisions
										.get(cursorNew);
								String rNewPurposeStr = "";
								String revisionPurposeNew = "";
								for (Integer rPurpose : rNewPurposesSet) {
									if (revisionPurposeNew.equals("")) {
										revisionPurposeNew = RevisionPurpose
												.getPurposeName(rPurpose);
									} else {
										if (rPurpose < RevisionPurpose
												.getPurposeIndex(revisionPurposeNew)) {
											revisionPurposeNew = RevisionPurpose
													.getPurposeName(rPurpose);
										}
									}
									rNewPurposeStr += RevisionPurpose
											.getPurposeName(rPurpose) + "+";
								}
								hmu.rPurposeNew = revisionPurposeNew;
								hmu.rPurposeNewStr = rNewPurposeStr;
							} else {
								hmu.rPurposeNew = "";
							}
							cursorOld++;
							cursorNew++;
							unitList.add(hmu);
						}
					} else {
						// In this case, both aligned to multiple different
						// sentences, so just move with the case
						hmu.realOldIndex = oldIndices.get(0);
						hmu.realNewIndex = newIndices.get(0);
						hmu.realOldSC = oldDraftSentences
								.get(hmu.realOldIndex - 1);
						hmu.realNewSC = newDraftSentences
								.get(hmu.realNewIndex - 1);
						if (oldRevisions.containsKey(cursorOld)) {
							HashSet<Integer> rOldPurposesSet = oldRevisions
									.get(cursorOld);
							String rOldPurposeStr = "";
							String revisionPurposeOld = "";
							for (Integer rPurpose : rOldPurposesSet) {
								if (revisionPurposeOld.equals("")) {
									revisionPurposeOld = RevisionPurpose
											.getPurposeName(rPurpose);
								} else {
									if (rPurpose < RevisionPurpose
											.getPurposeIndex(revisionPurposeOld)) {
										revisionPurposeOld = RevisionPurpose
												.getPurposeName(rPurpose);
									}
								}
								rOldPurposeStr += RevisionPurpose
										.getPurposeName(rPurpose) + "+";
							}
							hmu.rPurposeOld = revisionPurposeOld;
							hmu.rPurposeOldStr = rOldPurposeStr;
						} else {
							hmu.rPurposeOld = "";
						}
						if (newRevisions.containsKey(cursorNew)) {
							HashSet<Integer> rNewPurposesSet = newRevisions
									.get(cursorNew);
							String rNewPurposeStr = "";
							String revisionPurposeNew = "";
							for (Integer rPurpose : rNewPurposesSet) {
								if (revisionPurposeNew.equals("")) {
									revisionPurposeNew = RevisionPurpose
											.getPurposeName(rPurpose);
								} else {
									if (rPurpose < RevisionPurpose
											.getPurposeIndex(revisionPurposeNew)) {
										revisionPurposeNew = RevisionPurpose
												.getPurposeName(rPurpose);
									}
								}
								rNewPurposeStr += RevisionPurpose
										.getPurposeName(rPurpose) + "+";
							}
							hmu.rPurposeNew = revisionPurposeNew;
							hmu.rPurposeNewStr = rNewPurposeStr;
						} else {
							hmu.rPurposeNew = "";
						}
						cursorOld++;
						cursorNew++;
						unitList.add(hmu);
					}
				}
			}
		}
		return unitList;
	}

	/**
	 * A quick implementation, might not be the most efficient, save for future
	 * optimization
	 * 
	 * 
	 * @param doc
	 * @return
	 */
	public static List<HeatMapUnit> generateUnits(RevisionDocument doc) {
		ArrayList<String> oldDraftSentences = doc.getOldDraftSentences();
		ArrayList<String> newDraftSentences = doc.getNewDraftSentences();

		Hashtable<Integer, Integer> oldParagraphSentences = new Hashtable<Integer, Integer>();
		Hashtable<Integer, Integer> newParagraphSentences = new Hashtable<Integer, Integer>();

		Hashtable<Integer, Integer> oldParaIndices = new Hashtable<Integer, Integer>();
		Hashtable<Integer, Integer> newParaIndices = new Hashtable<Integer, Integer>();

		Hashtable<String, HeatMapUnit> unitMaps = new Hashtable<String, HeatMapUnit>();

		for (int i = 0; i < oldDraftSentences.size(); i++) {
			int index = i + 1;
			int sD1 = 1;
			int oldParaIndex = doc.getParaNoOfOldSentence(index);
			if (!oldParagraphSentences.containsKey(oldParaIndex))
				oldParagraphSentences.put(oldParaIndex, sD1);
			else {
				sD1 = oldParagraphSentences.get(oldParaIndex) + 1;
				oldParagraphSentences.put(oldParaIndex, sD1);
			}
			oldParaIndices.put(index, sD1);
		}

		for (int i = 0; i < newDraftSentences.size(); i++) {
			int index = i + 1;
			int sD2 = 1;
			int newParaIndex = doc.getParaNoOfNewSentence(index);
			if (!newParagraphSentences.containsKey(newParaIndex))
				newParagraphSentences.put(newParaIndex, sD2);
			else {
				sD2 = newParagraphSentences.get(newParaIndex) + 1;
				newParagraphSentences.put(newParaIndex, sD2);
			}
			newParaIndices.put(index, sD2);
		}

		ArrayList<RevisionUnit> rus = doc.getRoot().getRevisionUnitAtLevel(0);
		for (RevisionUnit ru : rus) {
			ArrayList<Integer> oldIndices = ru.getOldSentenceIndex();
			ArrayList<Integer> newIndices = ru.getNewSentenceIndex();
			String revisionOp = RevisionOp.getOpName(ru.getRevision_op());
			String revisionPurpose = RevisionPurpose.getPurposeName(ru
					.getRevision_purpose());

			if (ignoreSurface == true
					&& (ru.getRevision_purpose() == RevisionPurpose.CONVENTIONS_GRAMMAR_SPELLING
							|| ru.getRevision_purpose() == RevisionPurpose.WORDUSAGE_CLARITY || ru
							.getRevision_purpose() == RevisionPurpose.WORDUSAGE_CLARITY_CASCADED)) {
				revisionPurpose = RevisionPurpose
						.getPurposeName(RevisionPurpose.SURFACE);
			}
			if (oldIndices == null || oldIndices.size() == 0
					|| (oldIndices.size() == 1 && oldIndices.get(0) == -1)) {
				// Should be add
				int oldIndex = -1;
				int pD1 = -1;
				int sD1 = -1;
				String scD1 = "";
				for (Integer newIndex : newIndices) {
					if (newIndex != -1) {
						HeatMapUnit hmu = new HeatMapUnit();
						hmu.oldIndex = oldIndex;
						hmu.newIndex = newIndex;
						hmu.sD1 = sD1;
						hmu.sD2 = newParaIndices.get(newIndex);
						hmu.scD1 = scD1;
						hmu.scD2 = doc.getNewSentence(newIndex);
						hmu.pD1 = pD1;
						hmu.pD2 = doc.getParaNoOfNewSentence(newIndex);
						hmu.rType = revisionOp;
						hmu.rPurpose = revisionPurpose;
						String key = oldIndex + ":" + newIndex;
						unitMaps.put(key, hmu);
					}
				}
			} else if (newIndices == null || newIndices.size() == 0
					|| (newIndices.size() == 1 && newIndices.get(0) == -1)) {
				// Should be delete
				int newIndex = -1;
				int pD2 = -1;
				int sD2 = -1;
				String scD2 = "";
				for (Integer oldIndex : oldIndices) {
					if (oldIndex != -1) {
						HeatMapUnit hmu = new HeatMapUnit();
						hmu.oldIndex = oldIndex;
						hmu.newIndex = newIndex;
						hmu.sD1 = oldParaIndices.get(oldIndex);
						hmu.sD2 = sD2;
						hmu.scD1 = doc.getOldSentence(oldIndex);
						hmu.scD2 = scD2;
						hmu.pD1 = doc.getParaNoOfOldSentence(oldIndex);
						hmu.pD2 = pD2;
						hmu.rType = revisionOp;
						hmu.rPurpose = revisionPurpose;
						String key = oldIndex + ":" + newIndex;
						unitMaps.put(key, hmu);
					}
				}
			} else {

				for (Integer oldIndex : oldIndices) {
					if (oldIndex != -1) {
						for (Integer newIndex : newIndices) {
							if (newIndex != -1) {
								HeatMapUnit hmu = new HeatMapUnit();
								hmu.oldIndex = oldIndex;
								hmu.newIndex = newIndex;
								hmu.sD1 = oldParaIndices.get(oldIndex);
								hmu.sD2 = newParaIndices.get(newIndex);
								hmu.scD1 = doc.getOldSentence(oldIndex);
								hmu.scD2 = doc.getNewSentence(newIndex);
								hmu.pD1 = doc.getParaNoOfOldSentence(oldIndex);
								hmu.pD2 = doc.getParaNoOfNewSentence(newIndex);
								hmu.rType = revisionOp;
								hmu.rPurpose = revisionPurpose;
								String key = oldIndex + ":" + newIndex;
								unitMaps.put(key, hmu);
							}
						}
					}
				}
			}
		}

		// Now handling the rest of the alignments, should be perfect alignments
		for (int i = 0; i < oldDraftSentences.size(); i++) {
			int oldIndex = i + 1;
			ArrayList<Integer> newIndices = doc.getNewFromOld(oldIndex);
			if (newIndices != null && newIndices.size() != 0) {
				for (Integer newIndex : newIndices) {
					if (newIndex != -1) {
						String key = oldIndex + ":" + newIndex;
						if (!unitMaps.containsKey(key)) {
							HeatMapUnit hmu = new HeatMapUnit();
							hmu.oldIndex = oldIndex;
							hmu.newIndex = newIndex;
							hmu.sD1 = oldParaIndices.get(oldIndex);
							hmu.sD2 = newParaIndices.get(newIndex);
							hmu.scD1 = hmu.scD2 = doc.getOldSentence(oldIndex);
							hmu.pD1 = doc.getParaNoOfOldSentence(oldIndex);
							hmu.pD2 = doc.getParaNoOfNewSentence(newIndex);
							hmu.rType = RevisionPurpose
									.getPurposeName(RevisionPurpose.NOCHANGE);
							hmu.rPurpose = "";
							unitMaps.put(key, hmu);
						}
					}
				}
			}
		}

		List<HeatMapUnit> hmUnits = new ArrayList<HeatMapUnit>();
		Iterator<String> it = unitMaps.keySet().iterator();
		while (it.hasNext()) {
			String key = it.next();
			hmUnits.add(unitMaps.get(key));
		}

		// Collections.sort(hmUnits);
		hmUnits = topoSort(hmUnits);
		return hmUnits;
	}

	/**
	 * This version includes the unannotated changes
	 * 
	 * For cases: Half aligned or not aligned at all
	 * 
	 * @param doc
	 * @return
	 */
	public static List<HeatMapUnit> generateUnits4Tagging(RevisionDocument doc) {
		ArrayList<String> oldDraftSentences = doc.getOldDraftSentences();
		ArrayList<String> newDraftSentences = doc.getNewDraftSentences();

		Hashtable<Integer, Integer> oldParagraphSentences = new Hashtable<Integer, Integer>();
		Hashtable<Integer, Integer> newParagraphSentences = new Hashtable<Integer, Integer>();

		Hashtable<Integer, Integer> oldParaIndices = new Hashtable<Integer, Integer>();
		Hashtable<Integer, Integer> newParaIndices = new Hashtable<Integer, Integer>();

		Hashtable<String, HeatMapUnit> unitMaps = new Hashtable<String, HeatMapUnit>();

		for (int i = 0; i < oldDraftSentences.size(); i++) {
			int index = i + 1;
			int sD1 = 1;
			int oldParaIndex = doc.getParaNoOfOldSentence(index);
			if (!oldParagraphSentences.containsKey(oldParaIndex))
				oldParagraphSentences.put(oldParaIndex, sD1);
			else {
				sD1 = oldParagraphSentences.get(oldParaIndex) + 1;
				oldParagraphSentences.put(oldParaIndex, sD1);
			}
			oldParaIndices.put(index, sD1);
		}

		for (int i = 0; i < newDraftSentences.size(); i++) {
			int index = i + 1;
			int sD2 = 1;
			int newParaIndex = doc.getParaNoOfNewSentence(index);
			if (!newParagraphSentences.containsKey(newParaIndex))
				newParagraphSentences.put(newParaIndex, sD2);
			else {
				sD2 = newParagraphSentences.get(newParaIndex) + 1;
				newParagraphSentences.put(newParaIndex, sD2);
			}
			newParaIndices.put(index, sD2);
		}

		ArrayList<RevisionUnit> rus = doc.getRoot().getRevisionUnitAtLevel(0);
		for (RevisionUnit ru : rus) {
			ArrayList<Integer> oldIndices = ru.getOldSentenceIndex();
			ArrayList<Integer> newIndices = ru.getNewSentenceIndex();
			String revisionOp = RevisionOp.getOpName(ru.getRevision_op());
			String revisionPurpose = RevisionPurpose.getPurposeName(ru
					.getRevision_purpose());

			if (ignoreSurface == true
					&& (ru.getRevision_purpose() == RevisionPurpose.CONVENTIONS_GRAMMAR_SPELLING
							|| ru.getRevision_purpose() == RevisionPurpose.WORDUSAGE_CLARITY || ru
							.getRevision_purpose() == RevisionPurpose.WORDUSAGE_CLARITY_CASCADED)) {
				revisionPurpose = RevisionPurpose
						.getPurposeName(RevisionPurpose.SURFACE);
			}
			if (oldIndices == null || oldIndices.size() == 0
					|| (oldIndices.size() == 1 && oldIndices.get(0) == -1)) {
				// Should be add
				int oldIndex = -1;
				int pD1 = -1;
				int sD1 = -1;
				String scD1 = "";
				for (Integer newIndex : newIndices) {
					if (newIndex != -1) {
						HeatMapUnit hmu = new HeatMapUnit();
						hmu.oldIndex = -1;
						hmu.newIndex = newIndex;
						hmu.sD1 = sD1;
						hmu.sD2 = newParaIndices.get(newIndex);
						hmu.scD1 = scD1;
						hmu.scD2 = doc.getNewSentence(newIndex);
						hmu.pD1 = pD1;
						hmu.pD2 = doc.getParaNoOfNewSentence(newIndex);
						hmu.rType = revisionOp;
						hmu.rPurpose = revisionPurpose;
						String key = oldIndex + ":" + newIndex;
						unitMaps.put(key, hmu);
					}
				}
			} else if (newIndices == null || newIndices.size() == 0
					|| (newIndices.size() == 1 && newIndices.get(0) == -1)) {
				// Should be delete
				int newIndex = -1;
				int pD2 = -1;
				int sD2 = -1;
				String scD2 = "";
				for (Integer oldIndex : oldIndices) {
					if (oldIndex != -1) {
						HeatMapUnit hmu = new HeatMapUnit();
						hmu.oldIndex = oldIndex;
						hmu.newIndex = -1;
						hmu.sD1 = oldParaIndices.get(oldIndex);
						hmu.sD2 = sD2;
						hmu.scD1 = doc.getOldSentence(oldIndex);
						hmu.scD2 = scD2;
						hmu.pD1 = doc.getParaNoOfOldSentence(oldIndex);
						hmu.pD2 = pD2;
						hmu.rType = revisionOp;
						hmu.rPurpose = revisionPurpose;
						String key = oldIndex + ":" + newIndex;
						unitMaps.put(key, hmu);
					}
				}
			} else {

				for (Integer oldIndex : oldIndices) {
					if (oldIndex != -1) {
						for (Integer newIndex : newIndices) {
							if (newIndex != -1) {
								HeatMapUnit hmu = new HeatMapUnit();
								hmu.oldIndex = oldIndex;
								hmu.newIndex = newIndex;
								hmu.sD1 = oldParaIndices.get(oldIndex);
								hmu.sD2 = newParaIndices.get(newIndex);
								hmu.scD1 = doc.getOldSentence(oldIndex);
								hmu.scD2 = doc.getNewSentence(newIndex);
								hmu.pD1 = doc.getParaNoOfOldSentence(oldIndex);
								hmu.pD2 = doc.getParaNoOfNewSentence(newIndex);
								hmu.rType = revisionOp;
								hmu.rPurpose = revisionPurpose;
								String key = oldIndex + ":" + newIndex;
								unitMaps.put(key, hmu);
							}
						}
					}
				}
			}
		}

		// Now handling the rest of the alignments, if aligned, should be
		// perfect alignments
		for (int i = 0; i < oldDraftSentences.size(); i++) {
			int oldIndex = i + 1;
			ArrayList<Integer> newIndices = doc.getNewFromOld(oldIndex);
			if (newIndices != null && newIndices.size() != 0) {
				for (Integer newIndex : newIndices) {
					if (newIndex != -1) {
						String key = oldIndex + ":" + newIndex;
						if (!unitMaps.containsKey(key)) {
							HeatMapUnit hmu = new HeatMapUnit();
							hmu.oldIndex = oldIndex;
							hmu.newIndex = newIndex;
							hmu.sD1 = oldParaIndices.get(oldIndex);
							hmu.sD2 = newParaIndices.get(newIndex);
							hmu.scD1 = doc.getOldSentence(oldIndex);
							hmu.scD2 = doc.getNewSentence(newIndex);
							if (hmu.scD1.equals(hmu.scD2)) {
								hmu.rType = "Nochange";
							} else {
								hmu.rType = "Modify";
							}
							hmu.pD1 = doc.getParaNoOfOldSentence(oldIndex);
							hmu.pD2 = doc.getParaNoOfNewSentence(newIndex);
							hmu.rPurpose = "";
							unitMaps.put(key, hmu);
						}
					}
				}
			} else {
				String key = oldIndex + ":-1";
				if (!unitMaps.containsKey(key)) {
					HeatMapUnit hmu = new HeatMapUnit();
					hmu.oldIndex = oldIndex;
					hmu.newIndex = -1;
					hmu.sD1 = oldParaIndices.get(oldIndex);
					hmu.sD2 = -1;
					hmu.scD1 = doc.getOldSentence(oldIndex);
					hmu.scD2 = "";
					hmu.rType = "Delete";
					hmu.pD1 = doc.getParaNoOfOldSentence(oldIndex);
					hmu.pD2 = -1;
					hmu.rPurpose = "";
					unitMaps.put(key, hmu);
				}
			}
		}

		for (int i = 0; i < newDraftSentences.size(); i++) {
			int newIndex = i + 1;
			ArrayList<Integer> oldIndices = doc.getOldFromNew(newIndex);
			if (oldIndices == null || oldIndices.size() == 0) {
				String key = "-1:" + newIndex;
				if (!unitMaps.containsKey(key)) {
					HeatMapUnit hmu = new HeatMapUnit();
					hmu.oldIndex = -1;
					hmu.newIndex = newIndex;
					hmu.sD1 = -1;
					hmu.sD2 = newParaIndices.get(newIndex);
					hmu.scD1 = "";
					hmu.scD2 = doc.getNewSentence(newIndex);
					hmu.rType = "Add";
					hmu.pD1 = -1;
					hmu.pD2 = doc.getParaNoOfNewSentence(newIndex);
					hmu.rPurpose = "";
					unitMaps.put(key, hmu);
				}
			}
		}

		List<HeatMapUnit> hmUnits = new ArrayList<HeatMapUnit>();
		Iterator<String> it = unitMaps.keySet().iterator();
		while (it.hasNext()) {
			String key = it.next();
			hmUnits.add(unitMaps.get(key));
		}

		// Collections.sort(hmUnits);
		try {
			hmUnits = topoSort(hmUnits);
		} catch (Exception exp) {
			System.out.println(doc.getDocumentName());
		}
		return hmUnits;
	}

	public static void breakUnits(List<HeatMapUnit> hmUnits) {

	}

	public static ArrayList<HeatMapUnit> topoSort(List<HeatMapUnit> hmUnits) {
		DirectedGraph<HeatMapUnit, DefaultEdge> g = new DefaultDirectedGraph<HeatMapUnit, DefaultEdge>(
				DefaultEdge.class);
		for (int i = 0; i < hmUnits.size(); i++) {
			g.addVertex(hmUnits.get(i));
		}
		for (int i = 0; i < hmUnits.size(); i++) {
			for (int j = i + 1; j < hmUnits.size(); j++) {
				HeatMapUnit unit1 = hmUnits.get(i);
				HeatMapUnit unit2 = hmUnits.get(j);
				int score = HeatMapUnit.compare(unit1, unit2);
				if (score > 0) {
					g.addEdge(unit2, unit1);
				} else if (score < 0) {
					g.addEdge(unit1, unit2);
				}
			}
		}
		ArrayList<HeatMapUnit> newList = new ArrayList<HeatMapUnit>();
		TopologicalOrderIterator<HeatMapUnit, DefaultEdge> iter = new TopologicalOrderIterator<HeatMapUnit, DefaultEdge>(
				g);
		while (iter.hasNext()) {
			HeatMapUnit node = iter.next();
			newList.add(node);
		}
		return newList;
	}
}
