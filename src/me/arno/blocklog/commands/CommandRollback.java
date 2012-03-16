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

public class CommandRollback implements CommandExecutor {
	BlockLog plugin;
	Logger log;
	
	public CommandRollback(BlockLog plugin) {
		this.plugin = plugin;
		this.log = plugin.log;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		Player player = null;
		
		if (sender instanceof Player)
			player = (Player) sender;
		
		if(!cmd.getName().equalsIgnoreCase("blrollback"))
			return false;
		
		if (player == null) {
			sender.sendMessage("This command can only be run by a player");
			return true;
		}
		
		if(args.length < 2 || args.length > 3)
			return false;
		
		int time;
		
		Set<String> Second = new HashSet<String>(Arrays.asList("s", "sec","secs","second","seconds"));
		Set<String> Minute = new HashSet<String>(Arrays.asList("m", "min","mins","minute","minutes"));
		Set<String> Hour = new HashSet<String>(Arrays.asList("h", "hour","hours"));
		Set<String> Day = new HashSet<String>(Arrays.asList("d", "day","days"));
		Set<String> Week = new HashSet<String>(Arrays.asList("w", "week","weeks"));

		Integer timeInt = Integer.parseInt(args[0]);
		String timeVal = args[1].toLowerCase();
		
		if(args.length == 3) {
			timeInt = Integer.parseInt(args[1]);
			timeVal = args[2].toLowerCase();
		}
		
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
			Rollback rb = new Rollback(plugin, player, 0);
			if(args.length == 3)
				rb.doRollback(player.getServer().getPlayer(args[0]), time);
			else
				rb.doRollback(time);
			rb.close();
			return true;
		} catch(SQLException e) {
			e.printStackTrace();
		} catch(Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}
