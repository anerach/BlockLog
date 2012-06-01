package me.arno.blocklog.util;

import java.util.logging.Logger;

import me.arno.blocklog.BlockLog;

public class Debug {
	private static final String plugin = "BlockLog";
	private static final Logger log = Logger.getLogger("Minecraft");
	
	public static void SQL(String msg) {
		log("SQL", msg);
	}
	
	public static void general(String msg) {
		log("General", msg);
	}
	
	private static void log(String type, String msg) {
		if(BlockLog.plugin.getSettingsManager().isDebugEnabled())
			log.warning("[" + plugin + "][Debug][" + type + "] " + msg);
	}
}
