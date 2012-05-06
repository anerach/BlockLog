package me.arno.blocklog.commands;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import me.arno.blocklog.BlockLog;
import me.arno.blocklog.database.Query;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

public class CommandRead extends BlockLogCommand {
	public CommandRead(BlockLog plugin) {
		super(plugin, "blocklog.report.read");
	}
	
	public boolean execute(Player player, Command cmd, String[] args) {
		if(args.length > 6) {
			player.sendMessage(ChatColor.WHITE + "/bl read [id] [player <value>] [since <value>] [until <value]");
			return true;
		}
		
		if(!getConfig().getBoolean("blocklog.reports")) {
			player.sendMessage(ChatColor.DARK_RED + "[BlockLog] " + ChatColor.GOLD + "The report system is disabled");
			return true;
		}
		
		if(!hasPermission(player)) {
			player.sendMessage("You don't have permission");
			return true;
		}
		
		try {
			Statement stmt = conn.createStatement();
			Query query = new Query("blocklog_reports");
			query.addSelect("*").addOrderBy("date", "DESC");
			
			if(args.length == 0) {
				query.addWhere("seen", 0);
				ResultSet reports = query.getResult();
				player.sendMessage(ChatColor.DARK_RED + "[Reports]");
				while(reports.next()) {
					player.sendMessage(String.format("[#%s] %s", reports.getString("id"), reports.getString("player")));
				}
			} else if(args.length == 1) {
				query.addWhere("id", args[0]);
				ResultSet reports = query.getResult();
				reports.next();
				player.sendMessage(ChatColor.DARK_RED + "[#" + reports.getString("id") + "] " + ChatColor.GOLD + reports.getString("player"));
				player.sendMessage(ChatColor.BLUE + reports.getString("message"));
				stmt.executeUpdate("UPDATE blocklog_reports SET seen = 1 WHERE id = " + args[0]);
			} else {
				String target = null;
				Integer untilTime = 0;
				Integer sinceTime = 0;
				
				for(int i=0;i<args.length;i+=2) {
					String type = args[i];
					String value = args[i+1];
					if(type.equalsIgnoreCase("player")) {
						target = value;
					} else if(type.equalsIgnoreCase("since")) {
						Character c = value.charAt(value.length() - 1);
						sinceTime = convertToUnixtime(Integer.valueOf(value.replace(c, ' ').trim()), c.toString());
					} else if(type.equalsIgnoreCase("until")) {
						Character c = value.charAt(value.length() - 1);
						untilTime = convertToUnixtime(Integer.valueOf(value.replace(c, ' ').trim()), c.toString());
					}
				}
				
				if(untilTime != 0 && sinceTime > untilTime) {
					player.sendMessage(ChatColor.WHITE + "Until can't be bigger than since.");
					return true;
				}
				
				query.addWhere("seen", 0);
				if(target != null)
					query.addWhere("player", target);
				if(sinceTime != 0)
					query.addWhere("date", sinceTime.toString(), ">");
				if(untilTime != 0)
					query.addWhere("date", untilTime.toString(), "<");
				
				ResultSet reports = query.getResult();
				player.sendMessage(ChatColor.DARK_RED + "[Reports]");
				while(reports.next()) {
					player.sendMessage(String.format("[#%s] %s", reports.getString("id"), reports.getString("player")));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return true;
	}

}
