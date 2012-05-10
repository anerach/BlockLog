package me.arno.blocklog.commands;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import me.arno.blocklog.database.Query;

public class CommandSimulateRollback extends BlockLogCommand {

	public CommandSimulateRollback() {
		super("blocklog.rollback");
		setCommandUsage("/bl simrollback [delay <value>] [limit <amount>] [player <value>] [since <value>] [until <value>] [area <value>]");
	}
	
	@Override
	public boolean execute(Player player, Command cmd, String[] args) {
		if(args.length < 2)
			return false;
		
		if(args.length % 2 != 0) {
			player.sendMessage("Invalid amount of args");
			return true;
		}
		
		if(!hasPermission(player)) {
			player.sendMessage("You don't have permission");
			return true;
		}
		
		try {
			String target = null;
			String entity = null;
			Integer untilTime = 0;
			Integer sinceTime = 0;
			Integer area = 0;
			Integer limit = 200;
			Integer delay = 3;
			
			for(int i=0;i<args.length;i+=2) {
				String type = args[i];
				String value = args[i+1];
				if(type.equalsIgnoreCase("limit")) {
					limit = Integer.valueOf(value);
				} else if(type.equalsIgnoreCase("area")) {
					area = Integer.valueOf(value);
				} else if(type.equalsIgnoreCase("player")) {
					target = value;
				} else if(type.equalsIgnoreCase("entity")) {
					entity = value;
				} else if(type.equalsIgnoreCase("since")) {
					Character c = value.charAt(value.length() - 1);
					sinceTime = convertToUnixtime(Integer.valueOf(value.replace(c, ' ').trim()), c.toString());
				} else if(type.equalsIgnoreCase("until")) {
					Character c = value.charAt(value.length() - 1);
					untilTime = convertToUnixtime(Integer.valueOf(value.replace(c, ' ').trim()), c.toString());
				} else if(type.equalsIgnoreCase("delay")) {
					Character c = value.charAt(value.length() - 1);
					delay = Integer.valueOf(value.replace(c, ' ').trim());
				}
			}
			
			if(untilTime != 0 && sinceTime > untilTime) {
				player.sendMessage(ChatColor.WHITE + "Until can't be bigger than since.");
				return true;
			}
			
			World world = player.getWorld();
			
			Query query = new Query("blocklog_blocks");
			query.select("*");
			if(target != null) {
				query.where("trigered", target);
			}
			if(entity != null) {
				if(entity.equalsIgnoreCase("tnt"))
					entity = "primed_tnt";
				query.where("entity", entity);
			}
			if(sinceTime != 0)
				query.where("date", sinceTime.toString(), ">");
			if(untilTime != 0)
				query.where("date", untilTime.toString(), "<");
			if(area != 0) {
				Integer xMin = player.getLocation().getBlockX() - area;
				Integer xMax = player.getLocation().getBlockX() + area;
				Integer yMin = player.getLocation().getBlockY() - area;
				Integer yMax = player.getLocation().getBlockY() + area;
				Integer zMin = player.getLocation().getBlockZ() - area;
				Integer zMax = player.getLocation().getBlockZ() + area;
				
				query.where("x", xMin.toString(), ">=");
				query.where("x", xMax.toString(), "<=");
				
				query.where("y", yMin.toString(), ">=");
				query.where("y", yMax.toString(), "<=");
				
				query.where("z", zMin.toString(), ">=");
				query.where("z", zMax.toString(), "<=");
			}
			query.where("world", world.getName());
			query.where("rollback_id", 0);
			query.groupBy("x", "y", "z");
			query.orderBy("date", "DESC");
			
			int blockCount = query.getRowCount();
			
			player.sendMessage(ChatColor.BLUE + "This rollback will affect " + ChatColor.GOLD + blockCount + " blocks");
			player.sendMessage(ChatColor.BLUE + "At a speed of " + ChatColor.GOLD + (limit/delay) + " blocks/second");
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
