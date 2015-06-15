package edu.pitt.lrdc.cs.revision.statistics;

import java.util.ArrayList;

import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import org.apache.commons.math3.stat.inference.ChiSquareTest;
import org.apache.commons.math3.stat.inference.OneWayAnova;
import org.apache.commons.math3.stat.inference.TTest;

/**
 * Packs up the calculation for statistical analysis
 * @author zhangfan
 *
 */
public class ApacheStatAssist {
	private static TTest ttester = new TTest();;
	private static PearsonsCorrelation pctester = new PearsonsCorrelation();
	private static ChiSquareTest chitester = new ChiSquareTest();
	private static OneWayAnova owa = new OneWayAnova();
	
	
	public ApacheStatAssist() {
	}
	
	public static double pairedTtest(double[] arr1, double[] arr2) {
		return ttester.pairedTTest(arr1, arr2);
	}
	
	public static double onewayAnova(ArrayList<double[]> lists) {
		//return owa.anovaFValue(lists);
		return owa.anovaPValue(lists);
	}
	
	public static double chiSquareTest(double[] arr1, double[] arr2) {
		long[] arrl1 = new long[arr1.length];
		long[] arrl2 = new long[arr2.length];
		for(int i = 0;i<arr1.length;i++) {
			arrl1[i] = (long)arr1[i];
		}
		for(int i = 0;i<arr2.length;i++) {
			arrl2[i] = (long)arr2[i];
		}
		//chitester.
		return chitester.chiSquareTestDataSetsComparison(arrl1,arrl2);
	}
	
	public static boolean isCorrleatedChi(double[] arr1, double[] arr2, double alpha) {
		long[] arrl1 = new long[arr1.length];
		long[] arrl2 = new long[arr2.length];
		for(int i = 0;i<arr1.length;i++) {
			arrl1[i] = (long)arr1[i];
		}
		for(int i = 0;i<arr2.length;i++) {
			arrl2[i] = (long)arr2[i];
		}
		return chitester.chiSquareTestDataSetsComparison(arrl1, arrl2, alpha);
	}
	
	public static double unpairedTTest(double[] arr1, double[] arr2) {
		return ttester.tTest(arr1, arr2);
	}
	
	public static double pearsonCorrelation(double[] arr1, double[] arr2) {
		return pctester.correlation(arr1, arr2);
	}
	
	public static double getCorrSignificance(double val, int size) {
		return val*Math.sqrt(size-2)/Math.sqrt(1-Math.pow(val, 2));
	}
}
