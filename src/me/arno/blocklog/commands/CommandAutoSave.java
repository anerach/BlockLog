package me.arno.blocklog.commands;

import me.arno.blocklog.BlockLog;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

public class CommandAutoSave extends BlockLogCommand {
	public CommandAutoSave(BlockLog plugin) {
		super(plugin);
	}
	
	public boolean execute(Player player, Command cmd, String[] args) {
		if(args.length > 1) {
			player.sendMessage(ChatColor.WHITE + "/bl autosave [amount|info]");
			return true;
		}
		
		if(args.length == 0) {
			plugin.autoSave = 0;
			sendAdminMessage(String.format(ChatColor.DARK_RED + "[BlockLog] " + ChatColor.GOLD + "Autosave disabled by %s", player.getName()));
			log.info(String.format("Autosave disabled by %s", player.getName()));
			return true;
		} else if(args.length == 1) {
			if(args[0].equalsIgnoreCase("info")) {
				if(plugin.autoSave != 0) {
					if(player == null)
						log.info(String.format("Autosave configured at %s blocks", plugin.autoSave));
					else
						player.sendMessage(String.format(ChatColor.DARK_RED + "[BlockLog] " + ChatColor.GOLD + "Autosave configured at %s blocks", plugin.autoSave));
				} else {
					if(player == null)
						log.info("There is no autosave configured");
					else
						player.sendMessage(ChatColor.DARK_RED + "[BlockLog] " + ChatColor.GOLD + "There is no autosave configured");
				}
			} else {
				plugin.autoSave = Integer.valueOf(args[0]);
				sendAdminMessage(String.format(ChatColor.DARK_RED + "[BlockLog] " + ChatColor.GOLD + "Autosave enabled at %s blocks by %s", plugin.autoSave, player.getName()));
				log.info(String.format("Autosave enabled at %s blocks by %s", plugin.autoSave, player.getName()));
			}
			return true;
		}
		return false;
	}
}
