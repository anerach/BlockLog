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
		if(args.length < 2) {
			player.sendMessage(ChatColor.WHITE + "/bl rollback [player <value>] [from <value>] [until <value>] [area <value>]");
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
			
			String param_target = null;
			String param_until = null;
			String param_from = null;
			String param_area = null;
			
			for(int i=0;i<args.length;i+=2) {
				String type = args[i];
				String value = args[i+1];
				log.info("type: " + type);
				log.info("value: " + value);
				if(type.equalsIgnoreCase("area")) {
					param_area = value;
					area = Integer.valueOf(value);
				} else if(type.equalsIgnoreCase("player")) {
					param_target = value;
					target = value;
				} else if(type.equalsIgnoreCase("entity")) {
					param_target = value;
					entity = value;
				} else if(type.equalsIgnoreCase("since")) {
					param_from = value;
					Character c = value.charAt(value.length() - 1);
					sinceTime = convertToUnixtime(Integer.valueOf(value.replace(c, ' ').trim()), c.toString());
				} else if(type.equalsIgnoreCase("until")) {
					param_until = value;
					Character c = value.charAt(value.length() - 1);
					untilTime = convertToUnixtime(Integer.valueOf(value.replace(c, ' ').trim()), c.toString());
				}
			}
			
			if(sinceTime != 0 && sinceTime < untilTime) {
				player.sendMessage(ChatColor.WHITE + "from time can't be bigger than until time.");
				return true;
			}
			
			World world = player.getWorld();
			
			Query query = new Query("blocklog_blocks");
			query.addSelect("*");
			if(target != null) {
				query.addWhere("entity", "player");
				query.addWhere("trigered", target);
			}
			if(entity != null)
				query.addWhere("entity", entity);
			if(sinceTime != 0)
				query.addWhere("date", sinceTime.toString(), ">");
			if(untilTime != 0)
				query.addWhere("date", untilTime.toString(), "<");
			if(area != 0) {
				Integer xMin = player.getLocation().getBlockX() - area;
				Integer xMax = player.getLocation().getBlockX() + area;
				Integer yMin = player.getLocation().getBlockY() - area;
				Integer yMax = player.getLocation().getBlockY() + area;
				Integer zMin = player.getLocation().getBlockZ() - area;
				Integer zMax = player.getLocation().getBlockZ() + area;
				
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
			query.addOrderBy("date", "DESC");
			
			log.info(query.getQuery());
			
			Statement rollbackStmt = conn.createStatement();
			Statement blocksStmt = conn.createStatement();
			
			rollbackStmt.executeUpdate("INSERT INTO blocklog_rollbacks (player, world, param_player, param_from, param_until, param_area, date) VALUES ('" + player.getName() + "', '" + player.getWorld().getName() + "', '" + param_target + "', '" + param_from + "', '" + param_until + "', " + param_area + ", " + System.currentTimeMillis()/1000 + ")");
			
			ResultSet rollback = rollbackStmt.executeQuery("SELECT id FROM blocklog_rollbacks ORDER BY id DESC");
			rollback.next();
			
			Integer rollbackID = rollback.getInt("id");
			
			ResultSet blocks = blocksStmt.executeQuery(query.getQuery());
			
			plugin.getServer().getScheduler().scheduleAsyncDelayedTask(plugin, new Rollback(plugin, player, rollbackID, blocks));
			
			return true;
		} catch(NumberFormatException e) {
			return false;
		} catch(Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}
