package edu.pitt.lrdc.cs.revision.evaluate;

import java.util.ArrayList;

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
	
	public double prec2;
	public double recall2;
	public double f2;
	
	public double prec3;
	public double recall3;
	public double f3;
	
	public double prec4;
	public double recall4;
	public double f4;
	
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



	public double getPrec2() {
		return prec2;
	}



	public void setPrec2(double prec2) {
		this.prec2 = prec2;
	}



	public double getRecall2() {
		return recall2;
	}



	public void setRecall2(double recall2) {
		this.recall2 = recall2;
	}



	public double getF2() {
		return f2;
	}



	public void setF2(double f2) {
		this.f2 = f2;
	}



	public double getPrec3() {
		return prec3;
	}



	public void setPrec3(double prec3) {
		this.prec3 = prec3;
	}



	public double getRecall3() {
		return recall3;
	}



	public void setRecall3(double recall3) {
		this.recall3 = recall3;
	}



	public double getF3() {
		return f3;
	}



	public void setF3(double f3) {
		this.f3 = f3;
	}



	public double getPrec4() {
		return prec4;
	}



	public void setPrec4(double prec4) {
		this.prec4 = prec4;
	}



	public double getRecall4() {
		return recall4;
	}



	public void setRecall4(double recall4) {
		this.recall4 = recall4;
	}



	public double getF4() {
		return f4;
	}



	public void setF4(double f4) {
		this.f4 = f4;
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



	public void fromEvaluation(Evaluation eval, int classNum) {
		double precisionAll = 0;
		double recallAll = 0;
		double fMeasureAll = 0;
		
		for(int i = 0;i<classNum;i++) {
			precisionAll += eval.precision(i);
			recallAll += eval.recall(i);
			fMeasureAll += eval.fMeasure(i);
		}
		prec0 = eval.precision(0);
		recall0 = eval.recall(0);
		f0 = eval.fMeasure(0);
		
		prec1 = eval.precision(1);
		recall1 = eval.recall(1);
		f1 = eval.fMeasure(1);
		
		if(classNum > 2) {
			prec2 = eval.precision(2);
			recall2 = eval.recall(2);
			f2 = eval.fMeasure(2);
			
			prec3 = eval.precision(3);
			recall3 = eval.recall(3);
			f3 = eval.fMeasure(3);
			
			prec4 = eval.precision(4);
			recall4 = eval.recall(4);
			f4 = eval.fMeasure(4);
		}
		
		kappa = eval.kappa();
		
		unweightedAvgPrec = precisionAll/classNum;
		unweightedAvgRecall = recallAll/classNum;
		unweightedAvgFvalue = fMeasureAll/classNum;
		//unweightedAvgFvalue = eval.unweightedMacroFmeasure();
		accuracy = eval.pctCorrect();
		
	}
}
