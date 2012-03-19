package me.arno.blocklog.commands;

import me.arno.blocklog.BlockLog;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandHelp implements CommandExecutor {

	BlockLog plugin;
	
	public CommandHelp(BlockLog plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		Player player = null;
		
		if (sender instanceof Player)
			player = (Player) sender;
		
		if(!cmd.getName().equalsIgnoreCase("blhelp"))
			return false;
		
		if (player == null) {
			sender.sendMessage("This command can only be run by a player");
			return true;
		}
		
		if(args.length > 0)
			return false;
		
		player.sendMessage(ChatColor.DARK_RED +"[BlockLog] " + ChatColor.GOLD + "Commands");
		player.sendMessage(ChatColor.DARK_RED +"/blhelp" + ChatColor.GOLD + " - Shows this message");
		if(player.isOp() || player.hasPermission("blocklog.reload"))
			player.sendMessage(ChatColor.DARK_RED +"/blreload" + ChatColor.GOLD + " - Reloads blocklog config file");
		if(player.isOp() || player.hasPermission("blocklog.wand"))
			player.sendMessage(ChatColor.DARK_RED +"/blwand" + ChatColor.GOLD + " - Enables blocklog's wand");
		if(player.isOp() || player.hasPermission("blocklog.save"))
			player.sendMessage(ChatColor.DARK_RED +"/blsave [amount]" + ChatColor.GOLD + " - Saves 25 or the specified amount of blocks");
		if(player.isOp() || player.hasPermission("blocklog.fullsave"))
			player.sendMessage(ChatColor.DARK_RED +"/blfullsave" + ChatColor.GOLD + " - Saves all the blocks");
		if(player.isOp() || player.hasPermission("blocklog.autosave"))
			player.sendMessage(ChatColor.DARK_RED +"/blautosave <blocks> <message>" + ChatColor.GOLD + " - Enables autosave feature");
		if(player.isOp() || player.hasPermission("blocklog.rollback")) {
			player.sendMessage(ChatColor.DARK_RED +"/blrollback [player] <time> <sec|min|hour|day|week>" + ChatColor.GOLD + " - Blocklog's rollback command");
			player.sendMessage(ChatColor.DARK_RED +"/blrollbackradius [player] <time> <sec|min|hour|day|week>" + ChatColor.GOLD + " - Blocklog's radius rollback command");
		}
		if(player.isOp() || player.hasPermission("blocklog.clear"))
			player.sendMessage(ChatColor.DARK_RED +"/blclear <amount> <day|week>" + ChatColor.GOLD + " - Clears blocklog's history");
		if(player.isOp() || player.hasPermission("blocklog.undo"))
			player.sendMessage(ChatColor.DARK_RED +"/blundo [rollback]" + ChatColor.GOLD + " - Undo's the latest or specified rollback");
		if(player.isOp() || player.hasPermission("blocklog.config"))
			player.sendMessage(ChatColor.DARK_RED +"/blconfig <get/set> <key> [value]" + ChatColor.GOLD + " - Change blocklog's command values ingame");
		
		return true;
	}
}
