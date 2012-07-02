package me.arno.blocklog.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.arno.blocklog.Rollback;
import me.arno.blocklog.util.Syntax;

public class CommandSimulateRollback extends BlockLogCommand {
	public CommandSimulateRollback() {
		super("blocklog.rollback");
		setCommandUsage("/bl simrollback [delay <value>] [limit <amount>] [player <value>] [since <value>] [until <value>] [area <value>]");
	}
	
	@Override
	public boolean execute(CommandSender sender, Command cmd, String[] args) {
		if(args.length < 2)
			return false;
		
		if(args.length % 2 != 0) {
			sender.sendMessage("Invalid amount of args");
			return true;
		}
		
		if(!hasPermission(sender)) {
			sender.sendMessage("You don't have permission");
			return true;
		}
		
		Player player = (Player) sender;
		
		try {
			Syntax syn = new Syntax(args);
			
			String target = syn.getString("player");
			String entity = syn.getString("entity");
			int untilTime = syn.getTimeFromNow("until");
			int sinceTime = syn.getTimeFromNow("since");
			int area = syn.getInt("area");
			int limit = syn.getInt("limit", 200);
			int delay = syn.getTime("delay", "3s");
			
			if(untilTime != 0 && sinceTime > untilTime) {
				player.sendMessage(ChatColor.WHITE + "Until can't be bigger than since.");
				return true;
			}
			
			Rollback rollback = new Rollback(player, target, entity, sinceTime, untilTime, area, delay, limit, 0);
			
			int blockCount = rollback.getAffectedBlockCount();
			
			player.sendMessage(ChatColor.BLUE + "This rollback will affect " + ChatColor.GOLD + blockCount + " blocks");
			player.sendMessage(ChatColor.BLUE + "At a speed of " + ChatColor.GOLD + Math.round(limit/delay) + " blocks/second");
			player.sendMessage(ChatColor.BLUE + "It will take about " + ChatColor.GOLD + Math.round(blockCount/(limit/delay)) + " seconds " + ChatColor.BLUE + "to complete the rollback");
			return true;
		} catch(NumberFormatException e) {
			return false;
		} catch(Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}
