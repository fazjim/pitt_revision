package edu.pitt.lrdc.cs.revision.agreement;

import edu.pitt.lrdc.cs.revision.io.RevisionDocumentReader;
import edu.pitt.lrdc.cs.revision.model.*;

import java.io.File;
import java.util.*;

/**
 * Kappa calculator, borrows the idea from RST hierarchical annotation Kappa
 * calculuation
 * 
 * This is the implementation for Cohen's Kappa (Two raters)
 * 
 * Procedures 1. Read the two annotated file 2. Get the annotated units at
 * different level 3. Build the confusion matrix based on the previous step 4.
 * Calculate kappa
 * 
 * @author zhangfan
 * @version 1.0
 */
public class KappaCalc {
	/**
	 * Get the kappa from two annotation of the same document
	 * 
	 * @param r1
	 * @param r2
	 * @return
	 */
	public static Kappa getKappa(RevisionDocument r1, RevisionDocument r2) {
		Kappa k = new Kappa();
		return k;
	}

	/**
	 * Not implemented yet
	 * 
	 * @param pair
	 * @return
	 */
	public static Kappa getKappa(ComparePair pair) {
		return getKappa(pair.r1, pair.r2);
	}

	/**
	 * Get the kappa from two annotation of the same document at a specific
	 * level
	 * 
	 * @param r1
	 * @param r2
	 * @param level
	 * @return
	 */
	public double getKappaAtLevel(RevisionDocument r1, RevisionDocument r2,
			int level, int option) {
		double kappa = 1.0;
		int[][] confusionMatrix = buildLevelUnitMatrix(r1.getRoot(),
				r2.getRoot(), level, option);
		printMatrix(confusionMatrix);
		kappa = kappaCalc(confusionMatrix);
		return kappa;
	}

