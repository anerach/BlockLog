package me.arno.blocklog.util;

public class Text {
	
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
