package me.arno.blocklog.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

public class CommandCancel extends BlockLogCommand {
	public CommandCancel() {
		super("blocklog.rollback");
		setCommandUsage("/bl cancel <id>");
	}
	
	@Override
	public boolean execute(Player player, Command cmd, String[] args) {
		if(args.length != 1) {
			return false;
		}
		
		if(!hasPermission(player)) {
			player.sendMessage("You don't have permission");
			return true;
		}
		
		try {
			Integer sid = Integer.valueOf(args[0]);
			
			if(getSchedules().containsKey(sid)) {
				getServer().getScheduler().cancelTask(sid);
				
				player.sendMessage(ChatColor.DARK_RED + "[BlockLog] " + ChatColor.GOLD + "You've cancelled the scheduled rollback #" + sid);
				player.sendMessage(ChatColor.DARK_RED + "[BlockLog] " + ChatColor.GOLD + "Use the command " + ChatColor.GREEN + "/bl undo " + getSchedules().get(sid) + ChatColor.GOLD + " to undo this rollback!");
			} else {
				player.sendMessage(ChatColor.BLUE + "There is no scheduled rollback with the id #" + sid);
			}
			return true;
		} catch(NumberFormatException e) {
			player.sendMessage(ChatColor.WHITE + "/bl cancel <id>");
			return true;
		}
	}
}
