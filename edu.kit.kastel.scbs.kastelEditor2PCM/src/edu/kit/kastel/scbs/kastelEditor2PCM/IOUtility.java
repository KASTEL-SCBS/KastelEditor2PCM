package edu.kit.kastel.scbs.kastelEditor2PCM;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class IOUtility {
	
	public static String readFromFile(File file) {
		String json = "";
		String line = "";
		BufferedReader fileReader = null;
		 try {
				fileReader = new BufferedReader(new FileReader(file.getAbsolutePath()));
			   } catch (FileNotFoundException e) {
				System.out.println("File not found even with prior check");
				return "";
			   }
			   
			   try {
				while((line = fileReader.readLine()) != null) {
					   json += line;
				   }
			   } catch (IOException e) {
				   System.out.println("IOException");
				   return "";
			   } finally {
				   try {
					   fileReader.close();
				   } catch (IOException e) {
					   System.out.println("Error while closing fileReader");
					   return "";
				   }
			   }   
			   return json;
		} 
	
	public static boolean writeToFile(File file, String fileContent){

		BufferedWriter writer = null;

		try {
			writer = new BufferedWriter(new FileWriter(file.getAbsoluteFile()));
			writer.write(fileContent);
		} catch (IOException e) {
			return false;
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
					return false;
				}
			}
		}
		
		return true;
	}
}
