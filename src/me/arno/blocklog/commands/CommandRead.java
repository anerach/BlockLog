package me.arno.blocklog.commands;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import me.arno.blocklog.BlockLog;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandRead implements CommandExecutor {
	BlockLog plugin;
	Connection conn;
	
	public CommandRead(BlockLog plugin) {
		this.plugin = plugin;
		this.conn = plugin.conn;
		
	}
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		Player player = null;
		
		if (sender instanceof Player)
			player = (Player) sender;
		
		if(!cmd.getName().equalsIgnoreCase("blread"))
			return true;
		
		if (player == null) {
			sender.sendMessage("This command can only be run by a player");
			return true;
		}
		
		if(args.length > 1)
			return false;
		
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
				while(reports.next()) {
					player.sendMessage(ChatColor.DARK_RED + "[#" + reports.getString("id") + "] " + ChatColor.GOLD + reports.getString("player"));
					player.sendMessage(ChatColor.BLUE + reports.getString("message"));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return true;
	}

}
