package edu.kit.kastel.scbs.kastelEditor2PCM.Util;

public class StringUtil {

	public static String removeCharAndStringSymbols(String string) {
		String cleaned = "";
		
		cleaned = string.replaceAll("\'", "");
		cleaned = cleaned.replaceAll("\"", "");
		
		return cleaned;
	}
	
}
