package me.arno.blocklog.schedules;

import me.arno.blocklog.BlockLog;
import me.arno.blocklog.util.Util;

public class AliveSchedule implements Runnable {
	
	@Override
	public void run() {
		Util.sendNotice("Sending alive query");
		try {
			BlockLog.getInstance().getConnection().createStatement().executeQuery("SELECT * FROM blocklog_data LIMIT 1");
		} catch(Exception e) {
			Util.sendNotice("Something went wrong while sending the alive query");
		}
	}
}
