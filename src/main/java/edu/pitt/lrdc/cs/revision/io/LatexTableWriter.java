package edu.pitt.lrdc.cs.revision.io;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;

/**
 * The tool for generating latex table
 * @author zhf4pal
 *
 */
public class LatexTableWriter {
	private ArrayList<String> headers;
	private ArrayList<String> rows;
	private int rowNum = 1;
	private int colNum = 1;
	
	/**
	 * Generate a long table or not
	 */
	private boolean isLongTable = true;
	
	private Hashtable<String, Integer> headerIndex;
	private Hashtable<String, Integer> rowIndex;
	
	private String tableName;
	private String alignStr = "l"; //default to align to left
	
	public void setLeftAlign() {
		alignStr = "l";
	}
	public void setCenterAlign() {
		alignStr = "c";
	}
	public void setRightAlign() {
		alignStr = "r";
	}
	
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	
	/**
	 * Set whether the table is long or not
	 * @param isLong
	 */
	public void setLong(boolean isLong) {
		isLongTable = isLong;
	}
	
	private String latexStr = "";
	
	private String[][] table;
	public LatexTableWriter() {
		headerIndex = new Hashtable<String, Integer>();
		rowIndex = new Hashtable<String, Integer>();
		headers = new ArrayList<String>();
		rows = new ArrayList<String>();
	}
	
	
	public LatexTableWriter(String tableName) {
		this();
		this.tableName = tableName;
	}
	
	/**
	 * Add a row
	 * @param rowName
	 */
	public void addRow(String rowName) {
		rows.add(rowName);
		rowIndex.put(rowName, rowNum);
		rowNum++;
	}
	
	/**
	 * Add a column
	 * @param columnName
	 */
	public void addColumn(String columnName) {
		headers.add(columnName);
		headerIndex.put(columnName, colNum);
		colNum++;
	}
	
	/**
	 * Generate the table, with headers and columns
	 */
	public void makeTable() {
		table = new String[rowNum][colNum];
		table[0][0] = "Experiments";
		for(String header: headers) {
			table[0][headerIndex.get(header)] = "\\bf "+ header;
		}
		for(String row: rows) {
			table[rowIndex.get(row)][0] = row;
		}
	}
	
	public void setValue(String row, String column, Object val) {
		table[rowIndex.get(row)][headerIndex.get(column)] = val.toString();
	}
	
	public void setValue(int i, int j, String val) {
		table[i][j] = val;
	}
	
	/**
	 * Cleans the latex string
	 */
	public void cleanStr() {
		latexStr = "";
	}
	
	public void addHeading() {
		String beginStr = "\\begin{table}";
		if(isLongTable) beginStr = "\\begin{table*}";
		latexStr += beginStr;
		latexStr += "\n";
		latexStr += "\\begin{center}";
		latexStr += "\n";
		latexStr += "\\begin{tabular}";
	}
	
	/**
	 * Set the boundary blabla....
	 */
	public void addLatexColumnSetting() {
		latexStr += "{|";
		for(int i = 0;i<colNum;i++) {
			latexStr += alignStr;
			latexStr += "|"; //My table always have boundaries
		}
		latexStr += "}";
	}
	
	/**
	 * Add the content to the table
	 */
	public void addTableContent() {
		for(int i = 0;i<rowNum;i++) {
			latexStr += "\\hline";
			latexStr += "\n";
			for(int j = 0;j<colNum;j++) {
				latexStr += table[i][j] + "&";
			}
			latexStr = latexStr.substring(0,latexStr.length()-1); //remove the last &
			latexStr += "\\\\";
			latexStr += "\n";
		}
		latexStr += "\\hline";
		latexStr += "\n";
	}
	
	public void addEnding() {
		latexStr += "\\end{tabular}";
		latexStr += "\n";
		latexStr += "\\caption{Example, Add it by yourself}";
		latexStr += "\n";
		latexStr += "\\label{"+"table:"+tableName+"}";
		latexStr += "\n";
		latexStr += "\\end{center}";
		latexStr += "\n";
		if(isLongTable) latexStr += "\\end{table*}"; else latexStr += "\\end{table}";
	}
	
	public String generateTable() {
		cleanStr();
		addHeading();
		addLatexColumnSetting();
		addTableContent();
		addEnding();
		return latexStr;
	}
	
	private String outputPath;
	
	public void setOutputPath(String output){
		this.outputPath = output + "/" +tableName +".txt";
	}
	
	public void print() throws IOException {
		BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath));
		writer.write(generateTable());
		writer.close();
	}
}
