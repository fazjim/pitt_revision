package edu.pitt.cs.revision.reviewLinking.annotation;

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.StringTokenizer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import edu.pitt.cs.revision.reviewLinking.CommentBoxReview;
import edu.pitt.cs.revision.reviewLinking.ReviewItem;
import edu.pitt.cs.revision.reviewLinking.ReviewSolution;
import edu.pitt.cs.revision.reviewLinking.ReviewTarget;

public class ReviewAnnotationReader {

	public static List<String> getValues(Element eElement) {
		List<String> values = new ArrayList<String>();
		NodeList list = eElement.getElementsByTagName("Node");
		for(int li = 0;li<list.getLength();li++) {
			Node ee = list.item(li);
			Element eeElement = (Element)ee;
			//System.out.println(eeElement.getAttribute("id"));
			values.add(eeElement.getNextSibling().getTextContent());
		}
		return values;
	}

	public static String queryStr(List<String> values, List<String> locs,
			String startLoc, String endLoc) {
		int startIndex = 0, endIndex = 0;

		for (int i = 0; i < locs.size(); i++) {
			if (locs.get(i).equals(startLoc)) {
				startIndex = i;
			}
			if (locs.get(i).equals(endLoc)) {
				endIndex = i;
			}
		}
		
		StringBuffer sb = new StringBuffer();
		for (int index = startIndex; index < endIndex; index++) {
			sb.append(values.get(index));
		}
		return sb.toString();
	}

	public static CommentBoxReview getReview(List<CommentBoxReview> cbrs,
			List<Integer> locs, int startIndex) {
		int start = 1;
		while (start < cbrs.size()) {
			if (startIndex < locs.get(start)) {
				return cbrs.get(start-1);
			} else {
				start++;
			}
		}
		return cbrs.get(cbrs.size() - 1);
	}

	public static List<CommentBoxReview> readReviews(String fileName) {
		List<CommentBoxReview> boxReviews = new ArrayList<CommentBoxReview>();
		try {
			File fXmlFile = new File(fileName);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
			doc.getDocumentElement().normalize();

			NodeList textList = doc.getElementsByTagName("TextWithNodes");
			List<String> reviewStrs = null;
			for (int temp = 0; temp < textList.getLength(); temp++) {
				Node nNode = textList.item(temp);
				Element eElement = (Element)nNode;
				reviewStrs = getValues(eElement);
			}

			List<String> locs = new ArrayList<String>();
			List<Integer> locsInt = new ArrayList<Integer>();
			NodeList locList = doc.getElementsByTagName("Node");
			for (int temp = 0; temp < locList.getLength(); temp++) {
				Node nNode = locList.item(temp);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;
					String loc = eElement.getAttribute("id");
					locs.add(loc);
				}
			}

			// Adding box reviews
			NodeList annotationSets = doc.getElementsByTagName("AnnotationSet");
			Element commentBoxElementRoot = (Element) annotationSets.item(1);
			NodeList comments = commentBoxElementRoot
					.getElementsByTagName("Annotation");
			for (int temp = 0; temp < comments.getLength(); temp++) {
				Node nNode = comments.item(temp);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;
					String startNode = eElement.getAttribute("StartNode");
					String endNode = eElement.getAttribute("EndNode");
					locsInt.add(Integer.parseInt(startNode));
					String review = queryStr(reviewStrs, locs, startNode,
							endNode);
					CommentBoxReview cbr = new CommentBoxReview();
					cbr.setContent(review);
					boxReviews.add(cbr);
				}
			}

			// Review targets and review solutions
			List<ReviewTarget> targets = new ArrayList<ReviewTarget>();
			List<ReviewSolution> solutions = new ArrayList<ReviewSolution>();

			// Adding annotations
			Element annotationElementRoot = (Element) annotationSets.item(0);
			NodeList annotations = annotationElementRoot
					.getElementsByTagName("Annotation");
			for (int temp = 0; temp < annotations.getLength(); temp++) {
				Node nNode = annotations.item(temp);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;
					String type = eElement.getAttribute("Type");
					String startNode = eElement.getAttribute("StartNode");
					String endNode = eElement.getAttribute("EndNode");
					int startIndex = Integer.parseInt(startNode);
					int endIndex = Integer.parseInt(endNode);
					String content = queryStr(reviewStrs, locs, startNode,
							endNode);
					if (type.equals("review")) {
						ReviewItem item = new ReviewItem();
						item.setStart(startIndex);
						item.setEnd(endIndex);
						item.setContent(content);

						NodeList features = eElement
								.getElementsByTagName("Feature");
						for (int fIndex = 0; fIndex < features.getLength(); fIndex++) {
							Node feature = features.item(fIndex);
							Element fElement = (Element) feature;
							String name = fElement.getElementsByTagName("Name")
									.item(0).getTextContent();
							if (name.equals("reviewType")) {
								String value = fElement
										.getElementsByTagName("Value").item(0)
										.getTextContent();
								item.setType(value);
								getReview(boxReviews, locsInt, startIndex)
										.addReview(item);
							}
						}
					} else if (type.equals("reviewTarget")) {
						ReviewTarget rt = new ReviewTarget();
						rt.setContent(content);
						rt.setStart(startIndex);
						rt.setEnd(endIndex);
						targets.add(rt);
					} else if (type.equals("reviewOperation")) {
						ReviewSolution rs = new ReviewSolution();
						rs.setContent(content);
						rs.setStart(startIndex);
						rs.setEnd(endIndex);
						solutions.add(rs);
					}
				}
			}

			// Assigning targets and solutions to reviews
			for (ReviewTarget rt : targets) {
				getReview(boxReviews, locsInt, rt.getStart()).getReview(
						rt.getStart(), rt.getEnd()).addTarget(rt);
			}
			for (ReviewSolution rs : solutions) {
				getReview(boxReviews, locsInt, rs.getStart()).getReview(
						rs.getStart(), rs.getEnd()).addSolution(rs);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return boxReviews;
	}
	
	public static void main(String[] args) {
		//test
		List<CommentBoxReview> cbrs = readReviews("C:\\Not Backed Up\\data\\reviewAnnotation\\Bananaphone.xlsx.txt.xml");
		for(CommentBoxReview cbr: cbrs) {
			System.out.println("BOX REVIEW:\n"+cbr+"\n");
		}
	}
}
