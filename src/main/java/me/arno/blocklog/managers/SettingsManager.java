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
			if(type == LogType.CREEPER || type == LogType.FIREBALL || type == LogType.TNT)
				type = LogType.EXPLOSION;
			
			Config config = new Config("worlds" + File.separator + world.getName() + ".yml");
			
			if(!config.getConfig().getBoolean(type.name()))
				return false;
		}
		return true;
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
		return getConfig().getInt("blocklog.delay");
	}
	
	public boolean isAutoSaveEnabled() {
		return getConfig().getBoolean("blocklog.autosave.enabled");
	}
	
	public int getAutoSaveBlocks() {
		return getConfig().getInt("blocklog.autosave.blocks");
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
	
	public FileConfiguration getConfig() {
		return BlockLog.plugin.getConfig();
	}
	
	public void saveConfig() {
		BlockLog.plugin.saveConfig();
	}
	
	public void reloadConfig() {
		BlockLog.plugin.reloadConfig();
	}
}
