package me.arno.blocklog.commands;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import me.arno.blocklog.BlockLog;
import me.arno.blocklog.database.DatabaseSettings;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandClear implements CommandExecutor {
	BlockLog plugin;
	Logger log;
	DatabaseSettings dbSettings;
	
	public CommandClear(BlockLog plugin) {
		this.plugin = plugin;
		this.log = plugin.log;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		dbSettings = new DatabaseSettings(plugin);
		Player player = null;
		
		if (sender instanceof Player)
			player = (Player) sender;
		
		if(!commandLabel.equalsIgnoreCase("blclear"))
			return false;
		
		if (player == null) {
			sender.sendMessage("This command can only be run by a player");
			return true;
		}
		
		if(args.length != 2)
			return false;
		
		try {
	    	Connection conn = dbSettings.getConnection();
			Statement stmt = conn.createStatement();
			
			int time;
			
			Set<String> Day = new HashSet<String>(Arrays.asList("d", "day","days"));
			Set<String> Week = new HashSet<String>(Arrays.asList("w", "week","weeks"));

			Integer timeInt = Integer.parseInt(args[0]);
			String timeVal = args[1].toLowerCase();
			String timeType;
			
			if(Day.contains(timeVal)) {
				time = (int) (System.currentTimeMillis()/1000 - timeInt * 60 * 60 * 24);
				timeType = "day(s)";
			} else if(Week.contains(timeVal)) {
				time = (int) (System.currentTimeMillis()/1000 - timeInt * 60 * 60 * 24 * 7);
				timeType = "week(s)";
			} else {
				player.sendMessage(ChatColor.DARK_GREEN + "Invalid time");
				return false;
			}
			
			Long UNIX_TIMESTAMP = System.currentTimeMillis()/1000;
			
			if(dbSettings.MySQLEnabled())
	    		stmt.executeUpdate("DELETE FROM `blocklog_blocks` WHERE `date` < " + (UNIX_TIMESTAMP - time));
			else
				stmt.executeUpdate("DELETE FROM blocklog_blocks WHERE date < " + (UNIX_TIMESTAMP - time));
			
			player.sendMessage(ChatColor.DARK_RED +"[BlockLog] removed block history older than " + timeVal + " " + timeType);
	    } catch (SQLException e) {
    		log.info("[BlockLog][BlockToDatabase][SQL] Exception!");
			log.info("[BlockLog][BlockToDatabase][SQL] " + e.getMessage());
    	}
		
		return true;
	}

}
