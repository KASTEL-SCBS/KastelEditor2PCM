package edu.kit.kastel.scbs.kastelEditor2PCM.Util;

import edu.kit.kastel.scbs.kastelEditor2PCM.GoalModelToPCMElementTransformator.UpperOrLower;

public class StringUtil {

	public static String removeCharAndStringSymbols(String string) {
		String cleaned = "";
		
		cleaned = string.replaceAll("\'", "");
		cleaned = cleaned.replaceAll("\"", "");
		cleaned = cleaned.replaceAll("[-]", "");
		cleaned = cleaned.replaceAll("[()]", "");
		
		return cleaned;
	}
	
	public static String trimWhiteSpace(String name, UpperOrLower first) {
		char[] characters = "".toCharArray();
		String result = "";
		
		if(!name.contains(" ")){
			result = name;
		} else {
			
		String[] nameParts = name.split(" ");
	
		for (int i = 0; i < nameParts.length; i++) {
			characters = nameParts[i].toCharArray();
			
			characters[0] = Character.toUpperCase(characters[0]);

			result += String.valueOf(characters);
			}
		}
		
	
		
		characters = result.toCharArray();
		switch (first) {
		case UPPER:
			characters[0] = Character.toUpperCase(characters[0]);
			break;
		case LOWER:
			characters[0] = Character.toLowerCase(characters[0]);
			break;
		default:
			break;
		}
		
		result = String.valueOf(characters);
		return result;
	}
	
	public static String cleanAndTrim(String name, UpperOrLower first) {
		String working = removeCharAndStringSymbols(name);
		working = trimWhiteSpace(working, first);
		
		return working;
	}
	
}
