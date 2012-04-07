package me.arno.blocklog.commands;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import me.arno.blocklog.BlockLog;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandReport implements CommandExecutor {
	BlockLog plugin;
	Connection conn;
	
	public CommandReport(BlockLog plugin) {
		this.plugin = plugin;
		this.conn = plugin.conn;
		
	}
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		Player player = null;
		
		if (sender instanceof Player)
			player = (Player) sender;
		
		if(!cmd.getName().equalsIgnoreCase("blreport"))
			return true;
		
		if (player == null) {
			sender.sendMessage("This command can only be run by a player");
			return true;
		}
		
		if(args.length < 1)
			return false;
		
		if(!plugin.getConfig().getBoolean("blocklog.reports")) {
			player.sendMessage(ChatColor.DARK_RED + "[BlockLog] " + ChatColor.GOLD + "The report system is disabled");
			return true;
		}
			
		
		String msg = "";
		
		for(int i = 0; i < args.length;i++)
			msg += ((i == 0) ? "" : " ") + args[i];
		
		try {
			PreparedStatement stmt = conn.prepareStatement("INSERT INTO blocklog_reports (player, message, seen) VALUES (?, ?, 0)");
			stmt.setString(1, player.getName());
			stmt.setString(2, msg);
			stmt.executeUpdate();
			player.sendMessage(ChatColor.DARK_RED + "[BlockLog] " + ChatColor.GOLD + "Your report has been created");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return true;
	}

}
