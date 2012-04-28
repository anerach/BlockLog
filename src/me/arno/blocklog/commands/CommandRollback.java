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
			player.sendMessage(ChatColor.WHITE + "/bl rollback [delay <value>] [limit <amount>] [player <value>] [since <value>] [until <value>] [area <value>]");
			return true;
		}
		
		Integer clauses = args.length/2;
		
		if(!clauses.toString().matches("[0-9]*")) {
			player.sendMessage("Invalid amount of args");
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
			
			String param_target = null;
			String param_until = null;
			String param_from = null;
			String param_area = null;
			
			for(int i=0;i<args.length;i+=2) {
				String type = args[i];
				String value = args[i+1];
				if(type.equalsIgnoreCase("limit")) {
					param_area = value;
					limit = Integer.valueOf(value);
				} else if(type.equalsIgnoreCase("area")) {
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
				} else if(type.equalsIgnoreCase("delay")) {
					Character c = value.charAt(value.length() - 1);
					delay = Integer.valueOf(value.replace(c, ' ').trim());
				}
			}
			
			if(sinceTime != 0 && sinceTime > untilTime) {
				player.sendMessage(ChatColor.WHITE + "Until can't be bigger than since.");
				return true;
			}
			
			World world = player.getWorld();
			
			Query query = new Query("blocklog_blocks");
			query.addSelect("*");
			if(target != null) {
				if(entity == null)
					query.addWhere("entity", "player");
				query.addWhere("trigered", target);
			}
			if(entity != null) {
				if(entity.equalsIgnoreCase("tnt"))
					entity = "primed_tnt";
				query.addWhere("entity", entity);
			}
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
			
			Statement rollbackStmt = conn.createStatement();
			Statement blocksStmt = conn.createStatement();
			
			rollbackStmt.executeUpdate("INSERT INTO blocklog_rollbacks (player, world, param_player, param_from, param_until, param_area, date) VALUES ('" + player.getName() + "', '" + player.getWorld().getName() + "', '" + param_target + "', '" + param_from + "', '" + param_until + "', " + param_area + ", " + System.currentTimeMillis()/1000 + ")");
			
			ResultSet rollback = rollbackStmt.executeQuery("SELECT id FROM blocklog_rollbacks ORDER BY id DESC");
			rollback.next();
			
			Integer rollbackID = rollback.getInt("id");
			
			ResultSet blocks = blocksStmt.executeQuery(query.getQuery());
			Rollback rb = new Rollback(plugin, player, rollbackID, blocks, limit);
			Integer sid = plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, rb, 20L, delay * 20L);
			rb.setId(sid);
			addSchedule(sid, rollbackID);
			player.sendMessage(ChatColor.BLUE + "You've started a rollback, to cancel it say /bl cancel " + sid);
			return true;
		} catch(NumberFormatException e) {
			player.sendMessage(ChatColor.WHITE + "/bl rollback <delay <value>> [limit <amount>] [player <value>] [since <value>] [until <value>] [area <value>]");
			return true;
		} catch(Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}
