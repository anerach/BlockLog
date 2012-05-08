package me.arno.blocklog;

import java.io.File;

import me.arno.blocklog.logs.LogType;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;

public class SettingsManager {
	public final BlockLog plugin;
	
	public SettingsManager() {
		this.plugin = BlockLog.plugin;
	}
	
	public boolean isLoggingEnabled(World world, LogType type) {
		if(type == LogType.EXPLOSION_CREEPER || type == LogType.EXPLOSION_GHAST || type == LogType.EXPLOSION_TNT)
			type = LogType.EXPLOSION;
		
		Config config = new Config("world" + File.pathSeparator + world.getName() + ".yml");
		
		return config.getConfig().getBoolean(type.name(), false);
	}
	
	public boolean isMetricsEnabled() {
		return getConfig().getBoolean("blocklog.metrics");
	}
	
	public String getDateFormat() {
		return getConfig().getString("blocklog.dateformat");
	}
	
	public Material getWand() {
		return Material.getMaterial(getConfig().getInt("blocklog.wand"));
	}
	
	public int getMaxResults() {
		return getConfig().getInt("blocklog.results");
	}
	
	public FileConfiguration getConfig() {
		return new Config().getConfig();
	}
}
