package edu.pitt.cs.revision.purpose.pdtb;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Stack;

import java.util.List;

/**
 * Parse the folder
 * 
 * @author zhangfan
 *
 */
public class ManualParseResultReader {
	/**
	 * Return a list of ParseResultFile Objects, path can be any folder
	 * containing the files
	 * 
	 * @param path
	 * @return
	 * @throws IOException
	 */
	public static List<ManualParseResultFile> readFiles(String path)
			throws IOException {
		List<ManualParseResultFile> files = new ArrayList<ManualParseResultFile>();
		File folder = new File(path);
		Stack<File> root = new Stack<File>();
		root.push(folder);
		while (!root.empty()) {
			File temp = root.pop();
			if (temp.isDirectory()) {
				File[] subs = temp.listFiles();
				for (File sub : subs) {
					root.push(sub);
				}
			} else {
				if (temp.getAbsolutePath().contains("-outputroot files")) {
					ManualParseResultFile psf = new ManualParseResultFile(
							temp.getAbsolutePath());
					files.add(psf);
				}
			}
		}
		return files;
	}
}
