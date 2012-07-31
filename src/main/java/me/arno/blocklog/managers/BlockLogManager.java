package me.arno.blocklog.managers;

import me.arno.blocklog.BlockLog;

public class BlockLogManager {
	public SettingsManager getSettingsManager() {
		return BlockLog.getInstance().getSettingsManager();
	}
	
	public DatabaseManager getDatabaseManager() {
		return BlockLog.getInstance().getDatabaseManager();
	}
	
	public QueueManager getQueueManager() {
		return BlockLog.getInstance().getQueueManager();
	}
}
