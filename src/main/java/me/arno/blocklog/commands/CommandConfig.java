package me.arno.blocklog.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

public class CommandConfig extends BlockLogCommand {
	public CommandConfig() {
		super("blocklog.config");
		setCommandUsage("/bl config <set|get|help> <config> [value]");
	}

	@Override
	public boolean execute(Player player, Command cmd, String[] args) {
		if(args.length < 1)
			return false;
		
		if(!hasPermission(player)) {
			player.sendMessage("You don't have permission");
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
				
				if(getSettingsManager().getConfig().isString(ConfigKey))
					getSettingsManager().getConfig().set(ConfigKey, ConfigValue);
				else if(getSettingsManager().getConfig().isInt(ConfigKey))
					getSettingsManager().getConfig().set(ConfigKey, Integer.parseInt(ConfigValue));
				else if(getSettingsManager().getConfig().isBoolean(ConfigKey))
					getSettingsManager().getConfig().set(ConfigKey, Boolean.parseBoolean(ConfigValue));
				else
					return false;
				
				player.sendMessage(ChatColor.DARK_RED +"[BlockLog][Config] " + ChatColor.GOLD + "Changed value of " + ConfigKey + " to " + ConfigValue);
				getSettingsManager().saveConfig();
				getSettingsManager().reloadConfig();
			} else if(action.equalsIgnoreCase("get")) {
				if(args.length != 2)
					return false;
				
				ConfigKey = args[1].toString();
				
				String Result;
				if(getSettingsManager().getConfig().isString(ConfigKey))
					Result = getSettingsManager().getConfig().getString(ConfigKey);
				else if(getSettingsManager().getConfig().isInt(ConfigKey))
					Result = Integer.toString(getSettingsManager().getConfig().getInt(ConfigKey));
				else if(getSettingsManager().getConfig().isBoolean(ConfigKey))
					Result = Boolean.toString(getSettingsManager().getConfig().getBoolean(ConfigKey));
				else 
					return false;
				
				player.sendMessage(ChatColor.DARK_RED +"[BlockLog][Config] " + ChatColor.GOLD + "Value of " + ConfigKey + ": " + Result);
				
			}
			return true;
		}
		return false;
	}

}
