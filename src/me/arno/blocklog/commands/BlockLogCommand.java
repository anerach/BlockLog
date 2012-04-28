package me.arno.blocklog.commands;

import java.sql.Connection;
import java.util.HashMap;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import me.arno.blocklog.BlockLog;

public class BlockLogCommand {
	public final BlockLog plugin;
	public final Logger log;
	public final Connection conn;
	public final String permission;
	public final Boolean console;
	
	public BlockLogCommand(BlockLog plugin) {
		this(plugin, null, false);
	}
	
	public BlockLogCommand(BlockLog plugin, Boolean console) {
		this(plugin, null, console);
	}
	
	public BlockLogCommand(BlockLog plugin, String permission) {
		this(plugin, permission, false);
	}
	
	public BlockLogCommand(BlockLog plugin, String permission, Boolean console) {
		this.plugin = plugin;
		this.log = plugin.log;
		this.conn = plugin.conn;
		this.permission = permission;
		this.console = console;
	}
	
	public void sendAdminMessage(String msg) {
		for (Player player : Bukkit.getServer().getOnlinePlayers()) {
	    	if (player.isOp() || player.hasPermission("blocklog.notices")) {
	    		player.sendMessage(msg);
	        }
	    }
	}
	
	public Boolean hasPermission(Player player) {
		if(player == null && console)
			return true;
		else if(player == null && console == false)
			return false;
		else if(permission != null && player != null)
			return player.hasPermission(permission);
		else if(player != null)
			return player.isOp();
		
		return false;
	}
	
	public HashMap<Integer, Integer> getSchedules() {
		return plugin.getSchedules();
	}
	
	public void addSchedule(Integer id, Integer rollback) {
		plugin.getSchedules().put(id, rollback);
	}
	
	public Server getServer() {
		return plugin.getServer();
	}
	
	public FileConfiguration getConfig() {
		return plugin.getConfig();
	}
	
	public void saveConfig() {
		plugin.saveConfig();
	}
	
	public void reloadConfig() {
		plugin.reloadConfig();
	}
	
	public Integer convertToUnixtime(Integer timeInt, String timeVal) {
		Integer time = 0;
		if(timeVal.equalsIgnoreCase("s"))
			time = (int) (System.currentTimeMillis()/1000 - timeInt);
		else if(timeVal.equalsIgnoreCase("m"))
			time = (int) (System.currentTimeMillis()/1000 - timeInt * 60);
		else if(timeVal.equalsIgnoreCase("h"))
			time = (int) (System.currentTimeMillis()/1000 - timeInt * 60 * 60);
		else if(timeVal.equalsIgnoreCase("d"))
			time =(int) (System.currentTimeMillis()/1000 - timeInt * 60 * 60 * 24);
		else if(timeVal.equalsIgnoreCase("w"))
			time = (int) (System.currentTimeMillis()/1000 - timeInt * 60 * 60 * 24 * 7);
		return time;
	}
}
