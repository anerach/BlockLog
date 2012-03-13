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

public class CommandRollback implements CommandExecutor {
	BlockLog plugin;
	Logger log;
	DatabaseSettings dbSettings;
	
	public CommandRollback(BlockLog plugin) {
		this.plugin = plugin;
		this.log = plugin.log;
	}
	
	public String getQuery(String str) {
		if(str.equalsIgnoreCase("PlayerRollback"))
			return "SELECT id,block_id,type,x,y,z FROM blocklog_blocks WHERE date > '%s' AND rollback_id = 0 AND world = '%s' AND player = '%s' ORDER BY date DESC";
		else if(str.equalsIgnoreCase("TotalRollback"))
			return "SELECT id,block_id,type,x,y,z FROM blocklog_blocks WHERE date > '%s' AND rollback_id = 0 AND world = '%s' ORDER BY date DESC";
		return "";
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		dbSettings = new DatabaseSettings(plugin);
		Player player = null;
		
		if (sender instanceof Player)
			player = (Player) sender;
		
		if(!(commandLabel.equalsIgnoreCase("blrollback") || commandLabel.equalsIgnoreCase("blrb")))
			return false;
		
		if (player == null) {
			sender.sendMessage("This command can only be run by a player");
			return true;
		}
		
		if(args.length < 2 || args.length > 3)
			return false;
		
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
			
			String timeVal = ((args.length == 3) ? args[2].toLowerCase() : args[1].toLowerCase());
			Integer timeInt = Integer.parseInt(((args.length == 3) ? args[1] : args[0]));
			
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
			
			Rollback rb = new Rollback(plugin, player, 0);
			
			int BlockCount = 0;
			int BlockSize = plugin.blocks.size();
			
			while(BlockSize > BlockCount)
			{
				LoggedBlock LBlock = plugin.blocks.get(BlockCount); 
				if(LBlock.getDate() > time) {
					
					Material m = Material.getMaterial(LBlock.getBlockId());
					LBlock.setRollback(rb.getId());
					if(args.length == 3) {
						if(args[0].equalsIgnoreCase(LBlock.getPlayer())) {
							if(LBlock.getType() == 0)
								player.getWorld().getBlockAt(LBlock.getLocation()).setType(m);
							else
								player.getWorld().getBlockAt(LBlock.getLocation()).setType(Material.AIR);
							
							BlockCount++;
						}
					} else {
						if(LBlock.getType() == 0)
							player.getWorld().getBlockAt(LBlock.getLocation()).setType(m);
						else
							player.getWorld().getBlockAt(LBlock.getLocation()).setType(Material.AIR);
						
						BlockCount++;
					}
				}
			}
				
			String Query =  String.format(getQuery("TotalRollback"), time, player.getWorld().getName());
				
			if(args.length == 3)
				Query = String.format(getQuery("PlayerRollback"), time, player.getWorld().getName(), args[0]);
			
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
			conn.close();
			player.sendMessage(ChatColor.DARK_RED + "[BlockLog] " + ChatColor.GREEN + (i + BlockCount) + ChatColor.GOLD + " blocks changed!");
			player.sendMessage(ChatColor.DARK_RED + "[BlockLog] " + ChatColor.GOLD + "use the command " + ChatColor.GREEN + "/blundo" + ChatColor.GOLD + " to undo this rollback!");
			return true;
		} catch(SQLException e) {
			e.printStackTrace();
		} catch(Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}
