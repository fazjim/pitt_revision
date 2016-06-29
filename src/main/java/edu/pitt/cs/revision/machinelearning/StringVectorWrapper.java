package edu.pitt.cs.revision.machinelearning;

import java.util.ArrayList;
import java.util.List;

import weka.core.Attribute;
import weka.core.Instances;
import weka.core.tokenizers.NGramTokenizer;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.StringToWordVector;

public class StringVectorWrapper {

	public InstancesPair applyStringVectorFilter(Instances data,
			String attribute, Instances otherData) throws Exception {
		StringToWordVector filter_ngram = new StringToWordVector();
		Attribute ngram = data.attribute(attribute);
		if (ngram != null) {
			int attributes[] = new int[1];
			attributes[0] = ngram.index();

			filter_ngram.setAttributeIndicesArray(attributes);
			filter_ngram.setUseStoplist(false);
			filter_ngram.setLowerCaseTokens(false);
			filter_ngram.setAttributeNamePrefix(attribute + "_");
			NGramTokenizer tokenizer_pos = new NGramTokenizer();
			tokenizer_pos.setOptions(weka.core.Utils
					.splitOptions("-min 1 -max 1"));
			filter_ngram.setTokenizer(tokenizer_pos);

			filter_ngram.setInputFormat(data);
			data = Filter.useFilter(data, filter_ngram);

			if (otherData != null)
				otherData = Filter.useFilter(otherData, filter_ngram);
		}

		return new InstancesPair(data, otherData);
	}

	public InstancesPair applyStringVectorFilter(Instances data,
			String attribute, String attribute2, Instances otherData)
			throws Exception {
		StringToWordVector filter_ngram = new StringToWordVector();

		Attribute ngram = data.attribute(attribute);
		if (ngram != null) {
			int attributes[] = new int[1];
			attributes[0] = ngram.index();

			filter_ngram.setAttributeIndicesArray(attributes);
			filter_ngram.setUseStoplist(false);
			filter_ngram.setLowerCaseTokens(true);
			filter_ngram.setAttributeNamePrefix(attribute + "_");
			NGramTokenizer tokenizer_pos = new NGramTokenizer();
			tokenizer_pos.setOptions(weka.core.Utils
					.splitOptions("-min 1 -max 1"));
			filter_ngram.setTokenizer(tokenizer_pos);

			filter_ngram.setInputFormat(data);
			data = Filter.useFilter(data, filter_ngram);

			if (otherData != null)
				otherData = Filter.useFilter(otherData, filter_ngram);
		}

		Attribute ngram2 = data.attribute(attribute2);
		if (ngram2 != null) {
			int attributes[] = new int[1];
			attributes[0] = ngram.index();

			filter_ngram.setAttributeIndicesArray(attributes);
			filter_ngram.setUseStoplist(false);
			filter_ngram.setLowerCaseTokens(false);
			filter_ngram.setAttributeNamePrefix(attribute2 + "_");
			NGramTokenizer tokenizer_pos = new NGramTokenizer();
			tokenizer_pos.setOptions(weka.core.Utils
					.splitOptions("-min 1 -max 1"));
			filter_ngram.setTokenizer(tokenizer_pos);

			filter_ngram.setInputFormat(data);
			data = Filter.useFilter(data, filter_ngram);

			if (otherData != null)
				otherData = Filter.useFilter(otherData, filter_ngram);
		}

		return new InstancesPair(data, otherData);
	}

	public InstancesPair applyStringVectorFilter(Instances data,
			List<String> attributeStrs, Instances otherData) throws Exception {
		StringToWordVector filter_ngram = new StringToWordVector();

		for (String attribute : attributeStrs) {
			Attribute ngram = data.attribute(attribute);
			if (ngram != null) {
				int attributes[] = new int[1];
				attributes[0] = ngram.index();

				filter_ngram.setAttributeIndicesArray(attributes);
				filter_ngram.setUseStoplist(false);
				filter_ngram.setLowerCaseTokens(true);
				filter_ngram.setAttributeNamePrefix(attribute + "=");
				NGramTokenizer tokenizer_pos = new NGramTokenizer();
				tokenizer_pos.setOptions(weka.core.Utils
						.splitOptions("-min 1 -max 1"));
				filter_ngram.setTokenizer(tokenizer_pos);

				filter_ngram.setInputFormat(data);
				data = Filter.useFilter(data, filter_ngram);

				if (otherData != null)
					otherData = Filter.useFilter(otherData, filter_ngram);
			}
		}

		return new InstancesPair(data, otherData);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
