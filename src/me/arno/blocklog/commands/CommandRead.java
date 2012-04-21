package me.arno.blocklog.commands;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import me.arno.blocklog.BlockLog;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

public class CommandRead extends BlockLogCommand {
	public CommandRead(BlockLog plugin) {
		super(plugin);
	}
	
	public boolean execute(Player player, Command cmd, String[] args) {
		if(args.length > 1) {
			player.sendMessage(ChatColor.WHITE + "/bl read [id]");
			return true;
		}
		
		if(!plugin.getConfig().getBoolean("blocklog.reports")) {
			player.sendMessage(ChatColor.DARK_RED + "[BlockLog] " + ChatColor.GOLD + "The report system is disabled");
			return true;
		}
		
		try {
			Statement stmt = conn.createStatement();
			if(args.length == 0) {
				ResultSet reports = stmt.executeQuery("SELECT * FROM blocklog_reports WHERE seen = 0");
				player.sendMessage(ChatColor.DARK_RED + "[Reports]");
				while(reports.next()) {
					player.sendMessage(String.format("[#%s] %s", reports.getString("id"), reports.getString("player")));
				}
			} else {
				ResultSet reports = stmt.executeQuery("SELECT * FROM blocklog_reports WHERE id = " + args[0]);
				
				reports.next();
				player.sendMessage(ChatColor.DARK_RED + "[#" + reports.getString("id") + "] " + ChatColor.GOLD + reports.getString("player"));
				player.sendMessage(ChatColor.BLUE + reports.getString("message"));
				stmt.executeUpdate("UPDATE blocklog_reports SET seen = 1 WHERE id = " + args[0]);
			
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return true;
	}

}
