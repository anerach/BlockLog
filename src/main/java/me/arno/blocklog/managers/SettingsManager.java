package me.arno.blocklog.managers;

import java.io.File;

import me.arno.blocklog.BlockLog;
import me.arno.blocklog.Config;
import me.arno.blocklog.logs.LogType;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;

public class SettingsManager extends BlockLogManager {
	
	public boolean isLoggingEnabled(World world, LogType... types) {
		for(LogType type : types) {
			if(type == LogType.EXPLOSION_CREEPER || type == LogType.EXPLOSION_FIREBALL || type == LogType.EXPLOSION_TNT)
				type = LogType.EXPLOSION_OTHER;
			
			Config config = new Config("worlds" + File.separator + world.getName() + ".yml");
			
			if(!config.getConfig().getBoolean(type.name()))
				return false;
		}
		return true;
	}
	
	public boolean isDebugEnabled() {
		return getConfig().getBoolean("blocklog.debug");
	}
	
	public boolean isPurgeEnabled(String table) {
		return getConfig().getBoolean("purge." + table + ".enabled");
	}
	
	public boolean isPurgeLoggingEnabled() {
		return getConfig().getBoolean("purge.log");
	}
	
	public int getPurgeDate(String table) {
		return getConfig().getInt("purge." + table + ".days");
	}
	
	public int getBlockSaveDelay() {
		return getConfig().getInt("blocklog.save-delay");
	}
	
	public boolean isAutoSaveEnabled() {
		return getConfig().getBoolean("auto-save.enabled");
	}
	
	public int getAutoSaveBlocks() {
		return getConfig().getInt("auto-save.blocks");
	}

	public boolean saveOnWorldSave() {
		return (isAutoSaveEnabled() && getConfig().getBoolean("auto-save.world-save"));
	}
	
	public boolean isReportsEnabled() {
		return getConfig().getBoolean("blocklog.reports");
	}
	
	public boolean isUpdatesEnabled() {
		return getConfig().getBoolean("blocklog.updates");
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
	
	public String getDatabasePrefix() {
		return getConfig().getString("database.prefix");
	}
	
	public FileConfiguration getConfig() {
		return BlockLog.getInstance().getConfig();
	}
	
	public void saveConfig() {
		BlockLog.getInstance().saveConfig();
	}
	
	public void reloadConfig() {
		BlockLog.getInstance().reloadConfig();
	}
}
