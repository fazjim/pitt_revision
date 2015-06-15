package edu.pitt.cs.revision.purpose;

import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * Struct storing the alignment info
 * 
 * @author zhangfan
 *
 */
public class AlignStruct {
	public String documentpath;
	public ArrayList<Integer> newIndices;
	public ArrayList<Integer> oldIndices;

	/**
	 * Parsing ID to get info
	 * 
	 * @param ID
	 * @return
	 */
	public static AlignStruct parseID(String ID) {
		AlignStruct as = new AlignStruct();
		StringTokenizer stk = new StringTokenizer(ID, "|");
		String name = "";
		String newIndiceStr = "";
		String oldIndiceStr = "";
		if (stk.hasMoreTokens()) {
			name = stk.nextToken();
		}
		if (stk.hasMoreTokens()) {
			newIndiceStr = stk.nextToken();
		}
		if (stk.hasMoreTokens()) {
			oldIndiceStr = stk.nextToken();
		}

		ArrayList<Integer> newIndices = new ArrayList<Integer>();
		if (newIndiceStr.contains("_")) {
			String[] splits = newIndiceStr.split("_");
			for (String index : splits) {
				if (index != null && index.length() > 0)
					newIndices.add(Integer.parseInt(index));
			}
		}
		ArrayList<Integer> oldIndices = new ArrayList<Integer>();
		if (oldIndiceStr.contains("_")) {
			String[] splits = oldIndiceStr.split("_");
			for (String index : splits) {
				if (index != null && index.length() > 0)
					oldIndices.add(Integer.parseInt(index));
			}
		}
		as.documentpath = name;
		as.newIndices = newIndices;
		as.oldIndices = oldIndices;
		return as;

	}
}