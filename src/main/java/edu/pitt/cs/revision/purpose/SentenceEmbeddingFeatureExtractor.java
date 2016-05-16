package edu.pitt.cs.revision.purpose;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.nd4j.linalg.api.ndarray.INDArray;

import edu.pitt.cs.revision.machinelearning.FeatureName;
import edu.pitt.lrdc.cs.revision.model.RevisionDocument;

public class SentenceEmbeddingFeatureExtractor {
	private static SentenceEmbeddingFeatureExtractor instance;
	private Word2Vec w2v;
	private String path = "C:\\Not Backed Up\\word2vec\\GoogleNews-vectors-negative300.bin.gz";
	private int dimension = 300;
	HashSet<String> stopWords = new HashSet<String>();

	public Word2Vec getW2v() {
		return w2v;
	}

	private SentenceEmbeddingFeatureExtractor() throws IOException {
		System.err.println("Loading vectors:");
		long time1 = System.currentTimeMillis();
		File gModel = new File(path);
		w2v = (Word2Vec) WordVectorSerializer.loadGoogleModel(gModel, true);

		List<String> stopWordsList = w2v.getStopWords();
		System.err.println("Stop Words size:" + stopWordsList.size());
		for (String stopWord : stopWordsList) {
			stopWords.add(stopWord);
		}

		long time2 = System.currentTimeMillis();
		System.err.println("Loading word2vec costs:" + (time2 - time1) / 1000
				+ " seconds");
	}

	public static SentenceEmbeddingFeatureExtractor getInstance()
			throws IOException {
		if (instance == null) {
			instance = new SentenceEmbeddingFeatureExtractor();
		}
		return instance;
	}

	public void insertFeature(FeatureName features) {
		//insertDivide(features);
		//insertSubtract(features);
		// insertDivideAVG(features);
		// insertSubtractAVG(features);
	}

	public void insertDivide(FeatureName features) {
		for (int i = 0; i < dimension; i++) {
			features.insertFeature("WORD2VEC_DIVIDE_" + i, Double.TYPE);
		}
	}

	public void insertDivideAVG(FeatureName features) {
		for (int i = 0; i < dimension; i++) {
			features.insertFeature("WORD2VEC_DIVIDE_AVG_" + i, Double.TYPE);
		}
	}

	public void insertCohesion(FeatureName features) {
		features.insertFeature("COHESION_UP", Double.TYPE);
		features.insertFeature("COHESION_DOWN", Double.TYPE);
		features.insertFeature("COHESION_CHANGE_UP", Double.TYPE);
		features.insertFeature("COHESION_CHANGE_DOWN", Double.TYPE);

		features.insertFeature("COHESION_UP_LEX", Double.TYPE);
		features.insertFeature("COHESION_DOWN_LEX", Double.TYPE);
		features.insertFeature("COHESION_CHANGE_UP_LEX", Double.TYPE);
		features.insertFeature("COHESION_CHANGE_DOWN_LEX", Double.TYPE);

		features.insertFeature("COHESION_CHANGE_PARA", Double.TYPE);
		features.insertFeature("COHESION_CHANGE_PARA_LEX", Double.TYPE);
		/*
		 * features.insertFeature("COHESION_ADD_LEX", Double.TYPE);
		 * features.insertFeature("COHESION_DELETE_LEX", Double.TYPE);
		 * features.insertFeature("COHESION_MODIFY_LEX", Double.TYPE);
		 * features.insertFeature("COHESION_ADD_SEM", Double.TYPE);
		 * features.insertFeature("COHESION_DELETE_SEM", Double.TYPE);
		 * features.insertFeature("COHESION_MODIFY_SEM", Double.TYPE);
		 */
		//insertFeature(features);
	}

