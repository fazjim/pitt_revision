package edu.pitt.cs.revision.machinelearning;

import cc.mallet.pipe.CharSequence2TokenSequence;
import cc.mallet.pipe.CharSequenceLowercase;
import cc.mallet.pipe.Input2CharSequence;
import cc.mallet.pipe.Pipe;
import cc.mallet.pipe.SerialPipes;
import cc.mallet.pipe.TokenSequence2FeatureSequence;
import cc.mallet.pipe.TokenSequenceLowercase;
import cc.mallet.pipe.TokenSequenceRemoveStopwords;
import cc.mallet.pipe.iterator.CsvIterator;
import cc.mallet.pipe.iterator.FileIterator;
import cc.mallet.topics.ParallelTopicModel;
import cc.mallet.types.Alphabet;
import cc.mallet.types.FeatureSequence;
import cc.mallet.types.IDSorter;
import cc.mallet.types.InstanceList;
import cc.mallet.types.LabelSequence;

import java.io.File;
import java.io.Reader;
import java.util.*;
import java.util.regex.Pattern;
import java.io.*;

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

class TxtFilter implements FileFilter {

    /** Test whether the string representation of the file 
     *   ends with the correct extension. Note that {@ref FileIterator}
     *   will only call this filter if the file is not a directory,
     *   so we do not need to test that it is a file.
     */
    public boolean accept(File file) {
        return file.toString().endsWith(".txt");
    }
}

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
	private static String stopwordPath = "C:\\Not Backed Up\\libraries\\mallet-stoplists";
	
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
	
	public static void main(String[] args) throws Exception {
		String filePath = "C:\\Not Backed Up\\discourse_parse_results\\litman_corpus\\Braverman\\allTxt";
		trainTopicModel(filePath, 10, 50);
	}
	
	
	
    

  

  
	
	 public static void trainTopicModel(String filePath, int numTopics, int topWords) throws Exception {

		 	File[] directories = new File[1];
		 	directories[0] = new File(filePath);
	        // Begin by importing documents from text to feature sequences
	        ArrayList<Pipe> pipeList = new ArrayList<Pipe>();

	        // Pipes: lowercase, tokenize, remove stopwords, map to features
	        pipeList.add(new Input2CharSequence("UTF-8"));
	        //pipeList.add( new CharSequenceLowercase() );
	        pipeList.add( new CharSequence2TokenSequence(Pattern.compile("\\p{L}[\\p{L}\\p{P}]+\\p{L}")) );
	        pipeList.add(new TokenSequenceLowercase());
	        pipeList.add( new TokenSequenceRemoveStopwords(new File(stopwordPath+"/en.txt"), "UTF-8", false, false, false) );
	        pipeList.add( new TokenSequence2FeatureSequence() );
	        
	        FileIterator fileIterator =
	                new FileIterator(directories,
	                                 new TxtFilter(),
	                                 FileIterator.LAST_DIRECTORY);

	            // Construct a new instance list, passing it the pipe
	            //  we want to use to process instances.
	            InstanceList instances = new InstanceList(new SerialPipes(pipeList));

	            // Now process each instance provided by the iterator.
	            instances.addThruPipe(fileIterator);

	       /* Reader fileReader = new InputStreamReader(new FileInputStream(new File(filePath)), "UTF-8");
	        instances.addThruPipe(new CsvIterator (fileReader, Pattern.compile("^(\\S*)[\\s,]*(\\S*)[\\s,]*(.*)$"),
	                                               3, 2, 1)); // data, label, name fields*/

	        // Create a model with 100 topics, alpha_t = 0.01, beta_w = 0.01
	        //  Note that the first parameter is passed as the sum over topics, while
	        //  the second is the parameter for a single dimension of the Dirichlet prior
	        ParallelTopicModel model = new ParallelTopicModel(numTopics, 1.0, 0.01);

	        model.addInstances(instances);

	        // Use two parallel samplers, which each look at one half the corpus and combine
	        //  statistics after every iteration.
	        model.setNumThreads(8);

	        // Run the model for 50 iterations and stop (this is for testing only, 
	        //  for real applications, use 1000 to 2000 iterations)
	        model.setNumIterations(1000);
	        model.estimate();

	        // Show the words and topics in the first instance

	        // The data alphabet maps word IDs to strings
	        Alphabet dataAlphabet = instances.getDataAlphabet();
	        
	        FeatureSequence tokens = (FeatureSequence) model.getData().get(0).instance.getData();
	        LabelSequence topics = model.getData().get(0).topicSequence;
	        
	        Formatter out = new Formatter(new StringBuilder(), Locale.US);
	        for (int position = 0; position < tokens.getLength(); position++) {
	            out.format("%s-%d ", dataAlphabet.lookupObject(tokens.getIndexAtPosition(position)), topics.getIndexAtPosition(position));
	        }
	        System.out.println(out);
	        
	        // Estimate the topic distribution of the first instance, 
	        //  given the current Gibbs state.
	        double[] topicDistribution = model.getTopicProbabilities(0);

	        // Get an array of sorted sets of word ID/count pairs
	        ArrayList<TreeSet<IDSorter>> topicSortedWords = model.getSortedWords();
	        
	        // Show top 5 words in topics with proportions for the first document
	        for (int topic = 0; topic < numTopics; topic++) {
	            Iterator<IDSorter> iterator = topicSortedWords.get(topic).iterator();
	            
	            out = new Formatter(new StringBuilder(), Locale.US);
	            out.format("%d\t%.3f\t", topic, topicDistribution[topic]);
	            int rank = 0;
	            while (iterator.hasNext() && rank < topWords) {
	                IDSorter idCountPair = iterator.next();
	                out.format("%s (%.0f) ", dataAlphabet.lookupObject(idCountPair.getID()), idCountPair.getWeight());
	                rank++;
	            }
	            System.out.println(out);
	        }
	        
	    }


}
