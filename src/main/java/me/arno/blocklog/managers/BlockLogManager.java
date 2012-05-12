package me.arno.blocklog.managers;

import me.arno.blocklog.BlockLog;

public class BlockLogManager {
	public SettingsManager getSettingsManager() {
		return BlockLog.plugin.getSettingsManager();
	}
	
	public DatabaseManager getDatabaseManager() {
		return BlockLog.plugin.getDatabaseManager();
	}
	
	public QueueManager getQueueManager() {
		return BlockLog.plugin.getQueueManager();
	}
}
