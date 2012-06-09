package me.arno.blocklog.commands;

import java.sql.SQLException;

import me.arno.blocklog.util.Query;
import me.arno.blocklog.util.Util;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class CommandStorage extends BlockLogCommand {
	public CommandStorage() {
		super("blocklog.storage", true);
		setCommandUsage("/bl storage");
	}

	@Override
	public boolean execute(CommandSender sender, Command cmd, String[] args) {
		if(args.length > 0)
			return false;
		
		if(!hasPermission(sender)) {
			sender.sendMessage("You don't have permission");
			return true;
		}
		
		try {
			sender.sendMessage(ChatColor.YELLOW + "BlockLog Queue" + ChatColor.DARK_GRAY + " -------------------------------");
			sender.sendMessage(ChatColor.GRAY + Util.addSpaces("Queue", 100) + "Amount");
			sender.sendMessage(Util.addSpaces(ChatColor.GOLD + "Blocks", 109) + new Query("blocklog_blocks").getRowCount());
			sender.sendMessage(Util.addSpaces(ChatColor.GOLD + "Chests", 109) + new Query("blocklog_chests").getRowCount());
			sender.sendMessage(Util.addSpaces(ChatColor.GOLD + "Data", 109) + new Query("blocklog_data").getRowCount());
			sender.sendMessage(Util.addSpaces(ChatColor.GOLD + "Interactions", 109) + new Query("blocklog_interactions").getRowCount());
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return true;
	}
}
