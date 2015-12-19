package edu.pitt.lrdc.cs.revision.evaluate;
import java.util.ArrayList;
import java.util.Hashtable;
public class ConfusionMatrix {
	private ArrayList<String> attributes;
	private Hashtable<String, Integer> attributeIndex;
	private String name;
	private int[][] attrTable;
	
	public ConfusionMatrix(String name) {
		this();
		this.name = name;
	}
	
	public void merge(ConfusionMatrix matrix) {
		int[][] attrTableToMerge = matrix.getAttrTable();
		for(int i = 0;i<attributes.size();i++) {
			for(int j = 0;j<attributes.size();j++) {
				attrTable[i][j] += attrTableToMerge[i][j];
			}
		}
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public ArrayList<String> getAttrs() {
		return attributes;
	}
	
	public int[][] getAttrTable() {
		return attrTable;
	}

	public ConfusionMatrix() {
		attributes = new ArrayList<String>();
		attributeIndex = new Hashtable<String, Integer>();
	}
	
	public void initMatrix() {
		attrTable = new int[attributes.size()][attributes.size()];
	}
	
	public void addAttribute(String name) {
		attributeIndex.put(name, attributes.size());
		attributes.add(name);
	}
	
	public void count(String predictedValue, String realValue) {
		//System.out.println(predictedValue);
		int predictedIndex = attributeIndex.get(predictedValue);
		int realIndex = attributeIndex.get(realValue);
		attrTable[predictedIndex][realIndex]++;
	}
	
	public double getOverallKappa() {
		double[] sumRows = new double[attrTable.length];
		double[] sumColumns = new double[attrTable.length];
		double sumOfWeights = 0;
		for (int i = 0; i < attrTable.length; i++) {
			for (int j = 0; j < attrTable.length; j++) {
				sumRows[i] += attrTable[i][j];
				sumColumns[j] += attrTable[i][j];
				sumOfWeights += attrTable[i][j];
			}
		}
		double correct = 0, chanceAgreement = 0;
		for (int i = 0; i < attrTable.length; i++) {
			chanceAgreement += (sumRows[i] * sumColumns[i]);
			correct += attrTable[i][i];
		}
		chanceAgreement /= (sumOfWeights * sumOfWeights);
		correct /= sumOfWeights;

		if (chanceAgreement < 1) {
			return (correct - chanceAgreement) / (1 - chanceAgreement);
		} else {
			return 1;
		}
	}
	
	public double getOverallAccuracy() {	
		double correct = 0, all = 0;
		for (int i = 0; i < attrTable.length; i++) {
			correct += attrTable[i][i];
			for(int j = 0;j<attrTable.length;j++) {
				all += attrTable[i][j];
			}
		}
		return correct/all;
	}
	
	public double getIndivdiualKappa(String name) {
		int index = attributeIndex.get(name);
		int[][] individualMatrix = new int[2][2]; 
		for(int i = 0;i<attributeIndex.size();i++) {
			for(int j = 0;j<attributeIndex.size();j++) {
				if(i == index && j == index) {
					individualMatrix[1][1] += attrTable[i][j];
				} else if(i == index && j != index) {
					individualMatrix[1][0] += attrTable[i][j];
				} else if(i != index && j == index) {
					individualMatrix[0][1] += attrTable[i][j];
				} else {
					individualMatrix[0][0] += attrTable[i][j];
				}
			}
		}
		double[] sumRows = new double[individualMatrix.length];
		double[] sumColumns = new double[individualMatrix.length];
		double sumOfWeights = 0;
		for (int i = 0; i < individualMatrix.length; i++) {
			for (int j = 0; j < individualMatrix.length; j++) {
				sumRows[i] += individualMatrix[i][j];
				sumColumns[j] += individualMatrix[i][j];
				sumOfWeights += individualMatrix[i][j];
			}
		}
		double correct = 0, chanceAgreement = 0;
		for (int i = 0; i < individualMatrix.length; i++) {
			chanceAgreement += (sumRows[i] * sumColumns[i]);
			correct += individualMatrix[i][i];
		}
		chanceAgreement /= (sumOfWeights * sumOfWeights);
		correct /= sumOfWeights;

		if (chanceAgreement < 1) {
			return (correct - chanceAgreement) / (1 - chanceAgreement);
		} else {
			return 1;
		}
	}
	
	public double getBinaryPrec(String name, boolean isOne) {
		int index = attributeIndex.get(name);
		if(isOne) {
			int tp = attrTable[index][index];
			int all = 0;
			for(int i = 0;i<attributeIndex.size();i++) {
				all += attrTable[index][i];
			}
			if(all == 0) return 1;
			return tp*1.0/all;
		} else {
			int tp = 0;
			int all = 0;
			for(int i = 0;i<attributeIndex.size();i++) {
				for(int j = 0;j<attributeIndex.size();j++) {
					if(i!=index) {
						all+=attrTable[i][j];
						if(j!=index) {
							tp+=attrTable[i][j];
						}
					}
				}
			}
			if(all==0) return -1;
			return tp*1.0/all;
		}
		
	}
	
	public double getBinaryRecall(String name, boolean isOne) {
		int index = attributeIndex.get(name);
		if(isOne) {
			int tp = attrTable[index][index];
			int all = 0;
			for(int i = 0;i<attributeIndex.size();i++) {
				all += attrTable[i][index];
			}
			if(all == 0) return -1;
			return tp*1.0/all;
		} else {
			int tp = 0;
			int all = 0;
			for(int i = 0;i<attributeIndex.size();i++) {
				for(int j = 0;j<attributeIndex.size();j++) {
					if(i!=index) {
						all+=attrTable[j][i];
						if(j!=index) {
							tp+=attrTable[j][i];
						}
					}
				}
			}
			if(all == 0) return -1;
			return tp*1.0/all;
		}
	}
	
	
	public double getUnWeightedFMeasure(String name) {
		double p0 = getBinaryPrec(name, true);
		double r0 = getBinaryRecall(name, true);
		double p1 = getBinaryPrec(name, false);
		double r1 = getBinaryRecall(name, false);
		if(p0<0||r0<0||p1<0||r1<0) return -1;
		double f0 = 2*p0*r0/(p0 + r0);
		double f1 = 2*p1*r1/(p1 + r1);
		return (f0+f1)/2;
	}
	
	public double getPrec(String name) {
		int index = attributeIndex.get(name);
		int tp = attrTable[index][index];
		int all = 0;
		for(int i = 0;i<attributeIndex.size();i++) {
			all += attrTable[index][i];
		}
		if(all == 0) return -1;
		return tp*1.0/all;
	}
	
	public double getRecall(String name) {
		int index = attributeIndex.get(name);
		int tp = attrTable[index][index];
		int all = 0;
		for(int i = 0;i<attributeIndex.size();i++) {
			all += attrTable[i][index];
		}
		if(all == 0) return -1;
		return tp* 1.0/all;
	}
	
	public double getFMeasure(String name) {
		double prec = getPrec(name);
		double recall = getRecall(name);
		if(prec<0||recall<0) return -1;
		if(prec == 0 && recall == 0) return -1;
		return prec*recall*2/(prec + recall);
	}
	
	public String toString() {
		String tableStr = "";
		//header
		tableStr += "Real/Predict";
		for(int i = 0;i<attributes.size();i++)  {
			tableStr += "\t" + attributes.get(i);
		}
		tableStr += "\n";
		
		//Content
		for(int i = 0;i<attributes.size();i++) {
			tableStr += attributes.get(i);
			for(int j = 0;j<attributes.size();j++) {
				tableStr += "\t"+ attrTable[j][i];
			}
			tableStr += "\n";
		}
		
		return tableStr;
	}
}