	public void extractCohesionAddition(FeatureName features,
			Object[] featureVector, RevisionDocument doc,
			ArrayList<Integer> newIndexes, ArrayList<Integer> oldIndexes) {
		int lastIndexOld = -1;
		int nextIndexOld = -1;
		int lastIndexNew = -1;
		int nextIndexNew = -1;

		for (Integer oldIndex : oldIndexes) {
			if (oldIndex < lastIndexOld)
				lastIndexOld = oldIndex;
			if (oldIndex > nextIndexOld)
				nextIndexOld = oldIndex;
		}

		for (Integer newIndex : newIndexes) {
			if (newIndex < lastIndexNew)
				lastIndexNew = newIndex;
			if (newIndex > nextIndexNew)
				nextIndexNew = newIndex;
		}

		int changeParaIndex = features.getIndex("COHESION_CHANGE_PARA");
		int changeLEXIndex = features.getIndex("COHESION_CHANGE_PARA_LEX");

		if (lastIndexOld != -1 && lastIndexNew != -1) {
			ArrayList<ArrayList<String>> paragraphs = findMatchedParagraph(0,
					doc, lastIndexOld, lastIndexNew);
			ArrayList<String> oldParagraph = paragraphs.get(0);
			ArrayList<String> newParagraph = paragraphs.get(1);

			if (oldParagraph == null || oldParagraph.size() < 2) {
				featureVector[changeParaIndex] = 0.0;
				featureVector[changeLEXIndex] = 0.0;
			} else if (newParagraph == null || newParagraph.size() < 2) {
				featureVector[changeParaIndex] = 0.0;
				featureVector[changeLEXIndex] = 0.0;
			} else {
				double paraAllOld = 0;
				double paraLexAllOld = 0;
				for (int i = 0; i < oldParagraph.size() - 1; i++) {
					paraAllOld += calculateSim(oldParagraph.get(i),
							oldParagraph.get(i + 1));
					paraLexAllOld += calculateSimLex(oldParagraph.get(i),
							oldParagraph.get(i + 1));
				}
				double paraAllNew = 0;
				double paraLexAllNew = 0;
				for (int i = 0; i < newParagraph.size() - 1; i++) {
					paraAllNew += calculateSim(newParagraph.get(i),
							newParagraph.get(i + 1));
					paraLexAllNew += calculateSimLex(newParagraph.get(i),
							newParagraph.get(i + 1));
				}

				int oldLength = oldParagraph.size() - 1;
				int newLength = newParagraph.size() - 1;
				paraAllOld = paraAllOld / oldLength;
				paraLexAllOld = paraLexAllOld / oldLength;
				paraAllNew = paraAllNew / newLength;
				paraLexAllNew = paraLexAllNew / newLength;

				if (paraAllOld == 0) {
					featureVector[changeParaIndex] = 0.0;
				} else {
					featureVector[changeParaIndex] = paraAllNew / paraAllOld;
				}

				if (paraLexAllOld == 0) {
					featureVector[changeLEXIndex] = 0.0;
				} else {
					featureVector[changeLEXIndex] = paraLexAllNew
							/ paraLexAllOld;
				}
			}
		} else {
			featureVector[changeParaIndex] = 0.0;
			featureVector[changeLEXIndex] = 0.0;
		}
	}

