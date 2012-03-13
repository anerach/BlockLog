package me.arno.blocklog.commands;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.arno.blocklog.BlockLog;
import me.arno.blocklog.LoggedBlock;
import me.arno.blocklog.Rollback;
import me.arno.blocklog.database.DatabaseSettings;

public class CommandRadiusRollback implements CommandExecutor {
	BlockLog plugin;
	Logger log;
	DatabaseSettings dbSettings;
	
	public CommandRadiusRollback(BlockLog plugin) {
		this.plugin = plugin;
		this.log = plugin.log;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		dbSettings = new DatabaseSettings(plugin);
		Player player = null;
		
		if (sender instanceof Player)
			player = (Player) sender;
		
		if(!(commandLabel.equalsIgnoreCase("blrollbackradius") || commandLabel.equalsIgnoreCase("blrbradius") || commandLabel.equalsIgnoreCase("blrbr")))
			return false;
		
		if (player == null) {
			sender.sendMessage("This command can only be run by a player");
			return true;
		}
		
		if(args.length < 3 || args.length > 4)
			return false;
		
		String strPlayer = null;
		int radius;
		int timeInt;
		String timeVal;
		
		if(args.length == 3) {
			radius = Integer.parseInt(args[0]);
			timeInt = Integer.parseInt(args[1]);
			timeVal = args[2];
		} else if(args.length == 4) {
			strPlayer = args[0];
			radius = Integer.parseInt(args[1]);
			timeInt = Integer.parseInt(args[2]);
			timeVal = args[3];
		} else
			return false;
		
		int xMin = player.getLocation().getBlockX() - radius;
		int xMax = player.getLocation().getBlockX() + radius;
		int yMin = player.getLocation().getBlockY() - radius;
		int yMax = player.getLocation().getBlockY() + radius;
		int zMin = player.getLocation().getBlockZ() - radius;
		int zMax = player.getLocation().getBlockZ() + radius;
		
		if(player.getWorld().getMaxHeight() < yMax)
			yMax =	player.getWorld().getMaxHeight();
		if(0 > yMin)
			yMin = 0;
		
		try {
			Connection conn = dbSettings.getConnection();
			Statement stmt = conn.createStatement();
			Statement updateStmt = conn.createStatement();
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
			
			Rollback rb = new Rollback(plugin, player, 1);
			
			int BlockCount = 0;
			int BlockSize = plugin.blocks.size();
			
			while(BlockSize > BlockCount)
			{
				LoggedBlock LBlock = plugin.blocks.get(BlockCount); 
				if(LBlock.getDate() > time && (LBlock.getX() >= xMin && LBlock.getX() <= xMax ) && (LBlock.getY() >= yMin && LBlock.getY() <= yMax ) && (LBlock.getZ() >= zMin && LBlock.getZ() <= zMax )) {
					Material m = Material.getMaterial(LBlock.getBlockId());
					LBlock.setRollback(rb.getId());
					if(strPlayer != null) {
						if(LBlock.getPlayer() == strPlayer) {
							if(LBlock.getType() == 0)
								player.getWorld().getBlockAt(LBlock.getLocation()).setType(m);
							else
								player.getWorld().getBlockAt(LBlock.getLocation()).setType(Material.AIR);
						}
					} else {
						if(LBlock.getType() == 0)
							player.getWorld().getBlockAt(LBlock.getLocation()).setType(m);
						else
							player.getWorld().getBlockAt(LBlock.getLocation()).setType(Material.AIR);
					}
					BlockCount++;
				}
			}
				
			String Query = String.format("SELECT id,block_id,type,x,y,z FROM blocklog_blocks WHERE date > '%s' AND rollback_id = 0 AND world = '%s' AND x >= %s AND x <= %s AND y >= %s AND y <= %s AND z >= %s AND z <= %s ORDER BY date DESC", time, player.getWorld().getName(), xMin,xMax,yMin,yMax,zMin,zMax);
				
			if(strPlayer != null)
				Query = String.format("SELECT id,block_id,type,x,y,z FROM blocklog_blocks WHERE date > '%s' AND rollback_id = 0 AND world = '%s' AND x >= %s AND x <= %s AND y >= %s AND y <= %s AND z >= %s AND z <= %s AND player = '%s' ORDER BY date DESC", time, player.getWorld().getName(), xMin,xMax,yMin,yMax,zMin,zMax, strPlayer);
			
			ResultSet rs = stmt.executeQuery(Query);
			
			int i = 0;
			while(rs.next()) {
				Material m = Material.getMaterial(rs.getInt("block_id"));
				int type = rs.getInt("type");
				if(type == 0)
					player.getWorld().getBlockAt(rs.getInt("x"),rs.getInt("y"),rs.getInt("z")).setType(m);
				else
					player.getWorld().getBlockAt(rs.getInt("x"),rs.getInt("y"),rs.getInt("z")).setType(Material.AIR);
				
				updateStmt.executeUpdate(String.format("UPDATE blocklog_blocks SET rollback_id = %s WHERE id = %s", rb.getId(), rs.getInt("id")));
				i++;
			}
			
			player.sendMessage(ChatColor.DARK_RED +"[BlockLog] " + ChatColor.GREEN + (i + BlockCount) + ChatColor.GOLD + " blocks changed!");
			player.sendMessage(ChatColor.DARK_RED + "[BlockLog] " + ChatColor.GOLD + "use the command " + ChatColor.GREEN + "/blundo" + ChatColor.GOLD + " to undo this rollback!");
			conn.close();
			return true;
		} catch(SQLException e) {
			e.printStackTrace();
		} catch(Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}
