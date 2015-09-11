package edu.pitt.cs.revision.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;

import com.google.common.io.Files;



public class StringConverter {
	public static String convertString(String str) {
		String out = null;
		// out = new String(str.getBytes("Windows-1252"), "UTF-8");
		final Charset windowsCharset = Charset.forName("Windows-1252");
		final Charset utfCharset = Charset.forName("UTF-8");

		byte[] incomingBytes = str.getBytes();
		final CharBuffer windowsEncoded = windowsCharset.decode(ByteBuffer
				.wrap(incomingBytes));

		final byte[] utfEncoded = utfCharset.encode(windowsEncoded).array();
		String s = new String(utfEncoded);
		

		return s;
	}
	
	public static Charset getCorrectCharsetToApply() {
		return Charset.forName("UTF-8");
	}
	
	public static void main(String[] args) throws IOException {
		String path = "C:\\Not Backed Up\\data\\newData\\sample-papers\\abate\\draft2\\AliceCooper - 17731.txt";
		BufferedReader reader = new BufferedReader(new FileReader(path));
		        //Files.newReader(new File(path),getCorrectCharsetToApply());
		String line = reader.readLine();
		while (line != null) {
			System.out.println("ORIGINAL: " + line);
			System.out.println("NEW:" + convertString(line));
			line = reader.readLine();
		}
	}
}
