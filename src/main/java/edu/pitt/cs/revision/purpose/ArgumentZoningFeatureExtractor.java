package edu.pitt.cs.revision.purpose;

import java.util.ArrayList;
import java.util.Stack;
import java.util.Hashtable;
import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import edu.pitt.cs.revision.machinelearning.FeatureName;
import edu.pitt.lrdc.cs.revision.model.RevisionDocument;

public class ArgumentZoningFeatureExtractor {
	private static ArgumentZoningFeatureExtractor instance = null;

	private String aim = "AIM";
	private String basis = "BAS";
	private String background = "BKG";
	private String contrast = "CTR";
	private String other = "OTH";
	private String own = "OWN";
	private String text = "TXT";
	private String empty = "EMPTY";
	private String[] tags = { aim, basis, background, contrast, other, own,
			text };

	private static String path = "C:\\Users\\zhangfan\\Downloads\\dlitmanRaz";

	private Hashtable<String, Hashtable<String, String>> argIndicesD1;
	private Hashtable<String, Hashtable<String, String>> argIndicesD2;

	private ArgumentZoningFeatureExtractor() {
		argIndicesD1 = new Hashtable<String, Hashtable<String, String>>();
		argIndicesD2 = new Hashtable<String, Hashtable<String, String>>();
	}

	public static ArgumentZoningFeatureExtractor getInstance()
			throws ParserConfigurationException, SAXException, IOException {
		if (instance == null) {
			createInstance(null);
		}
		return instance;
	}

	public static void createInstance(String inputPath)
			throws ParserConfigurationException, SAXException, IOException {
		instance = new ArgumentZoningFeatureExtractor();
		instance.readFiles(inputPath);
	}

	private String getRealNameTxt(String fileName) {
		File f = new File(fileName);
		String fName = f.getName();
		int index = fName.indexOf(".txt");
		fName = fName.substring(0, index);
		if (fName.contains("-")) {
			fName = fName.substring(0, fName.indexOf("-"));
		}
		fName = fName.replaceAll("_", " ");
		return fName.trim();
	}

	private void readFile(Hashtable<String, Hashtable<String, String>> table,
			File f) throws ParserConfigurationException, SAXException,
			IOException {
		String realName = getRealNameTxt(f.getName());
		Hashtable<String, String> typeIndex = new Hashtable<String, String>();
		table.put(realName, typeIndex);

		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(f);
		doc.getDocumentElement().normalize();

		NodeList nodeList = doc.getElementsByTagName("algorithm");
		for (int temp = 0; temp < nodeList.getLength(); temp++) {
			Element element = (Element) nodeList.item(temp);
			NodeList razList = element.getElementsByTagName("raz");
			for (int i = 0; i < razList.getLength(); i++) {
				Element raz = (Element) razList.item(i);
				for (String tag : tags) {
					NodeList tagList = raz.getElementsByTagName(tag);
					for (int j = 0; j < tagList.getLength(); j++) {
						Element tagElement = (Element) tagList.item(j);
						String valueStr = tagElement.getTextContent();
						String[] splits = valueStr.split(" ");
						String sentence = "";
						for (String split : splits) {
							sentence += split.substring(0, split.indexOf("_"))
									+ " ";
						}
						sentence = sentence.trim();
						typeIndex.put(sentence, tag);
					}
				}
			}
		}

	}

	private void readFiles(String inputPath)
			throws ParserConfigurationException, SAXException, IOException {
		File f = new File(inputPath);
		Stack<File> fileStack = new Stack<File>();
		fileStack.push(f);
		while (!fileStack.isEmpty()) {
			File root = fileStack.pop();
			if (root.isDirectory()) {
				File[] subs = root.listFiles();
				for (File sub : subs) {
					fileStack.push(sub);
				}
			} else {
				if (root.getAbsolutePath().contains("draft1")
						&& root.getName().endsWith(".raz")) {
					readFile(argIndicesD1, root);
				} else if (root.getAbsolutePath().contains("draft2")
						&& root.getName().endsWith(".raz")) {
					readFile(argIndicesD2, root);
				}
			}
		}
	}

	public String getRealNameRevision(String fileName) {
		File f = new File(fileName);
		String fName = f.getName();
		if (fName.contains(".txt")) {
			int index = fName.indexOf(".txt");
			fName = fName.substring(0, index);
		}
		if (fName.contains(".xlsx")) {
			int index = fName.indexOf(".xlsx");
			fName = fName.substring(0, index);
		}
		if (fName.contains("Annotation_")) {
			String annotationStr = "Annotation_";
			int index = fName.indexOf(annotationStr) + annotationStr.length();
			fName = fName.substring(index);
		}
		if (fName.contains(" - ")) {
			int index = fName.indexOf("-");
			fName = fName.substring(0, index).trim();
		}

		String[] strToTrim = { "Fan", "Christian", "Fian" };
		for (String str : strToTrim) {
			if (fName.endsWith(str)) {
				fName = fName.substring(0, fName.indexOf(str));
			}
		}
		fName = fName.replaceAll("_", " ");
		return fName.trim();
	}

	public void extractFeature(FeatureName features, Object[] featureVector,
			RevisionDocument doc, ArrayList<Integer> newIndexes,
			ArrayList<Integer> oldIndexes) {
		String name = getRealNameRevision(doc.getDocumentName());
		Hashtable<String, String> arg1Table = argIndicesD1.get(name);
		Hashtable<String, String> arg2Table = argIndicesD2.get(name);

		int oldFIndex = features.getIndex("OLD_ARGUMENT");
		int newFIndex = features.getIndex("NEW_ARGUMENT");

		if (oldIndexes != null) {
			int oldIndex = oldIndexes.get(0);
			if (oldIndex != -1) {
				String oldSent = doc.getOldSentence(oldIndex);
				if (arg1Table.containsKey(oldSent)) {
					String tag = arg1Table.get(oldSent);
					featureVector[oldFIndex] = tag;
				} else {
					featureVector[oldFIndex] = empty;
				}
			} else {
				featureVector[oldFIndex] = empty;
			}
		} else {
			featureVector[oldFIndex] = empty;
		}

		if (newIndexes != null) {
			int newIndex = newIndexes.get(0);
			if (newIndex != -1) {
				String newSent = doc.getNewSentence(newIndex);
				if (arg2Table.containsKey(newSent)) {
					String tag = arg2Table.get(newSent);
					featureVector[newFIndex] = tag;
				} else {
					featureVector[newFIndex] = empty;
				}
			} else {
				featureVector[newIndex] = empty;
			}

		} else {
			featureVector[newFIndex] = empty;
		}
	}

	public void insertFeature(FeatureName features) {
		ArrayList<Object> options = new ArrayList<Object>();
		for (String tag : tags) {
			options.add(tag);
		}
		options.add(empty);
		features.insertFeature("OLD_ARGUMENT", options);
		features.insertFeature("NEW_ARGUMENT", options);
	}
}
