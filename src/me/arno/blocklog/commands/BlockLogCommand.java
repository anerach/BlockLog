package me.arno.blocklog.commands;

import java.sql.Connection;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import me.arno.blocklog.BlockLog;

public class BlockLogCommand {
	public final BlockLog plugin;
	public final Logger log;
	public final Connection conn;
	
	public BlockLogCommand(BlockLog plugin) {
		this.plugin = plugin;
		this.log = plugin.log;
		this.conn = plugin.conn;
	}
	
	public void sendAdminMessage(String msg) {
		for (Player player : Bukkit.getServer().getOnlinePlayers()) {
	    	if (player.isOp() || player.hasPermission("blocklog.notices")) {
	    		player.sendMessage(msg);
	        }
	    }
	}
	
	public FileConfiguration getConfig() {
		return plugin.cfg.getConfig();
	}
}
