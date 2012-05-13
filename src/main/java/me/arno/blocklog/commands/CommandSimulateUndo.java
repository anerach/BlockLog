package me.arno.blocklog.commands;

import java.sql.ResultSet;
import java.sql.Statement;
import me.arno.blocklog.database.Query;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

public class CommandSimulateUndo extends BlockLogCommand {
	public CommandSimulateUndo() {
		super("blocklog.rollback");
		setCommandUsage("/bl simundo [id] [delay <value>] [limit <amount>]");
	}

	@Override
	public boolean execute(Player player, Command cmd, String[] args) {
		if(args.length > 5)
			return false;
		
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
			
			Statement rollbackStmt = conn.createStatement();
			if(args.length == 1) {
				rollbackID = Integer.valueOf(args[0]);
			} else {
				ResultSet rs = rollbackStmt.executeQuery("SELECT id FROM blocklog_rollbacks ORDER BY id DESC");
				rs.next();
				rollbackID = rs.getInt("id");
			}
			
			Query query = new Query("blocklog_blocks");
			query.where("rollback_id", rollbackID);
			
			if(rollbackID == 0) {
				player.sendMessage(ChatColor.WHITE + "Rollback ID can't be 0");
				return true;
			}
			
			int blockCount = query.getRowCount();
			
			player.sendMessage(ChatColor.BLUE + "This undo will affect " + ChatColor.GOLD + blockCount + " blocks");
			player.sendMessage(ChatColor.BLUE + "At a speed of about " + ChatColor.GOLD + Math.round(limit/delay) + " blocks/second");
			player.sendMessage(ChatColor.BLUE + "It will take about " + ChatColor.GOLD + Math.round(blockCount/(limit/delay)) + " seconds " + ChatColor.BLUE + "to complete the rollback");
			return true;
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}