package me.arno.blocklog.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class CommandAutoSave extends BlockLogCommand {
	public CommandAutoSave() {
		super("blocklog.autosave", true);
		setCommandUsage("/bl autosave [amount|info]");
	}
	
	@Override
	public boolean execute(CommandSender sender, Command cmd, String[] args) {
		if(args.length > 1) {
			return false;
		}
		
		if(!hasPermission(sender)) {
			sender.sendMessage("You don't have permission");
			return true;
		}
		
		if(args.length == 0) {
			if(plugin.autoSave == 0) {
				sender.sendMessage(ChatColor.DARK_RED + "[BlockLog] " + ChatColor.GOLD + "Autosave has already been disabled");
				return true;
			}
			plugin.autoSave = 0;
			sendAdminMessage(String.format(ChatColor.DARK_RED + "[BlockLog] " + ChatColor.GOLD + "Autosave disabled by %s", sender.getName()));
			return true;
		} else if(args.length == 1) {
			if(args[0].equalsIgnoreCase("info")) {
				if(plugin.autoSave != 0)
					sender.sendMessage(String.format(ChatColor.DARK_RED + "[BlockLog] " + ChatColor.GOLD + "Autosave configured at %s blocks", plugin.autoSave));
				else
					sender.sendMessage(ChatColor.DARK_RED + "[BlockLog] " + ChatColor.GOLD + "There is no autosave configured");
			} else {
				plugin.autoSave = Integer.valueOf(args[0]);
				sendAdminMessage(String.format(ChatColor.DARK_RED + "[BlockLog] " + ChatColor.GOLD + "Autosave enabled at %s blocks by %s", plugin.autoSave, sender.getName()));
				log.info(String.format("Autosave enabled at %s blocks by %s", plugin.autoSave, sender.getName()));
			}
			return true;
		}
		return false;
	}
}
