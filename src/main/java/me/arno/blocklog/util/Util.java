package me.arno.blocklog.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.logging.Level;

import org.bukkit.Bukkit;

import me.arno.blocklog.BlockLog;

public class Util {
	
	public static void sendNotice(String message) {
		sendNotice(message, Level.WARNING);
	}
	
	public static void sendNotice(String message, Level level) {
		Bukkit.broadcast(message, "blocklog.notices");
		Bukkit.getLogger().log(level, message);
	}
	
	public static String getDate(long time) {
		String format = BlockLog.getInstance().getSettingsManager().getDateFormat();
		
		Calendar calendar = GregorianCalendar.getInstance();
		calendar.setTimeInMillis(time * 1000);
		
		String seconds = (calendar.get(Calendar.SECOND) > 9 ? "" : "0") + calendar.get(Calendar.SECOND);
		String minutes = (calendar.get(Calendar.MINUTE) > 9 ? "" : "0") + calendar.get(Calendar.MINUTE);
		String hours = (calendar.get(Calendar.HOUR_OF_DAY) > 9 ? "" : "0") + calendar.get(Calendar.HOUR_OF_DAY);
		
		String day =  (calendar.get(Calendar.DAY_OF_MONTH) > 9 ? "" : "0") + calendar.get(Calendar.DAY_OF_MONTH);
		String month = ((calendar.get(Calendar.MONTH) + 1) > 9 ? "" : "0") + (calendar.get(Calendar.MONTH) + 1);
		String year = (calendar.get(Calendar.YEAR) > 9 ? "" : "0") + calendar.get(Calendar.YEAR);
		
		String date = format.replace("%s", seconds).replace("%i", minutes).replace("%H", hours).replace("%d", day).replace("%m", month).replace("%Y", year);
		return date;
	}
	
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
			InputStream resourceFile = BlockLog.getInstance().getResource("resources/" + file);
			 
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
