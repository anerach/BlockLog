package me.arno.blocklog.commands;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import me.arno.blocklog.BlockLog;
import me.arno.blocklog.schedules.UndoRollback;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandUndo implements CommandExecutor {

	BlockLog plugin;
	Connection conn;
	
	public CommandUndo(BlockLog plugin) {
		this.plugin = plugin;
		this.conn = plugin.conn;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		Player player = null;
		
		if(sender instanceof Player)
			player = (Player) sender;
		
		if(!cmd.getName().equalsIgnoreCase("blundo"))
			return false;
		
		if(player == null) {
			sender.sendMessage("This command can only be run by a player");
			return true;
		}
		
		if(args.length > 1)
			return false;
		
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