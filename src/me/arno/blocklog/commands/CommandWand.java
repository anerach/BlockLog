package me.arno.blocklog.commands;

import me.arno.blocklog.BlockLog;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandWand implements CommandExecutor {
	BlockLog plugin;
	
	public CommandWand(BlockLog plugin) {
		this.plugin = plugin;
		
	}
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		Player player = null;
		
		if (sender instanceof Player)
			player = (Player) sender;
		
		if(!commandLabel.equalsIgnoreCase("blwand"))
			return true;
		
		if (player == null) {
			sender.sendMessage("This command can only be run by a player");
			return true;
		}
		
		if(plugin.users.isEmpty()) {
			plugin.users.add(player.getName());
			player.sendMessage(ChatColor.DARK_RED +"[BlockLog] " + ChatColor.GOLD + "Wand enabled!");
		} else if(plugin.users.contains(player.getName())) {
			plugin.users.remove(player.getName());
			player.sendMessage(ChatColor.DARK_RED +"[BlockLog] " + ChatColor.GOLD + "Wand disabled!");
		} else {
			plugin.users.add(player.getName());
			player.sendMessage(ChatColor.DARK_RED +"[BlockLog] " + ChatColor.GOLD + "Wand enabled!");
		}
		return true;
	}

}
