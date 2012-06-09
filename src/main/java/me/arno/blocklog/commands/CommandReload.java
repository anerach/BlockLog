package me.arno.blocklog.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class CommandReload extends BlockLogCommand {
	public CommandReload() {
		super("blocklog.reload", true);
		setCommandUsage("/bl reload");
	}
	
	@Override
	public boolean execute(CommandSender sender, Command cmd, String[] args) {
		if(args.length > 0)
			return false;
		
		if(!hasPermission(sender)) {
			sender.sendMessage("You don't have permission");
			return true;
		}
		
		sender.sendMessage(ChatColor.DARK_RED + "[BlockLog] " + ChatColor.GOLD + "Reloading!");
		if(plugin.reloadPlugin())
			sender.sendMessage(ChatColor.DARK_RED + "[BlockLog] " + ChatColor.GOLD + "Reloaded Succesfully!");
		else
			sender.sendMessage(ChatColor.DARK_RED + "[BlockLog] " + ChatColor.GOLD + "An error occured while reloading BlockLog!");
		return true;
	}

}
