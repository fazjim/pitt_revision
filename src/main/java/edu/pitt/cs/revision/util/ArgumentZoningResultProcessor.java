package edu.pitt.cs.revision.util;

import java.util.Stack;
import java.io.File;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class ArgumentZoningResultProcessor {
	public static void main(String[] args) throws IOException {
		String folder = "C:\\Not Backed Up\\discourse_parse_results\\dlitmanRazTest";
		File f = new File(folder);
		Stack<File> root = new Stack<File>();
		root.push(f);
		while (!root.isEmpty()) {
			File top = root.pop();
			if (top.isDirectory()) {
				File[] subs = top.listFiles();
				for (File sub : subs) {
					root.push(sub);
				}
			} else {
				if (top.getName().endsWith(".raz")) {
					processFile(top.getAbsolutePath());
				}
			}
		}
	}

	public static void processFile(String fileName) throws IOException {
		String str = "";
		BufferedReader reader = new BufferedReader(new FileReader(fileName));
		ArrayList<String> tags = new ArrayList<String>();
		ArrayList<String> tagEnds = new ArrayList<String>();
		ArrayList<String> content = new ArrayList<String>();
		String line = reader.readLine();
		boolean enterRaz = false;
		while (line != null) {
			if (line.startsWith("</raz>"))
				enterRaz = false;
			if (enterRaz) {
				if(line.contains("&")) line = line.replaceAll("&","&#038;");
				if (line.indexOf(">") == 4) {
					String tag = line.substring(0, 5);
					String value = line.substring(5, line.length() - 6);
					String end = line.substring(line.length() - 6);
					tags.add(tag);
					content.add(value);
					tagEnds.add(end);
				} else if (line.startsWith("<undefined>")) {
					String value = line.substring(11, line.length() - 12);
					tags.add("<undefined>");
					content.add(value);
					tagEnds.add("</undefined>");
				} else {
					String value = line.substring(3, line.length() - 4);
					content.add(value);
				}
			} else {
				if (!line.startsWith("</raz>")
						&& !line.startsWith("</algorithm>"))
					str += line + "\n";
			}
			if (line.startsWith("<raz>"))
				enterRaz = true;
			line = reader.readLine();
		}
		reader.close();

		BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
		writer.write(str);
		for (int i = 0; i < tags.size(); i++) {
			writer.append(tags.get(i) + content.get(i + 1) + tagEnds.get(i)
					+ "\n");
		}
		writer.append("</raz>\n");
		writer.append("</algorithm>\n");
		writer.close();
	}
}
