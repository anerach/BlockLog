package me.arno.blocklog.commands;

import java.sql.ResultSet;
import java.sql.SQLException;

import me.arno.blocklog.ReportStatus;
import me.arno.blocklog.logs.ReportEntry;
import me.arno.blocklog.util.Query;
import me.arno.blocklog.util.Util;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class CommandRead extends BlockLogCommand {
	public CommandRead() {
		super("blocklog.report.read", true);
		setCommandUsage("/bl read [id] [player <value>] [since <value>] [until <value]");
	}
	
	@Override
	public boolean execute(CommandSender sender, Command cmd, String[] args) {
		if(args.length > 6)
			return false;
		
		if(!getSettingsManager().isReportsEnabled()) {
			sender.sendMessage(ChatColor.DARK_RED + "[BlockLog] " + ChatColor.GOLD + "The report system is disabled");
			return true;
		}
		
		if(!hasPermission(sender)) {
			sender.sendMessage("You don't have permission");
			return true;
		}
		
		try {
			Query query = new Query("blocklog_reports");
			query.select("*").orderBy("date", "DESC");
			
			if(args.length == 0) {
				query.where("status", 3, "<");
				ResultSet reports = query.getResult();
				sender.sendMessage(ChatColor.DARK_RED + "[Reports]");
				while(reports.next()) {
					sender.sendMessage(String.format("[#%s] %s", reports.getString("id"), reports.getString("player")));
				}
			} else if(args.length == 1) {
				if(Util.isNumeric(args[0])) {
					ReportEntry report = new ReportEntry(Integer.valueOf(args[0]));
					sender.sendMessage(ChatColor.DARK_RED + "[#" + report.getId() + "] Player: " + ChatColor.GOLD + report.getPlayer() + " Status: " + report.getStatus().toString());
					sender.sendMessage(ChatColor.BLUE + report.getMessage());
				}
			} else {
				String target = null;
				int untilTime = 0;
				int sinceTime = 0;
				int status = 0;
				
				for(int i=0;i<args.length;i+=2) {
					String type = args[i];
					String value = args[i+1];
					if(type.equalsIgnoreCase("player")) {
						target = value;
					} else if(type.equalsIgnoreCase("status")) {
						status = ReportStatus.values()[Integer.valueOf(value.toUpperCase())].getId();
					} else if(type.equalsIgnoreCase("since")) {
						Character c = value.charAt(value.length() - 1);
						sinceTime = convertToUnixtime(Integer.valueOf(value.replace(c, ' ').trim()), c.toString());
					} else if(type.equalsIgnoreCase("until")) {
						Character c = value.charAt(value.length() - 1);
						untilTime = convertToUnixtime(Integer.valueOf(value.replace(c, ' ').trim()), c.toString());
					}
				}
				
				if(untilTime != 0 && sinceTime > untilTime) {
					sender.sendMessage(ChatColor.WHITE + "Until can't be bigger than since.");
					return true;
				}
				
				query.where("status", status);
				if(target != null)
					query.where("player", target);
				if(sinceTime != 0)
					query.where("date", sinceTime, ">");
				if(untilTime != 0)
					query.where("date", untilTime, "<");
				
				ResultSet reports = query.getResult();
				sender.sendMessage(ChatColor.DARK_RED + "[Reports]");
				while(reports.next()) {
					sender.sendMessage(String.format("[#%s] %s", reports.getString("id"), reports.getString("player")));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return true;
	}

}
