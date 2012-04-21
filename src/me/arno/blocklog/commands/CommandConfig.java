package me.arno.blocklog.commands;

import me.arno.blocklog.BlockLog;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

public class CommandConfig extends BlockLogCommand {
	public CommandConfig(BlockLog plugin) {
		super(plugin);
	}

	public boolean execute(Player player, Command cmd, String[] args) {
		if(args.length < 1) {
			player.sendMessage(ChatColor.WHITE + "/bl config <set|get|help> <config> [value]");
			return true;
		}
		
		String action = args[0].toString();
		String ConfigKey;
		String ConfigValue;
		
		if(player.isOp() || player.hasPermission("blocklog.config")) {
			if(action.equalsIgnoreCase("help")) {
				player.sendMessage(ChatColor.DARK_RED + "[BlockLog][Config] " + ChatColor.GOLD + "Help");
				player.sendMessage(ChatColor.DARK_GREEN + "/blconfig help - Shows this message");
				player.sendMessage(ChatColor.DARK_GREEN + "/blconfig set <key> <value> - Changes a blocklog config value");
				player.sendMessage(ChatColor.DARK_GREEN + "/blconfig get <key> - Shows a blocklog config value");
			} else if(action.equalsIgnoreCase("set")) {
				if(args.length != 3)
					return false;
				
				ConfigKey = args[1];
				ConfigValue = args[2];
				
				if(getConfig().isString(ConfigKey))
					getConfig().set(ConfigKey, ConfigValue);
				else if(getConfig().isInt(ConfigKey))
					getConfig().set(ConfigKey, Integer.parseInt(ConfigValue));
				else if(getConfig().isBoolean(ConfigKey))
					getConfig().set(ConfigKey, Boolean.parseBoolean(ConfigValue));
				else
					return false;
				
				player.sendMessage(ChatColor.DARK_RED +"[BlockLog][Config] " + ChatColor.GOLD + "Changed value of " + ConfigKey + " to " + ConfigValue);
				saveConfig();
				reloadConfig();
			} else if(action.equalsIgnoreCase("get")) {
				if(args.length != 2)
					return false;
				
				ConfigKey = args[1].toString();
				
				String Result;
				if(getConfig().isString(ConfigKey))
					Result = getConfig().getString(ConfigKey);
				else if(getConfig().isInt(ConfigKey))
					Result = Integer.toString(getConfig().getInt(ConfigKey));
				else if(getConfig().isBoolean(ConfigKey))
					Result = Boolean.toString(getConfig().getBoolean(ConfigKey));
				else 
					return false;
				
				player.sendMessage(ChatColor.DARK_RED +"[BlockLog][Config] " + ChatColor.GOLD + "Value of " + ConfigKey + ": " + Result);
				
			}
			return true;
		}
		return false;
	}

}
