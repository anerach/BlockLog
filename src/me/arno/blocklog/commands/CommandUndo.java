package me.arno.blocklog.commands;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import me.arno.blocklog.BlockLog;
import me.arno.blocklog.schedules.UndoRollback;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

public class CommandUndo extends BlockLogCommand {
	public CommandUndo(BlockLog plugin) {
		super(plugin);
	}

	public boolean execute(Player player, Command cmd, String[] args) {
		if(args.length > 1) {
			player.sendMessage(ChatColor.WHITE + "/bl undo [id]");
			return true;
		}
		
		int rollbackID = 0;
		
		try {
			Statement undoStmt = conn.createStatement();
			Statement rollbackStmt = conn.createStatement();
			Statement blocksStmt = conn.createStatement();
			
			if(args.length == 1) {
				rollbackID = Integer.parseInt(args[0]);
			} else {
				ResultSet rs = rollbackStmt.executeQuery("SELECT id FROM blocklog_rollbacks ORDER BY id DESC");
				rs.next();
				rollbackID = rs.getInt("id");
			}
			
			if(rollbackID == 0)
				return false;
			
			undoStmt.executeUpdate("INSERT INTO blocklog_undos (rollback_id, player, date) VALUES (" + rollbackID + ", '" + player.getName() + "', " + System.currentTimeMillis()/1000 + ")");
			
			ResultSet blocks = blocksStmt.executeQuery(String.format("SELECT * FROM blocklog_blocks WHERE rollback_id = '%s'", rollbackID));
			
			plugin.getServer().getScheduler().scheduleAsyncDelayedTask(plugin, new UndoRollback(plugin, player, rollbackID, blocks));
			
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}