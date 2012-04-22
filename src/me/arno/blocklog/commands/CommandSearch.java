package me.arno.blocklog.commands;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import me.arno.blocklog.BlockLog;
import me.arno.blocklog.database.Query;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

public class CommandSearch extends BlockLogCommand {
	public CommandSearch(BlockLog plugin) {
		super(plugin, "blocklog.save");
	}

	public boolean execute(Player player, Command cmd, String[] args) {
		if(args.length != 1) {
			player.sendMessage(ChatColor.WHITE + "/bl search <player>");
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
			query.addSelectDate("date");
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
			query.addLimit(getConfig().getInt("blocklog.results"));
			
			Statement stmt = conn.createStatement();
			ResultSet actions = stmt.executeQuery(query.getQuery());
			
			while(actions.next()) {
				String name = Material.getMaterial(actions.getInt("block_id")).toString();
				int type = actions.getInt("type");
				
				player.sendMessage(ChatColor.BLUE + "[" + actions.getString("fdate") + "]" + ChatColor.DARK_RED + "[World:" + actions.getString("world") + ", X:" + actions.getString("x") + ", Y:" + actions.getString("y") + ", Z:" + actions.getString("z") + "]");
				if(type == 0) {
					player.sendMessage(ChatColor.GOLD + actions.getString("player") + ChatColor.DARK_GREEN + " broke a " + ChatColor.GOLD + name);
				} else if(type == 1) {
					player.sendMessage(ChatColor.GOLD + actions.getString("player") + ChatColor.DARK_GREEN + " placed a " + ChatColor.GOLD + name);
				}
			}
		} catch(SQLException e) {
			
		}
		return true;
	}

}
