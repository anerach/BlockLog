package me.arno.blocklog.commands;

import java.util.logging.Logger;

import me.arno.blocklog.BlockLog;

import org.bukkit.ChatColor;
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
		
		if (player == null) {
			sender.sendMessage("This command can only be run by a player");
			return true;
		}
		
		if(cmd.getName().equalsIgnoreCase("blsave")) {
			int blockCount = 100;
			if(args.length == 1)
				blockCount = Integer.parseInt(args[0]);
			
			player.sendMessage(ChatColor.DARK_RED +"[BlockLog] " + ChatColor.GOLD + "Saving " + blockCount + " block edits!");
			plugin.saveBlocks(blockCount);
			player.sendMessage(ChatColor.DARK_RED +"[BlockLog] " + ChatColor.GOLD + "Successfully saved " + blockCount + " block edits!");
			return true;
		} else if(cmd.getName().equalsIgnoreCase("blfullsave")) {
			player.sendMessage(ChatColor.DARK_RED +"[BlockLog] " + ChatColor.GOLD + "Saving all the block edits!");
			plugin.saveBlocks(0);
			player.sendMessage(ChatColor.DARK_RED +"[BlockLog] " + ChatColor.GOLD + "Successfully saved all the block edits!");
			return true;
		}
		return false;
	}

}
