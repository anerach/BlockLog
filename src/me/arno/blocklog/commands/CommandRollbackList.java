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
		Integer clauses = args.length/2;
		
		if(!clauses.toString().matches("[0-9]*")) {
			player.sendMessage("Invalid amount of args");
			player.sendMessage(ChatColor.WHITE + "/bl rollbacklist [id <value>] [player <value>] [since <value>] [until <value>] [area <value>]");
			return true;
		}
		
		if(!hasPermission(player)) {
			player.sendMessage("You don't have permission");
			return true;
		}
		
		try {
			String target = null;
			Integer id = 0;
			Integer untilTime = 0;
			Integer sinceTime = 0;
			Integer area = 0;
			
			for(int i=0;i<args.length;i+=2) {
				String type = args[i];
				String value = args[i+1];
				if(type.equalsIgnoreCase("player")) {
					target = value;
				} else if(type.equalsIgnoreCase("id")) {
					id = Integer.valueOf(value);
				} else if(type.equalsIgnoreCase("area")) {
					area = Integer.valueOf(value);
				} else if(type.equalsIgnoreCase("since")) {
					Character c = value.charAt(value.length() - 1);
					sinceTime = convertToUnixtime(Integer.valueOf(value.replace(c, ' ').trim()), c.toString());
				} else if(type.equalsIgnoreCase("until")) {
					Character c = value.charAt(value.length() - 1);
					untilTime = convertToUnixtime(Integer.valueOf(value.replace(c, ' ').trim()), c.toString());
				}
			}
			
			if(sinceTime != 0 && sinceTime > untilTime) {
				player.sendMessage(ChatColor.WHITE + "Until can't be bigger than since.");
				return true;
			}
			
			Query query = new Query("blocklog_rollbacks");
			query.addLeftJoin("blocklog_undos", "id", "rollback_id");
			
			query.addSelect("blocklog_rollbacks.id","blocklog_rollbacks.player");
			query.addSelectDateAs("blocklog_rollbacks.date", "date");
			
			query.addSelectAs("blocklog_undos.player", "uplayer");
			
			if(target != null)
				query.addWhere("blocklog_rollbacks.player", target);
			if(id != 0)
				query.addWhere("blocklog_rollbacks.id", id.toString());
			if(area != 0)
				query.addWhere("blocklog_rollbacks.area", area.toString());
			if(sinceTime != 0)
				query.addWhere("blocklog_rollbacks.date", sinceTime.toString(), ">");
			if(untilTime != 0)
				query.addWhere("blocklog_rollbacks.date", untilTime.toString(), "<");
			
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
