package me.arno.blocklog.commands;

import me.arno.blocklog.BlockLog;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandConfig implements CommandExecutor {

	BlockLog plugin;
	
	public CommandConfig(BlockLog plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
Player player = null;
		
		if (sender instanceof Player)
			player = (Player) sender;
		
		
		
		if(!cmd.getName().equalsIgnoreCase("blconfig"))
			return false;
		
		if (player == null) {
			sender.sendMessage("This command can only be run by a player");
			return true;
		}
		
		if(args.length < 1)
			return false;
		
		String Action = args[0].toString();
		String ConfigKey;
		String ConfigValue;
		
		if(player.isOp() || player.hasPermission("blocklog.config")) {
			if(Action.equalsIgnoreCase("help")) {
				player.sendMessage(ChatColor.DARK_RED + "[BlockLog][Config] " + ChatColor.GOLD + "Help");
				player.sendMessage(ChatColor.DARK_GREEN + "/blconfig help - Shows this message");
				player.sendMessage(ChatColor.DARK_GREEN + "/blconfig set <key> <value> - Changes a blocklog config value");
				player.sendMessage(ChatColor.DARK_GREEN + "/blconfig get <key> - Shows a blocklog config value");
			} else if(Action.equalsIgnoreCase("set")) {
				if(args.length != 3)
					return false;
				
				ConfigKey = args[1];
				ConfigValue = args[2];
				
				if(plugin.getConfig().isString(ConfigKey))
					plugin.getConfig().set(ConfigKey, ConfigValue);
				else if(plugin.getConfig().isInt(ConfigKey))
					plugin.getConfig().set(ConfigKey, Integer.parseInt(ConfigValue));
				else if(plugin.getConfig().isBoolean(ConfigKey))
					plugin.getConfig().set(ConfigKey, Boolean.parseBoolean(ConfigValue));
				else
					return false;
				
				player.sendMessage(ChatColor.DARK_RED +"[BlockLog][Config] " + ChatColor.GOLD + "Changed value of " + ConfigKey + " to " + ConfigValue);
				plugin.saveConfig();
				plugin.reloadConfig();
			} else if(Action.equalsIgnoreCase("get")) {
				if(args.length != 2)
					return false;
				
				ConfigKey = args[1].toString();
				
				String Result;
				if(plugin.getConfig().isString(ConfigKey))
					Result = plugin.getConfig().getString(ConfigKey);
				else if(plugin.getConfig().isInt(ConfigKey))
					Result = Integer.toString(plugin.getConfig().getInt(ConfigKey));
				else if(plugin.getConfig().isBoolean(ConfigKey))
					Result = Boolean.toString(plugin.getConfig().getBoolean(ConfigKey));
				else 
					return false;
				
				player.sendMessage(ChatColor.DARK_RED +"[BlockLog][Config] " + ChatColor.GOLD + "Value of " + ConfigKey + ": " + Result);
				
			}
			return true;
		}
		return false;
	}

}
