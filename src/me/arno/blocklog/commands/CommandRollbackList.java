package me.arno.blocklog.commands;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import me.arno.blocklog.BlockLog;
import me.arno.blocklog.database.Query;

public class CommandRollbackList extends BlockLogCommand {
	public CommandRollbackList(BlockLog plugin) {
		super(plugin, "blocklog.rollback");
	}

	public boolean execute(Player player, Command cmd, String[] args) {
		if(args.length > 0) {
			player.sendMessage(ChatColor.WHITE + "/bl rollbacklist");
			return true;
		}
		
		if(!hasPermission(player)) {
			player.sendMessage("You don't have permission");
			return true;
		}
		
		try {
			Query query = new Query("blocklog_rollbacks");
			query.addLeftJoin("blocklog_undos", "id", "rollback_id");
			
			query.addSelect("blocklog_rollbacks.id","blocklog_rollbacks.player");
			query.addSelectDateAs("blocklog_rollbacks.date", "date");
			
			query.addSelectAs("blocklog_undos.player", "uplayer");
			
			query.addOrderBy("blocklog_rollbacks.date", "DESC");
			query.addLimit(getConfig().getInt("blocklog.results"));
			
			Statement rollbacksStmt = conn.createStatement();
			ResultSet rollbacks = rollbacksStmt.executeQuery(query.getQuery());
			
			player.sendMessage(ChatColor.DARK_RED + "BlockLog Rollbacks (" + plugin.getConfig().getString("blocklog.results") + " Last Rollbacks)");
			while(rollbacks.next()) {
				if(rollbacks.getString("uplayer") != null)
					player.sendMessage(ChatColor.YELLOW + "[#" + rollbacks.getString("id") + "]" + ChatColor.BLUE + "[" + rollbacks.getString("date") + "] " + ChatColor.GOLD + rollbacks.getString("player") + ChatColor.GREEN + " Undone by " + ChatColor.GOLD + rollbacks.getString("uplayer"));
				else
					player.sendMessage(ChatColor.YELLOW + "[#" + rollbacks.getString("id") + "]" + ChatColor.BLUE + "[" + rollbacks.getString("date") + "] " + ChatColor.GOLD + rollbacks.getString("player"));
			}
			return true;
		} catch(SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
}
