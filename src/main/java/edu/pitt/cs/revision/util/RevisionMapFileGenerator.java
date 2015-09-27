package edu.pitt.cs.revision.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import com.google.gson.Gson;

import edu.pitt.lrdc.cs.revision.alignment.model.HeatMapUnit;
import edu.pitt.lrdc.cs.revision.io.RevisionDocumentReader;
import edu.pitt.lrdc.cs.revision.model.RevisionDocument;
import edu.pitt.lrdc.cs.revision.model.RevisionOp;
import edu.pitt.lrdc.cs.revision.model.RevisionPurpose;
import edu.pitt.lrdc.cs.revision.model.RevisionUnit;

public class RevisionMapFileGenerator {
	// If true, then do not distinguish between surfaces
	public static boolean ignoreSurface = true;

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
		List<HeatMapUnit> units = generateUnits(doc);
		adjustUnits(units);
		for (HeatMapUnit unit : units) {
			txt = addLine(unit, txt);
		}
		return txt;
	}
	
	public static String generateJson(RevisionDocument doc) {
		List<HeatMapUnit> units = generateUnits(doc);
		adjustUnits(units);
		return toJson(units);
	}

	public static ArrayList<ArrayList<HeatMapUnit>> getUnits4CRF(
			RevisionDocument doc) {
		List<HeatMapUnit> units = generateUnits(doc);
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
		String path = "C:\\Not Backed Up\\data\\newSample\\Annotation_A4effort - 18178.txt.xlsx";
		String outputPath = "C:\\Not Backed Up\\data\\newSample.txt";
		// RevisionDocument doc = RevisionDocumentReader.readDoc(path);
		// generateHeatMapFile(doc, outputPath);
		//String root = "C:\\Not Backed Up\\data\\newSample";
		String root = "C:\\Not Backed Up\\data\\newData\\tmp\\Jiaoyang";
		//String outputPathRoot = "C:\\Not Backed Up\\data\\newSampleMap";
		String outputPathRoot = "C:\\Not Backed Up\\data\\newData\\tmp\\JiaoyangMap";
		File folder = new File(root);
		File[] files = folder.listFiles();
		for (File tempFile : files) {
			RevisionDocument doc = RevisionDocumentReader.readDoc(tempFile
					.getAbsolutePath());
			String fileName = tempFile.getName();
			String outPath = outputPathRoot + "/"
					+ fileName.substring(0, fileName.length() - 5);
			 generateHeatMapFile(doc, outPath);
			/*ArrayList<ArrayList<HeatMapUnit>> units = getUnits4CRF(doc);
			System.out.println(units.size());
			for (ArrayList<HeatMapUnit> unitArr : units) {
				for (HeatMapUnit unit : unitArr) {
					System.out.println("REV:" + unit.revPurpose);
					System.out.println("S1:" + unit.scD1);
					System.out.println("S2:" + unit.scD2);
				}
				System.out.println("==============");
			}*/
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
				aC++;
				aVR++;
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

	}

	/**
	 * A quick implementation, might not be the most efficient, save for future
	 * optimization
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
				revisionPurpose = "Surface";
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
							hmu.sD1 = oldParaIndices.get(oldIndex);
							hmu.sD2 = newParaIndices.get(newIndex);
							hmu.scD1 = hmu.scD2 = doc.getOldSentence(oldIndex);
							hmu.pD1 = doc.getParaNoOfOldSentence(oldIndex);
							hmu.pD2 = doc.getParaNoOfNewSentence(newIndex);
							hmu.rType = "Nochange";
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

		Collections.sort(hmUnits);
		return hmUnits;
	}
}
