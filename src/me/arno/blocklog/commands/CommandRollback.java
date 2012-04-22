package me.arno.blocklog.commands;

import java.sql.ResultSet;
import java.sql.Statement;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import me.arno.blocklog.BlockLog;
import me.arno.blocklog.database.Query;
import me.arno.blocklog.schedules.Rollback;

public class CommandRollback extends BlockLogCommand {
	public CommandRollback(BlockLog plugin) {
		super(plugin, "blocklog.rollback");
	}
	
	public boolean execute(Player player, Command cmd, String[] args) {
		if(args.length < 3) {
			player.sendMessage(ChatColor.WHITE + "/bl [radius <radius>] [player <player>] [from <amount> <secs|mins|hours|days|weeks>] <until <amount> <secs|mins|hours|days|weeks>>");
			return true;
		}
		
		if(!hasPermission(player)) {
			player.sendMessage("You don't have permission");
			return true;
		}
		try {
			String target = null;
			Integer untilTime = 0;
			Integer fromTime = 0;
			Integer radius = 0;
			
			for(int i=0;i<args.length;i+=2) {
				String type = args[i];
				String value = args[i+1];
				if(type.equalsIgnoreCase("area")) {
					radius = Integer.valueOf(value);
				} else if(type.equalsIgnoreCase("player")) {
					target = value;
				} else if(type.equalsIgnoreCase("from")) {
					Character c = value.charAt(value.length() - 1);
					fromTime = convertToUnixtime(Integer.valueOf(value.replace(c, ' ').trim()), c.toString());
				} else if(type.equalsIgnoreCase("until")) {
					Character c = value.charAt(value.length() - 1);
					untilTime = convertToUnixtime(Integer.valueOf(value.replace(c, ' ').trim()), c.toString());
				}
			}
			
			if(fromTime != 0 && fromTime < untilTime) {
				player.sendMessage(ChatColor.WHITE + "from time can't be bigger than until time.");
				return true;
			}
			
			World world = player.getWorld();
			
			Query query = new Query("blocklog_blocks");
			query.addSelect("*");
			if(target != null)
				query.addWhere("player", target);
			if(fromTime != 0)
				query.addWhere("date", fromTime.toString(), "<");
			if(untilTime != 0)
				query.addWhere("date", untilTime.toString(), ">");
			if(radius != 0) {
				Integer xMin = player.getLocation().getBlockX() - radius;
				Integer xMax = player.getLocation().getBlockX() + radius;
				Integer yMin = player.getLocation().getBlockY() - radius;
				Integer yMax = player.getLocation().getBlockY() + radius;
				Integer zMin = player.getLocation().getBlockZ() - radius;
				Integer zMax = player.getLocation().getBlockZ() + radius;
				
				query.addWhere("x", xMin.toString(), ">=");
				query.addWhere("x", xMax.toString(), "<=");
				
				query.addWhere("y", yMin.toString(), ">=");
				query.addWhere("y", yMax.toString(), "<=");
				
				query.addWhere("z", zMin.toString(), ">=");
				query.addWhere("z", zMax.toString(), "<=");
			}
			query.addWhere("world", world.getName());
			query.addWhere("rollback_id", new Integer(0).toString());
			query.addGroupBy("x");
			query.addGroupBy("y");
			query.addGroupBy("z");
			query.AddOrderBy("date", "DESC");
			
			log.info(query.getQuery());
			
			Statement rollbackStmt = conn.createStatement();
			Statement blocksStmt = conn.createStatement();
			
			rollbackStmt.executeUpdate("INSERT INTO blocklog_rollbacks (player, world, date, type) VALUES ('" + player.getName() + "', '" + player.getWorld().getName() + "', " + System.currentTimeMillis()/1000 + ", 0)");
			
			ResultSet rollback = rollbackStmt.executeQuery("SELECT id FROM blocklog_rollbacks ORDER BY id DESC");
			rollback.next();
			
			Integer rollbackID = rollback.getInt("id");
			
			ResultSet blocks = blocksStmt.executeQuery(query.getQuery());
			
			plugin.getServer().getScheduler().scheduleAsyncDelayedTask(plugin, new Rollback(plugin, player, target, rollbackID, blocks));
			
			return true;
		} catch(NumberFormatException e) {
			return false;
		} catch(Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}