	public void extractCohesion(FeatureName features, Object[] featureVector,
			RevisionDocument doc, ArrayList<Integer> newIndexes,
			ArrayList<Integer> oldIndexes) {
		double cohesionUpOld = 0;
		double cohesionUpNew = 0;
		double cohesionDownOld = 0;
		double cohesionDownNew = 0;

		double cohesionUpOldLEX = 0;
		double cohesionUpNewLEX = 0;
		double cohesionDownOldLEX = 0;
		double cohesionDownNewLEX = 0;

		int lastIndexOld = -1;
		int nextIndexOld = -1;
		int lastIndexNew = -1;
		int nextIndexNew = -1;

		for (Integer oldIndex : oldIndexes) {
			if (oldIndex < lastIndexOld)
				lastIndexOld = oldIndex;
			if (oldIndex > nextIndexOld)
				nextIndexOld = oldIndex;
		}

		for (Integer newIndex : newIndexes) {
			if (newIndex < lastIndexNew)
				lastIndexNew = newIndex;
			if (newIndex > nextIndexNew)
				nextIndexNew = newIndex;
		}

		String oldSentenceUp = null;
		String oldSentenceDown = null;
		String beforeOldSentence = null;
		String afterOldSentence = null;
		String newSentenceUp = null;
		String newSentenceDown = null;
		String beforeNewSentence = null;
		String afterNewSentence = null;

		if (lastIndexOld == -1) {
			oldSentenceUp = null;
			beforeOldSentence = null;
		} else {
			oldSentenceUp = doc.getOldSentence(lastIndexOld);
			int beforeIndex = lastIndexOld - 1;
			if (beforeIndex <= 0
					|| doc.getParaNoOfOldSentence(beforeIndex) != doc
							.getParaNoOfOldSentence(lastIndexOld)) {
				beforeOldSentence = null;
			} else {
				beforeOldSentence = doc.getOldSentence(beforeIndex);
			}
		}

		if (nextIndexOld == -1) {
			oldSentenceDown = null;
			afterOldSentence = null;
		} else {
			oldSentenceDown = doc.getOldSentence(nextIndexOld);
			int afterIndex = nextIndexOld + 1;
			if (afterIndex > doc.getOldDraftSentences().size()
					|| doc.getParaNoOfOldSentence(afterIndex) != doc
							.getParaNoOfOldSentence(nextIndexOld)) {
				afterOldSentence = null;
			} else {
				afterOldSentence = doc.getOldSentence(nextIndexOld);
			}
		}

		if (lastIndexNew == -1) {
			newSentenceUp = null;
			beforeNewSentence = null;
		} else {
			newSentenceUp = doc.getNewSentence(lastIndexNew);
			int beforeIndex = lastIndexNew - 1;
			if (beforeIndex <= 0
					|| doc.getParaNoOfNewSentence(beforeIndex) != doc
							.getParaNoOfNewSentence(lastIndexNew)) {
				beforeNewSentence = null;
			} else {
				beforeNewSentence = doc.getNewSentence(beforeIndex);
			}
		}

		if (nextIndexNew == -1) {
			newSentenceDown = null;
			afterNewSentence = null;
		} else {
			newSentenceDown = doc.getNewSentence(nextIndexNew);
			int afterIndex = nextIndexNew + 1;
			if (afterIndex > doc.getNewDraftSentences().size()
					|| doc.getParaNoOfNewSentence(afterIndex) != doc
							.getParaNoOfNewSentence(nextIndexNew)) {
				afterNewSentence = null;
			} else {
				afterNewSentence = doc.getNewSentence(nextIndexNew);
			}
		}

		cohesionUpOld = calculateSim(beforeOldSentence, oldSentenceUp);
		cohesionUpNew = calculateSim(beforeNewSentence, newSentenceUp);
		cohesionDownOld = calculateSim(afterOldSentence, oldSentenceDown);
		cohesionDownNew = calculateSim(afterNewSentence, newSentenceDown);

		cohesionUpOldLEX = calculateSimLex(beforeOldSentence, oldSentenceUp);
		cohesionUpNewLEX = calculateSimLex(beforeNewSentence, newSentenceUp);
		cohesionDownOldLEX = calculateSimLex(afterOldSentence, oldSentenceDown);
		cohesionDownNewLEX = calculateSimLex(afterNewSentence, newSentenceDown);

		int indexUp = features.getIndex("COHESION_UP");
		int indexDown = features.getIndex("COHESION_DOWN");
		int indexChangeUp = features.getIndex("COHESION_CHANGE_UP");
		int indexChangeDown = features.getIndex("COHESION_CHANGE_DOWN");

		int indexUpLEX = features.getIndex("COHESION_UP_LEX");
		int indexDownLEX = features.getIndex("COHESION_DOWN_LEX");
		int indexChangeUpLEX = features.getIndex("COHESION_CHANGE_UP_LEX");
		int indexChangeDownLEX = features.getIndex("COHESION_CHANGE_DOWN_LEX");

		/*
		 * int addLexIndex = features.getIndex("COHESION_ADD_LEX"); int
		 * deleteLexIndex = features.getIndex("COHESION_DELETE_LEX"); int
		 * modifyLexIndex = features.getIndex("COHESION_MODIFY_LEX"); int
		 * addSEMIndex = features.getIndex("COHESION_ADD_SEM"); int
		 * deleteSEMIndex = features.getIndex("COHESION_DELETE_SEM"); int
		 * modifySEMIndex = features.getIndex("COHESION_MODIFY_SEM");
		 */
		if (newIndexes == null || newIndexes.size() == 0
				|| (newIndexes.size() == 1 && newIndexes.get(0) == -1)) {
			featureVector[indexUp] = cohesionUpOld;
			featureVector[indexDown] = cohesionDownOld;
			featureVector[indexUpLEX] = cohesionUpOldLEX;
			featureVector[indexDownLEX] = cohesionDownOldLEX;
			featureVector[indexChangeUp] = 0.0;
			featureVector[indexChangeDown] = 0.0;
			featureVector[indexChangeUpLEX] = 0.0;
			featureVector[indexChangeDownLEX] = 0.0;

		} else if (oldIndexes == null || oldIndexes.size() == 0
				|| (oldIndexes.size() == 1 && oldIndexes.get(0) == -1)) {
			featureVector[indexUp] = cohesionUpNew;
			featureVector[indexDown] = cohesionDownNew;
			featureVector[indexUpLEX] = cohesionUpNewLEX;
			featureVector[indexDownLEX] = cohesionDownNewLEX;
			featureVector[indexChangeUp] = 0.0;
			featureVector[indexChangeDown] = 0.0;
			featureVector[indexChangeUpLEX] = 0.0;
			featureVector[indexChangeDownLEX] = 0.0;
		} else {
			featureVector[indexUp] = 0.0;
			featureVector[indexDown] = 0.0;
			featureVector[indexUpLEX] = 0.0;
			featureVector[indexDownLEX] = 0.0;
			featureVector[indexChangeUp] = cohesionUpNew - cohesionUpOld;
			featureVector[indexChangeDown] = cohesionDownNew - cohesionDownOld;
			featureVector[indexChangeUpLEX] = cohesionUpNewLEX
					- cohesionUpOldLEX;
			featureVector[indexChangeDownLEX] = cohesionDownNewLEX
					- cohesionDownOldLEX;
		}

		/*
		 * featureVector[addLexIndex] = 0; featureVector[deleteLexIndex] = 0;
		 * featureVector[modifyLexIndex] = 0; featureVector[addSEMIndex] = 0;
		 * featureVector[deleteSEMIndex] = 0; featureVector[modifySEMIndex] = 0;
		 */
		extractCohesionAddition(features, featureVector, doc, newIndexes,
				oldIndexes);
		//extractFeature(features, featureVector, doc, newIndexes,
		//		oldIndexes);
	}

