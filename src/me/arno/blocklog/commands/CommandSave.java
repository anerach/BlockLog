package me.arno.blocklog.commands;

import java.util.logging.Logger;

import me.arno.blocklog.BlockLog;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandSave implements CommandExecutor {
	BlockLog plugin;
	Logger log;
	
	public CommandSave(BlockLog plugin) {
		this.plugin = plugin;
		this.log = plugin.log;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		Player player = null;
		
		if (sender instanceof Player)
			player = (Player) sender;
		
		if(!(cmd.getName().equalsIgnoreCase("blsave") || cmd.getName().equalsIgnoreCase("blfullsave")))
			return false;
		
		if(cmd.getName().equalsIgnoreCase("blsave")) {
			int blockCount = 100;
			if(args.length == 1)
				blockCount = Integer.parseInt(args[0]);
			
			plugin.saveLogs(blockCount, player);
			return true;
		} else if(cmd.getName().equalsIgnoreCase("blfullsave")) {
			plugin.saveLogs(0, player);
			return true;
		}
		return false;
	}

}
