package me.arno.blocklog.commands;

import java.sql.SQLException;

import me.arno.blocklog.database.Query;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

public class CommandStorage extends BlockLogCommand {
	public CommandStorage() {
		super("blocklog.storage", true);
		setCommandUsage("/bl storage");
	}

	@Override
	public boolean execute(Player player, Command cmd, String[] args) {
		if(args.length > 0)
			return false;
		
		if(!hasPermission(player)) {
			player.sendMessage("You don't have permission");
			return true;
		}
		
		try {
			int blockEdits = new Query().from("blocklog_blocks").getRowCount();
			int blockInteractions = new Query().from("blocklog_interactions").getRowCount();
		
			if(player == null) {
				log.info(String.format("The database contains %s block edits!", blockEdits));
				log.info(String.format("The database contains %s block interactions!", blockInteractions));
			} else {
				player.sendMessage(String.format(ChatColor.DARK_RED +"[BlockLog] " + ChatColor.GOLD + "The database contains %s block edits!", blockEdits));
				player.sendMessage(String.format(ChatColor.DARK_RED +"[BlockLog] " + ChatColor.GOLD + "The database contains %s block interactions!", blockInteractions));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return true;
	}
}