	public void addSentences(boolean isOld, int paraNo, RevisionDocument doc,
			ArrayList<String> sentences) {
		if (isOld) {
			for (int i = 1; i <= doc.getOldDraftSentences().size(); i++) {
				if (doc.getParaNoOfOldSentence(i) == paraNo) {
					sentences.add(doc.getOldSentence(i));
				}
			}
		} else {
			for (int i = 1; i <= doc.getNewDraftSentences().size(); i++) {
				if (doc.getParaNoOfNewSentence(i) == paraNo) {
					sentences.add(doc.getNewSentence(i));
				}
			}
		}
	}

	public ArrayList<ArrayList<String>> findMatchedParagraph(int windowSize,
			RevisionDocument doc, int oldIndex, int newIndex) {
		ArrayList<ArrayList<String>> sentences = new ArrayList<ArrayList<String>>();
		ArrayList<String> oldSentences = new ArrayList<String>();
		ArrayList<String> newSentences = new ArrayList<String>();
		sentences.add(oldSentences);
		sentences.add(newSentences);

		if (oldIndex != -1) {
			int pIndex = doc.getParaNoOfOldSentence(oldIndex);
			addSentences(true, pIndex, doc, oldSentences);
		} else {
			int pIndex = doc.getParaNoOfNewSentence(newIndex);
			int prevIndex = newIndex - 1;
			int nextIndex = newIndex + 1;
			int tempP = -1;
			while (prevIndex >= 1
					&& prevIndex <= doc.getNewDraftSentences().size()) {
				if (doc.getParaNoOfNewSentence(prevIndex) == pIndex) {
					ArrayList<Integer> alignedOldIndices = doc
							.getOldFromNew(prevIndex);
					if (alignedOldIndices == null
							|| alignedOldIndices.size() == 0
							|| (alignedOldIndices.size() == 1 && alignedOldIndices
									.get(0) == -1)) {
						prevIndex--;
					} else {
						for (Integer tempSentenceIndex : alignedOldIndices) {
							if (tempSentenceIndex != -1) {
								tempP = doc
										.getParaNoOfOldSentence(tempSentenceIndex);
								break;
							}
						}
						break;
					}
				} else {
					break;
				}
			}

			while (nextIndex >= 1
					&& nextIndex <= doc.getNewDraftSentences().size()) {
				if (doc.getParaNoOfNewSentence(nextIndex) == pIndex) {
					ArrayList<Integer> alignedOldIndices = doc
							.getOldFromNew(nextIndex);
					if (alignedOldIndices == null
							|| alignedOldIndices.size() == 0
							|| (alignedOldIndices.size() == 1 && alignedOldIndices
									.get(0) == -1)) {
						nextIndex++;
					} else {
						for (Integer tempSentenceIndex : alignedOldIndices) {
							if (tempSentenceIndex != -1) {
								if (tempP == -1)
									tempP = doc
											.getParaNoOfOldSentence(tempSentenceIndex);
								break;
							}
						}
						break;
					}
				} else {
					break;
				}
			}
			if (tempP != -1) {
				addSentences(true, pIndex, doc, oldSentences);
			}
		}

		if (newIndex != -1) {
			int pIndex = doc.getParaNoOfNewSentence(newIndex);
			addSentences(false, pIndex, doc, newSentences);
		} else {
			int pIndex = doc.getParaNoOfOldSentence(oldIndex);
			int prevIndex = oldIndex - 1;
			int nextIndex = oldIndex + 1;
			int tempP = -1;
			while (prevIndex >= 1
					&& prevIndex <= doc.getOldDraftSentences().size()) {
				if (doc.getParaNoOfOldSentence(prevIndex) == pIndex) {
					ArrayList<Integer> alignedNewIndices = doc
							.getNewFromOld(prevIndex);
					if (alignedNewIndices == null
							|| alignedNewIndices.size() == 0
							|| (alignedNewIndices.size() == 1 && alignedNewIndices
									.get(0) == -1)) {
						prevIndex--;
					} else {
						for (Integer tempSentenceIndex : alignedNewIndices) {
							if (tempSentenceIndex != -1) {
								tempP = doc
										.getParaNoOfNewSentence(tempSentenceIndex);
								break;
							}
						}
						break;
					}
				} else {
					break;
				}
			}

			while (nextIndex >= 1
					&& nextIndex <= doc.getOldDraftSentences().size()) {
				if (doc.getParaNoOfOldSentence(nextIndex) == pIndex) {
					ArrayList<Integer> alignedNewIndices = doc
							.getNewFromOld(nextIndex);
					if (alignedNewIndices == null
							|| alignedNewIndices.size() == 0
							|| (alignedNewIndices.size() == 1 && alignedNewIndices
									.get(0) == -1)) {
						nextIndex++;
					} else {
						for (Integer tempSentenceIndex : alignedNewIndices) {
							if (tempSentenceIndex != -1) {
								if (tempP == -1)
									tempP = doc
											.getParaNoOfNewSentence(tempSentenceIndex);
								break;
							}
						}
						break;
					}
				} else {
					break;
				}
			}
			if (tempP != -1) {
				addSentences(false, pIndex, doc, newSentences);
			}
		}

		return sentences;
	}

