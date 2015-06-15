package edu.pitt.lrdc.cs.revision.evaluate;

import weka.classifiers.Evaluation;

/**
 * A data structure storing Prec, Recall, F, kappa, avg Prec, avg Recall, accuracy
 * @author zhangfan
 *
 */
public class ResultInfo {
	public double prec0;
	public double recall0;
	public double f0;
	
	
	public double prec1;
	public double recall1;
	public double f1;
	
	public double kappa;
	public double unweightedAvgPrec;
	public double unweightedAvgRecall;
	public double unweightedAvgFvalue;
	public double accuracy;
	
	public double weightedAvgPrec;
	public double weightedAvgRecall;
	
	
	
	public double getWeightedAvgPrec() {
		return weightedAvgPrec;
	}



	public void setWeightedAvgPrec(double weightedAvgPrec) {
		this.weightedAvgPrec = weightedAvgPrec;
	}



	public double getWeightedAvgRecall() {
		return weightedAvgRecall;
	}



	public void setWeightedAvgRecall(double weightedAvgRecall) {
		this.weightedAvgRecall = weightedAvgRecall;
	}



	public double getPrec0() {
		return prec0;
	}



	public void setPrec0(double prec0) {
		this.prec0 = prec0;
	}



	public double getRecall0() {
		return recall0;
	}



	public void setRecall0(double recall0) {
		this.recall0 = recall0;
	}



	public double getF0() {
		return f0;
	}



	public void setF0(double f0) {
		this.f0 = f0;
	}



	public double getPrec1() {
		return prec1;
	}



	public void setPrec1(double prec1) {
		this.prec1 = prec1;
	}



	public double getRecall1() {
		return recall1;
	}



	public void setRecall1(double recall1) {
		this.recall1 = recall1;
	}



	public double getF1() {
		return f1;
	}



	public void setF1(double f1) {
		this.f1 = f1;
	}



	public double getKappa() {
		return kappa;
	}



	public void setKappa(double kappa) {
		this.kappa = kappa;
	}



	public double getUnweightedAvgPrec() {
		return unweightedAvgPrec;
	}



	public void setUnweightedAvgPrec(double unweightedAvgPrec) {
		this.unweightedAvgPrec = unweightedAvgPrec;
	}



	public double getUnweightedAvgRecall() {
		return unweightedAvgRecall;
	}



	public void setUnweightedAvgRecall(double unweightedAvgRecall) {
		this.unweightedAvgRecall = unweightedAvgRecall;
	}



	public double getUnweightedAvgFvalue() {
		return unweightedAvgFvalue;
	}



	public void setUnweightedAvgFvalue(double unweightedAvgFvalue) {
		this.unweightedAvgFvalue = unweightedAvgFvalue;
	}



	public double getAccuracy() {
		return accuracy;
	}



	public void setAccuracy(double accuracy) {
		this.accuracy = accuracy;
	}



	public void fromEvaluation(Evaluation eval) {
		prec0 = eval.precision(0);
		recall0 = eval.recall(0);
		f0 = eval.fMeasure(0);
		
		prec1 = eval.precision(1);
		recall1 = eval.recall(1);
		f1 = eval.fMeasure(1);
		
		kappa = eval.kappa();
		unweightedAvgPrec = (prec0+prec1)/2;
		unweightedAvgRecall = (recall0+recall1)/2;
		unweightedAvgFvalue = (f0+f1)/2;
		//unweightedAvgFvalue = eval.unweightedMacroFmeasure();
		accuracy = eval.pctCorrect();
		
	}
}
