package me.arno.blocklog.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

public class CommandHelp extends BlockLogCommand {
	public CommandHelp() {
		super("blocklog.help");
		setCommandUsage("/bl help");
	}

	@Override
	public boolean execute(Player player, Command cmd, String[] args) {
		if(args.length > 0) {
			return false;
		}
		
		if(!hasPermission(player)) {
			player.sendMessage("You don't have permission");
			return true;
		}
		
		player.sendMessage(ChatColor.DARK_RED +"[BlockLog] " + ChatColor.GOLD + "Commands");
		if(player.hasPermission("blocklog.convert"))
			player.sendMessage(ChatColor.DARK_RED +"/bl convert" + ChatColor.GOLD + " - Converts your database (SQLite <-> MySQL)");
		
		if(player.hasPermission("blocklog.clear"))
			player.sendMessage(ChatColor.DARK_RED +"/bl clear" + ChatColor.GOLD + " - Clears blocklog's history");
		
		if(player.hasPermission("blocklog.config"))
			player.sendMessage(ChatColor.DARK_RED +"/bl config" + ChatColor.GOLD + " - Change blocklog's command values ingame");
		
		if(player.hasPermission("blocklog.report.write") && plugin.getConfig().getBoolean("blocklog.reports"))
			player.sendMessage(ChatColor.DARK_RED +"/bl report" + ChatColor.GOLD + " - Create a grief report");
		
		if(player.hasPermission("blocklog.report.read") && plugin.getConfig().getBoolean("blocklog.reports"))
			player.sendMessage(ChatColor.DARK_RED +"/bl read" + ChatColor.GOLD + " - Read a grief report");
		
		if(player.hasPermission("blocklog.undo"))
			player.sendMessage(ChatColor.DARK_RED +"/bl undo" + ChatColor.GOLD + " - Undo's the latest or specified rollback");
		
		if(player.hasPermission("blocklog.rollback"))
			player.sendMessage(ChatColor.DARK_RED +"/bl rollbackradius" + ChatColor.GOLD + " - Blocklog's radius rollback command");
			
		if(player.hasPermission("blocklog.rollback"))
			player.sendMessage(ChatColor.DARK_RED +"/bl rollback" + ChatColor.GOLD + " - Blocklog's rollback command");
		
		if(player.hasPermission("blocklog.autosave"))
			player.sendMessage(ChatColor.DARK_RED +"/bl autosave" + ChatColor.GOLD + " - Enables autosave feature");
		
		if(player.hasPermission("blocklog.save"))
			player.sendMessage(ChatColor.DARK_RED +"/bl fullsave" + ChatColor.GOLD + " - Saves all the blocks");
		
		if(player.hasPermission("blocklog.save"))
			player.sendMessage(ChatColor.DARK_RED +"/bl save" + ChatColor.GOLD + " - Saves 100 or the specified amount of blocks");
		
		if(player.hasPermission("blocklog.reload"))
			player.sendMessage(ChatColor.DARK_RED +"/bl reload" + ChatColor.GOLD + " - Reloads blocklog config file");
		
		if(player.hasPermission("blocklog.wand"))
			player.sendMessage(ChatColor.DARK_RED +"/bl wand" + ChatColor.GOLD + " - Enables blocklog's wand");
		
		player.sendMessage(ChatColor.DARK_RED +"/bl help" + ChatColor.GOLD + " - Shows this message");
		player.sendMessage(ChatColor.DARK_RED +"/blocklog" + ChatColor.GOLD + " - Basic Information");
		return true;
	}
}
