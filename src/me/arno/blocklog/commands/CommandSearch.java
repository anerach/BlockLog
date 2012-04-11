package me.arno.blocklog.commands;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

import me.arno.blocklog.BlockLog;
import me.arno.blocklog.database.DatabaseSettings;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandSearch implements CommandExecutor {
	BlockLog plugin;
	Connection conn;
	Logger log;
	
	public CommandSearch(BlockLog plugin) {
		this.plugin = plugin;
		this.conn = plugin.conn;
		this.log = plugin.log;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		Player player = null;
		
		if (sender instanceof Player)
			player = (Player) sender;
		
		if(!cmd.getName().equalsIgnoreCase("blsearch"))
			return true;
		
		if (player == null) {
			sender.sendMessage("This command can only be run by a player");
			return true;
		}
		
		if(args.length != 1)
			return false;
		
		try {
			Statement stmt = conn.createStatement();
			ResultSet actions;
			
			if(DatabaseSettings.DBType().equalsIgnoreCase("mysql"))
				actions = stmt.executeQuery(String.format("SELECT *, FROM_UNIXTIME(date, '%d-%m-%Y %H:%i:%s') AS date FROM blocklog_blocks WHERE player = '%s' ORDER BY date DESC LIMIT %s", args[0], plugin.getConfig().getInt("blocklog.results")));
			else
				actions = stmt.executeQuery(String.format("SELECT *, datetime(date, 'unixepoch', 'localtime') AS date FROM blocklog_blocks WHERE player = '%s' ORDER BY date DESC LIMIT %s", args[0], plugin.getConfig().getInt("blocklog.results")));
			
			while(actions.next()) {
				String name = Material.getMaterial(actions.getInt("block_id")).toString();
				int type = actions.getInt("type");
				
				if(type == 0)
					player.sendMessage(ChatColor.BLUE + "[" + actions.getString("date") + "][World:" + actions.getString("world") + ", X:" + actions.getString("x") + ", Y:" + actions.getString("y") + ", Z:" + actions.getString("z") + "] " + ChatColor.GOLD + actions.getString("player") + ChatColor.DARK_GREEN + " broke a " + ChatColor.GOLD + name);
				else if(type == 1)
					player.sendMessage(ChatColor.BLUE + "[" + actions.getString("date") + "][World:" + actions.getString("world") + ", X:" + actions.getString("x") + ", Y:" + actions.getString("y") + ", Z:" + actions.getString("z") + "] " + ChatColor.GOLD + actions.getString("player") + ChatColor.DARK_GREEN + " placed a " + ChatColor.GOLD + name);
			}
		} catch(SQLException e) {
			
		}
		return true;
	}

}
