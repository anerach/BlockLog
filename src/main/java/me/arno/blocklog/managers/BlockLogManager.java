package me.arno.blocklog.managers;

public class BlockLogManager {
	private DatabaseManager databaseManager;
	private SettingsManager settingsManager;
	private QueueManager queueManager;
	
	public BlockLogManager() {
		databaseManager = new DatabaseManager();
		settingsManager = new SettingsManager();
		queueManager = new QueueManager();
	}
	
	public DatabaseManager getDatabaseManager() {
		return databaseManager;
	}
	
	public SettingsManager getSettingsManager() {
		return settingsManager;
	}
	
	public QueueManager getQueueManager() {
		return queueManager;
	}
}
