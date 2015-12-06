package edu.pitt.cs.revision.purpose;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

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

	public Word2Vec getW2v() {
		return w2v;
	}

	private SentenceEmbeddingFeatureExtractor() throws IOException {
		System.err.println("Loading vectors:");
		long time1 = System.currentTimeMillis();
		File gModel = new File(path);
		w2v = (Word2Vec) WordVectorSerializer.loadGoogleModel(gModel, true);

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
		insertSubtract(features);
	}

	public void insertDivide(FeatureName features) {
		for (int i = 0; i < dimension; i++) {
			features.insertFeature("WORD2VEC_DIVIDE_" + i, Double.TYPE);
		}
	}

	public void extractDivide(FeatureName features, Object[] featureVector,
			double[] oldFeature, double[] newFeature) {
		double[] results = divideArray(newFeature, oldFeature);
		for (int i = 0; i < dimension; i++) {
			int index = features.getIndex("WORD2VEC_DIVIDE_" + i);
			featureVector[index] = results[i];
		}
	}

	public void insertSubtract(FeatureName features) {
		for (int i = 0; i < dimension; i++) {
			features.insertFeature("WORD2VEC_SUB_" + i, Double.TYPE);
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

		for (String oldToken : oldTokens) {
			oldToken = oldToken.trim();
			oldToken = oldToken.toLowerCase();
			if (!oldToken.equals(".") && !oldToken.equals(",")) {
				if (w2v.hasWord(oldToken)) {
					double[] tmp = w2v.getWordVector(oldToken);
					addArray(oldFeatures, tmp);
				}
			}
		}
		for (String newToken : newTokens) {
			newToken = newToken.trim();
			newToken = newToken.toLowerCase();
			if (!newToken.equals(".") && !newToken.equals(",")) {
				if (w2v.hasWord(newToken)) {
					double[] tmp = w2v.getWordVector(newToken);
					addArray(newFeatures, tmp);
				}
			}
		}
		//extractDivide(features, featureVector, oldFeatures, newFeatures);
		extractSubtract(features, featureVector, oldFeatures, newFeatures);
	}

	public static void main(String[] args) throws IOException {
		String word = "Hello";
	}
}
