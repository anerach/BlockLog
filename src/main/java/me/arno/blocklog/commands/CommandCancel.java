package me.arno.blocklog.commands;

import me.arno.blocklog.util.Util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class CommandCancel extends BlockLogCommand {
	public CommandCancel() {
		super("blocklog.rollback", true);
		setCommandUsage("/bl cancel <id>");
	}
	
	@Override
	public boolean execute(CommandSender sender, Command cmd, String[] args) {
		if(args.length != 1) {
			return false;
		}
		
		if(!hasPermission(sender)) {
			sender.sendMessage("You don't have permission");
			return true;
		}
		
		if(Util.isNumeric(args[0])) {
			int sid = Integer.valueOf(args[0]);
			
			if(getSchedules().containsKey(sid)) {
				Bukkit.getServer().getScheduler().cancelTask(sid);
				
				sender.sendMessage(ChatColor.DARK_RED + "[BlockLog] " + ChatColor.GOLD + "You've cancelled the scheduled rollback/undo #" + sid);
				sender.sendMessage(ChatColor.DARK_RED + "[BlockLog] " + ChatColor.GOLD + "Use the command " + ChatColor.GREEN + "/bl undo " + getSchedules().get(sid) + ChatColor.GOLD + " to undo the changes made by the rollback!");
			} else {
				sender.sendMessage(ChatColor.BLUE + "There is no scheduled rollback with the id #" + sid);
			}
			return true;
		} else {
			sender.sendMessage(ChatColor.WHITE + "/bl cancel <id>");
			return true;
		}
	}
}
