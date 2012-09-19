package me.arno.blocklog.commands;

import java.sql.Connection;
import java.sql.SQLException;

import me.arno.blocklog.managers.DatabaseManager;
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
			Connection conn = getDatabaseManager().getConnection();
			sender.sendMessage(ChatColor.YELLOW + "BlockLog Queue" + ChatColor.DARK_GRAY + " -------------------------------");
			sender.sendMessage(ChatColor.GRAY + Util.addSpaces("Queue", 100) + "Amount");
			sender.sendMessage(Util.addSpaces(ChatColor.GOLD + "Blocks", 109) + new Query(DatabaseManager.databasePrefix + "blocks").getRowCount(conn));
			sender.sendMessage(Util.addSpaces(ChatColor.GOLD + "Chests", 109) + new Query(DatabaseManager.databasePrefix + "chests").getRowCount(conn));
			sender.sendMessage(Util.addSpaces(ChatColor.GOLD + "Data", 109) + new Query(DatabaseManager.databasePrefix + "data").getRowCount(conn));
			sender.sendMessage(Util.addSpaces(ChatColor.GOLD + "Interactions", 109) + new Query(DatabaseManager.databasePrefix + "interactions").getRowCount(conn));
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return true;
	}
}
