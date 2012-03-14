package me.arno.blocklog.commands;

import java.util.logging.Logger;

import me.arno.blocklog.BlockLog;
import me.arno.blocklog.database.DatabaseSettings;

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
			player.sendMessage(ChatColor.DARK_RED + "[BlockLog] " + ChatColor.GOLD + "Auto save is disabled");
			return true;
		} else if(args.length > 0) {
			plugin.autoSave = Integer.parseInt(args[0]);
			if(args.length == 2)
				plugin.autoSaveMsg = Boolean.parseBoolean(args[0]);
				
			player.sendMessage(ChatColor.DARK_RED + "[BlockLog] " + ChatColor.GOLD + "Auto save is enabled");
			return true;
		}
		return false;
	}
}
