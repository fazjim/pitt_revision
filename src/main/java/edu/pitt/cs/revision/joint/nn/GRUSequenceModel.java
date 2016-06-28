package edu.pitt.cs.revision.joint.nn;
import java.util.*;

import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.BackpropType;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.Updater;
import org.deeplearning4j.nn.conf.layers.GRU;
import org.deeplearning4j.nn.conf.layers.RnnOutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.nd4j.linalg.lossfunctions.LossFunctions.LossFunction;

import edu.pitt.cs.revision.joint.EditSequence;
import edu.pitt.cs.revision.machinelearning.SequenceItem;

public class GRUSequenceModel {
	public MultiLayerConfiguration configureNetwork(int layer1InputPara, int gruLayerSize, int tbpttLength, int nOut) {
		MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
		.optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT).iterations(1)
		.learningRate(0.1)
		.rmsDecay(0.95)
		.seed(12345)
		.regularization(true)
		.l2(0.001)
        .weightInit(WeightInit.XAVIER)
        .updater(Updater.RMSPROP)
		.list()
		.layer(0, new GRU.Builder().nIn(layer1InputPara).nOut(gruLayerSize)
				.activation("tanh").build())
		.layer(1, new RnnOutputLayer.Builder(LossFunction.MCXENT).activation("softmax")        //MCXENT + softmax for classification
				.nIn(gruLayerSize).nOut(nOut).build())
        .backpropType(BackpropType.TruncatedBPTT).tBPTTForwardLength(tbpttLength).tBPTTBackwardLength(tbpttLength)
		.pretrain(false).backprop(true)
		.build();
		return conf;
	}
	
	public MultiLayerNetwork trainModel(List<List<SequenceItem>> sequenceDocs, MultiLayerConfiguration conf) {
		 MultiLayerNetwork net = new MultiLayerNetwork(conf);
	     net.init();
	     net.setListeners(new ScoreIterationListener(1));
	     
	     return net;	
	}
	
	
}
