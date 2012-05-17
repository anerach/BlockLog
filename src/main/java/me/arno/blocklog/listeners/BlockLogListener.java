package me.arno.blocklog.listeners;

import java.util.logging.Logger;

import me.arno.blocklog.BlockLog;
import me.arno.blocklog.managers.*;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public class BlockLogListener implements Listener {
	public final BlockLog plugin;
	public final Logger log;
	
	private float time;
	
	public BlockLogListener() {
		this.plugin = BlockLog.plugin;
		this.log = plugin.log;
	}
	
	public void sendAdminMessage(String msg) {
		for (Player player : Bukkit.getServer().getOnlinePlayers()) {
	    	if (player.isOp() || player.hasPermission("blocklog.notices")) {
	    		player.sendMessage(msg);
	        }
	    }
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
		int maxQueueSize = getSettingsManager().getConfig().getInt("warning.blocks");
		int delay = getSettingsManager().getConfig().getInt("warning.delay") * 1000;
		int repeat = getSettingsManager().getConfig().getInt("warning.repeat");
		
		if(plugin.saving == false && queueSize >= plugin.autoSave && queueSize != 0 && plugin.autoSave != 0) {
			plugin.saveLogs(0);
		} else if(plugin.autoSave == 0 && (queueSize ==  maxQueueSize || (queueSize > maxQueueSize && (queueSize % repeat == 0)))) {
			if(time < System.currentTimeMillis()) {
				time = System.currentTimeMillis() +  delay;
				sendAdminMessage(ChatColor.DARK_RED + "[BlockLog] " + ChatColor.GOLD + "BlockLog reached an internal storage of " + queueSize + "!");
				sendAdminMessage(ChatColor.DARK_RED + "[BlockLog] " + ChatColor.GOLD + "If you want to save all these blocks use " + ChatColor.DARK_BLUE + "/bl save all" + ChatColor.GOLD + " or " + ChatColor.DARK_BLUE + "/bl save <blocks>");
			}
		}
	}
}
