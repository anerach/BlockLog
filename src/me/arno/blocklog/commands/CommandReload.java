package me.arno.blocklog.commands;

import java.util.logging.Logger;

import me.arno.blocklog.BlockLog;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandReload implements CommandExecutor {
	BlockLog plugin;
	Logger log;
	
	public CommandReload(BlockLog plugin) {
		this.plugin = plugin;
		this.log = plugin.log;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		Player player = null;
		
		if (sender instanceof Player)
			player = (Player) sender;
		
		if(!cmd.getName().equalsIgnoreCase("blreload"))
			return false;
		
		if (player == null) {
			log.info("Reloading!");
			if(plugin.reloadPlugin())
				log.info("Reloaded Succesfully!");
			else
				log.info("An error occured while reloading BlockLog!");
		} else {
			player.sendMessage(ChatColor.DARK_RED + "[BlockLog] " + ChatColor.GOLD + "Reloading!");
			if(plugin.reloadPlugin())
				player.sendMessage(ChatColor.DARK_RED + "[BlockLog] " + ChatColor.GOLD + "Reloaded Succesfully!");
			else
				player.sendMessage(ChatColor.DARK_RED + "[BlockLog] " + ChatColor.GOLD + "An error occured while reloading BlockLog!");
		}
		return true;
	}

}
