package me.arno.blocklog.managers;

public class BlockLogManager {
	private DatabaseManager databaseManager;
	private SettingsManager settingsManager;
	private LogManager logManager;
	
	public BlockLogManager() {
		databaseManager = new DatabaseManager();
		settingsManager = new SettingsManager();
		logManager = new LogManager();
	}
	
	public DatabaseManager getDatabaseManager() {
		return databaseManager;
	}
	
	public SettingsManager getSettingsManager() {
		return settingsManager;
	}
	
	public LogManager getLogManager() {
		return logManager;
	}
}
