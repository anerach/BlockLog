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
		player.sendMessage(ChatColor.DARK_RED +"/bl help" + ChatColor.GOLD + " - Shows this message");
		
		if(player.hasPermission("blocklog.reload"))
			player.sendMessage(ChatColor.DARK_RED +"/bl reload" + ChatColor.GOLD + " - Reloads blocklog config file");
		
		if(player.hasPermission("blocklog.purge"))
			player.sendMessage(ChatColor.DARK_RED +"/bl purge" + ChatColor.GOLD + " - Clears blocklog's history");
		
		if(player.hasPermission("blocklog.config"))
			player.sendMessage(ChatColor.DARK_RED +"/bl config" + ChatColor.GOLD + " - Change blocklog's command values ingame");
		
		if(player.hasPermission("blocklog.report.write") && plugin.getConfig().getBoolean("blocklog.reports"))
			player.sendMessage(ChatColor.DARK_RED +"/bl report" + ChatColor.GOLD + " - Create a grief report");
		
		if(player.hasPermission("blocklog.report.read") && plugin.getConfig().getBoolean("blocklog.reports"))
			player.sendMessage(ChatColor.DARK_RED +"/bl read" + ChatColor.GOLD + " - Read a grief report");
		
		if(player.hasPermission("blocklog.storage"))
			player.sendMessage(ChatColor.DARK_RED +"/bl storage" + ChatColor.GOLD + " - Shows the total amount of stored block edits and interactions");
		
		if(player.hasPermission("blocklog.queue"))
			player.sendMessage(ChatColor.DARK_RED +"/bl queue" + ChatColor.GOLD + " - Shows the total amount of queued block edits and interactions");
		
		if(player.hasPermission("blocklog.rollback"))
			player.sendMessage(ChatColor.DARK_RED +"/bl undo" + ChatColor.GOLD + " - Undos the latest or specified rollback");
			
		if(player.hasPermission("blocklog.rollback"))
			player.sendMessage(ChatColor.DARK_RED +"/bl rollback" + ChatColor.GOLD + " - Blocklog's rollback command");
		
		if(player.hasPermission("blocklog.rollback"))
			player.sendMessage(ChatColor.DARK_RED +"/bl simundo" + ChatColor.GOLD + " - Simulates an undo");
			
		if(player.hasPermission("blocklog.rollback"))
			player.sendMessage(ChatColor.DARK_RED +"/bl simrollback" + ChatColor.GOLD + " - Simulates a rollback");
		
		if(player.hasPermission("blocklog.autosave"))
			player.sendMessage(ChatColor.DARK_RED +"/bl autosave" + ChatColor.GOLD + " - Enables autosave feature");
		
		if(player.hasPermission("blocklog.save"))
			player.sendMessage(ChatColor.DARK_RED +"/bl save" + ChatColor.GOLD + " - Saves queued block edits and interactions");
		
		if(player.hasPermission("blocklog.lookup"))
			player.sendMessage(ChatColor.DARK_RED +"/bl lookup" + ChatColor.GOLD + " - Used to search the database for a player's latest actions");
		
		if(player.hasPermission("blocklog.search"))
			player.sendMessage(ChatColor.DARK_RED +"/bl search" + ChatColor.GOLD + " - Used to search the database for results");
		
		if(player.hasPermission("blocklog.wand"))
			player.sendMessage(ChatColor.DARK_RED +"/bl wand" + ChatColor.GOLD + " - Enables blocklog's wand");
		player.sendMessage(ChatColor.DARK_RED +"/blocklog" + ChatColor.GOLD + " - Basic Information");
		return true;
	}
}
