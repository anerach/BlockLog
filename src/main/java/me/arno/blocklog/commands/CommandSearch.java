package me.arno.blocklog.commands;

import java.sql.ResultSet;
import java.sql.SQLException;

import me.arno.blocklog.util.Query;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class CommandSearch extends BlockLogCommand {
	public CommandSearch() {
		super("blocklog.search", true);
		setCommandUsage("/bl search <table> [player <value>] [since <value>] [until <value>]");
	}

	@Override
	public boolean execute(CommandSender sender, Command cmd, String[] args) {
		if(args.length < 3)
			return false;
		
		if(!hasPermission(sender)) {
			sender.sendMessage("You don't have permission");
			return true;
		}
		
		try {
			String table = args[0];
			String target = null;
			String victem = null;
			String killer = null;
			Integer untilTime = 0;
			Integer sinceTime = 0;
			
			for(int i=1;i<args.length;i+=2) {
				String type = args[i];
				String value = args[i+1];
				if(table.equalsIgnoreCase("kills")) {
					if(type.equalsIgnoreCase("victem")) {
						victem = value;
					} else if(type.equalsIgnoreCase("killer")) {
						killer = value;
					}
				} else {
					if(type.equalsIgnoreCase("player")) {
						target = value;
					}
				}
				
				if(type.equalsIgnoreCase("since")) {
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
			
			Query query = new Query("blocklog_" + table);
			query.select("*");
			query.selectDateAs("date" , "ldate");
			if(target != null)
				query.where("player", target);
			if(victem != null)
				query.where("victem", victem);
			if(killer != null)
				query.where("killer", killer);
			if(sinceTime != 0)
				query.where("date", sinceTime.toString(), "<");
			if(untilTime != 0)
				query.where("date", untilTime.toString(), ">");
			
			query.orderBy("date", "DESC");
			query.limit(getSettingsManager().getMaxResults());
			
			ResultSet actions = query.getResult();
			
			while(actions.next()) {
				if(table.equalsIgnoreCase("chat"))
					sender.sendMessage(ChatColor.DARK_RED + "[" + table + "]" + ChatColor.BLUE + "[" + actions.getString("ldate") + "] " + ChatColor.GOLD + "Player: " + ChatColor.GREEN + actions.getString("player") + ChatColor.GOLD +  " Message: " + ChatColor.GREEN + actions.getString("message"));
				else if(table.equalsIgnoreCase("commands"))
					sender.sendMessage(ChatColor.DARK_RED + "[" + table + "]" + ChatColor.BLUE + "[" + actions.getString("ldate") + "] " + ChatColor.GOLD + "Player: " + ChatColor.GREEN + actions.getString("player") + ChatColor.GOLD +  " Executed: " + ChatColor.GREEN + actions.getString("command"));
				else if(table.equalsIgnoreCase("kills"))
					sender.sendMessage(ChatColor.DARK_RED + "[" + table + "]" + ChatColor.BLUE + "[" + actions.getString("ldate") + "] " + ChatColor.GOLD + "Victem: " + ChatColor.GREEN + actions.getString("victem") + ChatColor.GOLD +  " Killer: " + ChatColor.GREEN + actions.getString("killer"));
				else if(table.equalsIgnoreCase("deaths"))
					sender.sendMessage(ChatColor.DARK_RED + "[" + table + "]" + ChatColor.BLUE + "[" + actions.getString("ldate") + "] " + ChatColor.GOLD + "Player: " + ChatColor.GREEN + actions.getString("player"));
				else
					sender.sendMessage(ChatColor.YELLOW + "Invalid table name");
			}
		} catch(SQLException e) {
			
		}
		return true;
	}

}
