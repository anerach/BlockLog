package me.arno.blocklog.commands;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import me.arno.blocklog.BlockLog;
import me.arno.blocklog.schedules.Rollback;

public class CommandRadiusRollback extends BlockLogCommand {
	public CommandRadiusRollback(BlockLog plugin) {
		super(plugin, "blocklog.rollback");
	}
	
	public boolean execute(Player player, Command cmd, String[] args) {
		if(args.length < 3 || args.length > 4) {
			player.sendMessage(ChatColor.WHITE + "/bl rollbackradius <radius> [player] <time> <secs|mins|hours|days|weeks>");
			return true;
		}
		
		if(!hasPermission(player)) {
			player.sendMessage("You don't have permission");
			return true;
		}
		
		try {
			String target = null;
			int radius = 0;
			int timeInt = 0;
			String timeVal = null;
			
			if(args.length == 3) {
				radius = Integer.valueOf(args[0]);
				timeInt = Integer.valueOf(args[1]);
				timeVal = args[2];
			} else if(args.length == 4) {
				target = args[0];
				radius = Integer.valueOf(args[1]);
				timeInt = Integer.valueOf(args[2]);
				timeVal = args[3];
			}
			
			int time;
			
			Set<String> Second = new HashSet<String>(Arrays.asList("s", "sec","secs","second","seconds"));
			Set<String> Minute = new HashSet<String>(Arrays.asList("m", "min","mins","minute","minutes"));
			Set<String> Hour = new HashSet<String>(Arrays.asList("h", "hour","hours"));
			Set<String> Day = new HashSet<String>(Arrays.asList("d", "day","days"));
			Set<String> Week = new HashSet<String>(Arrays.asList("w", "week","weeks"));
			
			if(Second.contains(timeVal))
				time = (int) (System.currentTimeMillis()/1000 - timeInt);
			else if(Minute.contains(timeVal))
				time = (int) (System.currentTimeMillis()/1000 - timeInt * 60);
			else if(Hour.contains(timeVal))
				time = (int) (System.currentTimeMillis()/1000 - timeInt * 60 * 60);
			else if(Day.contains(timeVal))
				time = (int) (System.currentTimeMillis()/1000 - timeInt * 60 * 60 * 24);
			else if(Week.contains(timeVal))
				time = (int) (System.currentTimeMillis()/1000 - timeInt * 60 * 60 * 24 * 7);
			else {
				player.sendMessage(ChatColor.DARK_GREEN + "Invalid time");
				return false;
			}
			
			Statement rollbackStmt = conn.createStatement();
			Statement blocksStmt = conn.createStatement();
			
			rollbackStmt.executeUpdate("INSERT INTO blocklog_rollbacks (player, world, date, type) VALUES ('" + player.getName() + "', '" + player.getWorld().getName() + "', " + System.currentTimeMillis()/1000 + ", 1)");
			
			ResultSet rs = rollbackStmt.executeQuery("SELECT id FROM blocklog_rollbacks ORDER BY id DESC");
			rs.next();
			
			Integer rollbackID = rs.getInt("id");
			
			Integer xMin = player.getLocation().getBlockX() - radius;
			Integer xMax = player.getLocation().getBlockX() + radius;
			Integer yMin = player.getLocation().getBlockY() - radius;
			Integer yMax = player.getLocation().getBlockY() + radius;
			Integer zMin = player.getLocation().getBlockZ() - radius;
			Integer zMax = player.getLocation().getBlockZ() + radius;
			
			World world = player.getWorld();
			ResultSet blocks;
			
			if(target == null)
				blocks = blocksStmt.executeQuery(String.format("SELECT * FROM blocklog_blocks WHERE date > %s AND rollback_id = 0 AND world = '%s' AND x >= %s AND x <= %s AND y >= %s AND y <= %s AND z >= %s AND z <= %s GROUP BY x, y, z ORDER BY date DESC", time, world.getName(), xMin,xMax,yMin,yMax,zMin,zMax));
			else
				blocks = blocksStmt.executeQuery(String.format("SELECT * FROM blocklog_blocks WHERE date > %s AND rollback_id = 0 AND world = '%s' AND x >= %s AND x <= %s AND y >= %s AND y <= %s AND z >= %s AND z <= %s AND player = '%s' GROUP BY x, y, z ORDER BY date DESC", time, world.getName(), xMin,xMax,yMin,yMax,zMin,zMax, target));
			
			plugin.getServer().getScheduler().scheduleAsyncDelayedTask(plugin, new Rollback(plugin, player, target, rollbackID, blocks));
			
			return true;
		} catch(NumberFormatException e) {
			return false;
		} catch(SQLException e) {
			e.printStackTrace();
		} catch(Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}
