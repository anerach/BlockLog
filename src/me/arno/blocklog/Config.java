package me.arno.blocklog;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class Config {
	FileConfiguration config;
	File configFile;
	String file;
	static File configDir = new File("plugins/BlockLog");
	
	public Config() {
		this("config.yml");
	}
	
	public Config(String file) {
		if(!configDir.exists())
			configDir.mkdirs();
		
		this.file = file;
		this.configFile = new File("plugins/BlockLog/" + file);
		loadConfig();
	}
	
	private void loadConfig() {
		this.config = YamlConfiguration.loadConfiguration(configFile);
	}
	
	public FileConfiguration getConfig() {
		return config;
	}
	
	public void saveConfig() {
		try {
			getConfig().save(configFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void reloadConfig() {
		saveConfig();
		loadConfig();
	}
	
	public void createDefaults() {
		getConfig().addDefault("database.type", "SQLite");
	    getConfig().addDefault("database.delay", 1);
		getConfig().addDefault("mysql.host", "localhost");
	    getConfig().addDefault("mysql.username", "root");
	    getConfig().addDefault("mysql.password", "");
	    getConfig().addDefault("mysql.database", "");
	    getConfig().addDefault("mysql.port", 3306);
	    getConfig().addDefault("logs.grow", true);
	    getConfig().addDefault("logs.leaves", false);
	    getConfig().addDefault("logs.portal", false);
	    getConfig().addDefault("logs.form", false);
	    getConfig().addDefault("logs.spread", false);
	   	getConfig().addDefault("blocklog.wand", 369);
	    getConfig().addDefault("blocklog.results", 5);
	    getConfig().addDefault("blocklog.warning.blocks", 500);
	    getConfig().addDefault("blocklog.warning.repeat", 100);
	    getConfig().addDefault("blocklog.warning.delay", 30);
	    getConfig().addDefault("blocklog.autosave.enabled", true);
	    getConfig().addDefault("blocklog.autosave.blocks", 1000);
	    getConfig().addDefault("blocklog.reports", true);
	    getConfig().addDefault("blocklog.updates", true);
	    getConfig().options().copyDefaults(true);
	}
}
