package edu.pitt.cs.revision.machinelearning;

import cc.mallet.types.Alphabet;
import cc.mallet.types.FeatureSequence;
import cc.mallet.types.InstanceList;

/**
 * Assist for creating instance list and invoking functions in mallet
 * 
 * The current version does not support stop-word trimming yet (given that stop
 * words might be an important feature in revision classification task
 * 
 * @author zhangfan
 * @version 1.0
 *
 */
public class MalletAssist {
	/**
	 * Add an instance to instanceList
	 * 
	 * Currently only supports unigram
	 * 
	 * @param list
	 * @param featureTable
	 * @param features
	 * @param cat
	 * @param ID
	 */
	
	private Alphabet dataAlphabet;
	private Alphabet taragetAlphabet;
	
	/**
	 * The alphabets will increase from each instance
	 */
	public MalletAssist() {
		this.dataAlphabet = new Alphabet();
		this.taragetAlphabet = new Alphabet();
	}
	
	public void addInstance(InstanceList list, FeatureName featureTable,
			Object[] features, String cat, String ID, boolean usingNgram) {

	}
	
	
	
	/**
	 * How mallet works:
	 * A pipe contains a dataAlphabet
	 * A feature sequence adds the features
	 * 
	 * For this one will be a 0-1 unigram (no word count)
	 *
	 * @param sentence
	 * @return
	 */
	public FeatureSequence getUnigramSequence(String sentence) {
		sentence = sentence.toLowerCase();
		String[] words = sentence.split(" ");
		FeatureSequence fs = new FeatureSequence(dataAlphabet,words.length);
		for(String word: words) {
			fs.add(word);
		}
		return fs;
	}

}
