package me.arno.blocklog.commands;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import me.arno.blocklog.BlockLog;
import me.arno.blocklog.database.Query;
import me.arno.blocklog.schedules.UndoRollback;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

public class CommandUndo extends BlockLogCommand {
	public CommandUndo(BlockLog plugin) {
		super(plugin, "blocklog.rollback");
	}

	public boolean execute(Player player, Command cmd, String[] args) {
		if(args.length > 5) {
			player.sendMessage(ChatColor.WHITE + "/bl undo [id] [delay <value>] [limit <amount>]");
			return true;
		}
		
		if(!hasPermission(player)) {
			player.sendMessage("You don't have permission");
			return true;
		}
		
		int rollbackID = 0;
		
		try {
			Integer limit = 200;
			Integer delay = 3;
			
			for(int i=1;i<args.length;i+=2) {
				String type = args[i];
				String value = args[i+1];
				if(type.equalsIgnoreCase("limit")) {
					limit = Integer.valueOf(value);
				} else if(type.equalsIgnoreCase("delay")) {
					Character c = value.charAt(value.length() - 1);
					delay = Integer.valueOf(value.replace(c, ' ').trim());
				}
			}
			
			Statement undoStmt = conn.createStatement();
			Statement rollbackStmt = conn.createStatement();
			Statement blocksStmt = conn.createStatement();
			
			if(args.length == 1) {
				rollbackID = Integer.valueOf(args[0]);
			} else {
				ResultSet rs = rollbackStmt.executeQuery("SELECT id FROM blocklog_rollbacks ORDER BY id DESC");
				rs.next();
				rollbackID = rs.getInt("id");
			}
			
			Query query = new Query("blocklog_blocks");
			query.addSelect("*");
			query.addWhere("rollback_id", rollbackID);
			
			if(rollbackID == 0) {
				player.sendMessage(ChatColor.WHITE + "Rollback ID can't be 0");
				return true;
			}
			
			undoStmt.executeUpdate("INSERT INTO blocklog_undos (rollback_id, player, date) VALUES (" + rollbackID + ", '" + player.getName() + "', " + System.currentTimeMillis()/1000 + ")");
			
			ResultSet blocks = blocksStmt.executeQuery(query.getQuery());
			
			UndoRollback undo = new UndoRollback(plugin, player, rollbackID, blocks, limit);
			Integer sid = plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, undo, 20L, delay * 20L);
			undo.setId(sid);
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}