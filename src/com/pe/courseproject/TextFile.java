package com.pe.courseproject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * A convenience class for reading and processing the data in a text file. 
 */
public class TextFile {

	public static String readFileContents(File file, Boolean keepNewLines) {
		StringBuilder sb = new StringBuilder();
        String newLine;
        
        // Since Java 7, there is a method that provides a platform-independent use of newline.
		if(keepNewLines) newLine = System.lineSeparator();
        else newLine = "";

		try {
			BufferedReader in = new BufferedReader(new FileReader(file));
			try {
				String s;
				
				while((s = in.readLine()) != null) {
					sb.append(s + newLine);
				}
			} finally {
				in.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		return sb.toString();
	}
	
	public static Map<Character, Double> obtainCharactersProbability(File file) {
		String text = readFileContents(file, false);
		return obtainCharactersProbability(text);
	}
	
	public static Map<Character, Double> obtainCharactersProbability(String text) {
		Map<Character, Double> charMapping = new HashMap<Character, Double>();
		for(int i = 0; i < text.length(); i++) {
			char ch = text.charAt(i);
			if(charMapping.containsKey(ch)) {
				double value = charMapping.get(ch);
				charMapping.put(ch, value + 1);
			} else {
				charMapping.put(ch, 1.0);
			}
		}
		
		for(Map.Entry<Character, Double> entry : charMapping.entrySet()) {
			charMapping.put(entry.getKey(), entry.getValue() / text.length());
		}
		
		return charMapping;
	}

}
