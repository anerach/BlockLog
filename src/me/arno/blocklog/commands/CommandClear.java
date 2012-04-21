package me.arno.blocklog.commands;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import me.arno.blocklog.BlockLog;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

public class CommandClear extends BlockLogCommand {
	public CommandClear(BlockLog plugin) {
		super(plugin, "blocklog.clear");
	}
	
	public boolean execute(Player player, Command cmd, ArrayList<String> listArgs) {
		String[] args = listArgs.toArray(new String[]{});
		if(args.length != 2) {
			player.sendMessage(ChatColor.WHITE + "/bl clear [amount] [days|weeks]");
			return true;
		}
		
		if(!hasPermission(player)) {
			player.sendMessage("You don't have permission");
			return true;
		}
		
		try {
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
			
			stmt.executeUpdate("DELETE FROM blocklog_blocks WHERE date < " + (UNIX_TIMESTAMP - time));
			
			player.sendMessage(ChatColor.DARK_RED +"[BlockLog] removed block history older than " + timeInt + " " + timeType);
	    } catch (SQLException e) {
    		e.printStackTrace();
    	}
		
		return true;
	}

}
