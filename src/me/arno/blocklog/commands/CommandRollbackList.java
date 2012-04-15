package me.arno.blocklog.commands;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.arno.blocklog.BlockLog;
import me.arno.blocklog.database.DatabaseSettings;

public class CommandRollbackList implements CommandExecutor {
	BlockLog plugin;
	Logger log;
	Connection conn;
	
	public CommandRollbackList(BlockLog plugin) {
		this.plugin = plugin;
		this.log = plugin.log;
		this.conn = plugin.conn;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		Player player = null;
		
		if (sender instanceof Player)
			player = (Player) sender;
		
		if(!cmd.getName().equalsIgnoreCase("blrollbacklist"))
			return false;
		
		if (player == null) {
			sender.sendMessage("This command can only be run by a player");
			return true;
		}
		
		if(args.length > 0)
			return false;
		
		try {
			Statement rollbacksStmt = conn.createStatement();
			ResultSet rollbacks;
			
			if(DatabaseSettings.DBType().equalsIgnoreCase("mysql")) {
				rollbacks = rollbacksStmt.executeQuery(
						"SELECT blocklog_rollbacks.id, blocklog_rollbacks.player, FROM_UNIXTIME(blocklog_rollbacks.date, '%d-%m-%Y %H:%i:%s') AS rbdate, blocklog_undos.player AS uplayer, FROM_UNIXTIME(blocklog_undos.date, '%d-%m-%Y %H:%i:%s') AS udate " +
						"FROM blocklog_rollbacks " +
						"LEFT JOIN blocklog_undos " +
						"ON blocklog_rollbacks.id = blocklog_undos.rollback_id " +
						"ORDER BY blocklog_rollbacks.date " +
						"DESC LIMIT " + plugin.getConfig().getInt("blocklog.results")
				);
			} else {
				rollbacks = rollbacksStmt.executeQuery(
						"SELECT blocklog_rollbacks.id, blocklog_rollbacks.player, datetime(blocklog_rollbacks.date, 'unixepoch', 'localtime') AS rbdate, blocklog_undos.player AS uplayer, datetime(blocklog_undos.date, 'unixepoch', 'localtime') AS udate " +
						"FROM blocklog_rollbacks " +
						"LEFT JOIN blocklog_undos " +
						"ON blocklog_rollbacks.id = blocklog_undos.rollback_id " +
						"ORDER BY blocklog_rollbacks.date " +
						"DESC LIMIT " + plugin.getConfig().getInt("blocklog.results")
				);
			}
			
			player.sendMessage(ChatColor.DARK_RED + "BlockLog Rollbacks (" + plugin.getConfig().getString("blocklog.results") + " Last Rollbacks)");
			while(rollbacks.next()) {
				player.sendMessage(ChatColor.YELLOW + "[#" + rollbacks.getString("id") + "]" + ChatColor.BLUE + "[" + rollbacks.getString("rbdate") + "] " + ChatColor.GOLD + rollbacks.getString("player"));
				if(rollbacks.getString("uplayer") != null)
					player.sendMessage(ChatColor.YELLOW + "[#" + rollbacks.getString("id") + "]" + ChatColor.BLUE + "[" + rollbacks.getString("udate") + "] " + ChatColor.GREEN + "Undone by " + ChatColor.GOLD + rollbacks.getString("uplayer"));
			}
			return true;
		} catch(SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
}
