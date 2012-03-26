package me.arno.blocklog.commands;

import java.util.logging.Logger;

import me.arno.blocklog.BlockLog;
import me.arno.blocklog.database.DatabaseSettings;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandAutoSave implements CommandExecutor {
	BlockLog plugin;
	Logger log;
	DatabaseSettings dbSettings;
	
	public CommandAutoSave(BlockLog plugin) {
		this.plugin = plugin;
		this.log = plugin.log;
	}

	public void sendAdminMessage(String msg) {
		for (Player player : Bukkit.getServer().getOnlinePlayers()) {
	    	if (player.isOp() || player.hasPermission("blocklog.notices")) {
	    		player.sendMessage(msg);
	        }
	    }
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		Player player = null;
		
		if (sender instanceof Player)
			player = (Player) sender;
		
		if(!cmd.getName().equalsIgnoreCase("blautosave"))
			return false;
		
		if (player == null) {
			sender.sendMessage("This command can only be run by a player");
			return true;
		}
		
		if(args.length > 2)
			return false;
		
		if(args.length == 0) {
			plugin.autoSave = 0;
			sendAdminMessage(String.format(ChatColor.DARK_RED + "[BlockLog] " + ChatColor.GOLD + "Autosave disabled by %s", player.getName()));
			return true;
		} else if(args.length == 1) {
			if(args[0].equalsIgnoreCase("info")) {
				if(plugin.autoSave != 0)
					player.sendMessage(String.format(ChatColor.DARK_RED + "[BlockLog] " + ChatColor.GOLD + "Autosave configured at %s blocks", plugin.autoSave, player.getName()));
				else
					player.sendMessage(String.format(ChatColor.DARK_RED + "[BlockLog] " + ChatColor.GOLD + "There is no autosave configured"));
			} else {
				plugin.autoSave = Integer.valueOf(args[0]);
				sendAdminMessage(String.format(ChatColor.DARK_RED + "[BlockLog] " + ChatColor.GOLD + "Autosave enabled at %s blocks by %s", plugin.autoSave, player.getName()));
			}
			return true;
		}
		return false;
	}
}
