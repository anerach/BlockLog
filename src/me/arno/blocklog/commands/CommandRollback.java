package me.arno.blocklog.commands;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import me.arno.blocklog.BlockLog;
import me.arno.blocklog.schedules.Rollback;

public class CommandRollback extends BlockLogCommand {
	public CommandRollback(BlockLog plugin) {
		super(plugin, "blocklog.rollback");
	}

	public boolean execute(Player player, Command cmd, ArrayList<String> listArgs) {
		String[] args = (String[]) listArgs.toArray();
		if(args.length < 2 || args.length > 3) {
			player.sendMessage(ChatColor.WHITE + "/bl rollback [player] <amount> <sec|min|hour|day|week>");
			return true;
		}
		
		if(!hasPermission(player)) {
			player.sendMessage("You don't have permission");
			return true;
		}
		
		try {
			Set<String> Second = new HashSet<String>(Arrays.asList("s", "sec","secs","second","seconds"));
			Set<String> Minute = new HashSet<String>(Arrays.asList("m", "min","mins","minute","minutes"));
			Set<String> Hour = new HashSet<String>(Arrays.asList("h", "hour","hours"));
			Set<String> Day = new HashSet<String>(Arrays.asList("d", "day","days"));
			Set<String> Week = new HashSet<String>(Arrays.asList("w", "week","weeks"));
	
			String target = null;
			String timeVal = null;
			Integer timeInt = 0;
			Integer time;
			
			if(args.length == 2) {
				timeInt = Integer.valueOf(args[0]);
				timeVal = args[1].toLowerCase();
			} else if(args.length == 3) {
				target = args[0];
				timeInt = Integer.valueOf(args[1]);
				timeVal = args[2].toLowerCase();
			}
			
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
			
			rollbackStmt.executeUpdate("INSERT INTO blocklog_rollbacks (player, world, date, type) VALUES ('" + player.getName() + "', '" + player.getWorld().getName() + "', " + System.currentTimeMillis()/1000 + ", 0)");
			
			ResultSet rollback = rollbackStmt.executeQuery("SELECT id FROM blocklog_rollbacks ORDER BY id DESC");
			rollback.next();
			
			Integer rollbackID = rollback.getInt("id");
			
			World world = player.getWorld();
			
			ResultSet blocks;
			if(target == null)
				blocks = blocksStmt.executeQuery(String.format("SELECT * FROM blocklog_blocks WHERE date > '%s' AND rollback_id = 0 AND world = '%s' ORDER BY date DESC", time, world.getName()));
			else
				blocks = blocksStmt.executeQuery(String.format("SELECT * FROM blocklog_blocks WHERE date > '%s' AND rollback_id = 0 AND world = '%s' AND player = '%s' ORDER BY date DESC", time, world.getName(), target));
			
			
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
