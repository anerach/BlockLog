package me.arno.blocklog.commands;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.arno.blocklog.BlockLog;
import me.arno.blocklog.Rollback;

public class CommandRadiusRollback implements CommandExecutor {
	BlockLog plugin;
	Logger log;
	
	public CommandRadiusRollback(BlockLog plugin) {
		this.plugin = plugin;
		this.log = plugin.log;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		Player player = null;
		
		if (sender instanceof Player)
			player = (Player) sender;
		
		if(!(commandLabel.equalsIgnoreCase("blrollbackradius") || commandLabel.equalsIgnoreCase("blrbradius") || commandLabel.equalsIgnoreCase("blrbr")))
			return false;
		
		if (player == null) {
			sender.sendMessage("This command can only be run by a player");
			return true;
		}
		
		if(args.length < 3 || args.length > 4)
			return false;
		
		String strPlayer = null;
		int radius = Integer.parseInt(args[0]);
		int timeInt = Integer.parseInt(args[1]);
		String timeVal = args[2];
		
		if(args.length == 4) {
			strPlayer = args[0];
			radius = Integer.parseInt(args[1]);
			timeInt = Integer.parseInt(args[2]);
			timeVal = args[3];
		}
		
		int time;
		
		Set<String> Second = new HashSet<String>(Arrays.asList("s", "sec","secs","second","seconds"));
		Set<String> Minute = new HashSet<String>(Arrays.asList("m", "min","mins","minute","minutes"));
		Set<String> Hour = new HashSet<String>(Arrays.asList("h", "hour","hours"));
		Set<String> Day = new HashSet<String>(Arrays.asList("d", "day","days"));
		Set<String> Week = new HashSet<String>(Arrays.asList("w", "week","weeks"));
		
		if(Second.contains(timeVal))
			time = (int) (System.currentTimeMillis()/1000 - timeInt);
		else if(Minute.contains(timeVal))
			time = (int) (System.currentTimeMillis()/1000 - timeInt * 60);
		else if(Hour.contains(timeVal))
			time = (int) (System.currentTimeMillis()/1000 - timeInt * 60 * 60);
		else if(Day.contains(timeVal))
			time = (int) (System.currentTimeMillis()/1000 - timeInt * 60 * 60 * 24);
		else if(Week.contains(timeVal))
			time = (int) (System.currentTimeMillis()/1000 - timeInt * 60 * 60 * 24 * 7);
		else {
			player.sendMessage(ChatColor.DARK_GREEN + "Invalid time");
			return false;
		}
		
		try {
			Rollback rb = new Rollback(plugin, player, 1);
			if(strPlayer != null)
				return rb.doRollback(player.getServer().getPlayer(strPlayer), time, radius);
			else
				return rb.doRollback(time, radius);
		} catch(SQLException e) {
			e.printStackTrace();
		} catch(Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}