	public double calculateAverageCohesion(RevisionDocument doc,
			ArrayList<Integer> newIndexes, ArrayList<Integer> oldIndexes) {

		return 0;
	}

	public double calculateSimLex(String sentence1, String sentence2) {
		if (sentence1 == null || sentence2 == null)
			return 0;
		return OtherAssist.getCosine(sentence1, sentence2);
	}

	public double calculateSimLSA(String sentence1, String sentence2) {
		if (sentence1 == null || sentence2 == null)
			return 0;
		return OtherAssist.getCosine(sentence1, sentence2);
	}

	public double calculateSim(String sentence1, String sentence2) {
		if (sentence1 == null || sentence2 == null)
			return 0;
		String[] oldTokens = sentence1.split(" ");
		String[] newTokens = sentence2.split(" ");
		double[] s1Array = new double[dimension];
		double[] s2Array = new double[dimension];
		for (String oldToken : oldTokens) {
			oldToken = oldToken.trim();
			if (!oldToken.equals(","))
				oldToken = oldToken.replace(",", "");
			oldToken = oldToken.toLowerCase();
			if (!oldToken.equals(".") && !oldToken.equals(",")) {
				//if (w2v.hasWord(oldToken) && !stopWords.contains(oldToken)) {
				if (w2v.hasWord(oldToken)) {
					double[] tmp = w2v.getWordVector(oldToken);
					addArray(s1Array, tmp);
				}
			}
		}

		for (String newToken : newTokens) {
			newToken = newToken.trim();
			newToken = newToken.toLowerCase();
			if (!newToken.equals(","))
				newToken = newToken.replace(",", "");
			if (!newToken.equals(".") && !newToken.equals(",")) {
				//if (w2v.hasWord(newToken) && !stopWords.contains(newToken)) {
				if (w2v.hasWord(newToken)) {
					double[] tmp = w2v.getWordVector(newToken);
					addArray(s2Array, tmp);
				}
			}
		}

		return calculateCosine(s1Array, s2Array);
	}