	/**
	 * Kappa calcuation from the confusion matrix
	 * 
	 * Referenced the link:
	 * http://en.wikibooks.org/wiki/Algorithm_Implementation
	 * /Statistics/Cohen's_kappa
	 * 
	 * 
	 * A-c1 A-c2 A-c3 B-c1 20 7 4
	 * 
	 * B-c2 5 15 1
	 * 
	 * B-c3 2 0 21
	 * 
	 * 
	 * @param matrix
	 * @return
	 */
	public static double kappaCalc(int[][] matrix) {
		int size = matrix.length;
		// Get the agreed ones from the diagonal
		int sumDiagonal = 0;
		for (int i = 0; i < size; i++) {
			sumDiagonal += matrix[i][i];
		}

		int sumTotal = 0;
		// Get all units
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				sumTotal += matrix[i][j];
			}
		}

		double pa = sumDiagonal * 1.0 / sumTotal;

		// PE: the sum of the multiplication of the marginal probabilities per
		// class divided by the sum of total instances
		int sumMarginCases = 0;
		for (int i = 0; i < size; i++) {
			// margin cases for case i
			int ann1 = 0;
			int ann2 = 0;
			for (int j = 0; j < size; j++) {
				ann1 += matrix[i][j];
			}
			for (int j = 0; j < size; j++) {
				ann2 += matrix[j][i];
			}
			sumMarginCases += ann1 * ann2;
		}
		double pe = sumMarginCases * 1.0 / (sumTotal * sumTotal);

		//System.out.println("PA:" + pa);
		//System.out.println("PE:" + pe);
		double kappa = (pa - pe) / (1 - pe);
		return kappa;
	}

	/**
	 * Add units of a specific level to the units array
	 * 
	 * @param level
	 * @param root
	 * @param units
	 *            the array to store the revision units
	 */
	public static void addLevelUnits(int level, RevisionUnit root,
			ArrayList<RevisionUnit> units) {
		if (root.getRevision_level() == level) {
			units.add(root);
		} else {
			ArrayList<RevisionUnit> childs = root.getUnits();
			for (int i = 0; i < childs.size(); i++) {
				addLevelUnits(level, childs.get(i), units);
			}
		}
	}

	/**
	 * Printing out the confusion matrix
	 * 
	 * @param matrix
	 */
	public void printMatrix(int[][] matrix) {
		String header = "Annotator";
		for (int i = 0; i < matrix.length; i++) {
			header += "\t" + indexName.get(i);
		}
		header += "\n";
		String content = "";
		for (int i = 0; i < matrix.length; i++) {
			content += indexName.get(i);
			for (int j = 0; j < matrix.length; j++) {
				content += "\t" + matrix[i][j];
			}
			content += "\n";
		}
		System.out.println(header + content);
	}

	public void printMatrix2(int[][] matrix) {
		String header = "Annotator";
		for (int i = 0; i < matrix.length; i++) {
			header += "\t" + i;
		}
		header += "\n";
		String content = "";
		for (int i = 0; i < matrix.length; i++) {
			content += i;
			for (int j = 0; j < matrix.length; j++) {
				content += "\t" + matrix[i][j];
			}
			content += "\n";
		}
		System.out.println(header + content);
	}

	// Storing the name of the index
	private Hashtable<Integer, String> indexName = new Hashtable<Integer, String>();
	// To retrieve the index according to the name
	private Hashtable<String, Integer> nameIndex = new Hashtable<String, Integer>();

	/**
	 * Initialize confusion matrix
	 * 
	 * @return
	 */
	public int[][] initializeCM(int option) {
		int index = 0;
		for (int i = RevisionPurpose.START; i <= RevisionPurpose.END; i++) {
			for (int j = RevisionOp.START; j <= RevisionOp.END; j++) {
				String name = getRevisionCategoryName(i, j, option);
				if (!nameIndex.containsKey(name)) {
					indexName.put(index, name);
					nameIndex.put(name, index);
					// System.out.println(name);
					index++;
				}
			}
		}
		indexName.put(index, "No Annotation");
		nameIndex.put("No annotation", index);
		index++;
		int[][] confusionMatrix = new int[index][index];
		return confusionMatrix;
	}

	private String getRevisionCategoryName(int revPurpose, int revOp, int option) {
		if (option == 0) {
			return RevisionPurpose.getPurposeName(revPurpose) + "-"
					+ RevisionOp.getOpName(revOp);
		} else if (option == 1) {
			return RevisionPurpose.getPurposeName(revPurpose);
		} else if (option == 2) {
			return RevisionOp.getOpName(revOp);
		}
		return "";
	}

	/**
	 * 
	 * @param ru
	 * @param option
	 *            0 for default, 1 for purpose only, 2 for operation only
	 * @return
	 */
	private String getRevisionCategoryName(RevisionUnit ru, int option) {
		return getRevisionCategoryName(ru.getRevision_purpose(),
				ru.getRevision_op(), option);
	}

	/**
	 * Build the confusion matrix with multiple categories The confusion matrix
	 * only includes the elements that are revised
	 * 
	 * @param root
	 * @param root2
	 * @return
	 */
	public int[][] buildRUMatrix(RevisionDocument r1, RevisionDocument r2,
			int category) {
		ArrayList<RevisionUnit> r1Units = r1.getRoot()
				.getRevisionUnitAtLevel(0);
		ArrayList<RevisionUnit> r2Units = r2.getRoot()
				.getRevisionUnitAtLevel(0);

		Hashtable<Integer, Integer> r1Ann = new Hashtable<Integer, Integer>();
		Hashtable<Integer, Integer> r2Ann = new Hashtable<Integer, Integer>();

		for (int i = 0; i < r1Units.size(); i++) {
			RevisionUnit ru = r1Units.get(i);
			int index = 0;
			if (ru.getRevision_op() == RevisionOp.DELETE) {
				index = 0 - ru.getOldSentenceIndex().get(0);
			} else {
				//System.out.println(ru.getRevision_index());
				index = ru.getNewSentenceIndex().get(0);
			}
			if (!r1Ann.containsKey(index) || r1Ann.get(index) == 0) {
				if (ru.getRevision_purpose() == category) {
					r1Ann.put(index, 1);
				} else {
					r1Ann.put(index, 0);
				}
			}
		}

		for (int i = 0; i < r2Units.size(); i++) {
			RevisionUnit ru = r2Units.get(i);
			int index = 0;
			if (ru.getRevision_op() == RevisionOp.DELETE) {
				index = 0 - ru.getOldSentenceIndex().get(0);
			} else {
				index = ru.getNewSentenceIndex().get(0);
			}
			if (!r2Ann.containsKey(index) || r2Ann.get(index) == 0) {
				if (ru.getRevision_purpose() == category) {
					r2Ann.put(index, 1);
				} else {
					r2Ann.put(index, 0);
				}
			}
		}

		Iterator<Integer> it = r1Ann.keySet().iterator();
		/*
		 * Matrix looks like this 0 1 0 1
		 */
		int[][] matrix = new int[2][2];
		while (it.hasNext()) {
			int index = it.next();
			int r1Label = r1Ann.get(index);
			int r2Label = 0;
			if (r2Ann.containsKey(index)) {
				r2Label = r2Ann.get(index);
			}
			if(r1Label != r2Label) System.out.println("DIFF:"+r1.getDocumentName());
			matrix[r1Label][r2Label] += 1;
		}
		return matrix;
	}

	/**
	 * Build the matrix for two folders
	 * 
	 * @param folder1
	 * @param folder2
	 * @return
	 * @throws Exception
	 */
	public int[][] buildRUMatrix(String folder1, String folder2, int category)
			throws Exception {
		File f1 = new File(folder1);
		File[] files = f1.listFiles();
		int[][] matrix = new int[2][2];
		RevisionDocumentReader reader = new RevisionDocumentReader();
		for (int i = 0; i < files.length; i++) {
			File file1 = files[i];
			String name = file1.getName();
			if(name.startsWith(".DS_Store")) continue;
			String file2 = folder2 + "/" + name;
			RevisionDocument doc1 = reader.readDoc(file1.getAbsolutePath());
			RevisionDocument doc2 = reader.readDoc(file2);
			int[][] tmpMatrix = buildRUMatrix(doc1, doc2, category);
			for (int ii = 0; ii < tmpMatrix.length; ii++) {
				for (int jj = 0; jj < tmpMatrix[ii].length; jj++) {
					matrix[ii][jj] += tmpMatrix[ii][jj];
				}
			}
		}
		return matrix;
	}

	/**
	 * Get the kappa table
	 * 
	 * @param r1
	 * @param r2
	 * @return
	 */
	public Hashtable<Integer, Double> getKappas(RevisionDocument r1,
			RevisionDocument r2) {
		Hashtable<Integer, Double> kappaTable = new Hashtable<Integer, Double>();
		for (int i = RevisionPurpose.START; i <= RevisionPurpose.END; i++) {
			int[][] matrix = buildRUMatrix(r1, r2, i);
			System.out
					.println("Kappa for " + RevisionPurpose.getPurposeName(i));
			printMatrix2(matrix);
			double kappa = kappaCalc(matrix);
			kappaTable.put(i, kappa);
			System.out.println(kappa);
		}
		return kappaTable;
	}

	public Hashtable<Integer, Double> getKappas(String folder1, String folder2)
			throws Exception {
		Hashtable<Integer, Double> kappaTable = new Hashtable<Integer, Double>();
		for (int i = RevisionPurpose.START; i <= RevisionPurpose.END; i++) {
			int[][] matrix = buildRUMatrix(folder1, folder2, i);
			System.out
					.println("Kappa for " + RevisionPurpose.getPurposeName(i));
			printMatrix2(matrix);
			double kappa = kappaCalc(matrix);
			kappaTable.put(i, kappa);
			System.out.println(kappa);
		}
		return kappaTable;
	}

	/**
	 * Build the confusion matrix
	 * 
	 * @param root
	 * @param root2
	 * @param level
	 * @param option
	 * @return
	 */
	public int[][] buildLevelUnitMatrix(RevisionUnit root, RevisionUnit root2,
			int level, int option) {
		int[][] matrix = initializeCM(option);
		int missIndex = matrix.length - 1;

		ArrayList<RevisionUnit> arrs = new ArrayList<RevisionUnit>();
		addLevelUnits(level, root, arrs);
		System.out.println("Annotator 1 have " + arrs.size()
				+ " units at this level:" + level);

		ArrayList<RevisionUnit> arrs2 = new ArrayList<RevisionUnit>();
		addLevelUnits(level, root2, arrs2);
		System.out.println("Annotator 2 have " + arrs.size()
				+ " units at this level:" + level);

		Hashtable<String, ArrayList<String>> table1 = new Hashtable<String, ArrayList<String>>();
		for (int i = 0; i < arrs.size(); i++) {
			RevisionUnit unit = arrs.get(i);
			String index = getUnitIndex(unit);
			String cat = getRevisionCategoryName(unit, option);
			if (table1.containsKey(index)) {
				table1.get(index).add(cat);
			} else {
				ArrayList<String> set = new ArrayList<String>();
				set.add(cat);
				table1.put(index, set);
			}
		}
		Hashtable<String, ArrayList<String>> table2 = new Hashtable<String, ArrayList<String>>();
		for (int i = 0; i < arrs2.size(); i++) {
			RevisionUnit unit = arrs2.get(i);
			String index = getUnitIndex(unit);
			String cat = getRevisionCategoryName(unit, option);
			if (table2.containsKey(index)) {
				table2.get(index).add(cat);
			} else {
				ArrayList<String> set = new ArrayList<String>();
				set.add(cat);
				table2.put(index, set);
			}
		}

		Iterator<String> it = table1.keySet().iterator();
		while (it.hasNext()) {
			String index = it.next();
			ArrayList<String> vals = table1.get(index);
			if (!table2.containsKey(index)) { // Missed
				for (int i = 0; i < vals.size(); i++) {
					int colIndex = nameIndex.get(vals.get(i));
					matrix[missIndex][colIndex] += 1;
				}
			} else {
				ArrayList<String> vals2 = table2.get(index);
				if (vals.size() == 1 && vals2.size() == 1) { // Only one
																// annotation
					int colIndexI = nameIndex.get(vals.get(0));
					// System.out.println(vals2.get(0));
					int rowIndexJ = nameIndex.get(vals2.get(0));
					matrix[rowIndexJ][colIndexI] += 1;
				} else {
					// Multiple annotations for one row
					for (int i = 0; i < vals.size(); i++) {
						for (int j = 0; j < vals2.size(); j++) {
							int colIndexI = nameIndex.get(vals.get(i));
							if (vals.get(i).equals(vals2.get(j))) {
								matrix[colIndexI][colIndexI] += 1;
							} else { // Missed
								matrix[missIndex][colIndexI] += 1;
							}
						}
					}
					// agreed ones has been scanned
					for (int i = 0; i < vals2.size(); i++) {
						boolean found = false;
						for (int j = 0; j < vals.size(); j++) {
							if (vals2.get(i).equals(vals.get(j))) {
								found = true;
								break;
							}

						}
						if (found == false) {
							int colIndexJ = nameIndex.get(vals2.get(i));
							matrix[colIndexJ][missIndex] += 1;
						}
						found = false;
					}
				}
			}
		}
		return matrix;
	}

	/**
	 * Get all the basic sentences it relates to
	 * 
	 * @param ru
	 * @param unitIndex
	 */
	public String getUnitIndex(RevisionUnit ru) {
		ArrayList<RevisionUnit> childs = ru.getUnits();
		String unitIndex = "";
		if (childs.size() == 0) {
			unitIndex += ru.getRevision_level() + "-"
					+ ru.getOldSentenceIndex() + "-" + ru.getNewSentenceIndex();
			return unitIndex;
		}
		for (int i = 0; i < childs.size(); i++) {
			unitIndex += getUnitIndex(childs.get(i)) + ",";
		}
		return unitIndex;
	}

	public int[][] addMatrix(int[][] matrix1,int[][] matrix2) {
		int r = matrix1.length;
		int c = matrix1[0].length;
		int[][] matrix = new int[r][c];
		for(int i = 0;i<r;i++) {
			for(int j = 0;j<c;j++) {
				matrix[i][j] = matrix1[i][j] + matrix2[i][j];
			}
		}
		return matrix;
	}
	public int[][] buildRUMatrixAtLevelBinary(ArrayList<RevisionDocument> docs1,ArrayList<RevisionDocument> docs2, int level, int category) {
		int[][] matrix = new int[2][2];
		for(int i = 0;i<docs1.size();i++) {
			addMatrix(matrix, buildRUMatrixAtLevelBinary(docs1.get(i),docs2.get(i),level,category));
		}
		return matrix;
	}
	
	 public int[][] buildRUMatrixAtLevelBinary(RevisionDocument r1, RevisionDocument r2, int level, int category) {
     	ArrayList<RevisionUnit> r1Units = r1.getRoot().getRevisionUnitAtLevel(level);
     	ArrayList<RevisionUnit> r2Units = r2.getRoot().getRevisionUnitAtLevel(level);
     
     	ArrayList<RevisionUnit> r1Lvl0Units = new ArrayList<RevisionUnit>();
     	ArrayList<RevisionUnit> r2Lvl0Units = new ArrayList<RevisionUnit>();
     	
     	for(RevisionUnit ru: r1Units) {
     		ArrayList<RevisionUnit> rus = ru.getRevisionUnitAtLevel(0);
     		for(RevisionUnit tmpRU: rus) {
     			tmpRU.setRevision_purpose(ru.getRevision_purpose());
     		}
     		r1Lvl0Units.addAll(rus);
     	}
     	for(RevisionUnit ru: r2Units) {
     		ArrayList<RevisionUnit> rus = ru.getRevisionUnitAtLevel(0);
     		for(RevisionUnit tmpRU: rus) {
     			tmpRU.setRevision_purpose(ru.getRevision_purpose());
     		}
     		r2Lvl0Units.addAll(rus);
     	}
     	return buildMatrix(r1Lvl0Units,r2Lvl0Units, category);
     }
     
     public int[][] buildMatrix(ArrayList<RevisionUnit> r1Units, ArrayList<RevisionUnit> r2Units, int category) {
     	 Hashtable<Integer, Integer> r1Ann = new Hashtable<Integer, Integer>();
          Hashtable<Integer, Integer> r2Ann = new Hashtable<Integer, Integer>();

          for (int i = 0; i < r1Units.size(); i++) {
                  RevisionUnit ru = r1Units.get(i);
                  int index = 0;
                  if (ru.getRevision_op() == RevisionOp.DELETE) {
                          index = 0 - ru.getOldSentenceIndex().get(0);
                  } else {
                          //System.out.println(ru.getRevision_index());
                          index = ru.getNewSentenceIndex().get(0);
                  }
                  if (!r1Ann.containsKey(index) || r1Ann.get(index) == 0) {
                          if (ru.getRevision_purpose() == category) {
                                  r1Ann.put(index, 1);
                          } else {
                                  r1Ann.put(index, 0);
                          }
                  }
          }

          for (int i = 0; i < r2Units.size(); i++) {
                  RevisionUnit ru = r2Units.get(i);
                  int index = 0;
                  if (ru.getRevision_op() == RevisionOp.DELETE) {
                          index = 0 - ru.getOldSentenceIndex().get(0);
                  } else {
                          index = ru.getNewSentenceIndex().get(0);
                  }
                  if (!r2Ann.containsKey(index) || r2Ann.get(index) == 0) {
                          if (ru.getRevision_purpose() == category) {
                                  r2Ann.put(index, 1);
                          } else {
                                  r2Ann.put(index, 0);
                          }
                  }
          }

          Iterator<Integer> it = r1Ann.keySet().iterator();
          /*
           * Matrix looks like this 0 1 0 1
           */
          int[][] matrix = new int[2][2];
          while (it.hasNext()) {
                  int index = it.next();
                  int r1Label = r1Ann.get(index);
                  int r2Label = 0;
                  if (r2Ann.containsKey(index)) {
                          r2Label = r2Ann.get(index);
                  }
                  //if(r1Label != r2Label) System.out.println("DIFF:"+r1.getDocumentName());
                  matrix[r1Label][r2Label] += 1;
          }
          return matrix;
     }

	
	public static void main(String[] args) throws Exception {
		KappaCalc kc = new KappaCalc();
		kc.getKappas(
				"/Users/faz23/Desktop/34/annotated/kappa/alex",
				"/Users/faz23/Desktop/34/annotated/kappa/fan");
	}
}
