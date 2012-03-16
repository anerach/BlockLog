package me.arno.blocklog.commands;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import me.arno.blocklog.BlockLog;
import me.arno.blocklog.Rollback;

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
		
		int RollbackId = 0;
		
		try {
			if(args.length == 1) {
				RollbackId = Integer.parseInt(args[0]);
			} else {
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery("SELECT id FROM blocklog_rollbacks ORDER BY id DESC");
				rs.next();
				RollbackId = rs.getInt("id");
			}
			
			if(RollbackId == 0)
				return false;
			
			Rollback rb = new Rollback(plugin, RollbackId);
			if(rb.exists()) {
				rb.undo();
			}
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}