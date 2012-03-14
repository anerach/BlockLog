package me.arno.blocklog.commands;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import me.arno.blocklog.BlockLog;
import me.arno.blocklog.Rollback;
import me.arno.blocklog.database.DatabaseSettings;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandUndo implements CommandExecutor {

	BlockLog plugin;
	DatabaseSettings dbSettings;
	
	public CommandUndo(BlockLog plugin) {
		this.plugin = plugin;
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
		
		dbSettings = new DatabaseSettings(plugin);
		try {
			if(args.length == 1) {
				RollbackId = Integer.parseInt(args[0]);
			} else {
				Connection conn = dbSettings.getConnection();
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery("SELECT COUNT(id) AS id FROM blocklog_rollbacks");
				rs.first();
				RollbackId = rs.getInt("id");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		if(RollbackId == 0)
			return false;
		
		Rollback rb = new Rollback(plugin, RollbackId);
		
		if(rb.exists()) {
			rb.undo();
			return true;
		}
		
		return false;
	}

}
