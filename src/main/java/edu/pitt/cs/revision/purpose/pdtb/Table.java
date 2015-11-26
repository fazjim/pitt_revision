package edu.pitt.cs.revision.purpose.pdtb;

import java.util.List;
import java.util.ArrayList;

/**
 * Table structure for output the statistics
 * 
 * @author zhangfan
 *
 */
public class Table {
	private String tableName;
	private List<String> columns;
	private List<List<String>> rows;

	public Table() {
		columns = new ArrayList<String>();
		rows = new ArrayList<List<String>>();
	}

	public Table(String name) {
		this.tableName = name;
		columns = new ArrayList<String>();
		rows = new ArrayList<List<String>>();
	}

	public String getTableName() {
		return tableName;
	}

	public List<List<String>> getRows() {
		return this.rows;
	}

	public List<String> getColumns() {
		return this.columns;
	}

	public void addRow(List<String> row) {
		this.rows.add(row);
	}

	public void addColumn(String columnName) {
		this.columns.add(columnName);
	}
}
