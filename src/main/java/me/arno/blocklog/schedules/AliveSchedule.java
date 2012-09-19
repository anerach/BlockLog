package me.arno.blocklog.schedules;

import java.sql.SQLException;

import me.arno.blocklog.BlockLog;

public class AliveSchedule implements Runnable {
	private final BlockLog plugin;

	public AliveSchedule() {
		this.plugin = BlockLog.getInstance();
	}
	
	@Override
	public void run() {
		try {
			plugin.getConnection().createStatement().executeQuery("SELECT NOW()");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
