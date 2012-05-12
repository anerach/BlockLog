package me.arno.blocklog.commands;

import java.sql.Connection;
import java.util.HashMap;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import me.arno.blocklog.BlockLog;
import me.arno.blocklog.managers.DatabaseManager;
import me.arno.blocklog.managers.QueueManager;
import me.arno.blocklog.managers.SettingsManager;

public class BlockLogCommand {
	public final BlockLog plugin;
	public final Logger log;
	public final Connection conn;
	public final String permission;
	public final Boolean console;
	
	public String usage;
	
	public BlockLogCommand() {
		this(null, false);
	}
	
	public BlockLogCommand(Boolean console) {
		this(null, console);
	}
	
	public BlockLogCommand(String permission) {
		this(permission, false);
	}
	
	public BlockLogCommand(String permission, Boolean console) {
		this.plugin = BlockLog.plugin;
		this.log = plugin.log;
		this.conn = plugin.conn;
		this.permission = permission;
		this.console = console;
	}
	
	public SettingsManager getSettingsManager() {
		return plugin.getSettingsManager();
	}
	
	public DatabaseManager getDatabaseManager() {
		return plugin.getDatabaseManager();
	}
	
	public QueueManager getQueueManager() {
		return plugin.getQueueManager();
	}
	
	public void setCommandUsage(String usage) {
		this.usage = usage;
	}
	
	public String getCommandUsage() {
		return usage;
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
	
	public Integer convertToUnixtime(Integer timeInt, String timeVal) {
		Integer time = 0;
		if(timeVal.equalsIgnoreCase("s"))
			time = (int) (System.currentTimeMillis()/1000 - timeInt);
		else if(timeVal.equalsIgnoreCase("m"))
			time = (int) (System.currentTimeMillis()/1000 - timeInt * 60);
		else if(timeVal.equalsIgnoreCase("h"))
			time = (int) (System.currentTimeMillis()/1000 - timeInt * 60 * 60);
		else if(timeVal.equalsIgnoreCase("d"))
			time = (int) (System.currentTimeMillis()/1000 - timeInt * 60 * 60 * 24);
		else if(timeVal.equalsIgnoreCase("w"))
			time = (int) (System.currentTimeMillis()/1000 - timeInt * 60 * 60 * 24 * 7);
		return time;
	}
	
	public boolean execute(Player player, Command cmd, String[] args) {
		player.sendMessage("This command doesn't exists. Say " + ChatColor.GOLD + "/bl help " + ChatColor.WHITE + "for a list of available commands.");
		return true;
	}
}
