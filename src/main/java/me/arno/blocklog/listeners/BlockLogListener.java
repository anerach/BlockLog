package me.arno.blocklog.listeners;

import me.arno.blocklog.BlockLog;
import me.arno.blocklog.managers.*;
import me.arno.blocklog.util.Util;

import org.bukkit.ChatColor;
import org.bukkit.event.Listener;

public class BlockLogListener implements Listener {
	public final BlockLog plugin;
	
	private float time;
	
	public BlockLogListener() {
		this.plugin = BlockLog.getInstance();
	}
	
	public DatabaseManager getDatabaseManager() {
		return plugin.getDatabaseManager();
	}
	
	public SettingsManager getSettingsManager() {
		return plugin.getSettingsManager();
	}
	
	public DependencyManager getDependencyManager() {
		return plugin.getDependencyManager();
	}
	
	public QueueManager getQueueManager() {
		return plugin.getQueueManager();
	}
	
	public void BlocksLimitReached() {
		int queueSize = getQueueManager().getEditQueueSize();
		int maxQueueSize = getSettingsManager().getWarningBlocks();
		int delay = getSettingsManager().getWarningDelay() * 1000;
		int repeat = getSettingsManager().getWarningRepeat();
		
		if(plugin.saving == false && queueSize >= plugin.autoSave && queueSize != 0 && plugin.autoSave != 0) {
			plugin.saveLogs(0);
		} else if(plugin.autoSave == 0 && (queueSize ==  maxQueueSize || (queueSize > maxQueueSize && (queueSize % repeat == 0)))) {
			if(time < System.currentTimeMillis()) {
				time = System.currentTimeMillis() +  delay;
				Util.sendNotice(ChatColor.DARK_RED + "[BlockLog] " + ChatColor.GOLD + "BlockLog reached an internal storage of " + queueSize + "!");
				Util.sendNotice(ChatColor.DARK_RED + "[BlockLog] " + ChatColor.GOLD + "If you want to save all the queued logs use " + ChatColor.DARK_BLUE + "/bl save all");
			}
		}
	}
}
