package me.arno.blocklog.commands;

import java.sql.ResultSet;

import me.arno.blocklog.Undo;
import me.arno.blocklog.util.Query;
import me.arno.blocklog.util.Syntax;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandSimulateUndo extends BlockLogCommand {
	public CommandSimulateUndo() {
		super("blocklog.rollback");
		setCommandUsage("/bl simundo [id] [delay <value>] [limit <amount>]");
	}

	@Override
	public boolean execute(CommandSender sender, Command cmd, String[] args) {
		if(args.length > 5)
			return false;
		
		if(!hasPermission(sender)) {
			sender.sendMessage("You don't have permission");
			return true;
		}
		
		Player player = (Player) sender;
		
		int rollbackID = 0;
		
		try {
			Syntax syn = new Syntax(args);

			int limit = syn.getInt("limit", 200);
			int delay = syn.getTime("delay", "3s");
			
			if(args.length == 1) {
				rollbackID = Integer.valueOf(args[0]);
			} else {
				ResultSet rs = new Query("blocklog_rollbacks").select("id").orderBy("id", "DESC").getResult();
				rs.next();
				rollbackID = rs.getInt("id");
			}
			
			if(rollbackID == 0) {
				player.sendMessage(ChatColor.WHITE + "Rollback ID can't be 0");
				return true;
			}
			
			Undo undo = new Undo(player, delay, limit, rollbackID);
			int blockCount = undo.getAffectedBlockCount();
			
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