package me.arno.blocklog.commands;

import me.arno.blocklog.BlockLog;
import me.arno.blocklog.Config;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandConfig implements CommandExecutor {
	BlockLog plugin;
	Config cfg;
	
	public CommandConfig(BlockLog plugin) {
		this.plugin = plugin;
		this.cfg = plugin.cfg;
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
				
				if(cfg.getConfig().isString(ConfigKey))
					cfg.getConfig().set(ConfigKey, ConfigValue);
				else if(cfg.getConfig().isInt(ConfigKey))
					cfg.getConfig().set(ConfigKey, Integer.parseInt(ConfigValue));
				else if(cfg.getConfig().isBoolean(ConfigKey))
					cfg.getConfig().set(ConfigKey, Boolean.parseBoolean(ConfigValue));
				else
					return false;
				
				player.sendMessage(ChatColor.DARK_RED +"[BlockLog][Config] " + ChatColor.GOLD + "Changed value of " + ConfigKey + " to " + ConfigValue);
				cfg.saveConfig();
				cfg.reloadConfig();
			} else if(Action.equalsIgnoreCase("get")) {
				if(args.length != 2)
					return false;
				
				ConfigKey = args[1].toString();
				
				String Result;
				if(cfg.getConfig().isString(ConfigKey))
					Result = cfg.getConfig().getString(ConfigKey);
				else if(cfg.getConfig().isInt(ConfigKey))
					Result = Integer.toString(cfg.getConfig().getInt(ConfigKey));
				else if(cfg.getConfig().isBoolean(ConfigKey))
					Result = Boolean.toString(cfg.getConfig().getBoolean(ConfigKey));
				else 
					return false;
				
				player.sendMessage(ChatColor.DARK_RED +"[BlockLog][Config] " + ChatColor.GOLD + "Value of " + ConfigKey + ": " + Result);
				
			}
			return true;
		}
		return false;
	}

}