	public double calculateCosine(double[] s1, double[] s2) {
		double absS1 = 0;
		double absS2 = 0;
		double multi = 0;
		for (int i = 0; i < s1.length; i++) {
			multi += s1[i] * s2[i];
			absS1 += s1[i] * s1[i];
			absS2 += s2[i] * s2[i];
		}
		if(absS1 == 0 || absS2 == 0) return 0;
		return multi / (Math.sqrt(absS1) * Math.sqrt(absS2));
	}

	public void extractDivide(FeatureName features, Object[] featureVector,
			double[] oldFeature, double[] newFeature) {
		double[] results = divideArray(newFeature, oldFeature);
		for (int i = 0; i < dimension; i++) {
			int index = features.getIndex("WORD2VEC_DIVIDE_" + i);
			featureVector[index] = results[i];
		}
	}

	public void extractDivideAVG(FeatureName features, Object[] featureVector,
			double[] oldFeature, double[] newFeature) {
		double[] results = divideArray(newFeature, oldFeature);
		for (int i = 0; i < dimension; i++) {
			int index = features.getIndex("WORD2VEC_DIVIDE_AVG_" + i);
			featureVector[index] = results[i];
		}
	}

	public void insertSubtract(FeatureName features) {
		for (int i = 0; i < dimension; i++) {
			features.insertFeature("WORD2VEC_SUB_" + i, Double.TYPE);
		}
	}

	public void insertSubtractAVG(FeatureName features) {
		for (int i = 0; i < dimension; i++) {
			features.insertFeature("WORD2VEC_SUB_AVG" + i, Double.TYPE);
		}
	}

