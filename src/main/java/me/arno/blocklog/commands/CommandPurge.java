package me.arno.blocklog.commands;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

public class CommandPurge extends BlockLogCommand {
	public CommandPurge() {
		super("blocklog.purge");
		setCommandUsage("/bl clear <table1> [table2] [...] <time>");
	}
	
	@Override
	public boolean execute(Player player, Command cmd, String[] args) {
		if(args.length < 2)
			return false;
		
		if(!hasPermission(player)) {
			player.sendMessage("You don't have permission");
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
				tables.add("chat");
				tables.add("deaths");
				tables.add("kills");
				tables.add("commands");
			} else if(args[i].equalsIgnoreCase("player")) {
				tables.add("chat");
				tables.add("deaths");
				tables.add("kills");
				tables.add("commands");
			} else if(args[i].equalsIgnoreCase("blocks")) {
				tables.add("blocks");
			} else if(args[i].equalsIgnoreCase("interactions")) {
				tables.add("interactions");
			} else if(args[i].equalsIgnoreCase("chat")) {
				tables.add("chat");
			} else if(args[i].equalsIgnoreCase("deaths")) {
				tables.add("deaths");
			} else if(args[i].equalsIgnoreCase("kills")) {
				tables.add("kills");
			} else if(args[i].equalsIgnoreCase("commands")) {
				tables.add("commands");
			}
		}
		
		
		
		try {
			Statement stmt = conn.createStatement();
			
			for(String table : tables) {
				ResultSet rs = stmt.executeQuery("SELECT COUNT(id) AS count FROM blocklog_" + table + " WHERE date < " + (currentTime - time));
				rs.next();
				Integer count = rs.getInt("count");
				if(count != 0) {
					stmt.executeUpdate("DELETE FROM blocklog_" + table + " WHERE date < " + (currentTime - time));
					player.sendMessage(ChatColor.DARK_RED +"[BlockLog] " + ChatColor.GOLD + "Removed " + ChatColor.GREEN + count + ChatColor.GOLD + " results from blocklog_" + table);
				}
			}
	    } catch (SQLException e) {
    		e.printStackTrace();
		}
		
		return true;
	}

}
