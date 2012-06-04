package me.arno.blocklog.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import me.arno.blocklog.BlockLog;

public class Util {
	
	public static boolean isNumeric(String str) {
		try {
			Integer.parseInt(str);
		} catch(NumberFormatException e) {
			return false;
		}
		return true;
	}
	
	
	public static String getResourceContent(String file) {
		try {
			InputStream resourceFile = BlockLog.plugin.getResource("resources/" + file);
			 
			final char[] buffer = new char[0x10000];
			StringBuilder strBuilder = new StringBuilder();
			Reader inputReader = new InputStreamReader(resourceFile, "UTF-8");
			int read;
			
			do {
				read = inputReader.read(buffer, 0, buffer.length);
				if (read > 0)
					strBuilder.append(buffer, 0, read);
				
			} while (read >= 0);
			
			inputReader.close();
			resourceFile.close();
			return strBuilder.toString();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static String addSpaces(String message, int totalLength) {
		double spaces = Math.round((totalLength - wordLength(message)) / charLength(' ')); // Space = 4, Letter = 6
		
		for(int i=0;i<spaces;i++)
			message += " ";
		return message;
	}
	
	public static int wordLength(String str) {
		int length = 0;
		for(char c : str.toCharArray()) {
			length += charLength(c);
		}
		return length;
	}
	
	public static int charLength(char c) {
        if (new String("i.:,;|!").indexOf(c) != -1)
        	return 2;
        else if (new String("l ").indexOf(c) != -1)
        	return 3;
        else if (new String("tI[]").indexOf(c) != -1)
        	return 4;
        else if (new String("fk{}<>\"*()").indexOf(c) != -1)
        	return 5;
        else if (new String("abcdeghjmnopqrsuvwxyzABCDEFGHJKLMNOPQRSTUVWXYZ1234567890\\/#?$%-=_+&^").indexOf(c) != -1)
        	return 7;
        else if (new String("@~").indexOf(c) != -1)
        	return 7;
        else if (c == ' ')
        	return 3;
        else
        	return -1;
    }
	
}
