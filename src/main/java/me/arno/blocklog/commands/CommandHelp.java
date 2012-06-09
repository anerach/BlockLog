package me.arno.blocklog.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class CommandHelp extends BlockLogCommand {
	public CommandHelp() {
		super("blocklog.help", true);
		setCommandUsage("/bl help");
	}

	@Override
	public boolean execute(CommandSender sender, Command cmd, String[] args) {
		if(args.length > 0) {
			return false;
		}
		
		if(!hasPermission(sender)) {
			sender.sendMessage("You don't have permission");
			return true;
		}
		
		sender.sendMessage(ChatColor.DARK_RED +"[BlockLog] " + ChatColor.GOLD + "Commands");
		sender.sendMessage(ChatColor.DARK_RED +"/bl help" + ChatColor.GOLD + " - Shows this message");
		
		if(sender.hasPermission("blocklog.reload"))
			sender.sendMessage(ChatColor.DARK_RED +"/bl reload" + ChatColor.GOLD + " - Reloads blocklog config file");
		
		if(sender.hasPermission("blocklog.purge"))
			sender.sendMessage(ChatColor.DARK_RED +"/bl purge" + ChatColor.GOLD + " - Clears blocklog's history");
		
		if(sender.hasPermission("blocklog.config"))
			sender.sendMessage(ChatColor.DARK_RED +"/bl config" + ChatColor.GOLD + " - Change blocklog's command values ingame");
		
		if(sender.hasPermission("blocklog.report.write") && plugin.getConfig().getBoolean("blocklog.reports"))
			sender.sendMessage(ChatColor.DARK_RED +"/bl report" + ChatColor.GOLD + " - Create a grief report");
		
		if(sender.hasPermission("blocklog.report.read") && plugin.getConfig().getBoolean("blocklog.reports"))
			sender.sendMessage(ChatColor.DARK_RED +"/bl read" + ChatColor.GOLD + " - Read a grief report");
		
		if(sender.hasPermission("blocklog.storage"))
			sender.sendMessage(ChatColor.DARK_RED +"/bl storage" + ChatColor.GOLD + " - Shows the total amount of stored block edits and interactions");
		
		if(sender.hasPermission("blocklog.queue"))
			sender.sendMessage(ChatColor.DARK_RED +"/bl queue" + ChatColor.GOLD + " - Shows the total amount of queued block edits and interactions");
		
		if(sender.hasPermission("blocklog.rollback"))
			sender.sendMessage(ChatColor.DARK_RED +"/bl undo" + ChatColor.GOLD + " - Undos the latest or specified rollback");
			
		if(sender.hasPermission("blocklog.rollback"))
			sender.sendMessage(ChatColor.DARK_RED +"/bl rollback" + ChatColor.GOLD + " - Blocklog's rollback command");
		
		if(sender.hasPermission("blocklog.rollback"))
			sender.sendMessage(ChatColor.DARK_RED +"/bl simundo" + ChatColor.GOLD + " - Simulates an undo");
			
		if(sender.hasPermission("blocklog.rollback"))
			sender.sendMessage(ChatColor.DARK_RED +"/bl simrollback" + ChatColor.GOLD + " - Simulates a rollback");
		
		if(sender.hasPermission("blocklog.autosave"))
			sender.sendMessage(ChatColor.DARK_RED +"/bl autosave" + ChatColor.GOLD + " - Enables autosave feature");
		
		if(sender.hasPermission("blocklog.save"))
			sender.sendMessage(ChatColor.DARK_RED +"/bl save" + ChatColor.GOLD + " - Saves queued block edits and interactions");
		
		if(sender.hasPermission("blocklog.lookup"))
			sender.sendMessage(ChatColor.DARK_RED +"/bl lookup" + ChatColor.GOLD + " - Used to search the database for a player's latest actions");
		
		if(sender.hasPermission("blocklog.search"))
			sender.sendMessage(ChatColor.DARK_RED +"/bl search" + ChatColor.GOLD + " - Used to search the database for results");
		
		if(sender.hasPermission("blocklog.wand"))
			sender.sendMessage(ChatColor.DARK_RED +"/bl wand" + ChatColor.GOLD + " - Enables blocklog's wand");
		sender.sendMessage(ChatColor.DARK_RED +"/blocklog" + ChatColor.GOLD + " - Basic Information");
		return true;
	}
}
