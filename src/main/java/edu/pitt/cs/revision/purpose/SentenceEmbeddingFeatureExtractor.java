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
	
	public Word2Vec getW2v() {
		return w2v;
	}
	
	private SentenceEmbeddingFeatureExtractor() throws IOException {
		System.err.println("Loading vectors:");
		long time1 = System.currentTimeMillis();
		File gModel = new File(path);
		w2v = (Word2Vec) WordVectorSerializer.loadGoogleModel(gModel, true);
		long time2 = System.currentTimeMillis();
		System.err.println("Loading word2vec costs:"+ (time2-time1)/1000+" seconds");
	}
	
	public static SentenceEmbeddingFeatureExtractor getInstance() throws IOException {
		if(instance == null) {
			instance = new SentenceEmbeddingFeatureExtractor();
		} 
		return instance;
	}
	
	public void insertFeature(FeatureName features) {
		
	}

	public void extractFeature(Object[] featureVector,
			RevisionDocument doc, ArrayList<Integer> newIndexes,
			ArrayList<Integer> oldIndexes) {
		
	}
	
	public static void main(String[] args) throws IOException {
		String word = "Hello";
	}
}
