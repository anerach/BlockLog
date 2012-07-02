package me.arno.blocklog.util;

import java.util.HashMap;

public class Syntax {
	private final HashMap<String, String> clauses = new HashMap<String, String>();
	
	public Syntax(String[] args) {
		this(args, 0);
	}
	
	public Syntax(String[] args, int start) {
		for(int i=start;i<args.length;i+=2) {
			String arg = args[i];
			String value = args[i+1];
			
			this.clauses.put(arg, value);
		}
	}
	
	public boolean containsArg(String arg) {
		return clauses.containsKey(arg);
	}
	
	public String getString(String arg) {
		return clauses.containsKey(arg) ? clauses.get(arg) : null;
	}
	
	public int getInt(String arg) {
		return getInt(arg, 0);
	}
	
	public int getInt(String arg, int defaultVal) {
		String value = clauses.containsKey(arg) ? clauses.get(arg) : null;
		
		if(value == null)
			return defaultVal;
		
		if(Util.isNumeric(value))
			return Integer.valueOf(value);
		
		return defaultVal;
	}
	
	public int getTime(String arg) {
		return getTime(arg, "0s");
	}
	
	public int getTime(String arg, String defaultTime) {
		String value = clauses.containsKey(arg) ? clauses.get(arg) : defaultTime;
		
		if(value.equalsIgnoreCase("0s"))
			return 0;
		
		char character = value.charAt(value.length() - 1);
		int time = Integer.valueOf(value.replace(character, ' ').trim());
		String timeVal = Character.toString(character);
		
		if(timeVal.equalsIgnoreCase("s"))
			return time;
		else if(timeVal.equalsIgnoreCase("m"))
			return time * 60;
		else if(timeVal.equalsIgnoreCase("h"))
			return time * 60 * 60;
		else if(timeVal.equalsIgnoreCase("d"))
			return time * 60 * 60 * 24;
		else if(timeVal.equalsIgnoreCase("w"))
			return time * 60 * 60 * 24 * 7;
		return 0;
	}
	
	public int getTimeFromNow(String arg) {
		return getTimeFromNow(arg, "0s");
	}
	
	public int getTimeFromNow(String arg, String defaultTime) {
		int time = getTime(arg, defaultTime);
		
		if(time == 0)
			return 0;
		
		return (int) (System.currentTimeMillis()/1000 - getTime(arg, defaultTime));
	}
}
