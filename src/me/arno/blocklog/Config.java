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
}
