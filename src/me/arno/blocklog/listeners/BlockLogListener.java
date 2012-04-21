package me.arno.blocklog.listeners;

import java.util.logging.Logger;

import me.arno.blocklog.BlockLog;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public class BlockLogListener implements Listener {
	public final BlockLog plugin;
	public final Logger log;
	
	private float time;
	
	public BlockLogListener(BlockLog plugin) {
		this.plugin = plugin;
		this.log = plugin.log;
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
		int WarningBlockSize = getConfig().getInt("blocklog.warning.blocks");
		int WarningDelay = getConfig().getInt("blocklog.warning.delay") * 1000;
		int WarningRepeat = getConfig().getInt("blocklog.warning.repeat");
		
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
	
	public FileConfiguration getConfig() {
		return plugin.getConfig();
	}
}
