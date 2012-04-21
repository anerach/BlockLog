package me.arno.blocklog.commands;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import me.arno.blocklog.BlockLog;
import me.arno.blocklog.database.DatabaseSettings;

public class CommandRollbackList extends BlockLogCommand {
	public CommandRollbackList(BlockLog plugin) {
		super(plugin, "blocklog.rollback");
	}

	public boolean execute(Player player, Command cmd, ArrayList<String> listArgs) {
		String[] args = (String[]) listArgs.toArray();
		if(args.length > 0) {
			player.sendMessage(ChatColor.WHITE + "/bl rollbacklist");
			return true;
		}
		
		if(!hasPermission(player)) {
			player.sendMessage("You don't have permission");
			return true;
		}
		
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
