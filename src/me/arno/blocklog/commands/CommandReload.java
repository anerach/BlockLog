package me.arno.blocklog.commands;

import me.arno.blocklog.BlockLog;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

public class CommandReload extends BlockLogCommand {
	public CommandReload(BlockLog plugin) {
		super(plugin);
	}

	public boolean execute(Player player, Command cmd, String[] args) {
		if(args.length > 0) {
			player.sendMessage(ChatColor.WHITE + "/bl reload");
			return true;
		}
		
		if (player == null) {
			log.info("Reloading!");
			if(plugin.reloadPlugin())
				log.info("Reloaded Succesfully!");
			else
				log.info("An error occured while reloading BlockLog!");
		} else {
			player.sendMessage(ChatColor.DARK_RED + "[BlockLog] " + ChatColor.GOLD + "Reloading!");
			if(plugin.reloadPlugin())
				player.sendMessage(ChatColor.DARK_RED + "[BlockLog] " + ChatColor.GOLD + "Reloaded Succesfully!");
			else
				player.sendMessage(ChatColor.DARK_RED + "[BlockLog] " + ChatColor.GOLD + "An error occured while reloading BlockLog!");
		}
		return true;
	}

}
