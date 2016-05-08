package edu.pitt.lrdc.cs.revision.io;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MyLogger {
	private static String logPath = "C:\\Not Backed Up\\experimentLog.txt";
	private MyLogger() {
		
	}
	public static MyLogger getInstance() {
		MyLogger logger = new MyLogger();
		return logger;
	}
	
	public void log(String content) {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(logPath,true));
			String dateStr = "";
			DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			Date date = new Date();
			dateStr = dateFormat.format(date);
			writer.write(dateStr + "\n");
			writer.write(content + "\n");
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
