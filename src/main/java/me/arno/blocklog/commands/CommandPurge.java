package me.arno.blocklog.commands;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import me.arno.blocklog.util.Query;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class CommandPurge extends BlockLogCommand {
	public CommandPurge() {
		super("blocklog.purge", true);
		setCommandUsage("/bl clear <table1> [table2] [...] <time>");
	}
	
	@Override
	public boolean execute(CommandSender sender, Command cmd, String[] args) {
		if(args.length < 2)
			return false;
		
		if(!hasPermission(sender)) {
			sender.sendMessage("You don't have permission");
			return true;
		}
		
		Integer time;
		Long currentTime = System.currentTimeMillis()/1000;
		
		String value = args[1];
		Character c = value.charAt(value.length() - 1);
		time = Integer.valueOf(value.replace(c, ' ').trim());
		
		if(c.toString().equalsIgnoreCase("d"))
			time = time * 60 * 60 * 24;
		else if(c.toString().equalsIgnoreCase("w"))
			time = time * 60 * 60 * 24 * 7;
		else
			time = 0;
		
		Set<String> tables = new HashSet<String>();
		
		for(int i=0;i<args.length-1;i++) {
			if(args[i].equalsIgnoreCase("all")) {
				tables.add("blocks");
				tables.add("interactions");
				tables.add("chests");
				tables.add("data");
			} else if(args[i].equalsIgnoreCase("data")) {
				tables.add("data");
			} else if(args[i].equalsIgnoreCase("blocks")) {
				tables.add("blocks");
			} else if(args[i].equalsIgnoreCase("interactions")) {
				tables.add("interactions");
			} else if(args[i].equalsIgnoreCase("chests")) {
				tables.add("chests");
			}
		}
		
		try {
			Query query;
			for(String table : tables) {
				query = new Query("blocklog_" + table);
				query.where("date", currentTime - time, "<");
				int count = query.deleteRows();
				
				if(count > 0) {
					sender.sendMessage(ChatColor.DARK_RED +"[BlockLog] " + ChatColor.GOLD + "Removed " + ChatColor.GREEN + count + ChatColor.GOLD + " results from blocklog_" + table);
				}
			}
	    } catch (SQLException e) {
    		e.printStackTrace();
		}
		
		return true;
	}

}
