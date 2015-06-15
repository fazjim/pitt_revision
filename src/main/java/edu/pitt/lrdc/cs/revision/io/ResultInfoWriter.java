package edu.pitt.lrdc.cs.revision.io;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Set;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import edu.pitt.lrdc.cs.revision.evaluate.ResultInfo;
import edu.pitt.lrdc.cs.revision.evaluate.ResultInfoRow;

/**
 * Write all the results to excel
 * 
 * @author zhangfan
 *
 */
public class ResultInfoWriter {
	//Print double better
	public static String outputDouble(double val) {
		val = val * 100;
		String valStr = Double.toString(val);
		if(valStr.length()>5) valStr = valStr.substring(0, 5);
		return valStr;
	}
	public static void persist(ArrayList<ResultInfoRow> results,
			Hashtable<String, LatexTableWriter> writerIndex, String purposeName, String outputFolder) throws IOException, IllegalArgumentException, IllegalAccessException {
		String filePath = outputFolder + "/allResults_"+purposeName.substring(0, 4)+".xlsx";
		FileOutputStream fileOut = new FileOutputStream(filePath);
		XSSFWorkbook xwb = new XSSFWorkbook();

		if (results.size() > 0) {
			Set<String> experiments = results.get(0).getKeys();
			// Use reflection to get all properties to write
			Field[] fields = ResultInfo.class.getDeclaredFields();
			for (Field field : fields) {
				String name = field.getName();
				LatexTableWriter latexWriter = writerIndex.get(name);
				XSSFSheet sheet = xwb.createSheet(name);
				XSSFRow header = sheet.createRow(0);
				int colIndex = 0;
				header.createCell(colIndex).setCellValue("Group");
				colIndex++;
				Hashtable<String, Integer> colIndexes = new Hashtable<String, Integer>();
				for (String experiment : experiments) {
					header.createCell(colIndex).setCellValue(experiment);
					colIndexes.put(experiment, colIndex);
					colIndex++;
				}
				Hashtable<Integer, Double> avg = new Hashtable<Integer, Double>();
				for (int i = 0; i < results.size(); i++) {
					ResultInfoRow row = results.get(i);
					XSSFRow xRow = sheet.createRow(i + 1);
					xRow.createCell(0).setCellValue(i);
					for (String experiment : experiments) {
						ResultInfo ri = row.getResult(experiment);
						double val = field.getDouble(ri);
						int index = colIndexes.get(experiment);
						xRow.createCell(index).setCellValue(val);
						if (avg.containsKey(index))
							avg.put(index, avg.get(index) + val);
						else
							avg.put(index, val);
					}
				}
				XSSFRow avgRow = sheet.createRow(results.size() + 1);
				avgRow.createCell(0).setCellValue("AVG:");
				for (String experiment : experiments) {
					int index = colIndexes.get(experiment);
					double val = avg.get(index)/results.size();
					avgRow.createCell(index).setCellValue(val);
					latexWriter.setValue(experiment, purposeName, outputDouble(val));
				}
			}

			xwb.write(fileOut);
			fileOut.flush();
			fileOut.close();
		}
	}

	/**
	 * Persist the evalution results into an excel file
	 * 
	 * @param filePath
	 * @throws IOException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 */
	public static void persist(ArrayList<ResultInfoRow> results, String filePath)
			throws IOException, IllegalArgumentException,
			IllegalAccessException {
		FileOutputStream fileOut = new FileOutputStream(filePath);
		XSSFWorkbook xwb = new XSSFWorkbook();

		if (results.size() > 0) {
			Set<String> experiments = results.get(0).getKeys();
			// Use reflection to get all properties to write
			Field[] fields = ResultInfo.class.getDeclaredFields();
			for (Field field : fields) {
				String name = field.getName();
				XSSFSheet sheet = xwb.createSheet(name);
				XSSFRow header = sheet.createRow(0);
				int colIndex = 0;
				header.createCell(colIndex).setCellValue("Group");
				colIndex++;
				Hashtable<String, Integer> colIndexes = new Hashtable<String, Integer>();
				for (String experiment : experiments) {
					header.createCell(colIndex).setCellValue(experiment);
					colIndexes.put(experiment, colIndex);
					colIndex++;
				}
				Hashtable<Integer, Double> avg = new Hashtable<Integer, Double>();
				for (int i = 0; i < results.size(); i++) {
					ResultInfoRow row = results.get(i);
					XSSFRow xRow = sheet.createRow(i + 1);
					xRow.createCell(0).setCellValue(i);
					for (String experiment : experiments) {
						ResultInfo ri = row.getResult(experiment);
						double val = field.getDouble(ri);
						int index = colIndexes.get(experiment);
						xRow.createCell(index).setCellValue(val);
						if (avg.containsKey(index))
							avg.put(index, avg.get(index) + val);
						else
							avg.put(index, val);
					}
				}
				XSSFRow avgRow = sheet.createRow(results.size() + 1);
				avgRow.createCell(0).setCellValue("AVG:");
				for (String experiment : experiments) {
					int index = colIndexes.get(experiment);
					avgRow.createCell(index).setCellValue(
							avg.get(index) / results.size());
				}
			}

			xwb.write(fileOut);
			fileOut.flush();
			fileOut.close();
		}
	}
}
