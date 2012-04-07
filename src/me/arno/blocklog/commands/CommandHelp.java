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
		if(player.hasPermission("blocklog.convert"))
			player.sendMessage(ChatColor.DARK_RED +"/blconvert" + ChatColor.GOLD + " - Converts your database (SQLite <-> MySQL)");
		
		if(player.hasPermission("blocklog.clear"))
			player.sendMessage(ChatColor.DARK_RED +"/blclear" + ChatColor.GOLD + " - Clears blocklog's history");
		
		if(player.hasPermission("blocklog.config"))
			player.sendMessage(ChatColor.DARK_RED +"/blconfig" + ChatColor.GOLD + " - Change blocklog's command values ingame");
		
		if(player.hasPermission("blocklog.report.write") && plugin.getConfig().getBoolean("blocklog.reports"))
			player.sendMessage(ChatColor.DARK_RED +"/blreport" + ChatColor.GOLD + " - Create a grief report");
		
		if(player.hasPermission("blocklog.report.read") && plugin.getConfig().getBoolean("blocklog.reports"))
			player.sendMessage(ChatColor.DARK_RED +"/blread" + ChatColor.GOLD + " - Read a grief report");
		
		if(player.hasPermission("blocklog.undo"))
			player.sendMessage(ChatColor.DARK_RED +"/blundo" + ChatColor.GOLD + " - Undo's the latest or specified rollback");
		
		if(player.hasPermission("blocklog.rollback"))
			player.sendMessage(ChatColor.DARK_RED +"/blrollbackradius" + ChatColor.GOLD + " - Blocklog's radius rollback command");
			
		if(player.hasPermission("blocklog.rollback"))
			player.sendMessage(ChatColor.DARK_RED +"/blrollback" + ChatColor.GOLD + " - Blocklog's rollback command");
		
		if(player.hasPermission("blocklog.autosave"))
			player.sendMessage(ChatColor.DARK_RED +"/blautosave" + ChatColor.GOLD + " - Enables autosave feature");
		
		if(player.hasPermission("blocklog.fullsave"))
			player.sendMessage(ChatColor.DARK_RED +"/blfullsave" + ChatColor.GOLD + " - Saves all the blocks");
		
		if(player.hasPermission("blocklog.save"))
			player.sendMessage(ChatColor.DARK_RED +"/blsave" + ChatColor.GOLD + " - Saves 100 or the specified amount of blocks");
		
		if(player.hasPermission("blocklog.reload"))
			player.sendMessage(ChatColor.DARK_RED +"/blreload" + ChatColor.GOLD + " - Reloads blocklog config file");
		
		if(player.hasPermission("blocklog.wand"))
			player.sendMessage(ChatColor.DARK_RED +"/blwand" + ChatColor.GOLD + " - Enables blocklog's wand");
		
		player.sendMessage(ChatColor.DARK_RED +"/blhelp" + ChatColor.GOLD + " - Shows this message");
		player.sendMessage(ChatColor.DARK_RED +"/blocklog" + ChatColor.GOLD + " - Basic Information");
		return true;
	}
}
