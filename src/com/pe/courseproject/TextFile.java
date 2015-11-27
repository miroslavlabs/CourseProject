package com.pe.courseproject;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class TextFile {

	private static String readFileContents(File file) {
		StringBuilder sb = new StringBuilder();
		try {
			BufferedReader in = new BufferedReader(new FileReader(file));
			try {
				String s;
				
				while((s = in.readLine()) != null) {
					sb.append(s);
				}
			} finally {
				in.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		return sb.toString();
	}
	
	public static Map<Character, Double> obtainCharactersProbabilityFromFile(File file) {
		String text = readFileContents(file);
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
	
	public static void main(String[] args) {
		/*for(Map.Entry<Character, Double> entry : obtainCharactersProbabilityFromFile("D:\\WORK\\esndirect.sql").entrySet()) {
			System.out.printf("Charcter: %s, Occurences: %f\n", entry.getKey(), entry.getValue());
		}*/
	}
}