	public void extractSubtract(FeatureName features, Object[] featureVector,
			double[] oldFeature, double[] newFeature) {
		double[] results = divideArray(newFeature, oldFeature);
		for (int i = 0; i < dimension; i++) {
			int index = features.getIndex("WORD2VEC_SUB_" + i);
			featureVector[index] = results[i];
		}
	}

	public void extractSubtractAVG(FeatureName features,
			Object[] featureVector, double[] oldFeature, double[] newFeature) {
		double[] results = divideArray(newFeature, oldFeature);
		for (int i = 0; i < dimension; i++) {
			int index = features.getIndex("WORD2VEC_SUB_AVG" + i);
			featureVector[index] = results[i];
		}
	}

	public void addArray(double[] src, double[] newArray) {
		for (int i = 0; i < src.length; i++) {
			src[i] += newArray[i];
		}
	}

	public double[] divideArray(double[] src, double[] newArray) {
		double[] result = new double[src.length];
		for (int i = 0; i < src.length; i++) {
			result[i] = src[i] / newArray[i];
		}
		return result;
	}

	public double[] subArray(double[] src, double[] newArray) {
		double[] result = new double[src.length];
		for (int i = 0; i < src.length; i++) {
			result[i] = newArray[i] - src[i];
		}
		return result;
	}

	public void extractFeature(FeatureName features, Object[] featureVector,
			RevisionDocument doc, ArrayList<Integer> newIndexes,
			ArrayList<Integer> oldIndexes) {
		String oldSentence = "";
		String newSentence = "";
		for (Integer oldIndex : oldIndexes) {
			oldSentence += doc.getOldSentence(oldIndex) + " ";
		}
		for (Integer newIndex : newIndexes) {
			newSentence += doc.getNewSentence(newIndex) + " ";
		}

		String[] oldTokens = oldSentence.split(" ");
		String[] newTokens = newSentence.split(" ");
		double[] oldFeatures = new double[dimension];
		double[] newFeatures = new double[dimension];

		double[] oldFeaturesAvg = new double[dimension];
		double[] newFeaturesAvg = new double[dimension];
		int oldN = 0;
		int newN = 0;

		for (String oldToken : oldTokens) {
			oldToken = oldToken.trim();
			oldToken = oldToken.toLowerCase();
			if (!oldToken.equals(".") && !oldToken.equals(",")) {
				if (w2v.hasWord(oldToken) && !stopWords.contains(oldToken)) {
					double[] tmp = w2v.getWordVector(oldToken);
					addArray(oldFeatures, tmp);
					oldN++;
				}
			}
		}
		for (String newToken : newTokens) {
			newToken = newToken.trim();
			newToken = newToken.toLowerCase();
			if (!newToken.equals(".") && !newToken.equals(",")) {
				if (w2v.hasWord(newToken) && !stopWords.contains(newToken)) {
					double[] tmp = w2v.getWordVector(newToken);
					addArray(newFeatures, tmp);
					newN++;
				}
			}
		}

		for (int i = 0; i < dimension; i++) {
			oldFeaturesAvg[i] = oldFeatures[i] / oldN;
		}
		for (int i = 0; i < dimension; i++) {
			newFeaturesAvg[i] = newFeatures[i] / newN;
		}
		//extractDivide(features, featureVector, oldFeatures, newFeatures);
		//extractSubtract(features, featureVector, oldFeatures, newFeatures);
		// extractDivideAVG(features, featureVector, oldFeaturesAvg,
		// newFeaturesAvg);
		// extractSubtractAVG(features, featureVector, oldFeaturesAvg,
		// newFeaturesAvg);
	}

	public static void main(String[] args) throws IOException {
		String sentence1 = "I like your examples, however, the thesis of each paragraph is not very clear. ";
		String sentence2 = "Some of them appear in the top of the paragraph while others are in the bottom.";
		System.out.println("Value:"+SentenceEmbeddingFeatureExtractor.getInstance().calculateSim(sentence1, sentence2));
	}
}
