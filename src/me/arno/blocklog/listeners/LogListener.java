package me.arno.blocklog.listeners;

import java.util.logging.Logger;

import me.arno.blocklog.BlockLog;
import me.arno.blocklog.Config;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public class LogListener implements Listener {
	BlockLog plugin;
	Logger log;
	Config cfg;
	float time;
	
	public LogListener(BlockLog plugin) {
		this.plugin = plugin;
		this.log = plugin.log;
		this.cfg = plugin.cfg;
	}
	
	public void sendAdminMessage(String msg) {
		for (Player player : Bukkit.getServer().getOnlinePlayers()) {
	    	if (player.isOp() || player.hasPermission("blocklog.notices")) {
	    		player.sendMessage(msg);
	        }
	    }
	}
	
	public void BlocksLimitReached() {
		int BlockSize = plugin.blocks.size();
		int WarningBlockSize = cfg.getConfig().getInt("blocklog.warning.blocks");
		int WarningDelay = cfg.getConfig().getInt("blocklog.warning.delay") * 1000;
		int WarningRepeat = cfg.getConfig().getInt("blocklog.warning.repeat");
		
		if(BlockSize == plugin.autoSave && BlockSize != 0 && plugin.autoSave != 0) {
			plugin.saveLogs(0);
		} else if(plugin.autoSave == 0 && (BlockSize ==  WarningBlockSize || (BlockSize > WarningBlockSize && (BlockSize % WarningRepeat == 0)))) {
			if(time < System.currentTimeMillis()) {
				time = System.currentTimeMillis() +  WarningDelay;
				sendAdminMessage(ChatColor.DARK_RED + "[BlockLog] " + ChatColor.GOLD + "BlockLog reached an internal storage of " + BlockSize + "!");
				sendAdminMessage(ChatColor.DARK_RED + "[BlockLog] " + ChatColor.GOLD + "If you want to save all these blocks use " + ChatColor.DARK_BLUE + "/blfullsave" + ChatColor.GOLD + " or " + ChatColor.DARK_BLUE + "/blsave <blocks>");
			}
		}
	}
}
