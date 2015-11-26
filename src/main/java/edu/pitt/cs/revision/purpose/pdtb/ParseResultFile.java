package edu.pitt.cs.revision.purpose.pdtb;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 * Data structure of a file containing the pipes
 * 
 * @author zhangfan
 *
 */
public class ParseResultFile {
	private String fileName;
	private List<PipeUnit> pipes;

	private boolean isPDTB1 = false;

	public void choosePDTB1() {
		isPDTB1 = true;
	}

	public void choosePDTB2() {
		isPDTB1 = false;
	}

	public boolean isPDTB1() {
		return isPDTB1;
	}

	public ParseResultFile(String fileName) throws IOException {
		this.fileName = fileName;
		if (fileName.contains("pdtb_1")) {
			choosePDTB1();
		} else {
			choosePDTB2();
		}
		pipes = new ArrayList<PipeUnit>();
		BufferedReader reader = new BufferedReader(new FileReader(fileName));
		String line = reader.readLine();
		while (line != null) {
			PipeUnit pu = new PipeUnit(line);
			pipes.add(pu);
			line = reader.readLine();
		}
		reader.close();
	}

	public String getFileName() {
		return this.fileName;
	}

	public List<PipeUnit> getPipes() {
		return this.pipes;
	}

	public int getCount(List<Integer> categoryTypes, List<String> categoryValues) {
		int cnt = 0;
		for (PipeUnit pipe : pipes) {
			boolean exists = true;
			for (int i = 0; i < categoryTypes.size(); i++) {
				int categoryType = categoryTypes.get(i);
				String categoryValue = categoryValues.get(i);
				String value = pipe.getAttr(categoryType).trim();
				if (!value.equals(categoryValue)) {
					exists = false;
					break;
				}
			}
			if (exists)
				cnt++;
		}
		return cnt;
	}

	/**
	 * Get the count of value of a specific category type(column)
	 * 
	 * @param categoryType
	 *            , the column index
	 * @param categoryValue
	 * @return
	 */
	public int getCount(int categoryType, String categoryValue) {
		int cnt = 0;
		for (PipeUnit pipe : pipes) {
			String value = pipe.getAttr(categoryType).trim();
			if (value.equals(categoryValue))
				cnt++;
		}
		return cnt;
	}

	public int getCountMerge(List<Integer> categoryTypes,
			List<String> categoryValues) {
		int cnt = 0;
		for (PipeUnit pipe : pipes) {
			boolean exists = true;
			for (int i = 0; i < categoryTypes.size(); i++) {
				int categoryType = categoryTypes.get(i);
				String categoryValue = categoryValues.get(i);
				String value = pipe.getAttr(categoryType).trim();
				if (value.contains("."))
					value = value.substring(0, value.indexOf("."));
				if (!value.equals(categoryValue)) {
					exists = false;
					break;
				}
			}
			if (exists)
				cnt++;
		}
		return cnt;
	}

	public int getCountMerge(Integer categoryType, String categoryValue) {
		int cnt = 0;
		for (PipeUnit pipe : pipes) {
			String value = pipe.getAttr(categoryType).trim();
			if (value.contains("."))
				value = value.substring(0, value.indexOf("."));
			if (value.equals(categoryValue))
				cnt++;
		}
		return cnt;
	}

	/**
	 * Get the counts of all the occurrences of a specific type(column)
	 * 
	 * @param categoryType
	 *            , the column index
	 * @return
	 */
	public Hashtable<String, Integer> getCount(int categoryType) {
		Hashtable<String, Integer> counts = new Hashtable<String, Integer>();
		for (PipeUnit pipe : pipes) {
			String value = pipe.getAttr(categoryType).trim();
			int cnt = 0;
			if (counts.containsKey(value))
				cnt = counts.get(value);
			cnt++;
			counts.put(value, cnt);
		}
		return counts;
	}
}
