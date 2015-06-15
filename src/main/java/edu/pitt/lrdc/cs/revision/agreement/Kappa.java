package edu.pitt.lrdc.cs.revision.agreement;

import java.util.ArrayList;

/**
 * Data structure, to record kappa at different levels
 * @author zhangfan
 * @version 1.0
 */
public class Kappa {
	private int kappaLevels = 1;
	public int getKappaLevels() {
		return this.kappaLevels;
	}
	
	private ArrayList<Double> kappas = new ArrayList<Double>();
	
	/**
	 * get the kappa at the specified level
	 * @param level
	 * @return
	 * @throws Exception
	 */
	public double getKappaAt(int level) throws Exception {
		if(level>=kappas.size()||level<0) throw new Exception("No kappa at this level");
		return kappas.get(level);
	}
	/**
	 * Set the kappa at the specified level
	 * @param level
	 * @param kappa
	 * @throws Exception
	 */
	public void setKappa(int level, double kappa) throws Exception {
		if(level>=kappas.size()||level<0) throw new Exception("No kappa at this level");
		this.kappas.set(level, kappa);
	}
}
